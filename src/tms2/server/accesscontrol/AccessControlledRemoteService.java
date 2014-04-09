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

import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.AccessControlException;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.User;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessControlledRemoteService extends RemoteServiceServlet 
{
	private static final long serialVersionUID = 22803479730443004L;
	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	protected boolean isUserSignedOn(HttpSession session, String authToken) throws AccessControlException
	{
		return AccessControlManager.isUserStillSignedOn(session, authToken);		
	}
	
	protected User getSignedOnUser(HttpSession session, String authToken) throws AccessControlException
	{
		return AccessControlManager.getSignedOnUser(session, authToken);
	}
	
	protected void validateUserIsSignedOn(HttpSession session, String authToken) throws AccessControlException
	{
		AccessControlManager.validateUserIsSignedOn(session, authToken);
	}
	
	protected void validateUserHasAdminRights(HttpSession session, String authToken) throws AccessControlException
	{
		AccessControlManager.validateUserHasAdminRights(session, authToken);
	}
	
	protected HttpSession getCurrentUserSession() throws AccessControlException
	{
		return getSession();
	}
	
	protected HttpSession getCurrentUserSession(String authToken) throws AccessControlException
	{
		if (authToken != null)
			return ApplicationSessionCache.getSessionInSessionCache(getSession(), authToken);
		else
			return getCurrentUserSession();
	}
	
	private HttpSession getSession() throws AccessControlException
	{
		HttpSession session = null;
		
		try
		{			
			session = getThreadLocalRequest().getSession(true);
		}
		catch (Exception e)
		{
			LogUtility.log(Level.SEVERE, _i18n.getConstants().server_ac_error_session(), e);
			throw new AccessControlException(_i18n.getConstants().server_ac_error_session() + ": " + e.getMessage());
		}
		
		if (session == null)
		{
			LogUtility.log(Level.SEVERE, _i18n.getConstants().server_ac_error_session());
			throw new AccessControlException(_i18n.getConstants().server_ac_error_session() + ".");
		}
		
		return session;
	}

}
