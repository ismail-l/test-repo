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

package tms2.client.i18n;

import com.google.gwt.i18n.client.Constants;

public interface TMSConstants extends Constants 
{
	@DefaultStringValue("TMS")
	// General
	String appName();
	String title();
	String titleAdmin();
	String welcome();
	String link();	
	String contact();
	String help();
	
	String helpPath();
	String helpHeader();
	String helpFeatures();
	
	// - Disclaimer
	String disclaimer();
	String disclaimer2();
	String terms_and_conditions();
	String companyName();
	String countryName();
	String jurisdictionName();
	
	// -------- CLIENT -----------
	// TerminologyBrowser 
	
	//InfoBar
	String filter_label();
	String num_recs_label();
	String no_record();
	String no_record_email();
	
	// ControlBar
	String no_records();
	String cannot_add_record();
	String cannot_retrieve_generic_im();
	String cb_error_no_term_db();
	String cb_error_no_wrapper();
	String cb_error_no_im_assigned();
	String cb_error_no_project_assigned();
	String cb_assign_project_record();
	String cb_no_assigned_im();
	String cb_generic_im_changed();
	String cb_indexfields_changed();
	String cb_indexfields_changed_guest();
		
	// EmailForm
	String email_record_id();
	String email_index_fields();
	String email_name();
	String email_tel();
	String email_email();
	String email_inst();
	String email_expert();
	String email_comments();
	String email_send();
	String email_close();
	String email_reload();
	
	// - Links
	String link_admin();
	String link_help();
	String link_signOff();
	
	// SearchDisplayPanel
	// - Search Types
	String search_default();
	String search_fuzzy();
	String search_exactmatch();
	String search_wildcard();
	String search_prompt();
	// - Button
	String search_button();
	// - ExpanderPanel
	String search_results();
	String search_noResults();
	
	// RecordRendering
	String recordRender_recordId();
	String recordRender_noIM();
	String recordRender_noEntries();
	String recordRender_emptyRecord();
	String recordRender_auditTrailTitle();
	// - Sound render
	String recordRender_sound_wait();
	String recordRender_sound_stop();
	String recordRender_sound_play();
	String recordRender_sound_play_notwork();
	String recordRender_sound_unavailable();
	// - Project panel
	String recordRender_project_available();
	String recordRender_project_selected();
	String recordRender_project_removeDefault();
	String recordRender_project_selectProject();
	String recordRender_project_limit();
	String recordRender_project_remove_unassigned();
	String recordRender_project_remove_im_no_match();
	String recordRender_project_remove_selected();
	
	// RecordEditor
	String recordEdit_selectedDB();
	String recordEdit_selectedProject();
	String recordEdit_selectedIM();
	String recordEdit_error_noProjects();
	String recordEdit_error_removeControl();	
	String recordEdit_sortindex_no_rights();
	String recordEdit_customsortindex_no_rights();
	String recordEdit_sortindex_no_rights_exit();
	String recordEdit_project_no_rights();
	String recordEdit_project_no_rights_exit();
	String recordEdit_project_no_rights_gen_IM();	
	String recordEdit_project_limit();
	String recordEdit_no_fields();
	String recordEdit_no_fields_exit();
	
	// - For Create
	String recordEdit_create_info();
	String recordEdit_terminology_objects();
	// - loadData
	
	String recordEdit_selectDB();
	String recordEdit_selectProject();
	String recordEdit_selectIM();
	String recordEdit_error_noIM();
	// - For Edit
	String recordEdit_error_cannotEdit();
	String recordEdit_error_nullTermObj();
	
	String recordEdit_change_browse();
	
	// General control strings
	String controls_title();
	String controls_insert_record_attrb();
	String controls_save();
	String controls_cancel();
	String controls_insert();
	String controls_insert_error();
	String controls_upload();
	String controls_reset();
	String controls_delete();
	String controls_new();
	String controls_loading();

	String controls_alert();
	String controls_confirm();
	String controls_ok();
	String controls_error();
	String controls_info();
	String controls_success();
	String controls_close();
	String controls_none();

	
	// - AccessControlDialog
	String controls_ac_error_user();
	String controls_ac_error_uc();
	String controls_ac_ucUpdateFail();
	String controls_ac_uUpdateFail();
	String controls_ac_ucInfo();
	String controls_ac_read();
	String controls_ac_update();
	String controls_ac_Delete();
	String controls_ac_Export();
	String controls_ac_error_globalUc();
	String controls_ac_ucr();
	String controls_ac_ucInfo2();
	String controls_ac_uInfo();
	String controls_ac_errorUsers();
	String controls_ac_urSelect();
	String controls_ac_ur();
	
	String controls_lbl_available();
	String controls_lbl_assigned();
	
	String controls_ac_deleterights_project_msg();
	String controls_ac_updaterights_project_msg();
	
	// - DragMathDialog
	String controls_dm_title();
	String controls_dm_error_install();
	
	// - ExtendedDatePicker
	String controls_edp_pickDate();
	// - ExtendedPasswordBox
	String controls_epb_valid();
	// - FieldsPalette
	String controls_fp_topimf();
	String controls_fp_add_field();
	String controls_fp_field();
	String controls_fp_fields();
	String controls_fp_noFields();
	// - UploadDialog
	String controls_ud_maxupload();
	// ExtendedSingleUploader
	String controls_btn_send();
	// - ExportDialog
	String export_openFilter();
	String export_download();
	String export_exportAs();
	String export_tab();
	String export_odt();
	String export_tbx();
	String export_tbx_message();
	String export_applyFilter();
	String export_filename();
	String export_exportDialog();
	String export_noFilTitle();
	String export_noFilMes();
	String export_noDataTitle();
	String export_noDataMes();
	String export_noFilenameTitle();
	String export_noFilenameMes();
	String export_include_field_names();
	
	// - ExportPanel
	String export_source();
	String export_target();
	String export_comment();
	String export_switch();
	String export_exportOn();
	String export_selectIm();
	String export_selectRa();
	String export_selectSource();
	String export_selectSourceAtrb();
	String export_selectTargets();
	String export_record_fields();
	String export_index_fields();
	String export_index_attrib_fields();
	String export_index_attrib_subfield();
	String export_btn_copy();
	String export_txt_name();
	String export_txt_value();
	String export_index_field_found();
	String export_index_field_found_mes();
	String export_duplicate_field();
	String export_duplicate_field_mes();
	String export_field_template();
	String export_im_no();
	String export_fields_no();
	String export_activate_deactive();
	String export_synonym_subfields();
	String export_validate();
	
	// - BusyDialog
	String busy_dialog_busy();
	
	// Filter
	// - AndOrCheckboxesPanel
	String filter_and();
	String filter_or();
	// - CreatedOrChangedCheckboxesPanel
	String filter_created();
	String filter_changed();
	String filter_createdOrChanged();
	// - FilterDatesPanel
	String filter_selDates();
	String filter_fromDate();
	String filter_from();
	String filter_error_toDate();
	String filter_toDate();
	String filter_to();
	String filter_error_fromDate();
	String filter_datePicker();
	String filter_selectedDateNone();
	String filter_selectedDate();
	// - FilterDialog
	String filter_filterDialog();
	String filter_setFilter();
	String filter_clear();
	// - FilterFields
	String filter_addMoreFields();
	String filter_selectFields();
	String filter_error_fields();
	String filter_contains();
	String files_doesNotContain();
	String filter_exclusive();
	String filter_project_id();
	String filter_project_name();
	String filter_selectField();
	String filter_fieldText();
	String filter_value();
	// - FilterTermbasePanel
	String filter_selectTermbase();
	String filter_error_termbase();
	// - FilterTopicsPanel
	String filter_addMoreProjects();
	String filter_selectProjects();
	// - FilterUserPanel
	String filter_error_users();
	String filter_selectUserDates();
	String filter_selectUser2();
	
	String filter_add_project();
	String filter_remove();
	String filter_project_exclusive();
	String filter_project_filters();
	String filter_user_date();
	String filter_fields();
	String filter_field_active();
	String filter_fields_prompt();
	
	// - FilterPanel
	String filter_export();
	String filter_openFilter();
	String filter_setNone();
	String filter_set();
	String filter_numRecords();
	String filter_error_unableToSet();
	String filter_error_unableClear();
	
	
	// RecordBrowsePanel
	String recordBrowse_label();
	String recordBrowse_search();
	String recordBrowse_emailadmin();
	String recordBrowse_createRecord();
	String recordBrowse_editRecord();
	String recordBrowse_deleteRecord();
	String recordBrowse_confirmDelete();
	String recordBrowse_error_recordCache();
	String record_edit();
	String record_delete();
	String record_filter();
	String recordBrowse_error_noFirstRecord();
	String recordBrowse_source();
	String recordBrowse_target();
	String recordBrowse_error_noPreviousRecord();
	String recordBrowse_error_noNextRecord();
	String recordBrowse_error_noLastRecord();
	String recordBrowse_no_record();
	
	// Term search
	String term_no_source();
	
	
	// AdministrationInterface
	String admin_link_termbase();
	String admin_link_help();
	String admin_info();
	String admin_tab_OnlineUsers();
	String admin_tab_unregistered_guest();
	String admin_tab_UserAndCategories();
	String admin_tab_UserCategories();
	String admin_tab_Users();
	String admin_tab_Termbases();
	String admin_tab_Fields();
	String admin_tab_InputmodelWorkbench();
	// - FieldDetailsPanel
	String admin_field_detail_label();
	String admin_field_detail_labelName();
	String admin_field_detail_labelType();
	String admin_field_detail_labelData();
	String admin_field_detail_labelMaxLen();
	String admin_field_detail_labelSortIndex();
	String admin_field_detail_error_sortIndex();
	String admin_field_detail_reset();
	String admin_field_errorSave();
	String admin_field_errorComplete();
	String admin_field_detail_synonym_error();
	// - FieldsPanel
	String admin_field_selectField();
	String admin_field_selectType();
	String admin_field_selectData();
	String admin_field_heading();
	String admin_field_label();
	String admin_field_labelExist();
	String admin_field_newButton();
	String admin_field_error_dataType();
	String admin_field_error_dataTypeEmpty();
	String admin_field_error_fieldId();
	// - Preset Attributes panel
	String admin_field_preset_attribFor();
	String admin_field_preset_existingAttr();
	String admin_field_preset_selectAttr();
	String admin_field_preset_newButton();
	String admin_field_preset_labelValue();
	String admin_field_preset_labelKey();
	String admin_field_preset_select();
	String admin_field_preset_exceed();
	String admin_field_validate();

	// Access Rights
	String admin_access_rights_label();
	String admin_access_rights_heading();
	String admin_access_rights_second_heading();
	String admin_access_rights_user();
	String admin_access_rights_user_cat();
	String admin_access_rights_users();
	String admin_access_rights_user_category();
	String admin_access_rights_prompt();
	String admin_access_rights_project_prompt();
	String admin_access_rights_dg_fieldid();
	String admin_access_rights_dg_fieldname();
	String admin_access_rights_dg_fieldtype();
	String admin_access_rights_dg_read();
	String admin_access_rights_dg_update();
	String admin_access_rights_dg_export();
	String admin_access_rights_dg_delete();
	String admin_access_rights_mark_read();
	String admin_access_rights_mark_update();
	String admin_access_rights_mark_export();
	String admin_access_rights_mark_delete();
	String admin_access_rights_save();
	String server_error_acu();
	String admin_access_rights_update();
	
	//  ~ FieldPanel
	//  ~ FieldsPalette
	String admin_im_fieldsPaletteLabel();
	String admin_im_errorNoRecordFields();
	String admin_im_errorNoIndexFields();
	String admin_im_errorNoAttributeFields();
	String admin_im_errorNoSubAttributeFields();
	String admin_im_labelRecordFields();
	String admin_im_labelIndexFields();
	String admin_im_labelAttributeFields();
	String admin_im_labelPresetAttributeFields();
	String admin_im_labelSubAttributeFields();
	String admin_im_labelPresetSubAttributeFields();
	//  ~ ModellingPanel
	String admin_im_errorSubFields();
	String admin_im_errorSubFieldNotSelected();
	
	//  ~ PropertiesPanel
	String admin_im_fieldProperties();
	String admin_im_accessControl();
	String admin_im_defaultValue();
	String admin_im_noDefaultValue();
	String admin_im_minOccurrence();
	String admin_im_maxOccurrence();
	String admin_im_error_maxFieldLenght();
	String admin_im_isparent_subfield();
	String admin_im_mandatory();
	String admin_im_inuse();
	
	// - Termbases
	//  ~ DatabaseDetailsPanel
	String admin_termbase_termbaseName();
	String admin_termbase_email();
	String admin_termbase_dateTimeCreated();
	String admin_termbase_lastUpdated();
	String admin_termbase_owner();
	//  ~ ProjectDetailsPanel
	String admin_termbase_projectName();
	String admin_project_validate();
	//  ~ TermbasesPanel
	String admin_termbase_newProject();
	String admin_termbase_error_noneFound();
	String admin_termbase_info();
	String admin_termbase_termbases();
	String admin_termbase_termbaseInfo();
	String admin_termbase_newTermbase();
	String admin_termbase_projectInfo();
	String admin_termbase_validate();
	
	// - OnlineUserPanel
	String admin_online_label();
	String admin_online_info();
	String admin_online_heading();
	String admin_online_error_load();
	String admin_online_error_noUserSelected();
	String admin_online_confirm();
	// - UserCategoryPanel
	String admin_cat_heading();
	String admin_cat_label();
	String admin_cat_info();
	String admin_cat_newCategory();
	String admin_cat_categoryName();
	String admin_cat_isAdmin();
	String admin_cat_selectCat();
	String admin_cat_validate();
	// - UserPanel
	String admin_user_category();
	String admin_user_label();
	String admin_cat_label2();
	String admin_user_info();
	String admin_user_newUser();
	String admin_user_firstName();
	String admin_user_lastName();
	String admin_user_username();
	String admin_user_password();
	String admin_user_activated();
	String admin_user_expireDate();
	String admin_user_lastSignOn();
	String admin_user_passwordDesc();
	String admin_user_selectUser();
	String admin_user_expireDate2();
	String admin_user_validate();
	
	// SignOnControl
	String signOn_signOn();
	String signOn_username();
	String signOn_password();
	String signOn_signOff();
	String signOn_sessionExpire();
	String signOn_signout();
	String signOn_null_properites();

	
	
	// Generally used
	String fault_minor();
	String fault_major();

	
	// - Alert
	String alert_admin_access();
		
	//--------------------------------------------------
	
	//---- SERVER -------------------------------------
	String server_error_signedOn();
	String server_error_admin();
	String server_error_passIncorrect();
	String server_error_userIncorrect();
	String server_error_userSessionNoMatch();
	
	// AccessControl
	// - AccessControlledRemoteService
	String server_ac_error_session();
	
	// FieldRetrievalServiceImpl	
	String server_mandatory_subfields_correspond();
	String server_mandatory_subfields_dont_correspond();
	String server_mandatory_fields_correspond();
	String server_mandatory_fields_dont_correspond();
	
	// AppConfigServiceImpl
	String db_host_null();
	String db_port_null();
	String db_user_null();
	String db_pass_null();
	String project_field_null();
	String sort_index_field_null();
	String synonym_field_null();	
	String context_field_null();
	String definition_field_null();
	String note_field_null();
	String synonym_context_null();
	String synonym_note_null();
	String specified_fields_error();
	String app_properties();
	String app_properties_error();
	String project_and_sort_index_field_mandatory();
	String synonym_mandatory_not_set();
	//--------------------------------------------------
	
	// Log Messages
	// DatabaseRetrievalServiceImpl
	String log_db_retrieve();
	// ExportServiceImpl
	String log_export_user();
	String log_export_exportdata();
	String log_export_output_file();
	String log_export_output_text();
	String log_export_outputdoc();
	String log_export_tbx();
	String log_export_tab();
	String log_export_odt_data();
	String log_export_odt();
	// ExportTypesServiceImpl
	String log_export_types();
	String log_export_ims();
	String log_export_master_im();
	String log_export_fields();
	// FieldDataTypeRetrievalServiceImpl
	String log_field_datatypes();
	String log_mimetypes();
	// FieldRetrievalServiceImpl
	String log_fields();
	String log_exportfields();
	// FieldTypeRetrievalServiceImpl
	String log_fieldtypes();
	// InputModelRetrievalServiceImpl
	String log_ims();
	String log_master_im();
	// InputModelTopicsManager
	String log_im_topics();
	// RecordRetrievalServiceImpl
	String lob_retrieve_first_record();
	String log_retrieve_last_record();
	String log_retrieve_next_record();
	String log_retrieve_previous_record();
	String log_num_recs();
	String log_lookup_rec();
	String log_first_rec();
	// RecordStateManagementServiceImpl
	String log_unlock_all_recs();
	// TermSearchServiceImpl
	String log_search();
	String log_fuzzy_search();
	String log_wildcard_search();
	String log_exact_search();
	String log_closest_neighbour();
	String log_find_duplicates();
	String log_convert_record_elements();
	// SynonymServiceImpl
	String log_synonym_fuzzy();
	String log_synonym_wildcard();
	String log_synonym_duplicates();
	String log_synonym_matching();
	String log_fetch_synonym_entries();
	String log_synonym_fieldid();
	String log_source_index_id();
	// TopicRetrievalServiceImpl
	String log_topic_alltopics();
	// UserCategoriesServiceImpl
	String log_all_user_cat();
	String log_all_user_cat_users();	
	// UserInputModelManager
	String log_manage_user_im();
	String log_all_user_ims();
	
	
	String log_record_edit_details();
	
	String log_filter();
	
	String log_export();
	
	// UsersManager
	String log_update_signon();
	String server_user_field_fail();
	String server_user_cat_field_fail();
	
	//------------------------------------------------
	
	// Newly added
	// Audit trail event types
	String event_type_create();
	String event_type_update();
	String event_type_delete();
	String event_type_export();
	String event_type_unknown();
	
	// Connection pool
	String conn_pool_init();
	String conn_pool_success();
	String conn_pool_fail();
	String conn_pool_shut_success();
	String conn_pool_shut_fail();
	
	// Database connector
	String conn_error();
}
