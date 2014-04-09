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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordElement implements IsSerializable
{
	private long _id = -1;
	private long _record_id = -1;
	private String _char_data = null;
	private String _fieldName = null;

	private String _originChardata = null;
	
	public RecordElement() 
	{
		
	}
	
	public RecordElement (long id, long record_id, String chardata)
	{
		_id = id;
		_record_id = record_id;
		_char_data = chardata;		
	}

	public RecordElement (long id, long record_id, String chardata, String fieldName, String originChardata)
	{
		_id = id;
		_record_id = record_id;
		_char_data = chardata;
		_fieldName = fieldName;
		_originChardata = originChardata;
	}
	
	public void setId(long id)
	{
		this._id = id;
	}
	
	public long getId ()
	{
		return _id;
	}
	
	public long getRecordId()
	{
		return _record_id;
	}
	
	public void setRecordId(long recordid)
	{
		_record_id = recordid;
	}
	
	public String getCharData()
	{
		return _char_data;
	}
	
	public void setCharData(String chardata)
	{
		_char_data = chardata;
	}
	
	public void setFieldName(String fieldName)
	{
		_fieldName = fieldName;
	}

	public String getFieldName()
	{
		return _fieldName;
	}

	public void setOriginChardata(String originChardata)
	{
		_originChardata = originChardata;
	}

	public String getOriginChardata()
	{
		return _originChardata;
	}
}
