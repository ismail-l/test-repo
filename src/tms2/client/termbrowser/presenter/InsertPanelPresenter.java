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

import tms2.client.accesscontrol.AccessController;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.util.AttachedPanelPositionUtility;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.HasSignOut;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class InsertPanelPresenter implements Presenter, HasSignOut
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private Display _display = null;
		
	private TextBoxBase _txt_text = null;
	
	public interface Display
	{
		public AttachedPanel getMainContentPanel();
		public ArrayList<Button> getDiacriticButtons();
		public Button getCancelButton();
		public Widget asWidget();
	}
	
	public InsertPanelPresenter(Display display)
	{
		_display = display;
		
		_access_controller.addSignOut(this);
	}
	
	private void bind()
	{
		AttachedPanelPositionUtility.addPosition(_display.getMainContentPanel());
		
		addDiacriticButtonsHandler();
		addCancelButtonHandler();
	}
	
	private void addDiacriticButtonsHandler()
	{
		ArrayList<Button> diacritic_buttons = _display.getDiacriticButtons();
		Iterator<Button> iter = diacritic_buttons.iterator();
		
		while (iter.hasNext())
		{
			final Button diacritic_button = iter.next();
			
			diacritic_button.addClickHandler(new ClickHandler()
			{				
				@Override
				public void onClick(ClickEvent event) 
				{
					if (_txt_text == null)
						AlertBox.show(_i18n.getConstants().controls_insert_error());
					else
					{												
						String text = _txt_text.getText();
						if (text.isEmpty())						
							_txt_text.setText(diacritic_button.getText());
						else
						{
							int position = _txt_text.getCursorPos();
							
							String start_text = text.substring(0, position);
							String end_text = text.substring(position);
							
							text = start_text + diacritic_button.getText() + end_text;
							
							_txt_text.setText(text);
						}
					}
				}
			});
		}
	}
		
	private void addCancelButtonHandler()
	{
		_display.getCancelButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_display.getMainContentPanel().hide();			
			}
		});
	}
		
	public void show()
	{
		_display.getMainContentPanel().position();
		_display.getMainContentPanel().show();		
	}
	
	public void setFocusTextBase(TextBoxBase textbox_base)
	{
		_txt_text = textbox_base;
	}
	
	@Override
	public void go(HasWidgets container) 
	{				
		container.add(_display.asWidget());	
		
		_display.getMainContentPanel().hide();
		
		bind();
	}

	@Override
	public void signOut() 
	{
		_display.getMainContentPanel().position();
	}
}
