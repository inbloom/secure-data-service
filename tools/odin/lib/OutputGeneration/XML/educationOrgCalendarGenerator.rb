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

require "mustache"

require_relative "./EntityWriter"
require_relative "interchangeGenerator.rb"
require_relative "../../Shared/data_utility.rb"

Dir["#{File.dirname(__FILE__)}/../EntityClasses/*.rb"].each { |f| load(f) }

# event-based education organization interchange generator
class EducationOrgCalendarGenerator < InterchangeGenerator
  
  # initialization will define the header and footer for the education organization calendar interchange
  # writes header to education organization calendar interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize
    @interchange = File.new("generated/InterchangeEducationOrgCalendar.xml", 'w')
    super(@interchange)

    @header, @footer = build_header_footer( "EducationOrgCalendar" )

    @writers[Session] = EntityWriter.new("session.mustache")
    @writers[GradingPeriod] = EntityWriter.new("grading_period.mustache")
    @writers[CalendarDate] = EntityWriter.new("calendar_date.mustache")
  end

  # creates and writes session to interchange
  def create_session(name, year, term, interval, ed_org_id, grading_periods)
  	self << Session.new(name, year, term, interval, ed_org_id, grading_periods)
  end

  # creates and writes grading period to interchange
  def create_grading_period(type, year, interval, ed_org_id, calendar_dates)
    self << GradingPeriod.new(type, year, interval, ed_org_id, calendar_dates)
  end

  # creates and writes calendar date to interchange
  def create_calendar_date(date, event)
    self << CalendarDate.new(date, event)
  end

end
