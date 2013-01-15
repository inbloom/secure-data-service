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

When /^I hit the realm editing URL$/ do
  @url = PropLoader.getProps['admintools_server_url'] + "/realm_management"
  @driver.get @url
end

When /^I should see that I am on the "([^"]*)" edit page$/ do |realmName|
  message = "Realm Management For #{realmName}"
  assertWithWait("Should show '#{message}' message") do
    @driver.page_source.index(message) != nil
   end
end

When /^I should enter "([^"]*)" into the Display Name field$/ do |newRealmName|
  @driver.find_element(:id, "realm_name").clear
  @driver.find_element(:id, "realm_name").send_keys newRealmName
end

Then /^I should enter "(.*?)" into IDP URL$/ do |url|
  @driver.find_element(:name, 'realm[idp][id]').send_keys url
end

Then /^I should enter "(.*?)" into Redirect Endpoint$/ do |url|
  @driver.find_element(:name, 'realm[idp][redirectEndpoint]').send_keys url
end

Then /^I should enter "(.*?)" into Realm Identifier$/ do |identifier|
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys identifier
end

When /^I should click the "([^"]*)" button$/ do |buttonText|
  @driver.find_elements(:xpath, ".//input[@value='#{buttonText}']")[0].click
end

Then /^I should be redirected back to the edit page$/ do
  #No code necessary
end

Then /^I should receive a notice that the realm was successfully "([^"]*)"$/ do |action|
  message = "Realm was successfully updated." if action == "updated"
  message = "Realm was successfully deleted." if action == "deleted"
  message = "Realm was successfully created." if action == "created"
  assertWithWait("Should give successful #{action} notice") do
    notice = (@driver.find_element(:id, "notice")).text
    notice.index(message) != nil
  end
end


When /^I should click the delete realm link$/ do
  # links = @driver.find_elements(:xpath, ".//a[@data-method='delete']")
  element = @driver.find_element(:link_text, "Delete Realm")
  element.click
  @driver.switch_to.alert.accept
end

Then /^I should see that I am on the new realm page$/ do
  assertWithWait("Should be on new realm page"){@driver.page_source.index("Manage Realm") != nil}
end

Then /^all of the input fields should be blank$/ do
  textFields = @driver.find_elements(:xpath, ".//input[@type='text']")
  textFields.each do |cur|
    assert(cur.text.length == 0, "Input fields should not contain text")
  end
end

Then /^I should hit the role mapping page$/ do
  @url = PropLoader.getProps['admintools_server_url'] + "/custom_roles"
  @driver.get @url
end

Then /^I should see that the page doesn't exist$/ do
  assertWithWait("Should not be able to edit roles for non-existent realm") do
    @driver.page_source.index("The page you were looking for doesn't exist") != nil
  end
end

Then /^I should be redirected to a new realm page$/ do
  assertWithWait("Should be on new realm page") {@driver.page_source.index("Manage Realm") != nil}
end

When /^I enter valid data into all fields$/ do
  @driver.find_element(:name, 'realm[name]').send_keys "Brand New Realm"
  @driver.find_element(:name, 'realm[idp][id]').send_keys "IDPID"
  @driver.find_element(:name, 'realm[idp][redirectEndpoint]').send_keys "RedirectEndpoint"
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys "Unique Identifier"
end

Then /^I should see that I am on the "([^"]*)" page$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I should remove all of the fields$/ do
  @driver.find_elements(:css, 'input[type="text"]').each {|field| field.clear}
end


Then /^I should get (\d+) errors$/ do |arg1|
  assert(arg1.to_i == @driver.find_element(:id, 'error_explanation').find_elements(:css, 'li').count, "We should have found #{arg1} validation errors")
end

Then /^I should not see any errors$/ do
  begin
    @driver.find_element(:id, 'error_explanation')
    assert(false, "Shouldn't be any errors")
  rescue
    assert(true, "There shouldn't be any errors found")
  end
end

Then /^I should make the unique identifier not unique$/ do
  @driver.find_element(:name, 'realm[uniqueIdentifier]').clear
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys "Shared Learning Collaborative"
end

Then /^I should make the display name not unique$/ do
  @driver.find_element(:name, 'realm[uniqueIdentifier]').clear
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys "Brand New Realm"
  @driver.find_element(:name, 'realm[name]').clear
  @driver.find_element(:name, 'realm[name]').send_keys "Illinois Daybreak School District 4529"
end

Then /^I should get (\d+) error$/ do |arg1|
  step "I should get 1 errors"
end

When /^I see the realms for "([^"]*)"$/ do |uid|
  title = (@driver.find_elements(:xpath, "//html/body/div/h1"))[0].text
  assert(title == "Realms for #{uid}", "Page title not expected")
end

When /^I click the "(.*?)" edit button$/ do |arg1|
  @driver.find_element(:link, "Edit").click
end

#def close_alert_and_get_its_text()
  #if (@accept_next_alert) then
    #alert.accept()
  #else
    #alert.dismiss()
  #end
  #alert.text
#ensure
  #@accept_next_alert = true
#end

When /^I click the "(.*?)" delete button and confirm deletion$/ do |arg1|
  @driver.find_element(:link, "Delete Realm").click
  alert = @driver.switch_to().alert()
  alert.accept()
  #assert_confirmation /^WARNING: DELETING REALM WILL PREVENT ANY USER ASSOCIATED WITH THIS REALM FROM AUTHENTICATING ON inBloom AND WILL RESET ROLE MAPPING\. ARE YOU SURE[\s\S]$/i
  assert_match alert.text, /^WARNING: DELETING REALM WILL PREVENT ANY USER ASSOCIATED WITH THIS REALM FROM AUTHENTICATING ON inBloom AND WILL RESET ROLE MAPPING\. ARE YOU SURE[\s\S]$/
  sleep 2
end

And /^the realm "(.*?)" will not exist$/ do |arg1|
  assert_no_match /^[\s\S]*#{arg1}[\s\S]*$/, @driver.find_element(:id, "realms").text
end

And /^the realm "(.*?)" will exist$/ do |arg1|
  assert_match /^[\s\S]*#{arg1}[\s\S]*$/, @driver.find_element(:id, "realms").text
end

When /^pause$/ do
  sleep 30
end
