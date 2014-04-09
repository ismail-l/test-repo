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
import java.util.Date;

/**
 * 
 * @author I. Lavangee
 *
 */
public class Field implements IsTerminologyObject
{
	private long _fieldid = -1;
	private String _fieldname = null;
	private int _fieldtypeid = -1;
	private int _fielddatatypeid = -1;
	private int _maxlength = -1;
	private String _default_value = "";					
	private ArrayList<PresetField> _preset_fields = null;
	private boolean _inuse = false;
	
	private long _recordid = -1;
	private long _resourceid = -1;
	private String _chardata = "";
	private Date _archivedtimestamp = null;
	private ArrayList<AuditableEvent> _audits = null;
	private AccessRight _user_accessright = null;
	private AccessRight _usercat_accesright = null;		
	private boolean _has_been_edited = false;
	private boolean _is_archived = false;
	private long _sort_index = -1;
	
	private boolean _is_mandatory = false;
		
	public enum FieldType
	{
		INDEX_FIELD(1, "Index Field"), TEXT_ATTR_FIELD(2, "Text Attribute Field"), PRESET_ATTR_FIELD(3, "Preset Attribute Field"), 
		TEXT_RECORD_FIELD(4, "Text Record Field"), SYNONYM_FIELD(5, "Synonym Field"), TEXT_SUB_ATTR_FIELD(6, "Text SubAttribute Field"), PRESET_SUB_ATTR_FIELD(7, "Preset SubAttribute Field");
		
		int _field_type = -1;
		String _field_type_name = null;
		
		FieldType(int field_type, String field_type_name)
		{
			_field_type = field_type;
			_field_type_name = field_type_name;
		}
		
		public int getFieldType()
		{
			return _field_type;
		}
		
		public String getFieldTypeName()
		{
			return _field_type_name;
		}
	}
	
	public enum FieldDataType
	{
		PLAIN_TEXT(1, "Plain text"), INTEGER(2, "Integer"), FLOATING_POINT_NUMBER(3, "Floating-point Number"),
		HTML_HYPERLINK(4, "HTML Hyperlink"), XML(5, "XML"), FORMULA(6, "Formula");
		
		int _field_data_type = -1;
		String _field_data_type_name = null;
		
		FieldDataType(int field_data_type, String field_data_type_name)
		{
			_field_data_type = field_data_type;
			_field_data_type_name = field_data_type_name;
		}
		
		public int getFieldDataType()
		{
			return _field_data_type;
		}
		
		public String getFieldDataTypeName()
		{
			return _field_data_type_name;
		}
	}
	
	public Field() 
	{

	}
	
	public long getFieldId()
	{
		return _fieldid;
	}

	public void setFieldId(long fieldid)
	{
		_fieldid = fieldid;
	}

	public String getFieldName()
	{
		return _fieldname;
	}

	public void setFieldName(String fieldname)
	{
		_fieldname = fieldname;
	}

	public int getFieldTypeId()
	{
		return _fieldtypeid;
	}

	public void setFieldTypeId(int fieldtypeid)
	{
		_fieldtypeid = fieldtypeid;
	}

	public boolean isIndexField()
	{
		FieldType field_type = FieldType.INDEX_FIELD;
		return getFieldTypeId() == field_type.getFieldType();
	}

	public boolean isSynonymField()
	{
		FieldType field_type = FieldType.SYNONYM_FIELD;
		return getFieldTypeId() == field_type.getFieldType();
	}

	public boolean isRecordAttribute()
	{
		FieldType field_type = FieldType.TEXT_RECORD_FIELD;
		return getFieldTypeId() == field_type.getFieldType();				
	}

	public boolean isPresetAttribute()
	{
		FieldType preset_attr = FieldType.PRESET_ATTR_FIELD;
				
		return getFieldTypeId() == preset_attr.getFieldType();			
	}
	
	public boolean isPresetSubAttribute()
	{
		FieldType preset_sub_attr = FieldType.PRESET_SUB_ATTR_FIELD;
		
		return getFieldTypeId() == preset_sub_attr.getFieldType();			
	}
	
	public boolean isFieldAttribute()
	{	
		FieldType field_type = FieldType.TEXT_ATTR_FIELD;
		
		return this.getFieldTypeId() == field_type.getFieldType();				
	}
	
	public boolean isFieldSubAttribute()
	{
		FieldType field_type = FieldType.TEXT_SUB_ATTR_FIELD;
		
		return this.getFieldTypeId() == field_type.getFieldType();
	}
			
	public int getFieldDataTypeId()
	{
		return _fielddatatypeid;
	}

	public void setFieldDataTypeId(int fielddatatypeid)
	{
		_fielddatatypeid = fielddatatypeid;
	}

	public boolean isPlainText()
	{
		FieldDataType field_datatype = FieldDataType.PLAIN_TEXT;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public boolean isInteger()
	{
		FieldDataType field_datatype = FieldDataType.INTEGER;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public boolean isFloat()
	{
		FieldDataType field_datatype = FieldDataType.FLOATING_POINT_NUMBER;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public boolean isXml()
	{
		FieldDataType field_datatype = FieldDataType.XML;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public boolean isFormula()
	{
		FieldDataType field_datatype = FieldDataType.FORMULA;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public boolean isHTMLHyperlink()
	{
		FieldDataType field_datatype = FieldDataType.HTML_HYPERLINK;
		
		return _fielddatatypeid == field_datatype.getFieldDataType();
	}
	
	public int getMaxlength()
	{
		return _maxlength;
	}

	public void setMaxlength(int maxlength)
	{
		_maxlength = maxlength > 0 ? maxlength : _maxlength;
	}

	/** Checks whether the field is the default sort index. */
	public boolean isSortIndex(String sort_index_fieldname)
	{				
		if (_fieldname.equalsIgnoreCase(sort_index_fieldname))
			return true;
		else
			return false;
	}
	
	public boolean isProject(String project_fielname)
	{
		if (_fieldname.equalsIgnoreCase(project_fielname))
			return true;
		else
			return false;
	}
	
	public boolean isContext(String context_fieldname)
	{
		if (_fieldname.equalsIgnoreCase(context_fieldname))
			return true;
		else
			return false;
	}
	
	public boolean isDefinition(String defintion_fieldname)
	{
		if (_fieldname.equalsIgnoreCase(defintion_fieldname))
			return true;
		else
			return false;
	}
	
	public boolean isNote(String note_fieldname)
	{
		if (_fieldname.equalsIgnoreCase(note_fieldname))
			return true;
		else
			return false;
	}
	
	public boolean isSynonymContext(String synonym_context_fieldname)
	{
		if (_fieldname.equalsIgnoreCase(synonym_context_fieldname))
			return true;
		else
			return false;
	}
	
	public boolean isSynonymNote(String synonym_note_fieldname)
	{
		if (_fieldname.equalsIgnoreCase(synonym_note_fieldname))
			return true;
		else
			return false;
	}
	
	public void addPresetField(PresetField attribute)
	{
		if (_preset_fields == null)
			_preset_fields = new ArrayList<PresetField>();
		
		_preset_fields.add(attribute);
	}

	public ArrayList<PresetField> getPresetFields()
	{
		return _preset_fields;
	}

	public void setPresetFields(ArrayList<PresetField> presetAttributes)
	{
		_preset_fields = presetAttributes;
	}

	public boolean hasPresetFields()
	{
		return _preset_fields != null
				&& _preset_fields.size() > 0;
	}

	public boolean updatePresetAttribute(PresetField attribute)
	{
		if (hasPresetFields())
		{
			for (int i = 0; i < _preset_fields.size(); i++)
			{
				PresetField attr = _preset_fields.get(i);
				if (attr.equals(attribute))
				{
					_preset_fields.set(i, attribute);
					return true;
				}
			}
		}

		return false;
	}
			
	public void setMandatory(boolean mandatory)
	{
		_is_mandatory = mandatory;
	}
	
	public boolean isMandatory()
	{
		return _is_mandatory;
	}
	
	public void setDefaultValue(String default_value)
	{
		_default_value = default_value;
	}
	
	public String getDefaultValue()
	{
		return _default_value;
	}
	
	public void setInuse(boolean inuse)
	{
		_inuse = inuse;
	}
	
	public boolean isInuse()
	{
		return _inuse;
	}
	
	public void setSortIndex(long sort_index)
	{
		_sort_index = sort_index;
	}
	
	public long getSortIndex()
	{
		return _sort_index;
	}
	
	@Override
	public long getRecordId() 
	{		
		return _recordid;
	}
	
	@Override
	public void setRecordId(long recordid) 
	{
		_recordid = recordid;		
	}
	
	@Override
	public void setResourceId(long resourceid)
	{
		_resourceid = resourceid;		
	}
	
	@Override
	public long getResourceId() 
	{	
		return _resourceid;
	}
	
	@Override
	public void setCharData(String chardata) 
	{
		_chardata = chardata;		
	}
	
	@Override
	public String getCharData() 
	{	
		return _chardata;
	}
	
	@Override
	public Date getArchivedTimestamp() 
	{		
		return _archivedtimestamp;
	}
	
	@Override
	public void setArchivedTimestamp(Date archivedtimestamp) 
	{
		_archivedtimestamp = archivedtimestamp;		
	}	
	
	@Override
	public void setAuditTrail(ArrayList<AuditableEvent> audits) 
	{
		_audits = audits;		
	}
	@Override
	public ArrayList<AuditableEvent> getAuditTrail() 
	{		
		return _audits;
	}
	
	@Override
	public void setUserAccessRight(AccessRight access_right) 
	{
		_user_accessright = access_right;		
	}
	
	@Override
	public AccessRight getUserAccessRight() 
	{	
		return _user_accessright;
	}
	
	@Override
	public void setUserCategoryAccessRight(AccessRight access_right) 
	{
		_usercat_accesright = access_right;		
	}
	
	@Override
	public AccessRight getUserCategoryAccessRight() 
	{		
		return _usercat_accesright;
	}
	
	@Override
	public void setHasBeenEdited(boolean has_been_edited)
	{
		_has_been_edited = has_been_edited;
	}
	
	@Override
	public boolean hasBeenEdited()
	{
		return _has_been_edited;		
	}

	@Override
	public void setIsArchived(boolean is_archived) 
	{
		_is_archived = is_archived;
	}

	@Override
	public boolean isArchived() 
	{
		return _is_archived;
	}
}