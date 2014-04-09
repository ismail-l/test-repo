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

package tms2.server.record;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import tms2.server.AppConfig;
import tms2.server.accessright.AccessRightManager;
import tms2.server.sql.FilterSqlGenerator;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.util.StringLiteralEscapeUtility;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.Record;
import tms2.shared.RecordElement;
import tms2.shared.Term;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.wrapper.RecordRecordElementWrapper;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermManager 
{
	public static ArrayList<TerminlogyObject> getAllTermsForRecord(Connection connection, long record_id, User user, boolean update_tracker) throws Exception
	{
		ArrayList<TerminlogyObject> terms = new ArrayList<TerminlogyObject>();
		
		String sql = "select terms.termid, terms.chardata, records.recordid, fields.* " + 
					 "from tms.terms, tms.records, tms.fields where " + 
					 "terms.recordid = records.recordid " + 
					 "and terms.fieldid = fields.fieldid " +
					 "and records.recordid = " + record_id + " " +  
					 "and terms.archivedtimestamp is NULL and records.archivedtimestamp is null order by fields.sortindex";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			Term term = getTerm(connection, result, user, update_tracker);
			terms.add(term);
		}
		
		return terms;
	}
	
	public static Term getTerm(Connection connection, ResultSet result, User user, boolean update_tracker) throws Exception
	{
		AppConfig config = AppConfig.getInstance();
		
		Term term = new Term();
		
		term.setResourceId(result.getLong("termid"));
		term.setCharData(result.getString("chardata"));		
		term.setRecordId(result.getLong("recordid"));
		term.setFieldId(result.getLong("fieldid"));
		term.setFieldName(result.getString("fieldname"));
		term.setFieldTypeId(result.getInt("fieldtypeid"));
		term.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		term.setMaxlength(result.getInt("maxlength"));
		term.setDefaultValue(result.getString("defaultvalue"));
		term.setSortIndex(result.getLong("sortindex"));
		
		if (user != null && ! user.isGuest() && update_tracker)
			term.setAuditTrail(AuditTrailManager.getAuditTrailForTerm(connection, term.getResourceId()));
		
		term.setTermAttributes(TermAttributeManager.getTermAttributesForTerm(connection, term, user, update_tracker));
		
		if (update_tracker)
		{
			if (user != null && ! user.isGuest())
			{
				term.setUserAccessRight(AccessRightManager.assignAccessRight(connection, term, user.getUserId(), true));
				term.setUserCategoryAccessRight(AccessRightManager.assignAccessRight(connection, term, user.getUserCategoryId(), false));
			}
			else
			{
				User guest = new User();
				guest.setUserCategoryId(3);
				
				term.setUserCategoryAccessRight(AccessRightManager.assignAccessRight(connection, term, guest.getUserCategoryId(), false));
			}
		}
		
		if (term.getFieldName().equals(config.getSortIndexField()))			
			term.setMandatory(true);	
				
		return term;
	}
	
	private static ArrayList<RecordElement> convertTermResultsToRecordElements(ResultSet result) throws SQLException
	{
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		while (result.next())
		{
			long recordid = result.getLong("recordid");
			String chardata = result.getString("chardata");
			String fieldname = result.getString("fieldname");
			
			RecordElement element = new RecordElement(recordid, recordid, chardata, fieldname, null);
			
			elements.add(element);
		}
		
		return elements;
	}
	
	public static RecordRecordElementWrapper searchExactMatch(Connection connection, HttpSession session, User user, 
															  Field field, Filter filter, String search_prompt, boolean browse_textbox_search,
															  long termdb_id, long project_id) throws Exception
	{
		RecordRecordElementWrapper wrapper = null;
		
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by terms.chardata";
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if(filter != null)		
			main_sql = sql_generator.generateSearchSQLStatement(filter, field) + " ";
		
		if ((termdb_id == -1 && project_id == -1) || browse_textbox_search)
		{
			main_sql += "\nselect terms.recordid, terms.termid, terms.chardata, fields.fieldname " +
			  			"from tms.terms, tms.fields, tms.records " + 
			  			"where terms.fieldid = fields.fieldid and " +
			  			"terms.recordid = records.recordid " + 
			  			"and fields.fieldid = " + field.getFieldId() + " " + 
			  			"and terms.archivedtimestamp is null and records.archivedtimestamp is null and lower(terms.chardata) = LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')";
					
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;			
		}
		else if (termdb_id > -1 && project_id > -1)
		{
			main_sql += "\nselect terms.recordid, terms.termid, terms.chardata, fields.fieldname " +
  						"from tms.terms, tms.fields, tms.records, tms.termbases, tms.projects, tms.recordprojects " + 
  						"where terms.fieldid = fields.fieldid and " +
  						"terms.recordid = records.recordid " + 
  						"and fields.fieldid = " + field.getFieldId() + " " + 
  						"and terms.archivedtimestamp is null and records.archivedtimestamp is null " +
  						"and termbases.termbaseid = projects.termbaseid " + 
  						"and termbases.termbaseid = " + termdb_id + " " + 
  						"and projects.projectid = " + project_id + " " +
 					    "and recordprojects.recordid = terms.recordid and recordprojects.projectid = projects.projectid " + 
 					    "and records.termbaseid = termbases.termbaseid " + 
  						"and lower(terms.chardata) = LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')";
					   
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
			
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "\nselect terms.recordid, terms.termid, terms.chardata, fields.fieldname " +
  						"from tms.terms, tms.fields, tms.records, tms.termbases " + 
  						"where terms.fieldid = fields.fieldid and " +
  						"terms.recordid = records.recordid " + 
  						"and fields.fieldid = " + field.getFieldId() + " " + 
  						"and terms.archivedtimestamp is null and records.archivedtimestamp is null " +
  						"and termbases.termbaseid = " + termdb_id + " " +   						 
 					    "and records.termbaseid = termbases.termbaseid " + 
  						"and lower(terms.chardata) = LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')";
	
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;			
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		ArrayList<RecordElement> term_elements = convertTermResultsToRecordElements(result);
		ArrayList<RecordElement> synonym_elements = SynonymManager.searchExactMatch(connection, session, user, field, filter, search_prompt, termdb_id, project_id);
		
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		elements.addAll(term_elements);
		elements.addAll(synonym_elements);
						
		wrapper = new RecordRecordElementWrapper();
				
		if (elements.size() == 0 && browse_textbox_search)
		{
			Record record = findClosestAlphabeticalNeighbour(connection, session, user, filter, field, search_prompt);
			wrapper.setRecord(record);
		}
		else
		{
			if (browse_textbox_search)
			{
				RecordElement element = elements.get(0);
				wrapper.setRecord(RecordManager.retrieveRecordByRecordId(connection, session, user, element.getRecordId(), true));
			}
			
			wrapper.setRecordElements(elements);
		}
			
		result.close();
		stored_procedure.close();
		
		return wrapper;
	}
	
	private static Record findClosestAlphabeticalNeighbour(Connection connection, HttpSession session, User user, Filter filter, Field field, String search_prompt) throws Exception
	{
		Record record = null;
		
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by terms.chardata desc";
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if(filter != null)		
			main_sql = sql_generator.generateSearchSQLStatement(filter, field) + " ";					

		main_sql += "select records.recordid, terms.chardata from tms.records, tms.terms, tms.fields " +
			        "where terms.archivedtimestamp is null and records.archivedtimestamp is null " +  
			        "and terms.recordid = records.recordid " +
			        "and terms.fieldid = fields.fieldid " +
			        "and fields.fieldid = " + field.getFieldId() + " ";
		
		if (filter != null)
			main_sql += filter_sql;
		
		main_sql += end_sql;		
						
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		search_prompt = search_prompt.toLowerCase();
		
		long previous_rec_id = 0;
		
		while (result.next())
		{
			long recordid = result.getLong("recordid");
			String chardata = result.getString("chardata").toLowerCase();
						
			if (search_prompt.compareTo(chardata) > 0)
			{		
				if (previous_rec_id != 0)
					record = RecordManager.retrieveRecordByRecordId(connection, session, user, previous_rec_id, true);	
				else
					record = RecordManager.retrieveRecordByRecordId(connection, session, user, recordid, true);
				
				break;
			}
			else
				previous_rec_id = recordid;
		}
				
		result.close();
		stored_procedure.close();
		
		return record;
	}
	
	public static RecordRecordElementWrapper searchFuzzy(Connection connection, HttpSession session, User user, 
			  											 Field field, Filter filter, String search_prompt,
			  											 long termdb_id, long project_id) throws Exception
	{
		RecordRecordElementWrapper wrapper = null;
				
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by tms.levenshtein(LOWER(terms.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "'))";
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if(filter != null)		
			main_sql = sql_generator.generateSearchSQLStatement(filter, field) + " ";
			
		if (termdb_id == -1 && project_id == -1)
		{
			main_sql += "\nselect terms.termid, terms.chardata, terms.recordid, fields.fieldname " +
					   "from tms.terms, tms.fields, tms.records " +
					   "where terms.archivedtimestamp is null and records.archivedtimestamp is null " +
					   "and terms.recordid = records.recordid " +
					   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " + 
					   "and tms.levenshtein(LOWER(terms.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3 ";
					
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
			
		}
		else if (termdb_id > -1 && project_id > -1)
		{
			main_sql += "\nselect terms.termid, terms.chardata, terms.recordid, fields.fieldname " +
					   "from tms.terms, tms.fields, tms.termbases, tms.projects, tms.recordprojects, tms.records " +
					   "where terms.archivedtimestamp is null and records.archivedtimestamp is null " +
					   "and terms.recordid = records.recordid " +
					   "and fields.fieldid = terms.fieldid " +
					   "and fields.fieldid =  " + field.getFieldId() + " " + 
					   "and termbases.termbaseid = projects.termbaseid " + 
					   "and termbases.termbaseid = " + termdb_id + " " +
					   "and projects.projectid = " + project_id + " " +
					   "and recordprojects.recordid = terms.recordid and recordprojects.projectid = projects.projectid " + 
					   "and records.termbaseid = termbases.termbaseid " + 
					   "and tms.levenshtein(LOWER(terms.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3";
					   
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
			
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "\nselect terms.termid, terms.chardata, terms.recordid, fields.fieldname " +
					   "from tms.terms, tms.fields, tms.termbases, tms.records " +
					   "where terms.archivedtimestamp is null and records.archivedtimestamp is null " +
					   "and records.recordid = terms.recordid " +
					   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " + 
					   "and termbases.termbaseid = " + termdb_id + " " + 
					   "and records.termbaseid = termbases.termbaseid " + 
					   "and tms.levenshtein(LOWER(terms.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3 ";					   
	
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;			
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		ArrayList<RecordElement> term_elements = convertTermResultsToRecordElements(result);		
		ArrayList<RecordElement> synonym_elements = SynonymManager.searchFuzzy(connection, session, field, user, filter, search_prompt, termdb_id, project_id);
		
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		elements.addAll(term_elements);
		elements.addAll(synonym_elements);
				
		wrapper = new RecordRecordElementWrapper();
		wrapper.setRecordElements(elements);
		
		result.close();
		stored_procedure.close();
		
		return wrapper;
	}
	
	public static RecordRecordElementWrapper searchWildCard(Connection connection, HttpSession session, User user, 
				 											Field field, Filter filter, String search_prompt, 
				 											long termdb_id, long project_id) throws Exception
	{
		search_prompt = search_prompt.replace('*', '%');
		search_prompt = search_prompt.replace('?', '_');
		
		RecordRecordElementWrapper wrapper = null;
		
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by terms.chardata";

		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if(filter != null)		
			main_sql = sql_generator.generateSearchSQLStatement(filter, field) + " ";		
				
		if (termdb_id == -1 && project_id == -1)
		{			
			main_sql += "\nselect terms.termid, terms.recordid, terms.chardata, fields.fieldname from tms.terms, tms.fields, tms.records " + 
						" where terms.archivedtimestamp is null and records.archivedtimestamp is null and " + 
						"lower(terms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " +
						"and terms.recordid = records.recordid " +
						"and fields.fieldid = terms.fieldid " +
						"and fields.fieldid = " + field.getFieldId();
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		else if (termdb_id > -1 && project_id > -1)
		{			 
			main_sql += "\nselect terms.termid, terms.recordid, terms.chardata, fields.fieldname from tms.terms, " +
						"tms.fields, tms.termbases, tms.projects, tms.recordprojects, tms.records" + 
						" where terms.archivedtimestamp is null and records.archivedtimestamp is null and " + 
						" lower(terms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " +						
						"and fields.fieldid = terms.fieldid " +
						"and fields.fieldid =  " + field.getFieldId() + " " +
						"and termbases.termbaseid = projects.projectid " + 
						"and termbases.termbaseid = " + termdb_id + " " + 
						"and projects.projectid = " + project_id + " " + 
						"and recordprojects.recordid = terms.recordid and recordprojects.projectid = projects.projectid " +
						"and records.termbaseid = termbases.termbaseid " + 
						"and records.recordid = terms.recordid";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "\nselect terms.termid, terms.recordid, terms.chardata, fields.fieldname from tms.terms, " +
						"tms.fields, tms.termbases, tms.records" + 
						" where terms.archivedtimestamp is null and records.archivedtimestamp is null and " +
						"lower(terms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " +						
						"and fields.fieldid = terms.fieldid " +
						"and fields.fieldid = " + field.getFieldId() + " " +
						"and termbases.termbaseid = " + termdb_id + " "  +
						"and records.termbaseid = termbases.termbaseid " + 
						"and records.recordid = terms.recordid";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;		
		}	
			
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		ArrayList<RecordElement> term_elements = convertTermResultsToRecordElements(result);
		ArrayList<RecordElement> synonym_elements = SynonymManager.searchWildCard(connection, session, field, user, filter, search_prompt, termdb_id, project_id);
		
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		elements.addAll(term_elements);
		elements.addAll(synonym_elements);
				
		wrapper = new RecordRecordElementWrapper();
		wrapper.setRecordElements(elements);		
		
		result.close();
		stored_procedure.close();
		
		return wrapper;		
	}
	
	public static RecordRecordElementWrapper searchDuplicates(Connection connection, HttpSession session, User user, 
														  Field field, String search_prompt) throws Exception
	{
		RecordRecordElementWrapper wrapper = null;
		
		String sql = "select terms.termid, terms.recordid, terms.chardata, fields.fieldname from tms.terms, " +
        			 "tms.records, tms.fields where " +
        			 "fields.fieldid = terms.fieldid " + 
        			 "and fields.fieldid = " + field.getFieldId() + " " +
        			 "and terms.recordid = records.recordid " + 
        			 "and LOWER(terms.chardata) = LOWER('" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " +
        			 "and terms.archivedtimestamp is null and records.archivedtimestamp is NULL";
			         
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		ArrayList<RecordElement> term_elements = convertTermResultsToRecordElements(results);
		ArrayList<RecordElement> synonym_elements = SynonymManager.searchDuplicates(connection, session, user, field, search_prompt);
		
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		elements.addAll(term_elements);
		elements.addAll(synonym_elements);
				
		wrapper = new RecordRecordElementWrapper();
		wrapper.setRecordElements(elements);		
		
		return wrapper;
	}
}
