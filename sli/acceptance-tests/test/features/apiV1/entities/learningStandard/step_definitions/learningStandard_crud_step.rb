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

  #assessment data
  id = 15                                       if human_readable_id == "ENTITY COUNT"
  id = "df9165f2-657e-8e27-a8ec-bdc5f4b75777"   if human_readable_id == "ENTITY ID"
  id = "df9165f2-65be-8e27-a82c-bec5f4b75778"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "df9165f2-653e-8f27-a86c-bec5f4b75779"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "learningStandard"                       if human_readable_id == "ENTITY TYPE"
  id = "learningStandards"                      if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "gradeLevel"                             if human_readable_id == "UPDATE FIELD"
  id = "Eighth grade"                           if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Ninth grade"                            if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "learningStandardId" => {
     "identificationCode" => "G.SRT.1"},
    "description" => "a description",
    "gradeLevel" => "Ninth grade",
    "contentStandard"=>"State Standard",
    "subjectArea" => "English"
  }
end
