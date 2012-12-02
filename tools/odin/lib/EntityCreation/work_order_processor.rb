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
require_relative 'student_work_order'

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
