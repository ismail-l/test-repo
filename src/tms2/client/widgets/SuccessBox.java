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
import com.google.gwt.user.client.ui.Image;

/**
 * Dialog to convey the success of an operation. Extends the  {@linkplain mysuso.client.xcontrols.ExtendedDialogBox   ExtendedDialogBox} .
 * @author  Wildrich Fourie
 * @author  Werner Liebenberg
 */
public class SuccessBox extends ExtendedDialogBox 
{
	
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	
	public SuccessBox() {
		this(false, false);
	}

	public SuccessBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		setTitle(constants.controls_success());
	}

	public SuccessBox(boolean autoHide) {
		this(autoHide, false);
	}
	
	public SuccessBox(String title, String caption, boolean autoHide, boolean modal) {
		super(title, caption, autoHide, modal);
	}
	
	public SuccessBox(String caption, boolean autoHide, boolean modal) {
		this(constants.controls_success(), caption, autoHide, modal);
	}
	
	public static void show(String title, String caption, boolean autoHide, boolean modal)
	{
		SuccessBox successBox = new SuccessBox(title, caption, autoHide, modal);
		successBox.center();
		successBox.show();
	}
	
	public static void show(String caption, boolean autoHide, boolean modal)
	{
		show(constants.controls_success(), caption, autoHide, modal);
	}
	
	public static void show(String caption)
	{
		show(caption, false, true);
	}

	@Override
	public void setIcon()
	{
		/* In cases where multiple dialog boxes of the same type, sharing the same icon image, pop up all at once, 
		 * only one of them shows the icon image. The other do not have an icon. This happens because these dialog boxes
		 * are shown using a static method show(), which creates and shows the dialog box, and the icon image prefetched for 
		 * the dialog box. When the static show method is called again, to show another dialog, 
		 * the same prefetched icon widget is used for the second instance of the dialog box. Since GWT does not allow
		 * for the same widget object instance to be duplicated, the browser essentially keeps moving the image to the next
		 * dialog box created. 
		 * 
		 * To ensure that all instances of the dialog box show the same icon, each of them need their own COPY of the icon
		 * image widget. To do that, the image must either be cloned, and in GWT this requires usinig DOM.clone(), but it fails 
		 * to work (it throws an assertion error claiming the icon was never added at least once to the Document body), 
		 * so I am loading a new image by hand every time, over the network. Hopefully the Internet is not slow whenever multiple
		 * dialog boxes of the same type pop up and need to load their icon image ...
		 */
	    body.setIcon(new Image(ICON_ACCEPTED.getUrl())); //Recreating the image manually
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
