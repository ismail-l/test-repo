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

package tms2.server.export;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeFontFaceDecls;
import org.odftoolkit.odfdom.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeText;
import org.odftoolkit.odfdom.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyleFontFace;
import org.odftoolkit.odfdom.doc.style.OdfStyleMasterPage;
import org.odftoolkit.odfdom.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStop;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStops;
import org.odftoolkit.odfdom.doc.style.OdfStyleTextProperties;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextPageNumberElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tms2.client.exception.TMSException;
import tms2.server.AppConfig;
import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.i18n.Internationalization;
import tms2.server.logging.LogUtility;
import tms2.server.record.RecordIdTracker;
import tms2.server.record.RecordIds;
import tms2.server.record.RecordManager;
import tms2.server.sql.FilterSqlGenerator;
import tms2.server.sql.StoredProcedureManager;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.ExportType;
import tms2.shared.Filter;
import tms2.shared.Record;
import tms2.shared.Synonym;
import tms2.shared.Term;
import tms2.shared.TerminlogyObject;
import tms2.shared.User;

// TODO The Source data retrieval and the target data retrieval is very similar.
// Could be combined into one.

/**
 * 
 * @author I. Lavangee
 *
 */
public class ExportManager 
{		
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private static final String EXPORT_TYPE = "exportType";
	
	private static final String MASTER_PAGE_STYLE = "Standard";
	private static final String TEXT_STYLE_NAME = "Footer";
	private static final String TEXT_PTEXT_NAME = "FooterP";	
		
	private static String _odt_font = null;
		
	private static final String NORMAL_8PT = "Normal_8pt";	
	private static final String NORMAL_9PT = "Normal_9pt";
	private static final String BOLD_12PT = "Bold_style_14pt";
	private static final String BOLD_10PT = "Bold_style_10pt";	
	private static final String BOLD_9PT = "Bold_style_9pt";	
	private static final String BOLD_8PT = "Bold_style_8pt";
	private static final String ITALICS = "Italics_style";
	private static final String UNDERLINE = "Underline";
	private static final String SYNONYM_REF = "Synonym_ref";
	
	private static class ExportData
	{
		private String filename = null;
		private String type = null;
		private OdfTextDocument outFile = null;	
		private String outText = null;
	}
		
	private static class TabData
	{
		private long _recordid = -1;
		private String _chardata = null;		
	}
	
	private static TabData getTabData(long recordid, String chardata)
	{
		TabData data = new TabData();
		
		data._recordid = recordid;
		data._chardata = chardata;
		
		return data;
	}
	
	private static ExportData getExportData(HttpSession session)
	{
		return (ExportData) session.getAttribute("exportData");
	}
	
	private static void setExportData(HttpSession session, ExportData exportData)
	{
		session.setAttribute("exportData", exportData);
	}
	
	public static void download(String authToken, HttpServletRequest req, HttpServletResponse resp) throws TMSException, IOException
	{
		int export_type = Integer.parseInt(req.getParameter(EXPORT_TYPE));
		
		System.out.println("ExServiceImpl: doGet (" + export_type + ")");
		
		AccessControlManager.getSignedOnUser(req.getSession(), authToken);
								
    	ExportData exportData = ExportManager.getExportData(req.getSession());
    	if (exportData == null)
    	{
    		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    		LogUtility.log(Level.WARNING, req.getSession(), _i18n.getConstants().log_export_exportdata(), authToken);
    		throw new TMSException(_i18n.getConstants().log_export_exportdata());
    	}
    	
		resp.setContentType("application/csv");
    	resp.setCharacterEncoding("UTF-8");
    	resp.setHeader("Content-disposition", "attachment; filename=" + exportData.filename);
    	
    	ServletOutputStream os = null;
    	OutputStreamWriter ow = null; 
    	
    	try
    	{
    		os = resp.getOutputStream();
        	        	
    		if (exportData.type.equalsIgnoreCase("ODT"))    
    		{
    	    	if (exportData.outFile == null)
    	    	{
    	    		Exception e = new Exception("No output file.");
    	    		LogUtility.log(Level.WARNING, req.getSession(),  _i18n.getConstants().log_export_output_file(), e, authToken);
    	    		throw e;
    	    	}
    	    	
    			exportData.outFile.save(os); 
    			exportData.outFile.close();    			
    		}
    		else  
    		{
    	    	if (exportData.outText == null)
    	    	{
    	    		TMSException tms = new TMSException( _i18n.getConstants().log_export_output_text());
    	    		LogUtility.log(Level.WARNING, req.getSession(),  _i18n.getConstants().log_export_output_text(), tms , authToken);
    	    		throw tms;
    	    	}
    	    	
    			ow = new OutputStreamWriter(os,"UTF-8"); 
    			ow.write(exportData.outText);
    			
        		ow.flush();
        		ow.close();
    		}
    	}
    	catch (Exception ex)
    	{    		
    		if (ow != null)
    			ow.close();
    		
    		throw new TMSException(ex.getMessage());    		
    	}
    	finally
    	{
    		if (os != null)
    		{
    			os.flush();
    			os.close();
    		}
    	}
	}
	
	public static boolean generateDocument(Connection connection, HttpSession session, String authToken, Filter filter, ExportType export_type, String filename) throws Exception
	{
		boolean export = false;
		
		ExportData exportData = new ExportData();
		
		exportData.type = export_type.getExportType();
		exportData.filename = filename;
		
		if (exportData.type.equalsIgnoreCase("TAB"))
		{				
			exportData.filename += ".TAB";
			setExportData(session, exportData);
			export = generateITETabDocument(connection, session, authToken, filter, export_type);
		}
		else if (exportData.type.equalsIgnoreCase("ODT"))
		{
			exportData.filename += ".odt";
			setExportData(session, exportData);
			export = generateOdtDocument(connection, session, authToken, filter, export_type);
		}
		else if (exportData.type.equalsIgnoreCase("TBX"))
		{
			exportData.filename += ".tbx";
			setExportData(session, exportData);				
			export = generateTbxDocument(connection, session, authToken, export_type);				
		}
		
		return export;
	}
	
	private static boolean generateITETabDocument(Connection connection, HttpSession session, String authToken, 
										   Filter filter, ExportType export_type) throws  SQLException
	{
    	ExportData exportData = getExportData(session);
    	if (exportData == null)
    		return false;
    	
    	FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
    	
		String filter_sql = sql_generator.generateSearchSQLStatement(filter, export_type.getSourceField());	
		String outText = "";
		
		LinkedList<String> returnList = tabExportLookup(connection, session, authToken, filter_sql, export_type);
		
		for(String str : returnList)
		{
			outText += str;
			
			AccessControlManager.getSignedOnUser(session, authToken);	
		}
				
		if (!outText.isEmpty())
		{
			exportData.outText = outText;
			setExportData(session, exportData);
			return true;
		}
		
		return false;
	}
	
	private static LinkedList<String> tabExportLookup(Connection connection, HttpSession session, String authToken, String filter_sql, ExportType export_type) throws SQLException
	{
		LinkedList<String> returnList = new LinkedList<String>();
		
		String source_sql =  filter_sql + " SELECT terms.chardata, fields.fieldid, terms.recordid \n" + 
									     " FROM tms.terms, tms.fields \n" + 
									     " WHERE terms.fieldid = fields.fieldid \n" + 
									     " AND terms.archivedtimestamp is null \n" + 
									     " AND fields.fieldid = " + export_type.getSourceField().getFieldId() + 												    
									     " AND terms.recordid IN (SELECT recordid FROM with_filter)" +
									     " order by terms.recordid";
		
		CallableStatement source_stored_procedure = StoredProcedureManager.genericReturnedRef(connection, source_sql);
		
		String target_sql =  filter_sql + " SELECT terms.chardata, fields.fieldid, terms.recordid \n" + 
									     " FROM tms.terms, tms.fields \n" + 
									     " WHERE terms.fieldid = fields.fieldid \n" + 
									     " AND terms.archivedtimestamp is null \n" + 
									     " AND fields.fieldid = " + export_type.getTargetField().getFieldId() + 												    
									     " AND terms.recordid IN (SELECT recordid FROM with_filter)" +
									     " order by terms.recordid";
		
		CallableStatement target_stored_procedure = StoredProcedureManager.genericReturnedRef(connection, target_sql);
		
		ResultSet source_results = (ResultSet) source_stored_procedure.getObject(1);
		ResultSet target_results = (ResultSet) target_stored_procedure.getObject(1);
			
		ArrayList<TabData> source_data = new ArrayList<TabData>();
		HashMap<Long, TabData> target_data = new HashMap<Long, TabData>();
		
		while (source_results.next())
		{										
			source_data.add(getTabData(source_results.getLong("recordid"), source_results.getString("chardata")));				
		}
		
		while (target_results.next())
		{
			target_data.put(target_results.getLong("recordid"), getTabData(target_results.getLong("recordid"), target_results.getString("chardata")));
		}
		
		Iterator<TabData> iter = source_data.iterator();
		while (iter.hasNext())
		{
			TabData sourcefield_data = iter.next();
											
			if (target_data.containsKey(sourcefield_data._recordid))				
				returnList.add(sourcefield_data._chardata + "\t" + target_data.get(sourcefield_data._recordid)._chardata + "\r\n");
			
			AccessControlManager.getSignedOnUser(session, authToken);
		}
		
		source_results.close();
		target_results.close();
		
		source_stored_procedure.close();
		target_stored_procedure.close();
					
		return returnList;
	}
	
	private static boolean generateOdtDocument(Connection connection, HttpSession session, String authToken, Filter filter, ExportType export_type) throws Exception
	{			
		FilterSqlGenerator sql_generator = FilterSqlGenerator.getInstance();
		
		String filter_sql = sql_generator.generateSearchSQLStatement(filter, export_type.getSourceField());
				
		ArrayList<ExportEntry> export_entries = retrieveData (connection, filter_sql, export_type, authToken, session);
		export_entries.addAll(extractSynonyms(export_entries));
		export_entries = sortExportEntries(export_entries);

    	ExportData exportData = getExportData(session);
    	if (exportData == null)
    		return false;
    		    	
    	exportData.outFile = OdfTextDocument.newTextDocument();
    	
		OdfFileDom officeDom = exportData.outFile.getContentDom();
		OdfFileDom styleDom = exportData.outFile.getStylesDom();
		OdfOfficeText officeText = exportData.outFile.getContentRoot();
		
		clearOdt(officeText);
		
		OdfOfficeStyles stylesOfficeStyles = exportData.outFile.getOrCreateDocumentStyles();	
		OdfOfficeMasterStyles stylesOfficeMasterStyles = exportData.outFile.getOfficeMasterStyles();			
		OdfOfficeAutomaticStyles stylesOfficeAutomaticStyles = styleDom.getAutomaticStyles();
		OdfStyleMasterPage master_style = stylesOfficeMasterStyles.getMasterPage(MASTER_PAGE_STYLE);
		
		createFontFaceDecls(stylesOfficeStyles);
											
		createDefaultStyle(stylesOfficeStyles, "10pt");
		createBold10PtStyle(stylesOfficeStyles, "10pt");
		createBold9PtStyle(stylesOfficeStyles, "9pt");
		createBold8PtStyle(stylesOfficeStyles, "8pt");
		createItalicsStyle(stylesOfficeStyles, "8pt");
		createUnderlineStyle(stylesOfficeStyles, "9pt");
		createNormal8ptStyle(stylesOfficeStyles, "8pt");
		createNormal9ptStyle(stylesOfficeStyles, "9pt");
		createSynonymReferenceStyle(stylesOfficeStyles, "14pt");
		createStartEntryStyle(stylesOfficeStyles, "12pt");					
		createTabStyle(styleDom);
					
		createPageNumbersStyle(stylesOfficeMasterStyles, stylesOfficeAutomaticStyles, styleDom, master_style);	
		createPageColumns(styleDom, stylesOfficeAutomaticStyles, master_style);	
		
		OdfTextParagraph paragraph = null;
		OdfTextSpan export_text = null;
																				
		HashMap<String, Integer> letters = getAlpabetLetters();
					
		for (ExportEntry entry : export_entries)
		{						
			placeLetter(entry, letters, officeDom, paragraph, export_text, officeText);				
							
			if (! entry.isSynonymEntry())				
				buildExportEntryCell(entry, export_type, officeDom, paragraph, export_text, officeText);				
			else				
				buildSynonymExportEntryCell((SynonymExportEntry)entry, export_type, officeDom, paragraph, export_text, officeText);				
			
			paragraph = new OdfTextParagraph(officeDom);
			addToOfficeText(officeText, paragraph);
			
			AccessControlManager.getSignedOnUser(session, authToken);
		}	
					
		setExportData(session, exportData);
					
		return true;
	}
	
	private static OdfOfficeFontFaceDecls getStylesFontFaceDecls(OdfOfficeStyles stylesOfficeStyles)
	{		
		_odt_font = "Arial";
		
		Node node = stylesOfficeStyles.getPreviousSibling();
		if (node instanceof OdfOfficeFontFaceDecls)
		{
			_odt_font = "Arial Unicode MS";
			return (OdfOfficeFontFaceDecls)node;
		}
		
		return null;
	}
	
	private static void clearOdt(OdfOfficeText officeDom)
	{
		Node childNode = null; 
		childNode = officeDom.getFirstChild();
		
		while (childNode != null) 
		{ 
			officeDom.removeChild(childNode); 
			childNode = officeDom.getFirstChild(); 
		} 
	}
	
	private static HashMap<String, Integer> getAlpabetLetters()
	{
		HashMap<String, Integer> letters = new HashMap<String, Integer>();
		
		for (char letter = 'a'; letter <= 'z'; letter++)
		{
			String alpha = "" + letter;
			letters.put(alpha.toUpperCase(), 1);
		}
		
		return letters;
	}
		
	private static void addToOfficeText(OdfOfficeText office_text, Node node)
	{
		office_text.appendChild(node);
	}
	
	private static void placeLetter(ExportEntry entry, HashMap<String, Integer> letters,
			OdfFileDom officeDom, OdfTextParagraph letter_paragraph, OdfTextSpan letter_text, OdfOfficeText office_text)
	{		
		// Note: Only add to the OdfOfficeText once the OdfTextParagraph has been re-initialized.
		
		String chardata = entry.getCharData();
		
		chardata = chardata.replaceAll("[0-9]", "");
		chardata = chardata.replaceAll("[^\\p{L}\\p{N}]", "");	
		
		if (chardata.isEmpty())
			return;
		
		String letter = chardata.substring(0, 1);
		
		if (! letter.matches("[a-zA-Z]"))
			return;
		
		if (letters.containsKey(letter.toUpperCase()))
		{
			letter_paragraph = new OdfTextParagraph(officeDom);
			
			letter_text = new OdfTextSpan(officeDom);
			
			letter_text.addStyledContent(BOLD_12PT, letter.toUpperCase());
			
			letter_paragraph.appendChild(letter_text);
				
			addToOfficeText(office_text, letter_paragraph);
			addToOfficeText(office_text, new OdfTextParagraph(officeDom));
						
			letters.remove(letter.toUpperCase());			
		}						
	}
		
	private static void buildExportEntryCell(ExportEntry entry, ExportType export_type,
			OdfFileDom officeDom, OdfTextParagraph entry_paragraph, OdfTextSpan entry_text, OdfOfficeText officeText) throws Exception
	{		
		AppConfig props = AppConfig.getInstance();
		
		// Note: Only add to the OdfOfficeText once the OdfTextParagraph has been re-initialized.
				
		String index_preset_metadata = getPresetMetaData(entry.getIndexData());
		String index_note_metadata = getNoteMetaData(entry.getIndexData());
		String index_context_metadata = getContextMetaData(entry.getIndexData());
			
		entry_paragraph = new OdfTextParagraph(officeDom);
		entry_text = new OdfTextSpan(officeDom);

		entry_text.addStyledContent(BOLD_10PT, entry.getCharData());
		entry_paragraph.appendChild(entry_text);
		
		entry_text = new OdfTextSpan(officeDom);
		
		entry_text.addContent(" ");		
		entry_paragraph.appendChild(entry_text);			
														
		if (! index_preset_metadata.isEmpty())	
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, index_preset_metadata);
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
		}
		
		if (! index_note_metadata.isEmpty())	
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, index_note_metadata);
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
		}
				
		if (! index_context_metadata.isEmpty())		
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(ITALICS, index_context_metadata);
						
			entry_paragraph.appendChild(entry_text);
		}
			
		addToOfficeText(officeText, entry_paragraph);
		
		String index_definition_metadata = getDefinitionMetaData(entry.getIndexData());
		
		if (! index_definition_metadata.isEmpty())
		{			
			entry_paragraph = new OdfTextParagraph(officeDom);
			
			entry_text = new OdfTextSpan(officeDom);			
			entry_text.addStyledContent(ITALICS, index_definition_metadata);
								
			entry_paragraph.appendChild(entry_text);
			
			addToOfficeText(officeText, entry_paragraph);
		}
							
		ArrayList<ExportEntry> index_subfields = entry.getIndexData();
				
		if (index_subfields.size() > 0)
		{		
			for (ExportEntry index_sub : index_subfields)
			{								
				if (index_sub.getInputField().isSynonymField())					
					addSynonyms(index_sub, officeDom, entry_paragraph, entry_text, export_type, officeText);									
			}
									
			for (ExportEntry index_sub : index_subfields)
			{		
				entry_paragraph = new OdfTextParagraph(officeDom);						
				
				if (index_sub.getInputField().isSynonymField())									
					continue;				
				
				if (index_sub.getInputField().getFieldName().equalsIgnoreCase(props.getDefinition()))
					continue;
				
				if (index_sub.getInputField().getFieldName().equalsIgnoreCase(props.getContext()))
					continue;
				
				if (index_sub.getInputField().hasPresetFields())
					continue;
				
				if (index_sub.getInputField().getFieldName().equalsIgnoreCase(props.getNote()))
					continue;
				
				if (export_type.includeFieldNames())
				{									
					entry_text = new OdfTextSpan(officeDom);						
					entry_text.addStyledContent(UNDERLINE, index_sub.getField());
					
					entry_paragraph.appendChild(entry_text);		
					
					entry_text = new OdfTextSpan(officeDom);							
					entry_text.addContentWhitespace("\t");
					entry_text.addContentWhitespace("\t");
					
					entry_paragraph.appendChild(entry_text);
											
					entry_text = new OdfTextSpan(officeDom);			
					entry_text.addStyledContent(NORMAL_9PT, index_sub.getCharData());
					
					entry_paragraph.appendChild(entry_text);
				}
				else							
				{
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addStyledContent(NORMAL_9PT, index_sub.getCharData());
					
					entry_paragraph.appendChild(entry_text);	
				}
				
				addToOfficeText(officeText, entry_paragraph);
			}
		}
		
		ArrayList<ExportEntry> target_data = entry.getTargetData();
		
		if (target_data.size() > 0)
		{
			for (ExportEntry target_entry : target_data)
			{			
				entry_paragraph = new OdfTextParagraph(officeDom);
											
				String target_preset_metadata = getPresetMetaData(target_entry.getTargetData());
				String target_note_metadata = getNoteMetaData(target_entry.getIndexData());
				String target_context_metadata = getContextMetaData(target_entry.getTargetData());
				
				if (export_type.includeFieldNames())
				{
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addStyledContent(NORMAL_8PT, target_entry.getField());
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);								
					entry_text.addContentWhitespace("\t");
					entry_text.addContentWhitespace("\t");
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addStyledContent(BOLD_9PT, target_entry.getCharData());	
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
				}							
				else	
				{
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addStyledContent(BOLD_9PT, target_entry.getCharData());
					
					entry_paragraph.appendChild(entry_text);
				}
																
				if (! target_preset_metadata.isEmpty())	
				{
					entry_text = new OdfTextSpan(officeDom);	
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);						
					entry_text.addStyledContent(NORMAL_8PT, target_preset_metadata);
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);	
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
				}
				
				if (! target_note_metadata.isEmpty())	
				{
					entry_text = new OdfTextSpan(officeDom);						
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);				
					entry_text.addStyledContent(NORMAL_8PT, target_note_metadata);
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);	
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
				}
				
				if (! target_context_metadata.isEmpty())	
				{
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addStyledContent(ITALICS, target_context_metadata);
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);		
					entry_text.addContent(" ");
					
					entry_paragraph.appendChild(entry_text);
				}
					
				addToOfficeText(officeText, entry_paragraph);
											
				String target_definition_metadata = getDefinitionMetaData(target_entry.getTargetData());
				
				if (! target_definition_metadata.isEmpty())
				{
					entry_paragraph = new OdfTextParagraph(officeDom);
					
					entry_text = new OdfTextSpan(officeDom);						
					entry_text.addStyledContent(ITALICS, target_definition_metadata);							
					
					entry_paragraph.appendChild(entry_text);
										
					addToOfficeText(officeText, entry_paragraph);
				}						
				
				ArrayList<ExportEntry> target_subs = target_entry.getTargetData();
				
				if (target_subs.size() > 0)
				{		
					for (ExportEntry target_sub_entry : target_subs)
					{												
						if (target_sub_entry.getInputField().isSynonymField())						
							addSynonyms(target_sub_entry, officeDom, entry_paragraph, entry_text, export_type, officeText);													
					}
					
					
					for (ExportEntry target_sub_entry : target_subs)
					{		
						entry_paragraph = new OdfTextParagraph(officeDom);									

						if (target_sub_entry.getInputField().isSynonymField())							
							continue;					
							
						if (target_sub_entry.getInputField().getFieldName().equalsIgnoreCase(props.getDefinition()))
							continue;
						
						if (target_sub_entry.getInputField().getFieldName().equalsIgnoreCase(props.getContext()))
							continue;
						
						if (target_sub_entry.getInputField().hasPresetFields())
							continue;
						
						if (target_sub_entry.getInputField().getFieldName().equalsIgnoreCase(props.getNote()))
							continue;
						
						if (export_type.includeFieldNames())
						{
							entry_text = new OdfTextSpan(officeDom);	
							entry_text.addStyledContent(UNDERLINE, target_sub_entry.getField());	
							
							entry_paragraph.appendChild(entry_text);		
							
							entry_text = new OdfTextSpan(officeDom);								
							entry_text.addContentWhitespace("\t");
							entry_text.addContentWhitespace("\t");
							
							entry_paragraph.appendChild(entry_text);
							
							entry_text = new OdfTextSpan(officeDom);										
							entry_text.addStyledContent(NORMAL_9PT, target_sub_entry.getCharData());	
							
							entry_paragraph.appendChild(entry_text);		
						}
						else
						{
							entry_text = new OdfTextSpan(officeDom);	
							entry_text.addStyledContent(NORMAL_9PT, target_sub_entry.getCharData());
							
							entry_paragraph.appendChild(entry_text);	
						}
																												
						addToOfficeText(officeText, entry_paragraph);
					}			
				}
			}					
		}	
														
		ArrayList<ExportEntry> record_data = entry.getRecordData();
		
		if (record_data.size() > 0)
		{										
			for (ExportEntry record_entry : record_data)
			{						
				entry_paragraph = new OdfTextParagraph(officeDom);
				
				if (export_type.includeFieldNames())
				{
					entry_text = new OdfTextSpan(officeDom);						
					entry_text.addStyledContent(UNDERLINE, record_entry.getField());
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);					
					entry_text.addContentWhitespace("\t");
					entry_text.addContentWhitespace("\t");
					
					entry_paragraph.appendChild(entry_text);
					
					entry_text = new OdfTextSpan(officeDom);	
					entry_text.addStyledContent(NORMAL_9PT, record_entry.getCharData());
					
					entry_paragraph.appendChild(entry_text);								
				}
				else
				{
					entry_text = new OdfTextSpan(officeDom);	
					entry_text.addStyledContent(NORMAL_8PT, record_entry.getCharData());
					
					entry_paragraph.appendChild(entry_text);
				}
				
				addToOfficeText(officeText, entry_paragraph);
			}											
		}		
	}
	
	private static void buildSynonymExportEntryCell(SynonymExportEntry entry, ExportType export_type,
			OdfFileDom officeDom, OdfTextParagraph synonym_paragraph, OdfTextSpan synonym_text, OdfOfficeText officeText)
	{		
		// Note: Only add to the OdfOfficeText once the OdfTextParagraph has been re-initialized.
		
		if (entry.getCharData().isEmpty())
			return;
		
		synonym_paragraph = new OdfTextParagraph(officeDom);
		
		synonym_text = new OdfTextSpan(officeDom);
		synonym_text.addStyledContent(BOLD_10PT, entry.getCharData());	
		
		synonym_paragraph.appendChild(synonym_text);
		
		synonym_text = new OdfTextSpan(officeDom);
		synonym_text.addContent(" ");
		
		synonym_paragraph.appendChild(synonym_text);
			
		synonym_text = new OdfTextSpan(officeDom);
		synonym_text.addStyledContent(SYNONYM_REF, "→");
		
		synonym_paragraph.appendChild(synonym_text);
		
		synonym_text = new OdfTextSpan(officeDom);
		synonym_text.addContent(" " + entry.getIndexTerm());
		
		synonym_paragraph.appendChild(synonym_text);
				
		addToOfficeText(officeText, synonym_paragraph);
	}
	
	private static void addSynonyms(ExportEntry entry, OdfFileDom officeDom, OdfTextParagraph entry_paragraph,
			OdfTextSpan entry_text, ExportType export_type, OdfOfficeText officeText) throws Exception
	{
		AppConfig props = AppConfig.getInstance();
		
		ArrayList<ExportEntry> subsub_entries = entry.getSubfieldSubfields();
		
		String synonym_presets = getPresetMetaData(subsub_entries);
		String synonym_note = getNoteMetaData(subsub_entries);
		String synonym_context = getContextMetaData(subsub_entries);
		
		entry_paragraph = new OdfTextParagraph(officeDom);		
		
		if (export_type.includeFieldNames())
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, entry.getField());
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContentWhitespace("\t");
			entry_text.addContentWhitespace("\t");
			
			entry_paragraph.appendChild(entry_text);
									
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(BOLD_9PT, entry.getCharData());
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);			
		}
		else
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(BOLD_9PT, entry.getCharData());
			
			entry_paragraph.appendChild(entry_text);			
		}
		
		if (! synonym_presets.isEmpty())
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, synonym_presets);
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
		}
								
		if (! synonym_note.isEmpty())
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, synonym_note);
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
		}
								
		if (! synonym_context.isEmpty())
		{
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addStyledContent(NORMAL_8PT, synonym_note);
			
			entry_paragraph.appendChild(entry_text);
			
			entry_text = new OdfTextSpan(officeDom);
			entry_text.addContent(" ");
			
			entry_paragraph.appendChild(entry_text);
		}
		
		addToOfficeText(officeText, entry_paragraph);
						
		boolean has_subsub_field = false;
		
		// Get this synonym's subsub fields
		for (ExportEntry subsubentry : subsub_entries)
		{
			entry_paragraph = new OdfTextParagraph(officeDom);
			
			if (subsubentry.getInputField().getFieldName().equalsIgnoreCase(props.getDefinition()))
				continue;
			
			if (subsubentry.getInputField().getFieldName().equalsIgnoreCase(props.getContext()))
				continue;
			
			if (subsubentry.getInputField().hasPresetFields())
				continue;
			
			if (subsubentry.getInputField().getFieldName().equalsIgnoreCase(props.getNote()))
				continue;
			
			if (export_type.includeFieldNames())
			{
				entry_text = new OdfTextSpan(officeDom);
				entry_text.addStyledContent(UNDERLINE, subsubentry.getField());
				
				entry_paragraph.appendChild(entry_text);
				
				entry_text = new OdfTextSpan(officeDom);
				entry_text.addContentWhitespace("\t");
				entry_text.addContentWhitespace("\t");
				
				entry_paragraph.appendChild(entry_text);
										
				entry_text = new OdfTextSpan(officeDom);
				entry_text.addStyledContent(NORMAL_9PT, subsubentry.getCharData());
				
				entry_paragraph.appendChild(entry_text);
				
				entry_text = new OdfTextSpan(officeDom);
				entry_text.addContent(" ");
				
				entry_paragraph.appendChild(entry_text);
			}
			else
			{
				entry_text = new OdfTextSpan(officeDom);
				entry_text.addStyledContent(NORMAL_9PT, subsubentry.getCharData());
				
				entry_paragraph.appendChild(entry_text);		
			}
			
			has_subsub_field = true;
		}
		
		if (has_subsub_field)
			addToOfficeText(officeText, entry_paragraph);
	}
	
	private static String getPresetMetaData(ArrayList<ExportEntry> export_entries)
	{
		String presets = "";
		Iterator<ExportEntry> iter = export_entries.iterator();
		while (iter.hasNext())
		{
			ExportEntry entry = iter.next();
			if (entry.getInputField().hasPresetFields())		
				presets = presets + "<" + entry.getCharData() + "> ";			
		}
		
		return presets;
	}
	
	private static String getNoteMetaData(ArrayList<ExportEntry> export_entries) throws Exception
	{
		AppConfig props = AppConfig.getInstance();
		
		String note_to_manager = "";
		Iterator<ExportEntry> iter = export_entries.iterator();
		while (iter.hasNext())
		{
			ExportEntry entry = iter.next();
			if (entry.getField().equalsIgnoreCase(props.getNote()))			
				note_to_manager = note_to_manager + "<" + entry.getCharData() + ">";								
		}
		
		return note_to_manager;
	}
	
	private static String getContextMetaData(ArrayList<ExportEntry> export_entries) throws Exception
	{
		AppConfig props = AppConfig.getInstance();
		
		String context = "";
		Iterator<ExportEntry> iter = export_entries.iterator();
		while (iter.hasNext())
		{
			ExportEntry entry = iter.next();
			if (entry.getField().equalsIgnoreCase(props.getContext()))			
				context = context + "{" + entry.getCharData() + "}";								
		}
		
		return context;
	}
		
	private static String getDefinitionMetaData(ArrayList<ExportEntry> export_entries) throws Exception
	{
		AppConfig props = AppConfig.getInstance();
		
		String definition = "";
		Iterator<ExportEntry> iter = export_entries.iterator();
		while (iter.hasNext())
		{
			ExportEntry entry = iter.next();
			if (entry.getField().equalsIgnoreCase(props.getDefinition()))			
				definition = definition + entry.getCharData();								
		}
		
		return definition;
	}
		
	private static void createFontFaceDecls(OdfOfficeStyles stylesOfficeStyles)
	{		
		OdfOfficeFontFaceDecls font_faces = getStylesFontFaceDecls(stylesOfficeStyles);
		
		if (font_faces != null)
		{
			OdfStyleFontFace font = (OdfStyleFontFace) font_faces.newStyleFontFaceElement(_odt_font); 
						
			font.setStyleFontFamilyGenericAttribute("system");
			font.setStyleFontPitchAttribute("variable");
			font.setSvgFontFamilyAttribute(_odt_font);
		}
	}
	
	private static void createDefaultStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfDefaultStyle defaultStyle = stylesOfficeStyles.getDefaultStyle(OdfStyleFamily.Paragraph);
			
		defaultStyle.setProperty(OdfStyleTextProperties.FontWeight, "normal");
		defaultStyle.setProperty(OdfStyleTextProperties.FontWeightAsian, "normal");
		defaultStyle.setProperty(OdfStyleTextProperties.FontWeightComplex, "normal");
		
		//defaultStyle.setProperty(OdfParagraphProperties.KeepTogether, "always");
		//defaultStyle.setProperty(OdfParagraphProperties.KeepWithNext, "always");
		
		addFontStyle(defaultStyle, _odt_font);
		addFontSizeStyle(defaultStyle, font_size);	
	}
	
	private static void createBold10PtStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
				
        style = stylesOfficeStyles.newStyle(BOLD_10PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(BOLD_10PT);
    	          
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addBoldStyle(style);
		addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createBold9PtStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
				
        style = stylesOfficeStyles.newStyle(BOLD_9PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(BOLD_9PT);
    	     
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addBoldStyle(style);
		addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createBold8PtStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
				
        style = stylesOfficeStyles.newStyle(BOLD_8PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(BOLD_8PT);
    	     
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addBoldStyle(style);
		addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createItalicsStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(ITALICS, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(ITALICS);
    	                
    	style.setProperty(OdfStyleTextProperties.FontStyle, "italic");
		style.setProperty(OdfStyleTextProperties.FontStyleAsian, "italic");
		style.setProperty(OdfStyleTextProperties.FontStyleComplex, "italic");
        
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
		
		addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createUnderlineStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(UNDERLINE, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(UNDERLINE);
    	
		style.setProperty(OdfStyleTextProperties.TextUnderlineStyle, "solid");
    	  
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
		
		addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createNormal8ptStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(NORMAL_8PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(NORMAL_8PT);
    	    
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createNormal9ptStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(NORMAL_9PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(NORMAL_9PT);
    	    
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
	
	private static void createSynonymReferenceStyle(OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(SYNONYM_REF, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(SYNONYM_REF);
    	    
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addFontStyle(style, _odt_font);
        addFontSizeStyle(style, "14pt");
	}
	
	private static void createStartEntryStyle (OdfOfficeStyles stylesOfficeStyles, String font_size)
	{
		OdfStyle style = null;
		
        style = stylesOfficeStyles.newStyle(BOLD_12PT, OdfStyleFamily.Text);
    	style.setStyleDisplayNameAttribute(BOLD_12PT);
    	    
    	//style.setProperty(OdfParagraphProperties.KeepTogether, "always");
    	//style.setProperty(OdfParagraphProperties.KeepWithNext, "always");
    	
    	addBoldStyle(style);
    	addFontStyle(style, _odt_font);
        addFontSizeStyle(style, font_size);
	}
		
	private static void createTabStyle(OdfFileDom stylesDom)
	{
		OdfStyleTabStops tabStops = null;
		OdfStyleTabStop tabStop = null;
		
		tabStop = new OdfStyleTabStop(stylesDom);
        tabStop.setStylePositionAttribute("7.5cm");
		tabStop.setStyleLeaderStyleAttribute("dotted");
        tabStop.setStyleLeaderTextAttribute(".");
        tabStop.setStyleTypeAttribute("right");

		tabStops = new OdfStyleTabStops(stylesDom);
		tabStops.appendChild(tabStop);
	}
		
	private static void createPageNumbersStyle(OdfOfficeMasterStyles stylesOfficeMasterStyles, 
			OdfOfficeAutomaticStyles stylesOfficeAutomaticStyles, 
			OdfFileDom stylesDom, OdfStyleMasterPage master_style)
	{												
		StyleFooterElement footer = master_style.newStyleFooterElement();			
					
		TextPElement text_p = footer.newTextPElement();			
		text_p.setTextStyleNameAttribute(TEXT_PTEXT_NAME);
		
		TextPageNumberElement number = text_p.newTextPageNumberElement();
		number.setTextSelectPageAttribute(TextPageNumberElement.TextSelectPageAttributeValue.CURRENT.toString());
								
		text_p.appendChild(number);			
		footer.appendChild(text_p);
		master_style.appendChild(footer);
																
		createPageNumbersAlignment(stylesOfficeAutomaticStyles);							
	}
	
	private static void createPageColumns(OdfFileDom stylesDom, OdfOfficeAutomaticStyles stylesOfficeAutomaticStyles,
								  OdfStyleMasterPage master_style)
	{
        int columnsNumber = 2;
        String vSpacingColumn = "0.20cm";
        
        String stylePageLayoutName = master_style.getStylePageLayoutNameAttribute();
        
        OdfStylePageLayout pageLayout = stylesOfficeAutomaticStyles.getPageLayout(stylePageLayoutName);
        NodeList vListStlePageLprop = pageLayout.getElementsByTagName("style:page-layout-properties");
        StylePageLayoutPropertiesElement vStlePageLprop = (StylePageLayoutPropertiesElement) vListStlePageLprop.item(0);
        
        StyleColumnsElement vStyleColumnsElement = new StyleColumnsElement(stylesDom);
        vStyleColumnsElement.setFoColumnCountAttribute(columnsNumber);
        vStyleColumnsElement.setFoColumnGapAttribute(vSpacingColumn);

        vStlePageLprop.appendChild(vStyleColumnsElement);
	}
	
	private static void addFontSizeStyle(OdfStyleBase style, String value)
	{
		style.setProperty(OdfStyleTextProperties.FontSize, value);
		style.setProperty(OdfStyleTextProperties.FontSizeAsian, value);
		style.setProperty(OdfStyleTextProperties.FontSizeComplex, value);
	}
	
	private static void addFontStyle(OdfStyleBase style, String value)
	{
		style.setProperty(OdfStyleTextProperties.FontName, value);
		style.setProperty(OdfStyleTextProperties.FontNameAsian, value);
		style.setProperty(OdfStyleTextProperties.FontNameComplex, value);		
	}
	
	private static void addBoldStyle(OdfStyleBase style)
	{
		style.setProperty(OdfStyleTextProperties.FontWeight, "bold");
		style.setProperty(OdfStyleTextProperties.FontWeightAsian, "bold");
		style.setProperty(OdfStyleTextProperties.FontWeightComplex, "bold");
	}
		
	private static void createPageNumbersAlignment(OdfOfficeAutomaticStyles stylesOfficeAutomaticStyles)
	{
		OdfStyle style = stylesOfficeAutomaticStyles.newStyle(OdfStyleFamily.Paragraph);
		
		style.setStyleNameAttribute(TEXT_PTEXT_NAME);		
		style.setStyleParentStyleNameAttribute(TEXT_STYLE_NAME);
		style.setProperty(OdfParagraphProperties.TextAlign, "center");
		style.setProperty(OdfParagraphProperties.JustifySingleWord, "false");
		
		addFontStyle(style, _odt_font);
		addFontSizeStyle(style, "8pt");
	}
	
	private static ArrayList<ExportEntry> retrieveData(Connection connection, String filter_sql, ExportType export_type, String authToken, HttpSession session) throws Exception
	{				
		ArrayList<ExportEntry> sort_indexes = new ArrayList<ExportEntry>();
		
		String sql = filter_sql + "select distinct terms.termid, terms.recordid, fields.fieldid, fields.fieldname, terms.chardata from " +
								  "tms.fields, tms.terms " +
								  "where fields.fieldid = terms.fieldid " +
								  "and fields.fieldid = " + export_type.getSourceField().getFieldId() + " " +
								  "and terms.archivedtimestamp is NULL " +
								  "and terms.recordid IN (SELECT recordid FROM with_filter) " +
								  "order by terms.chardata asc";
		
		CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
		
		ResultSet result_set = (ResultSet)stored_procedure.getObject(1);
							
		ExportEntry export_entry = null;
		
		while (result_set.next())
		{
			export_entry = new ExportEntry();
			
			long term_id = result_set.getLong("termid");
			long recordid = result_set.getLong("recordid");
			export_entry = createExportEntry(recordid, term_id, result_set.getString("fieldname"), result_set.getString("chardata"), export_type.getSourceField());
														
			ArrayList<TerminlogyObject> recordattributes = export_type.getRecordFields();
			if (recordattributes != null && recordattributes.size() > 0)
			{
				ArrayList<ExportEntry> record_data = retrieveRecordAttributes(connection, recordid, recordattributes);
				export_entry.setRecordData(record_data);
			}
				
			Term term = (Term) export_type.getSourceField();
			
			ArrayList<ChildTerminologyObject> index_subfields = term.getTermAttributes();
			
			if (index_subfields != null && index_subfields.size() > 0)
			{		
				ArrayList<ExportEntry> index_data = retrieveIndexSubFields(connection, term_id, recordid, export_type.getSourceField().getFieldId(), index_subfields);					
				export_entry.setIndexData(index_data);
				
				Iterator<ExportEntry> iter = index_data.iterator();
				while (iter.hasNext())					
				{					
					ExportEntry index_entry = iter.next();
					
					if (index_entry.getInputField().isSynonymField() && ! index_entry.getCharData().isEmpty())
					{
						Synonym synonym = (Synonym)index_entry.getInputField();
						
						ArrayList<ChildTerminologyObject> index_subfieldsubfields = synonym.getSynonymAttributes();
						
						if (index_subfieldsubfields != null && index_subfieldsubfields.size() > 0)
						{
							ArrayList<ExportEntry> index_synonymdata = retrieveAttributeSubfields(connection, recordid, export_entry.getResourceId(), index_entry.getResourceId(),  export_type.getSourceField().getFieldId(), index_subfieldsubfields);
							index_entry.setSubfieldSubfields(index_synonymdata);
						}
					}
				}
			}
			
			ArrayList<TerminlogyObject> target_entries = export_type.getTargetFields(); 
			
			if (target_entries != null && target_entries.size() > 0)
			{
				ArrayList<ExportEntry> targets = retrieveTargets(connection, recordid, target_entries);
				export_entry.setTargetData(targets);
			}
			
			sort_indexes.add(export_entry);
			
			AccessControlManager.getSignedOnUser(session, authToken);
		}			
		
		result_set.close();
		stored_procedure.close();
		
		return sort_indexes;
	}
	
	private static ArrayList<ExportEntry> retrieveIndexSubFields(Connection connection, long term_id, long recordid, long source_fieldid, ArrayList<ChildTerminologyObject> index_subfields) throws SQLException
	{
		ArrayList<ExportEntry> index_data = new ArrayList<ExportEntry>();
		
		if (index_subfields.size() > 0)
		{
			for (TerminlogyObject index_subfield : index_subfields)
			{
				CallableStatement stored_procedure = null;
				ResultSet results = null;
				
				if (index_subfield instanceof Synonym)
				{
					String sub_sql = " select distinct synonyms.synonymid, fields.fieldname, synonyms.chardata from tms.fields, tms.synonyms, tms.terms" +
									 " where fields.fieldid = "  + index_subfield.getFieldId() + 
									 " and fields.fieldid = synonyms.fieldid " + 
									 " and terms.fieldid = " + source_fieldid + 
									 " and synonyms.archivedtimestamp is NULL" + 
									 " and synonyms.termid = " + term_id + 
									 " and terms.recordid = " +  recordid +									  
									 " order by synonyms.chardata asc";
	
					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sub_sql);
					results = (ResultSet) stored_procedure.getObject(1);
						
					ExportEntry entry = null;
					
					while (results.next())
					{
						entry = new ExportEntry();
						
						entry = createExportEntry(results.getLong("synonymid"), results.getString("fieldname"), results.getString("chardata"), index_subfield);
						
						index_data.add(entry);
					}
				}
				else
				{
					String sub_sql = " select distinct termattributes.termattributeid, fields.fieldname, termattributes.chardata from tms.fields, tms.termattributes, tms.terms" +
									 " where fields.fieldid = "  + index_subfield.getFieldId() + 
									 " and fields.fieldid = termattributes.fieldid " + 
									 " and terms.fieldid = " + source_fieldid + 
									 " and termattributes.archivedtimestamp is NULL" + 
									 " and termattributes.termid = " + term_id + 
									 " and terms.recordid = " +  recordid +									  
									 " order by termattributes.chardata asc";
					
					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sub_sql);
					results = (ResultSet) stored_procedure.getObject(1);
						
					ExportEntry entry = null;
					
					while (results.next())
					{
						entry = new ExportEntry();
						
						entry = createExportEntry(results.getLong("termattributeid"), results.getString("fieldname"), results.getString("chardata"), index_subfield);
						
						index_data.add(entry);
					}	
				}
				
				results.close();
				stored_procedure.close();
			}
		}
		
		return index_data;
	}
	
	private static ArrayList<ExportEntry> retrieveAttributeSubfields(Connection connection, long recordid, long term_id, long synonym_id, long source_fieldid, ArrayList<ChildTerminologyObject> subfieldsubfields) throws SQLException
	{
		ArrayList<ExportEntry> sub_attr_subfield_data = new ArrayList<ExportEntry>();
		
		if (subfieldsubfields.size() > 0)
		{
			for (ChildTerminologyObject input_field : subfieldsubfields)
			{
				String sub_sql = " select distinct synonymattributes.synonymattributeid, fields.fieldname, synonymattributes.chardata from tms.fields, tms.synonymattributes, tms.synonyms, tms.terms " +
								 " where fields.fieldid = " +  input_field.getFieldId() + 
								 " and synonymattributes.fieldid = fields.fieldid " +   
								 " and synonymattributes.synonymid = synonyms.synonymid " + 
								 " and synonyms.synonymid = " + synonym_id + 
								 " and synonyms.termid = terms.termid " + 
								 " and terms.termid = " + term_id + 
								 " and terms.fieldid = " +  source_fieldid + 
								 " and terms.recordid = " +  recordid + 
								 " and synonymattributes.archivedtimestamp is NULL" + 
								 " order by synonymattributes.chardata asc";
				
				CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sub_sql);
				ResultSet results = (ResultSet)stored_procedure.getObject(1);
				
				ExportEntry entry = null;
				
				while (results.next())
				{
					entry = new ExportEntry();
					
					entry = createExportEntry(results.getLong("synonymattributeid"), results.getString("fieldname"), results.getString("chardata"), input_field);
					
					sub_attr_subfield_data.add(entry);
				}
				
				results.close();
				stored_procedure.close();
			}
		}
		
		return sub_attr_subfield_data;
	}
	
	private static ArrayList<ExportEntry> retrieveRecordAttributes(Connection connection, long recordid, ArrayList<TerminlogyObject> recordattributes) throws Exception
	{
		AppConfig props = AppConfig.getInstance();
		
		ArrayList<ExportEntry> record_data = new ArrayList<ExportEntry>();
		
		if (recordattributes.size() > 0)
		{
			for (TerminlogyObject input_field : recordattributes)
			{		
				CallableStatement stored_procedure = null;
				ResultSet result_set = null;
				
				if (! input_field.getFieldName().equalsIgnoreCase(props.getProjectField()))
				{
					String sql = " select distinct recordattributes.recordattributeid, fields.fieldname, recordattributes.chardata from tms.fields, tms.recordattributes" + 
								 " where fields.fieldid = recordattributes.fieldid "  + 
								 " and fields.fieldid = " + input_field.getFieldId() + 		
								 " and recordattributes.recordid =  " +  recordid +
								 " and recordattributes.archivedtimestamp is NULL" + 
								 " order by recordattributes.chardata asc";
						
					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
					
					result_set = (ResultSet) stored_procedure.getObject(1);
										
					ExportEntry export_entry = null;
					
					while (result_set.next())
					{
						export_entry = createExportEntry(result_set.getLong("recordattributeid"), result_set.getString("fieldname"), result_set.getString("chardata"), input_field);					
						record_data.add(export_entry);
					}	
				}
				else
				{
					String sql = "select projects.projectid, projects.projectname" + 
								 " from tms.projects, tms.records, tms.recordprojects" +  
								 " where projects.projectid = recordprojects.projectid" + 
								 " and records.recordid = recordprojects.recordid" +  
								 " and records.recordid = " + recordid +
								 " and records.archivedtimestamp is NULL";
					
					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);	
					
					result_set = (ResultSet) stored_procedure.getObject(1);
					
					ExportEntry export_entry = null;
					
					while (result_set.next())
					{
						export_entry = createExportEntry(result_set.getLong("projectid"), props.getProjectField(), result_set.getString("projectname"), input_field);					
						record_data.add(export_entry);
					}						
				}
				
				result_set.close();
				stored_procedure.close();				
			}
		}
		
		return record_data;
	}
	
	private static ArrayList<ExportEntry> retrieveTargets(Connection connection, long recordid, ArrayList<TerminlogyObject> targets) throws SQLException
	{
		ArrayList<ExportEntry> target_entries = new ArrayList<ExportEntry>();
		
		if (targets.size() > 0)
		{
			for (TerminlogyObject target_field : targets)
			{
				String sql = " select distinct terms.termid, fields.fieldname, fields.fieldid, terms.chardata from tms.fields, tms.terms" +
				 			 " where fields.fieldid = terms.fieldid "  + 
				 			 " and fields.fieldid =  "  + target_field.getFieldId() + 
				 			 " and terms.recordid = " + recordid + 
				 			 " and terms.archivedtimestamp is NULL" +
				 			 " order by terms.chardata asc";
				
				CallableStatement stored_procedure = StoredProcedureManager.genericReturnedRef(connection, sql);
				ResultSet result_set = (ResultSet)stored_procedure.getObject(1);
	
				ExportEntry export_entry = null;
	
				while (result_set.next())
				{
					export_entry = createExportEntry(result_set.getLong("termid"), result_set.getString("fieldname"), result_set.getString("chardata"),  target_field);
					export_entry.setField(result_set.getString("fieldname"));
															
					ArrayList<ChildTerminologyObject> target_subfields = ((Term)target_field).getTermAttributes();
					ArrayList<ExportEntry> target_data = retrieveTargetSubFields(connection, recordid, target_field.getFieldId(), target_subfields);
					export_entry.setTargetData(target_data);	
					
					Iterator<ExportEntry> iter = target_data.iterator();
					while (iter.hasNext())					
					{
						ExportEntry target_entry = iter.next();
						if (target_entry.getInputField().isSynonymField())
						{
							Synonym synonym = (Synonym)target_entry.getInputField();
							ArrayList<ChildTerminologyObject> index_subfieldsubfields = synonym.getSynonymAttributes();
							ArrayList<ExportEntry> synonymdata = retrieveAttributeSubfields(connection, recordid, export_entry.getResourceId(), export_entry.getResourceId(), target_entry.getResourceId(), index_subfieldsubfields);
							target_entry.setSubfieldSubfields(synonymdata);
						}
					}
				}
				
				if (export_entry != null)
					target_entries.add(export_entry);
				
				result_set.close();
				stored_procedure.close();
			}
		}
		
		return target_entries;
	}
	
	private static ArrayList<ExportEntry> retrieveTargetSubFields(Connection connection, long recordid, long source_fieldid,ArrayList<ChildTerminologyObject> target_subfields) throws SQLException
	{
		ArrayList<ExportEntry> targets = new ArrayList<ExportEntry>();
		
		if (target_subfields.size() > 0)
		{
			for (TerminlogyObject subfield : target_subfields)
			{		
				CallableStatement stored_procedure = null;
				ResultSet target_set = null;
				
				if (subfield instanceof Synonym)
				{
					String target_sql = " select distinct synonyms.synonymid, fields.fieldname, synonyms.chardata from tms.fields, tms.synonyms, tms.terms" + 
									    " where fields.fieldid = " +  subfield.getFieldId() +
										" and synonyms.fieldid = fields.fieldid " + 
										" and synonyms.termid = terms.termid " + 										
										" and terms.fieldid = " + source_fieldid + 
										" and terms.recordid = " + recordid + 
										" and synonyms.archivedtimestamp is NULL" + 
										" order by synonyms.chardata asc";

					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, target_sql);
					target_set = (ResultSet) stored_procedure.getObject(1);
					
					ExportEntry export_entry = null;
					
					while (target_set.next())
					{
						export_entry = createExportEntry(target_set.getLong("synonymid"), target_set.getString("fieldname"), target_set.getString("chardata"), subfield);										
						targets.add(export_entry);
					}
				}
				else
				{
					String target_sql = " select distinct termattributes.termattributeid, fields.fieldname, termattributes.chardata from tms.fields, tms.termattributes, tms.terms" + 
									    " where fields.fieldid = " +  subfield.getFieldId() +
										" and termattributes.fieldid = fields.fieldid " + 
										" and termattributes.termid = terms.termid " + 										
										" and terms.fieldid = " + source_fieldid + 
										" and terms.recordid = " + recordid + 
										" and termattributes.archivedtimestamp is NULL" + 
										" order by termattributes.chardata asc";

					stored_procedure = StoredProcedureManager.genericReturnedRef(connection, target_sql);
					target_set = (ResultSet) stored_procedure.getObject(1);
					
					ExportEntry export_entry = null;
					
					while (target_set.next())
					{
						export_entry = createExportEntry(target_set.getLong("termattributeid"), target_set.getString("fieldname"), target_set.getString("chardata"), subfield);										
						targets.add(export_entry);
					}
				}
				
				target_set.close();
				stored_procedure.close();
			}
		}
		
		return targets;
	}
	
	private static ArrayList<SynonymExportEntry> extractSynonyms(ArrayList<ExportEntry> export_entries)
	{
		ArrayList<SynonymExportEntry> synonyms = new ArrayList<SynonymExportEntry>();
		
		Iterator<ExportEntry> iter = export_entries.iterator();
		while (iter.hasNext())
		{
			ExportEntry export_entry = iter.next();
			
			ArrayList<ExportEntry> index_subdata = export_entry.getIndexData();
			
			Iterator<ExportEntry> index_subiter = index_subdata.iterator();
			while (index_subiter.hasNext())
			{
				ExportEntry index_sub_entry = index_subiter.next();
				if (index_sub_entry.getInputField().isSynonymField())				
					synonyms.add(createSynonymExportEntry(export_entry.getCharData(), 
														  index_sub_entry.getCharData(),
														  export_entry.getField(),
														  index_sub_entry.getField()));									
			}
		}
		
		return synonyms;
	}
	
	private static ArrayList<ExportEntry> sortExportEntries(ArrayList<ExportEntry> export_entries)
	{
		Collections.sort(export_entries, new Comparator<ExportEntry>()
        {
        	private Collator collator = Collator.getInstance(Locale.ROOT);

			@Override
			public int compare(ExportEntry e1, ExportEntry e2)
			{
				String char_data_1 = e1.getCharData().toLowerCase();
				String char_data_2 = e2.getCharData().toLowerCase();
					
				// Remove: numbers
				char_data_1 = char_data_1.replaceAll("[0-9]", "");
				char_data_2 = char_data_2.replaceAll("[0-9]", "");
				
				// Remove: Spaces, non-word characters
				char_data_1 = char_data_1.replaceAll("[^\\p{L}\\p{N}]", "");			
				char_data_2 = char_data_2.replaceAll("[^\\p{L}\\p{N}]", "");	
				
				return collator.compare(char_data_1, char_data_2);				
			}
        });
		
		return export_entries;
	}
	
	private static ExportEntry createExportEntry(long recordid, long resourceid, String fieldname, String chardata, TerminlogyObject inputfield)
	{
		ExportEntry entry = new ExportEntry();
		
		entry.setRecordId(recordid);
		entry.setResourceId(resourceid);
		entry.setField(fieldname);
		entry.setInputField(inputfield);
		entry.setCharData(chardata);
		
		return entry;		
	}
	
	private static ExportEntry createExportEntry(long resourceid, String fieldname, String chardata, TerminlogyObject inputfield)
	{
		ExportEntry entry = new ExportEntry();
				
		entry.setResourceId(resourceid);
		entry.setField(fieldname);
		entry.setCharData(chardata);
		entry.setInputField(inputfield);
		
		return entry;
	}
	
	private static SynonymExportEntry createSynonymExportEntry(String index_data, String synonym_data, String index_field, String synonym_field)
	{
		SynonymExportEntry synonym = new SynonymExportEntry();
		
		synonym.setIndexTerm(index_data);
		synonym.setCharData(synonym_data);
		synonym.setIndexField(index_field);
		synonym.setField(synonym_field);
		
		return synonym;
	}
	
	private static boolean generateTbxDocument(Connection connection, HttpSession session, String authToken, ExportType export_type) throws Exception
	{		
		// Retrieve the records.
		ArrayList<Record> records = new ArrayList<Record>();

    	ExportData exportData = getExportData(session);
    	if (exportData == null)
    		return false;
    		    	
		RecordIds<Long> recordIDs = RecordIdTracker.getRecordIds(session);
		
		for (long recordId : recordIDs)
		{						
			System.out.println(recordId);
			
			User user = AccessControlManager.getSignedOnUser(session, authToken);	
			
			Record record = RecordManager.retrieveRecordByRecordId(connection, session, user, recordId, false);

			if (record != null)
				records.add(record);								
		}
				
		String outText = TBXCompliance.createTBXString(connection, session, authToken, records, export_type.getXcsPath());
					
		if (!outText.isEmpty())
		{
			exportData.outText = outText;
			setExportData(session, exportData);
			return true;
		}

		
		return false;
	}
}
