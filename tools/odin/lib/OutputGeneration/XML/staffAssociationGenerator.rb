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

require_relative "./EntityWriter"
require_relative "./interchangeGenerator"
require_relative "../../Shared/util"

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# event-based staff association interchange generator 
class StaffAssociationGenerator < InterchangeGenerator

  # initialization will define the header and footer for the staff association interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer( "StaffAssociation" )    
    @writers[ Staff ] = EntityWriter.new("staff.mustache")
    @writers[ StaffEducationOrgAssignmentAssociation ] = EntityWriter.new("staff_ed_org_assignment_association.mustache")
    #@writers[ Teacher ] = EntityWriter.new("teacher.mustache")
    #@writers[ TeacherSchoolAssociation ] = EntityWriter.new("teacher_school_association.mustache")
    #@writers[ TeacherSectionAssociation ] = EntityWriter.new("teacher_section_association.mustache")
  end

  # creates and writes staff to interchange
  def create_staff(id, year_of, name = nil)
    self << Staff.new(id, year_of, name)
  end

  # creates and writes staff education organization assignment association to interchange
  def create_staff_ed_org_assignment_association(staff, ed_org, classification, title, begin_date, end_date = nil)
    self << StaffEducationOrgAssignmentAssociation.new(staff, ed_org, classification, title, begin_date, end_date)
  end

  # creates and writes staff program association to interchange --> not currently implemented
  #def create_staff_program_association
  #end

  # creates and writes teacher to interchange
  #def create_teacher
  #end

  # creates and writes teacher school association to interchange
  #def create_teacher_school_association
  #end

  # creates and writes teacher section association to interchange
  #def create_teacher_section_association
  #end
end
