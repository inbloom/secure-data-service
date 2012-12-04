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
require_relative "../lib/Shared/EntityClasses/enum/StaffClassificationType.rb"

# specifications for base data writer
describe "DataWriter" do

  before(:each) do
    @yaml   = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @random = Random.new(@yaml['seed'])
    @writer = DataWriter.new
  end

  after(:each) do
    @writer.finalize
  end

  describe "--> initialization" do
    it "will return an empty hash when requesting @counts after initialization" do
      @writer.get_counts_for_all_entities.empty?.should be_true
    end

    it "will return nil when requesting type for an entity that is not in @entities" do
      @writer.get_entities(:state_education_agency).should be_nil
    end

    it "will return zero when requesting count for an entity that is not in @counts" do
      @writer.get_entity_count(:state_education_agency).should eq(0)
    end
  end

  describe "--> creating entities" do
    it "will store a state education agency in-memory when handling call to create one" do
      @writer.get_entity_count(:state_education_agency).should eq(0)
      @writer.create_state_education_agency(@random, 1)
      @writer.get_entity_count(:state_education_agency).should_not be_nil
      @writer.get_entity_count(:state_education_agency).should eq(1)
    end

    it "will store a local education agency in-memory when handling call to create one" do
      @writer.get_entity_count(:local_education_agency).should eq(0)
      @writer.create_local_education_agency(@random, 2, 1)
      @writer.get_entity_count(:local_education_agency).should_not be_nil
      @writer.get_entity_count(:local_education_agency).should eq(1)
    end

    it "will store a school in-memory when handling call to create one" do
      @writer.get_entity_count(:school).should eq(0)
      @writer.create_school(@random, 3, 2, "elementary")
      @writer.get_entity_count(:school).should_not be_nil
      @writer.get_entity_count(:school).should eq(1)
    end

    it "will store a course in-memory when handling call to create one" do
      @writer.get_entity_count(:course).should eq(0)
      @writer.create_course(@random, 1, "title", 1)
      @writer.get_entity_count(:course).should_not be_nil
      @writer.get_entity_count(:course).should eq(1)
    end

    it "will store a program in-memory when handling call to create one" do
      @writer.get_entity_count(:program).should eq(0)
      @writer.create_program(@random, 1)
      @writer.get_entity_count(:program).should_not be_nil
      @writer.get_entity_count(:program).should eq(1)
    end

    it "will store a session in-memory when handling call to create one" do
      grading_periods = []
      start_date = DateUtility.random_school_day_on_interval(@random, Date.new(2012, 8, 25), Date.new(2012, 9, 10))
      interval   = DateInterval.create_using_start_and_num_days(@random, start_date, 180)
      @writer.get_entity_count(:session).should eq(0)
      @writer.create_session("name", 2012, "term", interval, 2, grading_periods)
      @writer.get_entity_count(:session).should_not be_nil
      @writer.get_entity_count(:session).should eq(1)
    end

    it "will store a grading period in-memory when handling call to create one" do
      calendar_dates = []
      start_date = DateUtility.random_school_day_on_interval(@random, Date.new(2012, 8, 25), Date.new(2012, 9, 10))
      interval   = DateInterval.create_using_start_and_num_days(@random, start_date, 180)
      @writer.get_entity_count(:grading_period).should eq(0)
      @writer.create_grading_period(:END_OF_YEAR, 2012, interval, 2, calendar_dates)
      @writer.get_entity_count(:grading_period).should_not be_nil
      @writer.get_entity_count(:grading_period).should eq(1)
    end

    it "will store a calendar date in-memory when handling call to create one" do
      @writer.get_entity_count(:calendar_date).should eq(0)
      @writer.create_calendar_date(Date.new(2012, 9, 1), :INSTRUCTIONAL_DAY, 1)
      @writer.get_entity_count(:calendar_date).should_not be_nil
      @writer.get_entity_count(:calendar_date).should eq(1)
    end

    it "will store a course offering in-memory when handling call to create one" do
      @writer.get_entity_count(:course_offering).should eq(0)
      @writer.create_course_offering(1, "title", "3", {}, {})
      @writer.get_entity_count(:course_offering).should_not be_nil
      @writer.get_entity_count(:course_offering).should eq(1)
    end

    it "will store a staff in-memory when handling call to create one" do
      @writer.get_entity_count(:staff).should eq(0)
      @writer.create_staff(1, 1969)
      @writer.get_entity_count(:staff).should_not be_nil
      @writer.get_entity_count(:staff).should eq(1)
    end

    it "will store a staff education organization assignment association in-memory when handling call to create one" do
      @writer.get_entity_count(:staff_ed_org_assignment_association).should eq(0)
      @writer.create_staff_ed_org_assignment_association(1, 1, :PRINCIPAL, "title", Date.new(2009, 9, 4))
      @writer.get_entity_count(:staff_ed_org_assignment_association).should_not be_nil
      @writer.get_entity_count(:staff_ed_org_assignment_association).should eq(1)
    end
  end
end