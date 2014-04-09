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

import java.util.ArrayList;
import java.util.Iterator;

import tms2.client.i18n.Internationalization;
import tms2.shared.Project;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Assigner control for Topics. Displays topics that are assigned and unassigned and can assign and unassign a topic to an Input model.
 * @author  Ismail Lavangee
 */
public class UserProjectAccessPanel extends DockPanel 
{	
	private static Internationalization _i18n = Internationalization.getInstance();	
	
	private Button _assign = new Button(">");
	private Button _remove = new Button("<");
	private Button _assign_all = new Button(">>");
	private Button _remove_all = new Button("<<");
	private ExtendedListBox<Project> _available = new ExtendedListBox<Project>(true);
	private ExtendedListBox<Project> _assigned = new ExtendedListBox<Project>(true);
	
	public UserProjectAccessPanel()
	{				
		init();
		reset();
	}
	
	public void setAvailableProjects(ArrayList<Project> available_topics)
	{
		_available.clear();
		
		Iterator<Project> iter = available_topics.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			_available.addItem(project.getProjectName(), project.getProjectId(), project);
		}
		
		_available.addChangeHandler(new ChangeHandler()
		{		
			@Override
			public void onChange(ChangeEvent event) 
			{						
				if (_available.isAnyItemSelected())
					_assign.setEnabled(true);
				else
					_assign.setEnabled(false);
			}
		});
		
		setButtonsInitialState();
	}
	
	public void setAssignedProjects(ArrayList<Project> assigned_topics)
	{
		_assigned.clear();
		
		Iterator<Project> iter = assigned_topics.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			_assigned.addItem(project.getProjectName(), project.getProjectId(), project);
		}
		
		_assigned.addChangeHandler(new ChangeHandler()
		{			
			@Override
			public void onChange(ChangeEvent event) 
			{		
				if (_assigned.isAnyItemSelected())
					_remove.setEnabled(true);
				else
					_remove.setEnabled(false);
			}
		});
		
		setButtonsInitialState();
	}
	
	private void init()
	{		
		setStyleName("cw-DockPanel");
	    setSpacing(4);
	    setHorizontalAlignment(DockPanel.ALIGN_CENTER);
			    		    
	    VerticalPanel left_panel = buildLeftPanel();
	    add(left_panel, WEST);
	    setCellHorizontalAlignment(left_panel, HasHorizontalAlignment.ALIGN_LEFT);
	    
	    VerticalPanel center_panel = buildCenterPanel();
	    add(center_panel, CENTER);
	    setCellHorizontalAlignment(center_panel, HasHorizontalAlignment.ALIGN_CENTER);
	    setCellVerticalAlignment(center_panel, HasVerticalAlignment.ALIGN_MIDDLE);
	    
	    VerticalPanel right_panel = buildRightPanel();
	    add(right_panel, EAST);
	    setCellHorizontalAlignment(right_panel, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	private VerticalPanel buildLeftPanel()
	{
	    VerticalPanel left_panel = new VerticalPanel();
	    left_panel.setWidth("33%");
	    left_panel.setSpacing(5);
	    	    	    
		Label lbl_available = new Label(_i18n.getConstants().controls_lbl_available(), false);
		lbl_available.addStyleName("labelTextBold");
		lbl_available.addStyleName("plainLabelText");
		left_panel.add(lbl_available);				
				
		left_panel.add(_available);		
		
		_available.setWidth("200px");
		_available.setHeight("100px");
		//_available.setStyleName("AssignControlListBox");
		
		return left_panel;
	}
	
	private VerticalPanel buildCenterPanel()
	{
	    VerticalPanel center_panel = new VerticalPanel();
	    center_panel.setWidth("33%");
	    center_panel.setSpacing(5);
	    
	    center_panel.add(_assign);
	    _assign.setStyleName("AssignControlButton");	    
	    _assign.addClickHandler(new ClickHandler()
	    {		
			@Override
			public void onClick(ClickEvent event) 
			{
				switchItem(_available, _assigned, _assign, _remove, _assign_all, _remove_all);
			}
		});
	    
	    center_panel.add(_remove);
	    _remove.setStyleName("AssignControlButton");
	    _remove.addClickHandler(new ClickHandler()
	    {		
			@Override
			public void onClick(ClickEvent event) 
			{
				switchItem(_assigned, _available, _remove, _assign, _remove_all, _assign_all);		
			}
		});
	    
	    center_panel.add(_assign_all);
	    _assign_all.setStyleName("AssignControlButton");
	    _assign_all.addClickHandler(new ClickHandler()
	    {		
			@Override
			public void onClick(ClickEvent event) 
			{			
				switchAllItems(_available, _assigned, _assign_all, _remove_all);
			}
		});
	    
	    center_panel.add(_remove_all);
	    _remove_all.setStyleName("AssignControlButton");
	    _remove_all.addClickHandler(new ClickHandler()
	    {			
			@Override
			public void onClick(ClickEvent event) 
			{			
				switchAllItems(_assigned, _available, _remove_all, _assign_all);
			}
		});	    	    	    	    
		
		return center_panel;
	}
	
	private VerticalPanel buildRightPanel()
	{
	    VerticalPanel right_panel = new VerticalPanel();
	    right_panel.setWidth("33%");
	    right_panel.setSpacing(5);	    	    	    	
		
		Label lbl_assigned = new Label(_i18n.getConstants().controls_lbl_assigned(), false);
		lbl_assigned.addStyleName("labelTextBold");
		lbl_assigned.addStyleName("plainLabelText");
		right_panel.add(lbl_assigned);		
		
		right_panel.add(_assigned);		
		
		_assigned.setWidth("200px");
		_assigned.setHeight("100px");
		//_assigned.setStyleName("AssignControlListBox");
		
		return right_panel;
	}
	
	private void switchItem(ExtendedListBox<Project> from_list, ExtendedListBox<Project> to_list, 
							Button from_button, Button to_button, 
							Button all_from_button, Button all_to_button)
	{		
		ArrayList<Project> from_topics = new ArrayList<Project>();
		ArrayList<Project> to_topics = new ArrayList<Project>();
		to_topics.addAll(to_list.getItems());
					
		for (int i = 0; i < from_list.getItemCount(); i++)
		{
			if (from_list.isItemSelected(i))
				to_topics.add(from_list.getItemAtIndex(i));
			else
				from_topics.add(from_list.getItemAtIndex(i));
		}
				
		from_list.clear();
		to_list.clear();
		
		Iterator<Project> iter = null;
		iter = from_topics.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			from_list.addItem(project.getProjectName(), project.getProjectId(), project);
		}
		
		iter = to_topics.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			to_list.addItem(project.getProjectName(), project.getProjectId(), project);
		}
		
		from_button.setEnabled(false);				
		to_button.setEnabled(false);
		
		if (from_list.getItemCount() == 0)
		{
			all_from_button.setEnabled(false);
			all_to_button.setEnabled(true);
		}
		else
		{
			all_from_button.setEnabled(true);
			all_to_button.setEnabled(false);
		}						
	}
	
	private void switchAllItems(ExtendedListBox<Project> from_list, ExtendedListBox<Project> to_list, 
								Button from_button, Button to_button)
	{		
		ArrayList<Project> from_topics = new ArrayList<Project>();
		from_topics.addAll(from_list.getItems());
		
		ArrayList<Project> to_topics = new ArrayList<Project>();
		to_topics.addAll(to_list.getItems());
				
		from_topics.addAll(to_topics);
		
		from_list.clear();
		to_list.clear();
		
		Iterator<Project> iter = from_topics.iterator();
		while (iter.hasNext())	
		{
			Project project = iter.next();
			to_list.addItem(project.getProjectName(), project.getProjectId(), project);
		}
								
		_assign.setEnabled(false);
		_remove.setEnabled(false);
		
		from_button.setEnabled(false);
		to_button.setEnabled(true);						
	}
	
	private void setButtonsInitialState()
	{
	    _assign.setEnabled(false);
	    _remove.setEnabled(false);
	    
    	_assign_all.setEnabled(true);
    	_remove_all.setEnabled(true);	
	    
	    if (_assigned.getItemCount() == 0)
	    {
	    	_assign_all.setEnabled(true);
	    	_remove_all.setEnabled(false);	    	
	    }
	    
	    if (_available.getItemCount() == 0)
	    {
	    	_assign_all.setEnabled(false);
	    	_remove_all.setEnabled(true);	
	    }
	    
	    if (_available.getItemCount() == 0 && _assigned.getItemCount() == 0)
	    {
	    	_assign_all.setEnabled(false);
	    	_remove_all.setEnabled(false);	
	    }
	}
	
	public ArrayList<Project> getAssignedProjects()
	{
		return _assigned.getItems();
	}
	
	public void reset()
	{
		_available.clear();
		_assigned.clear();
		
		setButtonsInitialState();
	}
}
