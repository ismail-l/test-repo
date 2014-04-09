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

package tms2.server.accesscontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.AccessControlException;
import tms2.client.exception.UserAuthenticationException;
import tms2.server.BCrypt;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.record.RecordManager;
import tms2.server.session.ApplicationSessionCache;
import tms2.server.session.SessionServiceImpl;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.user.UserManager;
import tms2.shared.User;

public class AccessControlManager 
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	public static final String AUTH_TOKEN = "UserAuthToken";
	public static final String IP_ADDRESS = "IP";

	public static User signOn(HttpSession session, String authToken, User user, String ip_address) throws AccessControlException
	{		
		user.setAuthToken(authToken);
		user.setIPAddress(ip_address);
			
		session.setAttribute(AUTH_TOKEN, authToken);
		session.setAttribute(IP_ADDRESS, ip_address);
		
		LogUtility.log(Level.INFO, "Creating user session.");
		
		// This is needed so that the Administrator can remotely terminate a stale user
		// session from the application, without having to restart the server.
		ApplicationSessionCache.storeInSessionCache(session, user);
		ApplicationSessionCache.addSessionInSessionCache(session, authToken);
			
		LogUtility.log(Level.INFO, "User: " + user.getUsername() + " in session " + session.getId() + " is signed in");
		
		return user;
	}

	public static void signOff(HttpSession session, User user, String authToken) 
	{		
		Connection connection = null;
		
		try 
		{													
			if (! user.isGuest())
			{
				connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				connection.setAutoCommit(false);
				
				RecordManager.unlockRecordsForUser(connection, user.getUserId());
				
				connection.commit();				
			}
			
			if (connection != null)			
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);																													
			
			ApplicationSessionCache.terminateSession(session, authToken);
			ApplicationSessionCache.removeSessionInSessionCache(session, authToken);
			
			LogUtility.log(Level.INFO, "User: " + user.getUsername() + " in session" + session.getId() + " is signed out");
				
			SessionServiceImpl.invalidateSession(session);
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
		}	
	}
	
	public static User getSignedOnUser(HttpSession session, String authToken)
	{
		return getSignedOnUser(session, authToken, true);
	}

	public static User getSignedOnUser(HttpSession session, String authToken, boolean lastAccess)
	{
		if (authToken != null)
		{
			User user = ApplicationSessionCache.getUserInSession(session, authToken, lastAccess);
			return user;
		}
		
		return null;
	}
	
	public static Vector<User> getAllRegisteredUsers(HttpSession session)
	{
		return ApplicationSessionCache.getSessionCache(session).getAllRegisteredUsers();
	}
		
	public static boolean isUserStillSignedOn(HttpSession session, String authToken)
	{
		return isUserStillSignedOn(session, authToken, true);
	}
	
	public static boolean isUserStillSignedOn(HttpSession session, String authToken, boolean lastAccess)
	{
		return (getSignedOnUser(session, authToken, lastAccess) != null);
	}

	public static boolean updateSignedOnUser(HttpSession session, User user)
	{
		User oldUser = ApplicationSessionCache.findUserInSessionCache(session, user);
		
		if (oldUser != null)
		{
			user.setAuthToken(oldUser.getAuthToken());
			user.setSessionId(oldUser.getSessionId());
			
			return ApplicationSessionCache.updateUserInSessionCache(session, user);
		}
		
		return false;
	}
		
	public static boolean hasUserAdminRights(HttpSession session, String authToken)
	{
		return hasUserAdminRights(session, authToken, true);
	}
	
	public static boolean hasUserAdminRights(HttpSession session, String authToken, boolean lastAccess)
	{
		User user = ApplicationSessionCache.getUserInSession(session, authToken, lastAccess);
		
		if (user != null)
			return user.isAdmin();
		
		return false;
	}
	
	public static void validateUserIsSignedOn(HttpSession session, String authToken) throws AccessControlException
	{
		if (!isUserStillSignedOn(session, authToken))
		{
			AccessControlException a = new AccessControlException(_i18n.getConstants().server_error_signedOn());
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().server_error_signedOn(), a, authToken);
			throw a;			
		}
	}

	public static void validateUserHasAdminRights(HttpSession session, String authToken) throws AccessControlException
	{
		if (!hasUserAdminRights(session, authToken))
		{
			AccessControlException a = new AccessControlException(_i18n.getConstants().server_error_admin());
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().server_error_admin(), a, authToken);
			throw a;
		}
	}
	
	public static User findDuplicateSignedOnUser(HttpSession session, User user, String authToken)
	{
		User oldUser = ApplicationSessionCache.findUserInSessionCache(session, user);

		if (oldUser != null)
		{
			if(user.getUserId() == oldUser.getUserId() && !oldUser.getAuthToken().equals(authToken))			
				return oldUser;			
		}
		
		return null;
	}
	
	public static User retrieveUserByUsername(HttpSession session, String username, String password, String authToken) throws AccessControlException
	{
		User user = null;		
		
		Connection connection = null;
		
		try
		{		
			connection = DatabaseConnector.getSuperUserConnectionFromPool();
			connection.setAutoCommit(false);
			
			user = UserManager.getUserByName(connection, username);
			
			if (user != null)
			{								
				Date expiryDate = user.getExpiryDate();
				boolean activated = user.isActivated();
				
				if (expiryDate != null && new Date().after(expiryDate))
				{
					UserAuthenticationException exception = new UserAuthenticationException(_i18n.getMessages().server_accountRenew(username));
					exception.setAccountStatus(UserAuthenticationException.ACCOUNT_EXPIRED);
					LogUtility.log(Level.WARNING, session,  _i18n.getMessages().server_accountRenew(username), exception, user.getAuthToken());
					throw exception;					
				}
				else if (!activated)
				{
					UserAuthenticationException exception = new UserAuthenticationException(_i18n.getMessages().server_accountActivate(username));
					exception.setAccountStatus(UserAuthenticationException.ACCOUNT_INACTIVE);
					LogUtility.log(Level.WARNING, session,  _i18n.getMessages().server_accountActivate(username), exception, user.getAuthToken());
					throw exception;					
				}
				else
				{
					String hashFromDB = UserManager.getUserPassword(connection, user.getUserId());	
					
					if (BCrypt.checkpw(password, hashFromDB))
					{
						user.setLastSignOn(new Date());
						user.setSessionId(session.getId());
						
						StoredProcedureManager.updateLastSignOn(connection, user.getUserId());					
					}
					else
					{
						UserAuthenticationException exception = new UserAuthenticationException(_i18n.getConstants().server_error_passIncorrect());
						exception.setAccountStatus(UserAuthenticationException.PASSWORD_INCORRECT);
						LogUtility.log(Level.WARNING, session, _i18n.getConstants().server_error_passIncorrect(), exception, user.getAuthToken());
						throw exception;						
					}
				}
			}
			else
			{
				UserAuthenticationException exception = new UserAuthenticationException(_i18n.getConstants().server_error_userIncorrect());
				exception.setAccountStatus(UserAuthenticationException.USERNAME_NOT_FOUND);
				LogUtility.log(Level.WARNING, session, _i18n.getConstants().server_error_userIncorrect(), exception);
				throw exception;				
			}	
			
			connection.commit();
		}
		catch (Exception ex)
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
			
			ex.printStackTrace();
			
			LogUtility.log(Level.WARNING, session,  _i18n.getMessages().log_session_signOn(""), ex, authToken);
						
			throw new AccessControlException(ex.getMessage());
		}
		finally
		{
			DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), null);
		}
		
		return user;
	}
}
