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

package tms2.server.logging;

import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.User;


public class LogUtility extends AccessControlledRemoteService 
{
	private static final long serialVersionUID = 4032283980352060235L;
		
	public static void log(Level level, HttpSession session, String message, Exception ex, String authToken)
	{
		Logger logger = Logger.getInstance();
		
		User user = ApplicationSessionCache.getUserInSession(session, authToken, false);
		if (user == null)
			logger.log(Level.SEVERE, session.getId(), message, ex);
		else
			logger.log(Level.SEVERE, user.getFullName() + "->" + user.getIPAddress() + "->" + session.getId(), message, ex);
	}

	public static void log(Level level, HttpSession session, String message, String authToken)
	{
		Logger logger = Logger.getInstance();
		
		User user = ApplicationSessionCache.getUserInSession(session, authToken, false);
		if (user == null)
			logger.log(Level.SEVERE, session.getId(), message);
		else
			logger.log(Level.SEVERE, user.getFullName() + "->" + user.getIPAddress() + "->" + session.getId(), message);
	}
	
	public static void log(Level level, HttpSession session, String message,  Exception ex)
	{		
		Logger logger = Logger.getInstance();
		
		logger.log(Level.SEVERE, session.getAttribute(AccessControlManager.IP_ADDRESS) + "->" + session.getId(), message, ex);
	}
	
	public static void log(Level level, HttpSession session, String message)
	{		
		Logger logger = Logger.getInstance();
		
		logger.log(Level.SEVERE, session.getAttribute(AccessControlManager.IP_ADDRESS) + "->" + session.getId(), message);
	}
	
	public static void log(Level level, String message)
	{
		Logger logger = Logger.getInstance();
		
		logger.log(level, message);
	}
	
	public static void log(Level level, String message, Exception ex)
	{
		Logger logger = Logger.getInstance();
		
		logger.log(level, message, ex);
	}
}

