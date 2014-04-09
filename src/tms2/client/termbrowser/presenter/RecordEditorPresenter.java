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
import java.util.Iterator;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.ResetNavigationEvent;
import tms2.client.event.SearchEvent;
import tms2.client.event.UpdateRecordEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.presenter.TermBrowserControllerPresenter;
import tms2.client.service.RecordService;
import tms2.client.service.RecordServiceAsync;
import tms2.client.termbrowser.view.RecordRenderingView;
import tms2.client.util.AttachedPanelPositionUtility;
import tms2.client.util.FieldDataValidator;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.ConfirmBox;
import tms2.client.widgets.ConfirmBox.ConfirmCallback;
import tms2.client.widgets.DragMathDialog;
import tms2.client.widgets.EditableWidget;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedGrid;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.HasSignOut;
import tms2.client.widgets.PopupDialog;
import tms2.client.widgets.UploadDialog;
import tms2.client.widgets.UserProjectAccessPanel;
import tms2.shared.AccessRight;
import tms2.shared.AppProperties;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Field;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.RecordAttribute;
import tms2.shared.RecordElement;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.wrapper.RecordEditDetailsWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordEditorPresenter implements Presenter, HasSignOut
{			
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static RecordServiceAsync _record_service = GWT.create(RecordService.class);
	
	private Display _display = null;
			
	private boolean _is_editing = false;
	
	private Record _record = null;
	
	private RecordEditDetailsWrapper _record_details = null;
	
	private Field _source_field = null;
	private Field _target_field = null;
	private Field _current_search_field = null;
		
	private InsertPanelPresenter _ip_presenter = null;
	
	private boolean _is_filter_set = false;
	private ArrayList<Button> _cb_state_buttons = null;
	
	public interface Display
	{		
		public VerticalPanel getEditorPanel();
		public AttachedPanel getControlsPanel();
		public Button getSaveButton();
		public Button getCancelButton();	
		public HorizontalPanel getTermBaseDetailsPanel();
		public Label getTermBaseLabel();
		public ExtendedListBox<TermBase> getTermBaseListBox();
		public Label getDefaultProjectLabel();
		public ExtendedListBox<Project> getDefaultProjectListBox();
		public VerticalPanel getUserProjectPanel();
		public HorizontalPanel getPromptPanel();
		public Label getUserAccessPromptLabel();
		public Label getTerminologyLabel();
		public ExtendedListBox<TerminlogyObject> getTerminologyListBox();
		public UserProjectAccessPanel getUserProjectAssigner();
		public VerticalPanel getEditDetailsPanel();
		public VerticalPanel getRecordAttributesPanel();
		public VerticalPanel getTermsPanel();
		public Widget asWidget();
	}
	
	private class EditableWidgetIconHandler implements ClickHandler
	{
		private EditableWidget _editable_widget = null;
		private boolean _is_adding = false;
		
		public EditableWidgetIconHandler(EditableWidget editable_widget, boolean is_adding)
		{
			_editable_widget = editable_widget;
			_is_adding = is_adding;
		}
		
		@Override
		public void onClick(ClickEvent event) 
		{
			if (_is_adding)
				insert();
			else 
				remove();
		}		
		
		private void insert()
		{
			InputModel inputmodel = _record_details.getInputModel();
			
			ArrayList<TerminlogyObject> terminology_objects = new ArrayList<TerminlogyObject>();			
			TerminlogyObject terminology_object = _editable_widget.getTerminologyObject();
			
			FieldsPalette palette = null;
			
			if (terminology_object instanceof Term)
			{
				terminology_objects.addAll(inputmodel.getTermAttributesForTerm((Term) terminology_object));
				
				palette = new FieldsPalette(_editable_widget.getIndex(), terminology_objects);
				
			}
			else if (terminology_object instanceof Synonym)
			{
				EditableWidget parent_editable_widget = getParentEditableWidgetForSynonym(); 
				terminology_objects.addAll(inputmodel.getSynonymAttributesForTerm((Term) parent_editable_widget.getTerminologyObject()));
				
				palette = new FieldsPalette(_editable_widget.getIndex(), 
											terminology_objects);
			}
			
			if (palette.fieldsPopulated())
				palette.showRelativeTo(_editable_widget.getInsertIcon());
			else
			{
				AlertBox.show(_i18n.getConstants().controls_fp_noFields(), false, true);
				palette.hide();
			}								
		}
		
		private void remove()
		{
			TerminlogyObject terminology_object = _editable_widget.getTerminologyObject();
			
			archiveTerminologyObject(terminology_object);
			
			if (terminology_object instanceof RecordAttribute)
			{
				VerticalPanel record_attr_panel = _display.getRecordAttributesPanel();
				int record_attributes = record_attr_panel.getWidgetCount();
				
				for (int i = 0; i < record_attributes; i++)
				{
					HorizontalPanel widget_panel = (HorizontalPanel) record_attr_panel.getWidget(i);					
					EditableWidget widget = (EditableWidget)widget_panel.getWidget(0);					
					
					if (_editable_widget.getIndex() == widget.getIndex())	
					{
						record_attr_panel.remove(widget_panel);
						break;
					}
				}
				
				refreshRecordAttributePanelIndexes();
				populateTerminologyObjectListBox();
			}
			else if (terminology_object instanceof Term)
			{
				ArrayList<HorizontalPanel> removable_widgets = new ArrayList<HorizontalPanel>();
				
				VerticalPanel terms_panel = _display.getTermsPanel();
				int terms = terms_panel.getWidgetCount();
				
				for (int i = 0; i < terms; i++)
				{
					HorizontalPanel term_widget_panel = (HorizontalPanel) terms_panel.getWidget(i);					
					EditableWidget term_widget = (EditableWidget)term_widget_panel.getWidget(0);					
					
					if (_editable_widget.getIndex() == term_widget.getIndex())	
					{
						removable_widgets.add(term_widget_panel);
						
						if ((i + 1) == terms)
							continue;
							
						int next_index = i + 1;
						
						for (int j = next_index; j < terms; j++)
						{
							HorizontalPanel term_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(j);							
							EditableWidget term_attr_widget = (EditableWidget)term_attr_widget_panel.getWidget(0);
							
							if (_editable_widget.getIndex() == term_attr_widget.getParentIndex())
							{
								removable_widgets.add(term_attr_widget_panel);
								
								if ((j + 1) == terms)
									continue;
									
								next_index = j + 1;
								
								if (term_attr_widget.getTerminologyObject() instanceof Synonym)
								{
									for (int k = next_index; k < terms; k++)
									{
										HorizontalPanel synonym_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(k);										
										EditableWidget synonym_attr_widget = (EditableWidget)synonym_attr_widget_panel.getWidget(0);
										
										if (_editable_widget.getIndex() == synonym_attr_widget.getParentIndex() &&
												term_attr_widget.getIndex() == synonym_attr_widget.getSubParentIndex())
										{
											removable_widgets.add(synonym_attr_widget_panel);
										}
									}
								}
							}
						}
					}
				}
				
				Iterator<HorizontalPanel> iter = removable_widgets.iterator();
				while (iter.hasNext())
				{
					HorizontalPanel widget = iter.next();
					terms_panel.remove(widget);
				}
				
				refreshTermsPanelIndexes();
				populateTerminologyObjectListBox();
			}
			else if (terminology_object instanceof SynonymAttribute)
			{
				VerticalPanel terms_panel = _display.getTermsPanel();
				int terms = terms_panel.getWidgetCount();
				
				for (int i = 0; i < terms; i++)
				{
					HorizontalPanel synonym_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(i);					
					EditableWidget synonym_attr_widget = (EditableWidget)synonym_attr_widget_panel.getWidget(0);					
					
					if (_editable_widget.getIndex() == synonym_attr_widget.getIndex())
					{
						terms_panel.remove(synonym_attr_widget_panel);
						break;
					}						
				}	
				
				refreshTermsPanelIndexes();
			}
			else if (terminology_object instanceof TermAttribute)
			{
				ArrayList<HorizontalPanel> removable_widgets = new ArrayList<HorizontalPanel>();
				
				VerticalPanel terms_panel = _display.getTermsPanel();
				int terms = terms_panel.getWidgetCount();
				
				for (int i = 0; i < terms; i++)
				{
					HorizontalPanel term_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(i);					
					EditableWidget term_attr_widget = (EditableWidget)term_attr_widget_panel.getWidget(0);					
					
					if (_editable_widget.getIndex() == term_attr_widget.getIndex())
					{
						removable_widgets.add(term_attr_widget_panel);
							
						if ((i + 1) == terms)
							continue;
							
						int next_index = i + 1;
						
						if (term_attr_widget.getTerminologyObject() instanceof Synonym)
						{
							for (int j = next_index; j < terms; j++)
							{
								HorizontalPanel synonym_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(j);								
								EditableWidget synonym_attr_widget = (EditableWidget)synonym_attr_widget_panel.getWidget(0);
								
								if (_editable_widget.getIndex() == synonym_attr_widget.getSubParentIndex())								
									removable_widgets.add(synonym_attr_widget_panel);								
							}
						}
					
					}								
				}	
				
				Iterator<HorizontalPanel> iter = removable_widgets.iterator();
				while (iter.hasNext())
				{
					HorizontalPanel widget = iter.next();
					terms_panel.remove(widget);
				}
				
				refreshTermsPanelIndexes();
			}						
		}
		
		private EditableWidget getParentEditableWidgetForSynonym()
		{
			int parent_index = _editable_widget.getParentIndex();
			
			VerticalPanel terms_panel = _display.getTermsPanel();
			int terms = terms_panel.getWidgetCount();									
			
			for (int i = 0; i < terms; i++)
			{
				HorizontalPanel widget_panel = (HorizontalPanel) terms_panel.getWidget(i);				
				EditableWidget widget = (EditableWidget)widget_panel.getWidget(0);
				
				if (widget.getIndex() == parent_index)
					return widget;
			}
			
			return null;
		}
	}
	
	private class EditableWidgetFocusHandler implements FocusHandler
	{		
		private EditableWidget _editable_widget = null;
		private boolean _is_uploading = false;
		private PopupDialog _dialog = null;
		
		public EditableWidgetFocusHandler(EditableWidget editable_widget, boolean is_uploading)
		{
			_editable_widget = editable_widget;
			_is_uploading = is_uploading;
		}
		
		@Override
		public void onFocus(FocusEvent event) 
		{
			if (_is_uploading)
				upload();
			else
				formula();
		}		
		
		private void upload()
		{		
			// Do not pop-up another dialog if the last one is still showing
			if (_dialog != null && _dialog.isShowing())
				return;
			
			String prefix = "";
			
			TerminlogyObject terminology_object = _editable_widget.getTerminologyObject();
			
			if (terminology_object instanceof RecordAttribute)
				prefix = "RA";
			else if (terminology_object instanceof SynonymAttribute)
				prefix = "TSA";
			else if (terminology_object instanceof TermAttribute)
				prefix = "TA";
			
			VerticalPanel record_attribute_panel = _display.getRecordAttributesPanel();
			int record_attrib_id = getUniqueId(record_attribute_panel);
			
			VerticalPanel terms_panel = _display.getTermsPanel();
			int term_attribe_id = getUniqueId(terms_panel);
			
			int unique_id = record_attrib_id + term_attribe_id;
			
			prefix = prefix + "." + unique_id;
			
			if (_is_editing)
				_dialog = UploadDialog.show(_i18n.getConstants().controls_upload(), (TextBoxBase) _editable_widget.getWidget(), _record.getRecordId(), terminology_object, prefix);
			else
				_dialog = UploadDialog.show(_i18n.getConstants().controls_upload(), (TextBoxBase) _editable_widget.getWidget(), terminology_object, prefix);
							
		}
						
		private int getUniqueId(VerticalPanel panel)
		{
			int count = 0;
			int limit = panel.getWidgetCount();
			
			for (int i = 0; i < limit; i++)
			{
				HorizontalPanel widget_panel = (HorizontalPanel) panel.getWidget(i);				
				EditableWidget widget = (EditableWidget)widget_panel.getWidget(0);
				
				TerminlogyObject terminology_object = widget.getTerminologyObject();
				
				if (terminology_object.isHTMLHyperlink())
				{
					String text = widget.getText();
					
					// Only increment the count if the widget is not empty.
					if (text != null && text.trim().length() > 0)
						count++;
				}				
			}
			
			return count;
		}
				
		private void formula()
		{		
			// Do not pop-up another dialog if the last one is still showing
			if (_dialog != null && _dialog.isShowing())
				return;
			
			
			_dialog = DragMathDialog.show((TextBoxBase)_editable_widget.getWidget(), _editable_widget.getTerminologyObject());
		}
	}
	
	private class FieldsPalette extends PopupPanel
	{		
		private int _parent_index = -1;
		private ArrayList<TerminlogyObject> _palette_objects = null;
		private ExtendedGrid _fields_panel = null;
		
		public FieldsPalette(int parent_index, ArrayList<TerminlogyObject> palette_objects)
		{
			super(true, false);
			
			setStyleName("fieldsPalette");
			setAnimationEnabled(true);
				
			_parent_index = parent_index;
			_palette_objects = palette_objects;
			
			buildFieldsPalette();
		}
				
		private void buildFieldsPalette()
		{
			VerticalPanel field_palette_panel = new VerticalPanel();
			field_palette_panel.setSpacing(10);
						
			StackPanel stack_panel = new StackPanel();
			
			field_palette_panel.add(stack_panel);
			
			ScrollPanel scroller = new ScrollPanel();
			scroller.setHeight("150px");
			scroller.setWidth("300px");
			
			_fields_panel = new ExtendedGrid(true, 5, 5);
			_fields_panel.setWidth("100%");
			
			scroller.add(_fields_panel);
			
			stack_panel.add(scroller, "<div class=labelTextBold><font style=\"font-size: 12pt\">\u25BC </font>" + _i18n.getConstants().controls_fp_fields() + "</div>", true);
					
			HorizontalPanel button_panel = new HorizontalPanel();
			button_panel.setSpacing(5);
			
			Button btn_cancel = new Button(_i18n.getConstants().controls_cancel());
			btn_cancel.addClickHandler(new ClickHandler() 
			{				
				@Override
				public void onClick(ClickEvent event) 
				{
					hide();
				}
			});
			
			button_panel.add(btn_cancel);
			
			field_palette_panel.add(button_panel);
			
			setWidget(field_palette_panel);	
		}
		
		private boolean fieldsPopulated()
		{
			boolean populated = false;
			
			Iterator<TerminlogyObject> iter = _palette_objects.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject palette_object = iter.next();
				
				if (! hasUpdateAccess(palette_object.getUserCategoryAccessRight()))
					continue;
				
				RadioButton btn_radio = new RadioButton("fields");							
				btn_radio.addClickHandler(new RadioButtonClickHandler(palette_object));
				
				Label label =  new Label(palette_object.getFieldName(), false);
				
				_fields_panel.appendWidgets(new Widget[] { btn_radio, label});	
				
				populated = true;
			}
			
			return populated;
		}
		
		private class RadioButtonClickHandler implements ClickHandler
		{			
			private TerminlogyObject _terminology_object = null;

			public RadioButtonClickHandler(TerminlogyObject terminology_object)
			{				
				_terminology_object = terminology_object;
			}
			
			@Override
			public void onClick(ClickEvent event) 
			{
				VerticalPanel terms_panel = _display.getTermsPanel();

				int index = _parent_index;
				++index;
				
				if (_terminology_object instanceof SynonymAttribute)											
					renderTerminologyObject(terms_panel, newTerminlogyObject(_terminology_object), index);				
				else if(_terminology_object instanceof TermAttribute)					
					renderTerminologyObject(terms_panel, newTerminlogyObject(_terminology_object), index);				
				
				refreshTermsPanelIndexes();
			}			
		}
	}
	
	public RecordEditorPresenter(Display display, Record record, Field source_field, Field target_field, 
							     boolean is_editing, InsertPanelPresenter ip_presenter, boolean is_filter_set, ArrayList<Button> cb_state_buttons)
	{
		_display = display;
		_record = record;
		_source_field = source_field;	
		_target_field = target_field;
		
		_is_editing = is_editing;	
		
		_ip_presenter = ip_presenter;
		
		_is_filter_set = is_filter_set;
		_cb_state_buttons = cb_state_buttons;
		
		_access_controller.addSignOut(this);
	}	
	
	public boolean validateRecordEditDetails(RecordEditDetailsWrapper record_edit_details)
	{
		if (record_edit_details == null)
		{
			AlertBox.show(_i18n.getConstants().log_record_edit_details());
			
			if (_is_editing)			
				unlockRecord();
			
			return false;					
		}
		
		final ArrayList<TermBase> termbases = record_edit_details.getTermBases();
						
		if (termbases == null || termbases.size() == 0) 
		{
			AlertBox.show(_i18n.getConstants().recordEdit_error_noProjects());	
			
			if (_is_editing)
				unlockRecord();
			
			return false;
		}					
		
		final InputModel inputmodel = record_edit_details.getInputModel();
		if (inputmodel != null)
		{
			AppProperties props = _access_controller.getAppProperties();
			if (! inputmodel.isProjectEditable(props.getProjectField()))
			{
				AlertBox.show(_i18n.getConstants().recordEdit_project_no_rights());	
				
				if (_is_editing)
					unlockRecord();
				
				return false;
			}
			
			if (! inputmodel.isSourceEditable(props.getSortIndexField()))
			{
				AlertBox.show(_i18n.getConstants().recordEdit_sortindex_no_rights());	
				
				if (_is_editing)
					unlockRecord();
				
				return false;
			}
			
			if (! props.getSortIndexField().equals(_source_field.getFieldName()))
			{
				if (! inputmodel.isSourceEditable(_source_field.getFieldName()))
				{
					AlertBox.show(_i18n.getConstants().recordEdit_customsortindex_no_rights());	
					
					if (_is_editing)
						unlockRecord();
					
					return false;
				}
			}
		}
		
		if (_is_editing)
		{
			final User user = record_edit_details.getUser();
			
			if (user != null)
			{
				AlertBox.show(_i18n.getMessages().recordBrowse_lockedForEdit(user.getFullName()));
				
				return false;
			}
		}
		
		_record_details = record_edit_details;
		
		return true;
	}
	
	public void edit()
	{
		resetEditorPanel();
		
		if (_is_editing)
		{
			if (! recordBelongsToProject())
			{
				ConfirmBox.show(_i18n.getConstants().cb_assign_project_record(), false, true, new ConfirmBox.ConfirmCallback()
				{													
					@Override
					public void onConfirm() 
					{			
						init();			
					}
					
					@Override
					public void onCancel() 
					{
						AttachedPanelPositionUtility.removePosition(_display.getControlsPanel());
						
						Iterator<Button> iter = _cb_state_buttons.iterator();
						while (iter.hasNext())
						{
							Button button = iter.next();
							
							if (button.getText().equals(_i18n.getConstants().filter_export()))
							{
								if (_is_filter_set)
									button.setEnabled(true);
								else
									button.setEnabled(false);
							}
							else
								button.setEnabled(true);
						}
						
						unlockRecord();
					}
				});
			}
			else
				init();	
		}
		else
			init();
	}
	
	private boolean recordBelongsToProject()	
	{
		ArrayList<TermBase> termbases = _record_details.getTermBases();
		
		ArrayList<Project> projects = new ArrayList<Project>();
		Iterator<TermBase> iter = termbases.iterator();
		
		while (iter.hasNext())
		{
			TermBase termbase = iter.next();
			projects.addAll(termbase.getProjects());
		}
		
		ArrayList<Project> record_projects = _record.getProjects();
		
		Iterator<Project> project_iter = projects.iterator();
		while (project_iter.hasNext())
		{
			Project project = project_iter.next();
			
			Iterator<Project> record_project_iter = record_projects.iterator();
			
			while (record_project_iter.hasNext())
			{
				Project record_project = record_project_iter.next();
				
				if (record_project.getProjectId() == project.getProjectId())
					return true;
			}
		}
		
		return false;
	}
	
	private void init()
	{		
		ArrayList<TermBase> access_controlled_termbases = _record_details.getTermBases();
		
		Label lbl_termbase = _display.getTermBaseLabel();
		Label lbl_project = _display.getDefaultProjectLabel();
		ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
		ExtendedListBox<Project> lst_project = _display.getDefaultProjectListBox();
		
		if (_is_editing)
		{		
			HorizontalPanel termdetails_panel = _display.getTermBaseDetailsPanel();
			termdetails_panel.setVisible(true);
			
			lbl_termbase.setVisible(false);
			lbl_project.setVisible(true);
			lst_termbase.setVisible(false);
			lst_project.setVisible(true);
			
			ArrayList<Project> assigned_projects = new ArrayList<Project>();
			assigned_projects.addAll(_record.getProjects());
			
			Project default_project = assigned_projects.remove(0);
			
			lst_project.addItem(default_project.getProjectName(), default_project.getProjectId(), default_project);
						
			ArrayList<Project> access_controlled_projects = new ArrayList<Project>();
			Iterator<TermBase> iter = access_controlled_termbases.iterator();
			while (iter.hasNext())
			{
				TermBase termbase = iter.next();
				
				access_controlled_projects.addAll(termbase.getProjects());
			}
			
			Iterator<Project> project_iter = access_controlled_projects.iterator();
			while (project_iter.hasNext())
			{
				Project project = project_iter.next();
				
				if (project.getProjectId() == default_project.getProjectId())
				{
					project_iter.remove();
					continue;
				}
				
				Iterator<Project> assigned_iter = assigned_projects.iterator();
				while (assigned_iter.hasNext())
				{
					Project assigned_project = assigned_iter.next();
					
					if (assigned_project.getProjectId() == project.getProjectId())
						project_iter.remove();
				}
			}
			
			populateProjectAssignerPanel(access_controlled_projects, assigned_projects);
		}
		else
		{
			HorizontalPanel termdetails_panel = _display.getTermBaseDetailsPanel();
			termdetails_panel.setVisible(true);
			
			lbl_termbase.setVisible(true);
			lbl_project.setVisible(false);
			lst_termbase.setVisible(true);
			lst_project.setVisible(false);
			
			populateTermBases(access_controlled_termbases);
		}
	}
	
	private void populateTermBases(ArrayList<TermBase> access_controlled_termbases)
	{
		ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
		lst_termbase.clear();
		
		lst_termbase.addItem(_i18n.getConstants().recordEdit_selectDB(), "-1", null);
		
		Iterator<TermBase> iter = access_controlled_termbases.iterator();
		while (iter.hasNext())
		{
			TermBase termbase = iter.next();
			lst_termbase.addItem(termbase.getTermdbname(), termbase.getTermdbid(), termbase);			
		}
	}
			
	private void bind()
	{
		AttachedPanelPositionUtility.addPosition(_display.getControlsPanel());
		
		addTermBaseListBoxHandler();
		
		if (_is_editing)
			addTerminologyListHandler();
				
		addSaveButtonHandler();
		addCancelButtonHandler();
	}
		
	private void addTermBaseListBoxHandler()
	{
		_display.getTermBaseListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{			
				TermBase termbase = _display.getTermBaseListBox().getSelectedItem();
				
				if (termbase != null)				
					populateProjectAssignerPanel(termbase.getProjects(), new ArrayList<Project>());
				else
					resetEditorPanel();
			}
		});
	}
	
	private void addTerminologyListHandler()
	{
		final ExtendedListBox<TerminlogyObject> lst_terminology_object = _display.getTerminologyListBox();
		
		lst_terminology_object.addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				TerminlogyObject terminology_object = lst_terminology_object.getSelectedItem();
				
				if (terminology_object != null)
				{
					VerticalPanel panel_to_render = null;
					int index = 0;
					
					if (terminology_object instanceof RecordAttribute)
					{
						index = _display.getRecordAttributesPanel().getWidgetCount();
						panel_to_render = _display.getRecordAttributesPanel();
					}
					else if (terminology_object instanceof Term)
					{
						index = _display.getTermsPanel().getWidgetCount();
						panel_to_render = _display.getTermsPanel();
					}
					
					renderTerminologyObject(panel_to_render, newTerminlogyObject(terminology_object), index);
					
					refreshRecordAttributePanelIndexes();	
					
					lst_terminology_object.removeItem(lst_terminology_object.getSelectedIndex());
				}
				
				_display.getTerminologyListBox().setSelectedIndex(0);
			}
		});
	}
	
	private void addSaveButtonHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				Record record = generateRecord();
				
				if (record != null)
				{
					TermBrowserControllerPresenter termbrowser_controller = _access_controller.getTermBrowserController();
					termbrowser_controller.enableNavigation();
					
					event_bus.fireEvent(new UpdateRecordEvent(record, false));
				}
			}
		});
	}
	
	private void addCancelButtonHandler()
	{		
		_display.getCancelButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				AttachedPanelPositionUtility.removePosition(_display.getControlsPanel());
				
				Iterator<Button> iter = _cb_state_buttons.iterator();
				while (iter.hasNext())
				{
					Button button = iter.next();
					
					if (button.getText().equals(_i18n.getConstants().filter_export()))
					{
						if (_is_filter_set)
							button.setEnabled(true);
						else
							button.setEnabled(false);
					}
					else
						button.setEnabled(true);
				}
				
				TermBrowserControllerPresenter termbrowser_controller = _access_controller.getTermBrowserController();
				termbrowser_controller.enableNavigation();
				
				if (_is_editing)				
					unlockRecord();				
				else
				{				
					RootPanel.get("content").clear();
					
					if (_record != null)
					{
						RecordRenderingPresenter rr_presenter = new RecordRenderingPresenter(new RecordRenderingView(), _record, _source_field, _target_field);		
						rr_presenter.go(RootPanel.get("content"));
						
						rr_presenter.render();
					}
				}					
			}
		});
	}
	
	private void populateProjectAssignerPanel(ArrayList<Project> access_controlled_projects, ArrayList<Project> assigned_projects)
	{		
		TermBrowserControllerPresenter termbrowser_controller = _access_controller.getTermBrowserController();
		termbrowser_controller.disableNavigation();
		
		_display.getControlsPanel().position();
		_display.getControlsPanel().show();
		
		VerticalPanel user_project_panel = _display.getUserProjectPanel();
		user_project_panel.setVisible(true);
				
		HorizontalPanel prompt_panel = _display.getPromptPanel();
		prompt_panel.setVisible(true);
		
		VerticalPanel edit_details_panel = _display.getEditDetailsPanel();
		edit_details_panel.setVisible(true);
				
		if (! _is_editing)
		{
			_display.getUserAccessPromptLabel().setVisible(true);
			_display.getTerminologyLabel().setVisible(false);
			_display.getTerminologyListBox().setVisible(false);	
		}

		UserProjectAccessPanel assigner = _display.getUserProjectAssigner();
		assigner.reset();
						
		assigner.setAvailableProjects(access_controlled_projects);
		assigner.setAssignedProjects(assigned_projects);
		
		renderRecordAttributes();
		renderTerms();
		
		refreshTermsPanelIndexes();
		
		if (_is_editing)
		{						
			_display.getUserAccessPromptLabel().setVisible(false);	
			
			populateTerminologyObjectListBox();
			
			if (_display.getTerminologyListBox().getItemCount() > 0)
			{
				_display.getTerminologyLabel().setVisible(true);
				_display.getTerminologyListBox().setVisible(true);
			}
			else
			{
				_display.getTerminologyLabel().setVisible(false);
				_display.getTerminologyListBox().setVisible(false);			
			}
		}
		
/*		int client_height = Window.getClientHeight();
		
		int control_bar_height = 23;
		int banner_height = 90;
		int record_navigator_height = termbrowser_controller.getDisplay().asWidget().getOffsetHeight();
		int project_assigner_height = assigner.getOffsetHeight();
		int prompt_panel_height = prompt_panel.getOffsetHeight();
		
		int running_height = control_bar_height + banner_height + record_navigator_height + project_assigner_height + prompt_panel_height;
		running_height += 150;
		
		int edit_sroller_height = client_height - running_height;
		
		_display.getEditScroller().setHeight(edit_sroller_height + "px");	*/	
	}
	
	private void populateTerminologyObjectListBox()
	{
		InputModel inputmodel = _record_details.getInputModel();
		
		AppProperties props = _access_controller.getAppProperties();
		
		ExtendedListBox<TerminlogyObject> lst_terminology_object = _display.getTerminologyListBox();
		lst_terminology_object.clear();
		
		lst_terminology_object.addItem(_i18n.getConstants().admin_field_selectField(), -1, null);
		
		Iterator<TerminlogyObject> iter = inputmodel.getRecordAttributes().iterator();
		while (iter.hasNext())
		{
			TerminlogyObject terminology_object = iter.next();
			
			if (hasUpdateAccess(terminology_object.getUserCategoryAccessRight()) && ! terminology_object.isProject(props.getProjectField()))
				lst_terminology_object.addItem(terminology_object.getFieldName(), terminology_object.getFieldId(), terminology_object);
		}
		
		iter = inputmodel.getTerms().iterator();
		while (iter.hasNext())
		{
			TerminlogyObject terminology_object = iter.next();
			
			if (hasUpdateAccess(terminology_object.getUserCategoryAccessRight()) 
				&& ! terminology_object.isSortIndex(props.getSortIndexField()) &&
				! terminology_object.isMandatory())
			{
				if (! isRecordTerminologyObject(terminology_object))
					lst_terminology_object.addItem(terminology_object.getFieldName(), terminology_object.getFieldId(), terminology_object);
			}
		}
	}
	
	private boolean isRecordTerminologyObject(TerminlogyObject terminology_object)
	{
		VerticalPanel terms_panel = _display.getTermsPanel();
		int terms = terms_panel.getWidgetCount();
		
		for (int i = 0; i < terms; i++)
		{
			HorizontalPanel term_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(i);								
			EditableWidget term_attr_widget = (EditableWidget)term_attr_widget_panel.getWidget(0);
			
			TerminlogyObject term = term_attr_widget.getTerminologyObject();
			
			if (term instanceof Term)
			{
				if (terminology_object.getFieldId() == term.getFieldId())						
					return true;		
			}
		}
				
		return false;
	}
	
	/**
	 * Archive a {@link TerminlogyObject} when it is removed from the form. Children of
	 * {@link TerminlogyObject}'s will also be archived.
	 * @param terminology_object
	 */
	private void archiveTerminologyObject(TerminlogyObject terminology_object)
	{
		if (terminology_object instanceof RecordAttribute)
		{
			ArrayList<TerminlogyObject> record_attributes = _record.getRecordAttributes();
			
			if (record_attributes == null || record_attributes.size() == 0)
				return;
			
			Iterator<TerminlogyObject> iter = record_attributes.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject record_attribute = iter.next();
				
				if (record_attribute.getResourceId() == terminology_object.getResourceId())
				{
					record_attribute.setIsArchived(true);
					break;
				}
			}
		}
		else if (terminology_object instanceof Term)
		{
			ArrayList<TerminlogyObject> terms = _record.getTerms();
			
			if (terms == null || terms.size() == 0)
				return;
			
			Iterator<TerminlogyObject> iter = terms.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject term = iter.next();
				
				if (term.getResourceId() == terminology_object.getResourceId())
				{
					term.setIsArchived(true);
																		
					ArrayList<ChildTerminologyObject> term_attributes = ((Term)term).getTermAttributes();
					
					if (term_attributes == null || term_attributes.size() == 0)
						break;
						
					Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
					while (term_attr_iter.hasNext())
					{
						ChildTerminologyObject term_attribute = term_attr_iter.next();
						
						term_attribute.setIsArchived(true);
							
						if (term_attribute instanceof Synonym)
						{
							ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
							
							if (synonym_attributes == null || synonym_attributes.size() == 0)
								continue;
							
							Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
							while (synonym_attr_iter.hasNext())
							{
								ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
								synonym_attribute.setIsArchived(true);
							}
						}						
					}
				}
			}
		}
		else if (terminology_object instanceof SynonymAttribute)
		{
			ArrayList<TerminlogyObject> terms = _record.getTerms();
			
			if (terms == null || terms.size() == 0)
				return;
			
			Iterator<TerminlogyObject> iter = terms.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject term = iter.next();
				
				ArrayList<ChildTerminologyObject> term_attributes = ((Term)term).getTermAttributes();
				if (term_attributes == null || term_attributes.size() == 0)
					continue;
				
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					ChildTerminologyObject term_attribute = term_attr_iter.next();
					
					if (term_attribute instanceof Synonym)
					{
						ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
						
						if (synonym_attributes == null || synonym_attributes.size() == 0)
							continue;
						
						Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
						while (synonym_attr_iter.hasNext())
						{
							ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
							
							if (synonym_attribute.getResourceId() == terminology_object.getResourceId())
							{
								synonym_attribute.setIsArchived(true);
								break;
							}
						}
					}
				}
			}
		}
		else
		{
			ArrayList<TerminlogyObject> terms = _record.getTerms();
			
			if (terms == null || terms.size() == 0)
				return;
			
			Iterator<TerminlogyObject> iter = terms.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject term = iter.next();
				
				ArrayList<ChildTerminologyObject> term_attributes = ((Term)term).getTermAttributes();
				if (term_attributes == null || term_attributes.size() == 0)
					continue;
				
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					ChildTerminologyObject term_attribute = term_attr_iter.next();
					
					if (term_attribute.getResourceId() == terminology_object.getResourceId())
					{
						term_attribute.setIsArchived(true);
												
						if (term_attribute instanceof Synonym)
						{
							ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
							
							if (synonym_attributes == null || synonym_attributes.size() == 0)
								continue;
							
							Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
							while (synonym_attr_iter.hasNext())
							{
								ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
															
								synonym_attribute.setIsArchived(true);																	
							}
						}
					}
				}
			}
		}
	}
			
	private void renderRecordAttributes()
	{		
		InputModel inputmodel = _record_details.getInputModel();
		AppProperties props = _access_controller.getAppProperties();
				
		int index = 0;
		
		VerticalPanel record_attributes_panel = _display.getRecordAttributesPanel();
		record_attributes_panel.clear();
						
		ArrayList<TerminlogyObject> record_attributes = null;
		
		if (! _is_editing)
			record_attributes = inputmodel.getRecordAttributes();
		else		
			record_attributes = _record.getRecordAttributes();		
		
		Iterator<TerminlogyObject> iter = record_attributes.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject record_attribute = iter.next();
			
			if (record_attribute.getFieldName().equals(props.getProjectField()))
				continue;
									
			// For create mode do not list all TerminologyObjects. Only
			// those that have sufficient rights.
			if (! _is_editing)
			{
				AccessRight access_right = record_attribute.getUserCategoryAccessRight();
				
				if (! hasUpdateAccess(access_right))
					continue;
			}
			else
				record_attribute = assignInputModelAccessRightsToTerminologyObject(record_attribute);
			
			renderTerminologyObject(record_attributes_panel, record_attribute, index);		
			index++;
		}				
	}
	
	private void renderTerms()
	{		
		InputModel inputmodel = _record_details.getInputModel();
		
		int index = 0;
		
		VerticalPanel terms_panel = _display.getTermsPanel();
		terms_panel.clear();		
		
		ArrayList<TerminlogyObject> terms = null;
		
		if (! _is_editing)
			terms = inputmodel.getTerms();
		else
			terms = _record.getTerms();
		
		Iterator<TerminlogyObject> term_iter = terms.iterator();
		while (term_iter.hasNext())
		{
			TerminlogyObject term = term_iter.next();
								
			// For create mode do not list all TerminologyObjects. Only
			// those that have sufficient rights.
			if (! _is_editing)
			{
				AccessRight access_right = term.getUserCategoryAccessRight();
				
				if (! hasUpdateAccess(access_right))
					continue;
			}
			else
				term = assignInputModelAccessRightsToTerminologyObject(term);
			
			renderTerminologyObject(terms_panel, term, index);			
			index++;
			
			ArrayList<ChildTerminologyObject> termattributes = new ArrayList<ChildTerminologyObject>();
			
			if (! _is_editing)
				termattributes = inputmodel.getTermAttributesForTerm((Term) term);
			else
				termattributes = ((Term)term).getTermAttributes();
			
			Iterator<ChildTerminologyObject> term_attr_iter = termattributes.iterator();
			while (term_attr_iter.hasNext())
			{
				ChildTerminologyObject term_attribute = term_attr_iter.next();
												
				// For create mode do not list all TerminologyObjects. Only
				// those that have sufficient rights.
				if (! _is_editing)
				{
					AccessRight term_attr_access_right = term_attribute.getUserCategoryAccessRight();
					
					if (! hasUpdateAccess(term_attr_access_right))
						continue;
				}
				else
					term_attribute = (ChildTerminologyObject) assignInputModelAccessRightsToChildTerminologyObject(term, term_attribute);
				
				renderTerminologyObject(terms_panel, term_attribute, index);
				index++;
				
				if (term_attribute.isSynonymField())
				{
					ArrayList<ChildTerminologyObject> synonym_attributes = new ArrayList<ChildTerminologyObject>();
					
					if (! _is_editing)
						synonym_attributes = inputmodel.getSynonymAttributesForTerm((Term) term);
					else
						synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
					
					Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
					while (synonym_attr_iter.hasNext())
					{
						ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
																		
						// For create mode do not list all TerminologyObjects. Only
						// those that have sufficient rights.
						if (! _is_editing)
						{
							AccessRight synonym_attr_access_right = synonym_attribute.getUserCategoryAccessRight();
							
							if (! hasUpdateAccess(synonym_attr_access_right))
								continue;
						}
						else
							synonym_attribute = (ChildTerminologyObject) assignInputModelAccessRightsToChildTerminologyObject(term, synonym_attribute);
						
						renderTerminologyObject(terms_panel, synonym_attribute, index);
						index++;
					}
				}
			}
		}
	}
	
	private void renderTerminologyObject(VerticalPanel panel_to_render_to, TerminlogyObject terminology_object, int index)
	{		
		HorizontalPanel widget_panel = new HorizontalPanel();
		widget_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		widget_panel.setSpacing(0);
		widget_panel.setWidth("100%");
		
		if (terminology_object instanceof RecordAttribute ||
			terminology_object instanceof Term )
			{
				widget_panel.setStyleName("recordBorderedBlock");
			}
			else
			{
				if (terminology_object instanceof SynonymAttribute)
					widget_panel.setStyleName("contentSubSubblock");
				else if (terminology_object instanceof Synonym)
					widget_panel.setStyleName("synonymblock");
				else
					widget_panel.setStyleName("contentSubblock");
			}
								
		EditableWidget editable_widget = new EditableWidget(_source_field, terminology_object, _is_editing, index);
					
		widget_panel.add(editable_widget);
		widget_panel.setCellVerticalAlignment(editable_widget, HasVerticalAlignment.ALIGN_TOP);
		
		panel_to_render_to.insert(widget_panel, index);
		panel_to_render_to.setCellVerticalAlignment(widget_panel, HasVerticalAlignment.ALIGN_TOP);
		
		// For edit mode display all TerminologyObjects, however
		// those that do not have sufficient rights will
		// be disabled.
		if (_is_editing)
		{
			if (! hasUpdateAccess(terminology_object.getUserCategoryAccessRight()))
				editable_widget.setEnabled(false);
			
			editable_widget.getInsertIcon().addClickHandler(new EditableWidgetIconHandler(editable_widget, true));
			editable_widget.getDeleteIcon().addClickHandler(new EditableWidgetIconHandler(editable_widget, false));
		}
		
		if (terminology_object.isHTMLHyperlink())
			editable_widget.getWidget().addFocusHandler(new EditableWidgetFocusHandler(editable_widget, true));
		else if (terminology_object.isFormula())
			editable_widget.getWidget().addFocusHandler(new EditableWidgetFocusHandler(editable_widget, false));	
		else 
		{
			final FocusWidget widget = editable_widget.getWidget();
			
			if (widget instanceof TextBoxBase)
			{
				widget.addFocusHandler(new FocusHandler() 
				{					
					@Override
					public void onFocus(FocusEvent event) 
					{
						_ip_presenter.setFocusTextBase((TextBoxBase) widget);
					}
				});
			}
		}
		
		if (terminology_object.isIndexField() || terminology_object.isSynonymField())
			searchDuplicates(terminology_object, editable_widget);
			
	}
		
	private TerminlogyObject assignInputModelAccessRightsToTerminologyObject(TerminlogyObject terminology_object)
	{
		InputModel inputmodel = _record_details.getInputModel();
		
		if (terminology_object instanceof RecordAttribute)
		{					
			Iterator<TerminlogyObject> iter = inputmodel.getRecordAttributes().iterator();
			while (iter.hasNext())
			{
				TerminlogyObject record_attribute = iter.next();
				
				if (record_attribute.getFieldId() == terminology_object.getFieldId())
				{
					terminology_object.setUserCategoryAccessRight(record_attribute.getUserCategoryAccessRight());
					break;
				}
			}
		}
		else 
		{
			Iterator<TerminlogyObject> iter = inputmodel.getTerms().iterator();
			while (iter.hasNext())
			{
				TerminlogyObject term = iter.next();
				
				if (term.getFieldId() == terminology_object.getFieldId())
				{
					terminology_object.setUserCategoryAccessRight(term.getUserCategoryAccessRight());
					break;
				}
			}
		}

		
		return terminology_object;
	}
	
	private ChildTerminologyObject assignInputModelAccessRightsToChildTerminologyObject(TerminlogyObject terminology_object, ChildTerminologyObject child_terminology_object)
	{
		InputModel inputmodel = _record_details.getInputModel();
		
		if (child_terminology_object instanceof SynonymAttribute)
		{				
			Iterator<TerminlogyObject> terms_iter = inputmodel.getTerms().iterator();
			while (terms_iter.hasNext())
			{
				TerminlogyObject term = terms_iter.next();
				
				if (term.getFieldId() == terminology_object.getFieldId())
				{
					ArrayList<ChildTerminologyObject> synonym_attributes = inputmodel.getSynonymAttributesForTerm((Term) term);
					
					Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
					while (synonym_attr_iter.hasNext())
					{
						ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
						
						if (synonym_attribute.getFieldId() == child_terminology_object.getFieldId())
						{
							child_terminology_object.setUserCategoryAccessRight(synonym_attribute.getUserCategoryAccessRight());
							break;
						}
					}
				}
			}
		}
		else
		{
			Iterator<TerminlogyObject> terms_iter = inputmodel.getTerms().iterator();
			while (terms_iter.hasNext())
			{
				TerminlogyObject term = terms_iter.next();
				
				if (term.getFieldId() == terminology_object.getFieldId())
				{
					ArrayList<ChildTerminologyObject> term_attributes = inputmodel.getTermAttributesForTerm((Term) term);
					
					Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
					while (term_attr_iter.hasNext())
					{
						ChildTerminologyObject term_attribute = term_attr_iter.next();
						
						if (term_attribute.getFieldId() == child_terminology_object.getFieldId())
						{
							child_terminology_object.setUserCategoryAccessRight(term_attribute.getUserCategoryAccessRight());
							break;
						}
					}
				}
			}
		}
		
		
		return child_terminology_object;
	}
	
	/**
	 * Reorder the index of EditableWidget's of this panel
	 */
	private void refreshRecordAttributePanelIndexes()
	{
		VerticalPanel record_attributes_panel = _display.getRecordAttributesPanel();
		int record_attributes = record_attributes_panel.getWidgetCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			HorizontalPanel record_attr_widget_panel = (HorizontalPanel) record_attributes_panel.getWidget(i);
			
			EditableWidget record_attr_widget = (EditableWidget)record_attr_widget_panel.getWidget(0);
			
			TerminlogyObject record_attribute = record_attr_widget.getTerminologyObject();
			
			if (record_attribute instanceof RecordAttribute)				
				record_attr_widget.setIndex(i);
		}
	}
	
	/**
	 * Reorder the index of EditableWidget's of this panel, also
	 * taking into consideration the {@link ChildTerminologyObject}'s of {@link TerminlogyObject}'s
	 */
	private void refreshTermsPanelIndexes()
	{
		VerticalPanel terms_panel = _display.getTermsPanel();
		int terms = terms_panel.getWidgetCount();
		
		for (int i = 0; i < terms; i++)
		{
			HorizontalPanel term_widget_panel = (HorizontalPanel) terms_panel.getWidget(i);
			
			EditableWidget term_widget = (EditableWidget)term_widget_panel.getWidget(0);
			
			TerminlogyObject term = term_widget.getTerminologyObject();
			
			if (term instanceof Term)	
			{
				// This is a TerminologyObject. Its childdern if it
				// has any will be handled by the subsequent loops.												
				term_widget.setIndex(i);
					
				if ((i + 1) == terms)
					continue;
					
				int next_index = i + 1;
				
				for (int j = next_index; j < terms; j++)
				{
					HorizontalPanel term_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(j);								
					EditableWidget term_attr_widget = (EditableWidget)term_attr_widget_panel.getWidget(0);
					
					TerminlogyObject term_attribute = term_attr_widget.getTerminologyObject();
					
					// Handle only synonyms here					
					if (term_attribute instanceof Synonym && 
						! (term_attribute instanceof Term) &&
						!(term_attribute instanceof SynonymAttribute))
					{			
						// A synonym's parent_index is the Term it falls under.
						// The index is its location on the form
						term_attr_widget.setParentIndex(i);
						term_attr_widget.setIndex(j);
						
						if ((j + 1) == terms)
							continue;

						next_index = j + 1;
						
						for (int k = next_index; k < terms; k++)
						{
							HorizontalPanel synonym_attr_widget_panel = (HorizontalPanel) terms_panel.getWidget(k);								
							EditableWidget synonym_attr_widget = (EditableWidget)synonym_attr_widget_panel.getWidget(0);
							
							TerminlogyObject synonym_attribute = synonym_attr_widget.getTerminologyObject();
							
							if (synonym_attribute instanceof SynonymAttribute)
							{
								// A SynonymAttribute's parent_index is the Term it fall under.
								// The sub_parent_index is the Synonym its falls under.
								// The index is its location on the form
								synonym_attr_widget.setParentIndex(i);
								synonym_attr_widget.setSubParentIndex(j);
								synonym_attr_widget.setIndex(k);	
							}
						}								
					}
					// Handle only TermAttributes here, No Synonyms, SynonymAttributes or Terms
					else if (!(term_attribute instanceof SynonymAttribute) && 
							! (term_attribute instanceof Term))
					{
						term_attr_widget.setParentIndex(i);
						term_attr_widget.setIndex(j);
					}
						
				}						
			}						
		}
	}
	
	private void searchDuplicates(TerminlogyObject terminology_object, final EditableWidget editable_widget)
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		final TextBoxBase widget = (TextBoxBase) editable_widget.getWidget();
		
		widget.addBlurHandler(new BlurHandler()
		{			
			@Override
			public void onBlur(BlurEvent event) 
			{		
				String text = widget.getText().trim();
				
				if (! text.isEmpty())
				{
					_current_search_field = editable_widget.getTerminologyObject();
					event_bus.fireEvent(new SearchEvent(RecordEditorPresenter.this, 
										text, _i18n.getConstants().search_exactmatch(), false));
				}
				else
					_current_search_field = null;
			}
		});				
	}
				
	private boolean hasUpdateAccess(AccessRight access_right)
	{
		if (access_right == null)
			return false;
		
		return access_right.mayUpdate();
	}
	
	private TerminlogyObject newTerminlogyObject(TerminlogyObject terminology_object)
	{
		TerminlogyObject new_terminology_object = null;
		
		if (terminology_object instanceof RecordAttribute)
			new_terminology_object = new RecordAttribute();
		else if (terminology_object instanceof Term)
			new_terminology_object = new Term();
		else	
		{
			if (terminology_object instanceof SynonymAttribute)
				new_terminology_object = new SynonymAttribute();
			else if (terminology_object instanceof Synonym)
				new_terminology_object = new Synonym();
			else
				new_terminology_object = new TermAttribute();
		}
						
		new_terminology_object.setFieldId(terminology_object.getFieldId());
		new_terminology_object.setFieldName(terminology_object.getFieldName());
		new_terminology_object.setFieldTypeId(terminology_object.getFieldTypeId());
		new_terminology_object.setFieldDataTypeId(terminology_object.getFieldDataTypeId());
		new_terminology_object.setPresetFields(terminology_object.getPresetFields());
		new_terminology_object.setMaxlength(terminology_object.getMaxlength());
		new_terminology_object.setDefaultValue(terminology_object.getDefaultValue());
		new_terminology_object.setUserCategoryAccessRight(terminology_object.getUserCategoryAccessRight());
		
		return new_terminology_object;
	}
	
	private Record generateRecord()
	{
		Record record = new Record();
		
		if (! _is_editing)				
			record.setTermdbId(_display.getTermBaseListBox().getSelectedItem().getTermdbid());		
		else	
		{
			record.setRecordId(_record.getRecordId());
			record.setTermdbId(_record.getTermdbId());
			
			ArrayList<TerminlogyObject> record_attributes = new ArrayList<TerminlogyObject>();
			
			if (_record.getRecordAttributes() != null && _record.getRecordAttributes().size() > 0)
				record_attributes.addAll(_record.getRecordAttributes());
			
			record.setRecordattributes(record_attributes);
			
			ArrayList<TerminlogyObject> terms = new ArrayList<TerminlogyObject>();
			
			if (_record.getTerms() != null && _record.getTerms().size() > 0)
				terms.addAll(_record.getTerms());
			
			record.setTerms(terms);
		}
		
		UserProjectAccessPanel assigner = _display.getUserProjectAssigner();
		
		if (! _is_editing)
		{
			if (assigner.getAssignedProjects() != null && assigner.getAssignedProjects().size() > 0)			
				record.setProjects(assigner.getAssignedProjects());			
			else
			{
				AlertBox.show(_i18n.getConstants().recordEdit_error_noProjects());
				return null;
			}
		}
		else
		{
			ArrayList<Project> projects = new ArrayList<Project>();
			
			if (assigner.getAssignedProjects() != null)
			{
				projects.addAll(assigner.getAssignedProjects());
				projects.add(0, _display.getDefaultProjectListBox().getSelectedItem());
			}
			else
			{
				projects = new ArrayList<Project>();
				projects.add( _display.getDefaultProjectListBox().getSelectedItem());
			}
			
			record.setProjects(projects);
		}
					
		VerticalPanel record_attribute_panel = _display.getRecordAttributesPanel();
		int record_attributes = record_attribute_panel.getWidgetCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			HorizontalPanel widget_panel = (HorizontalPanel) record_attribute_panel.getWidget(i);
			
			EditableWidget widget = (EditableWidget)widget_panel.getWidget(0);
			TerminlogyObject terminology_object = widget.getTerminologyObject();
						
			if (! validateFieldDataTypes(widget))
			{
				AlertBox.show(_i18n.getMessages().recordEdit_error_validValue(terminology_object.getFieldName()));
				return null;
			}
			
			if (! widget.getText().isEmpty())
			{				
				terminology_object.setCharData(widget.getText());	
				
				if (terminology_object.getResourceId() == -1)
					record.addRecordAttribute((RecordAttribute) terminology_object);
				else
					record.updateRecordAttribute((RecordAttribute) terminology_object);
			}
		}
		
		AppProperties props = _access_controller.getAppProperties();
				
		VerticalPanel terms_panel = _display.getTermsPanel();
		int terms = terms_panel.getWidgetCount();
		
		Term term = null;
		
		for (int i = 0; i < terms; i++)
		{
			HorizontalPanel widget_panel = (HorizontalPanel) terms_panel.getWidget(i);
			
			EditableWidget widget = (EditableWidget)widget_panel.getWidget(0);
			TerminlogyObject terminology_object = widget.getTerminologyObject();
												
			if (terminology_object.isSortIndex(props.getSortIndexField()) ||
				terminology_object.isMandatory())
			{
				if (widget.getText().isEmpty())
				{
					AlertBox.show(_i18n.getMessages().recordEdit_error_source_value(terminology_object.getFieldName(), terminology_object.getFieldName()));
					return null;
				}
			}
			
			if (terminology_object.getFieldId() == _source_field.getFieldId())
			{
				if (widget.getText().isEmpty())
				{
					AlertBox.show(_i18n.getMessages().recordEdit_error_source_value(_source_field.getFieldName(), _source_field.getFieldName()));
					return null;
				}
			}
			
			if (terminology_object.isPresetAttribute() || terminology_object.isPresetSubAttribute())
			{
				if (widget.getText().equals("None"))
					continue;
			}
			else
			{
				if (widget.getText().isEmpty())
					continue;
			}
			
			if (! validateFieldDataTypes(widget))
			{
				AlertBox.show(_i18n.getMessages().recordEdit_error_validValue(terminology_object.getFieldName()));
				return null;
			}
			
			terminology_object.setCharData(widget.getText());
			
			if (terminology_object instanceof Term)
			{						
				term = (Term) terminology_object;
				
				if (terminology_object.getResourceId() == -1)
				{
					if (term.getTermAttributes() != null)
						term.getTermAttributes().clear();
					
					record.addTerm(term);
				}	
				else
					record.updateTerm(term);
			}
			else if (terminology_object instanceof TermAttribute)
			{						
				if (terminology_object.getResourceId() == -1)
				{
					if (terminology_object instanceof Synonym)
					{
						if (((Synonym) terminology_object).getSynonymAttributes() != null)
							((Synonym) terminology_object).getSynonymAttributes().clear();						
					}
						
					if (terminology_object instanceof SynonymAttribute)						
						record.addSynonymAttributeToTerm(term, (SynonymAttribute) terminology_object);					
					else
						record.addTermAttributeToTerm(term, (TermAttribute) terminology_object);
				}
				else
				{
					if (terminology_object instanceof SynonymAttribute)
						record.addSynonymAttributeToTerm(term, (SynonymAttribute) terminology_object);
					else
						record.updateTermAttributeToTerm(term, (TermAttribute) terminology_object);
				}
			}
		}
		
		return record;
	}
	
	private boolean validateFieldDataTypes(EditableWidget widget)
	{
		if (widget.getTerminologyObject().isPlainText())
		{				
			if (! FieldDataValidator.validateText(widget.getText()))
				return false;				
		}
		else if (widget.getTerminologyObject().isInteger())
		{
			if (! FieldDataValidator.validateInteger(widget.getText()))
				return false;		
		}
		else if (widget.getTerminologyObject().isFloat())
		{
			if (! FieldDataValidator.validateFloat(widget.getText()))
				return false;		
		}
		else if (widget.getTerminologyObject().isHTMLHyperlink())
		{
			if (! FieldDataValidator.validateHTMLLink(widget.getText()))
				return false;		
		}
		else if (widget.getTerminologyObject().isFormula())
		{
			if (! FieldDataValidator.validateFormula(widget.getText()))
				return false;		
		}
		else if (widget.getTerminologyObject().isXml())
		{
			if (! FieldDataValidator.validateXML(widget.getText()))
				return false;		
		}
		
		return true;
	}
		
	private void unlockRecord()
	{				
		_record_service.unlockRecord(_access_controller.getAuthToken(), _record.getRecordId(), new BusyDialogAsyncCallBack<Void>(null)
		{
			@Override
			public void onComplete(Void result) 
			{						
				RootPanel.get("content").clear();
				
				RecordRenderingPresenter rr_presenter = new RecordRenderingPresenter(new RecordRenderingView(), _record, _source_field, _target_field);		
				rr_presenter.go(RootPanel.get("content"));
				
				rr_presenter.render();		
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}					
		});
	}
	
	private void unlockRecord(final RecordElement element)
	{				
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_record_service.unlockRecord(_access_controller.getAuthToken(), _record.getRecordId(), new BusyDialogAsyncCallBack<Void>(null)
		{
			@Override
			public void onComplete(Void result) 
			{	
				TermBrowserControllerPresenter termbrowser_controller = _access_controller.getTermBrowserController();
				termbrowser_controller.enableNavigation();
				
				event_bus.fireEvent(new ResetNavigationEvent(element.getRecordId(), false));
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}					
		});
	}
	
	public void handleDuplicateFound(final RecordElement element, String search_prompt)
	{	
		if (_is_editing)
		{
			if (_record.getRecordId() == element.getRecordId())
				return;
		}
		
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		ConfirmBox.show(_i18n.getMessages().recordEdit_duplicate(search_prompt), false, true, new ConfirmCallback()
		{	
			@Override
			public void onConfirm() 
			{
				AttachedPanelPositionUtility.removePosition(_display.getControlsPanel());
				
				if (_is_editing)
					unlockRecord(element);
				else
					event_bus.fireEvent(new ResetNavigationEvent(element.getRecordId(), false));
			}

			@Override
			public void onCancel() 
			{
				// Nothing to do
			}
		});	
	}
	
	private void resetEditorPanel()
	{
		_display.getControlsPanel().hide();
		
		HorizontalPanel term_details_panel = _display.getTermBaseDetailsPanel();
		term_details_panel.setVisible(false);
		
		VerticalPanel user_project_panel = _display.getUserProjectPanel();
		user_project_panel.setVisible(false);
		
		UserProjectAccessPanel assigner = _display.getUserProjectAssigner();
		assigner.reset();
		
		HorizontalPanel prompt_panel = _display.getPromptPanel();
		prompt_panel.setVisible(false);
		
		VerticalPanel edit_details_panel = _display.getEditDetailsPanel();
		edit_details_panel.setVisible(false);
	}
	
	@Override
	public void go(HasWidgets container) 
	{					
		container.add(_display.asWidget());
		
		bind();
	}
	
	public Field getCurrentSearchField()
	{
		return _current_search_field;
	}

	@Override
	public void signOut() 
	{
		_display.getControlsPanel().hide();
	}	
}
