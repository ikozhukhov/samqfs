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

// ident    $Id: VSNWrapper.java,v 1.5 2008/12/16 00:12:23 am143972 Exp $

package com.sun.netstorage.samqfs.web.model.media;

/**
 * Wrapper of VSN [] & DiskVol [].  Either array will be populated after
 * the evaluation of VSN expression defined in the VSN Browser Pop Up.
 */

public class VSNWrapper {

    public VSN [] allTapeVSNs;
    public DiskVolume[] allDiskVSNs;
    public long freeSpaceInMB;
    public int totalNumberOfVSNs;

    // Expression used in search criteria to come up with matching VSNs
    public String expressionUsed;

    /** Creates a new instance of VSNWrapper */
    public VSNWrapper() {
    }

    public VSNWrapper(
        VSN [] vsns, DiskVolume[] diskvolumes,
        long freeSpaceInMB, int totalNumberOfVSNs,
        String expressionUsed) {
        this.allTapeVSNs = vsns;
        this.allDiskVSNs = diskvolumes;
        this.freeSpaceInMB = freeSpaceInMB;
        this.totalNumberOfVSNs = totalNumberOfVSNs;
        this.expressionUsed = expressionUsed;
    }

    public VSN [] getAllTapeVSNs() {
        return allTapeVSNs;
    }

    public DiskVolume [] getAllDiskVSNs() {
        return allDiskVSNs;
    }

    public long getFreeSpaceInMB() {
        return freeSpaceInMB;
    }

    public int getTotalNumberOfVSNs() {
        return totalNumberOfVSNs;
    }

    public String getExpressionUsed() {
        return expressionUsed;
    }
}
