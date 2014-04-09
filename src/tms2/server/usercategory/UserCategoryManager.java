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
package tms2.server.usercategory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import tms2.server.sql.StoredProcedureManager;
import tms2.server.user.UserManager;
import tms2.shared.User;
import tms2.shared.UserCategory;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserCategoryManager
{		
	/**
	 * Retrieves all the user categories including the users the belong to each.
	 * Used by the User & Categories panel in the Administration Interface.
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
	public static ArrayList<UserCategory> getAllUserCategoriesWithUsers(Connection connection, HttpSession session, String authToken) throws SQLException 
	{		
		ArrayList<UserCategory> userCategories = new ArrayList<UserCategory>();
				
		String sql = "select * from tms.usercategories";
							
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			int userCategoryId = results.getInt("usercategoryid");
			ArrayList<User> users = UserManager.getAllUsersByCategory(connection, userCategoryId);
			UserCategory category = newUserCategoryWithUsers(results, users);
			userCategories.add(category);
		}
				
		results.close();
		stored_procedure.close();	
		
		return userCategories;
	}
	
	public static UserCategory getUserCategoryByUserCategoryId(Connection connection, long userCategoryId) throws  SQLException
	{		
		UserCategory userCategory = null;
			
		String sql = "select * from tms.usercategories where usercategoryid = " + userCategoryId;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		if (results.next())
			userCategory = newUserCategory(results);
						
		results.close();
		stored_procedure.close();
		
		return userCategory;
	}
	
	public static boolean getUserCategoryIsAdmin(Connection connection, long userCategoryId) throws SQLException
	{		
		boolean isAdmin = false;
				
		String sql = "select isadmin from tms.usercategories where usercategoryid = " + userCategoryId;
				
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		if (results.next())
			isAdmin = results.getBoolean("isadmin");
				
		results.close();
		stored_procedure.close();
				
		return isAdmin;
	}
		
	public static UserCategory updateUserCategory(Connection connection, UserCategory userCategory, boolean is_updating) throws  SQLException 
	{
		UserCategory updated = null;
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = StoredProcedureManager.updateUserCategory(connection, userCategory);			
		else			
			stored_procedure = StoredProcedureManager.createUserCategory(connection, userCategory);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			updated = getUserCategoryByUserCategoryId(connection, result);		
		
		stored_procedure.close();
		
		return updated;
	}

	public static void deleteUserCategory(Connection connection, UserCategory userCategory, String authToken, HttpSession session) throws SQLException 
	{
		String sql = "delete from tms.usercategories where usercategoryid = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, userCategory.getUserCategoryId());
		statement.executeUpdate();
	}
	
	private static UserCategory newUserCategory(ResultSet results) throws SQLException
	{
		UserCategory userCategory = new UserCategory();
		userCategory.setUserCategoryId(results.getInt("usercategoryid"));
		userCategory.setUserCategoryName(results.getString("usercategory"));
		userCategory.setAdmin(results.getBoolean("isadmin"));
		
		return userCategory;
	}
	
	// Create a UserCategory instance, with all the users belonging to this category, using the result set provided.
	private static UserCategory newUserCategoryWithUsers(ResultSet results, ArrayList<User> users) throws SQLException
	{
		UserCategory userCategory = new UserCategory();
		userCategory.setUserCategoryId(results.getInt("usercategoryid"));
		userCategory.setUserCategoryName(results.getString("usercategory"));
		userCategory.setAdmin(results.getBoolean("isadmin"));
		userCategory.setUsers(users);
		
		return userCategory;
	}
}
