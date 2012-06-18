require "selenium-webdriver"

require_relative '../../../utils/sli_utils.rb'

When /^I type the Realm page URL$/ do
  @url = "http://"+PropLoader.getProps['dashboard_api_server_url']+"/disco/realms/list.do" 
end

When /^I click on the Enter button$/ do
  @driver.get @url if @url
end

Given /^I see the Realm page$/ do
  @driver = Selenium::WebDriver.for :firefox
  url = "http://"+PropLoader.getProps['dashboard_api_server_url']+"/disco/realms/list.do"
  @driver.get url
  
  assertWithWait("Failed to navigate to Realm page")  { @driver.title.index("Choose your realm") }
end

When /^I choose realm "([^"]*)" in the drop\-down list$/ do |arg1|
  id = "1" if arg1 == "UED"
  id = "2" if arg1 == "OotP"
  id = "3" if arg1 == "SLI"
  @driver.find_element(:id, id).click
end

When /^I click on the page Go button$/ do
  @driver.find_element(:id, "go").click
#  pending # express the regexp above with the code you wish you had
end

Then /^I should be redirected to "([^"]*)" Realm Login page$/ do |arg1|
  assert(@driver.current_url.index("http://devdanil.slidev.org:8080/idp/SSORedirect/metaAlias/idp") != nil, "Failed to navigate to IDP login page")
end

Given /^a realm in the drop\-down list is not \(pre\)selected$/ do
  #No code needed, just dont click on anything
end

Then /^I should be notified that I must choose a realm$/ do
  assert(@driver.current_url.index("/disco/realms/") != nil, "Navigated off the page when we should have stayed")
end

When /^I choose NC in the realm drop\-down list$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I choose an empty item in the drop\-down list$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I was redirected to a State\/District login page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^it is not my State\/District login page$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the browser Back button$/ do
  pending # express the regexp above with the code you wish you had
end

After do |scenario|
  #@driver.quit if @driver
end