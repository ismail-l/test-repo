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
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.connection.ConnectionPoolManager;
import tms2.server.logging.LogUtility;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SessionPoll implements ServletContextListener
{
	private static final int TIMER_SCHEDULE = 10000; // Check every 10 seconds
	
	private static ServletContext context = null;
				
	private void sessionPoll()
	{
		try
		{
			final Thread threadExpiredUsers = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						while (true)
						{							
							HashMap<String, HttpSession> current_sessions = ApplicationSessionCache.getUserSessionManager(context).getCurrentSessions();
							
							for (HttpSession session : current_sessions.values())
							{
								String authToken = (String)session.getAttribute(AccessControlManager.AUTH_TOKEN);
								
								if (authToken != null)
								{
									boolean is_expired = ApplicationSessionCache.getSessionCache(context).getUserIsExpired(authToken);
									
									// Check if this user is expired.							 
									if (is_expired)	
									{																						
										User user = AccessControlManager.getSignedOnUser(session, authToken);
										
										if (user != null)
											AccessControlManager.signOff(session, user, authToken);											
										else									
											SessionServiceImpl.invalidateSession(session);
									}
								}
							}
							
							Thread.sleep(TIMER_SCHEDULE);
						}
					}
					catch (InterruptedException e)
					{
						LogUtility.log(Level.SEVERE, "SessionListener Timer Interrupted.", e);												
					}
					
					return;
				}
        	});
			
			threadExpiredUsers.setPriority(Thread.MIN_PRIORITY);
			threadExpiredUsers.start();
		}
		catch(Exception e)
		{
			LogUtility.log(Level.SEVERE, "Error in the initialization of the ExpireUsers thread.", e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent ce)
	{	
		HashMap<String, HttpSession> current_sessions = ApplicationSessionCache.getUserSessionManager(context).getCurrentSessions();
		
		for (HttpSession session : current_sessions.values())
		{
			String authToken = (String)session.getAttribute(AccessControlManager.AUTH_TOKEN);
			
			if (authToken != null)
			{
				User user = AccessControlManager.getSignedOnUser(session, authToken);
				
				if (user != null)
					AccessControlManager.signOff(session, user, authToken);
				else
					SessionServiceImpl.invalidateSession(session);
			}
			else							
				SessionServiceImpl.invalidateSession(session);		
		}
		
		context = null;
		
		// Shut down all connection pools when context is
		// destroyed.
		ConnectionPoolManager.getInstance().shutDown();
	}

	@Override
	public void contextInitialized(ServletContextEvent ce)
	{
		context = ce.getServletContext();	
		
		ConnectionPoolManager.getInstance().configurePools();
		
		sessionPoll();
	}
}
