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

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class representing the export types.
 * 
 * @author Ismail Lavangee
 */
public class ExportType implements IsSerializable
{
	private String _export_type = null;	
	private TerminlogyObject _source = null;
	private TerminlogyObject _target = null;			
	private ArrayList<TerminlogyObject> _record_fields = null;
	private ArrayList<TerminlogyObject> _target_fields = null;
	private boolean _include_fieldnames = false;
	private String _xcs_path = null;
	
	public ExportType()
	{

	}
	
	public void setExportType(String export_type)
	{
		this._export_type = export_type;
	}
	
	public String getExportType()
	{
		return this._export_type;
	}
										
	public void setSourceField(TerminlogyObject source)
	{
		this._source = source;
	}
	
	public TerminlogyObject getSourceField()
	{
		return this._source;
	}
	
	public void setTargetField(TerminlogyObject target)
	{
		this._target = target;
	}
	
	public TerminlogyObject getTargetField()
	{
		return this._target;
	}
			
	public void addRecordField(TerminlogyObject terminology_object)
	{
		if (_record_fields == null)
			_record_fields = new ArrayList<TerminlogyObject>();
		
		_record_fields.add(terminology_object);
	}
	
	public ArrayList<TerminlogyObject> getRecordFields()
	{
		return this._record_fields;
	}
				
	public void addTargetField(TerminlogyObject terminology_object)
	{
		if (_target_fields == null)
			_target_fields = new ArrayList<TerminlogyObject>();
		
		_target_fields.add(terminology_object);
	}
	
	public ArrayList<TerminlogyObject> getTargetFields()
	{
		return _target_fields;
	}
			
	public void setIncludeFieldNames(boolean include_fieldnames)
	{
		_include_fieldnames = include_fieldnames;
	}
	
	public boolean includeFieldNames()
	{
		return _include_fieldnames;
	}
	
	public void setXcsPath(String path)
	{
		_xcs_path = path;
	}
	
	public String getXcsPath()
	{
		return _xcs_path;
	}
}
