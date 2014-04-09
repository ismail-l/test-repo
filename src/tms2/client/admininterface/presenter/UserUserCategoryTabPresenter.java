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
import java.util.Date;
import java.util.Iterator;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.AdminTabPresenter;
import tms2.client.service.UserCategoryService;
import tms2.client.service.UserCategoryServiceAsync;
import tms2.client.service.UserService;
import tms2.client.service.UserServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedDatePicker;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.User;
import tms2.shared.UserCategory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * AdminTabPresenter class to manage UserCategories and Users.
 * 
 * @author I. Lavangee
 *
 */
public class UserUserCategoryTabPresenter implements AdminTabPresenter 
{
	private static UserCategoryServiceAsync _user_categories_service = GWT.create(UserCategoryService.class);
	private static UserServiceAsync _user_service = GWT.create(UserService.class);
	
	private Display _display = null;
	
	private static AccessController _access_controller = AccessController.getInstance();
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private UserCategory _user_cat = null;
	private User _user = null;
	
	public interface Display
	{
		public ExtendedListBox<UserCategory> getUserCategoryListBox();
		public Button getNewUserCategoryButton();
		public TextBox getUserCategoryNameTextBox();
		public CheckBox getIsAdminCheckBox();
		public Button getUserCategorySaveButton();
		public ExtendedListBox<User> getUserListBox();
		public Button getNewUserButton();
		public TextBox getFirstNameTextBox();
		public TextBox getLastNameTextBox();
		public TextBox getUserNameTextBox();
		public TextBox getPasswordTextBox();
		public CheckBox getActivatedCheckBox();
		public ExtendedDatePicker getDatePicker();
		public ExtendedListBox<UserCategory> getUserUserCategoryListBox();
		public Label getLastSignOnLabel();
		public Button getUserSaveButton();		
		public VerticalPanel getUserPanel();
		public Label getHeadingLabel();
		public Widget asWidget();
	}
	
	public UserUserCategoryTabPresenter(Display display)
	{
		_display = display;						
	}
	
	private void bind()
	{
		addUserCategoryListBoxHandler();
		addUserListBoxHandler();
		addNewUserCategoryHandler();
		addNewUserHandler();
		addUserCategorySaveHandler();	
		addUserSaveHandler();
		addUserNameHandler();
	}
	
	private void retrieveUserCategories()
	{
		resetUserCategoryPanel();
		
		_user_cat = null;
		
		if (! _access_controller.isGuest())
		{
			_user_categories_service.getAllUserCategoriesWithUsers(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<UserCategory>>(null) 
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
				}
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	private void addUserCategoryListBoxHandler()
	{
		_display.getUserCategoryListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler() 
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_user_cat = _display.getUserCategoryListBox().getSelectedItem();
				populateUserCategoryPanel();
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
				_user = _display.getUserListBox().getSelectedItem();
				populateUserPanel();
			}
		});
	}
	
	private void addNewUserCategoryHandler()
	{
		_display.getNewUserCategoryButton().addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetUserCategoryPanel();
				
				_user_cat = null;
				_user = null;
				
				_display.getUserCategoryListBox().setSelectedIndex(0);
			}
		});
	}
	
	private void addNewUserHandler()
	{
		_display.getNewUserButton().addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetUserPanel(true);
					
				_user = null;
			}
		});
	}
	
	private void addUserCategorySaveHandler()
	{		
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getUserCategorySaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateUserCategory())
				{
					if (_user_cat == null)
					{
						_user_cat = new UserCategory();
						
						fillUserCategoryForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(UserUserCategoryTabPresenter.this, AdminInterfaceEvent.UPDATE_USER_CATEGORY));
					}
					else
					{
						fillUserCategoryForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(UserUserCategoryTabPresenter.this, AdminInterfaceEvent.UPDATE_USER_CATEGORY));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_cat_validate());
			}
		});
	}
	
	private void addUserSaveHandler()
	{		
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getUserSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateUser())
				{
					if (_user == null)
					{
						_user = new User();
						
						fillUserForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(UserUserCategoryTabPresenter.this, AdminInterfaceEvent.UPDATE_USER));
					}
					else
					{
						fillUserForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(UserUserCategoryTabPresenter.this, AdminInterfaceEvent.UPDATE_USER));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_user_validate());				
			}
		});
	}
	
	private void addUserNameHandler()
	{
		final AccessController access_controller = AccessController.getInstance();
		final TextBox txt_username = _display.getUserNameTextBox();
				
		txt_username.addBlurHandler(new BlurHandler()
		{			
			@Override
			public void onBlur(BlurEvent event) 
			{
				if (! txt_username.getText().isEmpty())
				{	
					if (! _access_controller.isGuest())
					{
						_user_service.verifyUsernameExistence(access_controller.getAuthToken(), txt_username.getText(), new BusyDialogAsyncCallBack<Boolean>(null) 
						{
							@Override
							public void onComplete(Boolean result) 
							{
								if (result)							
									AlertBox.show(_i18n.getMessages().admin_user_exist(txt_username.getText()));							
							}
	
							@Override
							public void onError(Throwable caught) 
							{		
								ErrorBox.ErrorHandler.handle(caught);							
							}						
						});
					}
					else
						_access_controller.getEventBus().fireEvent(new SignOffEvent());
				}					
			}
		});
		
		TextBox txt_firstname = _display.getFirstNameTextBox();
		txt_firstname.addChangeHandler(new AutomaticUserNameHandler());
		
		TextBox txt_lastname = _display.getLastNameTextBox();				
		txt_lastname.addChangeHandler(new AutomaticUserNameHandler());
	}
	
	private class AutomaticUserNameHandler implements ChangeHandler
	{		
		@Override
		public void onChange(ChangeEvent event) 
		{						
			TextBox txt_firstname = _display.getFirstNameTextBox();
			TextBox txt_lastname = _display.getLastNameTextBox();
			final TextBox txt_username = _display.getUserNameTextBox();
						
			if (! txt_firstname.getText().isEmpty() && ! txt_lastname.getText().isEmpty())
			{
				if (! _access_controller.isGuest())
				{
					_user_service.generateUsername(_access_controller.getAuthToken(), txt_firstname.getText(), txt_lastname.getText(), new BusyDialogAsyncCallBack<String>(null)
					{
						@Override
						public void onComplete(String result) 
						{
							AlertBox.show(_i18n.getMessages().admin_user_recommend(result));
							txt_username.setText(result);
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							txt_username.setText("");
							
							ErrorBox.ErrorHandler.handle(caught);						
						}					
					});
				}
				else
					_access_controller.getEventBus().fireEvent(new SignOffEvent());
			}
		}		
	}
	
	private void populateUserCategoryPanel()
	{				
		resetUserCategoryPanel();
		
		if (_user_cat != null)
		{
			TextBox txt_user_cat_name = _display.getUserCategoryNameTextBox();
			CheckBox chk_is_admin = _display.getIsAdminCheckBox();										
			
			txt_user_cat_name.setText(_user_cat.getUserCategoryName());
			chk_is_admin.setValue(_user_cat.isAdmin());			
			
			populateUserListBox();
		}
	}
	
	private void populateUserListBox()
	{
		VerticalPanel user_panel = _display.getUserPanel();
		user_panel.setVisible(true);
		
		ExtendedListBox<User> lst_user = _display.getUserListBox();
		lst_user.clear();
		
		lst_user.addItem(_i18n.getConstants().admin_user_selectUser(), "-1", null);
		
		ArrayList<User> users = _user_cat.getUsers();
		
		if (users == null || users.size() == 0)
			lst_user.setEnabled(false);
		else
		{						
			Iterator<User> iter = users.iterator();
			while (iter.hasNext())
			{
				User user = iter.next();
				lst_user.addItem(user.getUsername(), user.getUserId() + "", user);
			}
			
			lst_user.setEnabled(true);
		}
		
		ExtendedListBox<UserCategory> lst_user_user_cat = _display.getUserUserCategoryListBox();
		lst_user_user_cat.clear();
		
		ArrayList<UserCategory> user_categories = _display.getUserCategoryListBox().getItems();
		
		int selected_index = 0;
		boolean found = false;
		
		Iterator<UserCategory> iter = user_categories.iterator();
		while (iter.hasNext())
		{
			UserCategory user_cat = iter.next();
			if (user_cat != null)
			{
				if (user_cat.getUserCategoryId() == _user_cat.getUserCategoryId())
					found = true;
				
				lst_user_user_cat.addItem(user_cat.getUserCategoryName(), user_cat.getUserCategoryId(), user_cat);
			}
			else
				lst_user_user_cat.addItem(_i18n.getConstants().admin_cat_selectCat(), "-1", null);
			
			if (! found)
				selected_index++;
		}
		
		lst_user_user_cat.setSelectedIndex(selected_index);
	}
	
	private void populateUserPanel()
	{	
		if (_user != null)
		{			
			TextBox txt_username = _display.getUserNameTextBox();
			TextBox txt_firstname = _display.getFirstNameTextBox();
			TextBox txt_lastname = _display.getLastNameTextBox();
			Label lbl_last_signon = _display.getLastSignOnLabel();			
			ExtendedDatePicker dp_datepicker = _display.getDatePicker();
			DateTimeFormat date_formatter = DateTimeFormat.getFormat("dd MMMM yyyy");
			CheckBox chk_activated = _display.getActivatedCheckBox();
			
			txt_firstname.setText(_user.getFirstName());
			txt_lastname.setText(_user.getLastName());
			txt_username.setText(_user.getUsername());
			
			if (_user.getLastSignOn() != null)
				lbl_last_signon.setText(date_formatter.format(_user.getLastSignOn()));
			else
				lbl_last_signon.setText("");
			
			if (_user.getExpiryDate() != null)
				dp_datepicker.setValue(_user.getExpiryDate());	
			
			chk_activated.setValue(_user.isActivated());
						
			Button btn_user_save = _display.getUserSaveButton();
			btn_user_save.setEnabled(true);
		}
		else
			resetUserPanel(true);
	}
	
	private void resetUserCategoryPanel()
	{		
		TextBox txt_user_cat_name = _display.getUserCategoryNameTextBox();
		CheckBox chk_is_admin = _display.getIsAdminCheckBox();
		
		txt_user_cat_name.setText("");
		chk_is_admin.setValue(false);
				
		resetUserPanel(false);
		
		_user = null;
	}
	
	private void resetUserPanel(boolean visible)
	{				
		TextBox txt_username = _display.getUserNameTextBox();
		txt_username.setText("");
		
		TextBox txt_firstname = _display.getFirstNameTextBox();
		txt_firstname.setText("");
		
		TextBox txt_lastname = _display.getLastNameTextBox();
		txt_lastname.setText("");
		
		TextBox txt_password = _display.getPasswordTextBox();
		txt_password.setText("");
		
		Label lbl_last_signon = _display.getLastSignOnLabel();
		lbl_last_signon.setText("");
		
		CheckBox chk_activated = _display.getActivatedCheckBox();
		chk_activated.setValue(true);
		
		ExtendedDatePicker dp_datepicker = _display.getDatePicker();
		dp_datepicker.setOpen(false);
				
		VerticalPanel user_panel = _display.getUserPanel();
		user_panel.setVisible(visible);
	}
		
	private boolean validateUserCategory()
	{
		TextBox txt_user_cat_name = _display.getUserCategoryNameTextBox();

		if (txt_user_cat_name.getText().isEmpty())
			return false;
						
		return true;
	}
	
	private boolean validateUser()
	{
		TextBox txt_username = _display.getUserNameTextBox();		
		TextBox txt_firstname = _display.getFirstNameTextBox();		
		TextBox txt_lastname = _display.getLastNameTextBox();		
		
		if (txt_firstname.getText().isEmpty())
			return false;
		
		if (txt_username.getText().isEmpty())
			return false;
		
		if (txt_lastname.getText().isEmpty())
			return false;
				
		return true;				
	}
	
	private void fillUserCategoryForEvent()
	{
		_user_cat.setUserCategoryName(_display.getUserCategoryNameTextBox().getText());
		_user_cat.setAdmin(_display.getIsAdminCheckBox().getValue());
	}
	
	private void fillUserForEvent()
	{
		_user.setFirstName(_display.getFirstNameTextBox().getText());
		_user.setLastName(_display.getLastNameTextBox().getText());
		_user.setUsername(_display.getUserNameTextBox().getText());
		_user.setActivated(_display.getActivatedCheckBox().getValue());
		
		long user_cat_id = -1;
		ExtendedListBox<UserCategory> lst_user_user_cat = _display.getUserUserCategoryListBox();
		if (lst_user_user_cat.getSelectedItem() == null)
			user_cat_id = _display.getUserCategoryListBox().getSelectedItem().getUserCategoryId();
		else
			user_cat_id = lst_user_user_cat.getSelectedItem().getUserCategoryId();
		
		_user.setUserCategoryId(user_cat_id);
		
		Date expiry_date = _display.getDatePicker().getValue();
		if (expiry_date != null)
			_user.setExpiryDate(expiry_date);
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
		retrieveUserCategories();		
	}
	
	public Display getDisplay()
	{
		return _display;
	}
	
	public UserCategory getUserCategory()
	{
		return _user_cat;
	}
	
	public void setUserCategory(UserCategory user_cat)
	{
		_user_cat = user_cat;
	}
	
	public User getUser()
	{
		return  _user;
	}
	
	public void setUser(User user)
	{
		_user = user;
	}
}
