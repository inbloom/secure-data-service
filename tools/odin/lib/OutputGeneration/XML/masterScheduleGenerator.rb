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

require_relative "./interchangeGenerator.rb"

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# event-based master schedule interchange generator
class MasterScheduleGenerator < InterchangeGenerator

  # course offering writer
  class CourseOfferingWriter < Mustache
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/Partials/course_offering.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def course_offerings
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

  # section writer
  class SectionWriter < Mustache
    def initialize(batch_size)
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/Partials/section.mustache"
      @batch_size    = batch_size
      @entities      = []
    end
    def sections
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

  # initialization will define the header and footer for the master schedule interchange
  # writes header to master schedule interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(batch_size)
    @header, @footer = build_header_footer( "MasterSchedule" )
    @handle = File.new("generated/InterchangeMasterSchedule.xml", 'w')
    @handle.write(@header)

    @course_offering_writer = CourseOfferingWriter.new(batch_size)
    @section_writer         = SectionWriter.new(batch_size)
  end

  # creates and writes course offering to interchange
  def create_course_offering(id, title, ed_org_id, session, course)
    @course_offering_writer.write(@handle, CourseOffering.new(id, title, ed_org_id, session, course))
  end

  # creates and writes section to interchange
  def create_section(id, sequence, environment, medium, population, school_id, course_offering, session)
    @section_writer.write(@handle, Section.new(id, sequence, environment, medium, population, school_id, course_offering, session))
  end

  # writes footer and closes education organization calendar interchange file handle
  def close
    @course_offering_writer.flush(@handle)
    @section_writer.flush(@handle)
    @handle.write(@footer)
    @handle.close()
  end

  # shalka - i will be deleting this as soon as i can
  def write(prng, yamlHash)
    File.open("generated/InterchangeMasterSchedule.xml", 'w') do |f|
      f.write(@header)
      #This needs to map to courses

      if !yamlHash['numCourseOffering'].nil?
        for id in 1..yamlHash['numCourseOffering'] do
          f.write CourseOffering.new(id.to_s, prng).render
        end
      end

      if !yamlHash['numBellSchedule'].nil?
        for id in 1..yamlHash['numBellSchedule'] do
          f.write BellSchedule.new(id.to_s, prng).render
        end
      end

      f.write(@footer)
    end
  end

end
