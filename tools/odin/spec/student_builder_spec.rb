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

require_relative '../student_builder.rb'

describe "StudentBuilder" do
  describe "#build" do
    context 'With a simple work order' do
      let(:work_order) {{:id => 42, :sessions => [{:school => 64, :sections => [{:id => 32, :edOrg => 64},
                                                                                  {:id => 33, :edOrg => 64},
                                                                                  {:id => 34, :edOrg => 128}]},
                                                    {:school => 65, :sections => [{:id => 16, :edOrg => 65},
                                                                                  {:id => 17, :edOrg => 65}]}]}}
      let(:studentParent) {StringIO.new('', 'w')}
      let(:enrollment) {StringIO.new('', 'w')}
      let(:builder) {StudentBuilder.new(work_order, {:studentParent => studentParent, :enrollment => enrollment})}
      before {builder.build}

      it "will build student documents with the given student id" do 
        studentParent.string.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      end

      it "will have the right number of schools associations" do
        enrollment.string.lines.select{|l| l.match('<StudentSchoolAssociation>')}.length.should eq(2)
      end

      it "will have the right number of section associations" do
        enrollment.string.lines.select{|l| l.match('<StudentSectionAssociation>')}.length.should eq(5)
      end
    end
  end
end
