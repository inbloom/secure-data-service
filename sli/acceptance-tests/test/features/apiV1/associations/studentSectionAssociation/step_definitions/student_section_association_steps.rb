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

  #teacher section association data
  id = 185                                      if human_readable_id == "ASSOCIATION COUNT"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 16                                       if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 16                                       if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "67ce204b-9999-4a11-aaae-000000000000"   if human_readable_id == "ASSOCIATION ID"
  id = "67ce204b-9999-4a11-aaae-000000000001"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "67ce204b-9999-4a11-aaae-000000000002"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentSectionAssociations"          if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentSectionAssociation"              if human_readable_id == "ASSOCIATION TYPE"
  id = "studentSectionAssociations"             if human_readable_id == "ASSOCIATION URI"
  
  #teacher related data
  id = "studentId"                              if human_readable_id == "ENDPOINT1 FIELD"
  id = "67ce204b-9999-4a11-aaaf-000000000940"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #section related data
  id = "sectionId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "67ce204b-9999-4a11-aaac-000000000004"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSection"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSections"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "section"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "sections"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "repeatIdentifier"                       if human_readable_id == "UPDATE FIELD"
  id = "Not repeated"                           if human_readable_id == "UPDATE FIELD EXPECTED VALUE"
  id = "Repeated, counted in grade point average"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE"
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #other
  id = "67ce204b-9999-4a11-aaaf-000000000254"   if human_readable_id == "Bradley Pearson"
  id = "67ce204b-9999-4a11-aaac-000000000000"   if human_readable_id == "math-8-6-1"
  
  #return the translated value
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "studentId" => "67ce204b-9999-4a11-aaaf-000000000254",
    "sectionId" => "67ce204b-9999-4a11-aaac-000000000000",
    "repeatIdentifier" => "Repeated, counted in grade point average",
    "beginDate" => "2011-12-01",
    "endDate" => "2012-01-01",
    "entityType" => "studentSectionAssociation"
  }
end