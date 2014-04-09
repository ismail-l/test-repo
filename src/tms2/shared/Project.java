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

import java.util.Date;

/**
 * 
 * @author I. Lavangee
 *
 */
public class Project extends TerminlogyObject implements IsAccessControlled 
{
	private long _project_id = -1;
	private String _project_name = null;
	private long _termbase_id = -1;
	private Date _datetimecreated = null;
	private Date _datetimelastupdated = null;	
	private boolean _is_default = false;
			
	public Project() {}
	
	public long getProjectId() 
	{
		return _project_id;
	}
	
	public void setProjectId(long topicid) 
	{
		_project_id = topicid;
	}
	
	public String getProjectName() {
		return _project_name;
	}
	
	public void setProjectName(String topicname) 
	{
		_project_name = topicname;
	}
	
	public long getTermBaseId() 
	{
		return _termbase_id;
	}
	
	public void setTermBaseId(long termdbid) 
	{
		_termbase_id = termdbid;
	}
		
	public Date getDatetimecreated() 
	{
		return _datetimecreated;
	}
	
	public void setDatetimecreated(Date datetimecreated) 
	{
		_datetimecreated = datetimecreated;
	}
	
	public Date getDatetimelastupdated() 
	{
		return _datetimelastupdated;
	}
	
	public void setDatetimelastupdated(Date datetimelastupdated) 
	{
		_datetimelastupdated = datetimelastupdated;
	}
	
	public void setIsDefault(boolean is_default)
	{
		_is_default = is_default;
	}
	
	public boolean isDefault()
	{
		return _is_default;
	}
}