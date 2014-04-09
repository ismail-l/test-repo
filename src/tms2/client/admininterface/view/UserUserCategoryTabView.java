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

package tms2.client.admininterface.view;

import java.util.Date;

import tms2.client.admininterface.presenter.UserUserCategoryTabPresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.widgets.AdminTab;
import tms2.client.widgets.ExtendedDatePicker;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.User;
import tms2.shared.UserCategory;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserUserCategoryTabView extends AdminTab implements UserUserCategoryTabPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _user_usercat_panel = null;
	private VerticalPanel _usercat_panel = null;
	private VerticalPanel _user_panel = null;
	private ExtendedListBox<UserCategory> _lst_usercat = null;
	private Button _btn_newusercat = null;
	private TextBox _txt_usercat_name = null;
	private CheckBox _chk_is_admin = null;
	private Button _btn_usercat_save=  null;
	
	private ExtendedListBox<User> _lst_user = null;
	private Button _btn_newuser = null;
	private TextBox _txt_firstname = null;
	private TextBox _txt_lastname = null;
	private TextBox _txt_username = null;
	private TextBox _txt_password = null;
	private CheckBox _chk_activated = null;
	private ExtendedDatePicker _dp_datepicker = null;
	private ExtendedListBox<UserCategory> _lst_user_usercat = null;
	private Label _lbl_lastsignon = null;
	private Button _btn_user_save = null;
	
	public UserUserCategoryTabView() 
	{
		super(_i18n.getConstants().admin_tab_UserAndCategories());
		
		_user_usercat_panel = new VerticalPanel();
		_user_usercat_panel.setSpacing(20);
		
		buildUserCategoryPanel();
		buildUserPanel();
		
		super.add(_user_usercat_panel);
	}
	
	private void buildUserCategoryPanel()
	{				
		_usercat_panel = new VerticalPanel();
		
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_cat_heading(), false);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_usercat_panel.add(lbl_first_heading);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_usercat_panel.add(lbl_dummy);		
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_cat_label2(), false);
		lbl_second_heading.addStyleName("labelTextBold");
		lbl_second_heading.addStyleName("plainLabelText");		
		lbl_second_heading.addStyleName("paddedBottom");
		
		_usercat_panel.add(lbl_second_heading);
		
		Label lbl_third_heading = new Label(_i18n.getConstants().admin_cat_info(), true);
		lbl_third_heading.addStyleName("plainLabelText");
		lbl_third_heading.addStyleName("tabHeading");
		
		_usercat_panel.add(lbl_third_heading);
		
		FlexTable user_cat_details_panel = new FlexTable();
		user_cat_details_panel.setCellSpacing(10);
				
		_lst_usercat = new ExtendedListBox<UserCategory>(false);
		
		_btn_newusercat = new Button(_i18n.getConstants().admin_cat_newCategory());
		_btn_newusercat.addStyleName("adminButton");
		
		user_cat_details_panel.setWidget(0, 0, _lst_usercat);
		user_cat_details_panel.setWidget(0, 1, _btn_newusercat);
										
		Label lbl_usercat_details = new Label(_i18n.getConstants().admin_cat_categoryName(), false);
		lbl_usercat_details.addStyleName("labelTextBold");
		lbl_usercat_details.addStyleName("plainLabelText");
		
		_txt_usercat_name = new TextBox();
		_txt_usercat_name.setWidth("200px");
		
		user_cat_details_panel.setWidget(1, 0, lbl_usercat_details);
		user_cat_details_panel.setWidget(1, 1, _txt_usercat_name);
		
		Label lbl_is_admin = new Label(_i18n.getConstants().admin_cat_isAdmin(), false);
		lbl_is_admin.addStyleName("labelTextBold");
		lbl_is_admin.addStyleName("plainLabelText");
		
		_chk_is_admin = new CheckBox();
		
		user_cat_details_panel.setWidget(2, 0, lbl_is_admin);
		user_cat_details_panel.setWidget(2, 1, _chk_is_admin);
						
		_btn_usercat_save = new Button(_i18n.getConstants().controls_save());
		_btn_usercat_save.addStyleName("adminButton");
		
		user_cat_details_panel.setWidget(3, 0, new Label());
		user_cat_details_panel.setWidget(3, 1, _btn_usercat_save);
		
		_usercat_panel.add(user_cat_details_panel);
		
		_user_usercat_panel.add(_usercat_panel);
	}
	
	public void buildUserPanel()
	{
		_user_panel = new VerticalPanel();
		_user_panel.setStyleName("borderedBlock");
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_user_panel.add(lbl_dummy);	
		
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_user_label(), false);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_user_panel.add(lbl_first_heading);
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_user_info(), true);
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("tabHeading");
		
		_user_panel.add(lbl_second_heading);
		
		HorizontalPanel select_user_panel = new HorizontalPanel();
		select_user_panel.setSpacing(5);
		
		_lst_user = new ExtendedListBox<User>(false);
		
		_btn_newuser = new Button(_i18n.getConstants().admin_user_newUser());
		_btn_newuser.addStyleName("adminButton");
		
		select_user_panel.add(_lst_user);
		select_user_panel.add(_btn_newuser);
		
		_user_panel.add(select_user_panel);
		
		FlexTable user_details_panel = new FlexTable();
		user_details_panel.setCellSpacing(10);
		
		Label lbl_firstname = new Label(_i18n.getConstants().admin_user_firstName(), false);
		lbl_firstname.addStyleName("labelTextBold");
		lbl_firstname.addStyleName("plainLabelText");
		
		_txt_firstname = new TextBox();
		
		user_details_panel.setWidget(0, 0, lbl_firstname);
		user_details_panel.setWidget(0, 1, _txt_firstname);
		
		Label lbl_lastname = new Label(_i18n.getConstants().admin_user_lastName(), false);
		lbl_lastname.addStyleName("labelTextBold");
		lbl_lastname.addStyleName("plainLabelText");
		
		_txt_lastname = new TextBox();
		
		user_details_panel.setWidget(0, 2, lbl_lastname);
		user_details_panel.setWidget(0, 3, _txt_lastname);
		
		Label lbl_username = new Label(_i18n.getConstants().admin_user_username(), false);
		lbl_username.addStyleName("labelTextBold");
		lbl_username.addStyleName("plainLabelText");
		
		_txt_username = new TextBox();
		
		user_details_panel.setWidget(1, 0, lbl_username);
		user_details_panel.setWidget(1, 1, _txt_username);
		
		Label lbl_password = new Label(_i18n.getConstants().admin_user_password(), false);
		lbl_password.addStyleName("labelTextBold");
		lbl_password.addStyleName("plainLabelText");
		
		_txt_password = new TextBox();
		
		user_details_panel.setWidget(1, 2, lbl_password);
		user_details_panel.setWidget(1, 3, _txt_password);
		
		Label lbl_activated = new Label(_i18n.getConstants().admin_user_activated(), false);
		lbl_activated.addStyleName("labelTextBold");
		lbl_activated.addStyleName("plainLabelText");
		
		_chk_activated = new CheckBox();
		_chk_activated.setValue(true);
		
		user_details_panel.setWidget(2, 0, lbl_activated);
		user_details_panel.setWidget(2, 1, _chk_activated);
		
		Label lbl_expiry_date = new Label(_i18n.getConstants().admin_user_expireDate(), false);
		lbl_expiry_date.addStyleName("labelTextBold");
		lbl_expiry_date.addStyleName("plainLabelText");
		
		_dp_datepicker = new ExtendedDatePicker(_i18n.getConstants().admin_user_expireDate2());
		_dp_datepicker.setOpen(false);
		_dp_datepicker.setMinimumDate(new Date());
		
		user_details_panel.setWidget(2, 2, lbl_expiry_date);
		user_details_panel.setWidget(2, 3, _dp_datepicker);
		
		Label lbl_usercat = new Label(_i18n.getConstants().admin_user_category(), false);
		lbl_usercat.addStyleName("labelTextBold");
		lbl_usercat.addStyleName("plainLabelText");
		
		_lst_user_usercat = new ExtendedListBox<UserCategory>(false);
		
		user_details_panel.setWidget(3, 0, lbl_usercat);
		user_details_panel.setWidget(3, 1, _lst_user_usercat);
		
		Label lbl_lastsignon = new Label((_i18n.getConstants().admin_user_lastSignOn()), false);
		lbl_lastsignon.addStyleName("labelTextBold");
		lbl_lastsignon.addStyleName("plainLabelText");
		
		_lbl_lastsignon = new Label("", false);
		_lbl_lastsignon.addStyleName("plainLabelText");
		
		user_details_panel.setWidget(3, 2, lbl_lastsignon);
		user_details_panel.setWidget(3, 3, _lbl_lastsignon);
		
		_btn_user_save = new Button(_i18n.getConstants().controls_save());
		_btn_user_save.addStyleName("adminButton");
				
		user_details_panel.setWidget(8, 0, new Label());
		user_details_panel.setWidget(7, 1, _btn_user_save);
		
		_user_panel.add(user_details_panel);
		_user_panel.setVisible(false);
		
		_user_usercat_panel.add(_user_panel);
	}
	
	@Override
	public Label getHeadingLabel() 
	{	
		return super.getTabHeading();
	}

	@Override
	public ExtendedListBox<UserCategory> getUserCategoryListBox() 
	{
		return _lst_usercat;
	}

	@Override
	public Button getNewUserCategoryButton() 
	{
		return _btn_newusercat;
	}

	@Override
	public TextBox getUserCategoryNameTextBox() 
	{
		return _txt_usercat_name;
	}

	@Override
	public CheckBox getIsAdminCheckBox() 
	{
		return _chk_is_admin;
	}

	@Override
	public Button getUserCategorySaveButton() 
	{
		return _btn_usercat_save;
	}

	@Override
	public ExtendedListBox<User> getUserListBox() 
	{
		return _lst_user;
	}

	@Override
	public Button getNewUserButton() 
	{
		return _btn_newuser;
	}

	@Override
	public TextBox getFirstNameTextBox() 
	{
		return _txt_firstname;
	}

	@Override
	public TextBox getLastNameTextBox() 
	{
		return _txt_lastname;
	}

	@Override
	public TextBox getUserNameTextBox() 
	{
		return _txt_username;
	}

	@Override
	public TextBox getPasswordTextBox() 
	{
		return _txt_password;
	}

	@Override
	public CheckBox getActivatedCheckBox()
{
		return _chk_activated;
	}

	@Override
	public ExtendedDatePicker getDatePicker() 
	{
		return _dp_datepicker;
	}

	@Override
	public ExtendedListBox<UserCategory> getUserUserCategoryListBox() 
	{
		return _lst_user_usercat;
	}

	@Override
	public Label getLastSignOnLabel() 
	{
		return _lbl_lastsignon;
	}

	@Override
	public Button getUserSaveButton() 
	{
		return _btn_user_save;
	}

	@Override
	public VerticalPanel getUserPanel() 
	{	
		return _user_panel;
	}

}
