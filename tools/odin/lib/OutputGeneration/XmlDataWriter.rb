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
    @directory = yaml['DIRECTORY']
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

    @student_parent_writer         = StudentParentGenerator.new(@yaml, initialize_interchange(directory, "StudentParent"))
    @education_organization_writer = EducationOrganizationGenerator.new(@yaml, initialize_interchange(directory, "EducationOrganization"))
    @education_org_calendar_writer = EducationOrgCalendarGenerator.new(@yaml, initialize_interchange(directory, "EducationOrgCalendar"))
    @master_schedule_writer        = MasterScheduleGenerator.new(@yaml, initialize_interchange(directory, "MasterSchedule"))
    @student_enrollment_writer     = EnrollmentGenerator.new(@yaml, initialize_interchange(directory, "StudentEnrollment"))
    @staff_association_writer      = StaffAssociationGenerator.new(@yaml, initialize_interchange(directory, "StaffAssociation"))
    
    # enable entities to be written
    # -> writes header
    # -> starts reporting
    @student_parent_writer.start
    @education_organization_writer.start
    @education_org_calendar_writer.start
    @master_schedule_writer.start
    @student_enrollment_writer.start
    @staff_association_writer.start
  end

  # initializes interchange of specified 'type' in 'directory'
  def initialize_interchange(directory, type)
    File.new(directory + "Interchange" + type + ".xml", 'w')
  end

  # flush all queued entities from event-based interchange generators, then
  # close file handles
  def finalize
    @student_parent_writer.finalize
    @education_organization_writer.finalize
    @education_org_calendar_writer.finalize
    @master_schedule_writer.finalize
    @student_enrollment_writer.finalize
    @staff_association_writer.finalize
  end

  # -------   education organization interchange entities   ------

  # write state education agency to education organization interchange
  def create_state_education_agency(rand, id)
    @education_organization_writer.create_state_education_agency(rand, id)
    increment_count(:state_education_agency)
  end

  # write local education agency to education organization interchange
  def create_local_education_agency(rand, id, parent_id)
    @education_organization_writer.create_local_education_agency(rand, id, parent_id)
    increment_count(:local_education_agency)
  end

  # write school to education organization interchange
  def create_school(rand, id, parent_id, type)
    @education_organization_writer.create_school(rand, id, parent_id, type)
    increment_count(:school)
  end

  # write course to education organization interchange
  def create_course(rand, id, title, ed_org_id)
    @education_organization_writer.create_course(rand, id, title, ed_org_id)
    increment_count(:course)
  end

  # write program to education organization interchange
  def create_program(rand, id)
    @education_organization_writer.create_program(rand, id)
    increment_count(:program)
  end

  # -------   education organization interchange entities   ------
  # --   education organization calendar interchange entities   --

  # write session to education organization calendar interchange
  def create_session(name, year, term, interval, ed_org_id, grading_periods)
    @education_org_calendar_writer.create_session(name, year, term, interval, ed_org_id, grading_periods)
    increment_count(:session)
  end

  # write grading period to education organization calendar interchange
  def create_grading_period(type, year, interval, ed_org_id, calendar_dates)
    @education_org_calendar_writer.create_grading_period(type, year, interval, ed_org_id, calendar_dates)
    increment_count(:grading_period)
  end

  # write calendar date to education organization calendar interchange
  def create_calendar_date(date, event, ed_org_id)
    @education_org_calendar_writer.create_calendar_date(date, event, ed_org_id)
    increment_count(:calendar_date)
  end

  # --   education organization calendar interchange entities   --
  # ----------   master schedule interchange entities   ----------
  
  # write course offering to master schedule interchange
  def create_course_offering(id, title, ed_org_id, session, course)
    @master_schedule_writer.create_course_offering(id, title, ed_org_id, session, course)
    increment_count(:course_offering)
  end

  # write section to master schedule interchange
  def create_section(id, year, term, interval, ed_org_id, grading_periods)
    @master_schedule_writer.create_section(id, year, term, interval, ed_org_id, grading_periods)
    increment_count(:section)
  end

  # ----------   master schedule interchange entities   ----------
  # -----------   student parent interchange entities   ----------

  # write student to student parent interchange
  def create_student(id, birthday_after)
    @student_parent_writer.create_student(id, birthday_after)
    increment_count(:student)
  end

  # -----------   student parent interchange entities   ----------
  # ---------   student enrollment interchange entities   --------

  # write student school association to student enrollment interchange
  def create_student_school_association(id, school_id, start_year, start_grade)
    @student_enrollment_writer.create_student_school_association(id, school_id, start_year, start_grade)
    increment_count(:student_school_association)
  end

  # write student section association to student enrollment interchange
  def create_student_section_association(id, section, school_id, start_year, start_grade)
    @student_enrollment_writer.create_student_section_association(id, section, school_id, start_year, start_grade)
    increment_count(:student_section_association)
  end

  # ---------   student enrollment interchange entities   --------
  # ----------   staff association interchange entities   --------

  # write staff to staff association interchange
  def create_staff(id, year_of)
    @staff_association_writer.create_staff(id, year_of)
    increment_count(:staff)
  end

  # write staff education organization assignment association to staff association interchange
  def create_staff_ed_org_assignment_association(staff, ed_org, classification, title, begin_date)
    @staff_association_writer.create_staff_ed_org_assignment_association(staff, ed_org, classification, title, begin_date)
    increment_count(:staff_ed_org_assignment_association)
  end

  # ----------   staff association interchange entities   --------
end
