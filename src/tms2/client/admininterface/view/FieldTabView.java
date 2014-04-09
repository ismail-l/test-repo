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

package tms2.client.admininterface.view;

import tms2.client.admininterface.presenter.FieldTabPresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.widgets.AdminTab;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.Field;
import tms2.shared.PresetField;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldTabView extends AdminTab implements FieldTabPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _fields_presets_panel = null;
	private VerticalPanel _fields_panel = null;
	private VerticalPanel _preset_fields_panel = null;
	private RadioButton _btn_record_attr = null;
	private RadioButton _btn_index = null;
	private RadioButton _btn_term_attr = null;
	private RadioButton _btn_preset_attr = null;
	private RadioButton _btn_term_sub_attr = null;
	private RadioButton _btn_preset_sub_attr = null;
	private ExtendedListBox<Field> _lst_field = null;
	private Button _btn_newfield = null;
	private TextBox _txt_fieldname = null;
	private TextBox _txt_fieldtype = null;
	private ListBox _lst_field_datatype = null;
	private TextBox _txt_max_length = null;
	private CheckBox _chk_field_inuse = null;
	private Button _btn_field_save = null;
	private Button _btn_field_reset = null;
	private Label _lbl_preset_heading = null;
	private ExtendedListBox<PresetField> _lst_preset_field = null;
	private Button _btn_newpresetfield = null;
	private TextBox _txt_preset_name = null;
	private Button _btn_preset_save = null;
	private Button _btn_preset_reset = null;
	
	public FieldTabView()
	{
		super(_i18n.getConstants().admin_tab_Fields());
		
		_fields_presets_panel = new VerticalPanel();
		_fields_presets_panel.setSpacing(20);
		
		buildFieldPanel();
		buildPresetFieldsPanel();
		
		super.add(_fields_presets_panel);
	}
	
	private void buildFieldPanel()
	{
		_fields_panel = new VerticalPanel();
		
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_field_heading(), true);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_fields_panel.add(lbl_first_heading);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_fields_panel.add(lbl_dummy);
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_field_label(), false);
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("tabHeading");
		
		_fields_panel.add(lbl_second_heading);
		
		HorizontalPanel radiobutton_panel = new HorizontalPanel();			
		radiobutton_panel.setSpacing(5);
		
		_btn_record_attr = new RadioButton("group", _i18n.getConstants().admin_im_labelRecordFields());
		_btn_index = new RadioButton("group", _i18n.getConstants().admin_im_labelIndexFields());
		_btn_term_attr = new RadioButton("group", _i18n.getConstants().admin_im_labelAttributeFields());
		_btn_preset_attr = new RadioButton("group", _i18n.getConstants().admin_im_labelPresetAttributeFields());
		_btn_term_sub_attr = new RadioButton("group", _i18n.getConstants().admin_im_labelSubAttributeFields());
		_btn_preset_sub_attr = new RadioButton("group", _i18n.getConstants().admin_im_labelPresetSubAttributeFields());
		
		radiobutton_panel.add(_btn_record_attr);
		radiobutton_panel.add(_btn_index);
		radiobutton_panel.add(_btn_term_attr);
		radiobutton_panel.add(_btn_preset_attr);
		radiobutton_panel.add(_btn_term_sub_attr);
		radiobutton_panel.add(_btn_preset_sub_attr);
		
		_fields_panel.add(radiobutton_panel);
		
		HorizontalPanel select_field_panel = new HorizontalPanel();
		select_field_panel.setHeight("50px");
		select_field_panel.setSpacing(5);
		select_field_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		select_field_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		_lst_field = new ExtendedListBox<Field>(false);
		
		_btn_newfield = new Button(_i18n.getConstants().admin_field_newButton());		
		_btn_newfield.addStyleName("adminButton");
		
		select_field_panel.add(_lst_field);
		select_field_panel.add(_btn_newfield);
		
		_fields_panel.add(select_field_panel);
		
		FlexTable field_details_panel = new FlexTable();
		field_details_panel.setCellSpacing(10);
		field_details_panel.setStyleName("borderedBlock");
		
		_chk_field_inuse = new CheckBox(_i18n.getConstants().admin_im_inuse());
		_chk_field_inuse.setEnabled(false);
		
		field_details_panel.setWidget(0, 0, _chk_field_inuse);
		field_details_panel.setWidget(0, 1, new Label());
		
		Label lbl_field_name = new Label(_i18n.getConstants().admin_field_detail_labelName(), false);
		lbl_field_name.addStyleName("labelTextBold");
		lbl_field_name.addStyleName("plainLabelText");
		
		_txt_fieldname = new TextBox();
		_txt_fieldname.setWidth("180px");
		
		field_details_panel.setWidget(1, 0, lbl_field_name);
		field_details_panel.setWidget(1, 1, _txt_fieldname);
		
		Label lbl_file_type = new Label(_i18n.getConstants().admin_field_detail_labelType());
		lbl_file_type.addStyleName("labelTextBold");
		lbl_file_type.addStyleName("plainLabelText");
		
		_txt_fieldtype = new TextBox();
		_txt_fieldtype.setWidth("180px");
		_txt_fieldtype.setEnabled(false);
		
		field_details_panel.setWidget(1, 2, lbl_file_type);
		field_details_panel.setWidget(1, 3, _txt_fieldtype);
		
		Label lbl_field_data_type = new Label(_i18n.getConstants().admin_field_detail_labelData());		
		lbl_field_data_type.addStyleName("labelTextBold");
		lbl_field_data_type.addStyleName("plainLabelText");
		
		_lst_field_datatype = new ListBox(false);
		_lst_field_datatype.setWidth("185px");
		
		field_details_panel.setWidget(2, 0, lbl_field_data_type);
		field_details_panel.setWidget(2, 1, _lst_field_datatype);
		
		Label lbl_max_length = new Label(_i18n.getConstants().admin_field_detail_labelMaxLen());
		lbl_max_length.addStyleName("labelTextBold");
		lbl_max_length.addStyleName("plainLabelText");
		
		_txt_max_length = new TextBox();
		_txt_max_length.setWidth("50px");
		
		field_details_panel.setWidget(2, 2, lbl_max_length);
		field_details_panel.setWidget(2, 3, _txt_max_length);
						
		_btn_field_save = new Button(_i18n.getConstants().controls_save());
		_btn_field_save.addStyleName("adminButton");
		
		_btn_field_reset = new Button(_i18n.getConstants().controls_reset());
		_btn_field_reset.addStyleName("adminButton");
		
		field_details_panel.setWidget(3, 1, _btn_field_save);
		field_details_panel.setWidget(3, 2, _btn_field_reset);
		
		_fields_panel.add(field_details_panel);
		
		_fields_presets_panel.add(_fields_panel);
	}
	
	private void buildPresetFieldsPanel()
	{
		_preset_fields_panel = new VerticalPanel();
		_preset_fields_panel.setStyleName("borderedBlock");
		_preset_fields_panel.setVisible(false);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_preset_fields_panel.add(lbl_dummy);	
		
		_lbl_preset_heading = new Label();
		_lbl_preset_heading.addStyleName("labelTextBold");
		_lbl_preset_heading.addStyleName("plainLabelText");
		
		_preset_fields_panel.add(_lbl_preset_heading);
		
		FlexTable preset_details_panel = new FlexTable();
		preset_details_panel.setCellSpacing(10);
		
		_lst_preset_field = new ExtendedListBox<PresetField>(false);
		
		_btn_newpresetfield = new Button(_i18n.getConstants().admin_field_preset_newButton());
		_btn_newpresetfield.addStyleName("adminButton");
		
		preset_details_panel.setWidget(0, 0, _lst_preset_field);
		preset_details_panel.setWidget(0, 1, _btn_newpresetfield);
		
		Label lbl_presetname = new Label(_i18n.getConstants().admin_field_preset_labelValue());
		lbl_presetname.addStyleName("labelTextBold");
		lbl_presetname.addStyleName("plainLabelText");
		
		_txt_preset_name = new TextBox();
		_txt_preset_name.setWidth("180px");
		
		preset_details_panel.setWidget(1, 0, lbl_presetname);
		preset_details_panel.setWidget(1, 1, _txt_preset_name);
		
		_btn_preset_save = new Button(_i18n.getConstants().controls_save());
		_btn_preset_save.addStyleName("adminButton");
		
		_btn_preset_reset = new Button(_i18n.getConstants().controls_reset());
		_btn_preset_reset.addStyleName("adminButton");
		
		preset_details_panel.setWidget(2, 0, _btn_preset_save);
		preset_details_panel.setWidget(2, 1, _btn_preset_reset);
		
		_preset_fields_panel.add(preset_details_panel);
		
		_fields_presets_panel.add(_preset_fields_panel);
	}
	
	@Override
	public Label getHeadingLabel() 
	{	
		return super.getTabHeading();
	}

	@Override
	public VerticalPanel getPresetFieldsPanel() 
	{
		return _preset_fields_panel;
	}

	@Override
	public RadioButton getRecordAttrRadioButton() 
	{
		return _btn_record_attr;
	}

	@Override
	public RadioButton getIndexRadioButton() 
	{
		return _btn_index;
	}

	@Override
	public RadioButton getTermAttrRadioButton()
	{
		return _btn_term_attr;
	}
	
	@Override
	public RadioButton getPresetAttrRadioButton() 
	{	
		return _btn_preset_attr;
	}
	
	@Override
	public RadioButton getTermSubAttrRadioButton() 
	{
		return _btn_term_sub_attr;
	}
	
	@Override
	public RadioButton getPresetSubAttrRadioButton() 
	{
		return _btn_preset_sub_attr;
	}
	
	@Override
	public ExtendedListBox<Field> getFieldsListBox() 
	{
		return _lst_field;
	}

	@Override
	public Button getNewFieldButton() 
	{
		return _btn_newfield;
	}

	@Override
	public TextBox getFieldNameTextBox() 
	{
		return _txt_fieldname;
	}

	@Override
	public TextBox getFieldTypeTextBox() 
	{
		return _txt_fieldtype;
	}

	@Override
	public ListBox getFieldDataTypeListBox() 
	{
		return _lst_field_datatype;
	}

	@Override
	public TextBox getMaxLengthTextBox() 
	{
		return _txt_max_length;
	}

	@Override
	public CheckBox getInUseCheckBox() 
	{
		return _chk_field_inuse;
	}

	@Override
	public Button getFieldSaveButton() 
	{
		return _btn_field_save;
	}

	@Override
	public Button getFieldResetButton() 
	{
		return _btn_field_reset;
	}

	@Override
	public Label getPresetHeadingLabel() 
	{
		return _lbl_preset_heading;
	}

	@Override
	public ExtendedListBox<PresetField> getPresetFieldListBox() 
	{
		return _lst_preset_field;
	}

	@Override
	public Button getNewPresetFieldButton() 
	{
		return _btn_newpresetfield;
	}

	@Override
	public TextBox getPresetNameTextBox() 
	{
		return _txt_preset_name;
	}

	@Override
	public Button getPresetFieldSaveButton() 
	{
		return _btn_preset_save;
	}

	@Override
	public Button getPresetFieldResetButton() 
	{
		return _btn_preset_reset;
	}
}
