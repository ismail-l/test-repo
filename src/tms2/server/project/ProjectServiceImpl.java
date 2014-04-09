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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.ProjectService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Project;
import tms2.shared.Result;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ProjectServiceImpl extends AccessControlledRemoteService implements ProjectService
{
	private static final long serialVersionUID = 7073695253368573925L;
	
	private static Internationalization _i18n = Internationalization.getInstance();

	@Override
	public Result<Project> updateProject(String authToken, Project project) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<Project> result = new Result<Project>();
		boolean is_updating = false;
				
		try 
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				validateUserHasAdminRights(session, authToken);
					
				user = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("updateProject connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (project.getProjectId() > -1)
					is_updating = true;
				
				Project updated_project = ProjectManager.updateProject(connection, project, is_updating);						
				
				connection.commit();
				
				System.out.println("updateProject connection done");
				
				if (updated_project == null)
					throw new TMSException();
				else
				{
					result.setResult(project);
					if (is_updating)
						result.setMessage(_i18n.getMessages().server_proj_update_success(project.getProjectName()));
					else
						result.setMessage(_i18n.getMessages().server_proj_create_success(project.getProjectName()));
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Project update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
			}
		}
		catch (Exception e) 
		{
			if (connection != null)
			{
				try
				{
					connection.rollback();
				}
				catch (SQLException e1)
				{
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, authToken);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			if (is_updating)
				result.setMessage(_i18n.getMessages().server_proj_update_fail(""));
			else
				result.setMessage(_i18n.getMessages().server_termbase_create_fail( ""));
			
			LogUtility.log(Level.SEVERE, session, result.getMessage(), e, authToken);
			throw new TMSException(result.getMessage());
		}
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return result;
	}
}
