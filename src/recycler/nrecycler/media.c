/*
 * media.c - accumulate state of removable and disk archive media.
 */
/*
 *    SAM-QFS_notice_begin
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at pkg/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at pkg/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2009 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 *
 *    SAM-QFS_notice_end
 */
#pragma ident "$Revision: 1.11 $"

static char *_SrcFile = __FILE__;

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/bitmap.h>

#include "recycler_c.h"
#include "recycler_threads.h"

#include "sam/types.h"
#include "aml/shm.h"
#include "aml/device.h"
#include "aml/catlib.h"
#include "aml/diskvols.h"
#include "aml/sam_rft.h"
#include "sam/lib.h"
#include "sam/sam_malloc.h"
#include "sam/sam_trace.h"
#include <sam/fs/bswap.h>

#include "recycler.h"

extern shm_alloc_t master_shm;
extern shm_ptr_tbl_t *shm_ptr_tbl;

static DiskVolumeSeqnum_t getMaxseqnum(char *volname, DiskVolumeInfo_t *dv);
static void createBitmap(MediaEntry_t *entry, size_t size);
static void *hashResize(void *ptr, size_t size, char *err_name);
static int getSpacePercent(uint64_t space, uint64_t capacity);

/*
 * Initialize media table.  All removable media and disk archive volumes
 * will be initialized in the specified media table.
 */
int
MediaInit(
	MediaTable_t *table,
	char *name)
{
	extern char *program_name;
	int i;
	int ret;
	dev_ent_t *dev;
	media_t media;
	struct CatalogEntry *catalog;
	struct CatalogEntry *ce;
	int numCatalog;
	DiskVolsDictionary_t *diskvols;
	char *volname;
	DiskVolumeInfo_t *dv;
	MediaEntry_t *entry;
	size_t size;
	long long chunk;
	int mapchunk;
	int retval;
	int pct;

	Trace(TR_MISC, "[%s] Init media table: 0x%x", name, (int)table);

	retval = 0;
	table->mt_name = strdup(name);

	for (dev = DeviceGetHead(); dev != NULL;
	    dev = (dev_ent_t *)SHM_REF_ADDR(dev->next)) {

		if (IS_ROBOT(dev) || IS_RSC(dev)) {
			catalog = CatalogGetEntriesByLibrary(dev->eq,
			    &numCatalog);
			for (i = 0; i < numCatalog; i++) {
				ce = &catalog[i];
				if ((ce->CeStatus & CES_inuse) == 0 ||
				    (ce->CeVsn[0] == '\0')) {
					continue;
				}
				media = sam_atomedia(ce->CeMtype);
				entry = MediaFind(table, media, ce->CeVsn);
				if (entry == NULL) {
					Trace(TR_ERR,
					    "Error: failed to find volume %s.%s"
					    "eq: %d",
					    ce->CeMtype, ce->CeVsn, dev->eq);
					retval = -1;
					continue;
				}

				/*
				 * Skip third-party volumes.
				 */
				if ((media & DT_CLASS_MASK) == DT_THIRD_PARTY) {
					continue;
				}

				CLEAR_FLAG(entry->me_flags, ME_noexist);

				if (GET_FLAG(ce->CeStatus,
				    CES_recycle) == B_TRUE) {
					SET_FLAG(entry->me_flags, ME_recycle);
				}

				entry->me_dev = dev;

				/*
				 * Save some information from the catalog.
				 */
				entry->me_slot = ce->CeSlot;
				entry->me_part = ce->CePart;
				entry->me_label = ce->CeLabelTime;

				/*
				 * Check if removable media volume should be
				 * ignore for recycling. Ignore volume if any
				 * of the following conditions are true.
				 *
				 * Space used is less than 50%
				 * Volume is marked unavailable
				 * Volume is not labeled
				 */
				pct = getSpacePercent(ce->CeSpace,
				    ce->CeCapacity);

				if (pct < 50 ||

				    GET_FLAG(ce->CeStatus,
				    CES_unavail) == B_TRUE ||

				    GET_FLAG(ce->CeStatus,
				    CES_labeled) == B_FALSE) {

					SET_FLAG(entry->me_flags, ME_ignore);
				}
			}
		}
	}

	/*
	 * Set parameters for disk volume bitmaps.  All bitmaps will be
	 * allocated the same.
	 */
	mapchunk = SEQNUM_CHUNK;
	chunk = (mapchunk + BITMAP_CHUNK) & ~(BITMAP_CHUNK - 1);
	size = (chunk / BT_NBIPUL) * sizeof (ulong_t);

	table->mt_mapchunk = mapchunk;

	ret = DiskVolsInit(&diskvols, DISKVOLS_VSN_DICT, program_name);
	if (diskvols == NULL || ret != 0) {
		return (retval);
	}

	ret = diskvols->Open(diskvols, DISKVOLS_RDONLY);
	if (ret == 0) {
		media = sam_atomedia("dk");
		SET_FLAG(table->mt_flags, MT_diskarchive);

		(void) diskvols->BeginIterator(diskvols);
		while (diskvols->GetIterator(diskvols,
		    &volname, &dv) == 0) {
			entry = MediaFind(table, media, volname);
			if (entry == NULL) {
				Trace(TR_MISC, "Failed to find volume dk.%s",
				    volname);
				retval = -1;
				continue;
			}

			entry->me_maxseqnum = getMaxseqnum(volname, dv);
			Trace(TR_MISC, "[%s] Volume '%s' max seqnum: %lld",
			    name, volname, entry->me_maxseqnum);

			/*
			 * Failed to find or access disk volume seqnum.
			 * Disk volume will be marked noexist.
			 */
			if (entry->me_maxseqnum == -1) {
				Log(20404, volname);
				continue;
			}
			entry->me_mapsize = size;
			createBitmap(entry, size);
			CLEAR_FLAG(entry->me_flags, ME_noexist);
		}
		(void) diskvols->EndIterator(diskvols);
		(void) diskvols->Close(diskvols);
	}
	(void) DiskVolsDestroy(diskvols);

	return (retval);
}

/*
 * Find or insert the specified media/vsn into the media table.  Entries
 * are hashed.
 */
MediaEntry_t *
MediaFind(
	MediaTable_t *table,
	media_t media,
	char *vsn)
{
	int hash_l = strlen(vsn);	/* length of VSN to be hashed */
	int hash_i;			/* index into VSN being hashed */
	int hash_shift = 0; 		/* # of bits to shift the current */
					/*   char by during hashing */
	unsigned int hash_v = 0;	/* hashed value of VSN */
	MediaEntry_t *newEntry;

#if 0
	Trace(TR_MISC, "Find vsn %s.%s in table 0x%x",
	    sam_mediatoa(media), vsn, table);
#endif

	/*
	 * Allocate and fill hash table with all "empty" flags.
	 */
	if (table->mt_hashTable == NULL) {
		table->mt_hashSize = HASH_INITIAL_SIZE;
		table->mt_hashTable = (int *)malloc(HASH_INITIAL_SIZE *
		    sizeof (int));
		for (hash_i = 0; hash_i < table->mt_hashSize; hash_i++) {
			table->mt_hashTable[hash_i] = HASH_EMPTY;
		}
	}

	/*
	 * Generate a hash key by summing C[i]<<i, for all i in the VSN,
	 * and taking that total modulo the hash table size.  Be careful
	 * to make the final result positive by declaring hash_v unsigned.
	 */
	hash_v = 0;
	for (hash_i = 0; hash_i < hash_l; hash_i++) {
		hash_v += vsn[hash_i]<<hash_shift;
		hash_shift = (hash_shift + 1) % 8;
	}
	hash_v %= table->mt_hashSize;

	/*
	 * Look through the hash table until we either get a HASH_EMPTY
	 * (which means that the VSN is new) or we find a match.
	 */
	while (table->mt_hashTable[hash_v] != HASH_EMPTY) {
		MediaEntry_t *trial;

		trial = &table->mt_data[table->mt_hashTable[hash_v]];
		if ((trial->me_type == media) &&
		    (strcmp(trial->me_name, vsn) == 0)) {
			return (trial);   /* Found a match */
		}

		hash_v++;
		if (hash_v >= table->mt_hashSize) {
			hash_v = 0;
		}
	}

	/*
	 * Need to add new entry.
	 */
	PthreadMutexLock(&table->mt_mutex);

	if (table->mt_tableUsed >= table->mt_tableAvail) {
		int i, new_size, new_hashSize;

		/*
		 * Allocate new sizes.
		 */
		new_size = table->mt_tableAvail + TABLE_INCREMENT;
		new_hashSize = table->mt_hashSize +
		    (TABLE_INCREMENT * HASH_MULTIPLIER);

		table->mt_data = (MediaEntry_t *)hashResize(
		    (void *) table->mt_data,
		    (size_t)(new_size * sizeof (MediaEntry_t)), "hash data");

		table->mt_hashPermute =
		    (int *)hashResize((void *) table->mt_hashPermute,
		    (size_t)(new_size * sizeof (int)), "hash permute");

		table->mt_hashTable =
		    (int *)hashResize((void *) table->mt_hashTable,
		    (size_t)(new_hashSize * sizeof (int)), "hash table");

		/*
		 * Re-init the newly allocated data.
		 */
		(void) memset(&table->mt_data[table->mt_tableAvail], 0,
		    sizeof (MediaEntry_t) * TABLE_INCREMENT);

		for (i = table->mt_tableAvail; i < new_size; i++) {
			table->mt_hashPermute[i] = i;
		}

		for (i = 0; i < new_hashSize; i++) {
			table->mt_hashTable[i] = HASH_EMPTY;
		}

		for (i = 0; i < new_size; i++) {
			MediaEntry_t *entry = &table->mt_data[i];

			int len = strlen(entry->me_name);
			int j;
			int shift = 0;
			unsigned int ref = 0;

			for (j = 0; j < len; j++) {
				ref += entry->me_name[j] << shift;
				shift = (shift + 1) % 8;
			}
			ref %= new_hashSize;
			while (table->mt_hashTable[ref] != HASH_EMPTY) {
				ref++;
				if (ref >= new_hashSize)
					ref = 0;
			}
			table->mt_hashTable[ref] = i;
		}

		/*
		 * Reset the avail counter.
		 */
		table->mt_tableAvail += TABLE_INCREMENT;
		table->mt_hashSize = new_hashSize;
	}

	/*
	 * Point hash table to this new table entry.
	 */
	table->mt_hashTable[hash_v] = table->mt_tableUsed;

	/*
	 * Insert new VSN table entry.
	 */
	newEntry = &table->mt_data[table->mt_tableUsed];

	newEntry->me_type = media;
	(void) strcpy(newEntry->me_name, vsn);
	SET_FLAG(newEntry->me_flags, ME_noexist);

	Trace(TR_SAMDEV, "Insert VSN entry '%s.%s' 0x%x",
	    sam_mediatoa(media), vsn, (int)newEntry);

	table->mt_tableUsed++;
	PthreadMutexUnlock(&table->mt_mutex);

	return (newEntry);
}

DiskVolumeSeqnum_t
MediaGetSeqnum(
	MediaTable_t *table)
{
	int i;
	DiskVolumeSeqnum_t maxseqnum;
	MediaEntry_t *entry;

	Trace(TR_SAMDEV, "Get max seqnum");
	maxseqnum = -1;
	for (i = 0; i < table->mt_tableUsed; i++) {
		entry = &table->mt_data[i];

		if (entry->me_type == DT_DISK) {
			Trace(TR_SAMDEV, "\t'%s:%s': %lld",
			    sam_mediatoa(entry->me_type),
			    entry->me_name, entry->me_maxseqnum);
			if (entry->me_maxseqnum > maxseqnum) {
				maxseqnum = entry->me_maxseqnum;
			}
		}
	}
	Trace(TR_SAMDEV, "Max seqnum: %lld", maxseqnum);
	return (maxseqnum);
}

void
MediaSetSeqnum(
	MediaTable_t *table,
	DiskVolumeSeqnum_t min,
	boolean_t diskArchive)
{
	int i;
	MediaEntry_t *entry;

	Trace(TR_MISC, "[%s] Set seqnum scan range: %lld:%lld",
	    table->mt_name, min, min + table->mt_mapchunk - 1);

	table->mt_diskArchive = diskArchive;

	for (i = 0; i < table->mt_tableUsed; i++) {
		entry = &table->mt_data[i];

		entry->me_files = 0;
		if (entry->me_type == DT_DISK) {
			(void) memset(entry->me_bitmap, 0, entry->me_mapsize);
			table->mt_mapmin = min;
		}
	}
}

void
MediaDebug(
	MediaTable_t *table)
{
	int i;
	MediaEntry_t *entry;
	DiskVolumeSeqnum_t seqnum;
	int idx;
	upath_t fullpath;
	boolean_t diskArchive;
	boolean_t noexist;
	boolean_t ignore;
	char *media;
	char *name;

	Trace(TR_MISC, "[%s] Media table dump, seqnum candidates: %lld-%lld",
	    table->mt_name, table->mt_mapmin,
	    table->mt_mapmin + table->mt_mapchunk - 1);

	diskArchive = table->mt_diskArchive;
	if (diskArchive == B_TRUE) {
		Trace(TR_MISC, "  disk archive accumulation only");
	}

	for (i = 0; i < table->mt_tableUsed; i++) {
		entry = &table->mt_data[i];

		noexist = GET_FLAG(entry->me_flags, ME_noexist);
		ignore = GET_FLAG(entry->me_flags, ME_ignore);

		if ((diskArchive == B_TRUE) && (entry->me_type != DT_DISK)) {
			continue;
		}

		media = sam_mediatoa(entry->me_type);
		name = entry->me_name;

		Trace(TR_MISC, "[%s.%s] active files: %d %s %s",
		    media, name, entry->me_files,
		    noexist ? "(noexist)" : "",
		    ignore  ? "(ignore)"  : "");

		if ((entry->me_type == DT_DISK) &&
		    (entry->me_bitmap != NULL) && (entry->me_files != 0)) {

			for (idx = 0; idx <= table->mt_mapchunk; idx++) {
				if (BT_TEST(entry->me_bitmap, idx) == 0) {
					seqnum = table->mt_mapmin + idx;
					if (seqnum <= entry->me_maxseqnum) {
						(void) DiskVolsGenFileName(
						    seqnum, fullpath,
						    sizeof (fullpath));
						Trace(TR_MISC,
						    "[%s.%s] no active files: "
						    "%s (%lld)",
						    media, name, fullpath,
						    seqnum);
					}
				}
			}
		}
	}
	Trace(TR_MISC, "[%s] End media table dump", table->mt_name);
}


/*
 * Get the sequence numbers in use by currently running arcopies.
 */
SeqNumsInUse_t *
MediaGetSeqnumsInUse(
	struct sam_fs_info *firstFs,
	int numFs,
	MediaTable_t *table
)
{
	int i, j;
	MediaEntry_t *entry;
	SeqNumsInUse_t *inuse = NULL;
	struct sam_fs_info *fsp;

	for (i = 0, fsp = firstFs; i < numFs; i++, fsp++) {
		for (j = 0; j < table->mt_tableUsed; j++) {
			entry = &table->mt_data[j];
			if (entry->me_type == DT_DISK) {
				inuse = GetSeqNumsInUse(entry->me_name,
				    fsp->fi_name, inuse);
			}
		}
	}

	return (inuse);
}


/*
 * Resize hash table.
 */
static void *
hashResize(
	void *ptr,
	size_t size,
	char *err_name)
{
	void *new_ptr;

	if ((new_ptr = realloc(ptr, size)) == NULL) {
		Trace(TR_MISC, "Error: realloc failed '%s'", err_name);
		abort();
	}
	return (new_ptr);
}

/*
 * Get recycle sequence number for diskvols.seqnum file for specified
 * disk archive volume.
 */
static DiskVolumeSeqnum_t
getMaxseqnum(
	char *volname,
	DiskVolumeInfo_t *dv)
{
	struct DiskVolumeSeqnumFile buf;
	SamrftImpl_t *rft;
	int nbytes;
	int ret;
	DiskVolumeSeqnum_t seqnum;
	size_t size;
	char *host;
	upath_t fullpath;
	char *filename;

	seqnum = -1;

	filename = DISKVOLS_SEQNUM_FILENAME;
	if (DISKVOLS_IS_HONEYCOMB(dv)) {
		filename = volname;
	}
	(void) snprintf(fullpath, sizeof (fullpath), "%s/%s.%s",
	    dv->DvPath, filename, DISKVOLS_SEQNUM_SUFFIX);

	host = DiskVolsGetHostname(dv);
	rft = SamrftConnect(host);
	if (rft == NULL) {
		Trace(TR_MISC, "Error: connect to '%s' failed", host);
		return (seqnum);
	}

	ret = SamrftOpen(rft, fullpath, O_RDONLY, NULL);

	if (ret == 0) {
		ret = SamrftFlock(rft, F_RDLCK);
		if (ret < 0) {
			Trace(TR_MISC,
			    "Error: flock(F_RDLCK) failed, errno = %d", errno);
			(void) SamrftClose(rft);
			return (seqnum);
		}

		size = sizeof (struct DiskVolumeSeqnumFile);
		nbytes = SamrftRead(rft, &buf, size);
		if (nbytes == size) {
			seqnum = buf.DsRecycle;
			if (buf.DsMagic == DISKVOLS_SEQNUM_MAGIC_RE) {
				sam_bswap8(&seqnum, 1);
			} else if (buf.DsMagic != DISKVOLS_SEQNUM_MAGIC) {
				Trace(TR_MISC,
				    "[%s] Seqnum header read error'%s'"
				    ", bad magic", volname, fullpath);
				(void) SamrftClose(rft);
				return (seqnum);
			}
		} else {
			Trace(TR_MISC, "[%s] Seqnum header read error '%s'"
			    ", expected %d received %d",
			    volname, fullpath, size, nbytes);
			(void) SamrftClose(rft);
			return (seqnum);
		}

		if (SamrftFlock(rft, F_UNLCK) < 0) {
			Trace(TR_MISC,
			    "Error: flock(F_UNLCK) failed, errno= %d", errno);
		}
		(void) SamrftClose(rft);

	} else {
		Trace(TR_MISC,
		    "[%s] Cannot open diskvols seqnum file '%s', errno = %d",
		    volname, fullpath, errno);
	}

	SamrftDisconnect(rft);

	return (seqnum);
}

/*
 * Create bitmap for disk volume seqnum numbers.
 */
static void
createBitmap(
	MediaEntry_t *entry,
	size_t size)
{
	SamMalloc(entry->me_bitmap, size);
	(void) memset(entry->me_bitmap, 0, size);
}

static int
getSpacePercent(
	uint64_t space,
	uint64_t capacity)
{
	double pct;

	if (capacity == 0 || capacity == space) {
		return (0);
	}

	pct = (double)100 *
	    (((double)capacity-(double)space)/(double)capacity) + 0.5;

	return ((int)pct);
}

/*
 * Keep lint happy.
 */
#if defined(lint)
void
swapdummy(
	void *buf)
{
	sam_bswap2(buf, 1);
	sam_bswap4(buf, 1);
	sam_bswap8(buf, 1);
}
#endif /* defined(lint) */
