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
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

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

Then /^the page should be reset back to default$/ do
  numChecked = 0

  # Check for correct default group titles should be the same as titles
  ["Educator","Leader","Aggregate Viewer","IT Administrator", "Student", "Parent"].each do |role|
    results = browser.find(:xpath, "//td/div[text()='#{role}']")
    moreResults = browser.find(:xpath, "//td/div/span[text()='#{role}']")

    results.text.should eql(moreResults.text)
  end

  # Check IT Administrator is the only Admin Role
  browser.all("input[type='checkbox']").each do |checkbox|
    if checkbox.checked? == true
      numChecked += 1
    end
  end

  numChecked.should == 1

  browser.find(:xpath, '//div/table/tbody/tr[3]/td[4]/input').should be_checked
end

# This step might need to be refactored
When /^I add a new role group$/ do
  browser.click_button('Add Role Group')
  browser.fill_in('Enter group name', :with => 'Application Authorizer')
  browser.fill_in('addRoleInput', :with => 'Application Authorizer')
  browser.click_on('Add')
  browser.select('APP_AUTHORIZE', :from => 'addRightSelect')
  browser.click_on('addRightButton')
  browser.select('READ_GENERAL', :from => 'addSelfRightSelect')
  browser.click_on('addSelfRightButton')
  browser.click_on('Save')
end

# Possible refactor will be required
Then /^I should see the new role group$/ do
  browser.within '#custom_roles' do
    @new_custom_role = browser.find(:xpath, '//div/table/tbody/tr[7]')

    browser.within @new_custom_role do
      browser.find('.groupTitle').text.should == 'Application Authorizer'
      browser.find('.role').text.should == 'Application Authorizer'
      browser.find('.right').text.should == 'APP_AUTHORIZE'
      browser.find('.self-right').text.should == 'READ_GENERAL'
    end
  end
end

And /^I create new application "([^"]*)"$/ do |app_name|
  fill_in_application_fields(:name => app_name,
                             :description => 'new application',
                             :version => '0.9',
                             :application_url => 'https://example.com',
                             :administration_url => 'https://example.com',
                             :redirect_uri => 'https://example.com',
                             :image_url => 'https://example.com')
  browser.check('app[installed]')
  browser.click_button 'Register'
end

Then /^application "([^"]*)" should be created$/ do |app_name|
  browser.page.should have_css('div.alert')
  browser.page.should have_content(app_name)
  browser.reset_session!
end

Then /^I should see all applications and new application "([^"]*)"$/ do |app_name|
  browser.should have_css('table#applications.table')
  @new_app_id = browser.find('table#applications tbody').first('tr')[:id]
  @new_app = browser.find_by_id(@new_app_id)
  @new_app.should satisfy{|page| page.has_content?(app_name) && page.has_content?('PENDING')}
end

When /^the application "Boyne" is approved$/ do
  browser.within @new_app do
    browser.should have_css('.approve-button')
    browser.find('.approve-button').click
  end
end

Then /^the 'Approve' button is disabled for the application$/ do
  browser.within @new_app do
    browser.should have_css('.approve-button[disabled]')
  end
end

Then /^I should see application "([^"]*)" as (.*)$/ do |new_app, status|
  @my_new_app = browser.all('tr', :text => /#{new_app}.*#{status}/).first

  browser.within @my_new_app do
    browser.should have_text(new_app)
    browser.should have_text(status)
  end
end

When /^I enable the education Organizations for the application$/ do
  browser.within @my_new_app do
    browser.find('.btn', text: 'In Progress').click
  end
  browser.click_link('Expand All')

  # check the checkbox for Illinois State Board of Education
  browser.check('b1bd3db6-d020-4651-b1b8-a8dba688d9e1')
  # check the checkbox for New York State Education System
  browser.check('87d0ab29-b493-46eb-a6f3-110701953afb')

  browser.click_button 'Save & Update'
end

Then /^the application status should be "([^"]*)"$/ do |status|
  browser.find('#' + @app_id).all(:xpath, ".//../..").first.should have_text(status)
end


And /^I navigate to the application authorization page$/ do
  browser.visit path_for('')
  login_to_the_tenants_realm
  browser.should have_link('Application Authorizations')

  browser.click_on('Application Authorizations')
  browser.should have_selector('h1', :text=> "Approve Applications")
end

When /^I authorize the application for "([^"]*)"$/ do |edOrg|
  browser.within @my_new_app do
    @app_id = browser.find('td form')[:id]
    browser.find('td form input').click
  end
  if edOrg == 'Illinois State Board of Education'
    browser.find('#hierarchical_mode').set(false)
  end

  browser.find(:xpath, '//form/div[2]/div/div/ul/li/ul/li/ul/li/input').set(true)
  browser.find(:xpath, '//form/div[2]/div/input[3]').click
end

And /^I go to application authorization page$/ do
  browser.visit path_for('application authorizations')
  login_to_the_tenants_realm
end

Then /^I should see the error message$/ do
  browser.should have_css('.alert')
  browser.should have_text("Sorry, you don't have access to this page. if you feel like you are getting this message in error, please contact your administrator.")
end

When /^I de-authorize the application for "([^"]*)"$/ do |edOrg|
  browser.find('#' + @app_id).find('input').click
  browser.find(:xpath, '//form/div[2]/div/div/ul/li/ul/li/ul/li/input').set(false)
  browser.find(:xpath, '//form/div[2]/div/input[3]').click
end