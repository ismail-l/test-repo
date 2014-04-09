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

package tms2.client.util;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import tms2.client.i18n.Internationalization;
import tms2.client.widgets.FixedPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FooterUtility 
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	public static FixedPanel getFooter()
	{
		FixedPanel footer = new FixedPanel();
		footer.addStyleName("footer");
		footer.getElement().getStyle().setLeft(0, Unit.PX);
		footer.getElement().getStyle().setBottom(0, Unit.PX);
		
		HorizontalPanel footer_panel = new HorizontalPanel();
		footer_panel.setSpacing(3);

		// Contact Developer label
		HTML contact = new HTML(_i18n.getConstants().contact());

		// Terms and Conditions
		HTML terms = new HTML(_i18n.getConstants().disclaimer2());
		
		// Help
		HTML help = new HTML(_i18n.getConstants().help());
		
		footer_panel.add(contact);
		footer_panel.add(new HTML(" | "));
		footer_panel.add(terms);
		footer_panel.add(new HTML(" | "));
		footer_panel.add(help);
		
		footer.add(footer_panel);	
		
		return footer;
	}
}
