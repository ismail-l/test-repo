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

package tms2.client.termbrowser.view;

import java.util.ArrayList;
import java.util.List;

import tms2.client.i18n.Internationalization;
import tms2.client.termbrowser.presenter.FilterPresenter;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.CreatedOrChangedCheckboxesPanel;
import tms2.client.widgets.ExtendedDatePicker;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.FieldFilterTree;
import tms2.shared.FilterableObject;
import tms2.shared.Project;
import tms2.shared.ProjectFilter;
import tms2.shared.TermBase;
import tms2.shared.User;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FilterView extends Composite implements FilterPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private AttachedPanel _filter_panel = null;
	private VerticalPanel _filter_details_panel = null;
	private DisclosurePanel _project_panel = null;
	private DisclosurePanel _user_panel = null;
	private DisclosurePanel _field_panel = null;
	private ExtendedListBox<TermBase> _lst_termbase = null;	
	private ExtendedListBox<Project> _lst_project = null;
	private DataGrid<ProjectFilter> _dg_project = null;
	private ListDataProvider<ProjectFilter> _list_project = null;
	private Column<ProjectFilter, String> _col_event = null;
	private Column<ProjectFilter, String> _col_remove_project = null;
	private ExtendedListBox<User> _lst_user = null;
	private CreatedOrChangedCheckboxesPanel _user_created_changed = null;
	private ExtendedDatePicker _dp_from_date_picker = null;
	private ExtendedDatePicker _dp_to_date_picker = null;
	private FieldFilterTree _field_filter_tree = null;
	private Button _btn_setfilter = null;
	private Button _btn_clear = null;
	private Button _btn_close = null;
	
	public FilterView(Button btn_parent)
	{
		_filter_panel = new AttachedPanel(btn_parent);
		_filter_panel.setWidth("780px");
		
		initWidget(_filter_panel);
		
		_filter_details_panel = new VerticalPanel();
		_filter_details_panel.setSpacing(5);
							
		ScrollPanel scroller = new ScrollPanel();
		scroller.add(_filter_details_panel);
		scroller.setWidth("780px");
		//scroller.setHeight((Window.getClientHeight() - 60) + "px");
				
		_filter_panel.add(scroller);
		
		buildTermBasePanel();
		buildProjectPanel();
		buildUserPanel();
		buildFieldPanel();
		buildActionPanel();
	}

	private void buildTermBasePanel()
	{
		VerticalPanel termbase_panel = new VerticalPanel();
		termbase_panel.setSpacing(5);
		
		HorizontalPanel termbase_details_panel = new HorizontalPanel();
		termbase_details_panel.setSpacing(5);
		
		Label lbl_termbase = new Label(_i18n.getConstants().filter_selectTermbase());
		lbl_termbase.addStyleName("labelTextBold");
		lbl_termbase.addStyleName("plainLabelText");
		
		_lst_termbase = new ExtendedListBox<TermBase>(false);
		
		termbase_details_panel.add(lbl_termbase);
		termbase_details_panel.add(_lst_termbase);
		
		termbase_panel.add(termbase_details_panel);
		
		_filter_details_panel.add(termbase_panel);		
	}
	
	private void buildProjectPanel()
	{
		_project_panel = new DisclosurePanel(_i18n.getConstants().filter_project_filters());
		_project_panel.setWidth("750px");
		
		VerticalPanel project_details_panel = new VerticalPanel();
		project_details_panel.setSpacing(5);
		
		HorizontalPanel project_action_panel = new HorizontalPanel();
		project_action_panel.setSpacing(5);
		
		Label lbl_project = new Label("Projects:");
		lbl_project.addStyleName("labelTextBold");
		lbl_project.addStyleName("plainLabelText");
		
		_lst_project = new ExtendedListBox<Project>(false);
				
		project_action_panel.add(lbl_project);
		project_action_panel.add(_lst_project);
		
		project_details_panel.add(project_action_panel);
		
		SimpleLayoutPanel project_filter_panel = new SimpleLayoutPanel();
		project_filter_panel.setStyleName("borderedBlock");
		project_filter_panel.setWidth("680px");	
		project_filter_panel.setHeight("200px");
		
		_dg_project = new DataGrid<ProjectFilter>();
		_dg_project.setWidth("680px");
		_dg_project.setHeight("200px");
		
		_list_project = new ListDataProvider<ProjectFilter>();
		_list_project.addDataDisplay(_dg_project);
				
		Column<ProjectFilter, String> col_projectname = new Column<ProjectFilter, String>(new TextCell()) 
		{			
			@Override
			public String getValue(ProjectFilter object) 
			{			
				return object.getProjectName();
			}
		};
		
		_dg_project.setColumnWidth(col_projectname, 300, Unit.PX);
		
		List<String> event_types = new ArrayList<String>();
		
		event_types.add(FilterableObject.Event.CONTAINS.getEventTypeName());
		event_types.add(FilterableObject.Event.NOT_CONTAINS.getEventTypeName());
		event_types.add(FilterableObject.Event.EXCLUSIVE.getEventTypeName());
		
		SelectionCell filter_event_cell = new SelectionCell(event_types);
		
		_col_event = new Column<ProjectFilter, String>(filter_event_cell) 
	    {
	        @Override
	        public String getValue(ProjectFilter object) 
	        {
	        	FilterableObject.Event event = null;
	        	
	        	if (object.isContains())
	        		event = FilterableObject.Event.CONTAINS;
	        	else if (object.isNotContains())
	        		event = FilterableObject.Event.NOT_CONTAINS;
	        	else if (object.isExclusive())
	        		event = FilterableObject.Event.EXCLUSIVE;
	        	
	        	if (event == null)
	        		event = FilterableObject.Event.CONTAINS;
	        	
	        	return event.getEventTypeName();
	        }
	    };
			
	    _dg_project.setColumnWidth(_col_event, 130, Unit.PX);
	    
	    List<String> and_or = new ArrayList<String>();
		
	    and_or.add(_i18n.getConstants().filter_and());
	    and_or.add(_i18n.getConstants().filter_or());
	    
		event_types.add(FilterableObject.Event.EXCLUSIVE.getEventTypeName());
		
		SelectionCell and_or_cell = new SelectionCell(and_or);
		
	    Column<ProjectFilter, String> col_and_or = new Column<ProjectFilter, String>(and_or_cell) 
	    {
	        @Override
	        public String getValue(ProjectFilter object) 
	        {
	        	String and_or = null;
	        	
	        	if (object.isAnd())
	        		and_or = _i18n.getConstants().filter_and();
	        	else 
	        		and_or = _i18n.getConstants().filter_or();
	        	
	        	return and_or;
	        }
	    };
		
	    col_and_or.setFieldUpdater(new FieldUpdater<ProjectFilter, String>()
	    {
			@Override
			public void update(int index, ProjectFilter object, String value) 
			{
				if (value.equals(_i18n.getConstants().filter_and()))
					object.setIsAnd(true);
				else
					object.setIsAnd(false);
			}
		});
	    	
	    _dg_project.setColumnWidth(col_and_or, 70, Unit.PX);
	    
	    ButtonCell btn_del = new ButtonCell();
	    _col_remove_project = new Column<ProjectFilter, String>(btn_del) 
	    {			
			@Override
			public String getValue(ProjectFilter object) 
			{			
				return _i18n.getConstants().filter_remove();
			}
		};
	    
		_dg_project.setColumnWidth(_col_remove_project, 100, Unit.PX);
		
	    _dg_project.addColumn(col_projectname,"");		
	    _dg_project.addColumn(_col_event, "");
	    _dg_project.addColumn(col_and_or, "");
	    _dg_project.addColumn(_col_remove_project, "");
	    
	    project_filter_panel.add(_dg_project);
	    
	    project_details_panel.add(project_filter_panel);			    
			
	    _project_panel.add(project_details_panel);
	    
	    _filter_details_panel.add(_project_panel);
	}
	
	private void buildUserPanel()
	{
		_user_panel = new DisclosurePanel(_i18n.getConstants().filter_user_date());
		_user_panel.setWidth("750px");
		
		VerticalPanel user_panel = new VerticalPanel();
		user_panel.setWidth("100%");
		user_panel.setHeight("150px");
		user_panel.setSpacing(5);
		user_panel.setStyleName("borderedBlock");
						
		Label filterHeading = new Label(_i18n.getConstants().filter_selectUserDates());
		filterHeading.addStyleName("labelTextBold");
		filterHeading.addStyleName("plainLabelText");
		user_panel.add(filterHeading);
		
		FlexTable user_details_panel = new FlexTable();
				
		_lst_user = new ExtendedListBox<User>(false);
		
		user_details_panel.setWidget(0, 0, _lst_user);
		
		_user_created_changed = new CreatedOrChangedCheckboxesPanel();
				
		user_details_panel.setWidget(0, 1, _user_created_changed);
		
		_dp_from_date_picker = new ExtendedDatePicker(_i18n.getConstants().filter_fromDate());
			
		user_details_panel.setWidget(1, 0, _dp_from_date_picker);
		
		_dp_to_date_picker = new ExtendedDatePicker(_i18n.getConstants().filter_toDate());
			
		user_details_panel.setWidget(1, 1, _dp_to_date_picker);
				
		user_panel.add(user_details_panel);
		
		_user_panel.add(user_panel);
		
		_filter_details_panel.add(_user_panel);
	}
	
	private void buildFieldPanel()
	{
		_field_panel = new DisclosurePanel(_i18n.getConstants().filter_fields());
		_field_panel.setWidth("750px");
		
		VerticalPanel field_panel = new VerticalPanel();
		field_panel.setWidth("100%");
		field_panel.setSpacing(5);
		field_panel.setStyleName("borderedBlock");
				
		Label lbl_prompt = new Label(_i18n.getConstants().filter_fields_prompt());
		lbl_prompt.addStyleName("labelTextBold");
		lbl_prompt.addStyleName("plainLabelText");
		
		field_panel.add(lbl_prompt);
		
		VerticalPanel inputmodel_panel = new VerticalPanel();
		inputmodel_panel.setWidth("100%");
		inputmodel_panel.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
		inputmodel_panel.getElement().getStyle().setBorderWidth(1, Unit.PX);
		inputmodel_panel.getElement().getStyle().setBorderColor("#531a17");
		
		ScrollPanel scroller = new ScrollPanel();
		
		_field_filter_tree = new FieldFilterTree(true);
		_field_filter_tree.setWidth("100%");
		
		scroller.add(_field_filter_tree);
		scroller.setHeight("300px");
		
		inputmodel_panel.add(scroller);
								
		field_panel.add(inputmodel_panel);
		
		_field_panel.add(field_panel);
		
		_filter_details_panel.add(_field_panel);
	}
	
	private void buildActionPanel()
	{
		HorizontalPanel action_panel = new HorizontalPanel();
		action_panel.setWidth("780px");
		action_panel.setStyleName("borderedBlock");
		action_panel.setSpacing(5);
		
		HorizontalPanel action_panel_details = new HorizontalPanel();
		action_panel_details.setSpacing(5);		
		
		_btn_setfilter = new Button(_i18n.getConstants().filter_setFilter());
		_btn_clear = new Button(_i18n.getConstants().filter_clear());
		_btn_close = new Button(_i18n.getConstants().controls_close());
		
		action_panel_details.add(_btn_setfilter);
		action_panel_details.add(_btn_clear);
		action_panel_details.add(_btn_close);
		
		action_panel.add(action_panel_details);
		
		_filter_panel.add(action_panel);
	}	
		
	@Override
	public AttachedPanel getFilterPanel() 
	{
		return _filter_panel;
	}

	@Override
	public ExtendedListBox<TermBase> getTermBaseListBox() 
	{	
		return _lst_termbase;
	}

	@Override
	public DataGrid<ProjectFilter> getProjectDataGrid() 
	{	
		return _dg_project;
	}

	@Override
	public ListDataProvider<ProjectFilter> getProjectDataProvider() 
	{
		return _list_project;
	}

	@Override
	public Button getSetFilterButton() 
	{	
		return _btn_setfilter;
	}

	@Override
	public Button getClearFilterButton() 
	{
		return _btn_clear;
	}

	@Override
	public Button getCloseButton() 
	{
		return _btn_close;
	}

	@Override
	public DisclosurePanel getProjectPanel() 
	{	
		return _project_panel;
	}

	@Override
	public Column<ProjectFilter, String> getProjectEventColumn() 
	{		
		return _col_event;
	}

	@Override
	public ExtendedListBox<Project> getProjectListBox() 
	{	
		return _lst_project;
	}

	@Override
	public Column<ProjectFilter, String> getRemoveProjectButton() 
	{	
		return _col_remove_project;
	}

	@Override
	public DisclosurePanel getUserPanel() 
	{	
		return _user_panel;
	}

	@Override
	public ExtendedListBox<User> getUserListBox() 
	{
		return _lst_user;
	}

	@Override
	public CreatedOrChangedCheckboxesPanel getUserCreatedOrChangedPanel() 
	{	
		return _user_created_changed;
	}

	@Override
	public ExtendedDatePicker getFromDatePicker() 
	{	
		return _dp_from_date_picker;
	}

	@Override
	public ExtendedDatePicker getToDatePicker() 
	{	
		return _dp_to_date_picker;
	}

	@Override
	public DisclosurePanel getFieldPanel() 
	{
		return _field_panel;
	}

	@Override
	public FieldFilterTree getFieldFilterTree() 
	{	
		return _field_filter_tree;
	}
}
