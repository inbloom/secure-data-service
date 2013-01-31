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

require_relative '../Shared/data_utility'
require_relative '../Shared/EntityClasses/studentAssessment'
require_relative '../Shared/EntityClasses/enum/GradeType'
require_relative 'assessment_work_order'
require_relative 'attendance_factory'
require_relative 'gradebook_entry_factory'
require_relative 'graduation_plan_factory'
require_relative 'world_builder'

# student work order factory creates student work orders
class StudentWorkOrderFactory
  def initialize(world, scenario, section_factory)
    $stdout.sync = true
    @log         = Logger.new($stdout)

    @world = world
    sea = world['seas'][0] unless world['seas'].nil?
    @scenario = Scenario.new scenario
    @next_id = 0
    @assessment_factory = AssessmentFactory.new(@scenario)
    @attendance_factory = AttendanceFactory.new(@scenario)
    @gradebook_entry_factory = GradebookEntryFactory.new(@scenario)
    @graduation_plans = GraduationPlanFactory.new(sea['id'], @scenario).build unless sea.nil?
    @section_factory = section_factory
  end

  def generate_work_orders(edOrg, yielder)
    students = edOrg['students']
    unless students.nil?
      years = students.keys.sort
      initial_year = years.first
      initial_grade_breakdown = students[initial_year]
      initial_grade_breakdown.each{|grade, num_students|
        (1..num_students).each{ |_|
          student_id = @next_id += 1
          plan       = generate_plan(edOrg, grade)
          yielder.yield StudentWorkOrder.new(student_id, scenario: @scenario, 
                                             initial_grade: grade, 
                                             initial_year: initial_year,
                                             plan: plan,
                                             assessment_factory: @assessment_factory,
                                             attendance_factory: @attendance_factory,
                                             gradebook_factory: @gradebook_entry_factory,
                                             graduation_plans: @graduation_plans,
                                             section_factory: @section_factory)
        }
      }
    end
  end

  # generate plan for student with specified id
  # -> plan starts at education organization (edOrg) in the specified grade
  # -> begin year is looked up using @scenario instance variable
  # -> the returned 'plan' is a map of { year --> education organization }, 
  #    and represents the plan for the student over the course of the simulation
  def generate_plan(edOrg, grade)
    begin_year = @scenario['BEGIN_YEAR']
    num_years  = @scenario['NUMBER_OF_YEARS']
    plan       = {}
    education_organization = edOrg
    current_grade          = grade
    (begin_year..(begin_year + num_years - 1)).step(1) do |year|
      curr_type     = GradeLevelType.school_type(current_grade)
      next_school   = education_organization['feeds_to'].first unless education_organization['feeds_to'].nil?
      plan[year]    = {:type => curr_type, :grade => current_grade, :school => education_organization, :programs => find_programs_above_school(education_organization)}
      current_grade = GradeLevelType.increment(current_grade)

      # grade will only be nil if trying to increment beyond 12th grade --> graduated!
      break if current_grade.nil?
      next_type = GradeLevelType.school_type(current_grade).to_s

      # high schools won't have a next school --> need to check first
      # students moving between school boundaries (graduating) will match 2nd conditional
      unless next_school.nil? or GradeLevelType.school_type(current_grade) == curr_type
        education_organization = @world[next_type].detect {|school| school['id'] == next_school} if @world[next_type].nil? == false
      end
      
      # education organization will only be nil if the next school could not be found
      # -> education organization cannot be nil at the start of this loop
      break if education_organization.nil?
    end
    plan
  end

  # finds the programs published by education organizations in the specified school's lineage
  # -> programs offered by local education agencies
  # -> programs offered by state education agencies
  def find_programs_above_school(school)
    programs = []
    parent_id = school["parent"]
    parent    = @world["leas"].detect {|lea| lea["id"] == parent_id} unless parent_id.nil?
    while parent != nil
      # get programs from local education agency
      # -> get parent id for current local education agency
      # -> find next local education agency (or nil if the next parent is a state education agency)
      programs  << parent["programs"].each {|program| program['ed_org_id'] = parent_id}
      parent_id = parent["parent"]
      parent    = @world["leas"].detect {|lea| lea["id"] == parent_id} unless parent_id.nil?
    end
    # parent_id should now be populated with id of the state education agency
    state_education_agency = @world["seas"].detect {|sea| sea["id"] == parent_id} unless parent_id.nil?
    programs << state_education_agency["programs"].each {|program| program['ed_org_id'] = parent_id} unless state_education_agency.nil?
    programs.flatten
  end

end

# student work order represents all data to be genreated for a given student
class StudentWorkOrder
  attr_accessor :id, :edOrg, :birth_day_after, :initial_grade, :initial_year

  def initialize(id, opts = {})
    $stdout.sync = true
    @log         = Logger.new($stdout)

    @id = id
    @rand = Random.new(@id)
    @initial_grade = (opts[:initial_grade] or :KINDERGARTEN)
    @initial_year = (opts[:initial_year] or 2011)
    @birth_day_after = Date.new(@initial_year - find_age(@initial_grade), 9, 1)
    @scenario = (opts[:scenario] or Scenario.new({}))
    @assessment_factory = opts[:assessment_factory]
    @attendance_factory = opts[:attendance_factory]
    @gradebook_factory = opts[:gradebook_factory]
    @graduation_plans = opts[:graduation_plans]
    @section_factory = opts[:section_factory]
    @plan = opts[:plan]
  end

  def build
    student = Student.new(@id, @birth_day_after)
    [student] + per_year_info + parents(student)
  end

  private

  def parents(student)
    parents_per_student = DataUtility.rand_float_to_int(@rand, @scenario['PARENTS_PER_STUDENT'] || 2)
    unless parents_per_student == 0
      pp = parents_per_student > 1 ? [:mom, :dad] : [:mom]
      pp.map{|type|
        [Parent.new(student, type), StudentParentAssociation.new(student, type)]}.flatten
    end
  end

  def per_year_info
    generated = []
    @plan.keys.sort.each do |year|
      curr_type = @plan[year][:type]
      grade     = @plan[year][:grade]
      school    = @plan[year][:school]
      programs  = @plan[year][:programs]
      session   = find_session_in_year(@plan[year][:school]['sessions'], year)
      school_id = DataUtility.get_school_id(school['id'], curr_type)

      unless session.nil?
        generated += generate_enrollment_and_gradebook_entries(school_id, curr_type, year, grade, session)
        generated += generate_grade_wide_assessment_info(grade, session)
        generated += generate_program_associations(session, curr_type, school, programs)
        generated += generate_cohorts(school_id, curr_type, session)
        generated += generate_attendances(session, curr_type, school_id)
      end
    end
    generated
  end

  def find_session_in_year(sessions, year)
    return sessions.detect { |session| session['year'] == year } unless sessions.nil?
    return nil
  end

  # generates attendance events for the student in the specified session
  def generate_attendances(session, type, school)
    return @attendance_factory.generate_attendance_events(@rand, @id, school, session, type) if @attendance_factory.nil? == false
    return []
  end

  # generates student -> program associations for the specified 'session' at the current school of specified 'type'
  # -> student will be involved in 0, 1, or 2 programs at their current school
  # -> student will be involved in 1, 2, or 3 programs at education organizations above their school (local or state education agency level)
  def generate_program_associations(session, type, school, programs)
    associations = []
    interval   = session['interval']
    begin_date = interval.get_begin_date unless interval.nil?
    end_date   = interval.get_end_date unless interval.nil?

    unless school['programs'].nil?
      num_school_programs = DataUtility.select_random_from_options(@rand, (0..2).to_a)
      school_programs = DataUtility.select_num_from_options(@rand, num_school_programs, school['programs'])
      school_programs.each do |school_program|
        program_id = DataUtility.get_program_id(school_program[:id])
        ed_org_id  = DataUtility.get_school_id(school['id'], type)
        associations << StudentProgramAssociation.new(@id, program_id, ed_org_id, begin_date, end_date)
      end
    end
    
    num_other_programs = DataUtility.select_random_from_options(@rand, (1..3).to_a)
    other_programs     = DataUtility.select_num_from_options(@rand, num_other_programs, programs)
    other_programs.each do |other_program|
      program_id = DataUtility.get_program_id(other_program[:id])
      ed_org_id = other_program['ed_org_id']
      associations << StudentProgramAssociation.new(@id, program_id, ed_org_id, begin_date, end_date)
    end
    associations
  end

  # creates student enrollment, gradebook entries, and report cards:
  # - StudentSchoolAssociation
  # - StudentSectionAssociation
  # - StudentGradebookEntry (as part of enrollment in section)
  # - ReportCard
  def generate_enrollment_and_gradebook_entries(school_id, type, year, grade, session)
    rval = []
    unless session.nil?
      begin_date = session['interval'].get_begin_date
      end_date   = session['interval'].get_end_date
      holidays   = session['interval'].get_holidays

      rval << StudentSchoolAssociation.new(@id, school_id, begin_date, grade, graduation_plan(type), end_date)

      unless @section_factory.nil?
        sections = @section_factory.sections(school_id, type.to_s, year, grade)
        unless sections.nil?
          final_grades = []
          student_competencies = []
          academic_subjects = AcademicSubjectType.get_academic_subjects(grade)
          code_values = (@scenario["COMPETENCY_LEVEL_DESCRIPTORS"] or []).collect{|competency_level_descriptor| competency_level_descriptor['code_value']}
          code_values = [1, 2, 3] if code_values.empty?
          sections.each{|course_offering, available_sections|
            sections_per_student = DataUtility.rand_float_to_int(@rand, @scenario['HACK_SECTIONS_PER_STUDENT'] || 1)
            for sps in 1..sections_per_student
              section    = available_sections[@id % available_sections.count]
              section_id = DataUtility.get_unique_section_id(section[:id])
              student_section_association = StudentSectionAssociation.new(@id, section_id, school_id, begin_date, grade)
              rval       << student_section_association
  
              unless @gradebook_factory.nil? or section[:gbe].nil?
                grades = {}
                section[:gbe].each do |type, gbe_num|
                  grades[type] = []
                  gbe_orders   = @gradebook_factory.generate_entries_of_type(session, section, type, gbe_num)
                  unless gbe_orders.size == gbe_num
                    @log.warn "section: #{section} calls for #{gbe_num} gbes (type:#{type}) --> actually created #{gbe_orders.size} gbe orders"
                  end
                  gbe_orders.each do |gbe_order|
                    sgbe         = get_student_gradebook_entry(gbe_order, school_id, section_id, session)
                    grades[type] << sgbe.numeric_grade_earned
                    rval         << sgbe
                  end
                end
                # compute final grade using breakdown --> need to look up breakdown from @scenario
                final_grade = get_student_final_grade(grade, grades, school_id, section_id, session)
                final_grades << final_grade
                rval << final_grade
              end
              rval += addDisciplineEntities(section[:id], school_id, session)
  
              academic_subject = AcademicSubjectType.to_string(academic_subjects[section[:id] % academic_subjects.size])
              num_objectives = (@scenario["NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE"] or 2)
              LearningObjective.build_learning_objectives(num_objectives, academic_subject, GradeLevelType.to_string(grade)).each {|learning_objective|
                student_competency = StudentCompetency.new(code_values[(section[:id] + @id) % code_values.size], learning_objective, student_section_association)
                student_competencies << student_competency
                rval << student_competency
              }
              rval << course_transcript(school_id, session, course_offering, grade, final_grade)
            end
          }
          grading_period = GradingPeriod.new(:END_OF_YEAR, session['year'], session['interval'], session['edOrgId'], [])
          report_card = ReportCard.new(@id, final_grades, grading_period, student_competencies)
          rval << report_card
          rval << academic_record(report_card, session)
        end
      end
    end
    rval
  end

  def course_transcript(school_id, session, course_id, grade, final_grade)
    CourseTranscript.new(@id, school_id, course_id, session, grade, final_grade)
  end

  def academic_record(report_card, session)
    StudentAcademicRecord.new(@id, session, report_card)
  end

  def addDisciplineEntities(section_id, school_id, session)
    num_incidents = @scenario['INCIDENTS_PER_SECTION'] || 0
    likelyhood = @scenario['LIKELYHOOD_STUDENT_WAS_INVOLVED']
    incidents = []
    num_incidents.times {|i|
      if @rand.rand < likelyhood.to_f
        incidents << StudentDisciplineIncidentAssociation.new(@id, i, section_id, school_id)
        incidents << DisciplineAction.new(@id, school_id, DisciplineIncident.new(i, section_id, school_id, nil, session['interval'], "Classroom"))
      end
    }
    incidents
  end

  # creates a student gradebook entry, based on the input gradebook entry work order
  # -> needs school, section, and session to fill out appropriate information
  def get_student_gradebook_entry(gbe_order, school_id, section_id, session)
    assigned        = gbe_order[:date_assigned]
    fulfilled       = assigned + 1
    association     = get_student_section_association(school_id, section_id, session['interval'].get_begin_date)
    gradebook_entry = {:type => gbe_order[:gbe_type], :date_assigned => assigned, :ed_org_id => school_id, :unique_section_code => section_id}
    letter, number  = get_grade_for_gradebook_entry
    return StudentGradebookEntry.new(fulfilled, letter, number, association, gradebook_entry)
  end

  def get_student_final_grade(grade, grades, school_id, section_id, session)
    numeric         = 0
    grade_breakdown = @scenario['GRADEBOOK_ENTRIES_BY_GRADE'][GradeLevelType.to_string(grade)]
    unless grade_breakdown.nil?
      grades.each do |type, scores|
        weight  = grade_breakdown[type]['weight'] / 100.0 unless grade_breakdown[type]['weight'].nil?
        weight  = 1.0 / grade_breakdown.size if weight.nil?
        average = scores.inject(:+) / scores.size
        numeric += (weight * average).floor
      end
    end
    return Grade.new(get_letter_grade_from_number(numeric), numeric, :FINAL, get_student_section_association(school_id, section_id, session['interval'].get_begin_date))
  end

  # translates the attributes for the student section association into a map (with values that mustache templates will recognize/expect)
  def get_student_section_association(school_id, section_id, begin_date)
    {:student => @id, :ed_org_id => school_id, :unique_section_code => section_id, :begin_date => begin_date}
  end

  # generates a grade (letter and numeric) for a student gradebook entry
  def get_grade_for_gradebook_entry
    number = DataUtility.select_random_from_options(@rand, (55..100).to_a)
    letter = get_letter_grade_from_number(number)
    return letter, number
  end

  # translates the numeric grade into a letter grade
  def get_letter_grade_from_number(number)
    return "A+" if number >= 97
    return "A"  if number >= 93
    return "A-" if number >= 90
    return "B+" if number >= 87
    return "B"  if number >= 83
    return "B-" if number >= 80
    return "C+" if number >= 77
    return "C"  if number >= 73
    return "C-" if number >= 70
    return "D+" if number >= 67
    return "D"  if number >= 63
    return "D-" if number >= 60
    return "F"
  end

  def graduation_plan(school_type)
    if school_type == :high
      @graduation_plans[@id % @graduation_plans.size] unless @graduation_plans.nil?
    end
  end

  def generate_grade_wide_assessment_info(grade, session)
    student_assessments = grade_wide_student_assessments(grade, session)
    student_assessment_items = generate_student_assessment_items(student_assessments)
    student_assessments + student_assessment_items
  end

  def grade_wide_student_assessments(grade, session)
    unless @assessment_factory.nil?
      times_taken = @scenario['ASSESSMENTS_TAKEN']['GRADE_WIDE_ASSESSMENTS']

      @assessment_factory.grade_wide_assessments(grade, session['year']).map{ |assessment|
        #TODO this is going to be a busy first couple of days of school, might want to spread them out
        if session.nil? == false && session['interval'].nil? == false
          start_date = session['interval'].get_begin_date + 1
          end_date = start_date + times_taken -1
          (start_date..end_date).map{ |date|
            StudentAssessment.new(@id, assessment, date, @rand)
          }
        end
      }.flatten
    end or []
  end

  def generate_student_assessment_items(student_assessments)
    student_assessments.map{|sa|
      sa.assessment.assessment_items.map{|item|
        StudentAssessmentItem.new(sa.studentId.odd?, sa, item)
      }
    }.flatten
  end

  def generate_cohorts(school, school_type, session)
    cohorts = WorldBuilder.cohorts(DataUtility.get_school_id(school, school_type), @scenario)
    cohorts.map{|cohort|
      prob = @scenario['PROBABILITY_STUDENT_IN_COHORT'].to_f
      if(@rand.rand < prob)
        StudentCohortAssociation.new(@id, cohort, session['interval'].get_begin_date, (@scenario['DAYS_IN_COHORT'] or -1))
      end
    }.select{|c| not c.nil?}
  end

  def find_age(grade)
    5 + GradeLevelType.get_ordered_grades.index(grade)
  end
end
