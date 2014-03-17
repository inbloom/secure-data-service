=begin

Copyright 2012-2014 inBloom, Inc. and its affiliates.

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

require_relative 'capybara_setup.rb'
require 'selenium-webdriver'

require 'pry'

When /^I navigate to the Custom Role Mapping page$/ do
  browser.visit path_for('')
  login_to_the_inbloom_realm

  browser.page.should have_link("Custom Roles")
end

Then /^I should get to the correct Custom Role Mapping Page$/ do
  browser.page.click_link 'Custom Roles'
  # Click on the Custom Roles for item 'Illinois Daybreak School District 4529'
  browser.page.find(:xpath, "//tr[td[contains(.,'Illinois Daybreak School District 4529')]]/td/a", :text => 'Custom Roles').click

  browser.page.should have_text('Custom Roles for Illinois Daybreak School District 4529')
  browser.page.should have_button('resetToDefaultsButton')
end

When /^I click on the Reset to Defaults button$/ do
  browser.page.click_button('resetToDefaultsButton')

  browser.alert_popup_message.should eql('Resetting to default roles will remove any existing role mapping and will restore the default roles.  This operation cannot be undone.  Are you sure you want to reset?')
  browser.confirm_popup
end

Then /^The page should be reset back to default$/ do
  # Check for correct default group titles, title, and text

  # Check IT Administrator is the only one with Admin Role
  #binding.pry
end