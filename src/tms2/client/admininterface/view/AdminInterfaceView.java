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

package tms2.client.admininterface.view;

import tms2.client.admininterface.presenter.AdminInterfacePresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.util.FooterUtility;
import tms2.client.widgets.FixedPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AdminInterfaceView extends Composite implements AdminInterfacePresenter.Display 
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private Label _lbl_prompt = null;
	private FixedPanel _footer = null;
	
	public AdminInterfaceView()
	{
		HorizontalPanel prompt_panel = new HorizontalPanel();
		
		initWidget(prompt_panel);
		
		_lbl_prompt = new Label(_i18n.getConstants().alert_admin_access());
		_lbl_prompt.addStyleName("plainLabelText");
		
		prompt_panel.add(_lbl_prompt);
		
		_footer = FooterUtility.getFooter();
	}

	@Override
	public Label getPromptLabel() 
	{	
		return _lbl_prompt;
	}

	@Override
	public FixedPanel getFooter() 
	{	
		return _footer;
	}
}
