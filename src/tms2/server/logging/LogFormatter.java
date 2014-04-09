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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter for a log file.
 * 
 * @author Wildrich Fourie
 */
public class LogFormatter extends Formatter
{
	// Formatter properties

	private Date date = new Date();
	private final static String stringFormat = "{0,date} {0, time}";
	
	private MessageFormat messageFormat;

	private Object args[] = new Object[1];
	
	private final static String newLine = System.getProperty("line.separator");
	public final static String seperator = " : ";
	

	@Override
	public synchronized String format(LogRecord record)
	{
		// INIT THE BUFFER
		StringBuffer sb = new StringBuffer();
		// DATE
		sb.append(addDate(record));
		sb.append(seperator);
		// LEVEL
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(seperator);
		// MESSAGE
		sb.append(message);
		sb.append(newLine);
		// Stack Trace
		sb.append(addException(record));
		
		return sb.toString();
	}
	
	
	// Creates a date string according to the stringFormat specified.
	private StringBuffer addDate(LogRecord record)
	{
		date.setTime(record.getMillis());
		args[0] = date;
		StringBuffer sb = new StringBuffer();
		if(messageFormat == null)
			messageFormat = new MessageFormat(stringFormat);
		
		messageFormat.format(args, sb, null);
		
		return sb;
	}
	
	
	// Writes out the exception to a buffer and returns,
	// the complete trace.
	private String addException(LogRecord record)
	{
		String ret = "";
		if(record.getThrown() != null)
		{
			try
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sw.append(newLine);
				ret = sw.toString();
			}
			catch (Exception ex)
			{
				ret = "";
			}
		}
		
		return ret;
	}
}