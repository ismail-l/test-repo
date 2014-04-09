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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Contains panel.
 * @author  Wildrich Fourie
 */
public class ContainsPanel extends HorizontalPanel
{	
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	
	/**
	 * @uml.property  name="contains"
	 */
	private boolean contains = true;
	/**
	 * @uml.property  name="containsListBox"
	 * @uml.associationEnd  
	 */
	private ExtendedListBox<Boolean> containsListBox = new ExtendedListBox<Boolean>(false);
	
	public ContainsPanel()
	{
		super();
		init();
	}
	
	private void init()
	{
		containsListBox.addItem(constants.filter_contains(), "contains", true);
		containsListBox.addItem(constants.files_doesNotContain(), "does not contain", false);
		containsListBox.setSelectedIndex(0);
		contains = true;
		
		containsListBox.addChangeHandler(new ChangeHandler() 
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onChange(ChangeEvent event) 
			{
				contains = ((ExtendedListBox<Boolean>)event.getSource()).getSelectedItem();
			}
		});
		this.add(containsListBox);
	}
	
	
	/** 
	 * Returns the selection made on this panel.
	 * @return
	 * <li> TRUE = Contains
	 * <li> FALSE = Does not contain
	 */
	public boolean contains()
	{
		return contains;
	}
	
	
	/**
	 * Returns the ContainsListBox; used to attach an additional listener.
	 * @uml.property  name="containsListBox"
	 */
	public ExtendedListBox<Boolean> getContainsListBox()
	{
		return containsListBox;
	}
}