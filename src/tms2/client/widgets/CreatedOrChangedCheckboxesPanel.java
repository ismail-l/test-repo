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


import tms2.client.i18n.TMSConstants;
import tms2.shared.Filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Panel containing the CREATED, CHANGED and CREATED OR CHANGED check boxes. This is used to define the role of a selected User or Date when defining a Filter.
 * @author  Wildrich Fourie
 */
public class CreatedOrChangedCheckboxesPanel extends HorizontalPanel
{
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	
	/**
	 * @uml.property  name="createdCb"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private CheckBox createdCb;
	/**
	 * @uml.property  name="changedCb"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private CheckBox changedCb;
	/**
	 * @uml.property  name="createOrChangeCb"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private CheckBox createOrChangeCb;
	
	/**
	 * @uml.property  name="cbh"
	 * @uml.associationEnd  
	 */
	private CheckBoxHandler cbh = new CheckBoxHandler();
	
	
	public CreatedOrChangedCheckboxesPanel() 
	{
		this.setSpacing(10);
		
		createdCb = new CheckBox(constants.filter_created());
		changedCb = new CheckBox(constants.filter_changed());
		createOrChangeCb = new CheckBox(constants.filter_createdOrChanged());
		
		createdCb.setValue(true);
		changedCb.setValue(false);
		createOrChangeCb.setValue(false);
		
		createdCb.addClickHandler(cbh);
		changedCb.addClickHandler(cbh);
		createOrChangeCb.addClickHandler(cbh);
		
		this.add(createdCb);
		this.add(changedCb);
		this.add(createOrChangeCb);
	}
	

	/** Returns the value selected. 0 = Error. */ 
	public int getValue()
	{
		if(createdCb.getValue())
			return Filter.UserFilterType.CREATED;
		else if(changedCb.getValue())
			return Filter.UserFilterType.CHANGED;
		else if(createOrChangeCb.getValue())
			return Filter.UserFilterType.CREATED_OR_CHANGED;
		else
			return 0;
	}
	
	
	/** Sets the selected value based on the Filter.UserFilterType types. */
	public void setValue(int userFilterType)
	{
		createdCb.setValue(false);
		changedCb.setValue(false);
		createOrChangeCb.setValue(false);
		
		switch(userFilterType)
		{
			case Filter.UserFilterType.CREATED:
				createdCb.setValue(true);
				break;
			case Filter.UserFilterType.CHANGED:
				changedCb.setValue(true);
				break;
			case Filter.UserFilterType.CREATED_OR_CHANGED:
				createOrChangeCb.setValue(true);
				break;
			default:
				createdCb.setValue(true);
		}
	}
	
	
	// The handler for the checkboxes. Only one may be checked.
	private class CheckBoxHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event)
		{
			createdCb.setValue(false);
			changedCb.setValue(false);
			createOrChangeCb.setValue(false);
			
			CheckBox selected = (CheckBox)event.getSource();
			selected.setValue(true);
		}
	}
}
