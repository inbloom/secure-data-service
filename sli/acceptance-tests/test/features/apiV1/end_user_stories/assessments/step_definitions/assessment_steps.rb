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
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "assessments"                                if human_readable_id == "ASSESSMENT URI"
  id = "teachers"                                   if human_readable_id == "TEACHER URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id = "sections"                                   if human_readable_id == "SECTION URI"
  id = "schools"                                    if human_readable_id == "SCHOOL URI"
  id = "custom"                                     if human_readable_id == "CUSTOM URI"
  id = "sectionAssessmentAssociations"              if human_readable_id == "SECTION ASSESSMENT ASSOC URI"
  id = "studentSectionAssociations"                 if human_readable_id == "STUDENT SECTION ASSOC URI"
  id = "studentAssessments"                                if human_readable_id == "STUDENT ASSESSMENT ASSOC URI"
  id = "teacherSectionAssociations"                 if human_readable_id == "TEACHER SECTION ASSOC URI"
  id = "learningStandards"                          if human_readable_id == "LEARNING STANDARDS ASSOC URI"
  id = "learningObjectives"                         if human_readable_id == "LEARNING OBJECTIVES ASSOC URI"

  #schools
  id = "studentSchoolAssociations"                  if human_readable_id == "STUDENT SCHOOL ASSOC URI"
  id = "ec2e4218-6483-4e9c-8954-0aecccfd4731"       if human_readable_id == "STUDENT SCHOOL URI"
  
  #sections
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" if human_readable_id == "'8th Grade English - Sec 6' ID"
  id = "58c9ef19-c172-4798-8e6e-c73e68ffb5a3_id"    if human_readable_id == "'Algebra II' ID"
  id = "baffb6f7-6d30-4341-b29e-0e1cd73ea2bf_id" if human_readable_id == "'Track and Field - Sec 6s10' ID"
  
  #students
  id = "0636ffd6-ad7d-4401-8de9-40538cf696c8_id" if human_readable_id == "'Preston Muchow' ID"
  id = "f7094bd8-46fc-4204-9fa2-a383fb71bdf6_id" if human_readable_id == "'Mayme Borc' ID"
  id = "6bfbdd9a-441a-490a-9f83-716785b61829_id" if human_readable_id == "'Malcolm Costillo' ID"
  id = "891faebe-bc84-4e0c-b7f3-195637cd981e_id" if human_readable_id == "'Tomasa Cleaveland' ID"
  id = "ffee781b-22b1-4015-81ff-3289ceb2c113_id" if human_readable_id == "'Merry Mccanse' ID"
  id = "5dd72fa0-98fe-4017-ab32-0bd33dc03a81_id" if human_readable_id == "'Samantha Scorzelli' ID"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id" if human_readable_id == "'Matt Sollars' ID"
  id = "5738c251-dd0b-4734-9ea6-417ac9320a15_id" if human_readable_id == "'Matt DERP' ID"
  id = "32932b97-d466-4d3c-9ebe-d58af010a87c_id" if human_readable_id == "'Dominic Brisendine' ID"
  id = "6f60028a-f57a-4c3d-895f-e34a63abc175_id" if human_readable_id == "'Lashawn Taite' ID"
  id = "4f81fd4c-c7c5-403e-af91-6a2a91f3ad04_id" if human_readable_id == "'Oralia Merryweather' ID"
  id = "766519bf-31f2-4140-97ec-295297bc045e_id" if human_readable_id == "'Dominic Bavinon' ID"
  id = "e13b1a7a-81d6-474c-b751-a6af054dbd6a_id" if human_readable_id == "'Rudy Bedoya' ID"
  id = "a17bd536-7502-4a4d-9d1f-538792b86795_id" if human_readable_id == "'Verda Herriman' ID"
  id = "7062c584-e229-4763-bf40-aec36bf112e7_id" if human_readable_id == "'Alton Maultsby' ID"
  id = "b1312b46-0a6b-4c6d-b73a-8cd7981e260e_id" if human_readable_id == "'Felipe Cianciolo' ID"
  id = "e0c2e40a-a472-4e78-9736-5ed0cbc16018_id" if human_readable_id == "'Lyn Consla' ID"
  id = "7ac04245-d931-447c-b8b2-aeef63fa3a4e_id" if human_readable_id == "'Felipe Wierzbicki' ID"
  id = "5714e819-0323-4281-b8d6-83604d3e95e8_id" if human_readable_id == "'Gerardo Giaquinto' ID"
  id = "2ec521f4-38e9-4982-8300-8df4eed2fc52_id" if human_readable_id == "'Holloran Franz' ID"
  id = "f11f341c-709b-4c8e-9b08-da9ff89ec0a9_id" if human_readable_id == "'Oralia Simmer' ID"
  id = "e62933f0-4226-4895-8fe3-aaffd5556032_id" if human_readable_id == "'Lettie Hose' ID"
  id = "903ea314-8212-4e9f-92b7-a18a25059804_id" if human_readable_id == "'Gerardo Saltazor' ID"
  id = "0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba_id" if human_readable_id == "'Lashawn Aldama' ID"
  id = "37edd9ae-3ac2-4bba-a8d8-be57461cd6de_id" if human_readable_id == "'Alton Ausiello' ID"
  id = "1d3e77f6-5f07-47c2-8086-b5aa6f4d703e_id" if human_readable_id == "'Marco Daughenbaugh' ID"
  id = "dbecaa89-29e6-41e1-8099-f80e29baf48e_id" if human_readable_id == "'Karrie Rudesill' ID"
  id = "414106a9-6156-47e3-a477-4bd4dda7e21a_id" if human_readable_id == "'Damon Iskra' ID"
  id = "e2d8ba15-953c-4cf7-a593-dbb419014901_id" if human_readable_id == "'Gerardo Rounsaville' ID"
    
  #teachers
  id = "67ed9078-431a-465e-adf7-c720d08ef512"       if human_readable_id == "'Linda Kim' ID"
  id = "e24b24aa-2556-994b-d1ed-6e6f71d1be97"       if human_readable_id == "'Ms. Smith' ID"
  
  #assessments
  id = "8b8fb81ea2153d439fc52f1376eb5b1ad8536a23_id"       if human_readable_id == "'Math Assessment' ID"
  id = "2108c0c84ca6998eb157e1efd4d894746e1fdf8b_id"       if human_readable_id == "'SAT' ID"
  id = "87fb8da5-e1aa-a6d9-efc7-b0eb091cd695_id" if human_readable_id == "'Most Recent SAT Student Assessment Association' ID"
  id = "87fc8da5-e1aa-a6d9-efc7-b0eb091cd695_id" if human_readable_id == "'SAT Student Assessment Association' ID"
  id = "e5d13e61-01aa-066b-efe0-710f7a0e8755_id" if human_readable_id == "'Most Recent Math Student Assessment Association' ID"
  id = "e5e13e61-01aa-066b-efe0-710f7a0e8755_id" if human_readable_id == "'Math Student Assessment Association' ID"
  id = "25d9d83d11cfa02c687d4ca91e92969261a43d2d_id"       if human_readable_id == "'Grade 2 BOY DIBELS' ID"
  id = "dd916592-7dfe-4e27-a8ac-bec5f4b757b7"       if human_readable_id == "'Grade 2 MOY READ2' ID"
  id = "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6"       if human_readable_id == "'Grade 2 BOY READ2' ID"
  id = "2899a720-4196-6112-9874-edde0e2541db_id1e0ddefb-875a-ef7e-b8c3-33bb5676115a_id"       if human_readable_id == "'Most Recent Student Assessment Association' ID"
  
  #teacher section associations
  id = "58c9ef19-c172-4798-8e6e-c73e68ffb5a3_id12f25c0f-75d7-4e45-8f36-af1bcc342871_id"       if human_readable_id == "'Teacher Ms. Jones and Section Algebra II' ID"
  
  id
end

Given /^I am a valid SEA\/LEA end user "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have a Role attribute returned from the "([^"]*)"$/ do |arg1|
# No code needed, this is done during the IDP configuration
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
# No code needed, this is done during the IDP configuration
end

Given /^I am authenticated on "([^"]*)"$/ do |arg1|
  idpRealmLogin(@user, @passwd, arg1)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I navigate to URI "([^"]*)" with filter sorting and pagination$/ do |href|
  @filterSortPaginationHref=href
end

When /^filter by "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @filterSortPaginationHref.include? "?"
    @filterSortPaginationHref = @filterSortPaginationHref+"&"+URI.escape(key)+"="+URI.escape(value)
  else
    @filterSortPaginationHref = @filterSortPaginationHref+"?"+URI.escape(key)+"="+URI.escape(value)
  end
end

When /^I submit the sorting and pagination request$/ do
  puts "\nAPI Call is: #{@filterSortPaginationHref}"
  step "I navigate to GET \"#{@filterSortPaginationHref}\""
  assert(@result != nil, "Response contains no data")
end

Then /^I should have a list of "([^"]*)" entities$/ do |entityType|
  assert(@result != nil, "Response contains no data")
  if @result.is_a?(Hash)
    assert(@result["entityType"] == entityType)
  else
    assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")

    @ids = Array.new
    @result.each do |entity|
      assert(entity["entityType"] == entityType)
      @ids.push(entity["id"])
    end
  end
end

Then /^I should have a list of (\d+) "([^"]*)" entities$/ do |size, entityType|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  if @result.is_a?(Hash)
    assert(@result["entityType"] == entityType, "Entity is not the right type, expected '#{entityType}', found '#{@result["entityType"]}'")
    assert(@result.length == size.to_i, "Entity count mismatch, found #{@result.length}, expected #{size}")
  else
    assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
    @ids = Array.new
    @result.each do |entity|
      assert(entity["entityType"] == entityType)
      @ids.push(entity["id"])
    end
    assert(@ids.size.to_s == size, "Got " + @ids.size.to_s + " entities, expected " + size.to_s + " in response.")
  end
end

Then /^I should have an entity with ID "([^"]*)"$/ do |entityId|
  found = false
  @ids.each do |id|
    if entityId == id
      found = true
    end
  end
  
  assert(found, "Entity id #{entityId} was not found")
end

Then /^the field "([^"]*)" should be "([^"]*)"$/ do |field, value|
  assert(@result != nil, "Response contains no data")
  val = @result.clone
  field.split(".").each do |part|
    is_num?(part) ? val = val[part.to_i] : val = val[part]
  end
   
  assert(val == value, "the #{field} is #{val}, not expected #{value}")
end

Then /^there are "([\d]*)" "([^"]*)"$/ do |count, collection|
  assert(@result[collection].length == convert(count), "Expected #{count} #{collection}, received #{@result[collection].length}")
  @col = @result[collection]
end

Then /^for the level at position "([\d]*)"$/ do |offset|
  @offset = convert(offset)
end

Then /^the key "([^"]*)" has value "([^"]*)"$/ do |key,value|
  val = @col[@offset].clone
  key.split(".").each do |part|
    is_num?(part) ? val = val[part.to_i] : val = val[part]
  end
  if is_num?(value)
    assert(val == value.to_i, "Expected value: #{value}, but received #{val}")
  else
    assert(val == value, "Expected value: #{value}, but received #{val}")
  end
end

def is_num?(str)
  Integer(str)
rescue ArgumentError
  false
else
  true
end

