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

# Enumerates the types of staff classifications. From Ed-Fi-Core.xsd:
# <xs:simpleType name="StaffClassificationType">
#   <xs:annotation>
#     <xs:documentation>An individual's title of employment, official status or rank.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Art Therapist"/>
#     <xs:enumeration value="Athletic Trainer"/>
#     <xs:enumeration value="Assistant Principal"/>
#     <xs:enumeration value="Assistant Superintendent"/>
#     <xs:enumeration value="Certified Interpreter"/>
#     <xs:enumeration value="Counselor"/>
#     <xs:enumeration value="High School Counselor"/>
#     <xs:enumeration value="Instructional Coordinator"/>
#     <xs:enumeration value="Instructional Aide"/>
#     <xs:enumeration value="Librarians/Media Specialists"/>
#     <xs:enumeration value="LEA Administrator"/>
#     <xs:enumeration value="LEA Specialist"/>
#     <xs:enumeration value="LEA System Administrator"/>
#     <xs:enumeration value="LEA Administrative Support Staff"/>
#     <xs:enumeration value="Librarian"/>
#     <xs:enumeration value="Principal"/>
#     <xs:enumeration value="Physical Therapist"/>
#     <xs:enumeration value="Teacher"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Superintendent"/>
#     <xs:enumeration value="School Nurse"/>
#     <xs:enumeration value="Specialist/Consultant"/>
#     <xs:enumeration value="School Administrator"/>
#     <xs:enumeration value="School Administrative Support Staff"/>
#     <xs:enumeration value="Student Support Services Staff"/>
#     <xs:enumeration value="School Leader"/>
#     <xs:enumeration value="School Specialist"/>
#     <xs:enumeration value="Substitute Teacher"/>
#   </xs:restriction>
# </xs:simpleType>
class StaffClassificationType
  include Enum

  StaffClassificationType.define :ART_THERAPIST, "Art Therapist"
  StaffClassificationType.define :ASSISTANT_PRINCIPAL, "Assistant Principal"
  StaffClassificationType.define :ASSISTANT_SUPERINTENDENT, "Assistant Superintendent"
  StaffClassificationType.define :ATHLETIC_TRAINER, "Athletic Trainer"
  StaffClassificationType.define :CERTIFIED_INTERPRETER, "Certified Interpreter"
  StaffClassificationType.define :COUNSELOR, "Counselor"
  StaffClassificationType.define :HIGH_SCHOOL_COUNSELOR, "High School Counselor"
  StaffClassificationType.define :INSTRUCTIONAL_AIDE, "Instructional Aide"
  StaffClassificationType.define :INSTRUCTIONAL_COORDINATOR, "Instructional Coordinator"
  StaffClassificationType.define :LEA_ADMINISTRATIVE_SUPPORT_STAFF, "LEA Administrative Support Staff"
  StaffClassificationType.define :LEA_ADMINISTRATOR, "LEA Administrator"
  StaffClassificationType.define :LEA_SPECIALIST, "LEA Specialist"
  StaffClassificationType.define :LEA_SYSTEM_ADMINISTRATOR, "LEA System Administrator"
  StaffClassificationType.define :LIBRARIAN, "Librarian"
  StaffClassificationType.define :LIBRARIANS_MEDIA_SPECIALISTS, "Librarians/Media Specialists"
  StaffClassificationType.define :OTHER, "Other"
  StaffClassificationType.define :PHYSICAL_THERAPIST, "Physical Therapist"
  StaffClassificationType.define :PRINCIPAL, "Principal"
  StaffClassificationType.define :SCHOOL_ADMINISTRATIVE_SUPPORT_STAFF, "School Administrative Support Staff"
  StaffClassificationType.define :SCHOOL_ADMINISTRATOR, "School Administrator"
  StaffClassificationType.define :SCHOOL_LEADER, "School Leader"
  StaffClassificationType.define :SCHOOL_NURSE, "School Nurse"
  StaffClassificationType.define :SCHOOL_SPECIALIST, "School Specialist"
  StaffClassificationType.define :SPECIALIST_CONSULTANT, "Specialist/Consultant"
  StaffClassificationType.define :STUDENT_SUPPORT_SERVICES_STAFF, "Student Support Services Staff"
  StaffClassificationType.define :SUBSTITUTE_TEACHER, "Substitute Teacher"
  StaffClassificationType.define :SUPERINTENDENT, "Superintendent"
  StaffClassificationType.define :TEACHER, "Teacher"
end
