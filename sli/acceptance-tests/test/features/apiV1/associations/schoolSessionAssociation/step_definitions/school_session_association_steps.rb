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
  id = 13                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 4                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 5                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 3                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 4                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = "67ce204b-9999-4a11-aacd-000000000000"   if human_readable_id == "ASSOCIATION ID"
  id = "67ce204b-9999-4a11-aacd-000000000001"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "67ce204b-9999-4a11-aacd-000000000002"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "getSchoolSessionAssociations"           if human_readable_id == "ASSOCIATION LINK NAME"
  id = "schoolSessionAssociation"               if human_readable_id == "ASSOCIATION TYPE"
  id = "schoolSessionAssociations"              if human_readable_id == "ASSOCIATION URI"
  
  #school related data
  id = "schoolId"                               if human_readable_id == "ENDPOINT1 FIELD"
  id = "67ce204b-9999-4a11-aaab-000000000008"   if human_readable_id == "ENDPOINT1 ID"          or human_readable_id == "ENDPOINT1 FIELD EXPECTED VALUE"
  id = "getSchool"                              if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getSchools"                             if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "school"                                 if human_readable_id == "ENDPOINT1 TYPE" 
  id = "schools"                                if human_readable_id == "ENDPOINT1 URI" 
  
  #session related data
  id = "sessionId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "67ce204b-9999-4a11-aacb-000000000000"   if human_readable_id == "ENDPOINT2 ID"          or human_readable_id == "ENDPOINT2 FIELD EXPECTED VALUE"
  id = "getSession"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getSessions"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "session"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "sessions"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "sessionId"                              if human_readable_id == "UPDATE FIELD"
  id = "67ce204b-9999-4a11-aacb-000000000000"   if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "67ce204b-9999-4a11-aacb-000000000001"   if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "sessionId" => "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e",
    "schoolId" => "41baa245-ceea-4336-a9dd-0ba868526b9b",
    "gradingPeriod" =>  {
      "gradingPeriod" => "Second Semester",
      "beginDate" => "2011-09-01",
      "endDate" => "2011-10-31",
      "totalInstructionalDays" =>  45
    }
  }
end
