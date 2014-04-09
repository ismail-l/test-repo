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

import tms2.server.accessright.AccessRightManager;
import tms2.server.field.FieldManager;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Synonym;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermAttributeManager 
{
	public static ArrayList<ChildTerminologyObject> getTermAttributesForTerm(Connection connection, Term term, User user, boolean update_tracker) throws SQLException
	{
		ArrayList<ChildTerminologyObject> term_attributes = new ArrayList<ChildTerminologyObject>();
		
		String sql = "select termattributes.termattributeid, termattributes.chardata, terms.termid, records.recordid, fields.* " +
					 "from tms.termattributes, tms.terms, tms.records, tms.fields where " +
					 "termattributes.termid = terms.termid " + 
					 "and terms.recordid = records.recordid " +
					 "and termattributes.fieldid = fields.fieldid " +
		 			 "and terms.termid = " + term.getResourceId() + "and " + 
		 			 "terms.archivedtimestamp is null and termattributes.archivedtimestamp is NULL and records.archivedtimestamp is null order by fields.sortindex";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			TermAttribute term_attribute = getTermAttribute(connection, term, result, user, update_tracker);
			term_attributes.add(term_attribute);
		}
		
		sql = "select synonyms.synonymid, synonyms.chardata, terms.termid, records.recordid, fields.* " +
		      "from tms.synonyms, tms.terms, tms.records, tms.fields where " + 
		      "synonyms.termid = terms.termid " +
		      "and terms.recordid = records.recordid " + 
		      "and synonyms.fieldid = fields.fieldid " +
		      "and terms.termid = " + term.getResourceId() + "and " + 
		      "terms.archivedtimestamp is null and synonyms.archivedtimestamp is null and records.archivedtimestamp is null order by fields.sortindex";

		stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			Synonym synonym = getSynonym(connection, term, result, user, update_tracker);
			term_attributes.add(synonym);
		}
		
		result.close();
		stored_procedure.close();
		
		return term_attributes;
	}
		
	private static TermAttribute getTermAttribute(Connection connection, Term term, ResultSet result, User user, boolean update_tracker) throws SQLException
	{
		TermAttribute term_attribute = new TermAttribute();
		
		term_attribute.setResourceId(result.getLong("termattributeid"));		
		term_attribute.setCharData(result.getString("chardata"));
		term_attribute.setRecordId(result.getLong("recordid"));
		term_attribute.setTermId(result.getLong("termid"));
		term_attribute.setFieldId(result.getLong("fieldid"));
		term_attribute.setFieldName(result.getString("fieldname"));
		term_attribute.setFieldTypeId(result.getInt("fieldtypeid"));
		term_attribute.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		term_attribute.setMaxlength(result.getInt("maxlength"));
		term_attribute.setDefaultValue(result.getString("defaultvalue"));
		term_attribute.setSortIndex(result.getLong("sortindex"));
		
		if (user != null && ! user.isGuest() && update_tracker)
			term_attribute.setAuditTrail(AuditTrailManager.getAuditTrailForTermAttribute(connection, term_attribute.getResourceId()));
		
		if (term_attribute.isPresetAttribute() || term_attribute.isPresetSubAttribute())				
			FieldManager.setPresetFields(connection, term_attribute);		
		
		if (update_tracker)
		{
			if (user != null && ! user.isGuest())
			{
				term_attribute.setUserAccessRight(AccessRightManager.assignChildAccessRight(connection, term_attribute, user.getUserId(), term.getFieldId(), true));					
				term_attribute.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, term_attribute, user.getUserCategoryId(), term.getFieldId(), false));
			}
			else
			{
				User guest = new User();
				guest.setUserCategoryId(3);
				
				term_attribute.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, term_attribute, guest.getUserCategoryId(), term.getFieldId(), false));
			}
		}
		
		return term_attribute;
	}
	
	private static Synonym getSynonym(Connection connection, Term term, ResultSet result, User user, boolean update_tracker) throws SQLException
	{
		Synonym synonym = new Synonym();
		
		synonym.setResourceId(result.getLong("synonymid"));		
		synonym.setCharData(result.getString("chardata"));
		synonym.setRecordId(result.getLong("recordid"));
		synonym.setTermId(result.getLong("termid"));
		synonym.setFieldId(result.getLong("fieldid"));
		synonym.setFieldName(result.getString("fieldname"));
		synonym.setFieldTypeId(result.getInt("fieldtypeid"));
		synonym.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		synonym.setMaxlength(result.getInt("maxlength"));
		synonym.setDefaultValue(result.getString("defaultvalue"));		
		synonym.setSortIndex(result.getLong("sortindex"));
		
		if (user != null && ! user.isGuest() && update_tracker)
			synonym.setAuditTrail(AuditTrailManager.getAuditTrailForSynonym(connection, synonym.getResourceId()));
		
		synonym.setSynonymAttributes(SynonymAttributeManager.getSynonymAttributesForSynonym(connection, term, synonym, user, update_tracker));
		
		if (update_tracker)
		{
			if (user != null && ! user.isGuest())
			{
				synonym.setUserAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym, user.getUserId(), term.getFieldId(), true));					
				synonym.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym, user.getUserCategoryId(), term.getFieldId(), false));
			}
			else
			{
				User guest = new User();
				guest.setUserCategoryId(3);
				
				synonym.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym, guest.getUserCategoryId(), term.getFieldId(), false));
			}
		}
		
		return synonym;
	}
}
