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

  #discipline incident data
  id = 7                                        if human_readable_id == "ENTITY COUNT"
  id = "3a0cc576-fe7f-40bd-b86c-ca861244db12"   if human_readable_id == "ENTITY ID"
  id = "44658e50-6982-40fd-aa4a-f6d8624f3de9"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "6e088863-c72a-4f9a-a6c4-b4eccc2dde4d"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "studentAcademicRecord"                  if human_readable_id == "ENTITY TYPE"
  id = "studentAcademicRecords"                 if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "studentId"                              if human_readable_id == "REQUIRED FIELD"
  id = "sessionId"                              if human_readable_id == "UPDATE FIELD"
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "67ce204b-9999-4a11-aacb-000000000003"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "studentId" => "eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d", 
    "sessionId" => "67ce204b-9999-4a11-aacb-000000000003"
  }
end

# New entity in json
#
# {
# "studentId":"eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d", 
# "sessionId": "67ce204b-9999-4a11-aacb-000000000003"
# }
