require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  #sessions
  id = "31e8e04f-5b1a-4631-91b3-a5433a735d3b"  if human_readable_id == "'Spring 2010 Session' ID"
  id = "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e"  if human_readable_id == "'Fall 2011 Session' ID"
  #courses
  id = "53777181-3519-4111-9210-529350429899"  if human_readable_id == "'French 1 Course' ID"
  id = "93d33f0b-0f2e-43a2-b944-7d182253a79a"  if human_readable_id == "'German 1 Course' ID"
  id = "a7444741-8ba1-424e-b83f-df88c57f8b8c"  if human_readable_id == "'Spanish 1 Course' ID"
  #associations
  id = "9ff65bb1-ef8b-4588-83af-d58f39c1bf68"  if human_readable_id == "'German 1 during Fall 2011' ID"
  #general
  id = "11111111-1111-1111-1111-111111111111"  if human_readable_id == "'Invalid ID'"
  id = @newId                                  if human_readable_id == "'newly created session-course-association' ID"
  id
end

Transform /^([^"]*\/)(<[^"]*>)$/ do |path_prefix, raw_id|
    path = path_prefix + Transform(raw_id)
    path
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^grading period "([^"]*)" is "([^"]*)"$/ do |key, value|
  @gradingPeriod = {} if !defined? @gradingPeriod
  @gradingPeriod[key] = value
  @fields = {} if !defined? @fields
  @fields['gradingPeriod'] = @gradingPeriod
  
end

Given /^grading period "([^"]*)" is (\d+)$/ do |key, value|
  @gradingPeriod = {} if !defined? @gradingPeriod
  @gradingPeriod[key] = value
  @fields = {} if !defined? @fields
  @fields['gradingPeriod'] = @gradingPeriod
  
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  @fields = {} if !defined? @fields
  
  if key == "instructionalGradeLevels"
    @fields[key] = [value]
  else
    @fields[key] = value
  end
end


###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN 2WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I navigate to POST "([^"]*)"$/ do |post_uri|
  data = prepareData(@format, @fields)
  restHttpPost(post_uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |property,value|
  step "\"#{property}\" is \"#{value}\""
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |url|
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil")
end


###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^grading period "([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?('gradingPeriod'), "Response does not contain gradingPeriod")
  assert(@result['gradingPeriod'][key] == value, "Grading period #{key} expected value #{value} != #{@result['gradingPeriod'][key]}")
end

Then /^grading period "([^"]*)" should be (\d+)$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  assert(@result.has_key?('gradingPeriod'), "Response does not contain gradingPeriod")
  assert(@result['gradingPeriod'][key] == value, "Grading period #{key} expected value #{value} != #{@result['gradingPeriod'][key]}")
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Hash), "Response contains #{@result.class}, expected Hash")
  if key == "instructionalGradeLevels"
    assert(@result.has_key?(key), "Response does not contain key #{key}")
    assert(@result[key][0] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
  else
    assert(@result.has_key?(key), "Response does not contain key #{key}")
    assert(@result[key] == value, "Expected #{key} to equal #{value}, received #{@result[key]}")
  end
  
end


Then /^I should receive a collection of (\d+) ([^"]*) links$/ do |number_of_links, link_type|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_links), "Expected response of size #{number_of_links}, received #{@result.length}");
  
  @ids = Array.new
    @result.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @ids.each do |id|
    found=false
    if findLink(id,"/session-course-associations/", rel,href)
      found = true
    end
    assert(found, "didnt receive link named #{rel} with URI #{href}")
  end
  
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  found = false
  @ids.each do |id|
    if findLink(id,"/session-course-associations/", rel,href)
      found = true
      break
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

When /^I attempt to update a non\-existing association "(\/session-course-associations\/<[^"]*>)"$/ do |uri|
  data = {}
  restHttpPut(uri, data.to_json)
  assert(@res != nil, "Response from rest-client PUT is nil")
end


# 
# Function data_builder
# Inputs: object_type - used only for error message when ID cannot be found
# Output: None
# Returns: ID of newly created object
# Description: Searches headers from an HTTP response for the newly created object's ID and returns it
#
def getIdOfNewlyCreatedObject (object_type="Object")
  headers = @res.raw_headers
  headers.should_not be_nil
  headers['location'].should_not be_nil
  s = headers['location'][0]
  newId = s[s.rindex('/')+1..-1]
  assert(newId != nil, "#{object_type} ID is nil")
  newId
end

# function: validateValueInResults
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResults(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    assert(result[property]==value, "Expected #{description} not found in response")
end

# function: validateValueInResultArray
# inputs:   value: value to check for
#           property: what name to search under for the value
#           description: how to describe the value not found if/when value is not found
# outputs:  true/false
# Returns:  Nothing, see output
# Description: Helper function for all the tests that need to check the presence of a value
#               in an array that is in a result body
def validateValueInResultArray(value,property,description)
    result = JSON.parse(@res.body)
    assert(result != nil, "Result of JSON parsing is nil")
    arrayOfResults = result[property]
    valueFound = false
    arrayOfResults.each do |valueFromArray|
        if(value==valueFromArray)
            valueFound = (true)
        end
    end
    assert(valueFound, "Expected #{description} not found in response")
end


# Function data_builder
# Inputs: None
# Output: Data object in json or XML format depending on what the @format variable is set to
# Returns: Nothing, see Output
# Description: Helper function to create json or XML data structures to PUT or POST 
#                   to reduce replication of code
def data_builder
  if @format == "application/json"
  formatted_data = @fields.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent => 2)
    formatted_data = builder.section { |b| 
      b.teacherId(@fields["teacherId"])
      b.schoolId(@fields["schoolId"])  
      }
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  formatted_data
end







