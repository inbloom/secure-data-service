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

  #school data
  id = 33                                       if human_readable_id == "ENTITY COUNT"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2"   if human_readable_id == "ENTITY ID"
  id = "2058ddfb-b5c6-70c4-3bee-b43e9e93307d"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "fdacc41b-8133-f12d-5d47-358e6c0c791c"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "school"                                 if human_readable_id == "ENTITY TYPE"
  id = "schools"                                if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "nameOfInstitution"                      if human_readable_id == "UPDATE FIELD"
  id = "Yellow Middle School"                   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Yellow Middle and High School"          if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "shortNameOfInstitution" => "SCTS",
    "nameOfInstitution" => "School Crud Test School",
    "webSite" => "www.scts.edu",
    "stateOrganizationId" => "152901001",
    "organizationCategories" => ["School"],
    "address" => [
      "addressType" => "Physical",
      "streetNumberName" => "123 Main Street",
      "city" => "Lebanon",
      "stateAbbreviation" => "KS",
      "postalCode" => "66952",
      "nameOfCounty" => "Smith County"
    ],
    "gradesOffered" => [
      "Kindergarten",
      "First grade",
      "Second grade",
      "Third grade",
      "Fourth grade",
      "Fifth grade"
    ]
  }
end
