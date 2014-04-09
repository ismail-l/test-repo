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

import tms2.client.i18n.TMSConstants;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.ModalUploadStatus;
import gwtupload.client.SingleUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * A SingleUploader with a custom form button.
 * @author  Ismail Lavangee
 */
public class ExtendedSingleUploader extends SingleUploader
{	
	/**
	 * @uml.property  name="_constants"
	 * @uml.associationEnd  
	 */
	private static TMSConstants _constants = GWT.create(TMSConstants.class);
	/**
	 * @uml.property  name="_btn_send"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Button _btn_send = new Button();
	
	public ExtendedSingleUploader ()
	{		
		super(FileInputType.BROWSER_INPUT, new ModalUploadStatus(), null, null);
				
		setSendButton();
	}
		
	public void setSendButton()
	{
		final FormFlowPanel form = (FormFlowPanel) super.getForm();
		
		_btn_send.setText(_constants.controls_btn_send());
		
		form.add(_btn_send);
		
		_btn_send.addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event)
			{
				form.submit();				
			}
		});
	}
	
	public Button getSendButton()
	{
		return _btn_send;
	}
}
