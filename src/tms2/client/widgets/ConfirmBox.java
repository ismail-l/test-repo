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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Confirmation dialog which poses a question to the user or is used for confirmation before implementing an action. Extends the  {@linkplain mysuso.client.xcontrols.ExtendedDialogBox   ExtendedDialogBox} .
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 */
public class ConfirmBox extends ExtendedDialogBox 
{
	
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	
    public interface ConfirmCallback 
    {
        public void onConfirm();
        public void onCancel();
    }
	
    /**
	 * @uml.property  name="_callback"
	 * @uml.associationEnd  
	 */
    ConfirmCallback _callback;
    
	public ConfirmBox(String title, String caption, boolean autoHide, boolean modal, ClickHandler confirmHandler) {
		super(title, caption, autoHide, modal, confirmHandler);
	}
	
	public ConfirmBox(String caption, boolean autoHide, boolean modal, ClickHandler confirmHandler) {
		this(constants.controls_confirm(), caption, autoHide, modal, confirmHandler);
	}
	
	public ConfirmBox(String caption, boolean autoHide, boolean modal, ConfirmCallback callback) {		
		super(constants.controls_confirm(), caption, autoHide, modal);
		_callback = callback;
	}
	
	public static void show(String caption, boolean autoHide, boolean modal, ConfirmCallback callback)
	{
		ConfirmBox confirmBox = new ConfirmBox(caption, autoHide, modal, callback);
		confirmBox.center();
		confirmBox.show();	
	}
	
	public static void show(String title, String caption, boolean autoHide, boolean modal, ClickHandler confirmHandler)
	{
		ConfirmBox confirmBox = new ConfirmBox(title, caption, autoHide, modal, confirmHandler);
		confirmBox.center();
		confirmBox.show();		
	}
	
	public static void show(String caption, boolean autoHide, boolean modal, ClickHandler confirmHandler)
	{
		show(constants.controls_confirm(), caption, autoHide, modal, confirmHandler);
	}
	
	public static void show(String caption, ClickHandler confirmHandler)
	{
		show(caption, false, true, confirmHandler);
	}
		
	
	@Override
	public void setIcon()
	{
		body.setIcon(ICON_ALERT);
	}

	/*
	@Override
	public void setText(String caption)
	{
		body.setText(caption);
	}
	*/
	
	@Override
	public void setTitle(String title)
	{
		super.setTitle(title);
	}
	
	@Override
	protected void setButtons()
	{
		Button okButton = new Button(constants.controls_ok());
		if (clickHandler != null)
			okButton.addClickHandler(clickHandler);
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {					
				ConfirmBox.this.hide();
				if (_callback != null)
					_callback.onConfirm();
			}
		});
		
		Button cancelButton = new Button(constants.controls_cancel());
		cancelButton.setFocus(true);
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {					
				ConfirmBox.this.hide(true);
				if (_callback != null)
					_callback.onCancel();
			}
		});
		
		super.setButtons(okButton, cancelButton);
	}	
}
