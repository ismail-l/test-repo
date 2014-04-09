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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.AdminTabPresenter;
import tms2.client.service.AccessRightService;
import tms2.client.service.AccessRightServiceAsync;
import tms2.client.service.UserCategoryService;
import tms2.client.service.UserCategoryServiceAsync;
import tms2.client.service.UserService;
import tms2.client.service.UserServiceAsync;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.UserProjectAccessPanel;
import tms2.shared.AccessRight;
import tms2.shared.AppProperties;
import tms2.shared.ChildAccessRight;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.Term;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.UserCategory;
import tms2.shared.wrapper.AccessRightDetailsWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Presenter class to manage Access Rights.
 * 
 * @author I. Lavangee
 *
 */
public class AccessRightsTabPresenter implements AdminTabPresenter
{		
	private Display _display = null; 
	
	private static AccessController _access_controller = AccessController.getInstance();
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static final int USER = 0;
	private static final int USER_CAT = 1;
	
	private static UserServiceAsync _user_service = GWT.create(UserService.class);
	private static UserCategoryServiceAsync _user_cat_service = GWT.create(UserCategoryService.class);
	private static AccessRightServiceAsync _access_right_service = GWT.create(AccessRightService.class);	
	
	private User _user = null;
	private UserCategory _user_cat = null;
	private InputModel _inputmodel= null;
	private boolean _is_user = false;
	
	private boolean _is_read_select_all = true;
	private boolean _is_update_select_all = true;
	private boolean _is_export_select_all = true;
	private boolean _is_delete_select_all = true;
	
	private AccessRightSelectDeselectHandler _read_handler = null;
	private AccessRightSelectDeselectHandler _update_handler = null;
	private AccessRightSelectDeselectHandler _export_handler = null;
	private AccessRightSelectDeselectHandler _delete_handler = null;
	
			
	public interface Display
	{
		public int RECORD_GRID = 0;
		public int TERM_GRID = 1;
		public int TERMATTRIBUTE_GRID = 2;
		public int TERMSUBATTRIBUTE_GRID = 3;
		
		public RadioButton getUserRadioButton();
		public RadioButton getUserCategoryRadioButton();
		public ExtendedListBox<User> getUserListBox();
		public ExtendedListBox<UserCategory> getUserCategoryListBox();
		public VerticalPanel getRightsPanel();
		public Label getAccessRightsLabel();
		public Label getHeadingLabel();
		public VerticalPanel getRadioButtonPanel();
		public DeckPanel getGridPanel();
		public RadioButton getRecordAttributeRadioButton();
		public RadioButton getIndexRadioButton();
		public RadioButton getAttributeRadioButton();
		public RadioButton getSubAttributeRadioButton();
		public Label getTermLabel();
		public ExtendedListBox<Term> getTermListBox();
		public DataGrid<AccessRight> getRecordAttributeDataGrid();
		public ListDataProvider<AccessRight> getRecordAttributeDataProvider();
		public ArrayList<Column<AccessRight, Boolean>> getRecordNonGuestColumns();
		public DataGrid<AccessRight> getTermDataGrid();
		public ListDataProvider<AccessRight> getTermDataProvider();
		public ArrayList<Column<AccessRight, Boolean>> getTermNonGuestColumns();
		public DataGrid<ChildAccessRight> getTermAttributeDataGrid();
		public ListDataProvider<ChildAccessRight> getTermAttributeDataProvider();
		public ArrayList<Column<ChildAccessRight, Boolean>> getTermAttributeNonGuestColumns();
		public DataGrid<ChildAccessRight> getTermSubAttributeDataGrid();
		public ListDataProvider<ChildAccessRight> getTermSubAttributeDataProvider();
		public ArrayList<Column<ChildAccessRight, Boolean>> getTermSubAttributeNonGuestColumns();
		public Button getMarkAllReadButton();
		public Button getMarkAllUpdateButton();
		public Button getMarkAllExportButton();
		public Button getMarkAllDeleteButton();		
		public VerticalPanel getUserProjectPanel();		
		public Label getUserProjectLabel();
		public UserProjectAccessPanel getUserProjectAssigner();
		public Button getProjectSaveButton();
		public Button getUpdateAccessRightsButton();				
		public Widget asWidget();
	}
	
	private class AccessRightSelectDeselectHandler implements ClickHandler
	{
		private int _type = -1;
		private boolean _is_select = false;
		
		public AccessRightSelectDeselectHandler(int type)
		{
			_type = type;
			
			if (type == 0)
				_is_select = _is_read_select_all;			
			else if (type == 1)							
				_is_select = _is_update_select_all;			
			else if (type == 2)							
				_is_select = _is_export_select_all;			
			else if (type == 3)
				_is_select = _is_delete_select_all;									
		}
				
		@Override
		public void onClick(ClickEvent event) 
		{
			RadioButton btn_record = _display.getRecordAttributeRadioButton();
			RadioButton btn_term = _display.getIndexRadioButton();			
			
			if (btn_record.getValue())
			{
				ListDataProvider<AccessRight> data_provider = _display.getRecordAttributeDataProvider();
				List<AccessRight> access_rights = data_provider.getList();
				
				selectDeselectAllAccessRightInfos(access_rights);
				
				data_provider.refresh();
			}
			else if (btn_term.getValue())
			{
				ListDataProvider<AccessRight> data_provider = _display.getTermDataProvider();
				List<AccessRight> access_rights = data_provider.getList();
				
				selectDeselectAllAccessRightInfos(access_rights);
				
				data_provider.refresh();
			}
			else
			{
				RadioButton btn_attr = _display.getAttributeRadioButton();
				RadioButton btn_sub_atr = _display.getSubAttributeRadioButton();
				
				ListDataProvider<ChildAccessRight> data_provider = null;
				List<ChildAccessRight> access_rights = null;
				Term term = null;
				
				if (btn_attr.getValue())
				{
					data_provider = _display.getTermAttributeDataProvider();
					access_rights = data_provider.getList();
					
					term = _display.getTermListBox().getSelectedItem();
					ArrayList<ChildTerminologyObject> term_attributes = _inputmodel.getTermAttributesForTerm(term);
					
					selectDeselectAllChildAccessRightInfos(access_rights, term_attributes);
					
					data_provider.refresh();
				}
				else if (btn_sub_atr.getValue())
				{
					data_provider = _display.getTermSubAttributeDataProvider();
					access_rights = data_provider.getList();
					
					term = _display.getTermListBox().getSelectedItem();
					ArrayList<ChildTerminologyObject> synonym_attributes = _inputmodel.getSynonymAttributesForTerm(term);
					
					selectDeselectAllChildAccessRightInfos(access_rights, synonym_attributes);
					
					data_provider.refresh();
				}
			}
							
			if (_is_select)
				_is_select = false;
			else if (! _is_select)
				_is_select = true;
			
			setSelectState();
		}	
		
		private void selectDeselectAllAccessRightInfos(List<AccessRight> access_rights)
		{
			Iterator<AccessRight> iter = access_rights.iterator();
			while (iter.hasNext())
			{
				AccessRight access_right = iter.next();
				
				switch (_type)
				{
					case 0:
					{
						if (_is_select)
							access_right.setMayRead(true);
						else
							access_right.setMayRead(false);
						
						break;
					}
					case 1:
					{
						if (_is_select)
							access_right.setMayUpdate(true);
						else
							access_right.setMayUpdate(false);
						
						break;
					}
					case 2:
					{
						if (_is_select)
							access_right.setMayExport(true);
						else
							access_right.setMayExport(false);
						
						break;
					}
					case 3:
					{
						if (_is_select)
							access_right.setMayDelete(true);
						else
							access_right.setMayDelete(false);
						
						break;
					}
				}				
			}				
		}
		
		private void selectDeselectAllChildAccessRightInfos(List<ChildAccessRight> access_rights, ArrayList<ChildTerminologyObject> child_attributes)
		{
			Iterator<ChildAccessRight> iter = access_rights.iterator();
			while (iter.hasNext())
			{
				ChildAccessRight access_right = iter.next();
				
				switch (_type)
				{
					case 0:
					{
						if (_is_select)
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayRead(true);
						}
						else
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayRead(false);
						}
						
						break;
					}
					case 1:
					{
						if (_is_select)
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayUpdate(true);
						}
						else
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayUpdate(false);
						}
						
						break;
					}
					case 2:
					{
						if (_is_select)
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayExport(true);
						}
						else
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayExport(false);
						}
						
						break;
					}
					case 3:
					{
						if (_is_select)
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayDelete(true);
						}
						else
						{
							if (isChildAccessRightInView(access_right, child_attributes))
								access_right.setMayDelete(false);
						}
						
						break;
					}
				}				
			}				
		}
		
		private boolean isChildAccessRightInView(ChildAccessRight access_right, ArrayList<ChildTerminologyObject> child_attributes)
		{
			Iterator<ChildTerminologyObject> iter = child_attributes.iterator();
			while (iter.hasNext())
			{
				ChildTerminologyObject child_attribute = iter.next();
				
				if (access_right.getFieldId() == child_attribute.getFieldId())
					return true;				
			}
			
			return false;
		}
		
		private void setSelectState()
		{
			switch (_type)
			{
				case 0:
				{
					_is_read_select_all = _is_select;
						
					break;
				}
				case 1:
				{
					_is_update_select_all = _is_select;
					
					break;
				}
				case 2:
				{
					_is_export_select_all = _is_select;
					
					break;
				}
				case 3:
				{
					_is_delete_select_all = _is_select;
					
					break;
				}
			}
		}
		
		private void setIsSelect(boolean is_select)
		{
			_is_select = is_select;
		}
	}
	
	public AccessRightsTabPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{		
		addUserRadioButtonHandler();
		addUserCategoryRadioButtonHandler();
		addUserListBoxHandler();
		addUserCategoryListBoxHandler();	
		addUserProjectSaveButtonHandler();
		addRecordRadioButtonHandler();
		addIndexRadioButtonHandler();
		addTextAttributeRadioButtonHandler();
		addTextSubAttributeRadioButtonHandler();
		addTermListBoxHandler();
		addMarkAllReadButtonHandler();
		addMarkAllUpdateButtonHandler();
		addMarkAllExportButtonHandler();
		addMarkAllDeleteButtonHandler();
		addUpdateAccessRightsButtonHandler();
	}
	
	private void retrieveData(int resource_type)
	{
		resetAccessRightsPanel();
		
		if (! _access_controller.isGuest())
		{
			if (resource_type == USER)
				getUsers();
			else
				getUserCategories();
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	private void getUsers()
	{		
		_user_service.getAllUsers(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<User>>(null)
		{
			@Override
			public void onComplete(ArrayList<User> result) 
			{				
				ExtendedListBox<User> lst_user = _display.getUserListBox();
				lst_user.clear();
				
				lst_user.addItem(_i18n.getConstants().admin_user_selectUser(), "-1", null);
				
				if (result == null || result.size() == 0)				
					lst_user.setEnabled(false);
				else
				{								
					Iterator<User> iter = result.iterator();
					while (iter.hasNext())
					{
						User user = iter.next();
						lst_user.addItem(user.getUsername(), user.getUserId(), user);				
					}
										
					lst_user.setEnabled(true);
					lst_user.setSelectedIndex(0);		
				}
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
				
				_user = null;
				_user_cat = null;	
				_inputmodel = null;
				_display.getUserProjectAssigner().reset();
				resetAccessRightsPanel();
			}			
		});
	}
	
	private void getUserCategories()
	{			
		_user_cat_service.getAllUserCategoriesWithUsers(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<UserCategory>>(null)
		{
			@Override
			public void onComplete(ArrayList<UserCategory> result) 
			{				
				ExtendedListBox<UserCategory> lst_user_cat = _display.getUserCategoryListBox();				
				lst_user_cat.clear();
				
				lst_user_cat.addItem(_i18n.getConstants().admin_cat_selectCat(), "-1", null);
								
				if (result == null || result.size() == 0)				
					lst_user_cat.setEnabled(false);
				else
				{
					Iterator<UserCategory> iter = result.iterator();
					while (iter.hasNext())
					{
						UserCategory user_cat = iter.next();
						lst_user_cat.addItem(user_cat.getUserCategoryName(), user_cat.getUserCategoryId(), user_cat);				
					}
										
					lst_user_cat.setEnabled(true);
					lst_user_cat.setSelectedIndex(0);					
				}
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
				
				_user = null;
				_user_cat = null;
				_inputmodel = null;
				_display.getUserProjectAssigner().reset();	
				
				resetAccessRightsPanel();
			}			
		});	
	}
	
	private void addUserRadioButtonHandler()
	{
		_display.getUserRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{												
				_user = null;
				_user_cat = null;
				_inputmodel = null;
				
				_display.getUserCategoryListBox().setVisible(false);				
				_display.getUserListBox().setVisible(true);
				
				retrieveData(USER);		
			}
		});
	}
	
	private void addUserCategoryRadioButtonHandler()
	{
		_display.getUserCategoryRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{								
				_user = null;
				_user_cat = null;
				_inputmodel = null;
				
				_display.getUserListBox().setVisible(false);				
				_display.getUserCategoryListBox().setVisible(true);
				
				retrieveData(USER_CAT);		
			}
		});
	}
	
	private void addUserListBoxHandler()
	{
		_display.getUserListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				resetAccessRightsPanel();
				
				_inputmodel = null;
				_user_cat = null;
								
				_user = _display.getUserListBox().getSelectedItem();	
				_is_user = true;	
				
				VerticalPanel user_project_panel = _display.getUserProjectPanel();
				VerticalPanel radiobutton_panel = _display.getRadioButtonPanel();
				
				if (_user == null)
				{					
					user_project_panel.setVisible(false);										
					radiobutton_panel.setVisible(false);					
				}
				else
					retrieveAccessRightsForConsumer(_user.getUserId());
			}
		});
	}	
	
	private void addUserCategoryListBoxHandler()
	{
		_display.getUserCategoryListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				resetAccessRightsPanel();
				
				_inputmodel = null;
				_user = null;
								
				_user_cat = _display.getUserCategoryListBox().getSelectedItem();	
				_is_user = false;	
				
				VerticalPanel user_project_panel = _display.getUserProjectPanel();
				VerticalPanel radiobutton_panel = _display.getRadioButtonPanel();
				
				if (_user_cat == null)
				{					
					user_project_panel.setVisible(false);										
					radiobutton_panel.setVisible(false);					
				}
				else
					retrieveAccessRightsForConsumer(_user_cat.getUserCategoryId());
			}
		});
	}
		
	private void addUserProjectSaveButtonHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getProjectSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				event_bus.fireEvent(new AdminInterfaceEvent(AccessRightsTabPresenter.this, AdminInterfaceEvent.UPDATE_ACCESS_RIGHT));
			}
		});
	}
	
	private void addRecordRadioButtonHandler()
	{
		_display.getRecordAttributeRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{				
				_display.getGridPanel().showWidget(Display.RECORD_GRID);				
				
				populateAccessPanel(Display.RECORD_GRID);				
				setSelectDeselectStates();
			}
		});
	}
	
	private void addIndexRadioButtonHandler()
	{
		_display.getIndexRadioButton().addClickHandler(new ClickHandler()
		{		
			@Override
			public void onClick(ClickEvent event) 
			{				
				_display.getGridPanel().showWidget(Display.TERM_GRID);		
				
				populateAccessPanel(Display.TERM_GRID);				
				setSelectDeselectStates();
			}
		});
	}
	
	private void addTextAttributeRadioButtonHandler()
	{
		_display.getAttributeRadioButton().addClickHandler(new ClickHandler()
		{		
			@Override
			public void onClick(ClickEvent event) 
			{						
				_display.getGridPanel().showWidget(Display.TERMATTRIBUTE_GRID);					
				
				populateAccessPanel(Display.TERMATTRIBUTE_GRID);				
				setSelectDeselectChildStates();
			}
		});
	}
	
	private void addTextSubAttributeRadioButtonHandler()
	{
		_display.getSubAttributeRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_display.getGridPanel().showWidget(Display.TERMSUBATTRIBUTE_GRID);						
				
				populateAccessPanel(Display.TERMSUBATTRIBUTE_GRID);				
				setSelectDeselectChildStates();
			}
		});
	}
	
	private void addTermListBoxHandler()
	{
		_display.getTermListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler() 
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				if (_display.getAttributeRadioButton().getValue())
					populateDataGrid(Display.TERMATTRIBUTE_GRID);
				else if (_display.getSubAttributeRadioButton().getValue())
					populateDataGrid(Display.TERMSUBATTRIBUTE_GRID);
				
				setSelectDeselectChildStates();
			}
		});
	}
	
	private void addMarkAllReadButtonHandler()
	{		
		_read_handler = new AccessRightSelectDeselectHandler(0);
		_display.getMarkAllReadButton().addClickHandler(_read_handler);		
	}
	
	private void addMarkAllUpdateButtonHandler()
	{
		_update_handler = new AccessRightSelectDeselectHandler(1);
		_display.getMarkAllUpdateButton().addClickHandler(_update_handler);
	}
	
	private void addMarkAllExportButtonHandler()
	{
		_export_handler = new AccessRightSelectDeselectHandler(2);
		_display.getMarkAllExportButton().addClickHandler(_export_handler);
	}
	
	private void addMarkAllDeleteButtonHandler()
	{
		_delete_handler = new AccessRightSelectDeselectHandler(3);
		_display.getMarkAllDeleteButton().addClickHandler(_delete_handler);
	}
	
	private void addUpdateAccessRightsButtonHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getUpdateAccessRightsButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				event_bus.fireEvent(new AdminInterfaceEvent(AccessRightsTabPresenter.this, AdminInterfaceEvent.UPDATE_ACCESS_RIGHT));
			}
		});
	}
	
	private void retrieveAccessRightsForConsumer(long consumer_id)
	{			
		_access_right_service.getAccessRightDetails(_access_controller.getAuthToken(), consumer_id, isUserAccessRight(), new BusyDialogAsyncCallBack<AccessRightDetailsWrapper>(null)
		{
			@Override
			public void onComplete(AccessRightDetailsWrapper result) 
			{
				if (result == null)
				{
					_display.getUserProjectAssigner().reset();
					
					if (isUserAccessRight())
					{
						_user = null;
						_display.getUserListBox().setSelectedIndex(0);
					}
					else
					{
						_user_cat = null;
						_display.getUserCategoryListBox().setSelectedIndex(0);
					}
					
					return;
				}
				
				_inputmodel = result.getInputModel();	
				
				if (_inputmodel == null)
				{
					_display.getUserProjectAssigner().reset();
					
					if (isUserAccessRight())
					{
						_user = null;
						_display.getUserListBox().setSelectedIndex(0);
					}
					else
					{
						_user_cat = null;
						_display.getUserCategoryListBox().setSelectedIndex(0);
					}
					
					return;
				}								
				
				if ((_user != null && ! _user.isGuest()) ||
				   (_user_cat != null && _user_cat.getUserCategoryId() != 3))	
				{
					ArrayList<Project> available_projects = result.getAvailableProjects();
					ArrayList<Project> assigned_projects = result.getAssignedProjects();
				
					populateUserProjectPanel(available_projects, assigned_projects);
				}
				else
				{
					VerticalPanel user_project_panel = _display.getUserProjectPanel();
					user_project_panel.setVisible(false);
					
					VerticalPanel radiobutton_panel = _display.getRadioButtonPanel();
					radiobutton_panel.setVisible(true);
				}
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
				_display.getUserProjectAssigner().reset();	
				
				_display.getUserProjectAssigner().reset();
				
				if (isUserAccessRight())
				{
					_user = null;
					_display.getUserListBox().setSelectedIndex(0);
				}
				else
				{
					_user_cat = null;
					_display.getUserCategoryListBox().setSelectedIndex(0);
				}
			}			
		});
	}
	
	private void populateAccessPanel(int type)
	{
		if (isUserAccessRight())
		{
			_display.getUserProjectLabel().setText(_i18n.getConstants().admin_access_rights_project_prompt() + "'" + _user.getUsername() + "'.");
			_display.getAccessRightsLabel().setText(_i18n.getConstants().admin_access_rights_prompt() + "'" + _user.getUsername() + "'.");
		}
		else
		{
			_display.getUserProjectLabel().setText(_i18n.getConstants().admin_access_rights_project_prompt() + "'" + _user_cat.getUserCategoryName() + "'.");
			_display.getAccessRightsLabel().setText(_i18n.getConstants().admin_access_rights_prompt() + "'" + _user_cat.getUserCategoryName() + "'.");
		}		
			
		VerticalPanel rights_panel = _display.getRightsPanel();
		rights_panel.setVisible(true);
		
		if ((_user != null && _user.isGuest()) ||
			 (_user_cat != null && _user_cat.getUserCategoryId() == 3))	
			{
				setNonGuestRightColumnWidth(type, 0);
			}
			else
				setNonGuestRightColumnWidth(type, 55);
									
		
		ExtendedListBox<Term> lst_term = _display.getTermListBox();		
		Label lbl_term = _display.getTermLabel();
		
		if (type == Display.TERMATTRIBUTE_GRID || type == Display.TERMSUBATTRIBUTE_GRID)
		{			
			lst_term.clear();
			lst_term.setVisible(true);
			
			ArrayList<TerminlogyObject> terms = _inputmodel.getTerms();
			Iterator<TerminlogyObject> iter = terms.iterator();
			while (iter.hasNext())
			{
				Term term = (Term)iter.next();						
				lst_term.addItem(term.getFieldName(), term.getFieldId(), term);												
			}
		}
		else
		{
			lst_term.setVisible(false);
			lbl_term.setVisible(false);
		}
		
		populateDataGrid(type);
	}
	
	private void populateUserProjectPanel(ArrayList<Project> available, ArrayList<Project> assigned_projects)
	{
		VerticalPanel user_project_panel = _display.getUserProjectPanel();
		VerticalPanel radiobutton_panel = _display.getRadioButtonPanel();
		
		user_project_panel.setVisible(true);
		radiobutton_panel.setVisible(true);
		
		UserProjectAccessPanel assigner = _display.getUserProjectAssigner();	
		assigner.reset();
		assigner.setAvailableProjects(available);
				
		if (assigned_projects == null || assigned_projects.size() == 0)
		{
			assigner.setAssignedProjects(new ArrayList<Project>());
			return;
		}
					
		assigner.setAssignedProjects(assigned_projects);						
	}
	
	private void setNonGuestRightColumnWidth(int type, int width)
	{		
		switch(type)
		{
			case Display.RECORD_GRID:
			{				
				DataGrid<AccessRight> data_grid = _display.getRecordAttributeDataGrid();
				ArrayList<Column<AccessRight, Boolean>> non_guest_columns = _display.getRecordNonGuestColumns();
			
				Iterator<Column<AccessRight, Boolean>> iter = non_guest_columns.iterator();
				while (iter.hasNext())
				{
					Column<AccessRight, Boolean> column = iter.next();
					data_grid.setColumnWidth(column, width, Unit.PX);
				}
				
				break;
			}
			case Display.TERM_GRID:
			{
				DataGrid<AccessRight> data_grid = _display.getTermDataGrid();
				ArrayList<Column<AccessRight, Boolean>> non_guest_columns = _display.getTermNonGuestColumns();
				
				Iterator<Column<AccessRight, Boolean>> iter = non_guest_columns.iterator();
				while (iter.hasNext())
				{
					Column<AccessRight, Boolean> column = iter.next();
					data_grid.setColumnWidth(column, width, Unit.PX);
				}
				
				break;
			}
			case Display.TERMATTRIBUTE_GRID:
			{
				DataGrid<ChildAccessRight> data_grid = _display.getTermAttributeDataGrid();
				ArrayList<Column<ChildAccessRight, Boolean>> non_guest_columns = _display.getTermAttributeNonGuestColumns();
				
				Iterator<Column<ChildAccessRight, Boolean>> iter = non_guest_columns.iterator();
				while (iter.hasNext())
				{
					Column<ChildAccessRight, Boolean> column = iter.next();
					data_grid.setColumnWidth(column, width, Unit.PX);
				}
				
				break;
			}
			case Display.TERMSUBATTRIBUTE_GRID:
			{
				DataGrid<ChildAccessRight> data_grid = _display.getTermSubAttributeDataGrid();
				ArrayList<Column<ChildAccessRight, Boolean>> non_guest_columns = _display.getTermSubAttributeNonGuestColumns();
				
				Iterator<Column<ChildAccessRight, Boolean>> iter = non_guest_columns.iterator();
				while (iter.hasNext())
				{
					Column<ChildAccessRight, Boolean> column = iter.next();
					data_grid.setColumnWidth(column, width, Unit.PX);
				}
				
				break;
			}
		}
	}
	
	private void populateDataGrid(int type)
	{	
		ExtendedListBox<Term> lst_term = _display.getTermListBox();
		Label lbl_term = _display.getTermLabel();
		
		switch (type)
		{
			case Display.RECORD_GRID:
			{
				ListDataProvider<AccessRight> data_provider = _display.getRecordAttributeDataProvider();
				List<AccessRight> access_rights = data_provider.getList();
				
				populateDataGridWithAccessRights(_inputmodel.getRecordAttributes(), access_rights);
				
				data_provider.refresh();
				
				break;
			}
			case Display.TERM_GRID:
			{
				ListDataProvider<AccessRight> data_provider = _display.getTermDataProvider();
				List<AccessRight> access_rights = data_provider.getList();
				
				populateDataGridWithAccessRights(_inputmodel.getTerms(), access_rights);
				
				data_provider.refresh();
				
				break;
			}
			case Display.TERMATTRIBUTE_GRID:
			{		
				lbl_term.setVisible(true);
				lbl_term.setText(_i18n.getConstants().admin_access_rights_prompt() +  _i18n.getConstants().admin_im_labelAttributeFields() + " for");
				
				ListDataProvider<ChildAccessRight> data_provider = _display.getTermAttributeDataProvider();
				List<ChildAccessRight> access_rights = data_provider.getList();
								
				Term term = lst_term.getSelectedItem();
				populateDataGridWithChildAccessRights(_inputmodel.getTermAttributesForTerm(term), access_rights);
												
				data_provider.refresh();
				
				break;
			}
			case Display.TERMSUBATTRIBUTE_GRID:
			{
				AppProperties props = _access_controller.getAppProperties();
				
				lbl_term.setVisible(true);
				lbl_term.setText(_i18n.getConstants().admin_access_rights_prompt() + "a " + props.getSynonymField() + " Field for");
				
				ListDataProvider<ChildAccessRight> data_provider= _display.getTermSubAttributeDataProvider();
				List<ChildAccessRight> access_rights = data_provider.getList();
				
				Term term = lst_term.getSelectedItem();															
				populateDataGridWithChildAccessRights(_inputmodel.getSynonymAttributesForTerm(term), access_rights);								
				
				data_provider.refresh();
				
				break;
			}
		}				
	}	
		
	private void populateDataGridWithAccessRights(ArrayList<TerminlogyObject> attributes, List<AccessRight> access_rights)
	{
		access_rights.clear();
		
		Iterator<TerminlogyObject> iter = attributes.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject attribute = iter.next();
			
			if (isUserAccessRight())
				access_rights.add(attribute.getUserAccessRight());
			else
				access_rights.add(attribute.getUserCategoryAccessRight());				
		}
	}
	
	private void populateDataGridWithChildAccessRights(ArrayList<ChildTerminologyObject> child_attributes, List<ChildAccessRight> access_rights)
	{
		access_rights.clear();
		
		Iterator<ChildTerminologyObject> iter = child_attributes.iterator();
		while (iter.hasNext())
		{
			ChildTerminologyObject child_attribute = iter.next();
			
			if (isUserAccessRight())
				access_rights.add((ChildAccessRight) child_attribute.getUserAccessRight());
			else
				access_rights.add((ChildAccessRight) child_attribute.getUserCategoryAccessRight());				
		}
	}
	
	private void setSelectDeselectStates()
	{		
		RadioButton btn_record = _display.getRecordAttributeRadioButton();
		RadioButton btn_term = _display.getIndexRadioButton();
		List<AccessRight> access_rights = null;
		
		if (btn_record.getValue())		
			access_rights = _display.getRecordAttributeDataProvider().getList();
		else if (btn_term.getValue())
			access_rights = _display.getTermDataProvider().getList();
		  
		int size = access_rights.size();
		
		int read_count = 0;
		int update_count = 0;
		int export_count = 0;
		int delete_count = 0;
		
		Iterator<AccessRight> iter = access_rights.iterator();
		while (iter.hasNext())
		{
			AccessRight access_right = iter.next();
			
			if (access_right.mayRead())
				read_count++;
			
			if (access_right.mayUpdate())
				update_count++;
			
			if (access_right.mayExport())
				export_count++;
			
			if (access_right.mayDelete())
				delete_count++;
		}
		
		if (size == read_count)
			_is_read_select_all = false;
		else
			_is_read_select_all = true;
		
		if (size == update_count)
			_is_update_select_all = false;
		else
			_is_update_select_all = true;
		
		if (size == export_count)
			_is_export_select_all = false;
		else
			_is_export_select_all = true;
		
		if (size == delete_count)
			_is_delete_select_all = false;
		else
			_is_delete_select_all = true;
		
		_read_handler.setIsSelect(_is_read_select_all);
		_update_handler.setIsSelect(_is_update_select_all);
		_export_handler.setIsSelect(_is_export_select_all);
		_delete_handler.setIsSelect(_is_delete_select_all);
	}
	
	private void setSelectDeselectChildStates()
	{				
		RadioButton btn_attr = _display.getAttributeRadioButton();
		RadioButton btn_subattr = _display.getSubAttributeRadioButton();
		List<ChildAccessRight> access_rights = null;
		ArrayList<ChildTerminologyObject> child_attributes = null;
		Term term = _display.getTermListBox().getSelectedItem();
		
		if (btn_attr.getValue())		
		{
			access_rights = _display.getTermAttributeDataProvider().getList();
			child_attributes = _inputmodel.getTermAttributesForTerm(term);
		}
		else if (btn_subattr.getValue())
		{
			access_rights = _display.getTermSubAttributeDataProvider().getList();
			child_attributes = _inputmodel.getSynonymAttributesForTerm(term);
		}
		  
		int size = child_attributes.size();
		
		int read_count = 0;
		int update_count = 0;
		int export_count = 0;
		int delete_count = 0;
						
		Iterator<ChildTerminologyObject> iter = child_attributes.iterator();
		while (iter.hasNext())
		{
			ChildTerminologyObject child_attribute = iter.next();
			
			ChildAccessRight access_right = getChildTerminologyObjectChildAccessRightInView(child_attribute, access_rights);
			
			if (access_right == null)
				continue;
			
			if (access_right.mayRead())
				read_count++;
			
			if (access_right.mayUpdate())
				update_count++;
			
			if (access_right.mayExport())
				export_count++;
			
			if (access_right.mayDelete())
				delete_count++;
		}
		
		if (size == read_count)
			_is_read_select_all = false;
		else
			_is_read_select_all = true;
		
		if (size == update_count)
			_is_update_select_all = false;
		else
			_is_update_select_all = true;
		
		if (size == export_count)
			_is_export_select_all = false;
		else
			_is_export_select_all = true;
		
		if (size == delete_count)
			_is_delete_select_all = false;
		else
			_is_delete_select_all = true;
		
		_read_handler.setIsSelect(_is_read_select_all);
		_update_handler.setIsSelect(_is_update_select_all);
		_export_handler.setIsSelect(_is_export_select_all);
		_delete_handler.setIsSelect(_is_delete_select_all);
	}
	
	private ChildAccessRight getChildTerminologyObjectChildAccessRightInView(ChildTerminologyObject child_attribute, List<ChildAccessRight> access_rights)
	{
		Iterator<ChildAccessRight> iter = access_rights.iterator();
		while (iter.hasNext())
		{
			ChildAccessRight access_right = iter.next();
			
			if (access_right.getFieldId() == child_attribute.getFieldId())
				return access_right;
		}
		
		return null;
	}
	
	private void resetAccessRightsPanel()
	{
		VerticalPanel user_project_panel = _display.getUserProjectPanel();
		user_project_panel.setVisible(false);
		
		VerticalPanel radiobutton_panel = _display.getRadioButtonPanel();
		radiobutton_panel.setVisible(false);
		
		VerticalPanel rights_panel = _display.getRightsPanel();
		rights_panel.setVisible(false);
		
		_display.getTermLabel().setText("");
		_display.getTermLabel().setVisible(false);
		
		_display.getTermListBox().clear();
		_display.getTermListBox().setVisible(false);
		
		_display.getRecordAttributeRadioButton().setValue(false);
		_display.getIndexRadioButton().setValue(false);
		_display.getAttributeRadioButton().setValue(false);
		_display.getSubAttributeRadioButton().setValue(false);
	}
		
	@Override
	public void go(HasWidgets container) 
	{				
		TabLayoutPanel admin_tab_panel = (TabLayoutPanel)container;		
		admin_tab_panel.add(_display.asWidget(), _display.getHeadingLabel());
		
		bind();
	}

	@Override
	public void loadAdminTabData() 
	{
		_display.getUserCategoryListBox().setVisible(false);				
		_display.getUserListBox().setVisible(true);
		_display.getUserRadioButton().setValue(true);
		retrieveData(USER);
	}
	
	public Display getDisplay()
	{
		return _display;
	}
		
	public void setUser(User user)
	{
		_user = user;
	}
	
	public User getUser()
	{
		return _user;
	}
	
	public void setUserCategory(UserCategory user_cat)
	{
		_user_cat = user_cat;
	}
	
	public UserCategory getUserCategory()
	{
		return _user_cat;
	}
		
	public boolean isUserAccessRight()
	{
		return _is_user;
	}
	
	public InputModel getInputModel()
	{
		return _inputmodel;
	}
}
