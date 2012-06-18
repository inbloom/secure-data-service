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

  #student section gradebook entry data
  id = 4                                        if human_readable_id == "ENTITY COUNT"
  id = "2713b97a-5632-44a5-8e04-031074bcb326"   if human_readable_id == "ENTITY ID"
  id = "31e52ceb-0439-4905-8d50-14b26a05c50f"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "7f05ef51-c974-4071-b91b-f644f9b087cf"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "studentSectionGradebookEntry"           if human_readable_id == "ENTITY TYPE"
  id = "studentGradebookEntries"         if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "gradebookEntryId"                       if human_readable_id == "REQUIRED FIELD"
  id = "gradebookEntryId"                       if human_readable_id == "UPDATE FIELD"
  id = "e49dc00c-182d-4f22-98c5-3d35b5f6d993"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "008fd89d-88a2-43aa-8af1-74ac16a29380"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "gradebookEntryId" => "008fd89d-88a2-43aa-8af1-74ac16a29380", 
    "letterGradeEarned" => "A", 
    "sectionId" => "706ee3be-0dae-4e98-9525-f564e05aa388", 
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085", 
    "numericGradeEarned" => 98, 
    "dateFulfilled" => "2012-01-31", 
    "diagnosticStatement" => "Finished the quiz in 5 minutes" 
  }
end
