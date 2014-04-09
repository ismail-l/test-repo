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

package tms2.client;

import tms2.client.accesscontrol.AccessController;
import tms2.client.admininterface.presenter.AdminInterfacePresenter;
import tms2.client.admininterface.view.AdminInterfaceView;
import tms2.client.presenter.Presenter;
import tms2.client.service.AccessControlService;
import tms2.client.service.AccessControlServiceAsync;
import tms2.client.service.AppConfigService;
import tms2.client.service.AppConfigServiceAsync;
import tms2.client.service.SessionService;
import tms2.client.service.SessionServiceAsync;
import tms2.client.termbrowser.presenter.TermBrowserPresenter;
import tms2.client.termbrowser.view.TermBrowserView;
import tms2.client.tmsintro.presenter.TMSIntroPresenter;
import tms2.client.tmsintro.view.TMSIntroView;
import tms2.client.util.JSONUtility;
import tms2.client.util.PageNavigateUtililty;
import tms2.client.util.WindowClosingUtility;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.shared.AppProperties;
import tms2.shared.Result;
import tms2.shared.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * EntryPoint class for the application. All three modules use this EntryPoint class
 * @author I. Lavangee
 */
public class TMS2 implements EntryPoint
{		
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static AppConfigServiceAsync service = GWT.create(AppConfigService.class);
	private static SessionServiceAsync _session_service = GWT.create(SessionService.class);
	private static AccessControlServiceAsync _access_control_service = GWT.create(AccessControlService.class);
			
	@Override
	public void onModuleLoad() 
	{		
		// Set this handler for everytime a new module loads
		WindowClosingUtility.getInstance().setClosingHandler();
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() 
		{  
			public void onUncaughtException(Throwable e) 
			{  
				ErrorBox.ErrorHandler.handle(e);
			}  
		});
		
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() 
	    {
	    	@Override
	    	public void execute() 
	    	{	
	    		final Storage access_control_store = Storage.getSessionStorageIfSupported();
	    		
	    		if (access_control_store != null)
	    			checkAccessControllerState(access_control_store);
	    		
	    		if (_access_controller.isConfigChecked())		    		
	    			resetSession();	    		
	    		else
	    		{
		    		service.validateConfig(new AsyncCallback<Result<AppProperties>>() 
		    		{
						@Override
						public void onSuccess(Result<AppProperties> result) 
						{
							if (result.getResult() != null)
							{																
								if (access_control_store != null)
								{
									_access_controller.setConfigChecked(true);	
									access_control_store.setItem("_config_checked", "true");	
								
									_access_controller.setAppProperties(result.getResult());
									access_control_store.setItem("_app_props", JSONUtility.appPropertiesToJSON(_access_controller.getAppProperties()));
								}
								
								resetSession();	
							}
							else
								AlertBox.show(result.getMessage());
						}
						
						@Override
						public void onFailure(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);	
						}		    			
					});
	    		}	    		    		
	    	}
	    });
	}
	
	private void resetSession()
	{
		_session_service.reset(_access_controller.getAuthToken(), new AsyncCallback<Void>()
		{
			@Override
			public void onSuccess(Void result) 
			{						
				loadCurrentModule();
			}
			@Override
			public void onFailure(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);						
			}
		});	
	}
	
	private void loadCurrentModule() 
	{
		_access_control_service.findSignedOnUser(_access_controller.getAuthToken(), new AsyncCallback<User>()
		{
			@Override
			public void onSuccess(User result) 
			{
				Storage access_control_store = Storage.getSessionStorageIfSupported();
				
				if (result != null && access_control_store != null)		
				{
					User user = _access_controller.getUser();
					String json_user = access_control_store.getItem("_user");
					
					if (user == null || json_user == null)
					{
						_access_controller.setUser(result);
						access_control_store.setItem("_user", JSONUtility.userToJSON(_access_controller.getUser()));
					}
				}
				
				Presenter presenter = null;
				
				String module_name = GWT.getModuleName();
				
				if (module_name.equals("tms2.TMSIntro"))		
				{
					// Just check for unregistered guests as there sessions
					// are not stored in the app's context.
					if ( _access_controller.getUser() == null)
					{
						presenter = new TMSIntroPresenter(new TMSIntroView());
						presenter.go(RootPanel.get("title"));
					}
					else					
						PageNavigateUtililty.navigate("termbrowser.jsp", "_top", null);					
				}
				else if (module_name.equals("tms2.TermBrowser"))
				{
					presenter = new TermBrowserPresenter(new TermBrowserView());
					presenter.go(RootPanel.get());
				}
				else
				{
					// Checks the sign on status of the SignOnPresenter of the
					// AdminInterfacePresenter 
					boolean should_sign_user_id = true;
					
					if (_access_controller.getUser() == null)
						should_sign_user_id = false;
					
					presenter = new AdminInterfacePresenter(new AdminInterfaceView(), should_sign_user_id);
					presenter.go(RootPanel.get("prompt"));
				}	
				 
				_access_controller.userPoll();				
			}
			
			@Override
			public void onFailure(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);				
			}			
		});
	}
	
	private void checkAccessControllerState(Storage access_control_store) 
	{		
		if (access_control_store.getItem("_config_checked") != null &&
			access_control_store.getItem("_user") != null &&
			access_control_store.getItem("_app_props") != null)
		{
			boolean config_checked = false;
			
			if (access_control_store.getItem("_config_checked").equals("true"))
				config_checked = true;
			
			_access_controller.setConfigChecked(config_checked);
			
			JSONObject json = JSONParser.parseStrict(access_control_store.getItem("_user")).isObject();

			User user = JSONUtility.jsonToUser(json);
			_access_controller.setUser(user);
			
			json = JSONParser.parseStrict(access_control_store.getItem("_app_props")).isObject();
			
			AppProperties app_props = JSONUtility.jsonToAppProperties(json);			
			_access_controller.setAppProperties(app_props);
		}
	}
}
