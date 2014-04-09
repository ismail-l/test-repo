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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Abstract type class to contain all the information used to define a filter.
 * This class can be serialised and passed as an object between the server and client.
 * @author W. Fourie
 */
public class Filter implements IsSerializable 
{	
	/**
	 * @uml.property  name="termbaseId"
	 */
	private long termbaseId = 0;														// Termbase
	/**
	 * @uml.property  name="projects"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="tms.shared.ProjectFilter"
	 */
	private ArrayList<ProjectFilter> projects = new ArrayList<ProjectFilter>();			// Projects
	/**
	 * @uml.property  name="userId"
	 */
	private long userId = 0;															// User
	/**
	 * @uml.property  name="userRole"
	 */
	private int userRole = 0;
	/**
	 * @uml.property  name="fromDate"
	 */
	private String fromDate = "";														// Dates
	/**
	 * @uml.property  name="toDate"
	 */
	private String toDate = "";
	/**
	 * @uml.property  name="recordFields"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="tms.shared.FieldFilter"
	 */
	private ArrayList<FieldFilter> recordFields = new ArrayList<FieldFilter>();			// Record Fields
	/**
	 * @uml.property  name="indexFields"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="tms.shared.FieldFilter"
	 */
	private ArrayList<FieldFilter> indexFields = new ArrayList<FieldFilter>();			// Index Fields & Sub Fields
	
	
	/**
	 * Creates a new filter from the parameters specified.
	 * @param termbaseId The ID of the selected termbase or <code>0</code> if none were selected.
	 * @param projectsFilter An array of <code>ProjectFilter</code>s containing the selected topics or an empty array if none were selected.
	 * @param userId The ID of the user selected or <code>0</code> if none were selected.
	 * @param userDatesRole The selected role of the specified user as one of the <code>Filter.UserFilterType</code> types.
	 * @param fromDate The from date selected or <code>null</code> if it was not selected.
	 * @param toDate The to date selected or <code>null</code> if it was not selected.
	 * @param fieldsFilter An array of <code>FieldFilter</code>s which contain the fields selected or an empty array if none were selected.
	 */
	public Filter(long termbaseId, ArrayList<ProjectFilter> projectsFilter, long userId, int userDatesRole, 
			String fromDate, String toDate, ArrayList<FieldFilter> recordFields, ArrayList<FieldFilter> indexFields) 
	{
		this.termbaseId = termbaseId;
		this.projects = projectsFilter;
		this.userId = userId;
		this.userRole = userDatesRole;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.recordFields = recordFields;
		this.indexFields = indexFields;
	}
	
	
	/** Empty constructor that is needed in order for this object to be serialised. */ 
	public Filter() {}
	
	
	/**
	 * @return
	 * @uml.property  name="termbaseId"
	 */
	public long getTermbaseId()
	{
		return termbaseId;
	}
	
	public ArrayList<ProjectFilter> getProjects()
	{
		return projects;
	}
	
	/**
	 * @return
	 * @uml.property  name="userId"
	 */
	public long getUserId()
	{
		return userId;
	}
	
	public int getUserDatesRole()
	{
		return userRole;
	}
	
	/**
	 * @return
	 * @uml.property  name="fromDate"
	 */
	public String getFromDate()
	{
		return fromDate;
	}
	
	/**
	 * @return
	 * @uml.property  name="toDate"
	 */
	public String getToDate()
	{
		return toDate;
	}
	
	public ArrayList<FieldFilter> getRecordFields()
	{
		return recordFields;
	}
	
	
	public ArrayList<FieldFilter> getIndexFields()
	{
		return indexFields;
	}

	
	/**
	 * User type class for the filter.
	 */
	public class UserFilterType
	{
		public static final int CHANGED = 1;
		public static final int CREATED = 2;
		public static final int CREATED_OR_CHANGED = 3;
	}
}