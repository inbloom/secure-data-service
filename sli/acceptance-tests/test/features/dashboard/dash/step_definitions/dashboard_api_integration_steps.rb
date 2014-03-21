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
require_relative '../../../utils/sli_utils.rb'


$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']


# TODO: externalize this to a method so we can reuse in the future
When /^I select "([^"]*)" and click go$/ do |arg1|
 realm_select = @explicitWait.until{@driver.find_element(:name=> "realmId")}
  
  options = realm_select.find_elements(:tag_name=>"option")
  found = false
  options.each do |e1|
    if (e1.text == arg1)
      found = true
      e1.click()
      break
    end
  end
  assert(found, "The exact realm cannot be found")
  clickButton("go", "id")
  
end

When /^the following students have "([^"]*)" lozenges: "([^"]*)"$/ do |lozengeName, studentList|
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}

  i = 0
  studentList.split(";").each do |studentName|
     studentCell = getStudentCell(studentName)
     programParticipations = getStudentProgramParticipation(studentCell)
     found = false
     programParticipations.each do |pp|
       if (pp.text == lozengeName)
        found = true  
       end
     end
     assert(found, studentName.to_s + " doesn't have " + lozengeName.to_s)
  end
end
