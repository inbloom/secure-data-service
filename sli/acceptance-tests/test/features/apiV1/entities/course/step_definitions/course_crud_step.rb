require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../common.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  case template
  when "<id: 'Russian 1 Course'>"
    "b8dbdefb-85b6-47e0-8a26-ef0f38568ddf"
  when "<id: 'Spanish 1 Course'>"
    "a7444741-8ba1-424e-b83f-df88c57f8b8c"
  when "<non-existent ID>"
    "11111111-1111-1111-1111-111111111111"
  when "<id: 'using a wrong URI'>"
    "b8dbdefb-85b6-47e0-8a26-ef0f38568ddf"
  when"<newly created ID>"
    @newId 
  when "<true>"
    true 
  when "<false>"
    false 
  when "<new-course: Chinese 1>"
    {"courseTitle"=>"Chinese 1", "numberOfParts"=>1, "courseCode"=>[{"ID"=>"C1", "identificationSystem"=>"School course code", "assigningOrganizationCode"=>"Bob's Code Generator"}], "courseLevel"=>"Basic or remedial", "courseLevelCharacteristics"=>["Advanced Placement"], "gradesOffered"=>"Eighth grade", "subjectArea"=>"Foreign Language and Literature", "courseDescription"=>"Intro to Chinese", "dateCourseAdopted"=>"2001-01-01", "highSchoolCourseRequirement"=>false, "courseDefinedBy"=>"LEA", "minimumAvailableCredit"=>{"credit"=>1.0}, "maximumAvailableCredit"=>{"credit"=>1.0}, "careerPathway"=>"Hospitality and Tourism"}
  else
    nil
  end
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end

Given /^I have data for the course "([^"]*)"$/ do |course|
  @fields = course
end

Given /^the response entity should match "([^"]*)"$/ do |course|
  puts @res
  course.each do |key,value|
    assert(@res[key] != value)
  end
end

When /^I navigate to POST "([^\"]+)"$/ do |url|
  puts "fields = #{@fields}"
  data = prepareData(@format, @fields)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end

When /^I attempt to update "([^"]*<[^"]*>)"$/ do |url|
  @result = {}
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |key, value|
  value = convert(value)
  @result[key] = value
end

Then /^I should see the "([^"]*)" is "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  assert(@result[key] == value, "Expected value \"#{key}\" != \"#{@result[key]}\"")
end

Then /^I should see the "([^"]*)" is ([0-9\.]+)$/ do |key, value|
  @result[key].should_not == nil
  assert(@result[key].to_f == value.to_f, "Expected value \"#{key}\" != \"#{@result[key]}\"")
end