<%--
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

// ident	$Id: AddLibraryDirectSelectLibrary.jsp,v 1.6 2008/03/17 14:40:37 am143972 Exp $

--%>

<%@ page language="java" %> 
<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%> 
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<script type="text/javascript"
    src="/samqfsui/js/media/wizards/AddLibraryDirectSelectLibrary.js"></script>
<script type="text/javascript">
    WizardWindow_Wizard.pageInit = wizardPageInit;
</script>

<jato:pagelet>

<cc:i18nbundle id="samBundle"
    baseName="com.sun.netstorage.samqfs.web.resources.Resources" />

<cc:alertinline name="Alert" bundleID="samBundle" />

<table>
<tr>
    <td valign="center" align="left" rowspan="1" colspan="1">
        <cc:label name="Label" defaultValue="AddLibrary.libnamelabel"
            bundleID="samBundle" elementName="DropDownMenu" />
    </td>
    <td valign="center" align="left" rowspan="1" colspan="1">
        <cc:dropdownmenu name="DropDownMenu"
            type="standard"
            onChange="handleMenuChange(this)"
            bundleID="samBundle"/>
    </td>
</tr>
<tr>
    <td valign="center" align="left" rowspan="1" colspan="1">
        <cc:label name="Label" 
            defaultValue="AddLibrary.label.name"
            bundleID="samBundle"/>
    </td>
    <td valign="center" align="left" rowspan="1" colspan="1">
        <cc:textfield name ="nameValue" 
            size="20" 
            maxLength="31"
            bundleID="samBundle" />
    </td>
</tr>
</table>

<cc:hidden name="libraryNames" />
<cc:hidden name="errorOccur" />
</jato:pagelet>
