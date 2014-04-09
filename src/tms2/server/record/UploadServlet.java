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

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import tms2.client.exception.TMSException;
import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.session.ApplicationSessionCache;
import tms2.server.sql.StoredProcedureManager;


/**
 * 
 * @author W. Fourie
 */
public class UploadServlet extends UploadAction 
{	
	private static final long serialVersionUID = 6378755739021377168L;
	
	private static Internationalization _i18n = Internationalization.getInstance();

	// Default images directory.
	private final String IMG_DIR = "tmsdata";
	private final static long MAX_SIZE = 3145728;
	
	/**
	 * Override executeAction to save the received files in a custom place
	 * and delete this items from session.  
	 */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException 
	{  
		// Servlet is running
		long start = Calendar.getInstance().getTimeInMillis();
				
		// Detect parameters
		String authToken = request.getParameter("authToken");
		String recordId = request.getParameter("recordid");
		String fieldName = request.getParameter("fieldName");
		String prefix = request.getParameter("prefix");
			
		try
		{
			if (!isUserSignedOn(request.getSession(), authToken))
			{
				removeSessionFileItems(request);
				LogUtility.log(Level.WARNING, request.getSession(), _i18n.getMessages().server_upload_error_user(""));
				return  _i18n.getMessages().server_upload_error_user("");
			}
		}
		catch (Exception e)
		{
			removeSessionFileItems(request);
			LogUtility.log(Level.WARNING,request.getSession(),   _i18n.getMessages().server_upload_error(""), e);
			return  _i18n.getMessages().server_upload_error(e.getMessage());
		}

		// Get the next possible recordId.
		long newRecordId = 0;
		
		if(recordId != null)		
			newRecordId = Long.parseLong(recordId);		
		else
		{
			try
			{
				newRecordId = retrieveNextRecordId(request);
			}
			catch (SQLException e)
			{
				removeSessionFileItems(request);
				LogUtility.log(Level.SEVERE, request.getSession(),  _i18n.getMessages().server_upload_error_recordid(""), e);
				return  _i18n.getMessages().server_upload_error_recordid("");
			} 
		}

		// The sessionFiles should always contain only one file.
		if(sessionFiles.size() != 1)
		{
			removeSessionFileItems(request);
			LogUtility.log(Level.WARNING, request.getSession(),  _i18n.getMessages().server_upload_error_dimensions(""));
			return  _i18n.getMessages().server_upload_error_dimensions("");
		}

		// Check that a file was received.
		FileItem receivedFileItem = sessionFiles.get(0);
		if(receivedFileItem.isFormField())
		{
			removeSessionFileItems(request);
			LogUtility.log(Level.WARNING, request.getSession(),  _i18n.getMessages().server_upload_error_formfield(""));
			return  _i18n.getMessages().server_upload_error_formfield("");
		}

		if (receivedFileItem.getSize() > MAX_SIZE)
		{
			removeSessionFileItems(request);
			LogUtility.log(Level.WARNING, request.getSession(),  _i18n.getMessages().server_upload_error_file_size(""));
			return  _i18n.getMessages().server_upload_error_file_size("");
		}
		
		// Response string
		String response = "";

		try
		{
			String dirString = "";
			
			dirString = getServletContext().getRealPath("/") + File.separator + IMG_DIR;
						
			System.out.println("Upload Dir: " + dirString);

			// Construct the file name.
			String fileName = receivedFileItem.getName();
			if (! fileName.isEmpty())
			{
				fileName = fileName.substring(fileName.lastIndexOf("."));					// Get the extension
				fileName = prefix + "." + fieldName + "." + newRecordId + fileName;						// Add the new filename (Image.RecordId.ext)
	
				// Create the directory
				File dirPath = new File(dirString);
				if(!createDirectory(dirPath))
				{
					removeSessionFileItems(request);
					LogUtility.log(Level.WARNING, request.getSession(),  _i18n.getMessages().server_upload_error_dir(""));
					return  _i18n.getMessages().server_upload_error_dir("");
				}
	
				// Create the file
				File saveFile = new File(dirPath.getCanonicalPath() + File.separator + fileName);
				System.out.println("SaveFile: " + saveFile.getCanonicalPath());
				if(!createFile(saveFile))
				{
					removeSessionFileItems(request);
					LogUtility.log(Level.WARNING, request.getSession(),  _i18n.getMessages().server_upload_error_file(""));
					return  _i18n.getMessages().server_upload_error_file("");
				}
	
	
				// Write the contents
				receivedFileItem.write(saveFile);
	
	
				// Create the response message		
				response += IMG_DIR + "/" + fileName;	
				
				System.out.println("\nResponse String: " + response + "\n");
			}
			
		}
		catch (Exception ex)
		{
			response +=  _i18n.getMessages().server_upload_error(ex.getMessage());
			LogUtility.log(Level.WARNING, request.getSession(), response, ex);
		}

		// Remove files from session because we have a copy of them
		removeSessionFileItems(request);
		
		long end = (Calendar.getInstance().getTimeInMillis() - start)/1000;
		
		LogUtility.log(Level.INFO, "Upload action in session " + request.getSession().getId() + " took " + end + " seconds" );
		
		return response;
	}


	// Retrieves the next RecordId from the recordid sequence.
	// returns the id if found
	// throws a SQLException if not found
	private long retrieveNextRecordId(HttpServletRequest request) throws SQLException 
	{
		HttpSession session = request.getSession();
		String authToken = (String)session.getAttribute(AccessControlManager.AUTH_TOKEN);
		
		Connection connection = null;
		
		long next_record_id = -1;
		
		try
		{
			connection = ApplicationSessionCache.getSessionCache(session).getUserConnection(authToken);
			connection.setAutoCommit(false);
			
			String sql = "select max (recordid) from tms.records;";
			
			CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
			
			ResultSet recordIdCurrentValue = (ResultSet) stored_procedure.getObject(1);
	
			if (recordIdCurrentValue != null && recordIdCurrentValue.next())			
				next_record_id = recordIdCurrentValue.getLong("max") + 1;	
			
			connection.commit();
			
			if (next_record_id == -1)
				throw new SQLException( _i18n.getMessages().server_upload_error_value(""));
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
			
			throw new SQLException( _i18n.getMessages().server_upload_error_value(""));
		}
		
		return next_record_id;
	}

	// Attempts to create the specified directory(ies).
	// True - Success.
	// False - Was unable to create the directory(ies).
	private boolean createDirectory(File dirPath) throws IOException
	{
		if(!dirPath.exists())
		{			
			if(!dirPath.mkdirs())		
				return false;			
		}

		return true;
	}


	// Attempts to create a new file specified by the parameter.
	private boolean createFile(File newFile) throws IOException
	{
		if(!newFile.exists())
		{			
			if(!newFile.createNewFile())						
				return false;			
		}
		return true;
	}
	
	private boolean isUserSignedOn(HttpSession session, String authToken) throws TMSException
	{
		return AccessControlManager.isUserStillSignedOn(session, authToken);
	}
}