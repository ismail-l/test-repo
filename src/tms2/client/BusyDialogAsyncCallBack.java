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

package tms2.client;

import tms2.client.i18n.Internationalization;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class which contains a {@link BusyDialog} and implements an {@link AsyncCallback}.
 * The {@link BusyDialog} will be run everytime this class is used.
 * 
 * @author 21620695
 *
 * @param <T>
 */
public abstract class BusyDialogAsyncCallBack<T> implements AsyncCallback<T> 
{
	private BusyDialog _busy = null;
	
	public BusyDialogAsyncCallBack(Widget widget)
	{
		if (_busy == null)
			_busy = new BusyDialog();
		
		_busy.centerOnParent(widget);
	}

	@Override
    public final void onSuccess(T result) 
    {      
		_busy.hide();
		onComplete(result);    
    }
	
	@Override
	public final void onFailure(Throwable caught) 
    {    
		_busy.hide();
		onError(caught); 
    } 
	
	public abstract void onComplete(T result);	
	public abstract void onError(Throwable caught);
	
	private static class BusyDialog extends DialogBox
	{
		private Internationalization _i18n = Internationalization.getInstance();
		
		public BusyDialog()
		{
			super(false, true);
			layout();
		}
		
		private void layout()
		{
			VerticalPanel busy_panel = new VerticalPanel();
			
			busy_panel.setSpacing(5);
			
			Button text = new Button();		
			text.setText(_i18n.getConstants().busy_dialog_busy());
			text.setEnabled(false);
					
			busy_panel.add(text);				
									
			Image loader = new Image("images/ajax-loader.gif");
									
			busy_panel.add(loader);
			
			busy_panel.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);
			busy_panel.setCellHorizontalAlignment(text, HasHorizontalAlignment.ALIGN_CENTER);
			busy_panel.setCellVerticalAlignment(loader, HasVerticalAlignment.ALIGN_MIDDLE);
					
			add(busy_panel);	
		}
		
		private void centerOnParent(Widget parent)
		{			
			if (parent == null)
				center();
			else
			{			
				int parent_height = parent.getOffsetHeight(); 
				int parent_width = parent.getOffsetWidth(); 
								
				int busy_dialog_width = 230;
				int busy_dialog_height = 65;
							
				int middle_left = (parent_width / 2) - (busy_dialog_width / 2);
				int middle_top = (parent_height / 2) - (busy_dialog_height / 2);
				
				int parent_top = parent.getAbsoluteTop();
				int parent_left = parent.getAbsoluteLeft();
				
				int left = parent_left + middle_left;
				int top = parent_top + middle_top;
				
				setPopupPosition(Math.abs(left), Math.abs(top));
			}
		}
	}
}
