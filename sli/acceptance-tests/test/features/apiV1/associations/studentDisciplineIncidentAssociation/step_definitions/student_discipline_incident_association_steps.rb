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
  when "ASSOCIATION COUNT" then 5
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 1
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 2
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 2
  when "ASSOCIATION ID" then "0e26de6c-225b-9f67-9625-5113ad50a03b"
  when "ASSOCIATION ID FOR UPDATE" then "0e26de6c-225b-9f67-9625-5113ad50a03b"
  when "ASSOCIATION ID FOR DELETE" then $createdId
  when "ASSOCIATION LINK NAME" then "getStudentDisciplineIncidentAssociations"
  when "ASSOCIATION TYPE" then "studentDisciplineIncidentAssociation"
  when "ASSOCIATION URI" then "studentDisciplineIncidentAssociations"
    
    #student related data
  when "ENDPOINT1 FIELD" then "studentId"
  when "ENDPOINT1 ID", "ENDPOINT1 FIELD EXPECTED VALUE" then "0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba"
  when "ENDPOINT1 LINK NAME" then "getStudent"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStudents"
  when "ENDPOINT1 TYPE" then "student"
  when "ENDPOINT1 URI" then "students"

    #discipline incident related data
  when "ENDPOINT2 FIELD" then "disciplineIncidentId"
  when "ENDPOINT2 ID", "ENDPOINT2 FIELD EXPECTED VALUE" then "0e26de79-222a-5e67-9201-5113ad50a03b"
  when "ENDPOINT2 LINK NAME" then "getDisciplineIncident"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getDisciplineIncidents"
  when "ENDPOINT2 TYPE" then "disciplineIncident"
  when "ENDPOINT2 URI" then "disciplineIncidents"

    #update related field data
  when "UPDATE FIELD" then "studentParticipationCode"
  when "UPDATE FIELD EXPECTED VALUE" then "Reporter"
  when "UPDATE FIELD NEW VALID VALUE" then "Witness"

    #general
  when "INVALID REFERENCE" then "11111111-1111-1111-1111-111111111111"
  when "INACCESSIBLE REFERENCE 1" then "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d"
  when "INACCESSIBLE REFERENCE 2" then "0e26de79-22aa-5d67-9201-5113ad50a03b"
  when "SELF LINK NAME" then "self"
  when "NEWLY CREATED ASSOCIATION ID" then $createdId = @newId
  when "VALIDATION" then "Validation failed"
  end
end




###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
    "disciplineIncidentId" => "0e26de79-22ea-5d67-9201-5113ad50a03b",    
    "studentParticipationCode" => "Reporter"
  }
end
