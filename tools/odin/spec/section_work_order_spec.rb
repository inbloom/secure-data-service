
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

describe "SectionWorkOrderFactory" do
  describe "sections" do
    context "with a simple edorg and scenario" do
      let(:scenario) {{'STUDENTS_PER_SECTION' => {'high' => 10}}}
      let(:offerings) {[{'id' => 1, 'grade' => :NINTH_GRADE},
                        {'id' => 2, 'grade' => :TENTH_GRADE},
                        {'id' => 3, 'grade' => :TENTH_GRADE}]}
      let(:edOrg) {{'id' => 42, 'students' => {2001 => {:NINTH_GRADE => 30, :TENTH_GRADE => 0},
                                               2002 => {:NINTH_GRADE => 0, :TENTH_GRADE => 30}},
                                'offerings' => {2001 => offerings, 2002 => offerings}}}
      let(:world) {{'high' => [edOrg]}}
      let(:factory) {SectionWorkOrderFactory.new world, scenario}
      it "will return enough sections so that each student can take it" do
        ninth_grade_sections = factory.sections(edOrg, 'high', 2001, :NINTH_GRADE)
        ninth_grade_sections[{'id' => 1, 'grade' => :NINTH_GRADE}].count.should eq 3
        tenth_grade_sections = factory.sections(edOrg, 'high', 2002, :TENTH_GRADE)
        tenth_grade_sections[{'id' => 2, 'grade' => :TENTH_GRADE}].count.should eq 3
        tenth_grade_sections[{'id' => 3, 'grade' => :TENTH_GRADE}].count.should eq 3
      end

      it "will return no sections when there are no available students" do
        ninth_grade_sections = factory.sections(edOrg, 'high', 2002, :NINTH_GRADE)
        ninth_grade_sections[{'id' => 1, 'grade' => :NINTH_GRADE}].should be_nil
        tenth_grade_sections = factory.sections(edOrg, 'high', 2001, :TENTH_GRADE)
        tenth_grade_sections[{'id' => 2, 'grade' => :TENTH_GRADE}].should be_nil
        tenth_grade_sections[{'id' => 3, 'grade' => :TENTH_GRADE}].should be_nil
      end

    end
  end
end
