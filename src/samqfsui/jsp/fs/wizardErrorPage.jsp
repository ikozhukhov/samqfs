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

// ident	$Id: wizardErrorPage.jsp,v 1.10 2008/12/16 00:10:47 am143972 Exp $
--%>

<%@ page language="java" %> 
<%@ page import="com.iplanet.jato.view.ViewBean" %>
<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%> 
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<script type="text/javascript">
  function wizardPageInit() {
      var disabled = true;
      WizardWindow_Wizard.setNextButtonDisabled(disabled, null);
      WizardWindow_Wizard.setPreviousButtonDisabled(disabled, null);
      WizardWindow_Wizard.setFinishButtonDisabled(disabled, null);
   }
   WizardWindow_Wizard.pageInit = wizardPageInit;

</script>

<jato:pagelet>
    
    <cc:i18nbundle id="samBundle"
                   baseName="com.sun.netstorage.samqfs.web.resources.Resources" />
    
    <%-- For now assume we're still presenting the components in a table
     which is output by the framework and components are
     in rows and cells
    --%>
    <tr>
        <td>
            <cc:alertinline name="Alert" bundleID="samBundle" /><br />
        </td>
    </tr>
    
</jato:pagelet>
