require 'selenium-webdriver'

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG'] 

# Given /^I have an open web browser$/ do
  # pending # express the regexp above with the code you wish you had
# end

Given /^I am not authenticated to SLI$/ do
  puts "not auth to SLI"
  # do nothing
end

When /^I navigate to the Dashboard home page$/ do
  url = "http://"+PropLoader.getProps['dashboard_server_address']+PropLoader.getProps['dashboard_home_url']
  @driver.get url
  assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  @realmPageUrl = PropLoader.getProps['realm_page_url']
  assert(@driver.current_url == @realmPageUrl, "Failed to navigate to "+@realmPageUrl)
end

Given /^I am authenticated to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should be redirected to the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I have tried to access the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^was redirected to the Realm page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I chose "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I clicked the button Go$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I was redirected to the SLI IDP Login page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am user "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^"([^"]*)" is valid "([^"]*)" user$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I enter "([^"]*)" in the username text field$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I enter "([^"]*)" in the password text field$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I click the Go button$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am authenticated to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^"([^"]*)" is invalid "([^"]*)" user$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I am informed that "([^"]*)" does not exists$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the SLI\-IDP Login Page$/ do
  pending # express the regexp above with the code you wish you had
end