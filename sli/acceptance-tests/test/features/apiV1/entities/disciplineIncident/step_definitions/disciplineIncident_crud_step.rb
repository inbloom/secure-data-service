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
  id = 5                                        if human_readable_id == "ENTITY COUNT"
  id = "0e26de79-22aa-5d67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID"
  id = "0e26de79-22aa-5d67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "0e26de79-226a-5d67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "disciplineIncident"                     if human_readable_id == "ENTITY TYPE"
  id = "disciplineIncidents"                    if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "IncidentLocation"                       if human_readable_id == "REQUIRED FIELD"
  id = "reporterName"                           if human_readable_id == "UPDATE FIELD"
  id = "disciplinary incident 2 reporter"       if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "New reporter"                           if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "incidentIdentifier" => "Incident ID XXX",
    "incidentDate" => "2012-02-14", 
    "incidentTime" => "01:00:00", 
    "incidentLocation" => "On School",
    "behaviors" => [[
        {"shortDescription" => "Behavior 012 description"},
        {"codeValue" => "BEHAVIOR 012"}
    ]],
    "schoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2"
  }
end


# New entity in json
#{
#    "incidentIdentifier": "Incident ID XXX",
#    "incidentDate": "2012-02-14", 
#    "incidentTime": "01:00:00", 
#    "incidentLocation": "On School",
#    "behaviors": [[
#        {"shortDescription":"Behavior 012 description"},
#        {"codeValue": "BEHAVIOR 012"}
#    ]],
#    "schoolId": "eb3b8c35-f582-df23-e406-6947249a19f2"
#}
