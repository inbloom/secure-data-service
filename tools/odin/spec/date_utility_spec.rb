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

require 'yaml'

require_relative '../lib/Shared/date_utility.rb'

describe "DateUtility" do

  before(:all) do
    @scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
    @random = Random.new(@scenario['seed'])
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
  end

  describe "--> requests for sets of dates" do
  	describe "--> requesting set of holidays for a school year" do
    end
    describe "--> requesting distribution of dates over an interval" do
    end
  end

  describe "--> requests for modifying or translating dates" do
  	describe "--> requesting translation of date by specified number of school days" do
  	end
  end
end