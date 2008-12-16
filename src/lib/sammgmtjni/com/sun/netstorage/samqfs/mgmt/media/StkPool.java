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

// ident	$Id: StkPool.java,v 1.8 2008/12/16 00:08:56 am143972 Exp $

package com.sun.netstorage.samqfs.mgmt.media;

public class StkPool {

    private int poolID;
    private int lowWaterMark;
    private long highWaterMark;
    private String overflowStatus;

    /**
     * constructor
     *
     * This constructor is made public due to the simulator.  Real mode
     * presentation/logic layer code should not need to use this constructor.
     */
    public StkPool(int poolID, long highWaterMark, int lowWaterMark,
            String overflowStatus) {

        this.poolID = poolID;
        this.lowWaterMark = lowWaterMark;
        this.highWaterMark = highWaterMark;
        this.overflowStatus = overflowStatus;

    }

    public int getPoolID() { return poolID; }
    public long getHighWaterMark() { return highWaterMark; }
    public int getLowWaterMark() { return lowWaterMark; }
    public String getOverflowStatus() { return overflowStatus; }

    public String toString() {
        String s = "poolID = " + poolID +
                ", highWaterMark = " + highWaterMark +
                ", lowWaterMark = " + lowWaterMark +
                ", overflowStatus = " + overflowStatus;
        return s;
    }
}
