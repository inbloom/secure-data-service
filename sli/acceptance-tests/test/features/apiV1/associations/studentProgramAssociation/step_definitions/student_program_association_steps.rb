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

  #school session association data
  id = 21                                         if human_readable_id == "ASSOCIATION COUNT"
  id = 1                                          if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 7                                          if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                          if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 7                                          if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "7ac82cbd-44e7-4bff-aee9-83d4457da4ab"     if human_readable_id == "ASSOCIATION ID"
  id = "0676342d-ad60-494f-9c2e-2371e18ae4e3"     if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "d2c31f6a-8084-4640-b4e9-cb9ebb20d54a"     if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentProgramAssociations"            if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentProgramAssociation"                if human_readable_id == "ASSOCIATION TYPE"
  id = "studentProgramAssociations"               if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                                if human_readable_id == "ENDPOINT1 FIELD"
  id = "714c1304-8a04-4e23-b043-4ad80eb60992"     if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                              if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                                 if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "programId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "e8d33606-d114-4ee4-878b-90ac7fc3df16"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getProgram"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getPrograms"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "program"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "programs"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "studentId"                              if human_readable_id == "UPDATE FIELD"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "bda1a4df-c155-4897-85c2-953926a3ebd8"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #return the translated value
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "studentId" => "1023bfc9-5cb8-4126-ae6a-4fefa74682c8",
    "programId" => "e8d33606-d114-4ee4-878b-90ac7fc3df16",
    "beginDate" => "2012-01-12",
    "educationOrganizationId" =>"2d7583b1-f8ec-45c9-a6da-acc4e6fde458"
}
end
