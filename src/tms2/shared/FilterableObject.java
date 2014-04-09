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

package tms2.shared;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FilterableObject extends Field
{		
	public enum Event
	{
		CONTAINS(1, "Contains"), NOT_CONTAINS(2, "Does not contain"), 
		EXCLUSIVE(3, "Exclusively");
				
		int _event_type = -1;
		String _event_type_name = null;
		
		Event(int event_type, String event_type_name)
		{
			_event_type = event_type;
			_event_type_name = event_type_name;
		}
		
		public int getEventType()
		{
			return _event_type;
		}
		
		public String getEventTypeName()
		{
			return _event_type_name;
		}
	}
		
	private int _event_type = -1;
		
	private boolean _is_and = false;
		
	public void setEventType(int event_type)
	{
		_event_type = event_type;
	}
	
	public boolean isContains()
	{
		Event event = Event.CONTAINS;
		
		if (_event_type == event.getEventType())
			return true;
		
		return false;
	}
	
	public boolean isNotContains()
	{
		Event event = Event.NOT_CONTAINS;
		
		if (_event_type == event.getEventType())
			return true;
		
		return false;
	}
	
	public boolean isExclusive()
	{
		Event event = Event.EXCLUSIVE;
		
		if (_event_type == event.getEventType())
			return true;
		
		return false;
	}
	
	public int getEventType()
	{
		return _event_type;
	}
	
	public void setIsAnd(boolean is_and)
	{
		_is_and = is_and;
	}
	
	public boolean isAnd()
	{
		return _is_and;
	}
}
