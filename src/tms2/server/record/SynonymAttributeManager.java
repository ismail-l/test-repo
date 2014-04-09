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
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SynonymAttributeManager 
{
	public static ArrayList<ChildTerminologyObject> getSynonymAttributesForSynonym(Connection connection, Term term, Synonym synonym, User user, boolean update_tracker) throws SQLException
	{
		ArrayList<ChildTerminologyObject> synonym_attributes = new ArrayList<ChildTerminologyObject>();
		
		String sql = "select synonymattributes.synonymattributeid, synonymattributes.chardata, synonyms.synonymid, records.recordid, fields.* " +
					 "from tms.synonymattributes, tms.synonyms, tms.terms, tms.records, tms.fields where " + 
					 "synonymattributes.synonymid = synonyms.synonymid " +
					 "and synonyms.termid = terms.termid " +
					 "and terms.recordid = records.recordid " +
					 "and synonymattributes.fieldid = fields.fieldid " +
		             "and synonyms.synonymid = " + synonym.getResourceId() + " and " + 
		             "synonymattributes.archivedtimestamp is null and synonyms.archivedtimestamp is null and " +
		             "terms.archivedtimestamp is null and records.archivedtimestamp is null order by fields.sortindex";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			SynonymAttribute synonym_attribute = getSynonymAttribute(connection, term, result, user, update_tracker);
			synonym_attributes.add(synonym_attribute);
		}
		
		result.close();
		stored_procedure.close();
		
		return synonym_attributes;
		
	}
	
	private static SynonymAttribute getSynonymAttribute(Connection connection, Term term, ResultSet result, User user, boolean update_tracker) throws SQLException
	{
		SynonymAttribute synonym_attribute = new SynonymAttribute();;
				
		synonym_attribute.setResourceId(result.getLong("synonymattributeid"));
		synonym_attribute.setCharData(result.getString("chardata"));		
		synonym_attribute.setRecordId(result.getLong("recordid"));
		synonym_attribute.setSynonymId(result.getLong("synonymid"));
		synonym_attribute.setFieldId(result.getLong("fieldid"));
		synonym_attribute.setFieldName(result.getString("fieldname"));
		synonym_attribute.setFieldTypeId(result.getInt("fieldtypeid"));
		synonym_attribute.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		synonym_attribute.setMaxlength(result.getInt("maxlength"));
		synonym_attribute.setDefaultValue(result.getString("defaultvalue"));
		synonym_attribute.setSortIndex(result.getLong("sortindex"));
		
		if (user != null && ! user.isGuest() && update_tracker)
			synonym_attribute.setAuditTrail(AuditTrailManager.getAuditTrailForSynonymAttribute(connection, synonym_attribute.getResourceId()));
		
		if (update_tracker)
		{
			if (user != null && ! user.isGuest())
			{
				synonym_attribute.setUserAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym_attribute, user.getUserId(), term.getFieldId(), true));
				synonym_attribute.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym_attribute, user.getUserCategoryId(), term.getFieldId(), false));
			}
			else
			{
				User guest = new User();
				guest.setUserCategoryId(3);
				
				synonym_attribute.setUserCategoryAccessRight(AccessRightManager.assignChildAccessRight(connection, synonym_attribute, guest.getUserCategoryId(), term.getFieldId(), false));
			}
		}
		
		if (synonym_attribute.isPresetAttribute() || synonym_attribute.isPresetSubAttribute())
			FieldManager.setPresetFields(connection, synonym_attribute);
				
		return synonym_attribute;
	}
}
