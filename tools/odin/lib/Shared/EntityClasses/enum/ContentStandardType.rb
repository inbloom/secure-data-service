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

# Enumerates the types of content standards. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ContentStandardType">
#   <xs:annotation>
#     <xs:documentation>An indication as to whether an assessment conforms to a standard. For example:
#     Local standard
#     Statewide standard
#     Regional standard
#     Association standard
#     School standard
#     ...
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="National Standard"/>
#     <xs:enumeration value="State Standard"/>
#     <xs:enumeration value="College Entrance Standard"/>
#     <xs:enumeration value="LEA Standard"/>
#     <xs:enumeration value="Texas Essential Knowledge and Skills"/>
#     <xs:enumeration value="SAT"/>
#     <xs:enumeration value="PSAT"/>
#     <xs:enumeration value="ACT"/>
#     <xs:enumeration value="Advanced Placement"/>
#     <xs:enumeration value="International Baccalaureate"/>
#   </xs:restriction>
# </xs:simpleType>
class ContentStandardType
  include Enum

  ContentStandardType.define :ACT, "ACT"
  ContentStandardType.define :ADVANCED_PLACEMENT, "Advanced Placement"
  ContentStandardType.define :COLLEGE_ENTRANCE_STANDARD, "College Entrance Standard"
  ContentStandardType.define :INTERNATIONAL_BACCALAUREATE, "International Baccalaureate"
  ContentStandardType.define :LEA_STANDARD, "LEA Standard"
  ContentStandardType.define :NATIONAL_STANDARD, "National Standard"
  ContentStandardType.define :PSAT, "PSAT"
  ContentStandardType.define :SAT, "SAT"
  ContentStandardType.define :STATE_STANDARD, "State Standard"
  ContentStandardType.define :TEXAS_ESSENTIAL_KNOWLEDGE_AND_SKILLS, "Texas Essential Knowledge and Skills"
end
