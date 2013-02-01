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

require_relative '../lib/Shared/data_utility.rb'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType.rb'
require_relative 'spec_helper'

# specifications for data utility
describe "DataUtility" do
  before(:all) do
    @yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @prng = Random.new(@yaml['seed'])
  end

  after(:all) do
    @yaml = nil
    @prng = nil
  end

  describe "Generates correct _id for each supported entity" do
    describe "#get_staff_unique_state_id" do
      it "will generate a staff unique state id with the correct format" do
        DataUtility.get_staff_unique_state_id(146724).should match("stff-0000146724")
      end
    end

    describe "#get_teacher_unique_state_id" do
      it "will generate a teacher unique state id with the correct format" do
        DataUtility.get_teacher_unique_state_id(146724).should match("tech-0000146724")
      end
    end

  end

  describe "Handles requests for entities correctly" do
    describe "--> request to get staff unique state id with string" do
      it "will return the string that was input" do
        DataUtility.get_staff_unique_state_id("rrogers").should match("rrogers")
      end
    end

    describe "--> request to get staff unique state id with integer" do
      it "will return the corresponding staff unique state id" do
        DataUtility.get_staff_unique_state_id(17).should match("stff-0000000017")
      end
    end

    describe "--> request to get teacher unique state id with string" do
      it "will return the string that was input" do
        DataUtility.get_teacher_unique_state_id("cgray").should match("cgray")
      end
    end

    describe "--> request to get teacher unique state id with integer" do
      it "will return the corresponding teacher unique state id" do
        DataUtility.get_teacher_unique_state_id(18).should match("tech-0000000018")
      end
    end
    describe "--> request to get random elementary school grade" do
      it "will always return only grades that are in elementary school" do
        grades = [:KINDERGARTEN, :FIRST_GRADE, :SECOND_GRADE, :THIRD_GRADE, :FOURTH_GRADE, :FIFTH_GRADE]
        (1..25).each do
          grades.include?(DataUtility.get_random_grade_for_type(@prng, "elementary")).should be_true
        end
      end
    end

    describe "--> request to get random middle school grade" do
      it "will always return only grades that are in middle school" do
        grades = [:SIXTH_GRADE, :SEVENTH_GRADE, :EIGHTH_GRADE]
        (1..25).each do
          grades.include?(DataUtility.get_random_grade_for_type(@prng, "middle")).should be_true
        end
      end
    end

    describe "--> request to get random high school grade" do
      it "will always return only grades that are in high school" do
        grades = [:NINTH_GRADE, :TENTH_GRADE, :ELEVENTH_GRADE, :TWELFTH_GRADE]
        (1..25).each do
          grades.include?(DataUtility.get_random_grade_for_type(@prng, "high")).should be_true
        end
      end
    end

    describe "--> request to get subset of choices" do
      it "will return a subset of choices with correct size" do
        options = [1,2,3,4,5,6,7,8,9,10]
        subset  = DataUtility.select_num_from_options(@prng, 5, options)
        subset.size.should eq 5
        subset.each do |number|
          options.include?(number).should be_true
        end
      end

      it "will return choices if the number specified is larger than the size of choices" do
        options = [1,2,3,4,5,6,7,8,9,10]
        subset  = DataUtility.select_num_from_options(@prng, 15, options)
        subset.size.should eq 10
        subset.should eq options
      end

      it "will return an empty array is the number specified is zero" do
        options = [1,2,3,4,5,6,7,8,9,10]
        subset  = DataUtility.select_num_from_options(@prng, 0, options)
        subset.size.should eq 0
      end
    end
  end

end
