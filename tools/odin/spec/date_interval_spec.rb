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
require_relative '../lib/Shared/date_interval.rb'

# specifications for date interval
describe "DateInterval" do
  before(:each) do
    @scenario = YAML.load_file(File.join(File.dirname(__FILE__),'../config.yml'))
    @yaml = YAML.load_file(File.join(File.dirname(__FILE__),'../scenarios/10students'))
    @random = Random.new(@scenario['seed'])
  end

  after(:each) do
    @scenario = nil
    @yaml = nil
    @random = nil
  end

  describe "--> creating interval using start date and number of instructional days" do
  	it "will create an interval with the correct end date using a single instructional day, and starting on a week day (only_school_days = true, with_holidays = true)" do
  	  start_date             = Date.new(2012, 12, 16)
  	  num_instructional_days = 1
  	  only_school_days       = true
  	  with_holidays          = true
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2012, 12, 17)
  	end

  	it "will create an interval with the correct end date using a single instructional day, and starting on a weekend day (only_school_days = true, with_holidays = true)" do
  	  start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 1
  	  only_school_days       = true
  	  with_holidays          = true
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2012, 12, 17)
  	end

  	it "will create an interval with the correct end date using multiple instructional days, starting on a week day (only_school_days = true, with_holidays = true)" do
  	  start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 10
  	  only_school_days       = true
  	  with_holidays          = true
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2013, 1, 3)
  	end

    it "will create an interval with the correct end date using multiple instructional days, starting on a week day (only_school_days = false, with_holidays = true)" do
      start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 10
  	  only_school_days       = false
  	  with_holidays          = true
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2012, 12, 28)
  	end

  	it "will create an interval with the correct end date using multiple instructional days, starting on a week day (only_school_days = true, with_holidays = false)" do
  	  start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 10
  	  only_school_days       = true
  	  with_holidays          = false
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2012, 12, 28)
  	end

  	it "will create an interval with the correct end date using multiple instructional days, starting on a week day (only_school_days = false, with_holidays = false)" do
  	  start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 10
  	  only_school_days       = false
  	  with_holidays          = false
  	  interval = DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days, only_school_days, with_holidays)
  	  fail if interval.get_end_date != Date.new(2012, 12, 26)
  	end

  	it "will raise an error if zero or negative number of instructional days is specified" do
  	  start_date             = Date.new(2012, 12, 17)
  	  num_instructional_days = 0
  	  expect { DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days) }.to raise_exception(ArgumentError)
  	  
  	  num_instructional_days = -10
  	  expect { DateInterval.create_using_start_and_num_days(@random, start_date, num_instructional_days) }.to raise_exception(ArgumentError)
  	end
  end

  describe "--> creating interval using start date and end date" do
  	it "will handle the case where start date is equivalent to end date and date is a week day (only_school_days = true, with_holidays = true)" do
      start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2012, 12, 17)
  	  only_school_days = true
  	  with_holidays    = true
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 1
  	end

  	it "will handle the case where start date is equivalent to end date and date is a weekend day (only_school_days = false, with_holidays = true)" do
  	  start_date       = Date.new(2012, 12, 16)
  	  end_date         = Date.new(2012, 12, 16)
  	  only_school_days = false
  	  with_holidays    = true
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 1
  	end

  	it "will create an interval and compute the correct number of instructional days (only_school_days = true, with_holidays = true)" do
  	  start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2013, 1, 3)
  	  only_school_days = true
  	  with_holidays    = true
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 10
  	end

  	it "will create an interval and compute the correct number of instructional days (only_school_days = false, with_holidays = true)" do
  	  start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2012, 12, 28)
  	  only_school_days = false
  	  with_holidays    = true
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 10
  	end

  	it "will create an interval and compute the correct number of instructional days (only_school_days = true, with_holidays = false)" do
  	  start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2012, 12, 28)
  	  only_school_days = true
  	  with_holidays    = false
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 10
  	end

  	it "will create an interval and compute the correct number of instructional days (only_school_days = false, with_holidays = false)" do
  	  start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2012, 12, 26)
  	  only_school_days = false
  	  with_holidays    = false
  	  interval = DateInterval.create_using_start_and_end_dates(@random, start_date, end_date, only_school_days, with_holidays)
  	  fail if interval.get_num_school_days != 10
  	end

  	it "will raise an error where start date is equivalent to end date and date is a weekend day (only_school_days = true, with_holidays = true)" do
  	  start_date       = Date.new(2012, 12, 16)
  	  end_date         = Date.new(2012, 12, 16)
  	  expect { DateInterval.create_using_start_and_end_dates(@random, start_date, end_date) }.to raise_exception(ArgumentError)
  	end

  	it "will raise an error if the end date is before the start date" do
  	  start_date       = Date.new(2012, 12, 17)
  	  end_date         = Date.new(2012, 12, 16)
  	  expect { DateInterval.create_using_start_and_end_dates(@random, start_date, end_date) }.to raise_exception(ArgumentError)
  	end
  end
end
