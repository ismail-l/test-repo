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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import tms2.shared.AccessRight;
import tms2.shared.ChildAccessRight;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Field;
import tms2.shared.PresetField;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.RecordAttribute;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.UserCategory;

/**
 * 
 * @author I. Lavangee
 *
 */
public class StoredProcedureManager 
{		
	public static CallableStatement genericReturnedRef(Connection connection, String sql) throws SQLException
	{		
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_generic_return_ref (?)}");
		stored_procedure.registerOutParameter(1, Types.OTHER);
		stored_procedure.setString(2, sql);
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement createUser(Connection connection, User user, String hashed_passwd) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_user (?, ?, ?, ?, ?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, user.getUsername().trim());
		stored_procedure.setString(3, hashed_passwd.trim());
		stored_procedure.setString(4, user.getFirstName());
		stored_procedure.setString(5, user.getLastName());
		stored_procedure.setBoolean(6, user.isActivated());
		stored_procedure.setDate(7, user.getExpiryDate() != null ? new Date(user.getExpiryDate().getTime()) : null);
		stored_procedure.setLong(8, user.getUserCategoryId());
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateUser(Connection connection, User user, String hashed_passwd) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_user (?, ?, ?, ?, ?, ?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, user.getUsername());		
		stored_procedure.setString(3, hashed_passwd);
		stored_procedure.setString(4, user.getFirstName());
		stored_procedure.setString(5, user.getLastName());
		stored_procedure.setBoolean(6, user.isActivated());
		stored_procedure.setDate(7, user.getExpiryDate() != null ? new Date(user.getExpiryDate().getTime()) : null);
		stored_procedure.setLong(8, user.getUserCategoryId());
		stored_procedure.setLong(9, user.getUserId());
		stored_procedure.executeUpdate();
		
		return stored_procedure;
	}
	
	public static CallableStatement createField(Connection connection, Field field, long sort_index) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_field (?, ?, ?, ?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, field.getFieldName());
		stored_procedure.setInt(3, field.getFieldTypeId());
		stored_procedure.setInt(4, field.getFieldDataTypeId());
		stored_procedure.setInt(5, field.getMaxlength());
		stored_procedure.setString(6, field.getDefaultValue());
		stored_procedure.setLong(7, sort_index);
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateField(Connection connection, Field field) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_field (?, ?, ?, ?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, field.getFieldName());
		stored_procedure.setInt(3, field.getFieldTypeId());
		stored_procedure.setInt(4, field.getFieldDataTypeId());
		stored_procedure.setInt(5, field.getMaxlength());
		stored_procedure.setString(6, field.getDefaultValue());
		stored_procedure.setLong(7, field.getFieldId());
		stored_procedure.executeUpdate();
				
		return stored_procedure;
	}
	
	public static CallableStatement createPresetField(Connection connection, PresetField field) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_presetfield (?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, field.getPresetFieldName());
		stored_procedure.setLong(3, field.getFieldId());
		stored_procedure.execute();
				
		return stored_procedure;
	}
	
	public static CallableStatement updatePresetField(Connection connection, PresetField field) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_presetfield (?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, field.getPresetFieldName());
		stored_procedure.setLong(3, field.getPresetattributeid());
		stored_procedure.executeUpdate();
				
		return stored_procedure;
	}
	
	public static CallableStatement createProject(Connection connection, Project project) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_project (?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, project.getProjectName());
		stored_procedure.setLong(3, project.getTermBaseId());		
		stored_procedure.execute();
				
		return stored_procedure;
	}
	
	public static CallableStatement updateProject(Connection connection, Project project) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_project (?, now(), ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, project.getProjectName());
		stored_procedure.setLong(3, project.getProjectId());		
		stored_procedure.executeUpdate();
				
		return stored_procedure;
	}
	
	public static CallableStatement createTermBase(Connection connection, TermBase termbase) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_termbase (?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, termbase.getTermdbname());
		stored_procedure.setLong(3, termbase.getOwneruserid());
		stored_procedure.setString(4, termbase.getEmail());
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateTermBase(Connection connection, TermBase termbase) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_termbase (?, ?, now(), ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, termbase.getTermdbname());
		stored_procedure.setLong(3, termbase.getOwneruserid());
		stored_procedure.setString(4, termbase.getEmail());
		stored_procedure.setLong(5, termbase.getTermdbid());
		stored_procedure.executeUpdate();
				
		return stored_procedure;
	}
	
	public static CallableStatement createUserCategory(Connection connection, UserCategory userCategory) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_category (?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, userCategory.getUserCategoryName());
		stored_procedure.setBoolean(3, userCategory.isAdmin());
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateUserCategory(Connection connection, UserCategory userCategory) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_update_user_category (?, ?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, userCategory.getUserCategoryName());
		stored_procedure.setBoolean(3, userCategory.isAdmin());
		stored_procedure.setLong(4, userCategory.getUserCategoryId());
		stored_procedure.executeUpdate();	
		
		return stored_procedure;
	}
		
	public static void updateLastSignOn(Connection connection, long user_id) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{call tms.sp_update_user_last_signon (?, now())}");
		stored_procedure.setLong(1, user_id);
		stored_procedure.executeUpdate();
		
		stored_procedure.close();
	}
	
	public static CallableStatement createAccessRight(Connection connection, AccessRight access_right, long consumer_id, boolean is_user_access_right) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_accessright (?, ?, ?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_category_accessright (?, ?, ?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setBoolean(2, access_right.mayRead());
		stored_procedure.setBoolean(3, access_right.mayUpdate());
		stored_procedure.setBoolean(4, access_right.mayExport());
		stored_procedure.setBoolean(5, access_right.mayDelete());
		stored_procedure.setLong(6, access_right.getFieldId());
		stored_procedure.setLong(7, consumer_id);
		stored_procedure.executeUpdate();
						
		return stored_procedure;
	}
	
	public static CallableStatement updateAccessRight(Connection connection, AccessRight access_right, long consumer_id, boolean is_user_access_right) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_user_accessright (?, ?, ?, ?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_user_category_accessright (?, ?, ?, ?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setBoolean(2, access_right.mayRead());
		stored_procedure.setBoolean(3, access_right.mayUpdate());
		stored_procedure.setBoolean(4, access_right.mayExport());
		stored_procedure.setBoolean(5, access_right.mayDelete());
		stored_procedure.setLong(6, access_right.getFieldId());
		stored_procedure.setLong(7, consumer_id);
		stored_procedure.setLong(8, access_right.getRightsId());
		stored_procedure.executeUpdate();
						
		return stored_procedure;
	}
	
	public static CallableStatement createChildAccessRight(Connection connection, ChildAccessRight access_right, boolean is_user_access_right) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_childaccessright (?, ?, ?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_usercategory_childaccessright (?, ?, ?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setBoolean(2, access_right.mayRead());
		stored_procedure.setBoolean(3, access_right.mayUpdate());
		stored_procedure.setBoolean(4, access_right.mayExport());
		stored_procedure.setBoolean(5, access_right.mayDelete());
		stored_procedure.setLong(6, access_right.getParentId());
		stored_procedure.setLong(7, access_right.getFieldId());
		stored_procedure.executeUpdate();
						
		return stored_procedure;
	}
	
	public static CallableStatement updateChildAccessRight(Connection connection, ChildAccessRight access_right, boolean is_user_access_right) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_user_childaccessright (?, ?, ?, ?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_usercategory_childaccessright (?, ?, ?, ?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setBoolean(2, access_right.mayRead());
		stored_procedure.setBoolean(3, access_right.mayUpdate());
		stored_procedure.setBoolean(4, access_right.mayExport());
		stored_procedure.setBoolean(5, access_right.mayDelete());
		stored_procedure.setLong(6, access_right.getParentId());
		stored_procedure.setLong(7, access_right.getFieldId());
		stored_procedure.setLong(8, access_right.getRightsId());
		stored_procedure.executeUpdate();
						
		return stored_procedure;
	}
	
	public static CallableStatement createUserProject(Connection connection, boolean is_user_access_right, long consumer_id, long project_id) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_project (?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_user_category_project (?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setLong(2, project_id);
		stored_procedure.setLong(3, consumer_id);
		stored_procedure.executeUpdate();
						
		return stored_procedure;
	}
	
	public static void removePreviousUserProjects(Connection connection, boolean is_user_access_right, long consumer_id) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_user_access_right)
			stored_procedure = connection.prepareCall("{call tms.sp_delete_user_project (?)}");
		else
			stored_procedure = connection.prepareCall("{call tms.sp_delete_user_category_project (?)}");
				
		stored_procedure.setLong(1, consumer_id);
		stored_procedure.executeUpdate();
		
		stored_procedure.close();
	}
	
	public static CallableStatement createRecord(Connection connection, Record record) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_record (now(), ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setLong(2, record.getTermdbId());		
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement archiveRecord(Connection connection, Record record) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_archive_record (now(), ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setLong(2, record.getRecordId());		
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static void lockRecord(Connection connection, long recordid, User user) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_lock_record (?, ?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		
		stored_procedure.setLong(2, user.getUserId());			
		stored_procedure.setLong(3, recordid);	
		stored_procedure.execute();
		
		stored_procedure.close();
	}
		
	public static void unlockRecord(Connection connection, long recordid) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_unlock_record (?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
						
		stored_procedure.setLong(2, recordid);	
		stored_procedure.execute();
		
		stored_procedure.close();
	}
	
	public static void unlockRecordsForUser(Connection connection, long userid) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_unlock_all_records (?)}");
		stored_procedure.registerOutParameter(1, Types.BIGINT);
						
		stored_procedure.setLong(2, userid);	
		stored_procedure.execute();
		
		stored_procedure.close();
	}
	
	public static CallableStatement updateRecordAuditTrail(Connection connection, long recordid, long user_id, boolean is_updating) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = connection.prepareCall("{? = call tms.sp_edit_record_audit (?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_record_audit (?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2,"");		
		stored_procedure.setBoolean(3, true);
		stored_procedure.setLong(4, user_id);
		stored_procedure.setLong(5, recordid);
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateTerminologyObjectAuditTrail(Connection connection, TerminlogyObject terminology_object, long resourceid, long user_id) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object.getResourceId() > -1)
		{
			if (terminology_object instanceof RecordAttribute)
				stored_procedure = connection.prepareCall("{? = call tms.sp_edit_record_attribute_audit (?, ?, ?, ?)}");
			else if (terminology_object instanceof Term)
				stored_procedure = connection.prepareCall("{? = call tms.sp_edit_term_audit (?, ?, ?, ?)}");
			else if (terminology_object instanceof TermAttribute)
			{
				if (terminology_object instanceof Synonym)
					stored_procedure = connection.prepareCall("{? = call tms.sp_edit_synonym_audit (?, ?, ?, ?)}");
				else if (terminology_object instanceof SynonymAttribute)
					stored_procedure = connection.prepareCall("{? = call tms.sp_edit_synonym__attribute_audit (?, ?, ?, ?)}");
				else
					stored_procedure = connection.prepareCall("{? = call tms.sp_edit_term_attribute_audit (?, ?, ?, ?)}");
			}
		}
		else
		{
			if (terminology_object instanceof RecordAttribute)
				stored_procedure = connection.prepareCall("{? = call tms.sp_add_record_attribute_audit (?, ?, ?, ?)}");
			else if (terminology_object instanceof Term)
				stored_procedure = connection.prepareCall("{? = call tms.sp_add_term_audit (?, ?, ?, ?)}");
			else if (terminology_object instanceof TermAttribute)
			{
				if (terminology_object instanceof Synonym)
					stored_procedure = connection.prepareCall("{? = call tms.sp_add_synonym_audit (?, ?, ?, ?)}");
				else if (terminology_object instanceof SynonymAttribute)
					stored_procedure = connection.prepareCall("{? = call tms.sp_add_synonym__attribute_audit (?, ?, ?, ?)}");
				else
					stored_procedure = connection.prepareCall("{? = call tms.sp_add_term_attribute_audit (?, ?, ?, ?)}");
			}
		}
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, terminology_object.getCharData());		
		stored_procedure.setBoolean(3, terminology_object.hasBeenEdited());
		stored_procedure.setLong(4, user_id);
		stored_procedure.setLong(5, resourceid);
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static void removePreviousRecordProjects(Connection connection, long recordid) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{call tms.sp_delete_record_project (?)}");
				
		stored_procedure.setLong(1, recordid);
		stored_procedure.executeUpdate();
		
		stored_procedure.close();
	}
	
	public static CallableStatement addRecordProjects(Connection connection, long recordid, long projectid) throws SQLException
	{
		CallableStatement stored_procedure = connection.prepareCall("{? = call tms.sp_add_record_project (?, ?)}");
				
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setLong(2, recordid);
		stored_procedure.setLong(3, projectid);
		stored_procedure.executeUpdate();
		
		return stored_procedure;
	}
	
	public static CallableStatement addTerminologyObject(Connection connection, TerminlogyObject terminology_object, long record_id) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object instanceof RecordAttribute)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_record_attribute (?, ?, ?)}");
		else if (terminology_object instanceof Term)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_term (?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, terminology_object.getCharData());
		stored_procedure.setLong(3, record_id);
		stored_procedure.setLong(4, terminology_object.getFieldId());
		stored_procedure.executeUpdate();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateTerminologyObject(Connection connection, TerminlogyObject terminology_object) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object instanceof RecordAttribute)			
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_record_attribute (?, ?, ?, ?)}");
		else if (terminology_object instanceof Term)
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_term (?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, terminology_object.getCharData());
		stored_procedure.setLong(3, terminology_object.getRecordId());		
		stored_procedure.setLong(4, terminology_object.getFieldId());	
		stored_procedure.setLong(5, terminology_object.getResourceId());
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static CallableStatement addChildTerminologyObject(Connection connection, TerminlogyObject terminology_object, long parent_id) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object instanceof Synonym)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_synonym (?, ?, ?)}");
		else if (terminology_object instanceof SynonymAttribute)
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_synonym_attribute (?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_add_term_attribute (?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, terminology_object.getCharData());
		stored_procedure.setLong(3, parent_id);
		stored_procedure.setLong(4, terminology_object.getFieldId());
		stored_procedure.executeUpdate();
		
		return stored_procedure;
	}
	
	public static CallableStatement updateChildTerminologyObject(Connection connection, ChildTerminologyObject terminology_object) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object instanceof Synonym)			
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_synonym (?, ?, ?, ?)}");
		else if (terminology_object instanceof SynonymAttribute)
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_synonym_attribute (?, ?, ?, ?)}");
		else
			stored_procedure = connection.prepareCall("{? = call tms.sp_update_term_attribute (?, ?, ?, ?)}");
		
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setString(2, terminology_object.getCharData());
		stored_procedure.setLong(3, terminology_object.getParentId());		
		stored_procedure.setLong(4, terminology_object.getFieldId());	
		stored_procedure.setLong(5, terminology_object.getResourceId());
		stored_procedure.execute();
		
		return stored_procedure;
	}
	
	public static void archiveTerminologyObject(Connection connection, TerminlogyObject terminology_object) throws SQLException
	{
		CallableStatement stored_procedure = null;
		
		if (terminology_object instanceof RecordAttribute)
			stored_procedure = connection.prepareCall("{? = call tms.sp_archive_recordattribute (now(), ?)}");
		else if (terminology_object instanceof Term)
			stored_procedure = connection.prepareCall("{? = call tms.sp_archive_term (now(), ?)}");
		else if (terminology_object instanceof TermAttribute)
		{
			if (terminology_object instanceof Synonym)
				stored_procedure = connection.prepareCall("{? = call tms.sp_archive_synonym (now(), ?)}");
			else if (terminology_object instanceof SynonymAttribute)
				stored_procedure = connection.prepareCall("{? = call tms.sp_archive_synonymattribute (now(), ?)}");
			else
				stored_procedure = connection.prepareCall("{? = call tms.sp_archive_termattribute (now(), ?)}");
		}
		stored_procedure.registerOutParameter(1, Types.BIGINT);
		stored_procedure.setLong(2, terminology_object.getResourceId());
		stored_procedure.execute();
		
		stored_procedure.close();	
	}
}
