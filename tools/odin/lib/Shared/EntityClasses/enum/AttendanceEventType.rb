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

require_relative 'Enum.rb'

# Enumerates the types of attendance events. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AttendanceEventType">
#   <xs:annotation>
#     <xs:documentation>A code describing the type of the attendance event (e.g., daily attendance, section attendance, etc.)</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Daily Attendance"/>
#     <xs:enumeration value="Section Attendance"/>
#     <xs:enumeration value="Program Attendance"/>
#     <xs:enumeration value="Extracurricular Attendance"/>
#   </xs:restriction>
# </xs:simpleType>
class AttendanceEventType
  include Enum

  AttendanceEventType.define :DAILY_ATTENDANCE, "Daily Attendance"
  AttendanceEventType.define :EXTRACURRICULAR_ATTENDANCE, "Section Attendance"
  AttendanceEventType.define :PROGRAM_ATTENDANCE, "Program Attendance"
  AttendanceEventType.define :SECTION_ATTENDANCE, "Extracurricular Attendance"

  # translates the specified Symbol into the ed-fi compliant String representation of the attendance event type
  # -> returns nil if the Symbol doesn't exist
  def self.to_string(key)
    const_get(key)
  end

  # translates the specified String representation of the attendance event type into a Symbol
  # -> returns nil if the String representation doesn't map to a Symbol
  def self.to_symbol(value)
    get_key(value)
  end
end
