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
import tms2.client.i18n.TMSMessages;
import tms2.shared.TerminlogyObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog in which the DragMath interface is embedded.
 * @author  Martin Schlemmer
 */
public class DragMathDialog extends PopupDialog
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
	
	private final static String DRAGMATH_DIR = "/DragMath";
		
	public DragMathDialog(String title, Widget content)
	{
		super(title, content);		
	}

	public static DragMathDialog show(final TextBoxBase widget, final TerminlogyObject terminology_object)
	{
		final VerticalPanel verticalPanel = new VerticalPanel();
		final DragMathDialog dialog = new DragMathDialog(constants.controls_dm_title(), verticalPanel);
		
		dialog.hide();
		
		final String path = (GWT.isScript() ? "." : "") + DRAGMATH_DIR;
		String url = path + "/applet/DragMath.jar";

		RequestBuilder builder = new RequestBuilder(RequestBuilder.HEAD, URL.encode(url));
		try
		{
		    builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response)
				{
					if (200 != response.getStatusCode())
					{
						AlertBox.show(constants.controls_dm_error_install());
						return;
					}

					HTML applet = new HTML();
				    applet.setHTML("<applet name=\"DragMath\"" +
				    		"  codebase=\"" + path + "/applet\"" +
				    		"  code=\"Display.MainApplet.class\"" +
				    		"  archive=\"DragMath.jar\"" +
				    		"  width=\"540\" height=\"310\">" +
				    		"<param name=language value=\"en\">" +
				    		"<param name=outputFormat value=\"Latex\">" +
				    		"<param name=hideMenu value=\"true\">" +
				    		"To use this page you need a Java-enabled browser. " +
				    		"Download the latest Java plug-in from <a href=\"http://www.java.com/\">Java.com</a>" +
				    		"</applet>");
				    verticalPanel.add(applet);
				    
				    Button insertButton = new Button(constants.controls_insert());
				    Button cancelButton = new Button(constants.controls_cancel());
				    
				    insertButton.addClickHandler(new ClickHandler()
				    {
						@Override
						public void onClick(ClickEvent event)
						{
							String math = getMathExpression();							
							dialog.hide();
							widget.setText(math);	
							terminology_object.setHasBeenEdited(true);
						}
				    });
				    cancelButton.addClickHandler(new ClickHandler()
				    {
						@Override
						public void onClick(ClickEvent event)
						{
							dialog.hide();
						}
				    });
				    
				    dialog.setButtons(insertButton, cancelButton);
				    
				    dialog.center();
				    dialog.show();
				}

				@Override
				public void onError(Request request, Throwable exception)
				{
					AlertBox.show(messages.alert_controls_RunApplet(constants.controls_dm_title()));
				}
		    });
		}
		catch (RequestException e)
		{
			AlertBox.show(messages.alert_controls_RunApplet(constants.controls_dm_title()));
		}
	    
	    return dialog;
	}

	private static native String getMathExpression()
	/*-{
		var text = "";

		if (typeof $wnd.document.DragMath !== 'undefined'
				&& $wnd.document.DragMath != null)
			text = $wnd.document.DragMath.getMathExpression();

		return text;
	}-*/;
}
