--
-- Autshumato Terminology Management System (TMS)
--  Free web application for the management of multilingual terminology databases (termbanks). 
--
--  Copyright (C) 2013 Centre for Text Technology (CTexT®), North-West University
--  and Department of Arts and Culture, Government of South Africa
--  Home page: http://www.nwu.co.za/ctext
--  Project page: http://autshumatotms.sourceforge.net
--   
--  Licensed under the Apache License, Version 2.0 (the "License");
--  you may not use this file except in compliance with the License.
--  You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
--  Unless required by applicable law or agreed to in writing, software
--  distributed under the License is distributed on an "AS IS" BASIS,
--  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  See the License for the specific language governing permissions and
--  limitations under the License.
--

-- Create users role group
-- This can be commented out if Postgres already has this ROLE
--CREATE ROLE user_group;

-- Create guest role group
-- This can be commented out if Postgres already has this ROLE
--CREATE ROLE guest_group;

-- Grant database privileges
GRANT ALL PRIVILEGES ON DATABASE tms2 to user_group;
GRANT ALL PRIVILEGES ON DATABASE tms2 to guest_group;

-- Grant scheme privileges
GRANT ALL PRIVILEGES ON SCHEMA tms to user_group;
GRANT ALL PRIVILEGES ON SCHEMA tms to guest_group;

-- Grant sequence privileges
GRANT ALL PRIVILEGES ON SEQUENCE tms."AccessRightsUserCategory_accessrightusercategoryid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AccessRightsUser_accessrightuserid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateRecords_audittrailcreaterecordid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailCreateTerms_audittrailcreatetermid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditRecordAttributes_audittraileditrecattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditRecords_audittraileditrecordid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditSynonyms_audittraileditsynonymid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditTermAttributes_audittrailedittermattrid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."AuditTrailEditTerms_audittrailedittermid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Fields_fieldid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."PresetFields_presetfieldid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Projects_projectid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."RecordAttributes_recordattributeid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."RecordProjects_recordprojectid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Records_recordid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."SynonymAttributes_synonymattributeid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Synonyms_synonymid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."TermAttributes_termattributeid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."TermBases_termbaseid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Terms_termid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."UserCategories_usercategoryid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms."Users_userid_seq" to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms.childaccessrightsuser_childaccessrightuserid_seq to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms.childaccessrightsusercategory_childaccessrightusercategoryi_seq to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms.userprojects_userprojectid_seq to user_group;
GRANT ALL PRIVILEGES ON SEQUENCE tms.usercategoryprojects_usercategoryprojectid_seq to user_group;

-- guest_group will not modify sequences

-- Grant table privileges
GRANT ALL PRIVILEGES ON TABLE tms.accessrightsuser to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.accessrightsusercategory to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailcreaterecords to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailcreatesynonymattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailcreatesynonyms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailcreatetermattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailcreateterms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittraileditrecordattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittraileditrecords to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittraileditsynonymattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittraileditsynonyms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailedittermattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittraileditterms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.audittrailtcreaterecordattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.childaccessrightsuser to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.childaccessrightsusercategory to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.presetfields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.projects to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.recordattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.recordprojects to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.records to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.synonymattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.synonyms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.termattributes to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.termbases to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.terms to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.usercategories to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.usercategoryprojects to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.userprojects to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.users to user_group;

GRANT SELECT ON TABLE tms.accessrightsuser to guest_group;
GRANT SELECT ON TABLE tms.accessrightsusercategory to guest_group;
GRANT SELECT ON TABLE tms.audittrailcreaterecords to guest_group;
GRANT SELECT ON TABLE tms.audittrailcreatesynonymattributes to guest_group;
GRANT SELECT ON TABLE tms.audittrailcreatesynonyms to guest_group;
GRANT SELECT ON TABLE tms.audittrailcreatetermattributes to guest_group;
GRANT SELECT ON TABLE tms.audittrailcreateterms to guest_group;
GRANT SELECT ON TABLE tms.audittraileditrecordattributes to guest_group;
GRANT SELECT ON TABLE tms.audittraileditrecords to guest_group;
GRANT SELECT ON TABLE tms.audittraileditsynonymattributes to guest_group;
GRANT SELECT ON TABLE tms.audittraileditsynonyms to guest_group;
GRANT SELECT ON TABLE tms.audittrailedittermattributes to guest_group;
GRANT SELECT ON TABLE tms.audittraileditterms to guest_group;
GRANT SELECT ON TABLE tms.audittrailtcreaterecordattributes to guest_group;
GRANT SELECT ON TABLE tms.childaccessrightsuser to guest_group;
GRANT SELECT ON TABLE tms.childaccessrightsusercategory to guest_group;
GRANT SELECT ON TABLE tms.fields to guest_group;
GRANT SELECT ON TABLE tms.presetfields to guest_group;
GRANT SELECT ON TABLE tms.projects to guest_group;
GRANT SELECT ON TABLE tms.recordattributes to guest_group;
GRANT SELECT ON TABLE tms.recordprojects to guest_group;
GRANT SELECT ON TABLE tms.records to guest_group;
GRANT SELECT ON TABLE tms.synonymattributes to guest_group;
GRANT SELECT ON TABLE tms.synonyms to guest_group;
GRANT SELECT ON TABLE tms.termattributes to guest_group;
GRANT SELECT ON TABLE tms.termbases to guest_group;
GRANT SELECT ON TABLE tms.terms to guest_group;
GRANT SELECT ON TABLE tms.usercategories to guest_group;
GRANT SELECT ON TABLE tms.usercategoryprojects to user_group;
GRANT SELECT ON TABLE tms.userprojects to user_group;
GRANT SELECT ON TABLE tms.users to guest_group;

-- Grant view privileges
GRANT ALL PRIVILEGES ON TABLE tms.filter_attribute_fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_index_fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_indexfieldaudittrailcreate to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_indexfieldaudittrailedit to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_record_fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_subattribute_fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.filter_synonym_fields to user_group;
GRANT ALL PRIVILEGES ON TABLE tms.synonymsmin to user_group;

GRANT SELECT ON TABLE tms.filter_attribute_fields to guest_group;
GRANT SELECT ON TABLE tms.filter_index_fields to guest_group;
GRANT SELECT ON TABLE tms.filter_indexfieldaudittrailcreate to guest_group;
GRANT SELECT ON TABLE tms.filter_indexfieldaudittrailedit to guest_group;
GRANT SELECT ON TABLE tms.filter_record_fields to guest_group;
GRANT SELECT ON TABLE tms.filter_subattribute_fields to guest_group;
GRANT SELECT ON TABLE tms.filter_synonym_fields to guest_group;
GRANT SELECT ON TABLE tms.synonymsmin to guest_group;

-- Grant function privileges
GRANT ALL PRIVILEGES ON FUNCTION tms.difference(text, text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.dmetaphone(text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.dmetaphone_alt(text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterattribute(integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterattribute_chardata(integer, integer, character varying) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterattribute_or(integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindex(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindex_chardata(integer, character varying) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindex_or(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexbetweendate_create(integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexbetweendate_edit(integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexfromdate_create(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexfromdate_edit(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindextodate_create(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindextodate_edit(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuser_create(integer, integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuser_edit(integer, integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuserbetweendate_create(integer, integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuserbetweendate_edit(integer, integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuserfromdate_create(integer, integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexuserfromdate_edit(integer, integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexusertodate_create(integer, integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterindexusertodate_edit(integer, integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterproject(integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterproject_not(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterproject_or(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterproject_exclusive(bigint[], integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecord(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecord_chardata(integer, character varying) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecord_or(bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordbetweendates_create(date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordbetweendates_edit(date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordfromdate_create(date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordfromdate_edit(date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordtodate_create(date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordtodate_edit(date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduser_create(integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduser_edit(integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduserbetweendates_create(integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduserbetweendates_edit(integer, date, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduserfromdate_create(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecorduserfromdate_edit(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordusertodate_create(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filterrecordusertodate_edit(integer, date) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersubattribute(integer, integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersubattribute_chardata(integer, integer, integer, character varying) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersubattribute_or(integer, integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersynonym(integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersynonym_chardata(integer, integer, character varying) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtersynonym_or(integer, bigint[]) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.filtertermbase(integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.levenshtein(text, text, integer, integer, integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.levenshtein(text, text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.metaphone(text, integer) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.soundex(text) to user_group;


GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user(text, text, text, text, boolean, date, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_category(text, boolean) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user_category(text, boolean, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user(text, text, text, text, boolean, date, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_get_user_passwd(bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_termbase(text, bigint, text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user_last_signon(bigint, timestamp with time zone) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_termbase(text, bigint, timestamp with time zone, text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_project(text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_project(text, timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_field(text, int, int, int, text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_field(text, int, int, int, text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_presetfield(text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_presetfield(text, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_project(bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_category_project(bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_delete_user_category_project(bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_delete_user_project(bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_generic_return_ref(text) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_record(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_record_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_record_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_delete_record_project(bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_record_project(bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_record_attribute(character varying, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_record_attribute(character varying, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_record_attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_record_attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_term(character varying, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_term(character varying, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_term_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_term_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_term_attribute(character varying, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_term_attribute(character varying, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_synonym(character varying, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_synonym(character varying, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_synonym_attribute(character varying, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_update_synonym_attribute(character varying, bigint, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_term_attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_term_attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_synonym_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_synonym_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_add_synonym__attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_edit_synonym__attribute_audit(character varying, boolean, bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_lock_record(bigint, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_unlock_record(bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_record(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_recordattribute(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_term(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_termattribute(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_synonym(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_archive_synonymattribute(timestamp with time zone, bigint) to user_group;
GRANT ALL PRIVILEGES ON FUNCTION tms.sp_unlock_all_records(bigint) to user_group;

-- This functino is no longer used
-- GRANT ALL PRIVILEGES ON FUNCTION tms.sp_roles(role_sql text) to user_group;

GRANT ALL PRIVILEGES ON FUNCTION tms.text_soundex(text) to user_group;

GRANT EXECUTE ON FUNCTION tms.sp_generic_return_ref(text) to guest_group;

-- guest_group will not modify functions

-- Ceate user role
-- This can be commented out if Postgres already has this ROLE
-- Change this password to match the postgres user password
--CREATE ROLE user_role WITH LOGIN PASSWORD 'password';

GRANT user_group to user_role;

-- Create guest role
-- This can be commented out if Postgres already has this ROLE
-- Change this password to match the postgres user password
--CREATE ROLE guest_role WITH LOGIN PASSWORD 'password';

GRANT guest_group to guest_role;

