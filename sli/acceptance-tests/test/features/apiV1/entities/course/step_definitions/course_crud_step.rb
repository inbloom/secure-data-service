require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  #course data
  id = 105                                        if human_readable_id == "ENTITY COUNT"
  id = "a7444741-8ba1-424e-b83f-df88c57f8b8c"   if human_readable_id == "ENTITY ID"
  id = "b8dbdefb-85b6-47e0-8a26-ef0f38568dd1"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "b8dbdefb-85b6-47e0-8a26-ef0f38568ddf"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "course"                                 if human_readable_id == "ENTITY TYPE"
  id = "courses"                                if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "courseTitle"                            if human_readable_id == "UPDATE FIELD"
  id = "Russian 1"                              if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Test 1"                                 if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ENTITY ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #other
  id = ""                                       if human_readable_id == "BLANK"
  
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "courseTitle" => "Chinese 1",
    "numberOfParts" => 1,
    "courseCode" => [{
      "ID" => "C1",
      "identificationSystem" => "School course code",
      "assigningOrganizationCode" => "Bob's Code Generator"
    }],
    "courseLevel" => "Basic or remedial",
    "courseLevelCharacteristics" => ["Advanced Placement"],
    "gradesOffered" => "Eighth grade",
    "subjectArea" => "Foreign Language and Literature",
    "courseDescription" => "Intro to Chinese",
    "dateCourseAdopted" => "2001-01-01",
    "highSchoolCourseRequirement" => false,
    "courseDefinedBy" => "LEA",
    "minimumAvailableCredit" => {
      "credit" => 1.0
    },
    "maximumAvailableCredit" => {
      "credit" => 1.0
    },
    "careerPathway" => "Hospitality and Tourism"
  }
end
