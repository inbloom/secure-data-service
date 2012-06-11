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
  id = 6                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "26a4a0fc-fad4-45f4-a00d-285acd1f83eb"   if human_readable_id == "ASSOCIATION ID"
  id = "26a4a0fc-fad4-45f4-a00d-285acd1f83eb"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "26a4a0fc-fad4-45f4-a00d-285acd1f83eb"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getTeacherSchoolAssociations"  		    if human_readable_id == "ASSOCIATION LINK NAME"
  id = "teacherSchoolAssociation"     			if human_readable_id == "ASSOCIATION TYPE"
  id = "teacherSchoolAssociations"     		    if human_readable_id == "ASSOCIATION URI"
  
  #teacher related data
  id = "teacherId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "67ed9078-431a-465e-adf7-c720d08ef512"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getTeacher"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getTeachers"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "teacher"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "teachers"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #school related data
  id = "schoolId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "ec2e4218-6483-4e9c-8954-0aecccfd4731"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSchool"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSchools"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "school"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "schools"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "schoolId"                         		if human_readable_id == "UPDATE FIELD"
  id = "ec2e4218-6483-4e9c-8954-0aecccfd4731"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "8f403e29-2a65-643e-6fac-5ccb53000db2"   if human_readable_id == "INACCESSIBLE REFERENCE 1"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"   if human_readable_id == "INACCESSIBLE REFERENCE 2"
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
