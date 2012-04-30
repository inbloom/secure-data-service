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
  id = 94                                       if human_readable_id == "ENTITY COUNT"
  id = "0e26de79-222a-4d67-9201-5113ad50a03b"   if human_readable_id == "ENTITY ID"
  id = "269be4c9-a806-4051-a02d-15a7af3ffe3e"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "c13cf9a6-6779-4de6-8b48-f3207952bfb8"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "staff"                                  if human_readable_id == "ENTITY TYPE"
  id = "staff"                                  if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "highestLevelOfEducationCompleted"       if human_readable_id == "UPDATE FIELD"
  id = "Bachelor's"                             if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Master's"                               if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "staffUniqueStateId" => "EMPLOYEE123456789",
    "sex" => "Male",
    "hispanicLatinoEthnicity" => false,
    "highestLevelOfEducationCompleted" => "Bachelor's",
    "name" => {
      "firstName" => "Teaches",
      "middleName" => "D.",
      "lastSurname" => "Students"
    }
  }
end
