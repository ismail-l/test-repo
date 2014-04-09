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
import tms2.client.termbrowser.presenter.InfoBarPresenter;
import tms2.client.widgets.FixedPanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author I. Lavangee
 *
 */
public class InfoBarView extends FixedPanel implements InfoBarPresenter.Display
{
	private Internationalization _i18n = Internationalization.getInstance();
	
	private Button _btn_filter_label = null;	
	private Image _image = null;
	private Button _btn_rec_num_label = null;
	private Button _btn_rec_num_display = null;
	private Button _btn_email = null;
	
	public InfoBarView()
	{
		super();
		
		addStyleName("InfoBar");
		
		init();
	}
	
	private void init()
	{
		HorizontalPanel layout_panel = new HorizontalPanel();
		
		_btn_filter_label = new Button(_i18n.getConstants().filter_label());
		_btn_filter_label.setEnabled(false);
		layout_panel.add(_btn_filter_label);
		
		_image = new Image();
		
		layout_panel.add(_image);
		
		_btn_rec_num_label = new Button(_i18n.getConstants().num_recs_label());
		_btn_rec_num_label.setEnabled(false);
		layout_panel.add(_btn_rec_num_label);
		
		_btn_rec_num_display = new Button("0");
		_btn_rec_num_display.setEnabled(false);
		layout_panel.add(_btn_rec_num_display);
		
		_btn_email = new Button(_i18n.getConstants().recordBrowse_emailadmin());
		layout_panel.add(_btn_email);
		
		layout_panel.setHorizontalAlignment(ALIGN_RIGHT);
		
		layout_panel.setCellHorizontalAlignment(_btn_filter_label, ALIGN_RIGHT);
		layout_panel.setCellHorizontalAlignment(_image, ALIGN_RIGHT);
		
		layout_panel.setCellVerticalAlignment(_image, ALIGN_MIDDLE);
		
		layout_panel.setCellHorizontalAlignment(_btn_rec_num_label, ALIGN_RIGHT);
		layout_panel.setCellHorizontalAlignment(_btn_rec_num_display, ALIGN_LEFT);
		
		add(layout_panel);
		setCellHorizontalAlignment(layout_panel, HasHorizontalAlignment.ALIGN_RIGHT);	
		getElement().getStyle().setRight(0, Unit.PX);
	}

	@Override
	public Button getEmailButton() 
	{		
		return _btn_email;
	}

	@Override
	public Button getNumRecLabelButton()
	{
		return _btn_rec_num_label;
	}

	@Override
	public Image getFilterImage() 
	{
		return _image;
	}

	@Override
	public Button getFilterLabelButton() 
	{
		return _btn_filter_label;
	}

	@Override
	public Button getNumRecButton() 
	{
		return _btn_rec_num_display;
	}
}
