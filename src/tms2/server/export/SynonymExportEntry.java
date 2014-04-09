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

/**
 * 
 * @author I. Lavangee
 *
 */
public class SynonymExportEntry extends ExportEntry
{
	/**
	 * @uml.property  name="_index_field"
	 */
	private String _index_field = null;
	/**
	 * @uml.property  name="_index_term"
	 */
	private String _index_term = null;
	
	public SynonymExportEntry()
	{
		
	}	
	
	public void setIndexField(String index_field)
	{
		_index_field = index_field;
	}
	
	public String getIndexField()
	{
		return _index_field;
	}
	
	public void setIndexTerm(String index_term)
	{
		_index_term = index_term;
	}
	
	public String getIndexTerm()
	{
		return _index_term;
	}
	
	@Override
	public void setField(String field)
	{
		this._field = field;
	}
	
	@Override
	public String getField()
	{
		return this._field;
	}
	
	@Override
	public void setCharData(String chardata)
	{
		this._chardata = chardata;
	}
	
	@Override
	public String getCharData()
	{
		return this._chardata;
	}
	
	@Override
	public boolean isSynonymEntry()
	{
		return true;
	}

}
