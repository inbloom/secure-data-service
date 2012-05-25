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
  @driver.find_element(:link, "Logout").click
end

Then /^I am redirected to a page that informs me that I have signed out$/ do
  assertWithWait("Failed to find message stating that sign off was successful") { @driver.page_source.downcase.index("success") != nil }
  assertWithWait("Failed to find message stating that sign off was successful") { @driver.page_source.downcase.index("signed off") != nil }
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
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//dd[text()='#{arg1}']")}
end

Then /^I am redirected to the particular associations Simple View$/ do
  assertWithWait("Failed to find table of associations")  {@driver.find_element(:id, "simple-table")}
end

Then /^I am redirected to the particular entity Detail View$/ do
  # Make sure you don't see the simple view
  assertWithWait("Failed to find table of associations")  {@driver.find_elements(:id, "simple-table").size == 0}
  
  # Then make sure you can see specific details of the entity
  assertWithWait("Failed to find entity details")  {@driver.find_element(:xpath, "//dd[text()='Fry High School']")}
end

When /^I click on any of the entity IDs$/ do
  pending # express the regexp above with the code you wish you had
end