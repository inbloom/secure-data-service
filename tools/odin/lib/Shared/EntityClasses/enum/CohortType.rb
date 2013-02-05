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

# Enumerates the types of cohorts. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CohortType">
#   <xs:annotation>
#     <xs:documentation>The type of the cohort (e.g., academic intervention, classroom breakout, etc.)</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Academic Intervention"/>
#     <xs:enumeration value="Attendance Intervention"/>
#     <xs:enumeration value="Discipline Intervention"/>
#     <xs:enumeration value="Classroom Pullout"/>
#     <xs:enumeration value="Extracurricular Activity"/>
#     <xs:enumeration value="Field Trip"/>
#     <xs:enumeration value="Principal Watch List"/>
#     <xs:enumeration value="Counselor List"/>
#     <xs:enumeration value="In-school Suspension"/>
#     <xs:enumeration value="Study Hall"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class CohortType
  include Enum

  CohortType.define :ACADEMIC_INTERVENTION, "Academic Intervention"
  CohortType.define :ATTENDANCE_INTERVENTION, "Attendance Intervention"
  CohortType.define :CLASSROOM_PULLOUT, "Classroom Pullout"
  CohortType.define :COUNSELOR_LIST, "Counselor List"
  CohortType.define :DISCIPLINE_INTERVENTION, "Discipline Intervention"
  CohortType.define :EXTRACURRICULAR_ACTIVITY, "Extracurricular Activity"
  CohortType.define :FIELD_TRIP, "Field Trip"
  CohortType.define :IN_SCHOOL_SUSPENSION, "In-school Suspension"
  CohortType.define :OTHER, "Other"
  CohortType.define :PRINCIPAL_WATCH_LIST, "Principal Watch List"
  CohortType.define :STUDY_HALL, "Study Hall"
end
