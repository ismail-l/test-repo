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

package tms2.server.termbase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import tms2.server.project.ProjectManager;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.Project;
import tms2.shared.TermBase;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermBaseManager 
{
	public static ArrayList<TermBase> getAllTermBases(Connection connection, boolean get_projects) throws Exception
	{		
		ArrayList<TermBase> termbases = new ArrayList<TermBase>();
				
		String termbase_sql = " select termbases.*, users.firstname, users.lastname  from " +
							  " tms.termbases, tms.users where users.userid = termbases.userid " + 
							  " order by termbases.termbasename";
										
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, termbase_sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
						
		while (results.next())
		{
			TermBase database = getTermBaseByTermBaseId(connection, results.getLong("termbaseid"), get_projects);				
			termbases.add(database);
		}
			
		results.close();
		stored_procedure.close();
		
		return termbases;
	}
		
	public static TermBase updateTermBase(Connection connection, TermBase termbase, HttpSession session, String authToken, boolean is_updating) throws Exception
	{		
		TermBase updated = null;
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = StoredProcedureManager.updateTermBase(connection, termbase);
		else
			stored_procedure = StoredProcedureManager.createTermBase(connection, termbase);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)		
			updated = getTermBaseByTermBaseId(connection, result, true);		
		
		stored_procedure.close();
		
		return updated;
	}
	
	public static TermBase getTermBaseByTermBaseId(Connection connection, long termbase_id, boolean get_projects) throws Exception
	{		
		TermBase termbase = null;
		
		String sql = " select termbases.*, users.firstname, users.lastname from " + 
					 " tms.termbases, tms.users where termbases.termbaseid = " + termbase_id + "" +
					 " and users.userid = termbases.userid";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		if (results.next())		
			termbase = getTermBase(connection, results, get_projects);					
				
		results.close();
		stored_procedure.close();
		
		return termbase;
	}	
	
	public static ArrayList<TermBase> getAccessControlledProjects(Connection connection, User user) throws Exception
	{
		ArrayList<TermBase> termbases = getAllTermBases(connection, false);
			
		ArrayList<TermBase> access_controlled_termbases = new ArrayList<TermBase>();
		
		Iterator<TermBase> iter = termbases.iterator();
		while (iter.hasNext())
		{
			TermBase termbase = iter.next();
			
			ArrayList<Project> projects = new ArrayList<Project>();
			
			ArrayList<Project> user_cat_projects = ProjectManager.getUserCategoryAccessControlledProjects(connection, termbase.getTermdbid(), user.getUserCategoryId());
			projects.addAll(user_cat_projects);
			
			ArrayList<Project> user_projects = ProjectManager.getUserAccessControlledProjects(connection, termbase.getTermdbid(), user_cat_projects, user.getUserId());
			projects.addAll(user_projects);
						
			// Only add termsbases that have projects
			if (projects.size() > 0)
			{
				termbase.setProjects(projects);				
				access_controlled_termbases.add(termbase);
			}
		}
		
		return access_controlled_termbases;
	}
	
	private static TermBase getTermBase(Connection connection, ResultSet result, boolean get_projects) throws Exception
	{
		TermBase termbase = new TermBase();
		
		termbase.setTermdbid(result.getLong("termbaseid"));
		termbase.setTermdbname(result.getString("termbasename"));
		termbase.setOwneruserid(result.getLong("userid"));		
		termbase.setDatetimecreated(result.getDate("datetimecreated"));
		termbase.setDatetimelastupdated(result.getDate("datetimelastupdated"));							
		termbase.setOwnername(result.getString("firstname") + " " + result.getString("lastname"));
		termbase.setEmail(result.getString("adminemail"));
		
		if (get_projects)
		{
			ArrayList<Project> projects = ProjectManager.getProjectsByTermBaseId(connection, termbase.getTermdbid());			
			termbase.setProjects(projects);
		}
		
		return termbase;
	}
	
	public static String getTermBaseEmail(Connection connection, long termbase_id) throws SQLException
	{
		String email = null;
		
		String sql = "SELECT adminemail FROM tms.termbases WHERE termbases.termbaseid = " + termbase_id;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		if (result.next())
			email = result.getString("adminemail");
		
		result.close();
		stored_procedure.close();
		
		return email;
	}
	
}
