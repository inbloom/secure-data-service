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
require_relative "../../Shared/EntityClasses/studentSchoolAssociation"

class StudentEnrollment < Mustache
  attr_accessor :studentSchools

  def initialize(entities)
    @studentSchools = entities
  end

  def self.template_path
    "#{File.dirname(__FILE__)}/interchangeTemplates"
  end
    
end

class EnrollmentGenerator < InterchangeGenerator

  def initialize(interchange, batchSize)
    super(interchange, batchSize)

    @header, @footer = build_header_footer( "StudentEnrollment" )
    @generator = StudentEnrollment 

    start()
  end

end
