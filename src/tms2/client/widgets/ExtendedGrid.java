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

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * Custom Grid widget.
 * 
 * @author Werner Liebenberg
 * @author Wildrich Fourie
 */
public class ExtendedGrid extends Grid {

	protected int selectedRowIndex;
	protected int selectedColumnIndex;
	protected ArrayList<Row> rows;
	protected boolean bordered = true;
	
	public ExtendedGrid() {
		this(1, 1);
	}
	
	public ExtendedGrid(boolean bordered, int cellspacing, int cellpadding)
	{
		this(1, 1, bordered, cellspacing, cellpadding);
	}

	public ExtendedGrid(int rows, int columns)
	{
		super((rows < 1 ? 1 : rows), (columns < 1 ? 1 : columns));
		this.setStyleName("ExtendedGrid");
		this.setCellSpacing(0);
		this.setCellPadding(5);
	}
	
	public ExtendedGrid(int rows, int columns, boolean bordered, int cellspacing, int cellpadding)
	{
		super((rows < 1 ? 1 : rows), (columns < 1 ? 1 : columns));
		this.setStyleName("ExtendedGrid");
		this.setCellSpacing(cellspacing);
		this.setCellPadding(cellpadding);
		this.setBordered(bordered);
	}
	
	/**
	 * Appends a new row to the end of the table and inserts the give widgets into the cells 
	 * from left to right, in the same order as they are stored in the Widget array.
	 * It also takes care of the border styling.
	 * 
	 * If the array has a longer length than there are columns in a row, extra columns are
	 * automatically added on to the right. This affects the entire grid.
	 * 
	 * @param widgets
	 */
	public int appendWidgets(Widget... widgets) {
		if (widgets != null && widgets.length > 0)
		{
			if (widgets.length <= this.getColumnCount())
			{
				if (this.getRowCount() == 1 && this.rowIsEmpty(0))
				{
					this.setWidgets(0, widgets);
					this.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
					return 0;
				}
				else
				{
					int newRow = this.insertRow(this.getLastRowIndex());
					this.setWidgets(newRow, widgets);
					return newRow;
				}
			}
			else
			{
				super.resizeColumns(widgets.length);
				return this.appendWidgets(widgets);
			}
		}
		return -1;
	}
	
	public int insertWidgets(Widget[] widgets, int afterRowIndex)
	{
		int rowIndex = insertRow(afterRowIndex);
		setWidgets(rowIndex, widgets);
		return rowIndex;
	}
	
	@Override
	public int insertRow(int afterRowIndex)
	{
		if (afterRowIndex >= -1) // start form -1 1 as the super.insertRow adds 1.
		{
			if (afterRowIndex >= getLastRowIndex())
			{
				//The table has at least one row already, and afterRowIndex is zero or more.
				this.resizeRows(this.getRowCount() + 1); //Add a blank row to the bottom, and return its index.
				
				if (isBordered())
				{
					for (int i = 0; i < this.getColumnCount(); i++) //Format its border
						this.getCellFormatter().addStyleName(this.getLastRowIndex(), i, "ExtendedGridTopCellBorder");
				}
				this.getRowFormatter().setVerticalAlign(this.getLastRowIndex(), HasVerticalAlignment.ALIGN_TOP);
				return this.getLastRowIndex();
			}
			else 
			{				 			
				int newRowIndex = super.insertRow(afterRowIndex + 1); 
				
				if (isBordered())
				{
					for (int i = 0; i < this.getColumnCount(); i++) //Format its border
						this.getCellFormatter().addStyleName(newRowIndex, i, "ExtendedGridTopCellBorder");
				}
				this.getRowFormatter().setVerticalAlign(newRowIndex, HasVerticalAlignment.ALIGN_TOP);
				return newRowIndex; 
			}
		}
		else
			throw new IndexOutOfBoundsException("Index " + afterRowIndex + " is out of bounds.");
	}
	
	public int insertRow(Widget[] widgets)
	{
		return this.appendWidgets(widgets);
	}
	
	public int getLastRowIndex()
	{
		return this.getRowCount() - 1;
	}
	
	protected void setWidgets(int rowIndex, Widget[] widgets)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			Widget widget = widgets[i];
			widget.addStyleName("ExtendedGridContent");
			this.setWidget(rowIndex, i, widget);
		}
	}
	
	@Override
	public void setWidget(int row, int column, Widget widget)
	{
		int columns = super.getColumnCount();
		if (column < columns)
		{
			super.setWidget(row, column, widget);
			if (column < this.getColumnCount() - 1 && isBordered()) //Not the last column
				//Place a border on the right of every cell in a row, provided it is not the last/only column
				this.getCellFormatter().addStyleName(row, column, "ExtendedGridRightCellBorder");
		}
		else
		{
			super.resizeColumns(columns + 1);
			super.setWidget(row, column, widget);
		}
	}
	
	public void setSelectedRowIndex(int index)
	{
		if (index > this.getRowCount() - 1 || index < 0)
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for row count " + this.getRowCount() + ".");
		else
			this.selectedRowIndex = index;
	}
	
	public void setSelectedColumnIndex(int index)
	{
		if (index > this.getColumnCount() - 1 || index < 0)
			throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for column count " + this.getColumnCount() + ".");
		else
			this.selectedColumnIndex = index;
	}
	
	public Widget[] getRowWidgets(int rowIndex) {
		Widget[] widgets = new Widget[this.getColumnCount()];
		if (rowIndex > this.getRowCount() - 1 || rowIndex < 0)
			throw new IndexOutOfBoundsException("Index " + rowIndex + " is out of bounds for row count " + this.getRowCount() + ".");
		else
		{
			for (int i = 0; i < this.getColumnCount(); i++)
				widgets[i] = this.getWidget(rowIndex, i);
		}
		
		return widgets;
	}
	
	public boolean rowIsEmpty(int rowIndex)
	{
		Row row = this.getRow(rowIndex);
		return row.isEmpty();
	}
	
	public Row getRow(int rowIndex)
	{
		if (rowIndex > this.getRowCount() - 1 || rowIndex < 0)
			throw new IndexOutOfBoundsException("Index " + rowIndex + " is out of bounds for row count " + this.getRowCount() + ".");
		else
		{
			Row row = new Row(rowIndex);
			Widget[] widgets = this.getRowWidgets(rowIndex);
			row.setWidgets(widgets);
			return row;
		}
	}
	
	public Row getSelectedRow()
	{
		return this.getRow(this.selectedRowIndex);
	}
	
	@Override
	public void resizeColumns(int columns) {
		super.resizeColumns(columns < 1 ? 1 : columns);
	}
	
	@Override
	public void resizeRows(int rows)
	{
		super.resizeRows(rows < 1 ? 1 : rows);
	}
	
	public boolean isBordered() {
		return bordered;
	}

	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	protected class Row extends Widget {
		
		private Widget[] widgets;
		@SuppressWarnings("unused")
		private int rowIndex;
		
		public Row(int rowIndex) {
			this.rowIndex = rowIndex;
		}

		public Widget[] getWidgets() {
			return widgets;
		}

		public void setWidgets(Widget[] widgets) {
			this.widgets = widgets;
		}
		
		public boolean isEmpty()
		{
			return this.widgets == null ||
				this.widgets.length == 0 ||
					arrayIsEmpty();
		}
		
		protected boolean arrayIsEmpty()
		{
			for (int i = 0; i < widgets.length; i++)
				if (widgets[i] != null)
					return false;
			
			return true;
		}
	}
}
