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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import tms2.shared.User;

public class ApplicationSessionCache
{
	/**
	 * Retrieves the User object matching the given sessionId, from the application
	 * context. The given session object is unrelated and belongs to the current caller.
	 * It is simply needed to get a reference to the application context.
	 * 
	 * @param session
	 * @param authToken
	 * @return
	 */
	public static User getUserInSession(HttpSession session, String authToken)
	{
		return getSessionCache(session).getUser(authToken);
	}
	
	/**
	 * Retrieves the User object matching the given sessionId, from the application
	 * context. The given session object is unrelated and belongs to the current caller.
	 * It is simply needed to get a reference to the application context.
	 * 
	 * @param session
	 * @param authToken
	 * @param updateAccess
	 * @return
	 */
	public static User getUserInSession(HttpSession session, String authToken, boolean updateAccess)
	{
		return getSessionCache(session).getUser(authToken, updateAccess);
	}
	
	/**
	 * Purges the User object matching the given sessionId, from the application context.
	 * The session object is unrelated to the sessionId parameter and is needed to get a 
	 * reference to the application context.
	 * 
	 * @param session
	 * @param authToken
	 * @return
	 */
	public static User terminateSession(HttpSession session, String authToken)
	{
		return getSessionCache(session).delUser(authToken);
	}
	
	/**
	 * Stores the given User object in the application context. The given session object may or may not be related
	 * to the user. The session object belongs to the current caller and is simply needed to get a reference
	 * to the application context.
	 * 
	 * @param session
	 * @param user
	 */
	public static void storeInSessionCache(HttpSession session, User user)
	{
		getSessionCache(session).addUser(session, user);
	}

	/**
	 * Returns the given User object in the application context if it already exists.
	 * 
	 * @param session
	 * @param user
	 */
	public static User findUserInSessionCache(HttpSession session, User user)
	{
		return getSessionCache(session).findUser(user);
	}
	
	public static void addSessionInSessionCache(HttpSession session, String authToken)
	{
		getSessionCache(session).addUserSession(session, authToken);
	}
	
	public static HttpSession getSessionInSessionCache(HttpSession session, String authToken)
	{
		return getSessionCache(session).getUserSession(authToken);		
	}
	
	public static void removeSessionInSessionCache(HttpSession session, String authToken)
	{
		getSessionCache(session).removeUserSession(authToken);
	}
	
	public static UserSessionManager getUserSessionManager(ServletContext context)
	{
		return getSessionCache(context).getUserSessionManager();
	}
	
	/**
	 * Updates the given User object in the application context if it already exists.
	 * 
	 * @param session
	 * @param user
	 */
	public static boolean updateUserInSessionCache(HttpSession session, User user)
	{
		return getSessionCache(session).updateUser(user);
	}
	
	/**
	 * Returns the application context to which the given session belongs.
	 * 
	 * @param session  The session object for the current request
	 * @return
	 */
	public static SessionCacheRootObject getSessionCache(HttpSession session)
	{
		ServletContext context = session.getServletContext();
		return getSessionCache(context);
	}

	/**
	 * Returns the application context to which the given session belongs.
	 * 
	 * @param context  The context object for the current servlet
	 * @return
	 */
	public static SessionCacheRootObject getSessionCache(ServletContext context)
	{
		SessionCacheRootObject cache = (SessionCacheRootObject)context.getAttribute("sessionCache");
		if (cache == null)
		{
			cache = new SessionCacheRootObject();
			context.setAttribute("sessionCache", cache);
		}
		
		return cache;
	}
}
