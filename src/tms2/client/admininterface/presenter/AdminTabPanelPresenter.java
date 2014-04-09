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

import tms2.client.presenter.AdminTabPresenter;
import tms2.client.presenter.Presenter;
import tms2.client.admininterface.presenter.OnlineUserTabPresenter;
import tms2.client.admininterface.view.AccessRightsTabView;
import tms2.client.admininterface.view.FieldTabView;
import tms2.client.admininterface.view.OnlineUserTabView;
import tms2.client.admininterface.view.TermBaseProjectTabView;
import tms2.client.admininterface.view.UserUserCategoryTabView;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presenter class to manage all the {@link AdminInterfacePresenter}s.
 * 
 * @author I. Lavangee
 *
 */
public class AdminTabPanelPresenter implements Presenter
{
	private Display _display = null;
	
	private AdminTabPresenter _ou_presenter = null;
	private AdminTabPresenter _uuc_presenter = null;
	private AdminTabPresenter _tp_presenter = null;
	private AdminTabPresenter _ft_presenter = null;
	private AdminTabPresenter _ar_presenter = null;
	
	public interface Display
	{
		public Widget asWidget();
	}
	
	public AdminTabPanelPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{
		TabLayoutPanel admin_panel = (TabLayoutPanel) _display.asWidget();
		admin_panel.addSelectionHandler(new SelectionHandler<Integer>()
		{			
			@Override
			public void onSelection(SelectionEvent<Integer> event) 
			{
				switch (event.getSelectedItem())
				{
					case 0:
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.startTimer();
						_ou_presenter.loadAdminTabData();
						break;
					}
					case 1:
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.stopTimer();
						_uuc_presenter.loadAdminTabData();
						break;
					}
					case 2:
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.stopTimer();
						_tp_presenter.loadAdminTabData();
						break;
					}
					case 3:
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.stopTimer();
						_ft_presenter.loadAdminTabData();
						break;
					}
					case 4:
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.stopTimer();
						_ar_presenter.loadAdminTabData();
						break;
					}
					default: 
					{
						OnlineUserTabPresenter presenter = (OnlineUserTabPresenter)_ou_presenter;
						presenter.startTimer();
						_ou_presenter.loadAdminTabData();				
					}					
				}									
			}
		});
	}
	
	@Override
	public void go(HasWidgets container) 
	{		
		container.add(_display.asWidget());
		
		_ou_presenter = new OnlineUserTabPresenter(new OnlineUserTabView());			
		_ou_presenter.go((HasWidgets) _display.asWidget());
		
		_uuc_presenter = new UserUserCategoryTabPresenter(new UserUserCategoryTabView());
		_uuc_presenter.go((HasWidgets) _display.asWidget());
		
		_tp_presenter = new TermBaseProjectTabPresenter(new TermBaseProjectTabView());
		_tp_presenter.go((HasWidgets) _display.asWidget());
		
		_ft_presenter = new FieldTabPresenter(new FieldTabView());
		_ft_presenter.go((HasWidgets) _display.asWidget());
		
		_ar_presenter = new AccessRightsTabPresenter(new AccessRightsTabView());
		_ar_presenter.go((HasWidgets) _display.asWidget());
		
		bind();
	}
}
