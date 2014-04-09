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

package tms2.client.shared.view;

import tms2.client.i18n.Internationalization;
import tms2.client.shared.presenter.SignOnPresenter;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SignOnView extends Composite implements SignOnPresenter.Display
{	
	private Internationalization _i18n = Internationalization.getInstance();
		
	private DeckPanel _signon_panel = null;
	
	private Anchor _anc_signon = null;
	
	private TextBox _txt_username = null;
	private PasswordTextBox _txt_password = null;
	private Anchor _anc_sign_in = null;
	
	private Label _lbl_username = null;
	private Anchor _anc_sign_out= null;
	
	public SignOnView()
	{
		_signon_panel = new DeckPanel();
		initWidget(_signon_panel);
		
		buildSignOnPanel();
		buildSigningInPanel();	
		buildSignedInPanel();
	}
	
	private void buildSignOnPanel()
	{
		HorizontalPanel signon_panel = new HorizontalPanel();		
		signon_panel.setWidth("84px");
		
		Image image = new Image("images/user24.png");
		image.setWidth("24px");		
						
		_anc_signon = new Anchor(_i18n.getConstants().signOn_signOn());
		_anc_signon.setStyleName("hyperLink");		
		_anc_signon.setWidth("60px");	
		
		signon_panel.add(image);
		signon_panel.add(_anc_signon);
		
		signon_panel.setCellWidth(image, "24px");
		signon_panel.setCellWidth(_anc_signon, "60px");
		
		_signon_panel.add(signon_panel);
	}
	
	private void buildSigningInPanel()
	{
		HorizontalPanel signingin_panel = new HorizontalPanel();
		signingin_panel.setSpacing(5);
		signingin_panel.setWidth("400px");
				
		_txt_username = new TextBox();
		_txt_username.setMaxLength(20);
		_txt_username.getElement().setAttribute("placeholder", "username");	
		_txt_username.setWidth("150px");
		
		_txt_password = new PasswordTextBox();
		_txt_password.setMaxLength(20);
		_txt_password.setWidth("150px");
		
		_anc_sign_in = new Anchor(_i18n.getConstants().signOn_signOn());
		_anc_sign_in.setStyleName("hyperLink");
		_anc_sign_in.setWidth("100px");
		
		signingin_panel.add(_txt_username);
		signingin_panel.add(_txt_password);
		signingin_panel.add(_anc_sign_in);	
		
		signingin_panel.setCellWidth(_txt_username, "150px");
		signingin_panel.setCellWidth(_txt_password, "150px");
		signingin_panel.setCellWidth(_anc_sign_in, "100px");
				
		_signon_panel.add(signingin_panel);
	}
	
	private void buildSignedInPanel()
	{
		HorizontalPanel signedin_panel = new HorizontalPanel();
		signedin_panel.setSpacing(5);
		signedin_panel.setWidth("350px");
		
		_lbl_username = new Label(null, true);
		_lbl_username.setWidth("150px");
			
		HorizontalPanel signout_panel = new HorizontalPanel();
		signout_panel.setSpacing(5);
		
		Image img_signout = new Image("images/leave24.png");
		img_signout.setWidth("24px");
		img_signout.setHeight("24px");
		
		_anc_sign_out = new Anchor(_i18n.getConstants().signOn_signOff());
		_anc_sign_out.setStyleName("hyperLink");
		_anc_sign_out.setWidth("100px");
		
		signout_panel.add(img_signout);
		signout_panel.add(_anc_sign_out);
		
		signedin_panel.add(_lbl_username);
		signedin_panel.add(signout_panel);	
		
		signedin_panel.setCellWidth(_lbl_username, "150px");
		signedin_panel.setCellWidth(signout_panel, "100px");
		
		signedin_panel.setCellVerticalAlignment(_lbl_username, HasVerticalAlignment.ALIGN_MIDDLE);
						
		_signon_panel.add(signedin_panel);
	}

	@Override
	public DeckPanel getSignOnPanel() 
	{
		return _signon_panel;
	}

	@Override
	public Anchor getSignOnAnchor() 
	{
		return _anc_signon;
	}

	@Override
	public TextBox getUsernameTextBox() 
	{	
		return _txt_username;
	}

	@Override
	public PasswordTextBox getPasswordTextBox() 
	{	
		return _txt_password;
	}

	@Override
	public Anchor getSignInAnchor() 
	{
		return _anc_sign_in;
	}

	@Override
	public Label getUsernameLabel() 
	{
		return _lbl_username;
	}

	@Override
	public Anchor getSignOutAnchor() 
	{
		return _anc_sign_out;
	}
}
