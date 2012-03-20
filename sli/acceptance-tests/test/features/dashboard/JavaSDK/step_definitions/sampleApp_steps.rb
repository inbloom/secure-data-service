require_relative '../../../utils/sli_utils.rb'
require_relative '../../dash/step_definitions/selenium_common_dash.rb'

Given /^the sampleApp is deployed on sampleApp server$/ do
  @appPrefix = "/oauth2-sample/students"
end

When /^I navigate to the sampleApp home page$/ do
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix
  @driver.get url
  # There's a redirect to the realm page, so this assert should fail
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  assert(@driver.current_url.start_with?("https://devapp1.slidev.org//api/oauth/authorize"))

end

When /^I select "([^"]*)" and click go$/ do |arg1|
  sleep(1)
  
 realm_select = @driver.find_element(:name=> "realmId")

  
  options = realm_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1.text == arg1)
      e1.click()
      break
    end
  end
  clickButton("go", "id")
  
end

When /^I login as "([^"]*)" "([^"]*)"/ do | username, password |
    sleep(1)
    wait = Selenium::WebDriver::Wait.new(:timeout => 5) # explicit wait for at most 5 sec
    wait.until{@driver.find_element(:id, "IDToken1")}.send_keys username
    @driver.find_element(:id, "IDToken2").send_keys password
    @driver.find_element(:name, "Login.Submit").click
end

When /^I go to List of Students$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the table includes header "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see student sampeStudent" in the list$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am authenticated to SLI as "([^"]*)" "([^"]*)"$/ do |user, pass|
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + PropLoader.getProps[@appPrefix]
  
  #url = "http://localhost:8080/dashboard"
  @driver.get(url)
  @driver.manage.timeouts.implicit_wait = 30
  @driver.find_element(:name, "j_username").clear
  @driver.find_element(:name, "j_username").send_keys user
  @driver.find_element(:name, "j_password").clear
  @driver.find_element(:name, "j_password").send_keys pass
  @driver.find_element(:name, "submit").click
end