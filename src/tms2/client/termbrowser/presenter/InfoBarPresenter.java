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

import tms2.client.accesscontrol.AccessController;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.EmailForm;
import tms2.client.widgets.FixedPanel;
import tms2.client.widgets.HasSignOut;
import tms2.shared.Field;
import tms2.shared.Record;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class InfoBarPresenter implements Presenter, HasSignOut, ResizeHandler
{
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private Display _display = null;	
	
	private Record _record = null;
	private Field _source_field = null;
	
	public static int _captcha_index = 0;
	
	public interface Display
	{		
		public static final String NOT_SET_URL = "images/delete24.png";
		public static final String SET_URL = "images/accepted24.png";
		
		public Button getFilterLabelButton();
		public Image getFilterImage();
		public Button getNumRecLabelButton();
		public Button getNumRecButton();
		public Button getEmailButton();
		public Widget asWidget();
	}
	
	public InfoBarPresenter(Display display)
	{
		_display = display;
		
		_access_controller.addSignOut(this);
		Window.addResizeHandler(this);
	}
	
	private void bind()
	{
		setFilterNotSetIcon();
		setButtons();
		addEmailButtonHandler();
	}
	
	private void setButtons()
	{				
		FixedPanel fixed_panel = (FixedPanel)_display.asWidget();
		
		if (! _access_controller.isGuest())		
		{
			_display.getEmailButton().setVisible(false);
			_display.getNumRecLabelButton().setVisible(true);
			_display.getNumRecButton().setVisible(true);
			
			fixed_panel.setWidth("40%");
		}
		else
		{
			_display.getEmailButton().setVisible(true);
			_display.getFilterLabelButton().setVisible(false);
			_display.getFilterImage().setVisible(false);
			_display.getNumRecLabelButton().setVisible(false);
			_display.getNumRecButton().setVisible(false);
			
			fixed_panel.setWidth("65%");
		}
	}
	
	public void setFilterNotSetIcon()
	{
		Image image = _display.getFilterImage();
		
		image.setUrl(Display.NOT_SET_URL);
		image.setWidth("15px");
		image.setHeight("15px");
	}
	
	public void setFilterSetIcon()
	{
		Image image = _display.getFilterImage();
		
		image.setUrl(Display.SET_URL);
		image.setWidth("15px");
		image.setHeight("15px");
	}
	
	private void addEmailButtonHandler()
	{
		_display.getEmailButton().addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (_record != null && _source_field != null)
				{
					EmailForm form = new EmailForm(_display, _record, _source_field);
					form.buildCurrentRecordForm();
					form.show();
				}
				else
				{
					AlertBox.show(_i18n.getConstants().no_record_email());
					_display.getEmailButton().setEnabled(true);
				}
			}
		});
	}
	
	public void setNumberOfRecord(int number_of_records)
	{
		_display.getNumRecButton().setText(Integer.toString(number_of_records));
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		container.add(_display.asWidget());
		
		bind();
	}
	
	public void setRecord(Record record)
	{
		_record = record;
	}
	
	public void setSourceField(Field field)
	{
		_source_field = field;
	}
	
	public Display getDisplay()
	{
		return _display;
	}

	@Override
	public void signOut() 
	{	
		setButtons();
	}

	@Override
	public void onResize(ResizeEvent event) 
	{		
		FixedPanel fixed_panel = (FixedPanel)_display.asWidget();
		
		if (_access_controller.isGuest())
			fixed_panel.setWidth("65%");
		else
			fixed_panel.setWidth("40%");
	}
}
