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
import java.sql.SQLException;
import java.util.logging.Level;

import tms2.server.AppConfig;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.shared.User;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ConnectionPoolManager 
{	
	private Internationalization _i18n = Internationalization.getInstance();
	
	private static int SUPERROLE = 0;
	public static int USERROLE = 1;
	public static int GUESTROLE = 2;
	
	public static final String DRIVER = "org.postgresql.Driver";
	
	private BoneCP _super_conn_pool = null;	
	private BoneCP _guest_conn_pool = null;
	
	private boolean _is_poolssetup = false;
	
	private static ConnectionPoolManager _manager = null;
		
	public static ConnectionPoolManager getInstance()
	{
		if (_manager == null)
			_manager = new ConnectionPoolManager();
		
		return _manager;
	}
	
	private ConnectionPoolManager() 
	{
		
	}
	
	/**
	 * Create connection pools for 2 postgres roles
	 */
	public void configurePools()
	{
		try
		{
			LogUtility.log(Level.INFO, _i18n.getConstants().conn_pool_init());
			
			AppConfig config = AppConfig.getInstance();
						
			_super_conn_pool = configureConnectionPoolForType(getJdbcUrl(SUPERROLE),
															  config.getDBSuperRole(),
															  config.getDBPass());
						
			_guest_conn_pool = configureConnectionPoolForType(getJdbcUrl(GUESTROLE),							
															  config.getDBGuestRole(),
															  config.getDBPass());
			
			_is_poolssetup = true;
			
			LogUtility.log(Level.INFO, _i18n.getConstants().conn_pool_success());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			_is_poolssetup = false;
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_pool_fail(), ex);
		}						
	}
	
	public String getJdbcUrl(int code) throws Exception
	{		
		AppConfig config = AppConfig.getInstance();
		
		String format = "jdbc:postgresql://%1$s:%2$s/%3$s?user=%4$s&password=%5$s" +
		"&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
				
		if (code == SUPERROLE)
		{
			// Super role
			
			return String.format(format, 
								config.getDBHost(), 
								config.getDBPort(), 
								config.getDBName(), 
								config.getDBSuperRole(), 
								config.getDBPass());
		}
		else if (code == USERROLE)
		{
			// User role
			// This URL will not be configured for use in a connection pool
			return String.format(format, 
								 config.getDBHost(), 
								 config.getDBPort(), 
								 config.getDBName(), 
								 config.getDBUserRole(), 
								 config.getDBPass());
		}
		else
		{
			// Guest role
			
			return String.format(format, 
								 config.getDBHost(), 
								 config.getDBPort(), 
								 config.getDBName(), 
								 config.getDBGuestRole(), 
								 config.getDBPass());
		}		
	}
	
	/**
	 * Create a role specfic connection pool
	 * @param host
	 * @param port
	 * @param user
	 * @param pass
	 * @return
	 * @throws Exception
	 */
	private BoneCP configureConnectionPoolForType(String url, String user, String password) throws Exception
	{		
		Class.forName(DRIVER);
		
		BoneCPConfig config = new BoneCPConfig();
		
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);
		
		// Sets the minimum number of connections that will be contained in every partition. 
		config.setMinConnectionsPerPartition(3);
		
		// Sets the maximum number of connections that will be contained in every partition. Setting this 
		// to 5 with 3 partitions means you will have 15 unique connections to the database. Note that 
		// the connection pool will not create all these connections in one go but rather start off with 
		// minConnectionsPerPartition and gradually increase connections as required. 
		config.setMaxConnectionsPerPartition(5);
		
		// Default: 1, minimum: 1, recommended: 2-4 (but very app specific) 
		config.setPartitionCount(3);
		
		// Sets statementsCacheSize setting. The number of statements to cache. 
		config.setStatementsCacheSize(100);
		
		// Sets number of helper threads to create that will handle releasing a connection. 
		config.setReleaseHelperThreads(15);
		
		return new BoneCP(config);		
	}
	
	public boolean isPoolsSetup()
	{
		return _is_poolssetup;
	}
	
	/**
	 * Get the a connection based on a specific role
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(User user) throws SQLException
	{		
		Connection connection = null;
		
		// Check for guest role
		if (user == null || user.isGuest())							
			connection = _guest_conn_pool.getConnection();					
		else
		{
			if (user.isSuperUser())			
				connection = _super_conn_pool.getConnection();
		}
		
		return connection;
	}
	
	/**
	 * Shut down all connection pools.
	 */
	public void shutDown()
	{
		try
		{
			if (_super_conn_pool != null)
				_super_conn_pool.shutdown();
						
			if (_guest_conn_pool != null)
				_guest_conn_pool.shutdown();
			
			_is_poolssetup = false;
			
			_super_conn_pool = null;
			_guest_conn_pool = null;
			
			LogUtility.log(Level.INFO, _i18n.getConstants().conn_pool_shut_success());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().conn_pool_shut_fail(), e);
		}
	}
}
