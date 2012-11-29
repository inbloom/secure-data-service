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
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/state_education_organization.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def state_education_agencies
      @entities
    end
    def write(handle, entity)
      @entities << entity
      if @entities.size >= @batch_size
        handle.write render
        @entities = []
      end
    end
    def flush(handle)
      handle.write render
    end
  end

  # local education agency writer
  class LocalEducationAgencyWriter < Mustache
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/local_education_organization.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def local_education_agencies
      @entities
    end
    def write(handle, entity)
      @entities << entity
      if @entities.size >= @batch_size
        handle.write render
        @entities = []
      end
    end
    def flush(handle)
      handle.write render
    end
  end

  # school writer
  class SchoolWriter < Mustache
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/school.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def schools
      @entities
    end
    def write(handle, entity)
      @entities << entity
      if @entities.size >= @batch_size
        handle.write render
        @entities = []
      end
    end
    def flush(handle)
      handle.write render
    end
  end

  # course writer
  class CourseWriter < Mustache
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/course.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def courses
      @entities
    end
    def write(handle, entity)
      @entities << entity
      if @entities.size >= @batch_size
        handle.write render
        @entities = []
      end
    end
    def flush(handle)
      handle.write render
    end
  end

  # initialization will define the header and footer for the education organization interchange
  # writes header to education organization interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(batch_size)
    @header, @footer = build_header_footer("EducationOrganization")
    
    @handle = File.new("generated/InterchangeEducationOrganization.xml", 'w')
    @handle.write(@header)

    @state_education_agency_writer = StateEducationAgencyWriter.new(1)
    @local_education_agency_writer = LocalEducationAgencyWriter.new(batch_size)
    @school_writer                 = SchoolWriter.new(batch_size)
    @course_writer                 = CourseWriter.new(batch_size)
  end

  # creates and writes state education agency to interchange
  def create_state_education_agency(rand, id)
    @state_education_agency_writer.write(@handle, SeaEducationOrganization.new(id, rand))
  end

  # creates and writes local education agency to interchange
  def create_local_education_agency(rand, id, parent_id)
    @local_education_agency_writer.write(@handle, LeaEducationOrganization.new(id, parent_id, rand))
  end

  # creates and writes school to interchange
  def create_school(rand, id, parent_id, type)
    @school_writer.write(@handle, SchoolEducationOrganization.new(rand, id, parent_id, type))
  end

  # creates and writes course to interchange
  def create_course(rand, id, title, ed_org_id)
    @course_writer.write(@handle, Course.new(id, title, ed_org_id))
  end

  # creates and writes program to interchange
  def create_program(rand, id)
    #@handle.write Program.new(id.to_s, prng).render
  end

  # writes footer and closes education organization interchange file handle
  def close
    @state_education_agency_writer.flush(@handle)
    @local_education_agency_writer.flush(@handle)
    @school_writer.flush(@handle)
    @course_writer.flush(@handle)
    @handle.write(@footer)
    @handle.close()
  end
end
