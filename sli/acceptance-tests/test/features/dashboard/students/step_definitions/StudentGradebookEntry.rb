Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "fe4374c2-6bca-498c-949a-4a06e348edf2"   if human_readable_id == "FIRST UNIT TEST"
  id = "6d590861-27e2-4253-a64f-dc97b4c185b0"   if human_readable_id == "SECOND UNIT TEST"
  id = "0c6b108b-c290-46bd-a19d-a183b491f35a"   if human_readable_id == "THIRD UNIT TEST"
  #return the translated value
  id
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