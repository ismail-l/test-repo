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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog that displays an error to the user. Extends the  {@linkplain mysuso.client.xcontrols.ExtendedDialogBox   ExtendedDialogBox} .
 * @author  Wildrich Fourie
 * @author  Werner Liebenberg
 */
public class ErrorBox extends ExtendedDialogBox 
{
	private static final TMSConstants constants = GWT.create(TMSConstants.class);

	public ErrorBox() {
		this(false, false);
	}

	public ErrorBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		setTitle(constants.controls_error());
	}

	public ErrorBox(boolean autoHide) {
		this(autoHide, false);
	}
	
	public ErrorBox(String title, String caption, boolean autoHide, boolean modal) {
		super(title, caption, autoHide, modal);
	}
	
	public ErrorBox(String caption, boolean autoHide, boolean modal) {
		this(constants.controls_error(), caption, autoHide, modal);
	}
	
	public static void show(String title, String caption, boolean autoHide, boolean modal)
	{
		ErrorBox errorBox = new ErrorBox(title, caption, autoHide, modal);
		errorBox.center();
		errorBox.show();
	}
	
	public static void show(String caption, boolean autoHide, boolean modal)
	{
		show(constants.controls_error(), caption, autoHide, modal);
	}
	
	public static void show(String caption)
	{
		show(caption, false, true);
	}

	@Override
	public void setIcon()
	{
		body.setIcon(new Image(ICON_CANCEL.getUrl()));
	}
	
	@Override
	public void setTitle(String title)
	{
		super.setTitle(title);
	}
	
	public static class ErrorHandler 
	{
		public static void handle(String message)
		{
			// NB: Special case to handle aborted RPC requests (on browser refresh/close/etc.)
			if (message.matches("^0\\s*$"))
				return;
					
			AlertBox.show(message);
		}
		
		public static void handle(Throwable caught)
		{		
			String message = caught.getMessage();
			if (message == null || message.isEmpty())
			{
				ErrorBox box = new ErrorBox();
				//box.setTitle("Exception Stacktrace");
				ScrollPanel panel = new ScrollPanel();
				panel.setSize("600px", "400px");
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.setSpacing(10);
				String traceToString = stackTraceToString(caught);
				vPanel.add(new HTML("<PRE>" + traceToString + "</PRE>"));
				panel.add(vPanel);
				box.setTextPanel(panel);
				box.setText("Exception stacktrace");
				box.setWidth("640px");
				box.center();
				box.show();
			}
			// NB: Special case to handle aborted RPC requests (on browser refresh/close/etc.)
			else if (message.matches("^0\\s*$"))
				return;
			else
				ErrorBox.show(caught.getMessage());				
		}
		
		private static String stackTraceToString(Throwable caught)
		{
			StackTraceElement[] elements = caught.getStackTrace();
			String str = caught.getClass().getName() + ": " + caught.getMessage() + " at\r\n\r\n";
			for (StackTraceElement element : elements)
			{
				str += "   " + element.toString() + "\r\n";
			}
			
			return str;
		}
	}
}
