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

  def write(prng, yamlHash)
    stime = Time.now
    File.open("generated/InterchangeStudentEnrollment.xml", 'w') do |f|
      f.write(@header)
      studentCount = 0
      for schoolId in 1..(1.0*yamlHash['studentCount'] / yamlHash['studentsPerSchool']).ceil do
        for studentId in 1..(yamlHash['studentsPerSchool']) do
          ssa = StudentSchoolAssociation.new studentId+studentCount, schoolId, prng
          f.write(ssa.render)
        end
        studentCount = studentCount + yamlHash['studentsPerSchool']
        if (studentCount % 100000 == 0)
          puts "\t#{studentCount} students enrolled."
        end
      end
      f.write(@footer)
    end
    elapsed = Time.now - stime
    puts "\t#{yamlHash['studentCount']} student enrollments generated in #{elapsed} seconds."
  end
  
end