require 'securerandom'

require_relative '../../apiV1/integration/step_definitions/app_oauth.rb'

Given /^I am managing my realms$/ do
  browser.visit realm_management_page
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => /Realms for/)
end

When /^I add a new realm$/ do
  @realm_name = "#{app_prefix}realm_#{Time.now.to_i}"
  realm_url = "#{Property['simpleIDP_login_url']}?realm=IL-Daybreak"
  add_realm do
    browser.fill_in('realm_name', :with => @realm_name)
    browser.fill_in('realm_idp_id', :with => realm_url)
    browser.fill_in('realm_idp_redirectEndpoint', :with => realm_url)
    browser.fill_in('realm_uniqueIdentifier', :with => @realm_name)
  end
  add_for_cleanup(:realm, @realm_name)
end

When /^I edit that realm$/ do
  row = find_realm_row
  browser.within(row) { browser.click_link 'Edit' }
  browser.page.should have_selector('h1', :text => "Realm Management For #{@realm_name}")
end

When /^I view the custom roles for that realm$/ do
  row = find_realm_row
  browser.within(row) { browser.click_link 'Custom Roles' }
  browser.page.should have_selector('h1', :text => "Custom Roles for #{@realm_name}")
end

When /^I delete that realm$/ do
  row = find_realm_row
  browser.within(row) { browser.click_link 'Delete Realm' }
  browser.confirm_popup
end

When /^I modify the realm name$/ do
  @realm_name = "#{app_prefix}realm_#{Time.now.to_i}"
  browser.fill_in 'realm_name', :with => @realm_name
  browser.click_button 'Save'
end

Then /^I see the (new|edited) realm listed$/ do |change_type|
  message = "Realm was successfully #{change_type == 'new' ? 'created' : 'updated'}"
  browser.page.should have_selector('#notice', :text => message)
  browser.page.should have_selector('table#realms tr > td:nth-child(1)', :text => @realm_name)
end

Then /^I do not see the realm listed$/ do
  # We use 'have_no_selector' instead of 'should_not have_selector' to
  # ensure that Capybara will wait for the row to be deleted (via Ajax)
  browser.page.should have_no_selector('table#realms tr > td:nth-child(1)', :text => @realm_name)
  #browser.page.should have_selector('#notice', :text => 'Realm was successfully deleted')
end

When /^I try to add a new realm without inputting any data$/ do
  add_realm
end

When /^I try to add a new realm with (invalid|valid|duplicate) values for:$/ do |valid, table|
  add_realm do
    table.raw.each do |row|
      field = row.first.strip
      browser.fill_in(field, :with => value_for(field, valid.to_sym))
    end
  end
end

Then /^I should (not )?see the groups and roles for:$/ do |not_see, table|
  labels = table.raw.map {|row| row.first}
  selectors = ['td .groupTitle', 'td .role']
  labels.each do |label|
    selectors.each do |selector|
      if not_see
        browser.page.should have_no_selector(selector, :text => label)
      else
        browser.page.should have_selector(selector, :text => label)
      end
    end
  end
end

# Used by realm authentication

When /^I attempt to log into the new realm$/ do
  browser.visit path_for('default administration page')
  login_to_realm @realm_name
end

Then /^I can login to that realm$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I can still login to my realm$/ do
  pending # express the regexp above with the code you wish you had
end

def find_realm_row
  browser.page.first('table#realms tr', :text => @realm_name)
end

def add_realm
  browser.page.click_link 'Add new'
  browser.page.should have_selector('h1', :text => 'Manage Realm')
  yield if block_given?
  browser.click_button 'Save'
end

def value_for(field, valid)
  values = {
    'Display Name' => {
        :valid   => "#{app_prefix}realm_#{Time.now.to_i}",
        :invalid => Time.now.to_i.to_s[-4..-1],
        :duplicate => @realm_name
      },
    'Realm Identifier' => {
        :valid   => "#{app_prefix}realm_#{Time.now.to_i}",
        :invalid => Time.now.to_i.to_s[-4..-1],
        :duplicate => @realm_name
      },
    'Artifact Resolution Endpoint' => {
        :valid => 'http://example.com',
        :invalid => 'malformed_url'
      },
    'IDP Source ID' => {
        :valid => SecureRandom.hex(20),
        :invalid => '$362j9/' # some random string not 40 characters with characters outside of [a-fA-F0-9]
    }
  }
  values[field][valid]
end