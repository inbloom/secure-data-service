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

# Enumerates the types of cohort scopes. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CohortScopeType">
#   <xs:annotation>
#     <xs:documentation>The scope of cohort (e.g., school, district, classroom, etc.)</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="District"/>
#     <xs:enumeration value="School"/>
#     <xs:enumeration value="Classroom"/>
#     <xs:enumeration value="Teacher"/>
#     <xs:enumeration value="Principal"/>
#     <xs:enumeration value="Counselor"/>
#     <xs:enumeration value="Statewide"/>
#   </xs:restriction>
# </xs:simpleType>
class CohortScopeType
  include Enum

  CohortScopeType.define :CLASSROOM, "Classroom"
  CohortScopeType.define :COUNSELOR, "Counselor"
  CohortScopeType.define :DISTRICT, "District"
  CohortScopeType.define :PRINCIPAL, "Principal"
  CohortScopeType.define :SCHOOL, "School"
  CohortScopeType.define :STATEWIDE, "Statewide"
  CohortScopeType.define :TEACHER, "Teacher"
end
