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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Custom Popup Dialog.
 * 
 * @author Werner Liebenberg
 * @author Wildrich Fourie
 */
public class PopupDialog extends DialogBox {

	protected DialogBoxBody body;

	public PopupDialog(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		this.setGlassEnabled(true);
		initialise();
	}
	
	public PopupDialog(String title, Widget content, boolean autoHide, boolean modal) {
		this(autoHide, modal);
		this.setGlassEnabled(true);
		//body.setTitle(title);
		super.setText(title);
		body.setBody(content);
		this.updateDimensions();
	}
	
	public PopupDialog(String title, Widget content) {
		this(false, true);
		this.setGlassEnabled(true);
		//body.setTitle(title);
		super.setText(title);
		body.setBody(content);
		this.updateDimensions();
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 * @param content
	 * @param width in pixels
	 * @param height in pixels
	 */
	public PopupDialog(String title, Widget content, int width, int height)
	{
		this(false, false);
		this.setGlassEnabled(true);
		body.setTitle(title);
		body.setBody(content);
		body.setSize(width + "px", height + "px");
		this.updateDimensions();
	}
	
	public static PopupDialog show(String title, Widget content, boolean autoHide, boolean modal) {
		PopupDialog dialog = new PopupDialog(title, content, autoHide, modal);
		dialog.center();
		dialog.show();
		return dialog;
	}

	/**
	 * 
	 * @param title
	 * @param content
	 * @param width in pixels
	 * @param height in pixels
	 */
	public static PopupDialog show(String title, Widget content, int width, int height) {
		PopupDialog dialog = new PopupDialog(title, content, width, height);
		dialog.center();
		dialog.show();
		return dialog;
	}
	
	public static PopupDialog show(String title, Widget content) {
		PopupDialog dialog = new PopupDialog(title, content);
		dialog.center();
		dialog.show();
		return dialog;
	}
	
	public void initialise()
	{
		body = new DialogBoxBody();
		this.setWidget(body);
		setButtons();
	}
	
	public void updateDimensions()
	{
		this.center();
		//body.updateDimensions();
	}
	
	protected void setButtons()
	{
		Button okButton = new Button("OK");
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PopupDialog.this.hide(true);
			}
		});
		
		body.setButtons(okButton);
	}
	
	public void setButtons(Button... buttons)
	{
		body.setButtons(buttons);
	}
	
	/*
	public void setTitle(String title)
	{
		body.setTitle(title);
	}
	*/
	
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
	
	public void setBodyWidget(Widget widget)
	{
		body.setBody(widget);
	}
	
	protected class DialogBoxBody extends VerticalPanel {
		
		private VerticalPanel titlePanel = new VerticalPanel();
		private VerticalPanel bodyPanel = new VerticalPanel();
		private HorizontalPanel contentPanel = new HorizontalPanel();
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		//private Label titleLabel = new Label();
		private Widget bodyContent = new VerticalPanel();
		
		public DialogBoxBody() {
			autoSize();
			layOut();
		}
		
		protected void layOut()
		{
			contentPanel.setSpacing(10);
			contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			contentPanel.add(bodyContent);

			buttonPanel.setSpacing(5);
			buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			bodyPanel.add(contentPanel);
			bodyPanel.add(buttonPanel);
			
			//this.add(titlePanel);
			this.add(bodyPanel);
		}
		
		protected void autoSize()
		{
			this.setHeight("150px");
			this.setWidth("350px");
		}
		
		@Override
		public void setSize(String width, String height)
		{
			this.setWidth(width);
			this.setHeight(height);
		}
		
		@Override
		public void setWidth(String width)
		{
			super.setWidth(width);
			//titlePanel.setWidth(width);
			bodyPanel.setWidth(width);
			contentPanel.setWidth(width);
			bodyContent.setWidth(width);
			//buttonPanel.setWidth(width);
		}
		
		@Override
		public void setHeight(String height)
		{
			super.setHeight(height);
			contentPanel.setHeight((super.getOffsetHeight() - titlePanel.getOffsetHeight() - buttonPanel.getOffsetHeight()) + "px");
			bodyContent.setHeight(contentPanel.getOffsetHeight() + "px");
		}
		
		/*
		public void setTitle(String title)
		{
			titlePanel.clear();
			titleLabel.setText(title);
			titlePanel.add(titleLabel);
		}
		*/
		
		public void setBody(Widget widget)
		{
			contentPanel.clear();
			this.bodyContent = widget;
			contentPanel.add(this.bodyContent);
		}
		
		/*
		public void updateDimensions()
		{
			//titlePanel.setWidth(contentPanel.getOffsetWidth() + "px");
			//buttonPanel.setWidth(contentPanel.getOffsetWidth() + "px");
		}
		*/
		
		public void setButtons(Button... buttons) {
			buttonPanel.clear();
			for (int i = 0; i < buttons.length; i++)
			{
				buttonPanel.add(buttons[i]);
			}
		}
	}
}
