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

  #student data
  id = 224                                      if human_readable_id == "ENTITY COUNT"
  id = "714c1304-8a04-4e23-b043-4ad80eb60992"   if human_readable_id == "ENTITY ID"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "c7146300-5bb9-4cc6-8b95-9e401ce34a03"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "student"                                if human_readable_id == "ENTITY TYPE"
  id = "students"                               if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "sex"                                    if human_readable_id == "UPDATE FIELD"
  id = "Female"                                 if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Male"                                   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "birthData" => {
      "birthDate" => "1994-04-04"
    },
    "sex" => "Male",
    "studentUniqueStateId" => "123456",
    "economicDisadvantaged" => false,
    "name" => {
      "firstName" => "Mister",
      "middleName" => "John",
      "lastSurname" => "Doe"
    }
  }
end
