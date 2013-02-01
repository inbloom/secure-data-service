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


When /^"([^"]*)" has no "([^"]*)"$/ do |student, attr|
  studentCell = getStudentCell(student)
  td = getAttribute(studentCell, attr).strip
  assert(td.length == 0, "Non-empty value found" + td)
end


# This will return all the TRs of the a grid, 1 for each student
def getStudentGrid()
   return getGrid(@driver)
end

# This will give the tr of the student in los
def getStudentCell (student_name)
  all_trs = getStudentGrid()

  studentCell = nil
  all_trs.each do |tr|
    if tr.attribute("innerHTML").to_s.include?(student_name)
      studentCell = tr
      break
    end
  end  

  return studentCell
end

#TODO call studentAttribute inside here
def getStudentName(studentTr)
  return getAttribute(studentTr, getStudentColumnName())
end

def getStudentProgramParticipation(studentTr)
  td = getTdBasedOnAttribute(studentTr, getStudentProgramParticipationColumnName())
  programParticipations = td.find_elements(:tag_name, "span")
  return programParticipations
end

#returns an array of grades
def getStudentGrades(studentTr)
  return getAttributes(studentTr, getGradeColumnName())
end


def getColumnLookupName(headerName)
  headerName.downcase!
  if (headerName == "student")
    return getStudentColumnName()
  elsif (headerName == "absence count")
    return getAbsenceCountColumnName()
  elsif (headerName == "statetest reading performance level") 
    return "StateTest Reading.perfLevel"
  elsif (headerName == "statetest writing performance level") 
    return "StateTest Writing.perfLevel"
  elsif (headerName == "unit test 1")
    return "currentSession-0"
  else
    assert(false, "unknown header name: " + headerName)
  end
end

def getStudentColumnName()
  return "fullName"
end

def getStudentProgramParticipationColumnName()
  return "programParticipation"
end

def getAbsenceCountColumnName()
  return "absenceCount"
end

