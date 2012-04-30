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

  #staff data
  id = 60                                       if human_readable_id == "ENTITY COUNT"
  id = "4f0c9368-8488-7b01-0000-000059f9ba56"   if human_readable_id == "ENTITY ID"
  id = "d66fb6fd-691d-fde1-7f5f-efed78f7e1dc"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "2d7583b1-f8ec-45c9-a6da-acc4e6fde458"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "educationOrganization"                  if human_readable_id == "ENTITY TYPE"
  id = "educationOrganizations"                 if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "nameOfInstitution"                      if human_readable_id == "UPDATE FIELD"
  id = "Krypton School District"                if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Bananas School District"                if human_readable_id == "UPDATE FIELD NEW VALID VALUE"
  id = "organizationCategories"                 if human_readable_id == "REQUIRED FIELD" 
  
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
    "organizationCategories" => ["State Education Agency"],
    "stateOrganizationId" => "15",
    "nameOfInstitution" => "Gotham City School District", 
    "address" => []
  }
end
