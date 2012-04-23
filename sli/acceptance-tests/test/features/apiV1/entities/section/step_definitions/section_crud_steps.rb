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

  #section data
  id = 132                                       if human_readable_id == "ENTITY COUNT"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480"   if human_readable_id == "ENTITY ID"
  id = "2934f72d-f9e3-48fd-afdd-56b94e2a3454"   if human_readable_id == "ENTITY ID FOR UPDATE"
  id = "c2efa2b3-f0c6-472a-b0d3-2e7495554acc"   if human_readable_id == "ENTITY ID FOR DELETE"
  id = "section"                                if human_readable_id == "ENTITY TYPE"
  id = "sections"                               if human_readable_id == "ENTITY URI"
  
  #update related field data
  id = "sequenceOfCourse"                       if human_readable_id == "UPDATE FIELD"
  id = "1"                                      if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "2"                                      if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
  #general
  id = "11111111-1111-1111-1111-111111111111"   if human_readable_id == "INVALID REFERENCE"
  id = "self"                                   if human_readable_id == "SELF LINK NAME" 
  id = @newId                                   if human_readable_id == "NEWLY CREATED ENTITY ID"
  id = "Validation failed"                      if human_readable_id == "VALIDATION"
  
  #other
  id = ""                                       if human_readable_id == "BLANK"
  
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "uniqueSectionCode" => "SpanishB09",
    "sequenceOfCourse" => 1,
    "educationalEnvironment" => "Off-school center",
    "mediumOfInstruction" => "Independent study",
    "populationServed" => "Regular Students",
    "schoolId" => "eb3b8c35-f582-df23-e406-6947249a19f2",
    "sessionId" => "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e",
    "courseId" => "53777181-3519-4111-9210-529350429899"
  }
end
