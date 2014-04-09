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

package tms2.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import tms2.server.logging.LogUtility;

public class AppConfig 
{
	private final static String DEFAULT_PROPERTIES = "AppConfig.properties";

	private static AppConfig _config;
	private static Properties _properties;

	static public AppConfig getInstance() throws Exception 
	{
		try 
		{
			if (_config == null)
				_config = new AppConfig();
		} 
		catch (Exception e) 
		{
			_config = null;

			e.printStackTrace();
		}

		if (_config == null)
		{
			LogUtility.log(Level.SEVERE, "Failed to instanciate AppConfig");
			throw new Exception("Failed to instanciate AppConfig");
		}

		return _config;
	}

	public AppConfig() throws Exception 
	{
		if (_properties != null)
			return;

		InputStream is = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		_properties = new Properties();

		// Try to find the default configuration file in the ClassPath
		is = loader.getResourceAsStream(DEFAULT_PROPERTIES);

		if (is != null) 
		{
			try 
			{
				_properties.load(is);
			} 
			catch (Exception e) 
			{
				LogUtility.log(Level.SEVERE, "Failed to load properties", e);
				_properties = null;
				
				throw e;
			}
		}
		else 
		{
			try 
			{
				// Now load the override properties in the WEB-INF directory
				is = new FileInputStream("WEB-INF/" + DEFAULT_PROPERTIES);
	
				_properties.load(is);
			} 
			catch (Exception e)
			{
				LogUtility.log(Level.SEVERE, "Failed to load properties", e);
				_properties = null;
	
				throw e;
			}
		}
	}

	public String getDBHost() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_host");
	}

	public String getDBPort() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_port");
	}

	public String getDBSuperRole() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_superole");
	}
	
	public String getDBUserRole() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_user_role");
	}
	
	public String getDBGuestRole() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_guest_role");
	}
	
	public String getDBPass() 
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("db_pass");
	}
		
	public String getProjectField()
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("project_field");
	}
		
	public String getProjectFieldMaxLength()
	{
		if (_properties == null)
			return "";

		return _properties.getProperty("project_field_maxlength");
	}
	
	public String getSortIndexField()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("sort_index_field");
	}
	
	public String getSynonym()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("synonym_field");
	}
	
	public String getContext()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("context_field");
	}
	
	public String getDefinition()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("definition_field");
	}
	
	public String getNote()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("note_field");
	}
	
	public String getSynonymContext()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("synonym_context_field");
	}
	
	public String getSynonymNote()
	{
		if (_properties == null)
			return null;

		return _properties.getProperty("synonym_note_field");
	}
	
	public String getDBName() 
	{
		if (_properties == null)
			return null;
		return _properties.getProperty("db_name");
	}
	
}
