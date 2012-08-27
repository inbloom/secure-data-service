#!/usr/bin/perl

use File::Copy;

my $lzRoot = 'C:\Users\ldalgado\repo5\sli\sli\ingestion\ingestion-service\target\ingestion\lz\inbound';
my $count  = $ARGV[0];

if(!defined($count)) {die "Count parameter not defined"};

my @abbr = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $month = $abbr[$mon];
$year += 1900;
my $stamp = "$mday$month$year-$hour-$min";
for(0..($count-1)) {
    my $to = "$lzRoot/LTEST$_/$count-ccj-$stamp-MediumSampleDataSet.zip"; 
    print "Copying to [$to]\n";
    copy("MediumSampleDataSet.zip", $to) or die "Cannot copy:$!";
}
