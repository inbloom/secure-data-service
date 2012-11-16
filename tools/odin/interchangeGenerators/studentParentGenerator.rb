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
require_relative "../student_builder.rb"

Dir["#{File.dirname(__FILE__)}/../baseEntityClasses/*.rb"].each { |f| load(f) }

class StudentParentGenerator < InterchangeGenerator
  def initialize
    @header = <<-HEADER
<?xml version="1.0"?>
<InterchangeStudentParent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/domain/src/main/resources/edfiXsd-SLI/SLI-Interchange-StudentParent.xsd ">
HEADER
    @footer = <<-FOOTER
</InterchangeStudentParent>
FOOTER
  end

  def write(prng, yamlHash)
    stime = Time.now
    File.open("generated/InterchangeStudentParent.xml", 'w') do |f|
      f.write(@header)
      interchanges = {:studentParent => f}
      for id in 1..yamlHash['studentCount'] do
        work_order = {:id => id, :sessions => []}
        builder = StudentBuilder.new(work_order, interchanges)
        builder.build

        if (id % 100000 == 0)
          puts "\t#{id} students enrolled."
        end

      end
      f.write(@footer)
    end
    elapsed = Time.now - stime
    puts "\t#{yamlHash['studentCount']} students generated in #{elapsed} seconds."
  end
  
end
