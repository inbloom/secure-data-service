require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

When /^I navigate to the Realm page URL$/ do
  @url = PropLoader.getProps['api_server_url']+"/disco/realms/list.do"
  @driver.get @url
end

Then /^I should be directed to the Realm page$/ do
  assert(@driver.title.index("Choose your realm") != nil, webdriverDebugMessage(@driver,"Failed to navigate to Realm chooser"))
end

Given /^I see the Realm page$/ do
  @driver = Selenium::WebDriver.for :firefox
  url = PropLoader.getProps['api_server_url']+"/disco/realms/list.do"
  @driver.get url
  assert(@driver.current_url == url, webdriverDebugMessage(@driver,"Failed to navigate to "+url))
end

When /^I choose realm "([^"]*)" in the drop\-down list$/ do |arg1|
  dropdownbox = @driver.find_element(:name, "realmId")
  dropdownbox.click
  dropdownbox.find_elements(:tag_name => "option").find do |option|
    option.text == arg1
  end.click
end

When /^I click on the page Go button$/ do
  wait = Selenium::WebDriver::Wait.new(:timeout => 1)
  begin
    wait.until { @driver.find_element(:id, "go") }
  rescue
  end
  @driver.find_element(:id, "go").click
end

Then /^I should be redirected to "([^"]*)" Realm Login page$/ do |arg1|
  assert(@driver.current_url.index("http://devdanil.slidev.org:8080/idp/SSORedirect/metaAlias/idp") != nil, webdriverDebugMessage(@driver,"Failed to navigate to IDP login page"))
end

Given /^a realm in the drop\-down list is not \(pre\)selected$/ do
  #No code needed, just don't click on anything
end

Then /^I should be notified that I must choose a realm$/ do
  assert(@driver.current_url.index("/disco/realms/") != nil, webdriverDebugMessage(@driver,"Navigated off the page when we should have stayed"))
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