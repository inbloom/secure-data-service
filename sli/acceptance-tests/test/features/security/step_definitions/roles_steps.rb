require 'json'
require_relative '../../utils/sli_utils.rb'

Given /^I am valid SEA\/LEA end user$/ do
  # No code needed, this is done during the IDP configuration
end

Given /^I have a Role attribute returned from the "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
  @role = arg1
end

Given /^I am authenticated on "([^"]*)"$/ do |arg1|
  idpLogin(@role, @role+"1234")
  assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I make a REST API call$/ do
  restHttpGet("/schools/eb3b8c35-f582-df23-e406-6947249a19f2", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I get the JSON response displayed$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

Then /^I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

Given /^I do not have a Role attribute returned from the "([^"]*)"$/ do |arg1|
  @role = "nouser"
end