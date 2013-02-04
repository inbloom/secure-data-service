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

require_relative 'baseEntity'
require_relative 'enum/AttendanceEventCategory'
require_relative 'enum/AttendanceEventType'
require_relative 'enum/EducationalEnvironmentType'

# creates attendance event
class AttendanceEvent < BaseEntity

  attr_accessor :event_date, :type, :category, :reason, :environment, :student, :ed_org_id,
                :section_reference, :session_reference

  def initialize(student, ed_org_id, event_date, event_category, student_section_association = nil,
                 session = nil, reason = nil, event_type = :DAILY_ATTENDANCE, environment = :CLASSROOM)
    @student     = student
    @ed_org_id   = ed_org_id
    @event_date  = event_date
    @type        = event_type
    @category    = event_category
    @reason      = reason
    @environment = environment
    @rand = Random.new(@student)

    if (optional?) 
      @session_reference = session["name"] unless session.nil?
      valid_sections = student_section_association.select {|x| x.ed_org_id == @ed_org_id}
      @section_reference = valid_sections[0].section if (valid_sections && valid_sections.size > 0)
    end
  end

  def type_str
    AttendanceEventType.to_string(@type)
  end

  def category_str
    AttendanceEventCategory.to_string(@category)
  end

  def environment_str
    EducationalEnvironmentType.to_string(@environment)
  end

end
