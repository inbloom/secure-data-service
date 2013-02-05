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
require_relative '../../../utils/selenium_common.rb'

When /^I enter "([^"]*)" in the "([^"]*)" text field$/ do |value, fieldName|
  putTextToField(value,fieldName)
end

When /^I click the Go button$/ do
  clickButton("submit")
end

When /^I navigate to the Dashboard home page$/ do
  url = getBaseUrl()
  @driver.get url
  # Setting an explicit timeout for elements that may take a long time to load
  # We should be able to get rid of this and reply on the implicitWait
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 15)  
end

Then /^I click on the browser back button$/ do
  @driver.navigate().back()
end

Then /^the title of the page is "(.*?)"$/ do |pageTitle|
  verifyPageTitle(pageTitle)
end

def verifyPageTitle(pageTitle)
  assert(@driver.title == pageTitle, "Expected: " + pageTitle + " Actual " + @driver.title)
end

def getBaseUrl()
  return PropLoader.getProps['dashboard_server_address']+ 
          PropLoader.getProps['dashboard_app_prefix'] 
end

def assertMissingField(field, by)
  flag = true
  if (by == "id") and (@driver.find_element(:id, field) != nil)
    flag = false
  elsif (by == "name") and (@driver.find_element(:name, field) != nil)
    flag = false
  end
  assert(!flag, "Page does not contain a field with id:" + field)
end

# Asserts a piece of text exists in the body's page
def assertText(text)
  body = @driver.find_element(:tag_name, "body")
  puts "body.text = " + body.text
  assert(body.text.include?(text), "Text is missing from page: " + text )
end

def putTextToField(text, field, by)
  flag = true
  if (by == "id") and (@driver.find_element(:id, field) != nil)
    @driver.find_element(:id, field).send_keys text
  elsif (by == "name") and (@driver.find_element(:name, field) != nil)
    @driver.find_element(:name, field).send_keys text
  end
end    

def clickButton(button, by)
  if (by == "id")
    @driver.find_element(:id, button).click
  elsif (by == "name")
    @driver.find_element(:name, button).click
  end
end

def selectOption(selectFieldId, optionToSelect)
  select = @explicitWait.until{@driver.find_element(:id, selectFieldId)}
  all_options = select.find_elements(:tag_name, "option")
  optionFound = false
  all_options.each do |option|
    if option.attribute("text") == optionToSelect
      optionFound = true
      option.click
      break
    end
  end  
  assert(optionFound, "Desired option '" + optionToSelect + "' was not found in '" + selectFieldId + "' list")
end

def selectDropdownOption(selectFieldId, optionToSelect)
  puts "dropDownId = " + selectFieldId
  select = nil
  # Special case for view selector and filter, as they're a class
  if (selectFieldId == "viewSelectMenu" || selectFieldId == "filterSelectMenu")
    select = @explicitWait.until{@driver.find_element(:css, "div[class*='#{selectFieldId}']")}
  else
    select = @explicitWait.until{@driver.find_element(:id, selectFieldId)}
  end
  select.find_element(:tag_name,"a").click
  all_options = select.find_element(:class_name, "dropdown-menu").find_elements(:tag_name, "li")
  optionFound = false
  all_options.each do |option|
    if (option.find_element(:tag_name, "a").text == optionToSelect)
      optionFound = true
      option.find_element(:link_text,optionToSelect).click  
      break
    end
  end
  assert(optionFound, "Desired option '" + optionToSelect + "' was not found in '" + @dropDownId + "' list")
end

# TODO: add this paramteres (tableRef, by), also may want to add TR class
def countTableRows()
  @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  # we'll read from current tab if present
  source = @driver
  if (@currentTab != nil)
    source = @currentTab
  end
  tableRows = source.find_elements(:css, "tr[class*='ui-widget-content']")
  #puts "# of TR = " +  source.find_elements(:css, "tr").length.to_s + ", table rows = " + tableRows.length.to_s
  return tableRows.length
end

def listContains(desiredContent)
  result = false

  desiredContentArray = desiredContent.split(";")
  # Find all student names based on their class attribute
  
  los = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  studentNames = los.find_elements(:css,"td[aria-describedby*='name.fullName']")
  puts "num of studs = "+ studentNames.length.to_s
  
  nonFoundItems = desiredContentArray.length
  
  
  desiredContentArray.each do |searchValue|
    
    puts "in 1st loop, searchValue = " + searchValue
    studentNames.each do |student|
      # puts "in 2st loop, student.attribute('innerHTML').to_s = " + student.attribute("innerHTML").to_s.lstrip.rstrip[0..15]
      # puts "student.attribute('innerHTML').to_s.include?(searchValue) = " + student.attribute("innerHTML").to_s.include?(searchValue).to_s
      
      if student.attribute("innerHTML").to_s.lstrip.rstrip.include?(searchValue)
        nonFoundItems -= 1
        puts "Found desired item '" + searchValue + "', " + nonFoundItems.to_s + " more items to find"
        # Stop searching for this searchValue and move to the next one
        break
      end
    end
  end
  return nonFoundItems == 0
end

def tableHeaderContains(desiredContent)
  result = false

  desiredContentArray = desiredContent.split(";")
  # Find all student names based on their class attribute
  los = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-hbox")}
  headerNames = los.find_elements(:tag_name, "th")
  
  puts "num of studs = "+ headerNames.length.to_s
  
  nonFoundItems = desiredContentArray.length
  
  retries = 30;
  retry_sleep = 10
  until headerNames.length > 0 || retries == 0
    puts "No headers found.  Sleeping for #{retry_sleep}.  #{retries} retries remaining."
    retries -= 1
    sleep retry_sleep
    headerNames = @driver.find_elements(:tag_name, "th")
  end
  
  desiredContentArray.each do |searchValue|
    puts "in 1st loop, searchValue = " + searchValue
    headerNames.each do |header|
      puts "in 2st loop, header.attribute('innerHTML').to_s = " + header.attribute("innerHTML").to_s.lstrip.rstrip[0..15]
      # puts "student.attribute('innerHTML').to_s.include?(searchValue) = " + student.attribute("innerHTML").to_s.include?(searchValue).to_s
      
      if header.attribute("innerHTML").to_s.lstrip.rstrip.include?(searchValue)
        nonFoundItems -= 1
        puts "Found desired item '" + searchValue + "', " + nonFoundItems.to_s + " more items to find"
        # Stop searching for this searchValue and move to the next one
        break
      end
    end
  end
  return nonFoundItems == 0
end
