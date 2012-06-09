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
  id = 3                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "f11a2a30-d4fd-4400-ae18-353c00d581a2"   if human_readable_id == "ASSOCIATION ID"
  id = "f11a2a30-d4fd-4400-ae18-353c00d581a2"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "f11a2a30-d4fd-4400-ae18-353c00d581a2"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getCourseTranscripts"			  		if human_readable_id == "ASSOCIATION LINK NAME"
  id = "studentTranscriptAssociation"			if human_readable_id == "ASSOCIATION TYPE"
  id = "courseTranscripts"			     		if human_readable_id == "ASSOCIATION URI"
  
  #student related data
  id = "studentId"                         		if human_readable_id == "ENDPOINT1 FIELD"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getStudent"                             if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStudents"                            if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "student"                                if human_readable_id == "ENDPOINT1 TYPE" 
  id = "students"                               if human_readable_id == "ENDPOINT1 URI" 
  
  #course related data
  id = "courseId"         						if human_readable_id == "ENDPOINT2 FIELD"
  id = "f917478f-a6f2-4f78-ad9d-bf5972b5567b"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getCourse"               				if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getCourses"              				if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "course"                  				if human_readable_id == "ENDPOINT2 TYPE" 
  id = "courses"                 				if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "studentId"                         		if human_readable_id == "UPDATE FIELD"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "27fea52e-94ab-462c-b80f-7e868f6919d7"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
	"courseId" => "82ad1eb0-c6d4-4b00-909a-edd1c8d04e41",
	"courseAttemptResult" => "Pass", 
	"creditsEarned" => { 
		"credit" => 4.0
	}, 
	"gradeType" => "Final", 
	"finalLetterGradeEarned" => "A"}
end
