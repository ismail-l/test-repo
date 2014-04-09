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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that is positioned just under a given position.
 * 
 * @author Ismail Lavangee
 *
 */
public class AttachedPanel extends FixedPanel implements HasPosition
{
	private Widget _parent = null;
	
	private int _left = 0;
	private int _top = 0;
		
	public AttachedPanel(Widget parent)
	{
		super();
		
		_parent = parent;
		
		setAbsoluteLeft(_parent.getAbsoluteLeft());
		setAbsoluteTop(_parent.getOffsetHeight() + 1);
		setPosition();
		
		setStyleName("AttachedPanel");
		
		hide();
	}
		
	public AttachedPanel(int width, int height)
	{
		super();
		
		setAbsoluteLeft(Window.getClientWidth() - width);
		setAbsoluteTop((Window.getScrollTop() + height) + 1);
		setPosition();
		
		setStyleName("AttachedPanel");
		
		hide();
	}
	
	public void setAbsoluteLeft(int left)
	{
		_left = left;
	}
	
	public void setAbsoluteTop(int top)
	{
		_top = top;
	}
	
	public void setPosition()
	{
		getElement().getStyle().setLeft(_left, Style.Unit.PX);
		getElement().getStyle().setTop(_top, Style.Unit.PX);
	}
	
	public void show()
	{
		setVisible(true);
	}
	
	public void hide()
	{
		setVisible(false);	
	}

	@Override
	public void position() 
	{
		if (_parent != null)
		{
			setAbsoluteLeft(_parent.getAbsoluteLeft());
			setPosition();
		}
	}
}
