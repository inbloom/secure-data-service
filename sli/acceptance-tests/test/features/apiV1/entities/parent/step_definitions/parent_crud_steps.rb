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

Transform /^<([^"]*)>$/ do |val|

  case val
    #section data
  when "ENTITY COUNT" then 153
  when "ENTITY ID" then "1e1cdb04-2094-46b7-cd40-e3e481013480"
  when "ENTITY ID FOR UPDATE" then "5c4b1a9c-2fcd-4fa0-cd1c-f867cf4e7431"
  when "ENTITY ID FOR DELETE" then "4efb4262-bc49-f388-cd00-0000c9355700"
  when "ENTITY TYPE" then "parent"                                
  when "ENTITY URI" then "parents"                               

    #update related field data
  when "UPDATE FIELD" then "parentUniqueStateId"                       
  when "UPDATE FIELD EXPECTED VALUE" then "IdToUpdate"                                      
  when "UPDATE FIELD NEW VALID VALUE" then "UpdatedId"                                      

    #general
  when "INVALID REFERENCE" then "11111111-1111-1111-1111-111111111111"   
  when "SELF LINK NAME" then "self"                                   
  when "NEWLY CREATED ENTITY ID" then @newId                                   
  when "VALIDATION" then "Validation failed"                      

    #other
  when "BLANK" then ""                                       
  end
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "parentUniqueStateId" => "ParentID101",
    "name" => 
    { "firstName" => "John",
      "lastSurname" => "Doe",
    }
  }
end
