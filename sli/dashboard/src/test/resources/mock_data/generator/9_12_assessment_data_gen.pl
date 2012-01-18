use POSIX;
use List::Util qw[min max];

# Generates assessment json for a given list of students.
# 
# Generates data for the current year and the preceeding year. 
# 
# Arguments: 
#  student_file assement_metadata_file assessment_code current_year omit_percentile_rank skip_probability
# 
# All files are csv. 
# 
# student_file format: 
#  student_uid, current_grade
# where student_uid is any string, and grade is [3-8]
#
# assessment_metadata_file: 
#    lowest_score,min_level2_score,min_level3_score,min_level4_score,highest_score
#    prob. on or below scale 1,prob. on or below scale 2, etc... 
#   :
# 
# omit_percentile_rank: 
#   [y|n]
# 
# skip_probability: 
#   a number between 1 and 100

if ($#ARGV != 4) { 
    die "Usage: student_file assement_metadata_file assessment_code year skip_probability "; 
}

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $assessmentCode = $ARGV[2];
my $year = $ARGV[3];
my $skip_probability = $ARGV[4];

my @scoreRange = ();
my @probDistrib = ();

# build meta data structure: 
#  %probDistrib and 
#  %scoreRange
open (INPUT_METADATA, $metaDataFile);
while($line = <INPUT_METADATA>)
{
    if ($line =~ /^\#/) { next; }
    chomp ($line);
    my $scoreRangeLine = $line;
    @scoreRange = split (/,/, $scoreRangeLine);
    my $probDistribLine = <INPUT_METADATA>;
    @probDistrib = split (/,/, $probDistribLine);
}
close INPUT_METADATA;

## Debugging
#for(my $i = 0; $i <= $#scoreRange; $i++) {
#    print $scoreRange[$i] . " ";
#}
#print "\n";
#for(my $i = 0; $i <= $#probDistrib; $i++) {
#    print $probDistrib[$i] . " ";
#}
#print "\n";

# for each student, insert all historical data, governed by the probability 
# he'll be in a particular scale.
# Store all results into the result array. The elements are <studentId, scale> tuple. 
my @results = ();
open (INPUT_STUDENT, $studentFile);
while ($line = <INPUT_STUDENT>)
{
    if ($line =~ /^\#/) { next; }
    chomp($line);
    $studentUid = $line;

    # determine if this window would be skipped
    my $skip_rand = rand();
    if ($skip_rand * 100 < $skip_probability) { next; }

    # determine the bucket
    my $random_number = rand();
    my $score = 0;
    for(my $i = 0; $i <= $#probDistrib; $i++) {
	if($random_number <= $probDistrib[$i]) {
	    $score = $scoreRange[$i];
	    last;
	}
    }

    # debug
    # print $random_number . " " . $grade . " " . $score . " " . $scale . " " . $percentile . "\n";
    # put into result array
    my @thisAssessment = ($studentUid, $score);
    push (@results, \@thisAssessment);
}
close INPUT_STUDENT;

# Print out the results as a json array
print "[\n";
for(my $i = 0; $i <= $#results; $i++)
{
    @assessment = @{$results[$i]};
    print "{\n";
    print "        \"id\": \"TBD\",\n";
    print "        \"administrationDate\": \"" . $year . "-01-01\",\n";
    print "        \"studentId\": \"" . $assessment[0] . "\",\n";
    print "        \"assessmentId\": \"" . $assessmentCode . "\",\n";
    print "        \"links\" : [], \n";
    print "        \"administrationEndDate\": \"" . $year . "-01-01\",\n";
    print "        \"scoreResults\": [ ], \n";
    print "        \"performanceLevel\": \"" . $assessment[1] . "\",\n";
    print "        \"retestIndicator\": \"1\"\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";
