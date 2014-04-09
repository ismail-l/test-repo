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

import com.google.gwt.i18n.client.Messages;

public interface TMSMessages extends Messages
{
	//-------------- CLIENT ---------------------------------------------
	// General
	// - Terms and Conditions
	String general_termsAndConditions(String companyName, String country, String jurisdictionName);
	String general_disclaimer(String companyName);
	
	// SearchDisplayPanel
	// - Search Results
	String search_result(String searchType, String searchExpression, String resultsSize);
	String search_no_result(String searchType, String searchExpression);
	// - Init Search fault
	String search_init_fault(String numCharacters);
	
	// RecordEditorPanel
	String recordEdit_error_removeIndex(String inputFieldName);
	String recordEdit_error_validValue(String fieldName);
	String recordEdit_error_preset(String fieldName);
	String recordEdit_duplicate(String chardata);
	String recordEdit_error_source_value(String fieldName, String value);
	
	// RecordBrowsePanel
	String recordBrowse_search_noMatch(String value);
	String recordBrowser_search_error(String value);
	String recordBrowse_search_noAlpha(String value);
	String recordBrowse_search_recordNull(String value);
	String recordBrowse_lockedForEdit(String userFullName);
	String recordBrowse_noRights(String recordid);
	
	// SignOnControl
	String signOn_error_validUsername(String min, String max);
	String signOn_error_validPassword(String min, String max);
	String signOn_signedOn(String userFullName);
	
	// AdministrationInterface
	// - InputModelWorkBench
	//  ~ AssemblyArea
	String admin_im_limitRecordFields(String max);
	String admin_im_limitIndexFields(String max);
	String admin_im_limitAttributeFields(String max);
	String admin_im_containsField(String fieldName);
	String admin_im_not_parent_subfield(String fieldname);
	// - OnlineUserPanel
	String admin_online_signOffSuccess(String username);
	String admin_online_signOffFail(String username);
	// - UserCategoryPanel
	String admin_cat_updateSuccess(String userCategoryName);
	String admin_cat_createSuccess(String userCategoryName);
	// - UserPanel
	String admin_user_exist(String username);
	String admin_user_recommend(String username);
	String admin_user_updateSuccess(String fullName);
	String admin_user_createSuccess(String fullName);
	

	// AccessControlDialog
	String controls_accessControl(String fieldName, String inputmodelName);
	String controls_ac_global(String inputModelName);
	String controls_ac_updateSuccess(String fieldName);
	String controls_ac_globalUpdateSuccess(String inputModelName);
	String controls_ac_ucLabel(String fieldName);
	String controls_ac_uLabel(String fieldName);
	
	// InputModelTopicsDialog
	String controls_im_topic_title(String inputmodelName);
	String controls_im_no_users_linked(String inputmodelName);
	
	// EditableControl
	String controls_ec_remove(String fieldName);
	String controls_ec_insert(String fieldName);
	// ExtendedDatePicker
	String controls_edp_dateAfter(String date);
	String controls_edp_dateBefore(String date);
	// - ExtendedPasswordBox
	String controls_epb_invalid(String name);
	// - FieldsPalette
	String controls_fp_imidnull(String inputmodelId);
	// - ExportDialog
	String export_generate(String exportType);
	// - ExportPanel
	String export_selectTargetAtrb(String fieldName);
	String export_no_rights1(String export);
	String export_no_rights2(String export);
	
	// Alert Messages
	String alert_search_noResults(String value);
	String alert_search_noAlpha(String value);
	String alert_recordRetriever_recordIsNull(String value);
	String alert_editRecord_locked(String value);
	
	String alert_recordBrowse_NoFirstRecordForProject(String topicName);
	String alert_recordBrowse_NoInputModelForTopic(String topicName);
	
	String alert_recordBrowse_NoPrevious();
	String alert_recordBrowse_NoPreviousForTopic(String topicName);
	
	String alert_recordBrowse_NoNext();
	String alert_recordBrowse_NoNextForTopic(String topicName);
	
	String alert_recordBrowser_NoLast();
	String alert_recordBrowser_NoLastForTopic(String topicName);
	
	String alert_recordEditor_NoTermbases();
	
	// Controls (Edit & Create
	String alert_controls_NoField();
	String alert_controls_RunApplet(String appletName);
	
	// Record Editor
	String alert_recordEditor_NoInputmodels();
	String alert_recordEditor_NoTopicsForDb();
	String recordEditor_EditError();
	String recordEditor_EmptyRecord();
	String recordEditor_EntryNotValid(String fieldName);
	String recordEditor_RecordAddSuccess();
	
	// Record Rendering
	String recordRender_EmptyRecord();
	String recordRender_NullRecord();
	
	// Search Display
	String searchDisplay_results(String searchExpression);
	String searchDisplay_noResults(String searchExpression);
	String searchDisplay_searchLength();
	
	// Admin
	String admin_onlineUsers_logOutSuccess(String username);
	String admin_onlineUsers_logOutFailure(String username);
	
	String admin_userCategories_updateSuccess(String userCategoryName);
	String admin_userCategories_createSuccess(String userCategoryName);
	
	String admin_user_UsernameExists(String username);
	String admin_user_RecommendUsername(String username, String fullname);
	String admin_user_UserUpdated(String username);
	String admin_user_UserCreated(String username);
	
	String admin_field_preset_heading(String fieldname);
	
	//-----------------------------------------------------------------------
	
	//------------- SERVER -----------------------------------------------
	String server_accountRenew(String username);
	String server_accountActivate(String username);
	String server_error_acuc(String fieldname);
	String server_error_acu(String fieldname);
	String server_error_audittrailSave(String eventTypeName, String resourceId, String resourceType);
	String server_error_audittrailLoad(String eventId);
	String server_error_audittrailLoadRecord(String recordId);
	String server_termbase_exist(String termbaseName);
	String server_termbase_create_success(String termbaseName, String owner);
	String server_termbase_create_fail(String empty);
	String server_termbase_update_success(String termbaseName);
	String server_termbase_update_fail(String empty);
	// - Fields
	String server_fields_fieldNotFound(String fieldname);
	String server_fields_exist(String fieldname);
	String server_fields_notExist(String fieldname);
	String server_field_error_exist(String fieldname);
	String server_fields_index_notUsed(String fieldname);
	String server_fields_index_used(String fieldname);
	String server_fields_ra_notUsed(String fieldname);
	String server_fields_ra_used(String fieldname);
	String server_fields_af_notUsed(String fieldname);
	String server_fields_af_used(String fieldname);
	String server_fields_error_used(String fieldname);
	String server_fields_error_usedMore(String empty);
	
	String server_fields_create_success(String fieldname);
	String server_fields_create_fail(String empty);
	String server_fields_update_success(String fieldname);
	String server_fields_update_fail(String empty);
	String server_fields_update_failExist(String fieldname);
	String server_fields_update_use(String fieldname);
	// - InputModel
	String server_im_exist(String name);
	String server_im_notExist(String name);
	String server_im_create_success(String inputmodelName);
	String server_im_create_fail(String inputmodelName);
	String server_im_update_success(String inputmodelName);
	String server_im_update_fail(String inputmodelName);
	String server_im_id(String inputmodelName);
	String server_im_id_not(String inputmodelName);
	// - PresetAttribute
	String server_pa_create_success(String attributeValue);
	String server_pa_create_fail(String empty);
	String server_pa_update_success(String attributeValue);
	String server_pa_update_fail(String empty);
	// - Record
	String server_record_save_success(String empty);
	String server_record_save_fail(String empty);
	String server_record_retrieve_fail(String empty);
	String server_record_noRecords(String empty);
	String server_record_noRecordId(String fieldId);
	String server_record_update_success(String empty);
	String server_record_update_fail(String empty);
	String server_record_update_retrieve(String empty);
	String server_record_delete_success(String empty);
	String server_record_delete_fail(String empty);
	String server_record_delete_retrieve(String empty);
	// - Term
	String server_term_create_fail(String chardata);
	String server_term_exists(String chardata);
	String server_term_update_fail(String chardata);
	String server_term_delete_fail(String chardata);
	// - Project
	String server_proj_exist(String projectname);
	String server_proj_notExist(String projectname);
	String server_proj_create_success(String projectname);
	String server_proj_create_fail(String empty);
	String server_proj_update_success(String projectname);
	String server_proj_update_fail(String empty);
	String server_proj_im_link_fail(String inputmodelName);
	// - Upload
	String controls_ud_warning(String companyName);
	String server_upload_error_user(String empty);
	String server_upload_error_recordid(String empty);
	String server_upload_error_dimensions(String empty);
	String server_upload_error_formfield(String empty);
	String server_upload_error_dir(String empty);
	String server_upload_error_file(String empty);
	String server_upload_error_file_size(String empty);
	String server_upload_error(String message);
	String server_upload_error_value(String empty);
	String server_upload_error_session(String empty);
	// - UserCategory
	String server_uc_create(String userCategoryName);
	String server_uc_update(String userCategoryName);
	// - User
	String server_user_exist(String username);
	String server_user_error_alpha(String username);
	String server_user_minlength(String username, String minLength);
	String server_user_maxlength(String username, String maxLength);
	String server_pass_alpha(String empty);
	String server_pass_minlength(String minLength);
	String server_pass_maxlength(String maxLength);
	String server_user_create_fail(String fullname, String message);
	String server_user_retrieve(String message);
	String server_user_update_fail(String fullname, String message);
	String server_user_generate_accept(String username, String fullname);
	String server_user_generate_fail(String message);
	String server_user_verify(String username, String message);
	
	
	
	// - AccessControl
	String server_ac_noUser(String empty);
	//----------------------------------------------------------------------
	
	//------------ LOG MESSAGES --------------------------------------------
	// These messages are for the Server side log.
	// - Session
	String log_session_nullSession(String empty);
	String log_session_timerInterrupt(String empty);
	String log_session_expireUserThread(String empty);
	String log_session_unlockRecords(String empty);
	String log_session_userMismatch(String empty);
	String log_session_adminMismatch(String empty);
	String log_session_userIsAdmin(String empty);
	String log_session_signOn(String empty);
	String log_session_signOff(String empty);
	// - Database
	String log_db_connect(String empty);	
	String log_db_rollback(String empty);
	String log_db_close(String empty);	
	String log_db_pool_close(String empty);
	String log_db_connector(String empty);
	// - AccessRights
	String log_ar_ucarRetrieve(String fieldname);
	String log_ar_uarRetrieve(String fieldname);
	// DatabaseRetrievalServiceImpl
	String log_db_retrieve_email(String termbase_id);
	String log_db_retrieve_termbase(String termbase_id);
	// ExportServiceImpl
	String log_export_error(String export_type);
	// FieldRetrievalServiceImpl
	String log_fields_fieldid(String fieldid);
	String log_fields_subfieldid(String fieldid);
	// InputModelRetrievalServiceImpl	
	String log_im_check_name(String im_name);
	String log_im_check_name_id(String im_id);
	String log_im_retrieve_im(String im_id);
	// InputModelTopicsManager
	String log_imtopics_manage(String imname);
	// RecordRetrievalServiceImpl
	String log_reset_browsing(String fieldid);
	String log_reset_browsing_2(String fieldid);
	// RecordStateManagementServiceImpl
	String log_state_edited(String recordid);
	String log_unlock_rec(String recordid);
	String log_set_rec_edited(String recordid);
	// TopicRetrievalServiceImpl
	String log_topic_rec(String recordid);
	String log_retrieve_topic(String topicid);
	// UserCategoriesManager
	String log_delete_user_cat(String usercat);
	String log_user_cat_is_admin(String usercat_id);
	// TermRetriever
	String log_retrieve_all_terms(String recordid);
	String log_retrieve_term(String termid);
	// InputFieldRetriever
	String log_retrieve_imif(String if_id);
	String log_retrieve_imsif(String if_id);
	String log_retrieve_imssif(String if_id);
	// AuditTrailManager
	String log_audittrail(String event_name, String resourceid, String resourcetype);
	String log_audit_retrieve(String eventid);
	String log_audit_retrieve_rec(String recordid);
	//----------------------------------------------------------------------
}
