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

# When /^I hit the Application Registration Tool URL$/ do
#   @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
# end

Then /^I see the list of \(only\) my applications$/ do
  assert(@driver.find_elements(:xpath, "//tr").count > 0, "Should be more than one application listed")
end

Then /^I can see the on\-boarded states\/districts$/ do
  assert(@driver.find_elements(:css, 'div#enable-menu div#lea-menu input:enabled[type="checkbox"]').count > 1, "One district should be enabled already")
end

Then /^I can see the on\-boarded states$/ do
  assert(@driver.find_elements(:css, 'div#enable-menu div#state-menu select option').count > 1, "At least one state should exist")
end

When /^I select a state$/ do
  select = @driver.find_element(:css, 'div#enable-menu div#state-menu select')
  select.find_element(:xpath, "//option[contains(text(),'Illinois')]").click
end

Then /^I see all of the Districts$/ do
  lis = @driver.find_elements(:css, 'div#enable-menu div#lea-menu ul li')
  assert(lis.count > 1, "One district should exist")
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
  assert(@driver.page_source.index("Application Registration Approval") != nil, "Should be at the Application Registration Approval Tool page")
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
