require "json"

# Usage: ruby superCohortConverter.rb <cohort json file> <studentCohortAssociation json file> <output json file>
# Example: ruby superCohortConverter.rb unified_data/uds_cohort.json unified_data/uds_studentCohortAssociation.json unified_data/out.json

cohortFileString = "cohort_fixture.json"
ssaFileString = "studentCohortAssociation_fixture.json"
newSsaFileString = "studentCohortAssociation_superDoc_fixture.json"

if ARGV.length == 0
  # use default files
elsif ARGV.length == 3
  cohortFileString = ARGV[0]
  ssaFileString = ARGV[1]
  newSsaFileString = ARGV[2]
else
  puts "Usage: #{$0} <cohort_json_file> <studentCohortAssociation_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\tcohort_json_file = #{cohortFileString}"
  puts "\tstudentCohortAssociation_json_file = #{ssaFileString}"
  puts "\toutput_json_file = #{newSsaFileString}"
  exit(1)
end

cohortFile = File.new(cohortFileString)

newSsaFile = File.new(newSsaFileString, "w")

from = 0.0
to = Time.now

while (line = cohortFile.gets)
  line_hash = JSON.parse(line)
  cohortId = line_hash["_id"]
  ssaFile = File.new(ssaFileString)
  ssa_array = [ ]
  while (ssaLine = ssaFile.gets)
    next if ssaLine.strip == ""
    ssaLine_hash = JSON.parse(ssaLine)
    if cohortId == ssaLine_hash["body"]["cohortId"]
      ssa_array << ssaLine_hash
    end
    if ssa_array.length > 0
      line_hash["studentCohortAssociation"] = ssa_array
    end
  end
      ssaFile.close
      newSsaFile.puts line_hash.to_json
end
cohortFile.close
newSsaFile.close
