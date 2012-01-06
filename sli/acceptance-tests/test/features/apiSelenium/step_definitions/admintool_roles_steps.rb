require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I am not authenticated to SLI IDP$/ do
  @driver.manage.delete_all_cookies
end

When /^I navigate to the SLI Default Roles Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']+"/admin/roles"
  @driver.get url
end

Then /^I should be redirected to the Realm page$/ do
  assert(@driver.current_url.index("/disco/realms/") != nil, "Failed to be redirected to Realmchooser")
end

Given /^I am authenticated to SLI IDP$/ do
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/UI/Login"
  @driver.get url
  @driver.find_element(:id, "IDToken1").send_keys "administrator"
  @driver.find_element(:id, "IDToken2").send_keys "administrator1234"
  @driver.find_element(:name, "Login.Submit").click
end

Then /^I should be redirected to the SLI Default Roles Admin Page$/ do
  assert(@driver.title.index("SLI Default Roles") != nil, "Failed to navigate to the Admintools Role page")
end

Given /^I have tried to access the SLI Default Roles Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']+"/admin/roles"
  @driver.get url
end

Given /^I was redirected to the Realm page$/ do
  assert(@driver.current_url.index("/disco/realms/") != nil, "Failed to be redirected to Realmchooser")
end

Given /^I choose my realm$/ do
  dropdownbox = @driver.find_element(:name, "realmId")
  dropdownbox.click
  dropdownbox.find_elements(:tag_name => "option").find do |option|
    option.text == "Shared Learning Initiative"
  end.click
  @driver.find_element(:id, "go").click
end

Given /^I was redirected to the SLI IDP Login page$/ do
  assert(@driver.current_url.index("/idp") != nil, "Failed to navigate to IDP login page")
end

Given /^I am user "([^"]*)"$/ do |arg1|
  #No code needed for this step
end

Given /^"([^"]*)" is valid "([^"]*)" user$/ do |arg1, arg2|
  #No code needed for this step
end

When /^I enter "([^"]*)" in the username text field$/ do |arg1|
  @driver.find_element(:id, "IDToken1").send_keys arg1
end

When /^I enter "([^"]*)" in the password text field$/ do |arg1|
  @driver.find_element(:id, "IDToken2").send_keys arg1
end

When /^I click the Go button$/ do
  @driver.find_element(:name, "Login.Submit").click
end

Then /^I am now authenticated to SLI IDP$/ do
  #No code needed, this is tested implicitly by accessing the admin roles page
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

Given /^I have navigated to the SLI Default Roles Admin Page$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the Logout link$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am no longer authenticated to SLI IDP$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the Default SLI Roles and Permissions URL$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the browser opens the confluence Default SLI Roles and Permissions page in a new browser window$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the browser focus is on the new browser window$/ do
  pending # express the regexp above with the code you wish you had
end