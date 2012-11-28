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

require_relative '../../../../utils/sli_utils.rb'
require_relative '../../common.rb'
require_relative '../../../utils/api_utils.rb'


############################################################
# STEPS: BEFORE
############################################################

Before do
  @updateFields = {
    #Entity Resource URI            => [ Update Field            , Updated Value                                ]
    "assessments"                 => [ "assessmentTitle"       , "Advanced Placement Test - Subject: Writing" ],
    "attendances"                 => [ "schoolYearAttendance"  , "[]"                                         ],
    "cohorts"                     => [ "cohortDescription"     , "frisbee golf team"                          ],
    "courses"                     => [ "courseDescription"     , "Advanced Linguistic Studies"                ],
    "disciplineActions"           => [ "disciplineDate"        , "2012-03-18"                                 ],
    "disciplineIncidents"         => [ "incidentTime"          , "01:02:15"                                   ],
    "educationOrganizations"      => [ "nameOfInstitution"     , "Bananas School District"                    ],
    "gradebookEntries"            => [ "gradebookEntryType"    , "Homework"                                   ],
    "learningObjectives"          => [ "academicSubject"       , "Mathematics"                                ],
    "learningStandards"           => [ "gradeLevel"            , "Ninth grade"                                ],
    "parents"                     => [ "parentUniqueStateId"   , "ParentID102"                                ],
    "programs"                    => [ "programSponsor"        , "State Education Agency"                     ],
    "schools"                     => [ "nameOfInstitution"     , "Yellow Middle School"                       ],
    "sections"                    => [ "sequenceOfCourse"      , "2"                                          ],
    "sessions"                    => [ "totalInstructionalDays", "43"                                         ],
    "staff"                       => [ "sex"                   , "Female"                                     ],
    "students"                    => [ "sex"                   , "Female"                                     ],
    "studentAcademicRecords"      => [ "sessionId"             , "67ce204b-9999-4a11-aacb-000000000003"       ],
    "studentGradebookEntries"     => [ "diagnosticStatement"   , "Finished the quiz in 5 hours"               ],
    "teachers"                    => [ "highlyQualifiedTeacher", "false"                                      ],
    "grades"                      => [ "gradeType"             , "Mid-Term Grade"                             ],
    "studentCompetencies"         => [ "diagnosticStatement"   , "advanced nuclear thermodynamics"            ],
    "gradingPeriods"              => [ "endDate"               , "2015-10-15"                                 ],
    "reportCards"                 => [ "numberOfDaysAbsent"    , "17"                                         ],
    "graduationPlans"             => [ "individualPlan"        , "true"                                       ],
    "studentCompetencyObjectives" => [ "objectiveGradeLevel"   , "First grade"                                ]
  }
end


 


#| Entity Type                    |
#| "assessment"                   |
#| "attendance"                   |
#| "cohort"                       |
#| "course"                       |
#| "disciplineAction"             |
#| "disciplineIncident"           |
#| "educationOrganization"        |
#| "gradebookEntry"               |
#| "learningObjective"            |
#| "learningStandard"             |
#| "parent"                       |
#| "program"                      |
#| "school"                       |
#| "section"                      |
#| "session"                      |
#| "staff"                        |
#| "student"                      |
#| "studentAcademicRecord"        |
#| "studentGradebookEntry"        |
#| "teacher"                      |
#| "grade"                        |
#| "studentCompetency"            |
#| "gradingPeriod"                |
#| "reportCard"                   |
#| "graduationPlan"               |
#| "studentCompetencyObjective"   |


When /^I navigate to POST for each resource available$/ do
  resources.each do |resource|
    steps %Q{
          Given a valid entity json document for a \"#{resource[1..-2]}\"
          When I navigate to POST \"/v1#{resource}\"
    }
    puts "Resource has valid json document"
    puts "Resource #{resource} ==> POST"
    begin
      result = JSON.parse(@res.body)
    end
    assert(result != nil, "result of parsing result body was nil!")
  end
end
def resources
  config_path = File.expand_path("../../../../../../../../api/src/main/resources/wadl/v1_resources.json", __FILE__)
  v1_resources = JSON.parse(File.read(config_path))['resources']
  get_resource_paths v1_resources
end

def get_resource_paths resources, base = ""
  paths = []
  resources.each do |resource|
    paths << base + resource['path']
    if resource.has_key? 'subResources'
      #do nothing for now
    end
  end
  paths
end

When /^I navigate to GET with invalid id for each resource available$/ do
  resources.each do |resource|
    badId = "bad1111111111111111111111111111111111111_id"
    uri = "/v1#{resource}/#{badId}"
    puts "GET " + uri
    step "I navigate to GET \"#{uri}\""
    step "I should receive a return code of 404"
  end
end

When /^I navigate to PUT with invalid id for each resource available$/ do
  resources.each do |resource|
    badId = "bad1111111111111111111111111111111111111_id"
    uri = "/v1#{resource}"
    puts "PUT " + uri
    puts "I navigate to PUT \"#{uri}\""
    step "I navigate to PUT \"#{uri}\""
    #step "I should receive a return code of 404"
  end
end






    #begin
    #  result = JSON.parse(@res.body)
    #  puts result
    #rescue JSON::ParserError => e
    #  $stderr.puts """JSON Parse Exception:
    #              \tresource = #{resource}
    #              \tqueryParams = #{@queryParams}
    #              \tbody = #{@res.body}
    #              \tuser = #{@user}"""
    #end
    #assert(result != nil, "result of parsing result body was nil!")
    #assert(@res.code != 500, "Return code was 500 from api, body = #{@res.body}\n
    #       #requesting resource #{resource}")



=begin
  
stuff to consider

OPTION A
need to reference schema
need to parse schema
need to pull out entities based on collection name

OPTION B
put list of update fields in config
put list of no update fields in config
if entity isn't in either list, error







  
=end





