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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.service.AppConfigService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.sql.StoredProcedureManager;
import tms2.server.util.StringLiteralEscapeUtility;
import tms2.shared.AppProperties;
import tms2.shared.Result;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AppConfigServiceImpl extends AccessControlledRemoteService implements AppConfigService 
{
	private static final long serialVersionUID = 5091104243484346713L;
	
	private static AppConfig _app_config = null;	
	private static Internationalization _i18n = Internationalization.getInstance();
		
	@Override
	public Result<AppProperties> validateConfig()
	{		
		Result<AppProperties> result = new Result<AppProperties>();
		
		try
		{					
			long start = Calendar.getInstance().getTimeInMillis();
			
			_app_config = AppConfig.getInstance();
			
			AppProperties app_properties = new AppProperties();
														
			String db_host = getDBHost();
			// check if there is a value for db_host
			if (db_host == null || db_host.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_host_null());				
				return result;
			}
			
			app_properties.setDBHost(db_host);
			
			String db_port = getDBPort();
			// check if there is a value for db_port
			if (db_port == null || db_port.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_port_null());
				return result;
			}
			
			app_properties.setDBPort(db_port);
			
			String db_super_role = getDBSuperRole();
			// check if there is a value for db_super_role
			if (db_super_role == null || db_super_role.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_user_null());
				return result;
			}
			
			app_properties.setDBSuperRole(db_super_role);
			
			String db_user_role = getDBUserRole();
			// check if there is a value for db_user_role
			if (db_user_role == null || db_user_role.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_user_null());
				return result;
			}
			
			app_properties.setDBUserRole(db_user_role);
			
			String db_guest_role = getDBGuestRole();
			// check if there is a value for db_guest_role
			if (db_guest_role == null || db_guest_role.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_user_null());
				return result;
			}
			
			app_properties.setDBGuestRole(db_guest_role);
			
			String db_pass = getDBPass();
			// check if there is a value for db_pass
			if (db_pass == null || db_pass.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().db_pass_null());
				return result;
			}
			
			app_properties.setDBPass(db_pass);
			
			String project_field = getProjectField(true);
			// check if there is a value for project_field
			if (project_field == null || project_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().project_field_null());
				return result;
			}
			
			app_properties.setProjectField(project_field);
			
			String sort_index_field = getSortIndexField(true);
			// check if there is a value for sort_index_field
			if (sort_index_field == null || sort_index_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().sort_index_field_null());
				return result;
			}
			
			app_properties.setSortIndexField(sort_index_field);
			
			String project_field_maxlength = _app_config.getProjectFieldMaxLength();
			app_properties.setProjectFieldMaxLength(Integer.parseInt(project_field_maxlength));
									
			String synonym_field = getSynonymField(true);
			// check if there is a value for synonym_field
			if (synonym_field == null || synonym_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().synonym_field_null());
				return result;
			}
			
			app_properties.setSynonymField(synonym_field);
			
			String context_field = getContextField(true);
			// check if there is a value for context_field
			if (context_field == null || context_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().context_field_null());
				return result;
			}
			
			app_properties.setContextField(context_field);
			
			String definition_field = getDefinitionField(true);
			// check if there is a value for definition_field
			if (definition_field == null || definition_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().definition_field_null());
				return result;
			}
			
			app_properties.setDefinitionField(definition_field);
			
			String note_field = getNoteField(true);
			// check if there is a value for note_field
			if (note_field == null || note_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().note_field_null());
				return result;
			}
			
			app_properties.setNoteField(note_field);
			
			String synonym_context_field = getSynonymContextField(true);
			// check if there is a value for synonym_context_field
			if (synonym_context_field == null || synonym_context_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().synonym_context_null());
				return result;
			}
			
			app_properties.setSynonymContextField(synonym_context_field);
			
			String synonym_note_field = getSynonymNoteField(true);
			// check if there is a value for synonym_context_field
			if (synonym_note_field == null || synonym_note_field.isEmpty())
			{
				result = new Result<AppProperties>(null, _i18n.getConstants().synonym_note_null());
				return result;
			}
			
			app_properties.setSynonymNoteField(synonym_note_field);
												
			result = new Result<AppProperties>(app_properties, _i18n.getConstants().app_properties());		
			
			long end = (Calendar.getInstance().getTimeInMillis() - start);
			
			LogUtility.log(Level.INFO, "Config validation took " + end + " ms");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getMessages().log_db_connect(""), e);
			result = new Result<AppProperties>(null, _i18n.getMessages().log_db_connect(""));	
		}
		catch (ClassNotFoundException c)
		{
			c.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getMessages().log_db_connector(""), c);
			result = new Result<AppProperties>(null, _i18n.getMessages().log_db_connector(""));	
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, _i18n.getConstants().app_properties_error(), e);
			e.printStackTrace();
			result = new Result<AppProperties>(null, _i18n.getConstants().app_properties_error());	
		}
		
		return result;		
	}
				
	private String getDBHost() throws Exception
	{		
		String db_host = null;
			
		db_host = _app_config.getDBHost();		
		if (db_host == null)
			return null;
				
		return db_host;		
	}
		
	private String getDBPort() throws Exception 
	{	
		String db_port = null;
							
		db_port = _app_config.getDBPort();
		if (db_port == null)
			return null;
		
		return db_port;		
	}
	
	private String getDBSuperRole() throws Exception 
	{			
		String db_user = null;
				
		db_user = _app_config.getDBSuperRole();
		if (db_user == null)
			return null;
				
		return db_user;		
	}
	
	private String getDBUserRole() throws Exception 
	{			
		String db_user = null;
				
		db_user = _app_config.getDBUserRole();
		if (db_user == null)
			return null;
				
		return db_user;		
	}
	
	private String getDBGuestRole() throws Exception 
	{			
		String db_user = null;
	
		db_user = _app_config.getDBGuestRole();
		if (db_user == null)
			return null;
				
		return db_user;		
	}
	
	private String getDBPass() throws Exception
	{		
		String db_pass = null;
					
		db_pass = _app_config.getDBPass();	
		if (db_pass == null)
			return null;
		
		return db_pass;		
	}
	
	private String getProjectField(boolean check_fieldname) throws Exception
	{
		String project_field = null;
				
		project_field = _app_config.getProjectField();
		if (project_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), project_field);
			if (! exists)
				return null;
		}
							
		return project_field;	
	}
	
	private String getSortIndexField(boolean check_fieldname) throws Exception 
	{
		String sort_index_field = null;
		
		sort_index_field = _app_config.getSortIndexField();
		if (sort_index_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), sort_index_field);
			if (! exists)
				return null;
		}
							
		return sort_index_field;	
	}
		
	private String getSynonymField(boolean check_fieldname) throws Exception 
	{
		String synonym_field = null;
		
		synonym_field = _app_config.getSynonym();
		if (synonym_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), synonym_field);
			if (! exists)
				return null;
		}
			
		return synonym_field;	
	}
	
	private String getContextField(boolean check_fieldname) throws Exception
	{
		String context_field = null;
			
		context_field = _app_config.getContext();
		if (context_field == null)
			return null;
		
		if(check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), context_field);
			if (! exists)
				return null;
		}
							
		return context_field;	
	}
	
	private String getDefinitionField(boolean check_fieldname) throws Exception
	{
		String definition_field = null;
			
		definition_field = _app_config.getDefinition();
		if (definition_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), definition_field);
			if (! exists)
				return null;
		}
							
		return definition_field;	
	}
	
	private String getNoteField(boolean check_fieldname) throws Exception
	{
		String note_field = null;
		
		note_field = _app_config.getNote();
		if (note_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), note_field);
			if (! exists)
				return null;
		}
		
		return note_field;	
	}
	
	private String getSynonymContextField(boolean check_fieldname) throws Exception
	{
		String synonym_context_field = null;
			
		synonym_context_field = _app_config.getSynonymContext();
		if (synonym_context_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), synonym_context_field);
			if (! exists)
				return null;
		}
		
		return synonym_context_field;
	}
	
	private String getSynonymNoteField(boolean check_fieldname) throws Exception
	{
		String synonym_note_field = null;
		
		synonym_note_field = _app_config.getSynonymNote();
		if (synonym_note_field == null)
			return null;
		
		if (check_fieldname)
		{
			boolean exists = fieldExists(getCurrentUserSession(), synonym_note_field);
			if (! exists)
				return null;
		}
		
		return synonym_note_field;
	}
	
	private boolean fieldExists (HttpSession session, String fieldName) throws Exception
	{	
		boolean field_exists = false;
		
		Connection connection = null;
		CallableStatement stored_procedure = null;
		ResultSet resultset = null;
		
		try
		{
			connection = DatabaseConnector.getSuperUserConnectionFromPool();
									
			String sql = "select fieldname from tms.fields where fieldname = '" + StringLiteralEscapeUtility.escapeStringLiteral(fieldName) + "'";
			
			connection.setAutoCommit(false);
			
			stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
			
			resultset = (ResultSet) stored_procedure.getObject(1);
			
			if (resultset.next())
				field_exists = true;
			
			connection.commit();						
		}
		catch(SQLException e)
		{
			if (connection != null)
			{
				try
				{
					connection.rollback();
				}
				catch (SQLException e1)
				{
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, null);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
		}
		finally
		{
			if (resultset != null)
				resultset.close();
			
			if (stored_procedure != null)
				stored_procedure.close();
			
			DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), null);
		}
		
		return field_exists;								
	}
}