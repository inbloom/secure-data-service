use POSIX;
use List::Util qw[min max];

# Generates assessment json for a given list of students.
# 
# Generates data for the current year and the preceeding year. 
# 
# Arguments: 
#  student_file assement_metadata_file assessment_code year skip_probability
# 
# All files are csv. 
# 

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
    print "        \"studentId\": \"" . $assessment[0] . "\",\n";
    print "        \"year\": \"" . $year . "\",\n";
    print "        \"perfLevel\": \"" . $assessment[1] . "\",\n";
    print "        \"assessmentName\": \"" . $assessmentCode . "\"\n";
    print "}" . ($i == $#results ? "" : ",") . "\n";
}
print "]\n";

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
