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


package tms2.client.termbrowser.presenter;

import tms2.client.accesscontrol.AccessController;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.util.PageNavigateUtililty;
import tms2.client.widgets.HasSignOut;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class NavigatorPresenter implements Presenter, HasSignOut
{		
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private Display _display = null;	
	
	public interface Display
	{
		public Anchor getAdminAnchor();
		public Anchor getSignOutAnchor();
		public Anchor getHelpAnchor();
		public Image getAdminImage();
		public Image getSignOutImage();		
		public Widget asWidget();
	}
	
	public NavigatorPresenter(Display display)
	{
		_display = display;
		
		_access_controller.addSignOut(this);
	}
	
	private void bind()
	{
		setNavigationLinks();
		addAdminAnchorHandler();
		addSignOutAnchorHandler();
		addHelpAnchorHandler();				
	}
	
	private void setNavigationLinks()
	{				
		if (_access_controller.isGuest())
		{
			_display.getAdminAnchor().setVisible(false);
			_display.getAdminImage().setVisible(false);
			_display.getSignOutAnchor().setVisible(false);
			_display.getSignOutImage().setVisible(false);
		}
		else if (! _access_controller.hasAdminRights())
		{
			_display.getAdminAnchor().setVisible(false);
			_display.getAdminImage().setVisible(false);
		}
	}
	
	private void addAdminAnchorHandler()
	{
		_display.getAdminAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				PageNavigateUtililty.navigate("administration.jsp", "_top", null);
			}
		});
	}
	
	private void addSignOutAnchorHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getSignOutAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{						
				event_bus.fireEvent(new SignOffEvent());
			}
		});
	}
	
	private void addHelpAnchorHandler()
	{
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

	@Override
	public void signOut() 
	{
		setNavigationLinks();		
	}
}
