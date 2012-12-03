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

class StudentWorkOrder
  attr_accessor :id, :edOrg, :birth_day_after, :initial_grade, :initial_year

  def self.gen_work_orders(students, edOrg, start_with_id, yielder)
    student_id = start_with_id
    years = students.keys.sort
    initial_year = years.first
    initial_grade_breakdown = students[initial_year]
    initial_grade_breakdown.each{|grade, num_students|
      (1..num_students).each{|_|
        student_id += 1
        yielder.yield StudentWorkOrder.new(student_id, grade, initial_year, edOrg)
      }
    }
    student_id
 end

  def initialize(id, initial_grade, initial_year, edOrg)
    @id = id
    @edOrg = edOrg
    @rand = Random.new(@id)
    @birth_day_after = Date.new(initial_year - find_age(initial_grade),9,1)
    @initial_grade = initial_grade
    @initial_year = initial_year
  end

  def build(interchanges)
    @student_interchange = interchanges[:studentParent]
    @enrollment_interchange = interchanges[:enrollment]
    student = Student.new(@id, @birth_day_after)
    @student_interchange << student unless @student_interchange.nil?
    schools = [@edOrg['id']] + (@edOrg['feeds_to'] or [])
    curr_type = GradeLevelType.school_type(@initial_grade)
    unless @enrollment_interchange.nil?
      @edOrg['sessions'].each{ |session|
        year = session['year']
        grade = GradeLevelType.increment(@initial_grade, year - @initial_year)
        unless grade.nil?
          if GradeLevelType.school_type(grade) != curr_type
            curr_type = GradeLevelType.school_type(grade)
            schools = schools.drop(1)
          end
          gen_enrollment(schools[0], year, grade)
        end
      }
    end
  end

  private

  def gen_enrollment(school_id, start_year, start_grade)
    schoolAssoc = StudentSchoolAssociation.new(@id, school_id, start_year, start_grade)
    @enrollment_interchange << schoolAssoc
  end

  def self.make_session(school, session)
    {:school => school, :sessionInfo => session}
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end

end
