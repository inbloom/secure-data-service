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

require_relative '../../apiV1/associations/crud/step_definitions/assoc_crud.rb'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'

###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

Before do
  @midgar_db_name = convertTenantIdToDbName('Midgar')
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  # General
  id = @newId                                        if human_readable_id == "NEW ID"
  # Student
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id"     if human_readable_id == "MARVIN MILLER"
  id = "067198fd6da91e1aa8d67e28e850f224d6851713_id" if human_readable_id == "INGESTED MATT SOLLARS"
  # Section
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e_id"     if human_readable_id == "8TH GRADE ENGLISH SEC 6"
  id = "b11d9f8e0790f441c72a15a3c2deba5ffa1a5c4a_id" if human_readable_id == "INGESTED 7TH GRADE ENGLISH SEC 5"
  # Program
  id = "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id"        if human_readable_id == "ACC TEST PROG 2"
  id = "983dd657325009aefa88a234fa18bdb1e11c82a8_id" if human_readable_id == "INGESTED ACC TEST PROG 2"
  # Session
  id = "fb0ac9e8-9e4e-48a0-95d2-ae07ee15ee92"        if human_readable_id == "FALL 2011"
  id = "62101257-592f-4cbe-bcd5-b8cd24a06f73"        if human_readable_id == "SPRING SEMESTER"
  id = "ba942e512de1fd5021a69a9d452b481c7512c1bd_id" if human_readable_id == "INGESTED FALL 2009 EAST BREAK JUNIOR HIGH"
  id = "1771687e116b8babb04c3e5e0a1e9bda10b583c8_id" if human_readable_id == "INGESTED SUMMER 2012 EAST BREAK JUNIOR HIGH"

  # StudentAssessment
  id = "fe0be78251b73eaeb5bdc9cc79819b42a472dab4_id" if human_readable_id == "STUDENT ASSESSMENT ID"

  # StudentSchoolAssociation
  id = "ec2e4218-6483-4e9c-8954-0aecccfd4731"        if human_readable_id == "MARVIN MILLER EAST DB JR HI"
  id = "b4a4697c3c7d08b48a38460c34b58286ad3b3f60_id" if human_readable_id == "INGESTED MATT SOLLARS EAST BREAK JUNIOR HIGH"

  #Cohort
  id = "b40926af-8fd5-11e1-86ec-0021701f543f_id"        if human_readable_id == "ACC-TEST-COH-2"
  id = "dce4d9a8240e3d3ebdbb1759a5376e1dd4bec4d0_id" if human_readable_id == "INGESTED MATT SOLLARS ACC-TEST-COH-4"

  # Return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid json document for a ([^"]*)$/ do |entity|
  @fields = deep_copy($entity_data[entity])
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I look at "([^\"]*)" in the "([^\"]*)"$/ do |id, coll|
  conn = Mongo::Connection.new(PropLoader.getProps['ingestion_db'], PropLoader.getProps['ingestion_db_port'])
  mdb = conn.db(@midgar_db_name)
  @doc = mdb.collection(coll).find("_id" => id).to_a
  assert(!@doc.nil?, "Cannot find the document with _id=#{id} in #{coll}")
  assert(@doc.size == 1, "Number of entities returned != 1 (received #{@doc.size}")
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive a new ID$/ do
  step "I should receive a new ID for the association I just created"
end

Then /^I (should|should not) find "([^\"]*)" in "([^\"]*)"$/ do |should_or_not, id, field|
  should = should_or_not == "should"? true : false
  found = false
  sub_doc = @doc[0][field]
  unless sub_doc.nil?
    sub_doc.each do |row|
      if row["_id"] == id
        found = true
        break
      end
    end
  end
  assert(found == should, "Failed should / should not check")
end

###############################################################################
# DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA
###############################################################################

$entity_data = {
  "studentSectionAssociation" => {
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
    "sectionId" => "ceffbb26-1327-4313-9cfc-1c3afd38122e_id",
    "repeatIdentifier" => "Repeated, counted in grade point average",
    "beginDate" => "2011-12-01",
    "endDate" => "2012-01-01",
    "homeroomIndicator" => true
  },
  "studentProgramAssociation" => {
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
    "programId" => "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id",
    "beginDate" => "2012-01-12",
    "endDate" => "2012-05-01",
    "reasonExited" => "Refused services",
    "educationOrganizationId" =>"ec2e4218-6483-4e9c-8954-0aecccfd4731"
  },
  "studentAcademicRecord" => {
      "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
      "sessionId" => "62101257-592f-4cbe-bcd5-b8cd24a06f73"
    },
  "studentAssessment" => {
      "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
      "assessmentId" => "7b2e6133-4224-4890-ac02-73962eb09645",
      "administrationDate" => "2013-01-01"
    },
    "studentCohortAssociation" => {
           "cohortId" => "b40926af-8fd5-11e1-86ec-0021701f543f_id",
           "studentId" =>"74cf790e-84c4-4322-84b8-fca7206f1085_id",
           "endDate" => "2020-01-15",
           "beginDate" => "2011-04-01"
        },
  "attendance" => {
    "entityType" => "attendance",
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085_id",
    "schoolId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731",
    "schoolYearAttendance" => [{
      "schoolYear" => "2011-2012",
      "attendanceEvent" => [{
        "date" => "2011-09-16",
        "event" => "Tardy"
      }]
    }]
  },
}
