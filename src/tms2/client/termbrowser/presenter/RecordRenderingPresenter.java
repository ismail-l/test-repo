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

package tms2.client.termbrowser.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import tms2.client.accesscontrol.AccessController;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.service.FieldService;
import tms2.client.service.FieldServiceAsync;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedGrid;
import tms2.client.widgets.FormulaLabel;
import tms2.shared.AccessRight;
import tms2.shared.AuditableEvent;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Field;
import tms2.shared.IsAuditableEvent;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.RecordAttribute;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.TerminlogyObject;

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.allen_sauer.gwt.voices.client.SoundType;
import com.allen_sauer.gwt.voices.client.handler.PlaybackCompleteEvent;
import com.allen_sauer.gwt.voices.client.handler.SoundHandler;
import com.allen_sauer.gwt.voices.client.handler.SoundLoadStateChangeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class RecordRenderingPresenter implements Presenter
{
	private static Internationalization _i18n = Internationalization.getInstance();	
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static FieldServiceAsync _field_service = GWT.create(FieldService.class);
	
	private Display _display = null;
	
	private Record _record = null;
	
	private Field _source_field = null;
	private Field _target_field = null;
			
	public interface Display
	{
		public Label getRecordIdLabel();
		public Label getProjectLabel();
		public ListBox getProjectsListBox();
		public VerticalPanel getRecordDataPanel();
		public Widget asWidget();
	}
	
	public RecordRenderingPresenter(Display display, Record record, Field source, Field target)
	{
		_display = display;
		_record = record;
		_source_field = source;
		_target_field = target;				
	}
	
	public void render() 
	{
		if (_record == null)
			AlertBox.show(_i18n.getConstants().recordRender_noEntries());
		else
		{
			populateDetailsPanel();
			populateRecordDataPanel();
		}
	}
	
	private void populateDetailsPanel()
	{
		Label lbl_recordid = _display.getRecordIdLabel();
		
		addAuditTrail(lbl_recordid, _record);
		
		ListBox lst_project = _display.getProjectsListBox();
		
		lbl_recordid.setText(Long.toString(_record.getRecordId()));
		
		if (! _access_controller.isGuest())
		{
			_display.getProjectLabel().setVisible(true);
			
			ArrayList<Project> projects = _record.getProjects();
			
			if (projects != null && projects.size() > 0)
			{			
				Iterator<Project> iter = projects.iterator();
				while (iter.hasNext())
				{
					Project project = iter.next();
					
					AccessRight user_accessright = project.getUserAccessRight();
					AccessRight usercat_accessright = project.getUserCategoryAccessRight();
					
					if (hasReadAccess(user_accessright, usercat_accessright))
						lst_project.addItem(project.getProjectName());
				}			
			}	
		}
		
		if (lst_project.getItemCount() > 0)
		{
			if (lst_project.getItemCount() > 1)
			{
				lst_project.setVisibleItemCount(lst_project.getItemCount());
				lst_project.setSelectedIndex(-1);
			}
		}
		else
		{
			_display.getProjectLabel().setVisible(false);
			
			lst_project.setVisible(false);
		}
	}
	
	private void populateRecordDataPanel()
	{
		renderRecordAttributes();
			
		ArrayList<Term> shuffled_terms = shuffleTerms();
		
		if (shuffled_terms.size() > 0)
			renderTerms(shuffled_terms);	
	}
	
	private ArrayList<Term> shuffleTerms()
	{
		ArrayList<Term> shuffled = new ArrayList<Term>();
		ArrayList<TerminlogyObject> terms = new ArrayList<TerminlogyObject>();
		
		terms.addAll(_record.getTerms());
		
		if (terms == null || terms.size() == 0)
			return shuffled;
		
		// This could be the default sort field aswell.
		Term source_term = _record.getCustomSortIndexTerm(_source_field);
									
		if (source_term == null)				
			source_term =_record.getSortIndexTerm(_access_controller.getAppProperties().getSortIndexField());		
		
		shuffled.add(source_term);
		
		Term target_term = null;
		
		Iterator<TerminlogyObject> iter = terms.iterator();
		while (iter.hasNext())
		{
			Term term = (Term) iter.next();
			
			if (term.getFieldId() == source_term.getFieldId())			
				iter.remove();							
			else if (_target_field != null)
			{
				if (term.getFieldId() == _target_field.getFieldId())
				{
					target_term = term;
					iter.remove();					
				}	
				else
					shuffled.add(term);
			}
			else
				shuffled.add(term);
		}
		
		if (target_term != null)
			shuffled.add(1, target_term);
		
		return shuffled;
	}
	
	private void renderRecordAttributes()
	{
		VerticalPanel data_panel = _display.getRecordDataPanel();
		
		ArrayList<TerminlogyObject> record_attributes = _record.getRecordAttributes();
		
		if (record_attributes == null || record_attributes.size() == 0)
			return;
				
		Iterator<TerminlogyObject> iter = record_attributes.iterator();
		while (iter.hasNext())
		{
			RecordAttribute record_attrbute = (RecordAttribute) iter.next();
			
			AccessRight user_accessright  = record_attrbute.getUserAccessRight();
			AccessRight usecat_accessright = record_attrbute.getUserCategoryAccessRight();
			
			if (! hasReadAccess(user_accessright, usecat_accessright))
				continue;
			
			HorizontalPanel record_attribute_panel = new HorizontalPanel();
			record_attribute_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);			
			record_attribute_panel.addStyleName("borderedBlock");
			record_attribute_panel.setWidth("100%");
			
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSpacing(5);
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			
			Label lbl_fieldname = new Label(record_attrbute.getFieldName() + ":");						
			panel.add(lbl_fieldname);
			
			addAuditTrail(lbl_fieldname, record_attrbute);
			
			if (! renderOtherFieldDataType(panel, record_attrbute, record_attrbute.getCharData()))
			{
				Label lbl_fieldvalue = new Label(record_attrbute.getCharData());
				lbl_fieldvalue.addStyleName("plainLabelText");
				
				addAuditTrail(lbl_fieldvalue, record_attrbute);
				
				sizeValueLabel(lbl_fieldvalue);
								
				panel.add(lbl_fieldvalue);
			}
			
			record_attribute_panel.add(panel);
			data_panel.add(record_attribute_panel);				
		}
	}
	
	private void renderTerms(ArrayList<Term> terms)
	{
		VerticalPanel data_panel = _display.getRecordDataPanel();
						
		Iterator<Term> term_iter = terms.iterator();
		while (term_iter.hasNext())
		{
			Term term = term_iter.next();
			
			AccessRight user_accessright  = term.getUserAccessRight();
			AccessRight usecat_accessright = term.getUserCategoryAccessRight();
			
			if (! hasReadAccess(user_accessright, usecat_accessright))
				continue;
			
			VerticalPanel term_panel = new VerticalPanel();			
			term_panel.setStyleName("borderedBlock");
			term_panel.setWidth("100%");
			
			FlexTable termdetails_panel = new FlexTable();
			termdetails_panel.setCellPadding(3);
			
			Label lbl_fieldname = new Label(term.getFieldName() + ": ");
			lbl_fieldname.addStyleName("labelTextBold");
			lbl_fieldname.setText(term.getFieldName() + ": ");
			
			Label lbl_fieldvalue = new Label(term.getCharData());
			lbl_fieldvalue.addStyleName("highlightText");
			
			termdetails_panel.setWidget(0, 0, lbl_fieldname);
			termdetails_panel.setWidget(0, 1, lbl_fieldvalue);
			
			addAuditTrail(lbl_fieldname, term);
			addAuditTrail(lbl_fieldvalue, term);
			
			ArrayList<ChildTerminologyObject> term_attributes = term.getTermAttributes();
			if (term_attributes == null || term_attributes.size()== 0)	
			{
				term_panel.add(termdetails_panel);
				data_panel.add(term_panel);
				
				continue;
			}
			
			termdetails_panel.getFlexCellFormatter().setColSpan(0, 1, 2);
									
			int row = 0;
			int column = 0;
			
			ExtendedGrid details = new ExtendedGrid();
			details.setBordered(false);
			details.setCellPadding(0); 
			details.setCellSpacing(0);
						
			termdetails_panel.setWidget(1, 1, details);
			
			Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
			while (term_attr_iter.hasNext())
			{
				TermAttribute term_attribute = (TermAttribute) term_attr_iter.next();																												
				
				AccessRight user_termattr_accessright  = term_attribute.getUserAccessRight();
				AccessRight usecat_termattr_accessright = term_attribute.getUserCategoryAccessRight();
				
				if (! hasReadAccess(user_termattr_accessright, usecat_termattr_accessright))
				{
					termdetails_panel.remove(details);
					continue;
				}
				
				FlexTable term_attr_details = renderTermAttribute(term_attribute);
				
				details.setWidget(row, column, term_attr_details);
				
				if (column == 1 && term_attr_iter.hasNext())
				{
					details.insertRow(row);
					row++;
					column = 0;
				}
				else
					column = 1;				
			}
			
			term_panel.add(termdetails_panel);
			data_panel.add(term_panel);
		}
	}
	
	private FlexTable renderTermAttribute(TermAttribute term_attribute)
	{
		FlexTable term_attr_details = new FlexTable();
		
		Label lbl_fieldname = new Label(term_attribute.getFieldName() + ":");	
		
		addAuditTrail(lbl_fieldname, term_attribute);
		
		term_attr_details.setWidget(0, 0, lbl_fieldname);
		
		if (term_attribute.hasPresetFields())
		{
			ListBox lst_preset = new ListBox(false);
			lst_preset.setEnabled(false);
			lst_preset.addItem(term_attribute.getCharData());
									
			term_attr_details.setWidget(0, 1, lst_preset);
		}
		else
		{
			if (! renderOtherFieldDataType(term_attr_details, term_attribute, term_attribute.getCharData()))
			{
				Label lbl_fieldvalue = new Label(term_attribute.getCharData());
				
				addAuditTrail(lbl_fieldvalue, term_attribute);
				
				if(term_attribute.isSynonymField())
					lbl_fieldvalue.addStyleName("highlightText");
				else
					lbl_fieldvalue.addStyleName("plainLabelText");
				
				sizeValueLabel(lbl_fieldvalue);
				
				term_attr_details.setWidget(0, 1, lbl_fieldvalue);
			}
		}
		
		if (term_attribute.isSynonymField())
		{
			ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
			
			if (synonym_attributes != null && synonym_attributes.size() > 0)
			{
				int row = 0;
				int column = 0;
				
				ExtendedGrid details = new ExtendedGrid();
				details.setBordered(false);
				details.setCellPadding(0); 
				details.setCellSpacing(0);
							
				term_attr_details.setWidget(1, 1, details);
				
				Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
				while (synonym_attr_iter.hasNext())
				{
					SynonymAttribute synonym_attribute = (SynonymAttribute) synonym_attr_iter.next();										
						
					AccessRight user_accessright  = synonym_attribute.getUserAccessRight();
					AccessRight usecat_accessright = synonym_attribute.getUserCategoryAccessRight();
					
					if (! hasReadAccess(user_accessright, usecat_accessright))
					{
						term_attr_details.remove(details);
						continue;
					}
					
					FlexTable synonym_attr_details = renderSynonymAttribute(synonym_attribute);
					
					details.setWidget(row, column, synonym_attr_details);
					
					if (column == 1 && synonym_attr_iter.hasNext())
					{
						details.insertRow(row);
						row++;
						column = 0;
					}
					else
						column = 1;
				}
			}			
		}
		
		term_attr_details.getCellFormatter().addStyleName(0, 0, "contentSubblock");
		term_attr_details.getCellFormatter().addStyleName(0, 1, "contentSubblock");
		
		return term_attr_details;
	}
		
	private FlexTable renderSynonymAttribute(SynonymAttribute synonym_attribute)
	{
		FlexTable synonym_attr_details = new FlexTable();
		
		Label lbl_fieldname = new Label(synonym_attribute.getFieldName() + ":");	
		
		addAuditTrail(lbl_fieldname, synonym_attribute);
		
		synonym_attr_details.setWidget(0, 0, lbl_fieldname);
		
		if (synonym_attribute.hasPresetFields())
		{
			ListBox lst_preset = new ListBox(false);
			lst_preset.setEnabled(false);
			lst_preset.addItem(synonym_attribute.getCharData());
									
			synonym_attr_details.setWidget(0, 1, lst_preset);
		}
		else
		{
			if (! renderOtherFieldDataType(synonym_attr_details, synonym_attribute, synonym_attribute.getCharData()))
			{
				Label lbl_fieldvalue = new Label(synonym_attribute.getCharData());
				lbl_fieldvalue.addStyleName("plainLabelText");
				
				addAuditTrail(lbl_fieldvalue, synonym_attribute);
				
				sizeValueLabel(lbl_fieldvalue);
				
				synonym_attr_details.setWidget(0, 1, lbl_fieldvalue);
			}
		}
		
		synonym_attr_details.getCellFormatter().addStyleName(0, 0, "contentSubblock");
		synonym_attr_details.getCellFormatter().addStyleName(0, 1, "contentSubblock");
		
		return synonym_attr_details;
	}
	
	private boolean renderOtherFieldDataType(Panel panel, Field object, String chardata)
	{				
		if (object instanceof RecordAttribute)
			object = (RecordAttribute) object;
		else if (object instanceof TermAttribute)
			object = (TermAttribute) object;
		else
			object = (SynonymAttribute) object;
		
		if (object.isFormula())
		{
			renderDataTypeFormula(panel, object, chardata);
			return true;
		}
		else if (object.isHTMLHyperlink())
		{
			renderDataTypeHtmlLink(panel, object, chardata);
			return true;
		}
		
		return false;
	}
	
	private void renderDataTypeFormula(Panel panel, Field field, String chardata)
	{
		FormulaLabel label = new FormulaLabel();
				
		label.setStyleName("labelText");
		
		label.setText(chardata);
			
		addAuditTrail(new Label(chardata), (IsAuditableEvent) field);
		
		if (panel instanceof FlexTable)
			((FlexTable)panel).setWidget(0, 1, label);
		else if (panel instanceof HorizontalPanel)
			((HorizontalPanel)panel).add(label);
		else
			System.out.println("Unhandled panel type!");
	}
	
	private void renderDataTypeHtmlLink(final Panel panel, final Field field, final String chardata)
	{		
		 _field_service.getFieldDataMimeType(_access_controller.getAuthToken(), chardata, new AsyncCallback<String>() 
		 {
			@Override
			public void onSuccess(String result)
			{
				String url = GWT.getHostPageBaseURL();
				
				// NB: If we do not make sure there is no double slash, images do not load in
				//     Development Mode. 
				if (url.endsWith("/") && chardata.startsWith("/"))
					url += chardata.substring(1);
				else
					url += chardata;
				
				//if (!GWT.isScript())
				//	url += "?gwt.codesvr=127.0.0.1:9997";
				
				FlowPanel frame = new FlowPanel();
				
				if (result.startsWith("image/"))
				{
					// Render an image
					Image image = new Image(url);
					image.setAltText(url);
					image.setStyleName("picScale");
					frame.add(image);
				}
				else if (result.startsWith("audio/"))
				{
					final Button playButton = new Button(_i18n.getConstants().recordRender_sound_wait());
					final Button stopButton = new Button(_i18n.getConstants().recordRender_sound_stop());
					
					SoundController soundController = new SoundController();
					
					// Set HTML5 sound as the preferred, this method is deprecated
					// but .mp3
					soundController.setPreferredSoundTypes(SoundType.HTML5, SoundType.FLASH, SoundType.WEB_AUDIO, SoundType.NATIVE);
					
					final Sound sound = soundController.createSound(result, url);

					playButton.setEnabled(false);
					stopButton.setEnabled(false);
					stopButton.setVisible(false);

					// Add a sound handler so we know when the sound has loaded
					sound.addEventHandler(new SoundHandler()
					{
						@Override
						public void onPlaybackComplete(PlaybackCompleteEvent event)
						{
							// NB: This do not get called for .wav files for example, so we cannot
							//     enable/disable buttons.
							//playButton.setEnabled(true);
							//stopButton.setEnabled(true);
							System.out.println("Done playing");
						}

						@Override
						public void onSoundLoadStateChange(SoundLoadStateChangeEvent event)
						{
							switch (event.getLoadState())
							{
								case LOAD_STATE_SUPPORTED_AND_READY:
								case LOAD_STATE_SUPPORTED_NOT_READY:
								case LOAD_STATE_SUPPORTED_MAYBE_READY:
									playButton.setEnabled(true);
									playButton.setText(_i18n.getConstants().recordRender_sound_play());
									stopButton.setEnabled(true);
									stopButton.setVisible(true);
									break;
								case LOAD_STATE_SUPPORT_NOT_KNOWN:
									playButton.setEnabled(true);
									playButton.setText(_i18n.getConstants().recordRender_sound_play_notwork());
									stopButton.setEnabled(true);
									stopButton.setVisible(true);
									break;
								case LOAD_STATE_UNINITIALIZED:
								case LOAD_STATE_NOT_SUPPORTED:
								default:
									playButton.setText(_i18n.getConstants().recordRender_sound_unavailable());
									break;
							}
						}
					});

					playButton.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							//playButton.setEnabled(false);
							//stopButton.setEnabled(true);
							sound.stop();
							sound.play();
						}
					});
					stopButton.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							//playButton.setEnabled(true);
							//stopButton.setEnabled(false);
							sound.stop();
						}
					});
					
					frame.add(playButton);
					frame.add(stopButton);
				}
				else
				{
					Label label = new Label(url);
					frame.add(label);
				}
						
				addAuditTrail(new Label(url), (IsAuditableEvent) field);
				
				if (panel instanceof FlexTable)
					((FlexTable)panel).setWidget(0, 1, frame);
				else if (panel instanceof HorizontalPanel)
					((HorizontalPanel)panel).add(frame);
				else
					System.out.println("Unhandled panel type!");
			}
			
			@Override
			public void onFailure(Throwable caught) 
			{
				ErrorBox.ErrorHandler.handle(caught);
			}
		});
	}
	
	private void sizeValueLabel(Label label)
	{
		String text = label.getText();
		
		if (! containsSpace(text))
		{
			label.setWidth("250px");
			label.addStyleName("wordwrap");
		}
		else
		{
			label.setWidth("100%");
			label.setWordWrap(true);
		}
	}
	
	private boolean containsSpace(String string)
	{
		if (string.isEmpty())
			return false;
		
		RegExp regExp = RegExp.compile("\\s+");
		
		return regExp.test(string);		
	}
	
	private void addAuditTrail(final Label label, IsAuditableEvent event)
	{
		ArrayList<AuditableEvent> events = event.getAuditTrail();
		
		if (events == null || events.size() == 0)
			return;
		
		Collections.reverse(events);
		
		ArrayList<AuditableEvent> audits = new ArrayList<AuditableEvent>();
		
		int audit_limit = 2;
		int audit_count = 0;
		
		Iterator<AuditableEvent> iter = events.iterator();
		while (iter.hasNext())
		{
			AuditableEvent audit = iter.next();
			
			if (audit_count >= audit_limit)
				break;
			
			if (audit.canBeRendered())
			{
				audits.add(audit);
				audit_count++;
			}				
		}
		
		if (audits.size() > 0)
		{
			FlexTable audit_panel = new FlexTable();
			audit_panel.setCellSpacing(10);
			
			 DateTimeFormat formatter = DateTimeFormat.getFormat("dd MMMM yyyy");
			
			int row = 0;
			
			iter = audits.iterator();
			while (iter.hasNext())
			{
				AuditableEvent audit = iter.next();
				
				Label lbl_username = new Label(audit.getFullUserName());
				lbl_username.addStyleName("plainLabelText");
				
				Label lbl_date = new Label(formatter.format(audit.getTimestamp()));
				lbl_date.addStyleName("plainLabelText");
				
				Label lbl_eventname = new Label(audit.getEventTypeName() + ":");
				lbl_eventname.addStyleName("plainLabelText");
				
				Label lbl_chardata = new Label(audit.getCharData());
				lbl_chardata.addStyleName("plainLabelText");
				
				audit_panel.setWidget(row, 0, lbl_username);
				audit_panel.setWidget(row, 1, lbl_date);
				audit_panel.setWidget(row, 2, lbl_eventname);
				audit_panel.setWidget(row, 3, lbl_chardata);
				
				row++;
			}
						
			final PopupPanel popup = new PopupPanel(true);
			popup.setAnimationEnabled(true);
			popup.setWidget(audit_panel);

			popup.setStyleName("auditTrailPopup");
							
			label.addMouseOverHandler(new MouseOverHandler() 
			{										
				@Override
				public void onMouseOver(MouseOverEvent event) 
				{		
					popup.showRelativeTo(label);
				}
			});
			
			label.addMouseOutHandler(new MouseOutHandler() 
			{					
				@Override
				public void onMouseOut(MouseOutEvent event) 
				{		
					popup.hide();						
				}
			});					
		}
	}
	
	private boolean hasReadAccess(AccessRight user, AccessRight user_cat)
	{
		if (user == null && user_cat == null)
			return false;		
		
		if (user != null && user.mayRead())
			return user.mayRead();
		else if (user_cat != null && user_cat.mayRead())
			return user_cat.mayRead();		
		
		return false;
	}
	
	@Override
	public void go(HasWidgets container) 
	{
		container.add(_display.asWidget());
	}
}
