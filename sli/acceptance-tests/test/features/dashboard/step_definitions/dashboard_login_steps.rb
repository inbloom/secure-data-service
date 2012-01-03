require 'selenium-webdriver'
require_relative '../../utils/sli_utils.rb'

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG'] 

Given /^I am not authenticated to SLI$/ do
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps['dashboard_app_prefix'] + PropLoader.getProps['dashboard_logout_page']
  @driver.get url
end

When /^I navigate to the Dashboard home page$/ do
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps['dashboard_app_prefix']
  @driver.get url
  # There's a redirect to the realm page, so this assert should fail
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  @realmPageUrl = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps['dashboard_app_prefix'] + PropLoader.getProps['dashboard_login_page']
  assert(@driver.current_url == @realmPageUrl, "Failed to navigate to "+@realmPageUrl)
end

Given /^I am authenticated to SLI as "([^"]*)" password "([^"]*)"$/ do |username, password|
  localLogin(username, password)
end

Then /^I should be redirected to the Dashboard home page$/ do
#  @expected_url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps['dashboard_app_prefix']+PropLoader.getProps['dashboard_landing_page'];
#  assert(@driver.current_url == @realmPageUrl, "Failed to navigate to "+@expected_url)
end

Given /^I have tried to access the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^was redirected to the Realm page$/ do
  pending # express the regexp above with the code you wish you had
  # This should block until the Realm page is loaded
end

Given /^was redirected to the SLI\-IDP login page$/ do
  pending # express the regexp above with the code you wish you had
  # This should block until the login page is loaded
end

Given /^I chose "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I clicked the Go button$/ do
  pending # express the regexp above with the code you wish you had
end


Given /^I enter "([^"]*)" in the username text field and "([^"]*)" in the password text field$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am informed that "([^"]*)" does not exists$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the SLI\-IDP Login Page$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I access "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I get an error code "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I can see "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the SLI IDP Login page$/ do
  pending # express the regexp above with the code you wish you had
end

