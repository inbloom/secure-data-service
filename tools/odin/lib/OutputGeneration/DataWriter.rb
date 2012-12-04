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

Dir["#{File.dirname(__FILE__)}/../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# base class for data writers to inherit
# -> keeps all entities written in memory
# -> useful for small simulations
class DataWriter
  
  # default constructor for data writer class
  def initialize
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @entities = Hash.new     # leave default value of nil for hash key not existing
    @counts   = Hash.new(0)  # set default value (when key doesn't exist in hash) to be zero
  end

  # -------   education organization interchange entities   ------

  # create state education agency and store in memory
  def create_state_education_agency(rand, id)
    initialize_entity(:state_education_agency)
    @entities[:state_education_agency] << SeaEducationOrganization.new(id, rand)
    increment_count(:state_education_agency)
  end

  # create local education agency and store in memory
  def create_local_education_agency(rand, id, parent_id)
    initialize_entity(:local_education_agency)
    @entities[:local_education_agency] << LeaEducationOrganization.new(id, parent_id, rand)
    increment_count(:local_education_agency)
  end

  # create school and store in memory
  def create_school(rand, id, parent_id, type)
    initialize_entity(:school)
    @entities[:school] << SchoolEducationOrganization.new(rand, id, parent_id, type)
    increment_count(:school)
  end

  # create course and store in memory
  def create_course(rand, id, title, ed_org_id)
    initialize_entity(:course)
    @entities[:course] << Course.new(id, title, ed_org_id)
    increment_count(:course)
  end

  # create program and store in memory
  def create_program(prng, id)
    initialize_entity(:program)
    @entities[:program] << Program.new(id.to_s, prng)
    increment_count(:program)
  end

  # -------   education organization interchange entities   ------
  # --   education organization calendar interchange entities   --

  # create session and store in memory
  def create_session(name, year, term, interval, ed_org_id, grading_periods)
    initialize_entity(:session)
    @entities[:session] << Session.new(name, year, term, interval, ed_org_id, grading_periods)
    increment_count(:session)
  end

  # create grading period and store in memory
  def create_grading_period(type, year, interval, ed_org_id, calendar_dates)
    initialize_entity(:grading_period)
    @entities[:grading_period] << GradingPeriod.new(type, year, interval, ed_org_id, calendar_dates)
    increment_count(:grading_period)
  end

  # create calendar date and store in memory
  def create_calendar_date(date, event)
    initialize_entity(:calendar_date)
    @entities[:calendar_date] << CalendarDate.new(date, event)
    increment_count(:calendar_date)
  end

  # --   education organization calendar interchange entities   --
  # ----------   master schedule interchange entities   ----------

  # create course offering and store in memory
  def create_course_offering(id, title, ed_org_id, session, course)
    initialize_entity(:course_offering)
    @entities[:course_offering] << CourseOffering.new(id, title, ed_org_id, session, course)
    increment_count(:course_offering)
  end

  # create section and store in memory
  def create_section(id, year, term, interval, ed_org_id, grading_periods)
    initialize_entity(:section)
    @entities[:section] << Section.new(id, year, term, interval, ed_org_id, grading_periods)
    increment_count(:section)
  end

  # ----------   master schedule interchange entities   ----------
  # -----------   student parent interchange entities   ----------

  # create student and store in memory
  def create_student(id, birthday_after)
    initialize_entity(:student)
    @entities[:student] << Student.new(id, birthday_after)
    increment_count(:student)
  end

  # -----------   student parent interchange entities   ----------
  # ---------   student enrollment interchange entities   --------

  # create student school association and store in memory
  def create_student_school_association(id, school_id, start_year, start_grade)
    initialize_entity(:student_school_association)
    @entities[:student_school_association] << StudentSchoolAssociation.new(id, school_id, start_year, start_grade)
    increment_count(:student_school_association)
  end

  # create student section association and store in memory
  def create_student_section_association
    initialize_entity(:student_section_association)
    #@entities[:student_section_association] << StudentSchoolAssociation.new(id, school_id, start_year, start_grade)
    increment_count(:student_section_association)
  end

  # ---------   student enrollment interchange entities   --------

  # if the specified entity (which should be a symbol used to identify the ed-fi entity) does not
  # currently have an entry in the @entities hash, then initialize an entry in both the @entities
  # and @counts hash
  def initialize_entity(entity)
    if @entities[entity].nil?
      @entities[entity] = []
      @counts[entity]   = 0
    end
  end
  
  # increments the count for the specified entity by the given number
  # -> if unspecified, the default number of entities to increment by is 1
  def increment_count(entity, num_entities = 1)
    @counts[entity] = 0 if @counts[entity].nil?
    @counts[entity] += 1
  end

  # displays the counts for entities created
  def display_entity_counts
    @log.info "Entity counts:"
    @log.info "-------------------------------------------------------"
    @counts.each do |entity, count|
      @log.info "#{entity}\t\t | count: #{count}"
    end
  end

  # get the entities created
  def get_entities(type)
    @entities[type]
  end

  # gets the number of entities created of specified entity 'type'
  def get_entity_count(type)
    @counts[type]
  end

  # get counts for all entities --> return counts hash
  def get_counts_for_all_entities
    @counts
  end

  # clear entity and count hash
  def finalize
    @entities.clear
    @counts.clear
  end

end
