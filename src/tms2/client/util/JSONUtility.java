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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import tms2.shared.AppProperties;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class JSONUtility 
{
	public static String userToJSON(User user)
	{
		JSONObject json_object = new JSONObject();

		json_object.put("_user_id", new JSONNumber(user.getUserId()));
		json_object.put("_user_category_id", new JSONNumber(user.getUserCategoryId()));		
		json_object.put("_username", new JSONString(user.getUsername()));
		json_object.put("_firstname", new JSONString(user.getFirstName()));
		json_object.put("_lastname", new JSONString(user.getLastName()));
		json_object.put("_authtoken", new JSONString(user.getAuthToken()));
		json_object.put("_activated", new JSONString(Boolean.toString(user.isActivated())));
						
		if (user.getExpiryDate() != null)
		{
			String expiry_date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(user.getExpiryDate());
			json_object.put("_expiry_date", new JSONString(expiry_date));
		}
		
		if (user.getLastSignOn() != null)
		{
			String last_signon_date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(user.getLastSignOn());
			json_object.put("_last_signon", new JSONString(last_signon_date));
		}
		
		json_object.put("_session_id", new JSONString(user.getSessionId()));
		json_object.put("_ip_address", new JSONString(user.getIPAddress()));
		json_object.put("_is_super_user", new JSONString(Boolean.toString(user.isSuperUser())));
		json_object.put("_guest", new JSONString(Boolean.toString(user.isGuest())));
		json_object.put("_admin", new JSONString(Boolean.toString(user.isAdmin())));
						
		return json_object.toString();	
	}
	
	public static User jsonToUser(JSONObject json)
	{
		User user = new User();
		
		JSONNumber js_user_id = (JSONNumber) json.get("_user_id");
		long user_id = (long) js_user_id.doubleValue();
		user.setUserId(user_id);
		
		JSONNumber js_user_category_id = (JSONNumber) json.get("_user_category_id");
		long user_category_id = (long) js_user_category_id.doubleValue();
		user.setUserCategoryId(user_category_id);
		
		user.setUsername(json.get("_username").isString().stringValue());
		user.setFirstName(json.get("_firstname").isString().stringValue());
		user.setLastName(json.get("_lastname").isString().stringValue());
		user.setAuthToken(json.get("_authtoken").isString().stringValue());
		
		boolean activated = false;
		String js_activated = json.get("_activated").isString().stringValue();
		if (js_activated.equals("true"))
			activated = true;
		
		user.setActivated(activated);
		
		JSONString js_expiry_date = (JSONString) json.get("_expiry_date");
		if (js_expiry_date != null && ! js_expiry_date.stringValue().isEmpty())
		{
			Date expiry_date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).parseStrict(js_expiry_date.stringValue());
			user.setExpiryDate(expiry_date);
		}
		
		JSONString js_last_signon_date = (JSONString) json.get("_last_signon");
		if (js_last_signon_date != null && ! js_last_signon_date.stringValue().isEmpty())
		{
			Date last_signon_date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).parseStrict(js_last_signon_date.stringValue());
			user.setLastSignOn(last_signon_date);
		}
		
		user.setSessionId(json.get("_session_id").isString().stringValue());
		user.setIPAddress(json.get("_ip_address").isString().stringValue());
		
		boolean superuser = false;
		String js_superuser = json.get("_is_super_user").isString().stringValue();
		if (js_superuser.equals("true"))
			superuser = true;
		
		user.setSuperUser(superuser);
		
		boolean guest = false;
		String js_guest = json.get("_guest").isString().stringValue();
		if (js_guest.equals("true"))
			guest = true;
		
		user.setGuest(guest);
		
		boolean admin = false;
		String js_admin = json.get("_admin").isString().stringValue();
		if (js_admin.equals("true"))
			admin = true;
		
		user.setIsAdmin(admin);
		
		return user;
	}
	
	public static String appPropertiesToJSON(AppProperties app_props)
	{
		JSONObject json_object = new JSONObject();
		
		json_object.put("_project_field", new JSONString(app_props.getProjectField()));
		json_object.put("_project_field_maxlength", new JSONString(Integer.toString(app_props.getProjectFieldMaxLength())));
		json_object.put("_sort_index_field", new JSONString(app_props.getSortIndexField()));
		json_object.put("_synonym_field", new JSONString(app_props.getSynonymField()));
		json_object.put("_context_field", new JSONString(app_props.getContextField()));
		json_object.put("_defintion_field", new JSONString(app_props.getDefinitionField()));
		json_object.put("_note_field", new JSONString(app_props.getNoteField()));
		json_object.put("_synonym_context_field", new JSONString(app_props.getSynonymContextField()));
		json_object.put("_synonym_note_field", new JSONString(app_props.getSynonymNoteField()));
				
		return json_object.toString();
	}
	
	public static AppProperties jsonToAppProperties(JSONObject json)
	{
		AppProperties app_props = new AppProperties();
		
		app_props.setProjectField(json.get("_project_field").isString().stringValue());
		app_props.setProjectFieldMaxLength(Integer.parseInt(json.get("_project_field_maxlength").isString().stringValue()));
		app_props.setSortIndexField(json.get("_sort_index_field").isString().stringValue());
		app_props.setSynonymField(json.get("_synonym_field").isString().stringValue());
		app_props.setContextField(json.get("_context_field").isString().stringValue());
		app_props.setDefinitionField(json.get("_defintion_field").isString().stringValue());
		app_props.setNoteField(json.get("_note_field").isString().stringValue());
		app_props.setSynonymContextField(json.get("_synonym_context_field").isString().stringValue());
		app_props.setSynonymNoteField(json.get("_synonym_note_field").isString().stringValue());
		
		return app_props;
	}
}
