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
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import tms2.server.AppConfig;
import tms2.server.field.FieldManager;
import tms2.server.sql.FilterSqlGenerator;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.util.StringLiteralEscapeUtility;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.RecordElement;
import tms2.shared.Term;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SynonymManager 
{	
	public static void clear(HttpSession session)
	{
		RecordIdTracker.getSynonymIds(session).clear();
	}
	
	public static void addSynonym (HttpSession session, long last_index, RecordElement record_element)
	{
		RecordIdTracker.getSynonymIds(session).put(last_index, record_element);
	}
	
	public static boolean isSynonym (HttpSession session, long record_id)
	{
		return RecordIdTracker.getSynonymIds(session).containsKey(record_id);
	}
	
	public static long getRealRecordId (HttpSession session, long record_id)
	{		
		// check if the record id is a synonym or not.
		// if it isn't just return the record id.
		// if it is, return the record id in the synonym hash
		if (isSynonym(session, record_id))
		{
			RecordElement record_element = RecordIdTracker.getSynonymIds(session).get(record_id);
			
			return record_element.getRecordId();
		}
		
		return record_id;
	}
	
	
	public static String getSynonym (HttpSession session, long record_id)
	{
		// return the charadata of a synonym
		
		RecordElement synonym_record = RecordIdTracker.getSynonymIds(session).get(record_id);
		
		return synonym_record.getCharData();
	}
	
	public static Term getSynonymTerm (HttpSession session, long record_id)
	{
		// get the synonym from the hash. Create a term that only has chardata. I
		// dont think we need all the other properties of a term
		
		RecordElement synonym_record = RecordIdTracker.getSynonymIds(session).get(record_id);
		
		Term synonym_term = new Term ();
		
		synonym_term.setCharData(synonym_record.getCharData());
						
		return synonym_term;
	}
	
	private static long getFakeRecordId(HttpSession session, String synonymChardata, long realRecordId, User user)
	{
		if (user == null || user.isGuest())
			return realRecordId;
		else
		{
			HashMap<Long, RecordElement> synonyms = RecordIdTracker.getSynonymIds(session);
			for(long key : synonyms.keySet())
			{
				RecordElement re = synonyms.get(key);
				if(re.getCharData().equals(synonymChardata) && re.getRecordId() == realRecordId)
					return key;
			}
		}
		
		return 0;
	}
	
	public static ArrayList<RecordElement> getSynonymRecords(Connection connection, Field field) throws SQLException
	{
		ArrayList<RecordElement> synonyms = new ArrayList<RecordElement>();
				
		String sql = "select synonyms.chardata, records.recordid from " +
					 "tms.synonyms, tms.terms, tms.records where " + 
					 "synonyms.termid = terms.termid and " +
					 "terms.recordid = records.recordid and " +
					 "terms.fieldid = " + field.getFieldId() + "and " + 
					 "records.archivedtimestamp is NULL";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			RecordElement synonym = new RecordElement();
			synonym.setRecordId(result.getLong("recordid"));
			synonym.setCharData(result.getString("chardata"));						
		}
		
		result.close();
		stored_procedure.close();
		
		return synonyms;
	}
	
	public static ArrayList<RecordElement> getSynonymRecords(Connection connection, Filter filter, Field field) throws Exception
	{
		ArrayList<RecordElement> synonyms = new ArrayList<RecordElement>();
		
		long synonymfieldid = FieldManager.getSynonymFieldId(connection);
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		String sql = sql_generator.generateSynonymSqlStatement(filter, field, synonymfieldid);
				
		System.out.println("=== Synonyms filter sql ===");
		System.out.println(sql);
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			RecordElement synonym = new RecordElement();
			synonym.setRecordId(result.getLong("recordid"));
			synonym.setCharData(result.getString("chardata"));						
		}
				
		result.close();
		stored_procedure.close();
		
		return synonyms;
	}
	
	private static ArrayList<RecordElement> convertSynonymResultsToRecordElements(HttpSession session, ResultSet result, User user) throws Exception
	{
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		AppConfig config = AppConfig.getInstance();
		
		while (result.next())
		{
			long record_id = result.getLong("recordid");
			String chardata = result.getString("chardata");
			String fieldname = config.getSynonym();
						
			long fakeId = getFakeRecordId(session, chardata, record_id, user);
			
			RecordElement element = new RecordElement(fakeId, record_id, chardata, fieldname, chardata);
			
			elements.add(element);
		}
		
		return elements;
	}
	
	public static ArrayList<RecordElement> searchExactMatch(Connection connection, HttpSession session, User user, Field field, Filter filter, String search_prompt, long termdb_id, long project_id) throws Exception
	{
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by synonyms.chardata";
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if (filter != null)			
			main_sql = sql_generator.generateSearchSynonymSqlStatement(filter, field, FieldManager.getSynonymFieldId(connection)) + " ";
		
		if (termdb_id == -1 && project_id == -1)
		{
			main_sql += "select terms.recordid, synonyms.synonymid, synonyms.chardata, fields.fieldname " +
					    "from tms.synonyms, tms.fields, tms.terms, tms.records " +
					    "where synonyms.termid = terms.termid " +
					    "and terms.recordid = records.recordid " +
					    "and fields.fieldid = terms.fieldid " +
					    "and fields.fieldid = " + field.getFieldId() + " " +
					    "and synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					    "and synonyms.chardata = '" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "'";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;	
		}
		else if (termdb_id > -1 && project_id > -1)
		{
			main_sql += "select terms.recordid, synonyms.synonymid, synonyms.chardata, fields.fieldname " +
					    "from tms.synonyms, tms.fields, tms.terms, tms.records, tms.termbases, tms.projects, tms.recordprojects " +
					    "where synonyms.termid = terms.termid " +
					    "and terms.recordid = records.recordid " +
					    "and fields.fieldid = terms.fieldid " +
					    "and fields.fieldid = " + field.getFieldId() + " " +
					    "and termbases.termbaseid = projects.termbaseid " + 
					    "and termbases.termbaseid = " + termdb_id + " " +
					    "and projects.projectid = " + project_id + " " +
					    "and recordprojects.recordid = records.recordid and recordprojects.projectid = projects.projectid " + 
					    "and records.termbaseid = termbases.termbaseid " + 
					    "and synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					    "and synonyms.chardata = '" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "'";

			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;	
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "select terms.recordid, synonyms.synonymid, synonyms.chardata, fields.fieldname " +
					    "from tms.synonyms, tms.fields, tms.terms, tms.records, tms.termbases " +
					    "where synonyms.termid = terms.termid " +
					    "and terms.recordid = records.recordid " +
					    "and fields.fieldid = terms.fieldid " +
					    "and fields.fieldid = " + field.getFieldId() + " " +					     
					    "and termbases.termbaseid = " + termdb_id + " " +					    
					    "and records.termbaseid = termbases.termbaseid " + 
					    "and synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					    "and synonyms.chardata = '" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "' "; 

			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		elements = convertSynonymResultsToRecordElements(session, result, user);
		
		result.close();
		stored_procedure.close();
		
		return elements;
	}
	
	public static ArrayList<RecordElement> searchFuzzy (Connection connection, HttpSession session, Field field, User user, 
						Filter filter, String search_prompt, long termdb_id, long project_id) throws Exception
	{		
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		String main_sql = "";
		String filter_sql = "\nand synonymsmin.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by tms.levenshtein(LOWER(synonymsmin.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "'))";
			
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if (filter != null)			
			main_sql = sql_generator.generateSearchSynonymSqlStatement(filter, field, FieldManager.getSynonymFieldId(connection)) + " ";
		
		if (termdb_id == -1 && project_id == -1)
		{
			main_sql += "\nselect synonymsmin.*, fields.fieldname " +
					   "from tms.terms, tms.synonymsmin, tms.fields, tms.records " +
					   "where synonymsmin.archivedtimestamp is null " +
					   "and synonymsmin.termid = terms.termid " + 
					   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " + 
					   "and synonymsmin.recordid = records.recordid " +
					   "and tms.levenshtein(LOWER(synonymsmin.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3 ";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;							 
		}
		else if (termdb_id > -1 && project_id > -1)
		{
			main_sql += "\nselect synonymsmin.*, fields.fieldname " +
			   		   "from tms.terms, tms.synonymsmin, tms.fields, tms.termbases, tms.projects, tms.recordprojects, tms.records " +
			   		   "where synonymsmin.archivedtimestamp is null " +
			   		   "and synonymsmin.termid = terms.termid " + 					  
			   		   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " + 
			   		   "and termbases.termbaseid = projects.termbaseid " + 
					   "and termbases.termbaseid = " + termdb_id + " " +
					   "and projects.projectid = " + project_id + " " +
					   "and recordprojects.recordid = synonymsmin.recordid and recordprojects.projectid = projects.projectid " + 
					   "and records.termbaseid = termbases.termbaseid " + 
					   "and records.recordid = synonymsmin.recordid " + 
					   "and tms.levenshtein(LOWER(synonymsmin.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3 ";
	
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "\nselect synonymsmin.*, fields.fieldname " +
	   		   		   "from tms.terms, tms.synonymsmin, tms.fields, tms.termbases, tms.records " +
	   		   		   "where synonymsmin.archivedtimestamp is null " +
	   		   		   "and synonymsmin.termid = terms.termid " + 
	   		   		   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " + 
	   		   		   "and termbases.termbaseid = " + termdb_id + " " +  
	   		   		   "and records.termbaseid = termbases.termbaseid " + 
	   		   		   "and records.recordid = synonymsmin.recordid " + 
	   		   		   "and tms.levenshtein(LOWER(synonymsmin.chardata), LOWER(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "')) < 3 ";		  		   		   

			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;								
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		elements = convertSynonymResultsToRecordElements(session, result, user);
		
		result.close();
		stored_procedure.close();
		
		return elements;
	}
	
	public static ArrayList<RecordElement> searchWildCard(Connection connection, HttpSession session, Field field, User user,
			Filter filter, String search_prompt, long termdb_id, long project_id) throws Exception
	{
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		String main_sql = "";
		String filter_sql = "\nand terms.recordid IN (SELECT recordid FROM with_filter) ";
		String end_sql = "\norder by synonyms.chardata";
		
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		if (filter != null)			
			main_sql = sql_generator.generateSearchSynonymSqlStatement(filter, field, FieldManager.getSynonymFieldId(connection)) + " ";
		
		if (termdb_id == -1 && project_id == -1)
		{
			main_sql += "\nselect terms.recordid, synonyms.chardata, fields.fieldname " +
					    "from tms.terms, tms.synonyms, tms.fields, tms.records " +
					    "where synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					    "and synonyms.termid = terms.termid " +
					    "and terms.fieldid = fields.fieldid " +
						"and fields.fieldid = " + field.getFieldId() + " " + 
					    "and terms.recordid = records.recordid " + 
					    "and lower(synonyms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') ";	
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		else if (termdb_id > -1 && project_id > -1)
		{
			main_sql += "\nselect terms.recordid, synonyms.chardata, fields.fieldname " +
					   "from tms.terms, tms.synonyms, tms.fields, tms.termbases, tms.projects, tms.recordprojects, tms.records " +
					   "where synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					   "and terms.termid = synonyms.termid " +
					   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " +  
					   "and termbases.termbaseid = projects.projectid " +
					   "and termbases.termbaseid = " + termdb_id + " " +
					   "and projects.projectid = " + project_id + " " + 
					   "and recordprojects.recordid = terms.recordid and recordprojects.projectid = projects.projectid " +
					   "and lower(synonyms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " + 
					   "and records.termbaseid = termbases.termbaseid " + 
					   "and records.recordid = terms.recordid";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;
		}
		else if (termdb_id > -1 && project_id == -1)
		{
			main_sql += "\nselect terms.recordid, synonyms.chardata, fields.fieldname " +
					   "from tms.terms, tms.synonyms, tms.fields, tms.termbases, tms.records " +
					   "where synonyms.archivedtimestamp is null and records.archivedtimestamp is null " + 
					   "and terms.termid = synonyms.termid " +
					   "and terms.fieldid = fields.fieldid " +
					   "and fields.fieldid = " + field.getFieldId() + " " +  
					   "and termbases.termbaseid = " + termdb_id + " " +						   
					   "and lower(synonyms.chardata) similar to lower(E'" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " + 
					   "and records.termbaseid = termbases.termbaseid " + 
					   "and records.recordid = terms.recordid";
			
			if (filter != null)
				main_sql += filter_sql;
			
			main_sql += end_sql;			
		}	 						 						 			 			
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, main_sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		elements = convertSynonymResultsToRecordElements(session, result, user);
		
		result.close();
		stored_procedure.close();
		
		return elements;
	}
	
	public static ArrayList<RecordElement> searchDuplicates(Connection connection, HttpSession session, User user, 
			  												Field field, String search_prompt) throws Exception
	{
		ArrayList<RecordElement> elements = new ArrayList<RecordElement>();
		
		String sql = "select terms.recordid, synonyms.chardata, fields.fieldname from tms.terms, " + 
			         "tms.synonyms, tms.records, tms.fields where " +
			         "synonyms.termid = terms.termid " +  
			         "and terms.fieldid = fields.fieldid " + 
			         "and fields.fieldid = " + field.getFieldId() + 
			         "and terms.recordid = records.recordid " +  
			         "and LOWER(synonyms.chardata) = LOWER('" + StringLiteralEscapeUtility.escapeStringLiteral(search_prompt) + "') " +
			         "and synonyms.archivedtimestamp is null and records.archivedtimestamp is null";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		elements = convertSynonymResultsToRecordElements(session, result, user);
		
		result.close();
		stored_procedure.close();
		
		return elements;		
	}
}
