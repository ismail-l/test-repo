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

package tms2.client.termbrowser;

import java.util.ArrayList;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.ExportEvent;
import tms2.client.event.FilterEvent;
import tms2.client.event.ResetNavigationEvent;
import tms2.client.event.SearchEvent;
import tms2.client.event.SignOffEvent;
import tms2.client.event.TermBrowserEvent;
import tms2.client.event.TermBrowserEventHandler;
import tms2.client.event.UpdateRecordEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.presenter.TermBrowserControllerPresenter;
import tms2.client.service.ExportService;
import tms2.client.service.ExportServiceAsync;
import tms2.client.service.FilterService;
import tms2.client.service.FilterServiceAsync;
import tms2.client.service.RecordService;
import tms2.client.service.RecordServiceAsync;
import tms2.client.termbrowser.presenter.ControlBarPresenter;
import tms2.client.termbrowser.presenter.ExportPresenter;
import tms2.client.termbrowser.presenter.InfoBarPresenter;
import tms2.client.termbrowser.presenter.RecordEditorPresenter;
import tms2.client.termbrowser.presenter.RecordNavigatorPresenter;
import tms2.client.termbrowser.presenter.SearchResultsPresenter;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.SuccessBox;
import tms2.shared.ExportType;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.RecordElement;
import tms2.shared.Result;
import tms2.shared.TermBase;
import tms2.shared.wrapper.RecordDetailsWrapper;
import tms2.shared.wrapper.RecordRecordElementWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author I. Lavangee
 *
 */
public class EventBusManager 
{	
	private static RecordServiceAsync _record_service = GWT.create(RecordService.class);
	private static FilterServiceAsync _filter_service = GWT.create(FilterService.class);
	private static ExportServiceAsync _export_service = GWT.create(ExportService.class);
			
	public static void manageTermBrowserEvents()
	{
		final Internationalization i18n = Internationalization.getInstance();
		
		final AccessController access_controller = AccessController.getInstance();;
		final HandlerManager event_bus = access_controller.getEventBus();
			
		event_bus.addHandler(TermBrowserEvent.TYPE, new TermBrowserEventHandler()
		{					
			@Override
			public void resetNavigation(TermBrowserEvent event) 
			{
				final TermBrowserControllerPresenter tmb_presenter = (TermBrowserControllerPresenter) access_controller.getTermBrowserController();
				
				final InfoBarPresenter ib_presenter = tmb_presenter.getInfoBarPresenter();
				
				Field source_field = tmb_presenter.getSourceField();
				
				_record_service.resetBrowser(access_controller.getAuthToken(), source_field, 
											((ResetNavigationEvent)event).getCurrentRecordId(), 
											((ResetNavigationEvent)event).shouldRefresh(), 
											new BusyDialogAsyncCallBack<Result<RecordDetailsWrapper>>(null) 
				{
					@Override
					public void onComplete(Result<RecordDetailsWrapper> result) 
					{
						if (result.getResult() != null)
						{							
							RecordDetailsWrapper record_details = result.getResult();
														
							ib_presenter.setNumberOfRecord(record_details.getNumberOfRecords());
							
							ControlBarPresenter cb_presenter = tmb_presenter.getControlBarPresenter();
							
							if (record_details.isFilter())
							{
								ib_presenter.setFilterSetIcon();								
								cb_presenter.toggleExportFunction(true);
							}
							else
							{
								ib_presenter.setFilterNotSetIcon();
								cb_presenter.toggleExportFunction(false);
							}
							
							cb_presenter.enableStateButtons();
							
							tmb_presenter.setRecord(record_details.getRecord());
							tmb_presenter.displayRecord();									
						}
						else
						{
							AlertBox.show(result.getMessage());
							ib_presenter.setNumberOfRecord(0);
						}
					}

					@Override
					public void onError(Throwable caught) 
					{
						ErrorBox.ErrorHandler.handle(caught);
					}					
				});	
			}
			
			private Presenter _presenter = null;
			private Field _source_field = null;
			private String _search_prompt = null;
			private String _search_type = null;
			private boolean _browse_textbox_search = false;
			private long _termbase_id = -1;
			private long _project_id = -1;
			private SearchEvent _search_event = null;
			
			@Override
			public void search(TermBrowserEvent event) 
			{			
				_search_event = ((SearchEvent)event);
				
				if (_search_event.getPresenter() instanceof RecordNavigatorPresenter)
				{
					_presenter = (RecordNavigatorPresenter)_search_event.getPresenter();
					RecordNavigatorPresenter.Display display = ((RecordNavigatorPresenter) _presenter).getDisplay();
					
					_source_field = ((RecordNavigatorPresenter) _presenter).getSourceField();
																				
					_termbase_id = -1;
					_project_id = -1;
					
					TermBase termbase = (TermBase) display.getTermBaseListBox().getSelectedItem();
					
					if (termbase != null)
						_termbase_id = termbase.getTermdbid();
					
					Project project = (Project) display.getProjectListBox().getSelectedItem();
					
					if (project != null)
						_project_id = project.getProjectId();
				}
				else if (_search_event.getPresenter() instanceof RecordEditorPresenter)
				{
					_presenter = (RecordEditorPresenter)_search_event.getPresenter();
					_source_field = ((RecordEditorPresenter) _presenter).getCurrentSearchField();
				}
				
				_browse_textbox_search = _search_event.isBrowseTextBoxSearch();
				_search_prompt = _search_event.getSearchPrompt();
				_search_type = _search_event.getSearchType();
				
				AsyncCallback<Result<RecordRecordElementWrapper>> search_callback = null;
				
				if (_search_event.getPresenter() instanceof RecordEditorPresenter)
				{				
					search_callback = new AsyncCallback<Result<RecordRecordElementWrapper>>() 
					{						
						@Override
						public void onSuccess(Result<RecordRecordElementWrapper> result) 
						{
							if (result.getResult() != null)
							{
								RecordRecordElementWrapper wrapper = result.getResult();
								
								ArrayList<RecordElement> elements = wrapper.getRecordElements();
								
								if (elements != null && elements.size() > 0)									
									((RecordEditorPresenter)_presenter).handleDuplicateFound(elements.get(0), _search_prompt);									
							}							
						}
						
						@Override
						public void onFailure(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);							
						}
					};
				}
				else
				{
					search_callback = new BusyDialogAsyncCallBack<Result<RecordRecordElementWrapper>>(null)
					{
						@Override
						public void onComplete(Result<RecordRecordElementWrapper> result) 
						{														
							if (result.getResult() != null)
							{
								RecordRecordElementWrapper wrapper = result.getResult();
								
								if (_browse_textbox_search)
								{																
									ArrayList<RecordElement> elements = wrapper.getRecordElements();
																									
									if (_search_event.getPresenter() instanceof RecordNavigatorPresenter)
									{		
										Record record = wrapper.getRecord();
										
										if (record != null)
										{
											((TermBrowserControllerPresenter) _presenter).setRecord(record);
											
											if (elements == null || elements.size() == 0)
											{																			
												AlertBox.show(i18n.getMessages().recordBrowse_search_noMatch(_search_prompt));
																						
												((TermBrowserControllerPresenter) _presenter).displayRecord();
											}
											else									
												((TermBrowserControllerPresenter) _presenter).displayRecord();		
										}
										else
										{
											AlertBox.show(i18n.getMessages().recordBrowser_search_error(_search_prompt));
											((TermBrowserControllerPresenter) _presenter).displayRecord();
										}
									}									
								}
								else
								{
									ArrayList<RecordElement> elements = wrapper.getRecordElements();
																											
									SearchResultsPresenter search_results = ((TermBrowserControllerPresenter) _presenter).getSearchResultsPresenter();
									search_results.populateSearchResults(elements, _search_prompt, _search_type);
																		
								}
							}
							else
								AlertBox.show(result.getMessage());
							
						}

						@Override
						public void onError(Throwable caught) 
						{
							ErrorBox.ErrorHandler.handle(caught);							
						}						
					};
				}
				
				_record_service.searchRecords(access_controller.getAuthToken(), _source_field, _search_prompt, 
											  _search_type, _browse_textbox_search, _termbase_id, _project_id, search_callback);
			
			}
			
			@Override
			public void updateRecord(TermBrowserEvent event) 
			{
				if (! access_controller.isGuest())
				{										
					Record record = ((UpdateRecordEvent)event).getRecord();
					final boolean is_archiving = ((UpdateRecordEvent)event).isArchiving();				
					
					final TermBrowserControllerPresenter tmb_presenter = (TermBrowserControllerPresenter) access_controller.getTermBrowserController();
					
					_record_service.updateRecord(access_controller.getAuthToken(), record, 
												 is_archiving, new BusyDialogAsyncCallBack<Result<Long>>(null)
					{
						@Override
						public void onComplete(Result<Long> result) 
						{
							if (result.getResult() > -1)	
							{		
								SuccessBox.show(result.getMessage());
																				
								if (is_archiving)
									// Reset the term browser to the first record
									tmb_presenter.populateSourceTargetListBoxes(-1);
								else							
									// Reload source and target listboxes and also reset termbrowser to the 
									// record id updated.
									tmb_presenter.populateSourceTargetListBoxes(result.getResult());
								
								ControlBarPresenter cb_presenter = tmb_presenter.getControlBarPresenter();
								cb_presenter.enableStateButtons();
																							
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
					event_bus.fireEvent(new SignOffEvent());			
			}
			
			@Override
			public void setFilter(TermBrowserEvent event) 
			{
				Filter filter = ((FilterEvent)event).getFilter();
				
				_filter_service.setFilter(access_controller.getAuthToken(), filter, new BusyDialogAsyncCallBack<Void>(null)						
				{
					@Override
					public void onComplete(Void result) 
					{
						if (! access_controller.isGuest())
							event_bus.fireEvent(new ResetNavigationEvent(-1, true));
					}

					@Override
					public void onError(Throwable caught) 
					{
						ErrorBox.ErrorHandler.handle(caught);
					}
				});					
			}
						
			@Override
			public void generateDocument(TermBrowserEvent event) 
			{
				if (! access_controller.isGuest())
				{
					final Presenter presenter = ((ExportEvent)event).getPresenter();
					final ExportPresenter.Display display = ((ExportPresenter)presenter).getDisplay();
					ExportType export_type = ((ExportEvent)event).getExportType();
					String filename = ((ExportEvent)event).getFileName();
										
					_export_service.generateExportDocument(access_controller.getAuthToken(), export_type, 
														   filename, 
														   new BusyDialogAsyncCallBack<Result<Boolean>>(null)
					{
						@Override
						public void onComplete(Result<Boolean> result) 
						{
							if (result.getResult())							
								display.getDownloadButton().setEnabled(true);							
							else
							{
								display.getDownloadButton().setEnabled(false);
								AlertBox.show(result.getMessage());
							}							
						}
	
						@Override
						public void onError(Throwable caught) 
						{
							display.getDownloadButton().setEnabled(false);
							ErrorBox.ErrorHandler.handle(caught);						
						}					
					});
				}
				else
					event_bus.fireEvent(new SignOffEvent());				
			}
		});
	}
}
