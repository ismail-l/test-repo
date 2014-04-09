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

package tms2.client.termbrowser.view;

import tms2.client.i18n.Internationalization;
import tms2.client.termbrowser.presenter.ExportPresenter;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.FieldFilterTree;
import tms2.shared.TerminlogyObject;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ExportView extends Composite implements ExportPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private AttachedPanel _export_panel = null;
	
	private VerticalPanel _export_details_panel = null;
	
	private ListBox _lst_export = null;
	private TextBox _txt_filename = null;
	private CheckBox _chk_filename=  null;
	
	private ExtendedListBox<TerminlogyObject> _lst_source = null;
	private ExtendedListBox<TerminlogyObject> _lst_target = null;
	
	private Button _btn_select_all = null;
	
	private FieldFilterTree _field_filter_tree = null;
	
	private Button _btn_generate = null;
	private Button _btn_download = null;
	private Button _btn_close = null;
	
	private DeckPanel _export_type_panel = null;
	
	public ExportView(Button btn_parent)
	{
		_export_panel = new AttachedPanel(btn_parent);
		_export_panel.setWidth("780px");
		
		initWidget(_export_panel);
		
		_export_details_panel = new VerticalPanel();
		_export_details_panel.setSpacing(5);
		
		_export_panel.add(_export_details_panel);
		
		buildExportInformationPanel();
		buildExportTypePanel();
		buildButtonsPanel();
	}
	
	private void buildExportInformationPanel()
	{
		VerticalPanel export_info_panel = new VerticalPanel();
		export_info_panel.setWidth("780px");
		export_info_panel.setSpacing(5);
		
		HorizontalPanel details_panel = new HorizontalPanel();
		details_panel.setSpacing(10);
		
		Label lbl_export = new Label(_i18n.getConstants().export_exportAs());
		lbl_export.addStyleName("plainLabelText");
		
		details_panel.add(lbl_export);
		
		_lst_export = new ListBox(false);
		_lst_export.addItem(_i18n.getConstants().export_tab());
		_lst_export.addItem(_i18n.getConstants().export_odt());
		_lst_export.addItem(_i18n.getConstants().export_tbx());
		
		details_panel.add(_lst_export);
		
		Label lbl_filename = new Label(_i18n.getConstants().export_filename());
		lbl_filename.addStyleName("plainLabelText");
		
		details_panel.add(lbl_filename);
		
		_txt_filename = new TextBox();
		
		details_panel.add(_txt_filename);		
		
		_chk_filename = new CheckBox(_i18n.getConstants().export_include_field_names());
		
		details_panel.add(_chk_filename);
		
		export_info_panel.add(details_panel);
		
		_export_details_panel.add(export_info_panel);								
	}

	private void buildExportTypePanel()
	{
		_export_type_panel = new DeckPanel();
		_export_type_panel.setWidth("750px");
		
		_export_type_panel.add(buildTabExportPanel());
		_export_type_panel.add(buildODTExportPanel());
		_export_type_panel.add(buildTBXExportPanel());
		
		_export_type_panel.showWidget(0);
		
		_export_details_panel.add(_export_type_panel);
	}
	
	private DisclosurePanel buildTabExportPanel()
	{
		DisclosurePanel tab_export_panel = new DisclosurePanel(_i18n.getConstants().export_tab());		
		tab_export_panel.setWidth("750px");
		
		VerticalPanel tab_details_panel = new VerticalPanel();
		tab_details_panel.setStyleName("borderedBlock");
		tab_details_panel.setSpacing(10);
		tab_details_panel.setWidth("100%");
		
		HorizontalPanel tab_export_details_panel = new HorizontalPanel();
		tab_export_details_panel.setSpacing(5);
		
		Label lbl_source = new Label(_i18n.getConstants().export_source());
		lbl_source.addStyleName("plainLabelText");
		
		tab_export_details_panel.add(lbl_source);
		
		_lst_source = new ExtendedListBox<TerminlogyObject>(false);
		_lst_source.setWidth("300px");
		
		tab_export_details_panel.add(_lst_source);
		
		Label lbl_target = new Label(_i18n.getConstants().export_target());
		lbl_target.addStyleName("plainLabelText");
		
		tab_export_details_panel.add(lbl_target);
		
		_lst_target = new ExtendedListBox<TerminlogyObject>(false);
		_lst_target.setWidth("300px");
		
		tab_export_details_panel.add(_lst_target);
		
		tab_details_panel.add(tab_export_details_panel);
		
		tab_export_panel.add(tab_details_panel);
		
		return tab_export_panel;
	}
	
	private DisclosurePanel buildODTExportPanel()
	{
		DisclosurePanel odt_export_panel = new DisclosurePanel(_i18n.getConstants().export_odt());		
		odt_export_panel.setWidth("750px");		
		
		VerticalPanel odt_panel = new VerticalPanel();
		odt_panel.setStyleName("borderedBlock");
		odt_panel.setWidth("100%");
		odt_panel.setSpacing(5);
		
		HorizontalPanel odt_export_details_panel = new HorizontalPanel();
		odt_export_details_panel.setSpacing(5);
					
		Label lbl_prompt = new Label(_i18n.getConstants().filter_fields_prompt());
		lbl_prompt.addStyleName("labelTextBold");
		lbl_prompt.addStyleName("plainLabelText");
		
		odt_export_details_panel.add(lbl_prompt);
		
		_btn_select_all = new Button(_i18n.getConstants().export_activate_deactive());
		
		odt_export_details_panel.add(_btn_select_all);
		
		odt_panel.add(odt_export_details_panel);
		
		ScrollPanel scroller = new ScrollPanel();
		scroller.setWidth("100%");
		scroller.setHeight("300px");
		
		_field_filter_tree = new FieldFilterTree(false);
		_field_filter_tree.setWidth("100%");
		
		scroller.add(_field_filter_tree);
		
		odt_panel.add(scroller);
		
		odt_export_panel.add(odt_panel);
				
		return odt_export_panel;
	}
	
	private DisclosurePanel buildTBXExportPanel()
	{
		DisclosurePanel tbx_export_panel = new DisclosurePanel(_i18n.getConstants().export_tbx());
		tbx_export_panel.setWidth("750px");		

		VerticalPanel tbx_export_details = new VerticalPanel();
		tbx_export_details.setStyleName("borderedBlock");
		tbx_export_details.setWidth("100%");
		tbx_export_details.setSpacing(5);
		
		Label lbl_prompt = new Label(_i18n.getConstants().export_tbx_message());
		lbl_prompt.addStyleName("labelTextBold");
		lbl_prompt.addStyleName("plainLabelText");
		
		tbx_export_details.add(lbl_prompt);
				
		tbx_export_panel.add(tbx_export_details);
				
		return tbx_export_panel;
	}
	
	private void buildButtonsPanel()
	{
		VerticalPanel buttons_panel = new VerticalPanel();
		buttons_panel.setWidth("100%");
		buttons_panel.setStyleName("borderedBlock");
		buttons_panel.setSpacing(5);
		
		HorizontalPanel button_details_panel = new HorizontalPanel();
		button_details_panel.setSpacing(5);
		
		_btn_generate = new Button(_i18n.getMessages().export_generate(""));
		
		button_details_panel.add(_btn_generate);
		
		_btn_download = new Button(_i18n.getConstants().export_download());
		_btn_download.setEnabled(false);
		
		button_details_panel.add(_btn_download);
		
		_btn_close = new Button(_i18n.getConstants().controls_close());
		
		button_details_panel.add(_btn_close);
		
		buttons_panel.add(button_details_panel);
		
		_export_details_panel.add(buttons_panel);
	}
	
	@Override
	public AttachedPanel getExportPanel() 
	{	
		return _export_panel;
	}
	
	@Override
	public ListBox getExportListBox() 
	{	
		return _lst_export;
	}

	@Override
	public TextBox getExportFileNameTextBox() 
	{	
		return _txt_filename;
	}

	@Override
	public CheckBox getExportFileNameCheckBox() 
	{	
		return _chk_filename;
	}

	@Override
	public ExtendedListBox<TerminlogyObject> getSourceListBox() 
	{	
		return _lst_source;
	}

	@Override
	public ExtendedListBox<TerminlogyObject> getTargetListBox() 
	{	
		return _lst_target;
	}

	@Override
	public FieldFilterTree getFieldFilterTree() 
	{	
		return _field_filter_tree;
	}

	@Override
	public DeckPanel getExportTypePanel() 
	{	
		return _export_type_panel;
	}

	@Override
	public Button getGenerateButton() 
	{	
		return _btn_generate;
	}

	@Override
	public Button getDownloadButton() 
	{	
		return _btn_download;
	}

	@Override
	public Button getCloseButtonHandler() 
	{	
		return _btn_close;
	}

	@Override
	public Button getSelectAllButton() 
	{	
		return _btn_select_all;
	}
}
