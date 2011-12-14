require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I go to the databrowser$/ do
  @url = "https://"+PropLoader.getProps['databrowser_server_url']+"/databrowser"
  @driver = Selenium::WebDriver.for :firefox
  @driver.get @url 
  assert(@driver.current_url == @url, "Failed to navigate to "+@url)
end

Given /^I go to School Menu page$/ do
  @driver.find_element(:id, "navSchoolMenu").click
  #assert(driver.current_url == @url+"/schools/", "Failed to navigate to the School Menu Page")
end

Then /^there should be Total Schools "([^"]*)"$/ do |arg1|
  numSchools = @driver.find_element(:id, "numSchools").text
  assert(numSchools == arg1, "NumSchools found ("+numSchools+") differs from expected value of "+arg1+".")
end

When /^I enter the name "([^"]*)"$/ do |arg1|
  @driver.find_element(:id, "schoolfullname").send_keys arg1
end

When /^click the Add School link$/ do
  @driver.find_element(:id, "addNewSchool").click
end

Then /^"([^"]*)" should appear on the list school page$/ do |arg1|
  assert(@driver.page_source.include?(arg1), "School name "+arg1+" not found on page")
end

When /^I update the school "([^"]*)" with "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:xpath, "//td[@class='schoolfullname']/input[@value='"+arg1+"']").send_keys arg2
end

When /^I click the Delete School link for "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[@value='"+arg1+"']/../following-sibling::td/a[contains(@onclick, 'delete_click')]").click
end

Then /^"([^"]*)" should not appear on the list school page$/ do |arg1|
  assert(@driver.page_source.index(arg1) == nil, "School name "+arg1+" was found on page in a negative test")
end

After do |scenario|
  @driver.quit if @driver
end