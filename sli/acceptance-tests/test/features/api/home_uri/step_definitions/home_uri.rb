require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'


# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = template
  id = "eb4d7e1b-7bed-6dd7-d9b4-1d729a37fd2d" if template == "<'administrator' ID>";
  id = "714c1304-8a04-4e23-b043-4ad80eb60992" if template == "<'mock' ID>"
  id = "fa45033c-5517-b14b-1d39-c9442ba95782" if template == "<'Macey' ID>";
  id = "738543275"                            if template == "<'Macey Home State' ID>"
  id = "344cf68d-50fd-8dd7-e8d6-ed9df76c219c" if template == "<'Belle' ID>"
  id = "11111111-1111-1111-1111-111111111111" if template == "<Unknown>"
  id = "824643f7-174b-4a50-9383-c9a6f762c49d" if template == "<'Christian' ID>"
  id = "a249d5d9-f149-d348-9b10-b26d68e7cb9c" if template == "<'Illiana' ID>"
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


Given /^mock student ID (<[^"]*>)$/ do |mock_id|
  @mock_id = mock_id
end

#Transform /^<([^"]*)>$/ do |step_arg|
#  id = "714c1304-8a04-4e23-b043-4ad80eb60992"  if step_arg == "mock ID"
#  id = "/home"                                 if step_arg == "home URI"
#  id = "/students/"+@mock_id                   if step_arg == "student by ID"
#  id
#end

When /^I navigate to GET (<[^"]*>)$/ do |uri|
  print uri
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

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)" and appropriate ID$/ do |rel, href|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
   @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href+@mock_id)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
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


def contentType(response)
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['content-type'] != nil, "There is no content-type set in the response")
  headers['content-type'][0]
end

