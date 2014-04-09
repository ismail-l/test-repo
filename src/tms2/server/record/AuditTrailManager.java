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

import tms2.server.sql.StoredProcedureManager;
import tms2.shared.AuditableEvent;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AuditTrailManager 
{	
	public static ArrayList<AuditableEvent> getAuditTrailForRecord(Connection connection, long record_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, record_id, AuditableEvent.RESOURCE_TYPE_RECORD,
																				AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, record_id, AuditableEvent.RESOURCE_TYPE_RECORD,
																		     AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	public static ArrayList<AuditableEvent> getAuditTrailForRecordAttribute(Connection connection, long record_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, record_id, AuditableEvent.RESOURCE_TYPE_RECORDFIELD, 
																				AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, record_id, AuditableEvent.RESOURCE_TYPE_RECORDFIELD, 
																			 AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	public static ArrayList<AuditableEvent> getAuditTrailForTerm(Connection connection, long term_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, term_id, AuditableEvent.RESOURCE_TYPE_INDEXFIELD,
																				AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, term_id, AuditableEvent.RESOURCE_TYPE_INDEXFIELD,
				 															 AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	public static ArrayList<AuditableEvent> getAuditTrailForTermAttribute(Connection connection, long termattribute_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, termattribute_id, AuditableEvent.RESOURCE_TYPE_ATTRIBUTEFIELD,
																			    AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, termattribute_id, AuditableEvent.RESOURCE_TYPE_ATTRIBUTEFIELD,
																			 AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	public static ArrayList<AuditableEvent> getAuditTrailForSynonym(Connection connection, long synonym_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, synonym_id, AuditableEvent.RESOURCE_TYPE_SYNONYMFIELD,
																				AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, synonym_id, AuditableEvent.RESOURCE_TYPE_SYNONYMFIELD,
																			 AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	public static ArrayList<AuditableEvent> getAuditTrailForSynonymAttribute(Connection connection, long synonymattribute_id) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		ArrayList<AuditableEvent> created_rec_audits = getAuditsForResourceType(connection, synonymattribute_id, AuditableEvent.RESOURCE_TYPE_SYNONYMATTRIBUTEFIELD,
																				AuditableEvent.EVENT_CREATE);
		
		ArrayList<AuditableEvent> edit_rec_audits = getAuditsForResourceType(connection, synonymattribute_id, AuditableEvent.RESOURCE_TYPE_SYNONYMATTRIBUTEFIELD,
																		     AuditableEvent.EVENT_UPDATE);
		
		audits.addAll(created_rec_audits);
		audits.addAll(edit_rec_audits);
		
		return audits;
	}
	
	private static ArrayList<AuditableEvent> getAuditsForResourceType(Connection connection, long resource_id, int resourcetype, int eventtype) throws SQLException
	{
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		String sql = null;
		String event_id = null;
		String resourceid = null;;			
		
		switch (resourcetype)
		{
			case AuditableEvent.RESOURCE_TYPE_RECORD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailcreaterecords.*, users.firstname, users.lastname from tms.audittrailcreaterecords, tms.users where " + 
				          "recordid = " + resource_id + " and audittrailcreaterecords.userid = users.userid ";
					
					event_id = "audittrailcreaterecordid";
					resourceid = "recordid";
				}
				else
				{
					sql = "select audittraileditrecords.*, users.firstname, users.lastname from tms.audittraileditrecords, tms.users where " + 
			              "recordid = " + resource_id + " and audittraileditrecords.userid = users.userid ";
					
					event_id = "audittraileditrecordid";
					resourceid = "recordid";
				}
				
				break;
			}
			case AuditableEvent.RESOURCE_TYPE_RECORDFIELD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailtcreaterecordattributes.*, users.firstname, users.lastname from tms.audittrailtcreaterecordattributes, tms.users where " + 
				          "recordattributeid = " + resource_id + " and audittrailtcreaterecordattributes.userid = users.userid ";
					
					event_id = "audittrailcreaterecordattrid";
					resourceid = "recordattributeid";
				}
				else
				{
					sql = "select audittraileditrecordattributes.*, users.firstname, users.lastname from tms.audittraileditrecordattributes, tms.users where " + 
			              "recordattributeid = " + resource_id + " and audittraileditrecordattributes.userid = users.userid ";
					
					event_id = "audittraileditrecattrid";
					resourceid = "recordattributeid";
				}
				
				break;
			}
			case AuditableEvent.RESOURCE_TYPE_INDEXFIELD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailcreateterms.*, users.firstname, users.lastname from tms.audittrailcreateterms, tms.users where " + 
				          "termid = " + resource_id + " and audittrailcreateterms.userid = users.userid ";
					
					event_id = "audittrailcreatetermid";
					resourceid = "termid";
				}
				else
				{
					sql = "select audittraileditterms.*, users.firstname, users.lastname from tms.audittraileditterms, tms.users where " + 
			              "termid = " + resource_id + " and audittraileditterms.userid = users.userid ";
					
					event_id = "audittrailedittermid";
					resourceid = "termid";
				}
				
				break;
			}
			case AuditableEvent.RESOURCE_TYPE_ATTRIBUTEFIELD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailcreatetermattributes.*, users.firstname, users.lastname from tms.audittrailcreatetermattributes, tms.users where " + 
				          "termattributeid = " + resource_id + " and audittrailcreatetermattributes.userid = users.userid ";
					
					event_id = "audittrailcreatetermattrid";
					resourceid = "termattributeid";
				}
				else
				{
					sql = "select audittrailedittermattributes.*, users.firstname, users.lastname from tms.audittrailedittermattributes, tms.users where " + 
			              "termattributeid = " + resource_id + " and audittrailedittermattributes.userid = users.userid ";
					
					event_id = "audittrailedittermattrid";
					resourceid = "termattributeid";
				}
				
				break;
			}
			case AuditableEvent.RESOURCE_TYPE_SYNONYMFIELD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailcreatesynonyms.*, users.firstname, users.lastname from tms.audittrailcreatesynonyms, tms.users where " + 
				          "synonymid = " + resource_id + " and audittrailcreatesynonyms.userid = users.userid ";
					
					event_id = "audittrailcreatesynonymid";
					resourceid = "synonymid";
				}
				else
				{
					sql = "select audittraileditsynonyms.*, users.firstname, users.lastname from tms.audittraileditsynonyms, tms.users where " + 
			              "synonymid = " + resource_id + " and audittraileditsynonyms.userid = users.userid ";
					
					event_id = "audittraileditsynonymid";
					resourceid = "synonymid";
				}
				
				break;
			}
			case AuditableEvent.RESOURCE_TYPE_SYNONYMATTRIBUTEFIELD:
			{
				if (eventtype == AuditableEvent.EVENT_CREATE)
				{
					sql = "select audittrailcreatesynonymattributes.*, users.firstname, users.lastname from tms.audittrailcreatesynonymattributes, tms.users where " + 
				          "synonymattributeid = " + resource_id + " and audittrailcreatesynonymattributes.userid = users.userid ";
					
					event_id = "audittrailcreatesynonymattrid";
					resourceid = "synonymattributeid";
				}
				else
				{
					sql = "select audittraileditsynonymattributes.*, users.firstname, users.lastname from tms.audittraileditsynonymattributes, tms.users where " + 
			              "synonymattributeid = " + resource_id + " and audittraileditsynonymattributes.userid = users.userid " ;
					
					event_id = "audittraileditsynonymattrid";
					resourceid = "synonymattributeid";
				}
				
				break;
			}				
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			AuditableEvent audit = getAudit(connection, result, event_id, resourceid, resourcetype, eventtype);
			audits.add(audit);
		}
		
		result.close();
		stored_procedure.close();
		
		return audits;
	}
		
	private static AuditableEvent getAudit(Connection connection, ResultSet result, String eventid, String resourceid, int resourcetype, int eventtype) throws SQLException
	{
		AuditableEvent audit = new AuditableEvent();
		
		audit = new AuditableEvent();
		audit.setEventId(result.getLong(eventid));
		audit.setTimestamp(result.getDate("auditdatetime"));
		audit.setCharData(result.getString("chardata"));
		audit.setCanBeRendered(result.getBoolean("canberendered"));
		audit.setUserId(result.getLong("userid"));
		audit.setResourceId(result.getLong(resourceid));
		audit.setEventType(eventtype);			
		audit.setResourceType(resourcetype);
		audit.setFullUserName(result.getString("firstname") + " " + result.getString("lastname"));
						
		return audit;
	}
	
	public static void updateRecordAuditTrail(Connection connection, User user, long recordid, boolean is_updating) throws SQLException
	{				
		CallableStatement stored_procedure = StoredProcedureManager.updateRecordAuditTrail(connection, recordid, user.getUserId(), is_updating);		
		stored_procedure.close();
	}
	
	public static void updateTerminlogyObjectAuditTrail(Connection connection, User user, TerminlogyObject terminology_object, long resource_id) throws SQLException
	{				
		CallableStatement stored_procedure = StoredProcedureManager.updateTerminologyObjectAuditTrail(connection, terminology_object, resource_id, user.getUserId());
		stored_procedure.close();
	}
}
