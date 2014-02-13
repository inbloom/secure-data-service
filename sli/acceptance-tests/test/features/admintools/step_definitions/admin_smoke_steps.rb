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

require 'selenium-webdriver'
require 'json'

require_relative '../../utils/sli_utils.rb'
require_relative '../../security/step_definitions/securityevent_util_steps.rb'
require_relative '../../sandbox/AccountApproval/step_definitions/prod_sandbox_AccountApproval_Inteface_steps.rb'

# TODO Move the capybara setup code to a common location
include Capybara::DSL
Capybara.default_driver = :selenium

Given /^I am a valid SLC developer$/ do
  @user = 'slcdeveloper' # an :operator
  @pass = 'slcdeveloper1234'
end

Given /^I am a valid SLC operator$/ do
  @user = 'slcoperator-email@slidev.org' # an :operator
  @pass = 'test1234'
end

Given /^I am a valid realm administrator$/ do
  @user = 'sunsetrealmadmin' # an :operator
  @pass = 'sunsetrealmadmin1234'
end


Given /^I am a valid district administrator$/ do
  @user = 'sunsetadmin'
  @pass = 'sunsetadmin1234'
end

Given /^I am a valid SEA administrator$/ do
  @user = 'iladmin'
  @pass = 'iladmin1234'
end

When /^I authenticate on the realm editing tool$/ do
  step "I hit the realm editing URL"
  step "I login"
end

When /^I authenticate on the Application Registration Tool$/ do
  step "I hit the Application Registration Tool URL"
  step "I login"
end

When /^I authenticate on the Admin Delegation Tool$/ do 
  step "I hit the delegation url"
  step "I login"
end

# HERE BELOW LIES NEW IMPROVED STEPDEFS

Given /^I am a valid inBloom developer$/ do
  @user = 'slcdeveloper' # an :operator
  @pass = 'slcdeveloper1234'
end

Given /^I am a valid inBloom operator$/ do
  @user = 'slcoperator' # an :operator
  @pass = 'slcoperator1234'
end


Given /^I am managing my applications$/ do
  Capybara.reset_session!
  ##step 'I authenticate on the Application Registration Tool'
  #@driver.get(Property['admintools_server_url']+"/apps/")
  visit admin_apps_page
  login_to_the_inbloom_realm

  ##step 'I see the list of my registered applications only'
  page.should have_selector('#applications')
  page.should have_selector('tbody > tr')

  #appsTable = @driver.find_element(:id, "applications")
  #trs = appsTable.find_elements(:xpath, ".//tbody/tr")
  #assert(trs.length > 0, "Should see at least one of my apps")
end

When /^I submit a new application for registration$/ do
  @app_name = "smoke_test_#{Time.now.to_i}"

  page.click_link 'New Application'
  page.should have_title('New Application')
  page.fill_in('app[name]', :with => @app_name)

  fill_in_app_registration

  page.click_button 'Register'
end

Then /^the application should get registered$/ do
  page.should have_selector('h1', :text => 'Manage Applications')
  page.should have_selector('#notice', :text => 'App was successfully created')
  page.should have_selector('tbody > tr:nth-child(1) > td', :text => @app_name)
end

Then /^the application status should be pending$/ do
  pending = /pending/i

  # Verify that the 'Creation Date' shows 'Pending'
  page.should have_selector('tbody > tr:nth-child(1) td:nth-child(5)', :text => pending)
  page.find('tbody > tr:nth-child(1) > td:nth-child(2)').click

  # Verify that the 'Client ID' and 'Shared Secret' show 'Pending'
  page.should have_selector('tbody > tr:nth-child(2) dd:nth-of-type(1)', :text => pending)
  page.should have_selector('tbody > tr:nth-child(2) dd:nth-of-type(2)', :text => pending)
end

When /^I see a pending application$/ do
  @pending_app_row = page.first('table#applications tr', :text => /#{@app_name}(.*)PENDING/)
  @pending_app_row.should_not be_nil
end

And /^I approve the pending application$/ do
  within @pending_app_row do
    click_button 'Approve'
  end
end

Then /^the application status should be approved$/ do
  page.should have_selector('table#applications tr', :text => /#{@app_name}(.*)APPROVED/)
end

# METHODS

def admin_apps_page
  "#{Property['admintools_server_url']}/apps/"
end

def login_to_the_inbloom_realm
  login_to_realm 'inBloom'
end

def login_to_realm(realm)
  choose_realm realm
  submit_idp_credentials @user, @pass
end

def choose_realm(realm)
  page.should have_title('Choose your realm')
  page.select(realm, :from => 'realmId')
  page.click_button 'Go'
end

def submit_idp_credentials(username, password)
  page.fill_in('user_id', :with => username)
  page.fill_in('password', :with => password)
  page.click_button 'login_button'
end

def fill_in_app_registration
  page.fill_in('app[description]', :with => 'smoke test')
  page.fill_in('app[version]', :with => '0.9')
  page.check('app[installed]')
end

def verify_registered_application(name)
  value = @driver.find_element(:id, 'notice').text
  assert(value =~ /successfully created/, "Should have valid flash message")
  assertWithWait("Couldn't locate #{app} at the top of the page") {@driver.find_element(:xpath, "//tbody/tr[1]/td[text()='#{app}']")}

end