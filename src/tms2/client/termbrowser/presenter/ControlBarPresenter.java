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
import tms2.client.event.FilterEvent;
import tms2.client.event.SignOffEvent;
import tms2.client.event.UpdateRecordEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.service.AccessRightService;
import tms2.client.service.AccessRightServiceAsync;
import tms2.client.termbrowser.view.ExportView;
import tms2.client.termbrowser.view.FilterView;
import tms2.client.termbrowser.view.InsertPanelView;
import tms2.client.termbrowser.view.RecordEditorView;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ConfirmBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.FixedPanel;
import tms2.client.widgets.HasSignOut;
import tms2.shared.Field;
import tms2.shared.Record;
import tms2.shared.wrapper.RecordEditDetailsWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ControlBarPresenter implements Presenter, HasSignOut, ResizeHandler
{
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static AccessRightServiceAsync _access_right_service = GWT.create(AccessRightService.class);
		
	private Display _display = null;
		
	private Field _source_field = null;
	private Field _target_field = null;
	
	private Record _record = null;
	
	private FilterPresenter _f_presenter = null;
	private InsertPanelPresenter _ip_presenter = null;
	private ExportPresenter _e_presenter = null;
	
	private ArrayList<Button> _state_buttons = null;
			
	public interface Display
	{
		public Button getAddButton();
		public Button getEditButton();
		public Button getDeleteButton();
		public Button getFilterButton();
		public Button getExportButton();
		public Button getInsertButton();
		public Widget asWidget();
	}
	
	public ControlBarPresenter(Display display)
	{
		_display = display;
								
		_access_controller.addSignOut(this);
		Window.addResizeHandler(this);
	}
		
	private void bind()
	{
		setButtons();
		
		addAddButtonHandler();
		addEditButtonHandler();
		addDeleteButtonHandler();
		addFilterButtonHandler();
		addExportButtonHandler();
		addInsertButtonHandler();
	}
	
	private void setButtons()
	{	
		FixedPanel fixed_panel = (FixedPanel)_display.asWidget();
		
		_state_buttons = new ArrayList<Button>();
		
		if (_access_controller.isGuest())
		{
			_display.getAddButton().setVisible(false);
			_display.getEditButton().setVisible(false);
			_display.getDeleteButton().setVisible(false);
			_display.getFilterButton().setVisible(false);
			_display.getExportButton().setVisible(false);
			
			fixed_panel.setWidth("35%");
		}
		else 
		{
			if (! _access_controller.hasAdminRights())	
			{
				_display.getDeleteButton().setVisible(false);
				
				_state_buttons.add(_display.getAddButton());
				_state_buttons.add(_display.getEditButton());
				_state_buttons.add(_display.getFilterButton());
				_state_buttons.add(_display.getExportButton());										
			}
			else
			{
				_state_buttons.add(_display.getAddButton());
				_state_buttons.add(_display.getEditButton());
				_state_buttons.add(_display.getDeleteButton());		
				_state_buttons.add(_display.getFilterButton());
				_state_buttons.add(_display.getExportButton());									
			}
			
			fixed_panel.setWidth("60%");
		}
		
		_display.getExportButton().setEnabled(false);
	}
	
	private void addAddButtonHandler()
	{		
		_display.getAddButton().addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				disableStateButtons();
			
				edit(_display.getAddButton(), false);
			}
		});
	}
	
	private void addEditButtonHandler()
	{		
		_display.getEditButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				disableStateButtons();
			
				edit(_display.getEditButton(), true);
			}
		});
	}
	
	private void addDeleteButtonHandler()
	{
		final HandlerManager event_bus= _access_controller.getEventBus();
		
		_display.getDeleteButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (_record != null)
				{
					ConfirmBox.show(_i18n.getConstants().recordBrowse_confirmDelete(), new ClickHandler()
					{						
						@Override
						public void onClick(ClickEvent event) 
						{							
							event_bus.fireEvent(new UpdateRecordEvent(_record, true));
						}
					});					
				}
				else
					AlertBox.show(_i18n.getConstants().record_delete());
			}
		});
	}
	
	private void addFilterButtonHandler()
	{		
		_display.getFilterButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{				
				disableStateButtons();
				
				if (! _f_presenter.hasRetrievedDetails())
					_f_presenter.retrieveFilterDetails();
				
				_f_presenter.show();				
			}
		});
	}
	
	private void addExportButtonHandler()
	{		
		_display.getExportButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				disableStateButtons();
				
				if (! _e_presenter.hasRetrievedDetails())
					_e_presenter.retrieveExportDetails();
				
				_e_presenter.show();				
			}
		});
	}
	
	private void addInsertButtonHandler()
	{
		_display.getInsertButton().addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_ip_presenter.show();
			}
		});
	}
		
	private void edit(final Button btn_parent, final boolean is_editing)
	{
		long record_id = -1;
		if (is_editing)
		{
			if (_record != null)
				record_id = _record.getRecordId();
			else
			{
				AlertBox.show(_i18n.getConstants().record_edit());
				enableStateButtons();
				return;
			}
		}
		
		if (! _access_controller.isGuest())
		{
			_access_right_service.getRecordEditDetails(_access_controller.getAuthToken(), record_id, is_editing, new BusyDialogAsyncCallBack<RecordEditDetailsWrapper>(null)
			{
				@Override
				public void onComplete(RecordEditDetailsWrapper result) 
				{										
					RecordEditorPresenter re_presenter = new RecordEditorPresenter(new RecordEditorView(btn_parent), 
																				   _record,																	   
																				   _source_field, 
																				   _target_field,
																				   is_editing, 
																				   _ip_presenter, 
																				   _f_presenter.isFilterSet(),
																				   _state_buttons);
					
					if (re_presenter.validateRecordEditDetails(result))
					{
						RootPanel.get("content").clear();
						re_presenter.go(RootPanel.get("content"));	
						
						re_presenter.edit();
					}
					else
						enableStateButtons();
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
			
	private void disableStateButtons()	
	{
		Iterator<Button> iter = _state_buttons.iterator();
		while (iter.hasNext())
		{
			Button button = iter.next();
			button.setEnabled(false);
		}
	}
	
	public void toggleExportFunction(boolean toggle)
	{
		_display.getExportButton().setEnabled(toggle);
	}
	
	public void enableStateButtons()	
	{
		Iterator<Button> iter = _state_buttons.iterator();
		while (iter.hasNext())
		{
			Button button = iter.next();
			
			if (button.getText().equals(_i18n.getConstants().filter_export()))
			{
				if (_f_presenter.isFilterSet())
					button.setEnabled(true);
				else
					button.setEnabled(false);
			}
			else
				button.setEnabled(true);	
		}
	}
	
	@Override
	public void go(HasWidgets container) 
	{						
		container.add(_display.asWidget());		
						
		bind();
		
		// Add these presenters after the ControlBar presenter has been added.		
		_f_presenter = new FilterPresenter(new FilterView(_display.getFilterButton()), _state_buttons);
		_f_presenter.go((HasWidgets)_display.asWidget());
		
		_ip_presenter = new InsertPanelPresenter(new InsertPanelView(_display.getInsertButton()));
		_ip_presenter.go((HasWidgets)_display.asWidget());	
		
		_e_presenter = new ExportPresenter(new ExportView(_display.getExportButton()), _state_buttons);
		_e_presenter.go((HasWidgets)_display.asWidget());				
	}
	
	public void setRecord(Record record)
	{
		_record = record;
	}
	
	public void setSourceField(Field field)
	{
		_source_field = field;		
	}
	
	public void setTargetField(Field field)
	{
		_target_field = field;
	}		
	
	public InsertPanelPresenter getInsertPanelPresenter()
	{
		return _ip_presenter;
	}
	
	@Override
	public void signOut() 
	{
		setButtons();
						
		if (_f_presenter.isFilterSet())
			_access_controller.getEventBus().fireEvent(new FilterEvent(null));	
	}

	@Override
	public void onResize(ResizeEvent event) 
	{		
		FixedPanel fixed_panel = (FixedPanel)_display.asWidget();
		
		if (_access_controller.isGuest())
			fixed_panel.setWidth("35%");
		else
			fixed_panel.setWidth("60%");
	}
}