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

package tms2.server.accessright;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import tms2.server.field.FieldManager;
import tms2.server.project.ProjectManager;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.AccessRight;
import tms2.shared.ChildAccessRight;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.RecordAttribute;
import tms2.shared.Synonym;
import tms2.shared.TerminlogyObject;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Term;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AccessRightManager 
{
	public static AccessRight createAccessRight(Connection connection, AccessRight access_right, long consumer_id, boolean is_user_access_right) throws SQLException
	{
		AccessRight created = null;
		
		CallableStatement stored_procedure = StoredProcedureManager.createAccessRight(connection, access_right, consumer_id, is_user_access_right);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			created = getAccessRightForConsumer(connection, result, is_user_access_right);		
		
		stored_procedure.close();
					
		return created;		
	}
	
	public static AccessRight updateAccessRight(Connection connection, AccessRight access_right, long consumer_id, boolean is_user_access_right) throws SQLException
	{
		AccessRight updated = null;
		
		CallableStatement stored_procedure = StoredProcedureManager.updateAccessRight(connection, access_right, consumer_id, is_user_access_right);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			updated = getAccessRightForConsumer(connection, result, is_user_access_right);		
		
		stored_procedure.close();
				
		return updated;		
	} 
	
	public static ChildAccessRight createChildAccessRight(Connection connection, ChildAccessRight access_right, boolean is_user_access_right) throws SQLException
	{
		ChildAccessRight created = null;
		
		CallableStatement stored_procedure = StoredProcedureManager.createChildAccessRight(connection, access_right, is_user_access_right);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			created = getChildAccessRightForConsumer(connection, result, is_user_access_right);		
		
		stored_procedure.close();
					
		return created;		
	}
	
	public static ChildAccessRight updateChildAccessRight(Connection connection, ChildAccessRight access_right, boolean is_user_access_right) throws SQLException
	{
		ChildAccessRight updated = null;
		
		CallableStatement stored_procedure = StoredProcedureManager.updateChildAccessRight(connection, access_right, is_user_access_right);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			updated = getChildAccessRightForConsumer(connection, result, is_user_access_right);		
		
		stored_procedure.close();
				
		return updated;		
	} 
	
	private static AccessRight getAccessRightForConsumer(Connection connection, long accessright_id, boolean is_user_access_right) throws SQLException
	{
		AccessRight access_right = null;
		String sql = null;
		
		if (is_user_access_right)
			sql = "select * from tms.accessrightsuser where accessrightsuser.accessrightuserid = " + accessright_id;
		else
			sql = "select * from tms.accessrightsusercategory where accessrightsusercategory.accessrightusercategoryid = " + accessright_id;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		while (results.next())
		{
			access_right = new AccessRight();
			access_right.setRightsId(accessright_id);
			access_right.setMayRead(results.getBoolean("mayread"));
			access_right.setMayUpdate(results.getBoolean("mayupdate"));
			access_right.setMayDelete(results.getBoolean("maydelete"));
			access_right.setMayExport(results.getBoolean("mayexport"));
			access_right.setFieldId(results.getLong("fieldid"));			
		}
						
		results.close();
		stored_procedure.close();
		
		return access_right;
	}
	
	private static ChildAccessRight getChildAccessRightForConsumer(Connection connection, long accessright_id, boolean is_user_access_right) throws SQLException
	{
		ChildAccessRight access_right = null;
		String sql = null;
		
		if (is_user_access_right)
			sql = "select * from tms.childaccessrightsuser where childaccessrightsuser.childaccessrightuserid = " + accessright_id;
		else
			sql = "select * from tms.childaccessrightsusercategory where childaccessrightsusercategory.childaccessrightusercategoryid = " + accessright_id;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		while (results.next())
		{
			access_right = new ChildAccessRight();
			access_right.setRightsId(accessright_id);
			access_right.setMayRead(results.getBoolean("mayread"));
			access_right.setMayUpdate(results.getBoolean("mayupdate"));
			access_right.setMayDelete(results.getBoolean("maydelete"));
			access_right.setMayExport(results.getBoolean("mayexport"));
			access_right.setFieldId(results.getLong("fieldid"));
			
			if (is_user_access_right)
				access_right.setParentId(results.getLong("accessrightsuserid"));
			else
				access_right.setParentId(results.getLong("accessrightsusecategoryid"));
		}
						
		results.close();
		stored_procedure.close();
		
		return access_right;
	}	
	
	public static InputModel getAccessRights(Connection connection, long consumer_id, boolean is_user_access_right) throws Exception
	{	
		InputModel inputmodel = FieldManager.createInputModel(connection);		
		
		if (inputmodel == null)
			return null;
		
		Iterator<TerminlogyObject> iter = inputmodel.getRecordAttributes().iterator();
		while(iter.hasNext())
		{
			TerminlogyObject record_attribute = iter.next();
			
			if (record_attribute instanceof RecordAttribute)
				assignAccessRight(connection, record_attribute, consumer_id, is_user_access_right);
		}
		
		iter = inputmodel.getTerms().iterator();
		while (iter.hasNext())
		{
			TerminlogyObject term = iter.next();
			
			if (term instanceof Term)
			{
				assignAccessRight(connection, term, consumer_id, is_user_access_right);
				
				ArrayList<ChildTerminologyObject> term_attributes = inputmodel.getTermAttributesForTerm((Term) term);
				
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					ChildTerminologyObject term_attribute = term_attr_iter.next();
					
					assignChildAccessRight(connection, term_attribute, consumer_id, term.getFieldId(), is_user_access_right);
					
					if (term_attribute instanceof Synonym)
					{
						ArrayList<ChildTerminologyObject> synonym_attributes = inputmodel.getSynonymAttributesForTerm((Term) term);
						
						Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
						while (synonym_attr_iter.hasNext())
						{
							ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
							
							assignChildAccessRight(connection, synonym_attribute, consumer_id, term.getFieldId(), is_user_access_right);
						}							
					}
				}
			}
		}
		
		return inputmodel;		
	}
	
	public static AccessRight assignAccessRight(Connection connection, TerminlogyObject attribute, long consumer_id, boolean is_user_access_right) throws SQLException 
	{		
		AccessRight access_right = null;
		
		String sql = null;	
					
		if (is_user_access_right)
		{
			sql = "select * from tms.accessrightsuser where userid = " + consumer_id + " " +
				  "and fieldid = " + attribute.getFieldId() + 
				  " order by accessrightsuser.fieldid";
		}
		else
		{
			sql = "select * from tms.accessrightsusercategory where usercategoryid = " + consumer_id + " " +
				  "and fieldid = " + attribute.getFieldId() + 
				  " order by accessrightsusercategory.fieldid";
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
						
		if (result.next())
		{
			access_right = new AccessRight();
							
			if (is_user_access_right)
				access_right.setRightsId(result.getLong("accessrightuserid"));
			else
				access_right.setRightsId(result.getLong("accessrightusercategoryid"));
										
			access_right.setMayRead(result.getBoolean("mayread"));
			access_right.setMayUpdate(result.getBoolean("mayupdate"));
			access_right.setMayExport(result.getBoolean("mayexport"));
			access_right.setMayDelete(result.getBoolean("maydelete"));
			access_right.setFieldId(attribute.getFieldId());				
			access_right.setFieldName(attribute.getFieldName());
			access_right.setFieldTypeId(attribute.getFieldTypeId());			
			
			if (is_user_access_right)
				attribute.setUserAccessRight(access_right);
			else
				attribute.setUserCategoryAccessRight(access_right);
		}
		else
		{
			access_right = new AccessRight();
			
			access_right.setFieldId(attribute.getFieldId());	
			access_right.setFieldName(attribute.getFieldName());
			access_right.setFieldTypeId(attribute.getFieldTypeId());	
			
			if (is_user_access_right)
				attribute.setUserAccessRight(access_right);
			else
				attribute.setUserCategoryAccessRight(access_right);
		}
		
		result.close();
		stored_procedure.close();
		
		return access_right;
	}	
		
	public static ChildAccessRight assignChildAccessRight(Connection connection, ChildTerminologyObject child_attribute, long consumer_id, long parent_fieldid, boolean is_user_access_right) throws SQLException 
	{		
		ChildAccessRight access_right = null;
		
		String sql = null;		
					
		if (is_user_access_right)
		{
			sql = "select childaccessrightsuser.* from tms.accessrightsuser, tms.childaccessrightsuser where userid = " + consumer_id + " " +
				  "and accessrightsuser.fieldid = " + parent_fieldid + " " +
				  "and childaccessrightsuser.fieldid = " + child_attribute.getFieldId() + 
				  " and accessrightsuser.accessrightuserid = childaccessrightsuser.accessrightsuserid" +
				  " order by childaccessrightsuser.fieldid";
		}
		else
		{
			sql = "select childaccessrightsusercategory.* from tms.accessrightsusercategory, tms.childaccessrightsusercategory where usercategoryid = " + consumer_id + " " +
				  "and accessrightsusercategory.fieldid = " + parent_fieldid + " " +
				  "and childaccessrightsusercategory.fieldid = " + child_attribute.getFieldId() + 
				  " and childaccessrightsusercategory.accessrightsusecategoryid = accessrightsusercategory.accessrightusercategoryid" + 
				  " order by childaccessrightsusercategory.fieldid";
		}
				
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
						
		if (result.next())
		{
			access_right = new ChildAccessRight();
			
			if (is_user_access_right)
			{
				access_right.setRightsId(result.getLong("childaccessrightuserid"));
				access_right.setParentId(result.getLong("accessrightsuserid"));
			}
			else
			{
				access_right.setRightsId(result.getLong("childaccessrightusercategoryid"));
				access_right.setParentId(result.getLong("accessrightsusecategoryid"));
			}
						
			access_right.setMayRead(result.getBoolean("mayread"));
			access_right.setMayUpdate(result.getBoolean("mayupdate"));
			access_right.setMayExport(result.getBoolean("mayexport"));
			access_right.setMayDelete(result.getBoolean("maydelete"));
			access_right.setFieldId(child_attribute.getFieldId());				
			access_right.setFieldName(child_attribute.getFieldName());
			access_right.setFieldTypeId(child_attribute.getFieldTypeId());			
			
			if (is_user_access_right)
				child_attribute.setUserAccessRight(access_right);
			else
				child_attribute.setUserCategoryAccessRight(access_right);
		}
		else
		{
			access_right = new ChildAccessRight();
			
			access_right.setFieldId(child_attribute.getFieldId());	
			access_right.setFieldName(child_attribute.getFieldName());
			access_right.setFieldTypeId(child_attribute.getFieldTypeId());
						
			if (is_user_access_right)
				child_attribute.setUserAccessRight(access_right);
			else
				child_attribute.setUserCategoryAccessRight(access_right);
		}
		
		result.close();
		stored_procedure.close();
		
		return access_right;
	}
	
	public static ArrayList<Project> getUserAssignedProjects(Connection connection, long consumer_id, boolean is_user_access_right) throws Exception
	{
		ArrayList<Project> user_assigned_projects = new ArrayList<Project>();
		String sql = null;
		
		if (is_user_access_right)
			sql = "select * from tms.userprojects where " + 
				  "userid = " + consumer_id;
		else
			sql = "select * from tms.usercategoryprojects where " + 
			      "usercategoryid = " + consumer_id;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			Project project = ProjectManager.getProjectByProjectId(connection, results.getLong("projectid"), null);					
			user_assigned_projects.add(project);
		}
		
		results.close();
		stored_procedure.close();
		
		return user_assigned_projects;
	}
		
	public static void assignProjects(Connection connection, ArrayList<Project> projects, boolean is_user_access_right, long consumer_id) throws SQLException
	{		
		StoredProcedureManager.removePreviousUserProjects(connection, is_user_access_right, consumer_id);
		
		if (projects != null && projects.size() > 0)
		{				
			Iterator<Project> iter = projects.iterator();
			while (iter.hasNext())
			{
				Project project = iter.next();
				
				CallableStatement stored_procedure = StoredProcedureManager.createUserProject(connection, is_user_access_right, consumer_id, project.getProjectId());
				stored_procedure.close();				
			}
		}				
	}	
	
	/**
	 * Combines an InputModel which contains access rights for a User Category and an
	 * InputModel which contains access rights for a user into one common InputModel with access
	 * right for both entities.
	 * @param user_inputmodel
	 * @param user_cat_inputmodel
	 * @return A combined InputModel with access rights for both entities.
	 */
	public static InputModel mergeInputModels(InputModel user_inputmodel, InputModel user_cat_inputmodel)
	{
		if (user_cat_inputmodel == null && user_inputmodel == null)
			return null;
		
		for (int i = 0; i < user_cat_inputmodel.getRecordAttributes().size(); i++)
		{
			TerminlogyObject user_cat_record_attribute = user_cat_inputmodel.getRecordAttributes().get(i);
			TerminlogyObject user_record_attribute =  user_inputmodel.getRecordAttributes().get(i);
						
			AccessRight user_cat_accessright = user_cat_record_attribute.getUserCategoryAccessRight();
			AccessRight user_access_right = user_record_attribute.getUserAccessRight();
			
			if (! user_cat_accessright.mayUpdate())
				user_cat_accessright.setMayUpdate(user_access_right.mayUpdate());
				
			if (! user_cat_accessright.mayDelete())
				user_cat_accessright.setMayDelete(user_access_right.mayDelete());
			
			if (! user_cat_accessright.mayExport())
				user_cat_accessright.setMayExport(user_access_right.mayExport());
		}
				
		for (int i = 0; i < user_cat_inputmodel.getTerms().size(); i++)
		{
			TerminlogyObject user_cat_term = user_cat_inputmodel.getTerms().get(i);
			TerminlogyObject user_term =  user_inputmodel.getTerms().get(i);
			
			AccessRight user_cat_accessright = user_cat_term.getUserCategoryAccessRight();
			AccessRight user_access_right = user_term.getUserAccessRight();
			
			if (! user_cat_accessright.mayUpdate())
				user_cat_accessright.setMayUpdate(user_access_right.mayUpdate());
				
			if (! user_cat_accessright.mayDelete())
				user_cat_accessright.setMayDelete(user_access_right.mayDelete());
			
			if (! user_cat_accessright.mayExport())
				user_cat_accessright.setMayExport(user_access_right.mayExport());
			
			ArrayList<ChildTerminologyObject> user_cat_term_attrs = user_cat_inputmodel.getTermAttributesForTerm(((Term)user_cat_term));
			ArrayList<ChildTerminologyObject> user_term_attrs = user_inputmodel.getTermAttributesForTerm(((Term)user_term));
			
			for (int j = 0; j < user_cat_term_attrs.size(); j++)
			{
				ChildTerminologyObject user_cat_term_attr = user_cat_term_attrs.get(j);
				ChildTerminologyObject user_term_attr =  user_term_attrs.get(j);
				
				AccessRight user_cat_term_attr_accessright = user_cat_term_attr.getUserCategoryAccessRight();
				AccessRight user_access_term_attr_accessright = user_term_attr.getUserAccessRight();
				
				if (! user_cat_term_attr_accessright.mayUpdate())
					user_cat_term_attr_accessright.setMayUpdate(user_access_term_attr_accessright.mayUpdate());
					
				if (! user_cat_term_attr_accessright.mayDelete())
					user_cat_term_attr_accessright.setMayDelete(user_access_term_attr_accessright.mayDelete());
				
				if (! user_cat_term_attr_accessright.mayExport())
					user_cat_term_attr_accessright.setMayExport(user_access_term_attr_accessright.mayExport());
				
				if (user_cat_term_attr instanceof Synonym)
				{
					ArrayList<ChildTerminologyObject> user_cat_synonym_attrs = user_cat_inputmodel.getSynonymAttributesForTerm(((Term)user_cat_term));
					ArrayList<ChildTerminologyObject> user_synonym_attrs = user_inputmodel.getSynonymAttributesForTerm(((Term)user_term));
					
					for (int k = 0; k < user_cat_synonym_attrs.size(); k++)
					{
						ChildTerminologyObject user_cat_synonym_attr = user_cat_synonym_attrs.get(k);
						ChildTerminologyObject user_synonym_attr =  user_synonym_attrs.get(k);
						
						AccessRight user_cat_synonym_attr_accessright = user_cat_synonym_attr.getUserCategoryAccessRight();
						AccessRight user_access_synonym_attr_accessright = user_synonym_attr.getUserAccessRight();
						
						if (! user_cat_synonym_attr_accessright.mayUpdate())
							user_cat_synonym_attr_accessright.setMayUpdate(user_access_synonym_attr_accessright.mayUpdate());
							
						if (! user_cat_synonym_attr_accessright.mayDelete())
							user_cat_synonym_attr_accessright.setMayDelete(user_access_synonym_attr_accessright.mayDelete());
						
						if (! user_cat_synonym_attr_accessright.mayExport())
							user_cat_synonym_attr_accessright.setMayExport(user_access_synonym_attr_accessright.mayExport());
					}
				}
			}
		}
				
		return user_cat_inputmodel;		
	}
}
