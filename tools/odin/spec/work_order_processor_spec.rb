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
require_relative '../lib/EntityCreation/work_order_processor.rb'
require_relative '../lib/Shared/demographics.rb'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType.rb'
require_relative '../lib/Shared/EntityClasses/studentSchoolAssociation.rb'
require_relative '../lib/Shared/EntityClasses/studentSectionAssociation.rb'
require_relative '../lib/Shared/date_interval.rb'
require_relative '../lib/OutputGeneration/XmlDataWriter'

describe "WorkOrderProcessor" do
  describe "#build" do
    context 'With a simple work order' do
      let(:section_factory) {double('section factory', :sections => {{'id' => 1} => [42, 43, 44], {'id' => 2} => [45, 46, 47]})}
      let(:assessment_factory) {double('assessment factory', :assessments => (1..5).map{|i| "assessment#{i}"})}
      let(:scenario) {{'ASSESSMENTS_TAKEN' => {grade_wide: 3}}}
      let(:ed_org) {{'id' => 64, 'sessions' => [{'year' => 2001, 'interval' => DateInterval.new(Date.new(2001), Date.new(2002), 180)}, 
                                                {'year' => 2002, 'interval' => DateInterval.new(Date.new(2002), Date.new(2003), 180)}]}}
      let(:work_order) {StudentWorkOrder.new(42, :initial_grade => :KINDERGARTEN, :initial_year => 2001, 
                                             :edOrg => ed_org, :section_factory => section_factory,
                                             :assessment_factory => assessment_factory, :scenario => scenario)}

      it "will generate the right number of entities for the student and subsequent enrollment" do
        data_writer = double
        data_writer.should_receive(:create_student).with(42, Date.new(1996, 9, 1)).once
        data_writer.should_receive(:create_student_school_association).with(42, 64, 2001, :KINDERGARTEN).once
        data_writer.should_receive(:create_student_school_association).with(42, 64, 2002, :FIRST_GRADE).once
        data_writer.should_receive(:create_student_section_association).exactly(4).times
        data_writer.should_receive(:create_student_assessment).exactly(30).times
        WorkOrderProcessor.new(data_writer, scenario).build(work_order)
      end

      it "will generate StudentSchoolAssociations with the correct information" do
        data_writer = double
        school_associations = []
        data_writer.should_receive(:create_student).with(42, Date.new(1996, 9, 1)).once
        data_writer.stub(:create_student_section_association)
        data_writer.stub(:create_student_assessment)
        data_writer.stub(:create_student_school_association) do |student, school, year, grade|
          school_associations << StudentSchoolAssociation.new(student, school, year, grade)
          student.should eq 42
          school.should eq 64
        end
        WorkOrderProcessor.new(data_writer, scenario).build(work_order)
        school_associations[0].startYear.should eq(2001) and school_associations[0].startGrade.should eq("Kindergarten")
        school_associations[1].startYear.should eq(2002) and school_associations[1].startGrade.should eq("First grade")
      end

      it "will generate StudentSectionAssociations with the correct information" do
        data_writer = XmlDataWriter.new scenario
        section_associations = {2001 => [], 2002 => []}
        data_writer.stub!(:create_student_section_association) do |student, section, course, school, year, grade|
          section_associations[year] << StudentSectionAssociation.new(student, section, course, school, year, grade)
          student.should eq 42
          school.should eq 64
        end
        WorkOrderProcessor.new(data_writer, scenario).build(work_order)
        section_associations[2001].count.should eq 2
        section_associations[2001][0].sectionId.should match(/sctn\-00001/)
        section_associations[2001][1].sectionId.should match(/sctn\-00002/)
        section_associations[2002].count.should eq 2
        section_associations[2002][0].sectionId.should match(/sctn\-00001/)
        section_associations[2002][1].sectionId.should match(/sctn\-00002/)
      end

      it "will generate StudentAssessments with the correct related assessment" do
        data_writer = XmlDataWriter.new scenario
        student_assessments = []
        data_writer.stub!(:create_student_assessment) do |sa|
          student_assessments << sa
        end
        WorkOrderProcessor.new(data_writer, scenario).build(work_order)
        sas_by_id = student_assessments.group_by{|sa| sa.assessment_id}
        assessment_factory.assessments.each{|i|
          sas_by_id[i].should have(6).items
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
                                                        'feeds_to' => [65, 66]})}
      let(:scenario) {{}}
      it "will get enrollments for each school" do
        data_writer = double
        data_writer.should_receive(:create_student).with(42, Date.new(1991, 9, 1)).once
        ssas        = []
        data_writer.stub(:create_student_school_association) do |student, school, year, grade|
          ssas << StudentSchoolAssociation.new(student, school, year, grade)
        end
        WorkOrderProcessor.new(data_writer, scenario).build(work_order)
        ssas[0].startYear.should eq(2001) and ssas[0].schoolStateOrgId.should eq('elem-0000000064')
        ssas[1].startYear.should eq(2002) and ssas[1].schoolStateOrgId.should eq('midl-0000000065')
        ssas[2].startYear.should eq(2003) and ssas[2].schoolStateOrgId.should eq('midl-0000000065')
        ssas[3].startYear.should eq(2004) and ssas[3].schoolStateOrgId.should eq('midl-0000000065')
        ssas[4].startYear.should eq(2005) and ssas[4].schoolStateOrgId.should eq('high-0000000066')
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
      let(:scenario) {{}}
      it "will only generate student school associations until the student has graduated" do
        data_writer = double
        data_writer.should_receive(:create_student).with(42, Date.new(1985, 9, 1)).once
        ssas        = []
        data_writer.stub(:create_student_school_association) do |student, school, year, grade|
          ssas << StudentSchoolAssociation.new(student, school, year, grade)
        end
        WorkOrderProcessor.new(data_writer, scenario).build(eleventh_grader)
        ssas.should have(2).items
        ssas[0].startYear.should eq(2001)
        ssas[1].startYear.should eq(2002)
        ssas = []
        data_writer.should_receive(:create_student).with(42, Date.new(1984, 9, 1)).once
        WorkOrderProcessor.new(data_writer, scenario).build(twelfth_grader)
        ssas.should have(1).items
        ssas[0].startYear.should eq(2001)
      end
    end
  end
end

describe "generate_work_orders" do
  context "with a world with 20 students in 4 schools" do

    let(:world) {{'seas' => [{'id' => 'sea1'}], 'leas' => [{'id' => 'lea1'}], 
                  'elementary' => [{'id' => 0, 'students' => {2011 => {:KINDERGARTEN => 5}, 2012 => {:FIRST_GRADE => 5}}, 'sessions' => [{}]},
                                   {'id' => 1, 'students' => {2011 => {:KINDERGARTEN => 5}, 2012 => {:FIRST_GRADE => 5}}, 'sessions' => [{}]}],
                  'middle' => [{'id' => 2, 'students' => {2011 => {:SEVENTH_GRADE => 5}, 2012 => {:EIGTH_GRADE => 5}}, 'sessions' => [{}]}],
                  'high' => [{'id' => 3, 'students' => {2011 => {:NINTH_GRADE => 5}, 2012 => {:TENTH_GRADE => 5}}, 'sessions' => [{}]}]}}
    
    let(:work_orders) { WorkOrderProcessor.generate_work_orders world, {}}

    it "will create a work order for each student" do
      work_orders.count.should eq(20)
    end

    it "will put the students in the right schools" do
      work_orders.each_with_index { |work_order, index| work_order.edOrg['id'].should eq(index/5) }
    end

    it "will give students the correct entry grade" do
      work_orders.select{|wo| wo.initial_grade == :KINDERGARTEN}.count.should eq(10)
      work_orders.select{|wo| wo.initial_grade == :SEVENTH_GRADE}.count.should eq(5)
      work_orders.select{|wo| wo.initial_grade == :NINTH_GRADE}.count.should eq(5)
    end

    it "will give students the correct entry year" do
      work_orders.select{|wo| wo.initial_year == 2011}.count.should eq(20)
    end

    it "will generate unique student ids" do
      work_orders.map{|wo| wo.id}.to_set.count.should eq(20)
    end
  end
  
  context "with an infinitely large school" do
    let(:world)  {{'high' => [{'id' => "Zeno High", 'students' => {2001 => {:KINDERGARTEN => 1.0/0}}, 'sessions' => [{}]}]}}

    it "will lazily create work orders in finite time" do
      Timeout::timeout(5){
        WorkOrderProcessor.generate_work_orders(world, {}).take(100).length.should eq(100)
      }
    end
  end

  context "with infinitely many schools" do
    let(:world)  {{'high' => [{'id' => "Zeno High", 'students' => {2001 => {:KINDERGARTEN => 5}}, 'sessions' => [{}]}].cycle}}

    it "will lazily create work orders in finite time" do
      Timeout::timeout(5){
        WorkOrderProcessor.generate_work_orders(world, {}).take(100).length.should eq(100)
      }
    end
  end

end
