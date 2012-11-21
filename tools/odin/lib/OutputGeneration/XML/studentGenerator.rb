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

    def initialize(entities)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/student_generator/student_interchange.mustache"
      #@students = []
      #entities.each do |entity|
      #  @students << entity
      #end
      @students = entities
    end

    def students
      @students
    end
  end

  def write(entities)
    stime = Time.now
    File.open("generated/InterchangeStudent.xml", 'w') do |student|
      g = StudentGenerator::StudentInterchange.new(entities)
      student << g.render()
    end
    elapsed = Time.now - stime
    puts "\t#{entities.length} students written in #{elapsed} seconds."
  end
end
