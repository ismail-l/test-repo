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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.AccessRightService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.project.ProjectManager;
import tms2.server.record.RecordManager;
import tms2.server.session.ApplicationSessionCache;
import tms2.server.termbase.TermBaseManager;
import tms2.shared.AccessRight;
import tms2.shared.ChildAccessRight;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.Result;
import tms2.shared.Synonym;
import tms2.shared.Term;
import tms2.shared.TermBase;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;
import tms2.shared.wrapper.AccessRightDetailsWrapper;
import tms2.shared.wrapper.RecordEditDetailsWrapper;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AccessRightServiceImpl extends AccessControlledRemoteService implements AccessRightService
{
	private static final long serialVersionUID = -6806985003493952048L;
	
	private static Internationalization _i18n = Internationalization.getInstance();

	@Override
	public Result<Boolean> updateAccessRights(String authToken, InputModel inputmodel, ArrayList<Project> projects, long consumer_id, boolean is_user_access_right) throws TMSException 
	{	
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<Boolean> result = new Result<Boolean>();				
		
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
				
				System.out.println("updateAccessRights connection: " + connection);
				
				connection.setAutoCommit(false);
					
				long start = Calendar.getInstance().getTimeInMillis();
				
				AccessRightManager.assignProjects(connection, projects, is_user_access_right, consumer_id);
				
				AccessRight updated_access_right = null;
				
				Iterator<TerminlogyObject> iter = inputmodel.getRecordAttributes().iterator();
				while (iter.hasNext())
				{
					TerminlogyObject record_attribute = iter.next();
					
					AccessRight access_right = null;
					
					if (is_user_access_right)
						access_right = record_attribute.getUserAccessRight();
					else
						access_right = record_attribute.getUserCategoryAccessRight();
					
					if (access_right.getRightsId() == -1)
						updated_access_right = AccessRightManager.createAccessRight(connection, access_right, consumer_id, is_user_access_right);
					else
						updated_access_right = AccessRightManager.updateAccessRight(connection, access_right, consumer_id, is_user_access_right);
					
					if (updated_access_right == null)
						throw new TMSException();
				}
				
				updated_access_right = null;
				
				iter = inputmodel.getTerms().iterator();
				while (iter.hasNext())
				{
					TerminlogyObject term = iter.next();
					
					AccessRight access_right = null;
					
					if (is_user_access_right)
						access_right = term.getUserAccessRight();
					else
						access_right = term.getUserCategoryAccessRight();
					
					if (access_right.getRightsId() == -1)
						updated_access_right = AccessRightManager.createAccessRight(connection, access_right, consumer_id, is_user_access_right);
					else
						updated_access_right = AccessRightManager.updateAccessRight(connection, access_right, consumer_id, is_user_access_right);
					
					if (updated_access_right == null)
						throw new TMSException();
					
					ChildAccessRight updated_child_access_right = null;
					
					ArrayList<ChildTerminologyObject> termattributes = inputmodel.getTermAttributesForTerm((Term)term);
					Iterator<ChildTerminologyObject> term_attr_iter = termattributes.iterator();
					while (term_attr_iter.hasNext())
					{
						ChildTerminologyObject term_attribute = term_attr_iter.next();
						
						ChildAccessRight term_attr_access_right = null;
						
						if (is_user_access_right)
							term_attr_access_right = (ChildAccessRight) term_attribute.getUserAccessRight();
						else
							term_attr_access_right = (ChildAccessRight) term_attribute.getUserCategoryAccessRight();																
						
						if (term_attr_access_right.getRightsId() == -1)
						{
							term_attr_access_right.setParentId(updated_access_right.getRightsId());
							updated_child_access_right = AccessRightManager.createChildAccessRight(connection, term_attr_access_right, is_user_access_right);
						}
						else
							updated_child_access_right = AccessRightManager.updateChildAccessRight(connection, term_attr_access_right, is_user_access_right);
						
						if (updated_child_access_right == null)
							throw new TMSException();
						
						if (term_attribute instanceof Synonym)
						{
							updated_child_access_right = null;
							
							ArrayList<ChildTerminologyObject> synonym_attributes = inputmodel.getSynonymAttributesForTerm((Term) term);
							Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
							while (synonym_attr_iter.hasNext())
							{
								ChildTerminologyObject synonym_attribute = synonym_attr_iter.next();
								
								if (is_user_access_right)
									term_attr_access_right = (ChildAccessRight) synonym_attribute.getUserAccessRight();
								else
									term_attr_access_right = (ChildAccessRight) synonym_attribute.getUserCategoryAccessRight();
								
								if (term_attr_access_right.getRightsId() == -1)
								{
									term_attr_access_right.setParentId(updated_access_right.getRightsId());
									updated_child_access_right = AccessRightManager.createChildAccessRight(connection, term_attr_access_right, is_user_access_right);
								}
								else
									updated_child_access_right = AccessRightManager.updateChildAccessRight(connection, term_attr_access_right, is_user_access_right);
								
								if (updated_child_access_right == null)
									throw new TMSException();
							}
						}
					}
				}
													
				connection.commit();
				
				System.out.println("updateAccessRights done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Access rights update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
				
				result.setResult(true);
				result.setMessage(_i18n.getConstants().admin_access_rights_update());	
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
			
			result.setMessage(_i18n.getConstants().server_error_acu());
						
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

	@Override
	public AccessRightDetailsWrapper getAccessRightDetails(String authToken, long consumer_id, boolean is_user_access_right) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		AccessRightDetailsWrapper wrapper = null;
		
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
				
				System.out.println("getAccessRightDetails connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				wrapper = new AccessRightDetailsWrapper();								
				
				InputModel inputmodel = AccessRightManager.getAccessRights(connection, consumer_id, is_user_access_right);
				wrapper.setInputModel(inputmodel);
				
				ArrayList<Project> assigned_projects = AccessRightManager.getUserAssignedProjects(connection, consumer_id, is_user_access_right);
				
				ArrayList<Project> available_projects = ProjectManager.getAllProjects(connection);
				
				Iterator<Project> assigned_iter = assigned_projects.iterator();
				while (assigned_iter.hasNext())
				{
					Project assigned_project = assigned_iter.next();
					
					Iterator<Project> available_iter = available_projects.iterator();
					while (available_iter.hasNext())
					{
						Project available_project = available_iter.next();
						if (assigned_project.getProjectId() == available_project.getProjectId())
						{
							available_iter.remove();
							break;
						}
					}				
				}
							
				wrapper.setAvailableProjects(available_projects);
				wrapper.setAssignedProjects(assigned_projects);
				
				connection.commit();
				
				System.out.println("getAccessRightDetails connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Access rights details for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_ar_uarRetrieve(""), e, authToken);
			throw new TMSException(e);
		}
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return wrapper;
	}

	@Override
	public RecordEditDetailsWrapper getRecordEditDetails(String authToken, long record_id, boolean is_editing) throws TMSException 
	{
		HttpSession session = null;	
		User user = null;
		Connection connection = null;
		RecordEditDetailsWrapper wrapper = null;
		
		try 
		{	
			session = getCurrentUserSession(authToken);	
			
			if (session != null)
			{
				user = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("getRecordEditDetails connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				wrapper = new RecordEditDetailsWrapper();
				
				InputModel user_inputmodel = AccessRightManager.getAccessRights(connection, user.getUserId(), true);						
				InputModel user_cat_inputmodel = AccessRightManager.getAccessRights(connection, user.getUserCategoryId(), false);
				
				InputModel inputmodel = AccessRightManager.mergeInputModels(user_inputmodel, user_cat_inputmodel);
				wrapper.setInputModel(inputmodel);
										
				ArrayList<TermBase> termbases = TermBaseManager.getAccessControlledProjects(connection, user);
				wrapper.setTermBases(termbases);
				
				if (is_editing)
				{
					User editing_user = RecordManager.isRecordLocked(connection, record_id);
					
					if (editing_user != null)					
						wrapper.setUser(editing_user);
					else
						RecordManager.lockRecord(connection, user, record_id);
				}
				
				connection.commit();
				
				System.out.println("getRecordEditDetails connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Record edit details for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_record_edit_details(), e, authToken);
			throw new TMSException(e);
		} 
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return wrapper;
	}
	
	@Override
	public InputModel getAccessRightInputModel(String authToken) throws TMSException 
	{
		HttpSession session = null;		
		User user = null;
		Connection connection = null;
		InputModel inputmodel = null;
		
		try 
		{		
			session = getCurrentUserSession(authToken);	
			
			if (session != null)
			{
				user = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("getAccessRightInputModel connection: " + connection);
				
				connection.setAutoCommit(false);
					
				long start = Calendar.getInstance().getTimeInMillis();
				
				InputModel user_inputmodel = AccessRightManager.getAccessRights(connection, user.getUserId(), true);						
				InputModel user_cat_inputmodel = AccessRightManager.getAccessRights(connection, user.getUserCategoryId(), false);
				
				inputmodel = AccessRightManager.mergeInputModels(user_inputmodel, user_cat_inputmodel);
				
				connection.commit();	
				
				System.out.println("getAccessRightInputModel connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Access rights input model for " + user.getUsername() + " in session " + session.getId() + " took: " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_export(), e, authToken);
			throw new TMSException(e);
		} 
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return inputmodel;
	}
}
