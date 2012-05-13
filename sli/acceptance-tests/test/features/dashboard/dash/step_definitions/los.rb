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
  return getAttributes(studentTr, getStudentProgramParticipationColumnName())
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
  else
    assert(true, "unknown header name: " + headerName)
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
