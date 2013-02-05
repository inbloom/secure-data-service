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

# Enumerates the types of incident locations. From Ed-Fi-Core.xsd:
# <xs:simpleType name="IncidentLocationType">
#   <xs:annotation>
#     <xs:documentation>Identifies where the incident occurred and whether or not it occurred on school.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="On School"/>
#     <xs:enumeration value="Administrative offices area"/>
#     <xs:enumeration value="Cafeteria area"/>
#     <xs:enumeration value="Classroom"/>
#     <xs:enumeration value="Hallway or stairs"/>
#     <xs:enumeration value="Locker room or gym areas"/>
#     <xs:enumeration value="Restroom"/>
#     <xs:enumeration value="Library/media center"/>
#     <xs:enumeration value="Computer lab"/>
#     <xs:enumeration value="Auditorium"/>
#     <xs:enumeration value="On-School other inside area"/>
#     <xs:enumeration value="Athletic field or playground"/>
#     <xs:enumeration value="Stadium"/>
#     <xs:enumeration value="Parking lot"/>
#     <xs:enumeration value="On-School other outside area"/>
#     <xs:enumeration value="Off School"/>
#     <xs:enumeration value="Bus stop"/>
#     <xs:enumeration value="School bus"/>
#     <xs:enumeration value="Walking to or from school"/>
#     <xs:enumeration value="Off-School at other school"/>
#     <xs:enumeration value="Off-School at other school district facility"/>
#     <xs:enumeration value="Online"/>
#     <xs:enumeration value="Unknown"/>
#   </xs:restriction>
# </xs:simpleType>
class IncidentLocationType
  include Enum

  IncidentLocationType.define :ADMINISTRATIVE_OFFICES_AREA, "Administrative offices area"
  IncidentLocationType.define :ATHLETIC_FIELD_OR_PLAYGROUND, "Athletic field or playground"
  IncidentLocationType.define :AUDITORIUM, "Auditorium"
  IncidentLocationType.define :BUS_STOP, "Bus stop"
  IncidentLocationType.define :CAFETERIA_AREA, "Cafeteria area"
  IncidentLocationType.define :CLASSROOM, "Classroom"
  IncidentLocationType.define :COMPUTER_LAB, "Computer lab"
  IncidentLocationType.define :HALLWAY_OR_STAIRS, "Hallway or stairs"
  IncidentLocationType.define :LIBRARY_MEDIA_CENTER, "Library/media center"
  IncidentLocationType.define :LOCKER_ROOM_OR_GYM_AREAS, "Locker room or gym areas"
  IncidentLocationType.define :OFF_SCHOOL, "Off School"
  IncidentLocationType.define :OFF_SCHOOL_AT_OTHER_SCHOOL, "Off-School at other school"
  IncidentLocationType.define :OFF_SCHOOL_AT_OTHER_SCHOOL_DISTRICT_FACILITY, "Off-School at other school district facility"
  IncidentLocationType.define :ONLINE, "Online"
  IncidentLocationType.define :ON_SCHOOL, "On School"
  IncidentLocationType.define :ON_SCHOOL_OTHER_INSIDE_AREA, "On-School other inside area"
  IncidentLocationType.define :ON_SCHOOL_OTHER_OUTSIDE_AREA, "On-School other outside area"
  IncidentLocationType.define :PARKING_LOT, "Parking lot"
  IncidentLocationType.define :RESTROOM, "Restroom"
  IncidentLocationType.define :SCHOOL_BUS, "School bus"
  IncidentLocationType.define :STADIUM, "Stadium"
  IncidentLocationType.define :UNKNOWN, "Unknown"
  IncidentLocationType.define :WALKING_TO_OR_FROM_SCHOOL, "Walking to or from school"
end
