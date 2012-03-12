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
  when "ASSOCIATION COUNT" then 129
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 2
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 2
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 1
  when "ASSOCIATION ID" then "3722a00a-b6d3-4003-84c7-71cbad22dbae"
  when "ASSOCIATION ID FOR UPDATE" then "65fc0e76-e8ac-46fc-b7a7-04df5fbd08b4"
  when "ASSOCIATION ID FOR DELETE" then "678db2c8-25dc-4ee2-89aa-e1d1bdfdbcb2"
  when "ASSOCIATION LINK NAME" then "getStudentParentAssociations"
  when "ASSOCIATION TYPE" then "studentParentAssociation"
  when "ASSOCIATION URI" then "studentParentAssociations"
    
    #student related data
  when "ENDPOINT1 FIELD" then "studentId"
  when "ENDPOINT1 ID", "ENDPOINT1 FIELD EXPECTED VALUE" then "fe7719d2-7e7d-457f-843f-76734db99388"
  when "ENDPOINT1 LINK NAME" then "getStudent"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStudents"
  when "ENDPOINT1 TYPE" then "student"
  when "ENDPOINT1 URI" then "students"

    #parent related data
  when "ENDPOINT2 FIELD" then "parentId"
  when "ENDPOINT2 ID", "ENDPOINT2 FIELD EXPECTED VALUE" then "0e950fce-4e47-4000-836d-c5f566fe2d74"
  when "ENDPOINT2 LINK NAME" then "getParent"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getParents"
  when "ENDPOINT2 TYPE" then "parent"
  when "ENDPOINT2 URI" then "parents"

    #update related field data
  when "UPDATE FIELD" then "studentId"
  when "UPDATE FIELD EXPECTED VALUE" then "60779bc1-87bb-455b-99f4-066a505d79e4"
  when "UPDATE FIELD NEW VALID VALUE" then "53560d13-f663-4683-8aa5-fbfcbf46432d"

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
    "studentId" => "e0e99028-6360-4247-ae48-d3bb3ecb606a",
    "parentId" => "96adf0d1-c482-447c-8386-b0702e084134"
  }
end
