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

import java.util.ArrayList;

import tms2.shared.InputModel;
import tms2.shared.Project;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AccessRightDetailsWrapper implements IsSerializable
{
	private InputModel _inputmodel = null;	
	private ArrayList<Project> _available_projects = null;;
	private ArrayList<Project> _assigned_projects = null;
	
	public AccessRightDetailsWrapper()
	{
		
	}
	
	public void setInputModel(InputModel inputmodel)
	{
		_inputmodel = inputmodel;
	}
	
	public InputModel getInputModel()
	{
		return _inputmodel;
	}
	
	public void setAvailableProjects(ArrayList<Project> available)
	{
		_available_projects = available;
	}
	
	public ArrayList<Project> getAvailableProjects()
	{
		return _available_projects;
	}
	
	public void setAssignedProjects(ArrayList<Project> assigned)
	{
		_assigned_projects = assigned;
	}
	
	public ArrayList<Project> getAssignedProjects()
	{
		return _assigned_projects;
	}
	
}
