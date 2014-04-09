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

package tms2.client.admininterface.presenter;

import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.util.PageNavigateUtililty;
import tms2.shared.User;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presenter class to navigate away from teh {@link AdminInterfacePresenter} and 
 * allow a {@link User} to sign out from the app.
 * 
 * @author I. Lavangee
 *
 */
public class NavigatorPresenter implements Presenter 
{
	private Internationalization _i18n = Internationalization.getInstance();
	
	private Display _display = null;
	
	public interface Display
	{
		public Anchor getTermBrowserAnchor();
		public Anchor getHelpAnchor();
		public Widget asWidget();
	}
	
	public NavigatorPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{
		_display.getTermBrowserAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				PageNavigateUtililty.navigate("termbrowser.jsp", "_top", null);			
			}
		});
		
		_display.getHelpAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{			
				PageNavigateUtililty.navigate(_i18n.getConstants().helpPath(), _i18n.getConstants().helpHeader(), _i18n.getConstants().helpFeatures());
			}
		});
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		container.add(_display.asWidget());
		
		bind();
	}
}
