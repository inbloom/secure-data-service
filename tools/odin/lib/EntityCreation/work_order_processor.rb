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

require 'set'

require_relative '../OutputGeneration/XML/studentParentInterchangeGenerator'
require_relative '../OutputGeneration/XML/enrollmentGenerator'
require_relative '../Shared/EntityClasses/student.rb'
require_relative '../Shared/EntityClasses/studentSchoolAssociation.rb'
require_relative '../Shared/EntityClasses/studentSectionAssociation.rb'

class WorkOrderProcessor

  def initialize(interchanges)
    @interchanges = interchanges
  end

  def build(work_order)
    work_order.build(@interchanges)
  end

  def self.run(world,  scenarioYAML)
    File.open("generated/InterchangeStudentParent.xml", 'w') do |studentParentFile|
      studentParent = StudentParentInterchangeGenerator.new(scenarioYAML, studentParentFile)
      studentParent.start
      File.open("generated/InterchangeStudentEnrollment.xml", 'w') do |enrollmentFile|
        enrollment = EnrollmentGenerator.new(scenarioYAML, enrollmentFile)
        enrollment.start
        interchanges = {:studentParent => studentParent, :enrollment => enrollment}
        processor = WorkOrderProcessor.new(interchanges)
        for work_order in gen_work_orders(world) do
          processor.build(work_order)
        end
        enrollment.finalize
      end
      studentParent.finalize
    end
  end

  def self.gen_work_orders(world)
    next_student = 0
    Enumerator.new do |y|
      world.each{|_, edOrgs|
        edOrgs.each{|edOrg|
          students = edOrg['students']
          unless students.nil?
            next_student = StudentWorkOrder.gen_work_orders(students, edOrg, next_student, y)
          end
        }
      }
    end
  end

end

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
        grade = GradeLevelType.increment(@initial_grade, year - @initial_year, false)
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
