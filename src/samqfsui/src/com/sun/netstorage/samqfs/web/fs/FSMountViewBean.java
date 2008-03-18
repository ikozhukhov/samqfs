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

// ident	$Id: FSMountViewBean.java,v 1.24 2008/03/17 14:43:33 am143972 Exp $

package com.sun.netstorage.samqfs.web.fs;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBean;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.netstorage.samqfs.mgmt.SamFSException;
import com.sun.netstorage.samqfs.mgmt.SamFSMultiHostException;
import com.sun.netstorage.samqfs.web.archive.CriteriaDetailsViewBean;
import com.sun.netstorage.samqfs.web.archive.PolicyDetailsViewBean;
import com.sun.netstorage.samqfs.web.archive.PolicySummaryViewBean;
import com.sun.netstorage.samqfs.web.util.BreadCrumbUtil;
import com.sun.netstorage.samqfs.web.util.CommonViewBeanBase;
import com.sun.netstorage.samqfs.web.util.Constants;
import com.sun.netstorage.samqfs.web.util.PageInfo;
import com.sun.netstorage.samqfs.web.util.PageTitleUtil;
import com.sun.netstorage.samqfs.web.util.SamUtil;
import com.sun.netstorage.samqfs.web.util.TraceUtil;
import com.sun.web.ui.model.CCBreadCrumbsModel;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.breadcrumb.CCBreadCrumbs;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHref;
import java.io.IOException;
import javax.servlet.ServletException;
import com.sun.netstorage.samqfs.web.util.Authorization;
import com.sun.netstorage.samqfs.web.util.SecurityManagerFactory;

/**
 * This class serves as the viewbean for all the Mount Options pages.
 * The type of FileSystem is determined by the PageSessionAttribute set by
 * the caller. The Valid values for the FileSystem are
 *      1. SharedQFS
 *      2. SharedSAMQFS
 *      3. UnsharedSAMFS
 *      4. UnsharedSAMQFS
 *      5. UnsharedQFS
 */

public class FSMountViewBean extends CommonViewBeanBase {

    // Constants to define the type of FileSystem
    public static final String TYPE_SHAREDQFS = "SharedQFS";
    public static final String TYPE_SHAREDSAMQFS = "SharedSAMQFS";
    public static final String TYPE_UNSHAREDQFS  = "UnsharedQFS";
    public static final String TYPE_UNSHAREDSAMFS  = "UnsharedSAMFS";
    public static final String TYPE_UNSHAREDSAMQFS = "UnsharedSAMQFS";

    // Page information...
    private static final String PAGE_NAME = "FSMount";
    private static final String DEFAULT_DISPLAY_URL = "/jsp/fs/FSMount.jsp";

    private static final String CHILD_BREADCRUMB = "BreadCrumb";
    private static final String CHILD_CONTAINER_VIEW = "MountOptionsView";

    // href to handle the breadcrumb
    private static final String CHILD_FS_SUM_HREF = "FileSystemSummaryHref";
    private static final String CHILD_FS_DET_HREF = "FileSystemDetailsHref";
    private static final String CHILD_FS_ARCH_POL_HREF = "FSArchivePolicyHref";
    public static final String CHILD_SHARED_FS_HREF = "SharedFSDetailsHref";

    // handler for archive pages (v44)
    public static final String POLICY_SUMMARY_HREF   = "PolicySummaryHref";
    public static final String POLICY_DETAILS_HREF   = "PolicyDetailsHref";
    public static final String CRITERIA_DETAILS_HREF = "CriteriaDetailsHref";

    private CCPageTitleModel pageTitleModel = null;

    /**
     * Constructor
     */
    public FSMountViewBean() {
        super(PAGE_NAME, DEFAULT_DISPLAY_URL);
        TraceUtil.initTrace();
        pageTitleModel = createPageTitleModel();
        registerChildren();
        TraceUtil.trace3("Exiting");
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        TraceUtil.trace3("Entering");
        super.registerChildren();
        PageTitleUtil.registerChildren(this, pageTitleModel);
        registerChild(CHILD_CONTAINER_VIEW, FSMountView.class);
        registerChild(CHILD_BREADCRUMB, CCBreadCrumbs.class);
        registerChild(CHILD_FS_SUM_HREF, CCHref.class);
        registerChild(CHILD_FS_DET_HREF, CCHref.class);
        registerChild(CHILD_FS_ARCH_POL_HREF, CCHref.class);
        registerChild(CHILD_SHARED_FS_HREF, CCHref.class);
        registerChild(POLICY_SUMMARY_HREF, CCHref.class);
        registerChild(POLICY_DETAILS_HREF, CCHref.class);
        registerChild(CRITERIA_DETAILS_HREF, CCHref.class);
        TraceUtil.trace3("Exiting");
    }

    /**
     * Instantiate each child view.
     *
     * @param name The name of the child view
     * @return View The instantiated child view
     */
    protected View createChild(String name) {
        TraceUtil.trace3("Entering");
        if (super.isChildSupported(name)) {
            TraceUtil.trace3("Exiting");
            return super.createChild(name);
        // Propertysheet Container.
        } else if (name.equals(CHILD_CONTAINER_VIEW)) {
            FSMountView child = new FSMountView(this, name);
            return child;
        // PageTitle Child
        } else if (PageTitleUtil.isChildSupported(pageTitleModel, name)) {
            TraceUtil.trace3("Exiting");
            return PageTitleUtil.createChild(this, pageTitleModel, name);
        // Breadcrumb
        } else if (name.equals(CHILD_BREADCRUMB)) {
            CCBreadCrumbsModel model =
                new CCBreadCrumbsModel("FSMountParams.pageTitle");
            BreadCrumbUtil.createBreadCrumbs(this, name, model);
            CCBreadCrumbs child = new CCBreadCrumbs(this, model, name);
            TraceUtil.trace3("Exiting");
            return child;
        // Breadcrumb HREFs
        } else if (name.equals(CHILD_FS_SUM_HREF) ||
                   name.equals(CHILD_FS_DET_HREF) ||
                   name.equals(CHILD_SHARED_FS_HREF) ||
                   name.equals(POLICY_SUMMARY_HREF) ||
                   name.equals(POLICY_DETAILS_HREF) ||
                   name.equals(CRITERIA_DETAILS_HREF) ||
                   name.equals(CHILD_FS_ARCH_POL_HREF)) {
            TraceUtil.trace3("Exiting");
            return new CCHref(this, name, null);
        } else
            throw new IllegalArgumentException(
                "Invalid child name [" + name + "]");
    }

    /**
     * Called as notification that the JSP has begun its display processing
     * @param event The DisplayEvent
     */
    public void beginDisplay(DisplayEvent event) throws ModelControlException {

        TraceUtil.trace3("Entering");

        String hostName = (String) getServerName();

        // disable save/reset/cancel buttons if no filesystem permission
        if (!SecurityManagerFactory.getSecurityManager().
            hasAuthorization(Authorization.FILESYSTEM_OPERATOR)) {

            ((CCButton)getChild("SaveButton")).setDisabled(true);
            ((CCButton)getChild("ResetButton")).setDisabled(true);
            ((CCButton)getChild("CancelButton")).setDisabled(true);
        }

        String sharedMetaServer = (String) getPageSessionAttribute(
            Constants.SessionAttributes.SHARED_METADATA_SERVER);

        String sharedMetaClient = (String) getPageSessionAttribute(
            Constants.SessionAttributes.SHARED_METADATA_CLIENT);

        TraceUtil.trace3("client = " +sharedMetaClient);
        TraceUtil.trace3("server = " +sharedMetaServer);

        if (sharedMetaClient != null && sharedMetaServer != null) {
            String [] str = new
                String[] {getFSName(), sharedMetaClient,
                sharedMetaServer};
            pageTitleModel.setPageTitleText(
                SamUtil.getResourceString(
                    "FSMountParams.pageTitle2",
                    (String[]) str));
        } else {
            pageTitleModel.setPageTitleText(
                SamUtil.getResourceString(
                    "FSMountParams.pageTitle1",
                    getFSName()));
        }

        try {
            FSMountView mountView = (FSMountView)getChild(CHILD_CONTAINER_VIEW);
            mountView.loadPropertySheetModel();
        } catch (SamFSException ex) {
            SamUtil.processException(
                ex,
                this.getClass(),
                "loadPropertySheetModel()",
                "failed to populate data",
                hostName);

            SamUtil.setErrorAlert(
                getParentViewBean(),
                CHILD_COMMON_ALERT,
                "FSMountParams.error.failedPopulate",
                ex.getSAMerrno(),
                ex.getMessage(),
                getServerName());
        }

        TraceUtil.trace3("Exiting");
    }

    /**
     * Create pagetitle model
     */
    private CCPageTitleModel createPageTitleModel() {
        TraceUtil.trace3("Entering");
        if (pageTitleModel == null) {
            pageTitleModel =
                PageTitleUtil.createModel("/jsp/fs/FSMountPageTitle.xml");
        }

        TraceUtil.trace3("Exiting");
        return pageTitleModel;
    }

    /**
     * Handle request for backto fs link
     * @param event RequestInvocationEvent event
     */
    public void handleFileSystemSummaryHrefRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        ViewBean targetView = getViewBean(FSSummaryViewBean.class);

        String hostName = getServerName();
        TraceUtil.trace3("set page session for hostName " + hostName);
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.SAMFS_SERVER_NAME, hostName);
        TraceUtil.trace3("set page session for fsName " + getFSName());
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.FILE_SYSTEM_NAME, getFSName());

        String s = (String) getDisplayFieldValue("FileSystemSummaryHref");
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            targetView,
            PageInfo.getPageInfo().getPageNumber(targetView.getName()), s);
        targetView.forwardTo(getRequestContext());
        TraceUtil.trace3("Exiting");
    }


    /**
     * Handle request for back to FS detail href
     * @param event RequestInvocationEvent event
     */
    public void handleSharedFSDetailsHrefRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        String s = (String) getDisplayFieldValue("SharedFSDetailsHref");
        ViewBean targetView = getViewBean(SharedFSDetailsViewBean.class);
        String hostName = (String)
            getPageSessionAttribute(
            Constants.PageSessionAttributes.SAMFS_SERVER_NAME);
        TraceUtil.trace3("set page session for hostName " +
            hostName);
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.SAMFS_SERVER_NAME, hostName);
        String fileSystemName = getFSName();
        TraceUtil.trace3("set page session for fsName " + fileSystemName);
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.FILE_SYSTEM_NAME, fileSystemName);
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            targetView,
            PageInfo.getPageInfo().getPageNumber(targetView.getName()), s);
        targetView.forwardTo(getRequestContext());
        TraceUtil.trace3("Exiting");
    }


    /**
     * Handle request for backto detail page link
     * @param event RequestInvocationEvent event
     */
    public void handleFileSystemDetailsHrefRequest(RequestInvocationEvent event)
        throws ServletException, IOException {
        TraceUtil.trace3("Entering");
        String s = (String) getDisplayFieldValue("FileSystemDetailsHref");
        ViewBean targetView = getViewBean(FSDetailsViewBean.class);

        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            PageInfo.getPageInfo().getPageNumber(targetView.getName()), s);
        forwardTo(targetView);
        TraceUtil.trace3("Exiting");
    }

    public void handleFSArchivePolicyHrefRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        String s = (String) getDisplayFieldValue("FSArchivePolicyHref");
        ViewBean targetView =
            getViewBean(FSArchivePoliciesViewBean.class);
        String hostName = (String)
            getPageSessionAttribute(
            Constants.PageSessionAttributes.SAMFS_SERVER_NAME);
        TraceUtil.trace3("set page session for hostName " +
            hostName);
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.SAMFS_SERVER_NAME, hostName);
        String fileSystemName = getFSName();
        TraceUtil.trace3("set page session for fsName " + fileSystemName);
        targetView.setPageSessionAttribute(
            Constants.PageSessionAttributes.FILE_SYSTEM_NAME, fileSystemName);
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            targetView,
            PageInfo.getPageInfo().getPageNumber(targetView.getName()), s);
        targetView.forwardTo(getRequestContext());
        TraceUtil.trace3("Exiting");
    }

    /**
     * Handle request for Button 'Save'
     */
    public void handleSaveButtonRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        boolean error = false;
        String sharedMetaClient = (String) getPageSessionAttribute(
                Constants.SessionAttributes.SHARED_METADATA_CLIENT);
        try {
            FSMountView mountView = (FSMountView)getChild(CHILD_CONTAINER_VIEW);
            mountView.handleSaveButtonRequest(event, sharedMetaClient);
        } catch (SamFSMultiHostException e) {
            error = true;
            SamUtil.doPrint(new StringBuffer().
                append("error code is ").
                append(e.getSAMerrno()).toString());
            String err_msg = SamUtil.handleMultiHostException(e);
            SamUtil.setErrorAlert(
                this,
                CHILD_COMMON_ALERT,
                "FSMountParams.error.save",
                e.getSAMerrno(),
                err_msg,
                getServerName());
        } catch (SamFSException ex) {
            error = true;
            SamUtil.processException(
                ex,
                this.getClass(),
                "handleSaveButtonRequest()",
                "Failed to save data",
                sharedMetaClient);

            SamUtil.setErrorAlert(
                this,
                CHILD_COMMON_ALERT,
                "FSMountParams.error.save",
                ex.getSAMerrno(),
                ex.getMessage(),
                getServerName());
        }

        if (error) {
            this.forwardTo();
        } else {
            forwardToTargetPage(true);
        }
        TraceUtil.trace3("Exiting");
    }

    /**
     * Handle request for Button 'Cancel'
     */
    public void handleCancelButtonRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        forwardToTargetPage(false);
        TraceUtil.trace3("Exiting");
    }

    private void forwardToTargetPage(boolean showAlert) {

        ViewBean targetView = null;
        String s = null;

        Integer[] temp = (Integer [])
            getPageSessionAttribute(Constants.SessionAttributes.PAGE_PATH);
        Integer[] path = BreadCrumbUtil.getBreadCrumbDisplay(temp);

        int index = path[path.length-1].intValue();

        PageInfo pageInfo = PageInfo.getPageInfo();
        String targetName = pageInfo.getPagePath(index).getCommandField();

        TraceUtil.trace3("target name = " + targetName);
        if (targetName.equals("FileSystemDetailsHref")) {
            targetView = getViewBean(FSDetailsViewBean.class);
            s = Integer.toString(
                BreadCrumbUtil.inPagePath(path, index, path.length-1));
        } else if (targetName.equals("SharedFSDetailsHref")) {
            targetView = getViewBean(SharedFSDetailsViewBean.class);
            s = Integer.toString(
                BreadCrumbUtil.inPagePath(path, index, path.length-1));
            TraceUtil.trace3("SharedFSDetails target ");
        } else if (targetName.equals("FileSystemSummaryHref")) {
            targetView = getViewBean(FSSummaryViewBean.class);
            removePageSessionAttribute(
                Constants.PageSessionAttributes.FILE_SYSTEM_NAME);
            s = Integer.toString(
                BreadCrumbUtil.inPagePath(path, index, path.length-1));
            TraceUtil.trace3("FS Summary target ");
        }

        if (showAlert) {
            showAlert(targetView);
        }

        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            PageInfo.getPageInfo().getPageNumber(targetView.getName()), s);
        forwardTo(targetView);
    }

    private void showAlert(ViewBean targetView) {
        TraceUtil.trace3("Entering");
        SamUtil.setInfoAlert(
            targetView,
            "Alert",
            "success.summary",
            "FSMountParams.save.success",
            getServerName());
        TraceUtil.trace3("Exiting");
    }

    /**
     * Function to setup the inline alert
     */
    private void showAlert() {
        TraceUtil.trace3("Entering");
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_COMMON_ALERT);
        alert.setSummary("FSMountParams.alertElement");
        alert.setDetail("FSMountParams.alertElementDetail");
        TraceUtil.trace3("Exiting");
    }

    // handle breadcrumb to the policy summary page
    public void handlePolicySummaryHrefRequest(RequestInvocationEvent evt)
        throws ServletException, IOException {
        String s = (String)getDisplayFieldValue(POLICY_SUMMARY_HREF);
        ViewBean target = getViewBean(PolicySummaryViewBean.class);

        // breadcrumb
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            PageInfo.getPageInfo().getPageNumber(target.getName()), s);

        forwardTo(target);
    }

    // handle breadcrumb to the policy details summary page
    public void handlePolicyDetailsHrefRequest(RequestInvocationEvent evt)
        throws ServletException, IOException {
        String s = (String)getDisplayFieldValue(POLICY_DETAILS_HREF);
        ViewBean target = getViewBean(PolicyDetailsViewBean.class);

        // breadcrumb
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            PageInfo.getPageInfo().getPageNumber(target.getName()), s);

        forwardTo(target);
    }

    // handle breadcrumb to the criteria details summary page
    public void handleCriteriaDetailsHrefRequest(
        RequestInvocationEvent evt)
        throws ServletException, IOException {
        String s = (String)getDisplayFieldValue(CRITERIA_DETAILS_HREF);
        ViewBean target = getViewBean(CriteriaDetailsViewBean.class);

        // breadcrumb
        BreadCrumbUtil.breadCrumbPathBackward(
            this,
            PageInfo.getPageInfo().getPageNumber(target.getName()), s);

        forwardTo(target);
    }

    private String getFSName() {
        return (String) getPageSessionAttribute(
            Constants.PageSessionAttributes.FILE_SYSTEM_NAME);
    }
}
