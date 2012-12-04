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

require_relative "./EntityWriter"
require_relative "./interchangeGenerator"
require_relative "../../Shared/EntityClasses/studentSchoolAssociation"
require_relative "../../Shared/util"

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

class EnrollmentGenerator < InterchangeGenerator

  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer( "StudentEnrollment" )

    @writers[StudentSchoolAssociation] = EntityWriter.new("student_school_association.mustache")
    @writers[StudentSectionAssociation] = EntityWriter.new("student_section_association.mustache")
  end

  # writes student school association to student enrollment interchange
  def create_student_school_association(student_id, school_id, year, grade)
    self.<< StudentSchoolAssociation.new(student_id, school_id, year, grade)
  end

  # writes student section association to student enrollment interchange
  def create_student_section_association(student_id, section_code, school_id, year, grade)
    self.<< StudentSectionAssociation.new(student_id, {'id' => section_code}, school_id, year, grade)
  end
end
