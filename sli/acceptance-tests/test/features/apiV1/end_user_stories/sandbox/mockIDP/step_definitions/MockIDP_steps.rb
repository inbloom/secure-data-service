require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../dashboard/dash/step_definitions/selenium_common_dash.rb'

Given /^I have selected the realm using the realm selector$/ do
  url = PropLoader.getProps['mockIDP_realm_url']+"?"+URI.escape(PropLoader.getProps['mockIDP_realm_params'])
  @driver.get url
   sleep(1)
   
   #assume the user selected the mock sli realm
  realmName= PropLoader.getProps['mockIDP_realm_SLI']
 realm_select = @driver.find_element(:name=> "realmId")
  options = realm_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1.text == realmName)
      e1.click()
      break
    end
  end
  clickButton("go", "id")
  
end

Then /^I should be redirected to the Mock IDP page for the realm$/ do
  mockIdpUrl = PropLoader.getProps['mockIDP_login_url']
  assert(@driver.current_url.start_with?(mockIdpUrl))
end

Then /^the Mock IDP Page has a drop down with all the users defined in tenancy$/ do
  userSelector=@driver.find_element(:id, "selected_user")
  userSelector.text.should include("Administrator, Sample")
end

Then /^the Mock IDP Page has a multi select which has all the roles defined in the tenancy$/ do
  roleSelector=@driver.find_element(:id, "selected_roles")
  roleSelector.text.should include("IT Administrator","Leader","Educator","Aggregator")
end

Then /^the Mock IDP Page has a button the user can use to log in$/ do
  loginButton=@driver.find_element(:id, "login_button").attribute("value")
  loginButton.should == "Login"
end

Then /^the Mock IDP Page has a log out link$/ do
  logOut = @driver.find_element(:xpath, "//input[@value='Logout']")
  logOut.should_not==nil
end

Then /^the heading of the Mock IDP Page is realm followed by "([^"]*)"$/ do |arg1|
  headers = @driver.find_elements(:xpath, "//span[@class='header']")
  headers.should_not==nil
  found = false
  headers.each do |header|
    if header.text.should include "IDP"
      found=true
    end
  end
  assert(found,"no realm info found in mock IDP page!")
end


When /^I select "([^"]*)" from the user drop down$/ do |arg1|
  userSelect=@driver.find_element(:id, "selected_user")
  user=@driver.find_element(:xpath, "//option[@value='"+arg1+"']")
  @driver.action.click(userSelect).click(user).perform
end

Then /^I select "([^"]*)" from role selector$/ do |arg1|
  #roleSelect=@driver.find_element(:id, "selected_roles")
  role=@driver.find_element(:xpath, "//option[@value='"+arg1+"']")
  @driver.action.click(role).perform
end

Then /^I select "([^"]*)"  and "([^"]*)" from role selector$/ do |arg1, arg2|
  # roleSelect=@driver.find_element(:id, "selected_roles")
  role1=@driver.find_element(:xpath, "//option[@value='"+arg1+"']")
  role2=@driver.find_element(:xpath, "//option[@value='"+arg2+"']")
  @driver.action.key_down(:control).click(role1).click(role2).key_up(:control).perform
end

When /^I click Login$/ do
 @driver.find_element(:id, "login_button").click
end

Then /^I have "([^"]*)" access to the sandbox tenancy$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I am able to write student data$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I have "([^"]*)" and "([^"]*)" access to the sandbox tenancy$/ do |arg1,arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I am able "([^"]*)" student data$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I am logged in to Mock\-IDP$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the Logout link$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am logged out$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to Realm Selector$/ do
  pending # express the regexp above with the code you wish you had
end

