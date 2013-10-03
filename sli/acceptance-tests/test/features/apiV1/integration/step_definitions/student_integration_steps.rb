=begin

Copyright 2013-2014 inBloom, Inc. and its affiliates.

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

require 'date'
require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'
require_relative '../../../bulk_extract/features/step_definitions/bulk_extract.rb'

Transform /^<(.*?)>$/ do |human_readable_id|
  # teacher hash transforms
  id = @entityMap["teacher"]["id"]                                            if human_readable_id == "teacher id"
  id = @entityMap["teacher"]["getSchools"]                                    if human_readable_id == "getSchools"
  id = @entityMap["teacher"]["getSections"]                                   if human_readable_id == "getSections"
  id = @entityMap["teacher"]["getEducationOrganizations"]                     if human_readable_id == "getEducationOrganizations"
  id = @entityMap["teacher"]["getTeacherSchoolAssociations"]                  if human_readable_id == "getTeacherSchoolAssociations"
  id = @entityMap["teacher"]["getTeacherSectionAssociations"]                 if human_readable_id == "getTeacherSectionAssociations"
  id = @entityMap["teacher"]["getStaffEducationOrgAssignmentAssociations"]    if human_readable_id == "getStaffEducationOrgAssignmentAssociations"
  id = @entityMap["teacher"]["sectionId"]                                     if human_readable_id == "teacher section list"
  id = @entityMap["teacher"]["sectionId"][0]                                  if human_readable_id == "teacher section"
  id = @entityMap["teacher"]["studentAssessments"]                            if human_readable_id == "student assessment list"
  id = @entityMap["teacher"]["studentAssessments"][0]                         if human_readable_id == "student assessment"
  id = @entityMap["teacher"]["assessment"]                                    if human_readable_id == "assessment"
  id = @entityMap["teacher"]["student"]                                       if human_readable_id == "student"

  # student hash transforms
  id = @entityMap["student"]["id"]                                            if human_readable_id == "my student id"
  id = @entityMap["student"]["teacher"]                                       if human_readable_id == "student teacher id"
  #id = @entityMap["getSchools"]                                    if human_readable_id == "getSchools"
  #id = @entityMap["getSections"]                                   if human_readable_id == "getSections"
  #id = @entityMap["getEducationOrganizations"]                     if human_readable_id == "getEducationOrganizations"
  #id = @entityMap["getTeacherSchoolAssociations"]                  if human_readable_id == "getTeacherSchoolAssociations"
  #id = @entityMap["getTeacherSectionAssociations"]                 if human_readable_id == "getTeacherSectionAssociations"
  #id = @entityMap["getStaffEducationOrgAssignmentAssociations"]    if human_readable_id == "getStaffEducationOrgAssignmentAssociations"
  #id = @entityMap["sectionId"]                                     if human_readable_id == "teacher section list"
  #id = @entityMap["sectionId"][0]                                  if human_readable_id == "teacher section"
  #id = @entityMap["studentAssessments"]                            if human_readable_id == "student assessment list"
  #id = @entityMap["studentAssessments"][0]                         if human_readable_id == "student assessment"
  #id = @entityMap["assessment"]                                    if human_readable_id == "assessment"
  #id = @entityMap["student"]                                       if human_readable_id == "student"

  # Ugly response body field transforms
  # The dot-delimited structure is used for nested hashes like: body.name.lastSurname
  # The zeroes mean that field is an array, and we are taking the first element in it
  # These dot-delmited strings are passed to fieldExtract, which recursively
  # walks the response body and ultimately returns the field we desire
  #
  # Student Domain
  id = "studentUniqueStateId"                                  if human_readable_id == "studentUniqueStateId"
  #
  # Assessment Domain
  id = "false"                                                 if human_readable_id == "correct response"
  id = "code1"                                                 if human_readable_id == "code value"
  id = "True-False"                                            if human_readable_id == "item category"
  id = "Number score"                                          if human_readable_id == "reporting method"
  id = "BOY-12-2013"                                           if human_readable_id == "APD.codeValue"
  id = "2013-Twelfth grade Assessment 1"                       if human_readable_id == "assessment 1"
  id = "2013-Twelfth grade Assessment 1#2"                     if human_readable_id == "assessment item 1"
  id = "2013-Twelfth grade Assessment 1.OA-1"                  if human_readable_id == "objective assessment"
  id = "2013-Twelfth grade Assessment 1.OA-1 Sub"              if human_readable_id == "sub objective assessment"
  id = "objectiveAssessment.0.maxRawScore"                     if human_readable_id == "OA.maxRawScore"
  id = "objectiveAssessment.0.nomenclature"                    if human_readable_id == "OA.nomenclature"
  id = "objectiveAssessment.0.identificationCode"              if human_readable_id == "OA.identificationCode"
  id = "objectiveAssessment.0.learningObjectives"              if human_readable_id == "OA.learningObjectives"
  id = "objectiveAssessment.0.percentOfAssessment"             if human_readable_id == "OA.percentOfAssessment"
  id = "assessmentIdentificationCode.0.ID"                     if human_readable_id == "AIC.ID"
  id = "assessmentIdentificationCode.0.identificationSystem"   if human_readable_id == "AIC.identificationSystem"
  
  # Assessment Family Hierarchy
  id = "2013 Standard.2013 Twelfth grade Standard"             if human_readable_id == "assessment family hierarchy"
  # Assessment Period Descriptor
  id = "Beginning of Year 2013-2014 for Twelfth grade"         if human_readable_id == "assessment period descriptor"

  # Search endpoints
  id = "assessmentIdentificationCode.0.ID"                     if human_readable_id == "search.assessment.ID"
  id = "assessmentIdentificationCode.0.identificationSystem"   if human_readable_id == "search.assessment.ID.system"

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
  # StudentObjectiveAssessments
  id = "studentObjectiveAssessments.0.scoreResults.0.result" if human_readable_id == "SOA.scoreResults.result"
  id = "studentObjectiveAssessments.0.objectiveAssessment.identificationCode" if human_readable_id == "SOA.OA.identificationCode"
  # StudentAssessmentItem    
  id = "studentAssessmentItems.0.rawScoreResult" if human_readable_id == "SAI.rawScoreResult"
  id = "studentAssessmentItems.0.assessmentItemResult" if human_readable_id == "SAI.assessmentItemResult"
  id = "studentAssessmentItems.0.assessmentItem.identificationCode" if human_readable_id == "SAI.AI.identificationCode"

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

Given /^I am accessing data about myself, "(.*?)"$/ do |arg1|
  # No code needed, this is an explanation step
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^I am authenticated on "(.*?)"$/ do |arg1|
  idpRealmLogin(@user, @passwd, arg1)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^I am using api version "(.*?)"$/ do |version|
  @api_version = version
end

Given /^I set the userSession clientId to nil$/ do
  conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  sli = conn.db("sli")
  coll = sli["userSession"]
  entity = coll.find_one({"body.appSession.token" => @sessionId})
  assert(entity, "cant find userSession with token #{@sessionId}")
  body = entity["body"]
  puts body
  appSessionArray = body["appSession"]
  puts appSessionArray
  appSessionArray.each do |hash|
     if hash.has_key?("clientId")
        hash["clientId"] = nil
     end
  end
  puts entity
  coll.save(entity)
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################
When /^I make a GET request to URI "(.*?)"$/ do |request|  
  uri = request.gsub("@id", @entityMap["teacher"]["sectionId"][0])
  #puts uri
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
  step "I navigate to GET \"#{@filterSortPaginationHref}\""
  assert(@result != nil, "Response contains no data")
end

When /^I follow the links for assessment$/ do
  raise "That entity has no response body" if @entityMap["teacher"]["assessment"].nil?
  step "I navigate to GET \"/v1/assessments/#{@entityMap["teacher"]['assessment']}\""
  raise "That entity has no links" if @result["links"].nil?
  @links = @result["links"]
end

When /^I verify the following response body fields in "(.*?)":$/ do |uri, table|
  step "I navigate to GET \"/#{@api_version}#{uri}\""
  table.hashes.map do |row|
    puts "Checking #{row['field']} is set to #{row['value']}" if $SLI_DEBUG
    step "the response field \"#{row['field']}\" should be \"#{row['value']}\""
  end
end

When /^I verify the following response body fields exist in "(.*?)":$/ do |uri, table|
  step "I navigate to GET \"/#{@api_version}#{uri}\""
  puts "api result is #{@result}" if $SLI_DEBUG
  table.hashes.map do |row|
    field = row['field']
    puts "Checking #{field} exists" if $SLI_DEBUG
    result = fieldExtract(field, @result)
    assert((not result.nil?), "No value set for field #{field}")
  end
end

When /^I verify the following response body fields do not exist in the response:$/ do |table|
  puts "api result is #{@result}" if $SLI_DEBUG
  table.hashes.map do |row|
    field = row['field']
    puts "Checking #{field} exists" if $SLI_DEBUG
    result = fieldExtract(field, @result)
    assert(result.nil?, "Invalid Access: User has access to field #{field} but should not")
  end
end

When /^I validate I have access to entities via the API access pattern "(.*?)":$/ do |uri, table| 
  table.hashes.map do |row|
    print "Verifying I get a 200 response from #{row["entity"]}/#{row["id"]} .. "
    call_uri = uri.gsub("Entity", row["entity"]).gsub("Id", row["id"])
    puts "Calling GET to #{call_uri}" if $SLI_DEBUG
    step "I navigate to GET \"#{call_uri}\""
    step "I should receive a return code of 200"
    print "OK\n"
  end
end

When /^I validate the current allowed association entities via API "(.*?)":$/ do |uri, table|
  # Do all current existing validation
  step "I validate the allowed association entities via API \"#{uri}\":", table

  # After its done, additionally check the response that all returned associations are current
  assert(@result.is_a?(Array), "Response of a Listing endpoint was not a list of entities")
  @result.each do |assoc|
    if assoc.has_key? "endDate"
      date = assoc["endDate"]
      assert(is_current?(date), "Date #{date} was not current")
    elsif assoc.has_key? "exitWithdrawDate"
      date = assoc["exitWithdrawDate"]
      assert(is_current?(date), "Date #{date} was not current")
    else
      # Any association with no end date is deemed to be current
    end
  end

end

When /^I validate there are "(.*?)" allowed association entities via API "(.*?)", some of them are:$/ do |count, uri, table|
  print "Verifying I get a 200 response from #{uri} .. "
  puts "Calling GET to #{uri}" if $SLI_DEBUG
  step "I navigate to GET \"#{uri}\""
  step "I should receive a return code of 200"
  print "OK\n"
    
  expected_ids = Set.new(table.hashes.collect{|row| row["id"]})
  assert(contains_all(@res, expected_ids, count.to_i), "Did not find one or more expected entities")
  print "OK\n"
end

When /^I validate the allowed association entities via API "(.*?)":$/ do |uri, table|
  print "Verifying I get a 200 response from #{uri} .. "
  puts "Calling GET to #{uri}" if $SLI_DEBUG
  step "I navigate to GET \"#{uri}\""
  step "I should receive a return code of 200"
  print "OK\n"

  print "Verifying the number of entities returned matches the expected count .. "

  expected_ids = Set.new(table.hashes.collect{|row| row["id"]})
  assert(contains_all(@res, expected_ids), "Did not find one or more expected entities")
  print "OK\n"
end

When /^I validate that I am denied access to restricted endpoints via API:$/ do |table|
  startRed = "\e[31m"
  colorReset = "\e[0m"
  success = true

  table.hashes.map do |row|
    step "I navigate to GET \"#{row['uri']}\""
    if @res.code != row['rc'].to_i
      print "#{startRed}Expected a #{row['rc']} response from #{row['uri']}, but got #{@res.code}#{colorReset}\n"
      success = false
    else
      print "Expected a #{row['rc']} response from #{row['uri']}, and got #{@res.code}\n"
    end
  end
  assert(success, "Received an unexpected http return code..")
end

When /^I validate that I am denied access to certain endpoints via API:$/ do |table|
   step "I validate that I am denied access to restricted endpoints via API:", table
end

When /^I validate the "(.*?)" HATEOS link for "(.*?)"$/ do |entity, key|
  uri = @entityMap[entity.to_s][key.to_s]
  step "I follow the HATEOS link named \"#{uri}\""
end

When /^I add an expired studentCohortAssociation to "(.*?)"$/ do |uniqueId|

end
###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################
Then /^I should get and store the "(.*?)" link named "(.*?)"$/ do |context, mylink|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  if !@result.nil? && !@result.empty?
    found = false
    @result["links"].each do |link|
      if link["rel"] == mylink
        found = true
        entityHashPush(context, mylink, link["href"])
        puts "DEBUG: @entityMap[#{context}][#{mylink}] is #{@entityMap[context][mylink]}"
      end
    end
  end
  assert(found, "Could not find the link #{mylink} in the URI Response: #{@result}")
end

Then /^I should validate all the HATEOS links$/ do
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  puts "DEBUG: Token is #{@sessionId}"
  if !@result.empty?
    @result["links"].each do |link|
      link["href"].match("/api/rest/v1\.[0-9]/(.*?)$")
      uri = $1
      assert(uri != nil, "Did not find a link for #{link["rel"]} => #{link["href"]}" )
      step "I navigate to GET \"/v1/#{uri}\""
      if link["rel"] == "custom"
        assert(@res.code == 404, "Return code was not expected: #{@res.code} but expected 404")
      else 
        puts "DEBUG: return code for #{link["rel"]} is #{@res.code}\nURL is #{link["href"]}"
        #step "I should receive a return code of 200"
      end
      #puts "DEBUG: 200 OK"
    end
  else
    assert(false, "Empty response from API call, expected HATEOS links in response body.")
  end
end

Then /^I should GET a return code of "(.*?)" for all the "(.*?)" links$/ do |rc, userType|
  # Strip out the first part of each URI to pass to GET method
  @entityMap[userType].each
  uris = something

  step "I navigate to GET '/v1/#{uri}'"
  step "I should receive a return code of #{rc}"
end

Then /^I should get and store the "(.*?)" from the response body$/ do |field|
  assert(@result != nil, "Response contains no data")
end

Then /^the response body "(.*?)" should match my "(.*?)" "(.*?)"$/ do |resKey, entityType, entityKey|
  entity = @entityMap["teacher"] if entityType == "teacher"
  assert(@result[resKey] == @entityMap[entityType][entityKey], "Expected response not found")
end

Then /^I sort the studentAssessmentItems$/ do
    studentAssessmentItems = @result["studentAssessmentItems"]
    studentAssessmentItems.sort! {|a, b| a["assessmentItem"]["identificationCode"]<=>b["assessmentItem"]["identificationCode"]}
    @result["studentAssessmentItems"] = studentAssessmentItems
end

Then /^the response field "(.*?)" should be "(.*?)"$/ do |field, value|
  startRed = "\e[31m"
  colorReset = "\e[0m"
  #puts "\n\nDEBUG: @result[#{field}]=#{@result[field]}\n"
  # dig the value for that field out of a potentially
  # dot-delimited response-body structure
  # ex: field=body.name.firstName, @result=[json response body]
  puts @result if $SLI_DEBUG
  result = fieldExtract(field, @result) 
  if (result.to_s != value)
    puts "#{startRed}Result for #{field} was #{result.to_s}#{colorReset}"
    assert(false, "Unexpected result for field #{field}, should be #{value} was #{result.to_s}")
  else
    puts "Result for #{field} was #{result.to_s}"
  end
end

Then /^the offset response field "([^"]*)" should be "([^"]*)"$/ do |field, value|
  #puts "\n\nDEBUG: response body is #{@result[0]}"
  result = fieldExtract(field, @result[0]) 
  assert(result == value, "Unexpected response: expected #{value}, found #{result}")  
end

Then /^the response field "(.*?)" should be the number "(.*?)"$/ do |field, value|
  result = fieldExtract(field, @result)
  assert(result == value.to_i, "Unexpected response: expected #{value}, found #{result}")  
end

Then /^I should extract the "(.*?)" id from the "(.*?)" URI$/ do |entity, link|
  puts "DEBUG: entity link is #{@entityMap[entity][link]}"
  value = @entityMap[entity][link].match(/#{entity}s\/(.*?_id)/)
  entityHashPush(entity, "id", $1)
end

Then /^I should extract the "(.*?)" from the response body to a list$/ do |resource|
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  values.sort! 
  #puts values
  entityHashPush("teacher", resource, values)
end

Then /^I should extract the "(.*?)" from the response body to a list and save to "(.*?)"$/ do |resource, entity|
  #value = @entityMap["teacher"][link].match(/#{resource}\/(.*?_id)/)
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  values.sort!
  #puts values
  entityHashPush("teacher", entity, values)
end

Then /^I should extract the learningObjectives from "(.*?)"$/ do |resource|
  values = fieldExtract(resource, @result)
  lo_hash = {resource => values}
  entityHashPush("teacher", "learningObjectives", lo_hash)
end

Then /^I store the studentAssessments$/ do
  #puts "\n\nDEBUG: Storing #{@result.length} studentAssessments"
  ids = Array.new
  @result.each do |studentAssessment|
    ids << studentAssessment["id"]
    entityHashPush("teacher", studentAssessment["id"], studentAssessment)
  end
  # Push the list of studentAsessment hash keys to a list in @entityMap["teacher"]
  ids.sort!
  puts "DEBUG: ids are #{ids.inspect}"
  entityHashPush("teacher", "studentAssessments", ids)
end

Then /^I make sure "(.*?)" match "(.*?)"$/ do |elem1, elem2|
  # section for array comparison
  if elem1.is_a?(Array)
    for i in 0..elem.length
      assert(@entityMap["teacher"][elem1[i]] == @entityMap["teacher"][elem2[i]], "When comparing references, #{elem1} does not equal #{elem2} but should.")
    end
  end
  # section for simple string field comparison
end


Then /^I should extract the assessment reference from studentAssessment$/ do
  assessment = @result["assessmentId"]
  #puts "assessment id is: #{assessment}"
  entityHashPush("teacher", "assessment", assessment)
end

Then /^I should extract the student reference from studentAssessment$/ do
  student = @result["studentId"]
  entityHashPush("teacher", "student", student)
end

Then /^I extract all the "(.*?)" links$/ do |entity|
  assert(entity == @result["entityType"], "Type mismatch, your current response is not from the #{entity} entity")
  @entityMap["teacher"]["links"] = Hash.new if @entityMap["teacher"]["links"].nil?
  @entityMap["teacher"]["links"][entity] = @result["links"]
end


  #I should validate the "objectiveAssessment.0.learningObjectives" from "assessment" links map to learningObjectives
Then /^I should validate the "(.*?)" from "(.*?)" links map to learningObjectives$/ do |loKey, entity|
  learningObjectives = @entityMap["teacher"]["learningObjectives"][loKey]
  @links.each do |link|
    next unless link["rel"].match(/learningObjectives/)
    step "I follow the HATEOS link named \"#{link["href"]}\""
    # Verify each id returned in the array of learning objectives
    @result.each do |lo|
      step "I navigate to GET \"/v1/learningObjectives/#{lo["id"]}\""
      #puts "\nSearching for learning objective"
      raise "NOT FOUND!" unless lo["entityType"] == "learningObjective"
      #puts "found"
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
  # Split the field string into URI segments
  part = field.split(".")
  # Parse the response field value based on how deep that field is embedded
  # Grab the first array element if an array is encountered
  for i in 0..(part.length-1)
    part[i] = part[i].to_i if is_num?(part[i])
  end

  # Preserve the original response body
  result = body
  # Recursively loop through a response body to dig our fields
  (1..(part.length)).each { |x| result = result[part[x-1]] }
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

# Build the entity hash map
def entityHashPush(entity, key, value)
  @entityMap = {} unless @entityMap != nil
  @entityMap[entity] = {} unless @entityMap[entity] != nil  
  @entityMap[entity][key] = value
end

def getTeacherSchools()

end

def pushStudentAssessment(studentAssessment)
  @entityMap["teacher"][studentAssessment["id"]] = studentAssessment
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

# Build the student array
def studentArray(value)
  @students = Array.new unless defined? @students
  # 
  if value.is_a?(Array)
    value.each{|student| @students << student}
  else
    @students << value
  end
end

#This function deterimes if a date string passed in (in format yyyy-mm-dd) is current or not
def is_current?(end_date_string)
  now_string = Date.today.strftime("%F")
  return  now_string <= end_date_string
end

def contains_all(api_result, should_exists, opt_count=0) 
  startRed = "\e[31m"
  colorReset = "\e[0m"

  @result = JSON.parse(api_result.body)
  response_ids = Set.new(@result.collect{|res| res["id"]})
  response_ids.each do |rid|
    puts "| " + rid.to_s + " |"
  end
  missing_ids = should_exists.subtract(response_ids.intersection(should_exists))
  missing_ids.each { |missed|
      print "#{startRed}Looking for Entity: #{missed}#{colorReset}\n"
  }

  unless missing_ids.empty?
    puts "Response ids :"
    response_ids.each do |rid|
      puts "| " + rid.to_s + " |"
    end
  end

  missing_ids.empty? && response_ids.size >= opt_count 
end

Then(/^I PATCH entities and check return code$/) do |table|
  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  @format = 'application/json'
  table.hashes.map do |params|
    uri = "#{params['Endpoint']}/#{params['Id']}"
    data = {}
    data[params['Field']]='onward'
    restHttpPatch("/#{@api_version}/#{uri}", data)
    #Verify the return code
    assert(@res != nil, "Response from rest-client PATCH is nil")
    if (@res == nil) || (@res.code != 403)
      success = false
      puts "#{startRed}Return code for URI: #{uri} was #{@res.code}#{colorReset}"
    else
      puts "Return code for URI: #{uri} was #{@res.code}"
    end
  end
  assert(success, "Response code was unexpected, see logs above.")
end

Given /^I get the rights for the "(.*?)" role in realm "(.*?)"$/ do |role, realm|
  @conn             = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  @db               = @conn['02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a']
  @roles_collection = @db.collection('customRole')
  puts "DEBUG: roles_collection is: #{@roles_collection}"
 #45b02cb0-1bad-4606-a936-094331bd47fe
  count = 0
  @roles_collection.find({'body.realmId' => realm}).each do |row|
    puts "DEBUG: row is: #{row.inspect}"
    row['body']['roles'].each do |group|
      puts "DEBUG: group is: #{group.inspect}"
      if group['groupTitle'] == role
        count += 1
        @previous_rights = group['rights']
      end
    end
  end
  assert(count == 1, "Did not find expected number of role documents. Expected 1, found #{count}.")
end

Given /^I change the "(.*?)" role for realm "(.*?)" to permit the following rights:$/ do |role, realm, table|
  id = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"
  rights = []
  table.hashes.map do |row|
    rights << row["right"]
  end
  ret = @roles_collection.update({'body.realmId' => realm, 'body.roles.groupTitle' => role}, {'$set' => {'body.roles.$.rights' => rights}}, {:multi=>true})
  assert(ret['ok'] == 1.0 && ret['err'] == nil && ret['n'] == 1, "Mongo returned an error, nothing unusual or interesting to see here.\n#{ret}")
end

Then /^I change the "(.*?)" role for realm "(.*?)" back to its original rights$/ do |role, realm|
  ret = @roles_collection.update({'body.realmId' => realm, 'body.roles.groupTitle' => role}, {'$set' => {'body.roles.$.rights' => @previous_rights}}, {:multi=>true})
  assert(ret['ok'] == 1.0 && ret['err'] == nil && ret['n'] == 1, "Mongo returned an error, nothing unusual or interesting to see here.\n#{ret}")
end

After('@student_expired_access, @parent_expired_access') do |scenario|
  step "I log in to realm \"Illinois Daybreak School District 4529\" using simple-idp as \"IT Administrator\" \"jstevenson\" with password \"jstevenson1234\""
  #step "I am logged in using \"jstevenson\" \"jstevenson1234\" to realm \"IL\""
  step "format \"application/json\""
  step "I am using api version \"v1\""
  step "I DELETE and validate the following entities:", table(%{
    | entity  | id                                          | returnCode  |
    | staff   | 2ff51e81ecbd9c4160a19be629d0ccb4cb529796_id | 204         |
    | staff   | bfddb715a20bb2996b8769abfc1813d029bfdf29_id | 204         |
    | student | b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id | 204         |
    | studentProgramAssociation | 067198fd6da91e1aa8d67e28e850f224d6851713_id001b57375dab7d013d6cca625fa78351862d6653_id  | 204 |
  })
end

After('@clean_up_student_posts') do |scenario|
  step "I log in to realm \"Illinois Daybreak School District 4529\" using simple-idp as \"IT Administrator\" \"jstevenson\" with password \"jstevenson1234\""
  #step "I am logged in using \"jstevenson\" \"jstevenson1234\" to realm \"IL\""
  step "format \"application/json\""
  step "I am using api version \"v1\""
  restHttpDelete("/v1/studentAssessments/f9643b7abba04ae01586723abed0e38c63e4f975_id")
  print "studentAssessments delete result: #{@res.code}\n"
  restHttpDelete("/v1/studentGradebookEntries/7f714f03238d978398fbd4f8abbf9acb3e5775fe_id")
  print "studentGradebookEntries delete result: #{@res.code}\n"
  restHttpDelete("/v1/grades/f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id")
  print "grades delete result: #{@res.code}\n"
  #step "I DELETE and validate the following entities:", table(%{
  #  | entity                | id                                          | returnCode  |
  #  | studentAssessment     | f9643b7abba04ae01586723abed0e38c63e4f975_id | 204         |
  #  | studentGradebookEntry | 7f714f03238d978398fbd4f8abbf9acb3e5775fe_id | 204         |
  #  | grade                 | f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id | 204 |
  #})
end

After('@clean_up_parent_posts') do |scenario|
  step "I log in to realm \"Illinois Daybreak School District 4529\" using simple-idp as \"IT Administrator\" \"rrogers\" with password \"rrogers1234\""
  step "format \"application/json\""
  step "I am using api version \"v1\""
  restHttpDelete("/v1/parents/1fe86fe9c45680234f1caa3b494a1c4b42838954_id")
  print "parents delete result: #{@res.code}\n"
  restHttpDelete("/v1/studentSchoolAssociations/23125624f5f1dcfcf7e27eae8e7b44d91945ad2e_id")
  print "studentSchoolAssociations delete result: #{@res.code}\n"
  restHttpDelete("/v1/studentSectionAssociations/cee6195d1c5e2605bea2f3c34d264442c78638d2_idf073a2639d4d95f8fea3b0cfb96e17580416c819_id")
  print "studentSectionAssociations delete result: #{@res.code}\n"
  #restHttpDelete("/v1/studentParentAssociations/fdd8ee3ee44133f489e47d2cae109e886b041382_idec053d2e0752799cb0217578d003a1fe8f06b9a0_id")
  #print "studentParentAssociations delete result: #{@res.code}\n"
end
