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

require 'date'

require_relative 'spec_helper'
require_relative '../lib/WorldDefinition/attendance_factory'
require_relative '../lib/Shared/EntityClasses/attendance_event'
require_relative '../lib/Shared/EntityClasses/session'
require_relative '../lib/Shared/EntityClasses/enum/SchoolTerm'
require_relative '../lib/Shared/date_interval'

# specification for the attendance event factory
describe "AttendanceFactory" do

  let(:random) { Random.new(1234567890) }
  let(:start_date) { Date.new(2012, 12, 17) }
  let(:end_date) { Date.new(2012, 12, 28) }
  let(:scenario) { {'WRITE_SECTION_ATTENDANCE' => false, 'DAILY_ATTENDANCE_PERCENTAGES' => {
      "elementary" => {"absent" => 6, "tardy" => 1},
      "middle" => {"absent" => 8, "tardy" => 3},
      "high" => {"absent" => 10, "tardy" => 5}
    }} 
  }
  let(:interval) { DateInterval.create_using_start_and_end_dates(random, start_date, end_date) }
  let(:session) { {"interval" => interval} }
  let(:factory) { AttendanceFactory.new(scenario) }

  describe "#attendances" do
    describe "--> scenario calling for 100% present rate (with exception only attendance set to false)" do
      it "will produce attendance event work orders for the specified student over the date interval with no absent events" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = false
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['absent'] = 0
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy']  = 0
        
        events = factory.generate_attendance_events(random, "student123", "school123", session, "elementary")
        present_events = events.select {|event| event.category == :PRESENT }
        absent_events  = events.select {|event| event.category == :ABSENT or event.category == :EXCUSED_ABSENCE or event.category == :UNEXCUSED_ABSENCE }
        
        events.size.should eq 8
        present_events.size.should eq 8
        absent_events.size.should eq 0
      end

      it "will not produce any event work orders that fall on holidays" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = false
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['absent'] = 0
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy']  = 0

        events = factory.generate_attendance_events(random, "student123", "school123", session, "elementary")
        holiday_events = events.select {|event| event.event_date == Date.new(2012, 12, 24) or event.event_date == Date.new(2012, 12, 25) }
        events.size.should eq 8
        holiday_events.size.should eq 0
      end
    end

    describe "--> scenario calling for 100% present rate (with exception only attendance set to true)" do
      it "will produce no attendance event work orders (empty array returned)" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = true
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['absent'] = 0
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy']  = 0

        events = factory.generate_attendance_events(random, "student123", "school123", session, "elementary")
        events.size.should eq 0
      end
    end

    describe "--> scenario calling for 100% absence rate (exception only attendance flag will not matter)" do
      it "will produce attendance event work orders for the specified student over the date interval with no present events" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = false
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['absent'] = 100
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy']  = 0
        
        events = factory.generate_attendance_events(random, "student123", "school123", session, "elementary")
        present_events = events.select {|event| event.category == :PRESENT }
        absent_events  = events.select {|event| event.category == :ABSENT or event.category == :EXCUSED_ABSENCE or event.category == :UNEXCUSED_ABSENCE }
        
        events.size.should eq 8
        present_events.size.should eq 0
        absent_events.size.should eq 8
      end

      it "will not produce any event work orders that fall on holidays" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = false
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['absent'] = 100
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy']  = 0
        
        events = factory.generate_attendance_events(random, "student123", "school123", session, "elementary")
        holiday_events = events.select {|event| event.event_date == Date.new(2012, 12, 24) or event.event_date == Date.new(2012, 12, 25) }
        events.size.should eq 8
        holiday_events.size.should eq 0
      end
    end

    describe "--> breakdown of present, absent, and tardy rates" do
      let(:start_date) { Date.new(2012, 9, 4) }
      let(:scenario) { {'EXCEPTION_ONLY_ATTENDANCE' => false, 
        'WRITE_SECTION_ATTENDANCE' => false, 
        'DAILY_ATTENDANCE_PERCENTAGES' => {
          "elementary" => {"absent" => 6, "tardy" => 1},
        }} 
      }
      let(:interval) { DateInterval.create_using_start_and_num_days(random, start_date, 100) }
      let(:session)  { {"interval" => interval} }
      let(:events)   { factory.generate_attendance_events(random, "student123", "school123", session, "elementary") }
      
      it "will produce attendance event work orders that match the breakdown" do
        events.size.should eq 100
        present_events = events.select { |event| event.category == :PRESENT }
        absent_events  = events.select { |event| event.category == :ABSENT or event.category == :EXCUSED_ABSENCE or event.category == :UNEXCUSED_ABSENCE }
        tardy_events   = events.select { |event| event.category == :TARDY   }
        present_events.size.should eq 96
        absent_events.size.should eq 4
        tardy_events.size.should eq 0
      end

      it "will not produce any event work orders that fall on holidays" do
        holiday_events = events.select {|event| session['interval'].get_holidays.include?(event.event_date) }
        holiday_events.size.should eq 0
      end
    end
  end

  describe "#negative testing" do
    describe "--> failing to find a property" do
      it "(exception-only attendance property) will exit with an argument error exception" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = nil
        expect { factory.generate_attendance_events(random, "student123", "school123", session, "elementary") }.to raise_exception(ArgumentError)
      end

      it "(daily attendance percentages: tardy property) will exit with an argument error exception" do
        scenario['DAILY_ATTENDANCE_PERCENTAGES']['elementary']['tardy'] = nil
        expect { factory.generate_attendance_events(random, "student123", "school123", session, "elementary") }.to raise_exception(ArgumentError)
      end
    end

    describe "--> failing to specify a session" do
      it "will return an empty array" do
        scenario['EXCEPTION_ONLY_ATTENDANCE'] = false
        factory.generate_attendance_events(random, "student123", "school123", nil, "elementary").should be_empty
      end
    end
  end
end
