require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |usr, pass|
  idpLogin(usr, pass)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Given /^mock student ID (<[^"]*>)$/ do |mock_id|
  @mock_id = mock_id
end

Transform /^<([^"]*)>$/ do |step_arg|
  id = "714c1304-8a04-4e23-b043-4ad80eb60992"  if step_arg == "mock ID"
  id = "/home"                                 if step_arg == "home URI"
  id = "/students/"+@mock_id                   if step_arg == "student by ID"
  id
end

When /^I navigate to GET (<[^"]*>)$/ do |uri|
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

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}")
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