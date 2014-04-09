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
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.TermBrowserControllerPresenter;
import tms2.client.service.FieldService;
import tms2.client.service.FieldServiceAsync;
import tms2.client.service.RecordService;
import tms2.client.service.RecordServiceAsync;
import tms2.client.service.TermBaseService;
import tms2.client.service.TermBaseServiceAsync;
import tms2.client.termbrowser.view.RecordRenderingView;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.HasSignOut;
import tms2.shared.AppProperties;
import tms2.shared.Field;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.Result;
import tms2.shared.Term;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordNavigatorPresenter implements TermBrowserControllerPresenter, HasSignOut
{
	private static Internationalization _i18n = Internationalization.getInstance();	
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static TermBaseServiceAsync _termbase_service = GWT.create(TermBaseService.class);
	private static FieldServiceAsync _field_service = GWT.create(FieldService.class);
	private static RecordServiceAsync _record_service = GWT.create(RecordService.class);
	
	private Display _display = null;
	
	private ControlBarPresenter _control_bar = null;
	private InfoBarPresenter _info_bar = null;
	private SearchResultsPresenter _search_results = null;
	
	private Field _source_field = null;
	private Field _target_field = null;
	
	private Record _record = null;
							
	public interface Display 
	{
		public final String SEARCH_DEFAULT = _i18n.getConstants().search_default();
		public final String SEARCH_FUZZY = _i18n.getConstants().search_fuzzy();
		public final String SEARCH_EXACT = _i18n.getConstants().search_exactmatch();
		public final String SEARCH_WILDCARD = _i18n.getConstants().search_wildcard();
		
		public PushButton getFirstRecordPushButton();
		public Anchor getPreviousRecordAnchor();
		public Label getBrowseLabel();
		public TextBox getBrowserSearchTextBox();
		public Anchor getNextRecordAnchor();
		public PushButton getLastRecordPushButton();
		public PushButton getOpenSearchPushButton();
		public TextBox getSearchTextBox();
		public ListBox getSearchOptionsListBox();
		public Button getSearchButton();
		public PushButton getCloseSearchPushButton();
		public ExtendedListBox<TermBase> getTermBaseListBox();
		public ExtendedListBox<Project> getProjectListBox();
		public ExtendedListBox<Field> getSourceLanguageListBox();
		public ExtendedListBox<Field> getTargetLanguageListBox();
		public Widget asWidget();
	}
	
	private class SearchKeyDownHandler implements KeyDownHandler, ClickHandler
	{		
		private TextBox _txt_search = null;
		private boolean _browser_search = false;
		private ListBox _lst_search_options = null;
		private HandlerManager _event_bus = null;
		
		public SearchKeyDownHandler(TextBox txt_search, boolean browser_search, 
								    ListBox lst_search, HandlerManager event_bus)
		{
			_txt_search = txt_search;
			_browser_search = browser_search; 
			_lst_search_options = lst_search;
			_event_bus = event_bus;
		}
				
		@Override
		public void onKeyDown(KeyDownEvent event) 
		{			
			String search_prompt = _txt_search.getText().trim();
			
			if (validateSearchPrompt(search_prompt) 
					&& event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{					
					String search_type = null;
					
					if (_browser_search)
					{
						if (_access_controller.isGuest())
							_access_controller.getEventBus().fireEvent(new SignOffEvent());
						
						search_type = Display.SEARCH_EXACT;
					}
					else
					{
						_search_results.openSearchResultsPanel();
						search_type = _lst_search_options.getItemText(_lst_search_options.getSelectedIndex());
					}
					
					_event_bus.fireEvent(new SearchEvent(RecordNavigatorPresenter.this, search_prompt, 
														search_type, _browser_search));
				}
		}		
		
		@Override
		public void onClick(ClickEvent event) 
		{		
			String search_prompt = _txt_search.getText().trim();			
			
			if (validateSearchPrompt(search_prompt))
			{
				_search_results.openSearchResultsPanel();
						
				String search_type = _lst_search_options.getItemText(_lst_search_options.getSelectedIndex());
				
				_event_bus.fireEvent(new SearchEvent(RecordNavigatorPresenter.this, search_prompt, 
													 search_type, _browser_search));
			}
		}
		
		private boolean validateSearchPrompt(String prompt)
		{
			if (prompt.isEmpty())
				return false;
			
			if (prompt.equals("*") || prompt.equals("?") ||
				prompt.equals("%") || prompt.equals("_"))
				return false;
												
			prompt = prompt.replaceAll("\\*+", "");
			prompt = prompt.replaceAll("\\?+", "");
			prompt = prompt.replaceAll("\\%+", "");
			prompt = prompt.replaceAll("\\_+", "");
			
			if (prompt.isEmpty())
				return false;
			
			return true;
		}
	}
	
	public RecordNavigatorPresenter(Display display, ControlBarPresenter control_bar, InfoBarPresenter info_bar, SearchResultsPresenter search_results)
	{
		_display = display;
		
		_control_bar = control_bar;
		_info_bar = info_bar;
		_search_results = search_results;
		
		_access_controller.addSignOut(this);
	}
		
	private void bind()
	{		
		setUserMode();								
		populateSourceTargetListBoxes(-1);		
		addSourceListhandler();
		addTargetListHandler();		
		addFirstRecordPushButtonhandler();
		addPreviousRecordAnchorHandler();
		addBrowserSearchTextBoxHandler();
		addNextRecordAnchorHandler();
		addLastRecordPushButtonHandler();
		addOpenSearchPushButtonHandler();
		addTermBaseListBoxHandler();
		addSearchButtonHandler();
		addSearchTextBoxHandler();
		addCloseSearchPushButtonHandler();
	}
	
	private void setUserMode()
	{
		_display.getSearchOptionsListBox().clear();
		
		if (! _access_controller.isGuest())
		{
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_DEFAULT + "...", "default");
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_FUZZY, "fuzzy");
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_EXACT, "exact");
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_WILDCARD, "wildcard");
			
			_display.getFirstRecordPushButton().setVisible(true);
			_display.getPreviousRecordAnchor().setVisible(true);
			_display.getBrowseLabel().setVisible(true);
			_display.getBrowserSearchTextBox().setVisible(true);
			_display.getNextRecordAnchor().setVisible(true);
			_display.getLastRecordPushButton().setVisible(true);
			
			hideSearchControlsPanel();
		}
		else
		{
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_DEFAULT + "...", "default");
			_display.getSearchOptionsListBox().addItem(Display.SEARCH_FUZZY, "fuzzy");
			
			_display.getFirstRecordPushButton().setVisible(false);
			_display.getPreviousRecordAnchor().setVisible(false);
			_display.getBrowseLabel().setVisible(false);
			_display.getBrowserSearchTextBox().setVisible(false);
			_display.getNextRecordAnchor().setVisible(false);
			_display.getLastRecordPushButton().setVisible(false);
			
			showSearchControlsPanel();
		}
		
		_record = null;
	}
				
	private void addSourceListhandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getSourceLanguageListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				ExtendedListBox<Field> lst_src = _display.getSourceLanguageListBox();
				ExtendedListBox<Field> lst_trg = _display.getTargetLanguageListBox();
				
				Field source_field = lst_src.getSelectedItem();
				
				if (source_field != null)
				{
					lst_trg.setEnabled(true);
					Field _target_field = lst_trg.getSelectedItem();
					
					if (_target_field != null)
					{
						if (_source_field.getFieldId() == _target_field.getFieldId())
							setTargetField(null);
					}
					
					if (source_field.getFieldId() != _source_field.getFieldId())	
					{												
						setSourceField(source_field);
						
						long record_id = -1;
						
						if (_record != null)
						{
							ArrayList<TerminlogyObject> terms = _record.getTerms();
							
							Iterator<TerminlogyObject> iter = terms.iterator();
							while (iter.hasNext())
							{
								Term term = (Term) iter.next();
								if (term.getFieldId() == _source_field.getFieldId())
									record_id = _record.getRecordId();
							}														
						}
						
						event_bus.fireEvent(new ResetNavigationEvent(record_id, true));						
					}	
										
					adjustTargetFieldListBox();
				}
				else
					lst_trg.setEnabled(false);
			}
		});
	}
	
	private void addTargetListHandler()
	{
		_display.getTargetLanguageListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				setTargetField(_display.getTargetLanguageListBox().getSelectedItem());
				
				if (_target_field != null)				
					renderRecord();				
			}
		});
	}
	
	private void addFirstRecordPushButtonhandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getFirstRecordPushButton().addClickHandler(new ClickHandler()
		{		
			@Override
			public void onClick(ClickEvent event) 
			{	
				if (! _access_controller.isGuest())
					event_bus.fireEvent(new ResetNavigationEvent(-1, true));
				else
					event_bus.fireEvent(new SignOffEvent());
			}
		});
	}
	
	private void addPreviousRecordAnchorHandler()
	{		
		_display.getPreviousRecordAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_control_bar.enableStateButtons();
				
				if (! _access_controller.isGuest())
				{
					_record_service.getPreviousRecord(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<Result<Record>>(null)
					{
						@Override
						public void onComplete(Result<Record> result) 
						{
							if (result.getResult() != null)	
							{
								setRecord(result.getResult());
								displayRecord();
							}
							else
								AlertBox.show(result.getMessage());					
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
		});
	}
	
	private void addBrowserSearchTextBoxHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();		
		final TextBox txt_browser_search = _display.getBrowserSearchTextBox();	
				
		txt_browser_search.addKeyDownHandler(new SearchKeyDownHandler(txt_browser_search, 
				   											 true, null, event_bus));
		
		txt_browser_search.addFocusHandler(new FocusHandler()
		{			
			@Override
			public void onFocus(FocusEvent event) 
			{
				InsertPanelPresenter ip_presenter = _control_bar.getInsertPanelPresenter();
				ip_presenter.setFocusTextBase(txt_browser_search);
			}
		});
				
		txt_browser_search.addMouseOverHandler(new MouseOverHandler()
		{			
			@Override
			public void onMouseOver(MouseOverEvent event) 
			{
				txt_browser_search.setTitle(txt_browser_search.getText());
				txt_browser_search.setText("");
			}
		});
		
		txt_browser_search.addMouseOutHandler(new MouseOutHandler() 
		{			
			@Override
			public void onMouseOut(MouseOutEvent event) 
			{
				txt_browser_search.setText(txt_browser_search.getTitle());
				txt_browser_search.setTitle("");
			}
		});
	}
	
	private void addNextRecordAnchorHandler()
	{		
		_display.getNextRecordAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_control_bar.enableStateButtons();
				
				if (! _access_controller.isGuest())
				{
					_record_service.getNextRecord(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<Result<Record>>(null)
					{
						@Override
						public void onComplete(Result<Record> result) 
						{
							if (result.getResult() != null)			
							{
								setRecord(result.getResult());
								displayRecord();
							}
							else
								AlertBox.show(result.getMessage());					
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
		});
	}
	
	private void addLastRecordPushButtonHandler()
	{		
		_display.getLastRecordPushButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				_control_bar.enableStateButtons();
				
				if (! _access_controller.isGuest())
				{
					_record_service.getLastRecord(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<Result<Record>>(null)
					{
						@Override
						public void onComplete(Result<Record> result) 
						{
							if (result.getResult() != null)		
							{
								setRecord(result.getResult());
								displayRecord();
							}
							else
								AlertBox.show(result.getMessage());					
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
		});
	}
	
	private void addTermBaseListBoxHandler()
	{
		_display.getTermBaseListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
				ExtendedListBox<Project> lst_project = _display.getProjectListBox();
				
				TermBase termbase = lst_termbase.getSelectedItem();
				
				if (termbase != null)
				{
					lst_project.setEnabled(true);
					lst_project.clear();
					lst_project.addItem(_i18n.getConstants().recordEdit_selectedProject(), "-1", null);
					
					ArrayList<Project> projects = termbase.getProjects();
					
					Iterator<Project> iter = projects.iterator();
					while (iter.hasNext())
					{
						Project project = iter.next();
						
						lst_project.addItem(project.getProjectName(), project.getProjectId() + "", project);					
					}
				}
				else
				{
					lst_project.setEnabled(false);
					lst_project.setSelectedIndex(0);
				}
			}
		});
	}
		
	private void addOpenSearchPushButtonHandler()
	{
		_display.getOpenSearchPushButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{			
				showSearchControlsPanel();
			}
		});
	}
	
	private void addSearchButtonHandler()
	{
		HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getSearchButton().addClickHandler(new SearchKeyDownHandler(_display.getSearchTextBox(), 
												   false, _display.getSearchOptionsListBox(), event_bus));
	}
	
	private void addSearchTextBoxHandler()
	{
		final TextBox txt_search = _display.getSearchTextBox();
		
		HandlerManager event_bus = _access_controller.getEventBus();
		
		txt_search.addKeyDownHandler(new SearchKeyDownHandler(_display.getSearchTextBox(), 
														false, _display.getSearchOptionsListBox(), event_bus));
		
		txt_search.addFocusHandler(new FocusHandler()
		{			
			@Override
			public void onFocus(FocusEvent event) 
			{
				InsertPanelPresenter ip_presenter = _control_bar.getInsertPanelPresenter();
				ip_presenter.setFocusTextBase(txt_search);
			}
		});
	}
		
	private void addCloseSearchPushButtonHandler()
	{
		_display.getCloseSearchPushButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				hideSearchControlsPanel();
			}
		});	
	}
	
	private void showSearchControlsPanel()
	{		
		_display.getSearchTextBox().setVisible(true);
		_display.getSearchOptionsListBox().setVisible(true);
		_display.getSearchButton().setVisible(true);
		_display.getCloseSearchPushButton().setVisible(true);
		_display.getTermBaseListBox().setVisible(true);
		_display.getProjectListBox().setVisible(true);
		
		_termbase_service.getAllDatabases(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<TermBase>>(null)
		{
			@Override
			public void onComplete(ArrayList<TermBase> result) 
			{
				ExtendedListBox<TermBase> lst_termbase = _display.getTermBaseListBox();
				ExtendedListBox<Project> lst_project = _display.getProjectListBox();
				
				lst_termbase.clear();
				lst_project.clear();
				
				lst_termbase.setEnabled(false);
				lst_project.setEnabled(false);
				
				lst_termbase.addItem(_i18n.getConstants().recordEdit_selectDB(), -1, null);
				lst_project.addItem(_i18n.getConstants().recordEdit_selectedProject(), -1, null);
				
				if (result != null && result.size() > 0)
				{
					Iterator<TermBase> iter = result.iterator();
					while (iter.hasNext())
					{
						TermBase termbase = iter.next();
						
						lst_termbase.addItem(termbase.getTermdbname(), termbase.getTermdbid(), termbase);;
					}
					
					lst_termbase.setEnabled(true);
				}						
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}			
		});
	}
	
	private void hideSearchControlsPanel()
	{
		_display.getSearchTextBox().setVisible(false);
		_display.getSearchOptionsListBox().setVisible(false);
		_display.getSearchButton().setVisible(false);
		_display.getCloseSearchPushButton().setVisible(false);
		_display.getTermBaseListBox().setVisible(false);
		_display.getProjectListBox().setVisible(false);
		
		_search_results.closeSearchResultsPanel();
	}
	
	private void adjustTargetFieldListBox()
	{				
		ExtendedListBox<Field> lst_trg = _display.getTargetLanguageListBox();
		
		lst_trg.clear();
		lst_trg.addItem(_i18n.getConstants().recordBrowse_target(), "-1", null);
		
		for (Field field : _display.getSourceLanguageListBox().getItems())
		{
			if (field == null)
				continue;
			
			if(field.getFieldId() != _source_field.getFieldId())
				lst_trg.addItem(field.getFieldName(), field.getFieldId(), field);
		}
	}
		
	private void renderRecord()
	{
		RootPanel.get("content").clear();
		
		RecordRenderingPresenter rr_presenter = new RecordRenderingPresenter(new RecordRenderingView(), _record, _source_field, _target_field);		
		rr_presenter.go(RootPanel.get("content"));
		
		rr_presenter.render();
	}
		
	private void setNavigationControlsState()
	{
		PushButton btn_first_rec = _display.getFirstRecordPushButton();
		Anchor anc_previous_rec = _display.getPreviousRecordAnchor();
		TextBox txt_browser = _display.getBrowserSearchTextBox();
		Anchor anc_next_rec = _display.getNextRecordAnchor();
		PushButton btn_last_rec = _display.getLastRecordPushButton();
		
		txt_browser.setEnabled(true);
		
		if (_record.getPreviousTerm() != null)
		{
			//Important note: The order of the following two lines is important because of a bug in PushButton. 
			//If you set the text first, and THEN enable the button, the text becomes empty!
			anc_previous_rec.setVisible(true);
			anc_previous_rec.setText(_record.getPreviousTerm().getCharData());
			btn_first_rec.setEnabled(true);
		}
		else
		{
			anc_previous_rec.setVisible(false);
			btn_first_rec.setEnabled(false);
		}
				
		if (_record.getNextTerm() != null)
		{
			//Important note: The order of the following two lines is important because of a bug in PushButton. 
			// If you set the text first, and THEN enable the button, the text becomes empty!
			anc_next_rec.setVisible(true);
			anc_next_rec.setText(_record.getNextTerm().getCharData());
			btn_last_rec.setEnabled(true);
		}
		else
		{
			anc_next_rec.setVisible(false);
			btn_last_rec.setEnabled(false);
		}
	}
	
	private void setSourceField(Field field)
	{
		_source_field = field;
		_control_bar.setSourceField(_source_field);
		_info_bar.setSourceField(_source_field);
	}
	
	private void setTargetField(Field field)
	{
		_target_field = field;
		_control_bar.setTargetField(field);
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		container.add(_display.asWidget());
		
		bind();
	}
	
	@Override
	public void displayRecord()
	{				
		PushButton btn_first_rec = _display.getFirstRecordPushButton();
		Anchor anc_previous_rec = _display.getPreviousRecordAnchor();
		TextBox txt_browser = _display.getBrowserSearchTextBox();
		Anchor anc_next_rec = _display.getNextRecordAnchor();
		PushButton btn_last_rec = _display.getLastRecordPushButton();
		
		if (_record != null)
		{
			if (! _access_controller.isGuest())
			{
				setNavigationControlsState();
							
				AppProperties props = _access_controller.getAppProperties();
				
				if (! _record.getIsSynonym())
				{
					Term sort_index = null;
					
					sort_index = _record.getCustomSortIndexTerm(_source_field);				
					
					// The sorting index may be null here if the current user is not allowed
					// to view the sorting index. Can take that to mean that the user may not view this record.
					if (sort_index == null)
						sort_index = _record.getSortIndexTerm(props.getSortIndexField());
					
					if (sort_index != null)
					{		
						if (! _record.getIsSynonym())
							txt_browser.setText(sort_index.getCharData());
						else
							txt_browser.setText(_record.getSynonym());
						
						txt_browser.setTitle(txt_browser.getText());
						
						renderRecord();
					}
					else						
						AlertBox.show(_i18n.getMessages().recordBrowse_noRights(Long.toString(_record.getRecordId())));							
				}
			}	
			else
				renderRecord();
		}
		else
		{
			anc_previous_rec.setVisible(false);
			btn_first_rec.setEnabled(false);
			txt_browser.setText("");
			txt_browser.setEnabled(false);
			anc_next_rec.setVisible(false);
			btn_last_rec.setEnabled(false);
						
			RootPanel.get("content").clear();
		}
	}
	
	@Override
	public Display getDisplay()
	{
		return _display;
	}
	
	@Override
	public void populateSourceTargetListBoxes(final long recordid)
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_field_service.getIndexFieldsInUse(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<ArrayList<Field>>(null)
		{
			@Override
			public void onComplete(ArrayList<Field> result) 
			{
				ExtendedListBox<Field> src_lst = _display.getSourceLanguageListBox();
				src_lst.clear();
				
				ExtendedListBox<Field> trg_lst = _display.getTargetLanguageListBox();
				trg_lst.clear();
				
				src_lst.addItem(_i18n.getConstants().recordBrowse_source(), -1, null);
				trg_lst.addItem(_i18n.getConstants().recordBrowse_target(), -1, null);
				
				ArrayList<Field> fields = result;
				
				if (fields == null || fields.size() == 0)
				{
					src_lst.setEnabled(false);
					src_lst.setEnabled(false);
				}
				else
				{		
					boolean is_source_set = false;
					AppProperties props = _access_controller.getAppProperties();
					
					Iterator<Field> iter = fields.iterator();
					while (iter.hasNext())
					{
						Field field = iter.next();
						
						if (_source_field == null)
						{
							if (field.isSortIndex(props.getSortIndexField()))
							{
								setSourceField(field);
								is_source_set = true;
							}
						}
						
						src_lst.addItem(field.getFieldName(), field.getFieldId(), field);		
					}
					
					// Check if a new source field was set. If not
					// display the correct field in the list.
					if (! is_source_set)
					{
						int index = 1;
						iter = fields.iterator();
						
						while (iter.hasNext())
						{
							Field field = iter.next();
							
							if (field.getFieldId() == getSourceField().getFieldId())
							{
								src_lst.setItemSelected(index, true);										
								break;
							}
							
							index++;
						}
					}
					
					setTargetField(trg_lst.getItemAtIndex(0));
					
					src_lst.setEnabled(true);
					trg_lst.setEnabled(false);
					
					if (! _access_controller.isGuest())
						event_bus.fireEvent(new ResetNavigationEvent(recordid, true));							
				}
			}

			@Override
			public void onError(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}
		});	
	}
	
	@Override
	public void setRecord(Record record)
	{		
		_record = record;
		_control_bar.setRecord(_record);
		_info_bar.setRecord(_record);
	}
			
	@Override
	public Field getSourceField()
	{
		return _source_field;
	}
	
	@Override
	public ControlBarPresenter getControlBarPresenter() 
	{
		return _control_bar;
	}
	
	@Override
	public InfoBarPresenter getInfoBarPresenter()
	{
		return _info_bar;
	}	
	
	@Override
	public SearchResultsPresenter getSearchResultsPresenter()
	{
		return _search_results;
	}
		
	@Override
	public void disableNavigation()
	{
		PushButton btn_first_rec = _display.getFirstRecordPushButton();				
		Anchor anc_previous_rec = _display.getPreviousRecordAnchor();
		TextBox txt_browser = _display.getBrowserSearchTextBox();
		Anchor anc_next_rec = _display.getNextRecordAnchor();
		PushButton btn_last_rec = _display.getLastRecordPushButton();
		
		ExtendedListBox<Field> lst_source = _display.getSourceLanguageListBox();
		ExtendedListBox<Field> lst_target = _display.getTargetLanguageListBox();
		
		PushButton btn_search = _display.getOpenSearchPushButton();
		
		anc_previous_rec.setVisible(false);
		btn_first_rec.setEnabled(false);
		
		txt_browser.setEnabled(false);
		
		anc_next_rec.setVisible(false);
		btn_last_rec.setEnabled(false);
		
		lst_source.setEnabled(false);
		lst_target.setEnabled(false);
		
		btn_search.setEnabled(false);
		
		hideSearchControlsPanel();
		
		_search_results.disable();
	}
	
	@Override
	public void enableNavigation()
	{
		PushButton btn_first_rec = _display.getFirstRecordPushButton();				
		Anchor anc_previous_rec = _display.getPreviousRecordAnchor();
		TextBox txt_browser = _display.getBrowserSearchTextBox();
		Anchor anc_next_rec = _display.getNextRecordAnchor();
		PushButton btn_last_rec = _display.getLastRecordPushButton();
		
		ExtendedListBox<Field> lst_source = _display.getSourceLanguageListBox();
		ExtendedListBox<Field> lst_target = _display.getTargetLanguageListBox();
		
		PushButton btn_search = _display.getOpenSearchPushButton();
		
		if (_record != null)		
			setNavigationControlsState();
		else
		{
			btn_first_rec.setEnabled(false);
			anc_previous_rec.setVisible(false);
			
			txt_browser.setEnabled(false);
			
			anc_next_rec.setVisible(false);
			btn_last_rec.setEnabled(false);			
		}		
		
		lst_source.setEnabled(true);
		
		if (lst_source.getSelectedItem() != null)
			lst_target.setEnabled(true);
		else
			lst_target.setEnabled(false);
		
		btn_search.setEnabled(true);
	}
	
	@Override
	public void signOut() 
	{
		setUserMode();
		setRecord(null);
	}
}
