require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^My API is running on HTTPS$/ do
  apiUrl = PropLoader.getProps['api_server_url']
  assert("https" == apiUrl[0,5] , "Your API must be running SSL (https://) to run this test")
end

Given /^I was redirected to the "([^"]*)" IDP Login page$/ do |arg1|
  if arg1=="OpenAM"
    assertWithWait("Was not redirected to the IDP login page")  { @driver.find_element(:name, "Login.Submit") }
  elsif arg1=="ADFS"
    assertWithWait("Was not redirected to the IDP login page")  { @driver.find_element(:id, "ctl00_ContentPlaceHolder1_SubmitButton") }
  else
    raise "IDP type '#{arg1}' not implemented yet"
  end
end

