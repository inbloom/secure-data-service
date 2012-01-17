require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"244520d2-8c6b-4a1e-b35e-d67819ec0211"  if arg2 == "'Ms. Jones' ID"
  id = arg1+"8e5b2d0e-959c-42ef-b3df-9b83cba85a33"  if arg2 == "'Mr. Smith' ID"
  id = arg1+"a249d5d9-f149-d348-9b10-b26d68e7cb9c"  if arg2 == "'Mrs. Solis' ID"
  id = arg1+"41baa245-ceea-4336-a9dd-0ba868526b9b"  if arg2 == "'Algebra Alternative' ID"
  id = arg1+"0f464187-30ff-4e61-a0dd-74f45e5c7a9d"  if arg2 == "'Biology High' ID"
  id = arg1+"b6ad1eb2-3cf7-41c4-96e7-2f393f0dd847"  if arg2 == "'Chemistry Elementary' ID"
  id = arg1+"fcb4b719-8d9c-4ef1-a164-9eff33ef8f0c"  if arg2 == "Physics Middle's ID"
  id = arg1+"53616a21-df46-4990-aca7-2c8514a9fdb4"  if arg2 == "'Teacher Mr. Smith and School Biology High' ID"
  id = arg1+"41bd4c23-665d-4bae-88fb-1cfab8312d17"  if arg2 == "'Teacher Ms. Jones and School Algebra Alternative' ID"
  id = arg1+@newId                                  if arg2 == "'newly created teacher-school-association' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111"  if arg2 == "'Invalid ID'"
  id
end


###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
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
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I navigate to POST "([^"]*)"$/ do |post_uri|
  data = data_builder
  restHttpPost(post_uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to (Teacher School Associations for (Teacher|School) <[^"]*>)$/ do |uri,arg2|
  @previousUri = uri
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |property,value|
  step "\"#{property}\" is \"#{value}\""
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  if @format == "application/json"
    modified = JSON.parse(@res.body)
    @fields.each do |key, value|
      modified[key] = value
    end
    data = modified.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I navigate to DELETE "([^"]*<[^"]*>)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

When /^I navigate to GET "([^"]*<[^"]*>)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json"
    begin
      @data = JSON.parse(@res.body);
    rescue
      @data = nil
    end
  elsif @format == "application/xml"
    assert(false, "XML not supported yet")
  else
    assert(false, "Unsupported MediaType")
  end
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
  @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

Then /^I should receive an ID for the newly created (.*)$/ do |object_type_arg|
  #common definition for extracting newly created object's ID
  @newId = getIdOfNewlyCreatedObject(object_type_arg)
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  if key == "instructionalGradeLevels"
    assert(@data.has_key?(key), "Response does not contain key #{key}")
    assert(@data[key][0] == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
  else
    assert(@data.has_key?(key), "Response does not contain key #{key}")
    assert(@data[key] == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
  end
  
end


Then /^I should receive a collection of (\d+) teacher\-school\-association links$/ do |size|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  assert(@data.length == Integer(size), "Expected response of size #{size}, received #{@data.length}");
  
  @ids = Array.new
    @data.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @ids.each do |id|
    found=false
    if findLink(id,"/teacher-school-associations/", rel,href)
      found = true
    end
    assert(found, "didnt receive link named #{rel} with URI #{href}")
  end
  
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  found = false
  @ids.each do |id|
    if findLink(id,"/teacher-school-associations/", rel,href)
      found = true
      break
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

#return boolean
def findLink(id, type, rel, href)
  found = false
  uri = type+id
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    dataH["links"].each do |link|
      if link["rel"]==rel and link["href"].include? href
        found = true
        break
      end
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  return found
end

When /^I attempt to update a non\-existing association "(\/teacher-school-associations\/<[^"]*>)"$/ do |uri|
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







