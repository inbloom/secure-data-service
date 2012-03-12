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

  #student transcript (course) association data
  id = 9                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 3                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "09eced61-edd9-4826-a7bc-137ffecda877"   if human_readable_id == "ASSOCIATION ID"
  id = "310755a4-3473-4649-8a89-dbbb1ae86388"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "3b86eab4-1779-4fed-b38f-27b7dba5d41b"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getStudentTranscriptAssociations"  		if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentTranscriptAssociation"     		if human_readable_id == "ASSOCIATION TYPE"
  id = "studentTranscriptAssociations"     		if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #course related data
  id = "courseId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "53777181-3519-4111-9210-529350429899"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getCourse"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getCourses"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "course"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "courses"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "studentId"                         		if human_readable_id == "UPDATE FIELD"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "289c933b-ca69-448c-9afd-2c5879b7d221"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
	"studentId" => "e0e99028-6360-4247-ae48-d3bb3ecb606a", 
	"courseId" => "b8dbdefb-85b6-47e0-8a26-ef0f38568ddf",
	"courseAttemptResult" => "Pass", 
	"creditsEarned" => { 
		"credit" => 4.0
	}, 
	"gradeType" => "Final", 
	"finalLetterGradeEarned" => "A"}
end
