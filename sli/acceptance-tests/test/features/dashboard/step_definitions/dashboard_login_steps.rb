require 'selenium-webdriver'
require_relative '../../utils/sli_utils.rb'

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG'] 

Given /^I am not authenticated to SLI$/ do
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix] + PropLoader.getProps['dashboard_logout_page']
  @driver.get url
end

When /^I navigate to the Dashboard home page$/ do
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix]
  @driver.get url
  # There's a redirect to the realm page, so this assert should fail
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  # TODO
  # Change to actual realm page url when it is integrated
  @realmPageUrl = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix] + PropLoader.getProps['dashboard_login_page']
  assert(@driver.current_url == @realmPageUrl, "Failed to navigate to "+@realmPageUrl)
end

Given /^I am authenticated to SLI as "([^"]*)" password "([^"]*)"$/ do |username, password|
  sleep 1
  localLogin(username, password)
end

Then /^I should be redirected to the Dashboard landing page$/ do
  @expected_url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix]+PropLoader.getProps['dashboard_landing_page'];
  assert(@driver.current_url == @expected_url, "Failed to navigate to "+@expected_url)
end

Given /^was redirected to the Realm page$/ do
  # TODO
  # This should block until the Realm page is loaded
  sleep 1
end

Given /^was redirected to the SLI\-IDP login page$/ do
  # TODO
  # This should block until the login page is loaded
  sleep 1
end

Given /^I chose "([^"]*)"$/ do |arg1|
  # TODO
  # Fill in when there is integration with Realm page
end

Given /^I clicked the Go button$/ do
  # TODO
  # Fill in when there is integration with Realm page
end

Given /^I enter "([^"]*)" in the username text field and "([^"]*)" in the password text field$/ do |username, password|
  assertMissingField("j_username", "name")
  assertMissingField("j_password", "name")
  putTextToField(username, "j_username", "name")
  putTextToField(password, "j_password", "name")
end

Given /^I clicked the Submit button$/ do
  assertMissingField("submit", "name")
  clickButton("submit", "name")
end

Then /^I am informed that "([^"]*)" does not exists$/ do |arg1|
  assertText("Your login attempt was not successful")
  assertText("Bad credentials")
end

Then /^I am redirected to the SLI\-IDP Login page$/ do
  # We don't have true integration yet
  # TODO: 
  @realmPageUrl = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix] + PropLoader.getProps['dashboard_login_page']
  assert(@driver.current_url.start_with?(@realmPageUrl), "Failed to navigate to "+@realmPageUrl)
end

When /^I access "([^"]*)"$/ do |path|
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps[@appPrefix] + path
  @driver.get url
end

Then /^I get an error code "([^"]*)"$/ do |errCode|
  # TODO: 
  # Is there's no way to get the http status code from selenium?? 
end

Then /^I can see "([^"]*)"$/ do |arg1|
  assertText(arg1)
end

