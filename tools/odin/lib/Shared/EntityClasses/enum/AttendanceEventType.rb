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
end
