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
import java.sql.Date;
import java.util.Iterator;

/**
 * 
 * @author I. Lavangee
 *
 */
public class Record implements IsAuditableEvent
{	
	private long _recordid = -1;
	private long _termdbid = -1;
	private Date _archivedtimestamp = null;
	private ArrayList<TerminlogyObject> _terms = null;
	private ArrayList<TerminlogyObject> _recordattributes = null;
	private Term _previous_term = null;;
	private Term _next_term = null;;
	private boolean _is_synonym = false;
	private String _synonym = null;
	private long _synonym_id = -1;	
	private ArrayList<Project> _projects = null;
	private ArrayList<Project> _assigned_projects = null;
	private ArrayList<AuditableEvent> _audits = null;

	public Record() 
	{
		
	}

	public long getRecordId()
	{
		return _recordid;
	}

	public void setRecordId(long recordid)
	{
		_recordid = recordid;
	}
	
	public long getTermdbId()
	{
		return _termdbid;
	}

	public void setTermdbId(long termdbid)
	{
		_termdbid = termdbid;
	}

	public void setProjects(ArrayList<Project> topics)
	{
		_projects = topics;
	}

	public ArrayList<Project> getProjects()
	{
		return _projects;
	}

	public Date getArchivedTimestamp()
	{
		return _archivedtimestamp;
	}

	public void setArchivedTimestamp(Date archivedtimestamp)
	{
		_archivedtimestamp = archivedtimestamp;
	}
	
	public boolean getIsSynonym()
	{
		return _is_synonym;
	}

	public String getSynonym()
	{
		return _synonym;
	}

	public long getSynonymId()
	{
		return _synonym_id;
	}

	public ArrayList<TerminlogyObject> getRecordAttributes()
	{
		return _recordattributes;
	}
	
	public void updateRecordAttribute(RecordAttribute record_attribute)
	{
		Iterator<TerminlogyObject> iter = _recordattributes.iterator();
		while (iter.hasNext())
		{
			TerminlogyObject record_record_attribute = iter.next();
			
			if (record_record_attribute.getResourceId() == record_attribute.getResourceId() &&
				! record_record_attribute.isArchived() &&
				record_record_attribute.getFormIndex() == record_attribute.getFormIndex())
			{
				record_record_attribute.setCharData(record_record_attribute.getCharData());
				break;
			}
		}
	}
	
	public void addRecordAttribute(RecordAttribute record_attribute)
	{
		if (_recordattributes == null)
			_recordattributes = new ArrayList<TerminlogyObject>();
		
		_recordattributes.add(record_attribute);
	}
	
	public void setRecordattributes(ArrayList<TerminlogyObject> recordattributes)
	{
		_recordattributes = recordattributes;
	}
	
	public void setTerms(ArrayList<TerminlogyObject> terms)
	{
		_terms = terms;
	}

	public void addTerm(Term term)
	{
		if (_terms == null)
			_terms = new ArrayList<TerminlogyObject>();
		
		_terms.add(term);
	}
	
	public void updateTerm(Term term)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term record_term = (Term) iter.next();
			if (record_term.getResourceId() == term.getResourceId() &&
				! record_term.isArchived() && 
				record_term.getFormIndex() == term.getFormIndex())
			{
				record_term.setCharData(term.getCharData());
				break;
			}
		}
	}
	
	public ArrayList<TerminlogyObject> getTerms()
	{
		return _terms;
	}
		
	public void updateTermAttributeToTerm(Term term, TermAttribute term_attribute)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term record_term = (Term) iter.next();
			
			if (record_term.getResourceId() == term.getResourceId() &&
				record_term.getFormIndex() == term.getFormIndex())
			{		
				ArrayList<ChildTerminologyObject> term_attributes = record_term.getTermAttributes();
				
				if (term_attributes == null || term_attributes.size() == 0)
				{
					if (record_term.getFormIndex() == term_attribute.getParentFormIndex())
						record_term.addTermAttribute(term_attribute);
				}
				else
				{
					Iterator<ChildTerminologyObject> term_attr_iter = record_term.getTermAttributes().iterator();
					while (term_attr_iter.hasNext())
					{
						TermAttribute record_term_attribute = (TermAttribute) term_attr_iter.next();
						
						if (record_term_attribute.getResourceId() == term_attribute.getResourceId() &&
							! record_term_attribute.isArchived() &&
							record_term.getFormIndex() == term_attribute.getParentFormIndex())
						{
							record_term_attribute.setCharData(term_attribute.getCharData());
							break;
						}
					}
				}
			}
		}
	}
	
	public void addTermAttributeToTerm(Term term, TermAttribute term_attribute)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term record_term = (Term) iter.next();
			
			if (record_term.getFieldId() == term.getFieldId() &&
				record_term.getFormIndex() == term_attribute.getParentFormIndex())
			{
				record_term.addTermAttribute(term_attribute);
				break;
			}
		}		
	}
	
	public void updateSynonymAttribute(Term term, SynonymAttribute synonym_attribute)
	{
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term record_term = (Term) iter.next();
			
			if (record_term.getResourceId() == term.getResourceId() &&
				record_term.getFormIndex() == term.getFormIndex())
			{					
				Iterator<ChildTerminologyObject> term_attr_iter = record_term.getTermAttributes().iterator();
				while (term_attr_iter.hasNext())
				{
					TermAttribute record_term_attribute = (TermAttribute) term_attr_iter.next();
					
					if (record_term_attribute instanceof Synonym)
					{
						ArrayList<ChildTerminologyObject> synonym_attributes = ((Synonym)record_term_attribute).getSynonymAttributes();
						if (synonym_attributes == null || synonym_attributes.size() == 0)
						{
							if (record_term_attribute.getFormIndex() == synonym_attribute.getSubParentFormIndex() )
								((Synonym)record_term_attribute).addSynonymAttribute(synonym_attribute);
						}
						else
						{
							Iterator<ChildTerminologyObject> synonym_attr_iter = synonym_attributes.iterator();
							while (synonym_attr_iter.hasNext())
							{
								SynonymAttribute record_synonym_attribute = (SynonymAttribute) synonym_attr_iter.next();
								if (record_synonym_attribute.getResourceId() == synonym_attribute.getResourceId() &&
									! record_synonym_attribute.isArchived())
								{
									int synonym_form_index = ((Synonym)record_term_attribute).getFormIndex();
									
									if (synonym_form_index == synonym_attribute.getSubParentFormIndex())
									{
										record_synonym_attribute.setCharData(synonym_attribute.getCharData());
										break;
									}
		
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void addSynonymAttributeToTerm(Term term, SynonymAttribute synonym_attribute)
	{
		Iterator<TerminlogyObject> term_iter = _terms.iterator();
		while (term_iter.hasNext())
		{
			Term record_term = (Term) term_iter.next();
			
			if (record_term.getFieldId() == term.getFieldId() &&
				record_term.getFormIndex() == term.getFormIndex())
			{
				ArrayList<ChildTerminologyObject> term_attributes = record_term.getTermAttributes();
				Iterator<ChildTerminologyObject> term_attr_iter = term_attributes.iterator();
				while (term_attr_iter.hasNext())
				{
					TermAttribute record_term_attribute = (TermAttribute) term_attr_iter.next();
					
					if (record_term_attribute instanceof Synonym)
					{		
						int synonym_form_index = ((Synonym)record_term_attribute).getFormIndex();
						
						if (synonym_form_index == synonym_attribute.getSubParentFormIndex())
						{
							((Synonym) record_term_attribute).addSynonymAttribute(synonym_attribute);
							break;
						}
					}					
				}
			}
		}
	}
	
	public void setIsSynonym(boolean flag)
	{
		this._is_synonym = flag;
	}

	public boolean isSynonym()
	{
		return _is_synonym;
	}
	
	public void setSynonym(String synonym)
	{
		this._synonym = synonym;
	}

	public void setSynonymId(long synonym_id)
	{
		this._synonym_id = synonym_id;
	}

	public Term getPreviousTerm()
	{
		return _previous_term;
	}
	
	public void setPreviousTerm(Term term)
	{
		_previous_term = term;
	}

	public Term getNextTerm()
	{
		return _next_term;
	}

	public void setNextTerm(Term nextIndexTerm)
	{
		_next_term = nextIndexTerm;
	}
		
	public void setAssignedProjects(ArrayList<Project> assigned_topics)
	{
		_assigned_projects = assigned_topics;
	}
	
	public ArrayList<Project> getAssignedProjects()
	{
		return _assigned_projects;
	}
	
	public Term getSortIndexTerm(String sort_index_field)
	{				
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term term = (Term) iter.next();
			
			if (term.getFieldName().equals(sort_index_field))
				return term;
		}
		
		return null;
	}
	
	public Term getCustomSortIndexTerm(Field field)
	{				
		Iterator<TerminlogyObject> iter = _terms.iterator();
		while (iter.hasNext())
		{
			Term term = (Term) iter.next();
			
			if (term.getFieldName().equals(field.getFieldName()))
				return term;
		}
		
		return null;
	}
	
	@Override
	public ArrayList<AuditableEvent> getAuditTrail()
	{
		return _audits;
	}
	
	@Override
	public void setAuditTrail(ArrayList<AuditableEvent> auditTrail)
	{
		_audits = auditTrail;
	}
}