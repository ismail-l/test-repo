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

package tms2.server.field;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.FieldService;
import tms2.server.AppConfig;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Field;
import tms2.shared.Field.FieldType;
import tms2.shared.PresetField;
import tms2.shared.Result;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldServiceImpl extends AccessControlledRemoteService implements FieldService 
{
	private static final long serialVersionUID = 1008340411569742063L;
	
	private Internationalization _i18n = Internationalization.getInstance();
	
	@Override
	public ArrayList<Field> getAllFields(String authToken) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		ArrayList<Field> fields = new ArrayList<Field>();
						
		try
		{	
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				user = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
					
				System.out.println("getAllFields connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				fields = FieldManager.getAllFields(connection);								
				
				connection.commit();
				
				System.out.println("getAllFields connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "All fields in session " + session.getId() + " took " +  end + " ms");
			}
		}
		catch (Exception e)
		{
			if (connection != null)
			{
				try
				{
					connection.rollback();
				}
				catch (SQLException e1)
				{
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, authToken);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_fields(), e, authToken);
			throw new TMSException(e);
		}
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
					
		return fields;
	}
	
	@Override
	public ArrayList<Field> getFieldsAsPerFieldType(String authToken, FieldType field_type) throws TMSException 
	{		
		ArrayList<Field> fields = getAllFields(authToken);
		
		if (fields == null || fields.size() == 0)
			return null;
		
		ArrayList<Field> returned_fields = new ArrayList<Field>();
		
		if (field_type.getFieldTypeName().equals("Text Record Field"))
		{						
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isRecordAttribute())
					returned_fields.add(field);
			}
			
		}
		else if (field_type.getFieldTypeName().equals("Index Field"))
		{			
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isIndexField())
					returned_fields.add(field);
			}
		}
		else if (field_type.getFieldTypeName().equals("Text Attribute Field") || 
				 field_type.getFieldTypeName().equals("Synonym Field"))
		{			
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isFieldAttribute() || field.isSynonymField())
					returned_fields.add(field);
			}
		}
		else if (field_type.getFieldTypeName().equals("Text SubAttribute Field"))
		{			
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isFieldSubAttribute())
					returned_fields.add(field);
			}
		}
		else if (field_type.getFieldTypeName().equals("Preset Attribute Field"))
		{			
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isPresetAttribute())
					returned_fields.add(field);
			}
		}
		else if (field_type.getFieldTypeName().equals("Preset SubAttribute Field"))
		{			
			Iterator<Field> iter = fields.iterator();
			while (iter.hasNext())
			{
				Field field = iter.next();
				if (field.isPresetSubAttribute())
					returned_fields.add(field);
			}
		}
		
		return returned_fields;
	}

	@Override
	public ArrayList<Field> getIndexFieldsInUse(String authToken) throws TMSException 
	{			
		HttpSession session = null;		
		ArrayList<Field> indexfields = new ArrayList<Field>();

		// Currently this is only used for the source/target ListBoxes, 
		// it should not be admin restricted, and it also should not return
		// an empty list for guest users.
						
		Connection connection = null;
		
		try
		{								
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				connection = DatabaseConnector.getSuperUserConnectionFromPool();
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				ArrayList<Field> fields = FieldManager.getAllFields(connection);
				
				Iterator<Field> iter = fields.iterator();
				while (iter.hasNext())
				{
					Field field = iter.next();
					if (field.isIndexField() && field.isInuse())
						indexfields.add(field);
				}
								
				// If this list is empty, get the default sort 
				// index field.
				if (indexfields.size() == 0)
				{
					AppConfig config = AppConfig.getInstance();
					
					iter = fields.iterator();
					while (iter.hasNext())
					{
						Field field = iter.next();
												
						if (field.isSortIndex(config.getSortIndexField()))
						{
							indexfields.add(field);
							break;							
						}
					}
				}
				
				connection.commit();
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
							
				LogUtility.log(Level.INFO, "Index fields in use in session " + session.getId() + " took " + end + " ms");
			}
		}
		catch (Exception e) 
		{
			if (connection != null)
			{
				try
				{
					connection.rollback();
				}
				catch (SQLException e1)
				{
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, authToken);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().server_fields_error_usedMore(""), e, authToken);
			throw new TMSException(_i18n.getMessages().server_fields_error_usedMore(""), e);
		}
		finally
		{
			DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return indexfields;
	}

	@Override
	public Result<Field> updateField(String authToken, Field field) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<Field> result = new Result<Field>();
		boolean is_updating = false;
				
		try 
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				validateUserHasAdminRights(session, authToken);
				
				user = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("updateField connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (! (field instanceof PresetField))
				{
					if (field.getFieldId() > -1)
						is_updating = true;
				}
				else
				{
					if (((PresetField) field).getPresetattributeid() > -1)
						is_updating = true;
				}
				
				Field updated_field = null;
				
				if (!(field instanceof PresetField))
					updated_field = FieldManager.updateField(connection, field, session, is_updating);
				else
					updated_field = FieldManager.updatePresetField(connection, (PresetField) field, is_updating);
				
				System.out.println("updateField connection done");
				
				connection.commit();
				
				if (updated_field == null)
					throw new TMSException();
				else
				{
					result.setResult(updated_field);
					if (is_updating)
					{
						if (! (updated_field instanceof PresetField))
							result.setMessage(_i18n.getMessages().server_fields_update_success(updated_field.getFieldName()));
						else
							result.setMessage(_i18n.getMessages().server_pa_update_success(((PresetField) updated_field).getPresetFieldName()));
					}
					else
					{
						if (! (updated_field instanceof PresetField))
							result.setMessage(_i18n.getMessages().server_fields_create_success(updated_field.getFieldName()));
						else
							result.setMessage(_i18n.getMessages().server_pa_create_success(((PresetField) updated_field).getPresetFieldName()));
					}
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Field update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
			}
		} 
		catch (Exception e) 
		{
			if (connection != null)
			{
				try
				{
					connection.rollback();
				}
				catch (SQLException e1)
				{
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, authToken);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			if (is_updating)
			{
				if (! (field instanceof PresetField))				
					result.setMessage(_i18n.getMessages().server_fields_update_fail(""));
				else
					result.setMessage(_i18n.getMessages().server_pa_update_fail(""));
			}
			else
			{
				if (! (field instanceof PresetField))				
					result.setMessage(_i18n.getMessages().server_fields_create_fail(""));
				else
					result.setMessage(_i18n.getMessages().server_pa_create_fail(""));
			}
			
			LogUtility.log(Level.SEVERE, session, result.getMessage(), e, authToken);
			throw new TMSException(result.getMessage());			
		} 
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return result;
	}

	@Override
	public String getFieldDataMimeType(String authToken, String dataUrl) throws TMSException 
	{
		HttpSession session = null;
		String mime_type = null;
		
		try
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				validateUserIsSignedOn(session, authToken);	
				
				String filename = getServletContext().getRealPath(dataUrl);
				mime_type = FieldManager.getMimeType(filename);
				
				if (mime_type == null)
					throw new TMSException(_i18n.getConstants().log_mimetypes());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_mimetypes(), e, authToken);
		}
		
		return mime_type;				
	}
}
