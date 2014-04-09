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

package tms2.server.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.shared.User;


/**
 * Class to initiate and connect to the database.
 * @author  Werner Liebenberg
 * @author  Martin Schlemmer
 * @author  Wildrich Fourie
 * @author  Ismail Lavangee
 */
public class DatabaseConnector
{			
	private static Internationalization _i18n = Internationalization.getInstance();
			
	public static Connection getConnection(User user)
	{
		Connection connection = null;
	
		try
		{									
			Class.forName(ConnectionPoolManager.DRIVER);
			
			ConnectionPoolManager manager = ConnectionPoolManager.getInstance();			
			connection = DriverManager.getConnection(manager.getJdbcUrl(ConnectionPoolManager.USERROLE));
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_error(), sql);
		}
		catch (ClassNotFoundException cnf)
		{
			cnf.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_error(), cnf);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_error(), e);
		}
		
		return connection;
	}
	
	public static Connection getConnectionFromPool(User user)
	{
		Connection connection = null;
		
		try
		{
			ConnectionPoolManager manager = ConnectionPoolManager.getInstance();
			if (! manager.isPoolsSetup())
				manager.configurePools();
			
			connection = manager.getConnection(user);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_error(), e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_error(), e);
		}
		
		return connection;
	}
		
	public static Connection getSuperUserConnectionFromPool() throws SQLException
	{
		User superuser = new User();
		superuser.setSuperUser(true);
		Connection connection = DatabaseConnector.getConnectionFromPool(superuser);
		
		return connection;
	}
	
	/**
	 * Releases the connection back into the pool or can close a connection if a
	 * signed on user has been signed off.
	 * @param connection
	 * @param session
	 * @param log_message
	 * @param authToken
	 */
	public static synchronized void closeConnection(Connection connection, HttpSession session, 
												    String log_message, String authToken)
	{
		try 
		{ 
			if (connection != null)					
				connection.close();			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			if (session != null)
				LogUtility.log(Level.SEVERE, session, log_message, e, authToken);
			else
				LogUtility.log(Level.SEVERE, log_message, e);
		}	
		finally
		{
			if (connection != null)
				connection = null;						
		}		
	}
}
