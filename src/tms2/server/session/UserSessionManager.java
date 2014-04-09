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

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserSessionManager 
{
	private HashMap<String, HttpSession> _current_sessions = new HashMap<String, HttpSession>();
	
	public UserSessionManager()
	{
		
	}
	
	/**
	 * Adds a session for a user
	 * @param session
	 * @param authToken
	 */
	public void addSession(HttpSession session, String authToken)
	{		
		_current_sessions.put(authToken, session);
	}
		
	/**
	 * Removes the session when the user is destroyed.
	 *  
	 *  * @param authToken
	 */
	public void removeSession(String authToken)
	{
		_current_sessions.remove(authToken);
	}
	
	/**
	 * Gets the current session for the user.
	 * @param authToken 
	 * @return
	 */
	public HttpSession getCurrentUserSession(String authToken)
	{						
		return _current_sessions.get(authToken);
	}
	
	/**
	 * Gets all the current sessions
	 * @return
	 */
	public HashMap<String, HttpSession> getCurrentSessions()
	{
		return _current_sessions;		
	}
}
