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

package tms2.client.admininterface.presenter;

import java.util.ArrayList;
import java.util.Iterator;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.AdminInterfaceEvent;
import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.AdminTabPresenter;
import tms2.client.service.FieldService;
import tms2.client.service.FieldServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.AppProperties;
import tms2.shared.Field;
import tms2.shared.Field.FieldDataType;
import tms2.shared.Field.FieldType;
import tms2.shared.PresetField;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * AdminTabPresenter class to manage Fields.
 * 
 * @author I. Lavangee
 *
 */
public class FieldTabPresenter implements AdminTabPresenter
{
	private Display _display = null;
	
	private static AccessController _access_controller = AccessController.getInstance();
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static FieldServiceAsync _field_service = GWT.create(FieldService.class);
	
	private Field _field = null;
	private PresetField _preset_field = null;
		
	public interface Display
	{
		public VerticalPanel getPresetFieldsPanel();
		public RadioButton getRecordAttrRadioButton();
		public RadioButton getIndexRadioButton();
		public RadioButton getTermAttrRadioButton();
		public RadioButton getPresetAttrRadioButton();
		public RadioButton getTermSubAttrRadioButton();
		public RadioButton getPresetSubAttrRadioButton();
		public ExtendedListBox<Field> getFieldsListBox();
		public Button getNewFieldButton();
		public TextBox getFieldNameTextBox();
		public TextBox getFieldTypeTextBox();
		public ListBox getFieldDataTypeListBox();
		public TextBox getMaxLengthTextBox();
		public CheckBox getInUseCheckBox();
		public Button getFieldSaveButton();
		public Button getFieldResetButton();
		public Label getPresetHeadingLabel();
		public ExtendedListBox<PresetField> getPresetFieldListBox();
		public Button getNewPresetFieldButton();
		public TextBox getPresetNameTextBox();
		public Button getPresetFieldSaveButton();
		public Button getPresetFieldResetButton();
		public Label getHeadingLabel();
		public Widget asWidget();
	}
	
	public FieldTabPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{		
		addRecordRadioButtonHandler();
		addIndexRadioButtonHandler();
		addTextAttrRadioButtonHandler();
		addPresetAttrRadioButtonHandler();
		addTextSubAttrRadioButtonHandler();
		addPresetSubAttrRadioButtonHandler();
		addFieldListBoxHandler();
		addPresetFieldListBoxHandler();
		addNewFieldHandler();
		addNewPresetFieldHandler();
		addFieldResetHandler();
		addPresetFieldResetHandler();
		addSaveFieldHandler();
		addSavePresetFieldHandler();
	}
	
	private void retrieveFields(final FieldType field_type)
	{
		resetFieldsPanel();				
		
		_field = null;
		
		TextBox txt_field_type = _display.getFieldTypeTextBox();
		txt_field_type.setText(field_type.getFieldTypeName());
			
		if (! _access_controller.isGuest())
		{
			_field_service.getFieldsAsPerFieldType(_access_controller.getAuthToken(), field_type, new BusyDialogAsyncCallBack<ArrayList<Field>>(null) 
			{
				@Override
				public void onComplete(ArrayList<Field> result) 
				{							
					ExtendedListBox<Field> lst_field = _display.getFieldsListBox();
					lst_field.clear();
					
					lst_field.addItem(_i18n.getConstants().admin_field_selectField(), -1, null);
					
					if (result == null || result.size() == 0)
						lst_field.setEnabled(false);
					else
					{
						Iterator<Field> iter = result.iterator();
						while (iter.hasNext())
						{
							Field field = iter.next();
							lst_field.addItem(field.getFieldName(), field.getFieldId(), field);						
						}
											
						lst_field.setEnabled(true);
						lst_field.setSelectedIndex(0);
					}
					
					if (field_type.getFieldType() == 1 || field_type.getFieldType() == 3 || field_type.getFieldType() == 7)
						_display.getFieldDataTypeListBox().setEnabled(false);
				}
	
				@Override
				public void onError(Throwable caught) 
				{
					ErrorBox.ErrorHandler.handle(caught);
					
					_field = null;
					_preset_field = null;
				}
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	private void addRecordRadioButtonHandler()
	{
		_display.getRecordAttrRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_field = null;
				_preset_field = null;
								
				FieldType record_type = FieldType.TEXT_RECORD_FIELD;	
				retrieveFields(record_type);
			}
		});
	}
	
	private void addIndexRadioButtonHandler()
	{
		_display.getIndexRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_field = null;
				_preset_field = null;
								
				FieldType index_type = FieldType.INDEX_FIELD;	
				retrieveFields(index_type);			
			}
		});
	}
	
	private void addTextAttrRadioButtonHandler()
	{
		_display.getTermAttrRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				_field = null;
				_preset_field = null;
								
				FieldType text_attr_type = FieldType.TEXT_ATTR_FIELD;	
				retrieveFields(text_attr_type);		
			}
		});
	}
	
	private void addPresetAttrRadioButtonHandler()
	{
		_display.getPresetAttrRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_field = null;
				_preset_field = null;
								
				FieldType preset_attr_type = FieldType.PRESET_ATTR_FIELD;
				retrieveFields(preset_attr_type);	
			}
		});
	}
	
	private void addTextSubAttrRadioButtonHandler()
	{
		_display.getTermSubAttrRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				_field = null;
				_preset_field = null;
								
				FieldType text_subattr_type = FieldType.TEXT_SUB_ATTR_FIELD;	
				retrieveFields(text_subattr_type);	
			}
		});
	}
	
	private void addPresetSubAttrRadioButtonHandler()
	{
		_display.getPresetSubAttrRadioButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_field = null;
				_preset_field = null;
								
				FieldType preset_subattr_type = FieldType.PRESET_SUB_ATTR_FIELD;
				retrieveFields(preset_subattr_type);					
			}
		});
	}
	
	private void addFieldListBoxHandler()
	{
		_display.getFieldsListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_field = _display.getFieldsListBox().getSelectedItem();
				populateFieldPanel();
			}
		});
	}
	
	private void addPresetFieldListBoxHandler()
	{
		_display.getPresetFieldListBox().addExtendedListBoxValueChangeHandler(new ListBoxValueChangeEventHandler()
		{			
			@Override
			public void onExtendedListBoxValueChange(ListBoxValueChangeEvent event) 
			{
				_preset_field = _display.getPresetFieldListBox().getSelectedItem();
				populatePresetFieldPanel();
			}
		});
	}
	
	private void addNewFieldHandler()
	{
		_display.getNewFieldButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetFieldsPanel();
				
				_field = null;
				_preset_field = null;
				
				_display.getFieldsListBox().setSelectedIndex(0);
				
				if (_display.getIndexRadioButton().getValue() || _display.getPresetAttrRadioButton().getValue() || _display.getPresetSubAttrRadioButton().getValue())
					_display.getFieldDataTypeListBox().setEnabled(false);
				else
					_display.getFieldDataTypeListBox().setEnabled(true);
			}
		});
	}
	
	private void addNewPresetFieldHandler()
	{
		_display.getNewPresetFieldButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetPresetFieldPanel();
				
				_preset_field = null;
				
				_display.getPresetFieldListBox().setSelectedIndex(0);
				
				VerticalPanel preset_panel = _display.getPresetFieldsPanel();
				preset_panel.setVisible(true);
			}
		});
	}
	
	private void addFieldResetHandler()
	{
		_display.getFieldResetButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetFieldsPanel();
				
				_field = null;
				_preset_field = null;
				
				_display.getFieldsListBox().setSelectedIndex(0);						
			}
		});
	}
	
	private void addPresetFieldResetHandler()
	{
		_display.getPresetFieldResetButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				resetPresetFieldPanel();
				
				_preset_field = null;
				
				_display.getPresetFieldListBox().setSelectedIndex(0);
				
				VerticalPanel preset_panel = _display.getPresetFieldsPanel();
				preset_panel.setVisible(true);						
			}
		});
	}
	
	private void addSaveFieldHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getFieldSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateField())
				{
					if (_field == null)
					{
						_field = new Field();
						
						fillFieldForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(FieldTabPresenter.this, AdminInterfaceEvent.UPDATE_FIELD));
					}
					else
					{
						fillFieldForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(FieldTabPresenter.this, AdminInterfaceEvent.UPDATE_FIELD));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_field_validate());
			}
		});		
	}
	
	private void addSavePresetFieldHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getPresetFieldSaveButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validatePresetField())
				{
					if (_preset_field == null)
					{
						_preset_field = new PresetField();
						
						fillPresetFieldForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(FieldTabPresenter.this, AdminInterfaceEvent.UPDATE_PRESET_FIELD));
					}
					else
					{
						fillPresetFieldForEvent();
						
						event_bus.fireEvent(new AdminInterfaceEvent(FieldTabPresenter.this, AdminInterfaceEvent.UPDATE_PRESET_FIELD));
					}
				}
				else
					AlertBox.show(_i18n.getConstants().admin_field_validate());
			}
		});
	}
	
	private void populateFieldPanel()
	{
		resetFieldsPanel();
						
		if (_field != null)
		{						
			TextBox txt_fieldname = _display.getFieldNameTextBox();
			TextBox txt_field_type = _display.getFieldTypeTextBox();
			TextBox txt_max_length = _display.getMaxLengthTextBox();
			CheckBox chk_inuse = _display.getInUseCheckBox();
			ListBox lst_field_data_type = _display.getFieldDataTypeListBox();
			
			txt_fieldname.setText(_field.getFieldName());			
			
			AppProperties app_props = _access_controller.getAppProperties();
			
			FieldType field_type = null;
			if (_field.isRecordAttribute())
			{
				field_type = FieldType.TEXT_RECORD_FIELD;
				if (_field.isProject(app_props.getProjectField()))
					lst_field_data_type.setEnabled(false);
			}
			else if (_field.isIndexField())
			{
				field_type = FieldType.INDEX_FIELD;
				lst_field_data_type.setEnabled(false);
			}
			else if (_field.isFieldAttribute())
				field_type = FieldType.TEXT_ATTR_FIELD;
			else if (_field.isSynonymField())
			{
				field_type = FieldType.SYNONYM_FIELD;
				lst_field_data_type.setEnabled(false);
			}
			else if (_field.isPresetAttribute())
			{
				field_type = FieldType.PRESET_ATTR_FIELD;
				lst_field_data_type.setEnabled(false);
			}
			else if (_field.isFieldSubAttribute())
				field_type = FieldType.TEXT_SUB_ATTR_FIELD;
			else if (_field.isPresetSubAttribute())
			{
				field_type = FieldType.PRESET_SUB_ATTR_FIELD;
				lst_field_data_type.setEnabled(false);
			}
							
			txt_field_type.setText(field_type.getFieldTypeName());
						
			// These list items are added to the list box in this order
			FieldDataType field_data_type = FieldDataType.PLAIN_TEXT;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(0);
			
			field_data_type = FieldDataType.INTEGER;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(1);
			
			field_data_type = FieldDataType.FLOATING_POINT_NUMBER;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(2);
			
			field_data_type = FieldDataType.HTML_HYPERLINK;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(3);
			
			field_data_type = FieldDataType.XML;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(4);
			
			field_data_type = FieldDataType.FORMULA;
			if (_field.getFieldDataTypeId() == field_data_type.getFieldDataType())
				lst_field_data_type.setSelectedIndex(5);
			
			txt_max_length.setText(Integer.toString(_field.getMaxlength()));
										
			if (_field.isInuse() || 
					_field.isProject(app_props.getProjectField()) || 
					_field.isSortIndex(app_props.getSortIndexField()) ||
					_field.isContext(app_props.getContextField()) ||
					_field.isDefinition(app_props.getDefinitionField()) ||
					_field.isNote(app_props.getNoteField()) ||
					_field.isSynonymContext(app_props.getSynonymContextField()) ||
					_field.isSynonymNote(app_props.getSynonymNoteField()))
			{
				chk_inuse.setValue(_field.isInuse());				
				disableFieldsPanel();
				
				resetPresetFieldPanel();
			}
			else
			{
				chk_inuse.setValue(false);				
				enableFieldsPanel();
				
				if (_field.isPresetAttribute() || _field.isPresetSubAttribute())
					populatePresetListBox();
				else				
					resetPresetFieldPanel();				
			}
			
			// Cannot change field name
			txt_fieldname.setEnabled(false);
		}
	}
	
	private void populatePresetListBox()
	{
		VerticalPanel preset_panel = _display.getPresetFieldsPanel();
		preset_panel.setVisible(true);
		
		Label lbl_preset_heading = _display.getPresetHeadingLabel();
		lbl_preset_heading.setText(_i18n.getConstants().admin_field_preset_attribFor() +  "'" + _field.getFieldName() + "'");
		
		ExtendedListBox<PresetField> lst_preset = _display.getPresetFieldListBox();
		lst_preset.clear();
		
		lst_preset.addItem(_i18n.getConstants().admin_field_preset_select(), "-1", null);
		
		ArrayList<PresetField> preset_fields = _field.getPresetFields();
		if (preset_fields == null || preset_fields.size() == 0)
			lst_preset.setEnabled(false);
		else
		{
			Iterator<PresetField> iter = preset_fields.iterator();
			while (iter.hasNext())
			{
				PresetField preset_field = iter.next();				
				lst_preset.addItem(preset_field.getPresetFieldName(), preset_field.getPresetFieldName(), preset_field);
			}
			
			lst_preset.setEnabled(true);
		}			
	}
	
	private void populatePresetFieldPanel()
	{
		TextBox txt_preset_name = _display.getPresetNameTextBox();
		
		if (_preset_field != null)
		{			
			txt_preset_name.setText(_preset_field.getPresetFieldName());
			
			if (_preset_field.isNoneActive())
				txt_preset_name.setEnabled(false);
			else
				txt_preset_name.setEnabled(true);
		}
		else
			txt_preset_name.setEnabled(true);
	}
	
	private void resetFieldsPanel()
	{		
		TextBox txt_fieldname = _display.getFieldNameTextBox();		
		ListBox lst_field_data_type = _display.getFieldDataTypeListBox();
		TextBox txt_max_length = _display.getMaxLengthTextBox();
		CheckBox chk_inuse = _display.getInUseCheckBox();
		
		chk_inuse.setValue(false);
		
		txt_fieldname.setText("");
						
		FieldDataType plain_text = FieldDataType.PLAIN_TEXT;
		FieldDataType integer = FieldDataType.INTEGER;
		FieldDataType floating_number = FieldDataType.FLOATING_POINT_NUMBER;
		FieldDataType html = FieldDataType.HTML_HYPERLINK;
		FieldDataType xml = FieldDataType.XML;
		FieldDataType formula = FieldDataType.FORMULA;
		
		lst_field_data_type.clear();
		
		lst_field_data_type.addItem(plain_text.getFieldDataTypeName());
		lst_field_data_type.addItem(integer.getFieldDataTypeName());
		lst_field_data_type.addItem(floating_number.getFieldDataTypeName());
		lst_field_data_type.addItem(html.getFieldDataTypeName());
		lst_field_data_type.addItem(xml.getFieldDataTypeName());
		lst_field_data_type.addItem(formula.getFieldDataTypeName());
		
		lst_field_data_type.setEnabled(true);
		
		txt_max_length.setText("");
		
		resetPresetFieldPanel();
		
		_preset_field = null;
		
		enableFieldsPanel();
	}
	
	private void resetPresetFieldPanel()
	{
		TextBox txt_preset_name = _display.getPresetNameTextBox();
		txt_preset_name.setText("");
		txt_preset_name.setEnabled(true);
		
		VerticalPanel preset_panel = _display.getPresetFieldsPanel();
		preset_panel.setVisible(false);
	}
	
	private void enableFieldsPanel()
	{
		TextBox txt_fieldname = _display.getFieldNameTextBox();
		TextBox txt_max_length = _display.getMaxLengthTextBox();		
		ListBox lst_field_data_type = _display.getFieldDataTypeListBox();
		
		txt_fieldname.setEnabled(true);
		txt_max_length.setEnabled(true);
		lst_field_data_type.setEnabled(lst_field_data_type.isEnabled());
	}
	
	private void disableFieldsPanel()
	{
		TextBox txt_fieldname = _display.getFieldNameTextBox();
		TextBox txt_max_length = _display.getMaxLengthTextBox();		
		ListBox lst_field_data_type = _display.getFieldDataTypeListBox();
		
		txt_fieldname.setEnabled(false);
		txt_max_length.setEnabled(false);
		lst_field_data_type.setEnabled(false);
	}
	
	private boolean validateField()
	{				
		TextBox txt_fieldname = _display.getFieldNameTextBox();
		TextBox txt_max_length = _display.getMaxLengthTextBox();
		
		if (txt_fieldname.getText().isEmpty())
			return false;
		
		if (txt_max_length.getText().isEmpty())
			return false;
						
		ArrayList<Field> fields = _display.getFieldsListBox().getItems();
		Iterator<Field> iter = fields.iterator();
		while (iter.hasNext())
		{
			Field field = iter.next();
			if (field == null)
				continue;
			
			// Validations for creating fields.

			// Dont allow duplicate fields. The DB could have have handled this bit, but
			// as with the new TMS design all fields are now in the tms.fields table.
			if (field.getFieldName().equals(txt_fieldname.getText()) && _field == null)
				return false;
						
			// The system will not allow the creation of any of the fields 
			// defined in the AppConfig.properties file.				
			if (field.isSortIndex(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isProject(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isDefinition(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isNote(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isContext(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isSynonymContext(txt_fieldname.getText()) && _field == null)
				return false;
			
			if (field.isSynonymNote(txt_fieldname.getText()) && _field == null)
				return false;
						
			if (field.isSynonymField() && _field == null)
			{
				if (field.getFieldName().equalsIgnoreCase(txt_fieldname.getText()))
				return false;
			}
		}
		
		return true;
	}
	
	private boolean validatePresetField()
	{	
		TextBox txt_preset_name = _display.getPresetNameTextBox();
		
		if (txt_preset_name.getText().isEmpty())
			return false;
		
		if (txt_preset_name.getText().equalsIgnoreCase("None"))
			return false;
		
		return true;
	}
	
	private void fillFieldForEvent()
	{
		_field.setFieldName(_display.getFieldNameTextBox().getText());
		_field.setMaxlength(Integer.parseInt(_display.getMaxLengthTextBox().getText()));
		_field.setDefaultValue("");
		_field.setFieldTypeId(getFieldTypeId());										
		_field.setFieldDataTypeId(getFieldDataTypeId());
	}
		
	private void fillPresetFieldForEvent()
	{				
		_preset_field.setFieldId(_field.getFieldId());
		_preset_field.setPresetFieldName(_display.getPresetNameTextBox().getText());
	}
	
	private int getFieldTypeId()
	{
		String field_type_name = _display.getFieldTypeTextBox().getText();
		
		FieldType field_type = FieldType.TEXT_RECORD_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))		
			return field_type.getFieldType();		
		
		field_type = FieldType.INDEX_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		field_type = FieldType.TEXT_ATTR_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		field_type = FieldType.SYNONYM_FIELD;
		if (field_type_name.endsWith(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		field_type = FieldType.TEXT_SUB_ATTR_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		field_type = FieldType.PRESET_ATTR_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		field_type = FieldType.PRESET_SUB_ATTR_FIELD;
		if (field_type_name.equals(field_type.getFieldTypeName()))
			return field_type.getFieldType();
		
		return -1;
	}
	
	private int getFieldDataTypeId()
	{
		String field_data_type_name = _display.getFieldDataTypeListBox().getItemText(_display.getFieldDataTypeListBox().getSelectedIndex());

		FieldDataType field_data_type = FieldDataType.PLAIN_TEXT;
		if (field_data_type_name.equals(field_data_type.getFieldDataTypeName()))			
			return field_data_type.getFieldDataType();
		
		field_data_type = FieldDataType.INTEGER;
		if (field_data_type_name.equals(field_data_type.getFieldDataTypeName()))
			return field_data_type.getFieldDataType();
		
		field_data_type = FieldDataType.FLOATING_POINT_NUMBER;
		if (field_data_type_name.equals(field_data_type.getFieldDataTypeName()))
			return field_data_type.getFieldDataType();
		
		field_data_type = FieldDataType.HTML_HYPERLINK;
		if (field_data_type_name.equals(field_data_type.getFieldDataTypeName()))
			return field_data_type.getFieldDataType();
		
		field_data_type = FieldDataType.XML;
		if (field_data_type_name.equals(field_data_type.getFieldDataTypeName()))
			return field_data_type.getFieldDataType();
		
		field_data_type = FieldDataType.FORMULA;
		return field_data_type.getFieldDataType();		
	}
		
	@Override
	public void go(HasWidgets container) 
	{				
		TabLayoutPanel admin_tab_panel = (TabLayoutPanel)container;		
		admin_tab_panel.add(_display.asWidget(), _display.getHeadingLabel());
		
		bind();
	}

	@Override
	public void loadAdminTabData() 
	{
		FieldType record_type = FieldType.TEXT_RECORD_FIELD;	
		_display.getRecordAttrRadioButton().setValue(true);
		
		retrieveFields(record_type);
	}
		
	public Display getDisplay()
	{
		return _display;
	}
	
	public Field getField()
	{
		return _field;
	}
	
	public void setField(Field field)
	{
		_field = null;
	}
	
	public PresetField getPresetField()
	{
		return _preset_field;
	}
	
	public void setPresetField(PresetField preset_field)
	{
		_preset_field = preset_field;
	}
}
