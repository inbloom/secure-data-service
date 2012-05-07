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
    #staff cohort association data
  when "ASSOCIATION COUNT" then 13
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 2
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 2
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 3
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 2
  when "ASSOCIATION - MULTIPLE - ENDPOINT1 COUNT" then 2
  when "ASSOCIATION - MULTIPLE - ENDPOINT2 COUNT" then 2
  when "ASSOCIATION ID - SINGLE" then "b4e31b1a-8e55-8803-722c-14d8087c0712"
  when "ASSOCIATION ID FOR UPDATE" then "b4e31b1a-8e55-8803-722c-14d8087c0712"
  when "ASSOCIATION ID FOR DELETE" then "b4e31b1a-8e55-8803-722c-14d8087c0712"
  when "ASSOCIATION ID - MULTIPLE" then "8fef446f-fc63-15f9-8606-0b85086c07d5"
  when "ASSOCIATION LINK NAME" then "getStaffCohortAssociations"
  when "ASSOCIATION TYPE" then "staffCohortAssociation"
  when "ASSOCIATION URI" then "staffCohortAssociations"

    #staff related data
  when "ENDPOINT1 FIELD" then "staffId"
  when "ENDPOINT1 ID - SINGLE" then "85585b27-5368-4f10-a331-3abcaf3a3f4c"
  when "ENDPOINT1 FIELD - SINGLE - EXPECTED VALUE" then ["85585b27-5368-4f10-a331-3abcaf3a3f4c"]
  when "ENDPOINT1 ID - MULTIPLE" then "f0e41d87-92d4-4850-9262-ed2f2723159b"
  when "ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE" then ["85585b27-5368-4f10-a331-3abcaf3a3f4c", "b4c2a73f-336d-4c47-9b47-2d24871eef96"]
  when "ENDPOINT1 LINK NAME" then "getStaff"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStaff"
  when "ENDPOINT1 TYPE" then "staff"
  when "ENDPOINT1 URI" then "staff"

    #cohort related data
  when "ENDPOINT2 FIELD" then "cohortId"
  when "ENDPOINT2 ID - SINGLE" then "b40926af-8fd5-11e1-86ec-0021701f543f"
  when "ENDPOINT2 FIELD - SINGLE - EXPECTED VALUE" then ["b40926af-8fd5-11e1-86ec-0021701f543f"]
  when "ENDPOINT2 ID - MULTIPLE" then "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"
  when "ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE" then ["b408635d-8fd5-11e1-86ec-0021701f543f", "b408d88e-8fd5-11e1-86ec-0021701f543f"]
  when "ENDPOINT2 LINK NAME" then "getCohort"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getCohorts"
  when "ENDPOINT2 TYPE" then "cohort"
  when "ENDPOINT2 URI" then "cohorts"

    #update related field data
  when "UPDATE FIELD" then "beginDate"
  when "UPDATE FIELD EXPECTED VALUE" then "2010-01-15"
  when "UPDATE FIELD NEW VALID VALUE" then "2012-03-07"

    #general
  when "INVALID REFERENCE" then "11111111-1111-1111-1111-111111111111"
  when "SELF LINK NAME" then "self"
  when "NEWLY CREATED ASSOCIATION ID" then @newId
  when "VALIDATION" then "Validation failed"
  end
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid association json document for a "([^"]*)"$/ do |arg1|
  @fields = {
    "staffId" => ["21e57d58-f775-4cc8-b759-d8d9d811b5b4"],
    "cohortId" => ["a50121a2-c566-401b-99a5-71eb5cab5f4f"],
    "beginDate" => "2012-02-29",
    "endDate" => "2012-03-29",
    "studentRecordAccess" => true
  }
end
