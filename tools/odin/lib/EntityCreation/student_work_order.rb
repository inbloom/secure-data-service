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

# student work order represents all data to be genreated for a given student
class StudentWorkOrder
  attr_accessor :id, :edOrg, :birth_day_after, :initial_grade, :initial_year

  def self.generate_work_orders(students, edOrg, start_with_id, yielder)
    student_id = start_with_id
    years = students.keys.sort
    initial_year = years.first
    initial_grade_breakdown = students[initial_year]
    initial_grade_breakdown.each{|grade, num_students|
      (1..num_students).each{|_|
        student_id += 1
        yielder.yield StudentWorkOrder.new(student_id, initial_grade: grade, initial_year: initial_year, edOrg: edOrg)
      }
    }
    student_id
  end

  def initialize(id, opts = {})
    @id = id
    @edOrg = opts[:edOrg]
    @rand = Random.new(@id)
    @initial_grade = (opts[:initial_grade] or :KINDERGARTEN)
    @initial_year = (opts[:initial_year] or 2011)
    @sections = (opts[:sections] or {})
    @birth_day_after = Date.new(@initial_year - find_age(@initial_grade),9,1)
  end

  def build(writer)
    writer.create_student(@id, @birth_day_after)
    schools = [@edOrg['id']] + (@edOrg['feeds_to'] or [])
    curr_type = GradeLevelType.school_type(@initial_grade)
    @edOrg['sessions'].each{ |session|
      year = session['year']
      grade = GradeLevelType.increment(@initial_grade, year - @initial_year)
      unless grade.nil?
        if GradeLevelType.school_type(grade) != curr_type
          curr_type = GradeLevelType.school_type(grade)
          schools = schools.drop(1)
        end
        generate_enrollment(writer, schools[0], year, grade, session, @sections[year])
      end
    }
  end

  private

  def generate_enrollment(writer, school_id, start_year, start_grade, session, sections)
    writer.create_student_school_association(@id, school_id, start_year, start_grade)
    unless sections.nil?
      sections_per_student = 5
      section_cycle = sections.cycle
      @rand.rand(sections.count).times { section_cycle.next }
      sections_per_student.times {
        section = section_cycle.next
        writer.create_student_section_association(@id, section, school_id, start_year, start_grade)
      }
    end
  end

  def self.make_session(school, session)
    {:school => school, :sessionInfo => session}
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end
end
