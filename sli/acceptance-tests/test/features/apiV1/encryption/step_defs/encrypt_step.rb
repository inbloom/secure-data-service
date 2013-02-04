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


require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################
API_DB = PropLoader.getProps['DB_HOST']
API_DB_PORT = PropLoader.getProps['DB_PORT']

DB_NAME = ENV['DB_NAME'] ? ENV['DB_NAME'] : "Midgar";
API_DB_NAME = convertTenantIdToDbName(DB_NAME);


###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  id = "students"                                if human_readable_id == "STUDENT URI"
  id = "sections"                                if human_readable_id == "SECTION URI"
  id = "schools"                                 if human_readable_id == "SCHOOL URI"
  id = "studentSchoolAssociations"               if human_readable_id == "STUDENT SCHOOL ASSOCIATION URI"
  id = "studentSectionAssociations"              if human_readable_id == "STUDENT SECTION ASSOCIATION URI"
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" if human_readable_id == "English Sec 6"
  id = "45831a9d-772e-45b3-9024-fa76ca4fe558_id"    if human_readable_id == "English Sec 7"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"    if human_readable_id == "South Daybreak Elementary ID"

  id = @newId                                    if human_readable_id == "NEWLY CREATED ENTITY ID"

  #return the translated value
  id
end



###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^no record exists in "([^\"]*)" with a "([^\"]*)" of "([^\"]*)"$/ do |collection, field, value|
  conn = Mongo::Connection.new(API_DB, API_DB_PORT)
  db = conn[API_DB_NAME]
  col = db.collection(collection)
  resp = col.remove({field => value});
  col.find({field => value}).count().should == 0
end

Given /^parameter "([^\"]*)" is not "([^\"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "!=" "#{value}"}
end

Given /^parameter "([^"]*)" less than "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "<" "#{value}"}
end

Given /^parameter "([^"]*)" greater than "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" ">" "#{value}"}
end

Given /^parameter "([^"]*)" greater than or equal to "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" ">=" "#{value}"}
end

Given /^parameter "([^"]*)" less than or equal to "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "<=" "#{value}"}
end

Given /^parameter "([^"]*)" matches via regex "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "=~" "#{value}"}
end

When /^I have a valid school association for the student$/ do
  @fields = {
    "studentId" => @newId,
    "schoolId" => "b1bd3db6-d020-4651-b1b8-a8dba688d9e1",
    "entryDate" => "2012-11-02",
    "entryGradeLevel" => "Eighth grade"
  }
end

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  @fields = {
  		"loginId" => "rsd",
  		"sex" => "Female",
  		"studentCharacteristics" => [
  			{
  				"beginDate" => "2000-10-01",
  				"characteristic" => "Parent in Military"
  			}
  		],
  		"disabilities" => [
  			{
  				"disability" => "Other Health Impairment"
  			}
  		],
  		"hispanicLatinoEthnicity" => false,
  		"economicDisadvantaged" => false,
  		"cohortYears" => [
  			{
  				"schoolYear" => "2010-2011",
  				"cohortYearType" => "First grade"
  			}
  		],
  		"section504Disabilities" => [
  			"Medical Condition"
  		],
  		"oldEthnicity" => "Black, Not Of Hispanic Origin",
  		"race" => [
  			"Black - African American"
  		],
  		"programParticipations" => [
  			{
  				"program" => "Section 504 Placement"
  			}
  		],
  		"languages" => [
  			"English"
  		],
  		"studentUniqueStateId" => "530425896",
  		"name" => {
  			"middleName" => "Shannon",
  			"generationCodeSuffix" => "Jr",
  			"lastSurname" => "Delgado",
  			"firstName" => "Rhonda"
  		},
  		"birthData" => {
  			"birthDate" => "2006-07-02"
  		},
  		"otherName" => [
  			{
  				"middleName" => "Wren",
  				"lastSurname" => "Einstein",
  				"firstName" => "Julie",
  				"otherNameType" => "Nickname"
  			}
  		],
  		"studentIndicators" => [
  			{
  				"indicator" => "At risk",
  				"indicatorName" => "At risk"
  			}
  		],
  		"homeLanguages" => [
  			"English"
  		],
  		"learningStyles" => {
  			"visualLearning" => 33,
  			"tactileLearning" => 33,
  			"auditoryLearning" => 33
  		},
  		"limitedEnglishProficiency" => "NotLimited",
  		"studentIdentificationCode" => [ ],
  		"address" => [
  			{
  				"postalCode" => "27701",
  				"streetNumberName" => "1234 Shaggy",
  				"stateAbbreviation" => "NC",
  				"city" => "Durham"
  			}
  		],
  		"electronicMail" => [
  			{
  				"emailAddress" => "rsd@summer.nc.edu"
  			}
  		],
  		"schoolFoodServicesEligibility" => "Reduced price",
  		"displacementStatus" => "Slightly to the right",
  		"telephone" => [
  			{
  				"telephoneNumber" => "919-555-8765"
  			}
  		]
  	}
end

Given /^student Rhonda Delagio exists$/ do
  steps %Q{
    Given no record exists in "student" with a "body.studentUniqueStateId" of "530425896"
    Given a valid entity json document for a "student"
    When I navigate to POST "/<STUDENT URI>"
    Then I should receive a return code of 201
    Then I should receive an ID for the newly created student
  }
end

Given /^Rhonda Delagio is associated with "([^\"]*)"$/ do |section_id|
  @rhonda = @newId
  record = %Q{
    {
      "studentId" : "#{@rhonda}",
      "sectionId" : "#{section_id}",
      "beginDate" : "2011-08-21",
      "repeatIdentifier" : "Not repeated"
    }
  }
  puts record if ENV['DEBUG']
  @fields = JSON.parse(record)
  steps %Q{
    When I navigate to POST "/<STUDENT SECTION ASSOCIATION URI>"
    Then I should receive a return code of 201
    Then I should receive an ID for the newly created association
  }
  @rhondaAssoc = @newId
  puts @rhondaAssoc if ENV['DEBUG']
end


###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I find a mongo record in "([^\"]*)" with "([^\"]*)" equal to "([^\"]*)"$/ do |collection, searchTerm, value|
  conn = Mongo::Connection.new(API_DB, API_DB_PORT)
  db = conn[API_DB_NAME]
  col = db.collection(collection)

  @record = col.find_one({searchTerm => value})
  @record.should_not == nil
  conn.close
end

Then /^the field "([^\"]*)" has value "([^\"]*)"$/ do |field, value|
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object.to_s.should == value.to_s
end

Then /^the field "([^\"]*)" with value "([^\"]*)" is encrypted$/ do |field, value|
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object.should_not =~ /#{value}/i
end

Then /^all students should have "([^\"]*)" equal to "([^\"]*)"$/ do |field, value|
  @result.should be_a Array
  @result.each do |entity|
    object = entity
    field.split(".").each do |f|
      object[f].should_not == nil
      object = object[f]
    end
    object.should == value
  end
end

Then /^no student should have "([^"]*)" equal to "([^"]*)"$/ do |field, value|
  @result.should be_a Array
  @result.each do |entity|
    object = entity
    field.split(".").each do |f|
      object[f].should_not == nil
      object = object[f]
    end
    object.should_not == value
  end
end
