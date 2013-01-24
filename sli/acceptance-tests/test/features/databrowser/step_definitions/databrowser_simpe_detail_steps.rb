=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^I navigated to the Data Browser Home URL$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Given /^I was redirected to the Realm page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

When /^I choose realm "([^"]*)" in the drop\-down list$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
end

Given /^I click on the realm page Go button$/ do
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

Given /^I was redirected to the SLI IDP Login page$/ do
  assertWithWait("Was not redirected to the IDP login page")  { @driver.find_element(:name, "Login.Submit") }
end

When /^I enter "([^"]*)" in the username text field$/ do |arg1|
  @driver.find_element(:id, "IDToken1").send_keys arg1
end

When /^I enter "([^"]*)" in the password text field$/ do |arg1|
  @driver.find_element(:id, "IDToken2").send_keys arg1
end

When /^I click the IDP page Go button$/ do
  @driver.find_element(:name, "Login.Submit").click
  begin
    @driver.switch_to.alert.accept
  rescue
  end

end

Then /^I should be redirected to the Data Browser home page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

Then /^I should see my available links labeled$/ do
  assertWithWait("Failed to find 'me' Link on page")  {@driver.find_element(:link_text, "Me")}
end

When /^I click on the Logout link$/ do

  # current logout functionaly means delete all the cookies
  @driver.manage.delete_all_cookies
  browser = PropLoader.getProps['browser'].downcase
  # cannot delete httponly cookie in IE
  if (browser == "ie")
    @driver.quit
    @driver = Selenium::WebDriver.for :ie
  end
  #@driver.find_element(:css, "a.menulink").click
  #@driver.find_element(:link, "Logout").click
end

Then /^I am redirected to a page that informs me that I have signed out$/ do
  assertWithWait("Failed to find message stating that sign off was successful") { @driver.page_source.downcase.index("successfully logged out") != nil }
end

Then /^I am forced to reauthenticate to access the databrowser$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
  assertWithWait("Was not redirected to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

Given /^I have navigated to the "([^"]*)" page of the Data Browser$/ do |arg1|
  @driver.get PropLoader.getProps['databrowser_server_url']
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
  assertWithWait("Failed to find table of associations")  {@driver.find_element(:id, "simple-table")}
end

Then /^those names include the IDs of both "([^"]*)" and "([^"]*)" in the association$/ do |arg1, arg2|
  assertWithWait("Failed to find Coulumn title of "+arg1)  {@driver.find_element(:xpath, "//th/span[contains(text(),'#{arg1}')]")}
  assertWithWait("Failed to find Coulumn title of "+arg2)  {@driver.find_element(:xpath, "//th/span[contains(text(),'#{arg2}')]")}
end

When /^I click on the row containing "([^"]*)"$/ do |arg1|
  @expandedRow = arg1
  assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
  @driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']").click  
end

Then /^the row expands below listing the rest of the attributes for the item$/ do
  assertWithWait("Failed to find expanded row")  {@driver.find_element(:css, "td.details")}
end

When /^I click on the expanded row$/ do
  assertWithWait("Failed to find row containing text: "+@expandedRow)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{@expandedRow}']")}
  @driver.find_element(:xpath, "//tr/td[normalize-space()='#{@expandedRow}']").click  
end

Then /^the row collapses hiding the additional attributes$/ do
  assertWithWait("Failed to find row collapsed row") do
      begin
        @driver.find_element(:css, "td.details")
        false
      rescue
        true
      end
    end
end

When /^I click on the "([^"]*)" of any of the associating entities$/ do |arg1|
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

Then /^I am redirected to a page that page lists all of the "([^"]*)" entity's fields$/ do |arg1|
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//div[text()='#{arg1}']")}
end

Then /^I am redirected to the particular associations Simple View$/ do
  assertWithWait("Failed to find table of associations")  {@driver.find_element(:id, "simple-table")}
end

Then /^I am redirected to the particular entity Detail View$/ do
  # Make sure you don't see the simple view
  assertWithWait("Failed to find table of associations")  {@driver.find_elements(:id, "simple-table").size == 0}
  
  # Then make sure you can see specific details of the entity
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//div[text()='Organization']")}
end

When /^I click on any of the entity IDs$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I have navigated to the <Page> of the Data Browser$/ do |table|
  table.hashes.each do |hash|
    @driver.get PropLoader.getProps['databrowser_server_url']
    # Wait for home page to load
    assertWithWait("Failed to find '"+hash["Page"]+"' Link on page")  {@driver.find_element(:link_text, hash["Page"])}
    @driver.find_element(:link_text, hash["Page"]).click
    
    assertWithWait("Failed to find 'Home' Link on page")  {@driver.find_element(:link_text, "data browser")}
    @driver.find_element(:link_text, "data browser").click

    assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}

  end
end

When /^I have navigated to the "(.*?)" listing of the Data Browser$/ do |arg1|
  @driver.get PropLoader.getProps['databrowser_server_url'] + '/entities/schools'
end

When /^I click on "(.*?)" in the list of schools$/ do |arg1|
  @row = @driver.find_element(:xpath, "//td[text()=\"#{arg1}\"]/..")
  @row.click
end

When /^then click on the "(.*?)" link$/ do |arg1|
  # 
  @driver.find_element(:xpath, "/html/body/div/div[2]/div/div/table/tbody/tr[22]/td/div/div[4]/ul/li[4]/a").click
end

When /^I search for the identifier "([^\"]*)" in "([^\"]*)"$/ do |id, type|
  # select search type
  select_list = @driver.find_element(:id, "search_type")
  drop_down = Selenium::WebDriver::Support::Select.new(select_list)
  drop_down.select_by(:value, type)
  # input search id
  @driver.find_element(:id, "search_id").clear
  @driver.find_element(:id, "search_id").send_keys id
  @driver.find_element(:xpath, "//input[@value=\"Search\"]").click
end

Then /^I should see "(.*?)" on the page$/ do |arg1|
  assertWithWait("Failed to find #{arg1} on the page.")  {@driver.page_source.include?(arg1)}
end

Then /^I should not see "(.*?)" on the page$/ do |arg1|
  assertWithWait("Found #{arg1} on the page when we shouldn't have.")  {!@driver.page_source.include?(arg1)}
end

Then /^I see a "(.*?)" alert box$/ do |arg1|
  flash = @driver.find_element(:css, 'div#alert')
  assert(!flash.nil?, "We should see an alert box")
end

Then /^I click the X$/ do
  @driver.find_element(:css, 'div#alert').find_element(:css, "button").click
  
end

Then /^the error is dismissed$/ do
  begin 
    @driver.find_element(:css, 'div#alert')
  rescue Selenium::WebDriver::Error::NoSuchElementError => e
    assert(true)
  else
    assert(false, "The error should be dismissed")
  end

end

Then /^I should click on the Home link and be redirected back$/ do
  #Ignored, should be verified in previous steps
end

Then /^I should be on the detailed page for an SEA$/ do
  assertWithWait("Failed to be directed to Databrowser's Page for an SEA")  {@driver.page_source.include?("State Education Agency")}
end

Then /^I should be on the detailed page for an LEA$/ do
  assertWithWait("Failed to be directed to Databrowser's Page for an SEA")  {@driver.page_source.include?("Local Education Agency")}
end
