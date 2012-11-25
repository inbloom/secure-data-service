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
require 'mustache'

require_relative "./interchangeGenerator"
require_relative "../../Shared/EntityClasses/student"

class StudentGenerator < InterchangeGenerator

  class StudentInterchange < Mustache

    def initialize()
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/student_generator/student.mustache"
      @students = []
    end

    def set(entities)
      @students = entities
    end

    def students
      @students
    end
  end


  def initialize(filename)
    super(filename)

    @generator = StudentGenerator::StudentInterchange.new

    @header = <<-HEADER
<InterchangeStudentParent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
  xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/edfi-schema/src/main/resources/edfiXsd/Interchange-StudentParent.xsd">
    HEADER
    @footer = <<-FOOTER
</InterchangeStudentParent>
    FOOTER

    start()
  end

  def <<(entities)
    super(entities)
    @generator.set(entities)
    @interchange << @generator.render()
  end

end
