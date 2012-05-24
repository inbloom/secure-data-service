require 'selenium-webdriver'
# require_relative '../../utils/sli_utils.rb'


# idpLogin("demo", "demo1234")


Given /^I selected the "([^"]*)" application$/ do |appName|
  @driver.find_element(:link_text, appName).click
end

When /^I look in the ed org drop\-down$/ do
  @dropDownId = "edOrgSelectMenu"
  assertMissingField(@dropDownId, "id")
end

When /^I look in the school drop\-down$/ do
  @dropDownId = "schoolSelectMenu"
  assertMissingField(@dropDownId, "id")
end

Then /^I only see "([^"]*)"$/ do |listContent|
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_elements(:class, "dropdown-menu").first.find_elements(:tag_name, "li")
  matchCondition = true
  # If any list item has a value that is not in the list - set flag to false
  all_options.each do |option|
    if option.find_element(:tag_name, "a").attribute("text")  != listContent and 
      option.find_element(:tag_name, "a").attribute("text") != "" then
      matchCondition = false
    end
  end
  assert(matchCondition, "list has more then required string(s) " + listContent)
end

When /^I look in the course drop\-down$/ do
  @dropDownId = "courseSelectMenu"
  assertMissingField(@dropDownId, "id")
end

When /^I look at the section drop\-down$/ do
  @dropDownId = "sectionSelectMenu"
  assertMissingField(@dropDownId, "id")
end

Then /^I see these values in the drop\-down: "([^"]*)"$/ do |listContent|
  puts "@dropDownId = " + @dropDownId
  desiredContentArray = listContent.split(";")
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_element(:class_name, "dropdown-menu").find_elements(:tag_name, "li")
  matchCondition = true
  selectContent = ""
  # If any list item has a value that is not in the list - set flag to false
  all_options.each do |option|
    selectContent += option.find_element(:tag_name, "a").attribute("text") + ";"
    puts "selectContent = " + selectContent
  end
  selectContentArray = selectContent.split(";")
  result = (desiredContentArray | selectContentArray) - (desiredContentArray & selectContentArray)
  assert(result == [], "list content does not match required content: " + listContent)  
end

Then /^I don't see these values in the drop\-down: "([^"]*)"$/ do |listContent|
  isValuesInList(listContent, false)  
end

Then /^I see a list of (\d+) students$/ do |numOfStudents|
  studentList = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  
  actualCount = countTableRows()
  puts "numOfStudents should be " + numOfStudents.to_s + ", actualCount = " + actualCount.to_s
  assert(actualCount == numOfStudents.to_i, "List contains '" + actualCount.to_s + "' students and not '" + numOfStudents.to_s + "'")
end

When /^I select ed org "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "edOrgSelectMenu"
  selectDropdownOption(@dropDownId, optionToSelect)
  @dropDownId = "schoolSelectMenu"
end

When /^I select school "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "schoolSelectMenu"
  selectDropdownOption(@dropDownId, optionToSelect)
  @dropDownId = "courseSelectMenu"
end

When /^I select course "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "courseSelectMenu"
  selectDropdownOption(@dropDownId, optionToSelect)
  @dropDownId = "sectionSelectMenu"
end

When /^I select section "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "sectionSelectMenu"
  selectDropdownOption(@dropDownId, optionToSelect)
end

When /^I select user view "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "viewSelectMenu"
  selectDropdownOption(@dropDownId, optionToSelect)
end


Then /^the list includes: "([^"]*)"$/ do |desiredContent|
  assert(listContains(desiredContent), "List does not contain desired values: '" + desiredContent + "'")
end

Then /^I see these values in the section drop\-down: "([^"]*)"$/ do |listContent|
  @dropDownId = "sectionSelectMenu"
  desiredContentArray = listContent.split(";")
  select = @explicitWait.until{@driver.find_element(:id, @dropDownId)}
  all_options = select.find_element(:class, "dropdown-menu").find_elements(:tag_name, "li")
  matchCondition = true
  selectContent = ""
  # If any list item has a value that is not in the list - set flag to false
  all_options.each do |option|
    selectContent += option.attribute("text") + ";"
    puts "selectContent = " + selectContent
  end
  selectContentArray = selectContent.split(";")
  result = (desiredContentArray | selectContentArray) - (desiredContentArray & selectContentArray)
  assert(result == [""], "list content does not match required content: " + listContent)  
end

Then /^I copy my current URL$/ do
  @copiedUrl = @driver.current_url
end

Given /^I paste my copied URL$/ do
  puts @copiedUrl
  @driver.get @copiedUrl
end

Then /^I don't see a course selection$/ do
  begin
    course = @driver.find_element(:id,"courseSelectMenu")
  rescue
    assert(course == nil, "Course is not nil")
  end
end

def isValuesInList(listContent, isInList)
  puts "@dropDownId = " + @dropDownId
  desiredContentArray = listContent.split(";")
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_element(:class_name, "dropdown-menu").find_elements(:tag_name, "li")
  matchCondition = true
  selectContent = ""
  # If any list item has a value that is not in the list - set flag to false
  all_options.each do |option|
    selectContent += option.find_element(:tag_name, "a").attribute("text") + ";"
    puts "selectContent = " + selectContent
  end
  selectContentArray = selectContent.split(";")
  if (isInList)
    result = (desiredContentArray | selectContentArray) - (desiredContentArray & selectContentArray)
    assert(result == [], "list content does not match required content: " + listContent)
  else
    result = selectContentArray - desiredContentArray
    assert(result == selectContentArray, "The content is found: " + listContent)
  end
end
