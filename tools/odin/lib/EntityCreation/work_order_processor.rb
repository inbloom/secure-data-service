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

require_relative '../OutputGeneration/XML/studentGenerator'
require_relative '../OutputGeneration/XML/enrollmentGenerator'
require_relative '../Shared/EntityClasses/student.rb'
require_relative '../Shared/EntityClasses/studentSchoolAssociation.rb'
require_relative '../Shared/EntityClasses/studentSectionAssociation.rb'

class WorkOrderProcessor

  def initialize(work_order, interchanges)
    @id = work_order[:id]
    @rand = Random.new(@id)
    @work_order = work_order
    @student_interchange = interchanges[:studentParent]
    @enrollment_interchange = interchanges[:enrollment]
  end

  def build
    s = Student.new(@id, @work_order[:birth_day_after])
    @student_interchange << s unless @student_interchange.nil?
    unless @enrollment_interchange.nil?
      @work_order[:sessions].each{ |session|
        gen_enrollment session
      }
    end
  end

  def gen_enrollment(session)
    school_id = session[:school]
    schoolAssoc = StudentSchoolAssociation.new(@id, school_id)
    @enrollment_interchange << schoolAssoc
  end

end

def run_work_orders(yamlHash, batch_size)
  numSchools = (1.0*yamlHash['studentCount']/yamlHash['studentsPerSchool']).ceil
  File.open("generated/InterchangeStudent.xml", 'w') do |studentParentFile|
    studentParent = StudentGenerator.new(studentParentFile, batch_size)
    File.open("generated/InterchangeStudentEnrollment.xml", 'w') do |enrollmentFile|
      enrollment = EnrollmentGenerator.new(enrollmentFile, batch_size)
      interchanges = {:studentParent => studentParent, :enrollment => enrollment}
      for id in 1..yamlHash['studentCount'] do
        work_order = make_work_order(id, yamlHash, numSchools)
        WorkOrderProcessor.new(work_order, interchanges).build
      end
      enrollment.finalize
    end
    studentParent.finalize
  end
end

#TODO this is a mocked out work order, make one more intelligent and relating to the world
def make_work_order(id, yamlHash, numSchools)
  {:id => id, :sessions => (1..yamlHash['numYears']).map{|i| {:school => i % numSchools, :sections => []}},
              :birth_day_after => Date.new(2000, 9, 1)}
end
