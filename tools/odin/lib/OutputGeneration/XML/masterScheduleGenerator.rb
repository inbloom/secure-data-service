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
require_relative "./interchangeGenerator.rb"

Dir["#{File.dirname(__FILE__)}/../../Shared/EntityClasses/*.rb"].each { |f| load(f) }

# event-based master schedule interchange generator
class MasterScheduleGenerator < InterchangeGenerator

  # initialization will define the header and footer for the master schedule interchange
  # writes header to master schedule interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer( "MasterSchedule" )

    @writers[CourseOffering] = EntityWriter.new("course_offering.mustache")
    @writers[Section] = EntityWriter.new("section.mustache")
  end

  # creates and writes course offering to interchange
  def create_course_offering(id, title, ed_org_id, session, course)
    self << CourseOffering.new(id, title, ed_org_id, session, course)
  end

  # creates and writes section to interchange
  def create_section(id, year, term, interval, ed_org_id, grading_periods)
    self << Section.new(id, year, term, interval, ed_org_id, grading_periods)
  end

end
