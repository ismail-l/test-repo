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

import tms2.client.accesscontrol.AccessController;
import tms2.client.admininterface.presenter.TermBaseProjectTabPresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.widgets.AdminTab;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.AppProperties;
import tms2.shared.Project;
import tms2.shared.TermBase;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermBaseProjectTabView extends AdminTab implements TermBaseProjectTabPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _termbase_projects_panel = null;
	private VerticalPanel _termbase_panel = null;
	private VerticalPanel _project_panel = null;
		
	private ExtendedListBox<TermBase> _lst_termbase = null;
	private Button _btn_newtermbase = null;
	private TextBox _txt_termbase_name = null;
	private TextBox _txt_email = null;
	private Label _lbl_termbase_date_created = null;
	private Label _lbl_termbase_date_update = null;
	private Label _lbl_termbase_owner = null;
	private Button _btn_termbase_save = null;
	
	private ExtendedListBox<Project> _lst_project = null;
	private Button _btn_newproject = null;
	private TextBox _txt_projectname = null;
	private Label _lbl_project_date_created = null;
	private Label _lbl_project_date_updated = null;
	private Button _btn_project_save = null;
	
	public TermBaseProjectTabView() 
	{
		super(_i18n.getConstants().admin_tab_Termbases());
		
		_termbase_projects_panel = new VerticalPanel();
		_termbase_projects_panel.setSpacing(20);
		
		buildTermBasePanel();
		buildProjectPanel();
		
		super.add(_termbase_projects_panel);
	}
	
	private void buildTermBasePanel()
	{
		_termbase_panel = new VerticalPanel();
				
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_termbase_info(), false);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_termbase_panel.add(lbl_first_heading);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_termbase_panel.add(lbl_dummy);
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_termbase_termbases(), false);
		lbl_second_heading.addStyleName("labelTextBold");
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("paddedBottom");
		
		_termbase_panel.add(lbl_second_heading);
				
		Label lbl_third_heading = new Label(_i18n.getConstants().admin_termbase_termbaseInfo(), true);
		lbl_third_heading.addStyleName("plainLabelText");
		lbl_third_heading.addStyleName("tabHeading");
		
		_termbase_panel.add(lbl_third_heading);
		
		HorizontalPanel select_termbase_panel = new HorizontalPanel();
		select_termbase_panel.setHeight("50px");
		select_termbase_panel.setSpacing(5);
		select_termbase_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		select_termbase_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		_lst_termbase = new ExtendedListBox<TermBase>(false);
		
		_btn_newtermbase = new Button(_i18n.getConstants().admin_termbase_newTermbase());		
		_btn_newtermbase.addStyleName("adminButton");
		
		select_termbase_panel.add(_lst_termbase);
		select_termbase_panel.add(_btn_newtermbase);
		
		_termbase_panel.add(select_termbase_panel);
		
		FlexTable termbase_details_panel = new FlexTable();
		termbase_details_panel.setCellSpacing(10);
						
		Label lbl_termbase_name = new Label(_i18n.getConstants().admin_termbase_termbaseName(), false);
		lbl_termbase_name.addStyleName("labelTextBold");
		lbl_termbase_name.addStyleName("plainLabelText");
		
		_txt_termbase_name = new TextBox();
		_txt_termbase_name.setWidth("200px");
		
		termbase_details_panel.setWidget(0, 0, lbl_termbase_name);
		termbase_details_panel.setWidget(0, 1, _txt_termbase_name);
		
		Label lbl_email = new Label(_i18n.getConstants().admin_termbase_email(), false);
		lbl_email.addStyleName("labelTextBold");
		lbl_email.addStyleName("plainLabelText");
		
		_txt_email = new TextBox();
		_txt_email.setWidth("200px");
		
		termbase_details_panel.setWidget(1, 0, lbl_email);
		termbase_details_panel.setWidget(1, 1, _txt_email);
		
		Label lbl_date_created = new Label(_i18n.getConstants().admin_termbase_dateTimeCreated(), false);
		lbl_date_created.addStyleName("labelTextBold");
		lbl_date_created.addStyleName("plainLabelText");
		
		_lbl_termbase_date_created = new Label();
		_lbl_termbase_date_created.addStyleName("plainLabelText");
		
		termbase_details_panel.setWidget(2, 0, lbl_date_created);
		termbase_details_panel.setWidget(2, 1, _lbl_termbase_date_created);
		
		Label lbl_date_update = new Label(_i18n.getConstants().admin_termbase_lastUpdated(), false);
		lbl_date_update.addStyleName("labelTextBold");
		lbl_date_update.addStyleName("plainLabelText");
		
		_lbl_termbase_date_update = new Label();
		_lbl_termbase_date_update.addStyleName("plainLabelText");
		
		termbase_details_panel.setWidget(3, 0, lbl_date_update);
		termbase_details_panel.setWidget(3, 1, _lbl_termbase_date_update);
		
		Label lbl_owner = new Label(_i18n.getConstants().admin_termbase_owner(), false);
		lbl_owner.addStyleName("labelTextBold");
		lbl_owner.addStyleName("plainLabelText");
		
		_lbl_termbase_owner = new Label();
		_lbl_termbase_owner.addStyleName("plainLabelText");
		
		termbase_details_panel.setWidget(4, 0, lbl_owner);
		termbase_details_panel.setWidget(4, 1, _lbl_termbase_owner);
		
		_btn_termbase_save = new Button(_i18n.getConstants().controls_save());
		_btn_termbase_save.addStyleName("adminButton");
		
		termbase_details_panel.setWidget(5, 0, new Label());
		termbase_details_panel.setWidget(5, 1, _btn_termbase_save);
		
		_termbase_panel.add(termbase_details_panel);
		
		_termbase_projects_panel.add(_termbase_panel);		
	}
	
	private void buildProjectPanel()
	{
		_project_panel = new VerticalPanel();
		_project_panel.setStyleName("borderedBlock");
		
		
		Label lbl_first_heading = new Label("Projects", false);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_project_panel.add(lbl_first_heading);
				
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_termbase_projectInfo(), true);
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("paddedBottom");
		lbl_second_heading.addStyleName("tabHeading");
		
		_project_panel.add(lbl_second_heading);
		
		HorizontalPanel select_project_panel = new HorizontalPanel();		
		select_project_panel.setHeight("50px");
		select_project_panel.setSpacing(5);
		select_project_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		select_project_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		_lst_project = new ExtendedListBox<Project>(false);
		_lst_project.setEnabled(false);
		
		_btn_newproject = new Button(_i18n.getConstants().admin_termbase_newProject());
		_btn_newproject.setWidth("105px");
		_btn_newproject.addStyleName("adminButton");
				
		select_project_panel.add(_lst_project);
		select_project_panel.add(_btn_newproject);
		
		_project_panel.add(select_project_panel);
		
		FlexTable project_details_panel = new FlexTable();
		project_details_panel.setCellSpacing(10);		
		
		Label lbl_project_name = new Label(_i18n.getConstants().admin_termbase_projectName(), false);
		lbl_project_name.addStyleName("labelTextBold");
		lbl_project_name.addStyleName("plainLabelText");
		
		_txt_projectname = new TextBox();
		_txt_projectname.setWidth("400px");
		
		AppProperties props = AccessController.getInstance().getAppProperties();
		_txt_projectname.setMaxLength(props.getProjectFieldMaxLength());
		
		project_details_panel.setWidget(0, 0, lbl_project_name);
		project_details_panel.setWidget(0, 1, _txt_projectname);
		
		Label lbl_date_created = new Label(_i18n.getConstants().admin_termbase_dateTimeCreated(), false);
		lbl_date_created.addStyleName("labelTextBold");
		lbl_date_created.addStyleName("plainLabelText");
		
		_lbl_project_date_created = new Label();
		_lbl_project_date_created.addStyleName("plainLabelText");
		
		project_details_panel.setWidget(1, 0, lbl_date_created);
		project_details_panel.setWidget(1, 1, _lbl_project_date_created);
		
		Label lbl_date_update = new Label(_i18n.getConstants().admin_termbase_lastUpdated(), false);
		lbl_date_update.addStyleName("labelTextBold");
		lbl_date_update.addStyleName("plainLabelText");
		
		_lbl_project_date_updated = new Label();
		_lbl_project_date_updated.addStyleName("plainLabelText");
		
		project_details_panel.setWidget(2, 0, lbl_date_update);
		project_details_panel.setWidget(2, 1, _lbl_project_date_updated);
		
		_btn_project_save = new Button(_i18n.getConstants().controls_save());	
		_btn_project_save.addStyleName("adminButton");
		
		project_details_panel.setWidget(3, 0, new Label());
		project_details_panel.setWidget(3, 1, _btn_project_save);
		
		_project_panel.add(project_details_panel);
		
		_termbase_projects_panel.add(_project_panel);
	}
	
	@Override
	public Label getHeadingLabel() 
	{
		return super.getTabHeading();
	}

	@Override
	public VerticalPanel getProjectPanel() 
	{
		return _project_panel;
	}

	@Override
	public ExtendedListBox<TermBase> getTermBaseListBox() 
	{
		return _lst_termbase;
	}

	@Override
	public Button getNewTermBaseButton() 
	{
		return _btn_newtermbase;
	}

	@Override
	public TextBox getTermBaseNameTextBox() 
	{
		return _txt_termbase_name;
	}

	@Override
	public TextBox getEmailTextBox() 
	{
		return _txt_email;
	}

	@Override
	public Label getTermBaseDateCreatedLabel() 
	{
		return _lbl_termbase_date_created;
	}

	@Override
	public Label getTermBaseDateUpdatedLabel() 
	{
		return _lbl_termbase_date_update;
	}

	@Override
	public Label getTermBaseOwnerLabel()
	{
		return _lbl_termbase_owner;
	}

	@Override
	public Button getTermBaseSaveButton() 
	{
		return _btn_termbase_save;
	}

	@Override
	public ExtendedListBox<Project> getProjectListBox() 
	{
		return _lst_project;
	}

	@Override
	public Button getNewProjectButton() 
	{
		return _btn_newproject;
	}

	@Override
	public TextBox getProjectNameTextBox() 
	{
		return _txt_projectname;
	}

	@Override
	public Label getProjectDateCreatedLabel() 
	{
		return _lbl_project_date_created;
	}

	@Override
	public Label getProjectDateUpdatedLabel() 
	{
		return _lbl_project_date_updated;
	}

	@Override
	public Button getProjectSaveButton() 
	{
		return _btn_project_save;
	}
	
}
