require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'


Given /^I am a valid SLC Operator$/ do
  @user = 'slcoperator' # an :operator
  @pass = 'slcoperator1234'
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
  step "I navigate to the Complex-Configurable Role Mapping Page"
  step "I login"
end

And /^I login$/ do
  step "I was redirected to the \"Simple\" IDP Login page"
  step "I submit the credentials \"#@user\" \"#@pass\" for the \"Simple\" login page"
end

Then /^the api should generate a (\d+) error$/ do |arg1|
  puts @driver.current_url
  assert(@driver.current_url.end_with?("api/rest/saml/sso/post"), "Should have gotten an error")
end

