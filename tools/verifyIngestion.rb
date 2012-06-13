
require 'mongo'
require 'json'

#Talk to maestro's mongos
mongohost = 'nxmaestro.slidev.org'
mongoport = 27017

dbName = ARGV[1]
if ( dbName.nil? ) 
	dbName = "sli"
end

puts "\==========================================================="
puts "Talking to mongo \e[32m#{mongohost}:#{mongoport} \e[0mfor #{dbName} database"
  
expected={"900K" => {
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
  
    
  "8M" => {
  "assessment"=>163,
  "attendance"=>30544,#3420928
  "calendarDate"=>184,
  "cohort"=>92,
  "course"=>100,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>44022,
  "disciplineIncident"=>44022,
  "educationOrganization"=>48,
  "educationOrganizationAssociation"=>135,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>736,
  "learningObjective"=>393,
  "parent"=>45904,
  "program"=>47,
  "school"=>0,
  "schoolSessionAssociation"=>0, 
  "section"=>30912,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>92,
  "sessionCourseAssociation"=>0,
  "staff"=>135,
  "staffCohortAssociation"=>92,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>47,
  "student"=>30544,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>305440,
  "studentCohortAssociation"=>32983,
  "studentDisciplineIncidentAssociation"=>44022,
  "studentParentAssociation"=>45904,
  "studentProgramAssociation"=>26267,
  "studentSchoolAssociation"=>30544,
  "studentSectionAssociation"=>427616,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>2484,
  "teacherSchoolAssociation"=>2484,
  "teacherSectionAssociation"=>30912
  },
  
  # TODO
    "22M" => {
  "assessment"=>163,
  "attendance"=>30544,#3420928
  "calendarDate"=>184,
  "cohort"=>92,
  "course"=>100,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>44022,
  "disciplineIncident"=>44022,
  "educationOrganization"=>48,
  "educationOrganizationAssociation"=>135,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>736,
  "learningObjective"=>393,
  "parent"=>45904,
  "program"=>47,
  "school"=>0,
  "schoolSessionAssociation"=>0, 
  "section"=>30912,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>92,
  "sessionCourseAssociation"=>0,
  "staff"=>135,
  "staffCohortAssociation"=>92,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>47,
  "student"=>30544,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>305440,
  "studentCohortAssociation"=>32983,
  "studentDisciplineIncidentAssociation"=>44022,
  "studentParentAssociation"=>45904,
  "studentProgramAssociation"=>26267,
  "studentSchoolAssociation"=>30544,
  "studentSectionAssociation"=>427616,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>2484,
  "teacherSchoolAssociation"=>2484,
  "teacherSectionAssociation"=>30912
  },
  
   # TODO
    "25M" => {
  "assessment"=>163,
  "attendance"=>1026278,#10262784
  "calendarDate"=>552,
  "cohort"=>92,
  "course"=>6624,
  "courseOffering"=>0,
  "courseSectionAssociation"=>0,
  "disciplineAction"=>44022,
  "disciplineIncident"=>44022,
  "educationOrganization"=>146,
  "educationOrganizationAssociation"=>135,
  "educationOrganizationSchoolAssociation"=>0,
  "gradebookEntry"=>0,
  "gradingPeriod"=>1104,
  "learningObjective"=>38,
  "parent"=>137192,
  "program"=>145,
  "school"=>0,
  "schoolSessionAssociation"=>72, 
  "section"=>92736,
  "sectionAssessmentAssociation"=>0,
  "sectionSchoolAssociation"=>0,
  "session"=>276,
  "sessionCourseAssociation"=>0,
  "staff"=>7587,
  "staffCohortAssociation"=>92,
  "staffEducationOrganizationAssociation"=>135,
  "staffProgramAssociation"=>135,
  "student"=>91632,
  "studentAcademicRecord"=>0,
  "studentAssessmentAssociation"=>916320,
  "studentCohortAssociation"=>99103,
  "studentDisciplineIncidentAssociation"=>396198,
  "studentParentAssociation"=>137192,
  "studentProgramAssociation"=>78047,
  "studentSchoolAssociation"=>91632,
  "studentSectionAssociation"=>1282848,
  "studentSectionGradebookEntry"=>0,
  "studentTranscriptAssociation"=>0,
  "teacher"=>0,
  "teacherSchoolAssociation"=>7452,
  "teacherSectionAssociation"=>92736
  }
  
  
  
}

# Print total counts

expectationTotals = { "900K" => 0, "8M" => 0, "22M" => 0 }

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
