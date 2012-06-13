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
  id = 19                                         if human_readable_id == "ASSOCIATION COUNT"  
  id = 1                                          if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 7                                          if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                          if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 7                                          if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "b3f68907-8fd5-11e1-86ec-0021701f543f"     if human_readable_id == "ASSOCIATION ID"
  id = "b3f68907-8fd5-11e1-86ec-0021701f543f"     if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "b3f68907-8fd5-11e1-86ec-0021701f543f"     if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentProgramAssociations"            if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentProgramAssociation"                if human_readable_id == "ASSOCIATION TYPE"
  id = "studentProgramAssociations"               if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                                if human_readable_id == "ENDPOINT1 FIELD"
  id = "0f0d9bac-0081-4900-af7c-d17915e02378"     if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                              if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                                 if human_readable_id == "ENDPOINT1 URI" 
  
  #program related data
  id = "programId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "9b8cafdc-8fd5-11e1-86ec-0021701f543f"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getProgram"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getPrograms"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "program"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "programs"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "studentId"                              if human_readable_id == "UPDATE FIELD"
  id = "0f0d9bac-0081-4900-af7c-d17915e02378"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "ace1dc53-8c1d-4c01-b922-c3ebb7ff5be8"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d"   if human_readable_id == "INACCESSIBLE REFERENCE 1"
  id = "cb292c7d-3503-414a-92a2-dc76a1585d79"   if human_readable_id == "INACCESSIBLE REFERENCE 2"
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
    "studentId" => "0636ffd6-ad7d-4401-8de9-40538cf696c8",
    "programId" => "9b8c3aab-8fd5-11e1-86ec-0021701f543f",
    "beginDate" => "2012-01-12",
    "educationOrganizationId" =>"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
}
end
