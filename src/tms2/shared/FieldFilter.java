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

import java.util.ArrayList;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldFilter extends FilterableObject
{
	private String _field_text = null;
	private ArrayList<FieldFilter> _sub_filters;

	public FieldFilter() 
	{
		
	}
		
	public void setFieldText(String field_text)
	{
		_field_text = field_text;
	}
	
	public String getFieldText()
	{
		return _field_text;
	}	
	
	public void addSubFilter(FieldFilter field_filter)
	{
		if (_sub_filters == null)
			_sub_filters = new ArrayList<FieldFilter>();
		
		_sub_filters.add(field_filter);
	}
	
	public void removeSubFilters()
	{
		if (_sub_filters != null)
			_sub_filters.clear();
		
		_sub_filters = null;
	}
	
	public ArrayList<FieldFilter> getSubFilters()
	{
		return _sub_filters;
	}
}
