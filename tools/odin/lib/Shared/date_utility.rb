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

require "date"
require "logger"

# Date Utility class
class DateUtility

  def initialize
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO
  end

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
  	# if date1 is after date2 --> raise illegal argument error

    if date1.year == date2.year
      year  = date1.year
      if date1.month == date2.month
      	month = date1.month
      	if date1.day == date2.day
      	  day = date1.day
      	else
      	  day = random_on_interval(random, date1.day, date2.day)
      	end
      else
      	month = random_month(random, date1.month, date2.month)
      	day   = random_day(random, month, year)
      end
    else
      year  = random_year(random, date1.year, date2.year)
      month = random_month(random)
      day   = random_day(random, month, year)
    end
  	
  	random_date = Date.new(year, month, day)
  	random_date
  end

  # generates a random school day on the specified interval (inclusive of the specified dates)
  def self.random_school_day_on_interval(random, date1, date2 = date1)
  	# if date1 is after date2 --> raise illegal argument error

  	if date1 == date2
  	  # check to make sure its not a weekend day --> raise illegal argument error
  	  return date1
  	end

  	random_date = random_date_on_interval(random, date1, date2)

  	if random_date.wday == 0 or random_date.wday == 6
  	  if random_date == date2
  	  	# move date backward
  	  	random_date = random_date - 1 until ((random_date.wday + 7) % 7) == 5 or random_date == date1

  	  	if random_date.wday == 0 or random_date.wday == 6
  	  	  random_date = random_date + 1 until ((random_date.wday + 7) % 7) == 1 or random_date == date1
  	  	end
  	  else
  	  	# move date forward
  	  	random_date = random_date + 1 until ((random_date.wday + 7) % 7) == 1 or random_date == date2

  	  	if random_date.wday == 0 or random_date.wday == 6
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
end