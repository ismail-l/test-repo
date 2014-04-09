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

package tms2.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import tms2.client.accesscontrol.AccessController;
import tms2.client.service.SessionService;
import tms2.client.service.SessionServiceAsync;
import tms2.client.widgets.ErrorBox;


/**
 * 
 * @author I. Lavangee
 *
 */
public class WindowClosingUtility 
{
	private static SessionServiceAsync _session_service = GWT.create(SessionService.class);
	
	private boolean _closing_state = true;
	
	private static WindowClosingUtility _window_closer = null;
	
	public static WindowClosingUtility getInstance()
	{
		if (_window_closer == null)
			_window_closer = new WindowClosingUtility();
		
		return _window_closer;
	}
	
	private WindowClosingUtility()
	{
		
	}
	
	public void setClosingState(boolean closing_state)
	{
		_closing_state = closing_state;
	}
	
	public void setClosingHandler()
	{
		Window.addWindowClosingHandler(new ClosingHandler() 
		{			
			@Override
			public void onWindowClosing(ClosingEvent  event) 
			{
				// FIXME This handler will be called whenever the browser is closed or refreshed.
				// No way to distinguish between a close and a refresh
				
				AccessController access_controller = AccessController.getInstance();
								
				// Invalidate unregistered guest sessions
				if (_closing_state && access_controller.isGuest())
				{					
					Storage access_control_store = Storage.getSessionStorageIfSupported();
					
					if (access_control_store != null)
					{
						access_control_store.removeItem("_config_checked");
						access_control_store.removeItem("_user");
						access_control_store.removeItem("_app_props");
					}	
					
					_session_service.invalidate(access_controller.getAuthToken(), new AsyncCallback<Void>() 
					{						
						@Override
						public void onSuccess(Void result) 
						{
	
						}
						
						@Override
						public void onFailure(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);							
						}
					});
				}
				
				_closing_state = true;
			}
		});
	}
}
