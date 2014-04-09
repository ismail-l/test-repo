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

package tms2.server.logging;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;


/**
 * The Logger class initiates, maintains and writes to the log file.
 * @author  Wildrich Fourie
 */
public class Logger
{
	/** When enabled more debugging information is also appended in the log. */
	public final static boolean DEBUG_MODE = true; 
	public static java.util.logging.Logger tmsLogger = java.util.logging.Logger.getLogger("ctext.tms");
	
	public static final String LOG_FILE_NAME = "logs/tms2/AutshumatoTMS2.log";
	
	private static Logger _logger = null;
	
	public static Logger getInstance()
	{
		if (_logger == null)
			_logger = new Logger();
		
		return _logger;
	}
		
	/** Assigns the logger to the logging file. */
	private Logger()
	{
		try
		{		
			File logs_dir = new File("logs");
			
			if (! logs_dir.exists())
			{
				logs_dir.mkdir();
				
				File tms_dir = new File("logs/tms2");
				
				if (! tms_dir.exists())				
					tms_dir.mkdir();								 
			}
			else
			{
				File tms_dir = new File("logs/tms2");
				
				if (! tms_dir.exists())				
					tms_dir.mkdir();	
			}
			
			Handler handler = new FileHandler(LOG_FILE_NAME, true);
			handler.setFormatter(new LogFormatter());
			
			tmsLogger.addHandler(handler);
			tmsLogger.setLevel(Level.ALL);
		}
		catch (Exception ex)
		{
			System.out.println("TMS: Unable to load/create " + LOG_FILE_NAME + " log file!");
			ex.printStackTrace();
		}
	}
				
	public synchronized void log(Level level, String message)
	{
		tmsLogger.log(level, message);
	}
	
	public synchronized void log(Level level, String message, Throwable exception)
	{
		tmsLogger.log(level, message, exception);
	}
	
	public synchronized void log(Level level, String session, String message)
	{
		tmsLogger.log(level, session + LogFormatter.seperator + message);
	}
	
	public synchronized void log(Level level, String session, String message, Throwable exception)
	{
		tmsLogger.log(level, session + LogFormatter.seperator + message, exception);
	}
}