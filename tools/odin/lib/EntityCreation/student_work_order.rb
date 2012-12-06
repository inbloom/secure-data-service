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

# student work order factory creates student work orders
class StudentWorkOrderFactory
  def initialize(world, scenario, section_factory)
    @world = world
    @scenario = scenario
    @section_factory = section_factory
    @next_id = 0
  end

  def generate_work_orders(edOrg, yielder)
    students = edOrg['students']
    unless students.nil?
      years = students.keys.sort
      initial_year = years.first
      initial_grade_breakdown = students[initial_year]
      initial_grade_breakdown.each{|grade, num_students|
        (1..num_students).each{|_|
          student_id = @next_id += 1
          yielder.yield StudentWorkOrder.new(student_id, scenario: @scenario, initial_grade: grade,
                                             initial_year: initial_year, edOrg: edOrg,
                                             section_factory: @section_factory)
        }
      }
    end
  end

end

# student work order represents all data to be genreated for a given student
class StudentWorkOrder
  attr_accessor :id, :edOrg, :birth_day_after, :initial_grade, :initial_year

  def initialize(id, opts = {})
    @id = id
    @edOrg = opts[:edOrg]
    @rand = Random.new(@id)
    @initial_grade = (opts[:initial_grade] or :KINDERGARTEN)
    @initial_year = (opts[:initial_year] or 2011)
    @birth_day_after = Date.new(@initial_year - find_age(@initial_grade),9,1)
    @section_factory = opts[:section_factory]
    @scenario = opts[:scenario]
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
        generate_enrollment(writer, schools[0], curr_type, year, grade, session)
      end
    }
  end

  private

  def generate_enrollment(writer, school_id, type, start_year, start_grade, session)
    writer.create_student_school_association(@id, school_id, start_year, start_grade)
    unless @section_factory.nil?
      sections = @section_factory.sections(school_id, type.to_s, start_year, start_grade)
      unless sections.nil?
        #generate a section for each available course offering
        sections.each{|course_offering, available_sections|
          section = available_sections.to_a[id % available_sections.count]
          writer.create_student_section_association(@id, section, course_offering['id'],
                                                    school_id, start_year, start_grade)
        }
      end
    end
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end
end
