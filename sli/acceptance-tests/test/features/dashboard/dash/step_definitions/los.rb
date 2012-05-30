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
    return "FallSemester2011-2012"
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

