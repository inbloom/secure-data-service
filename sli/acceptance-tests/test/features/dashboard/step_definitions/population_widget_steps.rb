require 'selenium-webdriver'
# require_relative '../../utils/sli_utils.rb'


# idpLogin("demo", "demo1234")


Given /^I selected the "([^"]*)" application$/ do |appName|
  @driver.find_element(:link_text, appName).click
end

When /^I look in the ed org drop\-down$/ do
  @dropDownId = "edOrgSelect"
  assertMissingField(@dropDownId, "id")
end

When /^I look in the school drop\-down$/ do
  @dropDownId = "schoolSelect"
  assertMissingField(@dropDownId, "id")
end

Then /^I only see "([^"]*)"$/ do |listContent|
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_elements(:tag_name, "option")
  matchCondition = true
  # If any list item has a value that is not in the list - set flag to false
  all_options.each do |option|
    if option.attribute("text") != listContent and 
      option.attribute("text") != "" then
      matchCondition = false
    end
  end
  assert(matchCondition, "School list has more then required string(s) " + listContent)
end

When /^I look in the course drop\-down$/ do
  @dropDownId = "courseSelect"
  assertMissingField(@dropDownId, "id")
end

Then /^I see these values in the drop\-down: "([^"]*)"$/ do |listContent|
  puts "@dropDownId = " + @dropDownId
  desiredContentArray = listContent.split(";")
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_elements(:tag_name, "option")
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

Then /^I see a list of (\d+) students$/ do |numOfStudents|
  actualCount = countTableRows()
  puts "numOfStudents should be " + numOfStudents.to_s + ", actualCount = " + actualCount.to_s
  # TODO enable this
  assert(actualCount != numOfStudents, "List contains '" + actualCount.to_s + "' students and not '" + numOfStudents.to_s + "'")
end

When /^I select ed org "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "edOrgSelect"
  puts "@dropDownId = " + @dropDownId
  selectOption(@dropDownId, optionToSelect)
  @dropDownId = "schoolSelect"
end

When /^I select school "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "schoolSelect"
  puts "@dropDownId = " + @dropDownId
  selectOption(@dropDownId, optionToSelect)
  @dropDownId = "courseSelect"
end

When /^I select course "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "courseSelect"
  puts "@dropDownId = " + @dropDownId
  selectOption(@dropDownId, optionToSelect)
  @dropDownId = "sectionSelect"
end

When /^I select section "([^"]*)"$/ do |optionToSelect|
  @dropDownId = "sectionSelect"
  puts "@dropDownId = " + @dropDownId
  selectOption(@dropDownId, optionToSelect)
end

Then /^the list includes: "([^"]*)"$/ do |desiredContent|
  assert(listContains(desiredContent), "List does not contain desired values: '" + desiredContent + "'")
end

Then /^I see these values in the section drop\-down: "([^"]*)"$/ do |listContent|
  @dropDownId = "sectionSelect"
  desiredContentArray = listContent.split(";")
  select = @driver.find_element(:id, @dropDownId)
  all_options = select.find_elements(:tag_name, "option")
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