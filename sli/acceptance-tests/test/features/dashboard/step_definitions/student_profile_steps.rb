require 'selenium-webdriver'
require_relative '../../utils/sli_utils.rb'
require_relative 'lozenges.rb'

When /^I click on student, their name shows up on student profile$/ do 
  studentTable = @driver.find_element(:id, "studentList");
  student_cells = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  
  x = student_cells[0].find_elements(:tag_name, "a")
  student_name = x[0].text
  x[0].click()
  z = @driver.find_element(:id, "InnerStudentProfile")
  
  assert(z.text == student_name)
  end
