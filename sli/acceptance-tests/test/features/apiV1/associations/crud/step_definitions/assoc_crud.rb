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


require_relative '../../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = human_readable_id
  id = @newId                                                    if human_readable_id == "NEWLY CREATED ASSOC ID"
  id = "11111111-1111-1111-1111-111111111111"                    if human_readable_id == "INVALID REFERENCE"
  id = "Invalid reference. No association to referenced entity." if human_readable_id == "BAD REFERENCE"
  id
end

Transform /^\/([^"]*)$/ do |uri_placeholder|
  uri = "/v1/" + Transform(uri_placeholder)
  uri
end

Transform /^\/([^"]*)\/([^"]*)$/ do |uri_placeholder1, uri_placeholder2|
  uri = "/v1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2)
  uri
end

Transform /^\/([^"]*)\/([^"]*)\/([^"]*)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3|
  uri = "/v1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3)
  uri
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for ([^"]*)$/ do |arg1|
  @fields = deep_copy($entityData[arg1])
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive a new ID for the association I just created$/ do
  step "I should receive an ID for the newly created entity"
  assert(@newId != nil, "Cannot obtain new ID (== nil)")
end

###############################################################################
# DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA
###############################################################################

$entityData = {
    "courseOffering" => {
        "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
        "sessionId" => "fb0ac9e8-9e4e-48a0-95d2-ae07ee15ee92",
        "courseId" => "ddf01d82-9293-49ba-b16e-0fe5b4f4804d",
        "localCourseCode" => "LCCGR1",
        "localCourseTitle" => "German 1 - Intro to German"
    },
    "staffCohortAssociation" => {
        "staffId" => ["04f708bc-928b-420d-a440-f1592a5d1073"],
        "cohortId" => ["b408635d-8fd5-11e1-86ec-0021701f543f"],
        "beginDate" => "2010-01-15",
        "endDate" => "2012-03-29",
        "studentRecordAccess" => true
    },
    "staffEducationOrganizationAssociation" => {
        "staffReference" => "45d6c371-e7f1-4fa8-899a-e9f2309cbe4e",
        "educationOrganizationReference" => "bd086bae-ee82-4cf2-baf9-221a9407ea07",
        "staffClassification" => "Counselor",
        "beginDate" => "2011-01-13"
    },
    "staffProgramAssociation" => {
        "staffId" => ["04f708bc-928b-420d-a440-f1592a5d1073"],
        "programId" => ["9b8c3aab-8fd5-11e1-86ec-0021701f543f"],
        "beginDate" => "2012-01-01"
    },
    "studentAssessmentAssociation" => {
        "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
        "assessmentId" => "dd916592-7d7e-5d27-a87d-dfc7fcb757f6",
        "administrationDate" => "2011-10-01",
        "administrationEndDate" => "2012-01-01",
        "retestIndicator" => "1st Retest",
    },
    "studentCohortAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
        "cohortId" => "b408d88e-8fd5-11e1-86ec-0021701f543f",
        "beginDate" => "2012-02-29",
        "endDate" => "2012-03-29"
    },
    "studentDisciplineIncidentAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
        "disciplineIncidentId" => "0e26de79-22ea-5d67-9201-5113ad50a03b",
        "studentParticipationCode" => "Reporter"
    },
    "studentParentAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
        "parentId" => "38ba6ea7-7e73-47db-99e7-d0956f83d7e9",
        "livesWith" => true
    },
    "studentProgramAssociation" => {
        "studentId" => "0636ffd6-ad7d-4401-8de9-40538cf696c8",
        "programId" => "9b8c3aab-8fd5-11e1-86ec-0021701f543f",
        "beginDate" => "2012-01-12",
        "endDate" => "2012-05-01",
        "reasonExited" => "Refused services",
        "educationOrganizationId" =>"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    },
    "studentSchoolAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
        "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
        "entryGradeLevel" => "First grade",
        "entryDate" => "2011-09-01"
    },
    "studentSectionAssociation" => {
        "studentId" => "0636ffd6-ad7d-4401-8de9-40538cf696c8",
        "sectionId" => "ceffbb26-1327-4313-9cfc-1c3afd38122e",
        "repeatIdentifier" => "Repeated, counted in grade point average",
        "beginDate" => "2011-12-01",
        "endDate" => "2012-01-01",
        "homeroomIndicator" => true
    },
    "studentTranscriptAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
        "courseId" => "82ad1eb0-c6d4-4b00-909a-edd1c8d04e41",
        "courseAttemptResult" => "Pass",
        "creditsEarned" => {
            "credit" => 4.0
        },
        "gradeType" => "Final",
        "finalLetterGradeEarned" => "A"
    },
    "teacherSchoolAssociation" => {
        "teacherId" => "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b",
        "schoolId" => "b2c6e292-37b0-4148-bf75-c98a2fcc905f",
        "programAssignment" => "Regular Education"
    },
    "teacherSectionAssociation" => {
        "teacherId" => "edce823c-ee28-4840-ae3d-74d9e9976dc5",
        "sectionId" => "7295e51e-cd51-4901-ae67-fa33966478c7",
        "classroomPosition" => "Teacher of Record"
    }
}
