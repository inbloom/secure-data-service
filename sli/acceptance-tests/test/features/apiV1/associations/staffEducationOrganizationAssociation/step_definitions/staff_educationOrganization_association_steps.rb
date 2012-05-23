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
  id = 18                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 3                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "3620e862-8858-4a96-92d4-f6ac1d703033"   if human_readable_id == "ASSOCIATION ID"
  id = "3620e862-8858-4a96-92d4-f6ac1d703033"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "3620e862-8858-4a96-92d4-f6ac1d703033"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStaffEducationOrgAssignmentAssociations" if human_readable_id == "ASSOCIATION LINK NAME"
  id = "staffEducationOrganizationAssociation"      if human_readable_id == "ASSOCIATION TYPE"
  id = "staffEducationOrgAssignmentAssociations"    if human_readable_id == "ASSOCIATION URI"
  
  #staff related data
  id = "staffReference"                         if human_readable_id == "ENDPOINT1 FIELD"
  id = "87b82ed6-27d7-4af4-866f-be1693af549a"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "educationOrganizationReference"         if human_readable_id == "ENDPOINT2 FIELD"
  id = "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getEducationOrganization"               if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getEducationOrganizations"              if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "educationOrganization"                  if human_readable_id == "ENDPOINT2 TYPE" 
  id = "educationOrganizations"                 if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "staffReference"                         if human_readable_id == "UPDATE FIELD"
  id = "87b82ed6-27d7-4af4-866f-be1693af549a"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "269be4c9-a806-4051-a02d-15a7af3ffe3e"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "staffReference" => "0e26de79-222a-4d67-9201-5113ad50a03b",
    "educationOrganizationReference" => "d66fb6fd-691d-fde1-7f5f-efed78f7e1dc",
    "staffClassification" => "Counselor", 
    "beginDate" => "2011-01-13"
}
end
