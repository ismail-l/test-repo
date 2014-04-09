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

import tms2.client.accesscontrol.AccessController;
import tms2.client.admininterface.EventBusManager;
import tms2.client.admininterface.view.AdminTabPanelView;
import tms2.client.admininterface.view.NavigatorView;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.shared.presenter.SignOnPresenter;
import tms2.client.shared.view.SignOnView;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.FixedPanel;
import tms2.client.widgets.HasSignOut;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presenter class that the {@link AdminTabPanelPresenter} will be inserted onto. Class can also signout.
 * 
 * @author I. Lavangee
 *
 */
public class AdminInterfacePresenter implements Presenter, HasSignOut
{
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private Display _display = null;
	private boolean _should_sign_user_id = false;
	
	public interface Display
	{		
		public Label getPromptLabel();
		public FixedPanel getFooter();
		public Widget asWidget();
	}
	
	public AdminInterfacePresenter(Display display, boolean should_sign_user_id)
	{
		_display = display;
		_should_sign_user_id = should_sign_user_id;
		
		EventBusManager.manageAdminEvents();
		
		_access_controller.addSignOut(this);
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		container.add(_display.asWidget());	
								
		Presenter so_presenter = new SignOnPresenter(new SignOnView(), this);
		so_presenter.go(RootPanel.get("signon"));	
		
		Presenter n_presenter = new NavigatorPresenter(new NavigatorView());
		n_presenter.go(RootPanel.get("navigation"));
		
		RootPanel.get("footer").add(_display.getFooter());
		
		if (_access_controller.hasAdminRights())
		{						
			SignOnPresenter signon_presenter = (SignOnPresenter)so_presenter;

			setSignOnStatus(signon_presenter);
			displayBody();
		}
		else
		{
			if (! _should_sign_user_id)
				displayBody();
			else
			{
				SignOnPresenter signon_presenter = (SignOnPresenter)so_presenter;
				
				setSignOnStatus(signon_presenter);
				displayBody();
			}
		}
	}
	
	private void setSignOnStatus(SignOnPresenter signon_presenter)
	{
		SignOnPresenter.Display display = (SignOnPresenter.Display) signon_presenter.getDisplay();
		display.getSignOnPanel().showWidget(SignOnPresenter.Display.SIGNEDIN_PANEL);
								
		display.getUsernameLabel().setText("You are signed in as " + _access_controller.getUser().getFullName() + ".");
	}
	
	public void displayBody()
	{		
		if (! _access_controller.hasAdminRights())
			AlertBox.show(_i18n.getConstants().alert_admin_access(), false, true);
		else
		{
			_display.getPromptLabel().setText(_i18n.getConstants().admin_info());
		
			Presenter presenter = new AdminTabPanelPresenter(new AdminTabPanelView());
			presenter.go(RootPanel.get("tablayout"));
		}
	}

	@Override
	public void signOut() 
	{
		RootPanel.get("tablayout").clear();
		
		AlertBox.show(_i18n.getConstants().alert_admin_access(), false, true);
		_display.getPromptLabel().setText(_i18n.getConstants().alert_admin_access());		
	}
}
