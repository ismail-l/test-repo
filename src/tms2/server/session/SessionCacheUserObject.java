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
import java.util.Date;

import tms2.shared.User;

public class SessionCacheUserObject
{
	private User _user = null;	
	private long _last_access = 0;
	private Connection _connection = null;
	
	private static final long TIMEOUT_INTERVAL = 7200000L; // Time out after two hours
	//private static final long TIMEOUT_INTERVAL = 60000;
	
	public SessionCacheUserObject()
	{
		updateLastAccess();
	}
	
	public void setUser(User user)
	{
		_user = user;
	}
	
	public void setConnection(Connection connection)
	{
		_connection = connection;
	}
	
	public Connection getConnection()
	{
		return _connection;		
	}
	
	public User getUser()
	{
		return getUser(true);
	}

	public User getUser(boolean updateAccess)
	{
		if (updateAccess)
			updateLastAccess();
		
		return _user;
	}

	public boolean isExpired()
	{
		long current_access = getTime();
					
		if ((current_access - _last_access) > TIMEOUT_INTERVAL)		
		{
			System.out.println("User expired.");
			return true;
		}
				
		return false;				
	}

	private long getTime()
	{
		return new Date().getTime();
	}
	
	private void updateLastAccess()
	{
		_last_access = getTime();
	}
}
