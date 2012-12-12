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

require 'logger'

require_relative "XML/studentParentGenerator.rb"
require_relative "XML/educationOrganizationGenerator.rb"
require_relative "XML/educationOrgCalendarGenerator.rb"
require_relative "XML/masterScheduleGenerator.rb"
require_relative "XML/enrollmentGenerator.rb"
require_relative "XML/staffAssociationGenerator.rb"
require_relative "XML/studentAssessmentGenerator.rb"
require_relative "XML/assessment_metadata_generator.rb"
require_relative "DataWriter.rb"

# ed-fi xml interchange writer class
# -> sub-class of (in-memory) data writer
# -> batches writes to interchanges by specified BATCH_SIZE
# -> meant to be event-driven writing of entities to xml interchanges
class XmlDataWriter < DataWriter
  
  # default constructor for data writer class
  # future implementations should take a map of {interchange => true/false}, indicating whether or not
  # that interchange is to be written as part of the current scenario
  def initialize(yaml)
    super()
    @yaml      = yaml
    @directory = (yaml['DIRECTORY'] or 'generated/')
    initialize_edfi_xml_writers(@directory)
  end

  # initlizes the file handles used for writing ed-fi xml interchanges
  def initialize_edfi_xml_writers(directory)
    @log.info "initializing ed-fi xml data writer using directory: #{@directory}"
    if Dir.exists?(@directory)
      @log.info "directory: #{@directory} already exists."
    else
      @log.info "directory: #{@directory} does not exist --> creating it."
      Dir.mkdir(@directory)
    end

    @writers = []
    @writers << StudentParentGenerator.new(@yaml, initialize_interchange(directory, "StudentParent"))
    @writers << EducationOrganizationGenerator.new(@yaml, initialize_interchange(directory, "EducationOrganization"))
    @writers << EducationOrgCalendarGenerator.new(@yaml, initialize_interchange(directory, "EducationOrgCalendar"))
    @writers << MasterScheduleGenerator.new(@yaml, initialize_interchange(directory, "MasterSchedule"))
    @writers << AssessmentMetadataGenerator.new(@yaml, initialize_interchange(directory, "AssessmentMetadata"))
    @writers << EnrollmentGenerator.new(@yaml, initialize_interchange(directory, "StudentEnrollment"))
    @writers << StaffAssociationGenerator.new(@yaml, initialize_interchange(directory, "StaffAssociation"))
    @writers << StudentAssessmentGenerator.new(@yaml, initialize_interchange(directory, "StudentAssessment"))
    
    # enable entities to be written
    # -> writes header
    # -> starts reporting
    @writers.each do |writer|
      writer.start
    end
  end

  # initializes interchange of specified 'type' in 'directory'
  def initialize_interchange(directory, type)
    File.new(directory + "Interchange" + type + ".xml", 'w')
  end

  # flush all queued entities from event-based interchange generators, then
  # close file handles
  def finalize
    @writers.each do |writer|
      writer.finalize
    end
    display_entity_counts
  end

  def write_one_entity(entity)

    if entity.is_a?(Array)
      entity.each do |e|
        write_one_entity(e)
    end

    else
      initialize_entity(entity.class)

      found = false
      @writers.each do |writer|
        if writer.can_write?(entity.class)
          writer << entity
          found = true
        end
      end

      if found == false
        puts "<<<< #{entity}: writer not registered for type #{entity.class}"
        exit -1
      end

      increment_count(entity)
    end

  end

  def << (entity)
    write_one_entity(entity)
  end
end
