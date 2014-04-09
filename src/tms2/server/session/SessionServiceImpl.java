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

import java.util.Enumeration;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.SessionService;
import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.logging.LogUtility;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SessionServiceImpl extends AccessControlledRemoteService implements SessionService
{
	private static final long serialVersionUID = 7556373975361783173L;	
			
	@Override
	public void reset(String authToken) throws TMSException 
	{
		HttpSession session = getCurrentUserSession(authToken);
		
		if (session != null)
			reset(session);		
	}
	
	public synchronized static void reset(HttpSession session)
	{
		// Clear all except UserAuthToken and IP.				
		
		Enumeration<String> attributes = session.getAttributeNames();
		while (attributes.hasMoreElements())
		{
			String attribute_name = attributes.nextElement();
			if (! attribute_name.equalsIgnoreCase(AccessControlManager.AUTH_TOKEN) && 
				! attribute_name.equalsIgnoreCase(AccessControlManager.IP_ADDRESS))				
			{
				if (session.getAttribute(attribute_name) != null)
				{
					System.out.println("Removing session attribute: " + attribute_name);
					session.removeAttribute(attribute_name);
				}
			}
		}
		
		LogUtility.log(Level.INFO, "Session: " + session.getId() + " reset");
	}
		
	@Override
	public void invalidate(String authToken) throws TMSException 
	{
		HttpSession session = getCurrentUserSession(authToken);
		
		if (session != null)
		{
			if (authToken == null)
				invalidateSession(session);
			else
			{
				User user = AccessControlManager.getSignedOnUser(session, authToken);
				
				if (user != null)
					AccessControlManager.signOff(session, user, authToken);
			}
		}
	}
	
	/**
	 * Invalidate a session
	 * @param session
	 */
	public static void invalidateSession(HttpSession session)
	{
		if (session != null)
		{	
			LogUtility.log(Level.INFO, "Session: " + session.getId() + " invalidated");
			
			session.invalidate();						
		}
	}
}
