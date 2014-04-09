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
import java.util.Iterator;

import tms2.client.BusyDialogAsyncCallBack;
import tms2.client.accesscontrol.AccessController;
import tms2.client.event.ExportEvent;
import tms2.client.event.SignOffEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.service.AccessRightService;
import tms2.client.service.AccessRightServiceAsync;
import tms2.client.util.AttachedPanelPositionUtility;
import tms2.client.widgets.AlertBox;
import tms2.client.widgets.AttachedPanel;
import tms2.client.widgets.ErrorBox;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.FieldFilterTree;
import tms2.client.widgets.FieldFilterTreeItem;
import tms2.client.widgets.HasSignOut;
import tms2.shared.AccessRight;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.ExportType;
import tms2.shared.InputModel;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import tms2.shared.TerminlogyObject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class ExportPresenter implements Presenter, HasSignOut
{
	private static Internationalization _i18n = Internationalization.getInstance();
	private static AccessController _access_controller = AccessController.getInstance();
	
	private static AccessRightServiceAsync _accessright__service = GWT.create(AccessRightService.class);
	
	private Display _display = null;
	
	private ArrayList<Button> _cb_state_buttons = null;
	
	private boolean _select_all = true;
	
	private boolean _retrieved_details = false;
		
	public interface Display
	{
		public static final String TAB = _i18n.getConstants().export_tab();
		public static final String ODT = _i18n.getConstants().export_odt();
		public static final String TBX = _i18n.getConstants().export_tbx();
		
		public static final String EXPORT_TYPE = "exportType";
		
		public AttachedPanel getExportPanel();
		public ListBox getExportListBox();
		public TextBox getExportFileNameTextBox();
		public CheckBox getExportFileNameCheckBox();
		public ExtendedListBox<TerminlogyObject> getSourceListBox();
		public ExtendedListBox<TerminlogyObject> getTargetListBox();
		public FieldFilterTree getFieldFilterTree();
		public DeckPanel getExportTypePanel();
		public Button getSelectAllButton();
		public Button getGenerateButton();
		public Button getDownloadButton();
		public Button getCloseButtonHandler();
		public Widget asWidget();
	}
	
	public ExportPresenter(Display display, ArrayList<Button> cb_state_buttons)
	{
		_display = display;
		_cb_state_buttons = cb_state_buttons;
		
		_access_controller.addSignOut(this);
	}
	
	private void bind()
	{
		AttachedPanelPositionUtility.addPosition(_display.getExportPanel());
		
		addExportListBoxHandler();
		addFilterFieldTreeHandler();
		addSelectAllButtonHandler();
		addGenerateButtonHandler();
		addDownloadButtonHandler();
		addCloseButtonHandler();
	}
	
	public void retrieveExportDetails()
	{
		_display.getExportListBox().setSelectedIndex(0);
		_display.getExportTypePanel().showWidget(0);
		_display.getExportFileNameCheckBox().setVisible(false);
		_display.getDownloadButton().setEnabled(false);
		_display.getExportFileNameTextBox().setText("");
		
		if (! _access_controller.isGuest())
		{
			_accessright__service.getAccessRightInputModel(_access_controller.getAuthToken(), new BusyDialogAsyncCallBack<InputModel>(null)
			{
				@Override
				public void onComplete(InputModel result) 
				{
					if (result == null)
					{
						AlertBox.show(_i18n.getConstants().log_ims());
						return;
					}
					
					populateTabListBoxes(result);
					populateFieldFilterTree(result);
					
					_retrieved_details = true;
				}
	
				@Override
				public void onError(Throwable caught) 
				{
					ErrorBox.ErrorHandler.handle(caught);
					_retrieved_details = false;
				}			
			});
		}
		else
			_access_controller.getEventBus().fireEvent(new SignOffEvent());
	}
	
	private void addExportListBoxHandler()
	{
		final ListBox lst_export = _display.getExportListBox();
		lst_export.addChangeHandler(new ChangeHandler() 
		{			
			@Override
			public void onChange(ChangeEvent event) 
			{
				_display.getExportFileNameCheckBox().setVisible(false);
				
				String export_type = lst_export.getItemText(lst_export.getSelectedIndex());
				
				if (export_type.equals(Display.TAB))
					_display.getExportTypePanel().showWidget(0);
				else if (export_type.equals(Display.ODT))
				{
					_display.getExportTypePanel().showWidget(1);
					_display.getExportFileNameCheckBox().setVisible(true);
				}
				else
					_display.getExportTypePanel().showWidget(2);
			}
		});
	}
		
	private void addFilterFieldTreeHandler()
	{
		_display.getFieldFilterTree().addSelectionHandler(new SelectionHandler<TreeItem>()
		{
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) 
			{
				TreeItem tree_item = event.getSelectedItem();
				if (tree_item != null && tree_item instanceof FieldFilterTreeItem)
				{
					if (! ((FieldFilterTreeItem)tree_item).isDisabled())
						((FieldFilterTreeItem)tree_item).showControls();
				}
			}
		});
	}
		
	private void addSelectAllButtonHandler()
	{
		final Button _btn_select_all = _display.getSelectAllButton();
		
		_btn_select_all.addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				_display.getFieldFilterTree().toggleActivate(_select_all);
				
				if (_select_all)
					_select_all = false;
				else
					_select_all = true;
			}
		});
	}
	
	private void addGenerateButtonHandler()
	{
		final HandlerManager event_bus = _access_controller.getEventBus();
		
		_display.getGenerateButton().addClickHandler(new ClickHandler()
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				if (validateExport())
				{
					ExportType export_type = generateExportType();	
					
					event_bus.fireEvent(new ExportEvent(ExportPresenter.this, export_type,  
							            URL.encodeQueryString(_display.getExportFileNameTextBox().getText())));
				}
				else
					AlertBox.show(_i18n.getConstants().export_validate());
				
				_display.getDownloadButton().setEnabled(false);
			}
		});
	}
	
	private void addDownloadButtonHandler()
	{
		final Button btn_download = _display.getDownloadButton();
		
		btn_download.addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				System.out.println("Downloading document");
				String baseURL = com.google.gwt.core.client.GWT.getModuleBaseURL();
				String authToken = _access_controller.getAuthToken();
				String exportType = Integer.toString(_display.getExportListBox().getSelectedIndex());
				String parameters = "?authToken=" + ((authToken == null) ? "" : URL.encodeQueryString(authToken)) +
						"&" + Display.EXPORT_TYPE + "=" + URL.encodeQueryString(exportType);
				String url = baseURL + "exportService" + parameters;
							
				Window.open(url, "", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=no,toolbar=true,width=" + 
				Window.getClientWidth() + ",height=" + Window.getClientHeight());
				
				btn_download.setEnabled(false);	
			}
		});
	}
	
	private void addCloseButtonHandler()
	{
		_display.getCloseButtonHandler().addClickHandler(new ClickHandler() 
		{			
			@Override
			public void onClick(ClickEvent event) 
			{
				Iterator<Button> iter = _cb_state_buttons.iterator();
				while (iter.hasNext())
				{
					Button button = iter.next();
					button.setEnabled(true);
				}
				
				_display.getExportPanel().hide();
			}
		});
	}
	
	private void populateTabListBoxes(InputModel inputmodel)
	{
		ArrayList<TerminlogyObject> terms = inputmodel.getTerms();
		
		ExtendedListBox<TerminlogyObject> lst_source = _display.getSourceListBox();
		lst_source.setEnabled(true);
		lst_source.clear();
		
		ExtendedListBox<TerminlogyObject> lst_target = _display.getTargetListBox();
		lst_target.setEnabled(true);
		lst_target.clear();
		
		Iterator<TerminlogyObject> iter = terms.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject term = iter.next();
			
			AccessRight access_right = term.getUserCategoryAccessRight();
			if (! hasExportAccess(access_right))
				continue;
			
			lst_source.addItem(term.getFieldName(), term.getFieldId(), term);
			lst_target.addItem(term.getFieldName(), term.getFieldId(), term);
		}
		
		if (lst_source.getItemCount() == 0)
		{
			lst_source.setEnabled(false);
			lst_target.setEnabled(false);
		}			
	}
	
	private void populateFieldFilterTree(InputModel inputmodel)
	{
		FieldFilterTree tree = _display.getFieldFilterTree();
		tree.populateTreeWithInputModel(inputmodel);
		
		TreeItem record_attribute_item = tree.getRecordAttributeTreeItem();
		int record_attributes = record_attribute_item.getChildCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			FieldFilterTreeItem tree_item = (FieldFilterTreeItem) record_attribute_item.getChild(i);
			TerminlogyObject terminology_object = tree_item.getTerminologyObject();
			
			if (! hasExportAccess(terminology_object.getUserCategoryAccessRight()))						
				tree_item.setIsDisabled(true, true);											
		}
		
		TreeItem term_item = tree.getTermsTreeItem();
		int terms = term_item.getChildCount();
		
		for (int i = 0; i < terms; i++)
		{
			FieldFilterTreeItem tree_item = (FieldFilterTreeItem) term_item.getChild(i);
			TerminlogyObject term = tree_item.getTerminologyObject();
			
			if (! hasExportAccess(term.getUserCategoryAccessRight()))
			{
				tree_item.setIsDisabled(true, true);	
				
				int term_attributes = tree_item.getChildCount();
				
				for (int j = 0; j < term_attributes; j++)
				{
					FieldFilterTreeItem term_attr_tree_item = (FieldFilterTreeItem) tree_item.getChild(j);
										
					term_attr_tree_item.setIsDisabled(true, false);	
					
					int synonym_attributes = term_attr_tree_item.getChildCount();
																							
					for (int k = 0; k < synonym_attributes; k++)
					{
						FieldFilterTreeItem synonym_attr_tree_item = (FieldFilterTreeItem) term_attr_tree_item.getChild(k);
											
						synonym_attr_tree_item.setIsDisabled(true, false);	
					}					
				}
			}
			else
			{				
				int term_attributes = tree_item.getChildCount();
				
				for (int j = 0; j < term_attributes; j++)
				{
					FieldFilterTreeItem term_attr_item = (FieldFilterTreeItem) tree_item.getChild(j);
					TerminlogyObject term_attribute = term_attr_item.getTerminologyObject();
					
					if (! hasExportAccess(term_attribute.getUserCategoryAccessRight()))
					{
						term_attr_item.setIsDisabled(true, true);		
						
						int synonym_attributes = term_attr_item.getChildCount();
						
						for (int k = 0; k < synonym_attributes; k++)
						{
							FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
								
							synonym_attr_item.setIsDisabled(true, false);							
						}
					}
					else
					{
						int synonym_attributes = term_attr_item.getChildCount();
						
						for (int k = 0; k < synonym_attributes; k++)
						{
							FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
							TerminlogyObject synonym_attribute = synonym_attr_item.getTerminologyObject();
							
							if (! hasExportAccess(synonym_attribute.getUserCategoryAccessRight()))							
								synonym_attr_item.setIsDisabled(true, true);							
						}
					}
				}
			}
		}		
	}
	
	private boolean hasExportAccess(AccessRight access_right)
	{
		if (access_right == null)
			return false;
		
		return access_right.mayExport();
	}
	
	private boolean validateExport()
	{
		if (_display.getExportFileNameTextBox().getText().isEmpty())
			return false;
		
		return true;
	}
	
	private ExportType generateExportType()
	{
		ExportType export_type = new ExportType();
		
		switch (_display.getExportListBox().getSelectedIndex())
		{
			case 0:
			{
				export_type.setSourceField(_display.getSourceListBox().getSelectedItem());
				export_type.setTargetField(_display.getTargetListBox().getSelectedItem());
				export_type.setExportType(Display.TAB);	
				
				break;
			}
			case 1:
			{
				FieldFilterTree tree = _display.getFieldFilterTree();
				TreeItem record_attr_item = tree.getRecordAttributeTreeItem();
				int record_attributes = record_attr_item.getChildCount();
				
				for (int i = 0; i < record_attributes; i++)
				{
					FieldFilterTreeItem tree_item = (FieldFilterTreeItem) record_attr_item.getChild(i);
					
					if (tree_item.isActive() && ! tree_item.isDisabled())					
						export_type.addRecordField(tree_item.getTerminologyObject());										
				}
				
				TreeItem terms_item = tree.getTermsTreeItem();
				int terms = terms_item.getChildCount();
				
				for (int i = 0; i < terms; i++)
				{
					FieldFilterTreeItem term_tree_item = (FieldFilterTreeItem) terms_item.getChild(i);
					if (term_tree_item.isActive() && ! term_tree_item.isDisabled())
					{		
						TerminlogyObject term = term_tree_item.getTerminologyObject();
						((Term)term).getTermAttributes().clear();
						
						if (export_type.getSourceField() == null)											
							export_type.setSourceField(term);						
						else if (export_type.getTargetField() == null)						
							export_type.addTargetField(term);						
													
						int term_attributes = term_tree_item.getChildCount();
						
						for (int j = 0; j < term_attributes; j++)
						{
							FieldFilterTreeItem term_attr_item = (FieldFilterTreeItem) term_tree_item.getChild(j);
							
							if (term_attr_item.isActive() && ! term_attr_item.isDisabled())
							{
								ChildTerminologyObject term_attribute = (ChildTerminologyObject) term_attr_item.getTerminologyObject();
								((Term)term).addTermAttribute((TermAttribute) term_attribute);
								
								if (term_attribute instanceof Synonym)
								{
									((Synonym)term_attribute).getSynonymAttributes().clear();
									int synonym_attributes = term_attr_item.getChildCount();
									
									for (int k = 0; k < synonym_attributes; k++)
									{
										FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
										if (synonym_attr_item.isActive() && ! synonym_attr_item.isDisabled())
										{
											ChildTerminologyObject synonym_attribute = (ChildTerminologyObject) synonym_attr_item.getTerminologyObject();
											((Synonym)term_attribute).addSynonymAttribute((SynonymAttribute) synonym_attribute);
										}
									}
								}
							}
						}
					}
				}
				
				export_type.setIncludeFieldNames(_display.getExportFileNameCheckBox().getValue());
				export_type.setExportType(Display.ODT);					
				break;				
			}
			case 2:
			{
				export_type.setExportType(Display.TBX);
				break;
			}
		}
		
		return export_type;		
	}
	
	public void show()
	{
		_display.getExportPanel().position();
		_display.getExportPanel().show();
	}
	
	public boolean hasRetrievedDetails()
	{
		return  _retrieved_details;
	}
	
	@Override
	public void go(HasWidgets container) 
	{
		container.add(_display.asWidget());
		
		_display.getExportPanel().hide();
		
		bind();
	}
	
	public Display getDisplay()
	{
		return _display;
	}

	@Override
	public void signOut() 
	{
		_display.getExportPanel().hide();
	}
}
