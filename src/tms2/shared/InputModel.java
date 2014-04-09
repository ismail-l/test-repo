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

package tms2.shared;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author I. Lavangee
 *
 */
public class InputModel implements IsSerializable
{		
	private ArrayList<TerminlogyObject> _record_attributes = null;
	private ArrayList<TerminlogyObject> _terms = null;	
	
	public InputModel() {}

	public ArrayList<TerminlogyObject> getRecordAttributes()
	{
		return _record_attributes;
	}

	public ArrayList<TerminlogyObject> getTerms()
	{
		return _terms;
	}
	
	public void addRecordAttribute(RecordAttribute record_attribute)
	{
		if (_record_attributes == null)
			_record_attributes = new ArrayList<TerminlogyObject>();
		
		_record_attributes.add(record_attribute);
	}	
	
	public void addTerm(Term term)
	{
		if (_terms == null)
			_terms = new ArrayList<TerminlogyObject>();
		
		_terms.add(term);
	}
	
	public void addTermAttribute(Term term, TermAttribute termattribute)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		
		while(iter.hasNext())
		{
			Term im_term = (Term)iter.next();
			if (im_term.getFieldId() == term.getFieldId())	
			{
				im_term.addTermAttribute(termattribute);
				break;
			}			
		}
	}
	
	public void addSynonymAttribute(Term term, Synonym synonym, SynonymAttribute synonym_attribute)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		
		while(iter.hasNext())
		{
			Term im_term = (Term) iter.next();
			if (im_term.getFieldId() == term.getFieldId())	
			{
				ArrayList<ChildTerminologyObject> term_attributes = im_term.getTermAttributes();
				if (term_attributes != null && term_attributes.size() > 0)
				{
					Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
					while (term_attr_iter.hasNext())
					{
						TermAttribute term_attribute = (TermAttribute) term_attr_iter.next();
						if (term_attribute.getFieldId() == synonym.getFieldId())
						{
							((Synonym)term_attribute).addSynonymAttribute(synonym_attribute);
							break;
						}
					}
				}
								
				break;
			}			
		}
	}
	
	public ArrayList<ChildTerminologyObject> getTermAttributesForTerm(Term term)
	{
		ArrayList<ChildTerminologyObject> term_attributes = new ArrayList<ChildTerminologyObject>();
		
		Iterator<TerminlogyObject> iter = _terms.iterator();
		
		while (iter.hasNext())
		{
			Term terminology_object = (Term) iter.next();
			
			if (terminology_object.getFieldId() == term.getFieldId())
			{
				term_attributes.addAll(terminology_object.getTermAttributes());
				
				break;
			}
		}
		
		return term_attributes;
	}
	
	public ArrayList<ChildTerminologyObject> getSynonymAttributesForTerm(Term term)
	{
		ArrayList<ChildTerminologyObject> synonym_attributes = new ArrayList<ChildTerminologyObject>();
		
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term terminology_object = (Term) iter.next();
			
			if (terminology_object.getFieldId() == term.getFieldId())
			{												
				Iterator<ChildTerminologyObject> term_attr_iter = terminology_object.getTermAttributes().iterator();
				while (term_attr_iter.hasNext())
				{
					TermAttribute term_attribute = (TermAttribute) term_attr_iter.next();
					if (term_attribute instanceof Synonym)
					{
						synonym_attributes.addAll(((Synonym)term_attribute).getSynonymAttributes());	
						
						break;
					}
				}																				
				
				break;
			}
		}
		
		return synonym_attributes;
	}
		
	public TerminlogyObject getProjectTerminologyObject(String project_field_name)
	{				
		Iterator<TerminlogyObject> iter = _record_attributes.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject terminology_object = iter.next();
			if (terminology_object.getFieldName().equalsIgnoreCase(project_field_name))
				return terminology_object;
		}
		
		return null;
	}
	
	public boolean isProjectEditable(String project_field_name)
	{
		TerminlogyObject project = getProjectTerminologyObject(project_field_name);
		
		if (project == null)
			return false;
		
		return project.getUserCategoryAccessRight().mayUpdate();
	}
	
	public boolean isSourceEditable(String sort_index)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject terminology_object = iter.next();
			
			if (terminology_object.getFieldName().equals(sort_index))
				return terminology_object.getUserCategoryAccessRight().mayUpdate();
		}
		
		return false;
	}
}
