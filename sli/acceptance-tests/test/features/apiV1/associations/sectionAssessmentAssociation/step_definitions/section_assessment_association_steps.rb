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

  #school session association data
  id = 22                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 3                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 4                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 4                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "df3e6d22-1c3f-460a-870a-49aba80bfb18"   if human_readable_id == "ASSOCIATION ID"
  id = "1580e803-04ea-4720-8439-5f0f4834decd"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "62819875-d666-4b34-98ae-f115cbf90217"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getSectionAssessmentAssociations"       if human_readable_id == "ASSOCIATION LINK NAME"
  id = "sectionAssessmentAssociation"           if human_readable_id == "ASSOCIATION TYPE"
  id = "sectionAssessmentAssociations"          if human_readable_id == "ASSOCIATION URI"
  
  #section related data
  id = "sectionId"                              if human_readable_id == "ENDPOINT1 FIELD"
  id = "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getSection"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getSections"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "section"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "sections"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #assessment related data
  id = "assessmentId"                           if human_readable_id == "ENDPOINT2 FIELD"
  id = "6a53f63e-deb8-443d-8138-fc5a7368239c"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getAssessment"                          if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getAssessments"                         if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "assessment"                             if human_readable_id == "ENDPOINT2 TYPE" 
  id = "assessments"                            if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "sectionId"                              if human_readable_id == "UPDATE FIELD"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "67ce204b-9999-4a11-aaac-000000000005"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #other
  id = "41baa245-ceea-4336-a9dd-0ba868526b9b"   if human_readable_id == "'Algebra Alternative' ID"
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e"   if human_readable_id == "'Fall 2011 Session' ID"
  
  #return the translated value
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "sectionId" => "67ce204b-9999-4a11-aaac-000000000005",
    "assessmentId" => "6a53f63e-deb8-443d-8138-fc5a7368239c",
    "entityType" => "sectionAssessmentAssociation"
  }
end
