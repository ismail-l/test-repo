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

import tms2.client.i18n.Internationalization;
import tms2.client.termbrowser.presenter.NavigatorPresenter;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
	
	private Anchor _anc_admin = null;
	private Image _img_admin = null;
	private Anchor _anc_signout = null;
	private Image _img_signout = null;
	private Anchor _anc_help = null;
	
	public NavigatorView()
	{
		_navigation_panel = new HorizontalPanel();
		_navigation_panel.setSpacing(10);
		initWidget(_navigation_panel);
		
		buildAdminLink();
		buildSignOutLink();
		buildHelpLink();
	}
	
	private void buildAdminLink()
	{
		HorizontalPanel admin_panel = new HorizontalPanel();
		admin_panel.setSpacing(5);
		
		_img_admin = new Image("images/gear24.png");
		_img_admin.setWidth("24px");
		_img_admin.setHeight("24px");
		
		_anc_admin = new Anchor();
		_anc_admin.setStyleName("hyperLink");
		_anc_admin.setText(_i18n.getConstants().link_admin());
		_anc_admin.setWidth("100%");
		
		admin_panel.add(_img_admin);
		admin_panel.add(_anc_admin);
						
		admin_panel.setCellVerticalAlignment(_anc_admin, HasVerticalAlignment.ALIGN_MIDDLE);
		
		_navigation_panel.add(admin_panel);		
	}
	
	private void buildSignOutLink()
	{
		HorizontalPanel signout_panel = new HorizontalPanel();
		signout_panel.setSpacing(5);
		
		_img_signout = new Image("images/leave24.png");
		_img_signout.setWidth("24px");
		_img_signout.setHeight("24px");
		
		_anc_signout = new Anchor();
		_anc_signout.setStyleName("hyperLink");
		_anc_signout.setText(_i18n.getConstants().link_signOff());
		_anc_signout.setWidth("100%");
		
		signout_panel.add(_img_signout);
		signout_panel.add(_anc_signout);
		
		signout_panel.setCellVerticalAlignment(_anc_signout, HasVerticalAlignment.ALIGN_MIDDLE);
		
		_navigation_panel.add(signout_panel);
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
		
		help_panel.setCellVerticalAlignment(_anc_help, HasVerticalAlignment.ALIGN_MIDDLE);
		
		_navigation_panel.add(help_panel);
	}

	@Override
	public Anchor getAdminAnchor() 
	{		
		return _anc_admin;
	}

	@Override
	public Anchor getSignOutAnchor() 
	{
		return _anc_signout;
	}

	@Override
	public Anchor getHelpAnchor() 
	{
		return _anc_help;
	}

	@Override
	public Image getAdminImage() 
	{	
		return _img_admin;
	}

	@Override
	public Image getSignOutImage() 
	{	
		return _img_signout;
	}
}
