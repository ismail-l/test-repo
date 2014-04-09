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

package tms2.client.event;

import tms2.client.presenter.Presenter;
import tms2.shared.ExportType;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ExportEvent extends TermBrowserEvent
{	
	private Presenter _presenter = null;
	private ExportType _export_type = null;
	private String _filename = null;
	
	public ExportEvent(Presenter presenter, ExportType export_type, String filename)
	{
		_presenter = presenter;	
		_export_type = export_type;
		_filename = filename;
		
		setCurrentEvent(this);
	}
				
	public Presenter getPresenter()
	{
		return _presenter;
	}
	
	public ExportType getExportType()
	{
		return _export_type;
	}
	
	public String getFileName()
	{
		return _filename;
	}
}
