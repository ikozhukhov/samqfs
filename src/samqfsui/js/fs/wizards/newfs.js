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

// ident	$Id: newfs.js,v 1.2 2008/07/09 22:20:56 kilemba Exp $

// handlers for the new file system wizard popup

var formName = "NewFileSystemPopupForm";
var prefix = "NewFileSystemPopup.";
var QFS = "qfs";
var UFS = "ufs";

/* this variable holds the file system configuration type attributes in http
 * request parameter format i.e. &n=v&n=v to the passed to the new file system
 * wizard. The parameters to be passed are :
 * fsType -> qfs or ufs
 * archiving -> true or false
 * shared -> true or false
 * hpc -> true or false
 * fsName -> string
 * 
 * NOTE: if a true/false parameter is not passed, its value will be assumed to
 * be false. For examle, these two parameter strings are equivalent.
 * 1.) "&fsType=qfs&archiving=false" 2.) "fsType=qfs"
 */
var fsTypeParams = null;

/* an array of the existing file system names*/
var fsNames = null;

/* errors messages in the following order
 * 0 - archiving media is not configured
 * 1 - fs name is blank
 * 2 - fs name is invalid (not well-formed)
 * 3 - a fs with the given name already exists
 * 4 - warns that HPC HA is not supported
 */
var errorMessages = null;

/* return the error message at the given index */
function getErrorMessage(index) {
    var theForm = document.forms[formName];
    if (errorMessages == null) {
        // create error array
        var msgString = theForm.elements[prefix + "messages"].value;
        if (msgString != null) {
            errorMessages = msgString.split(";");
        }
    }
    
    // makes sure the index is with the message array bounds
    if (index > (1 + errorMessages.length)) {
        alert("invalid error message index " + index);
        return null;
    }

    return errorMessages[index];
}

/* return an array of the currently configured file system names
 */
function isFSNameUsed(name) {
    if (name == null)
        return false;
    
    if (fsNames == null) {
        var theForm = document.forms[formName];
        var fsNameString = theForm.elements[prefix + "existingFSNames"].value;
        if (fsNameString != null) {
            fsNames = fsNameString.split(";");
        }
    }
    
    var found  = false;
    var i = 0;
    for (i = 0; !found && i < fsNames.length; i++) {
        found = (fsNames[i] == name);
    }
    
    return found;
}

/* handle the cancel button on the new file system popup */
function handleNewFileSystemPopupCancel(button) {
    window.close();
    return false;
}

/* handle the OK button on the new file system wizard */
function handleNewFileSystemPopupOk(button) {
    var params = null;
    var theForm = document.forms[formName];
    
    // determine type of file system selected
    var fsType = null;
    var fsTypeRadioButton = theForm.elements[prefix + "QFSRadioButton"];
    // var found = false;
    for (var i = 0, found = false; !found && i < fsTypeRadioButton.length; i++) {
        if (fsTypeRadioButton[i].checked) {
            fsType = fsTypeRadioButton[i].value;
            found = true;
        }
    }

    // append the file system type to the parameter list
    params = "&fsType=" + fsType;
    
    // if qfs file system is selected, determine the archiving, shared,
    // or shared hpc configuration
    if (fsType == QFS) {
        // check for hafs
        var hafsCheckBox = theForm.elements[prefix + "HAFSCheckBox"];
        var hafs = false;
        if (hafsCheckBox != null) {
            hafs = hafsCheckBox.checked;
        }
        
        if (hafs) {
            params += "&hafs=" + hafs;
        }
        
        // check for archiving
        var archiving = theForm.elements[prefix + "archivingCheckBox"].checked;
        if (archiving) {
            params += "&archiving=" + archiving;
        }
        
        // check for shared
        var shared = theForm.elements[prefix + "sharedCheckBox"].checked;
        if (shared) {
            params += "&shared=" + shared;
            
            // check for hpc
            var hpc = theForm.elements[prefix + "HPCCheckBox"].checked;
            if (hpc) {
                var fsname = theForm.elements[prefix + "FSName"].value;
                
                // check the validity of the file system name
                if (!isFSNameValid(fsname)) {
                    return false;
                }
                
                params += "&hpc=" + hpc;
                params += "&fsname=" + fsname;
                // create the the proto fs by submitting the ok button
                // return true;
            }
        }
    }

    // update the fsTypeParams and launch the wizard
    fsTypeParams = params;
    launchNewFileSystemWizard(button);
    
    // alert("Coming soon ...");

    window.close();
    return false;
}

function isFSNameValid(fsname) {
    var theForm = document.forms[formName];
    
    // make sure the name field is not blank
    if (fsname == null || fsname.length == 0) {
        alert(getErrorMessage(1));
        return false;
    }
    
    // make sure the file system name is well formed
    if (!isValidFileSystemName(trim(fsname))) {
        alert(getErrorMessage(2));
        return false;
    }
    
    // check if a file system name with this name already exists
    if (isFSNameUsed(fsname)) {
        alert(getErrorMessage(3));
        return false;
    }
    
    // if we get this far, the file system name is ok.
    return true;
}

/* launch the wizard */
function launchNewFileSystemWizard(button) {
    var wizButtonName =
      "NewFileSystemPopup.NewFileSystemPopupView.newFileSystemWizard";
    var theForm = document.forms[formName];
    var newFSWizardButton = theForm.elements[wizButtonName];

    if (newFSWizardButton != null) {
      newFSWizardButton.click();
    } else {
      alert("new fs wizard button is null ... :-(");
    }
    
    return false;
}

/* handler for the HAFS check box */
function handleHAFSCheckBox(checkbox) {
    var theForm = document.forms[formName];
    var archiving = theForm.elements[prefix + "archivingCheckBox"];
    var hpc = theForm.elements[prefix + "HPCCheckBox"];
    var shared = theForm.elements[prefix + "sharedCheckBox"];

    if (checkbox.checked) {
        // disable archiving. Its not supported with HAFS
        archiving.checked = false;
        ccSetCheckBoxDisabled(prefix + "archivingCheckBox", formName, 1);
        
        // if shared is selected, disable the HPC check box. HAFS is not
        // supported with HPC
        if (shared.checked) {
            hpc.checked = false;
            ccSetCheckBoxDisabled(prefix + "HPCCheckBox", formName, 1);
            ccSetTextFieldDisabled(prefix + "FSName", formName, 1);
        }
    } else {
        // enable archiving
        ccSetCheckBoxDisabled(prefix + "archivingCheckBox", formName, 0);

        // if shared is checked, enable hpc
        if (shared.checked) {
            ccSetCheckBoxDisabled(prefix + "HPCCheckBox", formName, 0);
        }
    }
}

/* */
function handleArchivingCheckBox(checkbox) {

    var hafs = checkbox.form.elements[prefix + "HAFSCheckBox"];
    if (checkbox.checked) {
        // warn he user about creating an archiving file system without
        // configuring archiving media first.
        if (hasArchiveMedia() == "false") {
            alert(getErrorMessage(0));
        }

        // disable HAFS. Archiving HAFS is not supported
        if (hafs != null) {
            ccSetCheckBoxDisabled(prefix + "HAFSCheckBox", formName, 1);
        }
    } else {
        if (hafs != null) {
            ccSetCheckBoxDisabled(prefix + "HAFSCheckBox", formName, 0);
        }
    }
    
    return false;
}

/* handler for the shared fs check box */
function handleSharedCheckBox(checkbox) {
    var theForm = document.forms[formName];
    
    var hafs = theForm.elements[prefix + "HAFSCheckBox"];    
    var hpcCheckBox = theForm.elements[prefix + "HPCCheckBox"];
    if (hpcCheckBox != null)
        handleHPCCheckBox(hpcCheckBox);

    if (checkbox.checked) {
        // enable hpc checkbox if HAFS not selected
        if (hafs != null && hafs.checked) {
            ccSetCheckBoxDisabled(prefix + "HPCCheckBox", formName, 1);
        } else {
            ccSetCheckBoxDisabled(prefix + "HPCCheckBox", formName, 0);
        }
    } else {
        // disable hpc checkbox and fs name
        hpcCheckBox.checked = false;
        ccSetCheckBoxDisabled(prefix + "HPCCheckBox", formName, 1);
        ccSetTextFieldDisabled(prefix + "FSName", formName, 1);

        // if HAFS available enable it just incase it had been disabled by
        // clicking on HPC
        if (hafs != null) {
            ccSetCheckBoxDisabled(hafs.name, formName, 0);
        }
    }
    return false;
}

/* handler for the HPC Configuration check box */
function handleHPCCheckBox(checkbox) {
    var hafs = checkbox.form.elements[prefix + "HAFSCheckBox"];

    if (checkbox.checked) {
        ccSetTextFieldDisabled(prefix + "FSName", formName, 0);
        
        // disable HAFS, its not supported under HPC
        if (hafs != null) {
            hafs.checked = false;
            ccSetCheckBoxDisabled(prefix + "HAFSCheckBox", formName, 1);
        }            
    } else {
        ccSetTextFieldDisabled(prefix + "FSName", formName, 1);
        if (hafs != null) {
            ccSetCheckBoxDisabled(prefix + "HAFSCheckBox", formName, 0);
        }
    }
    
    return false;
}

/* */
function getClientParams() {
    var params = "&serverNameParam=" + getServerName();
    
    // append the file system type to the request
    if (fsTypeParams != null) {
        params += fsTypeParams;
    }
    
    return params;
}

/* */
function getServerName() {
    var theForm = document.forms[formName];
    var serverName = theForm.elements[prefix + "serverName"].value;
    
    return serverName;
}

/* */
function hasArchiveMedia() {
    var theForm = document.forms[formName];
    var archiveMedia = theForm.elements[prefix + "hasArchiveMedia"].value;
    
    return archiveMedia;
}