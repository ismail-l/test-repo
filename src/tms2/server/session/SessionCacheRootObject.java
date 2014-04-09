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

package tms2.server.session;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.shared.User;


public class SessionCacheRootObject
{
	private HashMap<String, SessionCacheUserObject> cacheUsers = null;
	private UserSessionManager _user_session_manager = null;
	private Internationalization _i18n = Internationalization.getInstance();
	
	public SessionCacheRootObject()
	{
		setCacheUsers(new HashMap<String, SessionCacheUserObject>());
		setUserSessionManager(new UserSessionManager());
	}
	
	public void addUser(HttpSession session, User user)
	{
		// If this user is already cached remove it as the
		// cached user is still in guest mode.
		if (getCacheUsers().containsKey(user.getAuthToken()))
		{
			User olduser = getUser(user.getAuthToken(), false);
			
			Connection connection = getUserConnection(olduser.getAuthToken());
			
			DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), olduser.getAuthToken());
			delUser(olduser.getAuthToken());
		}
		
		SessionCacheUserObject userObject = new SessionCacheUserObject();
		
		userObject.setUser(user);
		
		// Only give a dedicated connection to non-guest users.
		if (! user.isGuest())
		{			
			Connection connection = DatabaseConnector.getConnection(user);			
			userObject.setConnection(connection);
		}
		
		getCacheUsers().put(user.getAuthToken(), userObject);
	}
	
	public User getUser(String authToken)
	{
		return getUser(authToken, true);
	}
	
	public User getUser(String authToken, boolean updateAccess)
	{
		SessionCacheUserObject userObject = getCacheUsers().get(authToken);
		
		if (userObject != null)
			return userObject.getUser(updateAccess);
		
		return null;
	}
	
	public Connection getUserConnection(String authToken)
	{
		SessionCacheUserObject userObject = getCacheUsers().get(authToken);
		
		if (userObject != null)
			return userObject.getConnection();
		
		return null;
	}
	
	public User delUser(String authToken)
	{
		SessionCacheUserObject userObject = getCacheUsers().remove(authToken);
		
		if (userObject != null)
			return userObject.getUser(false);
		
		return null;
	}
	
	public boolean updateUser(User user)
	{
		HashMap<String, SessionCacheUserObject> userCache = getCacheUsers();
		
		for (String authToken : userCache.keySet())
		{
			User tmpUser = userCache.get(authToken).getUser();
			
			if (user.getUserId() == tmpUser.getUserId())
			{
				userCache.get(authToken).setUser(user);
				return true;
			}
		}
		
		return false;
	}

	public User findUser(User user)
	{
		HashMap<String, SessionCacheUserObject> userCache = getCacheUsers();
		
		for (String authToken : userCache.keySet())
		{
			User tmpUser = userCache.get(authToken).getUser(false);
			
			if (user.getUserId() == tmpUser.getUserId())
				return tmpUser;
		}
		
		return null;
	}

	public Vector<User> getAllRegisteredUsers()
	{
		HashMap<String, SessionCacheUserObject> userCache = getCacheUsers();
		Vector<User> users = new Vector<User>();
		
		for (SessionCacheUserObject userObject : userCache.values())	
		{
			if (userObject.getConnection() != null)			
				users.add(userObject.getUser(false));
		}
		
		return users;
	}
	
/*	public Vector<User> getAllUnRegisteredUsers()
	{
		Vector<User> users = new Vector<User>();
		HashMap<String, HttpSession> current_sessions = _user_session_manager.getCurrentSessions();
		
		for (String authToken : current_sessions.keySet())
		{
			HttpSession session = current_sessions.get(authToken);
			User user = ApplicationSessionCache.getUserInSession(session, authToken);
			
			// If this user is null it is an unregistered guest.
			if (user == null)
			{				
				User unregistered_user = new User();
				unregistered_user.setIPAddress((String)session.getAttribute(AccessControlManager.IP_ADDRESS));
				unregistered_user.setFirstName("Guest");
				unregistered_user.setLastName("user");
								
				users.add(unregistered_user);
			}
		}
					
		return users;
	}*/
	
	public boolean getUserIsExpired(String authToken)
	{
		SessionCacheUserObject userObject = getCacheUsers().get(authToken);
		
		if (userObject != null)
			return userObject.isExpired();
		
		return false;
	}
	
	public Vector<User> getExpiredUsers()
	{
		HashMap<String, SessionCacheUserObject> userCache = getCacheUsers();
		Vector<User> users = new Vector<User>();
		
		for (SessionCacheUserObject userObject : userCache.values())
		{
			if (userObject.isExpired())
				users.add(userObject.getUser(false));
		}
		
		return users;
	}
	
	private void setCacheUsers(HashMap<String, SessionCacheUserObject> cacheUsers)
	{
		this.cacheUsers = cacheUsers;
	}

	private HashMap<String, SessionCacheUserObject> getCacheUsers()
	{
		return cacheUsers;
	}
	
	public void setUserSessionManager(UserSessionManager manager)
	{
		_user_session_manager = manager;
	}
	
	public UserSessionManager getUserSessionManager()
	{
		return _user_session_manager;
	}
	
	public void addUserSession(HttpSession session, String authToken)
	{
		_user_session_manager.addSession(session, authToken);
	}
	
	public HttpSession getUserSession(String authToken)
	{
		return _user_session_manager.getCurrentUserSession(authToken);
	}
	
	public void removeUserSession(String authToken)
	{
		_user_session_manager.removeSession(authToken);
	}
}
