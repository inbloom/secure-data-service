Given /^I am managing my realms$/ do
  browser.visit realm_management_page
  login_to_the_inbloom_realm
  browser.page.should have_selector('h1', :text => /Realms for/)
end

When /^I add a new realm$/ do
  browser.page.click_link 'Add new'
  browser.page.should have_selector('h1', :text => 'Manage Realm')
  @realm_name = "#{app_prefix}realm_#{Time.now.to_i}"
  browser.fill_in('realm_name', :with => @realm_name)
  browser.fill_in('realm_idp_id', :with => 'http://www.example.com')
  browser.fill_in('realm_idp_redirectEndpoint', :with => 'http://www.example.com')
  browser.fill_in('realm_uniqueIdentifier', :with => @realm_name)
  browser.click_button 'Save'
  add_for_cleanup(:realm, @realm_name)
end

When /^I edit that realm$/ do
  row = find_realm_row
  browser.within(row) { browser.click_link 'Edit' }
  browser.page.should have_selector('h1', :text => "Realm Management For #{@realm_name}")
end

When /^I delete that realm$/ do
  row = find_realm_row
  browser.within(row) { browser.click_link 'Delete Realm' }
  browser.confirm_popup
end

When /^I modify the realm name$/ do
  @realm_name = "#{app_prefix}realm_#{Time.now.to_i}"
  browser.fill_in 'realm[name]', :with => @realm_name
end

When /^I save the changes$/ do
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
end

def find_realm_row
  browser.page.first('table#realms tr', :text => @realm_name)
end