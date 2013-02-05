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

Then /^The students who have an ELL lozenge exist in the API$/ do
  #todo: grab a token id from api 
  @sessionId = "4cf7a5d4-37a1-ca19-8b13-b5f95131ac85"
  
  students_w_lozenges = getStudentsWithELLLozenge()
  students_w_lozenges.each do |student_id|
    urlHeader = makeUrlAndHeaders('get' ,"/v1/students/"+student_id, @sessionId, @format)

    @res = RestClient.get(urlHeader[:url], urlHeader[:headers]){|response, request, result| response }
    @result = JSON.parse(@res.body)
    assert(@result["limitedEnglishProficiency"].to_s == "Limited")
  end  
end

def getStudentsWithELLLozenge()
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_trs = studentTable.find_elements(:css,"tr[class*='ui-widget']")
  students_with_lozenges = []
  i = 0
  all_trs.each do |tr|
   fullName = tr.find_element(:css, "td[aria-describedby*='name.fullName']")
   programParticipation = tr.find_element(:css, "td[aria-describedby*='programParticipation'][title='ELL']")
   if (programParticipation.length > 0)
    students_with_lozenges[i] = fullName
    i+=1
   end
  end  
  return students_with_lozenges    
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
