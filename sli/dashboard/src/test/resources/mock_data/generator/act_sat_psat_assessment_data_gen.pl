use POSIX;
use List::Util qw[min max];

# Generates assessment json for a given list of students.
# 
# Generates data for the current year and the preceeding year. 
# 
# Arguments: 
#  student_file assement_metadata_file year skip_probability
# 
# All files are csv. 
# 

if ($#ARGV != 3) { 
    die "Usage: student_file assement_metadata_file year skip_probability "; 
}

my $studentFile = $ARGV[0];
my $metaDataFile = $ARGV[1];
my $year = $ARGV[2];
my $skip_probability = $ARGV[3];

my $assessmentCode = "";
my @scores = ();
my @percentiles = ();
my @heuristics = ();

# build meta data structure: 
#  %scores and 
#  %percentiles
open (INPUT_METADATA, $metaDataFile);
while($line = <INPUT_METADATA>)
{
    if ($line =~ /^\#/) { next; }
    chop ($line);
    chop ($line); # need to chop twice because of DOS newline and carriage return, i think
    $assessmentCode = $line;
    my $scoreLine = <INPUT_METADATA>;
    chop($scoreLine);
    chop($scoreLine);
    @scores = split (/,/, $scoreLine);
    my $percentileLine = <INPUT_METADATA>;
    chop($percentileLine);
    chop($percentileLine);
    @percentiles = split (/,/, $percentileLine);
    
    #my @thisHeuristic = ($assessmentCode, @scores, @percentiles);
    #push (@heuristics, \@thisHeuristic);
#}
#close INPUT_METADATA;

## Debugging
#print $assessmentCode . "\n";
#for(my $i = 0; $i <= $#scores; $i++) {
#    print $scores[$i] . " ";
#}
#print "\n";
#for(my $i = 0; $i <= $#percentiles; $i++) {
#    print $percentiles[$i] . " ";
#}
#print "\n";

# for each set of assessment heuristics, create assessment results for all students
for(my $h = 0; $h <= $#heuristics; $h++)
{
    @heuristic = @{$heuristics[$h]};
    $assessmentCode = $heuristic[0];
    @scores = @{$heuristic[1]};
    @percentiles = @{$heuristic[2]};
    print $assessmentCode . "\n";
    print @scores . "\n";
    print @percentiles . "\n";
}

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

    # determine the percentile and score
    my $random_number = rand();
    my $percentile = int($random_number * 99);
    my $score = 0;
    for(my $i = 0; $i <= $#percentiles; $i++) {
        if($percentile < $percentiles[$i]) {
            $a = ($percentile - $percentiles[$i-1]) / ($percentiles[$i] - $percentiles[$i-1]);
            $b = ($scores[$i] - $scores[$i-1]);
            $score = int(($a * $b) + $scores[$i-1]);
            if(($assessmentCode eq "SAT_READING") || ($assessmentCode eq "SAT_WRITING")) {
                $score = int($score / 10) * 10;
            }
            last;
        }
    }

#    my $score = 0;
#    for(my $i = 0; $i <= $#probDistrib; $i++) {
#    if($random_number <= $probDistrib[$i]) {
#        $score = $scoreRange[$i];
#        last;
#    }
#    }

    # debug
    # put into result array
    my @thisAssessment = ($studentUid, $score, $percentile);
    push (@results, \@thisAssessment);
}
close INPUT_STUDENT;

# Print out the results as a json array
#print "[\n";
for(my $i = 0; $i <= $#results; $i++)
{
    @assessment = @{$results[$i]};
    print "{\n";
    print "        \"studentId\": \"" . $assessment[0] . "\",\n";
    print "        \"year\": \"" . $year . "\",\n";
    print "        \"scaleScore\": \"" . $assessment[1] . "\",\n";
    print "        \"percentile\": \"" . $assessment[2] . "\",\n";
    print "        \"assessmentName\": \"" . $assessmentCode . "\"\n";
#    print "}" . ($i == $#results ? "" : ",") . "\n";
    print "},\n";
}
#print "]\n";

# The following lines prints out the result in "API-compatible" format... du jour.
# print "[\n";
# for(my $i = 0; $i <= $#results; $i++)
# {
#     @assessment = @{$results[$i]};
#     print "{\n";
#     print "        \"id\": \"TBD\",\n";
#     print "        \"administrationDate\": \"" . $year . "-01-01\",\n";
#     print "        \"studentId\": \"" . $assessment[0] . "\",\n";
#     print "        \"assessmentId\": \"" . $assessmentCode . "\",\n";
#     print "        \"links\" : [], \n";
#     print "        \"administrationEndDate\": \"" . $year . "-01-01\",\n";
#     print "        \"scoreResults\": [ ], \n";
#     print "        \"performanceLevel\": \"" . $assessment[1] . "\",\n";
#     print "        \"retestIndicator\": \"1\"\n";
#     print "}" . ($i == $#results ? "" : ",") . "\n";
# }
# print "]\n";
# 

}
close INPUT_METADATA;

