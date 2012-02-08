require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I am not authenticated to SLI IDP$/ do
  @driver.manage.delete_all_cookies
end

When /^I navigate to the SLI Default Roles Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']
  @driver.get url
end

Then /^I should be redirected to the Realm page$/ do
  assertWithWait("Failed to be redirected to Realmchooser")  {@driver.current_url.index("/disco/realms/") != nil}
end

Given /^I am authenticated to SLI IDP$/ do
  url = PropLoader.getProps['sli_idp_server_url']+"/UI/Login"
  @driver.get url
  @driver.find_element(:id, "IDToken1").send_keys "demo"
  @driver.find_element(:id, "IDToken2").send_keys "demo1234"
  @driver.find_element(:name, "Login.Submit").click
end

Given /^I am authenticated to SLI IDP as user "([^"]*)" with pass "([^"]*)"$/ do |arg1, arg2|
  url = PropLoader.getProps['sli_idp_server_url']+"/UI/Login"
  @driver.get url
  @driver.find_element(:id, "IDToken1").send_keys arg1
  @driver.find_element(:id, "IDToken2").send_keys arg2
  @driver.find_element(:name, "Login.Submit").click
end

Given /^I am authenticated to SEA\/LEA IDP as user "([^"]*)" with pass "([^"]*)"$/ do |arg1, arg2|
  url = PropLoader.getProps['sea_idp_server_url']+"/UI/Login"
  @driver.get url
  @driver.find_element(:id, "IDToken1").send_keys arg1
  @driver.find_element(:id, "IDToken2").send_keys arg2
  @driver.find_element(:name, "Login.Submit").click
end

Then /^I should be redirected to the SLI Default Roles Admin Page$/ do
  assertWithWait("Failed to navigate to the Admintools Role page")  {@driver.page_source.index("Default SLI Roles") != nil}
end

Given /^I have tried to access the SLI Default Roles Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']
  @driver.get url
end

Given /^I was redirected to the Realm page$/ do
  assert(@driver.current_url.index("/disco/realms/") != nil, webdriverDebugMessage(@driver,"Failed to be redirected to Realmchooser"))
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
  assert(@driver.current_url.index("/idp") != nil, webdriverDebugMessage(@driver,"Failed to navigate to IDP login page"))
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
  #No code needed for this step
end

Then /^I am informed that authentication has failed$/ do
  errorBox = @driver.find_element(:name, "Login.AlertImage")
  assert(errorBox != nil, webdriverDebugMessage(@driver,"Could not find error message box with name=Login.AlertImage"))
end

Then /^I do not have access to the SLI Default Roles Admin Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']
  assert(@driver.page_source.index("Default SLI Roles") == nil, webdriverDebugMessage(@driver,"Navigated to the Admintools Role page with no credentials"))
end

Given /^I have a Role attribute equal to "([^"]*)"$/ do |arg1|
  #No code needed, this is done durring the IDP configuration
end

Then /^I should get a message that I am not authorized$/ do
  assertWithWait("Could not find Not Authorized in page title")  {@driver.page_source.index("wrong")!= nil}
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
