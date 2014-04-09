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

import tms2.client.accesscontrol.AccessController;
import tms2.client.presenter.Presenter;
import tms2.client.presenter.TermBrowserControllerPresenter;
import tms2.client.termbrowser.EventBusManager;
import tms2.client.termbrowser.view.ControlBarView;
import tms2.client.termbrowser.view.InfoBarView;
import tms2.client.termbrowser.view.NavigatorView;
import tms2.client.termbrowser.view.RecordNavigatorView;
import tms2.client.termbrowser.view.SearchResultsView;
import tms2.client.widgets.FixedPanel;
import tms2.client.widgets.HasSignOut;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author I. Lavangee
 *
 */
public class TermBrowserPresenter implements Presenter, HasSignOut
{
	private static AccessController _access_controller = AccessController.getInstance();
	
	private Display _display = null;			
			
	public interface Display
	{
		public FixedPanel getFooter();
		public Widget asWidget();
	}
	
	public TermBrowserPresenter(Display display)
	{
		_display = display;		
		
		EventBusManager.manageTermBrowserEvents();
		
		_access_controller.addSignOut(this);
	}	
	
	@Override
	public void go(HasWidgets container) 
	{	
		container.add(_display.asWidget());
		
		Presenter ib_presenter = new InfoBarPresenter(new InfoBarView());
		ib_presenter.go(RootPanel.get("infobar"));
		
		Presenter cb_presenter = new ControlBarPresenter(new ControlBarView());
		cb_presenter.go(RootPanel.get("controlbar"));	
				
		Presenter n_presenter = new NavigatorPresenter(new NavigatorView());
		n_presenter.go(RootPanel.get("navigation"));
		
		Presenter sr_presenter = new SearchResultsPresenter(new SearchResultsView());
		sr_presenter.go(RootPanel.get("searchresults"));
		
		Presenter rn_presenter = new RecordNavigatorPresenter(new RecordNavigatorView(), 
															 (ControlBarPresenter) cb_presenter,
				 											 (InfoBarPresenter) ib_presenter,
															 (SearchResultsPresenter) sr_presenter);
		
		_access_controller.setTermBrowserControllerPresenter((TermBrowserControllerPresenter) rn_presenter);
		
		rn_presenter.go(RootPanel.get("recordnavigation"));
						
		RootPanel.get("footer").add(_display.getFooter());
	}

	@Override
	public void signOut() 
	{
		RootPanel.get("content").clear();		
	}	
}
