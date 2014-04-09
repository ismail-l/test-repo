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
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.AdminTabPresenter;
import tms2.client.service.AccessControlService;
import tms2.client.service.AccessControlServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ConfirmBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.SuccessBox;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * AdminTabPresenter class to manage online users
 * 
 * @author I. Lavangee
 *
 */
public class OnlineUserTabPresenter implements AdminTabPresenter
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	private AccessController _access_controller = AccessController.getInstance();
	
	private static AccessControlServiceAsync _access_control_service = GWT.create(AccessControlService.class);
	
	private static final int TIMER_SCHEDULE = 30000;
	
	private Display _display = null;
	private Timer _timer = null;
	
	public interface Display 
	{
		public Label getHeadingLabel();
		public ListBox getUsersListBox();
		public Button getSignOffButton();
		public Widget asWidget();
	}
	
	public OnlineUserTabPresenter(Display display)
	{
		_display = display;		
	}
	
	private void updateOnlineUsers()
	{				
		AccessController access_controller = AccessController.getInstance();
		
		HandlerManager event_bus = access_controller.getEventBus();
		event_bus.fireEvent(new AdminInterfaceEvent(this, AdminInterfaceEvent.ONLINE_USERS));		
	}
	
	public void startTimer()
	{
		if (_timer != null)
			stopTimer();
		
		_timer = new Timer()
		{
			@Override
			public void run()
			{
				loadAdminTabData();
			}
		};
		
		_timer.scheduleRepeating(TIMER_SCHEDULE);
	}
	
	public void stopTimer()
	{
		if (_timer != null) 
		{
			_timer.cancel();
			_timer = null;
		}
	}
	
	private void bind()
	{		
		_display.getSignOffButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{			
				ListBox lst_user = _display.getUsersListBox();
				
				// Get the selected user
				int selIndx = lst_user.getSelectedIndex();
				
				if(selIndx == -1)				
					AlertBox.show(_i18n.getConstants().admin_online_error_noUserSelected());				
				else
				{
					// Get the auth token as stored in the usersListBox.
					final String userToken = lst_user.getValue(selIndx);
					final String authToken = _access_controller.getAuthToken();
					
					final String username = lst_user.getItemText(selIndx);
					
					if (userToken != null && ! userToken.isEmpty() && ! userToken.equalsIgnoreCase("null"))
					{
						if (userToken.equals(authToken))
						{
							ConfirmBox.show(_i18n.getConstants().admin_online_confirm(), new ClickHandler() 
							{
								@Override
								public void onClick(ClickEvent event) 
								{									
									// Fire the SignOffEvent for this user's session
									_access_controller.getEventBus().fireEvent(new SignOffEvent());
								}
							});							
						}
						else
							logOff(userToken, username);
							// No need to fire SignOffEvent here as the poll of the
							// AccessController will sign this user off
					}
					else
						AlertBox.show(_i18n.getConstants().admin_tab_unregistered_guest(), false, true);
				}
			}
		});
	}
	
	private void logOff(final String userToken, final String username)
	{		
		if (! _access_controller.isGuest())
		{
			// Sign out the user using the auth token.
			_access_control_service.signOffUser(_access_controller.getAuthToken(), userToken, new AsyncCallback<Void>()
			{
				@Override
				public void onSuccess(Void result)
				{							
					SuccessBox.show(_i18n.getMessages().admin_online_signOffSuccess(username));							
					updateOnlineUsers();
				}
				
				@Override
				public void onFailure(Throwable caught)
				{
					AlertBox.show(_i18n.getMessages().admin_online_signOffFail(username));
					ErrorBox.ErrorHandler.handle(caught);
				}
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		TabLayoutPanel admin_tab_panel = (TabLayoutPanel)container;		
		admin_tab_panel.add(_display.asWidget(), _display.getHeadingLabel());
			
		loadAdminTabData();
		startTimer();
		
		bind();
	}

	@Override
	public void loadAdminTabData()
	{
		updateOnlineUsers();		
	}
	
	public Display getDisplay()
	{
		return _display;
	}	
}
