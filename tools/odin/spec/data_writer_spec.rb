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

require 'date'
require 'yaml'

require_relative '../lib/OutputGeneration/DataWriter.rb'
require_relative '../lib/Shared/data_utility.rb'
require_relative '../lib/Shared/date_interval.rb'
require_relative '../lib/Shared/date_utility.rb'
require_relative "../lib/Shared/EntityClasses/enum/GradingPeriodType.rb"
require_relative "../lib/Shared/EntityClasses/enum/CalendarEventType.rb"
require_relative "../lib/Shared/EntityClasses/enum/ProgramAssignmentType.rb"

# specifications for base data writer
describe "DataWriter" do
  context "with a basic writer" do
    before(:each) do
      @yaml   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
      scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
      @random = Random.new(@yaml['seed'])
      @writer = DataWriter.new(scenario)
    end

    after(:each) do
      @writer.finalize
    end

    describe "--> initialization" do
      it "will return an empty hash when requesting @counts after initialization" do
        @writer.get_counts_for_all_entities.empty?.should be_true
      end

      it "will return nil when requesting type for an entity that is not in @entities" do
        @writer.get_entities(StateEducationAgency).should be_nil
      end

      it "will return zero when requesting count for an entity that is not in @counts" do
        @writer.get_entity_count(StateEducationAgency).should eq(0)
      end
    end

    describe "--> creating entities" do
      it "will store a state education agency in-memory" do
        @writer.get_entity_count(StateEducationAgency).should eq(0)
        @writer << StateEducationAgency.new(1, @random)
        @writer.get_entity_count(StateEducationAgency).should_not be_nil
        @writer.get_entity_count(StateEducationAgency).should eq(1)
      end

      it "will store a local education agency in-memory" do
        @writer.get_entity_count(LocalEducationAgency).should eq(0)
        @writer << LocalEducationAgency.new(2, 1, @random)
        @writer.get_entity_count(LocalEducationAgency).should_not be_nil
        @writer.get_entity_count(LocalEducationAgency).should eq(1)
      end

      it "will store a school in-memory" do
        @writer.get_entity_count(School).should eq(0)
        @writer << School.new(3, 2, "elementary")
        @writer.get_entity_count(School).should_not be_nil
        @writer.get_entity_count(School).should eq(1)
      end

      it "will store a course in-memory" do
        @writer.get_entity_count(Course).should eq(0)
        @writer << Course.new(1, "Fifth grade", "title", 1)
        @writer.get_entity_count(Course).should_not be_nil
        @writer.get_entity_count(Course).should eq(1)
      end

      it "will store a program in-memory" do
        @writer.get_entity_count(Program).should eq(0)
        @writer << Program.new(@random, 1)
        @writer.get_entity_count(Program).should_not be_nil
        @writer.get_entity_count(Program).should eq(1)
      end

      it "will store a session in-memory" do
        grading_periods = []
        start_date = DateUtility.random_school_day_on_interval(@random, Date.new(2012, 8, 25), Date.new(2012, 9, 10))
        interval   = DateInterval.create_using_start_and_num_days(@random, start_date, 180)
        @writer.get_entity_count(Session).should eq(0)
        @writer << Session.new("name", 2012, "term", interval, 2, grading_periods)
        @writer.get_entity_count(Session).should_not be_nil
        @writer.get_entity_count(Session).should eq(1)
      end

      it "will store a grading period in-memory" do
        calendar_dates = []
        start_date = DateUtility.random_school_day_on_interval(@random, Date.new(2012, 8, 25), Date.new(2012, 9, 10))
        interval   = DateInterval.create_using_start_and_num_days(@random, start_date, 180)
        @writer.get_entity_count(GradingPeriod).should eq(0)
        @writer << GradingPeriod.new(:END_OF_YEAR, 2012, interval, 2, calendar_dates)
        @writer.get_entity_count(GradingPeriod).should_not be_nil
        @writer.get_entity_count(GradingPeriod).should eq(1)
      end

      it "will store a calendar date in-memory" do
        @writer.get_entity_count(CalendarDate).should eq(0)
        @writer << CalendarDate.new(Date.new(2012, 9, 1), :INSTRUCTIONAL_DAY, 1)
        @writer.get_entity_count(CalendarDate).should_not be_nil
        @writer.get_entity_count(CalendarDate).should eq(1)
      end

      it "will store a course offering in-memory" do
        @writer.get_entity_count(CourseOffering).should eq(0)
        @writer << CourseOffering.new(1, "title", "3", {}, {})
        @writer.get_entity_count(CourseOffering).should_not be_nil
        @writer.get_entity_count(CourseOffering).should eq(1)
      end

      # it "will store a staff in-memory when handling call to create one" do
      #   @writer.get_entity_count(Staff).should eq(0)
      #   @writer << Staff.new(1, 1969)
      #   @writer.get_entity_count(Staff).should_not be_nil
      #   @writer.get_entity_count(Staff).should eq(1)
      # end

      it "will store a staff education organization assignment association in-memory when handling call to create one" do
        @writer.get_entity_count(StaffEducationOrgAssignmentAssociation).should eq(0)
        @writer << StaffEducationOrgAssignmentAssociation.new(1, 1, :PRINCIPAL, "title", Date.new(2009, 9, 4))
        @writer.get_entity_count(StaffEducationOrgAssignmentAssociation).should_not be_nil
        @writer.get_entity_count(StaffEducationOrgAssignmentAssociation).should eq(1)
      end

      # it "will store a teacher in-memory when handling call to create one" do
      #   @writer.get_entity_count(Teacher).should eq(0)
      #   @writer << Teacher.new(3, 1971)
      #   @writer.get_entity_count(Teacher).should_not be_nil
      #   @writer.get_entity_count(Teacher).should eq(1)
      # end

      it "will store a teacher school association in-memory when handling call to create one" do
        @writer.get_entity_count(TeacherSchoolAssociation).should eq(0)
        @writer << TeacherSchoolAssociation.new(3, 1, :REGULAR_EDUCATION, [:FIRST_GRADE], [:COMPUTER_AND_INFORMATION_SCIENCES])
        @writer.get_entity_count(TeacherSchoolAssociation).should_not be_nil
        @writer.get_entity_count(TeacherSchoolAssociation).should eq(1)
      end

      # add assessment metadata tests
      # add student assessment tests
    end
  end

  describe "--> creating entities" do
    before(:each) do
      @yaml   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
      scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
      @random = Random.new(@yaml['seed'])
      @writer = DataWriter.new(scenario)
    end

    after(:each) do
      @writer.finalize
    end
    
    it "will store a state education agency in-memory" do
      @writer.get_entity_count(StateEducationAgency).should eq(0)
      @writer << StateEducationAgency.new(1, @random)
      @writer.get_entity_count(StateEducationAgency).should_not be_nil
      @writer.get_entity_count(StateEducationAgency).should eq(1)
    end

    it "will store a local education agency in-memory" do
      @writer.get_entity_count(LocalEducationAgency).should eq(0)
      @writer << LocalEducationAgency.new(2, 1, @random)
      @writer.get_entity_count(LocalEducationAgency).should_not be_nil
      @writer.get_entity_count(LocalEducationAgency).should eq(1)
    end

    it "will store a school in-memory" do
      @writer.get_entity_count(School).should eq(0)
      @writer << School.new(3, 2, "elementary")
      @writer.get_entity_count(School).should_not be_nil
      @writer.get_entity_count(School).should eq(1)
    end

    it "will store a course in-memory" do
      @writer.get_entity_count(Course).should eq(0)
      @writer << Course.new(1, "Fifth grade", "title", 1)
      @writer.get_entity_count(Course).should_not be_nil
      @writer.get_entity_count(Course).should eq(1)
    end

    it "will store a program in-memory" do
      @writer.get_entity_count(Program).should eq(0)
      @writer << Program.new(@random, 1)
      @writer.get_entity_count(Program).should_not be_nil
      @writer.get_entity_count(Program).should eq(1)
    end

    it "will store a session in-memory" do
      grading_periods = []
      start_date = DateUtility.random_school_day_on_interval(@random, Date.new(2012, 8, 25), Date.new(2012, 9, 10))
      interval   = DateInterval.create_using_start_and_num_days(@random, start_date, 180)
      @writer.get_entity_count(Session).should eq(0)
      @writer << Session.new("name", 2012, "term", interval, 2, grading_periods)
      @writer.get_entity_count(Session).should_not be_nil
      @writer.get_entity_count(Session).should eq(1)
    end
  end

 #  context "with a writer that contains a blacklist" do
 #    let(:scenario) {{'ENTITY_BLACKLIST' => ['Teacher']}}
 #    let(:writer) {DataWriter.new(scenario)}

 #    it "will not store entities that are blacklisted" do
 #      writer.get_entity_count(Teacher).should eq(0)
 #      writer << Teacher.new(3, 1971)
 #      writer << [Teacher.new(3, 1971)]
 #      writer.get_entity_count(Teacher).should eq(0)
 #    end

 #    it "will store entities that are not blacklisted" do
 #      writer.get_entity_count(Staff).should eq(0)
 #      writer << Staff.new(1, 1969)
 #      writer << [Staff.new(1, 1969)]
 #      writer.get_entity_count(Staff).should eq(2)
 #    end
 #  end

 # context "with a writer that contains a whitelist" do
 #    let(:scenario) {{'ENTITY_WHITELIST' => ['Teacher']}}
 #    let(:writer) {DataWriter.new(scenario)}

 #    it "will store entities that are whitelisted" do
 #      writer.get_entity_count(Teacher).should eq(0)
 #      writer << Teacher.new(3, 1971)
 #      writer << [Teacher.new(3, 1971)]
 #      writer.get_entity_count(Teacher).should eq(2)
 #    end

 #    it "will not store entities that are not whitelisted" do
 #      writer.get_entity_count(Staff).should eq(0)
 #      writer << Staff.new(1, 1969)
 #      writer << [Staff.new(1, 1969)]
 #      writer.get_entity_count(Staff).should eq(0)
 #    end
 #  end
end
