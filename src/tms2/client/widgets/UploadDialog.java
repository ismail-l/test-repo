/*
*  Autshumato Terminology Management System (TMS)
*  Free web application for the management of multilingual terminology databases (termbanks). 
*
*  Copyright (C) 2013 Centre for Text Technology (CTexT®), North-West University
*  and Department of Arts and Culture, Government of South Africa
*  Home page: http://www.nwu.co.za/ctext
*  Project page: http://autshumatotms.sourceforge.net
*   
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package tms2.client.widgets;

import tms2.client.accesscontrol.AccessController;
import tms2.client.i18n.TMSConstants;
import tms2.client.i18n.TMSMessages;
import tms2.shared.TerminlogyObject;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Upload dialog is used for uploading files to the server.
 * @author  Wildrich Fourie
 * @author  Ismail Lavangee
 */
public class UploadDialog extends PopupDialog
{
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	/**
	 * @uml.property  name="messages"
	 * @uml.associationEnd  
	 */
	private static final TMSMessages messages = GWT.create(TMSMessages.class);
	
	/**
	 * @uml.property  name="internalRef"
	 * @uml.associationEnd  
	 */
	private static UploadDialog internalRef;
	private static TextBoxBase focusObject;
	private static final String WARNING_STRING = messages.controls_ud_warning(constants.companyName());
	private static final String MAXUPLOAD = constants.controls_ud_maxupload();
	
	private static Button _btn_send = null;
	
	/**
	 * @uml.property  name="_field"
	 * @uml.associationEnd  
	 */
	private static TerminlogyObject _field = null;
	
	/**
	 * Creates a new upload dialog with the title and contents as provided.
	 * @param title
	 * @param content
	 */
	public UploadDialog(String title, Widget content, TerminlogyObject field)
	{
		super(title, content);
		internalRef = this;
		_field = field;
	}
	
	
	/**
	 * Shows the image upload dialog for create mode.
	 * @param title
	 * @param currentFocusObject
	 * @return
	 */
	public static UploadDialog show(String title, TextBoxBase currentFocusObject, TerminlogyObject field, String prefix)
	{	
		focusObject = currentFocusObject;
		
		// Init the components and layout
		VerticalPanel main = new VerticalPanel();
		main.setSpacing(3);
		
		VerticalPanel topPanel = new VerticalPanel();
		VerticalPanel bottomPanel = new VerticalPanel();
				
		final UploadDialog uploadDialog = new UploadDialog(title, main, field);
		ExtendedSingleUploader defaultUploader = new ExtendedSingleUploader();
		
		String authToken = AccessController.getInstance().getAuthToken();
		defaultUploader.setServletPath(defaultUploader.getServletPath() +
				"?authToken=" + ((authToken == null) ? "" : URL.encodeQueryString(authToken)) +
				"&fieldName=" + URL.encodeQueryString(field.getFieldName()) + 
				"&prefix=" + URL.encodeQueryString(prefix));
		
		_btn_send = defaultUploader.getSendButton();
		_btn_send.setEnabled(false);
		
		defaultUploader.addOnChangeUploadHandler(onChangehandler);
		defaultUploader.addOnStatusChangedHandler(onStatusChange);
		defaultUploader.addOnFinishUploadHandler(onFinishUploadHandler);
		
		Label lbl_limit = new Label(MAXUPLOAD);
		lbl_limit.addStyleName("plainLabelText");
		lbl_limit.getElement().getStyle().setFontSize(10, Unit.PX);
		
		topPanel.add(defaultUploader);
		topPanel.add(lbl_limit);
				
		Label lbl = new Label(WARNING_STRING, true);
		lbl.addStyleName("plainLabelText");
		lbl.getElement().getStyle().setColor("red");
								
		bottomPanel.add(lbl);
		
		main.add(topPanel);
		main.add(bottomPanel);
		
		uploadDialog.center();
		uploadDialog.show();
		
		return uploadDialog;
	}
	
	
	// Upload dialog for edit mode
	public static UploadDialog show(String title, TextBoxBase currentFocusObject, long recordId, TerminlogyObject field, String prefix) 
	{
		focusObject = currentFocusObject;
		
		// Init the components and layout
		VerticalPanel main = new VerticalPanel();
		main.setSpacing(3);
		
		VerticalPanel topPanel = new VerticalPanel();
		VerticalPanel bottomPanel = new VerticalPanel();
		
		final UploadDialog uploadDialog = new UploadDialog(title, main, field);
		ExtendedSingleUploader defaultUploader = new ExtendedSingleUploader();
		
		String authToken = AccessController.getInstance().getAuthToken();
		defaultUploader.setServletPath(defaultUploader.getServletPath() +
				"?authToken=" + ((authToken == null) ? "" : URL.encodeQueryString(authToken)) +
				"&fieldName=" + URL.encodeQueryString(field.getFieldName()) +
				"&recordid=" + recordId + 
				"&prefix=" + URL.encodeQueryString(prefix));
		
		_btn_send = defaultUploader.getSendButton();
		_btn_send.setEnabled(false);
		
		defaultUploader.addOnChangeUploadHandler(onChangehandler);
		defaultUploader.addOnStatusChangedHandler(onStatusChange);
		defaultUploader.addOnFinishUploadHandler(onFinishUploadHandler);
							
		Label lbl_limit = new Label(MAXUPLOAD);
		lbl_limit.addStyleName("plainLabelText");
		lbl_limit.getElement().getStyle().setFontSize(10, Unit.PX);
		
		topPanel.add(defaultUploader);
		topPanel.add(lbl_limit);
				
		Label lbl = new Label(WARNING_STRING, true);
		lbl.addStyleName("plainLabelText");
		lbl.getElement().getStyle().setColor("red");
								
		bottomPanel.add(lbl);
		
		main.add(topPanel);
		main.add(bottomPanel);
		
		uploadDialog.center();
		uploadDialog.show();
		
		return uploadDialog;
	}
	
	private static IUploader.OnChangeUploaderHandler onChangehandler = new IUploader.OnChangeUploaderHandler()
	{		
		@Override
		public void onChange(IUploader uploader) 
		{
			if (uploader.getStatus() == Status.CHANGED)				
				_btn_send.setEnabled(true);						
		}		
	};
	
	private static IUploader.OnStatusChangedHandler onStatusChange = new IUploader.OnStatusChangedHandler()
	{
		@Override
		public void onStatusChanged(IUploader uploader)
		{
			if (uploader.getStatus() == Status.ERROR)	
			{
				_btn_send.setEnabled(false);
				uploader.reset();
			}			
		}	
	};
	
	// Finished upload handler
	private static IUploader.OnFinishUploaderHandler onFinishUploadHandler = new IUploader.OnFinishUploaderHandler()
	{
		@Override
		public void onFinish(IUploader uploader)
		{
			if (uploader.getStatus() == Status.SUCCESS)
			{					
				if (uploader.getServerInfo() != null)
				{
					System.out.println("\n...Upload Finished...\n");
					
					// Gets the correct return message
			        System.out.println("ServerInfo: " + uploader.getServerMessage().getMessage());
			 
			        // Construct the relative URL.
			        String imgURL = "/" + uploader.getServerMessage().getMessage();
			        
			        internalRef.hide();
			        focusObject.setText(imgURL);	
			        _field.setHasBeenEdited(true);
				}
			}
		}
	};
}
