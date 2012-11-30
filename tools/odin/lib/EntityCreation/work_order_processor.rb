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

  def self.run(world, batch_size)
    File.open("generated/InterchangeStudentParent.xml", 'w') do |studentParentFile|
      studentParent = StudentParentInterchangeGenerator.new(studentParentFile, batch_size)
      studentParent.start
      File.open("generated/InterchangeStudentEnrollment.xml", 'w') do |enrollmentFile|
        enrollment = EnrollmentGenerator.new(enrollmentFile, batch_size)
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
    Enumerator.new do |y|
      student_id = 0
      world.each{|_, edOrgs|
        edOrgs.each{|edOrg|
          students = edOrg['students']
          unless students.nil?
            years = students.keys.sort
            initial_year = years.first
            initial_grade_breakdown = students[initial_year]
            initial_grade_breakdown.each{|grade, num_students|
              sessions = edOrg['sessions'].map{|session| make_session(edOrg['id'], session)}
              (1..num_students).each{|_|
                student_id += 1
                y.yield StudentWorkOrder.new(student_id, grade, initial_year, sessions)
              }
            }
          end
        }
      }
    end
  end

  private

  def self.make_session(school, session)
    {:school => school, :sessionInfo => session}
  end

end

class StudentWorkOrder
  attr_accessor :id, :sessions, :birth_day_after, :initial_grade, :initial_year

  def initialize(id, initial_grade, initial_year, sessions)
    @id = id
    @sessions = sessions
    @rand = Random.new(@id)
    @birth_day_after = Date.new(2000,9,1) #TODO fix this once I figure out what age they should be
    @initial_grade = initial_grade
    @initial_year = initial_year
  end

  def build(interchanges)
    @student_interchange = interchanges[:studentParent]
    @enrollment_interchange = interchanges[:enrollment]
    s = Student.new(@id, @birth_day_after)
    @student_interchange << s unless @student_interchange.nil?
    unless @enrollment_interchange.nil?
      schools = Set.new(@sessions.map{|s| s[:school]})
      schools.each{ |school|
        gen_enrollment(school, @initial_year, @initial_grade)
      }
    end
  end

  private

  def gen_enrollment(school_id, start_year, start_grade)
    schoolAssoc = StudentSchoolAssociation.new(@id, school_id, start_year, start_grade)
    @enrollment_interchange << schoolAssoc
  end

end
