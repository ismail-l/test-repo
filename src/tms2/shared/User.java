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

package tms2.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class User implements IsSerializable
{
	private long _user_id = -1;
	private long _user_category_id = -1;	
	private String _username;
	private String _firstname;
	private String _lastname;
	private String _authtoken = null;
	private boolean _activated;
	private Date _expiry_date = null;
	private Date _last_signon = null;
	private String _session_id = null;
	private String _ip_address = null;
	private boolean _is_super_user = false;
	private boolean _guest = false;
	private boolean _admin = false;
	
	public User() {}
	
	public User(String username, long userId)
	{
		_username = username;
		_user_id = userId;
	}

	public long getUserId() 
	{
		return _user_id;
	}

	public void setUserId(long userId) 
	{
		_user_id = userId;
	}

	public long getUserCategoryId() 
	{
		return _user_category_id;
	}

	public void setUserCategoryId(long userCategoryId) 
	{
		_user_category_id = userCategoryId;
	}
		
	public String getUsername() 
	{
		return _username;
	}

	public void setUsername(String username) 
	{
		_username = username;
	}

	public String getFirstName() 
	{
		return _firstname;
	}

	public void setFirstName(String firstName) 
	{
		_firstname = firstName;
	}

	public String getLastName() 
	{
		return _lastname;
	}

	public void setLastName(String lastName) 
	{
		_lastname = lastName;
	}
	
	public String getAuthToken() 
	{
		return _authtoken;
	}

	public void setAuthToken(String authToken) 
	{
		_authtoken = authToken;
	}

	public boolean isActivated() 
	{
		return _activated;
	}

	public void setActivated(boolean activated) 
	{
		_activated = activated;
	}

	public Date getExpiryDate() 
	{
		return _expiry_date;
	}

	public void setExpiryDate(Date expiryDate) 
	{
		_expiry_date = expiryDate;
	}

	public Date getLastSignOn() 
	{
		return _last_signon;
	}

	public void setLastSignOn(Date lastSignOn) 
	{
		_last_signon = lastSignOn;
	}

	public String getFullName()
	{
		return getFirstName() + " " + getLastName();
	}
	
	public String getSessionId() 
	{
		return _session_id;
	}

	public void setSessionId(String sessionId) 
	{
		_session_id = sessionId;
	}

	public void setGuest(boolean guest)
	{
		_guest = guest;
	}

	public boolean isGuest()
	{
		return _guest;
	}
		
	public void setIPAddress(String ip_address)
	{
		_ip_address = ip_address;
	}
	
	public String getIPAddress()
	{
		return _ip_address;
	}
	
	public void setSuperUser(boolean super_user)
	{
		_is_super_user = super_user;
	}
	
	public boolean isSuperUser()
	{
		return _is_super_user;
	}
	
	public void setIsAdmin(boolean is_admin)
	{
		_admin = is_admin;
	}
	
	public boolean isAdmin()
	{
		return  _admin;
	}
	
	@Override
	public boolean equals(Object user)
	{
		User comparable = (User) user;
		
		if (_activated == comparable._activated &&
			_guest == comparable._guest &&						
			_firstname.equalsIgnoreCase(comparable._firstname) &&
			_lastname.equalsIgnoreCase(comparable._lastname) &&						
			_user_category_id == comparable._user_category_id &&
			_user_id == comparable._user_id &&
			_username.equalsIgnoreCase(comparable._username))
			return true;
		
		return false;
	}
}
