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

package tms2.server.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.exception.UserAuthenticationException;
import tms2.server.BCrypt;
import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.usercategory.UserCategoryManager;
import tms2.server.util.StringLiteralEscapeUtility;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class UserManager
{
	private static final Internationalization _i18n = Internationalization.getInstance();
	
	private static final int USERNAME_MIN_LENGTH = 6;
	private static final int PASSWORD_MIN_LENGTH = 6;
	private static final int USERNAME_MAX_LENGTH = 20;
	private static final int PASSWORD_MAX_LENGTH = 20;
	
	public static User createNewUser(Connection connection, User user, String password, String authToken, HttpSession session) throws TMSException, SQLException 
	{
		User createdUser = null;
		
		if (verifyUserExistence(connection, user.getUsername()))
			throw new TMSException(_i18n.getMessages().server_user_exist(user.getUsername()));

		validateUsername(user.getUsername().trim(), authToken, session);
		validatePassword(password.trim(), session, authToken);
		
		String hashed_passwd = BCrypt.hashpw(password, BCrypt.gensalt());
				
		CallableStatement stored_procedure = StoredProcedureManager.createUser(connection, user, hashed_passwd);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
			createdUser = getUser(connection, result);
				
		stored_procedure.close();
		
		return createdUser;				
	}
		
	public static ArrayList<User> getAllUsers(Connection connection) throws TMSException, SQLException
	{
		ArrayList<User> users = new ArrayList<User>();

		String sql = " SELECT * FROM tms.users, tms.usercategories " +
					 " WHERE users.usercategoryid = usercategories.usercategoryid";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		while (results.next())
		{
			User user = newUser(results);
			boolean is_admin = UserCategoryManager.getUserCategoryIsAdmin(connection, user.getUserCategoryId());
			user.setIsAdmin(is_admin);
			users.add(user);
		}

		results.close();
		stored_procedure.close();
		
		return users;
	}
	
	/**
	 * Retrieves all the users that belong to a certain specified user category.
	 * @param connection
	 * @param userCategoryId
	 * @return ArrayList<User> containing all the users belonging to the category identified by the ID specified.
	 * @throws RetrievalException
	 * @throws SQLException 
	 */
	public static ArrayList<User> getAllUsersByCategory(Connection connection, int userCategoryId) throws SQLException
	{		
		ArrayList<User> users = new ArrayList<User>();
				
		String sql = "SELECT * FROM tms.users WHERE users.usercategoryid = " + userCategoryId;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
			
		while (results.next())
		{
			User user = newUser(results);
			boolean is_admin = UserCategoryManager.getUserCategoryIsAdmin(connection, user.getUserCategoryId());
			user.setIsAdmin(is_admin);
			users.add(user);
		}
				
		results.close();
		stored_procedure.close();
				
		return users;
	}
	
	public static User getUser(Connection connection, long userid) throws SQLException 
	{		
		User user = null;
		
		String sql = "select * from tms.users where userid = " + userid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		if (results.next())
		{
			user = newUser(results);
			boolean is_admin = UserCategoryManager.getUserCategoryIsAdmin(connection, user.getUserCategoryId());
			user.setIsAdmin(is_admin);
		}
		
		results.close();
		stored_procedure.close();
				
		return user;
	}

	public static User getUserByName(Connection connection, String username) throws SQLException 		
	{
		User user = null;
			
		String sql = "select * from tms.users where username = '" + StringLiteralEscapeUtility.escapeStringLiteral(username) + "'";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
				
		if (results.next())
		{
			user = newUser(results);
			boolean is_admin = UserCategoryManager.getUserCategoryIsAdmin(connection, user.getUserCategoryId());
			user.setIsAdmin(is_admin);
		}
					
		results.close();
		stored_procedure.close();
		
		return user;
	}

	public static boolean verifyUserExistence(Connection connection, String username) throws SQLException 
	{
		long userid = -1;
		
		User user = getUserByName(connection, username);
		if (user != null)
			userid = user.getUserId();
				
		if (userid != -1) //Username already exists.
			return true;
		
		return false;
	}
	
	public static String getUserPassword(Connection connection, long userid) throws SQLException 
	{		
		String password = null;
			
		String sql = "select passwd from tms.users where userid = " + userid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet results = (ResultSet) stored_procedure.getObject(1);
		
		if (results.next())
			password = results.getString("passwd");
				
		results.close();
		stored_procedure.close();
				
		return password;
	}

	
	public static User updateUser(Connection connection, User user, String password, HttpSession session, String authToken, boolean is_updating) throws SQLException, UserAuthenticationException
	{
		User updated = null;
		String hash_passwd = null;
				
		if (password != null && !password.isEmpty())
		{
			validatePassword(password, session, authToken);
			hash_passwd = BCrypt.hashpw(password, BCrypt.gensalt());
		}
		else
		{		
			String sql = "select * from tms.sp_get_user_passwd (" + user.getUserId() + ")";
			CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
			
			ResultSet result = (ResultSet) stored_procedure.getObject(1);
			
			if (result.next())
				hash_passwd = result.getString("sp_get_user_passwd");
		
			result.close();
			stored_procedure.close();
		}
			
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = StoredProcedureManager.updateUser(connection, user, hash_passwd);
		else
			stored_procedure = StoredProcedureManager.createUser(connection, user, hash_passwd);
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)				
		{
			// NB: Update the user if logged in, in case access rights have changed.
			AccessControlManager.updateSignedOnUser(session, user);
			updated = getUser(connection, result);
		}				
		
		stored_procedure.close();
		
		return updated;
	}
		
	private static void validateUsername(String username, String authToken, HttpSession session) throws UserAuthenticationException
	{
		if (!username.matches("[a-zA-Z0-9]+"))
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_user_error_alpha(username));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_error_alpha(username), tms_ex, authToken);
			throw tms_ex;
		}
		
		if (username.length() < USERNAME_MIN_LENGTH) 
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_user_minlength(username, "" + USERNAME_MIN_LENGTH));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_minlength(username, "" + USERNAME_MIN_LENGTH), tms_ex, authToken);
			throw tms_ex;
		}
		
		if (username.length() > USERNAME_MAX_LENGTH) 
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_user_maxlength(username, "" + USERNAME_MAX_LENGTH));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_user_maxlength(username, "" + USERNAME_MAX_LENGTH), tms_ex, authToken);
			throw tms_ex;
		}
	}
	
	private static void validatePassword(String password, HttpSession session, String authToken) throws UserAuthenticationException
	{
		if (!password.matches("[a-zA-Z0-9]+"))
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_pass_alpha(""));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_pass_alpha(""), tms_ex, authToken);
			throw tms_ex;
		}
		
		if (password.length() < PASSWORD_MIN_LENGTH) 
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_pass_minlength("" + PASSWORD_MIN_LENGTH));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_pass_minlength("" + PASSWORD_MIN_LENGTH), tms_ex, authToken);
			throw tms_ex;
		}
		
		if (password.length() > PASSWORD_MAX_LENGTH) 
		{
			UserAuthenticationException tms_ex = new UserAuthenticationException(_i18n.getMessages().server_pass_maxlength("" + PASSWORD_MAX_LENGTH));
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_pass_maxlength("" + PASSWORD_MAX_LENGTH), tms_ex, authToken);
			throw tms_ex;
		}
	}
	
	private static User newUser(ResultSet results) throws SQLException
	{
		User user = new User();
		user.setActivated(results.getBoolean("activated"));
		user.setExpiryDate(results.getDate("expirydate"));
		user.setFirstName(results.getString("firstname"));
		user.setLastName(results.getString("lastname"));
		user.setLastSignOn(results.getDate("lastsignon"));
		user.setUserCategoryId(results.getInt("usercategoryid"));
		user.setUserId(results.getInt("userid"));
		user.setUsername(results.getString("username"));
		user.setGuest(false);
		
		// XXX: Is there a generic way to do this
		if (user.getUserCategoryId() == 3)
			user.setGuest(true);
				
		return user;
	}
}
