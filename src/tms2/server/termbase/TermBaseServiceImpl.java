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

package tms2.server.termbase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.TermBaseService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.Result;
import tms2.shared.TermBase;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermBaseServiceImpl extends AccessControlledRemoteService implements TermBaseService
{
	private static final long serialVersionUID = 8225841933271909052L;
	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	@Override
	public ArrayList<TermBase> getAllDatabases(String authToken) throws TMSException 
	{
		HttpSession session = null;	
		Connection connection = null;
		ArrayList<TermBase> termbases = null;
		
		try 
		{		
			session = getCurrentUserSession(authToken);	
			
			if (session != null)
			{				
				connection = DatabaseConnector.getSuperUserConnectionFromPool();
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				termbases = TermBaseManager.getAllTermBases(connection, true);
				
				connection.commit();		
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "All termbases in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_db_retrieve(), e, authToken);
			throw new TMSException(e);
		} 
		finally
		{
			DatabaseConnector.closeConnection(connection, session,  _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return termbases;
	}

	@Override
	public String getEmailForTermbaseId(long termdbid) throws TMSException 
	{
		HttpSession session = null;		
		String address = null;
		Connection connection = null;
		
		try
		{		
			session = getCurrentUserSession();
						
			connection = DatabaseConnector.getConnectionFromPool(null);
			connection.setAutoCommit(false);
			
			long start = Calendar.getInstance().getTimeInMillis();
			
			address = TermBaseManager.getTermBaseEmail(connection, termdbid);
			
			connection.commit();
			
			long end = (Calendar.getInstance().getTimeInMillis() - start);
			
			LogUtility.log(Level.INFO, "Admin email retrieval in session " + session.getId() + " took " + end + " ms");
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
					LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_rollback(""), e1, null);
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			LogUtility.log(Level.SEVERE, session, _i18n.getMessages().log_db_retrieve_email(Long.toString(termdbid)), e, null);
			throw new TMSException(_i18n.getMessages().log_db_retrieve_email(Long.toString(termdbid)));
		} 
		finally
		{
			DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), null);
		}
		
		return address;
	}

	@Override
	public Result<TermBase> updateTermBase(String authToken, TermBase termbase) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<TermBase> result = new Result<TermBase>();
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
				
				System.out.println("updateTermBase connection: " + connection);
				
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				if (termbase.getTermdbid() > -1)
					is_updating = true;
				
				TermBase updated_termbase = TermBaseManager.updateTermBase(connection, termbase, session, authToken, is_updating);		
				
				connection.commit();
				
				System.out.println("updateTermBase connection done");
				
				if (updated_termbase == null)
					throw new TMSException();
				else
				{
					result.setResult(updated_termbase);
					if (is_updating)					
						result.setMessage(_i18n.getMessages().server_termbase_update_success(updated_termbase.getTermdbname()));
					else
						result.setMessage(_i18n.getMessages().server_termbase_create_success(updated_termbase.getTermdbname(), updated_termbase.getOwnername()));
				}
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Termbase update for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
				result.setMessage(_i18n.getMessages().server_termbase_update_fail(""));
			else
				result.setMessage(_i18n.getMessages().server_termbase_create_fail(""));
			
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
}
