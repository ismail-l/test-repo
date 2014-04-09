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
public class ProjectFilter extends FilterableObject
{	
	private long _project_id = -1;
	private String _project_name = null;
	private ArrayList<Project> _all_projects = null;
	
	public ProjectFilter() 
	{
		
	}
	
	public void setProjectId(long projectid)
	{
		_project_id = projectid;
	}
	
	public long getProjectId()
	{
		return _project_id;
	}
	
	public void setProjectName(String project_name)
	{
		_project_name = project_name;
	}
	
	public String getProjectName()
	{
		return _project_name;
	}
	
	public void setAllProjects(ArrayList<Project> projects)
	{
		_all_projects = projects;
	}
	
	public ArrayList<Project> getAllProjects()
	{
		return _all_projects;
	}
}