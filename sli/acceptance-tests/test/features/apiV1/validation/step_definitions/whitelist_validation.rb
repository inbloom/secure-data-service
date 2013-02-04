=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'

#########################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
#########################################################################

Given /^a valid document for ([^\"]*)$/ do |entity|
  @result = deep_copy($entityData[entity])
  @entity_type = entity
end

#########################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
#########################################################################

When /^I change ([^\"]*) to ([^\"]*)$/ do |key, value|
  keys = key.split(".")
  @result[key] = value if keys.length == 1
  steps "When I change the 2-level-nested \"#{key}\" to \"#{value}\"" if keys.length == 2
  steps "When I change the 3-level-nested \"#{key}\" to \"#{value}\"" if keys.length == 3
end

When /^I change the 2-level-nested "([^\"]*)" to "([^\"]*)"$/ do |key, value|
  keys = key.split(".")
  if @result[keys[0]].is_a? Array
    @result[keys[0]][0][keys[1]] = value
  else
    @result[keys[0]][keys[1]] = value
  end
end

When /^I change the 3-level-nested "([^\"]*)" to "([^\"]*)"$/ do |key, value|
  keys = key.split(".")
  if @result[keys[0]].is_a? Array
    if @result[keys[0]][0][keys[1]].is_a? Array
      @result[keys[0]][0][keys[1]][0][keys[2]] = value
    else
      @result[keys[0]][0][keys[1]][keys[2]] = value
    end
  else
    if @result[keys[0]][keys[1]].is_a? Array
      @result[keys[0]][keys[1]][0][keys[2]] = value
    else
      @result[keys[0]][keys[1]][keys[2]] = value
    end
  end
end

#########################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
#########################################################################

Then /^I should see "([^\"]*)" in the error message$/ do |keyword|
 assert(@res.body.to_s.downcase.include?(keyword.downcase), "Response does not contain \"#{keyword}\"")
end

#########################################################################
# DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA
#########################################################################

$entityData = {
    "student" => {
        "birthData" => {
            "birthDate" => "1994-04-04"
        },
        "sex" => "Male",
        "studentUniqueStateId" => "123456",
        "economicDisadvantaged" => false,
        "address" => [
            "streetNumberName" => "111 Ave C",
            "city" => "Chicago",
            "stateAbbreviation" => "IL",
            "postalCode" => "10098",
            "nameOfCounty" => "Wake",
            "countyFIPSCode" => "00183"
        ],
        "name" => {
            "firstName" => "Mister",
            "middleName" => "John",
            "lastSurname" => "Doe"
        },
        "electronicMail" => [
            "emailAddress" => "student@abcdef.com",
            "emailAddressType" => "Home/Personal"
        ],
        "profileThumbnail" => "http://www.google.com/photo.jpg"
    },
    "educationOrganization" => {
        "organizationCategories" => ["State Education Agency"],
        "stateOrganizationId" => "15",
        "nameOfInstitution" => "Gotham City School District",
        "address" => [
            "streetNumberName" => "111 Ave C",
            "city" => "Chicago",
            "stateAbbreviation" => "IL",
            "postalCode" => "10098",
            "nameOfCounty" => "Wake",
            "countyFIPSCode" => "00183"
        ],
        "webSite" => "http://www.google.com",
        "agencyHierarchyName" => "agency.h.name"
    },
    "staff" => {
        "staffUniqueStateId" => "WLVDSUSID00001",
        "staffIdentificationCode" => [
            "ID" => "STAFF123456",
            "identificationSystem" => "State",
            "assigningOrganizationCode" => "STATE11111"
        ],
        "address" => [
            "streetNumberName" => "111 Ave C",
            "city" => "Chicago",
            "stateAbbreviation" => "IL",
            "postalCode" => "10098",
            "nameOfCounty" => "Wake",
            "countyFIPSCode" => "00183"
        ],
        "sex" => "Male",
        "hispanicLatinoEthnicity" => false,
        "highestLevelOfEducationCompleted" => "Bachelor's",
        "name" => {
            "firstName" => "Teaches",
            "middleName" => "D.",
            "lastSurname" => "Students"
        },
        "electronicMail" => [
          "emailAddress" => "staff@abcdef.com",
          "emailAddressType" => "Organization"
        ]
    }
}
