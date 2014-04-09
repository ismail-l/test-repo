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

import java.util.Date;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This entity represents a Termbase
 * @author  Werner Liebenberg
 * @author  Wildrich Fourie
 */
public class TermBase implements IsSerializable 
{

	private long termdbid = -1;	
	private String termdbname = "";
	private long owneruserid = -1;
	private String ownername = "No Owner";
	private Date datetimecreated = new Date();
	private Date datetimelastupdated = null;
	private ArrayList<Project> _projects = new ArrayList<Project>();
	private String email = "";
	
	public TermBase() 
	{
	}
	
	public long getTermdbid() 
	{
		return termdbid;
	}

	public void setTermdbid(long termdbid) 
	{
		this.termdbid = termdbid;
	}

	public String getTermdbname() 
	{
		return termdbname;
	}

	public void setTermdbname(String termdbname) 
	{
		this.termdbname = termdbname;
	}

	public long getOwneruserid()
	{
		return owneruserid;
	}

	public void setOwneruserid(long owneruserid) {
		
		this.owneruserid = owneruserid;
	}

	public String getOwnername() 
	{
		return ownername;
	}

	public void setOwnername(String ownername) 
	{
		this.ownername = ownername;
	}

	public Date getDatetimecreated() 
	{
		return datetimecreated;
	}

	public void setDatetimecreated(Date datetimecreated) 
	{
		this.datetimecreated = datetimecreated;
	}

	public Date getDatetimelastupdated() 
	{
		return datetimelastupdated;
	}

	public void setDatetimelastupdated(Date datetimelastupdated) 
	{
		this.datetimelastupdated = datetimelastupdated;
	}

	public ArrayList<Project> getProjects() {
		return _projects;
	}	

	public void setProjects(ArrayList<Project> topics) 
	{
		_projects = topics;
	}
	
	public void reset() {
		termdbid = -1;	
		termdbname = "";
		owneruserid = -1;
		ownername = "No Owner";
		datetimecreated = new Date();
		_projects = new ArrayList<Project>();
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return email;
	}
}