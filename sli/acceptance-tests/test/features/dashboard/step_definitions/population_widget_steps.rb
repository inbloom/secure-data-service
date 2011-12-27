require 'selenium-webdriver'
require_relative '../../utils/sli_utils.rb'


# "main"
# driver = Selenium::WebDriver.for :firefox
# driver.get "http://google.com"
# 
# element = driver.find_element :name => "q"
# element.send_keys "Cheese!"
# element.submit
# 
# puts "Page title is #{driver.title}"
# 
# wait = Selenium::WebDriver::Wait.new(:timeout => 10)
# wait.until { driver.title.downcase.start_with? "cheese!" }
# 
# puts "Page title is #{driver.title}"
# driver.quit
# puts "after driver.quit"

# idpLogin("demo", "demo1234")


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  puts "login"
  @user = arg1
  @passwd = arg2
end

When /^I look in the school drop\-down$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I only see Daybreak Central High$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I select Daybreak Central High$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I look in the course drop\-down$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I see these values in the drop\-down: "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I select course U\.S\. Literature$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I see these values in the class drop\-down: "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I select Sec (\d+)$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I see a list of (\d+) students$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the list includes: Johnny Patel and Carmen Ortiz$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I select course Writing about Government$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I see these values in the class drop\-down: Sec (\d+)$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end
