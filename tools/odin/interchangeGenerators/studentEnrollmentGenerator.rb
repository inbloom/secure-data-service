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

require_relative "./interchangeGenerator.rb"
Dir["#{File.dirname(__FILE__)}/../baseEntityClasses/*.rb"].each { |f| load(f) }

class StudentEnrollmentGenerator < InterchangeGenerator
  def initialize
    @header = <<-HEADER
<?xml version="1.0"?>
<InterchangeStudentEnrollment xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/domain/src/main/resources/edfiXsd-SLI/SLI-Interchange-StudentEnrollment.xsd ">
HEADER
    @footer = <<-FOOTER
</InterchangeStudentEnrollment>
FOOTER
  end

  def write(prng, students, yamlHash)
    File.open("generated/InterchangeStudentEnrollment.xml", 'w') do |f|
      f.write(@header)
      (0..(1.0*students/yamlHash['studentsPerSchool']).ceil - 1).to_a.each do |schoolId|
        (schoolId*yamlHash['studentsPerSchool']..((schoolId+1) * yamlHash['studentsPerSchool'])-1).to_a.each do |studentId|
          ssa = StudentSchoolAssociation.new studentId, schoolId, prng
          f.write(ssa.render)
        end
        
      end
      

      f.write(@footer)
    end
  end
  
end