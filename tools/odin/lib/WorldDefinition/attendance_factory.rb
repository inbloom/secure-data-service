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
  end

  # generate attendance event work orders for the specified 'student' at the specified education organization
  # over the interval of dates specified by the 'session'
  # -> type refers to education organization type (elementary, middle, or high school)
  # -> type is required to perform lookup on daily attendance percentages breakdown (for absence and tardiness)
  def generate_attendance_events(prng, student, ed_org_id, session, type, student_section_association)
    absent, tardy     = get_absent_and_tardy_percentages(type)
    attendance_events = []
    unless session.nil?
      raise(ArgumentError, "Missing configuration parameter related to exception-only attendance.") if @scenario['EXCEPTION_ONLY_ATTENDANCE'].nil?
      exceptions_only = @scenario['EXCEPTION_ONLY_ATTENDANCE']
      # set up present, absent, tardy attendance events array
      events = Array.new(100, :PRESENT)
      if absent == 100
        events = Array.new(100, :EXCUSED_ABSENCE)
        # ignore tardy events
      else
        # add tardy and absent events
        choices = (1..100).to_a
        tardy_indexes  = []
        absent_indexes = []
        begin
          tardy_indexes  = DataUtility.select_num_from_options(prng, tardy, choices)
          absent_indexes = DataUtility.select_num_from_options(prng, absent, choices)
        end while (tardy_indexes & absent_indexes).size > 0
        tardy_indexes.each  { |index| events[index - 1] = :TARDY  }
        absent_indexes.each { |index| events[index - 1] = :EXCUSED_ABSENCE }
      end

      begin_date = session['interval'].get_begin_date
      end_date   = session['interval'].get_end_date
      holidays   = session['interval'].get_holidays
      (begin_date..end_date).step(1) do |current_date|
        next if DateUtility.is_weekend_day(current_date) or is_holiday(current_date, holidays)
        category = DataUtility.select_random_from_options(prng, events)
        reason   = get_attendance_event_reason(category) if category != :PRESENT
        attendance_events << AttendanceEvent.new(prng.seed, student, ed_org_id, current_date, category, student_section_association,
                                                 session, reason) unless category == :PRESENT and exceptions_only
      end
    end
    attendance_events
  end

  def get_absent_and_tardy_percentages(type)
    type = type.to_s if type.kind_of? Symbol
    raise(ArgumentError, "Missing configuration parameter related to absent and tardy events") if missing_pre_requisite(type)
    return @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['absent'], @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['tardy']
  end

  # checks that the specified @scenario contains percentages for absent and tardy
  def missing_pre_requisite(type)
    @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['absent'].nil? or @scenario['DAILY_ATTENDANCE_PERCENTAGES'][type]['tardy'].nil?
  end

  # return true if the specified date is contained within the array of holidays, and false if it is not included
  # -> refactor this into date utility when more logic is added to support not specifying 'holidays'
  def is_holiday(date, holidays)
    holidays.include?(date)
  end

  # returns a string representing the reason for the attendance event
  # -> returns nil if attendance event is not tardy or absent
  def get_attendance_event_reason(category)
    return "Missed school bus" if category == :TARDY
    return "Excused: sick"     if category == :EXCUSED_ABSENCE
    return nil
  end
end
