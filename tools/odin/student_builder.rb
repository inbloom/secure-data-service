=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require_relative 'baseEntityClasses/student.rb'

class StudentBuilder

  def initialize(work_order, files)
    @id = work_order.id
    @rand = Random.new(id)
    @work_order = work_order
    @student_writer = files.studentParent
    @enrollment_writer = files.enrollment
  end

  def build
    s = Student.new(id, work_order.student_demographics, rand)
    @student_writer.write(s.render)
    (0..@work_order.enrollments).each{ |term|
      gen_enrollment(term)
    }
  end

  def gen_enrollment(term)
    school_id = term.school
    schoolAssoc = StudentSchoolAssociation.new(@id, school_id, @rand)
    @enrollment_writer.write(schoolAssoc.render)
    (0..term.sections).each{ |section|
      gen_section(id, section, writer, rand)
    }
  end

  def gen_section(id, section, writer, rand)
    sectionAssoc = StudentSectionAssociation.new(id, section.id)
    writer.write(sectionAssoc.render)
  end

end
