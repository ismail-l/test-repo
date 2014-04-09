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

import java.util.Date;

import tms2.client.i18n.TMSConstants;
import tms2.client.i18n.TMSMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Integrated and refined date picker component.
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 */
public class ExtendedDatePicker extends Composite implements HasValueChangeHandlers<Date> 
{
	/**
	 * @uml.property  name="constants"
	 * @uml.associationEnd  
	 */
	private static final TMSConstants constants = GWT.create(TMSConstants.class);
	/**
	 * @uml.property  name="messages"
	 * @uml.associationEnd  
	 */
	private static final TMSMessages messages = GWT.create(TMSMessages.class);
	
	/**
	 * @uml.property  name="plus"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PushButton plus = new PushButton(new Image("images/bullet_toggle_plus.png"));
	/**
	 * @uml.property  name="minus"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PushButton minus = new PushButton(new Image("images/bullet_toggle_minus.png"));
	/**
	 * @uml.property  name="editButton"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	final PushButton editButton = new PushButton(new Image("images/calendar_edit.png"));
	/**
	 * @uml.property  name="resetButton"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	final PushButton resetButton = new PushButton(new Image("images/cross16.png"));
	
	/**
	 * @uml.property  name="datePicker"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private DatePicker datePicker = new DatePicker();
	/**
	 * @uml.property  name="currentValue"
	 */
	private Date currentValue;
	/**
	 * @uml.property  name="minimumDate"
	 */
	private Date minimumDate;
	/**
	 * @uml.property  name="maximumDate"
	 */
	private Date maximumDate;
	/**
	 * @uml.property  name="open"
	 */
	private boolean open = false;
	
	/**
	 * @uml.property  name="headingLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	final Label headingLabel = new Label("", false);
	/**
	 * @uml.property  name="valueLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	final Label valueLabel = new Label("", false);
	/**
	 * @uml.property  name="blankText"
	 */
	private String blankText = constants.controls_none().toLowerCase();
	/**
	 * @uml.property  name="headerPanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	HorizontalPanel headerPanel = new HorizontalPanel();
	/**
	 * @uml.property  name="container"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	VerticalPanel container = new VerticalPanel();
	
	public ExtendedDatePicker(String heading)
	{
		Image.prefetch("images/bullet_toggle_minus.png");
		Image.prefetch("images/bullet_toggle_plus.png");
		Image.prefetch("images/cross16.png");
		Image.prefetch("images/calendar_edit.png");
		
//		plus.setStyleName("ExtendedDatePickerArrowButton");
		plus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleState(!open);
			}
		});
		
//		minus.setStyleName("ExtendedDatePickerArrowButton");
		minus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleState(!open);
			}
		});
		
		datePicker.setVisible(false);
		
		headingLabel.setText(heading);
//		headingLabel.setStyleName("ExtendedDatePickerHeading");
		headingLabel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
//				headingLabel.addStyleDependentName("mouseover");
			}
		});
		
		headingLabel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
//				headingLabel.removeStyleDependentName("mouseover");
			}
		});
		
		headingLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleState(!open);
			}
		});
		
		valueLabel.setText(blankText);
//		valueLabel.setStyleName("ExtendedDatePickerCurrentValue");
		valueLabel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
//				valueLabel.addStyleDependentName("mouseover");
			}
		});
		
		valueLabel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
//				valueLabel.removeStyleDependentName("mouseover");
			}
		});
		
		valueLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleState(!open);
			}
		});

//		datePicker.addStyleName("ExtendedDatePickerContent");
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				currentValue = event.getValue();
				valueLabel.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(currentValue));
				// Deprecated
				//valueLabel.setText(DateTimeFormat.getMediumDateFormat().format(currentValue));
//				valueLabel.addStyleName("ExtendedDatePickerCurrentValueSet");
				resetButton.setVisible(true);
				resetButton.setEnabled(true);
			}
		});
		
//		editButton.setStyleName("ExtendedDatePickerEditButton");
		editButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleState();
			}
		});
		
//		resetButton.setStyleName("ExtendedDatePickerResetButton");
		resetButton.setVisible(false);
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				datePicker.setValue(null);
				datePicker.setCurrentMonth(new Date());
				valueLabel.setText(blankText);
//				valueLabel.removeStyleName("ExtendedDatePickerCurrentValueSet");
				currentValue = null;
				toggleState(false);
			}
		});

		
		headerPanel.setSpacing(5);
		headerPanel.add(plus);
		headerPanel.add(headingLabel);
		headerPanel.add(valueLabel);
		headerPanel.add(editButton);
		headerPanel.add(resetButton);
		
		container.add(headerPanel);
		container.add(datePicker);
		initWidget(container);
	}
	
	public void setValue(Date date)
	{
		setValue(date, false, false);
	}
	
	public void setValue(Date date, boolean open, boolean fireEvents)
	{
		if (date != null && (minimumDate != null || maximumDate != null))
		{
			DateTimeFormat formatter = DateTimeFormat.getFormat("dd MMMM yyyy");			
			if (minimumDate != null && date.before(minimumDate))
			{
				AlertBox.show(messages.controls_edp_dateAfter(formatter.format(minimumDate)));
				valueLabel.setText(formatter.format(minimumDate));
			}
			else if (maximumDate != null && date.after(maximumDate))
			{
				AlertBox.show(messages.controls_edp_dateBefore(formatter.format(maximumDate)));
				valueLabel.setText(formatter.format(maximumDate));
			}
			else
			{
				datePicker.setValue(date, fireEvents);
				currentValue = date;
				if (currentValue != null)
				{
					valueLabel.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(currentValue));
					// Deprecated
					//valueLabel.setText(DateTimeFormat.getMediumDateFormat().format(currentValue));
//					valueLabel.addStyleName("ExtendedDatePickerCurrentValueSet");
					datePicker.setCurrentMonth(currentValue);
					toggleState(open);
				}
				else
				{
					valueLabel.setText(blankText);
//					valueLabel.removeStyleName("ExtendedDatePickerCurrentValueSet");
					datePicker.setCurrentMonth(new Date());
				}
			}
		}
		else
		{
			datePicker.setValue(date, fireEvents);
			currentValue = date;
			if (currentValue != null)
			{
				valueLabel.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(currentValue));
				// Deprecated
				//valueLabel.setText(DateTimeFormat.getMediumDateFormat().format(currentValue));
//				valueLabel.addStyleName("ExtendedDatePickerCurrentValueSet");
				datePicker.setCurrentMonth(currentValue);
				toggleState(open);
			}
			else
			{
				valueLabel.setText(blankText);
//				valueLabel.removeStyleName("ExtendedDatePickerCurrentValueSet");
				datePicker.setCurrentMonth(new Date());
			}
		}
		
		resetButton.setVisible(currentValue != null);
		resetButton.setEnabled(currentValue != null);
	}
	
	public Date getValue()
	{
		return currentValue;
	}
	
	/**
	 * @return
	 * @uml.property  name="minimumDate"
	 */
	public Date getMinimumDate() {
		return minimumDate;
	}

	/**
	 * @param minimumDate
	 * @uml.property  name="minimumDate"
	 */
	public void setMinimumDate(Date minimumDate) {
		this.minimumDate = minimumDate;
	}

	/**
	 * @return
	 * @uml.property  name="maximumDate"
	 */
	public Date getMaximumDate() {
		return maximumDate;
	}

	/**
	 * @param maximumDate
	 * @uml.property  name="maximumDate"
	 */
	public void setMaximumDate(Date maximumDate) {
		this.maximumDate = maximumDate;
	}

	/**
	 * @return
	 * @uml.property  name="open"
	 */
	public boolean isOpen() {
		return open;
	}
	
	/**
	 * @param open
	 * @uml.property  name="open"
	 */
	public void setOpen(boolean open) {
		toggleState(open);
	}

	/**
	 * @return
	 * @uml.property  name="blankText"
	 */
	public String getBlankText() {
		return blankText;
	}

	/**
	 * @param blankText
	 * @uml.property  name="blankText"
	 */
	public void setBlankText(String blankText) {
		this.blankText = blankText;
		if (currentValue == null)
			valueLabel.setText(blankText);
	}

	/**
	 * @return
	 * @uml.property  name="datePicker"
	 */
	public DatePicker getDatePicker() {
		return datePicker;
	}

	protected void toggleState()
	{
		if (open)
			open = false;
		else
			open = true;
		toggleState(open);
	}
	
	protected void toggleState(boolean state)
	{
		open = state;
		datePicker.setVisible(state);
		
		if (open)
		{
//			editButton.addStyleDependentName("disabled");
			editButton.setEnabled(false);
			headerPanel.remove(plus);
			headerPanel.insert(minus, headerPanel.getWidgetIndex(headingLabel));
			if (currentValue == null)
				valueLabel.setText(constants.controls_edp_pickDate());
		}
		else
		{
//			editButton.removeStyleDependentName("disabled");
			editButton.setEnabled(true);
			headerPanel.remove(minus);
			headerPanel.insert(plus, headerPanel.getWidgetIndex(headingLabel));
			if (currentValue == null)
				valueLabel.setText(blankText);
		}
		
		if (currentValue == null)
		{
			resetButton.setVisible(false);
			resetButton.setEnabled(false);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		return datePicker.addValueChangeHandler(handler);
	}

}
