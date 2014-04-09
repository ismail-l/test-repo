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


/**
 * Dialog box to show an alert. Extends the  {@linkplain mysuso.client.xcontrols.ExtendedDialogBox   ExtendedDialogBox} .
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 */
public class AlertBox extends ExtendedDialogBox 
{
	
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);

	public AlertBox() {
		this(false, false);
	}

	public AlertBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		setTitle(constants.controls_alert());
	}

	public AlertBox(boolean autoHide) {
		this(autoHide, false);
	}
	
	public AlertBox(String title, String caption, boolean autoHide, boolean modal) {
		super(title, caption, autoHide, modal);
	}
	
	public AlertBox(String caption, boolean autoHide, boolean modal) {
		this(constants.controls_alert(), caption, autoHide, modal);
	}
	
	public static void show(String title, String caption, boolean autoHide, boolean modal)
	{
		AlertBox alertBox = new AlertBox(title, caption, autoHide, modal);
		alertBox.center();
		alertBox.show();
	}
	
	public static void show(String caption, boolean autoHide, boolean modal)
	{
		show(constants.controls_alert(), caption, autoHide, modal);
	}
	
	public static void show(String caption)
	{
		show(caption, false, true);
	}

	@Override
	public void setIcon()
	{
		body.setIcon(ICON_ALERT);
	}
	
	@Override
	public void setText(String caption)
	{
		body.setMessage(caption);
	}
	
	@Override
	public void setTitle(String title)
	{
		body.setTitle(title);
	}
}
