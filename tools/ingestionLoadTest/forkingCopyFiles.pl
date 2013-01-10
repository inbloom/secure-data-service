#!/usr/bin/perl

use File::Copy;

my $count  = $ARGV[0];
my $lzRoot = 'C:\Users\ldalgado\repo5\sli\sli\ingestion\ingestion-service\target\ingestion\lz\inbound';
#C:\Users\ldalgado\repo5\sli\sli\ingestion\ingestion-service\target\ingestion\lz\inbound/LTEST99/101-ccj-24Aug2012-13-44-MediumSampleDataSet.zip
my $threadCount = 10; #processCount actually

die "Count parameter not defined" if(!defined($count));

my @abbr = qw( Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec );
my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
my $month = $abbr[$mon];
$year += 1900;
my $stamp = "$mday$month$year-$hour-$min";

$threadCount = 1 if($count < $threadCount);
my $filesPerThread = int($count/$threadCount);

for(0..($threadCount - 1)){
    my $id = $_;
    my $pid = fork();
    if($pid != 0) {
        my $start = $id * $filesPerThread;
        my $end   = $id * $filesPerThread + $filesPerThread -1;
        $end = $count - 1 if ($id == ($threadCount - 1));
        kopy($id, $start, $end);
	last;
    }
}

sub kopy{
    my ($id, $start, $end) = @_;
    for($start..$end) {
        my $to = "$lzRoot/LTEST$_/$count-ccj-$stamp-MediumSampleDataSet.zip"; 
        print "$id-Copying to [$to]\n";
        copy("MediumSampleDataSet.zip", $to) or die "Cannot copy:$!";
    }
}
