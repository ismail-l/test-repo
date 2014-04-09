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

import tms2.client.admininterface.presenter.NavigatorPresenter;
import tms2.client.i18n.Internationalization;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author I. Lavangee
 *
 */
public class NavigatorView extends Composite implements NavigatorPresenter.Display
{
	private Internationalization _i18n = Internationalization.getInstance();
	
	private HorizontalPanel _navigation_panel = null;
	
	private Anchor _anc_termbrowser = null;	
	private Anchor _anc_help = null;
	
	public NavigatorView()
	{
		_navigation_panel = new HorizontalPanel();
		_navigation_panel.setSpacing(10);
		initWidget(_navigation_panel);
		
		buildTermBrowserLink();
		buildHelpLink();
	}
	
	private void buildTermBrowserLink()
	{
		HorizontalPanel termbrowser_panel = new HorizontalPanel();
		termbrowser_panel.setSpacing(5);
		
		Image img_termbrowser = new Image("images/browse24.png");
		img_termbrowser.setWidth("24px");
		img_termbrowser.setHeight("24px");
		
		_anc_termbrowser = new Anchor();
		_anc_termbrowser.setStyleName("hyperLink");
		_anc_termbrowser.setText(_i18n.getConstants().admin_link_termbase());
		_anc_termbrowser.setWidth("100%");
		
		termbrowser_panel.add(img_termbrowser);
		termbrowser_panel.add(_anc_termbrowser);
										
		_navigation_panel.add(termbrowser_panel);	
	}
	
	private void buildHelpLink()
	{
		HorizontalPanel help_panel = new HorizontalPanel();
		help_panel.setSpacing(5);
		
		Image img_help = new Image("images/help24.png");
		img_help.setWidth("24px");
		img_help.setHeight("24px");
		
		_anc_help = new Anchor();
		_anc_help.setStyleName("hyperLink");
		_anc_help.setText(_i18n.getConstants().link_help());
		_anc_help.setWidth("100%");
		
		help_panel.add(img_help);
		help_panel.add(_anc_help);
						
		_navigation_panel.add(help_panel);
	}

	@Override
	public Anchor getTermBrowserAnchor() 
	{
		return _anc_termbrowser;
	}

	@Override
	public Anchor getHelpAnchor() 
	{
		return _anc_help;
	}
}
