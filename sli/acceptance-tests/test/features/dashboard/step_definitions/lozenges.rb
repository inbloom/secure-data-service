Then /^the lozenge for student "([^"]*)" include "([^"]*)"$/ do |student_name, lozenge_label|

  # first, find the table cell for that student. 
  studentCell = getStudentCell(student_name)
  assert(!studentCell.nil?, "Student '" + student_name + "' was not found in studnet list table.")

  # Then, make sure the lozenge label is as expected
  labelFound = false
  all_lozengeSpans = studentCell.find_elements(:tag_name, "span")
  all_lozengeSpans.each do |lozengeSpan|
    if lozengeSpan.attribute("innerHTML").to_s.include?(lozenge_label)
      labelFound = true
    end

#   Apparently Selenium doesn't handle svg elements. 
#     lozengeLabels = lozengeSpan.find_elements(:xpath, "./svg//tspan");
#     lozengeLabels.each do |lozengeLabel|
#       puts lozengeLabel.attribute("innerHTML").to_s
#     end

  end

  assert(labelFound, "Lozenge '" + lozenge_label + "' was not found for the student " + student_name);

end


Then /^there is no lozenges for student "([^"]*)"$/ do |student_name|

  # first, find the table cell for that student. 
  studentCell = getStudentCell(student_name)
  assert(!studentCell.nil?, "Student '" + student_name + "' was not found in studnet list table.")

  # Then, make sure there is no lozenge label
  all_lozengeSpans = studentCell.find_elements(:tag_name, "span")
  assert(all_lozengeSpans.length == 0, "Student '" + student_name + "' should have no spans in his cell");

end

def getStudentCell (student_name)
  studentTable = @driver.find_element(:id, "studentList");
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name']")

  studentCell = nil
  all_tds.each do |td|
    if td.attribute("innerHTML").to_s.include?(student_name)
      studentCell = td
    end
  end  

  return studentCell
end
