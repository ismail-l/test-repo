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
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.RecordAttribute;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordAttributeManager 
{
	public static ArrayList<TerminlogyObject> getAllRecordAttributesForRecord(Connection connection, long record_id, User user, boolean update_tracker) throws SQLException
	{
		ArrayList<TerminlogyObject> record_attributes = new ArrayList<TerminlogyObject>();
		
		String sql = "select recordattributes.recordattributeid, recordattributes.chardata, records.recordid, fields.* " +
					 "from tms.recordattributes, tms.records, tms.fields where " + 
					 "recordattributes.recordid = records.recordid " +
					 "and recordattributes.fieldid = fields.fieldid " +
					 "and records.recordid = " + record_id + " and " + 
					 "recordattributes.archivedtimestamp is NULL and records.archivedtimestamp is null order by fields.sortindex";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			RecordAttribute record_attribute = getRecordAttribute(connection, result, user, update_tracker);
			record_attributes.add(record_attribute);
		}
		
		result.close();
		stored_procedure.close();
		
		return record_attributes;
	}
	
	private static RecordAttribute getRecordAttribute(Connection connection, ResultSet result, User user, boolean update_tracker) throws SQLException
	{
		RecordAttribute record_attribute = new RecordAttribute();
		
		record_attribute.setResourceId(result.getLong("recordattributeid"));
		record_attribute.setCharData(result.getString("chardata"));
		record_attribute.setRecordId(result.getLong("recordid"));
		record_attribute.setFieldId(result.getLong("fieldid"));
		record_attribute.setFieldName(result.getString("fieldname"));
		record_attribute.setFieldTypeId(result.getInt("fieldtypeid"));
		record_attribute.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		record_attribute.setMaxlength(result.getInt("maxlength"));
		record_attribute.setDefaultValue(result.getString("defaultvalue"));
		record_attribute.setSortIndex(result.getLong("sortindex"));
		
		if (user != null && ! user.isGuest() && update_tracker)
			record_attribute.setAuditTrail(AuditTrailManager.getAuditTrailForRecordAttribute(connection, record_attribute.getResourceId()));
		
		if (update_tracker)
		{
			if (user != null && ! user.isGuest())
			{
				record_attribute.setUserAccessRight(AccessRightManager.assignAccessRight(connection, record_attribute, user.getUserId(), true));
				record_attribute.setUserCategoryAccessRight(AccessRightManager.assignAccessRight(connection, record_attribute, user.getUserCategoryId(), false));
			}
			else	
			{
				User guest = new User();
				guest.setUserCategoryId(3);
				
				record_attribute.setUserCategoryAccessRight(AccessRightManager.assignAccessRight(connection, record_attribute, guest.getUserCategoryId(), false));
			}
		}
				
		return record_attribute;
	}
}
