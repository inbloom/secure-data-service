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

require_relative '../Shared/data_utility'
require_relative '../Shared/EntityClasses/attendance_event'
require_relative '../Shared/EntityClasses/enum/AttendanceEventCategory'

# factory for creating attendance events for students
class AttendanceFactory

  def initialize(scenario)
    @scenario = scenario
    #TODO Decide whether to use local random number generator
    @attendanceRandom = Random.new(49852)
  end

  # generate attendance event work orders for the specified 'student' at the specified education organization
  # over the interval of dates specified by the 'session'
  # -> type refers to education organization type (elementary, middle, or high school)
  # -> type is required to perform lookup on daily attendance percentages breakdown (for absence and tardiness)
  def generate_attendance_events(prng, student, ed_org_id, session, type, student_section_association)
    attendance_events = []
    unless session.nil?
      raise(ArgumentError, "Missing configuration parameter related to exception-only attendance.") if @scenario['EXCEPTION_ONLY_ATTENDANCE'].nil?
      exceptions_only = @scenario['EXCEPTION_ONLY_ATTENDANCE']

      begin_date = session['interval'].get_begin_date
      end_date   = session['interval'].get_end_date
      holidays   = session['interval'].get_holidays
      year       = session['year']
      (begin_date..end_date).step(1) do |current_date|
        next if DateUtility.is_weekend_day(current_date) or is_holiday(current_date, holidays)
        categories = get_attendance_categories(@attendanceRandom, type)
        categories.each do |category|
          reason   = get_attendance_event_reason(category) if category != :PRESENT
          attendance_events << AttendanceEvent.new(prng.seed, student, ed_org_id, year, current_date, category, student_section_association,
                                                   session, reason) unless category == :PRESENT and exceptions_only
        end
      end
    end
    attendance_events
  end

  def get_attendance_categories(prng, type)
    present, absent, tardy, excused, unexcused, early_dep, iss, oss = get_attendance_percentages(type)
    categoryEvents = []
    categoryEvents.push(:PRESENT) if prng.rand(1..100) <= present
    categoryEvents.push(:ABSENT) if prng.rand(1..100) <= absent
    categoryEvents.push(:TARDY) if prng.rand(1..100) <= tardy
    categoryEvents.push(:EXCUSED_ABSENCE) if prng.rand(1..100) <= excused
    categoryEvents.push(:UNEXCUSED_ABSENCE) if prng.rand(1..100) <= unexcused
    categoryEvents.push(:EARLY_DEPARTURE) if prng.rand(1..100) <= early_dep
    categoryEvents.push(:IN_SCHOOL_SUSPENSION) if prng.rand(1..100) <= iss
    categoryEvents.push(:OUT_OF_SCHOOL_SUSPENSION) if prng.rand(1..100) <= oss
    categoryEvents.push(:PRESENT) if categoryEvents.empty?
    return categoryEvents
  end

  def get_attendance_percentages(type)
    type = type.to_s if type.kind_of? Symbol
    raise(ArgumentError, "Missing configuration parameter related to attendance events") if missing_pre_requisite(type)
    return @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['present'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['absent'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['tardy'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['excused'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['unexcused'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['early_dep'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['iss'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['oss']
  end

  # checks that the specified @scenario contains percentages for attendance events
  def missing_pre_requisite(type)
    @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['present'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['absent'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['tardy'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['excused'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['unexcused'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['early_dep'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['iss'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['oss'].nil?
  end

  # return true if the specified date is contained within the array of holidays, and false if it is not included
  # -> refactor this into date utility when more logic is added to support not specifying 'holidays'
  def is_holiday(date, holidays)
    holidays.include?(date)
  end

  # returns a string representing the reason for the attendance event
  # -> returns nil if attendance event is not tardy or absent
  def get_attendance_event_reason(category)
    return "Missed school bus"         if category == :TARDY
    return "Excused: sick"             if category == :EXCUSED_ABSENCE
    return "Unexcused: skipped"        if category == :UNEXCUSED_ABSENCE
    return "Was not here"              if category == :ABSENT
    return "Doctor appointment"        if category == :EARLY_DEPARTURE
    return "ISS: Spray painted locker" if category == :IN_SCHOOL_SUSPENSION
    return "OSS: Kicked teacher"       if category == :OUT_OF_SCHOOL_SUSPENSION
    return nil
  end
end
