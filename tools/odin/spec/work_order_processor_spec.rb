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

require_relative '../lib/EntityCreation/work_order_processor.rb'
require_relative '../lib/Shared/demographics.rb'
require_relative '../lib/OutputGeneration/XML/studentGenerator.rb'
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
        WorkOrderProcessor.new(work_order, {:studentParent => studentParent}).build
      end

      it "will generate the right number of entities for the enrollment generator" do
        enrollment = double
        enrollment.should_receive(:<<).with(an_instance_of(StudentSchoolAssociation)).twice
        WorkOrderProcessor.new(work_order, {:enrollment => enrollment}).build
      end

    end
  end

  describe ".run_work_orders" do
  end

  describe ".make_work_order" do
  end
end
