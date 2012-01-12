require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = template
  id = @newId.to_s if template == "<'newly created section' ID>"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480" if template == "<'chemistryF11' ID>"
  if template == "<'biologyF09' ID>"
    if @format == "application/json" or @format == "application/vnd.slc+json"
      id = "2934f72d-f9e3-48fd-afdd-56b94e2a3454" # biologyF09J
    elsif @format == "application/xml" or @format == "application/vnd.slc+xml"
      id = "c2efa2b3-f0c6-472a-b0d3-2e7495554acc" # biologyF09X
    end
  end
  id = "11111111-1111-1111-1111-111111111111" if template == "<'Invalid' ID>"
  id = "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431" if template == "<'physicsS08' ID>"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end


### GIVEN ###

Given /^I am logged in using "([^\"]*)" "([^\"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have access to all sections$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/xml", "text/plain"].should include(fmt)
  @format = fmt
end

Given /^the "([^\"]+)" is "([^\"]+)"$/ do |key, value|
  if !defined? @data
    @data = {}
  end
  value = convert(value)
  @data[key] = value
end

### WHEN ###

When /^I navigate to POST "([^\"]+)"$/ do |url|
  data = prepareData(@format, @data)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to GET "([^\"]+)"$/ do |url|
  restHttpGet(url)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res)
  if /application\/json/.match contentType
    @result = JSON.parse(@res.body)
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
    puts @result
  else
    @result = {}
  end
end

When /^I navigate to PUT "([^\"]*)"$/ do |url|
  if !defined? @data
    @data = {}
  end
  data = prepareData(@format, @data)
  restHttpPut(url, data)
end

When /^I navigate to DELETE "([^\"]*)"$/ do |url|
  restHttpDelete(url)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

When /^I set the "([^\"]*)" to "([^\"]*)"$/ do |key, value|
  step "the \"#{key}\" is \"#{value}\""
end

### THEN ###

Then /^I should receive a return code of (\d+)$/ do |status|
  @res.code.should == Integer(status)
end

Then /^I should receive an ID for the newly created (\w+)$/ do |entity|
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "#{entity} ID is nil")
end

Then /^the "([^\"]*)" should be "([^\"]*)"$/ do |key, value|
  value = convert(value)
  @result[key].should_not == nil
  @result[key].should == value
end

Then /^I should receive a link named "([^\"]*)" with URI "([^\"]*)"$/ do |rel, href|
  @result["links"].should_not == nil
  found = false
  @result["links"].each do |link|;
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
      break
    end
  end
  assert(found, "Did not find a link rel=#{rel} href=#{href}")
end

### Util methods ###

def convert(value)
  if /^true$/.match value
    true;
  elsif /^false$/.match value
    false;
  elsif /^\d+\.\d+$/.match value
    Float(value)
  elsif /^\d+$/.match value
    Integer(value)
  else
    value
  end
end

def prepareData(format, hash)
  if format == "application/json"
    hash.to_json
  elsif format == "application/xml"
    raise "XML not implemented"
  else
    assert(false, "Unsupported MIME type")
  end
end

def contentType(response) 
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['content-type'] != nil, "There is no content-type set in the response")
  headers['content-type'][0]
end