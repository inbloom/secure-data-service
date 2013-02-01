=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

require_relative "DataWriter.rb"
require_relative "XML/assessment_metadata_generator.rb"
require_relative "XML/cohortGenerator.rb"
require_relative "XML/educationOrganizationGenerator.rb"
require_relative "XML/educationOrgCalendarGenerator.rb"
require_relative "XML/enrollmentGenerator.rb"
require_relative "XML/masterScheduleGenerator.rb"
require_relative "XML/staffAssociationGenerator.rb"
require_relative "XML/studentAssessmentGenerator.rb"
require_relative "XML/studentAttendanceGenerator.rb"
require_relative "XML/studentGradeGenerator.rb"
require_relative "XML/studentParentGenerator.rb"
require_relative "XML/studentProgramGenerator.rb"
require_relative "XML/disciplineGenerator.rb"

# ed-fi xml interchange writer class
# -> event-driven writing of entities to xml interchanges
class XmlDataWriter < DataWriter
  
  # default constructor for data writer class
  # future implementations should take a map of {interchange => true/false}, indicating whether or not
  # that interchange is to be written as part of the current scenario
  def initialize(yaml)
    super(yaml)
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
    @writers << AssessmentMetadataGenerator.new(@yaml, initialize_interchange(directory, "AssessmentMetadata"))
    @writers << CohortGenerator.new(@yaml, initialize_interchange(directory, "StudentCohort"))
    @writers << EducationOrganizationGenerator.new(@yaml, initialize_interchange(directory, "EducationOrganization"))
    @writers << EducationOrgCalendarGenerator.new(@yaml, initialize_interchange(directory, "EducationOrgCalendar"))
    @writers << EnrollmentGenerator.new(@yaml, initialize_interchange(directory, "StudentEnrollment"))
    @writers << MasterScheduleGenerator.new(@yaml, initialize_interchange(directory, "MasterSchedule"))
    @writers << StaffAssociationGenerator.new(@yaml, initialize_interchange(directory, "StaffAssociation"))
    @writers << StudentAssessmentGenerator.new(@yaml, initialize_interchange(directory, "StudentAssessment"))
    @writers << StudentAttendanceGenerator.new(@yaml, initialize_interchange(directory, "Attendance"))
    @writers << StudentGradeGenerator.new(@yaml, initialize_interchange(directory, "StudentGrades"))
    @writers << StudentParentGenerator.new(@yaml, initialize_interchange(directory, "StudentParent"))
    @writers << StudentProgramGenerator.new(@yaml, initialize_interchange(directory, "StudentProgram"))
    @writers << DisciplineGenerator.new(@yaml, initialize_interchange(directory, "StudentDiscipline"))

    # enable entities to be written
    # -> writes header and starts reporting
    @writers.each { |writer| writer.start }
  end

  # initializes interchange of specified 'type' in 'directory'
  def initialize_interchange(directory, type)
    File.new(directory + "Interchange" + type + ".xml", 'w')
  end

  # flush all queued entities from event-based interchange generators, then
  # close file handles
  def finalize
    super
    @writers.each { |writer| writer.finalize }    
  end

  def write_one_entity(entity)
    found = false
    @writers.each do |writer|
      if writer.can_write?(entity.class)
        writer << entity
        found = true
        break
      end
    end

    if found == false
      puts "<<<< #{entity}: writer not registered for type #{entity.class}"
      exit -1
    end
  end
end
