require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  case template
  when "<id: 'Russian 1 Course'>"
    "b8dbdefb-85b6-47e0-8a26-ef0f38568ddf"
  when "<id: 'Spanish 1 Course'>"
    "a7444741-8ba1-424e-b83f-df88c57f8b8c"
  when "<non-existent ID>"
    "11111111-1111-1111-1111-111111111111"
  when"<newly created ID>"
    @newId 
  when "<true>"
    true 
  when "<false>"
    false 
  when "<course: Chinese 1>"
    {"courseTitle"=>"Chinese 1", "numberOfParts"=>1, "courseCode"=>[{"ID"=>"C1", "identificationSystem"=>"School course code", "assigningOrganizationCode"=>"Bob's Code Generator"}], "courseLevel"=>"Basic or remedial", "courseLevelCharacteristics"=>["Advanced Placement"], "gradesOffered"=>"Eighth grade", "subjectArea"=>"Foreign Language and Literature", "courseDescription"=>"Intro to Chinese", "dateCourseAdopted"=>"2001-01-01", "highSchoolCourseRequirement"=>false, "courseDefinedBy"=>"LEA", "minimumAvailableCredit"=>{"credit"=>1.0}, "maximumAvailableCredit"=>{"credit"=>1.0}, "careerPathway"=>"Hospitality and Tourism"}
  when "<course: Spanish 1>"
    # TODO: update fixture data and use here
  else
    nil
  end
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
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



Given /^list "([^"]*)" is "([^"]*)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end

  keys = key.split('.')
  list = keys.shift

  @fields[list] = [{}] if (@fields[list]==nil)
  hash = @fields[list].first

  add_field(hash, value, keys)
end

Given /^a "([^"]*)" is "([^"]*)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  if (@fields[key]!=nil)
    @fields[key].insert(0,value)
  end
  if (@fields[key]==nil)
    @fields[key] = [value]
  end
end

When /^I navigate to POST "([^\"]+)"$/ do |url|
  puts "fields = #{@fields}"
  data = prepareData(@format, @fields)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end

def add_field(hash, value, keys = [])
  return if keys.empty?
  if keys.size == 1
    hash[keys.first] = value
    return
  end

  key = keys.shift
  hash[key] = {} if !hash.has_key?(key)
  add_field(hash[key], value, keys)
end

def get_field(hash, value, keys = [])
  return if keys.empty?
  if keys.size == 1
    hash[keys.first] = value
    return
  end

  key = keys.shift
  hash[key] = {} if !hash.has_key?(key)
  add_field(hash[key], value, keys)
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end

  add_field(@fields, value, key.split('.'))
end

Given /^"([^"]*)" is ([\d\.]*)$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end

  if value['.']
    value = value.to_f 
  else
    value = value.to_i
  end

  add_field(@fields, value, key.split('.'))
end

When /^I set "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  step "\"#{arg1}\" is \"#{arg2}\""
end

Then /^I should receive a collection of (\d+) links$/ do |size|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(size), "Expected response of size #{size}, received #{@result.length}");

  @ids = Array.new
    @result.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^a "([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  found = false;
  @result[key].each do |value_from_array| 
    found = (value==value_from_array)?true:found;
  end
  assert(found, "Expected value #{value} not found in array")
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  assert(@result[key] == value, "Expected value \"#{key}\" != \"#{@result[key]}\"")
end

Then /^"([^"]*)" should be (\d+)$/ do |key, value|
  @result[key].should_not == nil
  assert(@result[key].to_i == value.to_i, "Expected value #{value} != #{@result[key]}")
end

Then /^I should have a link with ID "([^"]*)"$/ do |linkid|
  found = false
  @ids.each do |crnt_id|
    if crnt_id == linkid
      found = true
    end
  end

  assert(found, "Link ID not found.")
end
