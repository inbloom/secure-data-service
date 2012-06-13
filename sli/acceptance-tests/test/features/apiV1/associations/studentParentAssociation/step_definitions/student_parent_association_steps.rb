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
  when "ASSOCIATION COUNT" then 4
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 1
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 1
  when "ASSOCIATION ID" then "dd69083f-a053-4819-a3cd-a162cdc627d7"
  when "ASSOCIATION ID FOR UPDATE" then "c5aa1969-492a-5150-8479-71bfc4d57f1e"
  when "ASSOCIATION ID FOR DELETE" then $createdId
  when "ASSOCIATION LINK NAME" then "getStudentParentAssociations"
  when "ASSOCIATION TYPE" then "studentParentAssociation"
  when "ASSOCIATION URI" then "studentParentAssociations"
    
    #student related data
  when "ENDPOINT1 FIELD" then "studentId"
  when "ENDPOINT1 ID", "ENDPOINT1 FIELD EXPECTED VALUE" then "74cf790e-84c4-4322-84b8-fca7206f1085"
  when "ENDPOINT1 LINK NAME" then "getStudent"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStudents"
  when "ENDPOINT1 TYPE" then "student"
  when "ENDPOINT1 URI" then "students"

    #parent related data
  when "ENDPOINT2 FIELD" then "parentId"
  when "ENDPOINT2 ID", "ENDPOINT2 FIELD EXPECTED VALUE" then "eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d"
  when "ENDPOINT2 LINK NAME" then "getParent"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getParents"
  when "ENDPOINT2 TYPE" then "parent"
  when "ENDPOINT2 URI" then "parents"

    #update related field data
  when "UPDATE FIELD" then "primaryContactStatus"
  when "UPDATE FIELD EXPECTED VALUE" then "false"
  when "UPDATE FIELD NEW VALID VALUE" then "true"

    #general
  when "INVALID REFERENCE" then "11111111-1111-1111-1111-111111111111"
  when "INACCESSIBLE REFERENCE 1" then "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d"
  when "INACCESSIBLE REFERENCE 2" then "cb7a932f-2d44-800c-cd5a-cdb25a29fc75"
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
    "parentId" => "38ba6ea7-7e73-47db-99e7-d0956f83d7e9"
  }
end
