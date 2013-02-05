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
require_relative '../../security/step_definitions/securityevent_util_steps.rb'
require_relative '../../sandbox/AccountApproval/step_definitions/prod_sandbox_AccountApproval_Inteface_steps.rb'

Given /^I am a valid SLC developer$/ do
  @user = 'slcdeveloper' # an :operator
  @pass = 'slcdeveloper1234'
end

Given /^I am a valid SLC operator$/ do
  @user = 'slcoperator-email@slidev.org' # an :operator
  @pass = 'test1234'
end

Given /^I am a valid realm administrator$/ do
  @user = 'sunsetrealmadmin' # an :operator
  @pass = 'sunsetrealmadmin1234'
end


Given /^I am a valid district administrator$/ do
  @user = 'sunsetadmin'
  @pass = 'sunsetadmin1234'
end

When /^I authenticate on the realm editing tool$/ do
  step "I hit the realm editing URL"
  step "I login"
end

When /^I authenticate on the Application Registration Tool$/ do
  step "I hit the Application Registration Tool URL"
  step "I login"
end

When /^I authenticate on the Admin Delegation Tool$/ do 
  step "I hit the delegation url"
  step "I login"
end

