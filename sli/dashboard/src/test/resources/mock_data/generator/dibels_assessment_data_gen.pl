use POSIX;
use List::Util qw[min max];

# Generates assessment json for a given list of students.
#
# Generates one result for the current window, and one for the previous window. 
# 
# Arguments: 
#  student_file assement_metadata_file current_year_period skip_probability
#
# student_file format: 
#  student_uid, current_grade
# where student_uid is any string, and grade is [0-3]
# 
# assessment_metadata_file format: 
#  see dibels.txt
#
# current_year_period: 
#  yyyy-PPP, where yyyy is a four-digit year, PPP is [BME]OY
#
# skip_probability 
#   a number between 1 and 100


# Constants
my $PERCENTILE_VARIABILITY = 10;
my $ASSESSMENT_CODE = "DIBELS_NEXT";

if ($#ARGV != 3) { 
    die "Usage: student_file assement_metadata_file current_year_period skip_probability"; 
}

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $current_year_period = $ARGV[2];
my $skip_probability = $ARGV[3];


my %percentileDistrib = ();
my %scoreRange = ();
my %cutpoint = ();

# build metadata structure
open (INPUT_METADATA, $metaDataFile);
while($line = <INPUT_METADATA>)
{
    if ($line =~ /^\#/) { next; }
    if ($line =~ /^$/) { next; }
    chomp ($line);
    # Should be a window designator
    $window = &windowCode($line); 
    # next should be the percentile array
    my $percentileLine = <INPUT_METADATA>;
    chomp ($percentileLine);
    my @percentile = split (/,/, $percentileLine);
    # next is the score range array
    my $scoreRangeLine = <INPUT_METADATA>;
    chomp ($scoreRangeLine);
    my @scoreRange = split (/,/, $scoreRangeLine);
    # next is the cutpoint array
    my $cutpointLine = <INPUT_METADATA>;
    chomp ($cutpointLine);
    my @cutpoint = split (/,/, $cutpointLine);
    # store everything: 
    $percentileDistrib{$window} = \@percentile;
    $scoreRange{$window} = \@scoreRange;
    $cutpoint{$window} = \@cutpoint;

    # debugging
    # print $scoreRangeLine . "\n";
    # print $percentileLine . "\n";
    # print $cutpointLine . "\n";
    # print $window . "\n";
}
close (INPUT_METADATA);

# Debugging
#my $scoreRangeRef = $scoreRange{"2-MOY"};
#my @scoreRange = @{$scoreRangeRef};
#for(my $i = 0; $i < $#scoreRange; $i++) {
#    print $i . ": " . $scoreRange[$i] . " to " . $scoreRange[$i+1] . "\n" ;
#}

# find out the current and previous assessment window
# Assume BOYs are in September, MOYs are in January, and EOYs are in May
my ($current_year, $current_period) = split ('-', $current_year_period);
my $previous_year = $current_period eq "MOY" ? $current_year - 1 : $current_year;
my $previous_period;
if ($current_period eq "MOY") { $previous_period = "BOY"; }
elsif ($current_period eq "EOY") { $previous_period = "MOY"; }
elsif ($current_period eq "BOY") { $previous_period = "EOY"; }
else { die " incorrect current year/period specified: " . $current_year_period; }


# for each student, randomly generate two percentiles within $PERCENTILE_VARIABILITY percent of each other
# (even distribution). 
# Then generate his score according to the score spread for a given percentile rank, and then look up the 
# performance level. 
# Store all results into the result array. The elements are <studentId, grade, year, perfLevel, score, percentile, period> tuple. 
my @results = ();
open (INPUT_STUDENT, $studentFile);
while ($line = <INPUT_STUDENT>)
{
    if ($line =~ /^\#/) { next; }
    chomp($line);
    my ($studentUid, $current_grade) = split (/,/, $line);

    my $percentile_previous = rand() * 100;
    my $percentile_current = -1;
    do {
	my $variability = rand() * 2 * $PERCENTILE_VARIABILITY - $PERCENTILE_VARIABILITY;
	$percentile_current = $percentile_previous + $variability;
    } while ($percentile_current >= 100 || $percentile_current <= 0);

    # determine grade in previous assessment window. 
    # (If we're in grade K BOY, previous grade would still be grade K) 
    $current_grade = $current_grade + 0; 
    my $previous_grade = $current_grade == 0 ? 0 : ($current_period eq "BOY") ? $current_grade - 1 : $current_grade;

    # for each window, determine the score and perflevels and package into assessment object
    $currentAssessmentObj = &createAssessment($studentUid, $percentile_current, $current_grade, $current_year, $current_period);
    $previousAssessmentObj = &createAssessment($studentUid, $percentile_previous, $previous_grade, $previous_year, $previous_period);

    # determine if the window would be skipped
    my $skip_rand;
    $skip_rand = rand();
    if ($skip_rand * 100 >= $skip_probability) { push (@results, $currentAssessmentObj); }
    $skip_rand = rand();
    if ($skip_rand * 100 >= $skip_probability) { push (@results, $previousAssessmentObj); }

    # Debugging 
    # @assmtObj = @{$previousAssessmentObj};
    # for(my $i = 0; $i <= $#assmtObj; $i++) { print $assmtObj[$i] . "\n"; }
    # print "\n";
}
close INPUT_STUDENT;

# Print out the results as a json array
print "[\n";
for(my $i = 0; $i <= $#results; $i++)
{
    @assessment = @{$results[$i]};
    print "{\n";
    print "        \"studentId\": \"" . $assessment[0] . "\",\n";
    print "        \"grade\": \"" . $assessment[1] . "\",\n";
    print "        \"year\": \"" . $assessment[2] . "\",\n";
    print "        \"perfLevel\": \"" . $assessment[3] . "\",\n";
    print "        \"scaleScore\": \"" . $assessment[4] . "\",\n";
    print "        \"percentile\": \"" . $assessment[5] . "\",\n"; 
    print "        \"assessmentFamilyName\": \"" . $ASSESSMENT_CODE . "\",\n";
    print "        \"assessmentName\": \"" . $ASSESSMENT_CODE . "_GRADE_" . $assessment[1] . "_" . $assessment[6] . "\"\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";





#--------------- Subroutines ----------------------

# I don't know why the chomp didn't work. This cleans up the window code from the file
sub windowCode() {
    my $s = $_[0];
    if ($s =~ /^0-BOY/) { return "0-BOY"; }
    if ($s =~ /^0-MOY/) { return "0-MOY"; }
    if ($s =~ /^0-EOY/) { return "0-EOY"; }
    if ($s =~ /^1-BOY/) { return "1-BOY"; }
    if ($s =~ /^1-MOY/) { return "1-MOY"; }
    if ($s =~ /^1-EOY/) { return "1-EOY"; }
    if ($s =~ /^2-BOY/) { return "2-BOY"; }
    if ($s =~ /^2-MOY/) { return "2-MOY"; }
    if ($s =~ /^2-EOY/) { return "2-EOY"; }
    if ($s =~ /^3-BOY/) { return "3-BOY"; }
    if ($s =~ /^3-MOY/) { return "3-MOY"; }
    if ($s =~ /^3-EOY/) { return "3-EOY"; }
    die " invalid window in assessment metadata file: " . $s; 
    return $retVal;
}

# create an assessment array (a <studentId, grade, year, perfLevel, score, percentile, period> tuple). 
sub createAssessment() {
    my ($studentUid, $percentile, $grade, $year, $period) = @_;

    # see which tier the percentile is in the distribution 
    my @percentileDistrib = @{$percentileDistrib{$grade . "-" . $period}};
    my $tier = 0;
    for (my $i = 1; $i <= $#percentileDistrib; $i++) {
	if($percentile < $percentileDistrib[$i]) {
	    $tier = $i; 
	    last;
	}
    }

    # calculate the score. 
    my @scoreRange = @{$scoreRange{$grade . "-" . $period}};
    my $top = $scoreRange[$tier-1];
    my $bottom = $scoreRange[$tier];
    my $score = rand() * ($top - $bottom) + $bottom;

    # calculate the level
    my @cutpoint = @{$cutpoint{$grade . "-" . $period}};
    my $perfLevel = 0;
    for (my $i = 0; $i <= $#cutpoint; $i++) {
	if ($score < $cutpoint[$i]) {
	    $perfLevel = $i + 1;
	    last;
	}
    }

    # debugging
    # print $grade . "-" . $period . " " . $percentile . " => Tier is: " . $tier . "; Score is: " . $score . " PerfLevel is: " . $perfLevel .  "\n";

    # formatting for output
    $percentile = floor ($percentile * 100) / 100;
    $score = floor($score);
    $grade = $grade == 0 ? "K" : $grade;
    # put into result array
    my @thisAssessment = ($studentUid, $grade, $year, $perfLevel, $score, $percentile, $period);
    return \@thisAssessment;
}
