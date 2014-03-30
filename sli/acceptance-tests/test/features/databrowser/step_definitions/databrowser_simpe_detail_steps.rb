=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

When /^I click the IDP page Go button$/ do
  @driver.find_element(:name, "Login.Submit").click
  begin
    @driver.switch_to.alert.accept
  rescue
  end

end

Then /^I should see my available links labeled$/ do
  assertWithWait("Failed to find 'me' Link on page")  {@driver.find_element(:link_text, "Me")}
end

When /^I click on the Logout link$/ do

  # current logout functionaly means delete all the cookies
  @driver.manage.delete_all_cookies
  browser = Property['browser'].downcase
  # cannot delete httponly cookie in IE
  if (browser == "ie")
    @driver.quit
    @driver = Selenium::WebDriver.for :ie
  end
  #@driver.find_element(:css, "a.menulink").click
  #@driver.find_element(:link, "Logout").click
end

Then /^I am forced to reauthenticate to access the databrowser$/ do
  @driver.get Property['databrowser_server_url']
  assertWithWait("Was not redirected to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

Given /^I have navigated to the "([^"]*)" page of the Data Browser$/ do |arg1|
  @driver.get Property['databrowser_server_url']
  # Wait for home page to load
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

When /^I click on the "([^"]*)" link$/ do |arg1|
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

Then /^I am redirected to the educationOrganization page$/ do
  assertWithWait("Failed to be directed to educationOrganization page")  {@driver.page_source.include?("educationOrganization")}
end

Then /^I see the properties in the following <Order>$/ do |table|
  count = 1
  table.hashes.each do |hash|
    assertWithWait("Failed to find '"+hash["Order"]+"' property where expected")  {@driver.find_element(:xpath, "(//div[contains(concat(' ',normalize-space(@class),' '),' key ')])[#{count}]").text == hash["Order"] + ':'}
    count = count + 1
  end
end

Then /^I see "([^"]*)" last$/ do |arg1|
  assertWithWait("The '"+arg1+"' property was not listed last.") {@driver.find_element(:xpath, "(//div[contains(concat(' ',normalize-space(@class),' '),' key ')])[last()]").text == arg1 + ":"}
end

Then /^I see the list of "([^"]+)" in alphabetical order$/ do |arg1|
  realOrder = Array.new
  count = 1
  #TODO Find better way to get all the elements of the Links list.
  #TODO Find better way to assert that list of links is alphabetized also.
  while count < 21 do
    realOrder.push(@driver.find_element(:xpath, "(//div/ul/li/a)[#{count}]").text.downcase)
    count = count + 1
  end
  alphaOrder = realOrder.clone.sort
  assertWithWait("#{arg1} not in alphabetical order.") {alphaOrder == realOrder}
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
    @driver.get Property['databrowser_server_url']
    # Wait for home page to load
    assertWithWait("Failed to find '"+hash["Page"]+"' Link on page")  {@driver.find_element(:link_text, hash["Page"])}
    @driver.find_element(:link_text, hash["Page"]).click
    
    assertWithWait("Failed to find 'Home' Link on page")  {@driver.find_element(:link_text, "data browser")}
    @driver.find_element(:link_text, "data browser").click

    assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Welcome to the inBloom, inc. Data Browser")}

  end
end

When /^I have navigated to the "(.*?)" listing of the Data Browser$/ do |arg1|
  @driver.get Property['databrowser_server_url'] + '/entities/schools'
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

Then /^I can see my "(.*?)" is "(.*?)"$/ do |field, value|
  # Far more complicated then it needs to be because databrowser seems to randomly choose different formats for fields and values.
  found_value = nil
  begin
    found_value = @driver.find_element(:xpath, "//div[text()=\"#{field}:\"]/following-sibling::div[1]").text
  rescue
    begin
      found_value = @driver.find_element(:xpath, "//div/span[text()=\"#{field}\"]/../following-sibling::div[1]").text
    rescue => e
      puts "Could not find #{field} = #{value}"
      raise e
    end
  end
  assert(value == found_value, "Did not find #{field} equals #{value}.  Found #{found_value} instead.")
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

Then /^I should go to the "([^"]*)" page and look for the EdOrg table with a "([^"]*)" result$/ do | arg1, arg2 |
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
  # errors = @driver.find_elements(:class_name, "edOrg")
  begin
    @driver.find_element(:xpath, "//table[contains(@class, 'edOrg')]") 
    if arg2 == "Pass"
      assert(true)
    else
      assert(false, "There should NOT be an EdOrg table on this page")
    end
  rescue Selenium::WebDriver::Error::NoSuchElementError => e
    if arg2 == "Pass"
      assert(false, "There should be an EdOrg table on this page")
    else
      assert(true)
    end
  end
end

# this tests the current breadcrumb trail text for equivalence to the given value (case sig)
Then /^I should see a breadcrumbtrail of "(.*?)"$/ do |crumb|
  begin
    # @driver.get PropLoader.getProps['databrowser_server_url']
    bctValue = @driver.find_element(:class_name, "breadcrumb").text()
    puts "got breadcrumbtrail value of #{bctValue}"
    assert(crumb == bctValue, "breadcrumbtrail #{bctValue}, not #{crumb}")
  end
end

# this looks for a link with the given text and clicks on it
Then /^I click on the link "(.*?)"$/ do |link|
  begin
    link = @driver.find_element(:link_text, "#{link}")
    link.click()
  end
end

And /^I should see a row containing "([^"]*)"$/ do |arg1|
  assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
end

And /^I should NOT see a row containing "([^"]*)"$/ do |arg1|
  begin
    assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
    assert(false)
  rescue => e
    assert(true)
  end
end

Then /^I should navigate to <Link> and see columns in the correct order$/ do |table|
  table.hashes.each do |hash|
    steps %Q{
      Then I should navigate to #{hash['Link']}
    }
    assertWithWait("Failed to find the Last Name column")  {@driver.find_element(:xpath, "//table[@id='simple-table']//thead/tr/th[2][contains(text(), 'Last Name')]")}
    assertWithWait("Failed to find the First Name column")  {@driver.find_element(:xpath, "//table[@id='simple-table']//thead/tr/th[3][contains(text(), 'First Name')]")}
    assertWithWait("Failed to find the State Id column")  {@driver.find_element(:xpath, "//table[@id='simple-table']//thead/tr/th[4][contains(text(), 'State ID')]")}
    assertWithWait("Failed to find the ID column")  {@driver.find_element(:xpath, "//table[@id='simple-table']//thead/tr/th[5][contains(text(), 'Id')]")}
  end
end

Then /^I should see a count of <Total> for id <ID> staff total and <Current> for current$/ do |table|
  table.hashes.each do |hash|
    assertWithWait("Failed to find the appropriate total count text") { @driver.find_element(:xpath, "//table[@id='edorgcounts_#{hash['ID']}']//tr[1]/td[2][contains(text(), '#{hash['Total']}')]") }
    assertWithWait("Failed to find the appropriate total count text") { @driver.find_element(:xpath, "//table[@id='edorgcounts_#{hash['ID']}']//tr[1]/td[3][contains(text(), '#{hash['Current']}')]") }
  end
end

Then /^I should see a count of "([^"]*)" for id "([^"]*)" staff total and "([^"]*)" for current$/ do |arg1, arg2, arg3|
  assertWithWait("Failed to find the appropriate total count text") { @driver.find_element(:xpath, "//table[@id='edorgcounts_#{arg2}']//tr[1]/td[2][contains(text(), '#{arg1}')]") }
  assertWithWait("Failed to find the appropriate total count text") { @driver.find_element(:xpath, "//table[@id='edorgcounts_#{arg2}']//tr[1]/td[3][contains(text(), '#{arg3}')]") }
end

Then /^I should click on the <Number> link pound and get <Text> returned$/ do |table|
  table.hashes.each do |hash|
    elements = @driver.find_elements(:xpath, "//span[@class='count_link']")
    count = 0
    elements.each do |element|
      if (count == hash['Number'].to_i)
        element.click
        sleep(1)
        assert(element.text == hash['Text'])
        break
      end
      count = count + 1
    end
  end
end
#When /^I have navigated to the "(.*?)" page of the Data Browser as user "(.*?)" with edorg "(.*?)"$/ do |arg1, arg2, arg3|

# When /^I have navigated to the <Page> page of the Data Browser as user <User> with edorg <EdOrg>$/ do |table|
#   p arg1
#   p arg2
#   p table
#   table.hashes.each do |hash|
#     @driver.get Property['databrowser_server_url']
#     # Wait for home page to load
#     # Navigate to edorg
#     assertWithWait("Failed to find '"+hash['EdOrg']+"' Link on page")  {@driver.find_element(:link_text, hash['EdOrg'])}
#     @driver.find_element(:link_text, hash['EdOrg']).click
#     # Navigate to Staff
#     assertWithWait("Failed to find Staff Link on page")  {@driver.find_element(:link_text, 'Staff')}
#     @driver.find_element(:link_text, 'Staff').click
#     #Click to expand self on staff list
#     assertWithWait("Failed to find row containing text: "+hash['User'])  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{hash['User']}']")}
#     @driver.find_element(:xpath, "//tr/td[normalize-space()='#{hash['User']}']").click
     
#      assertWithWait("Failed to find '"+hash["Page"]+"' Link on page")  {@driver.find_element(:link_text, hash["Page"])}
#      @driver.find_element(:link_text, hash["Page"]).click
    
#      assertWithWait("Failed to find 'Home' Link on page")  {@driver.find_element(:link_text, "home")}
#      @driver.find_element(:link_text, "data browser").click

#      #assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Welcome to the inBloom, inc. Data Browser")}

#   end
# end
And /^I navigate to myself as user "([^"]*)" of edorg "([^"]*)"$/ do |arg1, arg2|
    assertWithWait("Failed to find '"+arg2+"' Link on page")  {@driver.find_element(:link_text, arg2)}
    @driver.find_element(:link_text, arg2).click
    # Navigate to Staff
    assertWithWait("Failed to find Staff Link on page")  {@driver.find_element(:link_text, 'Staff')}
    @driver.find_element(:link_text, 'Staff').click
    begin
      @driver.find_element(:id, 'simple-table')
      assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
      @driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']").click
    rescue
    end
end
And /^I navigate to myself as user "([^"]*)" of type "([^"]*)" and edorg "([^"]*)"$/ do |arg1, arg2, arg3|
    assertWithWait("Failed to find '"+arg3+"' Link on page")  {@driver.find_element(:link_text, arg3)}
    @driver.find_element(:link_text, arg3).click
    # Navigate to user collection
    assertWithWait("Failed to find #{arg2} Link on page")  {@driver.find_element(:link_text, arg2)}
    @driver.find_element(:link_text, arg2).click
    begin
      @driver.find_element(:id, 'simple-table')
      assertWithWait("Failed to find row containing text: "+arg1)  {@driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']")}
      @driver.find_element(:xpath, "//tr/td[normalize-space()='#{arg1}']").click
    rescue
    end
end