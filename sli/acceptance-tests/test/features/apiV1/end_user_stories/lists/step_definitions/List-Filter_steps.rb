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
require 'uri'
require 'json'
require 'builder'
require 'rexml/document'

include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../../security/step_definitions/securityevent_util_steps.rb'


# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = template
  id = "67ed9078-431a-465e-adf7-c720d08ef512" if template == "<'Linda Kim' ID>"
  id = "0636ffd6-ad7d-4401-8de9-40538cf696c8_id" if template == "<'Preston Muchow' ID>"
  id = "f7094bd8-46fc-4204-9fa2-a383fb71bdf6_id" if template == "<'Mayme Borc' ID>"
  id = "6bfbdd9a-441a-490a-9f83-716785b61829_id" if template == "<'Malcolm Costillo' ID>"
  id = "891faebe-bc84-4e0c-b7f3-195637cd981e_id" if template == "<'Tomasa Cleaveland' ID>"
  id = "ffee781b-22b1-4015-81ff-3289ceb2c113_id" if template == "<'Merry Mccanse' ID>"
  id = "5dd72fa0-98fe-4017-ab32-0bd33dc03a81_id" if template == "<'Samantha Scorzelli' ID>"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id" if template == "<'Matt Sollars' ID>"
  id = "32932b97-d466-4d3c-9ebe-d58af010a87c_id" if template == "<'Dominic Brisendine' ID>"
  id = "6f60028a-f57a-4c3d-895f-e34a63abc175_id" if template == "<'Lashawn Taite' ID>"
  id = "4f81fd4c-c7c5-403e-af91-6a2a91f3ad04_id" if template == "<'Oralia Merryweather' ID>"
  id = "766519bf-31f2-4140-97ec-295297bc045e_id" if template == "<'Dominic Bavinon' ID>"
  id = "e13b1a7a-81d6-474c-b751-a6af054dbd6a_id" if template == "<'Rudy Bedoya' ID>"
  id = "a17bd536-7502-4a4d-9d1f-538792b86795_id" if template == "<'Verda Herriman' ID>"
  id = "7062c584-e229-4763-bf40-aec36bf112e7_id" if template == "<'Alton Maultsby' ID>"
  id = "b1312b46-0a6b-4c6d-b73a-8cd7981e260e_id" if template == "<'Felipe Cianciolo' ID>"
  id = "e0c2e40a-a472-4e78-9736-5ed0cbc16018_id" if template == "<'Lyn Consla' ID>"
  id = "7ac04245-d931-447c-b8b2-aeef63fa3a4e_id" if template == "<'Felipe Wierzbicki' ID>"
  id = "5714e819-0323-4281-b8d6-83604d3e95e8_id" if template == "<'Gerardo Giaquinto' ID>"
  id = "2ec521f4-38e9-4982-8300-8df4eed2fc52_id" if template == "<'Holloran Franz' ID>"
  id = "f11f341c-709b-4c8e-9b08-da9ff89ec0a9_id" if template == "<'Oralia Simmer' ID>"
  id = "e62933f0-4226-4895-8fe3-aaffd5556032_id" if template == "<'Lettie Hose' ID>"
  id = "903ea314-8212-4e9f-92b7-a18a25059804_id" if template == "<'Gerardo Saltazor' ID>"
  id = "0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba_id" if template == "<'Lashawn Aldama' ID>"
  id = "37edd9ae-3ac2-4bba-a8d8-be57461cd6de_id" if template == "<'Alton Ausiello' ID>"
  id = "1d3e77f6-5f07-47c2-8086-b5aa6f4d703e_id" if template == "<'Marco Daughenbaugh' ID>"
  id = "dbecaa89-29e6-41e1-8099-f80e29baf48e_id" if template == "<'Karrie Rudesill' ID>"
  id = "414106a9-6156-47e3-a477-4bd4dda7e21a_id" if template == "<'Damon Iskra' ID>"
  id = "e2d8ba15-953c-4cf7-a593-dbb419014901_id" if template == "<'Gerardo Rounsaville' ID>"
  id = "2108c0c84ca6998eb157e1efd4d894746e1fdf8b_id" if template == "<'SAT READING' ID>"
  id = "5b0253d3-eb53-4c81-9e65-2f1f8347facc_id" if template == "<'French period 5' ID>"
  id = "706ee3be-0dae-4e98-9525-f564e05aa388_id" if template == "<'8th Grade English - Sec 5' ID>"
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e_id" if template == "<'8th Grade English - Sec 6' ID>"
  id = "7847b027-687d-46f0-bc1a-36d3c16956aa_id" if template == "<'Science 7A - Sec 5f10' ID>"
  id = "87fb8da5-e1aa-a6d9-efc7-b0eb091cd695_id" if template == "<'Matt Sollars Assessment' ID>"
  id
end

Transform /^([^"]*)\/(<.+>)([^"]*)$/ do |pre_url, template, post_url|
  pre_url + "/" + Transform(template) + Transform(post_url)
end

When /^I navigate to "([^"]*)" with URI "([^"]*)"$/ do |rel,href|
  restHttpGet(href)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI "(\/v1\/teachers\/<[^>]*>)\/teacherSectionAssociations\/sections" and filter by uniqueSectionCode is "([^"]*)"$/ do |rel, href, sectionCode|
  queryParams = "uniqueSectionCode="+sectionCode
  uri = href+"/teacherSectionAssociations/sections?"+URI.escape(queryParams,Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI "(\/v1\/students\/<[^>]*>)\/studentAssessments" and filter by administrationDate is between "([^"]*)" and "([^"]*)"$/ do |rel, href, date1, date2|
  queryParams = "administrationDate>"+date1+"&administrationDate<"+date2
  uri = href+"/studentAssessments?"+URI.escape(queryParams,Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI "(\/v1\/students\/<[^>]*>)\/studentAssessments\/assessments" and filter by assessmentTitle is "([^"]*)"$/ do |rel, href,assessmentTitle|
  queryParams = "assessmentTitle="+assessmentTitle
  uri = href+"/studentAssessments/assessments?"+URI.escape(queryParams,Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI "([^"]*)" to filter$/ do |rel,href|
  @uriWithQuery = href+"?"
end

When /^filter by administrationDate is between "([^"]*)" and "([^"]*)"$/ do |date1,date2|
  queryParams = "administrationDate>"+date1+"&administrationDate<"+date2
  @uriWithQuery = @uriWithQuery + URI.escape(queryParams)
end

When /^filter by studentId is (<[^>]*>)$/ do |studentId|
  queryParams = "&studentId="+studentId
  @uriWithQuery = @uriWithQuery + URI.escape(queryParams)
  restHttpGet(@uriWithQuery)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    uri = "/v1/studentAssessments/"+dataH[0]["id"]
    restHttpGet(uri)
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should have a list of (\d+) "([^"]*)" entities$/ do |size, entityType|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")

  @ids = Array.new
  @result.each do |entity|
    assert(entity["entityType"] == entityType)
    @ids.push(entity["id"])
  end

  assert(@ids.size.to_s == size, "Got " + @ids.size.to_s + " entities, expected " + size.to_s + " in response.")
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


Then /^I should receive a collection of (\d+) ([\w-]+) links$/ do |arg1, arg2|
  @collectionType = "/"+arg2+"s/"
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? @collectionType
       counter=counter+1
       @ids.push(link["id"])
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end


Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  found = false
  @ids.each do |id|
    if findLink(id,@collectionType, rel,href)
      found = true
      break
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

Then /^I should find Student with (<[^>]*>)$/ do |studentId|
  found =false
  @ids.each do |id|
    if id==studentId
      found = true
    end
  end
  assert(found, "didnt find student with ID #{studentId}")
end


Then /^I should find section with sectionName is "([^"]*)" and classPeriod is "([^"]*)"$/ do |sectionName, classPeriod|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
        if dataH["sectionName"]==sectionName and dataH["classPeriod"]==classPeriod
        found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with sectionName #{sectionName} and classPeriod #{classPeriod}")
end

Then /^I should find section with sectionName is "([^"]*)" and classPeriod is "([^"]*)"  with (<[^>]*>)$/ do |sectionName, classPeriod,id|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
        if dataH["sectionName"]==sectionName and dataH["classPeriod"]==classPeriod and dataH["id"]==id
        found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with sectionName #{sectionName} and classPeriod #{classPeriod} with #{id}")
end

Then /^I should find a ScoreResult is (\d+)$/ do |scoreResult|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["scoreResults"].each do |result|
      if Integer(result["result"])==scoreResult
        found = true
      end
    end
    assert(found, "did not find scoreResult #{scoreResult}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should find a performanceLevelDescriptors is "([^"]*)"$/ do |performanceLevel|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    level = dataH["performanceLevelDescriptors"][0][0]['description']
    assert(level==performanceLevel.to_s, "Expected performanceLevel is #{performanceLevel}, received #{level}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

