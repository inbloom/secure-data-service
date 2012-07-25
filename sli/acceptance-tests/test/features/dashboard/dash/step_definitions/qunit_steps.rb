
require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"


Then /^I check to see if there are any qunit failures$/ do
  fail_span = @driver.find_element(:id, "qunit-testresult")
  num_failed_span = fail_span.find_element(:class, "failed")

  assert(num_failed_span.text == '0')
end


When /^I navigate to qunit results page$/ do
  x = File.expand_path(File.dirname(__FILE__))
  y = 'file://' + x + '../../../../../../../dashboard/src/test/js/Test.SLC.dashboard.html'
  @driver.get y
  # Setting an explicit timeout for elements that may take a long time to load
  # We should be able to get rid of this and reply on the implicitWait
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 10)  
end