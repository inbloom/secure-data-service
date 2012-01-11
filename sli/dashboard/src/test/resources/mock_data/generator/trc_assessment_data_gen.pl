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
# where student_uid is any string, and grade is [1-3]
# 
# assessment_metadata_file format: 
#  see trc.txt
#
# current_year_period: 
#  yyyy-PPP, where yyyy is a four-digit year, PPP is [BME]OY
#
# skip_probability 
#   a number between 1 and 100


# Constants
my $ASSESSMENT_CODE = "TRC";

if ($#ARGV != 3) { 
    die "Usage: student_file assement_metadata_file current_year_period skip_probability"; 
}

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $current_year_period = $ARGV[2];
my $skip_probability = $ARGV[3];


my @possibleScores = [];
my @possibleLables = [];
my %cutpoint = ();


# build metadata structure
open (INPUT_METADATA, $metaDataFile);
# 1) scores array
while($line = <INPUT_METADATA>) { if ($line =~ /^\#/) { next; } last; }
chomp ($line);
@possibleScores = split (/,/, $line);
# 2) labels array
while($line = <INPUT_METADATA>) { if ($line =~ /^\#/) { next; } last; }
chomp ($line);
@possibleLabels = split (/,/, $line);
# 3) cutpoints
while ($line = <INPUT_METADATA>) 
{
    if ($line =~ /^\#/) { next; }
    if ($line =~ /^$/) { next; }
    chomp ($line);
    # Should be a window designator
    $window = &windowCode($line); 

    # next should be the cutpoints array
    my $cutpointLine = <INPUT_METADATA>;
    chomp ($cutpointLine);
    my @cutpoint = split (/,/, $cutpointLine); 
    $cutpoint{$window} = \@cutpoint;
}
close (INPUT_METADATA);

# debug
#for(my $i = 0; $i <= $#possibleLabels; $i++)
#{
#    print $i . " " . $possibleLabels[$i] . " == " . $possibleScores[$i] . "\n";
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

# for each student, randomly generate scores such that the distribution among the cutpoints are even 
# Generate one result for current period, and one for previous period. 
my @results = ();
open (INPUT_STUDENT, $studentFile);
while ($line = <INPUT_STUDENT>)
{
    if ($line =~ /^\#/) { next; }
    chomp($line);
    my ($studentUid, $current_grade) = split (/,/, $line);

    # determine previous grade 
    $current_grade = $current_grade + 0; 
    if ($current_grade == 0) { next; } # We don't generate data for kindergarten. 
    my $previous_grade = $current_grade == 1 ? 1 : ($current_period eq "BOY") ? $current_grade - 1 : $current_grade;

    # for each window, determine the score and perflevels and package into assessment object
    $currentAssessmentObj = &createAssessment($studentUid, $percentile_current, $current_grade, $current_year, $current_period);
    $previousAssessmentObj = &createAssessment($studentUid, $percentile_previous, $previous_grade, $previous_year, $previous_period);

    # determine if the window would be skipped
    my $skip_rand;
    $skip_rand = rand();
    if ($skip_rand * 100 >= $skip_probability) { push (@results, $currentAssessmentObj); }
    $skip_rand = rand();
    if ($skip_rand * 100 >= $skip_probability) { push (@results, $previousAssessmentObj); }
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
    print "        \"lexileScore\": \"" . $assessment[5] . "\",\n"; 
    print "        \"assessmentFamilyName\": \"" . $ASSESSMENT_CODE . "\",\n";
    print "        \"assessmentName\": \"" . $ASSESSMENT_CODE . "_GRADE_" . $assessment[1] . "_" . $assessment[6] . "\"\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";




# I don't know why the chomp didn't work. This cleans up the window code from the file
sub windowCode() {
    my $s = $_[0];
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

# create an assessment array (a <studentId, grade, year, perfLevel, score, scoreLabel, period> tuple). 
sub createAssessment() {
    my ($studentUid, $percentile, $grade, $year, $period) = @_;

    # determine the perf level
    my @cutpoint = @{$cutpoint{$grade . "-" . $period}};
    my $level = floor(rand() * ($#cutpoint)) + 1;

    # determine score 
    my $scoreIndex = floor(rand() * ($cutpoint[$level] - $cutpoint[$level-1]));
    $scoreIndex += $cutpoint[$level-1];

    my $score = $possibleScores[$scoreIndex];
    my $scoreLabel = $possibleLabels[$scoreIndex];

    # debug
    # print $level . ", " . $#cutpoint . " " . $scoreIndex . " " . $score . " " . $scoreLabel . " \n";

    # put into result array
    my @thisAssessment = ($studentUid, $grade, $year, $level, $score, $scoreLabel, $period);
    return \@thisAssessment;
}
