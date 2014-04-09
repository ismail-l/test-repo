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
import tms2.shared.ChildTerminologyObject;
import tms2.shared.InputModel;
import tms2.shared.Synonym;
import tms2.shared.Term;
import tms2.shared.TerminlogyObject;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldFilterTree extends Tree
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private TreeItem _record_attributes = null;
	private TreeItem _terms = null;
	private boolean _is_filter = false;
	
	public FieldFilterTree(boolean is_filter)
	{
		_is_filter = is_filter;
		
		_record_attributes = new TreeItem(new SafeHtml() 
		{
			private static final long serialVersionUID = -8334841230290311283L;

			@Override
			public String asString() 
			{
				return _i18n.getConstants().admin_im_labelRecordFields();
			}
		});

		addItem(_record_attributes);
		
		_terms = new TreeItem(new SafeHtml() 
		{			
			private static final long serialVersionUID = -2988703172226564036L;

			@Override
			public String asString() 
			{
				return _i18n.getConstants().admin_im_labelIndexFields();
			}
		});
		
		addItem(_terms);
		
		_record_attributes.addStyleName("indexFieldPanel");
		_record_attributes.addStyleName("labelTextBold");
		
		_terms.addStyleName("indexFieldPanel");
		_terms.addStyleName("labelTextBold");
	}
	
	public TreeItem getRecordAttributeTreeItem()
	{
		return _record_attributes;
	}
	
	public TreeItem getTermsTreeItem()
	{
		return _terms;
	}
	
	public void populateTreeWithInputModel(InputModel inputmodel)
	{
		_record_attributes.removeItems();
		_terms.removeItems();
		
		ArrayList<TerminlogyObject> record_attributes = inputmodel.getRecordAttributes();
		Iterator<TerminlogyObject> iter = record_attributes.iterator();
				
		while (iter.hasNext())
		{
			TerminlogyObject record_attribute = iter.next();				
			
			FieldFilterTreeItem tree_item = new FieldFilterTreeItem(record_attribute, _is_filter);
						
			_record_attributes.addItem(tree_item);
		}
		
		ArrayList<TerminlogyObject> terms = inputmodel.getTerms();
		iter = terms.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject term = iter.next();
						
			FieldFilterTreeItem tree_item = new FieldFilterTreeItem(term, _is_filter);
			_terms.addItem(tree_item);
			
			ArrayList<ChildTerminologyObject> term_attributes = ((Term)term).getTermAttributes();
			Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
			
			while (term_attr_iter.hasNext())
			{
				ChildTerminologyObject term_attribute = term_attr_iter.next();
							
				FieldFilterTreeItem term_attr_tree_item = new FieldFilterTreeItem(term_attribute, _is_filter);
				tree_item.addItem(term_attr_tree_item);
				
				if (term_attribute instanceof Synonym)
				{
					ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)term_attribute).getSynonymAttributes();
					Iterator<ChildTerminologyObject> synonyn_attr_iter = synonym_attributes.iterator();
					
					while (synonyn_attr_iter.hasNext())
					{
						ChildTerminologyObject synonym_attribute = synonyn_attr_iter.next();
												
						FieldFilterTreeItem synonym_attr_tree_item = new FieldFilterTreeItem(synonym_attribute, _is_filter);
						term_attr_tree_item.addItem(synonym_attr_tree_item);
					}
				}
			}
		}
	}
	
	public void toggleActivate(boolean toggle)
	{
		int record_attributes = _record_attributes.getChildCount();
		
		for (int i = 0; i < record_attributes; i++)
		{
			FieldFilterTreeItem tree_item =(FieldFilterTreeItem) _record_attributes.getChild(i);
			
			if (! tree_item.isDisabled())
				tree_item.setIsActive(toggle);			
		}
		
		int terms = _terms.getChildCount();
		
		for (int i = 0; i < terms; i++)
		{
			FieldFilterTreeItem term_item = (FieldFilterTreeItem) _terms.getChild(i);
			
			if (! term_item.isDisabled())
			{
				term_item.setIsActive(toggle);
				
				int term_attributes = term_item.getChildCount();
				
				for (int j = 0; j < term_attributes; j++)
				{
					FieldFilterTreeItem term_attr_item = (FieldFilterTreeItem) term_item.getChild(j);

					term_attr_item.setIsActive(toggle);
						
					int synonym_attributes = term_attr_item.getChildCount();
						
					for (int k = 0; k < synonym_attributes; k++)
					{
						FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
						
						synonym_attr_item.setIsActive(toggle);
					}
				}
			}
			else
			{			
				int term_attributes = term_item.getChildCount();
				
				for (int j = 0; j < term_attributes; j++)
				{
					FieldFilterTreeItem term_attr_item = (FieldFilterTreeItem) term_item.getChild(j);
					
					if (! term_attr_item.isDisabled())
					{
						term_attr_item.setIsActive(toggle);
						
						int synonym_attributes = term_attr_item.getChildCount();
						
						for (int k = 0; k < synonym_attributes; k++)
						{
							FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
							
							synonym_attr_item.setIsActive(toggle);
						}
					}
					else
					{
						int synonym_attributes = term_attr_item.getChildCount();
						
						for (int k = 0; k < synonym_attributes; k++)
						{
							FieldFilterTreeItem synonym_attr_item = (FieldFilterTreeItem) term_attr_item.getChild(k);
							
							if (! synonym_attr_item.isDisabled())
								synonym_attr_item.setIsActive(toggle);
						}
					}
				}
			}
		}
	}
}
