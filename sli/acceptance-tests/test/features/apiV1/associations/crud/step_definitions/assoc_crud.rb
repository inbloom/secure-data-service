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
  uri = "/v1.1/" + Transform(uri_placeholder)
  uri
end

Transform /^\/([^"]*)\/([^"]*)$/ do |uri_placeholder1, uri_placeholder2|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2)
  uri
end

Transform /^\/([^"]*)\/([^"]*)\/([^"]*)$/ do |uri_placeholder1, uri_placeholder2, uri_placeholder3|
  uri = "/v1.1/" + Transform(uri_placeholder1) + "/" + Transform(uri_placeholder2) + "/" + Transform(uri_placeholder3)
  uri
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for ([^"]*)$/ do |arg1|
  @fields = deep_copy($entityData[arg1])
end

Given /^my contextual access is defined by table:$/ do |table|
  @ctx={}
  table.hashes.each do |hash|
  @ctx[hash["Context"]]=hash["Ids"]
  end
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


Then /^uri was rewritten to "(.*?)"$/ do |expectedUri|
  root = expectedUri.match(/v1.1\/(.+?)\/|$/)[1]
  actual = @headers["x-executedpath"][0]

  #First, make sure the paths of the URIs are the same
  expectedPath = expectedUri.gsub("@ids", "[^/]*")
  expectedPath.slice!(0) #Delete the extra beginning slash
  assert(actual.match(expectedPath), "Rewriten URI path didn't match, expected:#{expectedPath}, actual:#{actual}")

  #Then, validate the list of ids are the same
  ids = []
  if @ctx.has_key? root
    idsString = actual.match(/v1.1\/[^\/]*\/([^\/]*)\//)[1]
    actualIds = idsString.split(",")
    expectedIds = @ctx[root].split(",")
    
    assert(actualIds.length == expectedIds.length,"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    expectedIds.each do |id|
      assert(actualIds.include?(id),"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    end
  end
end

###############################################################################
# DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA
###############################################################################

$entityData = {
    "courseOffering" => {
        "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772",
        "sessionId" => "c549e272-9a7b-4c02-aff7-b105ed76c904",
        "courseId" => "5a04c851-d741-4fd7-8bca-62e3f6f7220e",
        "localCourseCode" => "LCCGR101",
        "localCourseTitle" => "German 101 - Intro"
    },
    "courseTranscript" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
        "courseId" => "5a04c851-d741-4fd7-8bca-62e3f6f7220e",
        "studentAcademicRecordId" => "16afc8d4-6c91-48f9-8a51-de527c1131b7",
        "courseAttemptResult" => "Pass",
        "creditsEarned" => {
             "credit" => 4.0
        },
        "gradeType" => "Final",
        "finalLetterGradeEarned" => "A",
	"educationOrganizationReference" => ["92d6d5a0-852c-45f4-907a-912752831772"]
    },
    "staffCohortAssociation" => {
        "staffId" => "04f708bc-928b-420d-a440-f1592a5d1073",
        "cohortId" => "b408635d-8fd5-11e1-86ec-0021701f543f_id",
        "beginDate" => "2010-01-15",
        "endDate" => "2012-03-29",
        "studentRecordAccess" => true
    },
    "staffEducationOrganizationAssociation" => {
        "staffReference" => "04f708bc-928b-420d-a440-f1592a5d1073",
        "educationOrganizationReference" => "92d6d5a0-852c-45f4-907a-912752831772",
        "staffClassification" => "Counselor",
        "beginDate" => "2011-01-13"
    },
    "staffProgramAssociation" => {
        "staffId" => "b4c2a73f-336d-4c47-9b47-2d24871eef96",
        "programId" => "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id",
        "beginDate" => "2012-01-01",
        "endDate" => "2012-12-31"
    },
    "studentAssessment" => {
        "studentId" => "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "assessmentId" => "dd916592-7d7e-5d27-a87d-dfc7fcb757f6",
        "administrationDate" => "2011-10-01",
        "administrationEndDate" => "2012-01-01",
        "retestIndicator" => "1st Retest",
    },
    "studentCohortAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
        "cohortId" => "b408d88e-8fd5-11e1-86ec-0021701f543f_id",
        "beginDate" => "2012-02-29",
        "endDate" => "2012-03-29"
    },
    "studentDisciplineIncidentAssociation" => {
        "studentId" => "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "disciplineIncidentId" => "0e26de79-7efa-5e67-9201-5113ad50a03b",
        "studentParticipationCode" => "Reporter"
    },
    "studentParentAssociation" => {
        "studentId" => "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "parentId" => "9b8f7237-ce8e-4dff-98cf-66535880987b",
        "livesWith" => true
    },
    "studentProgramAssociation" => {
        "studentId" => "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "programId" => "9b8cafdc-8fd5-11e1-86ec-0021701f543f_id",
        "beginDate" => "2012-01-12",
        "endDate" => "2012-05-01",
        "reasonExited" => "Refused services",
        "educationOrganizationId" =>"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    },
    "studentSchoolAssociation" => {
        "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id",
        "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
        "entryGradeLevel" => "First grade",
        "entryDate" => "2011-09-01"
    },
    "studentSectionAssociation" => {
        "studentId" => "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "sectionId" => "47b5adbf-6fd0-4f07-ba5e-39612da2e234_id",
#        "studentId" => "0636ffd6-ad7d-4401-8de9-40538cf696c8_id",
#        "sectionId" => "ceffbb26-1327-4313-9cfc-1c3afd38122e_id",
        "repeatIdentifier" => "Repeated, counted in grade point average",
        "beginDate" => "2011-12-01",
        "endDate" => "2012-01-01",
        "homeroomIndicator" => true
    },
    "teacherSchoolAssociation" => {
        "teacherId" => "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b",
        "schoolId" => "92d6d5a0-852c-45f4-907a-912752831772",
        "programAssignment" => "Special Education"
    },
    "teacherSectionAssociation" => {
        "teacherId" => "edce823c-ee28-4840-ae3d-74d9e9976dc5",
        "sectionId" => "392d1835-f372-4690-b221-7065db1aed33_id",
        "classroomPosition" => "Teacher of Record"
    }
}
