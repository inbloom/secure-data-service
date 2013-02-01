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

require_relative "Enum.rb"

# Enumerates the types of calendar events. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CalendarEventType">
#   <xs:annotation>
#     <xs:documentation>The type of scheduled or unscheduled event for the day.  For example:
#     Instructional day
#     Teacher only day
#     Holiday
#     Make-up day
#     Weather day
#     Student late arrival/early dismissal
#     ...
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Instructional day"/>
#     <xs:enumeration value="Teacher only day"/>
#     <xs:enumeration value="Holiday"/>
#     <xs:enumeration value="Make-up day"/>
#     <xs:enumeration value="Weather day"/>
#     <xs:enumeration value="Student late arrival/early dismissal"/>
#     <xs:enumeration value="Emergency day"/>
#     <xs:enumeration value="Strike"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class CalendarEventType
  include Enum

  CalendarEventType.define :EMERGENCY_DAY, "Emergency day"
  CalendarEventType.define :HOLIDAY, "Holiday"
  CalendarEventType.define :INSTRUCTIONAL_DAY, "Instructional day"
  CalendarEventType.define :MAKE_UP_DAY, "Make-up day"
  CalendarEventType.define :OTHER, "Other"
  CalendarEventType.define :STRIKE, "Strike"
  CalendarEventType.define :STUDENT_LATE_ARRIVAL_EARLY_DISMISSAL, "Student late arrival/early dismissal"
  CalendarEventType.define :TEACHER_ONLY_DAY, "Teacher only day"
  CalendarEventType.define :WEATHER_DAY, "Weather day"
end
