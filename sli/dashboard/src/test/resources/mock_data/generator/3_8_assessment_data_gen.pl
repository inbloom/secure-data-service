use POSIX;

# Generates assessment json for a given list of students.
# 
# Arguments: 
#  student_file assement_metadata_file assessment_code current_year
# 
# All files are csv. 
# 
# student_file format: 
#  student_uid, current_grade
# where student_uid is any string, and grade is [3-8]
#
# assessment_metadata_file: 
#  grade
#    lowest_score,min_level2_score,min_level3_score,min_level4_score,highest_score
#    prob. on or below scale 1,prob. on or below scale 2, etc... 
#   :
#   :
#

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $assessmentCode = $ARGV[2];
my $current_year = $ARGV[3];

my %scoreRange = ();
my %probDistrib = ();

# build meta data structure: 
#  %probDistrib and 
#  %scoreRange
open (INPUT_METADATA, $metaDataFile);
while($line = <INPUT_METADATA>)
{
    if ($line =~ /^\#/) { next; }
    chomp ($line);
    my $grade = $line + 0;
    my $scoreRangeLine = <INPUT_METADATA>;
    my @scoreRange = split (/,/, $scoreRangeLine);
    my $probDistribLine = <INPUT_METADATA>;
    my @probDistrib = split (/,/, $probDistribLine);
    $scoreRange{$grade} = \@scoreRange;
    $probDistrib{$grade} = \@probDistrib;
}
close INPUT_METADATA;

## Debugging
#my $scoreRangeRef = $scoreRange{7};
#my @scoreRange = @{$scoreRangeRef};
#for(my $i = 0; $i <= $#scoreRange; $i++) {
#    print $scoreRange[$i] . " ";
#}
#my $probDistribRef = $probDistrib{7};
#my @probDistrib = @{$probDistribRef};
#for(my $i = 0; $i <= $#probDistrib; $i++) {
#    print $probDistrib[$i] . " ";
#}

# for each student, insert all historical data, governed by the probability 
# he'll be in a particular scale. 
# Store all results into the result array. The elements are <studentId, grade, year, scale, scale score, percentile> tuple. 
my @results = ();
open (INPUT_STUDENT, $studentFile);
while ($line = <INPUT_STUDENT>)
{
    if ($line =~ /^\#/) { next; }
    chomp($line);
    ($studentUid, $current_grade) = split (/,/, $line);
    # there is an assessment instance for each grade from 3 till the current grade, 
    # and the student is never retained. 
    for(my $grade = 3; $grade <= $current_grade; $grade++) {
	# determine the scale
	my @probDistrib = @{$probDistrib{$grade}};
	my $random_number = rand();
	my $scale = 0;
	for(my $i = 0; $i <= $#probDistrib; $i++) {
	    if($random_number <= $probDistrib[$i]) {
		$scale = $i;
		last;
	    }
	}
	# determine the scaled score
	my @scoreRange = @{$scoreRange{$grade}};
	my $score = rand() * ($scoreRange[$scale + 1]  - $scoreRange[$scale]);
	$score = $score + $scoreRange[$scale];
	$score = floor($score);

	# determine the percentile
	my $percentileLow = $scale == 0 ? 0 : $probDistrib[$scale-1];
        my $percentile = rand() * ($probDistrib[$scale] - $percentileLow);
	$percentile = $percentile + $percentileLow;
	$percentile = $percentile * 100;
	$percentile = floor($percentile);

	# debug
        # print $random_number . " " . $grade . " " . $score . " " . $scale . " " . $percentile . "\n";
	# put into result array
	my @thisAssessment = ($studentUid, $grade, $grade - $current_grade + $current_year, $scale, $score, $percentile);
	push (@results, \@thisAssessment);
    }
}
close INPUT_STUDENT;

# Print out the results as a json array
print "[\n";
for(my $i = 0; $i <= $#results; $i++)
{
    @assessment = @{$results[$i]};
    print "{\n";
    print "        \"studentId\": \"" . $assessment[0] . "\",\n";
    print "        \"assessmentName\": \"" . $assessmentCode . "\",\n";
    print "        \"grade\": \"" . $assessment[1] . "\",\n";
    print "        \"year\": \"" . $assessment[2] . "\",\n";
    print "        \"perfLevel\": \"" . $assessment[3] . "\",\n";
    print "        \"scaleScore\": \"" . $assessment[4] . "\",\n";
    print "        \"percentile\": \"" . $assessment[5] . "\",\n";
    print "        \"lexileScore\": \"" . $assessment[4] . "\",\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";

