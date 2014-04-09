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

package tms2.client.accesscontrol;

import java.util.ArrayList;
import java.util.Iterator;

import tms2.client.event.SignOffEvent;
import tms2.client.event.SignOffEventHandler;
import tms2.client.event.SignOnEvent;
import tms2.client.event.SignOnEventHandler;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.TermBrowserControllerPresenter;
import tms2.client.service.AccessControlService;
import tms2.client.service.AccessControlServiceAsync;
import tms2.client.shared.presenter.SignOnPresenter;
import tms2.client.shared.presenter.SignOnPresenter.Display;
import tms2.client.util.JSONUtility;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.HasSignOut;
import tms2.shared.AppProperties;
import tms2.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Singleton class that manages user access control. This class
 * will be re-created after every module load, however it's {@link User},
 * {@link AppProperties} object's and it's <code>_config_checked</code> property are cached client side.
 *   
 * @author I. Lavangee
 */
public class AccessController
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static final AccessControlServiceAsync _access_control = GWT.create(AccessControlService.class);
		
	private static final int TIMER_SCHEDULE = 10000; // Check every 10 seconds
			
	private User _user = null;
	
	private boolean _config_checked = false;
		
	private HandlerManager _event_bus = null;
	private AppProperties _app_props = null;
		
	private static Timer _timer = null;
	
	private static AccessController _access_controller = null;
		
	private TermBrowserControllerPresenter _termbrowser_controller = null;
	
	private ArrayList<HasSignOut> _signout_widgets = null;	
				
	public static AccessController getInstance()
	{				
		if (_access_controller == null)
			_access_controller = new AccessController();
		
		return _access_controller;
	}
	
	private AccessController() 
	{
		if (_access_controller == null)
		{
			_event_bus = new HandlerManager(null);
			
			_event_bus.addHandler(SignOnEvent.TYPE, new SignOnEventHandler()
			{
				@Override
				public void signOn(SignOnEvent event) 
				{
					SignOnPresenter presenter = (SignOnPresenter)event.getPresenter();									
					signInUser(presenter);
				}								
			});
			
			_event_bus.addHandler(SignOffEvent.TYPE, new SignOffEventHandler()
			{								
				@Override
				public void signOff(SignOffEvent event) 
				{
					signOutUser();
				}
			});
		}				
	}
		
	private void signInUser(final SignOnPresenter presenter)
	{
		final Display display = presenter.getDisplay();
		
		String username = display.getUsernameTextBox().getText();
		String password = display.getPasswordTextBox().getText();
		
		_access_control.signOn(username, password, new AsyncCallback<User>()
		{			
			@Override
			public void onSuccess(User user) 
			{
				if (user != null)
				{
					setUser(user);
					
					Storage access_control_store = Storage.getSessionStorageIfSupported();
					
					if (access_control_store != null)					
						access_control_store.setItem("_user", JSONUtility.userToJSON(_user));
					
					display.getUsernameTextBox().setText("");
					display.getPasswordTextBox().setText("");
					
					display.getSignOnPanel().showWidget(SignOnPresenter.Display.SIGNEDIN_PANEL);	
					display.getUsernameLabel().setText("You are signed in as " + _user.getFullName() + ".");
												
					presenter.displayPresenter();	
				}
			}
			
			@Override
			public void onFailure(Throwable caught) 
			{		
				ErrorBox.ErrorHandler.handle(caught);
				display.getSignOnPanel().showWidget(SignOnPresenter.Display.SIGNININGIN_PANEL);
				_user = null;
			}
		});
	}
	
	public void userPoll()
	{							    		    	    		
		if (_timer == null)
		{
			_timer = new Timer() 
			{
				@Override
				public void run() 
				{		
					if (_user != null)
					{
						if (_user == null)
						{
							if (_timer != null)
							{
								_timer.cancel();
								_timer = null;
							}
							
							return;
						}
																		
						// Check if the user was logged out remotely.
						_access_control.isUserStillSignedOn(getAuthToken(), new AsyncCallback<Boolean>()
						{
							@Override
							public void onSuccess(Boolean result)
							{
								if (result == null || result == false)
								{
									signOut();
									AlertBox.show(_i18n.getConstants().signOn_signout());
								}								
							}
							
							@Override
							public void onFailure(Throwable caught)
							{
								ErrorBox.ErrorHandler.handle(caught);
							}
						});
					}
				}
			};
			
			// Check every TIMER_SCHEDULE.
			_timer.scheduleRepeating(TIMER_SCHEDULE);
		}
	}
	
	private void signOutUser()
	{
		_access_control.signOff(getAuthToken(), new AsyncCallback<Void>()
		{
			@Override
			public void onSuccess(Void result) 
			{
				signOut();
			}
			
			@Override
			public void onFailure(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}			
		});
	}
		
	private void signOut()
	{
		_user = null;
		
		Storage access_control_store = Storage.getSessionStorageIfSupported();
		
		if (access_control_store != null)
			access_control_store.removeItem("_user");
		
		Iterator<HasSignOut> iter = _signout_widgets.iterator();
		while (iter.hasNext())
		{
			HasSignOut signout_widget = iter.next();
			signout_widget.signOut();
		}
	}
	
	public void addSignOut(HasSignOut signout_widget)
	{
		if (_signout_widgets == null)
			_signout_widgets= new ArrayList<HasSignOut>();
		
		_signout_widgets.add(signout_widget);
	}
			
	public void setUser(User user)
	{
		_user = user;
	}
	
	public User getUser()
	{
		return _user;
	}
		
	public String getAuthToken()
	{		
		if (_user == null)
			return null;
		
		return _user.getAuthToken();
	}
	
	public boolean hasAdminRights()
	{
		if (isGuest())
			return false;
		
		if (! _user.isAdmin())
			return false;
		
		return true;
	}
	
	public boolean isGuest()
	{
		if (_user == null || _user.isGuest())
			return true;
		
		return false;
	}
	
	public void setConfigChecked(boolean config_checked)
	{
		_config_checked = config_checked;
	}
	
	public boolean isConfigChecked()
	{
		return  _config_checked;
	}
		
	public HandlerManager getEventBus()
	{
		return _event_bus;
	}
	
	public void setAppProperties(AppProperties app_props)
	{
		_app_props = app_props;
	}
	
	public AppProperties getAppProperties()
	{
		return _app_props;
	}
	
	public void setTermBrowserControllerPresenter(TermBrowserControllerPresenter termbrowser_controller)
	{
		_termbrowser_controller = termbrowser_controller;
	}
	
	public TermBrowserControllerPresenter getTermBrowserController()
	{
		return _termbrowser_controller;
	}
}
