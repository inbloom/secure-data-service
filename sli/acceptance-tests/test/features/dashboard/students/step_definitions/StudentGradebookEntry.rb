Then /^I should see the name "([^"]*)" in student field with link$/ do |studentName|
  student = @driver.find_element(:id, studentName+".name_w_link")
  student.text.should include studentName
  @studentName = studentName
end

Then /^I should see his\/her current average grade is "([^"]*)"$/ do |avgGradeResult|
  grade = @driver.find_element(:id, @studentName+".currentTermGrade")
  grade.should_not be_nil
  grade.text.should == avgGradeResult
end