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

/**
 * 
 * @author I. Lavangee
 *
 */
public class SearchEvent extends TermBrowserEvent
{
	private Presenter _presenter = null;
	private String _search_prompt = null;
	private String _search_type = null;
	private boolean _browse_textbox_search = false;
	
	public SearchEvent(Presenter presenter, String search_prompt, String search_type, boolean browse_textbox_search)
	{
		_presenter = presenter;		
		_search_prompt = search_prompt;
		_search_type = search_type;
		_browse_textbox_search = browse_textbox_search;		
		
		setCurrentEvent(this);
	}
	
	public Presenter getPresenter()
	{
		return _presenter;
	}
	
	public String getSearchPrompt()
	{
		return _search_prompt;
	}
	
	public String getSearchType()
	{
		return _search_type;
	}
	
	public boolean isBrowseTextBoxSearch()
	{
		return _browse_textbox_search;
	}
}
