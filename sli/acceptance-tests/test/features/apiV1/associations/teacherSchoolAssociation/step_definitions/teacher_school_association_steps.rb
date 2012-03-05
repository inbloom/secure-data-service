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

  #teacher school association data
  id = 15                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 4                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 4                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "52c1f410-602e-46b6-9b40-77bf55d77568"   if human_readable_id == "ASSOCIATION ID"
  id = "53616a21-df46-4990-aca7-2c8514a9fdb4"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "53612aba-7a46-4990-aca7-2c8514a9fdb4"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getTeacherSchoolAssociations"  		    if human_readable_id == "ASSOCIATION LINK NAME"
  id = "teacherSchoolAssociation"     			if human_readable_id == "ASSOCIATION TYPE"
  id = "teacherSchoolAssociations"     		    if human_readable_id == "ASSOCIATION URI"
  
  #teacher related data
  id = "teacherId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "244520d2-8c6b-4a1e-b35e-d67819ec0211"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getTeacher"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getTeachers"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "teacher"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "teachers"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #school related data
  id = "schoolId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSchool"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSchools"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "school"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "schools"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "schoolId"                         		if human_readable_id == "UPDATE FIELD"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "b6ad1eb2-3cf7-41c4-96e7-2f393f0dd847"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "teacherId" => "244520d2-8c6b-4a1e-b35e-d67819ec0211",
    "schoolId" => "0f464187-30ff-4e61-a0dd-74f45e5c7a9d",
    "programAssignment" => "Regular Education"
  }
end
