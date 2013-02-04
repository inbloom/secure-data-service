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


Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "da5b4d1a-63a3-46d6-a4f1-396b3308af83_id6e42d32c-2be3-45de-97fe-894d4c065aa2_id"   if human_readable_id == "Matt Sollars FIRST UNIT TEST"
  id = "da5b4d1a-63a3-46d6-a4f1-396b3308af83_idc92277ec-a8f1-47e2-bc6e-719cc761deae_id"   if human_readable_id == "Matt Sollars SECOND UNIT TEST"
  id = "da5b4d1a-63a3-46d6-a4f1-396b3308af83_id00f627d7-1ccd-4c63-a1b3-64e104ec73de_id"   if human_readable_id == "Matt Sollars THIRD UNIT TEST"
  id = "c319cf6a-4f86-453c-9074-f37ebd8e6227"   if human_readable_id == "Carmen Ortiz CURRENT GRADE"
  #return the translated value
  id
end

When /^I select "([^"]*)" and click go$/ do |arg1|
  realm_select = @explicitWait.until{@driver.find_element(:name=> "realmId")}

  options = realm_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1.text == arg1)
      e1.click()
      break
    end
  end
  clickButton("go", "id")

end

When /^I login as "([^"]*)" "([^"]*)"/ do | username, password |
  @explicitWait.until{@driver.find_element(:id, "IDToken1")}.send_keys username
  @driver.find_element(:id, "IDToken2").send_keys password
  @driver.find_element(:name, "Login.Submit").click
  # Catches the encryption pop up seen in local box set up
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

Then /^I see a list of students$/ do
  studentList = @explicitWait.until{@driver.find_element(:id, "studentList")}
end

Then /^I should see the name "([^"]*)" in student field with link$/ do |studentName|
  student = @driver.find_element(:id, studentName+".name_w_link")
  student.text.should include studentName
  @studentName = studentName
end

Then /^I look at his\/her test grades$/ do
  @totalScore = 0
  @numberOfTests = 0
end

Then /^I should see his\/her "([^"]*)" grade is (\d+)$/ do |testId, testResult|
  grade = @driver.find_element(:id, @studentName+"."+testId)
  grade.should_not be_nil
  grade.text.should == testResult
  @totalScore = @totalScore + testResult.to_i
  @numberOfTests = @numberOfTests + 1
end

Then /^I should see his\/her current average grade is "([^"]*)"$/ do |avgGradeResult|
  grade = @driver.find_element(:id, @studentName+".currentTermGrade")
  grade.should_not be_nil
  grade.text.should == avgGradeResult
  @avgGrade = avgGradeResult.gsub(/$+/, "")
end

Then /^the current average grade is calculated correctly$/ do
  assert((@totalScore/@numberOfTests).round == @avgGrade.to_i, "Average grade is not calculated correctly")
end
