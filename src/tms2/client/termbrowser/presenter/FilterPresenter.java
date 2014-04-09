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

package tms2.client.termbrowser.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.FilterEvent;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.service.FilterService;
import tms2.client.service.FilterServiceAsync;
import tms2.client.util.AttachedPanelPositionUtility;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.CreatedOrChangedCheckboxesPanel;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedDatePicker;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.FieldFilterTree;
import tms2.client.widgets.FieldFilterTreeItem;
import tms2.client.widgets.HasSignOut;
import tms2.shared.AppProperties;
import tms2.shared.FieldFilter;
import tms2.shared.Filter;
import tms2.shared.FilterableObject;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.ProjectFilter;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.wrapper.FilterDetailsWrapper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FilterPresenter implements Presenter, HasSignOut
{
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static FilterServiceAsync _filter_service = GWT.create(FilterService.class);
	
	private Display _display = null;
			
	private TermBase _termbase = null;
		
	private Filter _filter = null;
	
	private ArrayList<Button> _cb_state_buttons = null;
	
	private boolean _retrieved_details = false;
	
	private FilterDetailsWrapper _filter_details = null;
	
	public interface Display
	{		
		public AttachedPanel getFilterPanel();
		public DisclosurePanel getProjectPanel();
		public ExtendedListBox<TermBase> getTermBaseListBox();
		public ExtendedListBox<Project> getProjectListBox();
		public DataGrid<ProjectFilter> getProjectDataGrid();
		public ListDataProvider<ProjectFilter> getProjectDataProvider();
		public Column<ProjectFilter, String> getProjectEventColumn();
		public Column<ProjectFilter, String> getRemoveProjectButton();
		public DisclosurePanel getUserPanel();
		public ExtendedListBox<User> getUserListBox();
		public CreatedOrChangedCheckboxesPanel getUserCreatedOrChangedPanel();
		public ExtendedDatePicker getFromDatePicker();
		public ExtendedDatePicker getToDatePicker();
		public DisclosurePanel getFieldPanel();
		public FieldFilterTree getFieldFilterTree();
		public Button getSetFilterButton();
		public Button getClearFilterButton();
		public Button getCloseButton();
		
		public Widget asWidget();
	}
	
	private class DatePickerValueChangeHandler implements ValueChangeHandler<Date>
	{
		private ExtendedDatePicker _dp_one = null;
		private ExtendedDatePicker _dp_two = null;
		
		private boolean _is_compare_from = false;
		
		public DatePickerValueChangeHandler(ExtendedDatePicker dp_one, ExtendedDatePicker dp_two, boolean compare_from)
		{
			_dp_one = dp_one;
			_dp_two = dp_two;
			
			_is_compare_from = compare_from;
		}
		
		@Override
		public void onValueChange(ValueChangeEvent<Date> event) 
		{
			if (_dp_two.getValue() != null && _dp_one.getValue() != null)
			{
				if (_is_compare_from)
				{
					if(_dp_two.getValue().compareTo(_dp_one.getValue()) <= 0)					
						_dp_one.setValue(null);
				}
				else
				{
					if(_dp_two.getValue().compareTo(_dp_one.getValue()) >= 0)	
						_dp_one.setValue(null);
				}
			}			
		}		
	}
		
	public FilterPresenter(Display display, ArrayList<Button> cb_state_buttons)
	{
		_display = display;
		
		_cb_state_buttons = cb_state_buttons;
		
		_access_controller.addSignOut(this);
	}
	
	private void bind()
	{
		AttachedPanelPositionUtility.addPosition(_display.getFilterPanel());
		
		addTermBaseListBoxHandler();
		addProjectListBoxHandler();
		addProjectEventColumnHandler();
		addProjectRemoveColumnHandler();
		addFromDateHandler();
		addToDateHandler();
		addFilterFieldTreeHandler();
		addSetFilterButtonHandler();
		addClearFilterButtonHandler();
		addCloseButtonHandler();
	}
	
	public void retrieveFilterDetails()
	{
		resetFilterPanel();

		if (! _access_controller.isGuest())
		{
			_filter_service.getFilterDetails(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<FilterDetailsWrapper>(null)
			{
				@Override
				public void onComplete(FilterDetailsWrapper result) 
				{
					if (result == null)
					{
						AlertBox.show(_i18n.getConstants().log_filter());
						resetFilterPanel();
						_termbase = null;
						_filter = null;
						return;
					}
															
					ArrayList<TermBase> termbases = result.getTermBases();
					if (termbases == null || termbases.size() == 0)
					{
						AlertBox.show(_i18n.getConstants().log_db_retrieve());
						resetFilterPanel();					
						_termbase = null;
						_filter = null;
						return;
					}
					
					_filter_details = result;
					
					populateFilterPanel();
					
					_retrieved_details = true;
				}
	
				@Override
				public void onError(Throwable caught) 
				{
					ErrorBox.ErrorHandler.handle(caught);
					resetFilterPanel();
					_termbase = null;
					_filter = null;
					
					_retrieved_details = false;
				}
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
		
	private void addProjectEventColumnHandler()
	{
		Column<ProjectFilter, String> event_column = _display.getProjectEventColumn();
		event_column.setFieldUpdater(new FieldUpdater<ProjectFilter, String>()
		{			
			@Override
			public void update(int index, ProjectFilter object, String value) 
			{		
				object.setAllProjects(null);
				
	        	if (value.equals(FilterableObject.Event.CONTAINS.getEventTypeName()))
	        		object.setEventType(FilterableObject.Event.CONTAINS.getEventType());
	        	else if (value.equals(FilterableObject.Event.NOT_CONTAINS.getEventTypeName()))
	        		object.setEventType(FilterableObject.Event.NOT_CONTAINS.getEventType());
	        	else 
	        	{
	        		object.setEventType(FilterableObject.Event.EXCLUSIVE.getEventType());
	        		object.setAllProjects(_termbase.getProjects());
	        		
	        		ListDataProvider<ProjectFilter> data_provider = _display.getProjectDataProvider();
	        		List<ProjectFilter> project_filters = data_provider.getList();
	        		
	        		Iterator<ProjectFilter> iter = project_filters.iterator();
	        		while (iter.hasNext())
	        		{
	        			ProjectFilter project_filter = iter.next();
	        			
	        			if (project_filter.getProjectId() != object.getProjectId())
	        				addProjectToListBox(project_filter);
	        		}
	        		
	        		addExclusiveProject(object);
	        		
	        		data_provider.refresh();
	        	}
			}
		});
	}
	
	private void addProjectRemoveColumnHandler()
	{
		Column<ProjectFilter, String> remove_project_column = _display.getRemoveProjectButton();
		remove_project_column.setFieldUpdater(new FieldUpdater<ProjectFilter, String>() 
		{			
			@Override
			public void update(int index, ProjectFilter object, String value) 
			{
				removeProjectFromGrid(object);
				addProjectToListBox(object);
			}
		});
	}
		
	private void addFromDateHandler()
	{
		ExtendedDatePicker dp_from_date = _display.getFromDatePicker();
		ExtendedDatePicker dp_to_date = _display.getToDatePicker();
		
		dp_from_date.addValueChangeHandler(new DatePickerValueChangeHandler(dp_from_date, dp_to_date, true));		
	}
	
	private void addToDateHandler()
	{
		ExtendedDatePicker dp_to_date = _display.getToDatePicker();
		ExtendedDatePicker dp_from_date = _display.getFromDatePicker();
			
		dp_to_date.addValueChangeHandler(new DatePickerValueChangeHandler(dp_to_date, dp_from_date, false));
	}
	
	private void addFilterFieldTreeHandler()
	{
		_display.getFieldFilterTree().addSelectionHandler(new SelectionHandler<TreeItem>()
		{
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) 
			{						
				TreeItem tree_item = event.getSelectedItem();
				if (tree_item != null && tree_item instanceof FieldFilterTreeItem)
				{										
					if (! ((FieldFilterTreeItem)tree_item).isDisabled())
						((FieldFilterTreeItem)tree_item).showControls();
				}
			}
		});
	}
	
	private void addSetFilterButtonHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getSetFilterButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateSettings())
				{
					_filter = generateFilter();
					event_bus.fireEvent(new FilterEvent(_filter));
					
					Iterator<Button> iter = _cb_state_buttons.iterator();
					while (iter.hasNext())
					{
						Button button = iter.next();
						
						if (button.getText().equals(_i18n.getConstants().filter_export()))
						{
							if (isFilterSet())
								button.setEnabled(true);
							else
								button.setEnabled(false);
						}
						else
							button.setEnabled(true);
					}
					
					_display.getFilterPanel().hide();
				}
			}
		});
	}
	
	private void addTermBaseListBoxHandler()
	{
		_display.getTermBaseListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_termbase = _display.getTermBaseListBox().getSelectedItem();
				
				resetFilterPanel();
				
				if (_termbase != null)	
				{
					DisclosurePanel project_panel = _display.getProjectPanel();
					project_panel.setVisible(true);
					
					DisclosurePanel user_panel = _display.getUserPanel();
					user_panel.setVisible(true);
					
					DisclosurePanel field_panel = _display.getFieldPanel();
					field_panel.setVisible(true);
					
					populateProjects(_termbase.getProjects());
				}
			}
		});
	}
	
	private void addProjectListBoxHandler()
	{
		final ExtendedListBox<Project> lst_project = _display.getProjectListBox();
		
		lst_project.addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				Project project = lst_project.getSelectedItem();
				
				if (project != null)
				{
					ListDataProvider<ProjectFilter> data_provider = _display.getProjectDataProvider();
					List<ProjectFilter> project_filters = data_provider.getList();
					
					boolean has_exclusive = false;
					
					Iterator<ProjectFilter> iter = project_filters.iterator();
					while (iter.hasNext())
					{
						ProjectFilter project_filter = iter.next();
						if (project_filter.isExclusive())
						{
							has_exclusive = true;
							break;
						}
					}
					
					if (! has_exclusive)
					{
						removeProjectFromListBox(project);
						addProjectToGrid(project);
					}
					/*else
						AlertBox.show(_i18n.getConstants().filter_project_exclusive());*/
				}
			}
		});
	}
	
	private void addClearFilterButtonHandler()
	{		
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getClearFilterButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_termbase  = null;
				
				_display.getTermBaseListBox().setSelectedIndex(0);
				
				resetFilterPanel();	
				
				if (isFilterSet())
				{
					_filter = null;
					event_bus.fireEvent(new FilterEvent(_filter));
				}
				
				populateFilterPanel();
			}
		});
	}
	
	private void addCloseButtonHandler()
	{
		_display.getCloseButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				Iterator<Button> iter = _cb_state_buttons.iterator();
				while (iter.hasNext())
				{
					Button button = iter.next();
					
					if (button.getText().equals(_i18n.getConstants().filter_export()))
					{
						if (isFilterSet())
							button.setEnabled(true);
						else
							button.setEnabled(false);
					}
					else
						button.setEnabled(true);
				}
				
				_display.getFilterPanel().hide();								
			}
		});
	}
	
	private void populateProjects(ArrayList<Project> projects)
	{
		ExtendedListBox<Project> lst_project = _display.getProjectListBox();
		lst_project.clear();
		
		lst_project.addItem(_i18n.getConstants().recordEdit_selectProject(), "-1", null);
		
		Iterator<Project> iter = projects.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			
			lst_project.addItem(project.getProjectName(), project.getProjectId(), project);
		}
	}
	
	private void addProjectToGrid(Project project)
	{
		ListDataProvider<ProjectFilter> data_provider = _display.getProjectDataProvider();
		List<ProjectFilter> project_filters = data_provider.getList();
		
		Iterator<ProjectFilter> iter = project_filters.iterator();
		while (iter.hasNext())
		{
			ProjectFilter project_filter = iter.next();
			if (project_filter.getProjectId() == project.getProjectId())
				return;			
		}
		
		ProjectFilter project_filter = new ProjectFilter();
		project_filter.setProjectId(project.getProjectId());
		project_filter.setProjectName(project.getProjectName());
		project_filter.setEventType(FilterableObject.Event.CONTAINS.getEventType());
		project_filter.setIsAnd(false);
				
		project_filters.add(project_filter);
				
		data_provider.refresh();
	}
	
	private void addExclusiveProject(ProjectFilter project_filter)
	{
		ListDataProvider<ProjectFilter> data_provider = _display.getProjectDataProvider();
		List<ProjectFilter> project_filters = data_provider.getList();
		
		project_filters.clear();
		
		project_filters.add(project_filter);
	}
	
	private void removeProjectFromGrid(ProjectFilter project)
	{
		ListDataProvider<ProjectFilter> data_provider = _display.getProjectDataProvider();
		List<ProjectFilter> project_filters = data_provider.getList();
		
		Iterator<ProjectFilter> iter = project_filters.iterator();
		while (iter.hasNext())
		{
			ProjectFilter project_filter = iter.next();
			if (project_filter.getProjectId() == project.getProjectId())
			{
				iter.remove();
				break;
			}
		}
						
		data_provider.refresh();
	}
	
	private void addProjectToListBox(ProjectFilter project_filter)
	{
		ArrayList<TermBase> termbases = _display.getTermBaseListBox().getItems();
		Iterator<TermBase> iter = termbases.iterator();
		while (iter.hasNext())
		{
			TermBase termbase = iter.next();
			
			if (termbase == null)
				continue;
			
			ArrayList<Project> projects = termbase.getProjects();
			Iterator<Project> project_iter = projects.iterator();
			while (project_iter.hasNext())
			{
				Project project = project_iter.next();
				if (project.getProjectId() == project_filter.getProjectId())
				{
					_display.getProjectListBox().addItem(project.getProjectName(), project.getProjectId(), project);
					return;
				}
			}
		}
	}
	
	private void removeProjectFromListBox(Project project)
	{
		ExtendedListBox<Project> lst_project = _display.getProjectListBox();
		
		lst_project.removeItem(lst_project.getSelectedIndex());
	}
	
	private void populateFieldFilterTree(InputModel inputmodel)
	{
		FieldFilterTree tree = _display.getFieldFilterTree();
		tree.populateTreeWithInputModel(inputmodel);
		
		AppProperties props = _access_controller.getAppProperties();
		
		TreeItem record_attribute_item = tree.getRecordAttributeTreeItem();
		int record_attributes = record_attribute_item.getChildCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			FieldFilterTreeItem tree_item = (FieldFilterTreeItem) record_attribute_item.getChild(i);
			TerminlogyObject terminology_object = tree_item.getTerminologyObject();
			
			if (terminology_object.isProject(props.getProjectField()))
			{				
				tree_item.setIsDisabled(true, true);
				break;
			}
		}
	}
	
	private Filter generateFilter()
	{
		long termbaseId = _termbase.getTermdbid();
		
		ArrayList<ProjectFilter> projects = new ArrayList<ProjectFilter>();
		
		List<ProjectFilter> project_filters = _display.getProjectDataProvider().getList();
		Iterator<ProjectFilter> iter = project_filters.iterator();
		while (iter.hasNext())
		{
			ProjectFilter project_filter = iter.next();
			projects.add(project_filter);
		}
		
		FieldFilterTree tree = _display.getFieldFilterTree();
		TreeItem record_attribute_tree_item = tree.getRecordAttributeTreeItem();
		
		ArrayList<FieldFilter> record_field_filters = new ArrayList<FieldFilter>();
		int record_attributes = record_attribute_tree_item.getChildCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			FieldFilterTreeItem tree_item = (FieldFilterTreeItem) record_attribute_tree_item.getChild(i);
						
			if (tree_item.isActive() && ! tree_item.isDisabled())
			{
				FieldFilter record_field_filter = tree_item.getFieldFilter();
				record_field_filters.add(record_field_filter);
			}			
		}
		
		TreeItem term_tree_item = tree.getTermsTreeItem();
		int terms = term_tree_item.getChildCount();
		
		ArrayList<FieldFilter> term_field_filters = new ArrayList<FieldFilter>();
		
		for (int i = 0; i < terms; i++)
		{
			FieldFilterTreeItem tree_item = (FieldFilterTreeItem) term_tree_item.getChild(i);
						
			if (tree_item.isActive() && ! tree_item.isDisabled())
			{
				FieldFilter term_field_filter = tree_item.getFieldFilter();
				// Remove sub filters as there could still be sub filters in this
				// list from the previous filter.
				term_field_filter.removeSubFilters();
				term_field_filters.add(term_field_filter);
				
				int term_attributes = tree_item.getChildCount();
				for (int j = 0; j < term_attributes; j++)
				{
					FieldFilterTreeItem term_attr_tree_item = (FieldFilterTreeItem) tree_item.getChild(j);					
					
					if (term_attr_tree_item.isActive() && ! term_attr_tree_item.isDisabled())
					{
						FieldFilter term_attr_field_filter = term_attr_tree_item.getFieldFilter();
						
						// Remove sub filters as there could still be sub filters in this
						// list from the previous filter.
						term_attr_field_filter.removeSubFilters();
						term_field_filter.addSubFilter(term_attr_field_filter);
						
						if (term_attr_field_filter.isSynonymField())
						{
							int synonym_attributes = term_attr_tree_item.getChildCount();
							for (int k = 0; k < synonym_attributes; k++)
							{
								FieldFilterTreeItem synonym_attr_tree_item = (FieldFilterTreeItem) term_attr_tree_item.getChild(k);								
								
								if (synonym_attr_tree_item.isActive() && ! synonym_attr_tree_item.isDisabled())
								{
									FieldFilter synonym_attr_field_filter = synonym_attr_tree_item.getFieldFilter();
									// Remove sub filters as there could still be sub filters in this
									// list from the previous filter.
									synonym_attr_field_filter.removeSubFilters();
									term_attr_field_filter.addSubFilter(synonym_attr_field_filter);
								}
							}
						}
					}
				}
			}
		}
		
		long userid = -1;
		User user = _display.getUserListBox().getSelectedItem();
		
		if (user != null)
			userid = user.getUserId();
		
		int user_role = _display.getUserCreatedOrChangedPanel().getValue();
		
		String from_date = null;
		Date dp_from_date = _display.getFromDatePicker().getValue();
		
		if (dp_from_date != null)
			from_date = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(dp_from_date);
		
		String to_date = null;
		Date to_from_date = _display.getToDatePicker().getValue();
		
		if (to_from_date != null)
			to_date = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(to_from_date);
		
		Filter filter = new Filter(termbaseId, projects, userid, user_role, from_date,to_date, 
							       record_field_filters, term_field_filters);
		
		return filter;
	}
	
	private boolean validateSettings()
	{
		if (_termbase != null)
			return true;
				
		return false;
	}
	
	public void show()
	{
		_display.getFilterPanel().position();
		_display.getFilterPanel().show();
	}
		
	public boolean hasRetrievedDetails()
	{
		return  _retrieved_details;
	}
	
	private void populateFilterPanel()
	{
		ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
		lst_termbase.clear();
		
		lst_termbase.addItem(_i18n.getConstants().recordEdit_selectDB(), "-1", null);
		
		Iterator<TermBase> iter = _filter_details.getTermBases().iterator();
		while (iter.hasNext())
		{
			TermBase termbase = iter.next();
			
			lst_termbase.addItem(termbase.getTermdbname(), termbase.getTermdbid(), termbase);
		}
		
		ArrayList<User> users = _filter_details.getUsers();
		
		if (users == null || users.size() == 0)
		{
			AlertBox.show(_i18n.getMessages().server_user_retrieve(""));
			resetFilterPanel();
			_termbase =  null;
			_filter = null;
			return;
		}
		
		ExtendedListBox<User> lst_user = _display.getUserListBox();
		lst_user.clear();
		
		lst_user.addItem(_i18n.getConstants().filter_selectUser2(), -1, null);
		
		Iterator<User> user_iter = users.iterator();
		while (user_iter.hasNext())
		{
			User user = user_iter.next();
			lst_user.addItem(user.getFullName(), user.getUserId(), user);
		}
		
		InputModel inputmodel = _filter_details.getInputModel();
		if (inputmodel == null)
		{
			AlertBox.show(_i18n.getConstants().log_ims());
			resetFilterPanel();
			_termbase = null;
			_filter = null;
			return;
		}
		
		populateFieldFilterTree(inputmodel);
	}
	
	private void resetFilterPanel()
	{
		DisclosurePanel project_panel = _display.getProjectPanel();
		project_panel.setVisible(false);
		
		DisclosurePanel user_panel = _display.getUserPanel();
		user_panel.setVisible(false);
		
		DisclosurePanel field_panel = _display.getFieldPanel();
		field_panel.setVisible(false);
				
		_display.getProjectListBox().clear();
		_display.getProjectDataProvider().getList().clear();
		
		_display.getFromDatePicker().setValue(null);
		_display.getFromDatePicker().setOpen(false);
		
		_display.getToDatePicker().setValue(null);
		_display.getToDatePicker().setOpen(false);
	}
	
	@Override
	public void go(HasWidgets container) 
	{
		container.add(_display.asWidget());
		
		_display.getFilterPanel().hide();
		
		bind();
	}
	
	public boolean isFilterSet()
	{
		if (_filter == null)
			return false;
		
		return true;
	}

	@Override
	public void signOut() 
	{
		_display.getFilterPanel().hide();
	}
}
