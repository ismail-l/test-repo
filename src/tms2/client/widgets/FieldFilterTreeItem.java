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

package tms2.client.widgets;

import tms2.client.i18n.Internationalization;
import tms2.shared.FieldFilter;
import tms2.shared.FilterableObject;
import tms2.shared.TerminlogyObject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldFilterTreeItem extends TreeItem
{		
	private FieldFilter _field_filter = null;
	private TerminlogyObject _terminology_object = null;
	
	private TreeItemControlsPanel _controls_panel = null;
	
	private boolean _is_filter = false;
	private boolean _is_active = false;
	private boolean _is_disabled = false;
	
	public FieldFilterTreeItem(TerminlogyObject terminology_object, boolean is_filter)
	{
		_terminology_object = terminology_object;
				
		_field_filter = new FieldFilter();
		_field_filter.setFieldId(_terminology_object.getFieldId());
		_field_filter.setFieldName(_terminology_object.getFieldName());
		_field_filter.setFieldTypeId(_terminology_object.getFieldTypeId());	
		_field_filter.setIsAnd(false);
		_field_filter.setEventType(FilterableObject.Event.CONTAINS.getEventType());
		
		_is_filter = is_filter;
		
		addStyleName("fieldPanelControlFont");
		
		setWidth("100%");
		
		setText(_field_filter.getFieldName());				
	}
			
	public FieldFilter getFieldFilter()
	{
		return _field_filter;
	}
	
	public TerminlogyObject getTerminologyObject()
	{
		return _terminology_object;
	}
	
	public void showControls()
	{
		if (_controls_panel == null)
			_controls_panel = new TreeItemControlsPanel();
		
		_controls_panel.showRelativeTo(this);
		_controls_panel.setValues();
	}
	
	public void setIsActive(boolean is_active)
	{
		if (_controls_panel == null)
			_controls_panel = new TreeItemControlsPanel();
		
		_controls_panel.setIsActive(is_active);
	}
	
	public boolean isActive()
	{
		return _is_active;
	}
	
	public void setIsDisabled(boolean is_disabled, boolean set_opacity)
	{
		_is_disabled = is_disabled;
		
		if (set_opacity)
			getElement().getStyle().setOpacity(0.5); 
	}
	
	public boolean isDisabled()
	{		
		return _is_disabled;
	}
	
	private class TreeItemControlsPanel extends PopupPanel
	{
		private Internationalization _i18n = Internationalization.getInstance();
				
		private VerticalPanel _controls_panel = null;
		
		private CheckBox _chk_active_filter = null;
		private ListBox _lst_contains = null;
		private ListBox _lst_and_or = null;
		private TextBox _txt_field_text = null;
		
		public TreeItemControlsPanel()
		{
			super(true, true);
			setAnimationEnabled(true);
			
			Style style = getElement().getStyle();
			
			style.setZIndex(10000);	
			style.setBackgroundColor("#FFFFFF");
			style.setBorderStyle(BorderStyle.SOLID);
			style.setBorderWidth(1, Unit.PX);
			style.setBorderColor("#531a17");
			
			_controls_panel = new VerticalPanel();
			_controls_panel.setSpacing(5);
			
			Label lbl_heading = new Label("Filter field properties for " + _field_filter.getFieldName());
			lbl_heading.addStyleName("headingLabel");
			lbl_heading.addStyleName("labelTextBold");
			lbl_heading.addStyleName("plainLabelText");
			
			_controls_panel.add(lbl_heading);
			
			add(_controls_panel);
			
			buildDetailsPanel();
		}
		
		private void buildDetailsPanel()
		{
			VerticalPanel controls = new VerticalPanel();
			controls.setSpacing(5);
			
			_chk_active_filter = new CheckBox(_i18n.getConstants().filter_field_active());
			_chk_active_filter.addValueChangeHandler(new ValueChangeHandler<Boolean>() 
			{			
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) 
				{
					_is_active = _chk_active_filter.getValue();					
				}
			});
			
			controls.add(_chk_active_filter);
			
			if (_is_filter)
			{
				HorizontalPanel controls_panel = new HorizontalPanel();
				controls_panel.setSpacing(5);
				
				_lst_contains = new ListBox();
				_lst_contains.addItem(_i18n.getConstants().filter_contains());
				_lst_contains.addItem(_i18n.getConstants().files_doesNotContain());
				_lst_contains.addChangeHandler(new ChangeHandler() 
				{				
					@Override
					public void onChange(ChangeEvent event) 
					{
						String value = _lst_contains.getItemText(_lst_contains.getSelectedIndex());
						
						if (value.equals(_i18n.getConstants().filter_contains()))
							_field_filter.setEventType(FilterableObject.Event.CONTAINS.getEventType());
						else if (value.equals(_i18n.getConstants().files_doesNotContain()))			
							_field_filter.setEventType(FilterableObject.Event.NOT_CONTAINS.getEventType());					
					}
				});
					
				controls_panel.add(_lst_contains);
				
				_lst_and_or = new ListBox();
				_lst_and_or.addItem(_i18n.getConstants().filter_and());
				_lst_and_or.addItem(_i18n.getConstants().filter_or());
				_lst_and_or.addChangeHandler(new ChangeHandler() 
				{				
					@Override
					public void onChange(ChangeEvent event) 
					{
						String value = _lst_and_or.getItemText(_lst_and_or.getSelectedIndex());
						
						if (value.equals(_i18n.getConstants().filter_and()))
							_field_filter.setIsAnd(true);
						else if (value.equals(_i18n.getConstants().filter_or()))			
							_field_filter.setIsAnd(false);		
					}
				});
				
				controls_panel.add(_lst_and_or);
											
				Label lbl_field_text = new Label(_i18n.getConstants().filter_value());
				lbl_field_text.addStyleName("plainLabelText");
				
				controls_panel.add(lbl_field_text);
				
				_txt_field_text = new TextBox();
				_txt_field_text.addChangeHandler(new ChangeHandler()
				{				
					@Override
					public void onChange(ChangeEvent event) 
					{
						String value = _txt_field_text.getText();
						if (! value.isEmpty())
							_field_filter.setFieldText(value);
						else
							_field_filter.setFieldText("");
					}
				});
					
				controls_panel.add(_txt_field_text);
				
				controls.add(controls_panel);
			}
			
			_controls_panel.add(controls);
			
			HorizontalPanel button_panel = new HorizontalPanel();
			button_panel.setSpacing(5);
			
			Button btn_close = new Button("Close");
			btn_close.addClickHandler(new ClickHandler()
			{			
				@Override
				public void onClick(ClickEvent event) 
				{
					hide();
				}
			});
			
			button_panel.add(btn_close);
			
			_controls_panel.add(button_panel);
			
			_controls_panel.setCellHorizontalAlignment(button_panel, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		
		public void setValues()
		{
			_chk_active_filter.setValue(_is_active);
			
			if (_is_filter)
			{
				if (_field_filter.isContains())
					_lst_contains.setSelectedIndex(0);
				else
					_lst_contains.setSelectedIndex(1);
				
				if (_field_filter.isAnd())
					_lst_and_or.setSelectedIndex(0);
				else
					_lst_and_or.setSelectedIndex(1);
				
				_txt_field_text.setText(_field_filter.getFieldText());
			}
		}
		
		private void setIsActive(boolean is_active)
		{
			_is_active = is_active;
			_chk_active_filter.setValue(_is_active);
		}
	}
}
