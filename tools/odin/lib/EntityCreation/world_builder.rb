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

require 'date'
require 'logger'

require_relative "../OutputGeneration/XML/educationOrganizationGenerator.rb"
require_relative "../OutputGeneration/XML/educationOrgCalendarGenerator.rb"
require_relative "../OutputGeneration/XML/masterScheduleGenerator.rb"
require_relative "../Shared/EntityClasses/enum/GradeLevelType.rb"
require_relative "../Shared/EntityClasses/enum/GradingPeriodType.rb"
require_relative "../Shared/EntityClasses/schoolEducationOrganization.rb"
require_relative "../Shared/data_utility.rb"
require_relative "../Shared/date_interval.rb"
require_relative "../Shared/date_utility.rb"

# World Builder
# -> intent is to create 'scaffolding' that represents a detailed, time-sensitive view of the world
#
# generation strategies:
# (1) create world using number of students + time information (begin year, number of years)
# (2) create world using number of schools  + time information (begin year, number of years) [not supported]
class WorldBuilder
  def initialize
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @edOrgs = Hash.new
    @edOrgs["seas"]       = []
    @edOrgs["leas"]       = []
    @edOrgs["elementary"] = []
    @edOrgs["middle"]     = []
    @edOrgs["high"]       = []

    @breakdown = Hash.new

    @education_organization_writer = EducationOrganizationGenerator.new
    @education_org_calendar_writer = EducationOrgCalendarGenerator.new
    @master_schedule_writer        = MasterScheduleGenerator.new
  end

  # Builds the initial snapshot of the world
  # -> generates SEA(s), LEAs, Schools, Course [Education Organization interchange]
  # -> Session, GradingPeriod, CalendarDate [Education Organization Calendar interchange]
  # -> generates Staff, Teacher, StaffEdOrgAssignmentAssociation, TeacherSchoolAssociation [Staff Association interchange]
  # -> generates CourseOffering [Master Schedule interchange]
  # -> out of scope: Program, StaffProgramAssociation, StudentProgramAssociation, Cohort, StaffCohortAssociation, StudentCohortAssociation
  # -> returns education organization structure built
  def build(rand, yaml)
    if !yaml["studentCount"].nil?
      build_world_from_students(rand, yaml)
    #elsif !yaml["schoolCount"].nil?
    #  build_world_from_edOrgs(rand, yaml)
    #  --> not supported yet	
    else
      @log.error "studentCount or schoolCount must be set for a world to be created --> Exiting..."
    end

    close_interchanges
    return @edOrgs
  end

  # Builds world using the specified number of students as the driving criteria
  # -> ignores schoolCount, if specified in yml file
  # -> creates elementary, middle, and high schools from number of students
  # -> walks back up ed-fi graph to create LEAs and SEA(s) from created schools
  def build_world_from_students(rand, yaml)
    num_students = yaml["studentCount"]
    @log.info "Creating world from initial number of students: #{num_students}"

    # create grade breakdown from total number of students
    # populate education organization structure using breakdown (number of students per grade)
    # update structure with time information
    # use time information to create master schedule (course offerings, sections)
    # finally, write interchanges
    compute_grade_breakdown(rand, yaml, num_students)
    create_edOrgs_using_breakdown(rand, yaml)
    add_time_information_to_edOrgs(rand, yaml)
    create_master_schedule(rand, yaml)
    write_interchanges(rand, yaml)
  end

  # iterates through the set of ordered grade levels (Kindergarten through 12th grade) and uses
  # the default percentages (as defined in yaml configuration file) for minimum and maximum per grade
  # to compute the number of students that will initially populate the specified grade level.
  def compute_grade_breakdown(rand, yaml, num_students)
    students_so_far = 0
    GradeLevelType::get_ordered_grades.each do |level|
      num_students_this_grade = get_num_students_per_grade(rand, yaml, num_students)
      students_so_far += num_students_this_grade
      if students_so_far > num_students
        @breakdown[level] = 0
      else
        if level == :TWELFTH_GRADE
          @breakdown[level] = num_students - students_so_far
        else
          @breakdown[level] = num_students_this_grade
        end 
      end
    end
  end

  # randomly select the number of students for the current grade by using the minimum percentage,
  # maximum percentage, and total number of students
  def get_num_students_per_grade(rand, yaml, num_students)
    min = yaml["MINIMUM_GRADE_PERCENTAGE"]
    max = yaml["MAXIMUM_GRADE_PERCENTAGE"]
    ((random_on_interval(rand, min, max) / 100) * num_students).round
  end

  # Uses the breakdown hash to bucketize number of students according to type of school
  # they would be enrolled at, and then computes the number of schools required to hold those
  # students according to national averages (average students per elementary school, ...).
  def create_edOrgs_using_breakdown(rand, yaml)
    num_elementary_school_students = 0
    num_middle_school_students     = 0
    num_high_school_students       = 0

    GradeLevelType::elementary.each do |level| 
      num_elementary_school_students += @breakdown[level]
    end

    GradeLevelType::middle.each do |level|
      num_middle_school_students += @breakdown[level]
    end

    GradeLevelType::high.each do |level|
      num_high_school_students += @breakdown[level]
    end

    num_schools   = 0
    num_schools   = create_schools(rand, yaml, "elementary", num_schools, num_elementary_school_students)
    num_schools   = create_schools(rand, yaml, "middle", num_schools, num_middle_school_students)
    num_schools   = create_schools(rand, yaml, "high", num_schools, num_high_school_students)
    num_districts = update_schools_with_districts(rand, yaml, num_schools)
    update_districts_with_states(rand, yaml, num_districts)
  end

  # uses the total number of students, as well as range of [min, max] to compute students at a school of type 'tag', and then
  # populates the edOrgs structure according to that tag (which should be "elementary", "middle", or "high").
  def create_schools(rand, yaml, tag, num_schools, total_num_students)
    if tag == "elementary"
      avg_num_students = yaml["AVERAGE_ELEMENTARY_SCHOOL_NUM_STUDENTS"]
    elsif tag == "middle"
      avg_num_students = yaml["AVERAGE_MIDDLE_SCHOOL_NUM_STUDENTS"]
    elsif tag == "high"
      avg_num_students = yaml["AVERAGE_HIGH_SCHOOL_NUM_STUDENTS"]
    end
    avg_num_students_threshold = yaml["AVERAGE_NUM_STUDENTS_THRESHOLD"]
    min                        = avg_num_students - (avg_num_students * avg_num_students_threshold)
    max                        = avg_num_students + (avg_num_students * avg_num_students_threshold)
    school_counter             = num_schools
    student_counter            = 0
    while student_counter < total_num_students
      current_students = random_on_interval(rand, min, max).round
      school_counter  += 1
      if current_students > (total_num_students - student_counter)        
        current_students = (total_num_students - student_counter)
      end
      student_counter += current_students
      
      edOrg              = Hash.new
      edOrg["id"]        = school_counter
      edOrg["parent"]    = nil
      edOrg["programs"]  = []
      edOrg["sessions"]  = []
      edOrg["staff"]     = create_staff_for_school(rand, yaml)
      edOrg["offerings"] = Hash.new
      edOrg["students"]  = current_students
      @edOrgs[tag]       << edOrg
    end
    return school_counter
  end

  # updates the populated edOrgs arrays for elementary, middle, and high schools by determining how many schools are to be
  # contained in a given district (looks at yaml configuration file for average number of schools per district and threshold),
  # and then actually going and updating the schools to reference back to newly created local education agencies
  def update_schools_with_districts(rand, yaml, num_schools)
    avg_num_schools_per_district = yaml["AVERAGE_NUM_SCHOOLS_PER_DISTRICT"]
    avg_num_schools_threshold    = yaml["AVERAGE_SCHOOLS_THRESHOLD"]
    min_num_schools_per_district = avg_num_schools_per_district - (avg_num_schools_per_district * avg_num_schools_threshold)
    max_num_schools_per_district = avg_num_schools_per_district + (avg_num_schools_per_district * avg_num_schools_threshold)
    all_schools      = (1..num_schools).to_a
    district_counter = num_schools
    school_counter   = 0
    while all_schools.size > 0
      district_counter += 1
      num_schools_in_this_district = random_on_interval(rand, min_num_schools_per_district, max_num_schools_per_district).round
      
      if num_schools_in_this_district > (num_schools - school_counter)
        num_schools_in_this_district = (num_schools - school_counter)
      end

      schools_in_this_district = all_schools.pop(num_schools_in_this_district)      
      update_schools_with_district_id(district_counter, schools_in_this_district)
      
      edOrg             = Hash.new
      edOrg["id"]       = district_counter
      edOrg["parent"]   = nil
      edOrg["programs"] = []
      edOrg["sessions"] = []
      edOrg["staff"]    = create_staff_for_local_education_agency(rand, yaml)
      @edOrgs["leas"]   << edOrg
      school_counter    += num_schools_in_this_district
    end
    return district_counter
  end

  # actually does the work to iterate through the edOrgs structure (specifically the arrays
  # contained in the tags: "elementary", "middle", and "high" schools), finding edOrgs whose
  # unique "id" is contained within "schools_in_this_district", and sets the "parent" attribute
  # of those matching edOrgs to the district id.
  def update_schools_with_district_id(district_id, schools_in_this_district)
    # check in edOrgs["elementary"]
    @edOrgs["elementary"].each do |edOrg|
      if schools_in_this_district.include? edOrg["id"] 
        edOrg["parent"] = district_id
      end
    end
    # check in edOrgs["middle"]
    @edOrgs["middle"].each do |edOrg|
      if schools_in_this_district.include? edOrg["id"] 
        edOrg["parent"] = district_id
      end
    end
    # check in edOrgs["high"]
    @edOrgs["high"].each do |edOrg|
      if schools_in_this_district.include? edOrg["id"] 
        edOrg["parent"] = district_id
      end
    end
  end

  # updates the populated edOrgs arrays for leas (local education agencies) by determining how many districts are to be
  # contained in a given state. current implementation assumes:
  # - single tier for local education agencies
  # - all local education agencies are contained within a single state
  # 
  # future implementation should create more 'layers' within the local education agency 'tier'
  # future implementation should look at yaml for average number of districts in a state and create multiple 
  #  state education agencies, as needed
  def update_districts_with_states(rand, yaml, num_districts)
    state_id          = num_districts + 1
    edOrg             = Hash.new
    edOrg["id"]       = state_id
    edOrg["courses"]  = create_courses(yaml)
    edOrg["staff"]    = create_staff_for_state_education_agency(rand, yaml)
    edOrg["programs"] = []
    @edOrgs["seas"]  << edOrg
    
    @edOrgs["leas"].each do |edOrg| 
      edOrg["parent"] = state_id
    end
  end

  # creates staff members at the state education agency level
  def create_staff_for_state_education_agency(rand, yaml)
    members = []
    members
  end

  # creates staff members at the local education agency level
  def create_staff_for_local_education_agency(rand, yaml)
    members = []
    members
  end  

  # creates staff members at the school level
  def create_staff_for_school(rand, yaml)
    members = []
    members
  end

  def build_world_from_edOrgs(rand, yaml)
  	num_schools = yaml["schoolCount"]
    @log.info "Creating world from initial number of schools: #{num_schools}"
    # NOT CURRENTLY SUPPORTED
    # update structure with time information
    add_time_information_to_edOrgs(rand, yaml)
  end

  def add_time_information_to_edOrgs(rand, yaml)
    begin_year   = yaml["beginYear"]
    num_years    = yaml["numYears"]
    
    if begin_year.nil?
      this_year = Date.today.year
      @log.info "Property: beginYear --> not set for scenario. Using default: #{this_year}"
      begin_year = this_year
    end

    if num_years.nil?
      @log.info "Property: numYears --> not set for scenario. Using default: 1"
      num_years = 1
    end

    # loop over years updating infrastructure and population
    for year in begin_year..(begin_year+num_years-1) do
      # create session for LEAs
      # -> create grading period(s) for session
      # -> create calendar date(s) for grading period
      # -> [not implemented] iterate through schools and determine with some probability if school overrides existing lea session
      @log.info "creating session information for school year: #{year}-#{year+1}"
      @edOrgs["leas"].each_index do |index|
        edOrg = @edOrgs["leas"][index]
        state_organization_id = DataUtility.get_local_education_agency_id(edOrg["id"])
        start_date = DateUtility.random_school_day_on_interval(rand, Date.new(year, 8, 25), Date.new(year, 9, 10))
        interval   = DateInterval.create_using_start_and_num_days(rand, start_date, 180)

        session             = Hash.new
        session["term"]     = :YEAR_ROUND
        session["year"]     = year
        session["name"]     = year.to_s + "-" + (year+1).to_s + " " + SchoolTerm.to_string(:YEAR_ROUND) + " session: " + state_organization_id
        session["interval"] = interval
        session["edOrgId"]  = state_organization_id
        @edOrgs["leas"][index]["sessions"] << session
      end
    end
  end
  
  # create master schedule interchange
  # -> create course offerings
  # -> create sections (using course offerings and breakdown of students per grade)
  # --> do NOT create teachers for course offerings at elementary school (this should be done for sections)
  # --> in middle and high schools, create teacher for course offering (teachers can teach multiple sections)
  def create_master_schedule(rand, yaml)
    offering_id = 0
    create_course_offerings_for_school_type("elementary", offering_id)
    create_course_offerings_for_school_type("middle", offering_id)
    create_course_offerings_for_school_type("high", offering_id)
  end

  # iterate through schools of 'type' (should be "elementary", "middle", or "high" schools)
  # -> create course offerings by
  # --> getting set of sessions to be used
  # --> getting set of courses  to be used
  # -> iterate through sessions
  # -> iterate through courses
  # --> use {session, course} pair to create course offering at current school
  def create_course_offerings_for_school_type(type, num_offerings)
    offering_id = num_offerings
    @edOrgs[type].each_index do |index|
      school    = @edOrgs[type][index]
      sessions  = get_sessions_to_be_used_by_school(school)
      courses   = get_courses_to_be_used_by_school(type, school)
      sessions.each do |session|
        offerings    = []
        current_year = session["year"]
        courses.each do |grade, current_courses| 
          current_courses.each do |course|
            # increment course offering unique id
            offering_id                += 1
            # create map for session information
            local_session              = Hash.new
            local_session["name"]      = session["name"]
            local_session["ed_org_id"] = session["edOrgId"]
            # create course offering container
            offering = Hash.new
            offering["id"]        = offering_id
            if type == "elementary"
              offering["ed_org_id"] = DataUtility.get_elementary_school_id(school["id"])
            elsif type == "middle"
              offering["ed_org_id"] = DataUtility.get_middle_school_id(school["id"])
            elsif type == "high"
              offering["ed_org_id"] = DataUtility.get_high_school_id(school["id"])
            end            
            offering["session"]   = local_session
            offering["course"]    = course
            offering["grade"]     = grade
            # add course offering into 'offerings' (course offerings for current year)
            offerings << offering
          end
        end
        @edOrgs[type][index]["offerings"][current_year] = offerings
      end
    end
  end

  # gets the sequence of sessions to be used by the school when creating course offerings
  # -> if school has its own sessions, use those
  # -> if not, traverse up leas hierarchy until sessions are found
  def get_sessions_to_be_used_by_school(school)
    sessions = []
    if school["sessions"].nil? or school["sessions"].size == 0
      parent   = school["parent"]
      while sessions.size == 0
        lea = @edOrgs["leas"].detect {|lea| lea["id"] == parent}
        if !lea["sessions"].nil? and lea["sessions"].size > 0
          sessions = lea["sessions"]
        else
          parent = lea["parent"]
        end
      end
    else
      # school has published its own sessions --> use those
      # this is not implemented because schools do not currently override district's sessions
    end
    sessions
  end

  # gets the set of courses to be used by the school when creating course offerings
  # -> traverses up to state education agency to find course catalog
  # -> future implementations should traverse until a set of courses is found (allow districts to extend state-level course catalog)
  def get_courses_to_be_used_by_school(type, school)
    # get school's immediate parent
    # traverse up local education agencies until no parent can be found
    # check in state education agencies for parent that failed to be in leas
    # if found, return state education agency
    parent_id = school["parent"]
    parent    = @edOrgs["leas"].detect {|lea| lea["id"] == parent_id}
    while parent != nil
      parent_id = parent["parent"]
      parent    = @edOrgs["leas"].detect {|lea| lea["id"] == parent_id}
    end
    # parent_id should now be populated with id of the state education agency
    state_education_agency = @edOrgs["seas"].detect {|sea| sea["id"] == parent_id}

    courses = Hash.new
    if type == "elementary"
      GradeLevelType.elementary.each do |grade|
        # get the current set of courses that the state education agency has published
        # add state education agency id to courses --> makes life easier when creating course offering
        current_courses              = state_education_agency["courses"][grade]
        current_courses.each { |element| element["ed_org_id"] = DataUtility.get_state_education_agency_id(state_education_agency["id"]) }
        courses[grade]               = current_courses
      end
    elsif type == "middle"
      GradeLevelType.middle.each do |grade|
        # get the current set of courses that the state education agency has published
        # add state education agency id to courses --> makes life easier when creating course offering
        current_courses              = state_education_agency["courses"][grade]
        current_courses.each { |element| element["ed_org_id"] = DataUtility.get_state_education_agency_id(state_education_agency["id"]) }
        courses[grade]               = current_courses
      end
    elsif type == "high"
      GradeLevelType.high.each do |grade|
        # get the current set of courses that the state education agency has published
        # add state education agency id to courses --> makes life easier when creating course offering
        current_courses              = state_education_agency["courses"][grade]
        current_courses.each { |element| element["ed_org_id"] = DataUtility.get_state_education_agency_id(state_education_agency["id"]) }
        courses[grade]               = current_courses
      end
    end
    courses
  end

  # writes ed-fi xml interchanges
  def write_interchanges(rand, yaml)
    write_education_organization_interchange(rand, yaml)
    write_education_org_calendar_interchange(rand, yaml)
    write_master_schedule_interchange(rand, yaml)
  end

  # close all file handles used for writing ed-fi xml interchanges
  def close_interchanges
    @education_organization_writer.close
    @education_org_calendar_writer.close
    @master_schedule_writer.close
  end

  # writes ed-fi xml interchange: education organization
  # entities:
  # - StateEducationAgency
  # - LocalEducationAgency
  # - School [Elementary, Middle, High]
  # - Course
  # - [not yet implemented] Program
  def write_education_organization_interchange(rand, yaml)
    # write state education agencies
    @edOrgs["seas"].each do |edOrg|
      @education_organization_writer.create_state_education_agency(rand, edOrg["id"])
      write_courses(rand, DataUtility.get_state_education_agency_id(edOrg["id"]), edOrg["courses"])
    end
    # write local education agencies
    @edOrgs["leas"].each do |edOrg|
      @education_organization_writer.create_local_education_agency(rand, edOrg["id"], edOrg["parent"])
    end
    # write schools
    @edOrgs["elementary"].each do |edOrg|
      @education_organization_writer.create_school(rand, edOrg["id"], edOrg["parent"], "elementary")
    end
    @edOrgs["middle"].each do |edOrg|
      @education_organization_writer.create_school(rand, edOrg["id"], edOrg["parent"], "middle")
    end
    @edOrgs["high"].each do |edOrg|
      @education_organization_writer.create_school(rand, edOrg["id"], edOrg["parent"], "high")
    end
  end

  # writes ed-fi xml interchange: education organization calendar
  # entities:
  # - Session
  # - GradingPeriod
  # - CalendarDate
  def write_education_org_calendar_interchange(rand, yaml)
    # write sessions at the district level
    @edOrgs["leas"].each do |ed_org|
      # get sessions currently stored on local education agency
      # iterate through sessions
      sessions  = ed_org["sessions"]
      ed_org_id = DataUtility.get_local_education_agency_id(ed_org["id"])
      sessions.each do |session|
        interval  = session["interval"]
        year      = session["year"]

        # create calendar date(s) using interval
        # create grading period(s) using interval, school year, and state organization id
        calendar_dates  = create_calendar_dates(interval)        
        grading_periods = create_grading_periods(interval, year, ed_org_id)

        # create and write session
        name      = session["name"]
        term      = session["term"]
        @education_org_calendar_writer.create_session(name, year, term, interval, ed_org_id, grading_periods)

        # write grading period(s)
        grading_periods.each do |grading_period|
          type      = grading_period["type"]
          year      = grading_period["year"]
          interval  = grading_period["interval"]
          ed_org_id = grading_period["ed_org_id"]
          @education_org_calendar_writer.create_grading_period(type, year, interval, ed_org_id, calendar_dates)
        end

        # write calendar date(s)
        calendar_dates.each do |calendar_date|
          @education_org_calendar_writer.create_calendar_date(calendar_date["date"], calendar_date["event"])
        end
      end
    end
  end

  # writes ed-fi xml interchange: master schedule
  # entities:
  # - [not done] CourseOffering
  # - [not done] Section
  def write_master_schedule_interchange(rand, yaml)
    write_course_offerings("elementary")
    write_course_offerings("middle")
    write_course_offerings("high")
  end

  # writes the course offerings for each 'type' of school ("elementary", "middle", or "high" school)
  # -> iterates through schools of 'type'
  # -> if the school has course offerings
  # --> assembles course offering and passes to master schedule interchange writer
  def write_course_offerings(type)
    @edOrgs[type].each do |school|
      if !school["offerings"].nil? and school["offerings"].size > 0
        school["offerings"].each do |year, course_offerings|
          course_offerings.each do |course_offering|
            id        = course_offering["id"]
            ed_org_id = course_offering["ed_org_id"]
            session   = course_offering["session"]
            course    = course_offering["course"]
            grade     = course_offering["grade"]
          
            if GradeLevelType.is_elementary_school_grade(grade)
              title   = GradeLevelType.get(grade)
            else
              title   = GradeLevelType.get(grade) + " " + course["title"]
            end
          
            @master_schedule_writer.create_course_offering(id, title, ed_org_id, session, course)
          end
        end
      end
    end
  end

  # creates an array of calendar dates that represent the specified interval
  # interval specifies:
  # - start date
  # - end date
  # - holidays
  # - num of instructional days (not used)
  def create_calendar_dates(interval)
    calendar_dates = []
    begin_date     = interval.get_begin_date
    end_date       = interval.get_end_date
    holidays       = interval.get_holidays
    (begin_date..end_date).step(1) do |date|
      if holidays.include?(date)
        calendar_date          = Hash.new
        calendar_date["date"]  = date
        calendar_date["event"] = :HOLIDAY
        calendar_dates << calendar_date
      else
        if date.wday != 0 and date.wday != 6
          calendar_date          = Hash.new
          calendar_date["date"]  = date
          calendar_date["event"] = :INSTRUCTIONAL_DAY
          calendar_dates << calendar_date
        end
      end
    end
    calendar_dates
  end

  # creates an array of grading periods that occur over the specified interval
  # -> currently, only a single grading period is generated (for whole session)
  # -> future implementations should take an additional variable that specifies breakdown into multiple grading periods
  def create_grading_periods(interval, year, ed_org_id)
    grading_periods              = []
    grading_period               = Hash.new
    grading_period["type"]       = :END_OF_YEAR
    grading_period["year"]       = year
    grading_period["interval"]   = interval
    grading_period["ed_org_id"]  = ed_org_id
    grading_periods              << grading_period
    grading_periods
  end

  # creates courses at the state education agency by populating a 'course catalog'
  # initially assumes a very simple course model
  # -> each grade contains Science, Math, English, and History
  # -> no honors or multiple course paths
  def create_courses(yaml)
    courses = Hash.new
    course_counter = 0
    GradeLevelType::get_ordered_grades.each do |grade|
      current_grade_courses = Array.new
      if !yaml[grade.to_s + "_COURSES"].nil?
        yaml[grade.to_s + "_COURSES"].each do |course|
          current_grade_course = Hash.new
          course_counter += 1
          current_grade_course["id"] = course_counter
          current_grade_course["title"] = course
          current_grade_courses << current_grade_course
        end
      else
        current_grade_course = Hash.new
        course_counter += 1
        current_grade_course["id"] = course_counter
        current_grade_course["title"] = GradeLevelType.get(grade)
        current_grade_courses << current_grade_course
      end
      courses[grade] = current_grade_courses
    end
    courses
  end

  # writes the courses at the state education agency to the education organization interchange
  def write_courses(rand, edOrgId, courses)
    courses.each do |key, value|
      grade = GradeLevelType.get(key)
      value.each do |course|
        id = DataUtility.get_course_unique_id(course["id"])
        if GradeLevelType.is_elementary_school_grade(key)
          title = grade
        else
          title = grade + " " + course["title"]
        end
        @education_organization_writer.create_course(rand, id, title, edOrgId)
      end
    end
  end

  # computes a random number on the interval [min, max]
  # does NOT round
  def random_on_interval(rand, min, max)
    min + rand.rand(max - min)
  end
end
