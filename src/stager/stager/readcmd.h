/*
 * readcmd.h
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

#if !defined(READCMD_H)
#define	READCMD_H

#pragma ident "$Revision: 1.14 $"

#include "stager_lib.h"

/* Functions */
void ReadCmds();

char *GetCfgLogFile();
int GetCfgLogEvents();
char *GetCfgTraceFile();
uint32_t GetCfgTraceMask();
boolean_t IsTraceEnabled(char *keys);
long GetCfgMaxActive();
long GetCfgMaxRetries();
char *GetCfgProbeDir();
size_t GetCfgProbeBufsize();
int GetCfgNumDrives();
sam_stager_drives_t *GetCfgDrives();

#endif /* READCMD_H */
