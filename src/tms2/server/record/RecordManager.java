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
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.server.AppConfig;
import tms2.server.logging.LogUtility;
import tms2.server.project.ProjectManager;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.user.UserManager;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.Record;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.wrapper.RecordDetailsWrapper;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordManager 
{
	public static RecordDetailsWrapper resetBrowser(Connection connection, HttpSession session, User user, 
												    Field field, long current_recordid, boolean refresh) throws Exception
	{
		RecordDetailsWrapper record_details = new RecordDetailsWrapper();
		RecordIds<Long> recordIds = null;
		
		Filter filter = (Filter) session.getAttribute("filter");
		
		if (filter != null)
			record_details.setIsFilter(true);
		
		if (refresh)
		{
			if (! record_details.isFilter())
				recordIds = RecordIdTracker.refreshRecordIdsInSessionByField(session, connection, field);
			else												
				recordIds = RecordIdTracker.refreshRecordIdsInSessionByFilter(session, connection, filter, field);			
		}

		if (current_recordid > -1)
		{
			Record record = retrieveRecordByRecordId(connection, session, user, current_recordid, true);
			
			// If this is a guest session, this RecordIdTracker may or may not be null
			int number_of_records = RecordIdTracker.getNumberOfRecordIdsInSession(session);;
						
			record_details.setRecord(record);
			
			// This value will not be displayed for a guest session
			record_details.setNumberOfRecords(number_of_records);
		}
		else
		{
			recordIds = RecordIdTracker.getRecordIds(session);
					
			if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
			{
				long record_id = recordIds.getFirstRecordId().longValue();
										
				Record record = retrieveRecordByRecordId(connection, session, user, record_id, true);
				int number_of_records = RecordIdTracker.getNumberOfRecordIdsInSession(session);;
								
				record_details.setRecord(record);
				record_details.setNumberOfRecords(number_of_records);
			}
			else			
				record_details.setNumberOfRecords(0);
		}
		
		return record_details;
	}	
	
	public static Record getPreviousRecord(Connection connection, HttpSession session, User user) throws Exception
	{
		Record record = null;
		
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
		{
			long record_id = recordIds.getPreviousRecordId().longValue(); 
			
			record = retrieveRecordByRecordId(connection, session, user, record_id, true);			
		}
		
		return record;
	}
	
	public static Record getNextRecord(Connection connection, HttpSession session, User user) throws Exception
	{
		Record record = null;
		
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
		{
			long record_id = recordIds.getNextRecordId().longValue(); 
			
			record = retrieveRecordByRecordId(connection, session, user, record_id, true);			
		}
		
		return record;
	}
	
	public static Record getLastRecord(Connection connection, HttpSession session, User user) throws Exception
	{
		Record record = null;
		
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
		{
			long record_id = recordIds.getLastRecordId().longValue(); 
			
			record = retrieveRecordByRecordId(connection, session, user, record_id, true);			
		}
		
		return record;
	}
	
	public static Record retrieveRecordByRecordId(Connection connection, HttpSession session, User user, long record_id, boolean update_tracker) throws Exception
	{
		Record record = lookupCurrentRecord(connection, session, user, record_id, update_tracker);
		
		if (record != null && update_tracker)
		{
			RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
			recordIds.setCurrentRecordId(new Long(record_id));	
			setPreviousAndNextRecords(connection, session, user, record);
		}
		
		return record;
	}
	
	private static Record lookupCurrentRecord(Connection connection, HttpSession session, User user, long record_id, boolean update_tracker) throws Exception
	{
		long start = Calendar.getInstance().getTimeInMillis();
		
		Record record = null;
				
		long real_recordid = SynonymManager.getRealRecordId(session, record_id);
		
		String sql = "select records.* from tms.records where records.recordid = " + real_recordid + 
					 "and records.archivedtimestamp is null";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		if (result.next())
		{
			record = new Record();
			record.setRecordId(result.getLong("recordid"));
			record.setTermdbId(result.getLong("termbaseid"));
			
			if (user != null && ! user.isGuest())
				record.setProjects(ProjectManager.getRecordProjects(connection, record.getRecordId(), user));
			
			if (user != null && ! user.isGuest() && update_tracker)
				record.setAuditTrail(AuditTrailManager.getAuditTrailForRecord(connection, record_id));
			
			record.setRecordattributes(RecordAttributeManager.getAllRecordAttributesForRecord(connection, record_id, user, update_tracker));
			record.setTerms(TermManager.getAllTermsForRecord(connection, record_id, user, update_tracker));									
			record.setIsSynonym(SynonymManager.isSynonym(session, record_id));
			
			if (record.isSynonym())
			{
				record.setSynonym(SynonymManager.getSynonym(session, record_id));
				record.setSynonymId(record_id);
			}
		}
		
		result.close();
		stored_procedure.close();
		
		long end = (Calendar.getInstance().getTimeInMillis() - start);
		
		LogUtility.log(Level.INFO, "Retrieval for record id " + record.getRecordId() + " in session " + session.getId() + " took " + end + " ms" );
		
		return record;
	}
	
	private static void setPreviousAndNextRecords(Connection connection, HttpSession session, User user, Record record) throws Exception 
	{		
		Term previousTerm = retrievePreviousTerm(connection, session, user, record);
		record.setPreviousTerm(previousTerm);
		Term nextTerm = retrieveNextTerm(connection, session, user, record);
		record.setNextTerm(nextTerm);
	}
	
	private static Term retrievePreviousTerm(Connection connection, HttpSession session, User user, Record record) throws Exception	
	{
		Term term = null;
		
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		
		if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
		{
			long recordid_index = recordIds.peekPreviousRecordId().longValue();
			
			//Only sneak a peek at the previous id. Don't get it, otherwise the tracker updates the index.
			//Record previousRecord = this.lookupRecordByRecordId(session, authToken, recordid_index, false);
			Record previousRecord = lookupCurrentRecord(connection, session, user, recordid_index, false);
			
			long current_rec_id = -1;
			
			if (record.getIsSynonym())
				current_rec_id = record.getSynonymId();
			else
				current_rec_id = record.getRecordId();
			
			long previous_rec_id = -1;
			
			if (previousRecord.getIsSynonym())
				previous_rec_id = previousRecord.getSynonymId();
			else
				previous_rec_id = previousRecord.getRecordId();
			
			if (current_rec_id != previous_rec_id)
			{	
				AppConfig config = AppConfig.getInstance();
				
				if (recordIds.isCustomInputModelFieldSet())
				{
					if (! previousRecord.getIsSynonym())					
						term = previousRecord.getCustomSortIndexTerm(recordIds.getCustomInputModelField());					
					else					
						term = SynonymManager.getSynonymTerm(session, recordid_index);											
				}
				else
				{
					if (! previousRecord.getIsSynonym())								
						term = previousRecord.getSortIndexTerm(config.getSortIndexField());					
					else					
						term = SynonymManager.getSynonymTerm(session, recordid_index);											
				}
			}
		}
		
		return term;
	}
	
	private static Term retrieveNextTerm(Connection connection, HttpSession session, User user, Record record) throws Exception 
	{		
		Term term = null;
		
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		
		if (recordIds != null && recordIds.size() > 0) //There are indeed records in the database
		{
			long recordid_index = recordIds.peekNextRecordId().longValue();
			
			//Only sneak a peek at the next id. Don't get it, otherwise the tracker updates the index.
			//Record nextRecord = this.lookupRecordByRecordId(session, authToken, recordid_index, false);
			Record nextRecord = lookupCurrentRecord(connection, session, user, recordid_index, false);
			
			long current_rec_id = -1;
			
			if (record.getIsSynonym())
				current_rec_id = record.getSynonymId();
			else
				current_rec_id = record.getRecordId();
			
			long next_rec_id = -1;
			
			if (nextRecord.getIsSynonym())
				next_rec_id = nextRecord.getSynonymId();
			else
				next_rec_id = nextRecord.getRecordId();
												
			if (current_rec_id != next_rec_id)			
			{	
				AppConfig config = AppConfig.getInstance();
				
				if (recordIds.isCustomInputModelFieldSet())
				{										
					if (! nextRecord.getIsSynonym())					
						term = nextRecord.getCustomSortIndexTerm(recordIds.getCustomInputModelField());					
					else					
						term = SynonymManager.getSynonymTerm(session, recordid_index);													
				}
				else
				{
					if (! nextRecord.getIsSynonym())					
						term = nextRecord.getSortIndexTerm(config.getSortIndexField());							
					else					
						term = SynonymManager.getSynonymTerm(session, recordid_index);					
				}
			}
		}	
		
		return term;
	}
	
	public static long archiveRecord(Connection connection, User user, Record record) throws Exception
	{		
		CallableStatement stored_procedure = StoredProcedureManager.archiveRecord(connection, record);
		
		long recordid = -1;
		
		recordid = (Long) stored_procedure.getObject(1);
		
		archiveTerminologyObjects(record);
		
		updateTerminologyObjects(connection, user, record.getRecordAttributes(), recordid);
		updateTerminologyObjects(connection, user, record.getTerms(), recordid);
		
		stored_procedure.close();
	
		return recordid;
	}
	
	private static void archiveTerminologyObjects(Record record) 
	{
		ArrayList<TerminlogyObject> record_attributes = record.getRecordAttributes();
		if (record_attributes != null && record_attributes.size() > 0)
		{
			Iterator<TerminlogyObject> iter = record_attributes.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject terminology_object = iter.next();
				terminology_object.setIsArchived(true);
			}
		}
		
		ArrayList<TerminlogyObject> terms = record.getTerms();
		if (terms != null && terms.size() > 0)
		{
			Iterator<TerminlogyObject> iter = terms.iterator();
			while (iter.hasNext())
			{
				TerminlogyObject term = iter.next();
				term.setIsArchived(true);
				
				ArrayList<ChildTerminologyObject> term_attributes = ((Term)term).getTermAttributes();
				if (term_attributes == null || term_attributes.size() == 0)
					continue;
				
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					ChildTerminologyObject term_attribute = term_attr_iter.next();
					term_attribute.setIsArchived(true);
					
					if (term_attribute instanceof Synonym)
					{
						ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
						if (synonym_attributes == null || synonym_attributes.size() == 0)
							continue;
						
						Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
						while (synonym_attr_iter.hasNext())
						{
							ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
							synonym_attribute.setIsArchived(true);
						}						
					}
				}
			}
		}
	}
	
	public static long updateRecord(Connection connection, User user, Record record, boolean is_updating) throws Exception
	{		
		long record_id = -1;
				
		if (! is_updating)
		{
			CallableStatement stored_procedure = StoredProcedureManager.createRecord(connection, record);
			
			record_id = (Long)stored_procedure.getObject(1);
			
			if (record_id == -1)
			{
				stored_procedure.close();
				return -1;
			}
			
			stored_procedure.close();
		}
		else
			record_id = record.getRecordId();
										

		AuditTrailManager.updateRecordAuditTrail(connection, user, record_id, is_updating);		
		ProjectManager.updateRecordProjects(connection, record_id, record.getProjects());
		
		updateTerminologyObjects(connection, user, record.getRecordAttributes(), record_id);
		updateTerminologyObjects(connection, user, record.getTerms(), record_id);
			
		if (is_updating)
			unlockRecord(connection, record_id);
		
		return record_id;
	}
	
	private static void updateTerminologyObjects(Connection connection, User user, ArrayList<TerminlogyObject> terminology_objects, long record_id) throws SQLException	
	{
		if (terminology_objects == null || terminology_objects.size() == 0)
			return;
		
		Iterator<TerminlogyObject> iter = terminology_objects.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject terminology_object = iter.next();
			
			CallableStatement stored_procedure = null;
			
			if (terminology_object.getResourceId() > -1)	
			{
				if (! terminology_object.isArchived())
					stored_procedure = StoredProcedureManager.updateTerminologyObject(connection, terminology_object);
				else
					StoredProcedureManager.archiveTerminologyObject(connection, terminology_object);
			}
			else			
				stored_procedure = StoredProcedureManager.addTerminologyObject(connection, terminology_object, record_id);			
			
			long result = -1;
			
			if (! terminology_object.isArchived())
			{
				result = (Long) stored_procedure.getObject(1);
			
				if (result > -1)
					AuditTrailManager.updateTerminlogyObjectAuditTrail(connection, user, terminology_object, result);
				
				stored_procedure.close();
			}
			
			if (terminology_object instanceof Term)
			{
				ArrayList<ChildTerminologyObject> term_attributes = ((Term)terminology_object).getTermAttributes();
				
				if (term_attributes == null || term_attributes.size() == 0)
					continue;
				
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					TermAttribute term_attribute = (TermAttribute) term_attr_iter.next();
					long parent_id = updateChildTerminologyObject(connection, user, term_attribute, result);
															
					if (term_attribute instanceof Synonym)
					{
						ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
						
						if (synonym_attributes == null || synonym_attributes.size() == 0)
							continue;
						
						Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
						while (synonym_attr_iter.hasNext())
						{
							SynonymAttribute synonym_attribute = (SynonymAttribute) synonym_attr_iter.next();
							updateChildTerminologyObject(connection, user, synonym_attribute, parent_id);														
						}
					}
					
				}
			}						
		}
	}
	
	private static long updateChildTerminologyObject(Connection connection, User user, ChildTerminologyObject child_terminology_object, long parent_id) throws SQLException
	{
		long result = -1;
		CallableStatement stored_procedure = null;
		
		if (child_terminology_object.getResourceId() > -1)
		{
			if (! child_terminology_object.isArchived())
				stored_procedure = StoredProcedureManager.updateChildTerminologyObject(connection, child_terminology_object);
			else
				StoredProcedureManager.archiveTerminologyObject(connection, child_terminology_object);
		}
		else
			stored_procedure = StoredProcedureManager.addChildTerminologyObject(connection, child_terminology_object, parent_id);
			
		if (! child_terminology_object.isArchived())
		{
			result = (Long) stored_procedure.getObject(1);
			
			if (result > -1)
				AuditTrailManager.updateTerminlogyObjectAuditTrail(connection, user, child_terminology_object, result);
			
			stored_procedure.close();	
		}
		
		return result;
	}
	
	public static User isRecordLocked(Connection connection, long recordid) throws SQLException
	{
		User user = null;
		
		String sql = "select beingeditedby from tms.records where recordid = " + recordid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			user = UserManager.getUser(connection, result.getLong("beingeditedby"));
		}
		
		result.close();
		stored_procedure.close();
		
		return user;
	}
	
	public static void lockRecord(Connection connection, User user, long recordid) throws SQLException
	{								
		StoredProcedureManager.lockRecord(connection, recordid, user);		
	}
	
	public static void unlockRecord(Connection connection, long recordid) throws SQLException
	{
		StoredProcedureManager.unlockRecord(connection, recordid);
	}
	
	public static void unlockRecordsForUser(Connection connection, long userid) throws SQLException
	{		
		StoredProcedureManager.unlockRecordsForUser(connection, userid);
	}
}
