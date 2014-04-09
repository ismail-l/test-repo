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
import tms2.client.termbrowser.presenter.RecordEditorPresenter;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.UserProjectAccessPanel;
import tms2.shared.Project;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordEditorView extends Composite implements RecordEditorPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
			 
	private VerticalPanel _editor_panel = null;

	private AttachedPanel _edit_controls_panel = null;	
	private VerticalPanel _user_project_panel = null;
	private HorizontalPanel _prompt_panel = null;
	private VerticalPanel _edit_details_panel = null;
	private VerticalPanel _record_attrs_panel = null;
	private VerticalPanel _terms_panel = null;
	private HorizontalPanel _termbase_details_panel = null;	
	private Label _lbl_termbase = null;
	private ExtendedListBox<TermBase> _lst_termbase = null;
	private Label _lbl_default_project = null;
	private ExtendedListBox<Project> _lst_default_project = null;
	private Label _lbl_terminology_objects = null;
	private ExtendedListBox<TerminlogyObject> _lst_terminology_objects = null;
	private Label _lbl_prompt = null;
	private UserProjectAccessPanel _user_project_assigner = null;
	private Button _btn_save = null;
	private Button _btn_cancel = null;
	
	public RecordEditorView(Button btn_parent)
	{		
		_editor_panel = new VerticalPanel();
		_editor_panel.setSpacing(0);
		_editor_panel.setWidth((Window.getClientWidth() - 50) + "px");	
		_editor_panel.setHeight("100%");
		
		initWidget(_editor_panel);				
			
		buildAttachedPoupPanel(btn_parent);		
		buildTermBasePanel();
		buildUserProjectPanel();
		buildPromptLabelPanel();
		buildEditDetailsPanel();
	}
	
	private void buildAttachedPoupPanel(Button btn_parent)
	{
		_edit_controls_panel = new AttachedPanel(btn_parent);
		
		HorizontalPanel controls_panel = new HorizontalPanel();
		controls_panel.setSpacing(5);
		
		_btn_save = new Button(_i18n.getConstants().controls_save());;
		_btn_cancel = new Button(_i18n.getConstants().controls_cancel());
		
		controls_panel.add(_btn_save);
		controls_panel.add(_btn_cancel);
		
		_edit_controls_panel.add(controls_panel);
		
		_editor_panel.add(_edit_controls_panel);
	}		
	
	private void buildTermBasePanel()
	{
		VerticalPanel termbase_panel = new VerticalPanel();
		termbase_panel.setStyleName("borderedBlock");
		termbase_panel.setSpacing(5);
		termbase_panel.setWidth("100%");
		
		_termbase_details_panel = new HorizontalPanel();
		_termbase_details_panel.setSpacing(5);
		
		_lbl_termbase = new Label(_i18n.getConstants().recordEdit_selectedDB());
		_lbl_termbase.addStyleName("labelTextBold");
		_lbl_termbase.addStyleName("plainLabelText");
		
		_lst_termbase = new ExtendedListBox<TermBase>(false);
		
		_termbase_details_panel.add(_lbl_termbase);
		_termbase_details_panel.add(_lst_termbase);
		
		_lbl_default_project = new Label("Default project:");
		_lbl_default_project.addStyleName("labelTextBold");
		_lbl_default_project.addStyleName("plainLabelText");
		
		_lst_default_project = new ExtendedListBox<Project>(false);
		_lst_default_project.setEnabled(false);
		
		_termbase_details_panel.add(_lbl_default_project);
		_termbase_details_panel.add(_lst_default_project);
		
		termbase_panel.add(_termbase_details_panel);
		
		_editor_panel.add(termbase_panel);
	}
	
	private void buildUserProjectPanel()
	{
		_user_project_panel = new VerticalPanel();
		_user_project_panel.setStyleName("borderedBlock");
		_user_project_panel.setSpacing(5);
		_user_project_panel.setWidth("100%");
						
		_user_project_assigner = new UserProjectAccessPanel();
		_user_project_assigner.reset();
				
		_user_project_panel.add(_user_project_assigner);
		
		_editor_panel.add(_user_project_panel);				
	}
	
	private void buildPromptLabelPanel()
	{
		VerticalPanel prompt_details_panel = new VerticalPanel();
		prompt_details_panel.setStyleName("borderedBlock");
		prompt_details_panel.setSpacing(4);
		prompt_details_panel.setWidth("100%");
		
		_prompt_panel = new HorizontalPanel();		
		_prompt_panel.setSpacing(5);
		
		_lbl_prompt = new Label(_i18n.getConstants().recordEdit_create_info());
		_lbl_prompt.addStyleName("plainLabelText");
		
		_lbl_terminology_objects = new Label(_i18n.getConstants().recordEdit_terminology_objects());
		_lbl_terminology_objects.addStyleName("plainLabelText");
		
		_lst_terminology_objects = new ExtendedListBox<TerminlogyObject>(false);
		
		_prompt_panel.add(_lbl_prompt);
		_prompt_panel.add(_lbl_terminology_objects);
		_prompt_panel.add(_lst_terminology_objects);
		
		prompt_details_panel.add(_prompt_panel);
		
		_editor_panel.add(prompt_details_panel);
	}
	
	private void buildEditDetailsPanel()
	{				
		_edit_details_panel = new VerticalPanel();
		_edit_details_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		_edit_details_panel.setSpacing(5);
		_edit_details_panel.setWidth("100%");
		
		_record_attrs_panel = new VerticalPanel();
		_record_attrs_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		_record_attrs_panel.setSpacing(5);
		_record_attrs_panel.setWidth("100%");
		
		_terms_panel = new VerticalPanel();
		_terms_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		_terms_panel.setSpacing(5);
		_terms_panel.setWidth("100%");
		
		_edit_details_panel.add(_record_attrs_panel);
		_edit_details_panel.add(_terms_panel);
		
		_edit_details_panel.setCellVerticalAlignment(_record_attrs_panel, HasVerticalAlignment.ALIGN_TOP);
		_edit_details_panel.setCellVerticalAlignment(_terms_panel, HasVerticalAlignment.ALIGN_TOP);
						
		_editor_panel.add(_edit_details_panel);
	}
	
	@Override
	public VerticalPanel getEditorPanel()
	{
		return _editor_panel;
	}
	
	@Override
	public AttachedPanel getControlsPanel() 
	{
		return _edit_controls_panel;
	}

	@Override
	public Button getSaveButton() 
	{	
		return _btn_save;
	}

	@Override
	public Button getCancelButton() 
	{
		return _btn_cancel;
	}

	@Override
	public ExtendedListBox<TermBase> getTermBaseListBox() 
	{	
		return _lst_termbase;
	}
	
	@Override
	public Label getUserAccessPromptLabel() 
	{	
		return _lbl_prompt;
	}

	@Override
	public UserProjectAccessPanel getUserProjectAssigner() 
	{	
		return _user_project_assigner;
	}

	@Override
	public VerticalPanel getEditDetailsPanel() 
	{	
		return _edit_details_panel;
	}

	@Override
	public VerticalPanel getRecordAttributesPanel() 
	{	
		return _record_attrs_panel;
	}

	@Override
	public VerticalPanel getTermsPanel() 
	{	
		return _terms_panel;
	}

	@Override
	public Label getTermBaseLabel() 
	{	
		return _lbl_termbase;
	}

	@Override
	public Label getDefaultProjectLabel() 
	{	
		return _lbl_default_project;
	}

	@Override
	public ExtendedListBox<Project> getDefaultProjectListBox() 
	{	
		return _lst_default_project;
	}

	@Override
	public VerticalPanel getUserProjectPanel() 
	{	
		return _user_project_panel;
	}

	@Override
	public HorizontalPanel getPromptPanel() 
	{	
		return _prompt_panel;
	}

	@Override
	public HorizontalPanel getTermBaseDetailsPanel() 
	{	
		return _termbase_details_panel;
	}

	@Override
	public Label getTerminologyLabel()
	{	
		return _lbl_terminology_objects;
	}

	@Override
	public ExtendedListBox<TerminlogyObject> getTerminologyListBox() 
	{	
		return _lst_terminology_objects;
	}
}
