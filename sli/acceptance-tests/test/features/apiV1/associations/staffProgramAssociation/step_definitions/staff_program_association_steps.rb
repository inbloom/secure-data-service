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
  id = 11                                       if human_readable_id == "ASSOCIATION COUNT"
  id = 2                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 1"
  id = 6                                        if human_readable_id == "ASSOCIATION COUNT FOR ENDPOINT 2"
  id = 1                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 1"
  id = 5                                        if human_readable_id == "RESOLUTION COUNT FOR ENDPOINT 2"
  id = 2                                        if human_readable_id == "ASSOCIATION - MULTIPLE - ENDPOINT1 COUNT" 
  id = 2                                        if human_readable_id == "ASSOCIATION - MULTIPLE - ENDPOINT2 COUNT"
  id = "22f88217-4dc0-4113-a712-b6027c241606"   if human_readable_id == "ASSOCIATION ID - SINGLE"
  id = "387f4818-b879-456a-acc7-5b4294c94549"   if human_readable_id == "ASSOCIATION ID FOR UPDATE"
  id = "9aaa3307-fc54-484d-bf4d-1f459aa75ee0"   if human_readable_id == "ASSOCIATION ID FOR DELETE"
  id = "5b3ecdfc-5244-4309-8570-6e549a881659"   if human_readable_id == "ASSOCIATION ID - MULTIPLE" 
  id = "getStaffProgramAssociations"            if human_readable_id == "ASSOCIATION LINK NAME"
  id = "staffProgramAssociation"                if human_readable_id == "ASSOCIATION TYPE"
  id = "staffProgramAssociations"               if human_readable_id == "ASSOCIATION URI"
  
  #staff related data
  id = "staffId"                                if human_readable_id == "ENDPOINT1 FIELD"
  id = "f0e41d87-92d4-4850-9262-ed2f2723159b"   if human_readable_id == "ENDPOINT1 ID - SINGLE"
  id = ["f0e41d87-92d4-4850-9262-ed2f2723159b"] if human_readable_id == "ENDPOINT1 FIELD - SINGLE - EXPECTED VALUE"
  id = ["0a26de79-222a-4d67-9301-5113ad50d43d", "21e57d58-f775-4cc8-b759-d8d9d811b5b4"] if human_readable_id == "ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE"
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 LINK NAME" 
  id = "getStaff"                               if human_readable_id == "ENDPOINT1 RESOLUTION LINK NAME" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 TYPE" 
  id = "staff"                                  if human_readable_id == "ENDPOINT1 URI" 
  
  #educationOrganization related data
  id = "programId"                              if human_readable_id == "ENDPOINT2 FIELD"
  id = "e8d33606-d114-4ee4-878b-90ac7fc3df16"   if human_readable_id == "ENDPOINT2 ID - SINGLE"
  id = ["e8d33606-d114-4ee4-878b-90ac7fc3df16"] if human_readable_id == "ENDPOINT2 FIELD - SINGLE - EXPECTED VALUE"
  id = ["cb292c7d-3503-414a-92a2-dc76a1585d79", "e8d33606-d114-4ee4-878b-90ac7fc3df16"] if human_readable_id == "ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE"
  id = "getProgram"                             if human_readable_id == "ENDPOINT2 LINK NAME" 
  id = "getPrograms"                            if human_readable_id == "ENDPOINT2 RESOLUTION LINK NAME" 
  id = "program"                                if human_readable_id == "ENDPOINT2 TYPE" 
  id = "programs"                               if human_readable_id == "ENDPOINT2 URI" 
  
  #update related field data
  id = "beginDate"                              if human_readable_id == "UPDATE FIELD"
  id = "2012-01-01"                             if human_readable_id == "UPDATE FIELD EXPECTED VALUE" 
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
