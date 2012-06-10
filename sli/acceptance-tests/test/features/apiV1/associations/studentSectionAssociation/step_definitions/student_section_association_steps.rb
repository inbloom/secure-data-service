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

  #student section association data
  id = 265                                      if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 25                                       if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 25                                       if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "4ae72560-3518-4576-a35e-a9607668c9ad"   if human_readable_id == "ASSOCIATION ID"
  id = "4ae72560-3518-4576-a35e-a9607668c9ad"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "4ae72560-3518-4576-a35e-a9607668c9ad"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentSectionAssociations"          if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentSectionAssociation"              if human_readable_id == "ASSOCIATION TYPE"
  id = "studentSectionAssociations"             if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                              if human_readable_id == "ENDPOINT1 FIELD"
  id = "27fea52e-94ab-462c-b80f-7e868f6919d7"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #section related data
  id = "sectionId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSection"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSections"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "section"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "sections"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "homeroomIndicator"                       if human_readable_id == "UPDATE FIELD"
  id = "true"                           if human_readable_id == "UPDATE FIELD EXPECTED VALUE"
  id = "false"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE"
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #return the translated value
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378",
    "sectionId" => "7295e51e-cd51-4901-ae67-fa33966478c7",
    "repeatIdentifier" => "Repeated, counted in grade point average",
    "beginDate" => "2011-12-01",
    "endDate" => "2012-01-01",
    "entityType" => "studentSectionAssociation"
  }
end