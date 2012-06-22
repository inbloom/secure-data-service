
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

puts "\==========================================================="
puts "Talking to mongo \e[32m#{mongohost}:#{mongoport} \e[0mfor #{dbName} database"
  
expected={
 "900K" => {
  "assessment"=>4,
  "attendance"=>15700,
  "calendarDate"=>0,
  "cohort"=>0,
  "course"=>25,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>0,
  "disciplineIncident"=>0,
  "educationOrganization"=>27,
  "educationOrganizationAssociation"=>0,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>0,
  "learningObjective"=>0,
  "parent"=>0,
  "program"=>0,
  "school"=>0,
  "schoolSessionAssociation"=>25,
  "section"=>3125,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>25,
  "sessionCourseAssociation"=>0,
  "staff"=>902,
  "staffCohortAssociation"=>0,
  "staffEducationOrganizationAssociation"=>2,
  "staffProgramAssociation"=>0,
  "student"=>15700,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>235500,
  "studentCohortAssociation"=>0,
  "studentDisciplineIncidentAssociation"=>0,
  "studentParentAssociation"=>0,
  "studentProgramAssociation"=>0,
  "studentSchoolAssociation"=>15700,
  "studentSectionAssociation"=>314000,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>0,
  "teacherSchoolAssociation"=>900,
  "teacherSectionAssociation"=>3125
  },
    
 "1M" => {
  "assessment"=>163,
  "attendance"=>3984,
  "calendarDate"=>24,
  "cohort"=>12,
  "course"=>288,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>810,
  "disciplineIncident"=>5742,
  "educationOrganization"=>8,
  "educationOrganizationAssociation"=>0,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>48,
  "learningObjective"=>45,
  "parent"=>5977,
  "program"=>9,
  "school"=>0,
  "schoolSessionAssociation"=>12, 
  "section"=>4032,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>12,
  "sessionCourseAssociation"=>0,
  "staff"=>459,
  "staffCohortAssociation"=>12,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>9,
  "student"=>3984,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>39840,
  "studentCohortAssociation"=>4373,
  "studentDisciplineIncidentAssociation"=>5742,
  "studentParentAssociation"=>5977,
  "studentProgramAssociation"=>3497,
  "studentSchoolAssociation"=>3984,
  "studentSectionAssociation"=>55776,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>0,
  "teacherSchoolAssociation"=>324,
  "teacherSectionAssociation"=>4032
  },
  
 # actual record count: 4,598,548
 # entities not persisted:
 # - assessmentFamily --> count: 74
 # - assessmentItem --> count: 388
 # - assessmentPeriodDescriptor --> count: 111
 # - courseOffering --> count: 0
 # - objectiveAssessment --> count: 97
 # - performanceLevelDescriptor --> count: 128
 # - serviceDescriptor --> count: 4
 # - studentAssessmentItem --> count: 0
 # - studentObjectiveAssessment --> count: 30596
 "8M" => {
  "assessment"=>163,
  "attendance"=>30544,
  "calendarDate"=>184,
  "cohort"=>92,
  "compentencyLevelDescriptor"=>0,
  "course"=>100,
  "disciplineAction"=>44022,
  "disciplineIncident"=>44022,
  "educationOrganization"=>48,
  "educationOrganizationAssociation"=>135,
  "educationOrganizationSchoolAssociation"=>0,
  "grade"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>736,
  "graduationPlan"=>0,
  "learningObjective"=>51,
  "learningStandard"=>279,
  "parent"=>45904,
  "program"=>47,
  "reportCard"=>0,
  "schoolSessionAssociation"=>92,
  "section"=>30912,
  "session"=>92,
  "staff"=>2619,
  "staffCohortAssociation"=>92,
  "staffEducationOrgAssignmentAssociation"=>135,
  "staffProgramAssociation"=>47,
  "student"=>30544,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>305440,
  "studentCohortAssociation"=>32983,
  "studentCompetency"=>0,
  "studentCompetencyObjective"=>0,
  "studentDisciplineIncidentAssociation"=>44022,
  "studentParentAssociation"=>45904,
  "studentProgramAssociation"=>26267,
  "studentSchoolAssociation"=>30544,
  "studentSectionAssociation"=>427616,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacherSchoolAssociation"=>2484,
  "teacherSectionAssociation"=>30912
 },
  
 # actual record count: 17,902,691
 #  entities not persisted:
 #  - AssessmentFamily           --> count: 77
 #  - AssessmentItem             --> count: 394
 #  - AssessmentPeriodDescriptor --> count: 113
 #  - CourseOffering             --> count: 0
 #  - Grade                      --> count: 0
 #  - ObjectiveAssessment        --> count: 95
 #  - PerformanceLevelDescriptor --> count: 125
 #  - ServiceDescriptor          --> count: 4
 #  - StudentAssessmentItem      --> count: 0
 #  - StudentObjectiveAssessment --> count: 121,544
 "22M" => {
  "assessment"=>163,
  "attendance"=>122176,
  "calendarDate"=>736,
  "cohort"=>368,
  "course"=>100,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>176088,
  "disciplineIncident"=>44022,
  "educationOrganization"=>189,
  "educationOrganizationAssociation"=>135,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>736,
  "learningObjective"=>55,
  "learningStandard"=>265,
  "parent"=>45904,
  "program"=>185,
  "school"=>0,
  "schoolSessionAssociation"=>368, 
  "section"=>123648,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>368,
  "sessionCourseAssociation"=>0,
  "staff"=>10071,
  "staffCohortAssociation"=>368,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>185,
  "student"=>30544,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>1221760,
  "studentCohortAssociation"=>131766,
  "studentDisciplineIncidentAssociation"=>176088,
  "studentParentAssociation"=>182998,
  "studentProgramAssociation"=>104902,
  "studentSchoolAssociation"=>122176,
  "studentSectionAssociation"=>1710464,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>0,
  "teacherSchoolAssociation"=>9936,
  "teacherSectionAssociation"=>123648
  },
  
 # actual record count: 17,749,248
 #  entities not persisted:
 #  - AssessmentFamily           --> count: 80
 #  - AssessmentItem             --> count: 370
 #  - AssessmentPeriodDescriptor --> count: 120
 #  - CourseOffering             --> count: 13248
 #  - ObjectiveAssessment        --> count: 80
 #  - PerformanceLevelDescriptor --> count: 126
 #  - ServiceDescriptor          --> count: 4
 #  - StudentAssessmentItem      --> count: 457504
 #  - StudentObjectiveAssessment --> count: 458737
 "25M" => {
  "assessment"=>163,
  "attendance"=>91632,
  "calendarDate"=>552,
  "cohort"=>276,
  "competencyLevelDescriptor"=>2,
  "course"=>6624,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>132066,
  "disciplineIncident"=>132066,
  "educationOrganization"=>142,
  "educationOrganizationAssociation"=>0,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>1,
  "gradingPeriod"=>1104,
  "graduationPlan"=>138,
  "learningObjective"=>38,
  "parent"=>137192,
  "program"=>145,
  "reportCard"=>91632,
  "school"=>0,
  "schoolSessionAssociation"=>276,
  "section"=>92736,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>276,
  "sessionCourseAssociation"=>0,
  "staff"=>7587,
  "staffCohortAssociation"=>276,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>145,
  "student"=>91632,
  "studentAcademicRecord"=>91632,
  "studentAssessmentAssociation"=>916320,
  "studentCohortAssociation"=>98551,
  "studentCompetency"=>183264,
  "studentCompetencyObjective"=>1,
  "studentDisciplineIncidentAssociation"=>132066,
  "studentParentAssociation"=>137192,
  "studentProgramAssociation"=>78403,
  "studentSchoolAssociation"=>91632,
  "studentSectionAssociation"=>1282848, 
  "studentSectionGradebookEntry"=>91632,
  "studentTranscriptAssociation"=>1374480,
  "teacher"=>0,
  "teacherSchoolAssociation"=>7452,
  "teacherSectionAssociation"=>92736
  },
  
 # actual record count: 70,999,024
 #  entities not persisted:
 #  - AssessmentFamily           --> count: 81
 #  - AssessmentItem             --> count: 391
 #  - AssessmentPeriodDescriptor --> count: 129
 #  - CourseOffering             --> count: 13248
 #  - Grade                      --> count: 5,131,392
 #  - ObjectiveAssessment        --> count: 82
 #  - PerformanceLevelDescriptor --> count: 117
 #  - ServiceDescriptor          --> count: 4
 #  - StudentAssessmentItem      --> count: 1,832,828
 #  - StudentObjectiveAssessment --> count: 1,831,859
 "100M" => {
  "assessment"=>163,
  "attendance"=>366528,
  "calendarDate"=>2208,
  "cohort"=>1104,
  "competencyLevelDescriptor"=>2,
  "course"=>26496,
  "courseOffering"=>0, # when supported: 52992
  "courseSectionAssociation"=>0,
  "disciplineAction"=>528264,
  "disciplineIncident"=>528264,
  "educationOrganization"=>565, 
  "educationOrganizationAssociation"=>0,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>1,
  "gradingPeriod"=>4416,
  "graduationPlan"=>552,
  "learningObjective"=>43,
  "learningStandard"=>257,
  "parent"=>549997,
  "program"=>577,
  "reportCard"=>366528,
  "school"=>0,
  "schoolSessionAssociation"=>1104,
  "section"=>370944,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>1104,
  "sessionCourseAssociation"=>0,
  "staff"=>29943,
  "staffCohortAssociation"=>1104,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>577,
  "student"=>366528,
  "studentAcademicRecord"=>366528,
  "studentAssessmentAssociation"=>3665280,
  "studentCohortAssociation"=>396319,
  "studentCompetency"=>733056,
  "studentCompetencyObjective"=>1,
  "studentDisciplineIncidentAssociation"=>528264,
  "studentParentAssociation"=>549997,
  "studentProgramAssociation"=>315727,
  "studentSchoolAssociation"=>366528,
  "studentSectionAssociation"=>5131392, 
  "studentSectionGradebookEntry"=>366528,
  "studentTranscriptAssociation"=>5497920,
  "teacher"=>0,
  "teacherSchoolAssociation"=>29808,
  "teacherSectionAssociation"=>370944
  }  
}

# Print total counts
expectationTotals = { "900K" => 0, "1M" => 0, "8M" => 0, "22M" => 0, "25M" => 0, "100M" => 0 }

expected.each do |set,collections|
  total=0
  collections.each do |name,count|
    total+=count
  end
  printf "%s %d\n",set,total
  expectationTotals[set] = total
end

# Check that user specified the set
if ARGV.count() < 1
  puts "must specify the type of batch"
  exit
end


if !expected.has_key?(ARGV[0])
  puts "Unsupported batch \e[31m#{ARGV[0]}\e[0m"
  puts "Supported: "
  expected.each_key {|key| puts key}
  exit
end

connection = Mongo::Connection.new( mongohost, mongoport)

db = connection.db( dbName )

printf "\e[35m%-40s %s\n","Collection","Actual(Expected)\e[0m"
puts "---------------------------------------------------------"

totalActualCount = 0

setName = ARGV[0]
expected[ setName ].each do |collectionName,expectedCount|
  coll = db.collection(collectionName)

  count = coll.count()
	totalActualCount += count
  color="\e[32m"
  if expectedCount != count
    color="\e[31m"
  end
  printf "#{color}%-40s %d(%d)\e[0m\n",collectionName,count,expectedCount

end

expectedSetTotal = expectationTotals[setName]
puts "Expected #{expectedSetTotal} and saw #{totalActualCount}"
puts "ALL DONE"
