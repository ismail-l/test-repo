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

import java.util.ArrayList;
import java.util.Iterator;

import tms2.client.i18n.Internationalization;
import tms2.client.service.CaptchaService;
import tms2.client.service.CaptchaServiceAsync;
import tms2.client.service.TermBaseService;
import tms2.client.service.TermBaseServiceAsync;
import tms2.client.termbrowser.presenter.InfoBarPresenter;
import tms2.shared.Field;
import tms2.shared.Record;
import tms2.shared.TerminlogyObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A representation of an Email form
 * @author Ismail Lavangee
 * @author Wildrich Fourie
 */
public class EmailForm extends PopupDialog
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static CaptchaServiceAsync _captcha = GWT.create(CaptchaService.class);
	private static  TermBaseServiceAsync _termbaseRetriever = GWT.create(TermBaseService.class);
		
	private Record _record = null;
	private Field _source_field = null;
	
	private int _row_index = 0;
	private int _index_row = 0;
			
	private VerticalPanel _main = new VerticalPanel();	
	private Image _captcha_image = null;
			
	private ExtendedListBox<TerminlogyObject> _index_list = new ExtendedListBox<TerminlogyObject>(false);
	
	private TextArea _txt_comments = new TextArea();
	private TextBox _txt_name = new TextBox();
	private TextBox _txt_tel = new TextBox();
	private TextBox _txt_mail = new TextBox();
	private TextArea _txt_inst = new TextArea();
	private TextArea _txt_expert = new TextArea();
	
	private InfoBarPresenter.Display _display = null;
	
	public EmailForm(InfoBarPresenter.Display display, Record record, Field field)
	{
		super(false, true);
			
		_display = display;
		
		int left = Window.getClientWidth() - 360;
		
		int scroll_top = Window.getScrollTop();
		int top = _display.getEmailButton().getOffsetHeight() + scroll_top + 1;
		
		setPopupPosition(left, top);
		
		setStyleName("AttachedPanel");
		
		_record = record;
		_source_field = field;
	}
				
	public void buildCurrentRecordForm()
	{				
		_main.setSpacing(5);
		
		final FlexTable table = new FlexTable();
		table.setWidth("100%");
								
		Label lbl_recordid = new Label(_i18n.getConstants().email_record_id());
		lbl_recordid.addStyleName("plainLabelText");
		lbl_recordid.setWidth("100px");
		
		Label lbl_recordid_value = new Label(Long.toString(_record.getRecordId()));
		lbl_recordid_value.addStyleName("plainLabelText");
		lbl_recordid_value.setWidth("100px");
		
		table.setWidget(_row_index, 0, lbl_recordid);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
				
		table.setWidget(_row_index, 1, lbl_recordid_value);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		
		_row_index++;
		_index_row = _row_index;
		
		Label lbl_indexfield = new Label(_i18n.getConstants().email_index_fields());
		lbl_indexfield.addStyleName("plainLabelText");
		lbl_indexfield.setWidth("100px");
				
		table.setWidget(_index_row, 0, lbl_indexfield);
		table.getCellFormatter().setVerticalAlignment(_index_row, 0, HasVerticalAlignment.ALIGN_TOP);
						
		loadIndexList(_index_list);
		_index_list.setWidth("243px");		
		
		table.setWidget(_index_row, 1, _index_list);
		table.getCellFormatter().setVerticalAlignment(_index_row, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_index_row, 1, HasHorizontalAlignment.ALIGN_LEFT);
					
		_row_index++;
		
		// Name label and text field.
		Label lbl_name = new Label(_i18n.getConstants().email_name());
		lbl_name.addStyleName("plainLabelText");
		lbl_name.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_name);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
		_txt_name.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_name);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		_row_index++;
		
		// Telephone number label and text field.
		Label lbl_tel = new Label(_i18n.getConstants().email_tel());
		lbl_tel.addStyleName("plainLabelText");
		lbl_tel.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_tel);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
		_txt_tel.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_tel);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		_row_index++;
		
		// E-mail address label and text field.
		Label lbl_mail = new Label(_i18n.getConstants().email_email());
		lbl_mail.addStyleName("plainLabelText");
		lbl_mail.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_mail);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
		_txt_mail.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_mail);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		_row_index++;
		
		// Institution label and text area.
		Label lbl_inst = new Label(_i18n.getConstants().email_inst());
		lbl_inst.addStyleName("plainLabelText");
		lbl_inst.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_inst);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
		_txt_inst.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_inst);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		_row_index++;
		
		// Field of Expertise label and text area.
		Label lbl_expert = new Label(_i18n.getConstants().email_expert());
		lbl_expert.addStyleName("plainLabelText");
		lbl_expert.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_expert);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
		_txt_expert.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_expert);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
		_row_index++;
		
		// Comments label and text area.
		Label lbl_comments = new Label(_i18n.getConstants().email_comments());
		lbl_comments.addStyleName("plainLabelText");
		lbl_comments.setWidth("100px");
		table.setWidget(_row_index, 0, lbl_comments);
		table.getCellFormatter().setVerticalAlignment(_row_index, 0, HasVerticalAlignment.ALIGN_TOP);
				
		_txt_comments.setWidth("237px");
		table.setWidget(_row_index, 1, _txt_comments);
		table.getCellFormatter().setVerticalAlignment(_row_index, 1, HasVerticalAlignment.ALIGN_TOP);	
		table.getCellFormatter().setHorizontalAlignment(_row_index, 1, HasHorizontalAlignment.ALIGN_LEFT);
																				
		final Button btn_send = new Button(_i18n.getConstants().email_send());
		btn_send.setEnabled(false);
		
		Button btn_close = new Button(_i18n.getConstants().email_close());
		
		final VerticalPanel captcha_panel = new VerticalPanel();
		captcha_panel.setSpacing(5);
		captcha_panel.setWidth("343px");
		
		if(GWT.isScript())
			_captcha_image = new Image("/tms2/verify_image.jpg?image_counter=" + InfoBarPresenter._captcha_index);
		else
			_captcha_image = new Image("/verify_image.jpg?image_counter=" + InfoBarPresenter._captcha_index);
		
		_captcha_image.setWidth("343px");
		_captcha_image.setHeight("50px");
								
		HorizontalPanel captcha_controls = new HorizontalPanel();
		captcha_controls.setSpacing(5);
		
		final Button btn_reload = new Button(_i18n.getConstants().email_reload());
		btn_reload.setWidth("60px");
				
		btn_reload.addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				reloadImage(captcha_panel);
				btn_send.setEnabled(false);
			}
		});
		
		final TextBox txt_captcha = new TextBox();
				
		captcha_controls.add(btn_reload);
		captcha_controls.setCellHorizontalAlignment(btn_reload, HasHorizontalAlignment.ALIGN_LEFT);
		
		captcha_controls.add(txt_captcha);
		captcha_controls.setCellHorizontalAlignment(txt_captcha, HasHorizontalAlignment.ALIGN_LEFT);
		
		captcha_panel.add(_captcha_image);
		captcha_panel.add(captcha_controls);
		
		HorizontalPanel button_panel = new HorizontalPanel();
		button_panel.setSpacing(5);
				
		btn_send.addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{		
				// Get email address form server. We can assume here that the 
				// verification code entered was correct.
				_termbaseRetriever.getEmailForTermbaseId(_record.getTermdbId(), new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result)
					{								
						mailto(result, "TMS: " + _record.getRecordId() + " : " + 
							   _record.getCustomSortIndexTerm(_source_field).getCharData(), buildMailBody());	
						
						reloadImage(captcha_panel);
						
						btn_send.setEnabled(false);
						txt_captcha.setText("");
						
					}
					
					@Override
					public void onFailure(Throwable caught)
					{ 
						ErrorBox.ErrorHandler.handle(caught); 
					}														
				});				
			}
		});
						
		btn_close.addClickHandler(new ClickHandler()
		{		
			@Override
			public void onClick(ClickEvent event) 
			{
				_display.getEmailButton().setEnabled(true);
				hide();				
			}
		});
				
		txt_captcha.addKeyUpHandler(new KeyUpHandler()
		{			
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				String captcha_text = txt_captcha.getText().trim();
				if (! captcha_text.isEmpty())
				{					
					_captcha.validateCaptcha(captcha_text, new AsyncCallback<Boolean>() 
					{					
						@Override
						public void onSuccess(Boolean result)
						{						
							if (result && 
									!_txt_name.getText().equals("") && 
									!_txt_tel.getText().equals("") && 
									!_txt_mail.getText().equals("") &&
									!_txt_inst.getText().equals("") &&
									!_txt_expert.getText().equals(""))
								btn_send.setEnabled(true);
							else
								btn_send.setEnabled(false);
						}
						
						@Override
						public void onFailure(Throwable caught)
						{
							ErrorBox.ErrorHandler.handle(caught);
						}
					});
				}
				else
					btn_send.setEnabled(false);
			}					
		});
				
		button_panel.add(btn_send);
		button_panel.add(btn_close);
		
		_main.add(table);	
		_main.add(captcha_panel);
		_main.add(button_panel);
		
		setWidget(_main);	
		
		_display.getEmailButton().setEnabled(false);
	}
			
	private String buildMailBody()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(_index_list.getSelectedItem().getFieldName() + ": " + _index_list.getSelectedItem().getCharData());
		sb.append("%0A");
							
		// Add all the other info (name, tel, mail, inst, expert)
		// No need to test for empty strings as the mail cannot be constructed
		// without all the fields being filled out.
		sb.append(_i18n.getConstants().email_name() + " " + _txt_name.getText() + "%0A");
		sb.append(_i18n.getConstants().email_tel() + " " + _txt_tel.getText() + "%0A");
		sb.append(_i18n.getConstants().email_email() + " " + _txt_mail.getText() + "%0A");
		sb.append(_i18n.getConstants().email_inst() + " " + _txt_inst.getText() + "%0A");
		sb.append(_i18n.getConstants().email_expert() + " " + _txt_expert.getText() + "%0A");
		sb.append(_i18n.getConstants().email_comments() + " " + _txt_comments.getText() + "%0A");									
				
		return sb.toString().replaceAll("\\s+", "%20");
	}
	
	
	private void loadIndexList(ExtendedListBox<TerminlogyObject> _index_list)
	{	
		ArrayList<TerminlogyObject> index_fields = _record.getTerms();
		
		Iterator<TerminlogyObject> iter = index_fields.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject term = iter.next();
			_index_list.addItem(term.getFieldName(), term.getFieldName(), term);
		}
		
		_index_list.setSelectedIndex(0);
	}	
	
	
	private void mailto(String address, String subject, String body)
	{
		Window.open("mailto:" + address + "?subject=" + subject + "&body=" + body, "Mailto", "_blank");
	}
	
	private void reloadImage(VerticalPanel captcha_panel)
	{
		int index = captcha_panel.getWidgetIndex(_captcha_image);
		captcha_panel.remove(_captcha_image);
		
		// Generate a new image
		InfoBarPresenter._captcha_index++;
		if(GWT.isScript())
			_captcha_image = new Image("/tms2/verify_image.jpg?image_counter=" + InfoBarPresenter._captcha_index);
		else
			_captcha_image = new Image("/verify_image.jpg?image_counter=" + InfoBarPresenter._captcha_index);
			
			
		//_captcha_image = new Image("/tms/verify_image.jpg?image_counter=" + _image_counter); 
		_captcha_image.setWidth("343px");
		_captcha_image.setHeight("50px");
		
		captcha_panel.insert(_captcha_image, index);				
	}
}
