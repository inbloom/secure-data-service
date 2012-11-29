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

require "mustache"

require_relative "interchangeGenerator.rb"
require_relative "../../Shared/data_utility.rb"
require_relative "../../Shared/util.rb"

Dir["#{File.dirname(__FILE__)}/../EntityClasses/*.rb"].each { |f| load(f) }

# event-based education organization interchange generator
class EducationOrganizationGenerator < InterchangeGenerator

  # state education agency writer
  class StateEducationAgencyWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/state_education_organization.mustache"
      @entity = nil
    end
    def state_education_agency
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # local education agency writer
  class LocalEducationAgencyWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/local_education_organization.mustache"
      @entity = nil
    end
    def local_education_agency
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # school writer
  class SchoolWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/school.mustache"
      @entity = nil
    end
    def school
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # course writer
  class CourseWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/course.mustache"
      @entity = nil
    end
    def course
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # initialization will define the header and footer for the education organization interchange
  # writes header to education organization interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize
    @header, @footer = build_header_footer("EducationOrganization")
    
    @handle = File.new("generated/InterchangeEducationOrganization.xml", 'w')
    @handle.write(@header)

    @state_education_agency_writer = StateEducationAgencyWriter.new
    @local_education_agency_writer = LocalEducationAgencyWriter.new
    @school_writer                 = SchoolWriter.new
    @course_writer                 = CourseWriter.new
  end

  # creates and writes state education agency to interchange
  def create_state_education_agency(rand, id)
    @handle.write @state_education_agency_writer.write(SeaEducationOrganization.new(id, rand))
  end

  # creates and writes local education agency to interchange
  def create_local_education_agency(rand, id, parent_id)
    @handle.write @local_education_agency_writer.write(LeaEducationOrganization.new(id, parent_id, rand))
  end

  # creates and writes school to interchange
  def create_school(rand, id, parent_id, type)
    @handle.write @school_writer.write(SchoolEducationOrganization.new(rand, id, parent_id, type))
  end

  # creates and writes course to interchange
  def create_course(rand, id, title, ed_org_id)
    @handle.write @course_writer.write(Course.new(id, title, ed_org_id))
  end

  # creates and writes program to interchange
  def create_program(rand, id)
    @handle.write Program.new(id.to_s, prng).render
  end

  # writes footer and closes education organization interchange file handle
  def close
    @handle.write(@footer)
    @handle.close()
  end
end
