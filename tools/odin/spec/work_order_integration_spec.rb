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
  let(:elementary_school) {:elementary}
  let(:middle_school) {:middle}
  let(:high_school) {:high}
  let(:kindergarten) {:KINDERGARTEN}
  let(:first_grade) {:FIRST_GRADE}
  let(:fifth_grade) {:FIFTH_GRADE}
  let(:sixth_grade) {:SIXTH_GRADE}
  let(:seventh_grade) {:SEVENTH_GRADE}
  let(:eighth_grade) {:EIGHTH_GRADE}
  let(:ninth_grade) {:NINTH_GRADE}
  let(:eleventh_grade) {:ELEVENTH_GRADE}
  let(:twelfth_grade) {:TWELFTH_GRADE}

  let(:config) {YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))}
  let(:prng) {Random.new(config['seed'])}
  let(:competency_level_descriptors) {[
      {'code_value' => 'Code Value 1', "description" => "Description 1"},
      {'code_value' => 'Code Value 2', "description" => "Description 2"},
  ]}
  let(:scenario) {Scenario.new({'BEGIN_YEAR' => 2001, 'NUMBER_OF_YEARS' => 2, 
                    'ASSESSMENTS_TAKEN' => {'GRADE_WIDE_ASSESSMENTS' => 5}, 'ASSESSMENTS_PER_GRADE'=>3,
                    'ASSESSMENT_ITEMS_PER_ASSESSMENT' => {'GRADE_WIDE_ASSESSMENTS' => 3},
                    'INCIDENTS_PER_SECTION' => 1,
                    'LIKELYHOOD_STUDENT_WAS_INVOLVED' => 0.5,
                    'INCLUDE_PARENTS' => true, 'COHORTS_PER_SCHOOL' => 4, 'PROBABILITY_STUDENT_IN_COHORT' => 1, 'DAYS_IN_COHORT' => 30,
                    'COMPETENCY_LEVEL_DESCRIPTORS' => competency_level_descriptors,
                    'OPTIONAL_FIELD_LIKELYHOOD' => 1})}
  describe "#build" do

    let(:entity_queue) {EntityQueue.new}
    let(:work_order_queue) {WorkOrderQueue.new}
    before(:all) do

      class Factory
        # student creation
        attr_accessor :students, :school_associations, :assessment_associations, :section_associations, :assessment_items,
          :parents, :parent_associations, :cohort_associations, :program_associations, :report_cards, :discipline_incidents,
          :discipline_actions, :student_competencies, :academic_records, :transcripts
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
          @report_cards = to_build.select{|a| a.kind_of? ReportCard}
          @academic_records = to_build.select{|a| a.kind_of? StudentAcademicRecord}
          @discipline_incidents = to_build.select{|a| a.kind_of? StudentDisciplineIncidentAssociation}
          @discipline_actions = to_build.select{|a| a.kind_of? DisciplineAction}
          @student_competencies = to_build.select{|a| a.kind_of? StudentCompetency}
          @transcripts = to_build.select{|a| a.kind_of? CourseTranscript}
        end
      end

    end
    let(:factory) {Factory.new}
    before { work_order_queue.factory(factory, entity_queue) }

    context 'With a simple work order' do
      let(:assessment_factory) {AssessmentFactory.new(scenario)}
      let(:section_factory) {double('SectionWorkOrderFactory', :sections => {{'id' => 1} => [{:id => 42}, {:id => 43}, {:id => 44}], {'id' => 2} => [{:id => 45}, {:id => 46}, {:id => 47}]})}
      
      let(:ed_org) {{'id' => 64, 'parent' => 2, 'sessions' => [{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180)},
                                                               {'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180)}],
                                 'programs' => [{:id => 1, :type => :IDEA, :sponsor => :SCHOOL}]}}
      let(:programs) {[{:id => 2, :type => :IDEA, :sponsor => :LOCAL_EDUCATION_AGENCY, :ed_org_id => 2}]}

      let(:work_order) {StudentWorkOrder.new(42, :initial_grade => :KINDERGARTEN, :initial_year => 2001, :scenario => scenario,
                                             :plan => {2001 => {:type => elementary_school, :grade => kindergarten, :school => ed_org, :programs => programs},
                                                       2002 => {:type => elementary_school, :grade => first_grade, :school => ed_org, :programs => programs}},
                                             :section_factory => section_factory, 
                                             :assessment_factory => assessment_factory)}

      before { work_order_queue.push_work_order(work_order) }

      it "will generate the right number of entities for the student and subsequent enrollment" do
        factory.students.should have(1).items        
        factory.school_associations.select{|ssa| ssa.start_date.year == 2001 and ssa.grade == "Kindergarten"}.should have(1).items
        factory.school_associations.select{|ssa| ssa.start_date.year == 2002 and ssa.grade == "First grade"}.should have(1).items
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
          a.student.should eq 42
          a.school.should eq 64
        }
        factory.school_associations[0].start_date.year.should eq(2001)
        factory.school_associations[0].grade.should eq("Kindergarten")
        factory.school_associations[1].start_date.year.should eq(2002)
        factory.school_associations[1].grade.should eq("First grade")
      end

      it "will generate StudentSectionAssociations with the correct information" do
        factory.section_associations.each{|a|
          a.student.should eq 42
          a.ed_org_id.should eq 64
        }
        section_associations = factory.section_associations.group_by{|a| a.begin_date.year}
        section_associations[2001].count.should eq 2
        section_associations[2001][0].section.should eq 42
        section_associations[2001][1].section.should eq 45
        section_associations[2002].count.should eq 2
        section_associations[2002][0].section.should eq 42
        section_associations[2002][1].section.should eq 45
      end

      it "will generate StudentAssessments with the correct related assessment" do
        student_assessments = factory.assessment_associations.group_by{|sa| sa.assessment.assessmentTitle }

        assessment_factory.grade_wide_assessments(kindergarten, 2001).each{|a|
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
          c.cohort.ed_org_id.should eq 64
          c.begin_date.should eq c.end_date - 30
        }
        factory.cohort_associations.group_by{|c| c.cohort.identifier}.each{|cohort, associations|
          associations.should have(2).items
        }

      end

      it "will generate the correct number of report cards with valid gpa range" do
        factory.report_cards.should have(2).items
        factory.report_cards.each{|report_card|
          report_card.gpa_given_grading_period.should be <= 4.0
          report_card.gpa_given_grading_period.should be >= 0.0
        }
      end

      it "will generate the correct number of student competencies with valid code values" do
        factory.student_competencies.should have(8).items
        factory.student_competencies.each{|student_competency|
          ["Code Value 1", "Code Value 2"].should include(student_competency.code_value)
        }
      end

      it "will generate the correct student academic records" do
        records = factory.academic_records
        records.should have(2).items
        records.select{|r| r.session['year'] == 2001}.should have(1).items
        records.select{|r| r.session['year'] == 2002}.should have(1).items
        records.map{|r| r.report_card}.should eq factory.report_cards
      end

      it "will generate student discipline incident associations when appropriate" do
        # with only two students in the section and one incident per section, student should get a 
        # single incident association each year
        # This may requiring tweaking some ideas, the important part is that they get generated in some cases
        # but not in others
        discipline_incident_count = factory.discipline_incidents.length
        student_section_count = factory.section_associations.length
        discipline_incident_count.should be > 0
        discipline_incident_count.should be < student_section_count
      end

      it "will generate discipline actions for each incident" do
        factory.discipline_actions.map{|a| a.incidents[0].incident_identifier}.should eq(factory.discipline_incidents.map{|i| i.incident})
      end

      it "will generate a course transcript for each course taken" do
        transcripts = factory.transcripts
        transcripts.should have(factory.section_associations.count).items
        transcripts.each{|t|
          t.student_id.should eq 42
          t.ed_org_id.should eq DataUtility.get_elementary_school_id 64
        }
      end
    end

    context 'With a work order that spans multiple schools' do
      let(:scenario) {Scenario.new({'BEGIN_YEAR' => 2001, 'NUMBER_OF_YEARS' => 5})}
      let(:sessions) {[{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180)},
                       {'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180)},
                       {'year' => 2003, 'interval' => DateInterval.new(Date.new(2003), Date.new(2004), 180)},
                       {'year' => 2004, 'interval' => DateInterval.new(Date.new(2004), Date.new(2005), 180)},
                       {'year' => 2005, 'interval' => DateInterval.new(Date.new(2005), Date.new(2006), 180)}]}
      let(:elementary_ed_org) { {'id' => 64, 'sessions' => sessions, 'feeds_to' => [65], 'programs' => []} }
      let(:middle_ed_org) { {'id' => 65, 'sessions' => sessions, 'feeds_to' => [66], 'programs' => []} }
      let(:high_ed_org) { {'id' => 66, 'sessions' => sessions, 'programs' => []} }

      let(:work_order) {StudentWorkOrder.new(42, :initial_grade => fifth_grade, :initial_year => 2001,
                          :plan => {2001 => {:type => elementary_school, :grade => fifth_grade, :school => elementary_ed_org, :programs => []},
                                    2002 => {:type => middle_school, :grade => sixth_grade, :school => middle_ed_org, :programs => []},
                                    2003 => {:type => middle_school, :grade => seventh_grade, :school => middle_ed_org, :programs => []},
                                    2004 => {:type => middle_school, :grade => eighth_grade, :school => middle_ed_org, :programs => []},
                                    2005 => {:type => high_school, :grade => ninth_grade, :school => high_ed_org, :programs => []}},
                          :graduation_plans => [ GraduationPlan.new("Standard", {'Math'=> 12}, "state-id")] )}

      before { work_order_queue.push_work_order(work_order) }
      it "will get enrollments for each school" do
        factory.students.should have(1).items
        factory.school_associations[0].start_date.year.should eq(2001)
        factory.school_associations[0].school.should eq(64)
        factory.school_associations[1].start_date.year.should eq(2002)
        factory.school_associations[1].school.should eq(65)
        factory.school_associations[2].start_date.year.should eq(2003)
        factory.school_associations[2].school.should eq(65)
        factory.school_associations[3].start_date.year.should eq(2004)
        factory.school_associations[3].school.should eq(65)
        factory.school_associations[4].start_date.year.should eq(2005)
        factory.school_associations[4].school.should eq(66)
      end

      it "will put graduation plans on the school association iff the school is a high school" do
        factory.school_associations[0].gradPlan.should be_nil
        factory.school_associations[1].gradPlan.should be_nil
        factory.school_associations[2].gradPlan.should be_nil
        factory.school_associations[3].gradPlan.should be_nil
        factory.school_associations[4].gradPlan.should_not be_nil
      end
    end

    context "with a work order that includes students graduating" do
      let(:education_organization) {{'id' => 64, 'sessions' => [{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180)},
                                                                {'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180)},
                                                                {'year' => 2003, 'interval' => DateInterval.new(Date.new(2003), Date.new(2004), 180)},
                                                                {'year' => 2004, 'interval' => DateInterval.new(Date.new(2004), Date.new(2005), 180)}]}}
      let(:eleventh_grader) {StudentWorkOrder.new(42, :initial_grade => eleventh_grade, :initial_year => 2001,
                              :plan => {2001 => {:type => high_school, :grade => eleventh_grade, :school => education_organization, :programs => []},
                                        2002 => {:type => high_school, :grade => twelfth_grade, :school => education_organization, :programs => []}})}
      let(:twelfth_grader) {StudentWorkOrder.new(42, :initial_grade => twelfth_grade, :initial_year => 2001,
                              :plan => {2001 => {:type => high_school, :grade => twelfth_grade, :school => education_organization, :programs => []}})}

      it "will only generate student school associations until the student has graduated" do
        work_order_queue.push_work_order(eleventh_grader)
        factory.students.should have(1).items
        factory.school_associations.should have(2).items
        factory.school_associations[0].start_date.year.should eq(2001)
        factory.school_associations[1].start_date.year.should eq(2002)

        factory.school_associations = []
        factory.students = []

        work_order_queue.push_work_order(twelfth_grader)
        factory.students.should have(1).items
        factory.school_associations.should have(1).items
        factory.students.should have(1).items
        factory.school_associations[0].start_date.year.should eq(2001)
      end
    end
  end
end


describe "generate_work_orders" do
  let(:config) {YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))}
  let(:prng) {Random.new(config['seed'])}
  let(:scenario) {Scenario.new({'BEGIN_YEAR' => 2011, 'NUMBER_OF_YEARS' => 2, 'ASSESSMENTS_TAKEN' => {'GRADE_WIDE_ASSESSMENTS' => 5}, 'ASSESSMENTS_PER_GRADE'=>3,
                   'ASSESSMENT_ITEMS_PER_ASSESSMENT' => {'GRADE_WIDE_ASSESSMENTS' => 3}})}

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

    it "will give students the correct entry grade" do
      work_orders.select{ |wo| wo.initial_grade == :KINDERGARTEN }.count.should eq(10)
      work_orders.select{ |wo| wo.initial_grade == :SEVENTH_GRADE }.count.should eq(5)
      work_orders.select{ |wo| wo.initial_grade == :NINTH_GRADE }.count.should eq(5)
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
