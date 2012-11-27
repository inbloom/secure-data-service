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

require_relative "interchangeGenerator.rb"
require_relative "../../Shared/data_utility.rb"

Dir["#{File.dirname(__FILE__)}/../EntityClasses/*.rb"].each { |f| load(f) }

# event-based education organization interchange generator
class EducationOrgCalendarGenerator < InterchangeGenerator
  
  # session writer
  class SessionWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/Partials/session.mustache"
      @entity = nil
    end
    def session
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # grading period writer
  class GradingPeriodWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/Partials/grading_period.mustache"
      @entity = nil
    end
    def grading_period
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # calendar date writer
  class CalendarDateWriter < Mustache
    def initialize
      @template_file = "#{File.dirname(__FILE__)}/interchangeTemplates/Partials/calendar_date.mustache"
      @entity = nil
    end
    def calendar_date
      @entity
    end
    def write(entity)
      @entity = entity
      render
    end
  end

  # initialization will define the header and footer for the education organization calendar interchange
  # writes header to education organization calendar interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize
    @header = <<-HEADER
<?xml version="1.0"?>
<InterchangeEducationOrgCalendar xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/SLI-Interchange-EducationOrgCalendar.xsd ">
HEADER
    @footer = <<-FOOTER
</InterchangeEducationOrgCalendar>
FOOTER
    @handle = File.new("generated/InterchangeEducationOrgCalendar.xml", 'w')
    @handle.write(@header)

    @session_writer        = SessionWriter.new
    @grading_period_writer = GradingPeriodWriter.new
    @calendar_date_writer  = CalendarDateWriter.new
  end

  # creates and writes session to interchange
  def create_session(name, year, term, interval, ed_org_id, grading_periods)
  	@handle.write @session_writer.write(Session.new(name, year, term, interval, ed_org_id, grading_periods))
  end

  # creates and writes grading period to interchange
  def create_grading_period(type, year, interval, ed_org_id, calendar_dates)
    @handle.write @grading_period_writer.write(GradingPeriod.new(type, year, interval, ed_org_id, calendar_dates))
  end

  # creates and writes calendar date to interchange
  def create_calendar_date(date, event)
    @handle.write @calendar_date_writer.write(CalendarDate.new(date, event))
  end

  # writes footer and closes education organization calendar interchange file handle
  def close
    @handle.write(@footer)
    @handle.close()
  end
end