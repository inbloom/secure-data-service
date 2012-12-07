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
    describe "#get_student_unique_state_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_student_unique_state_id(8042).should match("sdnt-0000008042")
      end
    end

    describe "#get_parent_unique_state_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_parent_unique_state_id(8042).should match("prnt-0000008042")
      end
    end

    describe "#get_state_education_agency_state_organization_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_state_education_agency_id(8042).should match("stte-0000008042")
      end
    end

    describe "#get_local_education_agency_state_organization_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_local_education_agency_id(8042).should match("locl-0000008042")
      end
    end

    describe "#get_elementary_school_id" do
      it "will generate an elementary school stateOrganizationId with the correct format" do
        DataUtility.get_elementary_school_id(26).should match("elem-0000000026")
      end
    end

    describe "#get_middle_school_id" do
      it "will generate a middle school stateOrganizationId with the correct format" do
        DataUtility.get_middle_school_id(47).should match("midl-0000000047")
      end
    end

    describe "#get_high_school_id" do
      it "will generate a high school stateOrganizationId with the correct format" do
        DataUtility.get_high_school_id(100375).should match("high-0000100375")
      end
    end

    describe "#get_course_unique_id" do
      it "will generate a course unique id with the correct format" do
        DataUtility.get_course_unique_id(100375).should match("crse-0000100375")
      end
    end

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

    describe "#get_course_offering_code" do
      it "will generate a course offering code with the correct format" do
        DataUtility.get_course_offering_code(90125555).should match("cofr-0090125555")
      end
    end

    describe "#get_unique_section_id" do
      it "will generate a unique section id with the correct format" do
        DataUtility.get_unique_section_id(42, 64).should match("sctn-0006400042")
      end
    end
  end

  describe "Handles requests for entities correctly" do
    describe "--> request to get elementary school id with string" do
      it "will return the string that was input" do
        DataUtility.get_school_id("Daybreak Elementary School", :elementary).should match("Daybreak Elementary School")
      end
    end

    describe "--> request to get elementary school id with integer" do
      it "will return the corresponding elementary school id" do
        DataUtility.get_school_id(34, :elementary).should match("elem-0000000034")
      end
    end

    describe "--> request to get middle school id with integer" do
      it "will return the corresponding middle school id" do
        DataUtility.get_school_id(35, :middle).should match("midl-0000000035")
      end
    end

    describe "--> request to get high school id with integer" do
      it "will return the corresponding high school id" do
        DataUtility.get_school_id(36, :high).should match("high-0000000036")
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
  end

  describe "Handles edge cases" do
    describe "--> state organization id edge cases" do
      it "will handle null input" do
        DataUtility.get_elementary_school_id(nil).should match("elem-0000000000")
      end

      it "will handle zero input" do
        DataUtility.get_elementary_school_id(0).should match("elem-0000000000")
      end

      it "will handle organization ids with more than 10 characters" do
        DataUtility.get_elementary_school_id(1123581321345589).should match("elem-1123581321345589")
      end
    end
  end

end
