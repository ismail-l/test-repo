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

package tms2.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AppProperties implements IsSerializable
{
	private String _db_host = null;
	private String _db_port = null;
	private String _db_superrole = null;
	private String _db_userrole = null;
	private String _db_guestrole = null;
	private String _db_pass = null;
	private String _project_field = null;
	private int _project_field_maxlength = -1;
	private String _sort_index_field = null;
	private String _synonym_field = null;
	private String _context_field = null;
	private String _defintion_field = null;
	private String _note_field = null;
	private String _synonym_context_field = null;
	private String _synonym_note_field = null;
	
	public AppProperties()
	{
		
	}
	
	public void setDBHost(String db_host)
	{
		_db_host = db_host;
	}
	
	public void setDBPort(String db_port)
	{
		_db_port = db_port;
	}
	
	public void setDBSuperRole(String db_user)
	{
		_db_superrole = db_user;
	}
	
	public void setDBUserRole(String db_user)
	{
		_db_userrole = db_user;
	}

	public void setDBGuestRole(String db_user)
	{
		_db_guestrole = db_user;
	}
	
	public void setDBPass(String db_pass)
	{
		_db_pass = db_pass;
	}
	
	public void setProjectField(String project_field)
	{
		_project_field = project_field;
	}
	
	public void setProjectFieldMaxLength(int maxlength)
	{
		_project_field_maxlength = maxlength;
	}
	
	public void setSortIndexField(String sort_index_field)
	{
		_sort_index_field = sort_index_field;
	}
	
	public void setSynonymField(String synonym_field)
	{
		_synonym_field = synonym_field;
	}
	
	public void setContextField(String context_field)
	{
		_context_field = context_field;
	}
	
	public void setDefinitionField(String definition_field)
	{
		_defintion_field = definition_field;
	}
	
	public void setNoteField(String note_field)
	{
		_note_field = note_field;
	}
	
	public void setSynonymContextField(String synonym_context_field)
	{
		_synonym_context_field = synonym_context_field;
	}
	
	public void setSynonymNoteField(String synonym_note_field)
	{
		_synonym_note_field = synonym_note_field;
	}
				
	public String getDBHost()
	{
		return _db_host;
	}
	
	public String getDBPort()
	{
		return _db_port;
	}
	
	public String getDBSuperRole()
	{
		return _db_superrole;
	}
	
	public String getDBUserRole()
	{
		return _db_userrole;
	}
	
	public String getDBGuestRole()
	{
		return _db_guestrole;
	}
	
	public String getDBPass()
	{
		return _db_pass;
	}
	
	public String getProjectField()
	{
		return _project_field;
	}
	
	public int getProjectFieldMaxLength()
	{
		return _project_field_maxlength;
	}
	
	public String getSortIndexField()
	{
		return _sort_index_field;
	}
	
	public String getSynonymField()
	{
		return _synonym_field;
	}
	
	public String getContextField()
	{
		return _context_field;
	}
	
	public String getDefinitionField()
	{
		return _defintion_field;
	}
	
	public String getNoteField()
	{
		return _note_field;
	}
	
	public String getSynonymContextField()
	{
		return _synonym_context_field;
	}
	
	public String getSynonymNoteField()
	{
		return _synonym_note_field;
	}
}
