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
import tms2.client.termbrowser.presenter.RecordRenderingPresenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordRenderingView extends Composite implements RecordRenderingPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _rendering_panel = null;
	private VerticalPanel _record_data_panel = null;
	;
	private Label _lbl_recordid = null;
	private Label _lbl_project = null;
	private ListBox _lst_project = null;
	
	public RecordRenderingView()
	{
		_rendering_panel = new VerticalPanel();
		_rendering_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		_rendering_panel.setWidth((Window.getClientWidth() - 50) + "px");
		//_rendering_panel.setHeight((Window.getClientHeight() - 60) + "px");
		
		initWidget(_rendering_panel);
		
		buildDetailsPanel();
		buildRecordDataPanel();
	}
	
	private void buildDetailsPanel()
	{	
		VerticalPanel details_panel = new VerticalPanel();
		details_panel.setWidth("100%");
		
		HorizontalPanel dummy = new HorizontalPanel();
		dummy.setWidth("100%");
		dummy.setStyleName("borderedBlock");
				
		details_panel.add(dummy);
		
		HorizontalPanel record_id_panel = new HorizontalPanel();
		record_id_panel.setSpacing(5);
				
		Label lbl_recordid = new Label(_i18n.getConstants().recordRender_recordId(), false);
		
		_lbl_recordid = new Label();
		_lbl_recordid.addStyleName("plainLabelText");
		
		record_id_panel.add(lbl_recordid);
		record_id_panel.add(_lbl_recordid);
					
		details_panel.add(record_id_panel);
		
		dummy = new HorizontalPanel();
		dummy.setWidth("100%");
		dummy.setStyleName("borderedBlock");
				
		details_panel.add(dummy);
		
		HorizontalPanel project_panel = new HorizontalPanel();
		project_panel.setSpacing(5);
		
		_lbl_project = new Label(_i18n.getConstants().recordRender_project_selected());
		
		_lst_project = new ListBox(false);
		_lst_project.addStyleName("projectListBox");
		_lst_project.setWidth("100%");
		_lst_project.setEnabled(false);
		_lst_project.setVisibleItemCount(1);
		_lst_project.setSelectedIndex(-1);
		
		project_panel.add(_lbl_project);
		project_panel.add(_lst_project);
		
		details_panel.add(project_panel);
				
		_rendering_panel.add(details_panel);		
	}
	
	private void buildRecordDataPanel()
	{		
		_record_data_panel = new VerticalPanel();
		_record_data_panel.setSpacing(5);
		_record_data_panel.setWidth("100%");
		_record_data_panel.setHeight("100%");
				
		_rendering_panel.add(_record_data_panel);		
	}

	@Override
	public Label getRecordIdLabel() 
	{	
		return _lbl_recordid;
	}

	@Override
	public ListBox getProjectsListBox() 
	{	
		return _lst_project;
	}

	@Override
	public VerticalPanel getRecordDataPanel() 
	{	
		return _record_data_panel;
	}

	@Override
	public Label getProjectLabel() 
	{
		return _lbl_project;
	}
}
