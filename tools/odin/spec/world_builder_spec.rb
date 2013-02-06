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
require_relative 'spec_helper'
require_relative '../lib/WorldDefinition/world_builder.rb'
require_relative '../lib/OutputGeneration/XmlDataWriter.rb'
require_relative '../lib/Shared/util.rb'
require_relative '../lib/EntityCreation/work_order_queue.rb'
require_relative '../lib/OutputGeneration/DataWriter.rb'
require_relative '../lib/EntityCreation/entity_factory.rb'
require_relative '../lib/Shared/EntityClasses/course.rb'

# specifications for the world builder
describe "WorldBuilder" do
  describe "--> with a set of 10 students (3 years)" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
        configYAML   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        scenarioYAML = load_scenario("10students_optional", configYAML)
        rand         = Random.new(configYAML['seed'])
        @queue        = WorkOrderQueue.new
        @pre_requisites = {:seas => {}, :leas => {}, :elementary => {}, :middle => {}, :high => {}}
        @builder      = WorldBuilder.new(rand, scenarioYAML, @queue, @pre_requisites)
        BaseEntity.set_scenario(scenarioYAML)
        @world = @builder.build
        @factory = EntityFactory.new(rand)
      end

      it "education organization interchange will contain a single state education agency" do
        @world["seas"].length.should eq(1)
        @queue.count(StateEducationAgency).should eq(1)
      end
      it "education organization interchange will contain a single local education agency" do
        @world["leas"].length.should eq(1)
        @queue.count(LocalEducationAgency).should eq(1)
      end
      it "education organization interchange contains 3 schools" do
        # check individual types of schools below.
        @queue.count(School).should eq(3)
      end
      it "education organization interchange will contain a single elementary school" do
        @world["elementary"].length.should eq(1)
      end
      it "education organization interchange will contain a single middle school" do
        @world["middle"].length.should eq(1)
      end
      it "education organization interchange will contain a single high school" do
        @world["high"].length.should eq(1)
      end
      it "education organization interchange will contain the correct number of courses" do
        count = 0
        @world["seas"].each do |sea|
          # organized by grade
          sea["courses"].each do |grade|
            # first element is the grade enum
            count = count + grade[1].length
          end
        end
        count.should eq(34)
        @queue.count(Course).should eq(34)
        course_entity = @factory.create(((@queue.get_work_orders(Course)))[0])[0]

        #verify course entity optional field is generated
        course_entity.nil?.should be false
        course_entity.course_level[:level].should eq("Honors")
        course_entity.course_level[:level_char].should eq("Other")
        course_entity.grades_offered.should eq("Kindergarten")
        course_entity.subject_area.should eq("ELA")
        course_entity.description.should eq("this is a course for Kindergarten")
        course_entity.gpa_appl.should eq("Applicable")
        course_entity.defined_by.should eq("LEA")
        course_entity.career_path.should eq("Education and Training")
        course_entity.learning_objectives[0].objective.should eq("Generic Learning Objective 1")
        course_entity.learning_objectives[0].academic_subject.should eq("ELA")
        course_entity.learning_objectives[0].objective_grade_level.should eq("Kindergarten")
        course_entity.competency_levels[0].should eq("Incompetent")

      end
      it "education organization interchange will contain the correct number of programs" do
        @queue.count(Program).should eq(62)
      end
      it "education organization calendar interchange will contain the correct number of sessions" do
        # sessions are defined at the LEA
        count = 0
        @world["leas"].each do |lea|
          count = count + lea["sessions"].length
        end
        count.should eq(3)
        @queue.count(Session).should eq(3)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        # Grading periods are not part of the world, they are created as work orders directly.
        @queue.count(GradingPeriod).should eq(3)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        # Calendar dates are not part of the world, they are created as work orders directly.
        @queue.count(CalendarDate).should eq(580)
      end
      it "master schedule interchange will contain the correct number of course offerings" do
        # Course offerings are not part of the world, they are created as work orders directly.
        @queue.count(CourseOffering).should eq(102)
      end
      it "staff association interchange will contain the correct number of staff members" do
        @queue.count(Staff).should eq(34)
      end
      it "staff association interchange will contain the correct number of staff education organization assignment associations" do
        @queue.count(StaffEducationOrgAssignmentAssociation).should eq(120)
      end

      it "grade wide assessment work orders will be created for each grade and year" do
        @queue.count(GradeWideAssessmentWorkOrder).should eq(39)
      end

      it "will create the configured number of cohorts for each school" do
        @queue.count(Cohort).should eq(9)

        # check that optional fields are generated
        @queue.get_work_orders(Cohort).each{|cohort|
          cohort_entity = @factory.create(cohort)
          cohort_entity.subject.should_not be_nil
          cohort_entity.programs.should have_at_least(1).items
        }
      end

      it "will associate a staff member for each cohort for each year" do
        @queue.count(StaffCohortAssociation).should eq(27)
      end

      it "will contain the correct number of Learning Objectives" do
        @queue.count(LearningObjective).should eq(750)
      end
    end
  end

  describe "--> with a set of 10,001 students (1 year)" do
    describe "--> builds correct infrastructure such that" do
      # generate the data once
      before(:all) do
        configYAML   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
        scenarioYAML = load_scenario("10001students", configYAML)
        rand         = Random.new(configYAML['seed'])
        @queue        = WorkOrderQueue.new
        pre_requisites = {:seas => {}, :leas => {}, :elementary => {}, :middle => {}, :high => {}}
        @builder      = WorldBuilder.new(rand, scenarioYAML, @queue, pre_requisites)
        @world = @builder.build
      end

      it "education organization interchange will contain a single state education agency" do
        @world["seas"].length.should eq(1)
        @queue.count(StateEducationAgency).should eq(1)
      end
      it "education organization interchange will contain multiple local education agencies" do
        @world["leas"].length.should be_> 2
        @queue.count(LocalEducationAgency).should be_> 2
      end
      it "education organization interchange contains many schools" do
        # check individual types of schools below.
        @queue.count(School).should be_ > 6
      end
      it "education organization interchange will contain many elementary schools" do
        @world["elementary"].length.should be_> 2
      end
      it "education organization interchange will contain many middle schools" do
        @world["middle"].length.should be_> 2
      end
      it "education organization interchange will contain many high schools" do
        @world["high"].length.should be_> 2
      end
      it "education organization interchange will contain the correct number of courses" do
        count = 0
        @world["seas"].each do |sea|
          # organized by grade
          sea["courses"].each do |grade|
            # first element is the grade enum
            count = count + grade[1].length
          end
        end
        count.should eq(34)
        @queue.count(Course).should eq(34)
      end
      it "education organization interchange will contain the correct number of programs" do
        @queue.count(Program).should eq(303)
      end
      it "education organization calendar interchange will contain the correct number of sessions" do
        # sessions are defined at the LEA
        count = 0
        @world["leas"].each do |lea|
          count = count + lea["sessions"].length
        end
        count.should eq(3)
        @queue.count(Session).should eq(3)
      end
      it "education organization calendar interchange will contain the correct number of grading periods" do
        # Grading periods are not part of the world, they are created as work orders directly.
        @queue.count(GradingPeriod).should eq(3)
      end
      it "education organization calendar interchange will contain the correct number of calendar dates" do
        # Calendar dates are not part of the world, they are created as work orders directly.
        @queue.count(CalendarDate).should eq(582)
      end
      it "master schedule interchange will contain the correct number of course offerings" do
        # Course offerings are not part of the world, they are created as work orders directly.
        @queue.count(CourseOffering).should eq(172)
      end
      it "staff association interchange will contain the correct number of staff members" do
        @queue.count(Staff).should eq(164)
      end
      it "staff association interchange will contain the correct number of staff education organization assignment associations" do
        @queue.count(StaffEducationOrgAssignmentAssociation).should eq(@queue.count(Staff) + @queue.count(Teacher))
      end
      it "grade wide assessment work orders will be created for each grade and year" do
        @queue.count(GradeWideAssessmentWorkOrder).should eq(13)
      end
      it "will generate each of the competency level descriptors" do
        @queue.count(CompetencyLevelDescriptor).should eq 4
      end
    end
  end

  describe "#choose_feeders" do
    context "with 8 elementary schools, 4 middle schools, and 2 high schools" do
      let(:elementary) {(1..8).map{|i| {'id' => i}}}
      let(:middle) {(9..12).map{|i| {'id' => i}}}
      let(:high) {(13..14).map{|i| {'id' => i}}}
      before(:all) do
        WorldBuilder.choose_feeders(elementary, middle, high)
      end
      it "will give middle schools a single feeder high school" do
        middle.each{|i|
          i['feeds_to'].should  have(1).items
          i['feeds_to'][0].should satisfy {|i| 13 <= i and i <= 14}
        }
      end
      it "will give elementary schools a feeder middle school and high school" do
        elementary.each{|i|
          i['feeds_to'].should  have(1).items
          i['feeds_to'][0].should satisfy {|i| 9 <= i and i <= 12}
        }
      end
    end
  end

  describe "#get_students_per_grade" do
    let(:grades) {[:KINDERGARTEN, :FIRST_GRADE, :SECOND_GRADE, :THIRDGRADE]}
    it "will evenly distribute students into grades" do
      students_per_grade = WorldBuilder.get_students_per_grade(grades, 20)
      grades.each{|g| students_per_grade[g].should eq 5}
    end
    it "will always distribute all students" do
      (0..40).each{|i|
        students_per_grade = WorldBuilder.get_students_per_grade(grades, i)
        students_per_grade.values.inject(:+).should eq i
      }
    end
  end
end
