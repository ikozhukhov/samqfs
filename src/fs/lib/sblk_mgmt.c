/*
 * sblk_mgmt.c - Library functions for superblock management
 */

/*
 *    SAM-QFS_notice_begin
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License")
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2008 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 *
 *    SAM-QFS_notice_end
 */

#pragma ident "$Revision: 1.17 $"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <strings.h>

#include <sys/param.h>
#include <sys/types.h>
#include <sys/errno.h>
#include <sys/stat.h>
#include <sys/vfs.h>

#include <sam/param.h>
#include <sam/types.h>
#include <sam/fs/samhost.h>
#include <sam/fs/ino.h>
#include <pub/devstat.h>
#include <sam/fs/sblk.h>
#include <sam/fs/sblk_mgmt.h>

/*
 * ----- sam_dev_fd_get - Get file descriptor for identified device.
 */
int						/* Errno status code */
sam_dev_fd_get(
	char *devp,				/* Device file name to open */
	unsigned long flags,			/* Required device file type */
	unsigned long mode,			/* Open mode */
	int *fdp)				/* Saved file descriptor */
{
	struct stat st;
	int err;

	if ((devp == NULL) || (fdp == NULL)) {
		return (EINVAL);
	}

	/* Open file */
	if ((*fdp = open(devp, mode)) < 0) {
		*fdp = -1;
		return (errno);
	}

	/* If requested, check device type. */
	if (flags & (DEV_FLAGS_BLK | DEV_FLAGS_CHR)) {

		if (fstat(*fdp, &st) < 0) {
			err = errno;
			close(*fdp);
			*fdp = -1;
			return (err);
		}

		if ((flags & DEV_FLAGS_BLK) && S_ISBLK(st.st_mode)) {
			return (0);
		}

		if ((flags & DEV_FLAGS_CHR) && S_ISCHR(st.st_mode)) {
			return (0);
		}

		close(*fdp);
		*fdp = -1;
		return (ENODEV);
	}

	return (0);
}

/*
 * ----- sam_fd_block_get - Retrieve file data relative to start.
 */
int						/* Errno status code */
sam_fd_block_get(
	int fd,					/* Target file descriptor */
	unsigned long block_offset,		/* Block offset from start */
	void *bp,				/* Data buffer */
	int nbytes)				/* Number of bytes to read */
{
	int n;

	if ((fd < 0) || (bp == NULL) || (nbytes < 0)) {
		return (EINVAL);
	}

	/* Seek to expected start of block. */
	if (llseek(fd, block_offset * SAM_DEV_BSIZE, SEEK_SET) == (off_t)-1) {
		return (errno);
	}

	/* Read up to <size> bytes. */
	n = read(fd, bp, nbytes);
	if (n < 0) {
		return (errno);
	}

	/* Verify block size. */
	if (n < nbytes) {
		return (ENODEV);
	}

	return (0);
}

/*
 * ----- sam_fd_block_get_next - Retrieve file data relative to current
 */
int					/* Errno status code */
sam_fd_block_get_next(
	int fd,				/* Target file descriptor */
	unsigned long block_offset,	/* Starting block offset from current */
	void *bp,			/* Data buffer */
	int nbytes)			/* Number of bytes to read */
{
	int n;

	if ((fd < 0) || (bp == NULL) || (nbytes < 0)) {
		return (EINVAL);
	}

	/* Seek to expected start of block. */
	if (llseek(fd, block_offset * SAM_DEV_BSIZE, SEEK_CUR) == (off_t)-1) {
		return (errno);
	}

	/* Read up to <size> bytes. */
	n = read(fd, bp, nbytes);
	if (n < 0) {
		return (errno);
	}

	/* Verify block size. */
	if (n < nbytes) {
		return (ENODEV);
	}

	return (0);
}

/*
 * ----- sam_fd_sb_get - Get SAM-FS/QFS superblock given file descriptor
 */
int				/* Errno status code */
sam_fd_sb_get(
	int fd,			/* Device file descriptor */
	struct sam_sblk *sbp)	/* Superblock buffer */
{
	int err;
	char sb_name[8];

	if ((fd < 0) || (sbp == NULL)) {
		return (EINVAL);
	}

	/* Get pointer to superblock. */
	if ((err = sam_fd_block_get(fd, SUPERBLK, (void *)sbp,
			sizeof (struct sam_sblk))) != 0) {
		return (err);
	}

	/* Verify that the superblock name is for a SAM-FS/QFS filesystem. */
	if ((err = sam_sb_name_get(sbp, sb_name, 8)) != 0) {
		return (err);
	}

	return (0);
}

/*
 * ----- sam_dev_sb_get - Get SAM-FS/QFS superblock given device file name
 */
int						/* Errno status code */
sam_dev_sb_get(
	char *devp,				/* Device file name */
	struct sam_sblk *sbp,			/* Superblock buffer */
	int *fdp)				/* Saved file descriptor */
{
	int err;

	if ((devp == NULL) || (sbp == NULL) || (fdp == NULL)) {
		return (EINVAL);
	}

	/* Get device file descriptor. */
	if ((err = sam_dev_fd_get(devp, (DEV_FLAGS_BLK | DEV_FLAGS_CHR),
	    O_RDONLY, fdp)) != 0) {
		return (err);
	}

	/* Get SAM-FS/QFS superblock. */
	if ((err = sam_fd_sb_get(*fdp, sbp)) != 0) {
		close(*fdp);
		*fdp = -1;
		return (err);
	}

	return (0);
}

/*
 * ----- sam_inodes_get - Get SAM-FS/QFS first .inodes entry
 */
int						/* Errno status code */
sam_inodes_get(
	int fd,					/* File descriptor */
	struct sam_perm_inode *pip)		/* Inode buffer */
{
	int err;
	struct sam_sblk sb;

	if ((fd < 0) || (pip == NULL)) {
		return (EINVAL);
	}

	/* Get SAM-FS/QFS superblock. */
	if ((err = sam_fd_sb_get(fd, (struct sam_sblk *)&sb)) != 0) {
		return (err);
	}

	/* Find and get .inodes inode. */
	if ((err = sam_fd_block_get(fd, sb.info.sb.inodes, pip,
			sizeof (struct sam_perm_inode))) != 0) {
		return (err);
	}

	/* Sanity check. */
	if ((pip->di.id.ino != SAM_INO_INO) ||
	    (pip->di.id.gen != SAM_INO_INO)) {
		return (ENODEV);
	}

	return (0);
}

/*
 * ----- sam_sb_version_get - Get SAM-FS/QFS superblock version
 */
int					/* Errno status code */
sam_sb_version_get(
	struct sam_sblk *sbp,		/* Superblock */
	unsigned long *version)		/* Saved version */
{
	if ((sbp == NULL) || (version == NULL)) {
		return (EINVAL);
	}

	switch (sbp->info.sb.magic) {
	case SAM_MAGIC_V1:
		*version = SAMFS_SBLKV1;
		break;
	case SAM_MAGIC_V2:
	case SAM_MAGIC_V2A:
		*version = SAMFS_SBLKV2;
		break;
	default:
		*version = SAMFS_SBLK_UNKNOWN;
		return (ENODEV);
		/* NOTREACHED */
	}

	return (0);
}

/*
 * ----- sam_sbord_devtype_get -  Get superblock ordinal entry device type
 */
int					/* Errno status code */
sam_sbord_devtype_get(
	struct sam_sbord *sbop,		/* Superblock ordinal entry */
	unsigned long *devtype)		/* Saved device type */
{
	if ((sbop == NULL) || (devtype == NULL)) {
		return (EINVAL);
	}

	switch (sbop->type) {
	case DT_DATA:
	case DT_META:
	case DT_RAID:
		*devtype = sbop->type;
		break;
	default:
		if (is_stripe_group(sbop->type)) {
			*devtype = DT_STRIPE_GROUP;
			break;
		}
		*devtype = DT_UNKNOWN;
		return (ENODEV);
		/* NOTREACHED */
	}

	return (0);
}

/*
 * ----- sam_sb_name_get - Get SAM-FS/QFS superblock name as a string
 */
int					/* Errno status code */
sam_sb_name_get(
	struct sam_sblk *sbp,		/* SAM-FS/QFS superblock */
	char *strp,			/* String buffer */
	int len)			/* String buffer length (bytes) */
{
	if ((sbp == NULL) || (strp == NULL) || (len < (SAMFS_SB_NAME_LEN+1))) {
		return (EINVAL);
	}

	strncpy(strp, sbp->info.sb.name, SAMFS_SB_NAME_LEN);
	strp[SAMFS_SB_NAME_LEN] = '\0';

	/* Validate the superblock name. */
	if (strcmp(strp, SAMFS_SB_NAME_STR) != 0) {
		return (ENODEV);
	}

	return (0);
}

/*
 * ----- sam_sb_fstype_get - Get SAM-FS/QFS filesystem type
 */
int					/* Errno status code */
sam_sb_fstype_get(
	struct sam_sblk *sbp,		/* SAM-FS/QFS Superblock */
	unsigned long *ftp)		/* Saved SAM-FS/QFS filesystem type */
{
	unsigned long version;
	int err;
	int i;

	int qfs = 0;
	int samfs = 0;

	if ((sbp == NULL) || (ftp == NULL)) {
		return (EINVAL);
	}

	/* Get SAM-FS/QFS superblock version. */
	if ((err = sam_sb_version_get(sbp, &version)) != 0) {
		*ftp = FSTYPE_UNKNOWN;
		return (err);
	}

	/* A host table indicates a QFS (shared) filesystem. */
	if (sbp->info.sb.hosts != 0) {
		qfs++;
	} else {
		int md = 0;
		int qd = 0;

		/*
		 * Search ordinal entries.  If we find an md device without
		 * any mm, mr, or g devices, then we're qfs.  Otherwise samfs.
		 */
		for (i = 0; i < sbp->info.sb.fs_count; i++) {
			unsigned long dtype;

			if (sam_sbord_devtype_get(&sbp->eq[i].fs, &dtype)
			    != 0) {
				*ftp = FSTYPE_UNKNOWN;
				return (ENODEV);
				/* NOTREACHED */
			}

			switch (dtype) {
			case DT_META:
			case DT_RAID:
			case DT_STRIPE_GROUP:
				qd++;
				break;
			case DT_DATA:
				md++;
				break;
			default:
				*ftp = FSTYPE_UNKNOWN;
				return (ENODEV);
				/* NOTREACHED */
			}
		}
		if (md & !qd) {
			samfs++;
		} else {
			qfs++;
		}
	}

	/* Determine specific type based on version. */
	switch (version) {
	case SAMFS_SBLKV1:
		if (samfs) {
			*ftp = FSTYPE_SAM_FS_SBV1;
		} else {
			*ftp = FSTYPE_SAM_QFS_SBV1;
		}
		break;
	case SAMFS_SBLKV2:
		if (samfs) {
			*ftp = FSTYPE_SAM_FS_SBV2;
		} else {
			*ftp = FSTYPE_SAM_QFS_SBV2;
		}
		break;
	default:
		*ftp = FSTYPE_UNKNOWN;
		return (ENODEV);
		/* NOTREACHED */
	}

	return (0);
}

/*
 * ----- sam_fd_host_table_get - Get host table
 */
int					/* Errno status code */
sam_fd_host_table_get(
	int fd,				/* Device file descriptor */
	struct sam_host_table *htp)	/* Host table buffer */
{
	struct stat st;
	struct sam_sblk sb;
	int nbytes;
	int err;

	if ((fd < 0) || (htp == NULL)) {
		return (EINVAL);
	}

	/*
	 * 'fd' must refer to a block special or character special file.
	 */
	if (fstat(fd, &st) < 0) {
		return (errno);
	}
	if (!S_ISBLK(st.st_mode) && !S_ISCHR(st.st_mode)) {
		return (ENODEV);
	}

	/* Get pointer to superblock. */
	if ((err = sam_fd_block_get(fd, SUPERBLK, (void *)&sb,
			sizeof (struct sam_sblk))) != 0) {
		return (err);
	}

	/* Verify this superblock has a host table. */
	if ((sb.info.sb.magic == SAM_MAGIC_V1) || (sb.info.sb.ord != 0) ||
	    (sb.info.sb.hosts == 0)) {
		return (ENOENT);
	}

	/* Find start of host table data */
	if (llseek(fd, sb.info.sb.hosts * SAM_DEV_BSIZE, SEEK_SET) ==
	    (off_t)-1) {
		return (errno);
	}

	/* Read host table info buffer */
	nbytes = read(fd, (char *)htp, sizeof (struct sam_host_table));
	if (nbytes < 0) {
		return (errno);
	}

	if (nbytes != sizeof (struct sam_host_table)) {
		return (ENODEV);
	}

	return (0);
}
