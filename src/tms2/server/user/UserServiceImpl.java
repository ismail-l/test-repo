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

package tms2.server.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.exception.UserAuthenticationException;
import tms2.client.service.UserService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Result;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserServiceImpl extends AccessControlledRemoteService implements UserService 
{
	private static final long serialVersionUID = 2087252588596832232L;

	private static final Internationalization _i18n = Internationalization.getInstance();
	
	@Override
	public ArrayList<User> getAllUsers(String authToken) throws TMSException 
	{	
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		ArrayList<User> users = null;
		
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
				
				System.out.println("getAllUsers connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				users = UserManager.getAllUsers(connection);
				
				connection.commit();
				
				System.out.println("getAllUsers connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "All users for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_retrieve(""), e, authToken);
			TMSException tms_ex = new TMSException(_i18n.getMessages().server_user_retrieve(e.getMessage()), e);
			throw tms_ex;
		}
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return users;
	}
	
	@Override
	public Result<User> updateUser(String authToken, User user, String password) throws TMSException
	{
		HttpSession session = null;
		User signon_user = null;
		Connection connection = null;
		Result<User> result = new Result<User>();
		boolean is_updating = false;
		
		try 
		{			
			session = getCurrentUserSession(authToken);	
			
			if (session != null)
			{
				validateUserHasAdminRights(session, authToken);
					
				signon_user = getSignedOnUser(session, authToken);												
				
				if (signon_user == null || signon_user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(signon_user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("updateUser connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (user.getUserId() > -1)
					is_updating = true;	
				
				User updatedUser = UserManager.updateUser(connection, user, password, session, authToken, is_updating);
										
				connection.commit();
				
				System.out.println("updateUser connection done");
				
				if (updatedUser == null)
					throw new TMSException();
				else
				{
					result.setResult(updatedUser);
					if (is_updating)
						result.setMessage(_i18n.getMessages().admin_user_updateSuccess(updatedUser.getUsername()));
					else
						result.setMessage(_i18n.getMessages().admin_user_createSuccess(updatedUser.getUsername()));
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "User update for " + signon_user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			if (e instanceof UserAuthenticationException)
				result.setMessage(e.getMessage());
			else
			{
				if (is_updating)
					result.setMessage(_i18n.getMessages().server_user_update_fail(user.getFullName(),""));				
				else				
					result.setMessage(_i18n.getMessages().server_user_create_fail(user.getFullName(),""));
			}
			
			LogUtility.log(Level.SEVERE, session,result.getMessage(), e, authToken);
			TMSException tms_ex = new TMSException(result.getMessage());
			throw tms_ex;
		}
		finally
		{
			if (signon_user == null || signon_user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return result;
	}
	
	@Override
	public String generateUsername(String authToken, String firstname, String lastname) throws TMSException 
	{		
		HttpSession session = null;
		String username = "";
		
		try 
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				validateUserHasAdminRights(session, authToken);
				
				if (lastname.length() >= 6)			
					username = (lastname.substring(0, 6) + firstname.substring(0, 2)).toLowerCase();			
				else if ( (lastname.length() + firstname.length()) >= 8)			
					username = (lastname + firstname.substring(0, 8 - lastname.length())).toLowerCase();			
				else
				{
					username = (lastname + firstname).toLowerCase();
					String time = new Date().getTime() + "";
					StringBuffer buffer = new StringBuffer(time);
					time = buffer.reverse().toString(); //To ensure a unique string with each call; the characters at the end of a time long value change the most frequently.
					int index = 0;
					while (username.length() < 8)
					{
						username += time.charAt(index);
						index++;
					}				
				}
				
				if (verifyUsernameExistence(authToken, username))
				{
					TMSException tms_ex = new TMSException(_i18n.getMessages().server_user_generate_accept(username,  firstname + lastname));
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_generate_accept(username,  firstname + lastname), tms_ex, authToken);
					throw tms_ex;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_generate_fail(""), e, authToken);
			TMSException tms_ex = new TMSException(_i18n.getMessages().server_user_generate_fail(e.getMessage()), e);
			throw tms_ex;
		}
		
		return username;
	}

	
	@Override
	public boolean verifyUsernameExistence(String authToken, String username) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		boolean verified = false;
		
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
				
				System.out.println("verifyUsernameExistence connection: " + connection);
				
				connection.setAutoCommit(false);
				
				verified = UserManager.verifyUserExistence(connection, username);
				
				connection.commit();
				
				System.out.println("verifyUsernameExistence connection done");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_verify(username, ""), e, authToken);
			TMSException tms_ex = new TMSException(_i18n.getMessages().server_user_verify(username, e.getMessage()), e);
			throw tms_ex;
		}
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return verified;
	}
}
