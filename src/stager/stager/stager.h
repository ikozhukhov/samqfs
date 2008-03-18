/*
 * stager.h - Stager definitions.
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

#if !defined(STAGER_H)
#define	STAGER_H

#pragma ident "$Revision: 1.8 $"

/*
 * Define prototypes in control.c
 */
void ReconfigLock();
void ReconfigUnlock();

/*
 * Stager start mode.
 */
enum StartMode {
	SM_cold,	/* Start after clean shutdown */
	SM_failover,	/* Start after failover */
	SM_restart	/* Start after abnormal termination */
};
#endif /* STAGER_H */
