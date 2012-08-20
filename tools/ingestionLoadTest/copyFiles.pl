#!/usr/bin/perl

use File::Copy;

my $lzRoot = "/ingestion/lz/inbound";
my $count  = 100;

my $last = $count -1 ;
for(0..$last) {
    my $i = $_;
    my $to = "$lzRoot/LTEST$i/$i-MediumSampleDataSet.zip"; 
    copy("MediumSampleDataSet.zip", $to);
    print "Copying to [$to]\n";
}
