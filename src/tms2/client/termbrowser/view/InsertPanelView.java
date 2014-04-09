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

import java.util.ArrayList;
import java.util.HashMap;

import tms2.client.i18n.Internationalization;
import tms2.client.termbrowser.presenter.InsertPanelPresenter;
import tms2.client.widgets.AttachedPanel;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author I. Lavangee
 *
 */
public class InsertPanelView extends Composite implements InsertPanelPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static HashMap<String, String[]> _diacritics;
	static 
	{
		_diacritics = new HashMap<String, String[]>();
		_diacritics.put("Afrikaans", new String[] {"à","á","â","ã","ä","è","é","ê","ë","í","î","ï","ó","ô","ö","ù","ú","û","ü","ý" });		
		_diacritics.put("Setswana / Sepedi", new String[] {"Š","š"});
		_diacritics.put("Tshivenḓa", new String[] {"Ḓ","Ḽ","Ṋ","Ṅ","Ṱ","ḓ","ḽ","ṋ","ṅ","ṱ"});
		_diacritics.put("Special Characters", new String[] {"¹","²","³","₁","₂","₃","©","®","™","°","±","µ","¶","¢","¥","£","€"});
		_diacritics.put("Mathematical", new String[] {"-","+","x","÷","°","<",">","≤","≥","√","π","½","¼","¾"});
	}
	
	private AttachedPanel _main_content_panel = null;	
	private Button _btn_cancel = null;
	private ArrayList<Button> _diacritic_buttons = null;
	
	public InsertPanelView(Button btn_parent)
	{
		_main_content_panel = new AttachedPanel(btn_parent);
		
		_main_content_panel.getElement().getStyle().setZIndex(10000);
				
		initWidget(_main_content_panel);
		
		buildPanelContents();
	}
	
	public void buildPanelContents()
	{				
		HorizontalPanel main = new HorizontalPanel();
		
		VerticalPanel tree_panel = new VerticalPanel();														
		
		Tree language_tree = new Tree();
				
		_diacritic_buttons = new ArrayList<Button>();
				
		Object[] keys = _diacritics.keySet().toArray();
		int limit = _diacritics.keySet().size();
		
		for (int i = 0; i < limit; i++) 
		{
			final String current_language = keys[i].toString();
			String[] current_diacritic = _diacritics.get(current_language);
			
			HorizontalPanel diacritic_button_panel = new HorizontalPanel();
			
			for (String diac : current_diacritic)
			{
				final Button diacritic_button = new Button(diac);
					
				_diacritic_buttons.add(diacritic_button);
				
				diacritic_button_panel.add(diacritic_button);		
			}
			
			TreeItem tree_item = new TreeItem(new SafeHtml()
			{
				private static final long serialVersionUID = 4388930569635727660L;

				@Override
				public String asString() 
				{				
					return current_language;
				}				
			});
			
			tree_item.addItem(diacritic_button_panel);
			language_tree.addItem(tree_item);
		}
								
		tree_panel.add(language_tree);
											
		main.add(tree_panel);										
		main.setWidth("100%");	
		
		_main_content_panel.add(main);
		
		_btn_cancel = new Button(_i18n.getConstants().controls_cancel());
		_main_content_panel.add(_btn_cancel);
		
		_main_content_panel.setCellHorizontalAlignment(_btn_cancel, HasHorizontalAlignment.ALIGN_RIGHT);	
	}

	@Override
	public AttachedPanel getMainContentPanel() 
	{	
		return _main_content_panel;
	}

	@Override
	public Button getCancelButton() 
	{	
		return _btn_cancel;
	}

	@Override
	public ArrayList<Button> getDiacriticButtons() 
	{
		return _diacritic_buttons;
	}
}
