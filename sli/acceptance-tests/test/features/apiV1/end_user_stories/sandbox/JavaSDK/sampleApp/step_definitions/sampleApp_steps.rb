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


require_relative '../../../../../../utils/sli_utils.rb'
require_relative '../../../../../../dashboard/dash/step_definitions/selenium_common_dash.rb'

Given /^the sampleApp is deployed on sampleApp server$/ do
  @appPrefix = "sample/students"
end

When /^I navigate to the sampleApp home page$/ do
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix
  puts url
  @driver.get url
  # There's a redirect to the realm page, so this assert should fail
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  assert(@driver.current_url.include?("/api/oauth/authorize"))

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
  begin
      @driver.switch_to.alert.accept
    rescue
    end
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix
  puts url
  @driver.get url
end

Then /^the page should include a table with header "([^"]*)"$/ do |name|
  headerName=@driver.find_element(:id, "header.Student")
  headerName.text.should == name
end

Then /^I should see student "([^"]*)" in the student list$/ do |studentName|
  name=@driver.find_element(:id, "name."+studentName)
  name.text.should == studentName
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
