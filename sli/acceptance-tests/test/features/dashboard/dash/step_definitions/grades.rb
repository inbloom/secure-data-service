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


Then /^the grades teardrop color widgets for "([^"]*)" are mapped correctly:$/ do |gradeColumns, table|
  gradeColumnsArray = gradeColumns.split(';')
  gradeMapping = Hash.new 
  table.hashes.each do |row|
    key = row['grade'].rstrip.lstrip
    value = row['teardrop'].rstrip.lstrip
    gradeMapping[key] = value
  end
  all_trs = getStudentGrid()
   
  all_trs.each do |tr|
    gradeColumnsArray.each do |column|
      foundGrade = getAttribute(tr, column)
      tdsWithGrade = getTdBasedOnAttribute(tr, column)
      if (gradeMapping[foundGrade] == nil)
          puts "Grade Mapping doesn't exist for grade: " + foundGrade
      else
          searchText = "div[class*='" + gradeMapping[foundGrade] + "']"
          tearDrop = tdsWithGrade.find_element(:css, searchText)
          assert(tearDrop != nil, "Expected color" + gradeMapping[foundGrade])
      end
    end
  end 
end

Then /^the "([^"]*)" grade for "([^"]*)" is "([^"]*)"$/ do |gradePeriod, studentName, grade|
  attribute = ""
  if (gradePeriod == "current")
    attribute = "currentSession-0"
  elsif (gradePeriod == "last semester")
    attribute = "previousSemester"
  elsif (gradePeriod == "2 semesters ago")
    attribute = "twoSemestersAgo"
  else
    puts "invalid grade period: " + gradePeriod
  end
  studentCell = getStudentCell(studentName)
  gradeFound = getAttribute(studentCell, attribute)
  assert(gradeFound==grade, "Expected: " + grade + " Actual: " + gradeFound)
end

def letterGradeMapping(grade)
  mapping = {"F-" => 0,
             "F" => 1,
             "F+" => 2, 
             "D-" => 3,
             "D" => 4, 
             "D+" => 5,
             "C-" => 6, 
             "C" => 7,
             "C+" => 8,
             "B-" => 9,
             "B" => 10,
             "B+" => 11,
             "A-" => 12,
             "A" => 13, 
             "A+" => 14}
  
  return mapping[grade]
end
