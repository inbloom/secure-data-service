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
  id = 8                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 5                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 4                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "0966614a-6c5d-4345-b451-7ec991823ac5"   if human_readable_id == "ASSOCIATION ID"
  id = "0966614a-6c5d-4345-b451-7ec991823ac5"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "0966614a-6c5d-4345-b451-7ec991823ac5"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStaffEducationOrgAssignmentAssociations" if human_readable_id == "ASSOCIATION LINK NAME"
  id = "staffEducationOrganizationAssociation"      if human_readable_id == "ASSOCIATION TYPE"
  id = "staffEducationOrgAssignmentAssociations"    if human_readable_id == "ASSOCIATION URI"
  
  #staff related data
  id = "staffReference"                         if human_readable_id == "ENDPOINT1 FIELD"
  id = "45d6c371-e7f1-4fa8-899a-e9f2309cbe4e"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "educationOrganizationReference"         if human_readable_id == "ENDPOINT2 FIELD"
  id = "bd086bae-ee82-4cf2-baf9-221a9407ea07"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getEducationOrganization"               if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getEducationOrganizations"              if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "educationOrganization"                  if human_readable_id == "ENDPOINT2 TYPE" 
  id = "educationOrganizations"                 if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "staffReference"                         if human_readable_id == "UPDATE FIELD"
  id = "45d6c371-e7f1-4fa8-899a-e9f2309cbe4e"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "7a295d99-015c-4f1c-8d84-4236d896b9b9"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "a249d5d9-f149-d348-9b10-b26d68e7cb9c"   if human_readable_id == "INACCESSIBLE REFERENCE 1"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"   if human_readable_id == "INACCESSIBLE REFERENCE 2"
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
    "staffReference" => "45d6c371-e7f1-4fa8-899a-e9f2309cbe4e",
    "educationOrganizationReference" => "bd086bae-ee82-4cf2-baf9-221a9407ea07",
    "staffClassification" => "Counselor", 
    "beginDate" => "2011-01-13"
}
end
