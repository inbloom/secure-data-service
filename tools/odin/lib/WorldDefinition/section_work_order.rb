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

require_relative '../Shared/data_utility.rb'
require_relative '../Shared/EntityClasses/enum/ClassroomPositionType.rb'
require_relative '../Shared/EntityClasses/section.rb'
require_relative '../Shared/EntityClasses/staff_ed_org_assignment_association.rb'
require_relative '../Shared/EntityClasses/teacher.rb'
require_relative '../Shared/EntityClasses/teacher_school_association.rb'
require_relative '../Shared/EntityClasses/teacher_section_association.rb'
require_relative 'gradebook_entry_factory.rb'

# factory for creating section work orders using the specified scenario (and corresponding world)
class SectionWorkOrderFactory

  def initialize(world, scenario, prng)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @world = world
    @scenario = scenario
    @teachers = {}
    @prng = prng
    @teacher_unique_state_id = 0
    @start_index = 1
    @gradebook_entry_factory = GradebookEntryFactory.new(@scenario)
  end

  # uses the instantiated @world to generate sections (and corresponding teachers) for the specified education organization
  # -> expected behaviour: teachers will NOT come into existence until sections, populated with students, are available for them to teach.
  #    for small scenarios (less than 500 students), it is possible that pre-requisite teachers will not come into existence until later years
  def generate_sections_with_teachers(ed_org, ed_org_type, yielder = [])
    school_id    = DataUtility.get_school_id(ed_org['id'], ed_org_type.to_sym)
    ed_org_index = @world[ed_org_type].index(ed_org) if @world[ed_org_type].nil? == false and @world[ed_org_type].size > 0

    @teachers[school_id] = [] if @teachers[school_id].nil?
    @world[ed_org_type][ed_org_index]['sections'] = {}
    unless ed_org['students'].nil?
      ed_org['students'].each{|year, student_map|
        @world[ed_org_type][ed_org_index]['sections'][year] = {}
        unless ed_org['offerings'].nil?
          student_map.each{|grade, num|
            @world[ed_org_type][ed_org_index]['sections'][year][grade] = {}

            teachers_for_this_grade = ed_org['teachers'].select { |teacher| teacher['grades'].include? grade }
            teachers_for_this_grade.each { |teacher| teacher['num_sections'] = sections_for_this_teacher(ed_org_type) } if !teachers_for_this_grade.nil? and teachers_for_this_grade.size > 0
            
            # take sections_from_edorg output and add teachers to sections
            # -> num_sections for each teacher is reset for each new year
            sections_with_teachers = add_teachers_to_sections(sections_from_edorg(ed_org, ed_org_type, year, grade), teachers_for_this_grade, ed_org_type)

            sections_with_teachers.each{ |offering, sections|
              @world[ed_org_type][ed_org_index]['sections'][year][grade][offering['id']] = [] 
              session = find_matching_session_for_school(school_id, offering)
              sections.each{ |section|
                teacher_id = 0
                isFirstTeacherForSection = true
                section[:section_teachers].each { |section_teacher|
                  if teacher_does_not_exist_yet(section_teacher['id'])
                    year_of = Date.today.year - DataUtility.select_random_from_options(@prng, (25..65).to_a)
                    # keep :name => nil in work order --> Teacher entity class will lazily create name for teacher if its nil
                    yielder << {:type=>Teacher, :id=>section_teacher['id'], :year=>year_of, :name=>section_teacher['name']}
                  end
                  teacher_id = section_teacher['id']
                  if !@teachers[school_id].include?(teacher_id)
                    @teachers[school_id] << teacher_id
                    yielder << {:type=>TeacherSchoolAssociation, 
                      :id=>teacher_id,
                      :school=>school_id, 
                      :assignment=>:REGULAR_EDUCATION, 
                      :grades=>[grade], 
                      :subjects=>section_teacher['subjects']
                    }
  
                    # add staff -> education organization assignment association for current session (where teacher is associated to school)
                    # -> this will eventually allow us to migrate teachers (even pre-requisite teachers) across education organizations as part of 
                    #    the current simulation
                    ed_org_associations = create_staff_ed_org_association_for_teacher(session, teacher_id, school_id, ed_org_type)
                    ed_org_associations.each { |order| yielder << order }
  
                    # add staff -> program associations (currently very random)
                    # future implementations should be based off of a program catalog, OR more intelligence in reacting to students during simulation
                    program_associations = create_staff_program_associations_for_teacher(session, teacher_id, ed_org["programs"])
                    program_associations.each { |order| yielder << order }
                  end
                  # A section can have multiple teachers, assign the first teacher to the section
                  if isFirstTeacherForSection
                    yielder << {:type=>TeacherSectionAssociation, :teacher=>teacher_id, :section=>section[:id], :school=>school_id, :position=>:TEACHER_OF_RECORD}
                    isFirstTeacherForSection = false
                  end
                }

                # generate gradebook entries here
                # -> need session for interval (start date and end date of session)
                # use section[:id] and offering['ed_org_id'] for section
                # use offering['grade'] for current grade
                gradebook_entries = @gradebook_entry_factory.generate_entries(@prng, grade, session, 
                  {:ed_org_id => offering['ed_org_id'], :unique_section_code => DataUtility.get_unique_section_id(section[:id])})

                # use unique section ids that map to each course offering --> stored in the @world
                # -> determine number of gradebook entries here, and store with section :id
                @world[ed_org_type][ed_org_index]['sections'][year][grade][offering['id']] << { :id => section[:id], :gbe => get_gradebook_entry_breakdown(grade, gradebook_entries) }
                
                gradebook_entries.each { |entry| yielder << entry }

                yielder << {:type=>Section, :id=>section[:id], :edOrg=>school_id, :offering=>offering}
                unless session.nil?
                  dates = session['interval']
                else
                  dates = DateInterval.new(Date.new, Date.new + 180, 180)
                end
                (@scenario['INCIDENTS_PER_SECTION'] or 0).times {|i|
                  yielder << DisciplineIncident.new(i, section[:id], school_id, teacher_id, dates, "Classroom")
                }
              }
            }
          }
        end
      }
    end
  end

  # gets the breakdown of gradebook entries created (performs lookup into @scenario for specified grade)
  def get_gradebook_entry_breakdown(grade, entries)
    breakdown = {}
    @scenario['GRADEBOOK_ENTRIES_BY_GRADE'][GradeLevelType.to_string(grade)].each do |type, guidelines|
      current_type = entries.select { |entry| entry[:gbe_type] == type }
      breakdown[type] = current_type.size
    end
    breakdown
  end

  # returns true if the teacher does NOT exist (and needs to be created), and false otherwise (teacher already exists)
  # -> used for determining whether or not to create a Teacher ed-fi entity for the incoming teacher
  # -> will check every school in the world to see if teacher exists anywhere
  def teacher_does_not_exist_yet(teacher_id)
    needs_to_be_created = true
    if !@teachers.nil? and @teachers.size > 0
      @teachers.each do |school, current_teachers|
        if current_teachers.include?(teacher_id)
          needs_to_be_created = false
          break
        end
      end
    end
    needs_to_be_created
  end

  def sections(id, type, year, grade)
    if (@world.nil? == false && @world[type].nil? == false)
      ed_org = @world[type].find{ |s| s['id'] == id }
      return ed_org['sections'][year][grade] unless ed_org.nil? or ed_org['sections'].nil? or ed_org['sections'][year].nil?
    end
    return nil
  end

  def sections_from_edorg(ed_org, ed_org_type, year, grade)
    section_map = {}
    if (ed_org['offerings'].nil? == false && ed_org['offerings'][year].nil? == false)
      offerings = ed_org['offerings'][year].select{|c| c['grade'] == grade}
      offerings.each{ |course| 
        sections_range      = find_sections(ed_org, ed_org_type, year, grade, course, @start_index) 
        @start_index        += sections_range.to_a.size
        section_map[course] = sections_range
      }
    end
    section_map
  end

  # take sections (map entries: {course offering --> range of sections}) and unroll range of sections to be an array
  # with elements that are {section=>section_id, teacher=>teacher_id}
  # -> teachers represents pre-requisite teachers that must have sections created first
  # -> grades that don't have pre-requisite teachers will create new of teachers
  # -> future implementations will update @world with newly created teachers
  def add_teachers_to_sections(sections, teachers, type)
    sections_with_teachers     = {}
    sections.each { |offering, section_range|
      # skip this offering if the section range is nil
      next if section_range.nil?

      sections_for_this_offering  = []

      section_range.each do |section|

        section_teachers = []
        teachers_per_section = DataUtility.rand_float_to_int(@prng, @scenario['TEACHERS_PER_SECTION'] || 1)
        for t in 1..teachers_per_section
          teacher = teachers.shift
          if teacher.nil?
            # need to create teacher
            # use course offering to create teacher school association
            @teacher_unique_state_id += 1
            ed_org_id                = offering['ed_org_id']
            teacher                  = create_teacher(type, DataUtility.get_teacher_unique_state_id(@teacher_unique_state_id), offering['grade'])          
            added_teacher            = push_new_teacher_into_world(teacher, ed_org_id, type)
            @log.warn "Failed to push teacher with id: #{teacher['id']} into world" if !added_teacher
          end
          section_teachers << teacher
        end        

        # decrement the number of sections available to the current teacher and push them back onto the @world
        # -> this operation will push the teacher onto the end of the 'teachers' queue for the given education organization,
        #    so sections will be spread evenly across teachers
        sections_for_this_offering << {:id=>section, :section_teachers=>section_teachers}
        if !teacher['num_sections'].nil?
          teacher['num_sections'] = teacher['num_sections'] - 1
          teachers << teacher if teacher['num_sections'] > 0
        end
      end
      sections_with_teachers[offering] = sections_for_this_offering
    }
    sections_with_teachers
  end

  private

  # performs the work of pushing the teacher onto the corresponding education organization (of specified 'type')
  # by iterating through @world[type] (such as @world["elementary"] --> all elementary schools) to match the specified
  # education organization id
  # -> returns true if the education organization was found (and teacher was pushed onto education organization)
  # -> returns false if the education organization was not found (teacher was not pushed)
  def push_new_teacher_into_world(teacher, ed_org_id, type)
    pushed_teacher = false
    return pushed_teacher if @world[type].nil?
    @world[type].each_index do |ed_org_index|
      ed_org = @world[type][ed_org_index]
      ed_org_str = DataUtility.get_school_id(ed_org['id'], type)
      if DataUtility.get_school_id(ed_org['id'], type) == ed_org_id
        pushed_teacher = true
        @world[type][ed_org_index]['teachers'] << teacher
        break
      end
    end
    pushed_teacher
  end

  # finds the school with the specified education organization id
  # -> returns the sessions stored on that school in the @world
  def find_matching_session_for_school(ed_org_id, course_offering)
    ed_org = find_school_with_id(ed_org_id, "elementary")
    ed_org = find_school_with_id(ed_org_id, "middle") if ed_org.nil?
    ed_org = find_school_with_id(ed_org_id, "high") if ed_org.nil?
    
    # 'ed_org' now contains the education organization matching ed_org_id (if it was found)
    session = ed_org['sessions'].detect { |session| session['name'] == course_offering['session']['name'] } if !ed_org.nil?
    session
  end

  def find_school_with_id(id, type)
    return @world[type].detect {|school| school["id"] == id} if @world[type].nil? == false
    return nil
  end

  # creates staff -> education organization assignment association for the teacher at the specified school, in the specified session
  def create_staff_ed_org_association_for_teacher(session, teacher_id, ed_org_id, type)
    associations = []
    if !@scenario["HACK_NO_STAFF_EDORG_ASSOCIATIONS_EXCEPT_SEA"] && !session.nil?
      if ed_org_id.kind_of? Integer
        state_org_id = DataUtility.get_state_education_agency_id(ed_org_id) if type == "seas"
        state_org_id = DataUtility.get_local_education_agency_id(ed_org_id) if type == "leas"
        state_org_id = DataUtility.get_school_id(ed_org_id, type)
      else
        state_org_id = ed_org_id
      end

      classification = :TEACHER
      title          = "Educator"
      interval       = session["interval"]
      begin_date     = interval.get_begin_date
      end_date       = interval.get_end_date
      associations << {:type=>StaffEducationOrgAssignmentAssociation, :id=>teacher_id, :edOrg=>state_org_id,
                       :classification=>classification, :title=>title, :beginDate=>begin_date, :endDate=>end_date}
    end 
    associations
  end

  # creates staff -> program association for the teacher in the specified session
  # -> randomly selects the number of programs the teacher is involved in (range is specified in scenario configuration)
  # -> assembles a subset of available programs and creates associations
  def create_staff_program_associations_for_teacher(session, teacher_id, programs)
    associations = []
    if !session.nil?
      staff_program_associations_per_teacher = DataUtility.rand_float_to_int(@prng, @scenario["HACK_STAFF_PROGRAM_ASSOCIATIONS_FOR_TEACHER"] || 1)
      for a in 1..staff_program_associations_per_teacher
        min          = @scenario["MINIMUM_NUM_PROGRAMS_PER_TEACHER"]
        max          = @scenario["MAXIMUM_NUM_PROGRAMS_PER_TEACHER"]
        interval     = session["interval"]
        begin_date   = interval.get_begin_date
        end_date     = interval.get_end_date
        num_programs = DataUtility.select_random_from_options(@prng, (min..max).to_a)
        my_programs  = DataUtility.select_num_from_options(@prng, num_programs, programs)
        my_programs.each do |program|
          access = DataUtility.select_random_from_options(@prng, [true, false])
          program_id = DataUtility.get_program_id(program[:id])
          associations << {:type=>StaffProgramAssociation, :staff=>teacher_id, :program=>program_id, :access=>access, :begin_date=>begin_date, :end_date=>end_date}
        end
      end
    end
    associations
  end

  def find_sections(ed_org, ed_org_type, year, grade, course, start_index = 1)
    student_count = ed_org['students'][year][grade]
    section_count = (student_count.to_f / students_per_section(ed_org_type)).ceil
    return nil if section_count < 1
    start_index..(start_index + section_count - 1)
  end

  # creates a teacher specific to education organization of specified 'type' (elementary, middle, or high school)
  # -> type is used to determine academic subjects
  # -> id is used to create a teacher unique state id
  # -> grade is used to assign the teacher at the specified grade
  def create_teacher(type, id, grade)
    subjects = DataUtility.get_random_academic_subjects_for_type(@prng, type)
    # name is intentionally left nil here
    # -> signal for Teacher.new (when invoked via teacher work order) to randomly create name for this teacher
    {"id"=>id, "name"=>nil, "grades"=>[grade], "subjects"=>subjects, "num_sections"=>sections_for_this_teacher(type)}
  end

  def students_per_section(type)
    @scenario['STUDENTS_PER_SECTION'][type]
  end

  # return the maximum number of sections available for a teacher in the given type of school
  # -> eventually pass this into random number generator
  def sections_for_this_teacher(type)
    DataUtility.select_random_from_options(@prng, (1..@scenario['MAX_SECTIONS_PER_TEACHER'][type]).to_a)
  end

end
