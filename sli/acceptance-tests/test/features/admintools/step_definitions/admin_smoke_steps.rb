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

# TODO Move the capybara setup code to a common location
class Browser
  include Capybara::DSL
  def initialize
    Capybara.default_driver = :selenium
    Capybara.reset_session!
  end
end

Given /^I have an open browser$/ do
  @browser = Browser.new
end

Given /^I am a valid inBloom developer$/ do
  @user = 'slcdeveloper'
  @pass = 'slcdeveloper1234'
end

Given /^I am a valid inBloom operator$/ do
  @user = 'slcoperator'
  @pass = 'slcoperator1234'
end

Given /^I am a valid tenant-level administrator$/ do
  @user = 'iladmin'
  @pass = 'iladmin1234'
end

Given /^I am a valid district-level administrator$/ do
  @user = 'sunsetadmin'
  @pass = 'sunsetadmin1234'
end

Given /^I am a valid realm administrator$/ do
  @user = 'sunsetrealmadmin'
  @pass = 'sunsetrealmadmin1234'
end

Given /^I am managing my applications$/ do
  browser.visit admin_apps_page
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => /(Manage|Authorize) Applications/)
end

Given /^I am managing my application authorizations$/ do
  browser.visit application_authorizations_page
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => 'Approve Applications')
end

Given /^I am managing my realms$/ do
  browser.visit realm_management_page
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => /Realms for/)
end

Given /^I have an in\-progress application$/ do
  @app_row = browser.page.first('table#applications tr', :text => /#{app_prefix}.*In Progress/)
  @app_row.should_not be_nil
  @app_name = @app_row.find('td:nth-child(2)').text.strip
end

When /^I submit a new application for registration$/ do
  @app_name = "#{app_prefix}#{Time.now.to_i}"

  browser.page.click_link 'New Application'
  browser.page.should have_title('New Application')
  browser.page.fill_in('app[name]', :with => @app_name)

  fill_in_app_registration

  browser.page.click_button 'Register'
end

When /^I edit the (in\-progress )?application$/ do |in_progress|
  link = in_progress ? 'In Progress' : 'Edit'
  browser.within @app_row do
    browser.click_link link
  end
end

When /^I edit the authorizations for an (approved )?application$/ do |approved|
  status_match = approved ? 'EdOrg\(s\)' : 'Not Approved'
  app_row = browser.page.first('table#AuthorizedAppsTable tr', :text => /#{app_prefix}.*#{status_match}/)
  @app_name = app_row.find('td:nth-child(1)').text.strip
  browser.within app_row do
    browser.click_button 'Edit Authorizations'
  end
end

When /^I see a pending application$/ do
  @app_row = browser.page.first('table#applications tr', :text => /#{app_prefix}.*PENDING/)
  @app_row.should_not be_nil
  @app_name = @app_row.find('td:nth-child(1)').text
end

When /^I approve the pending application$/ do
  browser.within @app_row do
    browser.click_button 'Approve'
  end
end

When /^(?:I )?(enable|authorize|disable|de\-authorize) the application for (?:an|all) education organizations?$/ do |check|
  check = (check =~ /^(enable|authorize)$/) ? true : false
  browser.page.should have_selector('#edorgTree #root')
  browser.within '#edorgTree' do
    check ? browser.check('root') : browser.uncheck('root')
  end
  save_button = browser.page.first(:button, 'Save & Update')
  save_button.click
end

When /^I add a new realm$/ do
  browser.page.click_link 'Add new'
  browser.page.should have_selector('h1', :text => 'Manage Realm')
  @realm_name = "#{app_prefix}_realm_#{Time.now.to_i}"
  browser.fill_in('realm_name', :with => @realm_name)
  browser.fill_in('realm_idp_id', :with => 'http://www.example.com')
  browser.fill_in('realm_idp_redirectEndpoint', :with => 'http://www.example.com')
  browser.fill_in('realm_uniqueIdentifier', :with => @realm_name)
  browser.click_button 'Save'
end

Then /^I see the new realm listed$/ do
  browser.page.should have_selector('#notice', :text => 'Realm was successfully created.')
  browser.page.should have_selector('table#realms tr > td:nth-child(1)', :text => @realm_name)
end

Then /^the application should be not approved$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the application should get registered$/ do
  browser.page.should have_selector('h1', :text => 'Manage Applications')
  browser.page.should have_selector('#notice', :text => 'App was successfully created')
  browser.page.should have_selector('tbody > tr:nth-child(1) > td', :text => @app_name)
end

Then /^the application status should be pending$/ do
  pending = /pending/i

  # Verify that the 'Creation Date' shows 'Pending'
  browser.page.should have_selector('tbody > tr:nth-child(1) td:nth-child(5)', :text => pending)
  browser.page.find('tbody > tr:nth-child(1) > td:nth-child(2)').click

  # Verify that the 'Client ID' and 'Shared Secret' show 'Pending'
  browser.page.should have_selector('tbody > tr:nth-child(2) dd:nth-of-type(1)', :text => pending)
  browser.page.should have_selector('tbody > tr:nth-child(2) dd:nth-of-type(2)', :text => pending)
end

Then /^the application status should be approved$/ do
  browser.page.should have_selector('table#applications tr', :text => /#{@app_name}(.*)APPROVED/)
end

Then /^the application should be ready$/ do
  browser.page.should have_selector('table#applications tr', :text => /#{@app_name}(.*)Edit/)
end

Then /^the application should be approved for all education organizations$/ do
  app_row = browser.page.first('table#AuthorizedAppsTable tr', :text => /#{@app_name}/)
  app_row.should have_selector('td:nth-child(4)', :text => /\d+ EdOrg\(s\)/)
end

Then /^the application should not be approved$/ do
  app_row = browser.page.first('table#AuthorizedAppsTable tr', :text => /#{@app_name}/)
  app_row.should have_selector('td:nth-child(4)', :text => /Not Approved/)
end

# METHODS

def browser
  @browser
end

def app_prefix
  'smoke_test_'
end

def admin_apps_page
  "#{Property['admintools_server_url']}/apps/"
end

def application_authorizations_page
  "#{Property['admintools_server_url']}/application_authorizations/"
end

def realm_management_page
  "#{Property['admintools_server_url']}/realm_management/"
end

def login_to_the_inbloom_realm
  login_to_realm 'inBloom'
end

def login_to_the_tenants_realm
  login_to_realm 'Illinois Daybreak School District 4529'
end

def login_to_realm(realm)
  choose_realm realm
  submit_idp_credentials @user, @pass
end

def choose_realm(realm)
  browser.page.should have_title('Choose your realm')
  browser.select(realm, :from => 'realmId')
  browser.click_button 'Go'
end

def submit_idp_credentials(username, password)
  browser.fill_in('user_id', :with => username)
  browser.fill_in('password', :with => password)
  browser.click_button 'login_button'
end

def fill_in_app_registration
  browser.fill_in('app[description]', :with => 'smoke test')
  browser.fill_in('app[version]', :with => '0.9')
  browser.check('app[installed]')
end

def verify_registered_application(name)
  value = @driver.find_element(:id, 'notice').text
  assert(value =~ /successfully created/, "Should have valid flash message")
  assertWithWait("Couldn't locate #{app} at the top of the page") {@driver.find_element(:xpath, "//tbody/tr[1]/td[text()='#{app}']")}
end