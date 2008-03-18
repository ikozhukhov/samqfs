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

// ident	$Id: ReservationFSView.java,v 1.19 2008/03/17 14:43:41 am143972 Exp $

package com.sun.netstorage.samqfs.web.media.wizards;

import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.util.NonSyncStringBuffer;
import com.sun.web.ui.view.html.CCRadioButton;

import com.sun.web.ui.view.wizard.CCWizardPage;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCHiddenField;

import com.sun.netstorage.samqfs.web.util.TraceUtil;
import com.sun.netstorage.samqfs.mgmt.SamFSException;
import com.sun.netstorage.samqfs.web.util.SamUtil;
import com.sun.netstorage.samqfs.web.model.SamQFSSystemModel;
import com.sun.netstorage.samqfs.web.model.fs.FileSystem;
import com.sun.netstorage.samqfs.web.util.Constants;
import com.sun.netstorage.samqfs.web.wizard.SamWizardModel;

/**
 * A ContainerView object for the pagelet for Reservation FS View.
 *
 */
public class ReservationFSView extends RequestHandlingViewBase
    implements CCWizardPage {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "ReservationFSView";

    // Child view names (i.e. display fields).
    public static final String CHILD_ACTIONTABLE = "SelectFSTable";
    public static final String CHILD_ALERT = "Alert";
    public static final String CHILD_ERROR = "errorOccur";

    private CCActionTableModel tableModel;
    private boolean error = false;
    private boolean previousError = false;

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public ReservationFSView(View parent, Model model) {
        this(parent, model, PAGE_NAME);
    }

    public ReservationFSView(View parent, Model model, String name) {
        super(parent, name);
        TraceUtil.initTrace();
        setDefaultModel(model);

        createActionTableHeader();
        try {
            createActionTableModel();
        } catch (SamFSException ex) {
            SamUtil.processException(
                ex,
                this.getClass(),
                "ReservationFSView()",
                "Failed to populate fs data",
                getServerName());
            error = true;
            SamUtil.setErrorAlert(
                this,
                ReservationFSView.CHILD_ALERT,
                "ReservationFS.error.populate",
                ex.getSAMerrno(),
                ex.getMessage(),
                getServerName());
        }

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
        registerChild(CHILD_ACTIONTABLE, CCActionTable.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_ERROR, CCHiddenField.class);
        tableModel.registerChildren(this);
        TraceUtil.trace3("Exiting");
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {
        TraceUtil.trace3(new NonSyncStringBuffer().append("Entering: name is ").
            append(name).toString());

        View child = null;
        if (name.equals(CHILD_ACTIONTABLE)) {
            child = new CCActionTable(this, tableModel, name);
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline myChild = new CCAlertInline(this, name, null);
            myChild.setValue(CCAlertInline.TYPE_ERROR);
            child = (View) myChild;
        } else if (name.equals(CHILD_ERROR)) {
            if (error) {
                child = new CCHiddenField(
                    this, name, Constants.Wizard.EXCEPTION);
            } else {
                child = new CCHiddenField(this, name, Constants.Wizard.SUCCESS);
            }
        } else if (tableModel.isChildSupported(name)) {
            child = tableModel.createChild(this, name);
        } else {
            throw new IllegalArgumentException(
                "ReservationFSView : Invalid child name [" + name + "]");
        }

        TraceUtil.trace3("Exiting");
        return child;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // CCWizardPage methods
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
            url = "/jsp/media/wizards/ReservationFS.jsp";
        } else {
            url = "/jsp/util/WizardErrorPage.jsp";
        }

        TraceUtil.trace3("Exiting");
        return url;
    }

    public void beginDisplay(DisplayEvent event)
        throws ModelControlException {
        TraceUtil.trace3("Entering");
        super.beginDisplay(event);

        // Disable Tooltip
        CCActionTable myTable = (CCActionTable) getChild(CHILD_ACTIONTABLE);
        CCRadioButton myRadio = (CCRadioButton) myTable.getChild(
            CCActionTable.CHILD_SELECTION_RADIOBUTTON);
        myRadio.setTitle("");
        myRadio.setTitleDisabled("");

        if (error) {
            ((CCHiddenField) getChild(CHILD_ERROR)).setValue(
                Constants.Wizard.EXCEPTION);
        } else {
            ((CCHiddenField) getChild(CHILD_ERROR)).setValue(
                Constants.Wizard.SUCCESS);
        }

        SamWizardModel wm = (SamWizardModel) getDefaultModel();

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
                    getServerName());
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

    private void createActionTableHeader() {
        tableModel = new CCActionTableModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/media/wizards/ReservationSelectFSTable.xml");
        tableModel.clear();

        tableModel.setActionValue(
            "NameColumn",
            "ReservationFS.selectfstable.heading.name");
        tableModel.setActionValue(
            "MountPointColumn",
            "ReservationFS.selectfstable.heading.mountpoint");
    }

    private void createActionTableModel() throws SamFSException {
        TraceUtil.trace3("Entering");

        SamWizardModel wm = (SamWizardModel) getDefaultModel();
        String fsName = (String) wm.getValue(
            ReservationSummaryView.CHILD_FS_FIELD);

        SamQFSSystemModel sysModel = SamUtil.getModel(getServerName());
        FileSystem[] allFS =
            sysModel.getSamQFSSystemFSManager().getAllFileSystems();

        if (allFS == null) {
            throw new SamFSException(null, -1000);
        }

        for (int i = 0; i < allFS.length; i++) {
            if (i > 0) {
                tableModel.appendRow();
            }

            tableModel.setValue("NameText", allFS[i].getName());
            tableModel.setValue("MountPointText", allFS[i].getMountPoint());

            if (fsName != null) {
                if (fsName.equals(allFS[i].getName())) {
                    tableModel.setRowSelected(true);
                }
            }
        }
        wm.setValue(ReservationSummaryView.CHILD_FS_FIELD, null);
    }

    private String getServerName() {
        SamWizardModel wm = (SamWizardModel) getDefaultModel();
        return (String) wm.getValue(Constants.Wizard.SERVER_NAME);
    }
}
