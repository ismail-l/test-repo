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

package tms2.client.util;


/**
 * Validates the data types.
 * @author W. Fourie
 */
public class FieldDataValidator 
{
	
	public static boolean validateText(Object input)
	{
		if(input instanceof String)
			return true;
		return false;
	}
	
	
	public static boolean validateInteger(String input)
	{
		try
		{
			@SuppressWarnings("unused")
			int val = Integer.parseInt(input);
		}
		catch (Exception ex)
		{
			return false;
		}
		return true;
	}
	
	
	public static boolean validateFloat(String input)
	{
		try
		{
			@SuppressWarnings("unused")
			float val = Float.parseFloat(input);
		}
		catch (Exception ex)
		{
			return false;
		}
		return true;
	}
	
	
	public static boolean validateHTMLLink(Object input)
	{
		if(input instanceof String)
		{
			// Only check for valid URL.
			if(isValidPath((String)input))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	
	public static boolean validateFsPath(Object input)
	{
		if(input instanceof String)
		{
			// Only check for valid URL.
			if(isValidPath((String)input))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	
	// No need to implement yet, there are no field which uses the XML type.
	public static boolean validateXML(Object input)
	{
		return true;
	}

	
	// TODO: Validate Latex
	public static boolean validateFormula(Object input)
	{
		return true;
	}
	
	
	private static native boolean isValidUrl(String url) 
	/*-{
    	var pattern = /^(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]+))?$/;
    	return pattern.test(url);
	}-*/;


	private static native boolean isValidPath(String url) 
	/*-{
    	var pattern = /^(\/|\/([\w#!:.?+=&%@!\-\/]+))$/;
    	return pattern.test(url);
	}-*/;
}
