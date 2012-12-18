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

require 'timeout'

require_relative 'spec_helper'
require_relative '../lib/Shared/demographics.rb'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType.rb'
require_relative '../lib/Shared/EntityClasses/enum/ProgramType.rb'
require_relative '../lib/Shared/EntityClasses/enum/ProgramSponsorType.rb'
require_relative '../lib/Shared/EntityClasses/studentSchoolAssociation.rb'
require_relative '../lib/Shared/EntityClasses/studentSectionAssociation.rb'
require_relative '../lib/Shared/EntityClasses/student_cohort.rb'
require_relative '../lib/Shared/date_interval.rb'
require_relative '../lib/OutputGeneration/XmlDataWriter'
require_relative '../lib/OutputGeneration/entity_queue'
require_relative '../lib/EntityCreation/work_order_queue'
require_relative '../lib/WorldDefinition/student_work_order'
require_relative '../lib/EntityCreation/entity_factory'
require_relative '../lib/EntityCreation/work_order_processor'

describe "WorkOrderProcessor" do
  let(:config) {YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))}
  let(:prng) {Random.new(config['seed'])}
  let(:scenario) {Scenario.new({'ASSESSMENTS_TAKEN' => {'grade_wide' => 5}, 'ASSESSMENTS_PER_GRADE'=>3, 
                   'ASSESSMENT_ITEMS_PER_ASSESSMENT' => {'grade_wide' => 3},
                   'INCLUDE_PARENTS' => true,
                   'COHORTS_PER_SCHOOL' => 4, 'PROBABILITY_STUDENT_IN_COHORT' => 1, 'DAYS_IN_COHORT' => 30})}
  describe "#build" do

    let(:entity_queue) {EntityQueue.new}
    let(:work_order_queue) {WorkOrderQueue.new}
    before(:all) do

      class Factory
        # student creation
        attr_accessor :students, :school_associations, :assessment_associations, :section_associations, :assessment_items,
          :parents, :parent_associations, :cohort_associations, :program_associations
        def create(work_order)
          to_build = work_order.build
          @students = to_build.select{|a| a.kind_of? Student}
          @school_associations = to_build.select{|a| a.kind_of? StudentSchoolAssociation}
          @assessment_associations = to_build.select{|a| a.kind_of? StudentAssessment}
          @section_associations = to_build.select{|a| a.kind_of? StudentSectionAssociation}
          @assessment_items = to_build.select{|a| a.kind_of? StudentAssessmentItem}
          @parents = to_build.select{|a| a.kind_of? Parent}
          @parent_associations = to_build.select{|a| a.kind_of? StudentParentAssociation}
          @program_associations = to_build.select{|a| a.kind_of? StudentProgramAssociation}
          @cohort_associations = to_build.select{|a| a.kind_of? StudentCohortAssociation}
        end
      end

    end
    let(:factory) {Factory.new}
    before { work_order_queue.factory(factory, entity_queue) }

    context 'With a simple work order' do
      let(:section_factory) {double('section factory', :sections => {{'id' => 1} => [42, 43, 44], {'id' => 2} => [45, 46, 47]})}
      let(:ed_org) {{'id' => 64, 'parent' => 2, 'sessions' => [{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180)},
                                                               {'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180)}],
                                 'programs' => [{:id => 1, :type => :IDEA, :sponsor => :SCHOOL}]}}
      let(:programs) {[{:id => 2, :type => :IDEA, :sponsor => :LOCAL_EDUCATION_AGENCY, :ed_org_id => 2}]}
      let(:work_order) {StudentWorkOrder.new(42, :initial_grade => :KINDERGARTEN, :initial_year => 2001,
                                             :edOrg => ed_org, :section_factory => section_factory, :programs => programs,
                                             :assessment_factory => AssessmentFactory.new(scenario), :scenario => scenario)}
      let(:assessment_factory) {AssessmentFactory.new(scenario)}
      before {
        work_order_queue.push_work_order(work_order)
      }

      it "will generate the right number of entities for the student and subsequent enrollment" do
        factory.students.should have(1).items
        factory.school_associations.select{|ssa| ssa.startYear == 2001 and ssa.startGrade == "Kindergarten"}.should have(1).items
        factory.school_associations.select{|ssa| ssa.startYear == 2002 and ssa.startGrade == "First grade"}.should have(1).items
        factory.section_associations.should have(4).items
        factory.assessment_associations.should have(30).items
        factory.cohort_associations.should have(8).items
      end

      it "will generate the right number of parents and student parent associations" do
        factory.parents.should have(2).items
        factory.parent_associations.should have(2).items
      end

      it "will generate StudentSchoolAssociations with the correct information" do
        factory.school_associations.each{|a|
          a.studentId.should eq 42
          a.schoolStateOrgId.should eq "elem-0000000064"
        }
        factory.school_associations[0].startYear.should eq(2001)
        factory.school_associations[0].startGrade.should eq("Kindergarten")
        factory.school_associations[1].startYear.should eq(2002)
        factory.school_associations[1].startGrade.should eq("First grade")
      end

      it "will generate StudentSectionAssociations with the correct information" do
        factory.section_associations.each{|a|
          a.studentId.should eq 42
          a.edOrgId.should eq "elem-0000000064"
        }
        section_associations = factory.section_associations.group_by{|a| a.year}
        section_associations[2001].count.should eq 2
        section_associations[2001][0].sectionId.should match(/sctn\-0000000042/)
        section_associations[2001][1].sectionId.should match(/sctn\-0000000045/)
        section_associations[2002].count.should eq 2
        section_associations[2002][0].sectionId.should match(/sctn\-0000000042/)
        section_associations[2002][1].sectionId.should match(/sctn\-0000000045/)
      end

      it "will generate StudentAssessments with the correct related assessment" do

        student_assessments = factory.assessment_associations.group_by{|sa| sa.assessment.assessmentTitle }

        assessment_factory.grade_wide_assessments(:KINDERGARTEN, 2001).each{|a|
          student_assessments[a.assessmentTitle].should have(5).item
        }
      end

      it "will generate the correct number of student assessment items" do
        assessment_items = factory.assessment_items.group_by{|item| item.student_assessment}
        factory.assessment_associations.each{|a|
          assessment_items[a].should have(3).items
        }
      end

      it "will generate correct cohort associations for the student" do
        factory.cohort_associations.each{|c|
          c.student.should eq 42
          c.cohort.ed_org_id.should eq "elem-0000000064"
          c.begin_date.should eq c.end_date - 30
        }
        factory.cohort_associations.group_by{|c| c.cohort.identifier}.each{|cohort, associations|
          associations.should have(2).items
        }

      end
    end

    context 'With a work order that spans multiple schools' do
      let(:work_order) {StudentWorkOrder.new(42, :initial_grade => :FIFTH_GRADE, :initial_year => 2001,
                                             :edOrg => {'id' => 64, 'sessions' => [{'year' => 2001},
                                                                                   {'year' => 2002},
                                                                                   {'year' => 2003},
                                                                                   {'year' => 2004},
                                                                                   {'year' => 2005}],
                                                        'feeds_to' => [65, 66]},
                                                        :graduation_plans => 
                                                          [GraduationPlan.new("Standard", {'Math'=> 12}, "state-id")])}
      before {
        work_order_queue.push_work_order(work_order)
      }
      it "will get enrollments for each school" do
        factory.students.should have(1).items
        factory.school_associations[0].startYear.should eq(2001)
        factory.school_associations[0].schoolStateOrgId.should eq('elem-0000000064')
        factory.school_associations[1].startYear.should eq(2002)
        factory.school_associations[1].schoolStateOrgId.should eq('midl-0000000065')
        factory.school_associations[2].startYear.should eq(2003)
        factory.school_associations[2].schoolStateOrgId.should eq('midl-0000000065')
        factory.school_associations[3].startYear.should eq(2004)
        factory.school_associations[3].schoolStateOrgId.should eq('midl-0000000065')
        factory.school_associations[4].startYear.should eq(2005)
        factory.school_associations[4].schoolStateOrgId.should eq('high-0000000066')
      end

      it "will put graduation plans on the school association iff the school is a high school" do
        factory.school_associations[0].gradPlan.should be_nil
        factory.school_associations[1].gradPlan.should be_nil
        factory.school_associations[2].gradPlan.should be_nil
        factory.school_associations[3].gradPlan.should be_nil
        factory.school_associations[4].gradPlan.should_not be_nil
      end
    end

    context "with a work order than includes students graduating" do
      let(:eleventh_grader) {StudentWorkOrder.new(42, :initial_grade => :ELEVENTH_GRADE, :initial_year => 2001,
                                                  :edOrg => {'id' => 64, 'sessions' => [{'year' => 2001},
                                                                                        {'year' => 2002},
                                                                                        {'year' => 2003},
                                                                                        {'year' => 2004}]})}
      let(:twelfth_grader) {StudentWorkOrder.new(42, :initial_grade => :TWELFTH_GRADE, :initial_year => 2001,
                                                 :edOrg => {'id' => 64, 'sessions' => [{'year' => 2001},
                                                                                       {'year' => 2002},
                                                                                       {'year' => 2003},
                                                                                       {'year' => 2004}]})}
      it "will only generate student school associations until the student has graduated" do

        work_order_queue.push_work_order(eleventh_grader)
        factory.students.should have(1).items
        factory.school_associations.should have(2).items
        factory.school_associations[0].startYear.should eq(2001)
        factory.school_associations[1].startYear.should eq(2002)

        factory.school_associations = []
        factory.students = []

        work_order_queue.push_work_order(twelfth_grader)
        factory.students.should have(1).items
        factory.school_associations.should have(1).items
        factory.students.should have(1).items
        factory.school_associations[0].startYear.should eq(2001)
      end
    end
  end
end


describe "generate_work_orders" do
  let(:config) {YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))}
  let(:prng) {Random.new(config['seed'])}
  let(:scenario) {Scenario.new({'ASSESSMENTS_TAKEN' => {'grade_wide' => 5}, 'ASSESSMENTS_PER_GRADE'=>3, 
                   'ASSESSMENT_ITEMS_PER_ASSESSMENT' => {'grade_wide' => 3}})}

  context "with a world with 20 students in 4 schools" do

    let(:world) {{'seas' => [{'id' => 'sea1'}], 'leas' => [{'id' => 'lea1'}],
                  'elementary' => [{'id' => 0, 'students' => {2011 => {:KINDERGARTEN => 5}, 2012 => {:FIRST_GRADE => 5}}, 'sessions' => [{}]},
                                   {'id' => 1, 'students' => {2011 => {:KINDERGARTEN => 5}, 2012 => {:FIRST_GRADE => 5}}, 'sessions' => [{}]}],
                  'middle' => [{'id' => 2, 'students' => {2011 => {:SEVENTH_GRADE => 5}, 2012 => {:EIGTH_GRADE => 5}}, 'sessions' => [{}]}],
                  'high' => [{'id' => 3, 'students' => {2011 => {:NINTH_GRADE => 5}, 2012 => {:TENTH_GRADE => 5}}, 'sessions' => [{}]}]}}

    let(:work_orders) { WorkOrderProcessor.generate_work_orders(world, scenario, prng)}

    it "will create a work order for each student" do
      work_orders.count.should eq(20)
    end

    it "will put the students in the right schools" do
      work_orders.each_with_index { |work_order, index|
        work_order.edOrg["id"].should eq(index/5)
      }
    end

    it "will give students the correct entry grade" do
      work_orders.select{|wo|
        wo.initial_grade == :KINDERGARTEN}.count.should eq(10)
      work_orders.select{|wo|
        wo.initial_grade == :SEVENTH_GRADE}.count.should eq(5)
      work_orders.select{|wo|
        wo.initial_grade == :NINTH_GRADE}.count.should eq(5)
    end

    it "will give students the correct entry year" do
      work_orders.select{|wo| wo.initial_year == 2011}.count.should eq(20)
    end

    it "will generate unique student ids" do
      work_orders.map{|wo| wo.id}.to_set.count.should eq(20)
    end
  end

  context "with an infinitely large school" do
    let(:world)  { {"high" => [{'id' => "Zeno High", 'students' => {2001 => {:KINDERGARTEN => 1.0/0}}, 'sessions' => [{}]}]} }

    it "will lazily create work orders in finite time" do
      Timeout::timeout(5) { WorkOrderProcessor.generate_work_orders(world, scenario, prng).take(100).length.should eq(100) }
    end
  end

  context "with infinitely many schools" do
    let(:world)  {{ "high" => Array.new(100000) {|school| {'id' => "Zeno High", 'students' => {2001 => {:KINDERGARTEN => 5}}, 'sessions' => [{}]}} }}

    it "will lazily create work orders in finite time" do
      Timeout::timeout(5) { WorkOrderProcessor.generate_work_orders(world, scenario, prng).take(100).length.should eq(100) }
    end
  end
end
