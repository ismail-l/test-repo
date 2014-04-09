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

package tms2.server.record;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.RecordService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Field;
import tms2.shared.Filter;
import tms2.shared.Record;
import tms2.shared.Result;
import tms2.shared.User;
import tms2.shared.wrapper.RecordDetailsWrapper;
import tms2.shared.wrapper.RecordRecordElementWrapper;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordServiceImpl extends AccessControlledRemoteService implements RecordService
{
	private static final long serialVersionUID = 1715160921454215290L;
	
	private static Internationalization _i18n = Internationalization.getInstance();
		
	@Override
	public Result<RecordDetailsWrapper> resetBrowser(String authToken, Field field, long current_recordid, boolean refresh) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<RecordDetailsWrapper> result = new Result<RecordDetailsWrapper>();
		
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
				
				System.out.println("resetBrowser connection: " + connection);
												
				connection.setAutoCommit(false);
				
				RecordDetailsWrapper record_details = RecordManager.resetBrowser(connection, session, user, field, current_recordid, refresh);
							
				connection.commit();
				
				System.out.println("resetBrowser connection done");
				
				if (record_details == null)
					throw new TMSException();
				else			
					result.setResult(record_details);	
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
			
			result.setMessage( _i18n.getConstants().lob_retrieve_first_record());
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
	public Result<Record> getPreviousRecord(String authToken) throws TMSException
	{
		HttpSession session = null;
		User user = null;		
		Connection connection = null;
		Result<Record> result = new Result<Record>();
		
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
				
				System.out.println("getPreviousRecord connection: " + connection);
				
				connection.setAutoCommit(false);
				
				Record record = RecordManager.getPreviousRecord(connection, session, user);
				
				connection.commit();
				
				System.out.println("getPreviousRecord connection done");
				
				if (record == null)
					throw new TMSException();
				else			
					result.setResult(record);		
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
			
			result.setMessage( _i18n.getConstants().log_retrieve_previous_record());
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
	public Result<Record> getNextRecord(String authToken) throws TMSException
	{
		HttpSession session = null;
		User user = null;	
		Connection connection = null;
		Result<Record> result = new Result<Record>();
		
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
				
				System.out.println("getNextRecord connection: " + connection);
				
				connection.setAutoCommit(false);
				
				Record record = RecordManager.getNextRecord(connection, session, user);
				
				connection.commit();
				
				System.out.println("getNextRecord connection done");
				
				if (record == null)
					throw new TMSException();
				else			
					result.setResult(record);		
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
			
			result.setMessage( _i18n.getConstants().log_retrieve_next_record());
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
	public Result<Record> getLastRecord(String authToken) throws TMSException
	{
		HttpSession session = null;
		User user = null;	
		Connection connection = null;
		Result<Record> result = new Result<Record>();
		
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
				
				System.out.println("getLastRecord connection: " + connection);
				
				connection.setAutoCommit(false);
				
				Record record = RecordManager.getLastRecord(connection, session, user);
				
				connection.commit();
				
				System.out.println("getLastRecord connection done");
				
				if (record == null)
					throw new TMSException();
				else			
					result.setResult(record);	
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
			
			result.setMessage( _i18n.getConstants().log_retrieve_last_record());
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
	public Result<Long> updateRecord(String authToken, Record record, boolean is_archiving) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<Long> result = new Result<Long>();
		boolean is_updating = false;
		
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
				
				System.out.println("updateRecord connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (record.getRecordId() > -1 && ! is_archiving)
					is_updating = true;
				
				long recordid = -1;
							
				if (! is_archiving)
					recordid = RecordManager.updateRecord(connection, user, record, is_updating);
				else
					recordid = RecordManager.archiveRecord(connection, user, record);
				
				connection.commit();
				
				System.out.println("updateRecord connection done");
				
				if (recordid == -1)
					throw new TMSException();
				else	
				{
					if (is_archiving)
						result.setMessage(_i18n.getMessages().server_record_delete_success(""));
					else 
					{
						if (is_updating)
							result.setMessage(_i18n.getMessages().server_record_update_success(""));
						else
							result.setMessage(_i18n.getMessages().server_record_save_success(""));
					}
					
					result.setResult(recordid);
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Record update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			if (is_archiving)
				result.setMessage(_i18n.getMessages().server_record_delete_fail(""));
			else
			{
				if (is_updating)
					result.setMessage(_i18n.getMessages().server_record_update_fail(""));
				else
					result.setMessage(_i18n.getMessages().server_record_save_fail(""));
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
	public Result<RecordRecordElementWrapper> searchRecords(String authToken, Field field, String search_prompt, String search_type, boolean browse_textbox_search, long termbaseid, long projectid) throws TMSException 
	{
		HttpSession session	= null;
		User signeduser = null;
		Connection connection = null;
		Result<RecordRecordElementWrapper> result = new Result<RecordRecordElementWrapper>();
		
		try
		{
			session = getCurrentUserSession(authToken);	
			
			if (session != null)
			{
				signeduser = getSignedOnUser(session, authToken);
				
				if (signeduser == null || signeduser.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(signeduser);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("searchRecords connection: " + connection);
				
				connection.setAutoCommit(false);
				
				RecordRecordElementWrapper wrapper = null;
					
				Filter filter = (Filter)session.getAttribute("filter");
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (search_type.equals(_i18n.getConstants().search_exactmatch()))
					wrapper = TermManager.searchExactMatch(connection, session, signeduser, field, filter, search_prompt, browse_textbox_search, termbaseid, projectid);
				else if (search_type.equals(_i18n.getConstants().search_fuzzy()))
					wrapper = TermManager.searchFuzzy(connection, session, signeduser, field, filter, search_prompt, termbaseid, projectid);
				else if (search_type.equals(_i18n.getConstants().search_wildcard()))
					wrapper = TermManager.searchWildCard(connection, session, signeduser, field, filter, search_prompt, termbaseid, projectid);
				else if (search_type.equals(_i18n.getConstants().search_default() + "..."))
					wrapper = TermManager.searchWildCard(connection, session, signeduser, field, filter, search_prompt + "*", termbaseid, projectid);
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
							
				LogUtility.log(Level.INFO, search_type + " for session " + session.getId() + " took "  + end + " ms");
				
				connection.commit();
				
				System.out.println("searchRecords connection done");
				
				if (wrapper == null)
					throw new TMSException();
				else
					result.setResult(wrapper);
			}
		}
		catch(Exception e)
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
			
			result.setMessage(_i18n.getConstants().log_search());
			LogUtility.log(Level.SEVERE, session, result.getMessage(), e, authToken);
			throw new TMSException(result.getMessage());
		}
		finally
		{
			if (signeduser == null || signeduser.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return result;
	}

	@Override
	public void unlockRecord(String authToken, long recordid)throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		
		try
		{
			session = getCurrentUserSession(authToken);	
				
			if (session != null)
			{
				user  = getSignedOnUser(session, authToken);
				
				if (user == null || user.isGuest())
					connection = DatabaseConnector.getConnectionFromPool(user);
				else
					connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
				
				System.out.println("unlockRecord connection: " + connection);
				
				connection.setAutoCommit(false);
					
				long start = Calendar.getInstance().getTimeInMillis();
				
				RecordManager.unlockRecord(connection, recordid);
				
				connection.commit();	
				
				System.out.println("unlockRecord connection done");
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Record unlock for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session,  _i18n.getMessages().log_unlock_rec(Long.toString(recordid)), e, authToken);
			throw new TMSException(_i18n.getMessages().log_unlock_rec(Long.toString(recordid)));
		}	
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
	}
}
