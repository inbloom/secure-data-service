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

  #program data
  id = 5                                       if human_readable_id == "ENTITY COUNT"
  id = "e8d33606-d114-4ee4-878b-90ac7fc3df16"  if human_readable_id == "ENTITY ID"
  id = "cb292c7d-3503-414a-92a2-dc76a1585d79"  if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "e8d33606-d114-4ee4-878b-90ac7fc3df16"  if human_readable_id == "ENTITY ID FOR DELETE"
  id = "program"                               if human_readable_id == "ENTITY TYPE"
  id = "programs"                              if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "programSponsor"                        if human_readable_id == "UPDATE FIELD"
  id = "Local Education Agency"                if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "State Education Agency"                if human_readable_id == "UPDATE FIELD NEW VALID VALUE"
  id = "programType"                           if human_readable_id == "REQUIRED FIELD" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"  if human_readable_id == "INVALID REFERENCE"
  id = "self"                                  if human_readable_id == "SELF LINK NAME" 
  id = @newId                                  if human_readable_id == "NEWLY CREATED ENTITY ID"
  id = "Validation failed"                     if human_readable_id == "VALIDATION"
  
  #other
  id = ""                                      if human_readable_id == "BLANK"
  
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "programId" => "ACC-TEST-PROG-3",
    "programType" => "Remedial Education",
    "programSponsor" => "Local Education Agency",
    "services" => [[
        {"codeValue" => "codeValue3"},
        {"shortDescription" => "Short description for acceptance test program 3"},
        {"description" => "This is a longer description of the services provided by acceptance test program 3. More detail could be provided here."}]]
  }
end
