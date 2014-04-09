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
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Field;
import tms2.shared.PresetField;
import tms2.shared.SynonymAttribute;
import tms2.shared.TerminlogyObject;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * 
 * @author I. Lavangee
 *
 */
public class EditableWidget extends HorizontalPanel
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private HorizontalPanel _icons_panel = null;
	
	private Image _ico_ins = new Image("images/add_term16.png");
	private Image _ico_del = new Image("images/delete_term16.png");		
		
	private TerminlogyObject _terminology_object = null;
		
	private boolean _is_editing = false;	
	
	private FocusWidget _widget = null;
	
	private static final int TEXTAREA_CHARACTERS_PER_LINE = 70;
	
	private int _index = -1;
	private int _parent_index = -1;	
	private int _sub_parent_index = -1;
	
	public EditableWidget(Field source_field, TerminlogyObject terminlogy_object, boolean is_editing, int index)
	{		
		_terminology_object = terminlogy_object;
				
		_is_editing = is_editing;
		
		setIndex(index);
		
		createFieldLabel(source_field);
		createWidget();
		
		if (_is_editing)
			createIconsPanel();
														
		setSpacing(0);
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
	}
	
	private void createWidget()
	{
		if (_terminology_object.hasPresetFields())
		{						
			_widget = new ListBox(false);
			
			int index = 0; 
			int found_index = 0;
			
			ArrayList<PresetField> attributes = _terminology_object.getPresetFields();
			Iterator<PresetField> itr = attributes.iterator();
			while (itr.hasNext())
			{
				PresetField preset_field = itr.next();
				((ListBox) _widget).addItem(preset_field.getPresetFieldName(), preset_field.getPresetFieldName());
				
				if (_terminology_object.getCharData().equals(preset_field.getPresetFieldName()))				
					found_index = index;				
				
				index++;
			}
				
			if (_is_editing)
				((ListBox) _widget).setSelectedIndex(found_index);
			
			_widget.setEnabled(true);
			
			((ListBox) _widget).addChangeHandler(new EditableWidgetChangeHandler());
		}
		else
		{
			if (_terminology_object.getMaxlength() <= 100)
			{
				_widget = new TextBox();
				_widget.setWidth("550px");
				
				if (_is_editing)
					((TextBox)_widget).setText(_terminology_object.getCharData());
				else
					((TextBox)_widget).setText(_terminology_object.getDefaultValue());
				
				((TextBox)_widget).setMaxLength(_terminology_object.getMaxlength());
				
				((TextBox)_widget).addChangeHandler(new EditableWidgetChangeHandler());								
			}
			else
			{
				_widget = new TextArea();
				_widget.setWidth("550px");
				
				if (_is_editing)
					((TextArea)_widget).setText(_terminology_object.getCharData());
				else
					((TextArea)_widget).setText(_terminology_object.getDefaultValue());
				
				((TextArea)_widget).setVisibleLines(1);

				int ratio = ((TextArea)_widget).getText().length() / TEXTAREA_CHARACTERS_PER_LINE;
				if (ratio == 0)
					ratio = 1;
				
				((TextArea)_widget).setVisibleLines(ratio);
				
				((TextArea)_widget).addKeyDownHandler(new KeyDownHandler()
				{
					@Override
					public void onKeyDown(KeyDownEvent event)
					{	
						int ratio = ((TextArea)_widget).getText().length() / TEXTAREA_CHARACTERS_PER_LINE;
						if (ratio == 0)
							ratio = 1;
						
						((TextArea)_widget).setVisibleLines(ratio);
					}
				});
												
				((TextArea)_widget).addChangeHandler(new EditableWidgetChangeHandler());
			}			
		}
				
		add(_widget);
	}
			
	private class EditableWidgetChangeHandler implements ChangeHandler
	{		
		@Override
		public void onChange(ChangeEvent event) 
		{
			_terminology_object.setHasBeenEdited(true);			
		}		
	}
				
	private void createFieldLabel(Field field)
	{
		Label lbl_fieldname = new Label(_terminology_object.getFieldName() + ":");
		
		if (_terminology_object.isMandatory() || 
			field.getFieldId() == _terminology_object.getFieldId())
		{
			lbl_fieldname.addStyleName("labelTextBold");
			lbl_fieldname.addStyleName("plainLabelText");
			lbl_fieldname.getElement().getStyle().setColor("red");		
		}
		else if (_terminology_object.isIndexField())
		{
			lbl_fieldname.addStyleName("labelTextBold");
			lbl_fieldname.addStyleName("plainLabelText");
		}
		else
			lbl_fieldname.addStyleName("plainLabelText");
		
		add(lbl_fieldname);
		
		setCellWidth(lbl_fieldname, "120px");
	}
	
	private void createIconsPanel()
	{
		_icons_panel = new HorizontalPanel();
		_icons_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		_icons_panel.setSpacing(5);
		
		_ico_ins.setTitle(_i18n.getMessages().controls_ec_insert(_terminology_object.getFieldName()));
		_ico_del.setTitle(_i18n.getMessages().controls_ec_remove(_terminology_object.getFieldName()));
		
		_ico_ins.addMouseOverHandler(new IconMouseOverHandler(_ico_ins));		
		_ico_ins.addMouseOutHandler(new IconMouseOutHandler(_ico_ins));
				
		_ico_del.addMouseOverHandler(new IconMouseOverHandler(_ico_del));				
		_ico_del.addMouseOutHandler(new IconMouseOutHandler(_ico_del));
				
		if (shouldAddInsertIcon())
			_icons_panel.add(_ico_ins);
		
		if (shouldAddDeleteIcon())
			_icons_panel.add(_ico_del);
				
		add(_icons_panel);	
	}
	
	private class IconMouseOverHandler implements MouseOverHandler
	{
		private Image _image = null;
		
		public IconMouseOverHandler(Image image)
		{
			_image = image;
		}
		
		@Override
		public void onMouseOver(MouseOverEvent event) 
		{
			_image.getElement().getStyle().setCursor(Cursor.POINTER);
		}		
	}
	
	private class IconMouseOutHandler implements MouseOutHandler
	{
		private Image _image = null;
		
		public IconMouseOutHandler(Image image)
		{
			_image = image;
		}
		
		@Override
		public void onMouseOut(MouseOutEvent event) 
		{
			_image.getElement().getStyle().setCursor(Cursor.DEFAULT);
		}		
	}
	
	private boolean shouldAddInsertIcon()
	{
		if ((_terminology_object.isIndexField() ||
			_terminology_object.isSynonymField()) && 
			_terminology_object.getUserCategoryAccessRight().mayUpdate())	
		{
			return true;
		}
		
		return false;
	}
	
	private boolean shouldAddDeleteIcon()
	{
		if (! _terminology_object.isMandatory() && 
			_terminology_object.getUserCategoryAccessRight().mayDelete())		
			return true;
		
		return false;
	}
	
	public void setEnabled(boolean enabled)
	{
		_widget.setEnabled(enabled);
	}
	
	public Image getInsertIcon()
	{
		return _ico_ins;
	}
	
	public Image getDeleteIcon()
	{
		return _ico_del;
	}
	
	public FocusWidget getWidget()
	{
		return _widget;
	}
	
	public TerminlogyObject getTerminologyObject()
	{
		return _terminology_object;
	}
		
	public void setIndex(int index)
	{
		_index = index;
		_terminology_object.setFormIndex(_index);
	}
	
	public int getIndex()
	{
		return _index;
	}
	
	public void setParentIndex(int index)
	{
		_parent_index = index;	
		((ChildTerminologyObject)_terminology_object).setParentFormIndex(_parent_index);
	}
	
	public int getParentIndex()
	{
		return _parent_index;
	}
	
	public void setSubParentIndex(int index)
	{
		_sub_parent_index = index;
		((SynonymAttribute)_terminology_object).setSubParentFormIndex(_sub_parent_index);
	}
	
	public int getSubParentIndex()
	{
		return _sub_parent_index;
	}
	
	public String getText()
	{
		if (_widget instanceof ListBox)
			return ((ListBox)_widget).getItemText(((ListBox)_widget).getSelectedIndex());
		else
			return ((TextBoxBase)_widget).getText();
	}
}
