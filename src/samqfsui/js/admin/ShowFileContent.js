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

// ident	$Id: ShowFileContent.js,v 1.4 2008/03/17 14:40:25 am143972 Exp $

/** 
 * This is the javascript file of Show File Content Pop Up
 */

    function getErrorMessage() {
        return document.ShowFileContentForm.
            elements["ShowFileContent.ErrorMessage"].value;
    }
    
    function validateTextField() {
        var value = document.ShowFileContentForm.
            elements["ShowFileContent.TextField"].value;
        if (isValidNum(value, "", "1", "", "60000", "") != 1) {
            alert(getErrorMessage());
            return false;
        } else {
            return true;
        }
    }

