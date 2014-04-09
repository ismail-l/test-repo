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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.server.logging.LogUtility;
import tms2.server.sql.FilterSqlGenerator;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.RecordElement;

public class RecordIdTracker 
{			
	public static RecordIds<Long> getRecordIds(HttpSession session)
	{
		@SuppressWarnings("unchecked")
		RecordIds<Long> recordIds = (RecordIds<Long>)session.getAttribute("recordIds");

		if (recordIds == null)
		{
			recordIds = new RecordIds<Long>();
			
			session.setAttribute("recordIds", recordIds);
		}

		return recordIds;
	}
	
	public static HashMap<Long, RecordElement> getSynonymIds(HttpSession session)
	{
		@SuppressWarnings("unchecked")
		HashMap<Long, RecordElement> recordIds = (HashMap<Long, RecordElement>)session.getAttribute("synonymIds");

		if (recordIds == null)
		{
			recordIds = new HashMap<Long, RecordElement>();
			
			session.setAttribute("synonymIds", recordIds);
		}

		return recordIds;
	}	
	
	public static RecordIds<Long> refreshRecordIdsInSessionByField(HttpSession session, Connection connection, 
			Field field) throws SQLException 
	{		
		long start = Calendar.getInstance().getTimeInMillis();
		
		ArrayList<RecordElement> record_elements = SynonymManager.getSynonymRecords(connection, field);		
		
		String sql = "select records.recordid, terms.chardata from " + 
		"tms.records, tms.terms where " + 
		"terms.archivedtimestamp is null and records.archivedtimestamp is null and " + 
		"terms.recordid = records.recordid and " +
		"terms.fieldid = " + field.getFieldId();
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		long end = (Calendar.getInstance().getTimeInMillis() - start);
				
		LogUtility.log(Level.INFO, "Refresh record id's for " + field.getFieldName() + " in session " + session.getId() + " took " + end + " ms");
		
		RecordIds<Long> recordIds = RecordIdTracker.updateRecordIds(session, result, record_elements);
		
		recordIds.setCustomInputModelField(field);
		
		return recordIds;
	}
	
	public static RecordIds<Long> refreshRecordIdsInSessionByFilter(HttpSession session, Connection connection, 
				Filter filter, Field sourceField) 
			throws Exception
	{
		long start = Calendar.getInstance().getTimeInMillis();
			
		ArrayList<RecordElement> record_elements = SynonymManager.getSynonymRecords(connection, filter, sourceField);				
			
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		String sql = sql_generator.generateTermSqlStatement(filter, sourceField);
		System.out.println("=== Terms filter sql ===");
		System.out.println(sql);
			
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
												
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		long end = (Calendar.getInstance().getTimeInMillis() - start);
					
		LogUtility.log(Level.INFO, "Filter in session " + session.getId() + " took " + end + " ms");
		
		RecordIds<Long> recordIds = RecordIdTracker.updateRecordIds(session, result, record_elements);
		
		recordIds.setCustomInputModelField(sourceField);
		
		result.close();
		stored_procedure.close();
		
		return recordIds;
	}
	
	
	public static int getNumberOfRecordIdsInSession(HttpSession session)
	{
		if(session != null)
		{
			RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
			if(recordIds != null)			
				return recordIds.size();			
			else
				return 0;
		}
		else
			return 0;
	}
		
	private static RecordIds<Long> updateRecordIds(HttpSession session, ResultSet recordIdResults, ArrayList<RecordElement> record_elements) throws SQLException
	{
		RecordIds<Long> recordIds = RecordIdTracker.getRecordIds(session);
		
		recordIds.clear();
		recordIds = RecordIdTracker.populate_record_ids(session, recordIdResults, record_elements, recordIds);
				
		return recordIds;
	}
	
	private static RecordIds<Long> populate_record_ids(HttpSession session, ResultSet recordIdResults, 
			ArrayList<RecordElement> record_elements, RecordIds<Long> recordIds) throws SQLException
	{		
		// temp list for recordIds
		ArrayList<RecordElement> record_ids = new ArrayList<RecordElement>();
		
		while (recordIdResults.next())
		{
			// add the current record ids now.							
			record_ids.add(new RecordElement(recordIdResults.getLong("recordid"), 
											 recordIdResults.getLong("recordid"),
							                 recordIdResults.getString("chardata")));
		}
				
		long last_index = retrieveHighestId(record_ids);
		
		// add one to index because we want to add ids after the last id
		last_index++;
			
		SynonymManager.clear(session);
		
		// add extra ids. We dont want duplicate ids in the list.
		// These ids will be used to for the synonym record lookup.		
		for (RecordElement record_element : record_elements)
		{
			record_element.setId(last_index);
			record_ids.add(record_element);
			SynonymManager.addSynonym(session, last_index, record_element);
			
			last_index++;
		}				
		
		long start = Calendar.getInstance().getTimeInMillis();

		// sort the list. We want it in ascending order	
		record_ids = sortRecords(record_ids, true);
		
		long end = (Calendar.getInstance().getTimeInMillis() - start);
				
		LogUtility.log(Level.INFO, "Record sorting in session " + session.getId() + " took " + end + " ms");
		
		ArrayList<RecordElement> special_char_recordids = getSpecialCharacterRecordIds(record_ids);
		
		special_char_recordids = sortRecords(special_char_recordids, false);
          
		addRecordIds(recordIds, record_ids);
		addRecordIds(recordIds, special_char_recordids);
                
		return recordIds;
	}
		
	private static long retrieveHighestId (ArrayList<RecordElement> record_ids)
	{
		long highest = 0;
		
		for (RecordElement record_id : record_ids)
		{
			if (highest == 0)
				highest = record_id.getId();
			else if (highest <= record_id.getId())
				highest = record_id.getId();				
		}
		
		return highest;
	}
	
	/**
	 * Seperates the terms that only has non word characters in them
	 * @param record_ids
	 * @return A list that contains terms that only have non word characters in them.
	 */
	private static ArrayList<RecordElement> getSpecialCharacterRecordIds(ArrayList<RecordElement> record_ids)
	{
		ArrayList<RecordElement> special_char_recordids = new ArrayList<RecordElement>();
		
		Iterator<RecordElement> iter = record_ids.iterator();
		while (iter.hasNext())
		{
			RecordElement rec_elem = iter.next();
			String chardata = rec_elem.getCharData();
				
			chardata = stringReplacement(chardata, "[0-9]");
			chardata = stringReplacement(chardata, "[^\\p{L}\\p{N}]");
			
			if (chardata.isEmpty())
			{
				special_char_recordids.add(rec_elem);
				iter.remove();
			}
		}
		
		return special_char_recordids;		
	}
	
	private static RecordIds<Long> addRecordIds(RecordIds<Long> recordIds, ArrayList<RecordElement> record_elements)
	{
	       // add it to the record ids.         
        for (RecordElement record_element : record_elements)
        {
        	long record_id = record_element.getId();
        	recordIds.addRecordId(record_id);
        }
		
		return recordIds;		
	}
	
	/** Sorts the received RecordElements and returns them sorted. */
	public static ArrayList<RecordElement> sortRecords(ArrayList<RecordElement> record_ids, final boolean replace)
	{
		Collections.sort(record_ids, new Comparator<RecordElement>()
        {
        	private Collator collator = Collator.getInstance(Locale.ROOT);

			@Override
			public int compare(RecordElement r1, RecordElement r2)
			{
				String char_data_1 = r1.getCharData().toLowerCase();
				String char_data_2 = r2.getCharData().toLowerCase();
				
				if (replace)
				{
					// Remove: numbers
					char_data_1 = stringReplacement(char_data_1, "[0-9]");
					char_data_2 = stringReplacement(char_data_2, "[0-9]");
					
					// Remove: Spaces, non-word characters
					char_data_1 = stringReplacement(char_data_1, "[^\\p{L}\\p{N}]");			
					char_data_2 = stringReplacement(char_data_2, "[^\\p{L}\\p{N}]");	
				}
				
				return collator.compare(char_data_1, char_data_2);				
			}
        });
			
		return record_ids;
	}
	
	private static String stringReplacement(String string, String regex)
	{
		return string.replaceAll(regex, "");
	}
}
