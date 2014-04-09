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
import tms2.client.termbrowser.presenter.ControlBarPresenter;
import tms2.client.widgets.FixedPanel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ControlBarView extends FixedPanel implements ControlBarPresenter.Display
{
	private Internationalization _i18n = Internationalization.getInstance();
	
	private Button _btn_add = null;
	private Button _btn_edit = null;
	private Button _btn_delete = null;
	private Button _btn_filter = null;
	private Button _btn_export = null;
	private Button _btn_insert = null;
	
	public ControlBarView()
	{
		super();
			    
	    addStyleName("controlBar");
	    
	    init();
	}
	
	private void init()
	{
		HorizontalPanel layout_panel = new HorizontalPanel();
		
		_btn_add = new Button(_i18n.getConstants().recordBrowse_createRecord());
		layout_panel.add(_btn_add);
		
		_btn_edit = new Button(_i18n.getConstants().recordBrowse_editRecord());
		layout_panel.add(_btn_edit);
		
		_btn_delete = new Button(_i18n.getConstants().recordBrowse_deleteRecord());
		layout_panel.add(_btn_delete);
		
		_btn_filter = new Button(_i18n.getConstants().filter_openFilter());
		layout_panel.add(_btn_filter);
		
		_btn_export = new Button(_i18n.getConstants().filter_export());
		layout_panel.add(_btn_export);
		
		_btn_insert = new Button(_i18n.getConstants().controls_insert());
		layout_panel.add(_btn_insert);
		
		add(layout_panel);			
	}

	@Override
	public Button getAddButton() 
	{
		return _btn_add;
	}

	@Override
	public Button getEditButton() 
	{	
		return _btn_edit;
	}

	@Override
	public Button getDeleteButton() 
	{	
		return _btn_delete;
	}

	@Override
	public Button getFilterButton() 
	{	
		return _btn_filter;
	}

	@Override
	public Button getExportButton() 
	{	
		return _btn_export;
	}

	@Override
	public Button getInsertButton() 
	{	
		return _btn_insert;
	}

}
