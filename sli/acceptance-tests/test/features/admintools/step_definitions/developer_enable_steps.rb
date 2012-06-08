require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

# When /^I hit the Application Registration Tool URL$/ do
#   @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
# end

Then /^I see the list of \(only\) my applications$/ do
  assert("Should be more than one application listed", @driver.find_elements(:xpath, "//tr").count > 0)
end

Then /^I can see the on\-boarded states\/districts$/ do
  assert("One district should be enabled already", @driver.find_elements(:css, 'input:enabled[type="checkbox"]').count > 1)
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
  assert("Should be at the Application Registration Approval Tool page", @driver.page_source.index("Application Registration Approval") != nil)
end

Then /^I see the newly enabled application$/ do
  assert("App should exist", get_app)
end

Then /^I see the newly enabled application is approved$/ do
  test_app = get_app
  assert("App should be approved", !test_app.find_element(:xpath, "//td[text()='Approved']").nil?)
end

Then /^I don't see the newly disabled application$/ do
  begin
    get_app
    fail("Shouldn't see app")
  rescue
    assert("Should not find the app", true)
  end
end

private
def get_app(name="Testing App")
  @driver.find_element(:xpath, "//tr/td[text()='#{name}']/..")
end
def check_app(arg1)
  @test_app = get_app(arg1)
  total_count = @test_app.find_elements(:css, "input:checked[type='checkbox']").count
  district_count = total_count - 1
  assert("All districts should be enabled.", total_count == district_count)
end
