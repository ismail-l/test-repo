#!/usr/bin/env perl -w
#
#  Autshumato Terminology Management System (TMS)
#  Free web application for the management of multilingual terminology databases (termbanks).
#
#  Copyright (C) 2011 Centre for Text Technology (CTexT®), North-West University
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

use strict;
use warnings;

use Archive::Zip qw( :ERROR_CODES :CONSTANTS );
use File::Copy;
use File::Path qw( make_path remove_tree );
use FindBin;
use IO::File;

sub copy_recursively {
    my($from_dir,$to_dir,$regex) = @_;
    opendir( my($dh), $from_dir ) or die("Could not open dir '$from_dir': $!");
    for my $entry ( readdir($dh) ) {
        next if ( $entry =~ /^\.{1,2}$/);
        next if ( $entry =~ /$regex/ );
        my $source = "$from_dir/$entry";
        my $destination = "$to_dir/$entry";
        if (-d $source) {
            mkdir($destination) or die("mkdir '$destination' failed: $!"),
                if ( not -e $destination );
            copy_recursively($source, $destination, $regex);
        } else {
            copy($source, $destination) or die("copy failed: $!");
        }
    }
    closedir($dh);
    return;
}

my $base = $FindBin::Bin;
my $zip = Archive::Zip->new();

autoflush STDOUT 1;

chdir($base);

eval {
    if ( -d "$base/tmp" ) {
        print "Removing old temp directory ...";
        remove_tree("$base/tmp");
        print " done\n";
    }

    print "Copying war/ to tmp/tms/ ...";
    make_path("$base/tmp/tms");
    copy_recursively("$base/war", "$base/tmp/tms", '.svn');
    print " done\n";

    print "Creating tms.war ...";
    unlink('tms.war') if ( -f 'tms.war' );
    $zip->addTree("$base/tmp/tms", 'tms');
    $zip->writeToFileNamed('tms.war') == AZ_OK or die("Could not write war: $!");
    print " done\n";

    print "Removing temp directory ...";
    remove_tree("$base/tmp");
    print " done\n";
};
if ($@) {
    print "\n\n$@";
}

if ($^O eq 'MSWin32') {
    print "\nPress any key to continue ...";
    <STDIN>;
}
