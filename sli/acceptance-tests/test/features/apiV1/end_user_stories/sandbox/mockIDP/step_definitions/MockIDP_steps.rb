require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../dashboard/dash/step_definitions/selenium_common_dash.rb'

Given /^I have selected the realm using the realm selector$/ do
  url = PropLoader.getProps['mockIDP_realm_server_address']+"api/oauth/authorize?"+URI.escape(PropLoader.getProps['mockIDP_realm_params'])
  @driver.get url
  sleep(1)

  #assume the user selected the mock sli realm
  realmName= PropLoader.getProps['mockIDP_realm_SLI'] + " - " + PropLoader.getProps['mockIDP_realm_suffix']
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

Given /^I navigate to sample app web page$/ do
  
  sampleAppUrl = PropLoader.getProps['sampleApp_server_address']
  url = sampleAppUrl+"oauth2-sample"
  @driver.get url
end

Then /^I will be redirected to realm selector web page$/ do
  puts @driver.current_url
  assert(@driver.current_url.include?("/api/oauth/authorize"))
end

When /^I select the "([^"]*)" realm$/ do |realmName|
  realmName = realmName + " - " + PropLoader.getProps['mockIDP_realm_suffix']
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

Then /^the heading of the Mock IDP Page contains the realm "([^"]*)"$/ do |realm|
  realm_text = @driver.find_elements(:xpath, "//span[@class='tenant']")
  realm_text.size.should == 1
  realm_text[0].text.should include realm
end


When /^I select "([^"]*)" from the user drop down$/ do |arg1|
  select_by_id("Doe, John (Staff)","selected_user")
end

Then /^I select "([^"]*)" from role selector$/ do |arg1|
  wait = Selenium::WebDriver::Wait.new(:timeout => 5)
  role = nil
  wait.until { role=@driver.find_element(:xpath, "//option[@value='"+arg1+"']") }
  role.click
end

Then /^I select "([^"]*)"  and "([^"]*)" from role selector$/ do |arg1, arg2|
  wait = Selenium::WebDriver::Wait.new(:timeout => 5)
  role1 = nil
  role2 = nil
  wait.until { role1=@driver.find_element(:xpath, "//option[@value='"+arg1+"']") }
  wait.until { role2=@driver.find_element(:xpath, "//option[@value='"+arg2+"']") }
  role1.click
  role2.click
end

When /^I click Login$/ do
  clickButton("login_button","id")
end

When /^I wait for (\d+) second$/ do |arg1|
  sleep(Integer(arg1))
end

Then /^I should be redirected to sample app web page$/ do
  sampleAppUrl = PropLoader.getProps['sampleApp_server_address']
  assert(@driver.current_url.start_with?(sampleAppUrl))
end

Then /^I am able Read student data$/ do
  rights =@driver.find_element(:xpath,"//td[@id='accessRights']")
  rights.text.should include("READ_GENERAL")
end

Then /^I have "([^"]*)" access to the sandbox tenancy$/ do |arg1|
  roles=@driver.find_element(:xpath,"//td[@id='roles']")
  roles.text.should include(arg1)
end

Then /^I am able to write student data$/ do
  rights =@driver.find_element(:xpath,"//td[@id='accessRights']")
  rights.text.should include("WRITE_GENERAL")
end

Then /^I have "([^"]*)" and "([^"]*)" access to the sandbox tenancy$/ do |arg1,arg2|
  roles=@driver.find_element(:xpath,"//td[@id='roles']")
  roles.text.should include(arg1,arg2)
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

def select_by_id(elem, select)
  begin
    Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, select)).select_by(:text, elem)
  rescue Selenium::WebDriver::Error::NoSuchElementError
    false
  end
end

