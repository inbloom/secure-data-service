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


require_relative '../../../utils/sli_utils.rb'

Then /^I should have multiple filters available$/ do
  arr = get_all_elements
  arr.size.should be > 0
end

When /^I select filter "([^"]*)"$/ do |filter|
  select_by_id(filter, "filterSelect")
end

Then /^I should see a student named "([^"]*)"$/ do |student|
  list = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  
  list.should_not be_nil
  list.text.should include student
end

def get_all_elements
  #click the droplist first else there are issues seeing hidden elements
  @selector.find_element(:tag_name, "a").click
  options = @selector.find_element(:class, "dropdown-menu").find_elements(:tag_name, "li")
  arr = []
  options.each do |option|
    link = option.find_element(:tag_name, "a").text
    arr << link
  end
  #unclick it
  @selector.find_element(:tag_name, "a").click
  arr
end
