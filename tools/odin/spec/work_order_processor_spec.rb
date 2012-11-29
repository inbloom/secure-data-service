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

require_relative '../lib/EntityCreation/work_order_processor.rb'
require_relative '../lib/Shared/demographics.rb'
require_relative '../lib/OutputGeneration/XML/studentParentInterchangeGenerator.rb'
require_relative '../lib/OutputGeneration/XML/enrollmentGenerator.rb'

describe "WorkOrderProcessor" do
  describe "#build" do
    context 'With a simple work order' do
      let(:work_order) {{:id => 42, :sessions => [{:school => 64, :sections => [{:id => 32, :edOrg => 64},
                                                                                  {:id => 33, :edOrg => 64},
                                                                                  {:id => 34, :edOrg => 128}]},
                                                    {:school => 65, :sections => [{:id => 16, :edOrg => 65},
                                                                                  {:id => 17, :edOrg => 65}]}],
                         :demographics => Demographics.new, :birth_day_after => Date.new(2000, 9, 1)}}

      it "will generate the right number of entities for the student generator" do
        studentParent = double
        studentParent.should_receive(:<<).with(an_instance_of(Student)).once
        WorkOrderProcessor.new({:studentParent => studentParent}).build(StudentWorkOrder.new(work_order))
      end

      it "will generate the right number of entities for the enrollment generator" do
        enrollment = double
        enrollment.should_receive(:<<).with(an_instance_of(StudentSchoolAssociation)).twice
        WorkOrderProcessor.new({:enrollment => enrollment}).build(StudentWorkOrder.new(work_order))
      end

    end
  end
end

describe "gen_work_orders" do
  context "with a world with 20 students in 4 schools" do
    let(:world) {{'seas' => [{'id' => 'sea1'}], 'leas' => [{'id' => 'lea1'}], 
                  'elementary' => [{'id' => 0, 'students' => 5, 'sessions' => [{}]},
                                   {'id' => 1, 'students' => 5, 'sessions' => [{}]}],
                  'middle' => [{'id' => 2, 'students' => 5, 'sessions' => [{}]}],
                  'high' => [{'id' => 3, 'students' => 5, 'sessions' => [{}]}]}}
    let(:work_orders) {WorkOrderProcessor.gen_work_orders world}

    it "will create a work order for each student" do
      work_orders.count.should eq(20)
    end

    it "will put the students in the right schools" do
      work_orders.each_with_index{|work_order, index|
        work_order.work_order[:sessions][0][:school].should eq(index/5)
      }
    end

    it "will generate unique student ids" do
      work_orders.map{|wo| wo.work_order[:id]}.to_set.count.should eq(20)
    end

  end
  
  context "with an infinitely large school" do
    let(:world) {{'high' => [{'id' => "Zeno High", 'students' => 1.0/0, 'sessions' => [{}]}]}}

    it "will lazily create work orders in finite time" do
      Timeout::timeout(5){
        WorkOrderProcessor.gen_work_orders(world).take(100).length.should eq(100)
      }
    end
  end

end
