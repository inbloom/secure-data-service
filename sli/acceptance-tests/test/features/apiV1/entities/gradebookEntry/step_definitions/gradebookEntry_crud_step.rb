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

  #gradebook entry data
  id = 4                                        if human_readable_id == "ENTITY COUNT"
  id = "008fd89d-88a2-43aa-8af1-74ac16a29380"   if human_readable_id == "ENTITY ID"
  id = "e49dc00c-182d-4f22-98c5-3d35b5f6d993"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "0b980a49-56b6-4d17-847b-2997b7227686"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "gradebookEntry"                         if human_readable_id == "ENTITY TYPE"
  id = "gradebookEntries"                       if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "gradebookEntryType"                     if human_readable_id == "REQUIRED FIELD"
  id = "gradebookEntryType"                     if human_readable_id == "UPDATE FIELD"
  id = "Quiz"                                   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Homework"                               if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "gradebookEntryType" => "Quiz", 
    "dateAssigned" => "2012-02-14", 
    "sectionId" => "58c9ef19-c172-4798-8e6e-c73e68ffb5a3"
  }
end
