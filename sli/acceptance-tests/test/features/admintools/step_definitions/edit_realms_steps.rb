When /^I hit the realm editing URL$/ do
  @url = PropLoader.getProps['admintools_server_url'] + "/realm_management"
  @driver.get @url
end

When /^I should see that I am on the "([^"]*)" edit page$/ do |realmName|
  assertWithWait("Should show 'Realm Management For #{realmName}' message") do
    @driver.page_source.index("Realm Management For " + realmName) != nil
   end
end

When /^I should enter "([^"]*)" into the Display Name field$/ do |newRealmName|
  @driver.find_element(:id, "realm_name").clear
  @driver.find_element(:id, "realm_name").send_keys newRealmName
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
  assertWithWait("Should be on new realm page"){@driver.page_source.index("Create New Realm") != nil}
end

Then /^all of the input fields should be blank$/ do
  textFields = @driver.find_elements(:xpath, ".//input[@type='text']")
  textFields.each do |cur|
    assert(cur.text.length == 0, "Input fields should not contain text")
  end
end

Then /^I should hit the role mapping page$/ do
  @url = PropLoader.getProps['admintools_server_url'] + "/realms"
  @driver.get @url
end

Then /^I should see that the page doesn't exist$/ do
  assertWithWait("Should not be able to edit roles for non-existent realm") do
    @driver.page_source.index("The page you were looking for doesn't exist") != nil
  end
end

Then /^I should be redirected to a new realm page$/ do
  assertWithWait("Should be on new realm page") {@driver.page_source.index("Create New Realm") != nil}
end

When /^I enter valid data into all fields$/ do
  @driver.find_element(:name, 'realm[name]').send_keys "Brand New Realm"
  @driver.find_element(:name, 'realm[idp][id]').send_keys "IDPID"
  @driver.find_element(:name, 'realm[idp][redirectEndpoint]').send_keys "RedirectEndpoint"
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys "Unique Identifier"
end

When /^I click the "([^"]*)" button$/ do |arg1|
  pending # express the regexp above with the code you wish you had
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
  @driver.find_element(:name, 'realm[uniqueIdentifier]').send_keys "Shared Learning Infrastructure"
end

Then /^I should get (\d+) error$/ do |arg1|
  step "I should get 1 errors"
end

