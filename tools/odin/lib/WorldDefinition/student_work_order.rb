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

require_relative 'world_builder'
require_relative './assessment_work_order'
require_relative '../Shared/EntityClasses/studentAssessment'
require_relative 'graduation_plan_factory'

# student work order factory creates student work orders
class StudentWorkOrderFactory
  def initialize(world, scenario, section_factory)
    @world = world
    @scenario = scenario
    @section_factory = section_factory
    @next_id = 0
    @assessment_factory = AssessmentFactory.new(@scenario)
    sea = world['seas'][0] unless world['seas'].nil?
    @graduation_plans = GraduationPlanFactory.new(sea['id'], @scenario).build unless sea.nil?
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
                                             assessment_factory: @assessment_factory,
                                             graduation_plans: @graduation_plans)
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
    @scenario = (opts[:scenario] or {})
    @assessment_factory = opts[:assessment_factory]
    @graduation_plans = opts[:graduation_plans]
    @enrollment = []
    @assessment = []
  end

  def build
    student = Student.new(@id, @birth_day_after)
    [student] + per_year_info + parents(student)
  end

  private

  def parents(student)
    if @scenario['INCLUDE_PARENTS']
      [:mom, :dad].map{|type|
        [Parent.new(student, type), StudentParentAssociation.new(student, type)]}.flatten
    else
      []
    end
  end

  def per_year_info
    generated = []
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
        school = schools[0]
        generated += generate_enrollment(school, curr_type, year, grade, session)
        generated += generate_grade_wide_assessment_info(grade, session)
        generated += generate_cohorts(school, curr_type, session)
      end
    }
    generated
  end

  def generate_enrollment(school_id, type, start_year, start_grade, session)
    rval = []
    rval << StudentSchoolAssociation.new(@id, school_id, start_year, start_grade, graduation_plan(type))
    unless @section_factory.nil?
      sections = @section_factory.sections(school_id, type.to_s, start_year, start_grade)
      unless sections.nil?
        #generate a section for each available course offering
        sections.each{|course_offering, available_sections|
          section = available_sections.to_a[id % available_sections.count]
          rval << StudentSectionAssociation.new(@id, DataUtility.get_unique_section_id(section),
                                                school_id, start_year, start_grade)
        }
      end
    end
    rval
  end

  def graduation_plan(school_type)
    if school_type == :high
      @graduation_plans[@id % @graduation_plans.size] unless @graduation_plans.nil?
    end
  end

  def generate_grade_wide_assessment_info(grade, session)
    student_assessments = grade_wide_student_assessments(grade, session)
    student_assessment_items = generate_student_assessment_items(student_assessments)
    student_assessments + student_assessment_items
  end

  def grade_wide_student_assessments(grade, session)
    unless @assessment_factory.nil?
      times_taken = @scenario['ASSESSMENTS_TAKEN']['grade_wide']

      @assessment_factory.grade_wide_assessments(grade, session['year']).map{ |assessment|
        #TODO this is going to be a busy first couple of days of school, might want to spread them out
        if session.nil? == false && session['interval'].nil? == false
          start_date = session['interval'].get_begin_date + 1
          end_date = start_date + times_taken -1
          (start_date..end_date).map{ |date|
            StudentAssessment.new(@id, assessment, date, @rand)
          }
        end
      }.flatten
    end or []
  end

  def generate_student_assessment_items(student_assessments)
    student_assessments.map{|sa|
      sa.assessment.assessment_items.map{|item|
        StudentAssessmentItem.new(sa.studentId.odd?, sa, item)
      }
    }.flatten
  end

  def generate_cohorts(school, school_type, session)
    cohorts = WorldBuilder.cohorts(DataUtility.get_school_id(school, school_type), @scenario)
    cohorts.map{|cohort|
      prob = @scenario['PROBABILITY_STUDENT_IN_COHORT'].to_f
      if(@rand.rand < prob)
        StudentCohortAssociation.new(@id, cohort, session['interval'].get_begin_date, (@scenario['DAYS_IN_COHORT'] or -1))
      end
    }.select{|c| not c.nil?}
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end
end
