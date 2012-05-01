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

Transform /^<([^"]*)>$/ do |val|

  case val
    #student parent association data
  when "ASSOCIATION COUNT" then 6
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 1
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 1
  when "ASSOCIATION ID" then "0e26de6c-225b-9f67-8621-5113ad50a03b"
  when "ASSOCIATION ID FOR UPDATE" then "0e26de6c-225b-9f67-8e23-5113ad50a03b"
  when "ASSOCIATION ID FOR DELETE" then "0e26de6c-225b-9f67-8621-5113ad50a03b"
  when "ASSOCIATION LINK NAME" then "getStudentDisciplineIncidentAssociations"
  when "ASSOCIATION TYPE" then "studentDisciplineIncidentAssociation"
  when "ASSOCIATION URI" then "studentDisciplineIncidentAssociations"
    
    #student related data
  when "ENDPOINT1 FIELD" then "studentId"
  when "ENDPOINT1 ID", "ENDPOINT1 FIELD EXPECTED VALUE" then "714c1304-8a04-4e23-b043-4ad80eb60992"
  when "ENDPOINT1 LINK NAME" then "getStudent"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStudents"
  when "ENDPOINT1 TYPE" then "student"
  when "ENDPOINT1 URI" then "students"

    #discipline incident related data
  when "ENDPOINT2 FIELD" then "disciplineIncidentId"
  when "ENDPOINT2 ID", "ENDPOINT2 FIELD EXPECTED VALUE" then "0e26de79-226a-5d67-9201-5113ad50a03b"
  when "ENDPOINT2 LINK NAME" then "getDisciplineIncident"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getDisciplineIncidents"
  when "ENDPOINT2 TYPE" then "disciplineIncident"
  when "ENDPOINT2 URI" then "disciplineIncidents"

    #update related field data
  when "UPDATE FIELD" then "studentId"
  when "UPDATE FIELD EXPECTED VALUE" then "e0e99028-6360-4247-ae48-d3bb3ecb606a"
  when "UPDATE FIELD NEW VALID VALUE" then "e1af7127-743a-4437-ab15-5b0dacd1bde0"

    #general
  when "INVALID REFERENCE" then "11111111-1111-1111-1111-111111111111"
  when "SELF LINK NAME" then "self"
  when "NEWLY CREATED ASSOCIATION ID" then @newId
  when "VALIDATION" then "Validation failed"
  end
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "studentId" => "e1af7127-743a-4437-ab15-5b0dacd1bde0",
    "disciplineIncidentId" => "0e26de79-22aa-5d67-9201-5113ad50a03b",    
    "studentParticipationCode" => "Reporter"
  }
end
