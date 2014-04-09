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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Class representing a CheckBox that is capable of taking any type.
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 */
public class ExtendedCheckBox<T> extends Composite implements HasClickHandlers {

	/**
	 * @uml.property  name="checkBox"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private CheckBox checkBox = null;
	/**
	 * @uml.property  name="value"
	 */
	private T value = null;
	/**
	 * @uml.property  name="panel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private SimplePanel panel = new SimplePanel();
	
	public ExtendedCheckBox(String label) {
		this(label, null, false);
	}
	
	public ExtendedCheckBox(String label, T value) {
		this(label, value, false);
	}
	
	public ExtendedCheckBox(String label, T value, boolean asHTML) {
		if (label != null)
			this.checkBox = new CheckBox(label, asHTML);
		else
			this.checkBox = new CheckBox();
		if (value instanceof Boolean && ((Boolean)value).booleanValue())
			checkBox.setValue(true);
		
		this.checkBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkBox.isEnabled())
				{
					if (checkBox.getValue())
						setFocussed(true);
					else
						setFocussed(false);
				}				
			}
		});
		
		this.checkBox.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				setFocussed(true);
			}
		});
		
		this.checkBox.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (!checkBox.getValue())
					setFocussed(false);
			}
		});
		
		
		this.panel.add(this.checkBox);
		//this.panel.setStyleName("CheckBoxPanel");
		//this.checkBox.setStyleName("CheckBoxPanelButton");
		this.value = value;
		initWidget(this.panel);
	}
	
	public void setEnabled(boolean enabled)
	{
		this.checkBox.setEnabled(enabled);
	}
	
	public void setCheckBoxStyleName(String styleName)
	{
		this.getCheckBox().setStyleName(styleName);
	}
	
	public void setChecked(boolean checked)
	{
		checkBox.setValue(checked);
		setFocussed(checked);
	}
	
	public boolean isChecked()
	{
		return checkBox.getValue().booleanValue();
	}
	
	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value
	 * @uml.property  name="value"
	 */
	public void setValue(T value) {
		this.value = value;
	}

	public void setPanelStyleName(String styleName)
	{
		this.panel.setStyleName(styleName);
	}
	
	/**
	 * @return
	 * @uml.property  name="checkBox"
	 */
	public CheckBox getCheckBox() {
		return checkBox;
	}

	/**
	 * @param checkBox
	 * @uml.property  name="checkBox"
	 */
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public void setFocussed(boolean focussed)
	{
		if (focussed)
		{
//			this.panel.addStyleDependentName("focussed");
//			this.checkBox.addStyleDependentName("focussed");
		}
		else
		{
//			this.panel.removeStyleDependentName("focussed");
//			this.checkBox.removeStyleDependentName("focussed");
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return this.checkBox.addClickHandler(handler);
	}
}
