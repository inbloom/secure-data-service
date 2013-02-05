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
require 'json'
require 'builder'
require 'rexml/document'
include REXML

require_relative '../../utils/sli_utils.rb'

Given /^I login with "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
    @user = arg1
    @passwd = arg2
    idpLogin(@user, @passwd)
end

Given /^I get an authentication session ID from the gatekeeper$/ do
    assert(@sessionId != nil, "Session returned was nil")
end

When /^I GET the url "([^"]*)" using that session ID$/ do |arg1|
    restHttpGet(arg1, "application/json")
    assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see the session debug context in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authentication'] != nil, "Session debug context doesn't contain 'authentication'")
    assert(data['authentication']['principal'] != nil, 
           "Session debug context doesn't contain 'authentication.principal'")
    assert(data['authentication']['authenticated'] != nil, 
           "Session debug context doesn't contain 'authentication.authenticated'")
    assert(data['authentication']['authenticated'] == true, 
           "Session debug context 'authentication.authenticated' is not true")
end

When /^I GET the url "([^"]*)" using a blank session ID$/ do |arg1|
    restHttpGet(arg1, "application/json", "")
end

When /^I GET the url "([^"]*)" using an invalid session ID$/ do |arg1|
    restHttpGet(arg1, "application/json", "invalid")
end

Then /^I should see the authenticated object in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authenticated'] != nil, "Response body does not contain 'authenticated'")
    assert(data['authenticated'] == true, "'authenticated' was not true")
end

Then /^I should see the non\-authenticated object in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authenticated'] != nil, "Response body does not contain 'authenticated'")
    assert(data['authenticated'] == false, "'authenticated' was not false ")
end

Then /^I should see a link in the responce header telling me where to authenicate$/ do
  assert(@res.headers[:www_authenticate] != nil, "There was no authentication header")
end

When /^I GET the url "(.*?)" using a staff ID$/ do |arg1|
  step 'I am logged in using "rrogers" "rrogers1234" to realm "IL"'
  step "I GET the url \"#{arg1}\" using that session ID"
end

Then /^I should see the email address in the response$/ do
  data = JSON.parse(@res.body)
  assert data['email'] != nil, "We should have a valid email address, not #{data['email']}"
end

Then /^I should see the work email address in the response$/ do
  data = JSON.parse(@res.body)
  assert data['email'] == "Work@Work.com", "We should have a valid work email address, not #{data['email']}"
end

When /^I GET the url "(.*?)" using an admin ID$/ do |arg1|
  step 'I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"'
  step "I GET the url \"#{arg1}\" using that session ID"
end
