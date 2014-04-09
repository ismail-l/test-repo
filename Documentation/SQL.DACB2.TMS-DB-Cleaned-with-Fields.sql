--
-- PostgreSQL database dump
--

-- Started on 2014-02-11 12:24:59

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 6 (class 2615 OID 174126)
-- Name: tms; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA tms;


ALTER SCHEMA tms OWNER TO postgres;

--
-- TOC entry 550 (class 2612 OID 174127)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

DROP PROCEDURAL LANGUAGE plpgsql;
CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = tms, pg_catalog;

--
-- TOC entry 21 (class 1255 OID 174128)
-- Dependencies: 6
-- Name: difference(text, text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION difference(text, text) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'difference';


ALTER FUNCTION tms.difference(text, text) OWNER TO postgres;

--
-- TOC entry 22 (class 1255 OID 174129)
-- Dependencies: 6
-- Name: dmetaphone(text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION dmetaphone(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'dmetaphone';


ALTER FUNCTION tms.dmetaphone(text) OWNER TO postgres;

--
-- TOC entry 23 (class 1255 OID 174130)
-- Dependencies: 6
-- Name: dmetaphone_alt(text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION dmetaphone_alt(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'dmetaphone_alt';


ALTER FUNCTION tms.dmetaphone_alt(text) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1737 (class 1259 OID 174131)
-- Dependencies: 6
-- Name: records; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE records (
    recordid bigint NOT NULL,
    archivedtimestamp timestamp with time zone,
    recordtimestamp timestamp with time zone NOT NULL,
    beingeditedby bigint,
    termbaseid bigint NOT NULL
);


ALTER TABLE tms.records OWNER TO postgres;

--
-- TOC entry 1738 (class 1259 OID 174134)
-- Dependencies: 6
-- Name: termattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE termattributes (
    termattributeid bigint NOT NULL,
    chardata character varying NOT NULL,
    termid bigint NOT NULL,
    fieldid bigint NOT NULL,
    archivedtimestamp time with time zone
);


ALTER TABLE tms.termattributes OWNER TO postgres;

--
-- TOC entry 1739 (class 1259 OID 174140)
-- Dependencies: 6
-- Name: terms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE terms (
    termid bigint NOT NULL,
    chardata character varying NOT NULL,
    recordid bigint NOT NULL,
    fieldid bigint NOT NULL,
    archivedtimestamp time with time zone
);


ALTER TABLE tms.terms OWNER TO postgres;

--
-- TOC entry 1740 (class 1259 OID 174146)
-- Dependencies: 1893 6
-- Name: filter_attribute_fields; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_attribute_fields AS
    SELECT termattributes.termattributeid, termattributes.fieldid AS attributefieldid, termattributes.chardata, termattributes.termid, terms.fieldid AS indexfieldid, terms.recordid, records.archivedtimestamp FROM termattributes, terms, records WHERE (((((termattributes.termid = terms.termid) AND (terms.recordid = records.recordid)) AND (terms.archivedtimestamp IS NULL)) AND (termattributes.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL));


ALTER TABLE tms.filter_attribute_fields OWNER TO postgres;

--
-- TOC entry 24 (class 1255 OID 174150)
-- Dependencies: 428 6
-- Name: filterattribute(integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterattribute(integer, bigint[]) RETURNS SETOF filter_attribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_attribute_fields"
WHERE "filter_attribute_fields".indexfieldid = $1
AND "filter_attribute_fields".attributefieldid = ALL($2);
$_$;


ALTER FUNCTION tms.filterattribute(integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 25 (class 1255 OID 174151)
-- Dependencies: 428 6
-- Name: filterattribute_chardata(integer, integer, character varying); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterattribute_chardata(integer, integer, character varying) RETURNS SETOF filter_attribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_attribute_fields"
WHERE "filter_attribute_fields".indexfieldid = $1
AND "filter_attribute_fields".attributefieldid = $2
AND lower("filter_attribute_fields".chardata) SIMILAR TO lower($3);
$_$;


ALTER FUNCTION tms.filterattribute_chardata(integer, integer, character varying) OWNER TO postgres;

--
-- TOC entry 26 (class 1255 OID 174152)
-- Dependencies: 428 6
-- Name: filterattribute_or(integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterattribute_or(integer, bigint[]) RETURNS SETOF filter_attribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_attribute_fields"
WHERE "filter_attribute_fields".indexfieldid = $1
AND "filter_attribute_fields".attributefieldid = ANY($2)
AND "filter_attribute_fields".archivedtimestamp is NULL;
$_$;


ALTER FUNCTION tms.filterattribute_or(integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 1741 (class 1259 OID 174153)
-- Dependencies: 6
-- Name: fields; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE fields (
    fieldid bigint NOT NULL,
    fieldname character varying(48) NOT NULL,
    fieldtypeid integer NOT NULL,
    fielddatatypeid integer NOT NULL,
    maxlength integer NOT NULL,
    defaultvalue character varying,
    sortindex bigint NOT NULL
);


ALTER TABLE tms.fields OWNER TO postgres;

--
-- TOC entry 1742 (class 1259 OID 174159)
-- Dependencies: 1894 6
-- Name: filter_index_fields; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_index_fields AS
    SELECT terms.termid, terms.chardata, terms.recordid, records.archivedtimestamp, terms.fieldid AS terms_fieldid, fields.fieldid FROM terms, fields, records WHERE ((((terms.fieldid = fields.fieldid) AND (terms.recordid = records.recordid)) AND (terms.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL));


ALTER TABLE tms.filter_index_fields OWNER TO postgres;

--
-- TOC entry 27 (class 1255 OID 174163)
-- Dependencies: 6 433
-- Name: filterindex(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindex(bigint[]) RETURNS SETOF filter_index_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_index_fields"
WHERE "filter_index_fields".fieldid = ALL ($1);
$_$;


ALTER FUNCTION tms.filterindex(bigint[]) OWNER TO postgres;

--
-- TOC entry 29 (class 1255 OID 174164)
-- Dependencies: 433 6
-- Name: filterindex_chardata(integer, character varying); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindex_chardata(integer, character varying) RETURNS SETOF filter_index_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_index_fields"
WHERE "filter_index_fields".fieldid = $1
AND lower("filter_index_fields".chardata) similar to lower($2);
$_$;


ALTER FUNCTION tms.filterindex_chardata(integer, character varying) OWNER TO postgres;

--
-- TOC entry 30 (class 1255 OID 174165)
-- Dependencies: 6 433
-- Name: filterindex_or(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindex_or(bigint[]) RETURNS SETOF filter_index_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_index_fields"
WHERE "filter_index_fields".fieldid = "filter_index_fields".terms_fieldid AND
"filter_index_fields".archivedtimestamp is NULL AND
"filter_index_fields".fieldid = ANY ($1);
$_$;


ALTER FUNCTION tms.filterindex_or(bigint[]) OWNER TO postgres;

--
-- TOC entry 1743 (class 1259 OID 174166)
-- Dependencies: 2096 2097 6
-- Name: audittrailcreateterms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailcreateterms (
    audittrailcreatetermid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    termid bigint NOT NULL
);


ALTER TABLE tms.audittrailcreateterms OWNER TO postgres;

--
-- TOC entry 1744 (class 1259 OID 174174)
-- Dependencies: 1895 6
-- Name: filter_indexfieldaudittrailcreate; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_indexfieldaudittrailcreate AS
    SELECT audittrailcreateterms.audittrailcreatetermid, audittrailcreateterms.userid, audittrailcreateterms.auditdatetime, terms.termid, terms.chardata, terms.fieldid, records.archivedtimestamp, terms.recordid FROM audittrailcreateterms, terms, records WHERE ((((terms.archivedtimestamp IS NULL) AND (records.archivedtimestamp IS NULL)) AND (audittrailcreateterms.termid = terms.termid)) AND (terms.recordid = records.recordid)) ORDER BY terms.recordid;


ALTER TABLE tms.filter_indexfieldaudittrailcreate OWNER TO postgres;

--
-- TOC entry 31 (class 1255 OID 174178)
-- Dependencies: 6 438
-- Name: filterindexbetweendate_create(integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexbetweendate_create(integer, date, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 						--Fieldid
AND "filter_indexfieldaudittrailcreate".auditdatetime between $2 AND $3;  	--Dates
$_$;


ALTER FUNCTION tms.filterindexbetweendate_create(integer, date, date) OWNER TO postgres;

--
-- TOC entry 1745 (class 1259 OID 174179)
-- Dependencies: 2099 2100 6
-- Name: audittraileditterms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittraileditterms (
    audittrailedittermid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    termid bigint NOT NULL
);


ALTER TABLE tms.audittraileditterms OWNER TO postgres;

--
-- TOC entry 1746 (class 1259 OID 174187)
-- Dependencies: 1896 6
-- Name: filter_indexfieldaudittrailedit; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_indexfieldaudittrailedit AS
    SELECT audittraileditterms.audittrailedittermid, audittraileditterms.userid, audittraileditterms.auditdatetime, terms.termid, terms.chardata, terms.fieldid, records.archivedtimestamp, terms.recordid FROM audittraileditterms, terms, records WHERE ((((terms.archivedtimestamp IS NULL) AND (records.archivedtimestamp IS NULL)) AND (audittraileditterms.termid = terms.termid)) AND (terms.recordid = records.recordid)) ORDER BY terms.recordid;


ALTER TABLE tms.filter_indexfieldaudittrailedit OWNER TO postgres;

--
-- TOC entry 32 (class 1255 OID 174191)
-- Dependencies: 443 6
-- Name: filterindexbetweendate_edit(integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexbetweendate_edit(integer, date, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 						--Fieldid
AND "filter_indexfieldaudittrailedit".auditdatetime between $2 AND $3;  	--Dates
$_$;


ALTER FUNCTION tms.filterindexbetweendate_edit(integer, date, date) OWNER TO postgres;

--
-- TOC entry 33 (class 1255 OID 174192)
-- Dependencies: 6 438
-- Name: filterindexfromdate_create(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexfromdate_create(integer, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 			--Fieldid
AND "filter_indexfieldaudittrailcreate".auditdatetime >= $2;
$_$;


ALTER FUNCTION tms.filterindexfromdate_create(integer, date) OWNER TO postgres;

--
-- TOC entry 34 (class 1255 OID 174193)
-- Dependencies: 6 443
-- Name: filterindexfromdate_edit(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexfromdate_edit(integer, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 		--Fieldid
AND "filter_indexfieldaudittrailedit".auditdatetime >= $2;
$_$;


ALTER FUNCTION tms.filterindexfromdate_edit(integer, date) OWNER TO postgres;

--
-- TOC entry 35 (class 1255 OID 174194)
-- Dependencies: 438 6
-- Name: filterindextodate_create(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindextodate_create(integer, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 			--Fieldid
AND "filter_indexfieldaudittrailcreate".auditdatetime < $2;   	--Date
$_$;


ALTER FUNCTION tms.filterindextodate_create(integer, date) OWNER TO postgres;

--
-- TOC entry 36 (class 1255 OID 174195)
-- Dependencies: 443 6
-- Name: filterindextodate_edit(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindextodate_edit(integer, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 			--Fieldid
AND "filter_indexfieldaudittrailedit".auditdatetime < $2;   	--Date
$_$;


ALTER FUNCTION tms.filterindextodate_edit(integer, date) OWNER TO postgres;

--
-- TOC entry 37 (class 1255 OID 174196)
-- Dependencies: 438 6
-- Name: filterindexuser_create(integer, integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuser_create(integer, integer) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 	--Fieldid
AND "filter_indexfieldaudittrailcreate".userid = $2;    --Userid
$_$;


ALTER FUNCTION tms.filterindexuser_create(integer, integer) OWNER TO postgres;

--
-- TOC entry 38 (class 1255 OID 174197)
-- Dependencies: 6 443
-- Name: filterindexuser_edit(integer, integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuser_edit(integer, integer) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 	--Fieldid
AND "filter_indexfieldaudittrailedit".userid = $2;    	--Userid
$_$;


ALTER FUNCTION tms.filterindexuser_edit(integer, integer) OWNER TO postgres;

--
-- TOC entry 20 (class 1255 OID 174198)
-- Dependencies: 6 438
-- Name: filterindexuserbetweendate_create(integer, integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuserbetweendate_create(integer, integer, date, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 					--Fieldid
AND "filter_indexfieldaudittrailcreate".userid = $2						--Userid
AND "filter_indexfieldaudittrailcreate".auditdatetime BETWEEN $3 AND $4;
$_$;


ALTER FUNCTION tms.filterindexuserbetweendate_create(integer, integer, date, date) OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 174199)
-- Dependencies: 443 6
-- Name: filterindexuserbetweendate_edit(integer, integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuserbetweendate_edit(integer, integer, date, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 					--Fieldid
AND "filter_indexfieldaudittrailedit".userid = $2						--Userid
AND "filter_indexfieldaudittrailedit".auditdatetime BETWEEN $3 AND $4;
$_$;


ALTER FUNCTION tms.filterindexuserbetweendate_edit(integer, integer, date, date) OWNER TO postgres;

--
-- TOC entry 39 (class 1255 OID 174200)
-- Dependencies: 6 438
-- Name: filterindexuserfromdate_create(integer, integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuserfromdate_create(integer, integer, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 				--Fieldid
AND "filter_indexfieldaudittrailcreate".userid = $2					--Userid
AND "filter_indexfieldaudittrailcreate".auditdatetime >= $3;  		--Date
$_$;


ALTER FUNCTION tms.filterindexuserfromdate_create(integer, integer, date) OWNER TO postgres;

--
-- TOC entry 40 (class 1255 OID 174201)
-- Dependencies: 443 6
-- Name: filterindexuserfromdate_edit(integer, integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexuserfromdate_edit(integer, integer, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 				--Fieldid
AND "filter_indexfieldaudittrailedit".userid = $2					--Userid
AND "filter_indexfieldaudittrailedit".auditdatetime >= $3;  		--Date
$_$;


ALTER FUNCTION tms.filterindexuserfromdate_edit(integer, integer, date) OWNER TO postgres;

--
-- TOC entry 41 (class 1255 OID 174202)
-- Dependencies: 6 438
-- Name: filterindexusertodate_create(integer, integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexusertodate_create(integer, integer, date) RETURNS SETOF filter_indexfieldaudittrailcreate
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailcreate"
WHERE "filter_indexfieldaudittrailcreate".fieldid = $1 			--Fieldid
AND "filter_indexfieldaudittrailcreate".userid = $2				--Userid
AND "filter_indexfieldaudittrailcreate".auditdatetime < $3;  	--Date
$_$;


ALTER FUNCTION tms.filterindexusertodate_create(integer, integer, date) OWNER TO postgres;

--
-- TOC entry 43 (class 1255 OID 174203)
-- Dependencies: 443 6
-- Name: filterindexusertodate_edit(integer, integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterindexusertodate_edit(integer, integer, date) RETURNS SETOF filter_indexfieldaudittrailedit
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_indexfieldaudittrailedit"
WHERE "filter_indexfieldaudittrailedit".fieldid = $1 			--Fieldid
AND "filter_indexfieldaudittrailedit".userid = $2				--Userid
AND "filter_indexfieldaudittrailedit".auditdatetime < $3;  		--Date
$_$;


ALTER FUNCTION tms.filterindexusertodate_edit(integer, integer, date) OWNER TO postgres;

--
-- TOC entry 1747 (class 1259 OID 174204)
-- Dependencies: 6
-- Name: recordprojects; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE recordprojects (
    recordprojectid bigint NOT NULL,
    recordid bigint NOT NULL,
    projectid bigint NOT NULL
);


ALTER TABLE tms.recordprojects OWNER TO postgres;

--
-- TOC entry 45 (class 1255 OID 174207)
-- Dependencies: 6 445
-- Name: filterproject(integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterproject(integer) RETURNS SETOF recordprojects
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.recordprojects
WHERE recordprojects.projectid = $1;
$_$;


ALTER FUNCTION tms.filterproject(integer) OWNER TO postgres;

--
-- TOC entry 136 (class 1255 OID 174968)
-- Dependencies: 6
-- Name: filterproject_exclusive(bigint[], integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterproject_exclusive(bigint[], integer) RETURNS TABLE(recordid bigint, count bigint)
    LANGUAGE sql STABLE
    AS $_$
select recordprojects.recordid, count(*) 
from tms.recordprojects 
where recordprojects.projectid != any ($1)
group by recordprojects.recordid
having count(recordprojects.projectid) < 2 AND (recordprojects.recordid IN (SELECT recordid FROM tms.filterproject($2)))
order by recordprojects.recordid
$_$;


ALTER FUNCTION tms.filterproject_exclusive(bigint[], integer) OWNER TO postgres;

--
-- TOC entry 47 (class 1255 OID 174209)
-- Dependencies: 6 445
-- Name: filterproject_not(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterproject_not(bigint[]) RETURNS SETOF recordprojects
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.recordprojects
WHERE recordprojects.projectid != ANY ($1);
$_$;


ALTER FUNCTION tms.filterproject_not(bigint[]) OWNER TO postgres;

--
-- TOC entry 48 (class 1255 OID 174210)
-- Dependencies: 445 6
-- Name: filterproject_or(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterproject_or(bigint[]) RETURNS SETOF recordprojects
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.recordprojects
WHERE recordprojects.projectid = ANY ($1);
$_$;


ALTER FUNCTION tms.filterproject_or(bigint[]) OWNER TO postgres;

--
-- TOC entry 1748 (class 1259 OID 174211)
-- Dependencies: 6
-- Name: recordattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE recordattributes (
    recordattributeid bigint NOT NULL,
    chardata character varying NOT NULL,
    recordid bigint NOT NULL,
    fieldid bigint NOT NULL,
    archivedtimestamp time with time zone
);


ALTER TABLE tms.recordattributes OWNER TO postgres;

--
-- TOC entry 1749 (class 1259 OID 174217)
-- Dependencies: 1897 6
-- Name: filter_record_fields; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_record_fields AS
    SELECT recordattributes.recordattributeid, recordattributes.fieldid AS recordattribute_fieldid, recordattributes.chardata, recordattributes.recordid, records.archivedtimestamp, fields.fieldid FROM recordattributes, fields, records WHERE ((((recordattributes.fieldid = fields.fieldid) AND (recordattributes.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL)) AND (recordattributes.recordid = records.recordid));


ALTER TABLE tms.filter_record_fields OWNER TO postgres;

--
-- TOC entry 49 (class 1255 OID 174221)
-- Dependencies: 6 450
-- Name: filterrecord(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecord(bigint[]) RETURNS SETOF filter_record_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT * 
FROM tms."filter_record_fields"
WHERE "filter_record_fields".recordattribute_fieldid = "filter_record_fields".fieldid AND
      "filter_record_fields".fieldid = ALL ($1) AND
      "filter_record_fields".archivedtimestamp IS NULL;
$_$;


ALTER FUNCTION tms.filterrecord(bigint[]) OWNER TO postgres;

--
-- TOC entry 50 (class 1255 OID 174222)
-- Dependencies: 450 6
-- Name: filterrecord_chardata(integer, character varying); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecord_chardata(integer, character varying) RETURNS SETOF filter_record_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT * 
FROM tms."filter_record_fields"
WHERE "filter_record_fields".recordattribute_fieldid = "filter_record_fields".fieldid AND
      "filter_record_fields".fieldid = $1 AND
      "filter_record_fields".archivedtimestamp IS NULL AND
      lower("filter_record_fields".chardata) similar to lower($2);
$_$;


ALTER FUNCTION tms.filterrecord_chardata(integer, character varying) OWNER TO postgres;

--
-- TOC entry 52 (class 1255 OID 174223)
-- Dependencies: 450 6
-- Name: filterrecord_or(bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecord_or(bigint[]) RETURNS SETOF filter_record_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT * 
FROM tms."filter_record_fields"
WHERE "filter_record_fields".recordattribute_fieldid = "filter_record_fields".fieldid AND
      "filter_record_fields".fieldid = ANY ($1) AND
      "filter_record_fields".archivedtimestamp IS NULL;
$_$;


ALTER FUNCTION tms.filterrecord_or(bigint[]) OWNER TO postgres;

--
-- TOC entry 1750 (class 1259 OID 174224)
-- Dependencies: 2104 2105 6
-- Name: audittrailcreaterecords; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailcreaterecords (
    audittrailcreaterecordid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    recordid bigint NOT NULL
);


ALTER TABLE tms.audittrailcreaterecords OWNER TO postgres;

--
-- TOC entry 53 (class 1255 OID 174232)
-- Dependencies: 6 452
-- Name: filterrecordbetweendates_create(date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordbetweendates_create(date, date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.auditdatetime BETWEEN $1 AND $2;	--Between Dates													
$_$;


ALTER FUNCTION tms.filterrecordbetweendates_create(date, date) OWNER TO postgres;

--
-- TOC entry 1751 (class 1259 OID 174233)
-- Dependencies: 2107 2108 6
-- Name: audittraileditrecords; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittraileditrecords (
    audittraileditrecordid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    recordid bigint NOT NULL
);


ALTER TABLE tms.audittraileditrecords OWNER TO postgres;

--
-- TOC entry 54 (class 1255 OID 174241)
-- Dependencies: 6 455
-- Name: filterrecordbetweendates_edit(date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordbetweendates_edit(date, date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.auditdatetime BETWEEN $1 AND $2;	--Between Dates													
$_$;


ALTER FUNCTION tms.filterrecordbetweendates_edit(date, date) OWNER TO postgres;

--
-- TOC entry 55 (class 1255 OID 174242)
-- Dependencies: 452 6
-- Name: filterrecordfromdate_create(date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordfromdate_create(date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.auditdatetime >= $1;		--FromDate
$_$;


ALTER FUNCTION tms.filterrecordfromdate_create(date) OWNER TO postgres;

--
-- TOC entry 56 (class 1255 OID 174243)
-- Dependencies: 455 6
-- Name: filterrecordfromdate_edit(date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordfromdate_edit(date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.auditdatetime >= $1;		--FromDate
$_$;


ALTER FUNCTION tms.filterrecordfromdate_edit(date) OWNER TO postgres;

--
-- TOC entry 57 (class 1255 OID 174244)
-- Dependencies: 452 6
-- Name: filterrecordtodate_create(date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordtodate_create(date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE  audittrailcreaterecords.auditdatetime < $1;		--ToDate
$_$;


ALTER FUNCTION tms.filterrecordtodate_create(date) OWNER TO postgres;

--
-- TOC entry 42 (class 1255 OID 174245)
-- Dependencies: 6 455
-- Name: filterrecordtodate_edit(date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordtodate_edit(date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE  audittraileditrecords.auditdatetime < $1;		--ToDate
$_$;


ALTER FUNCTION tms.filterrecordtodate_edit(date) OWNER TO postgres;

--
-- TOC entry 44 (class 1255 OID 174246)
-- Dependencies: 452 6
-- Name: filterrecorduser_create(integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduser_create(integer) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.userid = $1;		--UserID
$_$;


ALTER FUNCTION tms.filterrecorduser_create(integer) OWNER TO postgres;

--
-- TOC entry 46 (class 1255 OID 174247)
-- Dependencies: 6 455
-- Name: filterrecorduser_edit(integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduser_edit(integer) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.userid = $1;		--UserID
$_$;


ALTER FUNCTION tms.filterrecorduser_edit(integer) OWNER TO postgres;

--
-- TOC entry 51 (class 1255 OID 174248)
-- Dependencies: 6 452
-- Name: filterrecorduserbetweendates_create(integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduserbetweendates_create(integer, date, date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.userid = $1				--UserId
AND audittrailcreaterecords.auditdatetime BETWEEN $2 AND $3;	--ToDate
$_$;


ALTER FUNCTION tms.filterrecorduserbetweendates_create(integer, date, date) OWNER TO postgres;

--
-- TOC entry 58 (class 1255 OID 174249)
-- Dependencies: 6 455
-- Name: filterrecorduserbetweendates_edit(integer, date, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduserbetweendates_edit(integer, date, date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.userid = $1				--UserId
AND audittraileditrecords.auditdatetime BETWEEN $2 AND $3;	--ToDate
$_$;


ALTER FUNCTION tms.filterrecorduserbetweendates_edit(integer, date, date) OWNER TO postgres;

--
-- TOC entry 59 (class 1255 OID 174250)
-- Dependencies: 452 6
-- Name: filterrecorduserfromdate_create(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduserfromdate_create(integer, date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.userid = $1			--UserId
AND audittrailcreaterecords.auditdatetime >= $2;			--FromDate
$_$;


ALTER FUNCTION tms.filterrecorduserfromdate_create(integer, date) OWNER TO postgres;

--
-- TOC entry 60 (class 1255 OID 174251)
-- Dependencies: 6 455
-- Name: filterrecorduserfromdate_edit(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecorduserfromdate_edit(integer, date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.userid = $1			--UserId
AND audittraileditrecords.auditdatetime >= $2;			--FromDate
$_$;


ALTER FUNCTION tms.filterrecorduserfromdate_edit(integer, date) OWNER TO postgres;

--
-- TOC entry 61 (class 1255 OID 174252)
-- Dependencies: 452 6
-- Name: filterrecordusertodate_create(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordusertodate_create(integer, date) RETURNS SETOF audittrailcreaterecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittrailcreaterecords
WHERE audittrailcreaterecords.userid = $1			--UserId
AND audittrailcreaterecords.auditdatetime < $2;			--ToDate
$_$;


ALTER FUNCTION tms.filterrecordusertodate_create(integer, date) OWNER TO postgres;

--
-- TOC entry 62 (class 1255 OID 174253)
-- Dependencies: 455 6
-- Name: filterrecordusertodate_edit(integer, date); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filterrecordusertodate_edit(integer, date) RETURNS SETOF audittraileditrecords
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms.audittraileditrecords
WHERE audittraileditrecords.userid = $1			--UserId
AND audittraileditrecords.auditdatetime < $2;			--ToDate
$_$;


ALTER FUNCTION tms.filterrecordusertodate_edit(integer, date) OWNER TO postgres;

--
-- TOC entry 1752 (class 1259 OID 174254)
-- Dependencies: 6
-- Name: synonymattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE synonymattributes (
    synonymattributeid bigint NOT NULL,
    chardata character varying NOT NULL,
    synonymid bigint NOT NULL,
    fieldid bigint NOT NULL,
    archivedtimestamp time with time zone
);


ALTER TABLE tms.synonymattributes OWNER TO postgres;

--
-- TOC entry 1753 (class 1259 OID 174260)
-- Dependencies: 6
-- Name: synonyms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE synonyms (
    synonymid bigint NOT NULL,
    chardata character varying NOT NULL,
    termid bigint NOT NULL,
    fieldid bigint NOT NULL,
    archivedtimestamp time with time zone
);


ALTER TABLE tms.synonyms OWNER TO postgres;

--
-- TOC entry 1754 (class 1259 OID 174266)
-- Dependencies: 1898 6
-- Name: filter_subattribute_fields; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_subattribute_fields AS
    SELECT synonymattributes.synonymattributeid, synonymattributes.fieldid AS subattributefieldid, synonymattributes.chardata, records.archivedtimestamp, synonyms.synonymid, synonyms.fieldid AS attributefieldid, terms.termid, terms.fieldid AS indexfieldid, terms.recordid FROM synonymattributes, synonyms, terms, records WHERE (((((((synonymattributes.synonymid = synonyms.synonymid) AND (synonyms.termid = terms.termid)) AND (terms.recordid = records.recordid)) AND (synonymattributes.archivedtimestamp IS NULL)) AND (synonyms.archivedtimestamp IS NULL)) AND (terms.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL));


ALTER TABLE tms.filter_subattribute_fields OWNER TO postgres;

--
-- TOC entry 63 (class 1255 OID 174270)
-- Dependencies: 6 464
-- Name: filtersubattribute(integer, integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersubattribute(integer, integer, bigint[]) RETURNS SETOF filter_subattribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_subattribute_fields"
WHERE "filter_subattribute_fields".indexfieldid = $1
AND "filter_subattribute_fields".attributefieldid = $2
AND "filter_subattribute_fields".subattributefieldid = ALL($3);
--AND lower("filter_subattribute_fields".chardata) SIMILAR TO lower($3);
$_$;


ALTER FUNCTION tms.filtersubattribute(integer, integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 64 (class 1255 OID 174271)
-- Dependencies: 6 464
-- Name: filtersubattribute_chardata(integer, integer, integer, character varying); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersubattribute_chardata(integer, integer, integer, character varying) RETURNS SETOF filter_subattribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_subattribute_fields"
WHERE "filter_subattribute_fields".indexfieldid = $1
AND "filter_subattribute_fields".attributefieldid = $2
AND "filter_subattribute_fields".subattributefieldid = $3
AND lower("filter_subattribute_fields".chardata) SIMILAR TO lower($4);
$_$;


ALTER FUNCTION tms.filtersubattribute_chardata(integer, integer, integer, character varying) OWNER TO postgres;

--
-- TOC entry 65 (class 1255 OID 174272)
-- Dependencies: 6 464
-- Name: filtersubattribute_or(integer, integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersubattribute_or(integer, integer, bigint[]) RETURNS SETOF filter_subattribute_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_subattribute_fields"
WHERE "filter_subattribute_fields".indexfieldid = $1
AND "filter_subattribute_fields".attributefieldid = $2
AND "filter_subattribute_fields".subattributefieldid = ANY ($3);
--AND lower("filter_subattribute_fields".chardata) SIMILAR TO lower($3);
$_$;


ALTER FUNCTION tms.filtersubattribute_or(integer, integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 1755 (class 1259 OID 174273)
-- Dependencies: 1899 6
-- Name: filter_synonym_fields; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW filter_synonym_fields AS
    SELECT synonyms.synonymid, synonyms.fieldid AS attributefieldid, synonyms.chardata, records.archivedtimestamp, synonyms.termid, terms.fieldid AS indexfieldid, terms.recordid FROM synonyms, terms, records WHERE ((((synonyms.termid = terms.termid) AND (synonyms.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL)) AND (terms.recordid = records.recordid));


ALTER TABLE tms.filter_synonym_fields OWNER TO postgres;

--
-- TOC entry 66 (class 1255 OID 174277)
-- Dependencies: 6 466
-- Name: filtersynonym(integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersynonym(integer, bigint[]) RETURNS SETOF filter_synonym_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_synonym_fields"
WHERE "filter_synonym_fields".indexfieldid = $1
AND "filter_synonym_fields".attributefieldid = ALL($2);
$_$;


ALTER FUNCTION tms.filtersynonym(integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 67 (class 1255 OID 174278)
-- Dependencies: 6 466
-- Name: filtersynonym_chardata(integer, integer, character varying); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersynonym_chardata(integer, integer, character varying) RETURNS SETOF filter_synonym_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_synonym_fields"
WHERE "filter_synonym_fields".indexfieldid = $1
AND "filter_synonym_fields".attributefieldid = $2
AND lower("filter_synonym_fields".chardata) SIMILAR TO lower($3);
$_$;


ALTER FUNCTION tms.filtersynonym_chardata(integer, integer, character varying) OWNER TO postgres;

--
-- TOC entry 69 (class 1255 OID 174279)
-- Dependencies: 466 6
-- Name: filtersynonym_or(integer, bigint[]); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtersynonym_or(integer, bigint[]) RETURNS SETOF filter_synonym_fields
    LANGUAGE sql STABLE
    AS $_$
SELECT *
FROM tms."filter_synonym_fields"
WHERE "filter_synonym_fields".indexfieldid = $1
AND "filter_synonym_fields".attributefieldid = ANY($2)
AND "filter_synonym_fields".archivedtimestamp is NULL;
$_$;


ALTER FUNCTION tms.filtersynonym_or(integer, bigint[]) OWNER TO postgres;

--
-- TOC entry 70 (class 1255 OID 174280)
-- Dependencies: 420 6
-- Name: filtertermbase(integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION filtertermbase(integer) RETURNS SETOF records
    LANGUAGE sql STABLE
    AS $_$
SELECT * FROM tms.records
WHERE records.termbaseid = $1 and records.archivedtimestamp is null;
$_$;


ALTER FUNCTION tms.filtertermbase(integer) OWNER TO postgres;

--
-- TOC entry 71 (class 1255 OID 174281)
-- Dependencies: 6
-- Name: levenshtein(text, text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION levenshtein(text, text) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'levenshtein';


ALTER FUNCTION tms.levenshtein(text, text) OWNER TO postgres;

--
-- TOC entry 72 (class 1255 OID 174282)
-- Dependencies: 6
-- Name: levenshtein(text, text, integer, integer, integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION levenshtein(text, text, integer, integer, integer) RETURNS integer
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'levenshtein_with_costs';


ALTER FUNCTION tms.levenshtein(text, text, integer, integer, integer) OWNER TO postgres;

--
-- TOC entry 73 (class 1255 OID 174283)
-- Dependencies: 6
-- Name: metaphone(text, integer); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION metaphone(text, integer) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'metaphone';


ALTER FUNCTION tms.metaphone(text, integer) OWNER TO postgres;

--
-- TOC entry 74 (class 1255 OID 174284)
-- Dependencies: 6
-- Name: soundex(text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION soundex(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'soundex';


ALTER FUNCTION tms.soundex(text) OWNER TO postgres;

--
-- TOC entry 75 (class 1255 OID 174285)
-- Dependencies: 6 550
-- Name: sp_add_field(text, integer, integer, integer, text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_field(text, integer, integer, integer, text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_field_name ALIAS for $1;
		_fieldtype_id ALIAS for $2;
		_field_data_type ALIAS for $3;
		_max_length ALIAS for $4;
		_default_value ALIAS for $5;
		_sort_index ALIAS for $6;
		_returning_field_id bigint;
	BEGIN
		INSERT INTO tms.fields (fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) values 
				      (_field_name, _fieldtype_id, _field_data_type, _max_length, _default_value, _sort_index) RETURNING 
				      fieldid INTO _returning_field_id;
		
	RETURN _returning_field_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_field(text, integer, integer, integer, text, bigint) OWNER TO postgres;

--
-- TOC entry 76 (class 1255 OID 174286)
-- Dependencies: 550 6
-- Name: sp_add_presetfield(text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_presetfield(text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_presetfield_name ALIAS for $1;
		_field_id ALIAS for $2;
		_returning_presetfield_id bigint;
	BEGIN
		INSERT INTO tms.presetfields (presetfieldname, fieldid) values 
				      (_presetfield_name, _field_id) RETURNING 
				      presetfieldid INTO _returning_presetfield_id;
		
	RETURN _returning_presetfield_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_presetfield(text, bigint) OWNER TO postgres;

--
-- TOC entry 77 (class 1255 OID 174287)
-- Dependencies: 6 550
-- Name: sp_add_project(text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_project(text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE		
		_project_name ALIAS for $1;
		_termbase_id ALIAS for $2;
		_returning_project_id bigint;
	BEGIN
		INSERT INTO tms.projects (projectname, termbaseid) values 
				      (_project_name, _termbase_id) RETURNING 
				      projectid INTO _returning_project_id;

		RETURN _returning_project_id;
			                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_project(text, bigint) OWNER TO postgres;

--
-- TOC entry 78 (class 1255 OID 174288)
-- Dependencies: 6 550
-- Name: sp_add_record(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_record(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_record_timestamp ALIAS for $1;
		_termbase_id ALIAS for $2;
		_returning_record_id bigint;
	BEGIN
		INSERT INTO tms.records (recordtimestamp, termbaseid) values 
				      (_record_timestamp, _termbase_id) RETURNING 
				      recordid INTO _returning_record_id;

	RETURN _returning_record_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_record(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 79 (class 1255 OID 174289)
-- Dependencies: 550 6
-- Name: sp_add_record_attribute(character varying, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_record_attribute(character varying, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_record_id ALIAS for $2;
		_field_id ALIAS for $3;
		_returning_recordattribute_id bigint;
	BEGIN
		INSERT INTO tms.recordattributes (chardata, recordid, fieldid) values 
						 (_chardata, _record_id, _field_id) RETURNING 
				      recordattributeid INTO _returning_recordattribute_id;
		
	RETURN _returning_recordattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_record_attribute(character varying, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 80 (class 1255 OID 174290)
-- Dependencies: 6 550
-- Name: sp_add_record_attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_record_attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_recordattribute_id ALIAS for $4;
		_returning_audittrailcreaterecordattr_id bigint;
	BEGIN
		INSERT INTO tms.audittrailtcreaterecordattributes (chardata, canberendered, userid, recordattributeid) values 
							(_chardata, _canberendered, _user_id, _recordattribute_id) RETURNING 
							audittrailcreaterecordattrid INTO _returning_audittrailcreaterecordattr_id;

	RETURN _returning_audittrailcreaterecordattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_record_attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 68 (class 1255 OID 174291)
-- Dependencies: 6 550
-- Name: sp_add_record_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_record_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_record_id ALIAS for $4;
		_returning_audittrailcreaterecord_id bigint;
	BEGIN
		INSERT INTO tms.audittrailcreaterecords (chardata, canberendered, userid, recordid) values 
							(_chardata, _canberendered, _user_id, _record_id) RETURNING 
							audittrailcreaterecordid INTO _returning_audittrailcreaterecord_id;

	RETURN _returning_audittrailcreaterecord_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_record_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 81 (class 1255 OID 174292)
-- Dependencies: 550 6
-- Name: sp_add_record_project(bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_record_project(bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_record_id ALIAS for $1;
		_project_id ALIAS for $2;
		_returning_recordproject_id bigint;
	BEGIN
		INSERT INTO tms.recordprojects (recordid, projectid) values 
							 (_record_id, _project_id) RETURNING 
				      recordprojectid INTO _returning_recordproject_id;
		
	RETURN _returning_recordproject_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_record_project(bigint, bigint) OWNER TO postgres;

--
-- TOC entry 82 (class 1255 OID 174293)
-- Dependencies: 6 550
-- Name: sp_add_synonym(character varying, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_synonym(character varying, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_term_id ALIAS for $2;
		_field_id ALIAS for $3;
		_returning_synonym_id bigint;
	BEGIN
		INSERT INTO tms.synonyms (chardata, termid, fieldid) values 
						 (_chardata, _term_id, _field_id) RETURNING 
				      synonymid INTO _returning_synonym_id;
		
	RETURN _returning_synonym_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_synonym(character varying, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 83 (class 1255 OID 174294)
-- Dependencies: 550 6
-- Name: sp_add_synonym__attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_synonym__attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_synonymattribute_id ALIAS for $4;
		_returning_audittrailcreatesynonymattr_id bigint;
	BEGIN
		INSERT INTO tms.audittrailcreatesynonymattributes (chardata, canberendered, userid, synonymattributeid) values 
								  (_chardata, _canberendered, _user_id, _synonymattribute_id) RETURNING 
									audittrailcreatesynonymattrid INTO _returning_audittrailcreatesynonymattr_id;

	RETURN _returning_audittrailcreatesynonymattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_synonym__attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 84 (class 1255 OID 174295)
-- Dependencies: 6 550
-- Name: sp_add_synonym_attribute(character varying, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_synonym_attribute(character varying, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_synonym_id ALIAS for $2;
		_field_id ALIAS for $3;
		_returning_synonymattribute_id bigint;
	BEGIN
		INSERT INTO tms.synonymattributes (chardata, synonymid, fieldid) values 
						 (_chardata, _synonym_id, _field_id) RETURNING 
				      synonymattributeid INTO _returning_synonymattribute_id;
		
	RETURN _returning_synonymattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_synonym_attribute(character varying, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 85 (class 1255 OID 174296)
-- Dependencies: 550 6
-- Name: sp_add_synonym_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_synonym_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_synonym_id ALIAS for $4;
		_returning_audittrailcreatesynonym_id bigint;
	BEGIN
		INSERT INTO tms.audittrailcreatesynonyms (chardata, canberendered, userid, synonymid) values 
							(_chardata, _canberendered, _user_id, _synonym_id) RETURNING 
							audittrailcreatesynonymid INTO _returning_audittrailcreatesynonym_id;

	RETURN _returning_audittrailcreatesynonym_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_synonym_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 86 (class 1255 OID 174297)
-- Dependencies: 6 550
-- Name: sp_add_term(character varying, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_term(character varying, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_record_id ALIAS for $2;
		_field_id ALIAS for $3;
		_returning_term_id bigint;
	BEGIN
		INSERT INTO tms.terms (chardata, recordid, fieldid) values 
				     (_chardata, _record_id, _field_id) RETURNING 
				      termid INTO _returning_term_id;
		
	RETURN _returning_term_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_term(character varying, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 88 (class 1255 OID 174298)
-- Dependencies: 6 550
-- Name: sp_add_term_attribute(character varying, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_term_attribute(character varying, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_term_id ALIAS for $2;
		_field_id ALIAS for $3;
		_returning_termattribute_id bigint;
	BEGIN
		INSERT INTO tms.termattributes (chardata, termid, fieldid) values 
						 (_chardata, _term_id, _field_id) RETURNING 
				      termattributeid INTO _returning_termattribute_id;
		
	RETURN _returning_termattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_term_attribute(character varying, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 89 (class 1255 OID 174299)
-- Dependencies: 6 550
-- Name: sp_add_term_attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_term_attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_termattribute_id ALIAS for $4;
		_returning_audittrailcreatetermattr_id bigint;
	BEGIN
		INSERT INTO tms.audittrailcreatetermattributes (chardata, canberendered, userid, termattributeid) values 
							(_chardata, _canberendered, _user_id, _termattribute_id) RETURNING 
							audittrailcreatetermattrid INTO _returning_audittrailcreatetermattr_id;

	RETURN _returning_audittrailcreatetermattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_term_attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 90 (class 1255 OID 174300)
-- Dependencies: 6 550
-- Name: sp_add_term_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_term_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_term_id ALIAS for $4;
		_returning_audittrailcreateterm_id bigint;
	BEGIN
		INSERT INTO tms.audittrailcreateterms (chardata, canberendered, userid, termid) values 
							(_chardata, _canberendered, _user_id, _term_id) RETURNING 
							audittrailcreatetermid INTO _returning_audittrailcreateterm_id;

	RETURN _returning_audittrailcreateterm_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_term_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 91 (class 1255 OID 174301)
-- Dependencies: 6 550
-- Name: sp_add_termbase(text, bigint, text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_termbase(text, bigint, text) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_termbase_name ALIAS for $1;
		_user_id ALIAS for $2;
		_email ALIAS for $3;
		_returning_termbase_id bigint;
	BEGIN
		INSERT INTO tms.termbases (termbasename, userid, adminemail) values 
				      (_termbase_name, _user_id, _email) RETURNING 
				      termbaseid INTO _returning_termbase_id;

	RETURN _returning_termbase_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_termbase(text, bigint, text) OWNER TO postgres;

--
-- TOC entry 92 (class 1255 OID 174302)
-- Dependencies: 6 550
-- Name: sp_add_user(text, text, text, text, boolean, date, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user(text, text, text, text, boolean, date, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_username ALIAS for $1;
		_passwd ALIAS for $2;
		_firstname ALIAS for $3;
		_lastname ALIAS for $4;
		_activated ALIAS for $5;
		_expirydate ALIAS for $6;
		_usercategoryid ALIAS for $7;
		_returning_user_id bigint;
	BEGIN
		INSERT INTO tms.users (username, passwd, firstname, lastname, activated, expirydate, usercategoryid) values 
				      (_username, _passwd, _firstname, _lastname, _activated, _expirydate, _usercategoryid) RETURNING 
				      userid INTO _returning_user_id;

	RETURN _returning_user_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user(text, text, text, text, boolean, date, bigint) OWNER TO postgres;

--
-- TOC entry 87 (class 1255 OID 174303)
-- Dependencies: 6 550
-- Name: sp_add_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_field_id ALIAS for $5;
		_user_id ALIAS for $6;
		_returning_accessrightuser_id bigint;
	BEGIN
		INSERT INTO tms.accessrightsuser (mayread, mayupdate, maydelete, mayexport, fieldid, userid) values 
				      (_mayread, _mayupdate, _maydelete, _mayexport, _field_id, _user_id) RETURNING 
				      accessrightuserid INTO _returning_accessrightuser_id;
		
	RETURN _returning_accessrightuser_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 93 (class 1255 OID 174304)
-- Dependencies: 6 550
-- Name: sp_add_user_category(text, boolean); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_category(text, boolean) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_usercategory ALIAS for $1;
		_isadmin ALIAS for $2;
		_returning_user_cat_id bigint;
	BEGIN
		INSERT INTO tms.usercategories (usercategory, isadmin) values 
				      (_usercategory, _isadmin) RETURNING 
				      usercategoryid INTO _returning_user_cat_id;

	RETURN _returning_user_cat_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_category(text, boolean) OWNER TO postgres;

--
-- TOC entry 94 (class 1255 OID 174305)
-- Dependencies: 6 550
-- Name: sp_add_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_field_id ALIAS for $5;
		_user_category_id ALIAS for $6;
		_returning_accessrightusercategory_id bigint;
	BEGIN
		INSERT INTO tms.accessrightsusercategory (mayread, mayupdate, maydelete, mayexport, fieldid, usercategoryid) values 
				      (_mayread, _mayupdate, _maydelete, _mayexport, _field_id, _user_category_id) RETURNING 
				      accessrightusercategoryid INTO _returning_accessrightusercategory_id;
		
	RETURN _returning_accessrightusercategory_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 95 (class 1255 OID 174306)
-- Dependencies: 550 6
-- Name: sp_add_user_category_project(bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_category_project(bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_project_id ALIAS for $1;
		_usercategory_id ALIAS for $2;
		_returning_usercategoryproject_id bigint;
	BEGIN
		INSERT INTO tms.usercategoryprojects (projectid, usercategoryid) values 
							 (_project_id, _usercategory_id) RETURNING 
				      usercategoryprojectid INTO _returning_usercategoryproject_id;
		
	RETURN _returning_usercategoryproject_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_category_project(bigint, bigint) OWNER TO postgres;

--
-- TOC entry 96 (class 1255 OID 174307)
-- Dependencies: 550 6
-- Name: sp_add_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_accessrightsuser_id ALIAS for $5;
		_field_id ALIAS for $6;
		_returning_childaccessrightuser_id bigint;
	BEGIN
		INSERT INTO tms.childaccessrightsuser (mayread, mayupdate, maydelete, mayexport, accessrightsuserid, fieldid) values 
				      (_mayread, _mayupdate, _maydelete, _mayexport, _accessrightsuser_id, _field_id) RETURNING 
				      childaccessrightuserid INTO _returning_childaccessrightuser_id;
		
	RETURN _returning_childaccessrightuser_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 97 (class 1255 OID 174308)
-- Dependencies: 6 550
-- Name: sp_add_user_project(bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_user_project(bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_project_id ALIAS for $1;
		_user_id ALIAS for $2;
		_returning_userproject_id bigint;
	BEGIN
		INSERT INTO tms.userprojects (projectid, userid) values 
							 (_project_id, _user_id) RETURNING 
				      userprojectid INTO _returning_userproject_id;
		
	RETURN _returning_userproject_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_user_project(bigint, bigint) OWNER TO postgres;

--
-- TOC entry 98 (class 1255 OID 174309)
-- Dependencies: 6 550
-- Name: sp_add_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_add_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_accessrightsusecategory_id ALIAS for $5;
		_field_id ALIAS for $6;
		_returning_childaccessrightusercategory_id bigint;
	BEGIN
		INSERT INTO tms.childaccessrightsusercategory (mayread, mayupdate, maydelete, mayexport, accessrightsusecategoryid, fieldid) values 
				      (_mayread, _mayupdate, _maydelete, _mayexport, _accessrightsusecategory_id, _field_id) RETURNING 
				      childaccessrightusercategoryid INTO _returning_childaccessrightusercategory_id;
		
	RETURN _returning_childaccessrightusercategory_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_add_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 99 (class 1255 OID 174310)
-- Dependencies: 550 6
-- Name: sp_archive_record(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_record(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_record_id ALIAS for $2;
		_returning_record_id bigint;
	BEGIN
		UPDATE tms.records set
				 archivedtimestamp = _archivedtimestamp WHERE
				 recordid = _record_id RETURNING 
				 recordid INTO _returning_record_id;
		
	RETURN _returning_record_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_record(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 100 (class 1255 OID 174311)
-- Dependencies: 550 6
-- Name: sp_archive_recordattribute(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_recordattribute(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_recordattribute_id ALIAS for $2;
		_returning_recordattribute_id bigint;
	BEGIN
		UPDATE tms.recordattributes set
				 archivedtimestamp = _archivedtimestamp WHERE
				 recordattributeid = _recordattribute_id RETURNING 
				 recordattributeid INTO _returning_recordattribute_id;
		
	RETURN _returning_recordattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_recordattribute(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 101 (class 1255 OID 174312)
-- Dependencies: 550 6
-- Name: sp_archive_synonym(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_synonym(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_synonym_id ALIAS for $2;
		_returning_synonym_id bigint;
	BEGIN
		UPDATE tms.synonyms set
				 archivedtimestamp = _archivedtimestamp WHERE
				 synonymid = _synonym_id RETURNING 
				 synonymid INTO _returning_synonym_id;
		
	RETURN _returning_synonym_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_synonym(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 102 (class 1255 OID 174313)
-- Dependencies: 550 6
-- Name: sp_archive_synonymattribute(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_synonymattribute(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_synonymattribute_id ALIAS for $2;
		_returning_synonymattribute_id bigint;
	BEGIN
		UPDATE tms.synonymattributes set
				 archivedtimestamp = _archivedtimestamp WHERE
				 synonymattributeid = _synonymattribute_id RETURNING 
				 synonymattributeid INTO _returning_synonymattribute_id;
		
	RETURN _returning_synonymattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_synonymattribute(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 103 (class 1255 OID 174314)
-- Dependencies: 6 550
-- Name: sp_archive_term(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_term(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_term_id ALIAS for $2;
		_returning_term_id bigint;
	BEGIN
		UPDATE tms.terms set
				 archivedtimestamp = _archivedtimestamp WHERE
				 termid = _term_id RETURNING 
				 termid INTO _returning_term_id;
		
	RETURN _returning_term_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_term(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 104 (class 1255 OID 174315)
-- Dependencies: 6 550
-- Name: sp_archive_termattribute(timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_archive_termattribute(timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_archivedtimestamp ALIAS for $1;
		_termattribute_id ALIAS for $2;
		_returning_termattribute_id bigint;
	BEGIN
		UPDATE tms.termattributes set
				 archivedtimestamp = _archivedtimestamp WHERE
				 termattributeid = _termattribute_id RETURNING 
				 termattributeid INTO _returning_termattribute_id;
		
	RETURN _returning_termattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_archive_termattribute(timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 105 (class 1255 OID 174316)
-- Dependencies: 6 550
-- Name: sp_delete_record_project(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_delete_record_project(bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_recordproject_id ALIAS for $1;
	BEGIN
		DELETE FROM tms.recordprojects
			WHERE recordid = _recordproject_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_delete_record_project(bigint) OWNER TO postgres;

--
-- TOC entry 106 (class 1255 OID 174317)
-- Dependencies: 550 6
-- Name: sp_delete_user_category_project(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_delete_user_category_project(bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_usercategory_id ALIAS for $1;
	BEGIN
		DELETE FROM tms.usercategoryprojects
			WHERE usercategoryid = _usercategory_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_delete_user_category_project(bigint) OWNER TO postgres;

--
-- TOC entry 107 (class 1255 OID 174318)
-- Dependencies: 550 6
-- Name: sp_delete_user_project(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_delete_user_project(bigint) RETURNS void
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_user_id ALIAS for $1;
	BEGIN
		DELETE FROM tms.userprojects
			WHERE userid = _user_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_delete_user_project(bigint) OWNER TO postgres;

--
-- TOC entry 108 (class 1255 OID 174319)
-- Dependencies: 6 550
-- Name: sp_edit_record_attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_record_attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_recordattribute_id ALIAS for $4;
		_returning_audittraileditrecattr_id bigint;
	BEGIN
		INSERT INTO tms.audittraileditrecordattributes (chardata, canberendered, userid, recordattributeid) values 
							(_chardata, _canberendered, _user_id, _recordattribute_id) RETURNING 
							audittraileditrecattrid INTO _returning_audittraileditrecattr_id;

	RETURN _returning_audittraileditrecattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_record_attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 109 (class 1255 OID 174320)
-- Dependencies: 550 6
-- Name: sp_edit_record_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_record_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_record_id ALIAS for $4;
		_returning_audittraileditrecord_id bigint;
	BEGIN
		INSERT INTO tms.audittraileditrecords (chardata, canberendered, userid, recordid) values 
							(_chardata, _canberendered, _user_id, _record_id) RETURNING 
							audittraileditrecordid INTO _returning_audittraileditrecord_id;

	RETURN _returning_audittraileditrecord_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_record_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 110 (class 1255 OID 174321)
-- Dependencies: 6 550
-- Name: sp_edit_synonym__attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_synonym__attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_synonymattribute_id ALIAS for $4;
		_returning_audittraileditsynonymattr_id bigint;
	BEGIN
		INSERT INTO tms.audittraileditsynonymattributes (chardata, canberendered, userid, synonymattributeid) values 
								  (_chardata, _canberendered, _user_id, _synonymattribute_id) RETURNING 
								  audittraileditsynonymattrid INTO _returning_audittraileditsynonymattr_id;

	RETURN _returning_audittraileditsynonymattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_synonym__attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 111 (class 1255 OID 174322)
-- Dependencies: 6 550
-- Name: sp_edit_synonym_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_synonym_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_synonym_id ALIAS for $4;
		_returning_audittraileditsynonym_id bigint;
	BEGIN
		INSERT INTO tms.audittraileditsynonyms (chardata, canberendered, userid, synonymid) values 
							(_chardata, _canberendered, _user_id, _synonym_id) RETURNING 
							audittraileditsynonymid INTO _returning_audittraileditsynonym_id;

	RETURN _returning_audittraileditsynonym_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_synonym_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 112 (class 1255 OID 174323)
-- Dependencies: 550 6
-- Name: sp_edit_term_attribute_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_term_attribute_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_termattribute_id ALIAS for $4;
		_returning_audittrailedittermattr_id bigint;
	BEGIN
		INSERT INTO tms.audittrailedittermattributes (chardata, canberendered, userid, termattributeid) values 
							(_chardata, _canberendered, _user_id, _termattribute_id) RETURNING 
							audittrailedittermattrid INTO _returning_audittrailedittermattr_id;

	RETURN _returning_audittrailedittermattr_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_term_attribute_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 113 (class 1255 OID 174324)
-- Dependencies: 6 550
-- Name: sp_edit_term_audit(character varying, boolean, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_edit_term_audit(character varying, boolean, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_canberendered ALIAS for $2;
		_user_id ALIAS for $3;
		_term_id ALIAS for $4;
		_returning_audittraileditterm_id bigint;
	BEGIN
		INSERT INTO tms.audittraileditterms (chardata, canberendered, userid, termid) values 
							(_chardata, _canberendered, _user_id, _term_id) RETURNING 
							audittrailedittermid INTO _returning_audittraileditterm_id;

	RETURN _returning_audittraileditterm_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_edit_term_audit(character varying, boolean, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 114 (class 1255 OID 174325)
-- Dependencies: 6 550
-- Name: sp_generic_return_ref(text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_generic_return_ref(text) RETURNS refcursor
    LANGUAGE plpgsql IMMUTABLE
    AS $_$      
	DECLARE
		sql ALIAS for $1;
		ref refcursor;                                         
	BEGIN
		OPEN ref FOR EXECUTE sql;

	RETURN ref; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_generic_return_ref(text) OWNER TO postgres;

--
-- TOC entry 115 (class 1255 OID 174326)
-- Dependencies: 6
-- Name: sp_get_user_passwd(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_get_user_passwd(bigint) RETURNS text
    LANGUAGE sql STABLE
    AS $_$
SELECT passwd from tms.users where userid = $1;
$_$;


ALTER FUNCTION tms.sp_get_user_passwd(bigint) OWNER TO postgres;

--
-- TOC entry 116 (class 1255 OID 174327)
-- Dependencies: 6 550
-- Name: sp_lock_record(bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_lock_record(bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_user_id ALIAS for $1;
		_record_id ALIAS for $2;
		_returning_record_id bigint;
	BEGIN
		UPDATE tms.records set
				 beingeditedby = _user_id where
				 recordid = _record_id RETURNING 
				 recordid INTO _returning_record_id;

	RETURN _returning_record_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_lock_record(bigint, bigint) OWNER TO postgres;

--
-- TOC entry 117 (class 1255 OID 174328)
-- Dependencies: 550 6
-- Name: sp_unlock_all_records(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_unlock_all_records(bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_user_id ALIAS for $1;
		_returning_record_id bigint;
	BEGIN
		UPDATE tms.records set
				 beingeditedby = null where
				 beingeditedby = _user_id RETURNING 
				 recordid INTO _returning_record_id;

	RETURN _returning_record_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_unlock_all_records(bigint) OWNER TO postgres;

--
-- TOC entry 118 (class 1255 OID 174329)
-- Dependencies: 550 6
-- Name: sp_unlock_record(bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_unlock_record(bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_record_id ALIAS for $1;
		_returning_record_id bigint;
	BEGIN
		UPDATE tms.records set
				 beingeditedby = null where
				 recordid = _record_id RETURNING 
				 recordid INTO _returning_record_id;

	RETURN _returning_record_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_unlock_record(bigint) OWNER TO postgres;

--
-- TOC entry 119 (class 1255 OID 174330)
-- Dependencies: 6 550
-- Name: sp_update_field(text, integer, integer, integer, text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_field(text, integer, integer, integer, text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_field_name ALIAS for $1;
		_fieldtype_id ALIAS for $2;
		_field_data_type ALIAS for $3;
		_max_length ALIAS for $4;
		_default_value ALIAS for $5;
		_field_id ALIAS for $6;
		_returning_field_id bigint;
	BEGIN
		UPDATE tms.fields set
				 fieldname = _field_name,
				 fieldtypeid = _fieldtype_id,
				 fielddatatypeid = _field_data_type,
				 maxlength = _max_length,
				 defaultvalue = _default_value where
				 fieldid = _field_id RETURNING 
				 fieldid INTO _returning_field_id;
				 
		RETURN _returning_field_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_field(text, integer, integer, integer, text, bigint) OWNER TO postgres;

--
-- TOC entry 120 (class 1255 OID 174331)
-- Dependencies: 550 6
-- Name: sp_update_presetfield(text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_presetfield(text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_presetfield_name ALIAS for $1;
		_presetfield_id ALIAS for $2;
		_returning_presetfield_id bigint;
	BEGIN
		UPDATE tms.presetfields set
				 presetfieldname = _presetfield_name where
				 presetfieldid = _presetfield_id RETURNING 
				 presetfieldid INTO _returning_presetfield_id;
				 
		RETURN _returning_presetfield_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_presetfield(text, bigint) OWNER TO postgres;

--
-- TOC entry 121 (class 1255 OID 174332)
-- Dependencies: 550 6
-- Name: sp_update_project(text, timestamp with time zone, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_project(text, timestamp with time zone, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE		
		_project_name ALIAS for $1;
		_date_last_update ALIAS for $2;
		_project_id ALIAS for $3;		
		_returning_project_id bigint;
	BEGIN
		UPDATE tms.projects set
				 projectname = _project_name,
				 datetimelastupdated = _date_last_update where
				 projectid = _project_id RETURNING 
				 projectid INTO _returning_project_id;

		RETURN _returning_project_id;
			                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_project(text, timestamp with time zone, bigint) OWNER TO postgres;

--
-- TOC entry 122 (class 1255 OID 174333)
-- Dependencies: 6 550
-- Name: sp_update_record_attribute(character varying, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_record_attribute(character varying, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_record_id ALIAS for $2;
		_field_id ALIAS for $3;
		_recordattribute_id ALIAS for $4;
		_returning_recordattribute_id bigint;
	BEGIN
		UPDATE tms.recordattributes set
				 chardata = _chardata,
				 recordid = _record_id,
				 fieldid =  _field_id where
				 recordattributeid = _recordattribute_id RETURNING 
				 recordattributeid INTO _returning_recordattribute_id;
		
	RETURN _returning_recordattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_record_attribute(character varying, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 123 (class 1255 OID 174334)
-- Dependencies: 6 550
-- Name: sp_update_synonym(character varying, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_synonym(character varying, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_term_id ALIAS for $2;
		_field_id ALIAS for $3;
		_synonym_id ALIAS for $4;
		_returning_synonym_id bigint;
	BEGIN
		UPDATE tms.synonyms set
				 chardata = _chardata,
				 termid = _term_id,
				 fieldid =  _field_id where
				 synonymid = _synonym_id RETURNING 
				 synonymid INTO _returning_synonym_id;
		
	RETURN _returning_synonym_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_synonym(character varying, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 124 (class 1255 OID 174335)
-- Dependencies: 6 550
-- Name: sp_update_synonym_attribute(character varying, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_synonym_attribute(character varying, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_synonym_id ALIAS for $2;
		_field_id ALIAS for $3;
		_synonymattribute_id ALIAS for $4;
		_returning_synonymattribute_id bigint;
	BEGIN
		UPDATE tms.synonymattributes set
				 chardata = _chardata,
				 synonymid = _synonym_id,
				 fieldid =  _field_id where
				 synonymattributeid = _synonymattribute_id RETURNING 
				 synonymattributeid INTO _returning_synonymattribute_id;
		
	RETURN _returning_synonymattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_synonym_attribute(character varying, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 125 (class 1255 OID 174336)
-- Dependencies: 6 550
-- Name: sp_update_term(character varying, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_term(character varying, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_record_id ALIAS for $2;
		_field_id ALIAS for $3;
		_termid_id ALIAS for $4;
		_returning_termid_id bigint;
	BEGIN
		UPDATE tms.terms set
				 chardata = _chardata,
				 recordid = _record_id,
				 fieldid =  _field_id where
				 termid = _termid_id RETURNING 
				 termid INTO _returning_termid_id;
		
	RETURN _returning_termid_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_term(character varying, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 126 (class 1255 OID 174337)
-- Dependencies: 6 550
-- Name: sp_update_term_attribute(character varying, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_term_attribute(character varying, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_chardata ALIAS for $1;
		_term_id ALIAS for $2;
		_field_id ALIAS for $3;
		_termattribute_id ALIAS for $4;
		_returning_termattribute_id bigint;
	BEGIN
		UPDATE tms.termattributes set
				 chardata = _chardata,
				 termid = _term_id,
				 fieldid =  _field_id where
				 termattributeid = _termattribute_id RETURNING 
				 termattributeid INTO _returning_termattribute_id;
		
	RETURN _returning_termattribute_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_term_attribute(character varying, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 127 (class 1255 OID 174338)
-- Dependencies: 6 550
-- Name: sp_update_termbase(text, bigint, timestamp with time zone, text, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_termbase(text, bigint, timestamp with time zone, text, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE		
		_termbase_name ALIAS for $1;
		_user_id ALIAS for $2;
		_date_last_update ALIAS for $3;
		_email ALIAS for $4;
		_termbase_id ALIAS for $5;
		_returning_termbase_id bigint;
	BEGIN
		UPDATE tms.termbases set
				 termbasename = _termbase_name,
				 userid = _user_id,
				 datetimelastupdated = _date_last_update,
				 adminemail = _email where
				 termbaseid = _termbase_id RETURNING 
				 termbaseid INTO _returning_termbase_id;

		RETURN _returning_termbase_id;
			                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_termbase(text, bigint, timestamp with time zone, text, bigint) OWNER TO postgres;

--
-- TOC entry 128 (class 1255 OID 174339)
-- Dependencies: 550 6
-- Name: sp_update_user(text, text, text, text, boolean, date, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user(text, text, text, text, boolean, date, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_username ALIAS for $1;
		_passwd ALIAS for $2;
		_firstname ALIAS for $3;
		_lastname ALIAS for $4;
		_activated ALIAS for $5;
		_expirydate ALIAS for $6;
		_usercategoryid ALIAS for $7;
		_user_id ALIAS for $8;
		_returning_user_id bigint;
	BEGIN
		UPDATE tms.users set 
						username = _username,	
						passwd  = _passwd,
						firstname = _firstname,
						lastname = _lastname,
						activated = _activated,
						expirydate = _expirydate,
						usercategoryid = _usercategoryid where 
						userid = _user_id RETURNING 
						userid INTO _returning_user_id;

	RETURN _returning_user_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user(text, text, text, text, boolean, date, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 129 (class 1255 OID 174340)
-- Dependencies: 550 6
-- Name: sp_update_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_field_id ALIAS for $5;
		_userid ALIAS for $6;
		_accessrightuser_id ALIAS for $7;
		_returning_accessrightuser_id bigint;
	BEGIN
		UPDATE tms.accessrightsuser set
				 mayread = _mayread,
				 mayupdate =  _mayupdate,
				 maydelete = _maydelete,
				 mayexport = _mayexport,
				 fieldid =  _field_id,
				 userid = _userid where
				 accessrightuserid = _accessrightuser_id RETURNING 
				 accessrightuserid INTO _returning_accessrightuser_id;
				 
		RETURN _returning_accessrightuser_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 130 (class 1255 OID 174341)
-- Dependencies: 550 6
-- Name: sp_update_user_category(text, boolean, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user_category(text, boolean, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_usercategory ALIAS for $1;
		_isadmin ALIAS for $2;
		_usercategory_id ALIAS for $3;
		_returning_user_cat_id bigint;
	BEGIN
		UPDATE tms.usercategories set 
									usercategory = _usercategory,
									isadmin = _isadmin where 
									usercategoryid = _usercategory_id RETURNING
									usercategoryid INTO _returning_user_cat_id;

	RETURN _returning_user_cat_id; 
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user_category(text, boolean, bigint) OWNER TO postgres;

--
-- TOC entry 131 (class 1255 OID 174342)
-- Dependencies: 6 550
-- Name: sp_update_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_field_id ALIAS for $5;
		_user_category_id ALIAS for $6;
		_accessrightusercategory_id ALIAS for $7;
		_returning_accessrightusercategory_id bigint;
	BEGIN
		UPDATE tms.accessrightsusercategory set
				 mayread = _mayread,
				 mayupdate =  _mayupdate,
				 maydelete = _maydelete,
				 mayexport = _mayexport,
				 fieldid =  _field_id,
				 usercategoryid = _user_category_id where
				 accessrightusercategoryid = _accessrightusercategory_id RETURNING 
				 accessrightusercategoryid INTO _returning_accessrightusercategory_id;
				 
		RETURN _returning_accessrightusercategory_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user_category_accessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 132 (class 1255 OID 174343)
-- Dependencies: 550 6
-- Name: sp_update_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_accessrightsuser_id ALIAS for $5;
		_field_id ALIAS for $6;
		_childaccessrightuser_id ALIAS for $7;
		_returning_childaccessrightuser_id bigint;
	BEGIN
		UPDATE tms.childaccessrightsuser set
				 mayread = _mayread,
				 mayupdate =  _mayupdate,
				 maydelete = _maydelete,
				 mayexport = _mayexport,
				 accessrightsuserid = _accessrightsuser_id,
				 fieldid = _field_id where
				 childaccessrightuserid = _childaccessrightuser_id RETURNING 
				 childaccessrightuserid INTO _returning_childaccessrightuser_id;
				 
		RETURN _returning_childaccessrightuser_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 133 (class 1255 OID 174344)
-- Dependencies: 550 6
-- Name: sp_update_user_last_signon(bigint, timestamp with time zone); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_user_last_signon(bigint, timestamp with time zone) RETURNS void
    LANGUAGE plpgsql
    AS $_$      
	DECLARE		
		_user_id ALIAS for $1;	
		_last_signon ALIAS for $2;	
	BEGIN
		UPDATE tms.users set
				 lastsignon = _last_signon where
				 userid = _user_id;
			                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_user_last_signon(bigint, timestamp with time zone) OWNER TO postgres;

--
-- TOC entry 134 (class 1255 OID 174345)
-- Dependencies: 550 6
-- Name: sp_update_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION sp_update_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) RETURNS bigint
    LANGUAGE plpgsql
    AS $_$      
	DECLARE
		_mayread ALIAS for $1;
		_mayupdate ALIAS for $2;
		_mayexport ALIAS for $3;
		_maydelete ALIAS for $4;
		_accessrightsusecategory_id ALIAS for  $5;
		_field_id ALIAS for $6;
		_childaccessrightusercategory_id ALIAS for $7;
		_returning_childaccessrightusercategory_id bigint;
	BEGIN
		UPDATE tms.childaccessrightsusercategory set
				 mayread = _mayread,
				 mayupdate =  _mayupdate,
				 maydelete = _maydelete,
				 mayexport = _mayexport,
				 accessrightsusecategoryid = _accessrightsusecategory_id,
				 fieldid = _field_id where
				 childaccessrightusercategoryid = _childaccessrightusercategory_id RETURNING 
				 childaccessrightusercategoryid INTO _returning_childaccessrightusercategory_id;
				 
		RETURN _returning_childaccessrightusercategory_id;
	                                                      
	END;
	
	$_$;


ALTER FUNCTION tms.sp_update_usercategory_childaccessright(boolean, boolean, boolean, boolean, bigint, bigint, bigint) OWNER TO postgres;

--
-- TOC entry 135 (class 1255 OID 174346)
-- Dependencies: 6
-- Name: text_soundex(text); Type: FUNCTION; Schema: tms; Owner: postgres
--

CREATE FUNCTION text_soundex(text) RETURNS text
    LANGUAGE c IMMUTABLE STRICT
    AS '$libdir/fuzzystrmatch', 'soundex';


ALTER FUNCTION tms.text_soundex(text) OWNER TO postgres;

--
-- TOC entry 1756 (class 1259 OID 174347)
-- Dependencies: 6
-- Name: accessrightsusercategory; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE accessrightsusercategory (
    accessrightusercategoryid bigint NOT NULL,
    mayread boolean NOT NULL,
    mayupdate boolean NOT NULL,
    maydelete boolean NOT NULL,
    mayexport boolean NOT NULL,
    fieldid bigint NOT NULL,
    usercategoryid bigint NOT NULL
);


ALTER TABLE tms.accessrightsusercategory OWNER TO postgres;

--
-- TOC entry 1757 (class 1259 OID 174350)
-- Dependencies: 1756 6
-- Name: AccessRightsUserCategory_accessrightusercategoryid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AccessRightsUserCategory_accessrightusercategoryid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AccessRightsUserCategory_accessrightusercategoryid_seq" OWNER TO postgres;

--
-- TOC entry 2384 (class 0 OID 0)
-- Dependencies: 1757
-- Name: AccessRightsUserCategory_accessrightusercategoryid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AccessRightsUserCategory_accessrightusercategoryid_seq" OWNED BY accessrightsusercategory.accessrightusercategoryid;


--
-- TOC entry 2385 (class 0 OID 0)
-- Dependencies: 1757
-- Name: AccessRightsUserCategory_accessrightusercategoryid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AccessRightsUserCategory_accessrightusercategoryid_seq"', 1, false);


--
-- TOC entry 1758 (class 1259 OID 174352)
-- Dependencies: 6
-- Name: accessrightsuser; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE accessrightsuser (
    accessrightuserid bigint NOT NULL,
    mayread boolean NOT NULL,
    mayupdate boolean NOT NULL,
    maydelete boolean NOT NULL,
    mayexport boolean NOT NULL,
    fieldid bigint NOT NULL,
    userid bigint NOT NULL
);


ALTER TABLE tms.accessrightsuser OWNER TO postgres;

--
-- TOC entry 1759 (class 1259 OID 174355)
-- Dependencies: 1758 6
-- Name: AccessRightsUser_accessrightuserid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AccessRightsUser_accessrightuserid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AccessRightsUser_accessrightuserid_seq" OWNER TO postgres;

--
-- TOC entry 2386 (class 0 OID 0)
-- Dependencies: 1759
-- Name: AccessRightsUser_accessrightuserid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AccessRightsUser_accessrightuserid_seq" OWNED BY accessrightsuser.accessrightuserid;


--
-- TOC entry 2387 (class 0 OID 0)
-- Dependencies: 1759
-- Name: AccessRightsUser_accessrightuserid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AccessRightsUser_accessrightuserid_seq"', 1, false);


--
-- TOC entry 1760 (class 1259 OID 174357)
-- Dependencies: 2114 2115 6
-- Name: audittrailtcreaterecordattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailtcreaterecordattributes (
    audittrailcreaterecordattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    recordattributeid bigint NOT NULL
);


ALTER TABLE tms.audittrailtcreaterecordattributes OWNER TO postgres;

--
-- TOC entry 1761 (class 1259 OID 174365)
-- Dependencies: 1760 6
-- Name: AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq" OWNER TO postgres;

--
-- TOC entry 2388 (class 0 OID 0)
-- Dependencies: 1761
-- Name: AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq" OWNED BY audittrailtcreaterecordattributes.audittrailcreaterecordattrid;


--
-- TOC entry 2389 (class 0 OID 0)
-- Dependencies: 1761
-- Name: AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq"', 1, false);


--
-- TOC entry 1762 (class 1259 OID 174367)
-- Dependencies: 1750 6
-- Name: AuditTrailCreateRecords_audittrailcreaterecordid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateRecords_audittrailcreaterecordid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateRecords_audittrailcreaterecordid_seq" OWNER TO postgres;

--
-- TOC entry 2390 (class 0 OID 0)
-- Dependencies: 1762
-- Name: AuditTrailCreateRecords_audittrailcreaterecordid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateRecords_audittrailcreaterecordid_seq" OWNED BY audittrailcreaterecords.audittrailcreaterecordid;


--
-- TOC entry 2391 (class 0 OID 0)
-- Dependencies: 1762
-- Name: AuditTrailCreateRecords_audittrailcreaterecordid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateRecords_audittrailcreaterecordid_seq"', 1, false);


--
-- TOC entry 1763 (class 1259 OID 174369)
-- Dependencies: 2117 2118 6
-- Name: audittrailcreatesynonymattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailcreatesynonymattributes (
    audittrailcreatesynonymattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    synonymattributeid bigint NOT NULL
);


ALTER TABLE tms.audittrailcreatesynonymattributes OWNER TO postgres;

--
-- TOC entry 1764 (class 1259 OID 174377)
-- Dependencies: 1763 6
-- Name: AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq" OWNER TO postgres;

--
-- TOC entry 2392 (class 0 OID 0)
-- Dependencies: 1764
-- Name: AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq" OWNED BY audittrailcreatesynonymattributes.audittrailcreatesynonymattrid;


--
-- TOC entry 2393 (class 0 OID 0)
-- Dependencies: 1764
-- Name: AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq"', 1, false);


--
-- TOC entry 1765 (class 1259 OID 174379)
-- Dependencies: 2120 2121 6
-- Name: audittrailcreatesynonyms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailcreatesynonyms (
    audittrailcreatesynonymid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    synonymid bigint NOT NULL
);


ALTER TABLE tms.audittrailcreatesynonyms OWNER TO postgres;

--
-- TOC entry 1766 (class 1259 OID 174387)
-- Dependencies: 1765 6
-- Name: AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq" OWNER TO postgres;

--
-- TOC entry 2394 (class 0 OID 0)
-- Dependencies: 1766
-- Name: AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq" OWNED BY audittrailcreatesynonyms.audittrailcreatesynonymid;


--
-- TOC entry 2395 (class 0 OID 0)
-- Dependencies: 1766
-- Name: AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq"', 1, false);


--
-- TOC entry 1767 (class 1259 OID 174389)
-- Dependencies: 2123 2124 6
-- Name: audittrailcreatetermattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailcreatetermattributes (
    audittrailcreatetermattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    termattributeid bigint NOT NULL
);


ALTER TABLE tms.audittrailcreatetermattributes OWNER TO postgres;

--
-- TOC entry 1768 (class 1259 OID 174397)
-- Dependencies: 6 1767
-- Name: AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq" OWNER TO postgres;

--
-- TOC entry 2396 (class 0 OID 0)
-- Dependencies: 1768
-- Name: AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq" OWNED BY audittrailcreatetermattributes.audittrailcreatetermattrid;


--
-- TOC entry 2397 (class 0 OID 0)
-- Dependencies: 1768
-- Name: AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq"', 1, false);


--
-- TOC entry 1769 (class 1259 OID 174399)
-- Dependencies: 1743 6
-- Name: AuditTrailCreateTerms_audittrailcreatetermid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailCreateTerms_audittrailcreatetermid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailCreateTerms_audittrailcreatetermid_seq" OWNER TO postgres;

--
-- TOC entry 2398 (class 0 OID 0)
-- Dependencies: 1769
-- Name: AuditTrailCreateTerms_audittrailcreatetermid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailCreateTerms_audittrailcreatetermid_seq" OWNED BY audittrailcreateterms.audittrailcreatetermid;


--
-- TOC entry 2399 (class 0 OID 0)
-- Dependencies: 1769
-- Name: AuditTrailCreateTerms_audittrailcreatetermid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailCreateTerms_audittrailcreatetermid_seq"', 1, false);


--
-- TOC entry 1770 (class 1259 OID 174401)
-- Dependencies: 2126 2127 6
-- Name: audittraileditrecordattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittraileditrecordattributes (
    audittraileditrecattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    recordattributeid bigint NOT NULL
);


ALTER TABLE tms.audittraileditrecordattributes OWNER TO postgres;

--
-- TOC entry 1771 (class 1259 OID 174409)
-- Dependencies: 6 1770
-- Name: AuditTrailEditRecordAttributes_audittraileditrecattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditRecordAttributes_audittraileditrecattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditRecordAttributes_audittraileditrecattrid_seq" OWNER TO postgres;

--
-- TOC entry 2400 (class 0 OID 0)
-- Dependencies: 1771
-- Name: AuditTrailEditRecordAttributes_audittraileditrecattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditRecordAttributes_audittraileditrecattrid_seq" OWNED BY audittraileditrecordattributes.audittraileditrecattrid;


--
-- TOC entry 2401 (class 0 OID 0)
-- Dependencies: 1771
-- Name: AuditTrailEditRecordAttributes_audittraileditrecattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditRecordAttributes_audittraileditrecattrid_seq"', 1, false);


--
-- TOC entry 1772 (class 1259 OID 174411)
-- Dependencies: 6 1751
-- Name: AuditTrailEditRecords_audittraileditrecordid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditRecords_audittraileditrecordid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditRecords_audittraileditrecordid_seq" OWNER TO postgres;

--
-- TOC entry 2402 (class 0 OID 0)
-- Dependencies: 1772
-- Name: AuditTrailEditRecords_audittraileditrecordid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditRecords_audittraileditrecordid_seq" OWNED BY audittraileditrecords.audittraileditrecordid;


--
-- TOC entry 2403 (class 0 OID 0)
-- Dependencies: 1772
-- Name: AuditTrailEditRecords_audittraileditrecordid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditRecords_audittraileditrecordid_seq"', 1, false);


--
-- TOC entry 1773 (class 1259 OID 174413)
-- Dependencies: 2129 2130 6
-- Name: audittraileditsynonymattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittraileditsynonymattributes (
    audittraileditsynonymattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    synonymattributeid bigint NOT NULL
);


ALTER TABLE tms.audittraileditsynonymattributes OWNER TO postgres;

--
-- TOC entry 1774 (class 1259 OID 174421)
-- Dependencies: 1773 6
-- Name: AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq" OWNER TO postgres;

--
-- TOC entry 2404 (class 0 OID 0)
-- Dependencies: 1774
-- Name: AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq" OWNED BY audittraileditsynonymattributes.audittraileditsynonymattrid;


--
-- TOC entry 2405 (class 0 OID 0)
-- Dependencies: 1774
-- Name: AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq"', 1, false);


--
-- TOC entry 1775 (class 1259 OID 174423)
-- Dependencies: 2132 2133 6
-- Name: audittraileditsynonyms; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittraileditsynonyms (
    audittraileditsynonymid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    synonymid bigint NOT NULL
);


ALTER TABLE tms.audittraileditsynonyms OWNER TO postgres;

--
-- TOC entry 1776 (class 1259 OID 174431)
-- Dependencies: 1775 6
-- Name: AuditTrailEditSynonyms_audittraileditsynonymid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditSynonyms_audittraileditsynonymid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditSynonyms_audittraileditsynonymid_seq" OWNER TO postgres;

--
-- TOC entry 2406 (class 0 OID 0)
-- Dependencies: 1776
-- Name: AuditTrailEditSynonyms_audittraileditsynonymid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditSynonyms_audittraileditsynonymid_seq" OWNED BY audittraileditsynonyms.audittraileditsynonymid;


--
-- TOC entry 2407 (class 0 OID 0)
-- Dependencies: 1776
-- Name: AuditTrailEditSynonyms_audittraileditsynonymid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditSynonyms_audittraileditsynonymid_seq"', 1, false);


--
-- TOC entry 1777 (class 1259 OID 174433)
-- Dependencies: 2135 2136 6
-- Name: audittrailedittermattributes; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE audittrailedittermattributes (
    audittrailedittermattrid bigint NOT NULL,
    auditdatetime timestamp with time zone DEFAULT now() NOT NULL,
    chardata character varying NOT NULL,
    canberendered boolean DEFAULT true NOT NULL,
    userid bigint NOT NULL,
    termattributeid bigint NOT NULL
);


ALTER TABLE tms.audittrailedittermattributes OWNER TO postgres;

--
-- TOC entry 1778 (class 1259 OID 174441)
-- Dependencies: 6 1777
-- Name: AuditTrailEditTermAttributes_audittrailedittermattrid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditTermAttributes_audittrailedittermattrid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditTermAttributes_audittrailedittermattrid_seq" OWNER TO postgres;

--
-- TOC entry 2408 (class 0 OID 0)
-- Dependencies: 1778
-- Name: AuditTrailEditTermAttributes_audittrailedittermattrid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditTermAttributes_audittrailedittermattrid_seq" OWNED BY audittrailedittermattributes.audittrailedittermattrid;


--
-- TOC entry 2409 (class 0 OID 0)
-- Dependencies: 1778
-- Name: AuditTrailEditTermAttributes_audittrailedittermattrid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditTermAttributes_audittrailedittermattrid_seq"', 1, false);


--
-- TOC entry 1779 (class 1259 OID 174443)
-- Dependencies: 1745 6
-- Name: AuditTrailEditTerms_audittrailedittermid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "AuditTrailEditTerms_audittrailedittermid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."AuditTrailEditTerms_audittrailedittermid_seq" OWNER TO postgres;

--
-- TOC entry 2410 (class 0 OID 0)
-- Dependencies: 1779
-- Name: AuditTrailEditTerms_audittrailedittermid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "AuditTrailEditTerms_audittrailedittermid_seq" OWNED BY audittraileditterms.audittrailedittermid;


--
-- TOC entry 2411 (class 0 OID 0)
-- Dependencies: 1779
-- Name: AuditTrailEditTerms_audittrailedittermid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"AuditTrailEditTerms_audittrailedittermid_seq"', 1, false);


--
-- TOC entry 1780 (class 1259 OID 174445)
-- Dependencies: 6 1741
-- Name: Fields_fieldid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Fields_fieldid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Fields_fieldid_seq" OWNER TO postgres;

--
-- TOC entry 2412 (class 0 OID 0)
-- Dependencies: 1780
-- Name: Fields_fieldid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Fields_fieldid_seq" OWNED BY fields.fieldid;


--
-- TOC entry 2413 (class 0 OID 0)
-- Dependencies: 1780
-- Name: Fields_fieldid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Fields_fieldid_seq"', 60, false);


--
-- TOC entry 1781 (class 1259 OID 174447)
-- Dependencies: 6
-- Name: presetfields; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE presetfields (
    presetfieldid bigint NOT NULL,
    presetfieldname character varying(48) NOT NULL,
    fieldid bigint NOT NULL
);


ALTER TABLE tms.presetfields OWNER TO postgres;

--
-- TOC entry 1782 (class 1259 OID 174450)
-- Dependencies: 6 1781
-- Name: PresetFields_presetfieldid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "PresetFields_presetfieldid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."PresetFields_presetfieldid_seq" OWNER TO postgres;

--
-- TOC entry 2414 (class 0 OID 0)
-- Dependencies: 1782
-- Name: PresetFields_presetfieldid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "PresetFields_presetfieldid_seq" OWNED BY presetfields.presetfieldid;


--
-- TOC entry 2415 (class 0 OID 0)
-- Dependencies: 1782
-- Name: PresetFields_presetfieldid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"PresetFields_presetfieldid_seq"', 134, false);


--
-- TOC entry 1783 (class 1259 OID 174452)
-- Dependencies: 2139 6
-- Name: projects; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE projects (
    projectid bigint NOT NULL,
    projectname character varying NOT NULL,
    datetimecreated timestamp with time zone DEFAULT now() NOT NULL,
    datetimelastupdated timestamp with time zone,
    termbaseid bigint NOT NULL,
    archivedtimestamp timestamp with time zone
);


ALTER TABLE tms.projects OWNER TO postgres;

--
-- TOC entry 1784 (class 1259 OID 174459)
-- Dependencies: 1783 6
-- Name: Projects_projectid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Projects_projectid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Projects_projectid_seq" OWNER TO postgres;

--
-- TOC entry 2416 (class 0 OID 0)
-- Dependencies: 1784
-- Name: Projects_projectid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Projects_projectid_seq" OWNED BY projects.projectid;


--
-- TOC entry 2417 (class 0 OID 0)
-- Dependencies: 1784
-- Name: Projects_projectid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Projects_projectid_seq"', 1, false);


--
-- TOC entry 1785 (class 1259 OID 174461)
-- Dependencies: 6 1748
-- Name: RecordAttributes_recordattributeid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "RecordAttributes_recordattributeid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."RecordAttributes_recordattributeid_seq" OWNER TO postgres;

--
-- TOC entry 2418 (class 0 OID 0)
-- Dependencies: 1785
-- Name: RecordAttributes_recordattributeid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "RecordAttributes_recordattributeid_seq" OWNED BY recordattributes.recordattributeid;


--
-- TOC entry 2419 (class 0 OID 0)
-- Dependencies: 1785
-- Name: RecordAttributes_recordattributeid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"RecordAttributes_recordattributeid_seq"', 1, false);


--
-- TOC entry 1786 (class 1259 OID 174463)
-- Dependencies: 6 1747
-- Name: RecordProjects_recordprojectid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "RecordProjects_recordprojectid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."RecordProjects_recordprojectid_seq" OWNER TO postgres;

--
-- TOC entry 2420 (class 0 OID 0)
-- Dependencies: 1786
-- Name: RecordProjects_recordprojectid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "RecordProjects_recordprojectid_seq" OWNED BY recordprojects.recordprojectid;


--
-- TOC entry 2421 (class 0 OID 0)
-- Dependencies: 1786
-- Name: RecordProjects_recordprojectid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"RecordProjects_recordprojectid_seq"', 1, false);


--
-- TOC entry 1787 (class 1259 OID 174465)
-- Dependencies: 1737 6
-- Name: Records_recordid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Records_recordid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Records_recordid_seq" OWNER TO postgres;

--
-- TOC entry 2422 (class 0 OID 0)
-- Dependencies: 1787
-- Name: Records_recordid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Records_recordid_seq" OWNED BY records.recordid;


--
-- TOC entry 2423 (class 0 OID 0)
-- Dependencies: 1787
-- Name: Records_recordid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Records_recordid_seq"', 1, false);


--
-- TOC entry 1788 (class 1259 OID 174467)
-- Dependencies: 6 1752
-- Name: SynonymAttributes_synonymattributeid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "SynonymAttributes_synonymattributeid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."SynonymAttributes_synonymattributeid_seq" OWNER TO postgres;

--
-- TOC entry 2424 (class 0 OID 0)
-- Dependencies: 1788
-- Name: SynonymAttributes_synonymattributeid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "SynonymAttributes_synonymattributeid_seq" OWNED BY synonymattributes.synonymattributeid;


--
-- TOC entry 2425 (class 0 OID 0)
-- Dependencies: 1788
-- Name: SynonymAttributes_synonymattributeid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"SynonymAttributes_synonymattributeid_seq"', 1, false);


--
-- TOC entry 1789 (class 1259 OID 174469)
-- Dependencies: 1753 6
-- Name: Synonyms_synonymid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Synonyms_synonymid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Synonyms_synonymid_seq" OWNER TO postgres;

--
-- TOC entry 2426 (class 0 OID 0)
-- Dependencies: 1789
-- Name: Synonyms_synonymid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Synonyms_synonymid_seq" OWNED BY synonyms.synonymid;


--
-- TOC entry 2427 (class 0 OID 0)
-- Dependencies: 1789
-- Name: Synonyms_synonymid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Synonyms_synonymid_seq"', 1, false);


--
-- TOC entry 1790 (class 1259 OID 174471)
-- Dependencies: 1738 6
-- Name: TermAttributes_termattributeid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "TermAttributes_termattributeid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."TermAttributes_termattributeid_seq" OWNER TO postgres;

--
-- TOC entry 2428 (class 0 OID 0)
-- Dependencies: 1790
-- Name: TermAttributes_termattributeid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "TermAttributes_termattributeid_seq" OWNED BY termattributes.termattributeid;


--
-- TOC entry 2429 (class 0 OID 0)
-- Dependencies: 1790
-- Name: TermAttributes_termattributeid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"TermAttributes_termattributeid_seq"', 1, false);


--
-- TOC entry 1791 (class 1259 OID 174473)
-- Dependencies: 2141 6
-- Name: termbases; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE termbases (
    termbaseid bigint NOT NULL,
    termbasename character varying(100) NOT NULL,
    userid bigint NOT NULL,
    datetimecreated timestamp with time zone DEFAULT now() NOT NULL,
    datetimelastupdated timestamp with time zone,
    adminemail text NOT NULL,
    archivedtimestamp timestamp with time zone
);


ALTER TABLE tms.termbases OWNER TO postgres;

--
-- TOC entry 1792 (class 1259 OID 174480)
-- Dependencies: 6 1791
-- Name: TermBases_termbaseid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "TermBases_termbaseid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."TermBases_termbaseid_seq" OWNER TO postgres;

--
-- TOC entry 2430 (class 0 OID 0)
-- Dependencies: 1792
-- Name: TermBases_termbaseid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "TermBases_termbaseid_seq" OWNED BY termbases.termbaseid;


--
-- TOC entry 2431 (class 0 OID 0)
-- Dependencies: 1792
-- Name: TermBases_termbaseid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"TermBases_termbaseid_seq"', 1, false);


--
-- TOC entry 1793 (class 1259 OID 174482)
-- Dependencies: 1739 6
-- Name: Terms_termid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Terms_termid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Terms_termid_seq" OWNER TO postgres;

--
-- TOC entry 2432 (class 0 OID 0)
-- Dependencies: 1793
-- Name: Terms_termid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Terms_termid_seq" OWNED BY terms.termid;


--
-- TOC entry 2433 (class 0 OID 0)
-- Dependencies: 1793
-- Name: Terms_termid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Terms_termid_seq"', 1, false);


--
-- TOC entry 1794 (class 1259 OID 174484)
-- Dependencies: 2143 6
-- Name: usercategories; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE usercategories (
    usercategoryid bigint NOT NULL,
    usercategory character varying(100) NOT NULL,
    isadmin boolean DEFAULT false NOT NULL,
    archivedtimestamp timestamp with time zone
);


ALTER TABLE tms.usercategories OWNER TO postgres;

--
-- TOC entry 1795 (class 1259 OID 174488)
-- Dependencies: 1794 6
-- Name: UserCategories_usercategoryid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "UserCategories_usercategoryid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."UserCategories_usercategoryid_seq" OWNER TO postgres;

--
-- TOC entry 2434 (class 0 OID 0)
-- Dependencies: 1795
-- Name: UserCategories_usercategoryid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "UserCategories_usercategoryid_seq" OWNED BY usercategories.usercategoryid;


--
-- TOC entry 2435 (class 0 OID 0)
-- Dependencies: 1795
-- Name: UserCategories_usercategoryid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"UserCategories_usercategoryid_seq"', 4, false);


--
-- TOC entry 1796 (class 1259 OID 174490)
-- Dependencies: 2145 6
-- Name: users; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    userid bigint NOT NULL,
    username character varying NOT NULL,
    passwd character varying(64) NOT NULL,
    firstname character varying NOT NULL,
    lastname character varying NOT NULL,
    activated boolean DEFAULT true NOT NULL,
    expirydate date,
    lastsignon timestamp with time zone,
    usercategoryid bigint NOT NULL,
    archivedtimestamp timestamp with time zone
);


ALTER TABLE tms.users OWNER TO postgres;

--
-- TOC entry 1797 (class 1259 OID 174497)
-- Dependencies: 1796 6
-- Name: Users_userid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE "Users_userid_seq"
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms."Users_userid_seq" OWNER TO postgres;

--
-- TOC entry 2436 (class 0 OID 0)
-- Dependencies: 1797
-- Name: Users_userid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE "Users_userid_seq" OWNED BY users.userid;


--
-- TOC entry 2437 (class 0 OID 0)
-- Dependencies: 1797
-- Name: Users_userid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('"Users_userid_seq"', 2, false);


--
-- TOC entry 1798 (class 1259 OID 174499)
-- Dependencies: 6
-- Name: childaccessrightsuser; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE childaccessrightsuser (
    childaccessrightuserid bigint NOT NULL,
    mayread boolean NOT NULL,
    mayupdate boolean NOT NULL,
    maydelete boolean NOT NULL,
    mayexport boolean NOT NULL,
    accessrightsuserid bigint NOT NULL,
    fieldid bigint NOT NULL
);


ALTER TABLE tms.childaccessrightsuser OWNER TO postgres;

--
-- TOC entry 1799 (class 1259 OID 174502)
-- Dependencies: 1798 6
-- Name: childaccessrightsuser_childaccessrightuserid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE childaccessrightsuser_childaccessrightuserid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms.childaccessrightsuser_childaccessrightuserid_seq OWNER TO postgres;

--
-- TOC entry 2438 (class 0 OID 0)
-- Dependencies: 1799
-- Name: childaccessrightsuser_childaccessrightuserid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE childaccessrightsuser_childaccessrightuserid_seq OWNED BY childaccessrightsuser.childaccessrightuserid;


--
-- TOC entry 2439 (class 0 OID 0)
-- Dependencies: 1799
-- Name: childaccessrightsuser_childaccessrightuserid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('childaccessrightsuser_childaccessrightuserid_seq', 1, false);


--
-- TOC entry 1800 (class 1259 OID 174504)
-- Dependencies: 6
-- Name: childaccessrightsusercategory; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE childaccessrightsusercategory (
    childaccessrightusercategoryid bigint NOT NULL,
    mayread boolean NOT NULL,
    mayupdate boolean NOT NULL,
    maydelete boolean NOT NULL,
    mayexport boolean NOT NULL,
    accessrightsusecategoryid bigint NOT NULL,
    fieldid bigint NOT NULL
);


ALTER TABLE tms.childaccessrightsusercategory OWNER TO postgres;

--
-- TOC entry 1801 (class 1259 OID 174507)
-- Dependencies: 6 1800
-- Name: childaccessrightsusercategory_childaccessrightusercategoryi_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE childaccessrightsusercategory_childaccessrightusercategoryi_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms.childaccessrightsusercategory_childaccessrightusercategoryi_seq OWNER TO postgres;

--
-- TOC entry 2440 (class 0 OID 0)
-- Dependencies: 1801
-- Name: childaccessrightsusercategory_childaccessrightusercategoryi_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE childaccessrightsusercategory_childaccessrightusercategoryi_seq OWNED BY childaccessrightsusercategory.childaccessrightusercategoryid;


--
-- TOC entry 2441 (class 0 OID 0)
-- Dependencies: 1801
-- Name: childaccessrightsusercategory_childaccessrightusercategoryi_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('childaccessrightsusercategory_childaccessrightusercategoryi_seq', 4235, true);


--
-- TOC entry 1802 (class 1259 OID 174509)
-- Dependencies: 1900 6
-- Name: synonymsmin; Type: VIEW; Schema: tms; Owner: postgres
--

CREATE VIEW synonymsmin AS
    SELECT synonyms.synonymid, synonyms.fieldid, synonyms.chardata, records.archivedtimestamp, synonyms.termid, terms.recordid FROM synonyms, terms, records WHERE (((((octet_length((synonyms.chardata)::text) < 255) AND (synonyms.termid = terms.termid)) AND (terms.recordid = records.recordid)) AND (synonyms.archivedtimestamp IS NULL)) AND (records.archivedtimestamp IS NULL));


ALTER TABLE tms.synonymsmin OWNER TO postgres;

--
-- TOC entry 1803 (class 1259 OID 174513)
-- Dependencies: 6
-- Name: usercategoryprojects; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE usercategoryprojects (
    usercategoryprojectid bigint NOT NULL,
    projectid bigint NOT NULL,
    usercategoryid bigint NOT NULL
);


ALTER TABLE tms.usercategoryprojects OWNER TO postgres;

--
-- TOC entry 1804 (class 1259 OID 174516)
-- Dependencies: 1803 6
-- Name: usercategoryprojects_usercategoryprojectid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE usercategoryprojects_usercategoryprojectid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms.usercategoryprojects_usercategoryprojectid_seq OWNER TO postgres;

--
-- TOC entry 2442 (class 0 OID 0)
-- Dependencies: 1804
-- Name: usercategoryprojects_usercategoryprojectid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE usercategoryprojects_usercategoryprojectid_seq OWNED BY usercategoryprojects.usercategoryprojectid;


--
-- TOC entry 2443 (class 0 OID 0)
-- Dependencies: 1804
-- Name: usercategoryprojects_usercategoryprojectid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('usercategoryprojects_usercategoryprojectid_seq', 1, false);


--
-- TOC entry 1805 (class 1259 OID 174518)
-- Dependencies: 6
-- Name: userprojects; Type: TABLE; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE TABLE userprojects (
    userprojectid bigint NOT NULL,
    projectid bigint NOT NULL,
    userid bigint NOT NULL
);


ALTER TABLE tms.userprojects OWNER TO postgres;

--
-- TOC entry 1806 (class 1259 OID 174521)
-- Dependencies: 6 1805
-- Name: userprojects_userprojectid_seq; Type: SEQUENCE; Schema: tms; Owner: postgres
--

CREATE SEQUENCE userprojects_userprojectid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE tms.userprojects_userprojectid_seq OWNER TO postgres;

--
-- TOC entry 2444 (class 0 OID 0)
-- Dependencies: 1806
-- Name: userprojects_userprojectid_seq; Type: SEQUENCE OWNED BY; Schema: tms; Owner: postgres
--

ALTER SEQUENCE userprojects_userprojectid_seq OWNED BY userprojects.userprojectid;


--
-- TOC entry 2445 (class 0 OID 0)
-- Dependencies: 1806
-- Name: userprojects_userprojectid_seq; Type: SEQUENCE SET; Schema: tms; Owner: postgres
--

SELECT pg_catalog.setval('userprojects_userprojectid_seq', 1, false);


--
-- TOC entry 2113 (class 2604 OID 174523)
-- Dependencies: 1759 1758
-- Name: accessrightuserid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE accessrightsuser ALTER COLUMN accessrightuserid SET DEFAULT nextval('"AccessRightsUser_accessrightuserid_seq"'::regclass);


--
-- TOC entry 2112 (class 2604 OID 174524)
-- Dependencies: 1757 1756
-- Name: accessrightusercategoryid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE accessrightsusercategory ALTER COLUMN accessrightusercategoryid SET DEFAULT nextval('"AccessRightsUserCategory_accessrightusercategoryid_seq"'::regclass);


--
-- TOC entry 2106 (class 2604 OID 174525)
-- Dependencies: 1762 1750
-- Name: audittrailcreaterecordid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailcreaterecords ALTER COLUMN audittrailcreaterecordid SET DEFAULT nextval('"AuditTrailCreateRecords_audittrailcreaterecordid_seq"'::regclass);


--
-- TOC entry 2119 (class 2604 OID 174526)
-- Dependencies: 1764 1763
-- Name: audittrailcreatesynonymattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailcreatesynonymattributes ALTER COLUMN audittrailcreatesynonymattrid SET DEFAULT nextval('"AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq"'::regclass);


--
-- TOC entry 2122 (class 2604 OID 174527)
-- Dependencies: 1766 1765
-- Name: audittrailcreatesynonymid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailcreatesynonyms ALTER COLUMN audittrailcreatesynonymid SET DEFAULT nextval('"AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq"'::regclass);


--
-- TOC entry 2125 (class 2604 OID 174528)
-- Dependencies: 1768 1767
-- Name: audittrailcreatetermattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailcreatetermattributes ALTER COLUMN audittrailcreatetermattrid SET DEFAULT nextval('"AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq"'::regclass);


--
-- TOC entry 2098 (class 2604 OID 174529)
-- Dependencies: 1769 1743
-- Name: audittrailcreatetermid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailcreateterms ALTER COLUMN audittrailcreatetermid SET DEFAULT nextval('"AuditTrailCreateTerms_audittrailcreatetermid_seq"'::regclass);


--
-- TOC entry 2128 (class 2604 OID 174530)
-- Dependencies: 1771 1770
-- Name: audittraileditrecattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittraileditrecordattributes ALTER COLUMN audittraileditrecattrid SET DEFAULT nextval('"AuditTrailEditRecordAttributes_audittraileditrecattrid_seq"'::regclass);


--
-- TOC entry 2109 (class 2604 OID 174531)
-- Dependencies: 1772 1751
-- Name: audittraileditrecordid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittraileditrecords ALTER COLUMN audittraileditrecordid SET DEFAULT nextval('"AuditTrailEditRecords_audittraileditrecordid_seq"'::regclass);


--
-- TOC entry 2131 (class 2604 OID 174532)
-- Dependencies: 1774 1773
-- Name: audittraileditsynonymattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittraileditsynonymattributes ALTER COLUMN audittraileditsynonymattrid SET DEFAULT nextval('"AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq"'::regclass);


--
-- TOC entry 2134 (class 2604 OID 174533)
-- Dependencies: 1776 1775
-- Name: audittraileditsynonymid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittraileditsynonyms ALTER COLUMN audittraileditsynonymid SET DEFAULT nextval('"AuditTrailEditSynonyms_audittraileditsynonymid_seq"'::regclass);


--
-- TOC entry 2137 (class 2604 OID 174534)
-- Dependencies: 1778 1777
-- Name: audittrailedittermattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailedittermattributes ALTER COLUMN audittrailedittermattrid SET DEFAULT nextval('"AuditTrailEditTermAttributes_audittrailedittermattrid_seq"'::regclass);


--
-- TOC entry 2101 (class 2604 OID 174535)
-- Dependencies: 1779 1745
-- Name: audittrailedittermid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittraileditterms ALTER COLUMN audittrailedittermid SET DEFAULT nextval('"AuditTrailEditTerms_audittrailedittermid_seq"'::regclass);


--
-- TOC entry 2116 (class 2604 OID 174536)
-- Dependencies: 1761 1760
-- Name: audittrailcreaterecordattrid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE audittrailtcreaterecordattributes ALTER COLUMN audittrailcreaterecordattrid SET DEFAULT nextval('"AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq"'::regclass);


--
-- TOC entry 2147 (class 2604 OID 174537)
-- Dependencies: 1799 1798
-- Name: childaccessrightuserid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE childaccessrightsuser ALTER COLUMN childaccessrightuserid SET DEFAULT nextval('childaccessrightsuser_childaccessrightuserid_seq'::regclass);


--
-- TOC entry 2148 (class 2604 OID 174538)
-- Dependencies: 1801 1800
-- Name: childaccessrightusercategoryid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE childaccessrightsusercategory ALTER COLUMN childaccessrightusercategoryid SET DEFAULT nextval('childaccessrightsusercategory_childaccessrightusercategoryi_seq'::regclass);


--
-- TOC entry 2095 (class 2604 OID 174539)
-- Dependencies: 1780 1741
-- Name: fieldid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE fields ALTER COLUMN fieldid SET DEFAULT nextval('"Fields_fieldid_seq"'::regclass);


--
-- TOC entry 2138 (class 2604 OID 174540)
-- Dependencies: 1782 1781
-- Name: presetfieldid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE presetfields ALTER COLUMN presetfieldid SET DEFAULT nextval('"PresetFields_presetfieldid_seq"'::regclass);


--
-- TOC entry 2140 (class 2604 OID 174541)
-- Dependencies: 1784 1783
-- Name: projectid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE projects ALTER COLUMN projectid SET DEFAULT nextval('"Projects_projectid_seq"'::regclass);


--
-- TOC entry 2103 (class 2604 OID 174542)
-- Dependencies: 1785 1748
-- Name: recordattributeid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE recordattributes ALTER COLUMN recordattributeid SET DEFAULT nextval('"RecordAttributes_recordattributeid_seq"'::regclass);


--
-- TOC entry 2102 (class 2604 OID 174543)
-- Dependencies: 1786 1747
-- Name: recordprojectid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE recordprojects ALTER COLUMN recordprojectid SET DEFAULT nextval('"RecordProjects_recordprojectid_seq"'::regclass);


--
-- TOC entry 2092 (class 2604 OID 174544)
-- Dependencies: 1787 1737
-- Name: recordid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE records ALTER COLUMN recordid SET DEFAULT nextval('"Records_recordid_seq"'::regclass);


--
-- TOC entry 2110 (class 2604 OID 174545)
-- Dependencies: 1788 1752
-- Name: synonymattributeid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE synonymattributes ALTER COLUMN synonymattributeid SET DEFAULT nextval('"SynonymAttributes_synonymattributeid_seq"'::regclass);


--
-- TOC entry 2111 (class 2604 OID 174546)
-- Dependencies: 1789 1753
-- Name: synonymid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE synonyms ALTER COLUMN synonymid SET DEFAULT nextval('"Synonyms_synonymid_seq"'::regclass);


--
-- TOC entry 2093 (class 2604 OID 174547)
-- Dependencies: 1790 1738
-- Name: termattributeid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE termattributes ALTER COLUMN termattributeid SET DEFAULT nextval('"TermAttributes_termattributeid_seq"'::regclass);


--
-- TOC entry 2142 (class 2604 OID 174548)
-- Dependencies: 1792 1791
-- Name: termbaseid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE termbases ALTER COLUMN termbaseid SET DEFAULT nextval('"TermBases_termbaseid_seq"'::regclass);


--
-- TOC entry 2094 (class 2604 OID 174549)
-- Dependencies: 1793 1739
-- Name: termid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE terms ALTER COLUMN termid SET DEFAULT nextval('"Terms_termid_seq"'::regclass);


--
-- TOC entry 2144 (class 2604 OID 174550)
-- Dependencies: 1795 1794
-- Name: usercategoryid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE usercategories ALTER COLUMN usercategoryid SET DEFAULT nextval('"UserCategories_usercategoryid_seq"'::regclass);


--
-- TOC entry 2149 (class 2604 OID 174551)
-- Dependencies: 1804 1803
-- Name: usercategoryprojectid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE usercategoryprojects ALTER COLUMN usercategoryprojectid SET DEFAULT nextval('usercategoryprojects_usercategoryprojectid_seq'::regclass);


--
-- TOC entry 2150 (class 2604 OID 174552)
-- Dependencies: 1806 1805
-- Name: userprojectid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE userprojects ALTER COLUMN userprojectid SET DEFAULT nextval('userprojects_userprojectid_seq'::regclass);


--
-- TOC entry 2146 (class 2604 OID 174553)
-- Dependencies: 1797 1796
-- Name: userid; Type: DEFAULT; Schema: tms; Owner: postgres
--

ALTER TABLE users ALTER COLUMN userid SET DEFAULT nextval('"Users_userid_seq"'::regclass);


--
-- TOC entry 2361 (class 0 OID 174352)
-- Dependencies: 1758
-- Data for Name: accessrightsuser; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2360 (class 0 OID 174347)
-- Dependencies: 1756
-- Data for Name: accessrightsusercategory; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2356 (class 0 OID 174224)
-- Dependencies: 1750
-- Data for Name: audittrailcreaterecords; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2363 (class 0 OID 174369)
-- Dependencies: 1763
-- Data for Name: audittrailcreatesynonymattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2364 (class 0 OID 174379)
-- Dependencies: 1765
-- Data for Name: audittrailcreatesynonyms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2365 (class 0 OID 174389)
-- Dependencies: 1767
-- Data for Name: audittrailcreatetermattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2352 (class 0 OID 174166)
-- Dependencies: 1743
-- Data for Name: audittrailcreateterms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2366 (class 0 OID 174401)
-- Dependencies: 1770
-- Data for Name: audittraileditrecordattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2357 (class 0 OID 174233)
-- Dependencies: 1751
-- Data for Name: audittraileditrecords; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2367 (class 0 OID 174413)
-- Dependencies: 1773
-- Data for Name: audittraileditsynonymattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2368 (class 0 OID 174423)
-- Dependencies: 1775
-- Data for Name: audittraileditsynonyms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2369 (class 0 OID 174433)
-- Dependencies: 1777
-- Data for Name: audittrailedittermattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2353 (class 0 OID 174179)
-- Dependencies: 1745
-- Data for Name: audittraileditterms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2362 (class 0 OID 174357)
-- Dependencies: 1760
-- Data for Name: audittrailtcreaterecordattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2375 (class 0 OID 174499)
-- Dependencies: 1798
-- Data for Name: childaccessrightsuser; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2376 (class 0 OID 174504)
-- Dependencies: 1800
-- Data for Name: childaccessrightsusercategory; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2351 (class 0 OID 174153)
-- Dependencies: 1741
-- Data for Name: fields; Type: TABLE DATA; Schema: tms; Owner: postgres
--

INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (1, 'English', 1, 1, 100, NULL, 9);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (2, 'International scientific term', 1, 1, 100, NULL, 10);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (3, 'Afrikaans', 1, 1, 100, NULL, 11);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (4, 'IsiZulu', 1, 1, 100, NULL, 12);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (5, 'IsiXhosa', 1, 1, 100, NULL, 13);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (6, 'Siswati', 1, 1, 100, NULL, 14);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (7, 'IsiNdebele', 1, 1, 100, NULL, 15);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (8, 'Setswana', 1, 1, 100, NULL, 16);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (9, 'Sepedi', 1, 1, 100, NULL, 17);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (10, 'Sesotho', 1, 1, 100, NULL, 18);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (11, 'Tshivenḓa', 1, 1, 100, NULL, 19);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (12, 'Xitsonga', 1, 1, 100, NULL, 20);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (13, 'Definition', 2, 1, 250, NULL, 36);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (14, 'Source-definition', 2, 1, 250, NULL, 37);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (15, 'Example sentence', 2, 1, 250, NULL, 38);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (16, 'Collocation', 2, 1, 100, NULL, 40);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (17, 'Time label', 2, 1, 100, NULL, 41);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (18, 'Note', 2, 1, 100, NULL, 29);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (19, 'Plural', 2, 1, 100, NULL, 30);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (20, 'Full form', 2, 1, 100, NULL, 31);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (21, 'Research note', 2, 1, 250, NULL, 32);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (22, 'Source publication', 2, 1, 250, NULL, 33);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (23, 'TOT source', 2, 1, 250, NULL, 34);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (24, 'Text', 2, 1, 250, NULL, 39);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (25, 'Editing note', 2, 1, 250, NULL, 42);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (26, 'Context', 2, 1, 100, NULL, 35);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (27, 'Formula', 2, 6, 100, NULL, 28);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (28, 'Image', 2, 4, 250, NULL, 44);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (29, 'Sound', 2, 4, 250, NULL, 45);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (30, 'Part of speech', 3, 1, 10, NULL, 21);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (31, 'Category', 3, 1, 50, NULL, 22);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (32, 'Geographical usage', 3, 1, 50, NULL, 23);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (33, 'Origin', 3, 1, 50, NULL, 24);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (34, 'Term acceptability', 3, 1, 50, NULL, 25);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (35, 'Register', 3, 1, 50, NULL, 26);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (36, 'Term status', 3, 1, 50, NULL, 27);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (37, 'Admin', 4, 1, 250, NULL, 1);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (38, 'Subject', 4, 1, 100, NULL, 2);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (39, 'Note to manager', 4, 1, 250, NULL, 3);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (40, 'Keyword', 4, 1, 100, NULL, 5);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (41, 'Publication', 4, 1, 250, NULL, 7);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (42, 'Client/Dept', 4, 1, 100, NULL, 8);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (43, 'Record editing note', 4, 1, 250, NULL, 4);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (44, 'Project', 4, 1, 250, NULL, 6);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (45, 'Synonym', 5, 1, 100, NULL, 43);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (46, 'Note', 6, 1, 100, NULL, 52);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (47, 'Plural', 6, 1, 100, NULL, 53);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (48, 'Full form', 6, 1, 100, NULL, 54);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (49, 'Research note', 6, 1, 250, NULL, 55);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (50, 'Source publication', 6, 1, 250, NULL, 56);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (51, 'TOT source', 6, 1, 250, NULL, 57);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (52, 'Text', 6, 1, 250, NULL, 58);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (53, 'Editing note', 6, 1, 250, NULL, 59);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (54, 'Category', 7, 1, 50, NULL, 46);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (55, 'Geographical usage', 7, 1, 50, NULL, 47);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (56, 'Origin', 7, 1, 50, NULL, 48);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (57, 'Term acceptability', 7, 1, 50, NULL, 49);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (58, 'Register', 7, 1, 50, NULL, 50);
INSERT INTO fields (fieldid, fieldname, fieldtypeid, fielddatatypeid, maxlength, defaultvalue, sortindex) VALUES (59, 'Term status', 7, 1, 50, NULL, 51);


--
-- TOC entry 2370 (class 0 OID 174447)
-- Dependencies: 1781
-- Data for Name: presetfields; Type: TABLE DATA; Schema: tms; Owner: postgres
--

INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (1, 'None', 30);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (2, 'None', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (3, 'None', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (4, 'None', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (5, 'None', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (6, 'None', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (7, 'None', 36);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (8, 'None', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (9, 'None', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (10, 'None', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (11, 'None', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (12, 'None', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (13, 'None', 59);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (14, 'n.', 30);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (15, 'adj.', 30);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (16, 'adv.', 30);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (17, 'v.', 30);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (18, 'Abbreviation', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (19, 'Acronym', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (20, 'Short form', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (21, 'Full form', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (22, 'Variant', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (23, 'Diminutive', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (24, 'Singular', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (25, 'Plural', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (26, 'Circumscription', 31);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (27, 'United States', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (28, 'Great Britain', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (29, 'Europe', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (30, 'Republic of South Africa', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (31, 'Eastern Cape', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (32, 'Free State', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (33, 'Gauteng', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (34, 'KwaZulu-Natal', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (35, 'Mpumalanga', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (36, 'Northern Cape', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (37, 'Northern Province', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (38, 'North West Province', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (39, 'Western Cape', 32);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (40, 'Afrikaans', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (41, 'English', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (42, 'French', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (43, 'German', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (44, 'Greek', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (45, 'Greek/Latin', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (46, 'Latin', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (47, 'Spanish', 33);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (48, 'Preferred', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (49, 'Recommended', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (50, 'Not recommended', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (51, 'Superseded', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (52, 'Deprecated', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (53, 'Seldom used', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (54, 'Obsolete', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (55, 'Obsolescent', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (56, 'Historic', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (57, 'Archaic', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (58, 'Standardised', 34);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (59, 'Technical', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (60, 'Neutral', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (61, 'Standard', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (62, 'Less formal', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (63, 'Colloquial', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (64, 'Jargon', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (65, 'Slang', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (66, 'Vulgar', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (67, 'Obscene', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (68, 'Taboo', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (69, 'Offensive', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (70, 'Derogatory', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (71, 'Pejorative', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (72, 'Rural', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (73, 'Urban', 35);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (74, 'Unverified', 36);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (75, 'Verified', 36);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (76, 'Abbreviation', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (77, 'Acronym', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (78, 'Short form', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (79, 'Full form', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (80, 'Variant', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (81, 'Diminutive', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (82, 'Singular', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (83, 'Plural', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (84, 'Circumscription', 54);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (85, 'United States', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (86, 'Great Britain', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (87, 'Europe', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (88, 'Republic of South Africa', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (89, 'Eastern Cape', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (90, 'Free State', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (91, 'Gauteng', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (92, 'KwaZulu-Natal', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (93, 'Mpumalanga', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (94, 'Northern Cape', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (95, 'Northern Province', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (96, 'North West Province', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (97, 'Western Cape', 55);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (98, 'Afrikaans', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (99, 'English', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (100, 'French', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (101, 'German', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (102, 'Greek', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (103, 'Greek/Latin', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (104, 'Latin', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (105, 'Spanish', 56);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (106, 'Preferred', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (107, 'Recommended', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (108, 'Not recommended', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (109, 'Superseded', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (110, 'Deprecated', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (111, 'Seldom used', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (112, 'Obsolete', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (113, 'Obsolescent', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (114, 'Historic', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (115, 'Archaic', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (116, 'Standardised', 57);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (117, 'Technical', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (118, 'Neutral', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (119, 'Standard', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (120, 'Less formal', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (121, 'Colloquial', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (122, 'Jargon', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (123, 'Slang', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (124, 'Vulgar', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (125, 'Obscene', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (126, 'Taboo', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (127, 'Offensive', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (128, 'Derogatory', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (129, 'Pejorative', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (130, 'Rural', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (131, 'Urban', 58);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (132, 'Unverified', 59);
INSERT INTO presetfields (presetfieldid, presetfieldname, fieldid) VALUES (133, 'Verified', 59);


--
-- TOC entry 2371 (class 0 OID 174452)
-- Dependencies: 1783
-- Data for Name: projects; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2355 (class 0 OID 174211)
-- Dependencies: 1748
-- Data for Name: recordattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2354 (class 0 OID 174204)
-- Dependencies: 1747
-- Data for Name: recordprojects; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2348 (class 0 OID 174131)
-- Dependencies: 1737
-- Data for Name: records; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2358 (class 0 OID 174254)
-- Dependencies: 1752
-- Data for Name: synonymattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2359 (class 0 OID 174260)
-- Dependencies: 1753
-- Data for Name: synonyms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2349 (class 0 OID 174134)
-- Dependencies: 1738
-- Data for Name: termattributes; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2372 (class 0 OID 174473)
-- Dependencies: 1791
-- Data for Name: termbases; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2350 (class 0 OID 174140)
-- Dependencies: 1739
-- Data for Name: terms; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2373 (class 0 OID 174484)
-- Dependencies: 1794
-- Data for Name: usercategories; Type: TABLE DATA; Schema: tms; Owner: postgres
--

INSERT INTO usercategories (usercategoryid, usercategory, isadmin, archivedtimestamp) VALUES (1, 'Administrator', true, NULL);
INSERT INTO usercategories (usercategoryid, usercategory, isadmin, archivedtimestamp) VALUES (2, 'User', false, NULL);
INSERT INTO usercategories (usercategoryid, usercategory, isadmin, archivedtimestamp) VALUES (3, 'Guest', false, NULL);


--
-- TOC entry 2377 (class 0 OID 174513)
-- Dependencies: 1803
-- Data for Name: usercategoryprojects; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2378 (class 0 OID 174518)
-- Dependencies: 1805
-- Data for Name: userprojects; Type: TABLE DATA; Schema: tms; Owner: postgres
--



--
-- TOC entry 2374 (class 0 OID 174490)
-- Dependencies: 1796
-- Data for Name: users; Type: TABLE DATA; Schema: tms; Owner: postgres
--

INSERT INTO users (userid, username, passwd, firstname, lastname, activated, expirydate, lastsignon, usercategoryid, archivedtimestamp) VALUES (1, 'administrator', '$2a$10$b8rAOA3zCtVHsULufrjijuViRwucWv0GxC0BIgoV4T1ZDDgHg7vGS', 'Default', 'Administrator', true, NULL, NULL, 1, NULL);


--
-- TOC entry 2211 (class 2606 OID 174555)
-- Dependencies: 1756 1756
-- Name: pk_accessrightusercategoryid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY accessrightsusercategory
    ADD CONSTRAINT pk_accessrightusercategoryid PRIMARY KEY (accessrightusercategoryid);


--
-- TOC entry 2215 (class 2606 OID 174557)
-- Dependencies: 1758 1758
-- Name: pk_accessrightuserid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY accessrightsuser
    ADD CONSTRAINT pk_accessrightuserid PRIMARY KEY (accessrightuserid);


--
-- TOC entry 2220 (class 2606 OID 174559)
-- Dependencies: 1760 1760
-- Name: pk_audittrailcreaterecattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailtcreaterecordattributes
    ADD CONSTRAINT pk_audittrailcreaterecattrid PRIMARY KEY (audittrailcreaterecordattrid);


--
-- TOC entry 2192 (class 2606 OID 174561)
-- Dependencies: 1750 1750
-- Name: pk_audittrailcreaterecordid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailcreaterecords
    ADD CONSTRAINT pk_audittrailcreaterecordid PRIMARY KEY (audittrailcreaterecordid);


--
-- TOC entry 2225 (class 2606 OID 174563)
-- Dependencies: 1763 1763
-- Name: pk_audittrailcreatesynonymattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailcreatesynonymattributes
    ADD CONSTRAINT pk_audittrailcreatesynonymattrid PRIMARY KEY (audittrailcreatesynonymattrid);


--
-- TOC entry 2230 (class 2606 OID 174565)
-- Dependencies: 1765 1765
-- Name: pk_audittrailcreatesynonymid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailcreatesynonyms
    ADD CONSTRAINT pk_audittrailcreatesynonymid PRIMARY KEY (audittrailcreatesynonymid);


--
-- TOC entry 2235 (class 2606 OID 174567)
-- Dependencies: 1767 1767
-- Name: pk_audittrailcreatetermattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailcreatetermattributes
    ADD CONSTRAINT pk_audittrailcreatetermattrid PRIMARY KEY (audittrailcreatetermattrid);


--
-- TOC entry 2173 (class 2606 OID 174569)
-- Dependencies: 1743 1743
-- Name: pk_audittrailcreatetermid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailcreateterms
    ADD CONSTRAINT pk_audittrailcreatetermid PRIMARY KEY (audittrailcreatetermid);


--
-- TOC entry 2240 (class 2606 OID 174571)
-- Dependencies: 1770 1770
-- Name: pk_audittraileditrecattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittraileditrecordattributes
    ADD CONSTRAINT pk_audittraileditrecattrid PRIMARY KEY (audittraileditrecattrid);


--
-- TOC entry 2197 (class 2606 OID 174573)
-- Dependencies: 1751 1751
-- Name: pk_audittraileditrecordid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittraileditrecords
    ADD CONSTRAINT pk_audittraileditrecordid PRIMARY KEY (audittraileditrecordid);


--
-- TOC entry 2245 (class 2606 OID 174575)
-- Dependencies: 1773 1773
-- Name: pk_audittraileditsynonymattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittraileditsynonymattributes
    ADD CONSTRAINT pk_audittraileditsynonymattrid PRIMARY KEY (audittraileditsynonymattrid);


--
-- TOC entry 2250 (class 2606 OID 174577)
-- Dependencies: 1775 1775
-- Name: pk_audittraileditsynonymid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittraileditsynonyms
    ADD CONSTRAINT pk_audittraileditsynonymid PRIMARY KEY (audittraileditsynonymid);


--
-- TOC entry 2255 (class 2606 OID 174579)
-- Dependencies: 1777 1777
-- Name: pk_audittrailedittermattrid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittrailedittermattributes
    ADD CONSTRAINT pk_audittrailedittermattrid PRIMARY KEY (audittrailedittermattrid);


--
-- TOC entry 2178 (class 2606 OID 174581)
-- Dependencies: 1745 1745
-- Name: pk_audittrailedittermid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audittraileditterms
    ADD CONSTRAINT pk_audittrailedittermid PRIMARY KEY (audittrailedittermid);


--
-- TOC entry 2285 (class 2606 OID 174583)
-- Dependencies: 1800 1800
-- Name: pk_childaccessrightusercategoryid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY childaccessrightsusercategory
    ADD CONSTRAINT pk_childaccessrightusercategoryid PRIMARY KEY (childaccessrightusercategoryid);


--
-- TOC entry 2281 (class 2606 OID 174585)
-- Dependencies: 1798 1798
-- Name: pk_childaccessrightuserid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY childaccessrightsuser
    ADD CONSTRAINT pk_childaccessrightuserid PRIMARY KEY (childaccessrightuserid);


--
-- TOC entry 2168 (class 2606 OID 174587)
-- Dependencies: 1741 1741
-- Name: pk_fieldid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fields
    ADD CONSTRAINT pk_fieldid PRIMARY KEY (fieldid);


--
-- TOC entry 2258 (class 2606 OID 174589)
-- Dependencies: 1781 1781
-- Name: pk_presetfieldid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY presetfields
    ADD CONSTRAINT pk_presetfieldid PRIMARY KEY (presetfieldid);


--
-- TOC entry 2261 (class 2606 OID 174591)
-- Dependencies: 1783 1783
-- Name: pk_projectid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT pk_projectid PRIMARY KEY (projectid);


--
-- TOC entry 2187 (class 2606 OID 174593)
-- Dependencies: 1748 1748
-- Name: pk_recordattributeid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY recordattributes
    ADD CONSTRAINT pk_recordattributeid PRIMARY KEY (recordattributeid);


--
-- TOC entry 2155 (class 2606 OID 174595)
-- Dependencies: 1737 1737
-- Name: pk_recordid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY records
    ADD CONSTRAINT pk_recordid PRIMARY KEY (recordid);


--
-- TOC entry 2182 (class 2606 OID 174597)
-- Dependencies: 1747 1747
-- Name: pk_recordprojectid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY recordprojects
    ADD CONSTRAINT pk_recordprojectid PRIMARY KEY (recordprojectid);


--
-- TOC entry 2202 (class 2606 OID 174599)
-- Dependencies: 1752 1752
-- Name: pk_synonymattributeid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY synonymattributes
    ADD CONSTRAINT pk_synonymattributeid PRIMARY KEY (synonymattributeid);


--
-- TOC entry 2207 (class 2606 OID 174601)
-- Dependencies: 1753 1753
-- Name: pk_synonymid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY synonyms
    ADD CONSTRAINT pk_synonymid PRIMARY KEY (synonymid);


--
-- TOC entry 2160 (class 2606 OID 174603)
-- Dependencies: 1738 1738
-- Name: pk_termattributeid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY termattributes
    ADD CONSTRAINT pk_termattributeid PRIMARY KEY (termattributeid);


--
-- TOC entry 2266 (class 2606 OID 174605)
-- Dependencies: 1791 1791
-- Name: pk_termbaseid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY termbases
    ADD CONSTRAINT pk_termbaseid PRIMARY KEY (termbaseid);


--
-- TOC entry 2165 (class 2606 OID 174607)
-- Dependencies: 1739 1739
-- Name: pk_termid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY terms
    ADD CONSTRAINT pk_termid PRIMARY KEY (termid);


--
-- TOC entry 2270 (class 2606 OID 174609)
-- Dependencies: 1794 1794
-- Name: pk_usercategoryid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usercategories
    ADD CONSTRAINT pk_usercategoryid PRIMARY KEY (usercategoryid);


--
-- TOC entry 2289 (class 2606 OID 174611)
-- Dependencies: 1803 1803
-- Name: pk_usercategoryprojectid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usercategoryprojects
    ADD CONSTRAINT pk_usercategoryprojectid PRIMARY KEY (usercategoryprojectid);


--
-- TOC entry 2275 (class 2606 OID 174613)
-- Dependencies: 1796 1796
-- Name: pk_userid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT pk_userid PRIMARY KEY (userid);


--
-- TOC entry 2293 (class 2606 OID 174615)
-- Dependencies: 1805 1805
-- Name: pk_userprojectid; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userprojects
    ADD CONSTRAINT pk_userprojectid PRIMARY KEY (userprojectid);


--
-- TOC entry 2263 (class 2606 OID 174617)
-- Dependencies: 1783 1783
-- Name: unique_projectname; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT unique_projectname UNIQUE (projectname);


--
-- TOC entry 2268 (class 2606 OID 174619)
-- Dependencies: 1791 1791
-- Name: unique_termbasename; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY termbases
    ADD CONSTRAINT unique_termbasename UNIQUE (termbasename);


--
-- TOC entry 2272 (class 2606 OID 174621)
-- Dependencies: 1794 1794
-- Name: unique_usercategory; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usercategories
    ADD CONSTRAINT unique_usercategory UNIQUE (usercategory);


--
-- TOC entry 2277 (class 2606 OID 174623)
-- Dependencies: 1796 1796
-- Name: unique_username; Type: CONSTRAINT; Schema: tms; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT unique_username UNIQUE (username);


--
-- TOC entry 2212 (class 1259 OID 174624)
-- Dependencies: 1758
-- Name: index_accessrightsuser_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_accessrightsuser_fieldid ON accessrightsuser USING btree (fieldid);


--
-- TOC entry 2213 (class 1259 OID 174625)
-- Dependencies: 1758
-- Name: index_accessrightsuser_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_accessrightsuser_userid ON accessrightsuser USING btree (userid);


--
-- TOC entry 2208 (class 1259 OID 174626)
-- Dependencies: 1756
-- Name: index_accessrightsusercategory_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_accessrightsusercategory_fieldid ON accessrightsusercategory USING btree (fieldid);


--
-- TOC entry 2209 (class 1259 OID 174627)
-- Dependencies: 1756
-- Name: index_accessrightsusercategory_usercategoryid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_accessrightsusercategory_usercategoryid ON accessrightsusercategory USING btree (usercategoryid);


--
-- TOC entry 2216 (class 1259 OID 174628)
-- Dependencies: 1760
-- Name: index_audittrailcreaterecordattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecordattributes_auditdatetime ON audittrailtcreaterecordattributes USING btree (auditdatetime);


--
-- TOC entry 2217 (class 1259 OID 174629)
-- Dependencies: 1760
-- Name: index_audittrailcreaterecordattributes_recordattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecordattributes_recordattributeid ON audittrailtcreaterecordattributes USING btree (recordattributeid);


--
-- TOC entry 2218 (class 1259 OID 174630)
-- Dependencies: 1760
-- Name: index_audittrailcreaterecordattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecordattributes_userid ON audittrailtcreaterecordattributes USING btree (userid);


--
-- TOC entry 2188 (class 1259 OID 174631)
-- Dependencies: 1750
-- Name: index_audittrailcreaterecords_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecords_auditdatetime ON audittrailcreaterecords USING btree (auditdatetime);


--
-- TOC entry 2189 (class 1259 OID 174632)
-- Dependencies: 1750
-- Name: index_audittrailcreaterecords_recordid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecords_recordid ON audittrailcreaterecords USING btree (recordid);


--
-- TOC entry 2190 (class 1259 OID 174633)
-- Dependencies: 1750
-- Name: index_audittrailcreaterecords_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreaterecords_userid ON audittrailcreaterecords USING btree (userid);


--
-- TOC entry 2221 (class 1259 OID 174634)
-- Dependencies: 1763
-- Name: index_audittrailcreatesynonymattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonymattributes_auditdatetime ON audittrailcreatesynonymattributes USING btree (auditdatetime);


--
-- TOC entry 2222 (class 1259 OID 174635)
-- Dependencies: 1763
-- Name: index_audittrailcreatesynonymattributes_synonymattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonymattributes_synonymattributeid ON audittrailcreatesynonymattributes USING btree (synonymattributeid);


--
-- TOC entry 2223 (class 1259 OID 174636)
-- Dependencies: 1763
-- Name: index_audittrailcreatesynonymattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonymattributes_userid ON audittrailcreatesynonymattributes USING btree (userid);


--
-- TOC entry 2226 (class 1259 OID 174637)
-- Dependencies: 1765
-- Name: index_audittrailcreatesynonyms_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonyms_auditdatetime ON audittrailcreatesynonyms USING btree (auditdatetime);


--
-- TOC entry 2227 (class 1259 OID 174638)
-- Dependencies: 1765
-- Name: index_audittrailcreatesynonyms_synonymid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonyms_synonymid ON audittrailcreatesynonyms USING btree (synonymid);


--
-- TOC entry 2228 (class 1259 OID 174639)
-- Dependencies: 1765
-- Name: index_audittrailcreatesynonyms_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatesynonyms_userid ON audittrailcreatesynonyms USING btree (userid);


--
-- TOC entry 2231 (class 1259 OID 174640)
-- Dependencies: 1767
-- Name: index_audittrailcreatetermattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatetermattributes_auditdatetime ON audittrailcreatetermattributes USING btree (auditdatetime);


--
-- TOC entry 2232 (class 1259 OID 174641)
-- Dependencies: 1767
-- Name: index_audittrailcreatetermattributes_termattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatetermattributes_termattributeid ON audittrailcreatetermattributes USING btree (termattributeid);


--
-- TOC entry 2233 (class 1259 OID 174642)
-- Dependencies: 1767
-- Name: index_audittrailcreatetermattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreatetermattributes_userid ON audittrailcreatetermattributes USING btree (userid);


--
-- TOC entry 2169 (class 1259 OID 174643)
-- Dependencies: 1743
-- Name: index_audittrailcreateterms_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreateterms_auditdatetime ON audittrailcreateterms USING btree (auditdatetime);


--
-- TOC entry 2170 (class 1259 OID 174644)
-- Dependencies: 1743
-- Name: index_audittrailcreateterms_termid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreateterms_termid ON audittrailcreateterms USING btree (termid);


--
-- TOC entry 2171 (class 1259 OID 174645)
-- Dependencies: 1743
-- Name: index_audittrailcreateterms_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailcreateterms_userid ON audittrailcreateterms USING btree (userid);


--
-- TOC entry 2236 (class 1259 OID 174646)
-- Dependencies: 1770
-- Name: index_audittraileditrecordattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecordattributes_auditdatetime ON audittraileditrecordattributes USING btree (auditdatetime);


--
-- TOC entry 2237 (class 1259 OID 174647)
-- Dependencies: 1770
-- Name: index_audittraileditrecordattributes_recordattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecordattributes_recordattributeid ON audittraileditrecordattributes USING btree (recordattributeid);


--
-- TOC entry 2238 (class 1259 OID 174648)
-- Dependencies: 1770
-- Name: index_audittraileditrecordattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecordattributes_userid ON audittraileditrecordattributes USING btree (userid);


--
-- TOC entry 2193 (class 1259 OID 174649)
-- Dependencies: 1751
-- Name: index_audittraileditrecords_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecords_auditdatetime ON audittraileditrecords USING btree (auditdatetime);


--
-- TOC entry 2194 (class 1259 OID 174650)
-- Dependencies: 1751
-- Name: index_audittraileditrecords_recordid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecords_recordid ON audittraileditrecords USING btree (recordid);


--
-- TOC entry 2195 (class 1259 OID 174651)
-- Dependencies: 1751
-- Name: index_audittraileditrecords_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditrecords_userid ON audittraileditrecords USING btree (userid);


--
-- TOC entry 2241 (class 1259 OID 174652)
-- Dependencies: 1773
-- Name: index_audittraileditsynonymattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonymattributes_auditdatetime ON audittraileditsynonymattributes USING btree (auditdatetime);


--
-- TOC entry 2242 (class 1259 OID 174653)
-- Dependencies: 1773
-- Name: index_audittraileditsynonymattributes_synonymattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonymattributes_synonymattributeid ON audittraileditsynonymattributes USING btree (synonymattributeid);


--
-- TOC entry 2243 (class 1259 OID 174654)
-- Dependencies: 1773
-- Name: index_audittraileditsynonymattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonymattributes_userid ON audittraileditsynonymattributes USING btree (userid);


--
-- TOC entry 2246 (class 1259 OID 174655)
-- Dependencies: 1775
-- Name: index_audittraileditsynonyms_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonyms_auditdatetime ON audittraileditsynonyms USING btree (auditdatetime);


--
-- TOC entry 2247 (class 1259 OID 174656)
-- Dependencies: 1775
-- Name: index_audittraileditsynonyms_synonymid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonyms_synonymid ON audittraileditsynonyms USING btree (synonymid);


--
-- TOC entry 2248 (class 1259 OID 174657)
-- Dependencies: 1775
-- Name: index_audittraileditsynonyms_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditsynonyms_userid ON audittraileditsynonyms USING btree (userid);


--
-- TOC entry 2251 (class 1259 OID 174658)
-- Dependencies: 1777
-- Name: index_audittrailedittermattributes_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailedittermattributes_auditdatetime ON audittrailedittermattributes USING btree (auditdatetime);


--
-- TOC entry 2252 (class 1259 OID 174659)
-- Dependencies: 1777
-- Name: index_audittrailedittermattributes_termattributeid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailedittermattributes_termattributeid ON audittrailedittermattributes USING btree (termattributeid);


--
-- TOC entry 2253 (class 1259 OID 174660)
-- Dependencies: 1777
-- Name: index_audittrailedittermattributes_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittrailedittermattributes_userid ON audittrailedittermattributes USING btree (userid);


--
-- TOC entry 2174 (class 1259 OID 174661)
-- Dependencies: 1745
-- Name: index_audittraileditterms_auditdatetime; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditterms_auditdatetime ON audittraileditterms USING btree (auditdatetime);


--
-- TOC entry 2175 (class 1259 OID 174662)
-- Dependencies: 1745
-- Name: index_audittraileditterms_termid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditterms_termid ON audittraileditterms USING btree (termid);


--
-- TOC entry 2176 (class 1259 OID 174663)
-- Dependencies: 1745
-- Name: index_audittraileditterms_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_audittraileditterms_userid ON audittraileditterms USING btree (userid);


--
-- TOC entry 2278 (class 1259 OID 174664)
-- Dependencies: 1798
-- Name: index_childaccessrightsuser_accessrightsuserid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_childaccessrightsuser_accessrightsuserid ON childaccessrightsuser USING btree (accessrightsuserid);


--
-- TOC entry 2279 (class 1259 OID 174665)
-- Dependencies: 1798
-- Name: index_childaccessrightsuser_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_childaccessrightsuser_fieldid ON childaccessrightsuser USING btree (fieldid);


--
-- TOC entry 2282 (class 1259 OID 174666)
-- Dependencies: 1800
-- Name: index_childaccessrightsusercategory_accessrightsusercategoryid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_childaccessrightsusercategory_accessrightsusercategoryid ON childaccessrightsusercategory USING btree (accessrightsusecategoryid);


--
-- TOC entry 2283 (class 1259 OID 174667)
-- Dependencies: 1800
-- Name: index_childaccessrightsusercategory_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_childaccessrightsusercategory_fieldid ON childaccessrightsusercategory USING btree (fieldid);


--
-- TOC entry 2166 (class 1259 OID 174668)
-- Dependencies: 1741
-- Name: index_fields_fieldname; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_fields_fieldname ON fields USING btree (fieldname);


--
-- TOC entry 2256 (class 1259 OID 174669)
-- Dependencies: 1781
-- Name: index_presetfields_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_presetfields_fieldid ON presetfields USING btree (fieldid);


--
-- TOC entry 2259 (class 1259 OID 174670)
-- Dependencies: 1783
-- Name: index_projects_termbaseid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_projects_termbaseid ON projects USING btree (termbaseid);


--
-- TOC entry 2183 (class 1259 OID 174671)
-- Dependencies: 1748
-- Name: index_recordattributes_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_recordattributes_archivedtimestamp ON recordattributes USING btree (archivedtimestamp);


--
-- TOC entry 2184 (class 1259 OID 174672)
-- Dependencies: 1748
-- Name: index_recordattributes_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_recordattributes_fieldid ON recordattributes USING btree (fieldid);


--
-- TOC entry 2185 (class 1259 OID 174673)
-- Dependencies: 1748
-- Name: index_recordattributes_recordid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_recordattributes_recordid ON recordattributes USING btree (recordid);


--
-- TOC entry 2179 (class 1259 OID 174674)
-- Dependencies: 1747
-- Name: index_recordprojects_projectid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_recordprojects_projectid ON recordprojects USING btree (projectid);


--
-- TOC entry 2180 (class 1259 OID 174675)
-- Dependencies: 1747
-- Name: index_recordprojects_recordid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_recordprojects_recordid ON recordprojects USING btree (recordid);


--
-- TOC entry 2151 (class 1259 OID 174676)
-- Dependencies: 1737
-- Name: index_records_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_records_archivedtimestamp ON records USING btree (archivedtimestamp);


--
-- TOC entry 2152 (class 1259 OID 174677)
-- Dependencies: 1737
-- Name: index_records_termbaseid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_records_termbaseid ON records USING btree (termbaseid);


--
-- TOC entry 2153 (class 1259 OID 174678)
-- Dependencies: 1737
-- Name: index_records_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_records_userid ON records USING btree (beingeditedby);


--
-- TOC entry 2198 (class 1259 OID 174679)
-- Dependencies: 1752
-- Name: index_synonymattributes_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonymattributes_archivedtimestamp ON synonymattributes USING btree (archivedtimestamp);


--
-- TOC entry 2199 (class 1259 OID 174680)
-- Dependencies: 1752
-- Name: index_synonymattributes_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonymattributes_fieldid ON synonymattributes USING btree (fieldid);


--
-- TOC entry 2200 (class 1259 OID 174681)
-- Dependencies: 1752
-- Name: index_synonymattributes_synonymid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonymattributes_synonymid ON synonymattributes USING btree (synonymid);


--
-- TOC entry 2203 (class 1259 OID 174682)
-- Dependencies: 1753
-- Name: index_synonyms_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonyms_archivedtimestamp ON synonyms USING btree (archivedtimestamp);


--
-- TOC entry 2204 (class 1259 OID 174683)
-- Dependencies: 1753
-- Name: index_synonyms_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonyms_fieldid ON synonyms USING btree (fieldid);


--
-- TOC entry 2205 (class 1259 OID 174684)
-- Dependencies: 1753
-- Name: index_synonyms_termid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_synonyms_termid ON synonyms USING btree (termid);


--
-- TOC entry 2156 (class 1259 OID 174685)
-- Dependencies: 1738
-- Name: index_termattributes_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_termattributes_archivedtimestamp ON termattributes USING btree (archivedtimestamp);


--
-- TOC entry 2157 (class 1259 OID 174686)
-- Dependencies: 1738
-- Name: index_termattributes_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_termattributes_fieldid ON termattributes USING btree (fieldid);


--
-- TOC entry 2158 (class 1259 OID 174687)
-- Dependencies: 1738
-- Name: index_termattributes_termid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_termattributes_termid ON termattributes USING btree (termid);


--
-- TOC entry 2264 (class 1259 OID 174688)
-- Dependencies: 1791
-- Name: index_termbases_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_termbases_userid ON termbases USING btree (userid);


--
-- TOC entry 2161 (class 1259 OID 174689)
-- Dependencies: 1739
-- Name: index_terms_archivedtimestamp; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_terms_archivedtimestamp ON terms USING btree (archivedtimestamp);


--
-- TOC entry 2162 (class 1259 OID 174690)
-- Dependencies: 1739
-- Name: index_terms_fieldid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_terms_fieldid ON terms USING btree (fieldid);


--
-- TOC entry 2163 (class 1259 OID 174691)
-- Dependencies: 1739
-- Name: index_terms_recordid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_terms_recordid ON terms USING btree (recordid);


--
-- TOC entry 2286 (class 1259 OID 174692)
-- Dependencies: 1803
-- Name: index_usercategory_usercategoryid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_usercategory_usercategoryid ON usercategoryprojects USING btree (usercategoryid);


--
-- TOC entry 2287 (class 1259 OID 174693)
-- Dependencies: 1803
-- Name: index_usercategoryprojects_projectid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_usercategoryprojects_projectid ON usercategoryprojects USING btree (projectid);


--
-- TOC entry 2290 (class 1259 OID 174694)
-- Dependencies: 1805
-- Name: index_userproject_projectid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_userproject_projectid ON userprojects USING btree (projectid);


--
-- TOC entry 2291 (class 1259 OID 174695)
-- Dependencies: 1805
-- Name: index_userprojects_userid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_userprojects_userid ON userprojects USING btree (userid);


--
-- TOC entry 2273 (class 1259 OID 174696)
-- Dependencies: 1796
-- Name: index_users_usercategoryid; Type: INDEX; Schema: tms; Owner: postgres; Tablespace: 
--

CREATE INDEX index_users_usercategoryid ON users USING btree (usercategoryid);


--
-- TOC entry 2318 (class 2606 OID 174697)
-- Dependencies: 1758 1741 2167
-- Name: fk_accessrightsuser_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY accessrightsuser
    ADD CONSTRAINT fk_accessrightsuser_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2319 (class 2606 OID 174702)
-- Dependencies: 1796 1758 2274
-- Name: fk_accessrightsuser_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY accessrightsuser
    ADD CONSTRAINT fk_accessrightsuser_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2316 (class 2606 OID 174707)
-- Dependencies: 1756 2167 1741
-- Name: fk_accessrightsusercategory_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY accessrightsusercategory
    ADD CONSTRAINT fk_accessrightsusercategory_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2317 (class 2606 OID 174712)
-- Dependencies: 1756 2269 1794
-- Name: fk_accessrightsusercategory_usercategoryid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY accessrightsusercategory
    ADD CONSTRAINT fk_accessrightsusercategory_usercategoryid FOREIGN KEY (usercategoryid) REFERENCES usercategories(usercategoryid) MATCH FULL;


--
-- TOC entry 2320 (class 2606 OID 174717)
-- Dependencies: 1760 2186 1748
-- Name: fk_audittrailcreaterecordattributes_recordattributes_recordattr; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailtcreaterecordattributes
    ADD CONSTRAINT fk_audittrailcreaterecordattributes_recordattributes_recordattr FOREIGN KEY (recordattributeid) REFERENCES recordattributes(recordattributeid) MATCH FULL;


--
-- TOC entry 2321 (class 2606 OID 174722)
-- Dependencies: 2274 1796 1760
-- Name: fk_audittrailcreaterecordattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailtcreaterecordattributes
    ADD CONSTRAINT fk_audittrailcreaterecordattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2308 (class 2606 OID 174727)
-- Dependencies: 1737 2154 1750
-- Name: fk_audittrailcreaterecords_records_recordid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreaterecords
    ADD CONSTRAINT fk_audittrailcreaterecords_records_recordid FOREIGN KEY (recordid) REFERENCES records(recordid) MATCH FULL;


--
-- TOC entry 2309 (class 2606 OID 174732)
-- Dependencies: 1796 2274 1750
-- Name: fk_audittrailcreaterecords_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreaterecords
    ADD CONSTRAINT fk_audittrailcreaterecords_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2322 (class 2606 OID 174737)
-- Dependencies: 1752 2201 1763
-- Name: fk_audittrailcreatesynonymattributes_synonymattributes_synonyma; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatesynonymattributes
    ADD CONSTRAINT fk_audittrailcreatesynonymattributes_synonymattributes_synonyma FOREIGN KEY (synonymattributeid) REFERENCES synonymattributes(synonymattributeid) MATCH FULL;


--
-- TOC entry 2323 (class 2606 OID 174742)
-- Dependencies: 2274 1763 1796
-- Name: fk_audittrailcreatesynonymattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatesynonymattributes
    ADD CONSTRAINT fk_audittrailcreatesynonymattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2324 (class 2606 OID 174747)
-- Dependencies: 1765 2206 1753
-- Name: fk_audittrailcreatesynonyms_synonyms_synonymid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatesynonyms
    ADD CONSTRAINT fk_audittrailcreatesynonyms_synonyms_synonymid FOREIGN KEY (synonymid) REFERENCES synonyms(synonymid) MATCH FULL;


--
-- TOC entry 2325 (class 2606 OID 174752)
-- Dependencies: 1765 2274 1796
-- Name: fk_audittrailcreatesynonyms_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatesynonyms
    ADD CONSTRAINT fk_audittrailcreatesynonyms_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2326 (class 2606 OID 174757)
-- Dependencies: 1767 1738 2159
-- Name: fk_audittrailcreatetermattributes_termattributes_termattributei; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatetermattributes
    ADD CONSTRAINT fk_audittrailcreatetermattributes_termattributes_termattributei FOREIGN KEY (termattributeid) REFERENCES termattributes(termattributeid) MATCH FULL;


--
-- TOC entry 2327 (class 2606 OID 174762)
-- Dependencies: 1767 1796 2274
-- Name: fk_audittrailcreatetermattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreatetermattributes
    ADD CONSTRAINT fk_audittrailcreatetermattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2300 (class 2606 OID 174767)
-- Dependencies: 2164 1743 1739
-- Name: fk_audittrailcreateterms_terms_termid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreateterms
    ADD CONSTRAINT fk_audittrailcreateterms_terms_termid FOREIGN KEY (termid) REFERENCES terms(termid) MATCH FULL;


--
-- TOC entry 2301 (class 2606 OID 174772)
-- Dependencies: 1796 1743 2274
-- Name: fk_audittrailcreateterms_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailcreateterms
    ADD CONSTRAINT fk_audittrailcreateterms_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2328 (class 2606 OID 174777)
-- Dependencies: 1748 2186 1770
-- Name: fk_audittraileditrecordattributes_recordattributes_recordattrib; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditrecordattributes
    ADD CONSTRAINT fk_audittraileditrecordattributes_recordattributes_recordattrib FOREIGN KEY (recordattributeid) REFERENCES recordattributes(recordattributeid) MATCH FULL;


--
-- TOC entry 2329 (class 2606 OID 174782)
-- Dependencies: 2274 1796 1770
-- Name: fk_audittraileditrecordattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditrecordattributes
    ADD CONSTRAINT fk_audittraileditrecordattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2310 (class 2606 OID 174787)
-- Dependencies: 1751 2154 1737
-- Name: fk_audittraileditrecords_records_recordid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditrecords
    ADD CONSTRAINT fk_audittraileditrecords_records_recordid FOREIGN KEY (recordid) REFERENCES records(recordid) MATCH FULL;


--
-- TOC entry 2311 (class 2606 OID 174792)
-- Dependencies: 2274 1751 1796
-- Name: fk_audittraileditrecords_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditrecords
    ADD CONSTRAINT fk_audittraileditrecords_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2330 (class 2606 OID 174797)
-- Dependencies: 2201 1752 1773
-- Name: fk_audittraileditsynonymattributes_synonymattributes_synonymatt; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditsynonymattributes
    ADD CONSTRAINT fk_audittraileditsynonymattributes_synonymattributes_synonymatt FOREIGN KEY (synonymattributeid) REFERENCES synonymattributes(synonymattributeid) MATCH FULL;


--
-- TOC entry 2331 (class 2606 OID 174802)
-- Dependencies: 1773 2274 1796
-- Name: fk_audittraileditsynonymattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditsynonymattributes
    ADD CONSTRAINT fk_audittraileditsynonymattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2332 (class 2606 OID 174807)
-- Dependencies: 1775 1753 2206
-- Name: fk_audittraileditsynonyms_synonyms_synonymid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditsynonyms
    ADD CONSTRAINT fk_audittraileditsynonyms_synonyms_synonymid FOREIGN KEY (synonymid) REFERENCES synonyms(synonymid) MATCH FULL;


--
-- TOC entry 2333 (class 2606 OID 174812)
-- Dependencies: 1775 1796 2274
-- Name: fk_audittraileditsynonyms_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditsynonyms
    ADD CONSTRAINT fk_audittraileditsynonyms_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2334 (class 2606 OID 174817)
-- Dependencies: 2159 1777 1738
-- Name: fk_audittrailedittermattributes_termattributes_termattributeid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailedittermattributes
    ADD CONSTRAINT fk_audittrailedittermattributes_termattributes_termattributeid FOREIGN KEY (termattributeid) REFERENCES termattributes(termattributeid) MATCH FULL;


--
-- TOC entry 2335 (class 2606 OID 174822)
-- Dependencies: 1796 1777 2274
-- Name: fk_audittrailedittermattributes_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittrailedittermattributes
    ADD CONSTRAINT fk_audittrailedittermattributes_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2302 (class 2606 OID 174827)
-- Dependencies: 1739 1745 2164
-- Name: fk_audittraileditterms_terms_termid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditterms
    ADD CONSTRAINT fk_audittraileditterms_terms_termid FOREIGN KEY (termid) REFERENCES terms(termid) MATCH FULL;


--
-- TOC entry 2303 (class 2606 OID 174832)
-- Dependencies: 1796 1745 2274
-- Name: fk_audittraileditterms_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY audittraileditterms
    ADD CONSTRAINT fk_audittraileditterms_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2340 (class 2606 OID 174837)
-- Dependencies: 1798 1758 2214
-- Name: fk_childaccessrightsuser_accessrightsuserid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY childaccessrightsuser
    ADD CONSTRAINT fk_childaccessrightsuser_accessrightsuserid FOREIGN KEY (accessrightsuserid) REFERENCES accessrightsuser(accessrightuserid) MATCH FULL;


--
-- TOC entry 2342 (class 2606 OID 174842)
-- Dependencies: 1800 1756 2210
-- Name: fk_childaccessrightsusercategory_accessrightsusercategoryid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY childaccessrightsusercategory
    ADD CONSTRAINT fk_childaccessrightsusercategory_accessrightsusercategoryid FOREIGN KEY (accessrightsusecategoryid) REFERENCES accessrightsusercategory(accessrightusercategoryid) MATCH FULL;


--
-- TOC entry 2341 (class 2606 OID 174847)
-- Dependencies: 2167 1798 1741
-- Name: fk_childccessrightsuser_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY childaccessrightsuser
    ADD CONSTRAINT fk_childccessrightsuser_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2343 (class 2606 OID 174852)
-- Dependencies: 1741 1800 2167
-- Name: fk_childccessrightsusercategory_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY childaccessrightsusercategory
    ADD CONSTRAINT fk_childccessrightsusercategory_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2336 (class 2606 OID 174857)
-- Dependencies: 1741 1781 2167
-- Name: fk_presetfields_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY presetfields
    ADD CONSTRAINT fk_presetfields_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2337 (class 2606 OID 174862)
-- Dependencies: 1783 2265 1791
-- Name: fk_projects_termbases_termbaseid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY projects
    ADD CONSTRAINT fk_projects_termbases_termbaseid FOREIGN KEY (termbaseid) REFERENCES termbases(termbaseid) MATCH FULL;


--
-- TOC entry 2294 (class 2606 OID 174867)
-- Dependencies: 2265 1737 1791
-- Name: fk_record_termbases_termbaseid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY records
    ADD CONSTRAINT fk_record_termbases_termbaseid FOREIGN KEY (termbaseid) REFERENCES termbases(termbaseid) MATCH FULL;


--
-- TOC entry 2306 (class 2606 OID 174872)
-- Dependencies: 1748 1741 2167
-- Name: fk_recordattributes_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY recordattributes
    ADD CONSTRAINT fk_recordattributes_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2307 (class 2606 OID 174877)
-- Dependencies: 2154 1748 1737
-- Name: fk_recordattributes_records_recordid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY recordattributes
    ADD CONSTRAINT fk_recordattributes_records_recordid FOREIGN KEY (recordid) REFERENCES records(recordid) MATCH FULL;


--
-- TOC entry 2304 (class 2606 OID 174882)
-- Dependencies: 1783 1747 2260
-- Name: fk_recordprojects_projects_projectid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY recordprojects
    ADD CONSTRAINT fk_recordprojects_projects_projectid FOREIGN KEY (projectid) REFERENCES projects(projectid) MATCH FULL;


--
-- TOC entry 2305 (class 2606 OID 174887)
-- Dependencies: 1737 1747 2154
-- Name: fk_recordprojects_recordid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY recordprojects
    ADD CONSTRAINT fk_recordprojects_recordid FOREIGN KEY (recordid) REFERENCES records(recordid) MATCH FULL;


--
-- TOC entry 2295 (class 2606 OID 174892)
-- Dependencies: 1796 1737 2274
-- Name: fk_records_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY records
    ADD CONSTRAINT fk_records_users_userid FOREIGN KEY (beingeditedby) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2312 (class 2606 OID 174897)
-- Dependencies: 1741 1752 2167
-- Name: fk_synonymattributes_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY synonymattributes
    ADD CONSTRAINT fk_synonymattributes_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2313 (class 2606 OID 174902)
-- Dependencies: 1753 1752 2206
-- Name: fk_synonymattributes_synonyms_synonymid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY synonymattributes
    ADD CONSTRAINT fk_synonymattributes_synonyms_synonymid FOREIGN KEY (synonymid) REFERENCES synonyms(synonymid) MATCH FULL;


--
-- TOC entry 2314 (class 2606 OID 174907)
-- Dependencies: 1741 1753 2167
-- Name: fk_synonyms_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY synonyms
    ADD CONSTRAINT fk_synonyms_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2315 (class 2606 OID 174912)
-- Dependencies: 1739 1753 2164
-- Name: fk_synonyms_terms_termid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY synonyms
    ADD CONSTRAINT fk_synonyms_terms_termid FOREIGN KEY (termid) REFERENCES terms(termid) MATCH FULL;


--
-- TOC entry 2296 (class 2606 OID 174917)
-- Dependencies: 1741 1738 2167
-- Name: fk_termattributes_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY termattributes
    ADD CONSTRAINT fk_termattributes_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2297 (class 2606 OID 174922)
-- Dependencies: 1739 1738 2164
-- Name: fk_termattributes_terms_termid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY termattributes
    ADD CONSTRAINT fk_termattributes_terms_termid FOREIGN KEY (termid) REFERENCES terms(termid) MATCH FULL;


--
-- TOC entry 2338 (class 2606 OID 174927)
-- Dependencies: 1796 1791 2274
-- Name: fk_termbases_users_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY termbases
    ADD CONSTRAINT fk_termbases_users_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2298 (class 2606 OID 174932)
-- Dependencies: 1739 1741 2167
-- Name: fk_terms_fields_fieldid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY terms
    ADD CONSTRAINT fk_terms_fields_fieldid FOREIGN KEY (fieldid) REFERENCES fields(fieldid) MATCH FULL;


--
-- TOC entry 2299 (class 2606 OID 174937)
-- Dependencies: 1739 1737 2154
-- Name: fk_terms_records_recordid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY terms
    ADD CONSTRAINT fk_terms_records_recordid FOREIGN KEY (recordid) REFERENCES records(recordid) MATCH FULL;


--
-- TOC entry 2344 (class 2606 OID 174942)
-- Dependencies: 2260 1803 1783
-- Name: fk_usercategoryproject_projectid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY usercategoryprojects
    ADD CONSTRAINT fk_usercategoryproject_projectid FOREIGN KEY (projectid) REFERENCES projects(projectid) MATCH FULL;


--
-- TOC entry 2345 (class 2606 OID 174947)
-- Dependencies: 1796 1803 2274
-- Name: fk_usercategoryproject_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY usercategoryprojects
    ADD CONSTRAINT fk_usercategoryproject_userid FOREIGN KEY (usercategoryid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2346 (class 2606 OID 174952)
-- Dependencies: 1783 1805 2260
-- Name: fk_userproject_projectid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY userprojects
    ADD CONSTRAINT fk_userproject_projectid FOREIGN KEY (projectid) REFERENCES projects(projectid) MATCH FULL;


--
-- TOC entry 2347 (class 2606 OID 174957)
-- Dependencies: 1796 1805 2274
-- Name: fk_userproject_userid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY userprojects
    ADD CONSTRAINT fk_userproject_userid FOREIGN KEY (userid) REFERENCES users(userid) MATCH FULL;


--
-- TOC entry 2339 (class 2606 OID 174962)
-- Dependencies: 2269 1796 1794
-- Name: fk_users_usercategories_usercategoryid; Type: FK CONSTRAINT; Schema: tms; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk_users_usercategories_usercategoryid FOREIGN KEY (usercategoryid) REFERENCES usercategories(usercategoryid) MATCH FULL;


--
-- TOC entry 2383 (class 0 OID 0)
-- Dependencies: 7
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2014-02-11 12:24:59

--
-- PostgreSQL database dump complete
--

