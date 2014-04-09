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

package tms2.server.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.FilterService;
import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.field.FieldManager;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.server.termbase.TermBaseManager;
import tms2.server.user.UserManager;
import tms2.shared.Filter;
import tms2.shared.InputModel;
import tms2.shared.TermBase;
import tms2.shared.User;
import tms2.shared.wrapper.FilterDetailsWrapper;

/**
 * 
 * @author I. Lavangee
 *
 */
@SuppressWarnings("unused")
public class FilterServiceImpl extends AccessControlledRemoteService implements FilterService
{
	private static final long serialVersionUID = -5302418837736398960L;
	
	private static Internationalization _i18n = Internationalization.getInstance();

	@Override
	public FilterDetailsWrapper getFilterDetails(String authToken) throws TMSException 
	{				
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		FilterDetailsWrapper wrapper = null;
		
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
								
				connection.setAutoCommit(false);
				
				long start = Calendar.getInstance().getTimeInMillis();
				
				wrapper = new FilterDetailsWrapper();
				
				ArrayList<TermBase> termbases = TermBaseManager.getAllTermBases(connection, true);
				wrapper.setTermBases(termbases);
				
				ArrayList<User> users = UserManager.getAllUsers(connection);
				wrapper.setUsers(users);
				
				InputModel inputmodel = FieldManager.createInputModel(connection);
				wrapper.setInputModel(inputmodel);
				
				connection.commit();
								
				long end = (Calendar.getInstance().getTimeInMillis() - start);
				
				LogUtility.log(Level.INFO, "Filter details for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			LogUtility.log(Level.SEVERE, session, _i18n.getConstants().log_filter(), e, authToken);
			throw new TMSException(e);
		} 
		finally
		{
			if (user == null || user.isGuest())
				DatabaseConnector.closeConnection(connection, session, _i18n.getMessages().log_db_close(""), authToken);
		}
		
		return wrapper;
	}

	@Override
	public void setFilter(String authToken, Filter filter) throws TMSException 
	{
		HttpSession session = getCurrentUserSession(authToken);		
		
		if (session != null)
		{
			if (filter != null)
				session.setAttribute("filter", filter);
			else
				session.removeAttribute("filter");
		}
	}

}
