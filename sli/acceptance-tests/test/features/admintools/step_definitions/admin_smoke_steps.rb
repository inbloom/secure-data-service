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

require 'capybara'
require 'capybara-screenshot'
require 'capybara-screenshot/cucumber'
#require_relative '../../utils/db_client.rb'

Capybara.default_driver = :selenium

# TODO Move the capybara setup code to a common location
class Browser
  include Capybara::DSL
  def initialize
    Capybara.reset_session!
  end

  def reset_session!
    Capybara.reset_session!
  end

  def confirm_popup
    page.driver.browser.switch_to.alert.accept
  end

  def dismiss_popup
    page.driver.browser.switch_to.alert.dismiss
  end
end

Before('@track_entities') do
  @created_entities = []
end

def add_for_cleanup(collection, name)
  (@created_entities ||= []) << [collection, name]
end

After('@track_entities') do
  db_client = DbClient.new.for_sli
  @created_entities.each do |collection, name|
    db_client.remove(collection.to_s, {'body.name' => name})
  end
  db_client.close
end

Given /^I have an open browser$/ do
  @browser = Browser.new
end

Given /^I am a valid (.*)$/ do |user_type|
  @user, @pass = valid_user user_type
  @user.should_not be_nil
  @federated = !!(user_type =~ /federated/)
  @sandbox = !!(user_type =~ /sandbox/)
end

def valid_user(user_type)
  valid_users = {
      'inBloom developer'            => %w( slcdeveloper ),
      'inBloom operator'             => %w( slcoperator ),
      'tenant-level administrator'   => %w( iladmin ),
      'district-level administrator' => %w( sunsetadmin ),
      'realm administrator'          => %w( sunsetrealmadmin ),
      'federated district-level administrator' => %w( jstevenson ),
      'SLC Operator'                           => %w( slcoperator-email@slidev.org slcoperator-email1234 ),
      'Super Administrator'                    => %w( daybreaknorealmadmin ),
      'non-SLI hosted user with no roles' => %w( administrator ),
      'SLI hosted user with no roles' => %w( leader ),
      'tenant-level realm administrator' => %w( daybreakadmin ),
      'tenant-level IT administrator' => %w( rrogers ),
      'sandbox developer'             => %w( developer-email@slidev.org test1234 )
  }
  username, password = valid_users[user_type]
  [username, password || "#{username}1234"]
end

Given /^I am an unknown user$/ do
  @user, @pass = 'unknown_user','invalid_password'
end

Given /^I am managing my applications$/ do
  browser.visit path_for('apps')
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => /(Manage|Authorize) Applications/)
end

Given /^I want to create a new application$/ do
  browser.page.click_link 'New Application'
  browser.page.should have_title('New Application')
end

When /^I cancel out of the new application form$/ do
  browser.click_link 'Cancel'
end

Then /^I am managing my applications again$/ do
  browser.page.should have_selector('h1', :text => /(Manage|Authorize) Applications/)
end

Given /^I am managing my application authorizations$/ do
  browser.visit path_for('application authorizations')
  login_to_the_realm
  browser.page.should have_selector('h1', :text => 'Approve Applications')
end

When /^I attempt to manage application authorizations$/ do
  browser.visit path_for('application authorizations')
  login_to_the_realm
end

When /^I (?:attempt )?to go to the (.*) page$/ do |page|
  browser.visit path_for(page)
  puts "Attempting to go to page: #{url}"
  browser.visit url
  login_to_the_realm
end

Then /^I should (not )?be on the (.*) page$/ do |not_see, page|
  selector, header = 'h1', header_for(page)
  if not_see
    browser.page.should have_no_selector(selector, :text => header)
  else
    browser.page.should have_selector(selector, :text => header)
  end
end

Given /^I have an in\-progress application$/ do
  @app_row = browser.page.first('table#applications tr', :text => /#{app_prefix}.*In Progress/)
  @app_row.should_not be_nil
  @app_name = @app_row.find('td:nth-child(2)').text.strip
end

Given /^I have a deletable application$/ do
  @app_row = browser.page.first('table#applications tr', :text => /#{app_prefix}.*Delete/)
  @app_row.should_not be_nil
  @app_name = @app_row.find('td:nth-child(2)').text.strip
end

Given /^a developer has registered a new application$/ do
  @user, @pass = 'slcdeveloper', 'slcdeveloper1234'
  browser.visit admin_apps_page
  login_to_the_inbloom_realm
  register_new_application
  browser.reset_session!
end

When /^I attempt to manage applications$/ do
  browser.visit admin_apps_page
  login_to_the_inbloom_realm
end

When /^I delete the application$/ do
  @app_row = browser.page.first("table#applications tr", :text => /#{@app_name}/)
  @app_row.should_not be_nil
  browser.within @app_row do
    browser.click_link 'Delete'
    browser.confirm_popup
  end
  sleep 2 # allow ajax action to occur TODO: Replace with better implementation (e.g. wait on a flash message)
end

When /^I submit a new application for registration$/ do
  register_new_application
end

When /^I submit an application for registration without inputting any information$/ do
  submit_new_application
end

When /^I submit an application for registration marked as "Installed"$/ do
  submit_new_application do
    browser.check('app[installed]')
  end
end

When /^I submit an application for registration with improperly formatted URLs$/ do
  submit_new_application do
    fields = ['application_url', 'administration_url', 'redirect_uri', 'image_url']
    fill_in_application_fields fields.inject({}) {|hash, field| hash[field] = 'bad url'; hash}
  end
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

When /^I see (?:a|the|that) (pending|approved) application$/ do |status|
  @app_row = browser.page.first('table#applications tr', :text => /#{app_prefix}.*#{status.upcase}/)
  @app_row.should_not be_nil
  @app_name = @app_row.find('td:nth-child(1)').text
end

When /^I (approve|deny) the application$/ do |action|
  browser.within @app_row do
    browser.click_button action.capitalize
    browser.confirm_popup if action == 'deny'
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

Then /^I no longer see the application$/ do
  browser.page.should_not have_selector('table#applications tr', :text => /#{@app_name}/)
end

Then /^the application should get registered$/ do
  browser.page.should have_selector('#notice', :text => 'App was successfully created')
  browser.page.should have_selector('tbody > tr:nth-child(1) > td', :text => @app_name)
end

Then /^the application status should be (pending|approved)$/ do |status|
  @app_row = browser.page.first('table#applications tr', :text => /#{@app_name}.*#{status}/i)
  @app_row.should_not be_nil
end

Then /^the Client Id and Shared Secret should be set$/ do
  browser.within(@app_row) { browser.page.first('td').click  }
  browser.within("tr[id='#{@app_row[:id]}'] + tr") do # find the details row after the app's row
    browser.page.should have_selector('dd:nth-of-type(1)', :text => /[a-z0-9]{8,8}/i)   # client id
    browser.page.should have_selector('dd:nth-of-type(2)', :text => /[a-z0-9]{48,48}/i) # shared secret
  end
end

Then /^the application should be ready$/ do
  browser.page.should have_selector('table#applications tr:nth-child(1)', :text => /#{@app_name}(.*)Edit/)
end

Then /^the application should be approved for all education organizations$/ do
  app_row = browser.page.first('table#AuthorizedAppsTable tr', :text => /#{@app_name}/)
  app_row.should have_selector('td:nth-child(4)', :text => /\d+ EdOrg\(s\)/)
end

Then /^the application should not be approved$/ do
  app_row = browser.page.first('table#AuthorizedAppsTable tr', :text => /#{@app_name}/)
  app_row.should have_selector('td:nth-child(4)', :text => /Not Approved/)
end

Then /^I should not be allowed to access the page$/ do
  page_alerts_access_error
end

Then /^the (.*) should be notified$/ do |user_type|
  user, password = valid_user user_type
  email = "#{user}@slidev.org"

  ##TODO: When all these properties are gauranteed to be defined, the fallbacks should be removed
  #email    = Property['email_imap_inbloom_operator_email'] || 'slcoperator-email@slidev.org'
  #user     = Property['email_imap_inbloom_operator_username'] || email.split('@').first
  #password = Property['email_imap_inbloom_operator_password'] || "#{user}1234"

  subject, content = most_recent_email(email, user, password)

  subject.should include('New Application')
  content.should include(@app_name)
end

Then /^I am not authorized to access the following pages:$/ do |table|
  table.raw.each do |row|
    page = row.first
    browser.visit path_for(page)
    page_alerts_access_error
  end
end

# METHODS

def page_alerts_access_error
  browser.page.should have_selector('.alert-error', :text => /access to this page/)
end

# Get the subject and content of the most recent email for the given user
def most_recent_email(email, username=nil, password=nil)
  imap = Net::IMAP.new(
      Property['email_imap_host'],
      :port => Property['email_imap_port'],
      :ssl  => to_boolean(Property['email_imap_use_ssl'])
  )
  imap.authenticate('LOGIN', user, password)
  imap.examine('INBOX')

  ids = imap.search(['TO', email])

  subject, content = nil, nil

  unless ids.empty?
    content_attr = 'BODY[TEXT]'
    subject_attr = 'BODY[HEADER.FIELDS (SUBJECT)]'
    email_data = imap.fetch(ids.last, [content_attr, subject_attr]).first
    content = email_data.attr[content_attr]
    subject = email_data.attr[subject_attr]
  end

  imap.disconnect unless imap.disconnected?

  [subject, content]
end

def browser
  @browser
end

def to_boolean(value)
  !!(value.to_s =~ /(true|yes|1)/i)
end

def app_prefix
  'smoke_test_'
end

def path_for(page)
  path = case page
         when /default administration/; ''
         when /applications/; 'apps'
         else
            page.gsub(' ','_')
         end
  "#{Property['admintools_server_url']}/#{path}"
end

def header_for(page)
  case page
    when /default administration/; 'Admin Tools'
    when /applications/; 'Applications'
    when /custom roles/; 'Custom Roles'
    else
      fail "Unexpected page: #{page}"
  end
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

def login_to_the_realm
  @federated ? login_to_the_tenants_realm : login_to_the_inbloom_realm
end

def login_to_the_inbloom_realm
  login_to_realm 'inBloom'
end

def login_to_the_tenants_realm
  login_to_realm 'Illinois Daybreak School District 4529'
end

def login_to_realm(realm)
  puts "Logging into realm: #{realm}"
  choose_realm realm unless @sandbox
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

def submit_new_application
  browser.page.click_link 'New Application'
  browser.page.should have_title('New Application')
  yield if block_given?
  browser.click_button 'Register'
end

def register_new_application
  @app_name = "#{app_prefix}#{Time.now.to_i}"
  submit_new_application do
    fill_in_application_fields(:name => @app_name, :description => 'smoke_test', :version => '0.9')
    browser.check('app[installed]')
  end
  browser.page.should have_selector('h1', :text => 'Manage Applications') # ensure completed registration
end

def fill_in_application_fields(values)
  values.each do |name, value|
    browser.fill_in("app[#{name}]", :with => value)
  end
end
