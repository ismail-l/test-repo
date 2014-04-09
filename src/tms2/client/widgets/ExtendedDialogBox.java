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

package tms2.client.widgets;


import tms2.client.i18n.TMSConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Custom dialog box.
 * 
 * @author Werner Liebenberg
 * @author Wildrich Fourie
 *
 */
public abstract class ExtendedDialogBox extends DialogBox {

	protected static Image ICON_ALERT = new Image("images/alert24.png");
	protected static Image ICON_ACCEPTED = new Image("images/accepted24.png");
	protected static Image ICON_CANCEL = new Image("images/cancel24.png");
	protected static Image ICON_CLOSE = new Image("images/close24.png");
	protected static Image ICON_DELETE = new Image("images/delete24.png");
	protected static Image ICON_INFO = new Image("images/info24.png");
	protected static Image ICON_SMILE = new Image("images/smile24.png");
	
	protected ClickHandler clickHandler = null;
	
	protected DialogBoxBody body;
	private Button okButton;
	
	private TMSConstants constants = GWT.create(TMSConstants.class);
	
	public ExtendedDialogBox() {
		this(false, true);
		this.setGlassEnabled(true);
	}

	public ExtendedDialogBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		this.setGlassEnabled(true);
		initialise();
	}
	
	public ExtendedDialogBox(String title, String caption, boolean autoHide, boolean modal) {
		this(autoHide, modal);
		this.setGlassEnabled(true);
		super.setText(title);
		//super.setTitle(title);
		body.setMessage(caption);
	}
	
	public ExtendedDialogBox(boolean autoHide, boolean modal, ClickHandler clickHandler) {
		super(autoHide, modal);
		this.setGlassEnabled(true);
		this.clickHandler = clickHandler;
		initialise();
	}
	
	public ExtendedDialogBox(String title, String caption, boolean autoHide, boolean modal, ClickHandler clickHandler) {
		this(autoHide, modal, clickHandler);
		super.setText(title);
		//super.setTitle(title);
		body.setMessage(caption);
	}
 
	public ExtendedDialogBox(boolean autoHide) {
		this(autoHide, true);
	}
	
	public void initialise()
	{
		prefetchIcons();
		body = new DialogBoxBody();
		this.setWidget(body);
		setIcon();
		setButtons();
	}
	
	public static void prefetchIcons()
	{
		Image.prefetch("images/alert24.png");
		Image.prefetch("images/accepted24.png");
		Image.prefetch("images/cancel24.png");
		Image.prefetch("images/close24.png");
		Image.prefetch("images/delete24.png");
		Image.prefetch("images/info24.png");
		Image.prefetch("images/smile24.png");
	}
	
	protected void setIcon()
	{
		body.setIcon(ICON_INFO);
	}
	
	protected void setIcon(Image icon)
	{
		body.setIcon(icon);
	}
	
	protected void setButtons()
	{
		okButton = new Button(constants.controls_ok());
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ExtendedDialogBox.this.hide(true);
			}
		});
		
		body.setButtons(okButton);
	}
	
	protected void setButtons(Button... buttons)
	{
		body.setButtons(buttons);
	}
	
	/*
	@Override	
	public void setText(String caption)
	{
		body.setMessage(caption);
	}
	*/
	
	public void setTextPanel(Widget panel)
	{
		body.setTextPanel(panel);
	}
	
	@Override
	public void setTitle(String title)
	{
		super.setTitle(title);
	}
	
	@Override
	public void setWidth(String width)
	{
		body.setWidth(width);
	}
	
	@Override
	public void setHeight(String height)
	{
		body.setHeight(height);
	}
	
	@Override
	public void setSize(String width, String height)
	{
		body.setWidth(width);
		body.setHeight(height);
	}
	
	@Override
	public void show()
	{
		super.show();
		if(okButton != null)
			okButton.setFocus(true);
	}
	
	protected class DialogBoxBody extends VerticalPanel {
		
		private VerticalPanel bodyPanel = new VerticalPanel();
		private HorizontalPanel contentPanel = new HorizontalPanel();
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		HorizontalPanel buttonPanelContainer = new HorizontalPanel();
		private HTML messageLabel = new HTML();
		private Image icon;
		
		public DialogBoxBody() {
			autoSize();
			layOut();
		}
		
		protected void layOut()
		{			
			this.setStyleName("gwt-DialogBox");
			messageLabel.setWordWrap(true);
			
			contentPanel.setSpacing(10);
			contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
			buttonPanel.setSpacing(5);
			buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			buttonPanelContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			buttonPanelContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			buttonPanelContainer.add(buttonPanel);
			
			bodyPanel.add(contentPanel);
			bodyPanel.add(buttonPanelContainer);
			
			this.add(bodyPanel);
		}
		
		protected void autoSize()
		{
			this.setHeight("150px");
			this.setWidth("350px");
		}
		
		@Override
		public void setWidth(String width)
		{
			super.setWidth(width);
			contentPanel.setWidth(width);
			bodyPanel.setWidth(width);
			buttonPanelContainer.setWidth(width);
		}
		
		@Override
		public void setHeight(String height)
		{
			super.setHeight(height);
			contentPanel.setHeight((super.getOffsetHeight() - buttonPanel.getOffsetHeight()) + "px");
		}
		
		public void setMessage(String caption)
		{
			contentPanel.clear();
			messageLabel.setHTML(caption);
			contentPanel.add(icon);
			contentPanel.add(messageLabel);
		}
		
		public void setTextPanel(Widget panel)
		{
			contentPanel.clear();
			contentPanel.add(panel);
		}
		
		public void setIcon(Image image)
		{
			this.icon = image;
			contentPanel.add(icon);
		}
		
		public void setButtons(Button... buttons) {
			buttonPanel.clear();
			for (int i = 0; i < buttons.length; i++)
			{
//				Button button = buttons[i];
				buttonPanel.add(buttons[i]);
			}
		}
	}
}
