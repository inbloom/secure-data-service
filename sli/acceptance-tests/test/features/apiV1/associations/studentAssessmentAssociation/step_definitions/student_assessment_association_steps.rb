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

  #student assessment association data
  id = 11                                      if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 3                                      if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "e85b5aa7-465a-6dd3-8ffb-d02461ed79f8"   if human_readable_id == "ASSOCIATION ID"
  id = "e85b5aa7-465a-6dd3-8ffb-d02461ed79f8"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "e85b5aa7-465a-6dd3-8ffb-d02461ed79f8"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentAssessments"			        if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentAssessmentAssociation"           if human_readable_id == "ASSOCIATION TYPE"
  id = "studentAssessments"			            if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                              if human_readable_id == "ENDPOINT1 FIELD"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #assessment related data
  id = "assessmentId"                           if human_readable_id == "ENDPOINT2 FIELD"
  id = "dd916592-7d7e-5d27-a87d-dfc7fcb757f6"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getAssessment"                          if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getAssessments"                         if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "assessment"                             if human_readable_id == "ENDPOINT2 TYPE" 
  id = "assessments"                            if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "retestIndicator"                        if human_readable_id == "UPDATE FIELD"
  id = "1st Retest"                             if human_readable_id == "UPDATE FIELD EXPECTED VALUE"
  id = "2nd Retest"                             if human_readable_id == "UPDATE FIELD NEW VALID VALUE"
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d"   if human_readable_id == "INACCESSIBLE REFERENCE 1"
  id = "dd916592-7d3e-4f27-a8ac-bec5f4b757f6"   if human_readable_id == "INACCESSIBLE REFERENCE 2"
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
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
    "assessmentId" => "dd916592-7d7e-5d27-a87d-dfc7fcb757f6",
    "administrationDate" => "2011-10-01",
    "administrationEndDate" => "2012-01-01",
    "retestIndicator" => "1st Retest",
    "entityType" => "studentAssessmentAssociation"
  }
end