<%--
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

// ident	$Id: ISAllsetsPolicyDetailsPagelet.jsp,v 1.6 2008/12/16 00:10:42 am143972 Exp $
--%>

<%@page info="ISPolicyDetailsPagelet" language="java" %>
<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<style>
    table {border:0}
    td {height:25px; border:0}
    td.tab {text-indent:10px; white-space:nowrap}
    td.checkbox {vertical-align:center}
    div.copyDivName-1 {background:#F7F6EF}
    div.noCopyDivName-1 {background:#F7F6EF}
    div.copyDivName-3 {background:#F7F6EF}
    div.noCopyDivName-3 {background:#F7F6EF}
    div.copyDivName-0 {background:#EEEEEE}
    div.noCopyDivName-0 {background:#EEEEEE}
    div.copyDivName-2 {background:#EEEEEE}
    div.noCopyDivName-2 {background:#EEEEEE}
    div.copyDivName-4 {background:#EEEEEE}
</style>

<jato:pagelet>
<cc:i18nbundle id="samBundle"
         baseName="com.sun.netstorage.samqfs.web.resource.Resources"/>

<div style="margin:10px">
<cc:text name="instruction" bundleID="samBundle" 
    defaultValue="archiving.policy.basepolicy.instruction"/>
</div>

<table style="margin-left:10px">
<tr><td valign="top">


<table border="1" cellpadding="0" cellspacing="5">
<tr><td nowrap>
<cc:label name="copyNumberLabel" bundleID="samBundle" 
    defaultValue="archiving.copy.number"/>
</td></tr>
</table>

<div id="labelDiv" class="right">
<table cellpadding="0" cellspacing="5">
<tr><td class="tab">
<cc:label name="mediaPoolLabel" bundleID="samBundle"
    defaultValue="archiving.copy.mediapool" showRequired="true" />
</td></tr>
<tr><td class="tab">
<cc:label name="scratchPoolLabel" bundleID="samBundle"
    defaultValue="archiving.copy.scratchpool"/>
</td></tr>
</table>
</div>
</td>

<jato:tiledView name="ISAllsetsPolicyDetailsTiledView">
<td valign="top">

<table cellpadding="0" cellspacing="5" width="100%"/>
<tr><td style="text-decoration:underline;text-align:center;font-weight:bold">
<cc:text name="copyNumber" bundleID="samBundle" 
    defaultValue="archiving.copy.copyn"/>
</td></tr>
</table>

<div id="<cc:text name='copyDivName'/>" 
    class="<cc:text name='copyDivName'/>">
<table cellpadding="0" cellspacing="5">
<tr><td>
<cc:dropdownmenu name="mediaPool" bundleID="samBundle"/>
</td></tr>
<tr><td>
<cc:dropdownmenu name="scratchPool" bundleID="samBundle"/>
</td</tr>
<tr><td class="checkbox">
<cc:checkbox name="enableRecycling" bundleID="samBundle"
    label="archiving.copy.enablerecycling"/>
</td></tr>
<tr><td>
<cc:button name="editCopyOptions" bundleID="samBundle"
    defaultValue="archiving.copy.editoptions"/>
</td></tr>
</table>
</div>
</td>
</jato:tiledView>
</tr></table>

<cc:hidden name="availableCopyList" elementId="availableCopyList"/>
<cc:hidden name="tiledViewPrefix" elementId="tiledViewPrefix"/>
<cc:hidden name="uiResult" elementId="uiResult"/>
</jato:pagelet>
