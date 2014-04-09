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

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserCategory implements IsSerializable
{
	private String _user_catergoryname = null;
	private int _user_categoryid = -1;
	private boolean _is_admin = false;
	private ArrayList<User> _users;

	public UserCategory()
	{
	}

	public UserCategory(String name, int userCategoryId, boolean isAdmin)
	{
		_user_catergoryname = name;
		_user_categoryid = userCategoryId;
		_is_admin = isAdmin;
	}

	public String getUserCategoryName()
	{
		return _user_catergoryname;
	}

	public void setUserCategoryName(String userCategoryName)
	{
		_user_catergoryname = userCategoryName;
	}

	public int getUserCategoryId()
	{
		return _user_categoryid;
	}

	public void setUserCategoryId(int userCategoryId)
	{
		_user_categoryid = userCategoryId;
	}

	public void setAdmin(boolean isAdmin)
	{
		_is_admin = isAdmin;
	}

	public boolean isAdmin()
	{
		return _is_admin;
	}
	
	public ArrayList<User> getUsers() 
	{
		return _users;
	}
	
	public void setUsers(ArrayList<User> users) 
	{
		_users = users;
	}
}
