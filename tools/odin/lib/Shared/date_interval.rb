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
require 'logger'

require_relative 'date_utility.rb'

# Date Interval class
class DateInterval

  # class method for creating date interval from start date and number of instructional days
  def self.create_using_start_and_num_days(random, start_date, num_instructional_days, only_school_days = true, with_holidays = true)
    raise(ArgumentError, ":num_instructional_days cannot be less than or equal to zero.") if num_instructional_days <= 0

    if with_holidays
      holidays = DateUtility.get_school_holidays(random, start_date.year)
      end_date = compute_end_date(start_date, num_instructional_days, only_school_days, holidays)
    else
      holidays = []
      end_date = compute_end_date(start_date, num_instructional_days, only_school_days, holidays)
    end
    DateInterval.new(start_date, end_date, num_instructional_days, holidays, only_school_days)
  end

  # class method for creating date interval from start date and end date
  def self.create_using_start_and_end_dates(random, start_date, end_date, only_school_days = true, with_holidays = true)
    raise(ArgumentError, ":start_date must be before (or equal to) :end_date.") if start_date > end_date

    if only_school_days and (start_date.wday == 0 or start_date.wday == 6)
      raise(ArgumentError, "creating an interval of one day with :only_school_days set to true cannot start on a weekend day.")
    end

    if with_holidays
      holidays               = DateUtility.get_school_holidays(random, start_date.year)
      num_instructional_days = compute_num_instructional_days(start_date, end_date, only_school_days, holidays)
    else
      holidays               = []
      num_instructional_days = compute_num_instructional_days(start_date, end_date, only_school_days, holidays)
    end
    DateInterval.new(start_date, end_date, num_instructional_days, holidays, only_school_days)
  end

  # computes the end date for the date interval
  def self.compute_end_date(start_date, num_instructional_days, only_school_days, holidays)
    end_date = Date.new(start_date.year, start_date.month, start_date.day)
    duration = num_instructional_days
    while duration > 0 do
      if !holidays.include?(end_date)
        if only_school_days 
          if end_date.wday != 0 and end_date.wday != 6
            duration = duration - 1
          end
        else
          duration = duration - 1
        end
      end
      end_date = end_date + 1
    end
    # need to move end_date back one day --> final iteration increments it one past correct date
    end_date = end_date - 1
    end_date
  end

  # computes the number of instructional days for the date interval
  def self.compute_num_instructional_days(start_date, end_date, only_school_days, holidays)
    num_instructional_days = 0
    (start_date..end_date).step(1) do |date|
      if !holidays.include?(date)
        if only_school_days
          if date.wday != 0 and date.wday != 6
            num_instructional_days += 1
          end
        else
          num_instructional_days += 1
        end
      end
    end
    num_instructional_days
  end

  # default constructor
  def initialize(start_date, end_date, num_instructional_days, holidays = [], only_school_days = true)
    @start_date             = start_date
    @end_date               = end_date
    @num_instructional_days = num_instructional_days
    @only_school_days       = only_school_days
    @holidays               = holidays
  end

  def get_begin_date
    @start_date
  end

  def get_end_date
    @end_date
  end

  def get_num_school_days
    @num_instructional_days
  end

  def get_holidays
    @holidays
  end

  def random_day(rand)
    #TODO only use school days
    @start_date + rand.rand(@end_date - @start_date)
  end
end
