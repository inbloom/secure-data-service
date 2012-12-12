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

require_relative '../Shared/data_utility.rb'
require_relative '../Shared/EntityClasses/enum/ClassroomPositionType.rb'
require_relative '../Shared/EntityClasses/section.rb'
require_relative '../Shared/EntityClasses/staff_ed_org_assignment_association.rb'
require_relative '../Shared/EntityClasses/teacher.rb'
require_relative '../Shared/EntityClasses/teacher_school_association.rb'
require_relative '../Shared/EntityClasses/teacher_section_association.rb'

# factory for creating section work orders using the specified scenario (and corresponding world)
class SectionWorkOrderFactory

  def initialize(world, scenario, prng)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @world = world
    @scenario = scenario
    @prng = prng
    @teacher_unique_state_id = 0
    @start_index = 1
  end

  # uses the instantiated @world to generate sections (and corresponding teachers) for the specified education organization
  def generate_sections_with_teachers(ed_org, ed_org_type, yielder = [])
    school_id    = DataUtility.get_school_id(ed_org['id'], ed_org_type.to_sym)
    ed_org_index = @world[ed_org_type].index(ed_org) if @world[ed_org_type].nil? == false and @world[ed_org_type].size > 0
    
    @world[ed_org_type][ed_org_index]['sections'] = {}
    unless ed_org['students'].nil?
      teachers = []
      ed_org['students'].each{|year, student_map|
        @world[ed_org_type][ed_org_index]['sections'][year] = {}
        unless ed_org['offerings'].nil?
          student_map.each{|grade, num|
            @world[ed_org_type][ed_org_index]['sections'][year][grade] = {}

            teachers_for_this_grade = ed_org['teachers'].select { |teacher| teacher['grades'].include? grade }
            teachers_for_this_grade.each { |teacher| teacher['num_sections'] = sections_for_this_teacher(ed_org_type) } if !teachers_for_this_grade.nil? and teachers_for_this_grade.size > 0
            
            # take sections_from_edorg output and add teachers to sections
            # -> yielder is passed in so that newly created teachers will have work orders for their creation
            # -> num_sections for each teacher is reset for each new year
            sections_with_teachers = add_teachers_to_sections(sections_from_edorg(ed_org, ed_org_type, year, grade), teachers_for_this_grade, ed_org_type)

            sections_with_teachers.each{ |offering, sections|
              @world[ed_org_type][ed_org_index]['sections'][year][grade][offering['id']] = [] 
              sections.each{ |section|
                # remember the unique section ids that map to each course offering --> stored in the @world
                @world[ed_org_type][ed_org_index]['sections'][year][grade][offering['id']] << section[:id]
                year_of = Date.today.year - DataUtility.select_random_from_options(@prng, (25..65).to_a)

                # add staff -> education organization assignment association for current session (where teacher is associated to school)
                # -> this will eventually allow us to migrate teachers (even pre-requisite teachers) across education organizations as part of 
                #    the current simulation
                if !teachers.include?(section[:teacher]['id'])
                  yielder << {:type=>Teacher, :id=>section[:teacher]['id'], :year=>year_of, :name=>nil}
                  yielder << {:type=>TeacherSchoolAssociation, :id=>section[:teacher]['id'], :school=>school_id, :assignment=>:REGULAR_EDUCATION, :grades=>[offering['grade']], :subjects=>section[:teacher]['subjects']}
                  
                  create_staff_ed_org_association_for_teacher(find_matching_session_for_school(school_id, offering), section[:teacher]['id'], school_id, ed_org_type).each do |order|
                    yielder << order
                  end

                  teachers << section[:teacher]['id']
                end
                
                yielder << {:type=>Section, :id=>section[:id], :edOrg=>school_id, :offering=>offering}
                yielder << {:type=>TeacherSectionAssociation, :teacher=>section[:teacher]['id'], :section=>section[:id], :school=>school_id, :position=>:TEACHER_OF_RECORD}
              }
            }
          }
        end
      }
    end
  end

  def sections(id, type, year, grade)
    #puts "ed org id: #{id}"
    #puts "world: #{@world}"
    #puts ""
    if (@world.nil? == false && @world[type].nil? == false)
      ed_org = @world[type].find{|s| s['id'] == id}
      return ed_org['sections'][year][grade] if !ed_org.nil? and !ed_org['sections'].nil? and !ed_org['sections'][year].nil?
      return nil
    end
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
      # skip this offering if the second range is nil
      next if section_range.nil?

      sections_for_this_offering  = []

      section_range.each do |section|
        teacher = teachers.shift
        if teacher.nil?
          # need to create teacher
          # use course offering to create teacher school association
          @teacher_unique_state_id += 1
          ed_org_id                = offering['ed_org_id']
          teacher                  = create_teacher(type, DataUtility.get_teacher_unique_state_id(@teacher_unique_state_id), offering['grade'])
          
          @log.info "creating teacher: #{teacher['id']} with num_sections: #{teacher['num_sections']} at school: #{ed_org_id}"
          added_teacher = push_new_teacher_into_world(teacher, ed_org_id, type)
          @log.warn "Failed to push teacher with id: #{teacher['id']} into world" if !added_teacher
        end

        # decrement the number of sections available to the current teacher and push them back onto the @world
        # -> this operation will push the teacher onto the end of the 'teachers' queue for the given education organization,
        #    so sections will be spread evenly across teachers
        sections_for_this_offering << {:id=>section, :teacher=>teacher}
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

  # iterates through sessions, using begin and end date, to assemble teacher -> school associations
  # -> not used, yet
  # -> will be used soon (as soon as ed-fi changes to add start and end dates to teacher school associations)
  def create_staff_ed_org_association_for_teacher(session, teacher_id, ed_org_id, type)
    associations = []
    if !session.nil?
      if ed_org_id.kind_of? Integer
        state_org_id = DataUtility.get_state_education_agency_id(ed_org_id) if type == "seas"
        state_org_id = DataUtility.get_local_education_agency_id(ed_org_id) if type == "leas"
        state_org_id = DataUtility.get_elementary_school_id(ed_org_id)      if type == "elementary"
        state_org_id = DataUtility.get_middle_school_id(ed_org_id)          if type == "middle"
        state_org_id = DataUtility.get_high_school_id(ed_org_id)            if type == "high"
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
