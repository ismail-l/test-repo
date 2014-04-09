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

package tms2.client.shared.presenter;

import tms2.client.accesscontrol.AccessController;
import tms2.client.admininterface.presenter.AdminInterfacePresenter;
import tms2.client.event.SignOffEvent;
import tms2.client.event.SignOnEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.util.PageNavigateUtililty;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.HasSignOut;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SignOnPresenter implements Presenter, HasSignOut
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private AccessController _access_controller = AccessController.getInstance();
	private Display _display = null;
	private Presenter _presenter = null;
	
	public interface Display 
	{	
		public static final int SIGNON_PANEL = 0;
		public static final int SIGNININGIN_PANEL = 1;
		public static final int SIGNEDIN_PANEL = 2;
		
		public static final int USERNAME_MIN_LENGTH = 6;
		public static final int PASSWORD_MIN_LENGTH = 6;
		public static final int USERNAME_MAX_LENGTH = 20;
		public static final int PASSWORD_MAX_LENGTH = 20;
		
		public DeckPanel getSignOnPanel();
		public Anchor getSignOnAnchor();
		public TextBox getUsernameTextBox();
		public PasswordTextBox getPasswordTextBox();
		public Anchor getSignInAnchor();
		public Label getUsernameLabel();
		public Anchor getSignOutAnchor();
		
		public Widget asWidget();
	}
			
	public SignOnPresenter(Display display, Presenter presenter)
	{
		_display = display;
		_presenter = presenter;
		
		_access_controller.addSignOut(this);
	}
		
	public void displayPresenter()
	{		
		if (_presenter instanceof AdminInterfacePresenter)
		{
			AdminInterfacePresenter admin_presenter = (AdminInterfacePresenter)_presenter;			
			admin_presenter.displayBody();
		}
		else		
			PageNavigateUtililty.navigate("termbrowser.jsp", "_top", null);		
	}
	
	private void bind()
	{
		final HandlerManager event_bus =_access_controller.getEventBus();
		
		_display.getSignOnAnchor().addClickHandler(new ClickHandler()
		{		
			@Override
			public void onClick(ClickEvent event) 
			{
				_display.getSignOnPanel().showWidget(Display.SIGNININGIN_PANEL);
				_display.getUsernameTextBox().setFocus(true);
				
				// XXX For ie specific. In ie after this has been handled
				// the WindowClosing handler is called.
				// WindowClosingUtility.setClosingState(false);
			}
		});
		
		_display.getSignInAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{			
				event_bus.fireEvent(new SignOnEvent(SignOnPresenter.this));				
			}
		});
		
		_display.getSignOutAnchor().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				// XXX For ie specific. In ie after this has been handled
				// the WindowClosing handler is called.
				// WindowClosingUtility.setClosingState(false);
				
				event_bus.fireEvent(new SignOffEvent());
			}
		});
		
		_display.getUsernameTextBox().addKeyDownHandler(new EnterHandler(event_bus));
		_display.getPasswordTextBox().addKeyDownHandler(new EnterHandler(event_bus));
	}
	
	private class EnterHandler implements KeyDownHandler
	{
		private HandlerManager _event_bus = null;
		
		public EnterHandler(HandlerManager event_bus)
		{
			_event_bus = event_bus;
		}
		
		@Override
		public void onKeyDown(KeyDownEvent event)
		{
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				if (isCredentialsValid())
					_event_bus.fireEvent(new SignOnEvent(SignOnPresenter.this));
			}
		}
		
		private boolean isCredentialsValid()
		{
			TextBox txt_username = _display.getUsernameTextBox();
			PasswordTextBox txt_password = _display.getPasswordTextBox();
			
			if (txt_username.getText().isEmpty() ||
					txt_password.getText().isEmpty())			
				return false;
			
			int userLen = txt_username.getText().length();
			int passLen = txt_password.getText().length();
			
			if (! txt_username.getText().matches("[a-zA-Z0-9]+") ||
				userLen < Display.USERNAME_MIN_LENGTH || 
				userLen > Display.USERNAME_MAX_LENGTH)
			{
				AlertBox.show(_i18n.getMessages().signOn_error_validUsername("" + Display.USERNAME_MIN_LENGTH, "" + Display.USERNAME_MAX_LENGTH));
				txt_username.setText("");
				return false;
			}
			if (! txt_password.getText().matches("[a-zA-Z0-9]+") || 
				passLen < Display.PASSWORD_MIN_LENGTH || 
				passLen > Display.PASSWORD_MAX_LENGTH)
			{
				AlertBox.show(_i18n.getMessages().signOn_error_validPassword("" + Display.PASSWORD_MIN_LENGTH, "" + Display.PASSWORD_MAX_LENGTH));
				txt_password.setText("");
				return false;
			}
			
			return true;		
		}
	}
		
	@Override
	public void go(HasWidgets container) 
	{
		bind();
		
		DeckPanel signon_panel = _display.getSignOnPanel();
		signon_panel.showWidget(Display.SIGNON_PANEL);
		container.add(_display.asWidget());
	}

	public Presenter getPresenter() 
	{	
		return this;
	}
	
	public Display getDisplay()
	{
		return _display;
	}

	@Override
	public void signOut() 
	{
		_display.getSignOnPanel().showWidget(Display.SIGNON_PANEL);
	}
}
