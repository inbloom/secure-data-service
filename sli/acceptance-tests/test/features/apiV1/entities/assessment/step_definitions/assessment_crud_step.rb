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

  #assessment data
  id = 17                                       if human_readable_id == "ENTITY COUNT"
  id = "6c572483-fe75-421c-9588-d82f1f5f3af5"   if human_readable_id == "ENTITY ID"
  id = "df897f7a-7ac4-42e4-bcbc-8cc6fd88b91a"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "29f044bd-1449-4fb7-8e9a-5e2cf9ad252a"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "assessment"                             if human_readable_id == "ENTITY TYPE"
  id = "assessments"                            if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "assessmentTitle"                        if human_readable_id == "UPDATE FIELD"
  id = "Writing Advanced Placement Test"        if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Advanced Placement Test - Subject: Writing"      if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "assessmentTitle" => "Mathematics Achievement Assessment Test",
    "assessmentIdentificationCode" => [{
      "identificationSystem" => "School",
      "ID" => "01234B"
    }],
    "academicSubject" => "Mathematics",
    "assessmentCategory" => "Achievement test",
    "gradeLevelAssessed" => "Adult Education",
    "contentStandard" => "LEA Standard",
    "version" => 2
  }
end
