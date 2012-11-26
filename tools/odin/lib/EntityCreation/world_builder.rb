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
require_relative "../Shared/EntityClasses/enum/GradeLevelType.rb"
require_relative "../Shared/EntityClasses/schoolEducationOrganization.rb"
require_relative "../Shared/data_utility.rb"

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

    @education_organization_writer = EducationOrganizationGenerator.new
    #@education_org_calendar_writer = EducationOrgCalendarGenerator.new
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
    # go back and create courses (currently done at the state education agency level ONLY)
    # update structure with time information
    # finally, write interchanges
    breakdown = compute_grade_breakdown(rand, yaml, num_students)
    create_edOrgs_using_breakdown(rand, yaml, breakdown)
    add_time_information_to_edOrgs(rand, yaml)
    write_interchanges(rand, yaml)
  end

  # iterates through the set of ordered grade levels (Kindergarten through 12th grade) and uses
  # the default percentages (as defined in yaml configuration file) for minimum and maximum per grade
  # to compute the number of students that will initially populate the specified grade level.
  def compute_grade_breakdown(rand, yaml, num_students)
    breakdown = {}
    students_so_far = 0
    GradeLevelType::get_ordered_grades.each do |level|
      num_students_this_grade = get_num_students_per_grade(rand, yaml, num_students)
      students_so_far += num_students_this_grade
      if students_so_far > num_students
        breakdown[level] = 0
      else
        if level == :TWELFTH_GRADE
          breakdown[level] = num_students - students_so_far
        else
          breakdown[level] = num_students_this_grade
        end 
      end
    end
    breakdown
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
  def create_edOrgs_using_breakdown(rand, yaml, breakdown)
    num_elementary_school_students = 0
    num_middle_school_students     = 0
    num_high_school_students       = 0

    GradeLevelType::elementary.each do |level| 
      num_elementary_school_students += breakdown[level]
    end

    GradeLevelType::middle.each do |level|
      num_middle_school_students += breakdown[level]
    end

    GradeLevelType::high.each do |level|
      num_high_school_students += breakdown[level]
    end

    avg_elementary_school_students = yaml["AVERAGE_ELEMENTARY_SCHOOL_NUM_STUDENTS"]
    avg_middle_school_students     = yaml["AVERAGE_MIDDLE_SCHOOL_NUM_STUDENTS"]
    avg_high_school_students       = yaml["AVERAGE_HIGH_SCHOOL_NUM_STUDENTS"]
    avg_num_students_threshold     = yaml["AVERAGE_NUM_STUDENTS_THRESHOLD"]

    min_elementary_school_students = avg_elementary_school_students - (avg_elementary_school_students * avg_num_students_threshold)
    max_elementary_school_students = avg_elementary_school_students + (avg_elementary_school_students * avg_num_students_threshold)
    min_middle_school_students     = avg_middle_school_students     - (avg_middle_school_students * avg_num_students_threshold)
    max_middle_school_students     = avg_middle_school_students     + (avg_middle_school_students * avg_num_students_threshold)
    min_high_school_students       = avg_high_school_students       - (avg_high_school_students * avg_num_students_threshold)
    max_high_school_students       = avg_high_school_students       + (avg_high_school_students * avg_num_students_threshold)
    
    num_schools   = 0
    num_schools   = create_schools(rand, "elementary", num_schools, num_elementary_school_students, min_elementary_school_students, max_elementary_school_students)
    num_schools   = create_schools(rand, "middle", num_schools, num_middle_school_students, min_middle_school_students, max_middle_school_students)
    num_schools   = create_schools(rand, "high", num_schools, num_high_school_students, min_high_school_students, max_high_school_students)
    num_districts = update_schools_with_districts(rand, yaml, num_schools)
    update_districts_with_states(rand, yaml, num_districts)
  end

  # uses the total number of students, as well as range of [min, max] to compute students at a school of type 'tag', and then
  # populates the edOrgs structure according to that tag (which should be "elementary", "middle", or "high").
  def create_schools(rand, tag, num_schools, total_num_students, min, max)
    school_counter  = num_schools
    student_counter = 0
    while student_counter < total_num_students
      current_students = random_on_interval(rand, min, max).round
      school_counter  += 1
      if current_students > (total_num_students - student_counter)        
        current_students = (total_num_students - student_counter)
      end
      student_counter += current_students
      
      edOrg             = Hash.new
      edOrg["id"]       = school_counter
      edOrg["parent"]   = nil
      edOrg["programs"] = []
      edOrg["sessions"] = []
      edOrg["staff"]    = []
      edOrg["teachers"] = []
      edOrg["students"] = current_students
      @edOrgs[tag]     << edOrg
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
      edOrg["staff"]    = []
      @edOrgs["leas"]  << edOrg
      school_counter   += num_schools_in_this_district
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
    edOrg["staff"]    = []
    edOrg["programs"] = []
    @edOrgs["seas"]  << edOrg
    
    @edOrgs["leas"].each do |edOrg| 
      edOrg["parent"] = state_id
    end
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
      # Update infrastructure entities for the year
      #@log.info "creating session information for school year: #{year}-#{year+1} at education organization: #{edOrgId}"
      @log.info "creating session information for school year: #{year}-#{year+1}"
      # create session for LEAs
      # -> create grading periods for session
      # -> create calendar dates for grading period
      # iterate through schools and determine with some probability if school overrides existing lea session
    end
  end

  # writes ed-fi xml interchanges
  def write_interchanges(rand, yaml)
    write_education_organization_interchange(rand, yaml)
    write_education_org_calendar_interchange(rand, yaml)
  end

  # close all file handles used for writing ed-fi xml interchanges
  def close_interchanges
    @education_organization_writer.close
    #@education_org_calendar_writer.close
  end

  # writes ed-fi xml interchange: education organization
  # entities:
  # - StateEducationAgency
  # - LocalEducationAgency
  # - School [Elementary, Middle, High]
  # - [not yet implemented] Course
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

  def write_education_org_calendar_interchange(rand, yaml)
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

  # writes the sessions at each local education agency to the education organization calendar interchange
  # -> will also check to see if schools have extended district-level session
  def write_sessions(rand, edOrgId, sessions)
  end

  # computes a random number on the interval [min, max]
  # does NOT round
  def random_on_interval(rand, min, max)
    min + rand.rand(max - min)
  end
end
