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

import tms2.client.event.ListBoxValueChangeEvent;
import tms2.client.event.ListBoxValueChangeEventHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Extension of the ListBox making it able to take any type.
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 * @author  Ismail Lavangee
 */
public class ExtendedListBox<T> extends ListBox
{	
	private ArrayList<T> items = new ArrayList<T>();
	private boolean changed;
	private HandlerManager _handler_manager = new  HandlerManager(this);
		
	public ExtendedListBox(boolean isMultipleSelect)
	{
		super(isMultipleSelect);
		
		this.addChangeHandler(new ChangeHandler() 
		{			
			@Override
			public void onChange(ChangeEvent event) 
			{						
				doChange();					
			}
		});
		
		this.addKeyDownHandler(new KeyDownHandler()
		{		
			@Override
			public void onKeyDown(KeyDownEvent event) 
			{
				doChange();					
			}
		});
		
		this.addKeyUpHandler(new KeyUpHandler()
		{		
			@Override
			public void onKeyUp(KeyUpEvent event) 
			{
				doChange();				
			}
		});
	}
		
	@Override
	public void clear()
	{
		super.clear();
		this.items.clear();
		this.changed = false;
	}
	
	@Override
	public void removeItem(int index)
	{
		super.removeItem(index);
		this.items.remove(index);
	}
	
	public void removeAllItems()
	{
		while (this.getItemCount() > 0)
		{
			super.removeItem(0);
			this.items.remove(0);
		}
	}		
	
	public void update(String item_name, int item_id, T item)
	{
		if (this.getSelectedIndex() > 0)
			this.removeItem(getSelectedIndex());
		
		this.addItem(item_name, item_id, item);
		this.setSelectedIndex(this.getItemCount() - 1);
	}
		
	/**
	 * @return
	 * @uml.property  name="changed"
	 */
	public boolean isChanged() {
		return changed;
	}	

	/**
	 * @param changed
	 * @uml.property  name="changed"
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public void addItem(String label, String strValue, T item) {
		super.addItem(label, strValue);
		addItem(item);				
	}
	
	public void addItem(String label, int value, T item) {
		addItem(label, value + "", item);
	}
	
	public void addItem(String label, long value, T item)
	{
		addItem(label, value + "", item);
	}
	
	public void addItem(String label, double value, T item)
	{
		addItem(label, value + "", item);
	}

	/**
	 * @return
	 * @uml.property  name="items"
	 */
	public ArrayList<T> getItems() {
		return items;
	}

	public T getSelectedItem()
	{
		if (items != null && items.size() > 0)
			return getItems().get(super.getSelectedIndex());
		
		return null;
	}
	
	public T getItemAtIndex(int index)
	{
		if (items != null && items.size() > 0)
			return getItems().get(index);
		
		return null;
	}
	
	public boolean hasItemSelected()
	{
		T selectedItem = getSelectedItem();
		return selectedItem != null;
	}
		
	public boolean isAnyItemSelected()
	{
		return super.getSelectedIndex() > -1;		
	}
	
	
	public void setItems(ArrayList<T> items) {
		this.items = items;
	}
	
	protected void addItem(T item) {
		getItems().add(item);
	}

	public void setItem(int index, T item) 
	{
		getItems().set(index, item);		
	}
	
	public void addExtendedListBoxValueChangeHandler(ListBoxValueChangeEventHandler handler)
	{	
		 _handler_manager.addHandler(ListBoxValueChangeEvent.TYPE, handler);
	}
	
	private void doChange()
	{
		changed =  true;		
		_handler_manager.fireEvent(new ListBoxValueChangeEvent());
	}

}
