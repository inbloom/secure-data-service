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

  #student school association data
  id = 180                                      if human_readable_id == "ASSOCIATION COUNT"
  id = 60                                       if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 27                                       if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "e7e0e926-874e-4d05-9177-9776d44c5fb5"   if human_readable_id == "ASSOCIATION ID"
  id = "e7e0e926-874e-4d05-9177-9776d44c5fb5"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "e7e0e926-874e-4d05-9177-9776d44c5fb5"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentSchoolAssociations"           if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentSchoolAssociation"               if human_readable_id == "ASSOCIATION TYPE"
  id = "studentSchoolAssociations"              if human_readable_id == "ASSOCIATION URI"
  
  #school related data
  id = "schoolId"                               if human_readable_id == "ENDPOINT1 FIELD"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getSchool"                              if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getSchools"                             if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "school"                                 if human_readable_id == "ENDPOINT1 TYPE" 
  id = "schools"                                if human_readable_id == "ENDPOINT1 URI" 
  
  #student related data
  id = "studentId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "11d13fde-371c-4b58-b0b0-a6e2d955a947"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "entryGradeLevel"                        if human_readable_id == "UPDATE FIELD"
  id = "First grade"						    if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "Second grade"						    if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ASSOCIATION ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #other
  id = "41baa245-ceea-4336-a9dd-0ba868526b9b"  if human_readable_id == "'Algebra Alternative' ID"
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e"  if human_readable_id == "'Fall 2011 Session' ID"
  
  #return the translated value
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
     "studentId" => "714c1304-8a04-4e23-b043-4ad80eb60992",
     "schoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
     "entryGradeLevel" => "First grade",
     "entryDate" => "2011-09-01"
  }
end
