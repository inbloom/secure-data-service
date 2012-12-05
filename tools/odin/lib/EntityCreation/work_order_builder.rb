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

require_relative "../Shared/EntityClasses/enum/GradeLevelType.rb"

# class for building work orders specific to students
# -> uses the created 'world' to assemble work orders
# -> creates sections as they are specified in the students' work orders
# -> [not implemented] populates teachers for sections
# -> [not implemented] updates world so that teachers will stay at given education organization
class WorkOrderBuilder

  # initializes the work order builder (currently only instantiates instance logger)
  def initialize(random, yaml)
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO

    @random = random
    @yaml   = yaml
  end

  # generate work orders for students using the world
  def generate_student_work_orders(world)
    begin_year   = @yaml["beginYear"]
    num_years    = @yaml["numYears"]

    # iterate through schools --> high schools then middle schools then elementary schools
    # iterate through grades, starting at 12th grade and ending at Kindergarten
    #  if school contains grade,
    #   for current grade,
    #    check that students exist for school in current grade during this year
    #    if students don't exist (count == 0) --> do nothing
    #    if students exist       (count >  0)
    #     progress them through the world
    #     -> create students by wave (for each student, create parents and associations)
    #     -> iterate through years (begin year to begin year + num years - 1)
    #     --> create sections to hold students
    #     --> if no teachers are associated with the course offering, create them (otherwise use existing)
    #     ---> if creating teachers, update future course offerings with these teachers as well (teachers continue to teach at given school)
    #     --> assign teachers to teach sections (elementary schools: 1:1, middle/high schools: 1:2-4)
    #     --> assign students to sections
    #     ---> create all section-specific information for the given year

    world["high"].each do |school|
      (begin_year..(begin_year + num_years - 1)).step(1).each do |year|
        students_by_grade = school["students"][year]
        GradeLevelType.get_ordered_grades.reverse.each do |grade|
          if GradeLevelType.is_high_school_grade(grade)
            students_in_current_grade = students_by_grade[grade]
            if students_in_current_grade > 0
              curr_grade = grade
              @log.info "SCHOOL YEAR: #{year}-#{year+1}"
              @log.info "creating #{students_in_current_grade} students at school: #{school["id"]} in grade: #{grade}"
              school["students"][year][curr_grade] = 0
              ((year + 1)..(begin_year + num_years - 1)).step(1).each do |iterating_year|
                next_grade = GradeLevelType.increment(curr_grade)
                if next_grade != nil and next_grade != curr_grade
                  @log.info "year: #{iterating_year}-#{iterating_year+1} --> #{students_in_current_grade} students are now in grade: #{next_grade} at school: #{school["id"]}"
                  school["students"][iterating_year][next_grade] = 0
                  curr_grade = next_grade
                end
              end
            end
          end
        end
      end
    end

    # randomly selects the number of students per section, given the type of school
    # -> gets the minimum and maximum number of students per section for the school type
    # -> randomly selects a number on that interval
    def get_students_per_section_for_school_type(yaml, type)

    end

    world["elementary"].each do |school|
      (begin_year..(begin_year+num_years-1)).step(1).each do |year|
        offerings           = school["offerings"][year]
        students_by_grade   = school["students"][year]
        grades              = []
        offerings_this_year = []
        students_by_grade.each { |grade,students_in_grade| grades << grade if students_in_grade > 0               }
        offerings.each         { |offering| offerings_this_year << offering if grades.include?(offering["grade"]) }
        @log.info "school year: #{year}-#{year+1} --> elementary school #{school["id"]} requires offerings for grades: #{grades} (students by grade: #{students_by_grade})"
        @log.info "school year: #{year}-#{year+1} --> elementary school #{school["id"]} course offerings: #{offerings_this_year}"
      end
    end
  end

  # generate student work orders as 'vertical slices'
  def generate_student_work_orders_for_year(world, year)

  end
end