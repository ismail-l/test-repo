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

package tms2.server.usercategory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.UserCategoryService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Result;
import tms2.shared.User;
import tms2.shared.UserCategory;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserCategoryServiceImpl extends AccessControlledRemoteService implements UserCategoryService 
{
	private static final long serialVersionUID = -4787554379835143218L;
	private static final Internationalization _i18n = Internationalization.getInstance();
	
	@Override
	public ArrayList<UserCategory> getAllUserCategoriesWithUsers(String authToken) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		ArrayList<UserCategory> user_cats = null;
		
		try 
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				validateUserHasAdminRights(session, authToken);
				
				user  = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("getAllUserCategoriesWithUsers connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				user_cats =  UserCategoryManager.getAllUserCategoriesWithUsers(connection, session, authToken);
							
				connection.commit();
				
				System.out.println("getAllUserCategoriesWithUsers connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start)/1000;
				
				LogUtility.log(Level.INFO, "All user categories with users for " + user.getUsername() +  " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_all_user_cat_users(), e, authToken);
			throw new TMSException(e);
		} 	
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return user_cats;
	}
			
	@Override
	public Result<UserCategory> updateUserCategory(String authToken, UserCategory userCategory) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<UserCategory> result = new Result<UserCategory>();
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
				
				System.out.println("updateUserCategory connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (userCategory.getUserCategoryId() > -1)
					is_updating = true;
				
				UserCategory updated_category =  UserCategoryManager.updateUserCategory(connection, userCategory, is_updating);			
										
				connection.commit();
				
				System.out.println("updateUserCategory connection done");
				
				if (updated_category == null)
					throw new TMSException();
				else
				{
					result.setResult(updated_category);
					if (is_updating)
						result.setMessage(_i18n.getMessages().admin_cat_updateSuccess(updated_category.getUserCategoryName()));
					else
						result.setMessage(_i18n.getMessages().admin_cat_createSuccess(updated_category.getUserCategoryName()));
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start)/1000;
				
				LogUtility.log(Level.INFO, "User category update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
				result.setMessage(_i18n.getMessages().server_uc_create(userCategory.getUserCategoryName()));				
			else				
				result.setMessage(_i18n.getMessages().server_uc_update(userCategory.getUserCategoryName()));
			
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
