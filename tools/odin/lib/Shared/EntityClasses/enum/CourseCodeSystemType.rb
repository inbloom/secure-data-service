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

# Enumerates the types of course code systems. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CourseCodeSystemType">
#   <xs:annotation>
#     <xs:documentation>A system that is used to identify the organization of subject matter and related learning experiences provided for the instruction of students. In addition to identifying courses using the appropriate state codes, it is encouraged that courses are also cross referenced against one of the national course codes.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="CSSC course code"/>
#     <xs:enumeration value="Intermediate agency course code"/>
#     <xs:enumeration value="LEA course code"/>
#     <xs:enumeration value="NCES Pilot SNCCS course code"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="SCED course code"/>
#     <xs:enumeration value="School course code"/>
#     <xs:enumeration value="State course code"/>
#     <xs:enumeration value="University course code"/>
#   </xs:restriction>
# </xs:simpleType>
class CourseCodeSystemType
  include Enum

  CourseCodeSystemType.define :CSSC_COURSE_CODE, "CSSC course code"
  CourseCodeSystemType.define :INTERMEDIATE_AGENCY_COURSE_CODE, "Intermediate agency course code"
  CourseCodeSystemType.define :LEA_COURSE_CODE, "LEA course code"
  CourseCodeSystemType.define :NCES_PILOT_SNCCS_COURSE_CODE, "NCES Pilot SNCCS course code"
  CourseCodeSystemType.define :OTHER, "Other"
  CourseCodeSystemType.define :SCED_COURSE_CODE, "SCED course code"
  CourseCodeSystemType.define :SCHOOL_COURSE_CODE, "School course code"
  CourseCodeSystemType.define :STATE_COURSE_CODE, "State course code"
  CourseCodeSystemType.define :UNIVERSITY_COURSE_CODE, "University course code"
end
