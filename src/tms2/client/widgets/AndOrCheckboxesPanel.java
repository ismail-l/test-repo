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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Panel containing the AND & OR check boxes.  It is used to specify the inclusion of multiple defined filter constraints.
 * @author  Wildrich Fourie
 */
public class AndOrCheckboxesPanel extends HorizontalPanel
{
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	
	/**
	 * @uml.property  name="andCheckbox"
	 * @uml.associationEnd  
	 */
	private ExtendedCheckBox<Boolean> andCheckbox;
	/**
	 * @uml.property  name="orCheckbox"
	 * @uml.associationEnd  
	 */
	private ExtendedCheckBox<Boolean> orCheckbox;
	
	public AndOrCheckboxesPanel() 
	{
		// Styling
		this.setSpacing(10);
		this.setHorizontalAlignment(ALIGN_CENTER);
		// Add style by name?
		
		// AND Checkbox
		andCheckbox = new ExtendedCheckBox<Boolean>(constants.filter_and(), true);
		andCheckbox.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				orCheckbox.setChecked(!andCheckbox.isChecked());
			}
		});
		
		// Or Checkbox
		orCheckbox = new ExtendedCheckBox<Boolean>(constants.filter_or(), false);
		orCheckbox.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				andCheckbox.setChecked(!orCheckbox.isChecked());
			}
		});
		
		// Add the components
		this.add(andCheckbox);
		this.add(orCheckbox);
	}
	
	
	/**
	 * Gets the value selected for this set of checkboxes.
	 * @return
	 * <li> True = AND
	 * <li> False = OR
	 */
	protected boolean getValue()
	{
		return andCheckbox.isChecked();
	}
}
