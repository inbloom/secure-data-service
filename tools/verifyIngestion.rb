=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
 # actual record count: 24126105
 # entities not persisted:
 # - assessmentFamily --> count: 81
 # - assessmentItem --> count: 371
 # - assessmentPeriodDescriptor --> count: 112
 # - courseOffering --> count: 0
 # - objectiveAssessment --> count: 83
 # - performanceLevelDescriptor --> count: 117
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "DE1113_25M" => {
  "assessment"=>163,
  "attendance"=>150000,
  "calendarDate"=>6000,
  "cohort"=>3000,
  "compentencyLevelDescriptor"=>0,
  "course"=>36000,
  "disciplineAction"=>150000,
  "disciplineIncident"=>150000,
  "educationOrganization"=>1531,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>12000,
  "graduationPlan"=>1500,
  "learningObjective"=>44,
  "learningStandard"=>242,
  "parent"=>224962,
  "program"=>1561,
  "reportCard"=>0,
  "section"=>1008000,
  "session"=>3000,
  "staff"=>15135,
  "staffCohortAssociation"=>3000,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>1561,
  "student"=>150000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>1500000,
  "studentCohortAssociation"=>279790,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>150000,
  "studentParentAssociation"=>224962,
  "studentProgramAssociation"=>129790,
  "studentSchoolAssociation"=>150000,
  "studentSectionAssociation"=>2100000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>15000,
  "teacherSectionAssociation"=>1008000
 },

# actual record count: 4826466
 # entities not persisted:
 # - assessmentFamily --> count: 73
 # - assessmentItem --> count: 374
 # - assessmentPeriodDescriptor --> count: 121
 # - courseOffering --> count: 0
 # - objectiveAssessment --> count: 90
 # - performanceLevelDescriptor --> count: 137
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "DE1113_5M" => {
  "assessment"=>163,
  "attendance"=>30000,
  "calendarDate"=>1200,
  "cohort"=>600,
  "compentencyLevelDescriptor"=>0,
  "course"=>7200,
  "disciplineAction"=>30000,
  "disciplineIncident"=>30000,
  "educationOrganization"=>307,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>2400,
  "graduationPlan"=>300,
  "learningObjective"=>46,
  "learningStandard"=>256,
  "parent"=>45104,
  "program"=>313,
  "reportCard"=>0,
  "section"=>201600,
  "session"=>600,
  "staff"=>3135,
  "staffCohortAssociation"=>600,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>313,
  "student"=>30000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>300000,
  "studentCohortAssociation"=>55865,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>30000,
  "studentParentAssociation"=>45104,
  "studentProgramAssociation"=>25865,
  "studentSchoolAssociation"=>30000,
  "studentSectionAssociation"=>420000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>3000,
  "teacherSectionAssociation"=>201600
 },

 # actual record count: 1448952
 # entities not persisted:
 # - assessmentFamily --> count: 89
 # - assessmentItem --> count: 389
 # - assessmentPeriodDescriptor --> count: 117
 # - courseOffering --> count: 0
 # - objectiveAssessment --> count: 81
 # - performanceLevelDescriptor --> count: 144
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "DE1113_1.5M" => {
  "assessment"=>163,
  "attendance"=>9000,
  "calendarDate"=>360,
  "cohort"=>180,
  "compentencyLevelDescriptor"=>0,
  "course"=>2160,
  "disciplineAction"=>9000,
  "disciplineIncident"=>9000,
  "educationOrganization"=>100,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>720,
  "graduationPlan"=>90,
  "learningObjective"=>44,
  "learningStandard"=>246,
  "parent"=>13485,
  "program"=>109,
  "reportCard"=>0,
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
  "studentParentAssociation"=>13485,
  "studentProgramAssociation"=>7763,
  "studentSchoolAssociation"=>9000,
  "studentSectionAssociation"=>126000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>900,
  "teacherSectionAssociation"=>60480
 },

 # actual record count: 966714
 # entities not persisted:
 # - assessmentFamily --> count: 83
 # - assessmentItem --> count: 393
 # - assessmentPeriodDescriptor --> count: 126
 # - courseOffering --> count: 0
 # - objectiveAssessment --> count: 91
 # - performanceLevelDescriptor --> count: 131
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "DE1113_1M" => {
  "assessment"=>163,
  "attendance"=>6000,
  "calendarDate"=>240,
  "cohort"=>120,
  "compentencyLevelDescriptor"=>0,
  "course"=>1440,
  "disciplineAction"=>6000,
  "disciplineIncident"=>6000,
  "educationOrganization"=>67,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>480,
  "graduationPlan"=>60,
  "learningObjective"=>43,
  "learningStandard"=>262,
  "parent"=>9069,
  "program"=>73,
  "reportCard"=>0,
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
  "studentParentAssociation"=>9069,
  "studentProgramAssociation"=>5208,
  "studentSchoolAssociation"=>6000,
  "studentSectionAssociation"=>84000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>600,
  "teacherSectionAssociation"=>40320
 },

 # actual record count: 485,681
 # entities not persisted:
 # - assessmentFamily --> count: 92
 # - assessmentItem --> count: 374
 # - assessmentPeriodDescriptor --> count: 119
 # - objectiveAssessment --> count: 88
 # - performanceLevelDescriptor --> count: 133
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
  "gradingPeriod"=>240,
  "graduationPlan"=>30,
  "learningObjective"=>42,
  "learningStandard"=>264,
  "parent"=>4479,
  "program"=>37,
  "reportCard"=>0,
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
  "studentParentAssociation"=>4479,
  "studentProgramAssociation"=>2588,
  "studentSchoolAssociation"=>3000,
  "studentSectionAssociation"=>42000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>300,
  "teacherSectionAssociation"=>20160
 },
 
 # actual record count: 970,113
 # entities not persisted:
 # - assessmentFamily --> count: 83
 # - assessmentItem --> count: 393
 # - assessmentPeriodDescriptor --> count: 126
 # - objectiveAssessment --> count: 91
 # - performanceLevelDescriptor --> count: 131
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "1M" => {
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
  "gradingPeriod"=>480,
  "graduationPlan"=>60,
  "learningObjective"=>43,
  "learningStandard"=>262,
  "parent"=>9069,
  "program"=>73,
  "reportCard"=>0,
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
  "studentParentAssociation"=>9069,
  "studentProgramAssociation"=>5208,
  "studentSchoolAssociation"=>6000,
  "studentSectionAssociation"=>84000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>600,
  "teacherSectionAssociation"=>40320
 },
 
 # actual record count: 1,454,031
 # entities not persisted:
 # - assessmentFamily --> count: 89
 # - assessmentItem --> count: 389
 # - assessmentPeriodDescriptor --> count: 117
 # - objectiveAssessment --> count: 81
 # - performanceLevelDescriptor --> count: 144
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "1_5M" => {
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
  "gradingPeriod"=>720,
  "graduationPlan"=>90,
  "learningObjective"=>44,
  "learningStandard"=>246,
  "parent"=>13485,
  "program"=>109,
  "reportCard"=>0,
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
  "studentParentAssociation"=>13485,
  "studentProgramAssociation"=>7763,
  "studentSchoolAssociation"=>9000,
  "studentSectionAssociation"=>126000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>900,
  "teacherSectionAssociation"=>60480
 },
 
 # actual record count: 4,843,305
 # entities not persisted:
 # - assessmentFamily --> count: 73
 # - assessmentItem --> count: 374
 # - assessmentPeriodDescriptor --> count: 121
 # - objectiveAssessment --> count: 90
 # - performanceLevelDescriptor --> count: 137
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 0
 "5M" => {
  "assessment"=>163,
  "attendance"=>30000,
  "calendarDate"=>1200,
  "cohort"=>600,
  "compentencyLevelDescriptor"=>0,
  "course"=>7200,
  "courseOffering"=>14400,
  "disciplineAction"=>30000,
  "disciplineIncident"=>30000,
  "educationOrganization"=>307,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>2400,
  "graduationPlan"=>300,
  "learningObjective"=>46,
  "learningStandard"=>256,
  "parent"=>45104,
  "program"=>313,
  "reportCard"=>0,
  "section"=>201600,
  "session"=>600,
  "staff"=>3135,
  "staffCohortAssociation"=>600,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>313,
  "student"=>30000,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>300000,
  "studentCohortAssociation"=>55865,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>30000,
  "studentParentAssociation"=>45104,
  "studentProgramAssociation"=>25865,
  "studentSchoolAssociation"=>30000,
  "studentSectionAssociation"=>420000,
  "studentGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>3000,
  "teacherSectionAssociation"=>201600
 }
}

# Set total counts
expectationTotals = { "500k" => 0, "1M" => 0, "1_5M" => 0, "5M" => 0 }
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
printf " #{color}Persisted %d of %d records (%d%% missing). \n\n",totalActualCount,expectedSetTotal,(1.0-(Float(totalActualCount)/Float(expectedSetTotal)))*100
if expectedSetTotal != totalActualCount
  color="\e[31m"
  printf "#{color} Ingestion failed! \n\n"
else
  color="\e[32m"
  printf "#{color} Ingestion succeeded! \n\n"
end
