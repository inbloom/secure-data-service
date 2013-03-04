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

require 'yaml'

require_relative '../lib/Shared/date_utility.rb'

# specifications for date utility
describe "DateUtility" do
  before(:each) do
    @yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
    @random = Random.new(@yaml['seed'])
  end

  after(:each) do
    @scenario = nil
    @yaml = nil
    @random = nil
  end

  describe "--> requests for month and day utilities" do
      describe "--> requesting random month" do
        it "will return valid month" do
        for i in 1..100
            month = DateUtility.random_month(@random)
            fail if month > 12 or month < 1
          end
      end
      end

      describe "--> requesting random day" do
        it "will return a valid day within the requested month" do
            for i in 1..100
              day = DateUtility.random_day(@random, 1) 
              fail if day > 31 or day < 1
            end

            for i in 1..100
              day = DateUtility.random_day(@random, 2, 2011)
              fail if day > 28 or day < 1
            end
        
        for i in 1..100
              day = DateUtility.random_day(@random, 2, 2012)
              fail if day > 29 or day < 1
            end

        for i in 1..100
              day = DateUtility.random_day(@random, 4)
          fail if day > 30 or day < 1
            end
        end
      end

      describe "--> requesting leap year status" do
        it "will return true when the year is a leap year, and false otherwise" do
            fail if DateUtility.is_leap_year(2000) != true
            fail if DateUtility.is_leap_year(2100) != false
            fail if DateUtility.is_leap_year(2200) != false
            fail if DateUtility.is_leap_year(2300) != false
            fail if DateUtility.is_leap_year(2400) != true
            fail if DateUtility.is_leap_year(2401) != false
            fail if DateUtility.is_leap_year(2402) != false
            fail if DateUtility.is_leap_year(2403) != false
            fail if DateUtility.is_leap_year(2404) != true
        end
      end
  end

  describe "--> requests for random dates" do
      describe "--> requesting random date in a single year" do
        it "will return a date for the specified year" do
            for i in 1..100
              my_random_date = DateUtility.random_date_from_years(@random, i)
              fail if my_random_date.year != i
              fail if my_random_date.month > 12 or my_random_date.month < 1
            end
        end
      end
      describe "--> requesting random date for an interval of years" do
        it "will return a date that falls on the interval of specified years" do
            for i in 1..100
              my_random_date = DateUtility.random_date_from_years(@random, i, i + 100)
              fail if my_random_date.year > i + 100 or my_random_date.year < i
              fail if my_random_date.month > 12 or my_random_date.month < 1
            end
        end
      end
      describe "--> requesting random date for an interval of dates" do
        it "will return a date that falls on the interval of specified dates" do
            date1 = Date.new(Date.today.year, 1, 1)
            date2 = Date.new(Date.today.year, 12, 31)
            for i in 1..100
              my_random_date = DateUtility.random_date_on_interval(@random, date1, date2)
              fail if my_random_date > date2 or my_random_date < date1
            end
        end
      end
      describe "--> requesting random school day for an interval of dates" do
        it "will return a school day (not saturday or sunday) that falls on the interval of specified dates" do
            date1 = Date.new(2012, 12, 9)
            date2 = Date.new(2012, 12, 16)
            for i in 1..25
              my_random_date = DateUtility.random_school_day_on_interval(@random, date1, date2)
              fail if my_random_date > date2 or my_random_date < date1
              fail if my_random_date.wday == 0 or my_random_date.wday == 6
            end
        end
      end
    describe "--> checking if user-specified date is a weekend day" do
      it "will return true if the specified date is a saturday or sunday" do
        date1 = Date.new(2012, 12, 15)
        date2 = Date.new(2012, 12, 16)
        DateUtility.is_weekend_day(date1).should be_true
        DateUtility.is_weekend_day(date2).should be_true
      end
      it "will return false if the specified date is not a saturday or sunday" do
        date1 = Date.new(2012, 12, 17)
        date2 = Date.new(2012, 12, 18)
        date3 = Date.new(2012, 12, 19)
        date4 = Date.new(2012, 12, 20)
        date5 = Date.new(2012, 12, 21)
        DateUtility.is_weekend_day(date1).should be_false
        DateUtility.is_weekend_day(date2).should be_false
        DateUtility.is_weekend_day(date3).should be_false
        DateUtility.is_weekend_day(date4).should be_false
        DateUtility.is_weekend_day(date5).should be_false
      end
    end
  end

  describe "--> requests for sets of dates" do
      describe "--> requesting set of holidays for a school year" do
      it "will return an array of holidays for the specified school year" do
        holidays = DateUtility.get_school_holidays(@random, 2011)
        fail if !holidays.include? Date.new(2011, 9, 5)
        fail if !holidays.include? Date.new(2011, 10, 10)
        fail if !holidays.include? Date.new(2011, 11, 11)
        fail if !holidays.include? Date.new(2011, 11, 24)
        fail if !holidays.include? Date.new(2011, 11, 25)
        fail if !holidays.include? Date.new(2011, 12, 23)
        fail if !holidays.include? Date.new(2011, 12, 26)
        fail if !holidays.include? Date.new(2011, 12, 30)
        fail if !holidays.include? Date.new(2012, 1, 2)
        fail if !holidays.include? Date.new(2012, 3, 12)
        fail if !holidays.include? Date.new(2012, 3, 13)
        fail if !holidays.include? Date.new(2012, 3, 14)
        fail if !holidays.include? Date.new(2012, 3, 15)
        fail if !holidays.include? Date.new(2012, 3, 16)
        
        holidays = nil
        holidays = DateUtility.get_school_holidays(@random, 2012)
        fail if !holidays.include? Date.new(2012, 9, 3)
        fail if !holidays.include? Date.new(2012, 10, 8)
        fail if !holidays.include? Date.new(2012, 11, 9)
        fail if !holidays.include? Date.new(2012, 11, 22)
        fail if !holidays.include? Date.new(2012, 11, 23)
        fail if !holidays.include? Date.new(2012, 12, 24)
        fail if !holidays.include? Date.new(2012, 12, 25)
        fail if !holidays.include? Date.new(2012, 12, 31)
        fail if !holidays.include? Date.new(2013, 1, 1)
        fail if !holidays.include? Date.new(2013, 3, 11)
        fail if !holidays.include? Date.new(2013, 3, 12)
        fail if !holidays.include? Date.new(2013, 3, 13)
        fail if !holidays.include? Date.new(2013, 3, 14)
        fail if !holidays.include? Date.new(2013, 3, 15)
      end
    end

    describe "--> requesting distribution of dates over an interval" do
      it "will handle a single event for any size interval" do
        monday = Date.new(2012, 3, 26)
        friday = Date.new(2012, 3, 30)
        fail if DateUtility.get_school_days_over_interval(monday, friday, 1) != [Date.new(2012, 3, 30)] 
      end

      it "will return an evenly spread distribution of dates for an interval" do
        monday = Date.new(2012, 3, 26)
        friday = Date.new(2012, 3, 30)
        days = DateUtility.get_school_days_over_interval(monday, friday, 3)
        fail if days.size != 3
        days.each do |day|
          fail if day < monday or day > friday
          fail if day.wday == 0 or day.wday == 6
        end
      end
      
      it "will return an evenly spread distribution of dates for an interval" do
        first_day = Date.new(2012, 3, 1)
        last_day  = Date.new(2012, 3, 30)
        days = DateUtility.get_school_days_over_interval(first_day, last_day, 10)
        fail if days.size != 10
        days.each do |day|
          fail if day < first_day or day > last_day
          fail if day.wday == 0 or day.wday == 6
        end
      end

      it "will handle start date equivalent to end date for an interval" do
        monday = Date.new(2012, 3, 26)
        fail if DateUtility.get_school_days_over_interval(monday, monday, 1) != [Date.new(2012, 3, 26)] 
      end

      it "will return an empty array when zero events are requested" do
        friday = Date.new(2012, 3, 23)
        monday = Date.new(2012, 3, 26)
        DateUtility.get_school_days_over_interval(friday, monday, 0).should be_empty
      end

      it "will raise an exception when the start date is after the end date" do
        friday = Date.new(2012, 3, 23)
        monday = Date.new(2012, 3, 26)
        expect { DateUtility.get_school_days_over_interval(monday, friday, 1) }.to raise_exception(ArgumentError)
      end
    end
  end
end
