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

  #discipline action data
  id = 4                                        if human_readable_id == "ENTITY COUNT"
  id = "0e26de6c-225b-9f67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID"
  id = "0e26de6c-225b-9f67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "0e26de6c-225b-9f67-9281-7213ad50a03b"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "disciplineAction"                       if human_readable_id == "ENTITY TYPE"
  id = "disciplineActions"                      if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "disciplineActionIdentifier"             if human_readable_id == "REQUIRED FIELD"
  id = "disciplineDate"                         if human_readable_id == "UPDATE FIELD"
  id = "2012-02-24"                             if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "2012-03-21"                             if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "disciplineActionIdentifier" => "Discipline act XXX",
    "disciplines" => [[
        {"codeValue" => "Discp Act 3"},
        {"shortDescription" => "Disciplinary Action 3"},
        {"description" => "Long disciplinary Action 3"}
    ]],
    "disciplineDate" => "2012-01-28",
    "disciplineIncidentId" => ["0e26de79-22aa-5d67-9201-5113ad50a03b"],
    "studentId" => ["7a86a6a7-1f80-4581-b037-4a9328b9b650"],
    "responsibilitySchoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
    "assignmentSchoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2"
  }
end


# JSON format
#{
#    "disciplineActionIdentifier":"Discipline act XXX",
#    "disciplines":[{
#        "codeValue":"Discp Act 3",
#        "shortDescription":"Disciplinary Action 3",
#        "educationOrganizationId":["1d303c61-88d4-404a-ba13-d7c5cc324bc5"]
#    }],
#    "disciplineDate":"2012-01-28",
#    "disciplineIncidentId":["0e26de79-22aa-5d67-9201-5113ad50a03b"],
#    "studentId":["7a86a6a7-1f80-4581-b037-4a9328b9b650"],
#    "responsibilitySchoolId": "eb3b8c35-f582-df23-e406-6947249a19f2",
#    "assignmentSchoolId": "2058ddfb-b5c6-70c4-3bee-b43e9e93307d"
#  }
