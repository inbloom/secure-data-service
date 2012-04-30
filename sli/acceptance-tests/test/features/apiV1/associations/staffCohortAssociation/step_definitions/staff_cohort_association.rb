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
  when "ASSOCIATION COUNT" then 11
  when "ASSOCIATION COUNT FOR ENDPOINT 1" then 2
  when "ASSOCIATION COUNT FOR ENDPOINT 2" then 3
  when "RESOLUTION COUNT FOR ENDPOINT 1" then 1
  when "RESOLUTION COUNT FOR ENDPOINT 2" then 3
  when "ASSOCIATION - MULTIPLE - ENDPOINT1 COUNT" then 2
  when "ASSOCIATION - MULTIPLE - ENDPOINT2 COUNT" then 2
  when "ASSOCIATION ID - SINGLE" then "f4e62753-43e7-4cab-843b-c6c4a0d6c1c3"
  when "ASSOCIATION ID FOR UPDATE" then "e7a8cbfc-86c5-471f-a28d-dafe57b599a3"
  when "ASSOCIATION ID FOR DELETE" then "65ce346d-10d2-41bd-973a-12ab17f7ed45"
  when "ASSOCIATION ID - MULTIPLE" then "ab9561f3-471b-44cf-b447-864644048aa3"
  when "ASSOCIATION LINK NAME" then "getStaffCohortAssociations"
  when "ASSOCIATION TYPE" then "staffCohortAssociation"
  when "ASSOCIATION URI" then "staffCohortAssociations"

    #staff related data
  when "ENDPOINT1 FIELD" then "staffId"
  when "ENDPOINT1 ID - SINGLE" then "f0e41d87-92d4-4850-9262-ed2f2723159b"
  when "ENDPOINT1 FIELD - SINGLE - EXPECTED VALUE" then ["f0e41d87-92d4-4850-9262-ed2f2723159b"]
  when "ENDPOINT1 ID - MULTIPLE" then "f0e41d87-92d4-4850-9262-ed2f2723159b"
  when "ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE" then ["0a26de79-222a-4d67-9301-5113ad50d43d", "21e57d58-f775-4cc8-b759-d8d9d811b5b4"]
  when "ENDPOINT1 LINK NAME" then "getStaff"
  when "ENDPOINT1 RESOLUTION LINK NAME" then "getStaff"
  when "ENDPOINT1 TYPE" then "staff"
  when "ENDPOINT1 URI" then "staff"

    #cohort related data
  when "ENDPOINT2 FIELD" then "cohortId"
  when "ENDPOINT2 ID - SINGLE" then "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"
  when "ENDPOINT2 FIELD - SINGLE - EXPECTED VALUE" then ["7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"]
  when "ENDPOINT2 ID - MULTIPLE" then "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"
  when "ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE" then ["a50121a2-c566-401b-99a5-71eb5cab5f4f", "a6929135-4782-46f1-ab01-b4df2e6ad093"]
  when "ENDPOINT2 LINK NAME" then "getCohort"
  when "ENDPOINT2 RESOLUTION LINK NAME" then "getCohorts"
  when "ENDPOINT2 TYPE" then "cohort"
  when "ENDPOINT2 URI" then "cohorts"

    #update related field data
  when "UPDATE FIELD" then "beginDate"
  when "UPDATE FIELD EXPECTED VALUE" then "2012-01-01"
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
