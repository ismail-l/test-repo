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
public class AccessRight extends Field
{
	private long _rights_id = -1;	
	private boolean _may_read = false;
	private boolean _may_update = false;
	private boolean _may_export = false;
	private boolean _may_delete = false;
	
	public long getRightsId() 
	{
		return _rights_id;
	}

	public void setRightsId(long rightsId) 
	{
		_rights_id = rightsId;
	}

	public boolean mayRead() 
	{
		return _may_read;
	}
	
	public void setMayRead(boolean mayRead) 
	{
		_may_read = mayRead;
	}
	
	public boolean mayUpdate() 
	{
		return _may_update;
	}
	
	public void setMayUpdate(boolean mayUpdate) 
	{
		_may_update = mayUpdate;
	}
	
	public boolean mayDelete() 
	{
		return _may_delete;
	}
	
	public void setMayDelete(boolean mayDelete) 
	{
		_may_delete = mayDelete;
	}
		
	public void setMayExport(boolean mayExport) 
	{
		_may_export = mayExport;
	}	
	
	public boolean mayExport() 
	{
		return _may_export;
	}
}
