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

package tms2.shared.wrapper;

import tms2.shared.Record;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordDetailsWrapper implements IsSerializable
{
	private Record _record = null;
	private int _num_of_recs = -1;
	private boolean _is_filter = false;
	
	public RecordDetailsWrapper()
	{
		
	}
	
	public void setRecord(Record record)
	{
		_record = record;
	}
	
	public Record getRecord()
	{
		return _record;
	}
	
	public void setNumberOfRecords(int num_of_recs)
	{
		_num_of_recs = num_of_recs;
	}
	
	
	public int getNumberOfRecords()
	{
		return _num_of_recs;
	}
	
	public void setIsFilter(boolean is_filter)
	{
		_is_filter = is_filter;
	}
	
	public boolean isFilter()
	{
		return _is_filter;
	}
}
