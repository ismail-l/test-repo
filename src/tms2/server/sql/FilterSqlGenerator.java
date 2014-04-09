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
package tms2.server.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tms2.shared.Field;
import tms2.shared.FieldFilter;
import tms2.shared.Filter;
import tms2.shared.Project;
import tms2.shared.ProjectFilter;



/**
 * Class containing static methods to create sql statements based upon the filter set.
 * 
 * @author Wildrich Fourie
 * @author Ismail Lavangee
 */
public class FilterSqlGenerator
{	
	/**
	 * NOTES:
	 * - User and Dates filter
	 *   Works by using the first specified index field.
	 *   If no index field have been defined this filter will work on entire records.
	 *   
	 * - Field filter
	 * 	 This only works on the lowest level field defined as it has to test for its parent types in order to function.
	 * 
	 * - Termbases & Projects 
	 *   Can be viewed/created separate; does not have a direct impact on the other filters.
	 */
		
	private static FilterSqlGenerator _instance = null;
	
	/**
	 * Remembers last index for generating FieldFilter SQL statements.
	 */
	private int _index = -1;
	
	/**
	 * Keeps track of index ids used.
	 */
	private HashMap<Long, Integer> _indexes = new HashMap<Long, Integer>();
	
	/**
	 * Keeps track of attribute ids used.
	 */
	private HashMap<Long, Integer> _attributes = new HashMap<Long, Integer>();
	
	//-- FILTER FUNCTIONS --\\
	// Termbase and Project(s)
	private String function_termbase = "AND records.recordid IN (SELECT recordid FROM tms.filterTermbase($1)) \n";
	private String function_project = "records.recordid IN (SELECT recordid FROM tms.filterProject($1)) ";
	
	// Index field by User, Dates	
	private String filterindexuser_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuser_create($1, $2)) \n";
	private String filterindexuser_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuser_edit($1, $2)) \n";
	private String filterindexuserfromdate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuserfromdate_create($1, $2, '$3')) \n";
	private String filterindexuserfromdate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuserfromdate_edit($1, $2, '$3')) \n";
	private String filterindexusertodate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexusertodate_create($1, $2, '$3')) \n";
	private String filterindexusertodate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexusertodate_edit($1, $2, '$3')) \n";
	private String filterindexuserbetweendate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuserbetweendate_create($1, $2, '$3', '$4')) \n";
	private String filterindexuserbetweendate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexuserbetweendate_edit($1, $2, '$3', '$4')) \n";
	
	// Index field, no user, Dates		
	private String filterindexfromdate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexfromdate_create($1, '$2')) \n";
	private String filterindexfromdate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexfromdate_edit($1, '$2')) \n";	
	private String filterindextodate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindextodate_create($1, '$2')) \n";
	private String filterindextodate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindextodate_edit($1, '$2')) \n";			
	private String filterindexbetweendate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterindexbetweendate_create($1, '$2', '$3')) \n";
	private String filterindexbetweendate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterindexbetweendate_edit($1, '$2', '$3')) \n";		
	
	// Record by User, Dates
	private String filterrecorduser_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduser_create($1)) \n";
	private String filterrecorduser_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduser_edit($1)) \n";	
	private String filterrecorduserfromdate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduserfromdate_create($1, '$2')) \n";
	private String filterrecorduserfromdate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduserfromdate_edit($1, '$2')) \n";
	private String filterrecordusertodate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordusertodate_create($1, '$2')) \n";	
	private String filterrecordusertodate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordusertodate_edit($1, '$2')) \n";
	private String filterrecorduserbetweendates_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduserbetweendates_create($1, '$2', '$3')) \n";
	private String filterrecorduserbetweendates_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecorduserbetweendates_edit($1, '$2', '$3')) \n";	
	
	// Record, no user, Dates
	private String filterrecordfromdate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordfromdate_create('$1')) \n";
	private String filterrecordfromdate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordfromdate_edit('$1')) \n";			
	private String filterrecordtodate_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordtodate_create('$1')) \n";
	private String filterrecordtodate_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordtodate_edit('$1')) \n";		
	private String filterrecordbetweendates_create = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordbetweendates_create('$1','$2')) \n";
	private String filterrecordbetweendates_edit = "AND records.recordid IN (SELECT recordid FROM tms.filterrecordbetweendates_edit('$1','$2')) \n";			
	
	// Record fields
	private String function_records = "records.recordid IN (SELECT recordid FROM tms.filterrecord('{$1}')) \n";
	private String function_records_chardata = "records.recordid IN (SELECT recordid FROM tms.filterrecord_chardata($1, '$2')) \n";
	
	// Index fields
	private String function_indexes = "records.recordid IN (SELECT recordid FROM tms.filterindex('{$1}')) \n";
	private String function_indexes_chardata = "records.recordid IN (SELECT recordid FROM tms.filterindex_chardata($1, '$2')) \n";
	
	// Attribute fields
	private String function_attributes = "records.recordid IN (SELECT recordid FROM tms.filterattribute($1, '{$2}')) \n";
	private String function_attributes_chardata = "records.recordid IN (SELECT recordid FROM tms.filterattribute_chardata($1, $2, '$3')) \n";
			
	// SubAttributes
	private String function_subattributes = "records.recordid IN (SELECT recordid FROM tms.filtersubattribute($1, $2, '{$3}')) \n";
	private String function_subattributes_chardata = "records.recordid IN (SELECT recordid FROM tms.filtersubattribute_chardata($1, $2, $3, '$4')) \n";
			
	private String function_project_or = "records.recordid IN (SELECT recordid FROM tms.filterproject_or('{$1}'))\n ";
	private String function_record_or = "records.recordid IN (SELECT recordid FROM tms.filterrecord_or('{$1}')) \n";
	private String function_index_or = "records.recordid IN (SELECT recordid FROM tms.filterindex_or('{$1}')) \n";
	private String function_attributes_or = "records.recordid IN (SELECT recordid FROM tms.filterattribute_or($1, '{$2}')) \n";	
	private String function_subattributes_or = "records.recordid IN (SELECT recordid FROM tms.filtersubattribute_or($1, $2, '{$3}')) \n";
	
	// Does Not Contain functions
	private String function_project_not = "records.recordid IN (SELECT recordid FROM tms.filterproject_not('{$1}')) ";
	
	// Project exclusive function
	private String function_project_exclusive = "records.recordid IN (SELECT recordid FROM tms.filterproject_exclusive('{$1}', $2)) ";	
	
	public static FilterSqlGenerator getInstance()
	{
		if (_instance == null)
			_instance = new FilterSqlGenerator();
		
		return _instance;
	}
	
	private FilterSqlGenerator()
	{
		
	}
	
	/**
	 * Creates the sql statement to retrieve the records that adhere to the filter specifically for the search functions.
	 * @return String - The generated sql statement.
	 */
	public String generateSearchSQLStatement(Filter filter, Field sourceField)
	{
		String str = generateSqlStatement(filter, sourceField);
		str = "WITH with_filter AS ( \n" + str + ") \n";
		return str;
	}
	
	public String generateTermSqlStatement (Filter filter, Field sourceField)
	{
		String termString = "";
		String filterString = generateSqlStatement(filter, sourceField);
		termString = "WITH with_filter0 AS ( \n" + filterString + "\n )\n";
		termString += "select terms.recordid, terms.chardata \n" + 
					  "FROM tms.records, tms.terms, tms.fields \n" +
					  "WHERE records.archivedtimestamp is null \n" + 
					  "and terms.archivedtimestamp is null \n" +
					  "and terms.recordid = records.recordid \n" + 
					  "and terms.fieldid = fields.fieldid \n" +
					  "and fields.fieldid = " + sourceField.getFieldId() + " \n" +
					  "and terms.recordid IN (SELECT recordid FROM with_filter0) \n" + 
					  "ORDER BY terms.recordid ASC";
		
		return termString;
	}
	
	public String generateSynonymSqlStatement(Filter filter, Field sourceField, long synonymFieldId)
	{
		String synonymString = "";
		String filterString = generateSqlStatement(filter, sourceField);
		synonymString = "WITH with_filter0 AS ( \n" + filterString + "\n )\n";		
		synonymString += "select terms.recordid, synonyms.chardata \n" +
						"from tms.records, tms.synonyms, tms.terms, tms.fields \n" +
						"where records.archivedtimestamp is null \n" + 
						"and terms.archivedtimestamp is null \n" + 
						"and synonyms.archivedtimestamp is null \n" +
						"and synonyms.termid = terms.termid \n" +
						"and terms.recordid = records.recordid \n" +
						"and terms.fieldid = fields.fieldid \n" +
						"and terms.fieldid = " +  sourceField.getFieldId() + " \n" + 
						"and synonyms.fieldid = " + synonymFieldId +  " \n" +
						"and terms.recordid IN (SELECT recordid FROM with_filter0)";
		
		return synonymString;
	}
	
	public String generateSearchSynonymSqlStatement(Filter filter, Field sourceField, long synonymFieldId)
	{
		String str = generateSynonymSqlStatement(filter, sourceField, synonymFieldId);
		str = "WITH with_filter AS ( \n" + str + ") \n";
		return str;
	}
				
	private String genSql(Filter filter, Field sourceField)
	{
		String sql = "";
		
		String genericSelect = createGenericRecordSelect(sourceField);
		
		String WHERE = "";
		String ORDER = "ORDER BY recordid ASC";
		
		// :::::::: TERMBASE & PROJECTS ::::::::
		// Test if a Termbase was defined.
		if(filter.getTermbaseId() != 0)
		{// Termbase is defined
			WHERE += function_termbase.replace("$1", ""+ filter.getTermbaseId());
			
			if(filter.getProjects() != null && filter.getProjects().size() > 0)
			{// Project(s) defined
				
				WHERE += "AND (";
																	
				for(int i=0; i < filter.getProjects().size(); i++)
				{			
					ProjectFilter projFil = filter.getProjects().get(i);
					String func_proj = "";
					
					if (i < filter.getProjects().size() - 1)
					{
						// Note that the AND/OR is stored with that project and not the previous.
						if(filter.getProjects().get(i + 1).isAnd()) 	
						{									
							func_proj = function_project;
							
							if(!projFil.isContains())
								func_proj = function_project_not;
								//func_proj = func_proj.replace("IN", "NOT IN");
								
							WHERE += func_proj.replace("$1", "" + projFil.getProjectId());
							WHERE += "AND \n";
						}
						else
						{		
							// Handle OR's
							String funct_proj_or = function_project_or;
							ProjectFilter previous_project_filter = null;
							ArrayList<Long> or_projects = new ArrayList<Long>();
							
							for (int j = i; j <= filter.getProjects().size() - 1; j++)
							{		
								i = j;
																
								ProjectFilter projFilor = filter.getProjects().get(j);
								
								if (previous_project_filter == null)
								{
									if (! projFilor.isContains())	
										funct_proj_or = function_project_not;
										//funct_proj_or = funct_proj_or.replace("IN", "NOT IN");										
								}
								else
								{
									if (projFilor.isContains() !=  previous_project_filter.isContains())
									{
										i--;
										break; 		// If the the previous project filter and the current project filter's
									   				// isContains is not the same then exit. The current project filter will 
									   				// used as an OR
									}
								}															
								
								or_projects.add(projFilor.getProjectId());
								
								if (j < filter.getProjects().size() - 1)
								{
									// If the next project filter is an AND then exit
									if(filter.getProjects().get(j + 1).isAnd())																
										break;									
								}	
								
								previous_project_filter = projFilor;
							}
							
							if (or_projects.size() > 0)
							{
								StringBuffer project_ids = new StringBuffer();
								Iterator<Long> iter = or_projects.iterator();
								while (iter.hasNext())
								{
									long proj_id = iter.next();
									project_ids.append(proj_id);
									project_ids.append(",");
								}
								
								project_ids.delete(project_ids.length() - 1, project_ids.length());
								WHERE += funct_proj_or.replace("$1", "" + project_ids.toString());	
							}
							
							if (i < filter.getProjects().size() - 1)
							{
								if (filter.getProjects().get(i + 1).isAnd())
									WHERE += "AND\n ";
								else
									WHERE += "OR\n";
							}
						}
					}
					else
					{												
						if(! projFil.isContains())
						{
							if (! projFil.isExclusive())
							{
								func_proj = function_project_not;
								WHERE += func_proj.replace("$1", "" + projFil.getProjectId());
							}
							else
							{																
								func_proj = function_project_exclusive;
								
								ArrayList<Project> projects = projFil.getAllProjects();
								
								StringBuffer project_ids = new StringBuffer();
								Iterator<Project> iter = projects.iterator();
								while (iter.hasNext())
								{
									Project project = iter.next();
									if (project.getProjectId() != projFil.getProjectId())
									{
										project_ids.append(project.getProjectId());
										project_ids.append(",");
									}
								}
								
								project_ids.delete(project_ids.length() - 1, project_ids.length());
								
								func_proj =  func_proj.replace("$1", "" + project_ids.toString());
								
								WHERE += func_proj.replace("$2", "" + projFil.getProjectId());
								
							}
						}
						else
						{
							func_proj = function_project;
														
							WHERE += func_proj.replace("$1", "" + projFil.getProjectId());
						}
					}
				}
				
				WHERE += ") \n";
			 }
			
		 }
		
		// Detect the definitions.
		boolean recordDefined; // TRUE = Complete Record, FALSE = Specific Index field.
		boolean userDefined;   // TRUE = User defined, FALSE = User NOT defined.
		boolean fromDateDefined; // TRUE = From Date specified, FALSE = No From Date
		boolean toDateDefined; // TRUE = To Date specified, FALSE = No To Date
		FieldFilter indexField = null;
		if(filter.getIndexFields() == null || filter.getIndexFields().size() <= 0) recordDefined = true;
		else 
		{
			recordDefined = false;
			indexField = filter.getIndexFields().get(0);
		}
		if (filter.getUserId() > 0) 
			userDefined = true;
		else 
			userDefined = false;
		
		if (filter.getFromDate() != null) 
			fromDateDefined = true;
		else 
			fromDateDefined = false;
		
		if (filter.getToDate() != null) 
			toDateDefined = true;
		else 
			toDateDefined = false;
		
		// Check that either a user or one of the dates were specified
		if (userDefined || fromDateDefined || toDateDefined)
		{
			// A User, Date filter was defined.
			String funct = "";

			/**
			int CHANGED = 1;
			int CREATED = 2;
			int CREATED_OR_CHANGED = 3; */
			int eventType = 0;
			switch(filter.getUserDatesRole())
			{
				case Filter.UserFilterType.CHANGED:
					eventType = 2;
					break;
				case Filter.UserFilterType.CREATED:
					eventType = 1;
					break;
				case 3: 
					eventType = 3; // Create or changed. 
					break;
				default:
					eventType = 1;	
			}
			
			// On Complete Record
			if(recordDefined && userDefined && !fromDateDefined && !toDateDefined)
			{
				// Record & User
				if(eventType == 3)
				{
					String create_function = filterrecorduser_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1","" + filter.getUserId());
					
					funct += "OR \n";
					
					String edit_function = filterrecorduser_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1","" + filter.getUserId());
					
					funct += edit_function;
					
					funct += ")";
				}
				else
				{
					if (eventType == 1)
						funct = filterrecorduser_create.replace("$1","" + filter.getUserId());
					else if (eventType == 2)
						funct = filterrecorduser_edit.replace("$1","" + filter.getUserId());
				}
			}
			else if(recordDefined && !userDefined && fromDateDefined && !toDateDefined)
			{
				// Record & From Date
				if(eventType == 3)
				{
					String create_function = filterrecordfromdate_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1",filter.getFromDate());
					
					funct += " OR\n";
					
					String edit_function = filterrecordfromdate_edit.replace("AND", "");
					edit_function = edit_function.replace("$1",filter.getFromDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
						funct = filterrecordfromdate_create.replace("$1",filter.getFromDate());
					else
						funct = filterrecordfromdate_edit.replace("$1",filter.getFromDate());					
				}
			}
			else if(recordDefined && !userDefined && !fromDateDefined && toDateDefined)
			{
				// Record & To Date
				if(eventType == 3)
				{
					String create_function = filterrecordtodate_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1",filter.getToDate());
					
					funct += " OR\n";
					
					String edit_function = filterrecordtodate_edit.replace("AND", "");
					edit_function = edit_function.replace("$1",filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
						funct = filterrecordtodate_create.replace("$1",filter.getToDate());
					else
						funct = filterrecordtodate_edit.replace("$1",filter.getToDate());
				}
			}
			else if(recordDefined && !userDefined && fromDateDefined && toDateDefined)
			{
				// Record & Between Dates
				if(eventType == 3)
				{
					String create_function = filterrecordbetweendates_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1",filter.getFromDate());
					funct = funct.replace("$2", filter.getToDate());
					
					funct += " OR\n";
					
					String edit_function = filterrecordbetweendates_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1",filter.getFromDate());
					edit_function = edit_function.replace("$2", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterrecordbetweendates_create.replace("$1",filter.getFromDate());
						funct = funct.replace("$2", filter.getToDate());
					}
					else
					{
						funct = filterrecordbetweendates_edit.replace("$1",filter.getFromDate());
						funct = funct.replace("$2", filter.getToDate());
					}
				}
			}
			else if(recordDefined && userDefined && fromDateDefined && !toDateDefined)
			{
				// Record & User & From Date
				if(eventType == 3)
				{
					String create_function = filterrecorduserfromdate_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1", "" + filter.getUserId());
					funct = funct.replace("$2", filter.getFromDate());
					
					funct += " OR \n";
					
					String edit_function = filterrecorduserfromdate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + filter.getUserId());
					edit_function = edit_function.replace("$2", filter.getFromDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterrecorduserfromdate_create.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getFromDate());
					}
					else if (eventType == 2)
					{
						funct = filterrecorduserfromdate_edit.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getFromDate());
					}
				}
			}
			else if(recordDefined && userDefined && !fromDateDefined && toDateDefined)
			{
				// Record & User & To Date
				if(eventType == 3)
				{
					String create_function = filterrecordusertodate_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1", "" + filter.getUserId());
					funct = funct.replace("$2", filter.getToDate());
					
					funct += " OR \n";
					
					String edit_function = filterrecordusertodate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + filter.getUserId());
					edit_function = edit_function.replace("$2", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterrecordusertodate_create.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getToDate());
					}
					else if (eventType == 2)
					{
						funct = filterrecordusertodate_edit.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getToDate());
					}
				}
			}
			else if(recordDefined && userDefined && fromDateDefined && toDateDefined)
			{
				// Record & User & Between Dates
				if(eventType == 3)
				{
					String create_function = filterrecorduserbetweendates_create.replace("AND ", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1", "" + filter.getUserId());
					funct = funct.replace("$2", filter.getFromDate());
					funct = funct.replace("$3", filter.getToDate());
					
					funct += " OR \n";
					
					String edit_function = filterrecorduserbetweendates_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + filter.getUserId());
					edit_function = edit_function.replace("$2", filter.getFromDate());
					edit_function = edit_function.replace("$3", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterrecorduserbetweendates_create.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getFromDate());
						funct = funct.replace("$3", filter.getToDate());
					}
					else if (eventType == 2)
					{
						funct = filterrecorduserbetweendates_edit.replace("$1", "" + filter.getUserId());
						funct = funct.replace("$2", filter.getFromDate());
						funct = funct.replace("$3", filter.getToDate());
					}
				}
			}
			
			// Index Field defined
			else if(!recordDefined && userDefined && !fromDateDefined && !toDateDefined)
			{
				// Index Field & User
				if(eventType == 3)
				{
					String create_function = filterindexuser_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", "" + filter.getUserId());
					
					funct += " OR \n";
					
					String edit_function = filterindexuser_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", "" + filter.getUserId());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexuser_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
					}
					else if (eventType == 2)
					{
						funct = filterindexuser_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
					}
				}
			}
			else if(!recordDefined && !userDefined && fromDateDefined && !toDateDefined)
			{
				// Index Field & From Date
				if(eventType == 3)
				{
					String create_function = filterindexfromdate_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", filter.getFromDate());
					
					funct += " OR\n";
					
					String edit_function = filterindexfromdate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", filter.getFromDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexfromdate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getFromDate());
					}
					else if (eventType == 2)
					{
						funct = filterindexfromdate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getFromDate());
					}
				}
			}
			else if(!recordDefined && !userDefined && !fromDateDefined && toDateDefined)
			{
				// Index Field & To Date
				if(eventType == 3)
				{
					String create_function = filterindextodate_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", filter.getToDate());
					
					funct += " OR\n";
					
					String edit_function = filterindextodate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindextodate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getToDate());
					}
					else
					{
						funct = filterindextodate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getToDate());
					}										
				}
			}
			else if(!recordDefined && !userDefined && fromDateDefined && toDateDefined)
			{
				// Index Field & Between Dates
				if(eventType == 3)
				{
					String create_function = filterindexbetweendate_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", filter.getFromDate());
					funct = funct.replace("$3", filter.getToDate());
					
					funct += " OR \n";
					
					String edit_function = filterindexbetweendate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", filter.getFromDate());
					edit_function = edit_function.replace("$3", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexbetweendate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getFromDate());
						funct = funct.replace("$3", filter.getToDate());
					}
					else if (eventType == 2)
					{
						funct = filterindexbetweendate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", filter.getFromDate());
						funct = funct.replace("$3", filter.getToDate());
					}				
				}
			}
			else if(!recordDefined && userDefined && fromDateDefined && !toDateDefined)
			{
				// Index Field & User & From Date
				if(eventType == 3)
				{
					String create_function = filterindexuserfromdate_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", "" + filter.getUserId());
					funct = funct.replace("$3", filter.getFromDate());
					
					funct += " OR \n";
					
					String edit_function = filterindexuserfromdate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", "" + filter.getUserId());
					edit_function = edit_function.replace("$3", filter.getFromDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexuserfromdate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getFromDate());
					}
					else if (eventType == 2)
					{
						funct = filterindexuserfromdate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getFromDate());
					}
					
					funct = funct.replace("$4", "" + eventType);
				}
			}
			else if(!recordDefined && userDefined && !fromDateDefined && toDateDefined)
			{
				// Index Field & User & To Date
				if(eventType == 3)
				{
					String create_function = filterindexusertodate_create.replace("AND", "AND (");
					
					funct = create_function;
					
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", "" + filter.getUserId());
					funct = funct.replace("$3", filter.getToDate());
					
					funct += " OR\n";
					
					String edit_function = filterindexusertodate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", "" + filter.getUserId());
					edit_function = edit_function.replace("$3", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexusertodate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getToDate());
					}
					else
					{
						funct = filterindexusertodate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getToDate());
					}
				}
			}
			else if(!recordDefined && userDefined && fromDateDefined && toDateDefined)
			{
				// Index Field & User & Between Dates
				if(eventType == 3)
				{
					String create_function = filterindexuserbetweendate_create.replace("AND", "AND (");
					
					funct = create_function;
					funct = funct.replace("$1", "" + indexField.getFieldId());
					funct = funct.replace("$2", "" + filter.getUserId());
					funct = funct.replace("$3", filter.getFromDate());
					funct = funct.replace("$4", filter.getToDate());
					
					funct += " OR\n";
					
					String edit_function = filterindexuserbetweendate_edit.replace("AND", "");
					
					edit_function = edit_function.replace("$1", "" + indexField.getFieldId());
					edit_function = edit_function.replace("$2", "" + filter.getUserId());
					edit_function = edit_function.replace("$3", filter.getFromDate());
					edit_function = edit_function.replace("$4", filter.getToDate());
					
					funct += edit_function;
					
					funct += ") ";
				}
				else
				{
					if (eventType == 1)
					{
						funct = filterindexuserbetweendate_create.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getFromDate());
						funct = funct.replace("$4", filter.getToDate());
					}
					else
					{
						funct = filterindexuserbetweendate_edit.replace("$1", "" + indexField.getFieldId());
						funct = funct.replace("$2", "" + filter.getUserId());
						funct = funct.replace("$3", filter.getFromDate());
						funct = funct.replace("$4", filter.getToDate());
					}					
				}
			}
			WHERE += funct;
		}
				
		clearIds();
		
		// RECORD FIELDS.
		if(filter.getRecordFields() != null && filter.getRecordFields().size() > 0)
		{
			// Some records fields were defined.
			WHERE += "AND (";
								
			for (int i = 0; i < filter.getRecordFields().size(); i++)
			{
				_index = -1;
				FieldFilter field = filter.getRecordFields().get(i);

				if (! _indexes.containsKey(field.getFieldId()))
				{							
					WHERE  += generateFieldFilterSQL(filter.getRecordFields(), field, i, 
							function_records, function_record_or, function_records_chardata,
							Long.toString(field.getFieldId()), field.getFieldText(), null, null, 0);
													
					// There could have been OR's so just
					// update i
					if (_index != -1)
						i = _index;
					
					if (isAndStatement(filter.getRecordFields(), field, i, WHERE))
						WHERE += "AND\n";	
				}
			}
			
			WHERE += ") \n";
		}
			
		clearIds();
		_index = -1;
					
		// Index fields
		if (filter.getIndexFields() != null && filter.getIndexFields().size() > 0)
		{
			WHERE += "AND (";
			for(int i=0; i < filter.getIndexFields().size(); i++)
			{				
				_index = -1;
				_attributes.clear();
				
				// Already used above, just re-use.
				indexField = filter.getIndexFields().get(i);
				
				if (! _indexes.containsKey(indexField.getFieldId()))
				{						
					WHERE += generateFieldFilterSQL(filter.getIndexFields(), indexField, i, 
							function_indexes, function_index_or, function_indexes_chardata, 
							Long.toString(indexField.getFieldId()), indexField.getFieldText(), null, null, 0);
													
					// There could have been OR's so just
					// update i
					if (_index != -1)
						i = _index;
					
					if (isAndStatement(filter.getIndexFields(), indexField, i, WHERE))
						WHERE += "AND\n";										
				}
				
				if (indexField.getSubFilters() != null && indexField.getSubFilters().size() > 0)
				{					
					// Attribute fields defined.
					for(int j=0; j < indexField.getSubFilters().size(); j++)
					{			
						_index = -1;
						
						FieldFilter attributeField = indexField.getSubFilters().get(j);
						
						if (! _attributes.containsKey(attributeField.getFieldId()))
						{
							WHERE += generateFieldFilterSQL(indexField.getSubFilters(), attributeField, j, function_attributes, function_attributes_or, function_attributes_chardata,  
									Long.toString(indexField.getFieldId()), Long.toString(attributeField.getFieldId()), attributeField.getFieldText(), null, 1);
																						
							// There could have been OR's so just
							// update j
							if (_index != -1)
								j = _index;
							
							if (isAndStatement(filter.getIndexFields(), indexField.getSubFilters(), attributeField, i, j, WHERE))
								WHERE += "AND\n";															
						}
						
						if (attributeField.getSubFilters() != null && attributeField.getSubFilters().size() > 0)
						{
							// SubAttribute(s) defined.
							for(int k = 0; k < attributeField.getSubFilters().size(); k++)
							{
								_index = -1;
																																
								FieldFilter subAttributeField = attributeField.getSubFilters().get(k);
																
								WHERE += generateFieldFilterSQL(attributeField.getSubFilters(), subAttributeField, k, function_subattributes, function_subattributes_or, 
										function_subattributes_chardata, Long.toString(indexField.getFieldId()), Long.toString(attributeField.getFieldId()), 
										Long.toString(subAttributeField.getFieldId()), subAttributeField.getFieldText(), 2);
																								
								// There could have been OR's so just
								// update k
								if (_index != -1)
									k = _index;
								
								if (isAndStatement(filter.getIndexFields(), indexField.getSubFilters(), attributeField.getSubFilters(),
										i, j, k, WHERE))
									WHERE += "AND\n";	
							}
						}						
					}
				}

			}
			WHERE += ") \n";
		}
		
		sql = genericSelect + WHERE + ORDER;
		
		clearIds();
		
		return sql;
	}
	
	/**
	 * Generic function to generate FieldFilter SQL statements of RecordAttributes, Indexes, TermAttributes and TermSubAttributes
	 * @param field_filters
	 * @param current_field_filter
	 * @param index
	 * @param function
	 * @param function_or
	 * @param function_chardata
	 * @param $1
	 * @param $2
	 * @param $3
	 * @param $4
	 * @param code
	 * @return The generated SQL 
	 */
	public String generateFieldFilterSQL(ArrayList<FieldFilter> field_filters, FieldFilter current_field_filter, int current_index,
			String function, String function_or, String function_chardata, String $1, String $2, String $3, String $4, int code)
	{
		// code == 0 => Record fields, Index Fields
		// code == 1 => attribute fields
		// code == 2 => subattribute fields
		
		String field_filter_sql = "";
				
		String func = "";
		
		_index = current_index;
		
		if (_index < field_filters.size() - 1)
		{
			// Handle AND's
			if (current_field_filter.isAnd()) 	
			{
				// Check for chardata
				if (current_field_filter.getFieldText() == null || current_field_filter.getFieldText().equals(""))
				{
					func = function;
					
					if (! current_field_filter.isContains())
						func = func.replace("IN", "NOT IN");
					
					field_filter_sql += substituteAndReplace(func, $1, $2, $3, code);	
					
					if (code == 0)
						_indexes.put(current_field_filter.getFieldId(), 1);
					else
						_attributes.put(current_field_filter.getFieldId(), 1);
				}
				else
				{					
					// This chardata function will be handled with the AND operator as it 
					// is an AND FieldFilter
					func = function_chardata;
					
					if (! current_field_filter.isContains())
						func = func.replace("IN", "NOT IN");
					
					field_filter_sql += substituteAndReplaceForChardata(func, $1, $2, $3, $4, code);
					if (code == 0)
						_indexes.put(current_field_filter.getFieldId(), 1);
					else
						_attributes.put(current_field_filter.getFieldId(), 1);
				}				
			}
			else
			{
				// Th e
				// Handle OR's
				String func_or = "";
				FieldFilter previous_fieldfilter = null;
				ArrayList<Long> or_ids = new ArrayList<Long>();						
				
				for (int j = _index; j < field_filters.size(); j++)
				{
					_index = j;
					
					FieldFilter fieldfilter_or = field_filters.get(j);
					
					if (previous_fieldfilter == null)
					{
						if (fieldfilter_or.getFieldText() == null || fieldfilter_or.getFieldText().equals(""))
						{
							func_or = function_or;
							
							if (! fieldfilter_or.isContains())									
								func_or = func_or.replace("IN", "NOT IN");
																													
							or_ids.add(fieldfilter_or.getFieldId());
							
							if (code == 0)
								_indexes.put(fieldfilter_or.getFieldId(), 1);
							else
								_attributes.put(fieldfilter_or.getFieldId(), 1);
						}
						else
						{
							// Functions with chardata will be handled with the OR operator
							func = function_chardata;
							
							if ( ! fieldfilter_or.isContains())
								func = func.replace("IN", "NOT IN");
							
							field_filter_sql += substituteAndReplaceForChardata(func, $1, $2, $3, $4, code);
							
							if (code == 0)
								_indexes.put(fieldfilter_or.getFieldId(), 1);
							else
								_attributes.put(fieldfilter_or.getFieldId(), 1);
							
							field_filter_sql += "OR\n";								
							
							break;
						}
					}
					else
					{
						if (fieldfilter_or.getFieldText() == null || fieldfilter_or.getFieldText().equals(""))
						{									
							if (fieldfilter_or.isContains() != previous_fieldfilter.isContains())
							{
								func = function;
								func = func.replace("IN", "NOT IN");
								
								// code = 0. Change the field id of $1 and change the text of $2.
								// code = 1. Change the field id of $2 and change the text of $3.
								// code = 2. Change the field id of $3 and change the text of $4.
								
								if (code == 0)
									$1 = Long.toString(fieldfilter_or.getFieldId());
								else if (code == 1)
									$2 = Long.toString(fieldfilter_or.getFieldId());
								else
									$3 = Long.toString(fieldfilter_or.getFieldId());
								
								field_filter_sql += substituteAndReplace(func, $1, $2, $3, code);
								
								if (code == 0)
									_indexes.put(fieldfilter_or.getFieldId(), 1);
								else
									_attributes.put(fieldfilter_or.getFieldId(), 1);
								
								field_filter_sql += "OR\n";
								break;
							}
																																							
							or_ids.add(fieldfilter_or.getFieldId());
						}
						else
						{
							// Functions with chardata will be handled with the OR operator
							func = function_chardata;
							
							if (! fieldfilter_or.isContains())
								func = func.replace("IN", "NOT IN");
							
							field_filter_sql += substituteAndReplaceForChardata(func, $1, $2, $3, $4, code);
							
							if (code == 0)
								_indexes.put(fieldfilter_or.getFieldId(), 1);
							else
								_attributes.put(fieldfilter_or.getFieldId(), 1);
							
							field_filter_sql += "OR\n";	
							
							break;
						}
					}
																																	
					previous_fieldfilter = fieldfilter_or;
					
					if (_index < field_filters.size() - 1)
					{		
						int is_last = _index + 1;
						
						// Make provision for the last entry. Even if it is an AND, it
						// should be OR the current entry.								
						if (is_last == field_filters.size() - 1)
						{
							FieldFilter last_filter = field_filters.get(is_last);
							
							if (last_filter.isContains() != previous_fieldfilter.isContains())
							{
								func = function;
								func = func.replace("IN", "NOT IN");
								
								// code = 0. Change the field id of $1 and change the text of $2.
								// code = 1. Change the field id of $2 and change the text of $3.
								// code = 2. Change the field id of $3 and change the text of $4.
								
								if (code == 0)
									$1 = Long.toString(last_filter.getFieldId());
								else if (code == 1)
									$2 = Long.toString(last_filter.getFieldId());
								else
									$3 = Long.toString(last_filter.getFieldId());
								
								field_filter_sql += substituteAndReplace(func, $1, $2, $3, code);
								
								if (code == 0)
									_indexes.put(last_filter.getFieldId(), 1);
								else
									_attributes.put(last_filter.getFieldId(), 1);
								
								_index++;
								
								field_filter_sql += "OR\n";
								break;
							}
							else
							{
								or_ids.add(last_filter.getFieldId());
								
								if (code == 0)
									_indexes.put(last_filter.getFieldId(), 1);
								else
									_attributes.put(last_filter.getFieldId(), 1);
								
																
								// Check if the last entry has sub filters. If it does not, increment the current index to exit the 
								// calling loop. If it does do not, incremenent as the sub filter sql has to be generated
								// with an AND.
								if (last_filter.getSubFilters() == null || last_filter.getSubFilters().size() == 0)								
									_index++;														
								
								break;
							}
						}
						else
						{			
							FieldFilter filter = field_filters.get(is_last);
							
							if (filter.isAnd())
							{																								
								if (filter.getFieldText() == null || filter.getFieldText().equals(""))
								{
									if (filter.isContains() != previous_fieldfilter.isContains())
									{
										func = function;
										func = func.replace("IN", "NOT IN");
										
										field_filter_sql += substituteAndReplace(func, $1, $2, $3, code);
										
										if (code == 0)
											_indexes.put(filter.getFieldId(), 1);
										else
											_attributes.put(filter.getFieldId(), 1);
										
										field_filter_sql += "OR\n";
										break;
									}
									
									// This should be the OR of the previous entry even though it is an AND.
									// The AND of this entry will be handled seperatly.
									or_ids.add(filter.getFieldId());	
									
									if (code == 0)
										_indexes.put(filter.getFieldId(), 1);
									else
										_attributes.put(filter.getFieldId(), 1);
								}
								else
								{
									// Functions with chardata will be handled with the OR operator.
									func = function_chardata;
									if (! filter.isContains())
										func = func.replace("IN", "NOT IN");
									
									// code = 0. Change the field id of $1 and change the text of $2.
									// code = 1. Change the field id of $2 and change the text of $3.
									// code = 2. Change the field id of $3 and change the text of $4.

									if (code == 0)	
									{
										$1 = Long.toString(filter.getFieldId());
										$2 = filter.getFieldText();
									}
									else if (code == 1)
									{
										$2 = Long.toString(filter.getFieldId());
										$3 = filter.getFieldText();
									}
									else
									{
										$3 = Long.toString(filter.getFieldId());
										$4 = filter.getFieldText();
									}
									
									field_filter_sql += substituteAndReplaceForChardata(func, $1, $2, $3, $4, code);
									
									if (code == 0)
										_indexes.put(filter.getFieldId(), 1);
									else
										_attributes.put(filter.getFieldId(), 1);
									
									field_filter_sql += "OR\n";	
								}
									
								// update the index as the next entry in the current list
								// has been used.
								_index++;
								break;
							}
							else if (current_field_filter.getSubFilters() != null && current_field_filter.getSubFilters().size() > 0)	
							{
								// Check if the last entry has sub filters. If it does exit and handle the sub filters.
								break;
							}
						}
					}
				}
				
				if (or_ids.size() > 0)
				{
					StringBuffer ids = new StringBuffer();
					Iterator<Long> iter = or_ids.iterator();
					while (iter.hasNext())
					{
						long id = iter.next();
						ids.append(id);
						ids.append(",");								
					}
					
					ids.delete(ids.length() - 1, ids.length());
					
					if (code == 0)						
						field_filter_sql += func_or.replace("$1", ids.toString());					
					else if (code == 1)
					{
						func_or = func_or.replace("$1", $1);
						field_filter_sql += func_or.replace("$2", ids.toString());
					}
					else if (code == 2)
					{
						func_or = func_or.replace("$1", $1);
						func_or = func_or.replace("$2", $2);
						field_filter_sql += func_or.replace("$3", ids.toString());
					}
				}								
			}
		}
		else
		{
			// This is the last FieldFilter in the current list.
			if (current_field_filter.getFieldText() == null || current_field_filter.getFieldText().equals(""))
			{
				func = function;
				
				if (! current_field_filter.isContains())
					func = func.replace("IN", "NOT IN");
				
				field_filter_sql = substituteAndReplace(func, $1, $2, $3, code);	
				
				if (code == 0)
					_indexes.put(current_field_filter.getFieldId(), 1);
				else
					_attributes.put(current_field_filter.getFieldId(), 1);
			}
			else
			{
				// This chardata function will be handled with the AND operator as it 
				// the last entry in the current list.
				func = function_chardata;
				
				if (! current_field_filter.isContains())
					func = func.replace("IN", "NOT IN");
				
				field_filter_sql = substituteAndReplaceForChardata(func, $1, $2, $3, $4, code);
				
				if (code == 0)
					_indexes.put(current_field_filter.getFieldId(), 1);
				else
					_attributes.put(current_field_filter.getFieldId(), 1);
			}
		}				
		
		return field_filter_sql;			
	}
	
	/**
	 * Checks if the current statement needs an AND appened to it. Used with Index FieldFilters and Record FieldFilters.
	 * @param field_filters
	 * @param index
	 * @param current_statement
	 * @return
	 */
	private boolean isAndStatement(ArrayList<FieldFilter> field_filters, FieldFilter current_fieldfilter, int index, String current_statement)
	{
		if (! current_statement.endsWith("OR\n"))
		{
			if (index < field_filters.size() - 1)
				return true;	
			
			if (current_fieldfilter.getSubFilters() != null && current_fieldfilter.getSubFilters().size() > 0)
				return true;
		}

		return false;
	}
	
	/**
	 * Checks if the current statement needs an AND appened to it. Used with Attribute FieldFilters.
	 * @param index_filters
	 * @param attr_filters
	 * @param index_index
	 * @param attr_index
	 * @param current_statement
	 * @return
	 */
	private boolean isAndStatement(ArrayList<FieldFilter> index_filters, ArrayList<FieldFilter> attr_filters, FieldFilter current_fieldfilter,
			int index_index, int attr_index, String current_statement)
	{
		if (! current_statement.endsWith("OR\n"))
		{
			if (index_index < index_filters.size() - 1)
				return true;
			
			if (attr_index < attr_filters.size() - 1)
				return true;	
			
			if (current_fieldfilter.getSubFilters() != null && current_fieldfilter.getSubFilters().size() > 0)
				return true;
		}

		return false;
	}
	
	/**
	 * Checks if the current statement needs an AND appened to it. Used with SubAttribute FieldFilters
	 * @param index_filters
	 * @param attr_filters
	 * @param subattr_filters
	 * @param index_index
	 * @param attr_index
	 * @param subattr_index
	 * @param current_statement
	 * @return
	 */
	private boolean isAndStatement(ArrayList<FieldFilter> index_filters, ArrayList<FieldFilter> attr_filters, ArrayList<FieldFilter> subattr_filters,
			int index_index, int attr_index, int subattr_index, String current_statement)
	{
		if (! current_statement.endsWith("OR\n"))
		{
			if (index_index < index_filters.size() - 1)
				return true;
			
			if (attr_index < attr_filters.size() - 1)
				return true;	
			
			if (subattr_index < subattr_filters.size() - 1)
				return true;
		}

		return false;
	}
	
	/**
	 * Substitute and replace values for Postgres functions that do not filter chardata.
	 * @param func
	 * @param $1
	 * @param $2
	 * @param $3
	 * @param code
	 * @return The SQL string.
	 */
	private String substituteAndReplace(String func, String $1, String $2, String $3, int code)
	{
		String field_filter_sql = "";
		
		func = func.replace("$1", $1);
		
		if (code == 0)
			field_filter_sql = func;
		if (code == 1)				
			field_filter_sql += func.replace("$2", $2);				
		else if (code == 2)
		{
			func = func.replace("$2", $2);
			field_filter_sql += func.replace("$3", $3);
		}
		
		return field_filter_sql;
	}
	
	/**
	 * Substitute and replace values for Postgres functions that  filter chardata.
	 * @param func
	 * @param $1
	 * @param $2
	 * @param $3
	 * @param $4
	 * @param code
	 * @return The SQL string.
	 */
	private String substituteAndReplaceForChardata(String func, String $1, String $2, String $3, String $4, int code)
	{
		String field_filter_sql = "";
		
		func = func.replace("$1", $1);
		
		if (code == 0)
			field_filter_sql += func.replace("$2", wildcardSubstitution2($2));
		else if (code == 1)
		{
			func = func.replace("$2", $2);
			field_filter_sql += func.replace("$3", wildcardSubstitution2($3));
		}
		else if (code == 2)
		{
			func = func.replace("$2", $2);
			func = func.replace("$3", $3);
			field_filter_sql += func.replace("$4", wildcardSubstitution2($4));
		}
		
		return field_filter_sql;
	}
	
	private void clearIds()
	{
		_indexes.clear();
		_attributes.clear();
	}
	
	// Creates the SQL statement to only select the records that adhere to the Filter definitions.
	public String generateSqlStatement(Filter filter, Field sourceField)
	{		
		return genSql(filter, sourceField);
	}
	
	// for the new functions
	private String wildcardSubstitution2(String fieldText)
	{
		if(fieldText.contains("*") || fieldText.contains("?"))
		{
			fieldText = fieldText.replace('*', '%');
			fieldText = fieldText.replace('?', '_');
			//fieldText = "'" + fieldText + "'";
		}
		return fieldText;
	}
		
	
	// Creates a select statement for a certain source field.
	private String createGenericRecordSelect(Field sourceField)
	{
		String sql = "select records.recordid, terms.chardata \n" + 
		 			 "from tms.records, tms.terms, tms.fields \n" +
		 			 "where records.archivedtimestamp is null \n" + 
		 			 "and terms.archivedtimestamp is null \n" +
		 			 "and terms.recordid = records.recordid \n" + 
		 			 "and terms.fieldid = fields.fieldid \n" +
		 			 "and terms.fieldid = " + sourceField.getFieldId() + " ";

		return sql;
	}
}