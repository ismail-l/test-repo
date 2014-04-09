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
import tms2.client.termbrowser.presenter.RecordNavigatorPresenter;
import tms2.client.widgets.ExtendedListBox;
import tms2.shared.Field;
import tms2.shared.Project;
import tms2.shared.TermBase;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordNavigatorView extends Composite implements RecordNavigatorPresenter.Display
{
	private Internationalization _i18n = Internationalization.getInstance();
		
	private VerticalPanel _record_navigation_panel = null;
	private HorizontalPanel _navigation_panel = null;
	
	private PushButton _btn_first = null;
	private Anchor _anc_prev = null;
	private Label _lbl_browser = null;
	private TextBox _txt_browser_search = null;
	private Anchor _anc_next = null;
	private PushButton _btn_last = null;
	
	private PushButton _btn_open_search = null;
	
	private ExtendedListBox<TermBase> _lst_termbase = null;
	private ExtendedListBox<Project> _lst_project = null;
	private TextBox _txt_search = null;
	private ListBox _lst_search_options = null;
	private Button _btn_search = null;
	private PushButton _btn_close_search = null;
	
	private ExtendedListBox<Field> _lst_source = null;
	private ExtendedListBox<Field> _lst_target = null;
		
	public RecordNavigatorView()
	{
		_record_navigation_panel = new VerticalPanel();
		_record_navigation_panel.setWidth("100%");
		
		_navigation_panel = new HorizontalPanel();
		_navigation_panel.setSpacing(10);
		
		_record_navigation_panel.add(_navigation_panel);
		
		initWidget(_record_navigation_panel);
		
		buildRecordBrowserPanel();
		buildSearchPanel();
		buildSourceAndTargetPanel();
	}
	
	private void buildRecordBrowserPanel()
	{
		HorizontalPanel browse_panel = new HorizontalPanel();
		browse_panel.setSpacing(5);
		
		_lbl_browser = new Label(_i18n.getConstants().recordBrowse_label());
		_lbl_browser.addStyleName("labelTextBold");
		
		_btn_first = new PushButton(new Image("images/first.png"));		
		
		_anc_prev = new Anchor();		
		_anc_prev.getElement().getStyle().setVerticalAlign(VerticalAlign.BOTTOM);
		_anc_prev.addStyleName("highlightText");
		_anc_prev.addStyleName("browsingText");
					
		_txt_browser_search = new TextBox();
		_txt_browser_search.setWidth("200px");
		_txt_browser_search.addStyleName("highlightText");
		_txt_browser_search.addStyleName("browsingText");
		
		Style style = _txt_browser_search.getElement().getStyle();
		
		style.setTextAlign(TextAlign.CENTER);
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderWidth(1, Unit.PX);
		style.setBorderColor("#d3d3d3");
		
		_anc_next = new Anchor();		
		_anc_next.addStyleName("highlightText");
		_anc_next.addStyleName("browsingText");
		
		_btn_last = new PushButton(new Image("images/last.png"));
		
		browse_panel.add(_lbl_browser);
		browse_panel.add(_btn_first);
		browse_panel.add(_anc_prev);
		browse_panel.add(_txt_browser_search);
		browse_panel.add(_anc_next);
		browse_panel.add(_btn_last);
		
		//browse_panel.setCellVerticalAlignment(_lbl_browser, HasVerticalAlignment.ALIGN_MIDDLE);
		//browse_panel.setCellVerticalAlignment(_btn_first, HasVerticalAlignment.ALIGN_MIDDLE);
		//browse_panel.setCellVerticalAlignment(_anc_prev, HasVerticalAlignment.ALIGN_MIDDLE);
		//browse_panel.setCellVerticalAlignment(_txt_browser_search, HasVerticalAlignment.ALIGN_MIDDLE);
		//browse_panel.setCellVerticalAlignment(_anc_next, HasVerticalAlignment.ALIGN_MIDDLE);
		//browse_panel.setCellVerticalAlignment(_btn_last, HasVerticalAlignment.ALIGN_MIDDLE);
				
		_navigation_panel.add(browse_panel);
	}
	
	private void buildSearchPanel()
	{		
		HorizontalPanel search_panel = new HorizontalPanel();
		search_panel.setSpacing(5);
		search_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		Label lbl_search = new Label(_i18n.getConstants().recordBrowse_search());
		lbl_search.addStyleName("labelTextBold");
		
		_btn_open_search = new PushButton(new Image("images/search24.png"));
		
		search_panel.add(lbl_search);
		search_panel.add(_btn_open_search);
		
		//search_panel.setCellVerticalAlignment(lbl_search, HasVerticalAlignment.ALIGN_MIDDLE);
		//search_panel.setCellVerticalAlignment(_btn_open_search, HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel search_controls_panel = new HorizontalPanel();
		search_controls_panel.setSpacing(5);
				
		_lst_termbase = new ExtendedListBox<TermBase>(false);
		_lst_project = new ExtendedListBox<Project>(false);
		
		search_controls_panel.add(_lst_termbase);
		search_controls_panel.add(_lst_project);
						
		_txt_search = new TextBox();
		_txt_search.getElement().setAttribute("placeholder", "search...");	
		_txt_search.setWidth("150px");
		
		_lst_search_options = new ListBox(false);
		_lst_search_options.setWidth("150px");
		
		_btn_search = new Button(_i18n.getConstants().search_button());
		
		_btn_close_search = new PushButton(new Image("images/done16.png"));
		
		search_controls_panel.add(_txt_search);
		search_controls_panel.add(_lst_search_options);
		search_controls_panel.add(_btn_search);
		search_controls_panel.add(_btn_close_search);
							
		search_panel.add(search_controls_panel);
		
		_navigation_panel.add(search_panel);
		
		// Add this panel to the main panel. The search controls
		// are displayed under the _navaigation_panel.
		_record_navigation_panel.add(search_controls_panel);		
	}
	
	private void buildSourceAndTargetPanel()
	{
		HorizontalPanel source_target_panel = new HorizontalPanel();
		source_target_panel.setSpacing(5);
		
		_lst_source = new ExtendedListBox<Field>(false);
		_lst_target = new ExtendedListBox<Field>(false);
		
		_lst_source.setEnabled(false);
		_lst_target.setEnabled(false);
		
		source_target_panel.add(_lst_source);
		source_target_panel.add(_lst_target);
		
		source_target_panel.setCellVerticalAlignment(_lst_source, HasVerticalAlignment.ALIGN_MIDDLE);
		source_target_panel.setCellVerticalAlignment(_lst_target, HasVerticalAlignment.ALIGN_MIDDLE);
		
		_navigation_panel.add(source_target_panel);
	}
	
	@Override
	public PushButton getFirstRecordPushButton() 
	{	
		return _btn_first;
	}

	@Override
	public Anchor getPreviousRecordAnchor() 
	{	
		return _anc_prev;
	}

	@Override
	public Label getBrowseLabel()
	{
		return _lbl_browser;
	}
	
	@Override
	public TextBox getBrowserSearchTextBox() 
	{	
		return _txt_browser_search;
	}

	@Override
	public Anchor getNextRecordAnchor() 
	{	
		return _anc_next;
	}
	
	@Override
	public PushButton getLastRecordPushButton()
	{
		return _btn_last;
	}
	
	@Override
	public PushButton getOpenSearchPushButton() 
	{	
		return _btn_open_search;
	}
	
	@Override
	public TextBox getSearchTextBox() 
	{	
		return _txt_search;
	}

	@Override
	public ListBox getSearchOptionsListBox() 
	{	
		return _lst_search_options;
	}

	@Override
	public Button getSearchButton() 
	{	
		return _btn_search;
	}

	@Override
	public PushButton getCloseSearchPushButton() 
	{	
		return _btn_close_search;
	}


	@Override
	public ExtendedListBox<TermBase> getTermBaseListBox() 
	{	
		return _lst_termbase;
	}

	@Override
	public ExtendedListBox<Project> getProjectListBox()
	{	
		return _lst_project;
	}
	
	@Override
	public ExtendedListBox<Field> getSourceLanguageListBox() 
	{	
		return _lst_source;
	}

	@Override
	public ExtendedListBox<Field> getTargetLanguageListBox() 
	{
		return _lst_target;
	}
}
