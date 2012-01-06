use POSIX;
use List::Util qw[min max];

# Generates assessment json for a given list of students.
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
#  grade
#    lowest_score,min_level2_score,min_level3_score,min_level4_score,highest_score
#    prob. on or below scale 1,prob. on or below scale 2, etc... 
#   :
#   :
# 
# omit_percentile_rank: 
#   [y|n]
# 
# skip_probability: 
#   a number between 1 and 100

if ($#ARGV != 5) { 
    die "Usage: student_file assement_metadata_file assessment_code current_year omit_percentile_rank skip_probability"; 
}

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $assessmentCode = $ARGV[2];
my $current_year = $ARGV[3];
my $omit_percentile_rank = $ARGV[4];
my $skip_probability = $ARGV[5];

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
    # Enter two years' worth of student data, the student is never retained. 
    for(my $grade = max($current_grade-1, 3); $grade <= $current_grade; $grade++) {
        # determine if this window would be skipped
	my $skip_rand = rand();
        if ($skip_rand * 100 < $skip_probability) { next; }

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
	$percentile = floor ($percentile * 10000) / 100;

        # deternube the lexile
        my $lexile = $percentile * 12 + 100; # this should give me a number between 100 and 1300
        $lexile = floor($lexile) . "L";

	# debug
        # print $random_number . " " . $grade . " " . $score . " " . $scale . " " . $percentile . "\n";
	# put into result array
	my @thisAssessment = ($studentUid, $grade, $grade - $current_grade + $current_year, $scale + 1, $score, $percentile, $lexile);
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
    print "        \"assessmentFamilyName\": \"" . $assessmentCode . "\",\n";
    print "        \"grade\": \"" . $assessment[1] . "\",\n";
    print "        \"year\": \"" . $assessment[2] . "\",\n";
    print "        \"perfLevel\": \"" . $assessment[3] . "\",\n";
    print "        \"scaleScore\": \"" . $assessment[4] . "\",\n";
    if (!($omit_percentile_rank eq 'y') ) { print "        \"percentile\": \"" . $assessment[5] . "\",\n"; }
    print "        \"lexileScore\": \"" . $assessment[6] . "\",\n";
    print "        \"assessmentName\": \"" . $assessmentCode . "_GRADE_" . $assessment[1] . "_" . $assessment[2] . "\"\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";

