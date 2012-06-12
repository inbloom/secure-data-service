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
  id = 9                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "32b86a2a-e55c-4689-aedf-4b676f3da3fc"   if human_readable_id == "ASSOCIATION ID"
  id = "32b86a2a-e55c-4689-aedf-4b676f3da3fc"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "32b86a2a-e55c-4689-aedf-4b676f3da3fc"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getTeacherSectionAssociations"  		if human_readable_id == "ASSOCIATION LINK NAME"
  id = "teacherSectionAssociation"     			if human_readable_id == "ASSOCIATION TYPE"
  id = "teacherSectionAssociations"     		if human_readable_id == "ASSOCIATION URI"
  
  #teacher related data
  id = "teacherId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getTeacher"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getTeachers"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "teacher"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "teachers"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "sectionId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "15ab6363-5509-470c-8b59-4f289c224107"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSection"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSections"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "section"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "sections"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "sectionId"                         		if human_readable_id == "UPDATE FIELD"
  id = "15ab6363-5509-470c-8b59-4f289c224107"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "706ee3be-0dae-4e98-9525-f564e05aa388"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "8f403e29-2a65-643e-6fac-5ccb53000db2"   if human_readable_id == "INACCESSIBLE REFERENCE 1"
  id = "a47eb9aa-1c97-4c8e-9d0a-45689a66d4f8"   if human_readable_id == "INACCESSIBLE REFERENCE 2"
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
    "teacherId" => "edce823c-ee28-4840-ae3d-74d9e9976dc5",
    "sectionId" => "7295e51e-cd51-4901-ae67-fa33966478c7",
    "classroomPosition" => "Teacher of Record"
}
end
