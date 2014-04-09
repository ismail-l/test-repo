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
import tms2.client.termbrowser.presenter.SearchResultsPresenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class SearchResultsView extends Composite implements SearchResultsPresenter.Display
{
	private Internationalization _i18n = Internationalization.getInstance();
	
	private HorizontalPanel _search_panel = null;
	private VerticalPanel _content_panel = null;
	private ScrollPanel _scroller = null;
	private VerticalPanel _results_panel = null;
	private VerticalPanel _toggle_panel = null;
	private PushButton _btn_toggle_left = null;
	private PushButton _btn_toggle_right = null;
		
	public SearchResultsView()
	{
		_search_panel = new HorizontalPanel();
		_search_panel.setStylePrimaryName("searchResultsPanelClosed");
		
		initWidget(_search_panel);
		
		buildSearchPanel();
	}
	
	private void buildSearchPanel()
	{
		_content_panel = new VerticalPanel();
		_content_panel.setVisible(false);
		_content_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		Label lbl_label = new Label(_i18n.getConstants().search_results());
		lbl_label.addStyleName("labelTextBold");
		lbl_label.addStyleName("boxHeadingPanel");
		_content_panel.add(lbl_label);
		
		_results_panel = new VerticalPanel();
		_results_panel.setWidth("200px");
		_results_panel.setStylePrimaryName("boxContentPanel");
		
		_scroller = new ScrollPanel();
		_scroller.setWidth("100%");
		_scroller.setHeight((Window.getClientHeight() - 100)  + "px");
		
		_results_panel.add(_scroller);
		
		_content_panel.add(_results_panel);
		
		_search_panel.add(_content_panel);
		
		Image handleLeft = new Image("images/ExpanderHandleLeft.png");
		Image handleLeftHover = new Image("images/ExpanderHandleLeftHover.png");
		Image handleLeftDown = new Image("images/ExpanderHandleLeftDown.png");
		Image handleRight = new Image("images/ExpanderHandleRight.png");
		Image handleRightHover = new Image("images/ExpanderHandleRightHover.png");
		Image handleRightDown = new Image("images/ExpanderHandleRightDown.png");
		
		_btn_toggle_left = new PushButton(handleLeft, handleLeftDown);
		_btn_toggle_left.getUpHoveringFace().setImage(handleLeftHover);
		_btn_toggle_left.setStyleName("splitterButton");
		_btn_toggle_left.setWidth("20px");
		_btn_toggle_left.setHeight("240px");
		
		_btn_toggle_right = new PushButton(handleRight, handleRightDown);
		_btn_toggle_right.getUpHoveringFace().setImage(handleRightHover);
		_btn_toggle_right.setStyleName("splitterButton");
		_btn_toggle_right.setWidth("20px");
		_btn_toggle_right.setHeight("240px");
		
		_toggle_panel = new VerticalPanel();
		_toggle_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		_toggle_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		_toggle_panel.add(_btn_toggle_right);
		
		_search_panel.add(_toggle_panel);
	}

	@Override
	public HorizontalPanel getSearchPanel() 
	{
		return _search_panel;
	}

	@Override
	public VerticalPanel getContentPanel() 
	{
		return _content_panel;
	}

	@Override
	public VerticalPanel getResultsPanel() 
	{
		return _results_panel;
	}

	@Override
	public VerticalPanel getTogglePanel() 
	{
		return _toggle_panel;
	}

	@Override
	public PushButton getToggleLeftPushButton() 
	{
		return _btn_toggle_left;
	}

	@Override
	public PushButton getToggleRightPushButton() 
	{
		return _btn_toggle_right;
	}

	@Override
	public void setBlankResults() 
	{
		Label lbl_blank = new Label(_i18n.getConstants().search_noResults());
		lbl_blank.addStyleName("plainLabelText");
		
		_scroller.clear();
		_scroller.add(lbl_blank);
	}

	@Override
	public void setHeight(int height) 
	{
		_search_panel.setHeight(height + "px");
		_toggle_panel.setHeight(height + "px");
		_content_panel.setHeight(height + "px");
		_results_panel.setHeight(height + "px");
	}
	
	@Override
	public void updateToggleState(boolean is_open) 
	{
		if (is_open)
		{
			_search_panel.setStylePrimaryName("searchResultsPanelOpen");
			_toggle_panel.clear();
			_toggle_panel.add(_btn_toggle_left);
			_content_panel.setVisible(true);
		}
		else
		{
			_search_panel.setStylePrimaryName("searchResultsPanelClosed");
			_toggle_panel.clear();
			_toggle_panel.add(_btn_toggle_right);
			_content_panel.setVisible(false);
		}
	}

	@Override
	public ScrollPanel getScroller() 
	{
		return _scroller;
	}
}
