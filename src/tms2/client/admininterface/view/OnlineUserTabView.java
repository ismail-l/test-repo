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

import tms2.client.admininterface.presenter.OnlineUserTabPresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.widgets.AdminTab;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class OnlineUserTabView extends AdminTab implements OnlineUserTabPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _online_user_panel = null;
	private ListBox _lst_users = null;
	private Button _btn_signoff = null;
	
	public OnlineUserTabView()
	{
		super(_i18n.getConstants().admin_tab_OnlineUsers());
									
		init();
		
		super.add(_online_user_panel);
	}
	
	private void init()
	{				
		_online_user_panel = new VerticalPanel();
		_online_user_panel.setSpacing(20);
		
		VerticalPanel heading_panel = new VerticalPanel();
		
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_online_heading(), false);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		
		heading_panel.add(lbl_first_heading);
				
		VerticalPanel content_panel = new VerticalPanel();
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_online_label(), false);
		lbl_second_heading.addStyleName("labelTextBold");
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("paddedBottom");
		
		content_panel.add(lbl_second_heading);
		
		Label lbl_third_heading = new Label(_i18n.getConstants().admin_online_info(), false);
		lbl_third_heading.addStyleName("plainLabelText");
		lbl_third_heading.addStyleName("tabHeading");
		
		content_panel.add(lbl_third_heading);
				
		_lst_users = new ListBox(false);
		_lst_users.setVisibleItemCount(10);
		_lst_users.setWidth("250px");
		_lst_users.addStyleName("silverBordered");
		
		content_panel.add(_lst_users);
		
		_btn_signoff = new Button(_i18n.getConstants().signOn_signOff());		
		_btn_signoff.addStyleName("adminButton");
		_btn_signoff.setEnabled(false);
		
		content_panel.add(_btn_signoff);
		
		_online_user_panel.add(heading_panel);
		_online_user_panel.add(content_panel);
	}

	@Override
	public ListBox getUsersListBox() 
	{	
		return _lst_users;
	}

	@Override
	public Button getSignOffButton() 
	{
		return _btn_signoff;
	}

	@Override
	public Label getHeadingLabel() 
	{
		return super.getTabHeading();
	}
}
