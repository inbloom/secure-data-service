require_relative '../../../utils/sli_utils.rb'
require_relative '../../../dashboard/step_definitions/selenium_common.rb'

When /^I select <viewSelector> "([^"]*)"$/ do |view|
  select_by_id(view, "viewSelector")
  #sleep(5)
end

When /^the view configuration file set "([^"]*)" is "([^"]*)"$/ do |arg1, arg2|
  #do nothing, view configuration has been setup
end

Then /^I should see a field "([^"]*)" in this table$/ do |fieldName|
  field = @driver.find_element(:id, "listHeader."+@headerName+"."+fieldName)
  field.should_not be_nil
  field.text.should == fieldName
end

Then /^I should see  "([^"]*)" in student field$/ do |studentName|
  student = @driver.find_element(:id, studentName+".name_w_link")
  student.should_not be_nil
  student.text.should include studentName
  @studentName = studentName
end

Then /^I should see his\/her ISAT Reading Scale Score is "([^"]*)"$/ do |scoreResult|
  score = @driver.find_element(:id, @studentName+".ISAT-Reading.Scale score")
  score.should_not be_nil
  score.text.should == scoreResult
end

Then /^I should see his\/her ISAT Writing Scale Score is "([^"]*)"$/ do |scoreResult|
  score = @driver.find_element(:id, @studentName+".ISAT-Writing.Scale score")
  score.should_not be_nil
  score.text.should == scoreResult
end
  
def select_by_id(elem, select)
  Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, select)).
      select_by(:text, elem)
rescue Selenium::WebDriver::Error::NoSuchElementError
  false
end

