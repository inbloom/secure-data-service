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

require_relative '../lib/Shared/date_interval'
require_relative '../lib/Shared/date_utility'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType'
require_relative '../lib/Shared/EntityClasses/enum/SchoolTerm'
require_relative '../lib/Shared/EntityClasses/session'
require_relative '../lib/Shared/util'
require_relative '../lib/WorldDefinition/gradebook_entry_factory'
require_relative 'spec_helper'

# specifications for the grade book entry factory
describe "GradebookEntryFactory" do
  describe "--> requesting gradebook entries" do
    let(:random)         { Random.new(1234567890) }
    let(:year)           { 2012 }
    let(:start_date)     { Date.new(year, 12, 3) }
    let(:end_date)       { Date.new(year, 12, 28) }
    let(:interval)       { DateInterval.create_using_start_and_end_dates(random, start_date, end_date) }
    let(:grading_period) { {"interval" => interval, "year" => year} }
    let(:session)        { {"interval" => interval, "grading_periods" => [grading_period]} }
    let(:section)        { {:ed_org_id => "school123", :unique_section_code => 1} }
  
    let(:scenario) { {'GRADEBOOK_ENTRIES_BY_GRADE' => {
        "First grade" => { "Homework" => {"min" => 8, "max" => 16} },
        "Second grade" => { "Homework" => {"min" => 12, "max" => 24}, "Quiz" => {"min" => 2, "max" => 4} }
      }} 
    }
    let(:factory) { GradebookEntryFactory.new(scenario) }

    it "will create the specified number of gradebook entry work orders within the session for the requested grade" do
      grade           = :SECOND_GRADE
      orders          = factory.generate_entries(random, grade, session, section)
      homework_orders = orders.select { |order| order[:gbe_type] == "Homework" }
      quiz_orders     = orders.select { |order| order[:gbe_type] == "Quiz" }
      out_of_bounds   = orders.select { |order| order[:date_assigned] < start_date or order[:date_assigned] > end_date } 

      orders.size.should eq(21)
      homework_orders.size.should eq(17)
      quiz_orders.size.should eq(4)
      out_of_bounds.size.should eq(0)
    end

    it "will not create gradebook entry work orders that fall on holidays" do
      grade          = :SECOND_GRADE
      orders         = factory.generate_entries(random, grade, session, section)
      holiday_orders = orders.select { |order| order[:date_assigned] == Date.new(2012, 12, 24) || order[:date_assigned] == Date.new(2012, 12, 25) }

      orders.size.should eq(21)
      holiday_orders.size.should eq(0)
    end

    it "will not create unspecified gradebook entry work orders (specifically when a grade does not have Quiz, there should be no Quiz entries)" do      
      grade           = :FIRST_GRADE
      orders          = factory.generate_entries(random, grade, session, section)
      homework_orders = orders.select { |order| order[:gbe_type] == "Homework" }
      quiz_orders     = orders.select { |order| order[:gbe_type] == "Quiz" }
      out_of_bounds   = orders.select { |order| order[:date_assigned] < start_date or order[:date_assigned] > end_date } 

      orders.size.should eq(13)
      homework_orders.size.should eq(orders.size)
      quiz_orders.size.should eq(0)
      out_of_bounds.size.should eq(0)
    end
  end

  describe "--> requesting (edge case) gradebook entries" do
    let(:random)         { Random.new(1234567890) }
    let(:year)           { 2012 } 
    let(:start_date)     { Date.new(year, 12, 3) }
    let(:end_date)       { Date.new(year, 12, 28) }
    let(:interval)       { DateInterval.create_using_start_and_end_dates(random, start_date, end_date) }
    let(:grading_period) { {"interval" => interval, "year" => year} }
    let(:session)        { {"interval" => interval, "grading_periods" => [grading_period]} }
    let(:section)        { {:ed_org_id => "school123", :unique_section_code => 1} }
  
    let(:scenario) { {'GRADEBOOK_ENTRIES_BY_GRADE' => {
        "First grade" => { "Homework" => {"min" => 1, "max" => 1} },
        "Second grade" => { "Homework" => {"min" => 0, "max" => 0} }
      }} 
    }
    let(:factory) { GradebookEntryFactory.new(scenario) }

    it "will handle the case where one gradebook entry is specified by returning end date of session" do
      grade           = :FIRST_GRADE
      orders          = factory.generate_entries(random, grade, session, section)
      homework_orders = orders.select { |order| order[:gbe_type] == "Homework" }
      out_of_bounds   = orders.select { |order| order[:date_assigned] < start_date or order[:date_assigned] > end_date } 

      orders.size.should eq(1)
      homework_orders.size.should eq(orders.size)
      out_of_bounds.size.should eq(0)

      homework_order  = homework_orders.pop
      homework_order[:date_assigned].should eq(end_date)
    end

    it "will return an empty array when zero gradebook entries are specified" do
      grade           = :SECOND_GRADE
      orders          = factory.generate_entries(random, grade, session, section)
      orders.size.should eq(0)
    end
  end

  describe "--> when a property" do
    let(:grade) { :SECOND_GRADE }
    let(:random) { Random.new(1234567890) }
    let(:start_date) { Date.new(2012, 12, 3) }
    let(:end_date) { Date.new(2012, 12, 28) }
    let(:interval) { DateInterval.create_using_start_and_end_dates(random, start_date, end_date) }
    let(:session) { {"interval" => interval} }
    let(:section) { {:ed_org_id => "school123", :unique_section_code => 1} }
    
    it "(scenario) is missing --> raise error message" do
      scenario = nil
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end

    it "(gradebook entry breakdown) is missing --> raise error message" do
      scenario = {}
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end

    it "(requested grade) is missing --> raise error message" do
      scenario = { 'GRADEBOOK_ENTRIES_BY_GRADE' => {"Third grade" => { "Homework" => {"min" => 12, "max" => 24}}}}
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end

    it "(assignment key) is missing --> raise error message" do
      scenario = { 'GRADEBOOK_ENTRIES_BY_GRADE' => {"" => { "Homework" => {"min" => 12, "max" => 24}}}}
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end

    it "(minimum number of assignments) is missing --> raise error message" do
      scenario = { 'GRADEBOOK_ENTRIES_BY_GRADE' => {"Second grade" => { "Homework" => {"max" => 24}}}}
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end

    it "(maximum number of assignments) is missing --> raise error message" do
      scenario = { 'GRADEBOOK_ENTRIES_BY_GRADE' => {"Second grade" => { "Homework" => {"min" => 12}}}}
      factory  = GradebookEntryFactory.new(scenario)
      expect { factory.generate_entries(random, grade, session, section) }.to raise_exception(ArgumentError)
    end
  end
end
