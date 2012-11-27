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

describe "DataUtility" do
  describe "Generates correct _id for each supported entity" do
    describe "#get_student_unique_state_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_student_unique_state_id(8042) == "sdnt-0000008042"
      end
    end

    describe "#get_parent_unique_state_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_parent_unique_state_id(8042) == "prnt-0000008042"
      end
    end

    describe "#get_state_education_agency_state_organization_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_state_education_agency_id(8042) == "stte-0000008042"
      end
    end

    describe "#get_local_education_agency_state_organization_id" do
      it "will generate a student unique state id with the correct format" do
        DataUtility.get_local_education_agency_id(8042) == "locl-0000008042"
      end
    end

    describe "#get_elementary_school_id" do
      it "will generate an elementary school stateOrganizationId with the correct format" do
        DataUtility.get_elementary_school_id(26) == "elem-0000000026"
      end
    end

    describe "#get_middle_school_id" do
      it "will generate a middle school stateOrganizationId with the correct format" do
        DataUtility.get_middle_school_id(47) == "midl-0000000047"
      end
    end

    describe "#get_high_school_id" do
      it "will generate a high school stateOrganizationId with the correct format" do
        DataUtility.get_high_school_id(100375) == "high-0000100375"
      end
    end

    describe "#get_course_unique_id" do
      it "will generate a course unique id with the correct format" do
        DataUtility.get_course_unique_id(100375) == "crse-0000100375"
      end
    end

    describe "#get_staff_unique_state_id" do
      it "will generate a staff unique state id with the correct format" do
        DataUtility.get_staff_unique_state_id(146724) == "stff-0000146724"
      end
    end

    describe "#get_teacher_unique_state_id" do
      it "will generate a teacher unique state id with the correct format" do
        DataUtility.get_teacher_unique_state_id(146724) == "tech-0000146724"
      end
    end

    describe "#get_course_offering_code" do
      it "will generate a course offering code with the correct format" do
        DataUtility.get_course_offering_code(90125555) == "cofr-0090125555"
      end
    end

    describe "#get_unique_section_id" do
      it "will generate a unique section id with the correct format" do
        DataUtility.get_unique_section_id(90125555) == "sctn-0090125555"
      end
    end
  end

  describe "Handles edge cases" do
    describe "state organization id edge cases" do
      it "will handle null input" do
        DataUtility.get_elementary_school_id(nil) == "elem-0000000000"
      end

      it "will handle zero input" do
        DataUtility.get_elementary_school_id(0) == "elem-0000000000"
      end

      it "will handle organization ids with more than 10 characters" do
        DataUtility.get_elementary_school_id(1123581321345589) == "elem-1123581321345589"
      end
    end
  end

end