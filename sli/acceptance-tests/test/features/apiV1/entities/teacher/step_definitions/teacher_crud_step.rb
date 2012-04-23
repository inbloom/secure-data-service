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

  #teacher data
  id = 45                                       if human_readable_id == "ENTITY COUNT"
  id = "fa45033c-5517-b14b-1d39-c9442ba95782"   if human_readable_id == "ENTITY ID"
  id = "344cf68d-50fd-8dd7-e8d6-ed9df76c219c"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "824643f7-174b-4a50-9383-c9a6f762c49d"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "teacher"                                if human_readable_id == "ENTITY TYPE"
  id = "teachers"                               if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "highlyQualifiedTeacher"                 if human_readable_id == "UPDATE FIELD"
  id = "false"                                  if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "true"                                   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "birthDate" => "1954-08-31",
    "sex" => "Male",
    "yearsOfPriorTeachingExperience" => 32,
    "staffUniqueStateId" => "12345678",
    "highlyQualifiedTeacher" => true,
    "highestLevelOfEducationCompleted" => "Master's",
    "name" => {
      "firstName" => "Rafe",
      "middleName" => "Hairfire",
      "lastSurname" => "Esquith"
    }
  }
end
