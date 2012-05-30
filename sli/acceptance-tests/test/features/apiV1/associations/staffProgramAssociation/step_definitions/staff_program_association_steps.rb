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

  #staff program association data
  id = 3                                        if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 2                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "ASSOCIATION - MULTIPLE - ENDPOINT1 COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION - MULTIPLE - ENDPOINT2 COUNT"
  id = "9d19301f-54c7-48ae-b1c3-0ec1bd11fcec"   if human_readable_id == "ASSOCIATION ID - SINGLE"
  id = "9d19301f-54c7-48ae-b1c3-0ec1bd11fcec"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "9d19301f-54c7-48ae-b1c3-0ec1bd11fcec"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "9bf906cc-8fd5-11e1-86ec-0021701f543f"   if human_readable_id == "ASSOCIATION ID - MULTIPLE"
  id = "getStaffProgramAssociations"            if human_readable_id == "ASSOCIATION LINK NAME"
  id = "staffProgramAssociation"                if human_readable_id == "ASSOCIATION TYPE"
  id = "staffProgramAssociations"               if human_readable_id == "ASSOCIATION URI"
  
  #staff related data
  id = "staffId"                                if human_readable_id == "ENDPOINT1 FIELD"
  id = "85585b27-5368-4f10-a331-3abcaf3a3f4c"   if human_readable_id == "ENDPOINT1 ID - SINGLE"
  id = ["85585b27-5368-4f10-a331-3abcaf3a3f4c"] if human_readable_id == "ENDPOINT1 FIELD - SINGLE - EXPECTED VALUE"
  id = ["1b7b93b3-814a-4f30-86b9-bf19dd0064ff", "85585b27-5368-4f10-a331-3abcaf3a3f4c"] if human_readable_id == "ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE"
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 URI" 
  
  #program related data
  id = "programId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "9b8cafdc-8fd5-11e1-86ec-0021701f543f"   if human_readable_id == "ENDPOINT2 ID - SINGLE"
  id = ["9b8cafdc-8fd5-11e1-86ec-0021701f543f"] if human_readable_id == "ENDPOINT2 FIELD - SINGLE - EXPECTED VALUE"
  id = ["9b8c3aab-8fd5-11e1-86ec-0021701f543f", "9b8cafdc-8fd5-11e1-86ec-0021701f543f"] if human_readable_id == "ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE"
  id = "getProgram"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getPrograms"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "program"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "programs"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "beginDate"                              if human_readable_id == "UPDATE FIELD"
  id = "2011-06-01"                             if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
  id = "2012-03-07"                             if human_readable_id == "UPDATE FIELD NEW VALID VALUE" 
  
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
    "staffId" => ["21e57d58-f775-4cc8-b759-d8d9d811b5b4"],
    "programId" => ["e8d33606-d114-4ee4-878b-90ac7fc3df16"],
    "beginDate" => "2012-01-01"
}
end
