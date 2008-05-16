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
 * Copyright 2008 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 *
 *    SAM-QFS_notice_end
 */

// ident	$Id: SharedFSDetailsTiledView.java,v 1.7 2008/05/16 18:38:54 am143972 Exp $

package com.sun.netstorage.samqfs.web.fs;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.view.RequestHandlingTiledViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBean;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.event.TiledViewRequestInvocationEvent;
import com.sun.netstorage.samqfs.web.model.alarm.Alarm;
import com.sun.netstorage.samqfs.web.util.BreadCrumbUtil;
import com.sun.netstorage.samqfs.web.util.Constants;
import com.sun.netstorage.samqfs.web.util.PageInfo;
import com.sun.netstorage.samqfs.web.util.TraceUtil;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * The ContainerView used to register and create children for "column"
 * elements defined in the model's XML document.
 *
 * This ContainerView demonstrates how to evoke JATO's TiledView
 * behavior, which automatically appends the current model row index
 * to each child's qualified name. This approach can be useful when
 * retrieving values from rows of text fields, for example.
 *
 * Note that "column" element children must be in a separate
 * ContainerView when implementing JATO's TiledView. Otherwise,
 * HREFs and buttons used for table actions will be incorrectly
 * indexed as if they were also tiled. This results in JATO
 * throwing RuntimeException due to malformed qualified field
 * names because the tile index is not found during a request
 * event.
 *
 * That said, a separate ContainerView object is not necessary
 * when TiledView behavior is not required. All children can be
 * registered and created in the same ContainerView. By default,
 * CCActionTable will attempt to retrieve all children from the
 * ContainerView given to the CCActionTable.setContainerView method.
 *
 */

/**
 * This class is a tiled view class for FileSystemSummary actiontable
 */

public class SharedFSDetailsTiledView extends RequestHandlingTiledViewBase {
    // Child view names (i.e. display fields).
    public static final String CHILD_HREF = "Href";

    private ArrayList updatedModelIndex = new ArrayList();

    // Action table model.
    private SharedFSDetailsModel model = null;

    /**
     * Construct an instance with the specified properties.
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public SharedFSDetailsTiledView(
        View parent, SharedFSDetailsModel model, String name) {
        super(parent, name);
        TraceUtil.initTrace();
        TraceUtil.trace3("Entering");
        this.model = model;
        registerChildren();

        // Set the primary model to evoke JATO's TiledView behavior,
        // which will automatically add the current model row index to
        // each qualified name.
        setPrimaryModel(model);
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
        model.registerChildren(this);
        TraceUtil.trace3("Exiting");
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {
        TraceUtil.trace3("Entering");
        if (model.isChildSupported(name)) {
            // Create child from action table model.
            TraceUtil.trace3("Exiting");
            return model.createChild(this, name);
        } else {
            throw new IllegalArgumentException(
                "Invalid child name [" + name + "]");
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Request Handlers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Handle request for Href component
     *  @param RequestInvocationEvent event
     */
    public void handleHrefRequest(RequestInvocationEvent event)
        throws ServletException, IOException {

        TraceUtil.trace3("Entering");
        // Since children are bound to the primary model of a
        // TiledView, the model contains the submitted value
        // associated with each row. And because the primary model is
        // always reset to the first() position during submit, we need
        // to make sure we move to the right location in the model
        // before reading values. Otherwise, we would always read
        // values from the first row only.
        if (event instanceof TiledViewRequestInvocationEvent) {
            model.setRowIndex(
                ((TiledViewRequestInvocationEvent) event).getTileNumber());
        }

        String selected = (String) getDisplayFieldValue(CHILD_HREF);

        RequestContext rq = event.getRequestContext();
        HttpServletRequest httprq = rq.getRequest();
        HttpSession session = httprq.getSession();
        session.setAttribute(Constants.SessionAttributes.FS_NAME, selected);

        ViewBean targetView = getViewBean(FSDetailsViewBean.class);
        BreadCrumbUtil.breadCrumbPathForward(
            getParentViewBean(),
            targetView,
            PageInfo.getPageInfo().getPageNumber(
                getParentViewBean().getName()));
        targetView.forwardTo(rq);
        TraceUtil.trace3("Exiting");
    }
}
