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

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.AdminTabPresenter;
import tms2.client.service.TermBaseService;
import tms2.client.service.TermBaseServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.Project;
import tms2.shared.TermBase;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * AdminTabPresenter class to manage TermBases and Projects
 * 
 * @author I. Lavangee
 *
 */
public class TermBaseProjectTabPresenter implements AdminTabPresenter 
{
	private static TermBaseServiceAsync _termbase_service = GWT.create(TermBaseService.class);
	
	private Display _display = null;
	
	private static AccessController _access_controller = AccessController.getInstance();
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private TermBase _termbase = null;
	private Project _project = null;
	
	public interface Display
	{
		public VerticalPanel getProjectPanel();
		public ExtendedListBox<TermBase> getTermBaseListBox();
		public Button getNewTermBaseButton();
		public TextBox getTermBaseNameTextBox();
		public TextBox getEmailTextBox();
		public Label getTermBaseDateCreatedLabel();
		public Label getTermBaseDateUpdatedLabel();
		public Label getTermBaseOwnerLabel();
		public Button getTermBaseSaveButton();
		public ExtendedListBox<Project> getProjectListBox();
		public Button getNewProjectButton();
		public TextBox getProjectNameTextBox();
		public Label getProjectDateCreatedLabel();
		public Label getProjectDateUpdatedLabel();
		public Button getProjectSaveButton();		
		public Label getHeadingLabel();
		public Widget asWidget();
	}

	public TermBaseProjectTabPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{;
		addTermBaseListBoxHandler();
		addProjectListBoxHandler();
		addNewTermBaseHandler();
		addNewProjectHandler();
		addSaveTermBaseHandlder();
		addSaveProjectHandler();
	}
	
	private void retrieveTermBases()
	{
		resetTermBasePanel();
		
		_termbase = null;
		
		if (! _access_controller.isGuest())
		{
			_termbase_service.getAllDatabases(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<TermBase>>(null) 
			{
				@Override
				public void onComplete(ArrayList<TermBase> result) 
				{
					ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
					lst_termbase.clear();
					
					lst_termbase.addItem(_i18n.getConstants().recordEdit_selectDB(), "-1", null);
					
					if (result == null || result.size() == 0)
						lst_termbase.setEnabled(false);
					else
					{
						Iterator<TermBase> iter = result.iterator();
						while (iter.hasNext())
						{
							TermBase termbase = iter.next();
							lst_termbase.addItem(termbase.getTermdbname(), termbase.getTermdbid(), termbase);
						}
						
						lst_termbase.setEnabled(true);
						lst_termbase.setSelectedIndex(0);
					}
				}
	
				@Override
				public void onError(Throwable caught)
				{
					ErrorBox.ErrorHandler.handle(caught);
					
					_termbase = null;
					_project = null;			
				}
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	private void addTermBaseListBoxHandler()
	{
		_display.getTermBaseListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_termbase = _display.getTermBaseListBox().getSelectedItem();
				populateTermBasePanel();
			}
		});
	}
	
	private void addProjectListBoxHandler()
	{
		_display.getProjectListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_project = _display.getProjectListBox().getSelectedItem();
				populateProjectPanel();
			}
		});
	}
	
	private void addNewTermBaseHandler()
	{
		_display.getNewTermBaseButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetTermBasePanel();
				
				_termbase = null;
				_project = null;
				
				_display.getTermBaseListBox().setSelectedIndex(0);
			}
		});
	}
	
	private void addNewProjectHandler()
	{
		_display.getNewProjectButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetProjectPanel();
				
				_project = null;
				
				VerticalPanel project_panel = _display.getProjectPanel();
				project_panel.setVisible(true);
			}
		});
	}
	
	private void addSaveTermBaseHandlder()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getTermBaseSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateTermBase())
				{
					if (_termbase == null)
					{
						_termbase = new TermBase();
						
						fillTermBaseForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(TermBaseProjectTabPresenter.this, AdminInterfaceEvent.UPDATE_TERMBASE));
					}
					else
					{
						fillTermBaseForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(TermBaseProjectTabPresenter.this, AdminInterfaceEvent.UPDATE_TERMBASE));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_termbase_validate());
			}
		});
	}
	
	private void addSaveProjectHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getProjectSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateProject())
				{
					if (_project == null)
					{
						_project = new Project();
						
						fillProjectForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(TermBaseProjectTabPresenter.this, AdminInterfaceEvent.UPDATE_PROJECT));
					}
					else
					{
						fillProjectForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(TermBaseProjectTabPresenter.this, AdminInterfaceEvent.UPDATE_PROJECT));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_project_validate());
			}
		});
	}
	
	private void populateTermBasePanel()
	{
		resetTermBasePanel();
		
		if (_termbase != null)
		{
			TextBox txt_termbase_name = _display.getTermBaseNameTextBox();
			TextBox txt_email = _display.getEmailTextBox();
			Label lbl_date_created = _display.getTermBaseDateCreatedLabel();
			Label lbl_date_updated = _display.getTermBaseDateUpdatedLabel();
			Label lbl_owner = _display.getTermBaseOwnerLabel();
			DateTimeFormat date_formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
			
			txt_termbase_name.setText(_termbase.getTermdbname());
			txt_email.setText(_termbase.getEmail());
			
			if (_termbase.getDatetimecreated() != null)
				lbl_date_created.setText(date_formatter.format(_termbase.getDatetimecreated()));
			else
				lbl_date_created.setText("");
			
			if (_termbase.getDatetimelastupdated() != null)
				lbl_date_updated.setText(date_formatter.format(_termbase.getDatetimelastupdated()));
			else
				lbl_date_updated.setText("");
				
			lbl_owner.setText(_termbase.getOwnername());
			
			populateProjectListBox();
		}
	}
	
	private void populateProjectListBox()
	{
		VerticalPanel project_panel = _display.getProjectPanel();
		project_panel.setVisible(true);
		
		ExtendedListBox<Project> lst_project = _display.getProjectListBox();
		lst_project.clear();
		
		lst_project.addItem(_i18n.getConstants().recordEdit_selectProject(), "-1", null);
		
		ArrayList<Project> projects = _termbase.getProjects();
		
		if (projects == null || projects.size() == 0)
			lst_project.setEnabled(false);
		else
		{
			Iterator<Project> iter = projects.iterator();
			while (iter.hasNext())
			{
				Project project = iter.next();
				lst_project.addItem(project.getProjectName(), project.getProjectId(), project);
			}
			
			lst_project.setEnabled(true);
		}
	}
	
	private void populateProjectPanel()
	{
		if (_project != null)
		{
			TextBox txt_project_name = _display.getProjectNameTextBox();
			Label lbl_date_created = _display.getProjectDateCreatedLabel();
			Label lbl_date_updated = _display.getProjectDateUpdatedLabel();
			DateTimeFormat date_formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
			
			txt_project_name.setText(_project.getProjectName());
			
			if (_project.getDatetimecreated() != null)
				lbl_date_created.setText(date_formatter.format(_project.getDatetimecreated()));
			else
				lbl_date_created.setText("");
			
			if (_project.getDatetimelastupdated() != null)
				lbl_date_updated.setText(date_formatter.format(_project.getDatetimelastupdated()));
			else
				lbl_date_updated.setText("");
		}
	}
		
	private void resetTermBasePanel()
	{		
		TextBox txt_termbase_name = _display.getTermBaseNameTextBox();
		TextBox txt_email = _display.getEmailTextBox();
		Label lbl_date_created = _display.getTermBaseDateCreatedLabel();
		Label lbl_date_updated = _display.getTermBaseDateUpdatedLabel();
		Label lbl_owner = _display.getTermBaseOwnerLabel();
		
		txt_termbase_name.setText("");
		txt_email.setText("");
		lbl_date_created.setText("");
		lbl_date_updated.setText("");
		lbl_owner.setText("");
						
		resetProjectPanel();
		
		_project = null;
	}
	
	private void resetProjectPanel()
	{		
		TextBox txt_project_name = _display.getProjectNameTextBox();
		Label lbl_date_created = _display.getProjectDateCreatedLabel();
		Label lbl_date_updated = _display.getProjectDateUpdatedLabel();
		
		txt_project_name.setText("");
		lbl_date_created.setText("");
		lbl_date_updated.setText("");
		
		VerticalPanel project_panel = _display.getProjectPanel();
		project_panel.setVisible(false);
	}
	
	private boolean validateTermBase()
	{
		TextBox txt_termbase_name = _display.getTermBaseNameTextBox();
		TextBox txt_email = _display.getEmailTextBox();
		
		if (txt_termbase_name.getText().isEmpty())
			return false;
		
		if (txt_email.getText().isEmpty())
			return false;
		
		return true;
	}
	
	private boolean validateProject()
	{
		TextBox txt_project_name = _display.getProjectNameTextBox();
		
		if (txt_project_name.getText().isEmpty())
			return false;
		
		return true;
	}
	
	private void fillTermBaseForEvent()
	{
		_termbase.setTermdbname(_display.getTermBaseNameTextBox().getText());
		_termbase.setEmail(_display.getEmailTextBox().getText());
		_termbase.setOwneruserid(_access_controller.getUser().getUserId());
	}
	
	private void fillProjectForEvent()
	{
		_project.setProjectName(_display.getProjectNameTextBox().getText());
		_project.setTermBaseId(_termbase.getTermdbid());
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
		retrieveTermBases();		
	}
		
	public Display getDisplay()
	{
		return _display;
	}
	
	public TermBase getTermBase()
	{
		return _termbase;
	}
	
	public void setTermBase(TermBase termbase)
	{
		_termbase = termbase;
	}
	
	public Project getProject()
	{
		return _project;
	}
	
	public void setProject(Project project)
	{
		_project = project;
	}
}
