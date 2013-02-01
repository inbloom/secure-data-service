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
require 'selenium-webdriver'
require_relative '../../utils/selenium_common.rb'
require_relative '../../utils/sli_utils.rb'

# Require all dashboard step definitions
Dir["./test/features/dashboard/dash/step_definitions/*"].each {|file| require file}

When /^I navigate to the Portal home page$/ do
  @driver.get PropLoader.getProps['portal_server_address'] + PropLoader.getProps['portal_app_suffix']
  @explicitWait ||= Selenium::WebDriver::Wait.new(:timeout => 15)
end

When /^I navigate to the mini sandbox Portal home page$/ do
  puts PropLoader.getProps['minisb_portal_server_address'] + PropLoader.getProps['portal_app_suffix']
  @driver.get PropLoader.getProps['minisb_portal_server_address'] + PropLoader.getProps['portal_app_suffix']
  @explicitWait ||= Selenium::WebDriver::Wait.new(:timeout => 15)
end

Then /^I should see Admin link$/ do
  @driver.find_element(:link_text, "Admin")
end

Then /^I should not see Admin link$/ do
  adminLink = @driver.find_elements(:link_text,"Admin")
  assert(adminLink.length == 0, "Admin link was found")
end

#This logout step is for clicking the exit/logout link from the portal header
Then /^I click on log out$/ do
  menuList = @driver.find_element(:class, "menu_n").find_element(:class, "first_item")
  menu = menuList.find_element(:id,"menulink")
  menu.click
  begin
    menuList.find_element(:link_text, "Exit").click
  rescue
    menuList.find_element(:link_text, "Logout").click
  ensure
    assertWithWait("User didn't log out properly") {@driver.current_url != PropLoader.getProps['portal_server_address'] + PropLoader.getProps['portal_app_suffix']}
  end
end

# TODO, look for something now in eula, if found proceed
Then /^I should be on Portal home page$/ do
  sleep 2
  home = @driver.find_elements(:class, "sli_home_title")
  assert(home.length == 1, "User is not on the portal home page. Current URL: " + @driver.current_url)
  if (@driver.page_source.include?("d_popup"))
    accept = @driver.find_element(:css, "[class*='aui-button-input-submit']")
    puts accept.inspect
    puts "EULA is present"
    accept.click
    #sleep 2
    assertWithWait("EULA pop up did not get dismissed") { !@driver.page_source.include?("d_popup") }
  else
    puts "EULA has already been accepted"
  end
end

Then /^I should be on the authentication failed page$/ do
  @driver.page_source.include?('Invalid')
end

Then /^I should be on the admin page$/ do
  title = @driver.find_element(:class, "sli_home_title").text
  assert(title == "ADMIN", "User is not in the admin page")
end

Then /^(?:|I )should see "([^\"]*)"$/ do |text|
  body = @driver.find_element(:tag_name, "body")
  assert((body.attribute('innerHTML').include? text) == true, "Body doesn't contain #{text}")
end

Then /^(?:|I )should not see "([^\"]*)"$/ do |text|
  body = @driver.find_element(:tag_name, "body")
  assert((body.attribute('innerHTML').include? text) == false, "Body contains #{text}")
end

When /^I click on Admin$/ do
  clickOnLink("Admin")
end

And /^I should see logo$/ do
  logo = @driver.find_element(:class, "company-logo")
  text = @driver.find_element(:class, "sli_logo_main").text
  assert(text == "SLC", "Expected: SLC, Actual: {#text}")
end

And /^I should see footer$/ do
  footer = @driver.find_element(:class, "portlet-body")
end

And /^I should see username "([^"]*)"$/ do |expectedName|
  name = @driver.find_element(:class, "first_item").text
  assert(name == expectedName, "Expected: #{expectedName} Actual: #{name}")
end

Then /^under System Tools, I click on "(.*?)"$/ do |link|
  clickOnLink(link)
end

Then /^under Application Configuration, I click on "(.*?)"$/ do |link|
  clickOnLink(link)
end

Then /^under My Applications, I click on "(.*?)"$/ do |link|
  links = @driver.find_elements(:tag_name, "a")
  links.each do |availableLink|
    if (availableLink.text.include? link)
      yLocation = availableLink.location.y.to_s
      xLocation = availableLink.location.x.to_s
      @driver.execute_script("window.scrollTo(#{xLocation}, #{yLocation});")
      availableLink.click
      break
    end
  end
end

Then /^under Application Configuration, I see the following: "(.*?)"$/ do |links|
  section = @driver.find_element(:id, "column-5")
  verifyItemsInSections(links, section, "Application Configuration")
end

Then /^under System Tools, I see the following "(.*?)"$/ do |links|
  section = @driver.find_element(:id, "column-4")
  verifyItemsInSections(links, section, "System Tools")
end

Then /^under System Tools, I shouldn't see the following "(.*?)"$/ do |links|
  section = @driver.find_element(:id, "column-4")
  verifyItemsInSections(links, section, "System Tools", false)
end

Then /^under My Applications, I see the following apps: "(.*?)"$/ do |apps|
  myApps = @driver.find_element(:id, "column-4")
  verifyItemsInSections(apps, myApps, "My Applications")
end

Then /^I switch to the iframe$/ do
  wait = Selenium::WebDriver::Wait.new(:timeout => 20)
  wait.until{(iframe = isIframePresent()) != nil}
end

Then /^I exit out of the iframe$/ do
  @driver.switch_to.default_content
end

def isIframePresent()
  #TODO figure out how to determine when page is loaded instead of using sleep
  sleep 2
  @driver.switch_to.default_content
  begin
    iframe = @driver.find_element(:tag_name, "iframe")
    puts "iframe found"
    @driver.switch_to.frame(iframe.attribute('id'))
    puts "iframe switched"
    # This might not be a good solution that works for all
    @driver.find_element(:id,"messageContainer")
    puts "iframe contents appears to be loaded"
    return iframe
  rescue
    puts "iframe not fully loaded yet"
    @driver.switch_to.default_content
    return nil
  end
end

def clickOnLink(linkText)
  @driver.find_element(:link, linkText).click
end

def verifyItemsInSections(expectedItems, section, sectionTitle, exist = true)
  listOfItems = expectedItems.split(';')
  title = section.find_element(:class, "portlet-title-text")
  assert(title.text == sectionTitle, "Expected: #{sectionTitle} Actual: #{title}")
  all_trs = section.find_element(:class, "portlet-body").find_elements(:tag_name, "tr")
  listOfItems.each do |item|
    found = false
    all_trs.each do |tr|
      if (tr.text.include? item)
        found = true
        break
      end
    end
    message = (exist)? "#{item} should be found under #{sectionTitle}" :
        "#{item} should not be found under #{sectionTitle}"
    assert(found == exist, message)
  end
end

