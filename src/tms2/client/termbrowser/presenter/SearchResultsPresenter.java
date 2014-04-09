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

import tms2.client.accesscontrol.AccessController;
import tms2.client.event.ResetNavigationEvent;
import tms2.client.i18n.Internationalization;
import tms2.client.presenter.Presenter;
import tms2.client.util.AttachedPanelPositionUtility;
import tms2.client.widgets.HasSignOut;
import tms2.shared.RecordElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SearchResultsPresenter implements Presenter, HasSignOut
{	
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private Display _display = null;
	
	private boolean _is_open = false;
		
	public interface Display
	{
		public HorizontalPanel getSearchPanel();
		public ScrollPanel getScroller();
		public VerticalPanel getContentPanel();
		public VerticalPanel getResultsPanel();
		public VerticalPanel getTogglePanel();
		public PushButton getToggleLeftPushButton();
		public PushButton getToggleRightPushButton();
		public void setBlankResults();
		public void setHeight(int height);
		public void updateToggleState(boolean is_open);
		public Widget asWidget();
	}
	
	public SearchResultsPresenter(Display display)
	{
		_display = display;
	}
	
	private void bind()
	{
		_display.setHeight(Window.getClientHeight());
		_display.setBlankResults();
		_display.getToggleLeftPushButton().addClickHandler(new ToggleHandler());
		_display.getToggleRightPushButton().addClickHandler(new ToggleHandler());
	}
	
	public void populateSearchResults(ArrayList<RecordElement> results, 
									  String search_prompt, String search_type)
	{
		ScrollPanel scrollPanel = _display.getScroller();
		
		VerticalPanel allHitsPanel = new VerticalPanel();
		allHitsPanel.setWidth("100%");
				
		if (results != null && results.size() > 0)
		{	
			Label resultsPrompt = new Label(_i18n.getMessages().search_result(search_type, search_prompt, "" + results.size()));
			resultsPrompt.addStyleName("plainLabelText");
			allHitsPanel.add(resultsPrompt);
			
			Iterator<RecordElement> itr = results.iterator();
			while (itr.hasNext())
			{
				final RecordElement term = itr.next();
				VerticalPanel hitPanel = new VerticalPanel();
				hitPanel.setStyleName("borderedBlock");
				hitPanel.setWidth("100%");
				hitPanel.setSpacing(5);
								
				Label termAnchor = new Label(term.getCharData().trim(), true);
				
				termAnchor.addStyleName("highlightText");	
				termAnchor.addStyleName("browsingText");
				termAnchor.addClickHandler(new ClickHandler() 
				{
					@Override
					public void onClick(ClickEvent event) 
					{
						HandlerManager event_bus = AccessController.getInstance().getEventBus();
						event_bus.fireEvent(new ResetNavigationEvent(term.getRecordId(), false));
					}
				});
				
				hitPanel.add(termAnchor);
				
				Label fieldLabel = new Label(term.getFieldName().trim());
				fieldLabel.addStyleName("plainLabelText");
				fieldLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);				
				hitPanel.add(fieldLabel);
				
				allHitsPanel.add(hitPanel);
			}
			
			scrollPanel.clear();
			scrollPanel.add(allHitsPanel);
		}
		else
		{
			Label noresults = new Label(_i18n.getMessages().search_no_result(search_type, search_prompt));
			noresults.addStyleName("plainLabelText");
			
			scrollPanel.clear();
			scrollPanel.add(noresults);
		}	
	}
	
	public void openSearchResultsPanel()
	{
		_display.getSearchPanel().setVisible(true);
		
		if (! _is_open)
		{
			_is_open = true;
			_display.updateToggleState(_is_open);
		}
	}
	
	public void closeSearchResultsPanel()
	{
		_is_open = false;
		_display.updateToggleState(_is_open);
	}
		
	public void disable()
	{
		closeSearchResultsPanel();
				
		_display.setBlankResults();
	}
		
	private class ToggleHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			_is_open = !_is_open;
			_display.updateToggleState(_is_open);
			
			AttachedPanelPositionUtility.positionWidgets();
		}
	}
		
	@Override
	public void go(HasWidgets container) 
	{		
		container.add(_display.asWidget());
		
		bind();
	}

	@Override
	public void signOut() 
	{
		disable();
	}
}
