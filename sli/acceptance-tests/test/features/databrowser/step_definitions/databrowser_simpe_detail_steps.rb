require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^I am authenticated to SLI IDP as user "([^"]*)" with pass "([^"]*)"$/ do |arg1, arg2|
  url = PropLoader.getProps['sli_idp_server_url']+"/UI/Login"
  @driver.get url
  @driver.find_element(:id, "IDToken1").send_keys arg1
  @driver.find_element(:id, "IDToken2").send_keys arg2
  @driver.find_element(:name, "Login.Submit").click
end

When /^I navigate to the Data Browser Home URL$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Then /^I should be redirected to the Data Browser home page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

Then /^I should see my available links labeled$/ do
  assertWithWait("Failed to find 'me' Link on page")  {@driver.find_element(:link_text, "Me")}
end

Given /^I have navigated to any page of the Data Browser$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the Logout link$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to a page that informs me that I have signed out$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am no longer authenticated to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I have navigated to the "([^"]*)" page of the Data Browser$/ do |arg1|
  @driver.get PropLoader.getProps['admintools_server_url']
  # Wait for home page to load
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

When /^I click on the "([^"]*)" link$/ do |arg1|
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

Then /^I am redirected to the associations list page$/ do
  assertWithWait("Failed to be directed to association page")  {@driver.page_source.include?("Associations")}
end

Then /^I see a table displaying the associations in a list$/ do
  assertWithWait("Failed to find table of associations")  {@driver.find_element(:id, "simple")}
end

Then /^those names include the IDs of both "([^"]*)" and "([^"]*)" in the association$/ do |arg1, arg2|
  assertWithWait("Failed to find Coulumn title of "+arg1)  {@driver.find_element(:xpath, "//th/span[text()='#{arg1}']")}
  assertWithWait("Failed to find Coulumn title of "+arg2)  {@driver.find_element(:xpath, "//th/span[text()='#{arg2}']")}
end

When /^I click on the row containing "([^"]*)"$/ do |arg1|
  @expandedRow = arg1
  assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
  @driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']").click  
end

Then /^the row expands below listing the rest of the attributes for the item$/ do
  assertWithWait("Failed to find expanded row")  {@driver.find_element(:xpath, "//tr[contains(@style,'display: table-row')]//dd[text()='Independent study']")}
end

When /^I click on the expanded row$/ do
  assertWithWait("Failed to find row containing text: "+@expandedRow)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{@expandedRow}']")}
  @driver.find_element(:xpath, "//tr/td[normalize-space()='#{@expandedRow}']").click  
end

Then /^the row collapses hiding the additional attributes$/ do
  assertWithWait("Failed to find row collapsed row")  {@driver.find_element(:xpath, "//tr[contains(@style,'display: none')]//dd[text()='#{@expandedRow}']")}
end

When /^I click on the "([^"]*)" of any of the associating entities$/ do |arg1|
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

Then /^I am redirected to a page that page lists all of the "([^"]*)" entity's fields$/ do |arg1|
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//dd[text()='#{arg1}']")}
end

Then /^I am redirected to the particular associations Simple View$/ do
  assertWithWait("Failed to find table of associations")  {@driver.find_element(:id, "simple")}
end

Then /^I am redirected to the particular entity Detail View$/ do
  # Make sure you don't see the simple view
  assertWithWait("Failed to find table of associations")  {@driver.find_elements(:id, "simple").size == 0}
  
  # Then make sure you can see specific details of the entity
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//dd[text()='Fry High School']")}
end

When /^I click on any of the entity IDs$/ do
  pending # express the regexp above with the code you wish you had
end
