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


Then /^I can search for <Type> with a <Field> and get a <Result>$/ do |table|
  table.hashes.each do |hash|
    select = @driver.find_element(:tag_name, "select")
    all_options = select.find_elements(:tag_name, "option")
    all_options.each do |option|
      if option.attribute("value") == hash["Type"]
        option.click
        break
      end
    end
    @driver.find_element(:id, "search_id").clear
    @driver.find_element(:id, "search_id").send_keys(hash["Field"])
    @driver.find_element(:css, "input[type='submit']").click
    errors = @driver.find_elements(:id, "alert")
    if hash["Result"] == "Pass"
      assert(errors.size == 0, "Should not be a flash error for #{hash["Type"]}/#{hash["Field"]}")
    else
      assert(errors.size == 1, "There should be an error message for #{hash["Type"]}/#{hash["Field"]}")
    end   
  end
end

When /^I go to the students page$/ do
  url = @driver.current_url.gsub /home/, 'students'
  @driver.get(url)
end

When /^I click on the First Name column$/ do
  @first_element = @driver.find_element(:xpath, "//tbody/tr/td[2]")
  @driver.find_element(:xpath, "//thead/tr/th[2]").click
end

Then /^the order of the contents should change$/ do
  assert(@first_element != @driver.find_element(:xpath, "//tbody/tr/td[2]"), "The elements should have been sorted")
end

When /^I click again$/ do
  step "I click on the First Name column"
end

Then /^the contents should reverse$/ do
  step "the order of the contents should change"
end

Then /^I should see (\d+) students$/ do |arg1|
  @count = @driver.find_elements(:xpath, "//tbody/tr").size
  assert(@count == arg1.to_i, "There should be #{arg1} students")
end

When /^I scroll to the bottom$/ do
  
  @driver.find_element(:css, 'div#simple-table_info.dataTables_info')
end

When /^wait for (\d+) seconds$/ do |arg1|
  sleep arg1.to_i
end

Then /^I should see more students$/ do
  new_count = @driver.find_elements(:xpath, "//tbody/tr").size
  assert(@count < new_count, "We should have more students than before")
end
