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

// ident	$Id: ReservationSummaryView.java,v 1.11 2008/03/17 14:43:41 am143972 Exp $

package com.sun.netstorage.samqfs.web.media.wizards;

import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;

import com.sun.netstorage.samqfs.web.util.Constants;
import com.sun.netstorage.samqfs.web.util.TraceUtil;
import com.sun.netstorage.samqfs.web.util.SamUtil;
import com.sun.netstorage.samqfs.web.wizard.SamWizardModel;

import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.wizard.CCWizardPage;
import com.sun.web.ui.view.alert.CCAlertInline;


/**
 * A ContainerView object for the pagelet
 *
 */
public class ReservationSummaryView extends RequestHandlingViewBase
    implements CCWizardPage {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "ReservationSummaryView";

    // Child view names (i.e. display fields).

    public static final String CHILD_LABEL = "Label";
    public static final String CHILD_METHOD_FIELD = "methodValue";
    public static final String CHILD_FS_FIELD = "fsValue";
    public static final String CHILD_POLICY_FIELD = "policyValue";
    public static final String CHILD_OWNER_FIELD = "ownerValue";
    public static final String CHILD_GROUP_FIELD = "groupValue";
    public static final String CHILD_DIR_FIELD = "dirValue";
    public static final String CHILD_ALERT = "Alert";

    private boolean previousError = false;

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public ReservationSummaryView(View parent, Model model) {
        this(parent, model, PAGE_NAME);
    }

    public ReservationSummaryView(View parent, Model model, String name) {
        super(parent, name);
        // The wizard framework will call this constructor with
        // the model that was returned from the Wizard method
        // getPageModel(currentPageId). If the wizard does not
        // return a model, an instance of DefaultModel will be
        // returned.
        //
        // Alternatively the application can set any Model
        // here.

        // There are still issues with linking WizardState
        // and the individual wizard pages
        // If extend is required by the framework then
        // this can be part of CCWizardPage
        //
        TraceUtil.initTrace();
        setDefaultModel(model);

        registerChildren();
        TraceUtil.trace3("Exiting");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Child manipulation methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        TraceUtil.trace3("Entering");
        registerChild(CHILD_LABEL, CCLabel.class);
        registerChild(CHILD_METHOD_FIELD, CCStaticTextField.class);
        registerChild(CHILD_FS_FIELD, CCStaticTextField.class);
        registerChild(CHILD_OWNER_FIELD, CCStaticTextField.class);
        registerChild(CHILD_POLICY_FIELD, CCStaticTextField.class);
        registerChild(CHILD_DIR_FIELD, CCStaticTextField.class);
        registerChild(CHILD_GROUP_FIELD, CCStaticTextField.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        TraceUtil.trace3("Exiting");
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {
        TraceUtil.trace3("Entering");
        View child = null;
        if (name.equals(CHILD_LABEL)) {
            child = new CCLabel(this, name, null);
        } else if (name.equals(CHILD_METHOD_FIELD) ||
            name.equals(CHILD_FS_FIELD) ||
            name.equals(CHILD_POLICY_FIELD) ||
            name.equals(CHILD_OWNER_FIELD) ||
            name.equals(CHILD_DIR_FIELD) ||
            name.equals(CHILD_GROUP_FIELD)) {
            child = new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline myChild = new CCAlertInline(this, name, null);
            myChild.setValue(CCAlertInline.TYPE_ERROR);
            child = (View) myChild;
        } else {
            throw new IllegalArgumentException(
                "ReservationSummaryView : Invalid child name [" + name + "]");
        }
        TraceUtil.trace3("Exiting");
        return child;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // CCWizardBody methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        TraceUtil.trace3("Entering");

        String url = null;
        if (!previousError) {
            url = "/jsp/media/wizards/ReservationSummary.jsp";
        } else {
            url = "/jsp/util/WizardErrorPage.jsp";
        }

        TraceUtil.trace3("Exiting");
        return url;
    }

    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        TraceUtil.trace3("Entering");
        super.beginDisplay(event);

        SamWizardModel wm = (SamWizardModel) getDefaultModel();

        String serverName = (String) wm.getValue(Constants.Wizard.SERVER_NAME);
        String err = (String) wm.getValue(Constants.Wizard.WIZARD_ERROR);
        if (err != null && err.equals(Constants.Wizard.WIZARD_ERROR_YES)) {
            String msgs = (String) wm.getValue(Constants.Wizard.ERROR_MESSAGE);
            int code = Integer.parseInt(
                (String) wm.getValue(Constants.Wizard.ERROR_CODE));
            String errorSummary = "Reservation.error.carryover";
            previousError = true;
            String errorDetails =
                (String) wm.getValue(Constants.Wizard.ERROR_DETAIL);

            if (errorDetails != null) {
                errorSummary = (String)
                    wm.getValue(Constants.Wizard.ERROR_SUMMARY);

                if (errorDetails.equals(Constants.Wizard.ERROR_INLINE_ALERT)) {
                    previousError = false;
                } else {
                    previousError = true;
                }
            }

            if (previousError) {
                SamUtil.setErrorAlert(
                    this,
                    CHILD_ALERT,
                    errorSummary,
                    code,
                    msgs,
                    serverName);
            } else {
                SamUtil.setWarningAlert(
                    this,
                    CHILD_ALERT,
                    errorSummary,
                    msgs);
            }
        }
        TraceUtil.trace3("Exiting");
    }
}
