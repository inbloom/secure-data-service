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

Transform /^<(.*?)>$/ do |human_readable_id|
  # teacher hash transforms
  id = @teacher["id"]                                            if human_readable_id == "teacher id"
  id = @teacher["getSchools"]                                    if human_readable_id == "getSchools"
  id = @teacher["getSections"]                                   if human_readable_id == "getSections"
  id = @teacher["getEducationOrganizations"]                     if human_readable_id == "getEducationOrganizations"
  id = @teacher["getTeacherSchoolAssociations"]                  if human_readable_id == "getTeacherSchoolAssociations"
  id = @teacher["getTeacherSectionAssociations"]                 if human_readable_id == "getTeacherSectionAssociations"
  id = @teacher["getStaffEducationOrgAssignmentAssociations"]    if human_readable_id == "getStaffEducationOrgAssignmentAssociations"
  id = @teacher["sectionId"]                                     if human_readable_id == "teacher section list"
  id = @teacher["sectionId"][0]                                  if human_readable_id == "teacher section"
  id = @teacher["studentAssessments"]                            if human_readable_id == "student assessment list"
  id = @teacher["studentAssessments"][0]                         if human_readable_id == "student assessment"
  id = @teacher["assessment"]                                    if human_readable_id == "assessment"
  id = @teacher["student"]                                       if human_readable_id == "student"

  # Ugly response body field transforms
  # The dot-delimited structure is used for nested hashes like: body.name.lastSurname
  # The zeroes mean that field is an array, and we are taking the first element in it
  # These dot-delmited strings are passed to fieldExtract, which recursively
  # walks the response body and ultimately returns the field we desire
  id = "true"                                                  if human_readable_id == "correct response"
  id = "code1"                                                 if human_readable_id == "code value"
  id = "True-False"                                            if human_readable_id == "item category"
  id = "Number score"                                          if human_readable_id == "reporting method"
  id = "BOY-11-2014"                                           if human_readable_id == "APD.codeValue"
  id = "2014-Eleventh grade Assessment 1"                      if human_readable_id == "assessment 1"
  id = "2014-Eleventh grade Assessment 1#1"                    if human_readable_id == "assessment item 1"
  id = "2014-Eleventh grade Assessment 1.OA-0"                 if human_readable_id == "objective assessment"
  id = "2014-Eleventh grade Assessment 1.OA-0 Sub"             if human_readable_id == "sub objective assessment"
  id = "objectiveAssessment.0.maxRawScore"                     if human_readable_id == "OA.maxRawScore"
  id = "objectiveAssessment.0.nomenclature"                    if human_readable_id == "OA.nomenclature"
  id = "objectiveAssessment.0.identificationCode"              if human_readable_id == "OA.identificationCode"
  id = "objectiveAssessment.0.learningObjectives"              if human_readable_id == "OA.learningObjectives"
  id = "objectiveAssessment.0.percentOfAssessment"             if human_readable_id == "OA.percentOfAssessment"
  id = "assessmentIdentificationCode.0.ID"                     if human_readable_id == "AIC.ID"
  id = "assessmentIdentificationCode.0.identificationSystem"   if human_readable_id == "AIC.identificationSystem"
  
  # Assessment Family Hierarchy
  id = "2014 Standard.2014 Eleventh grade Standard"            if human_readable_id == "assessment family hierarchy"
  # Assessment Period Descriptor
  id = "Beginning of Year 2014-2015 for Eleventh grade"        if human_readable_id == "assessment period descriptor"

  # REALLY REALLY Ugly Transforms
  # Assessment Performance Level
  # The zeroes mean that field is an array, and we are taking the first element in it
  id = "objectiveAssessment.0.assessmentPerformanceLevel.0.performanceLevelDescriptor.0.codeValue" if human_readable_id == "OA.APL.PLD.codeValue"
  id = "objectiveAssessment.0.assessmentPerformanceLevel.0.assessmentReportingMethod" if human_readable_id == "OA.APL.assessmentReportingMethod"
  id = "objectiveAssessment.0.assessmentPerformanceLevel.0.minimumScore" if human_readable_id == "OA.AP.minimumScore"
  id = "objectiveAssessment.0.assessmentPerformanceLevel.0.maximumScore" if human_readable_id == "OA.AP.maximumScore"
  # Sub-Objective Assessments
  id = "objectiveAssessment.0.objectiveAssessments.0.nomenclature" if human_readable_id == "OA.OAS.nomenclature"
  id = "objectiveAssessment.0.objectiveAssessments.0.identificationCode" if human_readable_id == "OA.OAS.identificationCode"
  id = "objectiveAssessment.0.objectiveAssessments.0.percentOfAssessment" if human_readable_id == "OA.OAS.percentOfAssessment"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentPerformanceLevel.0.performanceLevelDescriptor.0.codeValue" if human_readable_id == "OA.OAS.APL.PLD.codeValue"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentPerformanceLevel.0.assessmentReportingMethod" if human_readable_id == "OA.OAS.APL.assessmentReportingMethod"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentPerformanceLevel.0.minimumScore" if human_readable_id == "OA.OAS.APL.minimumScore"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentPerformanceLevel.0.maximumScore" if human_readable_id == "OA.OAS.APL.maximumScore"
  id = "objectiveAssessment.0.objectiveAssessments.0.learningObjectives" if human_readable_id == "OA.OAS.learningObjectives"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.identificationCode" if human_readable_id == "OA.OAS.AI.identificationCode"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.correctResponse" if human_readable_id == "OA.OAS.AI.correctResponse"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.itemCategory" if human_readable_id == "OA.OAS.AI.itemCategory"
  id = "objectiveAssessment.0.objectiveAssessments.0.assessmentItem.0.maxRawScore" if human_readable_id == "OA.OAS.AI.maxRawScore"
      

  # URI transforms  
  id = "assessments"                                if human_readable_id == "ASSESSMENT URI"
  id = "teachers"                                   if human_readable_id == "TEACHER URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id = "sections"                                   if human_readable_id == "SECTION URI"
  id = "schools"                                    if human_readable_id == "SCHOOL URI"
  id = "custom"                                     if human_readable_id == "CUSTOM URI"
  id = "sectionAssessmentAssociations"              if human_readable_id == "SECTION ASSESSMENT ASSOC URI"
  id = "studentSectionAssociations"                 if human_readable_id == "STUDENT SECTION ASSOC URI"
  id = "studentAssessments"                         if human_readable_id == "STUDENT ASSESSMENT ASSOC URI"
  id = "teacherSectionAssociations"                 if human_readable_id == "TEACHER SECTION ASSOC URI"
  id = "learningStandards"                          if human_readable_id == "LEARNING STANDARDS ASSOC URI"
  id = "learningObjectives"                         if human_readable_id == "LEARNING OBJECTIVES ASSOC URI"

  #schools
  id = "studentSchoolAssociations"                  if human_readable_id == "STUDENT SCHOOL ASSOC URI"
  id = "ec2e4218-6483-4e9c-8954-0aecccfd4731"       if human_readable_id == "STUDENT SCHOOL URI"
  
  #sections
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e_id"    if human_readable_id == "'8th Grade English - Sec 6' ID"
  id = "58c9ef19-c172-4798-8e6e-c73e68ffb5a3_id"    if human_readable_id == "'Algebra II' ID"
  id = "baffb6f7-6d30-4341-b29e-0e1cd73ea2bf_id"    if human_readable_id == "'Track and Field - Sec 6s10' ID"
  
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

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I am a valid teacher "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I am a valid IT Administrator "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
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

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################
When /^I make a GET request to URI "(.*?)"$/ do |request|  
  uri = request.gsub("@id", @teacher["sectionId"][0])
  step "I navigate to GET \"/v1/#{uri}\""
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

When /^I follow the links for assessment$/ do
  raise "That entity has no response body" if @teacher["assessment"].nil?
  step "I navigate to GET \"/v1/assessments/#{@teacher['assessment']}\""
  raise "That entity has no links" if @result["links"].nil?
  @links = @result["links"]
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################
Then /^I should get and store the link named "(.*?)"$/ do |mylink|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  found = false
  if !@result.nil? && !@result.empty?
    @result["links"].each do |link|
      if link["rel"] == mylink
        found = true
        teacherHashPush(mylink, link["href"])
      end
    end
  end
  assert(found, "Could not find the link #{mylink} in the URI Response: #{@result}")
end

Then /^I should get and store the "(.*?)" from the response body$/ do |field|
  assert(@result != nil, "Response contains no data")
end

Then /^the response body "(.*?)" should match my teacher "(.*?)"$/ do |resKey, teacherKey|
  assert(@result[resKey] == @teacher[teacherKey], "Expected response not found")
end

Then /^the response field "(.*?)" should be "(.*?)"$/ do |field, value|
  #puts "\n\nDEBUG: @result[#{field}]=#{@result[field]}\n"
  # dig the value for that field out of a potentially
  # dot-delimited response-body structure
  # ex: field=body.name.firstName, @result=[json response body]
  result = fieldExtract(field, @result)
  assert(result == value, "Unexpected response: expected #{value}, found #{result}")  
end

Then /^the response field "(.*?)" should be the number "(.*?)"$/ do |field, value|
  result = fieldExtract(field, @result)
  assert(result == value.to_i, "Unexpected response: expected #{value}, found #{result}")  
end

Then /^I should extract the "(.*?)" id from the "(.*?)" URI$/ do |resource, link|
  value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  teacherHashPush("id", $1)
end

Then /^I should extract the "(.*?)" from the response body to a list$/ do |resource|
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  teacherHashPush(resource, values)
end

Then /^I should extract the "(.*?)" from the response body to a list and save to "(.*?)"$/ do |resource, entity|
  #value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  teacherHashPush(entity, values)
end

Then /^I should extract the learningObjectives from "(.*?)"$/ do |resource|
  values = fieldExtract(resource, @result)
  lo_hash = {resource => values}
  teacherHashPush("learningObjectives", lo_hash)
end

Then /^I store the studentAssessments$/ do
  #puts "\n\nDEBUG: Storing #{@result.length} studentAssessments"
  ids = Array.new

  @result.each do |studentAssessment|
    ids << studentAssessment["id"]
    teacherHashPush(studentAssessment["id"], studentAssessment)
  end
  # Push the list of studentAsessment hash keys to a list in @teacher
  teacherHashPush("studentAssessments", ids)
end

Then /^I make sure "(.*?)" match "(.*?)"$/ do |elem1, elem2|
  # section for array comparison
  if elem1.is_a?(Array)
    for i in 0..elem.length
      assert(@teacher[elem1[i]] == @teacher[elem2[i]], "When comparing references, #{elem1} does not equal #{elem2} but should.")
    end
  end
  # section for simple string field comparison
end


Then /^I should extract the assessment reference from studentAssessment$/ do
  assessment = @result["assessmentId"]
  teacherHashPush("assessment", assessment)
end

Then /^I should extract the student reference from studentAssessment$/ do
  student = @result["studentId"]
  teacherHashPush("student", student)
end

Then /^I extract all the "(.*?)" links$/ do |entity|
  assert(entity == @result["entityType"], "Type mismatch, your current response is not from the #{entity} entity")
  @teacher["links"] = Hash.new if @teacher["links"].nil?
  @teacher["links"][entity] = @result["links"]
end


  #I should validate the "objectiveAssessment.0.learningObjectives" from "assessment" links map to learningObjectives
Then /^I should validate the "(.*?)" from "(.*?)" links map to learningObjectives$/ do |loKey, entity|
  learningObjectives = @teacher["learningObjectives"][loKey]
  @links.each do |link|
    next unless link["rel"].match(/learningObjectives/)
    step "I follow the HATEOS link named \"#{link["href"]}\""
    # Verify each id returned in the array of learning objectives
    @result.each do |lo|
      step "I navigate to GET \"/v1/learningObjectives/#{lo["id"]}\""
      puts "\nSearching for learning objective"
      raise "NOT FOUND!" unless lo["entityType"] == "learningObjective"
      puts "found"
      # Follow the link to verify the child learning objective
      # Not implemented because Odin does not generate these yet
    end
  end
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

def fieldExtract(field, body)
  #split the field string into URI segments
  part = field.split(".")
  #parse the response field value based on how deep that field is embedded
  # Grab the first array element if an array is encountered
  for i in 0..(part.length-1)
    part[i] = part[i].to_i if is_num?(part[i])
  end

  result = body
  (1..(part.length)).each {|x| result = result[part[x-1]]}
  return result
end

# This proc takes a hash of arrays/hashes, and if
# it encounters an array of hashes, takes the first
# element in that array
def get_first_array_elements(body, parts)
  part_count = 0
  parts.each do |part|
    part_count += 1
    body[full_part] = body[full_part][0] if body[full_part].is_a?(Array)
  end
  return body
end

# Build the teacher hash
def teacherHashPush(key, value)
  @teacher = Hash.new unless defined? @teacher
  @teacher[key] = value
end

def getTeacherSchools()

end

def pushStudentAssessment(studentAssessment)
  @teacher[studentAssessment["id"]] = studentAssessment
end


# Build the section array
def sectionArray(value)
  @sections = Array.new unless defined? @sections  
  if value.is_a?(Array)
    value.each{|section| @sections << section}
  else
    @sections << value
  end
end

# Build the section array
def studentArray(value)
  @students = Array.new unless defined? @students
  # 
  if value.is_a?(Array)
    value.each{|student| @students << student}
  else
    @students << value
  end
end



