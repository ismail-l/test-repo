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

package tms2.server.field;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import tms2.server.AppConfig;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.Field;
import tms2.shared.InputModel;
import tms2.shared.PresetField;
import tms2.shared.RecordAttribute;
import tms2.shared.Synonym;
import tms2.shared.SynonymAttribute;
import tms2.shared.Term;
import tms2.shared.TermAttribute;
import eu.medsea.mimeutil.MimeUtil2;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FieldManager 
{
	public static ArrayList<Field> getAllFields(Connection connection) throws Exception
	{				
		ArrayList<Field> fields = new ArrayList<Field>();
						
		String sql = " select fields.fieldid, fields.fieldname, fields.fieldtypeid, fields.fielddatatypeid, " + 
					 " fields.maxlength, fields.defaultvalue, fields.sortindex " + 
					 " from tms.fields order by fields.sortindex";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
						
		while (result.next())
		{
			Field field = getField(connection, result);																																							
			fields.add(field);
		}
		
		result.close();
		stored_procedure.close();
		
		return fields;		
	}
	
	public static Field getFieldByFieldId(Connection connection, long fieldid) throws Exception
	{		
		Field field = null;
		
		String sql = " select fields.fieldid, fields.fieldname, fields.fieldtypeid, fields.fielddatatypeid, " + 
		 			 " fields.maxlength, fields.defaultvalue, fields.sortindex " + 
		 			 " from tms.fields " + 
		 			 " where fields.fieldid = " + fieldid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
				
		while (result.next())
		{
			field = getField(connection, result);
		}
		
		result.close();
		stored_procedure.close();
		
		return field;
	}
	
	public static PresetField getPresetFieldByFieldId(Connection connection, long fieldid) throws SQLException
	{		
		PresetField field = null;
		
		String sql = " select * from tms.presetfields where " +
			         " presetfields.presetfieldid = " + fieldid;
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
				
		if (result.next())		
			field = getPresetField(connection, result);				
		
		result.close();
		stored_procedure.close();
		
		return field;
	}
	
	public static void setPresetFields(Connection connection, Field field) throws SQLException
	{				
		String sql = " select presetfields.presetfieldid, presetfields.presetfieldname, " + 
								 " presetfields.fieldid " + 
								 " from tms.presetfields " + 
								 " where presetfields.fieldid = " + field.getFieldId() + " order by presetfields.presetfieldid";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
				
		while (result.next())
		{
			PresetField presetAttribute = new PresetField();
			presetAttribute.setPresetFieldid(result.getLong("presetfieldid"));			
			presetAttribute.setPresetFieldName(result.getString("presetfieldname"));			
			presetAttribute.setFieldId(result.getLong("fieldid"));
			
			field.addPresetField(presetAttribute);
		}
		
		result.close();
		stored_procedure.close();
	}
	
	public static long getSynonymFieldId(Connection connection) throws Exception
	{
		long synonym_fieldid = -1;
		
		AppConfig config = AppConfig.getInstance();
		
		String sql = "select fieldid from tms.fields where fieldname = '" + config.getSynonym() + "'";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		if (result.next())
			synonym_fieldid = result.getLong("fieldid");
		
		result.close();
		stored_procedure.close();
		
		return synonym_fieldid;
	}
	
	public static Field updateField(Connection connection, Field field, HttpSession session, boolean is_updating) throws Exception
	{		
		Field updated = null;
		CallableStatement stored_procedure = null;
		
		if (is_updating)
			stored_procedure = StoredProcedureManager.updateField(connection, field);
		else
		{
			long sort_index = getMaxSortIndex(connection);
			
			if (sort_index == -1)
				return null;
						
			stored_procedure = StoredProcedureManager.createField(connection, field, ++sort_index);
		}
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)		
			updated = getFieldByFieldId(connection, result);		
		
		if (! is_updating)
		{
			// Add the "None" to a newly created Preset or Preset Sub Attr
			if (updated.isPresetAttribute() || updated.isPresetSubAttribute())
			{
				PresetField none = new PresetField();
				none.setPresetFieldName("None");
				none.setFieldId(updated.getFieldId());
				
				PresetField created_preset = updatePresetField(connection, none, false);
				field.addPresetField(created_preset);
			}
		}
		
		stored_procedure.close();
		
		return updated;	
	}

	private static long getMaxSortIndex(Connection connection) throws SQLException
	{		
		CallableStatement stored_procedure = null;
		
		String sql = "select max (sortindex) from tms.fields";
		
		stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result = (ResultSet)stored_procedure.getObject(1);
		
		long sort_index = -1;
		
		if (result.next())
			sort_index = result.getLong("max");
		
		result.close();
		stored_procedure.close();
		
		return sort_index;
	}
	
	public static PresetField updatePresetField(Connection connection, PresetField field, boolean is_updating) throws SQLException
	{		
		PresetField updated = null;
		CallableStatement stored_procedure = null;
		
		if (is_updating)				
			stored_procedure = StoredProcedureManager.updatePresetField(connection, field);		
		else				
			stored_procedure = StoredProcedureManager.createPresetField(connection, field);		
		
		long result = -1;
		result = (Long)stored_procedure.getObject(1);
		
		if (result > -1)		
			updated = getPresetFieldByFieldId(connection, result);		
		
		stored_procedure.close();
		
		return updated;	
	}
		
	private static boolean isFieldInUse(Connection connection, Field field) throws SQLException
	{
		boolean inuse = false;
		String sql = null;
		
		if (field.isIndexField())		
		{
			sql = "select count(*) as count from tms.terms where " +
				  "terms.fieldid = " + field.getFieldId() + " and terms.archivedtimestamp is null";
		}
		else if (field.isRecordAttribute())
		{
			sql = "select count(*) as count from tms.recordattributes where " +
			      "recordattributes.fieldid = " + field.getFieldId() + " and recordattributes.archivedtimestamp is null";
		}
		else if (field.isFieldAttribute() || field.isPresetAttribute() || field.isSynonymField())
		{
			if (! field.isSynonymField())
			{
				sql = "select count(*) as count from tms.termattributes where " +
		      	  	  "termattributes.fieldid = " + field.getFieldId() + " and termattributes.archivedtimestamp is null";
			}
			else
			{
				sql = "select count(*) as count from tms.synonyms where " +
	      	  	      "synonyms.fieldid = " + field.getFieldId() + " and synonyms.archivedtimestamp is null";
			}
		}
		else if (field.isFieldSubAttribute() || field.isPresetSubAttribute())
		{
			sql = "select count(*) as count from tms.synonymattributes where " +
    	  	      "synonymattributes.fieldid = " + field.getFieldId() + " and synonymattributes.archivedtimestamp is null";
		}
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		ResultSet result = (ResultSet) stored_procedure.getObject(1);
		
		if (result.next())
		{
			int count = result.getInt("count");
			
			if (count > 0)
				inuse = true;
		}
		
		result.close();
		stored_procedure.close();
		
		return inuse;
	}
	
	public static String getMimeType(String filename)
	{
		MimeUtil2 mimeUtil = new MimeUtil2();
		
		mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
		//mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");

		@SuppressWarnings("rawtypes")
		Collection mimeTypes = mimeUtil.getMimeTypes(new File(filename));
		
		System.out.println("Mime (filename): " + filename);
		System.out.println("Mime (types): " + mimeTypes.toString());
		System.out.println("Mime (best type): " + MimeUtil2.getMostSpecificMimeType(mimeTypes).toString());
		
		return MimeUtil2.getMostSpecificMimeType(mimeTypes).toString();
	}
	
	public static InputModel createInputModel(Connection connection) throws Exception
	{
		InputModel inputmodel = null;
		
		ArrayList<Field> fields = getAllFields(connection);
		
		if (fields == null || fields.size() == 0)
			return null;
		else
			inputmodel = new InputModel();
		
		Iterator<Field> iter = fields.iterator();
		while (iter.hasNext())
		{
			Field field = iter.next();
			
			if (field.isRecordAttribute())
			{
				RecordAttribute record_attribute = new RecordAttribute();
				record_attribute.setFieldId(field.getFieldId());
				record_attribute.setFieldTypeId(field.getFieldTypeId());	
				record_attribute.setFieldDataTypeId(field.getFieldDataTypeId());
				record_attribute.setFieldName(field.getFieldName());
				record_attribute.setMaxlength(field.getMaxlength());
				record_attribute.setDefaultValue(field.getDefaultValue());
				
				inputmodel.addRecordAttribute(record_attribute);
			}
			else if (field.isIndexField())
			{
				Term term = new Term();
				term.setFieldId(field.getFieldId());
				term.setFieldTypeId(field.getFieldTypeId());
				term.setFieldDataTypeId(field.getFieldDataTypeId());
				term.setFieldName(field.getFieldName());
				term.setMaxlength(field.getMaxlength());
				term.setDefaultValue(field.getDefaultValue());
				term.setMandatory(field.isMandatory());
				
				inputmodel.addTerm(term);
				
				Iterator<Field> term_attr_iter = fields.iterator();
				while (term_attr_iter.hasNext())
				{
					Field term_attribute_field = term_attr_iter.next();					
										 					
					if (term_attribute_field.isFieldAttribute() || term_attribute_field.isPresetAttribute())
					{
						TermAttribute term_attribute = new TermAttribute();
						term_attribute.setFieldId(term_attribute_field.getFieldId());
						term_attribute.setFieldTypeId(term_attribute_field.getFieldTypeId());
						term_attribute.setFieldDataTypeId(term_attribute_field.getFieldDataTypeId());
						term_attribute.setFieldName(term_attribute_field.getFieldName());
						term_attribute.setMaxlength(term_attribute_field.getMaxlength());
						term_attribute.setDefaultValue(term_attribute_field.getDefaultValue());
						
						if (term_attribute_field.isPresetAttribute())
							term_attribute.setPresetFields(term_attribute_field.getPresetFields());
						
						inputmodel.addTermAttribute(term, term_attribute);						
					}
					else if (term_attribute_field.isSynonymField())
					{
						Synonym synonym = new Synonym();
						synonym.setFieldId(term_attribute_field.getFieldId());
						synonym.setFieldTypeId(term_attribute_field.getFieldTypeId());
						synonym.setFieldDataTypeId(term_attribute_field.getFieldDataTypeId());
						synonym.setFieldName(term_attribute_field.getFieldName());
						synonym.setMaxlength(term_attribute_field.getMaxlength());
						synonym.setDefaultValue(term_attribute_field.getDefaultValue());
						
						inputmodel.addTermAttribute(term, synonym);
						
						Iterator<Field> synonym_attr_iter = fields.iterator();
						while (synonym_attr_iter.hasNext())
						{
							Field synonym_attribute_field = synonym_attr_iter.next();
							
							if (synonym_attribute_field.isFieldSubAttribute() || synonym_attribute_field.isPresetSubAttribute())						
							{
								SynonymAttribute synonym_attribute = new SynonymAttribute();
								synonym_attribute.setFieldId(synonym_attribute_field.getFieldId());
								synonym_attribute.setFieldTypeId(synonym_attribute_field.getFieldTypeId());
								synonym_attribute.setFieldDataTypeId(synonym_attribute_field.getFieldDataTypeId());
								synonym_attribute.setFieldName(synonym_attribute_field.getFieldName());
								synonym_attribute.setMaxlength(synonym_attribute_field.getMaxlength());
								synonym_attribute.setDefaultValue(synonym_attribute_field.getDefaultValue());
								
								if (synonym_attribute_field.isPresetSubAttribute())
									synonym_attribute.setPresetFields(synonym_attribute_field.getPresetFields());
								
								inputmodel.addSynonymAttribute(term, synonym, synonym_attribute);
							}
						}
					}					
				}
			}
		}
		
		return inputmodel;
	}
	
	private static Field getField(Connection connection, ResultSet result) throws Exception
	{
		AppConfig config = AppConfig.getInstance();
		
		Field field = new Field();
		
		field.setFieldDataTypeId(result.getInt("fielddatatypeid"));
		field.setFieldId(result.getInt("fieldid"));
		field.setFieldName(result.getString("fieldname"));
		field.setFieldTypeId(result.getInt("fieldtypeid"));
		field.setMaxlength(result.getInt("maxlength"));	
		field.setSortIndex(result.getLong("sortindex"));
		
		if (field.isPresetAttribute() || field.isPresetSubAttribute())			
			setPresetFields(connection, field);			
					
		if (field.getFieldName().equalsIgnoreCase(config.getSortIndexField()))		
			field.setMandatory(true);					
		
		if (field.getFieldName().equalsIgnoreCase(config.getProjectField()))			
			field.setMandatory(true);					
		
		field.setInuse(isFieldInUse(connection, field));
		
		return field;
	}
	
	private static PresetField getPresetField(Connection connection, ResultSet result) throws SQLException	
	{
		PresetField field = new PresetField();
		
		field.setPresetFieldid(result.getLong("presetfieldid"));
		field.setPresetFieldName(result.getString("presetfieldname"));
		field.setFieldId(result.getLong("fieldid"));	
		
		return field;
	}
}
