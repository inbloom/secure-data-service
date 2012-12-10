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

require_relative './assessment_work_order'
require_relative '../Shared/EntityClasses/studentAssessment'

# student work order factory creates student work orders
class StudentWorkOrderFactory
  def initialize(world, scenario, section_factory)
    @world = world
    @scenario = scenario
    @section_factory = section_factory
    @next_id = 0
    @assessment_factory = AssessmentFactory.new(@scenario)
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
                                             section_factory: @section_factory,
                                             assessment_factory: @assessment_factory).to_hash
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
    @assessment_factory = opts[:assessment_factory]
    @enrollment = []
    @assessment = []
    build()
  end

  def build
    schools = [@edOrg['id']] + (@edOrg['feeds_to'] or [])
    curr_type = GradeLevelType.school_type(@initial_grade)
    @edOrg['sessions'].each{ |session|
      year = (session['year'] or @initial_year + 1)
      grade = GradeLevelType.increment(@initial_grade, year - @initial_year)
      unless grade.nil?
        if GradeLevelType.school_type(grade) != curr_type
          curr_type = GradeLevelType.school_type(grade)
          schools = schools.drop(1)
        end
        @enrollment.concat generate_enrollment(schools[0], curr_type, year, grade, session)
        @assessment.concat generate_grade_wide_assessments(grade, session)
      end
    }
  end

  def to_hash
    hash = {:type=>Student}
    instance_variables.each {|var| hash[var.to_s.delete("@").to_sym] = instance_variable_get(var) }
    hash
  end

  private

  def generate_enrollment(school_id, type, start_year, start_grade, session)
    rval = []
    rval << {:type=>StudentSchoolAssociation, :id=>@id, :schoolId=>school_id, :startYear=>start_year, :startGrade=>start_grade}
    unless @section_factory.nil?
      sections = @section_factory.sections(school_id, type.to_s, start_year, start_grade)

    unless sections.nil?
        #generate a section for each available course offering
        sections.each{|course_offering, available_sections|
          section = available_sections.to_a[id % available_sections.count]
          rval << {:type=>StudentSectionAssociation, :id=>@id, :sectionId=>section, :courseOffering => course_offering['id'], :schoolId=>school_id, :startYear=>start_year, :startGrade=>start_grade}
        }
      end
    end
    rval
  end

  def generate_grade_wide_assessments(grade, session)
    rval = []
    unless @assessment_factory.nil?
      times_taken = @scenario['ASSESSMENTS_TAKEN']['grade_wide']

      enumerator = Enumerator.new do |y|
        @assessment_factory.gen_assessments(y, grade: grade, year: session['year'])
      end

      enumerator.each do |assessment|
        #TODO this is going to be a busy first couple of days of school, might want to spread them out
        if session.nil? == false && session['interval'].nil? == false
          date = session['interval'].get_begin_date
          times_taken.times{
            rval << {:type=>StudentAssessment, :id=>@id, :assessment=>assessment, :date=>(date+=1), :rand=>@rand}
          }
        end
      end
    end
    rval
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end
end
