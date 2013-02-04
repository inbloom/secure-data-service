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


require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'


Given /^I am a valid SLC Operator$/ do
  @user = 'slcoperator-email@slidev.org' # an :operator
  @pass = 'slcoperator-email1234'
end

Given /^I am a valid Super Administrator$/ do
  @user = 'daybreaknorealmadmin' # a :super_admin
  @pass = 'daybreaknorealmadmin1234'
end

When /^I try to authenticate on the Application Approval Tool$/ do
  # NOTE approval tool and registration tool share the same url
  step "I hit the Application Registration Tool URL"
  step "I login"
end

When /^I try to authenticate on the Application Registration Tool$/ do
  step "I hit the Application Registration Tool URL"
  step "I login"
end

When /^I try to authenticate on the Application Authorization Tool$/ do
  step "I hit the Admin Application Authorization Tool"
  step "I login"
end

When /^I try to authenticate on the Role Mapping Tool$/ do
  step "I navigate to the Custom Role Mapping Page"
  step "I login"
end

And /^I login$/ do
  step "I was redirected to the \"Simple\" IDP Login page"
  step "I submit the credentials \"#@user\" \"#@pass\" for the \"Simple\" login page"
end

Then /^the api should generate a (\d+) error$/ do |arg1|
  sleep 2
  puts @driver.current_url
  assert(@driver.current_url.end_with?("api/rest/saml/sso/post"), "Should have gotten an error")
end

