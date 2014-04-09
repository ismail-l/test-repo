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

import java.util.ArrayList;

import tms2.shared.TerminlogyObject;

/**
 * Represents an entity to export.
 * 
 * @author Ismail Lavangee
 */
public class ExportEntry 
{	
	/**
	 * @uml.property  name="_record_id"
	 */
	private long _record_id = -1;	
	/**
	 * @uml.property  name="_resource_id"
	 */
	private long _resource_id = -1;
	/**
	 * @uml.property  name="_field"
	 */
	protected String _field = "";
	/**
	 * @uml.property  name="_chardata"
	 */
	protected String _chardata = "";
			
	/**
	 * @uml.property  name="_inputfield"
	 * @uml.associationEnd  
	 */
	private TerminlogyObject _inputfield = null;
	
	/**
	 * @uml.property  name="_recorddata"
	 */
	private ArrayList<ExportEntry> _recorddata = new ArrayList<ExportEntry>();
	/**
	 * @uml.property  name="_indexdata"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="tms.client.exporttool.ExportEntry"
	 */
	private ArrayList<ExportEntry> _indexdata = new ArrayList<ExportEntry>();
	/**
	 * @uml.property  name="_targetdata"
	 */
	private ArrayList<ExportEntry> _targetdata = new ArrayList<ExportEntry>();
	
	/**
	 * @uml.property  name="_subfield_subfields"
	 */
	private ArrayList<ExportEntry> _subfield_subfields = new ArrayList<ExportEntry>();
		
	public ExportEntry()
	{
		
	}
		
	public void setRecordId(long record_id)
	{
		this._record_id = record_id;
	}
		
	public long getRecordId()
	{
		return this._record_id;
	}
	
	public void setResourceId(long resource_id)
	{
		this._resource_id = resource_id;
	}
		
	public long getResourceId()
	{
		return this._resource_id;
	}
	
	public void setField(String field)
	{
		this._field = field;
	}
	
	public String getField()
	{
		return this._field;
	}
	
	public void setInputField(TerminlogyObject inputfield)
	{
		_inputfield = inputfield;
	}
	
	public TerminlogyObject getInputField()
	{
		return _inputfield;
	}
	
	public void setCharData(String chardata)
	{
		this._chardata = chardata;
	}
	
	public String getCharData()
	{
		return this._chardata;
	}
	
	public void setRecordData(ArrayList<ExportEntry> recorddata)
	{
		this._recorddata = recorddata;
	}
	
	public ArrayList<ExportEntry> getRecordData()
	{
		return this._recorddata;
	}
	
	public void setIndexData(ArrayList<ExportEntry> indexdata)
	{
		this._indexdata = indexdata;
	}
	
	public ArrayList<ExportEntry> getIndexData()
	{
		return this._indexdata;
	}
	
	public void setTargetData(ArrayList<ExportEntry> targetdata)
	{
		this._targetdata = targetdata;
	}
	
	public ArrayList<ExportEntry> getTargetData()
	{
		return this._targetdata;
	}
	
	public void setSubfieldSubfields(ArrayList<ExportEntry> subfield_subfields)
	{
		_subfield_subfields = subfield_subfields;
	}
	
	public ArrayList<ExportEntry> getSubfieldSubfields()
	{
		return _subfield_subfields;
	}
	
	public boolean isSynonymEntry()
	{
		return false;
	}
}
