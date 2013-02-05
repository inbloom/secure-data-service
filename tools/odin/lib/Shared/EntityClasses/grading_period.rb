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

require_relative "../date_interval.rb"
require_relative "baseEntity"
require_relative "enum/GradingPeriodType.rb"
require_relative "enum/CalendarEventType.rb"

# creates grading period
class GradingPeriod < BaseEntity

  attr_accessor :name, :school_year, :ed_org_id;

  def initialize(type, year, interval, ed_org_id, calendar_dates)
  	@type           = type
  	@school_year    = year.to_s + "-" + (year+1).to_s
  	@ed_org_id      = ed_org_id
    @calendar_dates = calendar_dates
    @begin_date = interval.get_begin_date.to_s
    @end_date = interval.get_end_date.to_s
    @num_school_days = interval.get_num_school_days
  end

  def type
  	GradingPeriodType.to_string(@type)
  end

  def begin_date
  	@begin_date
  end

  def end_date
  	@end_date
  end

  def num_school_days
    @num_school_days
  end

  # this is not strictly needed, yet
  # --> this will become needed when de2170 is complete (adds calendar event type back 
  #     into natural key of calendar date identity used by reference)
  def calendar_dates
    dates = []
    @calendar_dates.each do |calendar_date|
      date          = Hash.new
      date["date"]  = calendar_date["date"]
    #  date["event"] = CalendarEventType.to_string(calendar_date["event"])
      dates         << date
    end
    dates
  end
end
