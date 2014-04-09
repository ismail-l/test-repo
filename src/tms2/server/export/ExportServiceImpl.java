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

package tms2.server.export;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tms2.client.exception.TMSException;
import tms2.client.service.ExportService;
import tms2.server.accesscontrol.AccessControlledRemoteService;
import tms2.server.connection.DatabaseConnector;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.shared.ExportType;
import tms2.shared.Filter;
import tms2.shared.Result;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ExportServiceImpl extends AccessControlledRemoteService implements ExportService
{
	private static final long serialVersionUID = 106659186989807664L;

	private static Internationalization _i18n = Internationalization.getInstance();

	@Override
	public Result<Boolean> generateExportDocument(String authToken, ExportType export_type, String filename) throws TMSException 
	{
		HttpSession session = null;
		User user = null;
		Connection connection = null;
		Result<Boolean> result = new Result<Boolean>();
		
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
				
				System.out.println("generateExportDocument connection: " + connection);
				
				connection.setAutoCommit(false);
					
				long start = Calendar.getInstance().getTimeInMillis();
				
				Filter filter = (Filter)session.getAttribute("filter");
				
				export_type.setXcsPath(getServletContext().getRealPath(File.separator + TBXCompliance.XCS_Name));
				boolean exported = ExportManager.generateDocument(connection, session, authToken, filter, export_type, filename);
				
				connection.commit();
				
				System.out.println("generateExportDocument connection done");
				
				if (! exported)
					throw new TMSException();
				else
					result.setResult(exported);
				
				long end = (Calendar.getInstance().getTimeInMillis() - start);
							
				LogUtility.log(Level.INFO, "Export for " + user.getUsername() + " in session " + session.getId() + " took " + end + " ms");
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
			
			result.setMessage(_i18n.getConstants().log_export_outputdoc());
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

	// This is the service called when the download button is pressed.
	// The document should already be constructed and be ready to be transported.
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String authToken = req.getParameter("authToken");
		    	
    	try
    	{
    		ExportManager.download(authToken, req, resp);
    		
    		LogUtility.log(Level.INFO, "Document downloaded in session " + req.getSession().getId());
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		
    		LogUtility.log(Level.WARNING, req.getSession(), _i18n.getMessages().log_export_error(""), e, authToken);
    		System.out.println("ExServiceImpl: " + e.getMessage());
    		    		
    		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    	}
	}		
}
