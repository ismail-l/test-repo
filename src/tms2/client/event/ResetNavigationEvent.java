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

package tms2.client.event;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ResetNavigationEvent extends TermBrowserEvent
{	
	private long _current_recordid = -1;
	private boolean _refresh = false;
	
	public ResetNavigationEvent(long record_id, boolean refresh)
	{
		_current_recordid = record_id;
		_refresh = refresh;
		
		setCurrentEvent(this);
	}
				
	public long getCurrentRecordId()
	{
		return _current_recordid;
	}
	
	public boolean shouldRefresh()
	{
		return _refresh;
	}
}
