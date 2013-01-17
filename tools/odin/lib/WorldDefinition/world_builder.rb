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

require_relative '../Shared/EntityClasses/enum/GradeLevelType.rb'
require_relative '../Shared/EntityClasses/enum/GradingPeriodType.rb'
require_relative '../Shared/EntityClasses/enum/ProgramSponsorType.rb'
require_relative '../Shared/EntityClasses/enum/ProgramType.rb'
require_relative '../Shared/EntityClasses/enum/StaffClassificationType.rb'
require_relative '../Shared/data_utility.rb'
require_relative '../Shared/date_interval.rb'
require_relative '../Shared/date_utility.rb'
require_relative 'assessment_work_order.rb'
require_relative 'student_work_order.rb'
require_relative 'section_work_order.rb'
require_relative 'graduation_plan_factory.rb'

# World Builder
# -> intent is to create 'scaffolding' that represents a detailed, time-sensitive view of the world
#
# generation strategies:
# (1) create world using number of students + time information (begin year, number of years)
# (2) create world using number of schools  + time information (begin year, number of years) [not supported]
class WorldBuilder
  def initialize(prng, yaml, queue, pre_requisites)
    $stdout.sync = true
    @log         = Logger.new($stdout)
    @log.level   = Logger::INFO

    @prng = prng
    @scenarioYAML = yaml

    @world = {"seas" => [], "leas" => [], "elementary" => [], "middle" => [], "high" => []}

    @breakdown      = {}              # used for storing breakdown of students per grade
    @pre_requisites = pre_requisites  # pre-requisites for world building (education organizations and staff members/teachers that must be created)
    @schools        = []              # holds state organization id's of schools specified in the staff.json

    @num_staff_members = 0            # use to make sure staff   unique state ids are unique
    @num_teachers      = 0            # use to make sure teacher unique state ids are unique
    @unique_program_id = 0            # use to make sure program identifiers      are unique

    @queue = queue                    # queue for work orders (passed to entity factory for entity creation after world building)
  end

  # Builds the initial snapshot of the world
  # -> generates SEA(s), LEAs, Schools, Course [Education Organization interchange]
  # -> Session, GradingPeriod, CalendarDate [Education Organization Calendar interchange]
  # -> generates Staff, Teacher, StaffEdOrgAssignmentAssociation, TeacherSchoolAssociation [Staff Association interchange]
  # -> generates CourseOffering [Master Schedule interchange]
  # -> out of scope: Program, StaffProgramAssociation, StudentProgramAssociation, Cohort, StaffCohortAssociation, StudentCohortAssociation
  # -> returns education organization structure built
  def build()
    if !@scenarioYAML["STUDENT_COUNT"].nil?
      build_world_from_students
    #elsif !@scenarioYAML["SCHOOL_COUNT"].nil?
    #  build_world_from_edOrgs()
    #  --> not supported yet	
    else
      @log.error "STUDENT_COUNT or SCHOOL_COUNT must be set for a world to be created --> Exiting..."
    end

    return @world
  end

  # Builds world using the specified number of students as the driving criteria
  # -> ignores SCHOOL_COUNT, if specified in yml file
  # -> creates elementary, middle, and high schools from number of students
  # -> walks back up ed-fi graph to create LEAs and SEA(s) from created schools
  def build_world_from_students
    num_students = @scenarioYAML["STUDENT_COUNT"]
    @log.info "Creating world from initial number of students: #{num_students}"

    # create grade breakdown from total number of students
    # populate education organization structure using breakdown (number of students per grade)
    # update structure with time information (create sessions using begin year and number of years)
    # expand school enrollment information using previously created session information
    # use time information to create master schedule (course offerings)
    # finally, write interchange
    compute_grade_breakdown(num_students)
    create_edOrgs_using_breakdown
    begin_year, num_years = add_time_information_to_edOrgs
    expand_student_counts_using_time_information(begin_year, num_years)

    create_master_schedule
    create_assessments(begin_year, num_years)
    create_descriptors
    create_learning_objectives
    create_work_orders
  end

  # iterates through the set of ordered grade levels (Kindergarten through 12th grade) and uses
  # the default percentages (as defined in yaml configuration file) for minimum and maximum per grade
  # to compute the number of students that will initially populate the specified grade level.
  def compute_grade_breakdown(num_students)
    students_so_far = 0
    GradeLevelType::get_ordered_grades.each do |grade|
      num_students_this_grade = get_num_students_per_grade(num_students)
      num_students_this_grade = 0                              if students_so_far >= num_students
      num_students_this_grade = num_students - students_so_far if grade == :TWELFTH_GRADE and num_students_this_grade != 0
      @breakdown[grade]       = num_students_this_grade
      students_so_far += num_students_this_grade
      end
    end

  # randomly select the number of students for the current grade by using the minimum percentage,
  # maximum percentage, and total number of students
  def get_num_students_per_grade(num_students)
    min = @scenarioYAML["MINIMUM_GRADE_PERCENTAGE"]
    max = @scenarioYAML["MAXIMUM_GRADE_PERCENTAGE"]

    if min.nil?
      @log.error "MINIMUM_GRADE_PERCENTAGE must be set for a world to be created --> Exiting..."
      abort
    end
    if max.nil?
      @log.error "MAXIMUM_GRADE_PERCENTAGE must be set for a world to be created --> Exiting..."
      abort
    end

    ((random_on_interval(min, max) / 100) * num_students).round
  end

  # Uses the breakdown hash to bucketize number of students according to type of school
  # they would be enrolled at, and then computes the number of schools required to hold those
  # students according to national averages (average students per elementary school, ...).
  def create_edOrgs_using_breakdown
    num_elementary_school_students = 0
    num_middle_school_students     = 0
    num_high_school_students       = 0

    GradeLevelType::elementary.each { |level| num_elementary_school_students += @breakdown[level] }
    GradeLevelType::middle.each     { |level| num_middle_school_students     += @breakdown[level] }
    GradeLevelType::high.each       { |level| num_high_school_students       += @breakdown[level] }

    num_schools   = 0
    num_schools   = create_schools("elementary", num_schools, num_elementary_school_students)
    num_schools   = create_schools("middle", num_schools, num_middle_school_students)
    num_schools   = create_schools("high", num_schools, num_high_school_students)
    num_districts = update_schools_with_districts(num_schools)
    update_districts_with_states(num_districts)

    # re-shuffle breakdown so that it is representative of actual student distribution (this is primarly for scaled down scenarios)
    update_breakdown_based_on_student_distribution(@scenarioYAML["BEGIN_YEAR"])

    # choose the feeder schools
    WorldBuilder.choose_feeders(@world['elementary'], @world['middle'], @world['high'])
  end

  # go through education organizations and make sure that the @breakdown instance variable is representative of the world
  # that was built
  # -> divergence will occur when scenarios have been specified that are relatively 'small' (less than 500 students)
  # -> occurs because creation of education organizations assembles students into 'waves' (thought of as cohorts)
  def update_breakdown_based_on_student_distribution(year)
    new_breakdown = Hash.new
    @world["elementary"].each do |school|
      students_by_grade = school["students"][year]
      students_by_grade.each do |grade, num_students|
        new_breakdown[grade] = 0 if !new_breakdown.has_key?(grade)
        new_breakdown[grade] += num_students
      end
    end
    @world["middle"].each do |school|
      students_by_grade = school["students"][year]
      students_by_grade.each do |grade, num_students|
        new_breakdown[grade] = 0 if !new_breakdown.has_key?(grade)
        new_breakdown[grade] += num_students
      end
    end
    @world["high"].each do |school|
      students_by_grade = school["students"][year]
      students_by_grade.each do |grade, num_students|
        new_breakdown[grade] = 0 if !new_breakdown.has_key?(grade)
        new_breakdown[grade] += num_students
      end
    end
    @breakdown = new_breakdown
  end

  # uses the total number of students, as well as range of [min, max] to compute students at a school of type 'tag', and then
  # populates the edOrgs structure according to that tag (which should be "elementary", "middle", or "high").
  def create_schools(tag, num_schools, total_num_students)
    if tag == "elementary"
      avg_num_students = @scenarioYAML["AVERAGE_ELEMENTARY_SCHOOL_NUM_STUDENTS"]
      index = :elementary
    elsif tag == "middle"
      avg_num_students = @scenarioYAML["AVERAGE_MIDDLE_SCHOOL_NUM_STUDENTS"]
      index = :middle
    elsif tag == "high"
      avg_num_students = @scenarioYAML["AVERAGE_HIGH_SCHOOL_NUM_STUDENTS"]
      index = :high
    end
    avg_num_students_threshold = @scenarioYAML["AVERAGE_NUM_STUDENTS_THRESHOLD"]
    min                        = avg_num_students - (avg_num_students * avg_num_students_threshold)
    max                        = avg_num_students + (avg_num_students * avg_num_students_threshold)
    school_counter             = num_schools
    student_counter            = 0
    while student_counter < total_num_students
      current_students = random_on_interval(min, max).round
      school_counter   += 1
      current_students = (total_num_students - student_counter) if current_students > (total_num_students - student_counter)
      student_counter  += current_students
      school_id        = school_counter
      members          = []

      school_id, members = @pre_requisites[index].shift if @pre_requisites[index].size > 0

      # remember the school's state organization id if it's a String --> pop off later when creating education organizations
      @schools << school_id if school_id.kind_of? String
      
      staff, teachers    = create_staff_and_teachers_for_school(members)
      begin_year         = @scenarioYAML["BEGIN_YEAR"]

      school = {
        "id" => school_id, 
        "parent" => nil, 
        "sessions" => [], 
        "staff" => staff,
        "teachers" => teachers,
        "offerings" => {},
        "students" => {begin_year => assemble_students_into_waves(tag, current_students)},
        "programs" => create_programs_for_education_organization(tag, :SCHOOL),
      }
      if not @scenarioYAML['COURSES_ON_SEA']
        school["courses"] = create_courses(school_id)
      end
      @world[tag] << school
    end
    school_counter
  end

  # assembles students into waves by iterating through each grade and 'allocating' a section in each grade
  # until the specified number of students are exhausted.
  # -> if a small number of students are specified, it is likely that a single wave will exist
  # -> for larger numbers of students, many waves will be created
  def assemble_students_into_waves(type, num_students)
    if type == "elementary"
      WorldBuilder.get_students_per_grade(GradeLevelType.elementary, num_students)
    elsif type == "middle"
      WorldBuilder.get_students_per_grade(GradeLevelType.middle, num_students)
    elsif type == "high"
      WorldBuilder.get_students_per_grade(GradeLevelType.high, num_students)
    end
  end

  # takes the specified grades and uses the total number of students, as well as the minimum and maximum
  # number of students per section, to assemble 'waves' of students by grade
  def self.get_students_per_grade(grades, num_students)
    num_grades = grades.count
    students_per_grade = Hash[*grades.zip([num_students/num_grades].cycle(num_grades)).flatten]
    (0..(num_students % num_grades)-1).each{|i| students_per_grade[grades[i]] += 1 }
    students_per_grade
  end

  # updates the populated edOrgs arrays for elementary, middle, and high schools by determining how many schools are to be
  # contained in a given district (looks at yaml configuration file for average number of schools per district and threshold),
  # and then actually going and updating the schools to reference back to newly created local education agencies
  def update_schools_with_districts(num_schools)
    avg_num_schools_per_district = @scenarioYAML["AVERAGE_NUM_SCHOOLS_PER_DISTRICT"]
    avg_num_schools_threshold    = @scenarioYAML["AVERAGE_SCHOOLS_THRESHOLD"]
    min_num_schools_per_district = avg_num_schools_per_district - (avg_num_schools_per_district * avg_num_schools_threshold)
    max_num_schools_per_district = avg_num_schools_per_district + (avg_num_schools_per_district * avg_num_schools_threshold)
    all_schools      = (1..num_schools).to_a
    district_counter = num_schools
    school_counter   = 0
    while all_schools.size > 0
      district_counter += 1
      district_id      = district_counter
      members          = []
      num_schools_in_this_district = random_on_interval(min_num_schools_per_district, max_num_schools_per_district).round
      num_schools_in_this_district = (num_schools - school_counter) if num_schools_in_this_district > (num_schools - school_counter)

      district_id, members = @pre_requisites[:leas].shift if @pre_requisites[:leas].size > 0
 
      schools_in_this_district = []
      while @schools.size > 0 and num_schools_in_this_district > 0
        schools_in_this_district << @schools.shift
        num_schools_in_this_district -= 1
      end

      if num_schools_in_this_district > 0
        schools_in_this_district << all_schools.pop(num_schools_in_this_district)
        schools_in_this_district.flatten!
      end
      
      update_schools_with_district_id(district_id, schools_in_this_district)
      
      @world["leas"] << {"id" => district_id, 
        "parent" => nil, 
        "sessions" => [], 
        "staff" => create_staff_for_local_education_agency(members),
        "programs" => create_programs_for_education_organization("leas", :LOCAL_EDUCATION_AGENCY)
      }
      school_counter    += num_schools_in_this_district
    end
    district_counter
  end

  # actually does the work to iterate through the edOrgs structure (specifically the arrays
  # contained in the tags: "elementary", "middle", and "high" schools), finding edOrgs whose
  # unique "id" is contained within "schools_in_this_district", and sets the "parent" attribute
  # of those matching edOrgs to the district id.
  def update_schools_with_district_id(district_id, schools_in_this_district)
    # check in elementary, middle, and high schools
    @world["elementary"].each { |edOrg| edOrg["parent"] = district_id if schools_in_this_district.include?(edOrg["id"]) }
    @world["middle"].each     { |edOrg| edOrg["parent"] = district_id if schools_in_this_district.include?(edOrg["id"]) }
    @world["high"].each       { |edOrg| edOrg["parent"] = district_id if schools_in_this_district.include?(edOrg["id"]) }
  end

  # updates the populated edOrgs arrays for leas (local education agencies) by determining how many districts are to be
  # contained in a given state. current implementation assumes:
  # - single tier for local education agencies
  # - all local education agencies are contained within a single state
  # 
  # future implementation should create more 'layers' within the local education agency 'tier'
  # future implementation should look at yaml for average number of districts in a state and create multiple 
  #  state education agencies, as needed
  def update_districts_with_states(num_districts)
    state_id  = num_districts + 1
    members   = []
    state_id, members = @pre_requisites[:seas].shift if @pre_requisites[:seas].size > 0

    @world["seas"] << {"id" => state_id, 
      "courses" => (@scenarioYAML['COURSES_ON_SEA'] && create_courses(state_id)),
      "staff" => create_staff_for_state_education_agency(members), 
      "programs" => create_programs_for_education_organization("seas", :STATE_EDUCATION_AGENCY)}

    @world["leas"].each { |edOrg| edOrg["parent"] = state_id }
    @queue.push_work_order GraduationPlanFactory.new(state_id, @scenarioYAML)
  end

  # creates staff members at the state education agency level
  # -> initializes roles to be used at the state education agency level and calls generic create_staff method
  # -> allows optional input of staff members guaranteed to be created (rather than pseudo-randomly generated)
  def create_staff_for_state_education_agency(members = nil)
    create_staff_for_education_organization(get_default_state_education_agency_roles, members)
  end

  # creates staff members at the local education agency level
  # -> initializes roles to be used at the local education agency level and calls generic create_staff method
  # -> allows optional input of staff members guaranteed to be created (rather than pseudo-randomly generated)
  def create_staff_for_local_education_agency(members = nil)
    create_staff_for_education_organization(get_default_local_education_agency_roles, members)
  end  

  # creates staff members at the school level
  # -> initializes roles to be used at the school level and calls generic create_staff method
  # -> allows optional input of staff members guaranteed to be created (rather than pseudo-randomly generated)
  def create_staff_and_teachers_for_school(members = nil)
    staff    = create_staff_for_education_organization(get_default_school_roles, members)
    teachers = create_teachers_for_education_organization(members)
    return staff, teachers
  end

  # returns the list of default state education agency roles
  def get_default_state_education_agency_roles
    [:COUNSELOR, :INSTRUCTIONAL_COORDINATOR, :SPECIALIST_CONSULTANT, :STUDENT_SUPPORT_SERVICES_STAFF, :SUPERINTENDENT]
  end

  # returns the list of default local education agency roles
  def get_default_local_education_agency_roles
    [:ASSISTANT_SUPERINTENDENT, :LEA_ADMINISTRATIVE_SUPPORT_STAFF, :LEA_ADMINISTRATOR, :LEA_SPECIALIST, :LEA_SYSTEM_ADMINISTRATOR]
  end
  
  # returns the list of default school roles
  def get_default_school_roles
    [:ASSISTANT_PRINCIPAL, :ATHLETIC_TRAINER, :HIGH_SCHOOL_COUNSELOR, :INSTRUCTIONAL_AIDE, :LIBRARIAN, :PRINCIPAL, :SCHOOL_ADMINISTRATOR, :SCHOOL_NURSE]
  end  

  # creates staff members based on the specified roles and required staff members
  def create_staff_for_education_organization(roles, required)
    members = []
    if !required.nil? and required.size > 0
      required.each do |member|
        # skip this entry if its an Educator --> handled in 'create_teachers' method
        next if member[:role] == "Educator"
        
        @num_staff_members += 1
        members << {"id" => member[:staff_id], "role" => member[:role], "name" => member[:name]}
        for index in (0..(roles.size - 1)) do
          if StaffClassificationType.to_string(roles[index]) == member[:role]
            @log.info "Removing role: #{member[:role]} from default roles --> specified by member in staff catalog."
            roles.delete_at(index)
            break
          end
        end
      end
    end
    if !roles.nil? and roles.size > 0
      for index in (0..(roles.size - 1)) do
        @num_staff_members += 1
        members << {"id" => @num_staff_members, "role" => roles[index]}
      end
    end
    members
  end

  # creates teachers (ahead of time) based on required pre-requisites
  def create_teachers_for_education_organization(required)
    teachers = []
    if !required.nil? and required.size > 0
      required.each do |member|
        # skip this entry if its not an Educator --> handled in 'create_staff' method
        next if member[:role] != "Educator"

        @num_teachers += 1
        teachers << {"id" => member[:staff_id], "name" => member[:name]}
      end
    end
    teachers
  end

  # creates programs for the education organization
  def create_programs_for_education_organization(ed_org_type, sponsor)
    programs = []
    avg = @scenarioYAML["AVERAGE_NUM_PROGRAMS"][ed_org_type]
    min = (avg * (1 - @scenarioYAML["AVERAGE_NUM_PROGRAMS_THRESHOLD"])).round
    max = (avg * (1 + @scenarioYAML["AVERAGE_NUM_PROGRAMS_THRESHOLD"])).round
    num = DataUtility.select_random_from_options(@prng, (min..max).to_a)
    (1..num).each do 
      @unique_program_id += 1
      programs << {:id => @unique_program_id, :type => get_random_program_type, :sponsor => sponsor}
    end
    programs
  end

  # gets a random program type
  def get_random_program_type
    DataUtility.select_random_from_options(@prng, ProgramType.all).key
  end

  def build_world_from_edOrgs()
  	num_schools = @scenarioYAML["SCHOOL_COUNT"]
    @log.info "Creating world from initial number of schools: #{num_schools}"
    # NOT CURRENTLY SUPPORTED
    # update structure with time information
    #add_time_information_to_edOrgs
    @log.warn "Creating world from initial number of schools is not currently supported."
  end

  # creates sessions for each local education agency
  # -> uses 'BEGIN_YEAR' and 'NUMBER_OF_YEARS' properties specified in yaml configuration file (defaults to current year and 1, respectively, if not specified)
  # -> iterates from begin year to (begin year + num years), creating Sessions, Grading Periods, and Calendar Dates
  # -> each Session stores a date interval (contains start date, end date, number of instructional days, and holidays for that interval)
  def add_time_information_to_edOrgs
    begin_year   = @scenarioYAML["BEGIN_YEAR"]
    num_years    = @scenarioYAML["NUMBER_OF_YEARS"]
    
    if begin_year.nil?
      this_year = Date.today.year
      @log.info "Property: BEGIN_YEAR --> not set for scenario. Using default: #{this_year}"
      begin_year = this_year
    else
      @log.info "Property: BEGIN_YEAR --> Set in configuration: #{begin_year}"
    end

    if num_years.nil?
      @log.info "Property: NUMBER_OF_YEARS --> not set for scenario. Using default: 1"
      num_years = 1
    else
      @log.info "Property: NUMBER_OF_YEARS  --> Set in configuration: #{num_years}"
    end

    # loop over years updating infrastructure and population
    for year in begin_year..(begin_year+num_years-1) do
      # create session for LEAs
      # -> create grading period(s) for session
      # -> create calendar date(s) for grading period
      # -> [not implemented] iterate through schools and determine with some probability if school overrides existing lea session
      @log.info "creating session information for school year: #{year}-#{year+1}"
      @world["leas"].each_index do |index|
        edOrg = @world["leas"][index]
        if edOrg["id"].kind_of? String
          state_organization_id = edOrg["id"]
        else
        state_organization_id = DataUtility.get_local_education_agency_id(edOrg["id"])
        end
        
        start_date = DateUtility.random_school_day_on_interval(@prng, Date.new(year, 8, 25), Date.new(year, 9, 10))
        interval   = DateInterval.create_using_start_and_num_days(@prng, start_date, 180)

        session             = Hash.new
        session["term"]     = :YEAR_ROUND
        session["year"]     = year
        session["name"] = year.to_s + "-" + (year+1).to_s + " " + SchoolTerm.to_string(:YEAR_ROUND) + " session: " + state_organization_id.to_s
        session["interval"] = interval
        session["edOrgId"]  = state_organization_id
        @world["leas"][index]["sessions"] << session
      end
    end

    # update each of the schools already in existence with the sessions that were just created at the local education agency level
    # -> suggest implementing school 'extending' inherited sessions in these functions rather than above
    update_schools_with_sessions("elementary")
    update_schools_with_sessions("middle")
    update_schools_with_sessions("high")
    return begin_year, num_years
  end

  # iterate through schools of matching 'type' and set the sessions to be used
  def update_schools_with_sessions(type)
    @log.info "updating #{type} schools to include inherited sessions"
    @world[type].each_index do |index|
      school = @world[type][index]
      @world[type][index]["sessions"] = get_sessions_to_be_used_by_school(school)
    end
  end

  # uses the breakdown structure (maps each grade => number of students in that grade) to update student enrollment
  # counts at each school over the course of the simulation
  # -> this method will update the @breakdown structure as it walks through years
  # -> this method will embed enrollment information for each school on the school itself
  def expand_student_counts_using_time_information(begin_year, num_years)
    for year in (begin_year + 1)..(begin_year + num_years - 1) do
      # this method operates on @breakdown structure
      shuffle_students_forward

      # update students counts for each school
      update_students_within_school("elementary", year)
      update_students_within_school("middle", year)
      update_students_within_school("high", year)

      # after students have been shuffled...
      # -> update education organizations to include students that graduated and incoming kindergarten students
      assign_incoming_students("elementary", year)
      assign_incoming_students("middle", year)
      assign_incoming_students("high", year)
    end
  end

  # uses the specified education organization 'type' to determine which grade to analyze in the @breakdown instance variable,
  # and if incoming students are present, assigns them across education organizations of specified 'type'
  def assign_incoming_students(type, year)
    students_per_section = @scenarioYAML['STUDENTS_PER_SECTION'][type]
    if type == "elementary"
      grade = :KINDERGARTEN
    elsif type == "middle"
      grade = :SIXTH_GRADE
    elsif type == "high"
      grade = :NINTH_GRADE
    end
  
    if @breakdown[grade] > 0
      # actually perform split
      students_to_be_split = @breakdown[grade]
      students_so_far      = 0
      @log.info "year #{year}-#{year+1} --> there are #{students_to_be_split} #{GradeLevelType.to_string(grade)} students to be split across #{@world[type].size} schools"

      while students_to_be_split > 0
        @world[type].each_index do |index|
          num_students_assigned_to_this_school = students_per_section

          if students_to_be_split == 0
            num_students_assigned_to_this_school = 0
          elsif students_to_be_split < num_students_assigned_to_this_school
            num_students_assigned_to_this_school = students_to_be_split
          end
          students_so_far      += num_students_assigned_to_this_school
          students_to_be_split -= num_students_assigned_to_this_school

          @world[type][index]["students"][year][grade] += num_students_assigned_to_this_school

          if students_to_be_split == 0
            break
          end
        end
      end
    end
  end

  # iterates through education organizations of specified 'type' and ripples student counts forward for the specified 'year'
  def update_students_within_school(type, year)
    @world[type].each_index do |index|
      school = @world[type][index]
      students_by_year = school["students"]
      @world[type][index]["students"][year] = shuffle_students_forward_at_school(school["students"][year - 1]) if school["students"][year].nil?
      end
    end

  # ripples number of students forward to next year
  # -> allows 1st graders to become 2nd graders, 2nd graders to become 3rd graders, ...
  # -> actually performs work by iterating through grades in reverse order (12 -> 11 -> 10 ...)
  # -> number of 12th graders who graduated becomes number of incoming kindergarteners --> keeps number of students in world consistent
  # -> no current support for students who failed current grade to be held back
  def shuffle_students_forward
    grades          = GradeLevelType.get_ordered_grades.reverse
    kindergarteners = @breakdown[:TWELFTH_GRADE]
    grades.each     { |grade| @breakdown[GradeLevelType.increment(grade, 1)] = @breakdown[grade] unless grade == :TWELFTH_GRADE }
    @breakdown[:KINDERGARTEN] = kindergarteners
  end

  # ripples students forward to next year at current school
  def shuffle_students_forward_at_school(students_per_grade)
    new_breakdown = Hash.new
    grades        = GradeLevelType.get_ordered_grades.reverse
    grades.each do |grade|
      if students_per_grade.has_key?(grade) and students_per_grade.has_key?(GradeLevelType.increment(grade, 1))
        new_breakdown[GradeLevelType.increment(grade, 1)] = students_per_grade[grade]
        if grade == :KINDERGARTEN or grade == :SIXTH_GRADE or grade == :NINTH_GRADE
          new_breakdown[grade] = 0
        end
      end
    end
    new_breakdown
  end
  
  # create master schedule interchange
  # -> create course offerings
  # -> sections will be created later
  def create_master_schedule()
    # creating course offerings does not require any randomization --> only logic
    # -> computes intersection of courses to be offered at school with school's sessions
    offering_id = 0
    offering_id = create_course_offerings_for_school("elementary", offering_id)
    offering_id = create_course_offerings_for_school("middle", offering_id)
    offering_id = create_course_offerings_for_school("high", offering_id)
  end

  # iterate through schools of 'type' (should be "elementary", "middle", or "high" schools)
  # -> create course offerings by
  # --> getting set of sessions to be used
  # --> getting set of courses  to be used
  # -> iterate through sessions
  # -> iterate through courses
  # --> use {session, course} pair to create course offering at current school
  def create_course_offerings_for_school(type, num_offerings)
    offering_id = num_offerings
    @world[type].each_index do |index|
      school    = @world[type][index]
      sessions  = get_sessions_to_be_used_by_school(school)
      courses   = get_courses_to_be_used_by_school(type, school)
      sessions.each do |session|
        offerings    = []
        current_year = session["year"]
        courses.each do |grade, current_courses| 
          current_courses.each do |course|
            # increment course offering unique id
            offering_id                += 1
            # create course offering container
            offering = Hash.new
            offering["id"]        = offering_id
            offering["ed_org_id"] = DataUtility.get_school_id(school["id"], type)
            offering["session"]   = {"name"=>session["name"], "ed_org_id"=>session["edOrgId"]}
            offering["course"]    = course
            offering["grade"]     = grade
            
            # add course offering into 'offerings' (course offerings for current year)
            offerings << offering
          end
        end
        @world[type][index]["offerings"][current_year] = offerings
      end
    end
    offering_id
  end

  # gets the sequence of sessions to be used by the school when creating course offerings
  # -> if school has its own sessions, use those
  # -> if not, traverse up leas (and eventually seas) hierarchy until sessions are found
  # -> traverses until sessions are found or sea is reached (will not have a parent so while loop will be escaped)
  # -> if no education organization in the school's lineage has published sessions, an empty array will be returned
  def get_sessions_to_be_used_by_school(school)
    sessions = []
    if school["sessions"].nil? or school["sessions"].size == 0
      parent = get_parent_for_school(school)
      while !parent.nil? and sessions.size == 0
        if !parent["sessions"].nil? and parent["sessions"].size > 0
          sessions = parent["sessions"]
        else
          parent = get_parent_for_local_education_agency(parent)
        end
      end
    else
      # school has published its own sessions --> use those
      sessions = school["sessions"]
      # please note: right now, sessions of LEAs are stored on schools
      # -> there is no way to distinguish between a session published by the LEA or a school that is using its own
      # -> this will be mitigated in the future by writing sessions as they come into existence (in add_time method)
      # -> for now, school["sessions"] is used, but this does not imply that the school has actually published its own sessions
    end
    sessions
  end

  # finds the direct parent (local education agency) for a school
  # -> looks in @world structure under "leas"
  def get_parent_for_school(school)
    parent_id = school["parent"]
    parent    = @world["leas"].detect {|lea| lea["id"] == parent_id}
    parent
  end

  # finds the parent (local education agency or state education agency) for a local education agency
  # -> looks in @world structure under "leas"
  # -> looks in @world structure under "seas" (if no LEA parent was found)
  # this is done because local education agencies can nest a potentially unlimited number of times
  def get_parent_for_local_education_agency(local_education_agency)
    parent_id = local_education_agency["parent"]
    parent    = @world["leas"].detect {|lea| lea["id"] == parent_id}
    # if parent is nil --> no parent found under "leas"
    parent    = @world["seas"].detect {|sea| sea["id"] == parent_id} if parent.nil?
    parent
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
    parent    = @world["leas"].detect {|lea| lea["id"] == parent_id}
    while parent != nil
      parent_id = parent["parent"]
      parent    = @world["leas"].detect {|lea| lea["id"] == parent_id}
    end
    # parent_id should now be populated with id of the state education agency
    state_education_agency = @world["seas"].detect {|sea| sea["id"] == parent_id}
    
    courses = Hash.new
    if type == "elementary"
      grades = GradeLevelType.elementary
    elsif type == "middle"
      grades = GradeLevelType.middle
    elsif type == "high"
      grades = GradeLevelType.high
    end

    grades.each do |grade|
      # get the current set of courses that the state education agency has published
      # add state education agency id to courses --> makes life easier when creating course offering
      current_courses              = (state_education_agency["courses"] or school["courses"])[grade]
      courses[grade]               = current_courses
    end

    courses
  end

  # writes ed-fi xml interchanges
  def create_work_orders
    create_education_organization_work_orders
    create_education_org_calendar_work_orders
    create_master_schedule_work_orders
    create_staff_association_work_orders
    create_student_and_enrollment_work_orders
    create_competency_level_descriptor_work_order
  end

  # writes ed-fi xml interchange: education organization entities:
  # - StateEducationAgency
  # - LocalEducationAgency
  # - School [Elementary, Middle, High]
  # - Course
  # - [not yet implemented] Program
  def create_education_organization_work_orders
    # write state education agencies
    @world["seas"].each do |edOrg|
      ed_org_id = ""
      if edOrg["id"].kind_of? String
        ed_org_id = edOrg["id"]
      else
        ed_org_id = DataUtility.get_state_education_agency_id(edOrg["id"])
      end
      @queue.push_work_order({ :type => StateEducationAgency, :id => ed_org_id, :programs => get_program_ids(edOrg["programs"]) })
      
      create_course_work_orders(ed_org_id, edOrg["courses"] || [])
      create_program_work_orders(edOrg["programs"])
    end

    # write local education agencies
    @world["leas"].each       { |edOrg|
      @queue.push_work_order({ :type => LocalEducationAgency, :id => edOrg["id"], :parent => edOrg["parent"], :programs => get_program_ids(edOrg["programs"]) })
      create_program_work_orders(edOrg["programs"])
    }

    # write elementary, middle, and high schools 
    ["elementary", "middle", "high"].each{|classification|
      @world[classification].each { |edOrg|
        @queue.push_work_order({ :type => School, :id => edOrg["id"], :parent => edOrg["parent"], :classification => classification, :programs => get_program_ids(edOrg["programs"])})
        create_program_work_orders(edOrg["programs"])
        create_course_work_orders(edOrg['id'], edOrg["courses"] || [])
        create_cohorts DataUtility.get_school_id(edOrg['id'], classification)
      }
    }
  end

  # writes ed-fi xml interchange: education organization calendar entities:
  # - Session
  # - GradingPeriod
  # - CalendarDate
  def create_education_org_calendar_work_orders
    # write sessions at the district level
    @world["leas"].each do |ed_org|
      # get sessions currently stored on local education agency
      # iterate through sessions
      sessions  = ed_org["sessions"]
      if ed_org["id"].kind_of? String
        ed_org_id = ed_org["id"]
      else
        ed_org_id = DataUtility.get_local_education_agency_id(ed_org["id"])
      end
      
      sessions.each do |session|
        interval  = session["interval"]
        year      = session["year"]
        name      = session["name"]
        term      = session["term"]

        # create calendar date(s) using interval
        # create grading period(s) using interval, school year, and state organization id
        calendar_dates  = create_calendar_dates(interval, ed_org_id)        
        grading_periods = create_grading_periods(interval, year, ed_org_id)

        # create and write session
        @queue.push_work_order( {:type=>Session, :name=>name, :year=>year, :term=>term, :interval=>interval, :edOrg => ed_org_id, :gradingPeriods => grading_periods})

        # write grading period(s)
        grading_periods.each do |grading_period|
          type      = grading_period["type"]
          year      = grading_period["year"]
          interval  = grading_period["interval"]
          ed_org_id = grading_period["ed_org_id"]

          @queue.push_work_order({:type=>GradingPeriod, :period_type=>type, :year=>year, :interval=>interval, :edOrg=>ed_org_id, :calendarDates => calendar_dates})
        end

        # write calendar date(s)
        calendar_dates.each do |calendar_date|
          @queue.push_work_order({:type=>CalendarDate, :date=>calendar_date["date"], :event => calendar_date["event"], :edOrgId => calendar_date["ed_org_id"]})
        end
      end
    end
  end

  # writes ed-fi xml interchange: master schedule entities:
  # - CourseOffering
  def create_master_schedule_work_orders
    create_course_offerings_work_order("elementary")
    create_course_offerings_work_order("middle")
    create_course_offerings_work_order("high")
  end

  # writes ed-fi xml interchange: staff association entities:
  # - Staff
  # - [not implemented yet] StaffEducationOrgAssignmentAssociation
  def create_staff_association_work_orders
    ['seas', 'leas', 'elementary', 'middle', 'high'].each{|type|
      create_staff_at_ed_org_work_orders(type)
    }
    create_staff_cohort_associations
  end

  # writes the staff members and teachers out for each education organization that is of the specified 'type'
  def create_staff_at_ed_org_work_orders(type)
    @world[type].each_index do |ed_org_index|
      ed_org    = @world[type][ed_org_index]
      ed_org_id = ed_org["id"]
      sessions  = ed_org["sessions"]

      # create staff members for the current education organization
      if !ed_org["staff"].nil? and ed_org["staff"].size > 0
        ed_org["staff"].each do |member|
          year_of = Date.today.year - random_on_interval(25, 65)
          @queue.push_work_order({:type=>Staff, :id=>member["id"], :year=>year_of, :name=>member["name"]})
          
          if !sessions.nil? and sessions.size > 0
            # create staff members for {elementary, middle, high} schools and local education agencies in the same way
            # -> use their published (or inherited) sessions
            # -> perform no date manipulation (sets offset to zero)
            offset = 0
          else
            # state education agencies are not publishing sessions, so find a local education agency whose parent is
            # the state education agency being processed, and use its sessions (with minor manipulations)
            sessions = find_sessions_from_child_district(ed_org_id)
            offset   = 30
          end
          create_staff_ed_org_associations_for_sessions(sessions, offset, member, ed_org_id, type)
          create_staff_program_associations_for_sessions(sessions, offset, member, ed_org["programs"])
        end
      end


      # create teachers for the current education organization
      if !ed_org["teachers"].nil? and ed_org["teachers"].size > 0
        ed_org["teachers"].each_index do |teacher_index|
          teacher  = ed_org["teachers"][teacher_index]
          year_of  = Date.today.year - random_on_interval(25, 65)
          grades   = teacher["grades"]
          subjects = teacher["subjects"]
          
          if grades.nil? or grades.size == 0
            grades = [DataUtility.get_random_grade_for_type(@prng, type)]
            @world[type][ed_org_index]["teachers"][teacher_index]["grades"] = grades
          end
          if subjects.nil? or subjects.size == 0
            subjects = DataUtility.get_random_academic_subjects_for_type(@prng, type)
            @world[type][ed_org_index]["teachers"][teacher_index]["subjects"] = subjects
          end
          @log.info "assigning teacher from catalog: #{teacher["id"]} to education organization: #{ed_org_id}"
        end
      end
    end
  end

  def create_staff_cohort_associations
    ['elementary', 'middle', 'high'].each{|type|
      @world[type].each{|ed_org|
        staff = ed_org['staff'].cycle
        ed_org['sessions'].each{|session|
          WorldBuilder.cohorts(ed_org['id'], @scenarioYAML).each{|cohort|
            @queue.push_work_order(
              StaffCohortAssociation.new(staff.next['id'], cohort, @scenarioYAML['STAFF_HAVE_COHORT_ACCESS'], session['interval'].get_begin_date))
          }
        }
      }
    }
  end

  # uses the specified state education agency's state organization id to find a child local education agency,
  # and returns the sessions that the local education agency has published
  # -> does not perform any manipulation to sessions
  def find_sessions_from_child_district(ed_org_id)
    sessions = []
    child    = @world["leas"].detect {|lea| lea["parent"] == ed_org_id}
    child["sessions"]
  end


  # iterates through sessions, using begin and end date, to assemble staff -> education organization associations (assignment, not employment)
  # -> manipulates date interval of each session by 'offset' (subtracts offset from begin_date, adds offset to end_date)
  def create_staff_ed_org_associations_for_sessions(sessions, offset, member, ed_org_id, type)
    if !sessions.nil? and sessions.size > 0
      sessions.each do |session|
        title = member["role"]
        if StaffClassificationType.to_symbol(title).nil?
          classification = get_staff_classification_for_ed_org_type(type)
        else          
          classification = StaffClassificationType.to_symbol(title)
        end

        if ed_org_id.kind_of? Integer
          state_org_id = DataUtility.get_state_education_agency_id(ed_org_id) if type == "seas"
          state_org_id = DataUtility.get_local_education_agency_id(ed_org_id) if type == "leas"
          state_org_id = DataUtility.get_elementary_school_id(ed_org_id)      if type == "elementary"
          state_org_id = DataUtility.get_middle_school_id(ed_org_id)          if type == "middle"
          state_org_id = DataUtility.get_high_school_id(ed_org_id)            if type == "high"
        else
          state_org_id = ed_org_id
        end
                
        interval   = session["interval"]
        begin_date = interval.get_begin_date - offset
        end_date   = interval.get_end_date   + offset
        @queue.push_work_order({:type=>StaffEducationOrgAssignmentAssociation, :id=>member["id"], :edOrg=>state_org_id, :classification=>classification,
                               :title=>title, :beginDate=>begin_date, :endDate=>end_date})
      end
    end
  end

  # iterates through sessions, using begin and end date, to assemble staff -> program associations
  # -> manipulates date interval of each session by 'offset' (subtracts offset from begin_date, adds offset to end_date)
  # -> randomly chooses the number of program associations each staff member will have, then randomly selects programs
  def create_staff_program_associations_for_sessions(sessions, offset, member, programs)
    if !sessions.nil? and sessions.size > 0
      min = @scenarioYAML["MINIMUM_NUM_PROGRAMS_PER_STAFF_MEMBER"]
      max = @scenarioYAML["MAXIMUM_NUM_PROGRAMS_PER_STAFF_MEMBER"]
      sessions.each { |session| create_staff_program_association(session, offset, member, programs, min, max) }
    end
  end  

  # creates a staff program association using the specified session to extract begin and end dates, manipulates those
  # dates by specified 'offset', and then creates the association between the specified staff 'member' a subset of 
  # randomly selected programs (where the subset of randomly selected programs falls on the interval [min,max])
  def create_staff_program_association(session, offset, member, programs, min, max)
    interval     = session["interval"]
    begin_date   = interval.get_begin_date - offset
    end_date     = interval.get_end_date   + offset
    num_programs = DataUtility.select_random_from_options(@prng, (min..max).to_a)
    my_programs  = DataUtility.select_num_from_options(@prng, num_programs, programs)
    my_programs.each do |program|
      access = DataUtility.select_random_from_options(@prng, [true, false])
      staff_id   = DataUtility.get_staff_unique_state_id(member["id"])
      program_id = DataUtility.get_program_id(program[:id])
      @queue.push_work_order({:type=>StaffProgramAssociation, :staff=>staff_id, :program=>program_id, :access=>access, :begin_date=>begin_date, :end_date=>end_date})
    end
  end

  # based on the education organization type (state education agecy, local education agency, or school), choose a staff classification type
  def get_staff_classification_for_ed_org_type(type)
    return DataUtility.select_random_from_options(@prng, get_default_state_education_agency_roles) if type == "seas"
    return DataUtility.select_random_from_options(@prng, get_default_local_education_agency_roles) if type == "leas"
    return DataUtility.select_random_from_options(@prng, get_default_school_roles)
  end

  # writes the course offerings for each 'type' of school ("elementary", "middle", or "high" school)
  # -> iterates through schools of 'type'
  # -> if the school has course offerings
  # --> assembles course offering and passes to master schedule interchange writer
  def create_course_offerings_work_order(type)
    @world[type].each do |school|
      if !school["offerings"].nil? and school["offerings"].size > 0
        school["offerings"].each do |year, course_offerings|
          course_offerings.each do |course_offering|
            id        = course_offering["id"]
            ed_org_id = course_offering["ed_org_id"]
            session   = course_offering["session"]
            course    = course_offering["course"]
            grade     = course_offering["grade"]
          
            title = GradeLevelType.to_string(grade) + " " + course["title"]
            title = GradeLevelType.to_string(grade) if GradeLevelType.is_elementary_school_grade(grade)

            @queue.push_work_order({:type=>CourseOffering, :id=>id, :title=>title, :edOrgId=>ed_org_id, :session=>session, :course=>course})
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
  def create_calendar_dates(interval, ed_org_id)
    calendar_dates = []
    begin_date     = interval.get_begin_date
    end_date       = interval.get_end_date
    holidays       = interval.get_holidays
    (begin_date..end_date).step(1) do |date|
      if holidays.include?(date)
        calendar_dates << {"date" => date, "event" => :HOLIDAY, "ed_org_id" => ed_org_id}
      else
        calendar_dates << {"date" => date, "event" => :INSTRUCTIONAL_DAY, "ed_org_id" => ed_org_id} if date.wday != 0 and date.wday != 6
      end
    end
    calendar_dates
  end

  # creates an array of grading periods that occur over the specified interval
  # -> currently, only a single grading period is generated (for whole session)
  # -> future implementations should take an additional variable that specifies breakdown into multiple grading periods
  def create_grading_periods(interval, year, ed_org_id)
    grading_periods = []
    grading_periods << {"type" => :END_OF_YEAR, "year" => year, "interval" => interval, "ed_org_id" => ed_org_id}
    grading_periods
  end

  # creates courses at the state education agency by populating a 'course catalog'
  # initially assumes a very simple course model
  # -> each grade contains Science, Math, English, and History
  # -> no honors or multiple course paths
  def create_courses(ed_org_id)
    courses = Hash.new
    course_counter = 0
    GradeLevelType::get_ordered_grades.each do |grade|
      current_grade_courses = Array.new
      if !@scenarioYAML[grade.to_s + "_COURSES"].nil?
        @scenarioYAML[grade.to_s + "_COURSES"].each do |course|
          course_counter += 1
          current_grade_courses << {"id" => course_counter, "title" => course, "ed_org_id" => ed_org_id}
        end
      else
        course_counter += 1
        current_grade_courses << {"id" => course_counter, "title" => GradeLevelType.to_string(grade), "ed_org_id" => ed_org_id}
      end
      courses[grade] = current_grade_courses
    end
    courses
  end

  # creates course work orders to be written to the education organization interchange
  def create_course_work_orders(edOrgId, courses)
    courses.each do |key, value|
      grade = GradeLevelType.to_string(key)
      value.each do |course|
        id = DataUtility.get_course_unique_id(course["id"])
        if GradeLevelType.is_elementary_school_grade(key)
          title = grade
        else
          title = grade + " " + course["title"]
        end
        @queue.push_work_order({:type => Course, :id => id, :title => title, :edOrgId => edOrgId})
      end
    end
  end

  # creates program work orders to be written to the education organization interchange
  def create_program_work_orders(programs)
    if programs.nil? == false and programs.size > 0
      programs.each do |program|
        @queue.push_work_order({:type => Program, :id => DataUtility.get_program_id(program[:id]), :program_type => program[:type], :sponsor => program[:sponsor]})
      end
    end
  end

  # get the program identifiers (in correct format)
  def get_program_ids(programs)
    program_ids = []
    if programs.nil? == false and programs.size > 0
      programs.each do |program|
        program_ids << DataUtility.get_program_id(program[:id])
      end
    end
    program_ids
  end

  def create_cohorts(ed_org)
    WorldBuilder.cohorts(ed_org, @scenarioYAML).each{ |cohort|
      @queue.push_work_order(cohort)
    }
  end

  def self.cohorts(ed_org, scenario)
    (1..(scenario['COHORTS_PER_SCHOOL'] or 0)).map{ |i|
      Cohort.new(i, ed_org, scope: "School")
    }
  end

  # computes a random number on the interval [min, max]
  # does NOT round
  def random_on_interval(min, max)
    min + @prng.rand(max - min)
  end

  def self.choose_feeders(elem, mid, high)
    hs_cycle = high.cycle
    mid.each{|school|
      hs = hs_cycle.next
      school['feeds_to'] = [hs['id']]
    }
    mid_cycle = mid.cycle
    elem.each{|school|
      ms = mid_cycle.next
      school['feeds_to'] = [ms['id']]
    }
  end

  # generates both section and student work orders
  # -> section work orders drive creation of students
  def generate_student_work_orders
    section_factory = SectionWorkOrderFactory.new(@world, @scenarioYAML, @prng)
    student_factory = StudentWorkOrderFactory.new(@world, @scenarioYAML, section_factory)
    Enumerator.new do |yielder|
      # needs to be in this order for boundary students
      # -> if an elementary school student graduates to middle school, we need to guarantee that the
      #    infrastructure for the middle school exists (same for middle -> high school graduation)
      create_work_orders_using_factories(section_factory, student_factory, "high", yielder)
      create_work_orders_using_factories(section_factory, student_factory, "middle", yielder)
      create_work_orders_using_factories(section_factory, student_factory, "elementary", yielder)
    end
  end

  # uses the specified section factory and student factory to create sections for 
  # education organizations of specified 'type'
  def create_work_orders_using_factories(section_factory, student_factory, type, yielder)
    @world[type].each { |school|
      section_factory.generate_sections_with_teachers(school, type, yielder)
      student_factory.generate_work_orders(school, yielder)
    }
  end

  def create_student_and_enrollment_work_orders
    generate_student_work_orders.each { |work_order| @queue.push_work_order(work_order) }
  end

  def create_competency_level_descriptor_work_order
    (@scenarioYAML["COMPETENCY_LEVEL_DESCRIPTORS"] or []).each_with_index{ |competency_level_descriptor|
      @queue.push_work_order CompetencyLevelDescriptor.new(competency_level_descriptor['code_value'], competency_level_descriptor['description'], competency_level_descriptor['performance_base_conversion'])
    }
  end

  def create_assessments(begin_year, num_years)
    factory = AssessmentFactory.new(@scenarioYAML)
    (begin_year..(begin_year + num_years -1)).each{|year|
      gen_parent = true
      GradeLevelType.get_ordered_grades.each{|grade|
        @queue.push_work_order GradeWideAssessmentWorkOrder.new(grade, year, gen_parent, factory)
        gen_parent = false
      }
    }
  end

  def create_descriptors
    sea = @world['seas'][0]
    (@scenarioYAML["BEHAVIORS"] or []).each_with_index{ |behavior, index|
      @queue.push_work_order BehaviorDescriptor.new("BE#{index}", behavior['short'], behavior['desc'], sea['id'], behavior['category'])
    }
    (@scenarioYAML["DISCIPLINE_OPTIONS"] or []).each_with_index{ |discipline, index|
      @queue.push_work_order DisciplineDescriptor.new("DI#{index}", discipline['short'], discipline['desc'], sea['id'])
    }
  end

  def create_learning_objectives
    GradeLevelType.get_ordered_grades.each{|grade|
      AcademicSubjectType.get_academic_subjects(grade).each {|academic_subject|
        LearningObjective.build_learning_objectives((@scenarioYAML["NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE"] or 2), AcademicSubjectType.to_string(academic_subject), GradeLevelType.to_string(grade)).each {|learning_objective|
          @queue.push_work_order learning_objective
        }
      }
    }
  end

end
