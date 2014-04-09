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

package tms2.server.project;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import tms2.server.AppConfig;
import tms2.server.accessright.AccessRightManager;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.util.StringLiteralEscapeUtility;
import tms2.shared.Project;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ProjectManager 
{
	public static ArrayList<Project> getProjectsByTermBaseId(Connection connection, long termbase_id) throws Exception
	{		
		ArrayList<Project> projects = new ArrayList<Project>();
		
		String project_sql = " select projects.* from tms.projects " + 
			 				 " where projects.termbaseid = " + termbase_id;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, project_sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			Project project = getProjectByProjectId(connection, results.getLong("projectid"), null);			
			projects.add(project);
		}
		
		results.close();
		stored_procedure.close();
		
		return projects;
	}
		
	/**
	 * Retrieves a project in its entirety. The user parameter is used to retrieve specific 
	 * access rights for the project, however this parameter may be null. 
	 * @param connection
	 * @param project_id
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static Project getProjectByProjectId(Connection connection, long project_id, User user) throws Exception
	{		
		Project project = null;
		
		AppConfig config = AppConfig.getInstance();
		
		String sql = " select projects.*, fields.* from tms.projects, tms.fields where " + 
				     " projects.projectid = " + project_id + 
				     " and fields.fieldname = '" + StringLiteralEscapeUtility.escapeStringLiteral(config.getProjectField()) + "'";
					
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
						
		if (results.next())
		{
			project = new Project();
			project.setProjectId(results.getLong("projectid"));
			project.setProjectName(results.getString("projectname"));				
			project.setDatetimecreated(results.getDate("datetimecreated"));
			project.setDatetimelastupdated(results.getDate("datetimelastupdated"));								
			project.setTermBaseId(results.getLong("termbaseid"));
			project.setFieldId(results.getLong("fieldid"));
			project.setFieldName(results.getString("fieldname"));
			project.setFieldTypeId(results.getInt("fieldtypeid"));
			project.setFieldDataTypeId(results.getInt("fielddatatypeid"));
			project.setMaxlength(results.getInt("maxlength"));
			project.setDefaultValue(results.getString("defaultvalue"));
			project.setMandatory(true);
			
			if (user != null)
			{
				project.setUserAccessRight(AccessRightManager.assignAccessRight(connection, project,  user.getUserId(), true));
				project.setUserCategoryAccessRight(AccessRightManager.assignAccessRight(connection, project, user.getUserCategoryId(), false));
			}
		}
				
		results.close();
		stored_procedure.close();
				
		return project;
	}
	
	public static Project updateProject(Connection connection, Project project, boolean is_updating) throws Exception
	{		
		Project updated = null;
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = StoredProcedureManager.updateProject(connection, project);
		else
			stored_procedure = StoredProcedureManager.createProject(connection, project);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)		
			updated = getProjectByProjectId(connection, result, null);		
		
		stored_procedure.close();
		
		return updated;
	}
	
	public static ArrayList<Project> getAllProjects(Connection connection) throws Exception
	{
		ArrayList<Project> projects = new ArrayList<Project>();
		
		String sql = "select * from tms.projects";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			Project project = getProjectByProjectId(connection, results.getLong("projectid"), null);
			projects.add(project);
		}
		
		results.close();
		stored_procedure.close();
		
		return projects;
	}
	
	/**
	 * Gets the projects (along with its access rights) that are 
	 * assigned to the Record. 
	 * @param connection
	 * @param record_id
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Project> getRecordProjects(Connection connection, long record_id, User user) throws Exception
	{
		ArrayList<Project> projects = new ArrayList<Project>();
		
		String sql = "select projectid from tms.recordprojects, tms.records where " + 
					 "recordprojects.recordid = records.recordid and " + 
				     "recordprojects.recordid = " + record_id + " order by recordprojects.projectid";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		while (result.next())
		{
			Project project = getProjectByProjectId(connection, result.getLong("projectid"), user);
			projects.add(project);
		}
		
		result.close();
		stored_procedure.close();
		
		return projects;				
	}
	
	public static void updateRecordProjects(Connection connection, long record_id, ArrayList<Project> projects) throws SQLException
	{
		StoredProcedureManager.removePreviousRecordProjects(connection, record_id);
		
		Iterator<Project> iter = projects.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			
			CallableStatement stored_procedure = StoredProcedureManager.addRecordProjects(connection, record_id, project.getProjectId());
			stored_procedure.close();
		}
	}
	
	public static ArrayList<Project> getUserCategoryAccessControlledProjects(Connection connection, long termbaseid, long usercategory_id) throws Exception
	{
		ArrayList<Project> access_controlled_projects = new ArrayList<Project>();

		String sql =  "select usercategoryprojects.projectid from tms.projects, tms.usercategoryprojects, tms.termbases where " +
					  "projects.projectid = usercategoryprojects.projectid and " + 
					  "usercategoryprojects.usercategoryid = " + usercategory_id + " " +
					  " and termbases.termbaseid = projects.termbaseid " + " " + 
					  " and termbases.termbaseid = " + termbaseid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			Project project = getProjectByProjectId(connection, results.getLong("projectid"), null);
			access_controlled_projects.add(project);			
		}
		
		return access_controlled_projects;
	}
	
	public static ArrayList<Project> getUserAccessControlledProjects(Connection connection, long termbaseid, ArrayList<Project> projects, long user_id) throws Exception
	{
		ArrayList<Project> access_controlled_projects = new ArrayList<Project>();
		
		String sql = "select userprojects.projectid from tms.projects, tms.userprojects, tms.termbases where " +
		 			 "projects.projectid = userprojects.projectid and " +
		 			 "userprojects.userid = " + user_id + " " +
		 			 "and termbases.termbaseid = projects.termbaseid " + 
		 			 "and termbases.termbaseid = " + termbaseid;

		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			long project_id = results.getLong("projectid");
			
			if (! isDuplicate(projects, project_id))
			{
				Project project = getProjectByProjectId(connection, project_id, null);
				access_controlled_projects.add(project);
			}
		}
				
		return access_controlled_projects;
	}
	
	private static boolean isDuplicate(ArrayList<Project> projects, long project_id)
	{
		Iterator<Project> iter = projects.iterator();
		while (iter.hasNext())
		{
			Project project = iter.next();
			
			if (project.getProjectId() == project_id)
				return true;
		}
		
		return false;
	}
}
