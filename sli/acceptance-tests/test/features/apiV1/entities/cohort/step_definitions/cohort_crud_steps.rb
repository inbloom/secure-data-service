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

  #cohort data
  id = 7                                        if human_readable_id == "ENTITY COUNT"
  id = "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"   if human_readable_id == "ENTITY ID"
  id = "a50121a2-c566-401b-99a5-71eb5cab5f4f"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "a6929135-4782-46f1-ab01-b4df2e6ad093"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "cohort"                                 if human_readable_id == "ENTITY TYPE"
  id = "cohorts"                                if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "cohortType"                             if human_readable_id == "REQUIRED FIELD"
  id = "programId"                        if human_readable_id == "UPDATE FIELD"
  id = ["e8d33606-d114-4ee4-878b-90ac7fc3df16"]                            if human_readable_id == "UPDATE FIELD EXPECTED VALUE"
  id = "[]" if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "cohortIdentifier" => "ACC-TEST-COH-4",
    "cohortDescription" => "ultimate frisbee team",
    "cohortType" => "Extracurricular Activity",
    "cohortScope" => "Statewide",
    "academicSubject" => "Physical, Health, and Safety Education",
    "educationOrgId" => "9f5cb095-8e99-49a9-b130-bedfa20639d2",
    "programId" => ["cb292c7d-3503-414a-92a2-dc76a1585d79"]
  }
end
