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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author I. Lavangee
 *
 */
public class PageNavigateUtililty 
{
	private static String _hosted_server = "http://127.0.0.1:8888/<>?gwt.codesvr=127.0.0.1:9997";
	
	public static void navigate(String page, String name, String features)
	{	
		// Set this state to false two allow for navigation throughout
		// the app.
		WindowClosingUtility.getInstance().setClosingState(false);
		
		if(!GWT.isScript())
			Window.open(_hosted_server.replace("<>", page), name, features);
		else
		{
			String base_url = GWT.getModuleBaseURL();
			String module = GWT.getModuleName();
			
			// Remove the module name from the url
			base_url = base_url.replace(module + "/", "");
			Window.open(base_url + page, name, features);
		}
	}
}
