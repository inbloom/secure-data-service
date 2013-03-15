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
the large list of edorgs is loaded
=end


require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

# When /^I hit the Application Registration Tool URL$/ do
#   @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
# end
Given /^the large list of edorgs is loaded$/ do
  file = "#{File.dirname(__FILE__)}/edorgs/edorgs.json"
  status = system("mongoimport --drop -c educationOrganization -d #{convertTenantIdToDbName('Midgar')} --file #{file}")
  assert(status, "#{$?}")
  # Re-index edorg collection after drop
  index_rb_loc = "#{File.dirname(__FILE__)}/../../../../../config/scripts/indexTenantDb.rb"
  status = system("ruby #{index_rb_loc} localhost #{convertTenantIdToDbName('Midgar')}")
  assert(status, "#{$?}")
end

When /^I select the "(.*?)"$/ do |arg1|
  select = @driver.find_element(:css, 'div#state-menu select')
  select.find_element(:xpath, "//option[contains(text(),'#{arg1}')]").click
end

Then /^I see all of the pages of Districts$/ do
  assertWithWait("Should be more than one page of districts") {@driver.find_elements(:css, 'div#smartpager ul li').count > 1}
end

When /^I enable the first page of Districts$/ do
  step "I check the Districts"
end

Then /^the first page of districts are enabled$/ do
  visible_count = 0
  @driver.find_elements(:css, '#lea-table tr').each {|element| visible_count += 1 if element.displayed?}
  selected_count = 0
  @driver.find_elements(:css, '#lea-table tr input:checked').each {|element| selected_count += 1 if element.displayed?} 
  assert(visible_count == selected_count, "#{visible_count} total visible elemens should equal #{selected_count} selected elements")
end

When /^I click to the last page$/ do
  button = @driver.find_element(:xpath, '//div[contains(text(), "Last")]')
  button.click 
end

When /^I enable the last page of Districts$/ do
  step 'I check the Districts'
end

Then /^the last page of districts are enabled$/ do
  step 'the first page of districts are enabled'
end

When /^I click on the first page of Districts$/ do
  button = @driver.find_element(:xpath, '//div[contains(text(), "First")]')
  button.click 
end

Given /^I have replaced the edorg data$/ do
  file = "#{File.dirname(__FILE__)}/../../../data/Midgar_data/educationOrganization_fixture.json"
  status = system("mongoimport --drop -c educationOrganization -d #{convertTenantIdToDbName('Midgar')} --file #{file}")
  assert(status, "#{$?}")
  # Re-index edorg collection after drop
  index_rb_loc = "#{File.dirname(__FILE__)}/../../../../../config/scripts/indexTenantDb.rb"
  status = system("ruby #{index_rb_loc} localhost #{convertTenantIdToDbName('Midgar')}")
  assert(status, "#{$?}")
end

Then /^I see the list of \(only\) my applications$/ do
  assert(@driver.find_elements(:xpath, "//tr").count > 0, "Should be more than one application listed")
end

Then /^I can see the on\-boarded states\/districts$/ do
  assert(@driver.find_elements(:css, 'div#enable-menu div#lea-menu input[type="checkbox"]').count > 1, "One district should exist already")
end

Then /^I can see the on\-boarded states$/ do
  found = false
  for attempt in 1..5
    if attempt > 1
      puts "Did not find any states in select menu.  Retrying..."
      sleep(2)
    end
    found = true if @driver.find_elements(:css, 'div#state-menu select option').count > 1
  end
  assert(found, "At least one state should exist")
end

When /^I select a state$/ do
  options = @driver.find_elements(:css, 'div#state-menu select option')
  step "I select the \"#{options[1].text}\""
end

Then /^I see all of the Districts$/ do
  lis = @driver.find_elements(:css, 'div#enable-menu div#lea-menu table tbody tr')
  assert(lis.count >= 1, "One district should exist")
end

Then /^I check the Districts$/ do
  @driver.find_element(:link_text, 'Enable All').click
end

Then /^I uncheck the Districts$/ do
  @driver.find_element(:link_text, 'Disable All').click
end

When /^I click on Save$/ do
  @driver.find_element(:css, 'input:enabled[type="submit"]').click
end

Then /^the "([^"]*)" is enabled for Districts$/ do |arg1|
  check_app(arg1)
end

Then /^I log out$/ do
  @driver.find_element(:link, "Sign out").click
  @driver.manage.delete_all_cookies
end

Then /^I log in as a valid SLI Operator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  #Empty step
end

Then /^I am redirected to the Application Registration Approval Tool page$/ do
  assert(@driver.page_source.index("Authorize Applications") != nil, "Should be at the Application Registration Approval Tool page")
end

Then /^I see the newly enabled application$/ do
  assert(!get_app.nil?, "App should exist")
end

Then /^I see the newly enabled application is approved$/ do
  test_app = get_app
  assert(!test_app.find_element(:xpath, "//td[text()='Approved']").nil?, "App should be approved")
end

Then /^I don't see the newly disabled application$/ do
  begin
    get_app
    fail("Shouldn't see app")
  rescue
    assert(true, "Should not find the app")
  end
end

private
def get_app(name="Testing App")
  @driver.find_element(:xpath, "//tr/td[text()='#{name}']/..")
end
def check_app(arg1)
  @test_app = get_app(arg1)
  total_count = @test_app.find_elements(:css, "input:checked[type='checkbox']").count
  # TODO: the code below is not right. X can never be equal to (X - 1)
  #district_count = total_count - 1
  #puts "total count = #{total_count}, district count = #{district_count}"
  #assert(total_count == district_count, "All districts should be enabled.")
end
