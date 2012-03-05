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
  id = 34                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 4                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 4                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "660315c1-cf34-4904-b9f8-b5fb678c62d4"   if human_readable_id == "ASSOCIATION ID"
  id = "3220caff-8d95-40db-916d-bb969b2dbc01"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "661fa32f-afdd-4dbd-9423-faa2c095bae1"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getTeacherSectionAssociations"  		if human_readable_id == "ASSOCIATION LINK NAME"
  id = "teacherSectionAssociation"     			if human_readable_id == "ASSOCIATION TYPE"
  id = "teacherSectionAssociations"     		if human_readable_id == "ASSOCIATION URI"
  
  #teacher related data
  id = "teacherId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getTeacher"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getTeachers"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "teacher"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "teachers"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "sectionId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "4efb4262-bc49-f388-0000-0000c9355700"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSection"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSections"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "section"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "sections"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "sectionId"                         		if human_readable_id == "UPDATE FIELD"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "17a8658c-6fcb-4ece-99d1-b2dea1afd987"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "teacherId" => "a936f73f-7751-412d-922f-87ad78fd6bd1",
    "sectionId" => "4efb4262-bc49-f388-0000-0000c9355700",
    "classroomPosition" => "Teacher of Record"
}
end
