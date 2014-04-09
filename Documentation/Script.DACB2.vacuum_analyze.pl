#!/perl/bin/perl -w
#
#  Autshumato Terminology Management System (TMS)
#  Free web application for the management of multilingual terminology databases (termbanks).
#
#  Copyright (C) 2011-2012 Centre for Text Technology (CTexT®), North-West University
#  and Department of Arts and Culture, Government of South Africa
#  Home page: http://www.nwu.co.za/ctext
#  Project page: http://autshumatotms.sourceforge.net
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

#! /usr/bin/perl -w
use strict;

use DBI;
use IO::File;
use FindBin;

use constant LOG_FILE => "$FindBin::Bin/#vacuum_analyze.log";

sub vacuum_analyze
{	
	my ($dbname, $user, $password) = @_;
		
	my @tables = ();
	
	push (@tables, 'tms.accessrightsuser');
	push (@tables, 'tms.accessrightsusercategory');		
	push (@tables, 'tms.audittrailcreaterecords');
	push (@tables, 'tms.audittrailcreatesynonymattributes');
	push (@tables, 'tms.audittrailcreatesynonyms');
	push (@tables, 'tms.audittrailcreatetermattributes');
	push (@tables, 'tms.audittrailcreateterms');
	push (@tables, 'tms.audittraileditrecordattributes');
	push (@tables, 'tms.audittraileditrecords');
	push (@tables, 'tms.audittraileditsynonymattributes');
	push (@tables, 'tms.audittraileditsynonyms');
	push (@tables, 'tms.audittrailedittermattributes');
	push (@tables, 'tms.audittraileditterms');
	push (@tables, 'tms.audittrailtcreaterecordattributes');
	push (@tables, 'tms.childaccessrightsuser');
	push (@tables, 'tms.childaccessrightsusercategory');
	push (@tables, 'tms.fields');
	push (@tables, 'tms.presetfields');
	push (@tables, 'tms.projects');
	push (@tables, 'tms.recordattributes');
	push (@tables, 'tms.recordprojects');
	push (@tables, 'tms.records');
	push (@tables, 'tms.synonymattributes');
	push (@tables, 'tms.synonyms');
	push (@tables, 'tms.termattributes');
	push (@tables, 'tms.termbases');
	push (@tables, 'tms.terms');
	push (@tables, 'tms.usercategories');	
	push (@tables, 'tms.usercategoryprojects');
	push (@tables, 'tms.userprojects');
	push (@tables, 'tms.users');
	
	my $log = IO::File->new;
    
	$log->open (LOG_FILE, '>:utf8') or die ("Couldn't open log file");
    
    # try to connect to database.    	
	my $dbh = DBI->connect ("dbi:PgPP:dbname=$dbname;host=localhost;port=5432",                                           
                                                                 $user,
                                                                 $password);
	
    my $log_entry = "Connected to database.\n";
    
    if (! defined $dbh)
    {
        # Could not connect to database.
        $log_entry = "Failed to connect to database:$DBI::errstr\n";        
        $log->print ($log_entry);
        $log->close;
		
        exit (1);
    }
	
    # Connected to database.
    $log->print ($log_entry);
    	
	# This loop handles VACUUM ANALYZE's the tables.
	foreach my $table (@tables)
	{
		$log_entry = "VACUUM ANALYZE: $table.\n";
		
		print $log_entry;
		$log->print ($log_entry);
		
		my $ret = $dbh->do ("VACUUM ANALYZE $table");
		
		$log_entry = "VACUUM ANALYZE succeeded for $table.\n";
		$log_entry = "VACUUM ANALYZE failed for $table.\n" if (! defined $ret);
		
		print $log_entry;
		$log->print ($log_entry);	
	}	
			
    eval
    {
        $dbh->disconnect;
    };
    
    # check if disconnect was successful
    if ($@)
    {
        $log_entry = "Failed to disconnect from database: $DBI::errstr\n";        
        $log->print ($log_entry);
		
		$log->close;
    }
    else
    {
        $log_entry = "Disconnected from database.\n";
        $log->print ($log_entry);
    }
    
    print "DONE!!!\n";
	$log->close;
}

sub alter_sequences
{
	my ($dbname, $user, $password) = @_;
	
	print "Altering table sequences.\n";
	
	my @tables =
		(      
			{
				'table'     => 'tms.accessrightsuser',
				'sequence'  => "tms.\"AccessRightsUser_accessrightuserid_seq\"",
				'pk'        => 'accessrightuserid'
			},                                
			{
				'table'     => 'tms.accessrightsusercategory',
				'sequence'  => "tms.\"AccessRightsUserCategory_accessrightusercategoryid_seq\"",
				'pk'        => 'accessrightusercategoryid'
			},
			{
				'table'     => 'tms.audittrailcreaterecords',
				'sequence'  => "tms.\"AuditTrailCreateRecords_audittrailcreaterecordid_seq\"",
				'pk'        => 'audittrailcreaterecordid'
			},                                
			{
				'table'    => 'tms.audittrailcreatesynonymattributes',
				'sequence'  => "tms.\"AuditTrailCreateSynonymAttrib_audittrailcreatesynonymattrid_seq\"",
				'pk'        => 'audittrailcreatesynonymattrid'                
			},                                
			{
				'table'     => 'tms.audittrailcreatesynonyms',
				'sequence'  => "tms.\"AuditTrailCreateSynonyms_audittrailcreatesynonymid_seq\"",
				'pk'        => 'audittrailcreatesynonymid'
			},
			{
				'table'     => 'tms.audittrailcreatetermattributes',
				'sequence'  => "tms.\"AuditTrailCreateTermAttributes_audittrailcreatetermattrid_seq\"",
				'pk'        => 'audittrailcreatetermattrid'
			},
			{
				'table'     => 'tms.audittrailcreateterms',
				'sequence'  => "tms.\"AuditTrailCreateTerms_audittrailcreatetermid_seq\"",
				'pk'        => 'audittrailcreatetermid'
			},
			{
				'table'     => 'tms.audittraileditrecordattributes',
				'sequence'  => "tms.\"AuditTrailEditRecordAttributes_audittraileditrecattrid_seq\"",
				'pk'        => 'audittraileditrecattrid'
			},
			{
				'table'     => 'tms.audittraileditrecords',
				'sequence'  => "tms.\"AuditTrailEditRecords_audittraileditrecordid_seq\"",
				'pk'        => 'audittraileditrecordid'
			},
			{
				'table'     => 'tms.audittraileditsynonymattributes',
				'sequence'  => "tms.\"AuditTrailEditSynonymAttributes_audittraileditsynonymattrid_seq\"",
				'pk'        => 'audittraileditsynonymattrid'            
			},
			{
				'table'     => 'tms.audittraileditsynonyms',
				'sequence'  => "tms.\"AuditTrailEditSynonyms_audittraileditsynonymid_seq\"",
				'pk'        => 'audittraileditsynonymid'                                               
			},
			{
				'table'     =>'tms.audittrailedittermattributes',
				'sequence'  => "tms.\"AuditTrailEditTermAttributes_audittrailedittermattrid_seq\"",
				'pk'        => 'audittrailedittermattrid'
			},                
			{
				'table'     => 'tms.audittraileditterms',
				'sequence'  => "tms.\"AuditTrailEditTerms_audittrailedittermid_seq\"",
				'pk'        => 'audittrailedittermid'
			},                
			{
				'table'     => 'tms.audittrailtcreaterecordattributes',
				'sequence'  => "tms.\"AuditTrailCreateRecordAttribut_audittrailcreaterecordattrid_seq\"",
				'pk'        => 'audittrailcreaterecordattrid'                                          
			},
			{
				'table'     => 'tms.childaccessrightsuser',
				'sequence'  => "tms.\"childaccessrightsuser_childaccessrightuserid_seq\"",
				'pk'        => 'childaccessrightuserid'                                          
			},
			{
				'table'     => 'tms.childaccessrightsusercategory',
				'sequence'  => "tms.\"tms.childaccessrightsusercategory_childaccessrightusercategoryi_seq\"",
				'pk'        => 'childaccessrightusercategoryid'                                          
			},
			{
				'table'     => 'tms.fields',
				'sequence'  => "tms.\"Fields_fieldid_seq\"",
				'pk'        => 'fieldid'    
			},
			{
				'table'     => 'tms.presetfields',
				'sequence'  => "tms.\"PresetFields_presetfieldid_seq\"",
				'pk'        => 'presetfieldid'
															
			},
			{
				'table'     => 'tms.projects',
				'sequence'  => "tms.\"Projects_projectid_seq\"",
				'pk'        => 'projectid'
			},
			{
				'table'     => 'tms.recordattributes',
				'sequence'  => "tms.\"RecordAttributes_recordattributeid_seq\"",
				'pk'        => 'recordattributeid'    
			},
			{
				'table'     => 'tms.recordprojects',
				'sequence'  => "tms.\"RecordProjects_recordprojectid_seq\"",
				'pk'        => 'recordprojectid'    
			},
			{
				'table'     => 'tms.records',
				'sequence'  => "tms.\"Records_recordid_seq\"",
				'pk'        => 'recordid'
			},
			{
				'table'     => 'tms.synonymattributes',
				'sequence'  => "tms.\"SynonymAttributes_synonymattributeid_seq\"",
				'pk'        => 'synonymattributeid'
			},
			{
				'table'     => 'tms.synonyms',
				'sequence'  => "tms.\"Synonyms_synonymid_seq\"",
				'pk'        => 'synonymid'
			},
			{
				'table'     => 'tms.termattributes',
				'sequence'  => "tms.\"TermAttributes_termattributeid_seq\"",
				'pk'        => 'termattributeid'                                            
			},
			{
				'table'     => 'tms.termbases',
				'sequence'  => "tms.\"TermBases_termbaseid_seq\"",
				'pk'        => 'termbaseid'                                            
			},
			{
				'table'     => 'tms.terms',
				'sequence'  => "tms.\"Terms_termid_seq\"",
				'pk'        => 'termid'                                      
			},
			{
				'table'     => 'tms.usercategories',
				'sequence'  => "tms.\"UserCategories_usercategoryid_seq\"",
				'pk'        => 'usercategoryid'
															
			},
			{
				'table'     => 'tms.usercategoryprojects',
				'sequence'  => "tms.\"usercategoryprojects_usercategoryprojectid_seq\"",
				'pk'        => 'usercategoryprojectid'
															
			},
			{
				'table'     => 'tms.userprojects',
				'sequence'  => "tms.\"userprojects_userprojectid_seq\"",
				'pk'        => 'userprojectid'
															
			},
			{
				'table'     => 'tms.users',
				'sequence'  => "tms.\"Users_userid_seq\"",
				'pk'        => 'userid'
			}
		);
	    
	my $dbh = undef;
    
	eval
	{
		$dbh = DBI->connect ("dbi:PgPP:dbname=$dbname;host=localhost;port=5432",                                           
                                              $user,
                                              $password) or die "Cannot create DB handler.";
        
		my $sth = undef;
                        
		foreach my $table (@tables)
		{
		    my $id = get_last_id ($dbh, $table->{table}, $table->{pk});
		    die "Could not retrieve last id of $table->{table}" if (! defined $id);
		    
		    # set the next id 
		    $id++;
		    my $sequence_sql = "alter sequence $table->{sequence} restart with $id";
		    $sth = $dbh->prepare ($sequence_sql);
		    
		    $sth->execute or die "Cannot alter sequence for $table.\n";
		}        
		
		$sth->finish;
		$dbh->disconnect;
        
		undef $sth;
		undef $dbh;
	};
    
	if ($@)
	{        
	    $dbh->disconnect if (defined $dbh);
	    die ($@);
	}
}

sub get_last_id
{
    my ($dbh, $table, $pk) = @_;        
    
    #my $sth = $dbh->prepare ("select $pk from $table order by $pk desc");
    my $sth = $dbh->prepare ("select max ($pk) from $table");
    $sth->execute or return undef;
    
    my $id = $sth->fetchrow;    
    $id = 0 if (! defined $id);
    
    $sth->finish;
    return $id;
}

if (scalar (@ARGV) != 3)
{
	print "USAGE: ./script.pl [DB_NAME] [USER] [PASSWORD]\n";
	exit (1);
}

&vacuum_analyze ($ARGV[0],
				 $ARGV[1],
	             $ARGV[2]);

&alter_sequences ($ARGV[0],
				  $ARGV[1],
	              $ARGV[2]);

