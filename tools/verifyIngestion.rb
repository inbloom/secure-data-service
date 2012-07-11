=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'mongo'
require 'json'

#Talk to maestro's mongos
mongohost = ARGV[1]
if (mongohost.nil?) 
	mongohost = "nxmaestro.slidev.org"
end

mongoport = 27017

dbName = ARGV[2]
if ( dbName.nil? ) 
	dbName = "sli"
end

puts ""
puts " \==========================================================="
puts " Talking to mongo \e[32m#{mongohost}:#{mongoport} \e[0mfor #{dbName} database"
puts " \==========================================================="
puts ""

expected={
 # actual record count: 485,795
 # entities not persisted:
 # - assessmentFamily --> count: 85
 # - assessmentItem --> count: 375
 # - assessmentPeriodDescriptor --> count: 123
 # - objectiveAssessment --> count: 85
 # - performanceLevelDescriptor --> count: 128
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "500k" => {
  "assessment"=>163,
  "attendance"=>3000,
  "calendarDate"=>120,
  "cohort"=>60,
  "compentencyLevelDescriptor"=>0,
  "course"=>720,
  "courseOffering"=>1440,
  "disciplineAction"=>3000,
  "disciplineIncident"=>3000,
  "educationOrganization"=>34,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>480,
  "graduationPlan"=>30,
  "learningObjective"=>46,
  "learningStandard"=>262,
  "parent"=>4540,
  "program"=>37,
  "reportCard"=>0,
  "schoolSessionAssociation"=>0,
  "section"=>20160,
  "session"=>60,
  "staff"=>435,
  "staffCohortAssociation"=>60,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>37,
  "student"=>3000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>30000,
  "studentCohortAssociation"=>5588,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>3000,
  "studentParentAssociation"=>4540,
  "studentProgramAssociation"=>2588,
  "studentSchoolAssociation"=>3000,
  "studentSectionAssociation"=>42000,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>300,
  "teacherSectionAssociation"=>20160
 },
 
 # actual record count: 969,932
 # entities not persisted:
 # - assessmentFamily --> count: 92
 # - assessmentItem --> count: 376
 # - assessmentPeriodDescriptor --> count: 124
 # - objectiveAssessment --> count: 80
 # - performanceLevelDescriptor --> count: 114
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "1000k" => {
  "assessment"=>163,
  "attendance"=>6000,
  "calendarDate"=>240,
  "cohort"=>120,
  "compentencyLevelDescriptor"=>0,
  "course"=>1440,
  "courseOffering"=>2880,
  "disciplineAction"=>6000,
  "disciplineIncident"=>6000,
  "educationOrganization"=>67,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>960,
  "graduationPlan"=>60,
  "learningObjective"=>51,
  "learningStandard"=>245,
  "parent"=>9002,
  "program"=>73,
  "reportCard"=>0,
  "schoolSessionAssociation"=>0,
  "section"=>40320,
  "session"=>120,
  "staff"=>735,
  "staffCohortAssociation"=>120,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>73,
  "student"=>6000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>60000,
  "studentCohortAssociation"=>11208,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>6000,
  "studentParentAssociation"=>9002,
  "studentProgramAssociation"=>5208,
  "studentSchoolAssociation"=>6000,
  "studentSectionAssociation"=>84000,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>600,
  "teacherSectionAssociation"=>40320
 },
 
 # actual record count: 1,454,040
 # entities not persisted:
 # - assessmentFamily --> count: 79
 # - assessmentItem --> count: 359
 # - assessmentPeriodDescriptor --> count: 122
 # - objectiveAssessment --> count: 69
 # - performanceLevelDescriptor --> count: 112
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "1500k" => {
  "assessment"=>163,
  "attendance"=>9000,
  "calendarDate"=>360,
  "cohort"=>180,
  "compentencyLevelDescriptor"=>0,
  "course"=>2160,
  "courseOffering"=>4320,
  "disciplineAction"=>9000,
  "disciplineIncident"=>9000,
  "educationOrganization"=>100,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>1440,
  "graduationPlan"=>90,
  "learningObjective"=>34,
  "learningStandard"=>236,
  "parent"=>13539,
  "program"=>109,
  "reportCard"=>0,
  "schoolSessionAssociation"=>0,
  "section"=>60480,
  "session"=>180,
  "staff"=>1035,
  "staffCohortAssociation"=>180,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>109,
  "student"=>9000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>90000,
  "studentCohortAssociation"=>16763,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>9000,
  "studentParentAssociation"=>13539,
  "studentProgramAssociation"=>7763,
  "studentSchoolAssociation"=>9000,
  "studentSectionAssociation"=>126000,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>900,
  "teacherSectionAssociation"=>60480
 }
}

# Set total counts
expectationTotals = { "500k" => 0, "1000k" => 0, "1500k" => 0 }
expected.each do |set,collections|
  total=0
  collections.each do |name,count|
    total+=count
  end
  expectationTotals[set] = total
end

# Check that user specified the set
if ARGV.count() < 1
  puts "must specify the type of batch"
  exit
end

if !expected.has_key?(ARGV[0])
  puts " Unsupported data set size: \e[31m#{ARGV[0]}\e[0m"
  puts " Supported values: "
  puts " ------------------------------------------"
  expected.each_key {|key| puts " #{key}"}
  puts " ------------------------------------------"
  puts ""
  exit
end

connection = Mongo::Connection.new( mongohost, mongoport)

db = connection.db( dbName )

puts " Collection                            Actual(Expected)"
puts " ---------------------------------------------------------"

allCountsCorrect=true
totalActualCount = 0
setName = ARGV[0]
expected[ setName ].each do |collectionName,expectedCount|
  coll = db.collection(collectionName)
  count = coll.count()
  totalActualCount += count
  color="\e[32m"
  if expectedCount != count
    allCountsCorrect=false
    color="\e[31m"
    printf " #{color}%-40s %d(%d)\e[0m\n",collectionName,count,expectedCount
  end  
end

if allCountsCorrect
  color="\e[32m"
  printf "#{color} All collections have correct count! \n"
end

expectedSetTotal = expectationTotals[setName]
if expectedSetTotal != totalActualCount
  color="\e[31m"
  printf "#{color} Ingestion failed! \n\n"
else
  color="\e[32m"
  printf "#{color} Ingestion succeeded! \n\n"
end
