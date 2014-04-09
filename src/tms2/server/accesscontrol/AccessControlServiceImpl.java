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

import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.AccessControlException;
import tms2.client.service.AccessControlService;
import tms2.server.BCrypt;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.shared.User;

public class AccessControlServiceImpl extends AccessControlledRemoteService implements AccessControlService
{
	private static final long serialVersionUID = -148076217045507941L;
	
	private static Internationalization _i18n = Internationalization.getInstance();
		
	@Override
	public User signOn(String username, String password) throws AccessControlException
	{
		HttpSession session = getCurrentUserSession();
		
		if (session != null)
		{
			String authToken = BCrypt.hashpw(session.getId(), BCrypt.gensalt());
			
			User user = AccessControlManager.retrieveUserByUsername(session, username, password, authToken);
			
			User olduser = AccessControlManager.findDuplicateSignedOnUser(session, user, authToken);
			
			if (olduser != null)
			{				
				HttpSession user_session = getCurrentUserSession(olduser.getAuthToken());
								
				AccessControlManager.signOff(user_session, olduser, olduser.getAuthToken());
			}
						
			return AccessControlManager.signOn(session, authToken, user, getThreadLocalRequest().getRemoteAddr());
		}
		
		return null;
	}

	@Override
	public void signOff(String authToken) throws AccessControlException
	{
		HttpSession session = getCurrentUserSession(authToken);
		
		if (session != null)
		{
			User user = AccessControlManager.getSignedOnUser(session, authToken);
			
			try
			{	
				if (user != null)
					AccessControlManager.signOff(session, user, authToken);			
			}
			catch (Exception e)
			{
				LogUtility.log(Level.SEVERE, session, user.getFullName() + "->" + user.getIPAddress() + "->" + session.getId(), _i18n.getMessages().log_session_signOff(""));
				throw new AccessControlException(_i18n.getMessages().log_session_signOff(""));
			}
		}
	}
	
	@Override
	public void signOffUser(String authToken, String userToken) throws AccessControlException
	{
		HttpSession session = getCurrentUserSession(authToken);
			
		if (session != null)
		{
			User user = AccessControlManager.getSignedOnUser(session, userToken);
			
			try
			{
				validateUserHasAdminRights(session, authToken);
														
				HttpSession user_session = getCurrentUserSession(userToken);
							
				AccessControlManager.signOff(user_session, user, userToken);	
			}
			catch(Exception e)
			{
				LogUtility.log(Level.SEVERE, session, user.getFullName() + "->" + user.getIPAddress() + "->" + session.getId(), _i18n.getMessages().log_session_signOff(""));
				throw new AccessControlException(_i18n.getMessages().log_session_signOff(""));
			}
		}
	}
		
	@Override
	public User findSignedOnUser(String authToken) throws AccessControlException
	{
		HttpSession session = getCurrentUserSession(authToken);
		
		if (session != null)
		{			
			if (authToken == null)
				authToken = (String) session.getAttribute(AccessControlManager.AUTH_TOKEN);
			
			if (authToken == null)
				return null;
			else
				return AccessControlManager.getSignedOnUser(session, authToken);	
		}
		else
			return null;
	}
	
	@Override
	public Vector<User> getSignedOnUsers(String authToken) throws AccessControlException
	{		
		HttpSession session = getCurrentUserSession(authToken);
		Vector<User> online_users = new Vector<User>();
		
		if (session != null)			
			online_users = AccessControlManager.getAllRegisteredUsers(session);		
		
		return online_users;
	}
	
	@Override
	public boolean isUserStillSignedOn(String authToken) throws AccessControlException
	{
		HttpSession session = getCurrentUserSession(authToken);
		
		if (session == null) // This user could have been logged out remotely
			return false;
		else
			// NB: Do not update last accessed time.
			return AccessControlManager.isUserStillSignedOn(session, authToken, false);
	}
}
