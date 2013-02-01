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
Then /^I click on Report a Problem$/ do
  menuList = @driver.find_element(:class, "menu_n").find_element(:class, "first_item")
  menu = menuList.find_element(:id,"menulink")
  menu.click
  menuList.find_element(:link_text, "Report a problem").click
end

Then /^It opens a popup$/ do
  wait = Selenium::WebDriver::Wait.new(:timeout => 10)
  wait.until{
    frame=@driver.find_element(:tag_name, "iframe")
    @driver.switch_to.frame(frame) 
  }
end

Then /^I select problem type "([^"]*)"$/ do |problemType|
  iframe = @driver.find_element(:class, "aui-fieldset-content ")
  select = iframe.find_element(:tag_name, "select")
  all_options = select.find_elements(:tag_name, "option")
  optionFound = false
  all_options.each do |option|
    if (option.attribute('value') == problemType)
      optionFound = true
      option.click
    end
  end
  assert(optionFound, "Desired option '" + problemType + "' was not found" )
end

Then /^I type "([^"]*)" in Problem Description$/ do |problemDesc|
  textArea = @driver.find_element(:class, "aui-fieldset-content ").find_element(:tag_name, "textarea")
  textArea.send_keys problemDesc
end

Then /^I click on Report a Problem Button$/ do
  @driver.find_element(:class, "pop_btn_pnl").find_element(:tag_name, "input").click
end
