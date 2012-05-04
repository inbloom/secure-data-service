When /^I hit the realm editing URL$/ do
  @url = PropLoader.getProps['admintools_server_url'] + "/realm_editors"
  @driver.get @url
end

When /^I should see that I am on the "([^"]*)" edit page$/ do |realmName|
  assertWithWait("Should show 'Editing #{realmName}' message") do
    @driver.page_source.index("Editing " + realmName) != nil
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

Then /^I should receive a notice that the realm was successfully updated$/ do
  assertWithWait("Should give successful update notice") do
    @driver.page_source.index("Realm was successfully updated.") != nil
  end
end
