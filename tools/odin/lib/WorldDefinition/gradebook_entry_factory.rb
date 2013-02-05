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
require_relative '../Shared/date_interval'
require_relative '../Shared/date_utility'
require_relative '../Shared/EntityClasses/enum/GradeLevelType'
require_relative '../Shared/EntityClasses/gradebook_entry'

# factory for creating grade book entries for sections
class GradebookEntryFactory

  def initialize(scenario)
    @scenario = scenario
  end

  # generates gradebook entries using the specified 'grade' as the key to look up how many entries to create
  # -> gradebook entries are spread out over the interval (specified by session)
  # -> gradebook entries are associated to the specified section
  def generate_entries(prng, grade, session, section, grading_period = nil)
    entries   = []
    load_gradebook_breakdown_for_grade(grade).each do |type, guidelines|
      min     = guidelines['min']
      max     = guidelines['max']
      num_gbe = DataUtility.select_random_from_options(prng, (min..max).to_a)
      entries += generate_entries_of_type(session, section, type, num_gbe)
    end
    entries
  end

  # creates specified 'num' of gradebook entry work orders for the specified 'type' in the section over the specified 'session'
  # -> deterministic (no prng required)
  def generate_entries_of_type(session, section, type, num)
    entries = []
    unless session.nil?
      start_date  = session['interval'].get_begin_date
      end_date    = session['interval'].get_end_date
      holidays    = session['interval'].get_holidays
      school_days = DateUtility.get_school_days_over_interval(start_date, end_date, num, holidays)
      school_days.each do |date_assigned|
        entry   = create_gradebook_entry_work_order(type, date_assigned, section)
        entries << entry unless entries.include?(entry)
        # put the above check in place to make sure no duplicate gradebook entries are created
        # -> generally occurs around holidays, and this factory isn't smart enough to shuffle assignments out accordingly (yet)
      end
    end
    entries
  end

  # assembles the gradebook entry work order
  def create_gradebook_entry_work_order(gbe_type, date_assigned, section)
    {:type => GradebookEntry, :gbe_type => gbe_type, :date_assigned => date_assigned, :section => section}
  end

  # loads gradebook entry breakdown for the specified grade
  # -> majority of error checking is performed here
  def load_gradebook_breakdown_for_grade(grade)
    raise(ArgumentError, "Failed to load gradebook entry breakdown from scenario.") if @scenario.nil? or @scenario['GRADEBOOK_ENTRIES_BY_GRADE'].nil?
    breakdown = @scenario['GRADEBOOK_ENTRIES_BY_GRADE'][GradeLevelType.to_string(grade)]
    raise(ArgumentError, "Failed to load gradebook entry breakdown for specified grade (#{GradeLevelType.to_string(grade)}).") if breakdown.nil?
    breakdown.each do |assignment, guidelines|
      # traverse the gradebook entry breakdown for the grade and make sure guidelines are well-formed
      raise(ArgumentError, "Nil or empty key for grade: #{GradeLevelType.to_string(grade)}") if assignment.nil? or assignment.size == 0
      raise(ArgumentError, "Malformed gradebook entry breakdown for grade: #{GradeLevelType.to_string(grade)}") if guidelines['min'].nil? or guidelines['max'].nil?
    end
    breakdown
  end
end
