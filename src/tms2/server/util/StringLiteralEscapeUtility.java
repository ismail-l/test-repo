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

package tms2.server.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/**
 * String utility class to escape string literals from a postgres sql query.
 * @author Ismail Lavangee
 *
 */
public class StringLiteralEscapeUtility
{
	public static String escapeStringLiteral(String string)
	{				
		if (string.indexOf("'") >= 0)
			string = string.replaceAll("'", "''");
	
		string  = replaceBrackets(string, '(');
		string  = replaceBrackets(string, ')');
		string  = replaceBrackets(string, '[');
		string  = replaceBrackets(string, ']');				
		
		return string;		
	}
	
	private static String replaceBrackets(String string, char bracket)
	{
		StringBuffer sb = new StringBuffer();		
		StringCharacterIterator iter = new StringCharacterIterator(string);
		char character =  iter.current();
		
		while (character != CharacterIterator.DONE)
		{
			if (character == bracket)
				sb.append("\\\\" + bracket);
			else
				sb.append(character);
			
			character =  iter.next();
		}
		
		return sb.toString();
	}
}
