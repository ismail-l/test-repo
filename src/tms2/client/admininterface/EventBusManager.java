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


package tms2.client.admininterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.admininterface.presenter.AccessRightsTabPresenter;
import tms2.client.admininterface.presenter.AdminInterfacePresenter;
import tms2.client.admininterface.presenter.FieldTabPresenter;
import tms2.client.admininterface.presenter.OnlineUserTabPresenter;
import tms2.client.admininterface.presenter.TermBaseProjectTabPresenter;
import tms2.client.admininterface.presenter.UserUserCategoryTabPresenter;
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.AdminInterfaceEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.service.AccessControlService;
import tms2.client.service.AccessControlServiceAsync;
import tms2.client.service.AccessRightService;
import tms2.client.service.AccessRightServiceAsync;
import tms2.client.service.FieldService;
import tms2.client.service.FieldServiceAsync;
import tms2.client.service.ProjectService;
import tms2.client.service.ProjectServiceAsync;
import tms2.client.service.TermBaseService;
import tms2.client.service.TermBaseServiceAsync;
import tms2.client.service.UserCategoryService;
import tms2.client.service.UserCategoryServiceAsync;
import tms2.client.service.UserService;
import tms2.client.service.UserServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.SuccessBox;
import tms2.shared.Field;
import tms2.shared.InputModel;
import tms2.shared.PresetField;
import tms2.shared.Project;
import tms2.shared.Result;
import tms2.shared.TermBase;
import tms2.shared.User;
import tms2.shared.UserCategory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Class with one static method to manager {@link AdminInterfacePresenter} event's.
 * 
 * @author I. Lavangee
 */
public class EventBusManager 
{
	public static void manageAdminEvents()
	{
		final Internationalization i18n = Internationalization.getInstance();
		
		final AccessController access_controller = AccessController.getInstance();;
		final HandlerManager event_bus = access_controller.getEventBus();
		
		event_bus.addHandler(AdminInterfaceEvent.TYPE, new AdminInterfaceEventHandler() 
		{
			private FieldServiceAsync _field_service = GWT.create(FieldService.class);
			private AccessControlServiceAsync _access_control_service = GWT.create(AccessControlService.class);;
			private UserCategoryServiceAsync _user_category_service = GWT.create(UserCategoryService.class);
			private UserServiceAsync _user_service = GWT.create(UserService.class);
			private TermBaseServiceAsync _termbase_service = GWT.create(TermBaseService.class);
			private ProjectServiceAsync _project_service = GWT.create(ProjectService.class);
			private AccessRightServiceAsync _access_right_service = GWT.create(AccessRightService.class);
			
			@Override
			public void getOnlineUsers(AdminInterfaceEvent event) 
			{		
				OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)event.getPresenter();
				OnlineUserTabPresenter.Display display = presenter.getDisplay();
				
				final Button btn_signoff = display.getSignOffButton();
				final ListBox lst_users = display.getUsersListBox();
				
				btn_signoff.setEnabled(false);
				
				lst_users.clear();
				lst_users.addItem(i18n.getConstants().controls_loading());
								
				_access_control_service.getSignedOnUsers(access_controller.getAuthToken(), new AsyncCallback<Vector<User>>()
				{
					@Override
					public void onSuccess(Vector<User> result)
					{
						lst_users.clear();
										
						for (Enumeration<User> e = result.elements(); e.hasMoreElements();)
						{
							User user = e.nextElement();
						    
							lst_users.addItem(user.getFullName() + " (" + user.getIPAddress() + ")" , user.getAuthToken());
						}							
						
						result.clear();				
						btn_signoff.setEnabled(true);
					}
					
					@Override
					public void onFailure(Throwable caught)
					{
						ErrorBox.ErrorHandler.handle(caught);
						
						btn_signoff.setEnabled(false);
						lst_users.clear();
						lst_users.addItem(i18n.getConstants().admin_online_error_load());						
					}
				});
			}

			@Override
			public void updateUserCategory(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final UserUserCategoryTabPresenter presenter = (UserUserCategoryTabPresenter) event.getPresenter();
					
					UserCategory user_category = presenter.getUserCategory();	
									
					_user_category_service.updateUserCategory(access_controller.getAuthToken(), user_category, new BusyDialogAsyncCallBack<Result<UserCategory>>(null) 
					{
						@Override
						public void onComplete(Result<UserCategory> result) 
						{
							if (result.getResult() != null)						
								SuccessBox.show(result.getMessage());												
							else
								AlertBox.show(result.getMessage());
							
							presenter.loadAdminTabData();
						}
	
						@Override
						public void onError(Throwable caught) 
						{		
							ErrorBox.ErrorHandler.handle(caught);						
						}					
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updateUser(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final UserUserCategoryTabPresenter presenter = (UserUserCategoryTabPresenter) event.getPresenter();
					final UserUserCategoryTabPresenter.Display display = presenter.getDisplay();
					
					User user  = presenter.getUser();				
									
					_user_service.updateUser(access_controller.getAuthToken(), user, display.getPasswordTextBox().getText(), new BusyDialogAsyncCallBack<Result<User>>(null) 
					{
						@Override
						public void onComplete(Result<User> result) 
						{
							if (result.getResult() != null)						
								SuccessBox.show(result.getMessage());												
							else
								AlertBox.show(result.getMessage());
							
							presenter.loadAdminTabData();
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);			
						}					
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updateTermBase(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final TermBaseProjectTabPresenter presenter = (TermBaseProjectTabPresenter) event.getPresenter();
					
					TermBase termbase = presenter.getTermBase();
										
					_termbase_service.updateTermBase(access_controller.getAuthToken(), termbase, new BusyDialogAsyncCallBack<Result<TermBase>>(null) 
					{
						@Override
						public void onComplete(Result<TermBase> result) 
						{												
							if (result.getResult() != null)					
								SuccessBox.show(result.getMessage());
							else
								AlertBox.show(result.getMessage());
							
							presenter.loadAdminTabData();						
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);
						}
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updateProject(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final TermBaseProjectTabPresenter presenter = (TermBaseProjectTabPresenter) event.getPresenter();
					
					Project project = presenter.getProject();
									
					_project_service.updateProject(access_controller.getAuthToken(), project, new BusyDialogAsyncCallBack<Result<Project>>(null)
					{
						@Override
						public void onComplete(Result<Project> result)
						{
							if (result.getResult() != null)						
								SuccessBox.show(result.getMessage());							
							else
								AlertBox.show(result.getMessage());
							
							presenter.loadAdminTabData();
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);
						}					
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updateField(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final FieldTabPresenter presenter = (FieldTabPresenter) event.getPresenter();
					
					Field field = presenter.getField();
									
					_field_service.updateField(access_controller.getAuthToken(), field, new BusyDialogAsyncCallBack<Result<Field>>(null)
					{
						@Override
						public void onComplete(Result<Field> result) 
						{
							if (result.getResult() != null)						
								SuccessBox.show(result.getMessage());
							else
								AlertBox.show(result.getMessage());
																		
							presenter.loadAdminTabData();
						}
	
						@Override
						public void onError(Throwable caught)
						{
							ErrorBox.ErrorHandler.handle(caught);
						}					
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updatePresetField(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final FieldTabPresenter presenter = (FieldTabPresenter) event.getPresenter();
					
					PresetField field = presenter.getPresetField();
									
					_field_service.updateField(access_controller.getAuthToken(), field, new BusyDialogAsyncCallBack<Result<Field>>(null)
					{
						@Override
						public void onComplete(Result<Field> result) 
						{
							if (result.getResult() != null)						
								SuccessBox.show(result.getMessage());
							else
								AlertBox.show(result.getMessage());
																		
							presenter.loadAdminTabData();						
						}
	
						@Override
						public void onError(Throwable caught)
						{
							ErrorBox.ErrorHandler.handle(caught);
						}					
					});	
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}

			@Override
			public void updateUserAccessRight(AdminInterfaceEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final AccessRightsTabPresenter presenter = (AccessRightsTabPresenter) event.getPresenter();
					final AccessRightsTabPresenter.Display display = presenter.getDisplay();
													
					long consumer_id = -1;
					
					if (presenter.isUserAccessRight())
						consumer_id = presenter.getUser().getUserId();
					else
						consumer_id = presenter.getUserCategory().getUserCategoryId();	
					
					InputModel inputmodel = presenter.getInputModel(); 																				
					ArrayList<Project> projects = display.getUserProjectAssigner().getAssignedProjects();
									
					_access_right_service.updateAccessRights(access_controller.getAuthToken(), inputmodel, projects, consumer_id, presenter.isUserAccessRight(), new BusyDialogAsyncCallBack<Result<Boolean>>(null)
					{
						@Override
						public void onComplete(Result<Boolean> result) 
						{
							if (result.getResult())						
								SuccessBox.show(result.getMessage());							
							else
								AlertBox.show(result.getMessage());
						
							presenter.loadAdminTabData();
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);
						}					
					});
				}
				else
					event_bus.fireEvent(new SignOffEvent());
			}
		});
	}
}
