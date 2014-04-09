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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import tms2.server.accesscontrol.AccessControlManager;
import tms2.server.termbase.TermBaseManager;
import tms2.shared.ChildTerminologyObject;
import tms2.shared.Project;
import tms2.shared.Record;
import tms2.shared.Term;
import tms2.shared.TerminlogyObject;


/**
 * This class creates the TBX string for export as well as relating the custom Fields of the NLS to the fields specified by the TBX standard. Due to the unavailability of the TBX standard the TBX implementation might not be completely accurate as specified by the standard. This class will have to be modified as the standard becomes available again or by user request.
 * @author  Wildrich Fourie, I. Lavangee
 */

public class TBXCompliance
{			
	// TODO : How to make this more generic in order to be able to export new fields added to the TMS??	
	// Else document that this class have to be modified when adding a new field
	// OR attempt to find the new field in the List of accepted TBX fields if it could not be found generate custom XCS.
	// For this the database have to be changed to include XCS type when creating a Field
	// XXX: Add the audit trail info to the TBX and XCS
	
	// To simplify implementation a new XCS was created to define data categories that does not exist and
	// to modify some of the categories to work in the TBX as they would in the TMS.
	// Some of these categories could be implemented using the standard DTD and XCS specification,
	// categories like fullForm, plural, etc. It was not implemented this way due to time constraints.
	
	/**
	 * Example of standard use of termType (abbreviation, fullForm, plural)
	 * Taken from TBX-specification 2008 
	 * <langSet xml:lang="de">
	 *   <tig> 
	 *     <term>Proportionalglied plus Integrierglied</term>
	 *   </tig> 
	 *   <tig> 
	 *     <term>PI-Glied</term>
	 *     <termNote type="termType">abbreviation</termNote>
	 *   </tig>
	 * </langSet>
	 */
	
	public static String XCS_Name = "AutshumatoTMSTBXXCSV01.XCS"; 

	// Holds the relation between the NLS specified fields and the TBX standard (or custom) fields.
	private static HashMap<String, String> fieldsList;
	static
	{
		fieldsList = new HashMap<String, String>();
		
		// Record Attributes
		fieldsList.put("Termbase", "<admin type=\"originatingDatabase\"></admin>");
		fieldsList.put("Admin", "<admin type=\"adminNote\"></admin>");
		fieldsList.put("Subject", "<descrip type=\"subjectField\"></descrip>");
		fieldsList.put("Note to manager", "<admin type=\"noteToManager\"</admin>");
		fieldsList.put("Record editing note", "<admin type=\"recordEditingNote\"</admin>");
		fieldsList.put("Keyword", "<admin type=\"keyword\"></admin>");
		fieldsList.put("Project", "<admin type=\"projectSubset\"></admin>");
		fieldsList.put("Publication", "<admin type=\"publication\"></admin>");
		fieldsList.put("Client/Dept", "<admin type=\"customerSubset\"></admin>");
		
		// Index Fields
		fieldsList.put("English", "<langSet xml:lang=\"eng\"></langSet>");
		fieldsList.put("International Scientific Term", "<langSet xml:lang=\"mis\"></langSet>");
		fieldsList.put("Afrikaans", "<langSet xml:lang=\"afr\"></langSet>");
		fieldsList.put("IsiZulu", "<langSet xml:lang=\"zul\"></langSet>");
		fieldsList.put("IsiXhosa", "<langSet xml:lang=\"xho\"></langSet>");
		fieldsList.put("Siswati", "<langSet xml:lang=\"ssw\"></langSet>");
		fieldsList.put("IsiNdebele", "<langSet xml:lang=\"nbl\"></langSet>");
		fieldsList.put("Setswana", "<langSet xml:lang=\"tsn\"></langSet>");
		fieldsList.put("Sepedi", "<langSet xml:lang=\"nso\"></langSet>");
		fieldsList.put("Sesotho", "<langSet xml:lang=\"sot\"></langSet>");
		fieldsList.put("Tshivenda", "<langSet xml:lang=\"ven\"></langSet>");
		fieldsList.put("Xitsonga", "<langSet xml:lang=\"tso\"></langSet>");
		
		// Attribute Fields
		fieldsList.put("Part of speech", "<termNote type=\"partOfSpeech\"></termNote>");
		fieldsList.put("Category", "<termNote type=\"termType\"></termNote>");
		fieldsList.put("Geographical usage", "<termNote type=\"geographicalUsage\"></termNote>");
		fieldsList.put("Origin", "<admin type=\"conceptOrigin\"></admin>");
		fieldsList.put("Term acceptability", "<termNote type=\"termAcceptability></termNote>");
		fieldsList.put("Register", "<termNote type=\"register\"></termNote>");
		fieldsList.put("Term status", "<admin type=\"termStatus\"></admin>"); 
		fieldsList.put("Formula", "<termNote type=\"formula\"></termNote>");
		fieldsList.put("Note", "<termNote type=\"note\"></termNote>");
		fieldsList.put("Plural", "<termNote type=\"plural\"></termNote>");
		fieldsList.put("Full form", "<termNote type=\"fullForm\"></termNote>");
		fieldsList.put("Research note", "<termNote type=\"researchNote\"></termNote>");
		fieldsList.put("Source Publication", "<admin type=\"source\"></admin>");
		fieldsList.put("TOT source", "<admin type=\"totSource\"></admin>");
		fieldsList.put("Context", "<descrip type=\"context\"></descrip>");
		fieldsList.put("Definition", "<descrip type=\"definition\"></descrip>");
		fieldsList.put("Source-definition", "<descrip type=\"sourceDefinition\"></descrip>");
		fieldsList.put("Example sentence", "<descrip type=\"example\"></descrip>");
		fieldsList.put("Text", "<descrip type=\"text\"></descrip>");
		fieldsList.put("Collocation", "<termNote type=\"collocation\"></termNote>");
		fieldsList.put("Time label", "<termNote type=\"timeLabel\"></termNote>");
		fieldsList.put("Editing note", "<termNote type=\"editingNote\"></termNote>");
		fieldsList.put("Image", "<xref type=\"xGraphic\" target=\"\"></xref>");
		fieldsList.put("Sound", "<xref type=\"xAudio\" target=\"\"></xref>");
	}
		
	// Creates a valid TBX document, contained in a string, based on the records received.
	public static String createTBXString(Connection connection, HttpSession session, String authToken, ArrayList<Record> records, String XCSpath) throws Exception
	{				
		String returnString = "";
		
		String header = "<?xml version='1.0' encoding=\"UTF-8\"?>\n" +
				"<!DOCTYPE martif SYSTEM \"TBXcoreStructV02.dtd\">\n" +
				"<martif type=\"TBX\" xml:lang=\"eng\">\n" +
				" <martifHeader>\n" +
				"  <fileDesc>\n" +
				"   <titleStmt>\n" +
				"    <title>Autshumato TMS TBX export</title>\n" +
				"   </titleStmt>\n" +
				"  </fileDesc>\n\n" +
				"  <encodingDesc>\n" +
				"   <p type=\"XCSURI\">" + XCSpath + "</p>\n" +
				"  </encodingDesc>\n" +
				" </martifHeader>\n";
		
		String bodyStart = " <text>\n" +
				"  <body>\n";
		String bodyEnd = "  </body>\n" +
				" </text>\n" +
				"</martif>";
		
		returnString += header + bodyStart;
					
		for(Record r : records)
		{		
			// Just to update this users time
			AccessControlManager.getSignedOnUser(session, authToken);	
			
			String recordString = "";
			if(r != null)
			{
				recordString += "\t<termEntry id=\"RID" + r.getRecordId() + "\">\n";
				// Record Attributes
				// - Add the Termbase					
				String termBaseName = "" + r.getTermdbId();
				termBaseName = TermBaseManager.getTermBaseByTermBaseId(connection, r.getTermdbId(), false).getTermdbname();

				recordString += "\t\t" + fieldsList.get("Termbase").replace("><", ">" + termBaseName + "<") + "\n";
				// - Add the Project(s)
				ArrayList<Project> topics = r.getProjects();
				if(topics != null && topics.size() > 0)
				{
					for(Project tp : topics)
					{
						recordString += "\t\t" + fieldsList.get("Project").replace("><", ">" + tp.getProjectName() + "<") + "\n";
					}
				}
				
				ArrayList<TerminlogyObject> ras = r.getRecordAttributes();
				if(ras != null && ras.size() > 0)
				{
					for(TerminlogyObject ra : ras)
					{
						String raString = "";
						if(fieldsList.containsKey(ra.getFieldName()))
						{
							raString = "\t\t" + fieldsList.get(ra.getFieldName()).replace("><", ">" + ra.getCharData() +"<") + "\n";
							recordString += raString;
						}
					}
				}
				
				// Index Fields
				ArrayList<TerminlogyObject> terms = r.getTerms();
				if(terms != null && terms.size() > 0)
				{
					for(TerminlogyObject t : terms)
					{
						String termString = "";
						String synString = "";
						if(fieldsList.containsKey(t.getFieldName()))
						{
							String tS = "\t\t\t<tig>\n" +
									"\t\t\t\t<term id=\"TID" + t.getResourceId() + "\">" + t.getCharData() + "</term>\n";
							
							// Attribute Fields
							String tasString = "";
							ArrayList<ChildTerminologyObject> tas = ((Term)t).getTermAttributes();
							if(tas != null && tas.size() > 0)
							{
								for(ChildTerminologyObject ta : tas)
								{
									// Add the synonyms to the synString to be added after the <tig></tig> entry.
									if(ta.getFieldName().equals("Synonym"))
									{
										synString += "\n\t\t\t<tig>\n" +
												"\t\t\t\t<term>" + ta.getCharData() + "</term>\n" +
												"\t\t\t</tig>";
										continue;
									}
										
									String taString = "";
									if(fieldsList.containsKey(ta.getFieldName()))
									{
										// Extra bit of info required for these types
										if(ta.getFieldName().equals("Image") || ta.getFieldName().equals("Sound"))
										{
											taString += "\t\t\t\t" + fieldsList.get(ta.getFieldName()).replace("\"\"", "\"" + ta.getCharData() + "\"") + 
											fieldsList.get(ta.getFieldName()).replace("><", ">" + ta.getCharData() + "<") + "\n";
											tasString += taString;
											continue;
										}
											
										
										taString += "\t\t\t\t" + fieldsList.get(ta.getFieldName()).replace("><", ">" + ta.getCharData() + "<") + "\n";
										tasString += taString;
									}
								}
							}
							
							// Have to insert the encapsulated data in between >< for the string below 
							termString = "\t\t" + fieldsList.get(t.getFieldName()).replace("><", ">\n" + tS + tasString + "\t\t\t</tig>" + synString + "\n\t\t<") + "\n";
							
							recordString += termString;
						}
					}
				}
				recordString += "\t</termEntry>\n";
			}
			returnString += recordString;		
		}
		returnString += bodyEnd;
		
		return returnString;
	}
}