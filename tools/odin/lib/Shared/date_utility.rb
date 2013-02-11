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

require "date"
require "logger"

# Date Utility class
class DateUtility

  # generates a random date on the specified interval (inclusive of the specified years)
  def self.random_date_from_years(random, year1, year2 = year1)
  	if year1 != year2
  	  year = random_year(random, year1, year2)
  	else
  	  year = year1
  	end
  	
  	month       = random_month(random)
  	day         = random_day(random, month, year)
  	random_date = Date.new(year, month, day)
  	random_date
  end

  # generates a random date on the specified interval (inclusive of the specified dates)
  def self.random_date_on_interval(random, date1, date2 = date1)
    raise(ArgumentError, ":date1 must be before :date2") if date1 > date2
    dates = Array.new((date1..date2).step(1).to_a)
    dates[random.rand(dates.size) - 1]
  end

  # generates a random school day on the specified interval (inclusive of the specified dates)
  def self.random_school_day_on_interval(random, date1, date2 = date1)
  	raise(ArgumentError, ":date1 must be before :date2") if date1 > date2

  	if date1 == date2
      raise(ArgumentError, ":dates must not fall on a weekend") if is_weekend_day(date1)
  	  return date1
  	end

  	random_date = random_date_on_interval(random, date1, date2)

  	if is_saturday(random_date) or is_sunday(random_date)
  	  if random_date == date2
  	  	# move date backward
  	  	random_date = random_date - 1 until ((random_date.wday + 7) % 7) == 5 or random_date == date1

  	  	if is_saturday(random_date) or is_sunday(random_date)
  	  	  random_date = random_date + 1 until ((random_date.wday + 7) % 7) == 1 or random_date == date1
  	  	end
  	  else
  	  	# move date forward
  	  	random_date = random_date + 1 until ((random_date.wday + 7) % 7) == 1 or random_date == date2

  	  	if is_saturday(random_date) or is_sunday(random_date)
  	  	  random_date = random_date - 1 until ((random_date.wday + 7) % 7) == 5 or random_date == date1
  	  	end
  	  end
  	end
  	random_date
  end

  # get a random month on the specified interval
  def self.random_month(random, month1 = 1, month2 = 12)
  	random_on_interval(random, month1, month2)
  end

  # get a random day for the month (and year)
  def self.random_day(random, month, year = Date.today.year)
  	if is_leap_year(year) and month == 2
  	  return random_on_interval(random, 1, 29)
  	else
  	  return random_on_interval(random, 1, get_num_days(month))
  	end
  end

  # generates a random year on the specified interval (inclusive of the specified years)
  def self.random_year(random, year1, year2)
  	random_on_interval(random, year1, year2)
  end

  def self.random_on_interval(random, first, last)
  	random.rand(first..last)
  end

  # returns true if the specified year is a leap year, and false otherwise
  def self.is_leap_year(year)
  	(year % 4 == 0 and year % 100 != 0) or year % 400 == 0
  end

  # get the number of days for the month (excludes leap year calculation)
  def self.get_num_days(month)
    if month == 2
      return 28
    elsif month == 4 or month == 6 or month == 9 or month == 11
      return 30
    else
      return 31
    end
  end

  # get school holidays for specified year (start year for school year)
  def self.get_school_holidays(random, year)
    if year == nil
      raise(ArgumentError, ":year must must not be null")
    end

    holidays = []
    # get first monday of september (labor day)
    labor_day = Date.new(year, 9, 1)
    labor_day = labor_day + 1 until labor_day.wday == 1
    holidays << labor_day
    # get second monday of october
    columbus_day   = Date.new(year, 10, 1)
    monday_counter = 0
    until columbus_day.wday == 1 and monday_counter == 1 do 
      if columbus_day.wday == 1
        monday_counter += 1
      end
      columbus_day += 1
    end
    holidays << columbus_day
    # get second friday in november
    veterans_day   = Date.new(year, 11, 1)
    friday_counter = 0
    until veterans_day.wday == 5 and friday_counter == 1 do
      if veterans_day.wday == 5
        friday_counter += 1
      end
      veterans_day += 1
    end
    holidays << veterans_day
    # get third thursday of november (and friday)
    thanksgiving     = Date.new(year, 11, 1)
    thursday_counter = 0
    until thanksgiving.wday == 4 and thursday_counter == 3 do
      if thanksgiving.wday == 4
        thursday_counter += 1
      end
      thanksgiving += 1
    end
    holidays << thanksgiving
    holidays << thanksgiving + 1
    # get christmas eve and christmas days off
    christmas_eve = Date.new(year, 12, 24)
    christmas_day = Date.new(year, 12, 25)
    if christmas_eve.wday == 0
      christmas_eve += 1
      christmas_day = christmas_eve + 1
    elsif christmas_eve.wday == 6
      christmas_eve -= 1
      christmas_day += 1
    elsif christmas_eve.wday == 5
      christmas_day += 2
    end
    holidays << christmas_eve << christmas_day
    # get new year's eve and new year's day off
    new_years_eve = Date.new(year, 12, 31)
    new_years_day = Date.new(year+1, 1, 1)
    if new_years_eve.wday == 0
      new_years_eve += 1
      new_years_day += 1
    elsif new_years_eve.wday == 6
      new_years_eve -= 1
      new_years_day += 1
    elsif new_years_eve.wday == 5
      new_years_day += 2
    end
    holidays << new_years_eve << new_years_day
    # pick a random week in march for spring break
    monday_of_break = random_on_interval(random, 1, 3)
    monday_counter  = 0
    spring_break    = Date.new(year+1, 3, 1)
    until spring_break.wday == 1 and monday_counter == monday_of_break do 
      if spring_break.wday == 1
        monday_counter += 1
      end
      spring_break += 1
    end
    holidays << spring_break << spring_break + 1 << spring_break + 2 << spring_break + 3 << spring_break + 4
    holidays
  end

  # finds the dates to evenly spread the specified number of events between the start and end date
  def self.get_school_days_over_interval(start_date, end_date, num_events, holidays = [])
    raise(ArgumentError, ":start_date must be before :end_date") if start_date > end_date
    return [] if num_events == 0

    dates = []
    if start_date == end_date or num_events == 1
      dates << end_date
      return dates
    end
    
    num_dates           = end_date - start_date
    days_between_events = (num_dates / num_events).floor
    days_between_events = 1 if days_between_events == 0 

    # iterate from start to end date using 'days_between_events'
    (start_date..end_date).step(days_between_events) do |date|
      next if dates.size >= num_events
      if is_sunday(date)
        # if the day is sunday, shift forward one day to monday
        # -> check to make sure monday isn't a holiday (if it is, shift forward until a non-holiday date is found)
        new_date = date + 1
        new_date += 1 until !holidays.include?(new_date) 
        dates << new_date unless dates.include?(new_date)
      elsif is_saturday(date)
        # if the day is saturday, shift back one day to friday
        # -> check to make sure friday isn't a holiday (if it is, shift backward until a non-holiday date is found)
        new_date = date - 1
        new_date -= 1 until !holidays.include?(new_date) 
        dates << new_date unless dates.include?(new_date)
      else
        # check to see if the day is a holiday
        # -> if it is, shift forward to a non-holiday date
        if holidays.include?(date)
          new_date = date
          new_date += 1 until !holidays.include?(new_date) 
          dates << new_date unless dates.include?(new_date)
        else
          dates << date unless dates.include?(date)
        end
      end
    end
    dates
  end

  # checks if the specified 'date' is a weekend day
  # -> returns false if the 'date' is a week day
  # -> returns true if the 'date' is a weekend day
  def self.is_weekend_day(date)
    is_saturday(date) or is_sunday(date)
  end

  # returns true if the specified day is a Saturday, and false otherwise
  def self.is_saturday(date)
    date.wday == 6
  end

  # returns true if the specified day is a Sunday, and false otherwise
  def self.is_sunday(date)
    date.wday == 0
  end

  # add school days function?
end
