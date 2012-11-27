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
    attr_accessor :students

    def initialize(students)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/student_generator/student.mustache"
      @students = students
    end

  end


  def initialize(interchange, batchSize)
    super(interchange)
    @batchSize = batchSize

    @header, @footer = build_header_footer( "StudentParent" )
    @students = []

    start()
  end

  def <<(student)
    @students << student
    if @students.size >= @batchSize
      batchRender
      @students = []
    end
  end

  def batchRender
    report(@students)
    generator = StudentGenerator::StudentInterchange.new @students
    @interchange << generator.render()
  end

  def finalize
    batchRender
    super
  end

end
