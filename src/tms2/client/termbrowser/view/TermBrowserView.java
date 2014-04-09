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

package tms2.client.termbrowser.view;

import tms2.client.termbrowser.presenter.TermBrowserPresenter;
import tms2.client.util.FooterUtility;
import tms2.client.widgets.FixedPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermBrowserView extends VerticalPanel implements TermBrowserPresenter.Display
{
	private FixedPanel _footer = null;
	
	public TermBrowserView()
	{
		super();
		
		_footer = FooterUtility.getFooter();
	}

	@Override
	public FixedPanel getFooter() 
	{
		return _footer;
	}
}
