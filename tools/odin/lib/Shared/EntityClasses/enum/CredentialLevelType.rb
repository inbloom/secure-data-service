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

# Enumerates the types of credential levels. From Ed-Fi-Core.xsd:
# <xs:simpleType name="LevelType">
#   <xs:annotation>
#     <xs:documentation>The grade level(s) certified for teaching.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="All Level (Grade Level PK-12)"/>
#     <xs:enumeration value="All-Level (Grade Level EC-12)"/>
#     <xs:enumeration value="Early Childhood (PK-K)"/>
#     <xs:enumeration value="Elementary (Grade Level 1-6)"/>
#     <xs:enumeration value="Elementary (Grade Level 1-8)"/>
#     <xs:enumeration value="Elementary (Grade Level 4-8)"/>
#     <xs:enumeration value="Elementary (Grade Level EC-4)"/>
#     <xs:enumeration value="Elementary (Grade Level EC-6)"/>
#     <xs:enumeration value="Elementary (Grade Level PK-5)"/>
#     <xs:enumeration value="Elementary (Grade Level PK-6)"/>
#     <xs:enumeration value="Grade Level NA"/>
#     <xs:enumeration value="Junior High (Grade Level 6-8)"/>
#     <xs:enumeration value="Secondary (Grade Level 6-12)"/>
#     <xs:enumeration value="Secondary (Grade Level 8-12)"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
# <<11/01/2012: removed 'Other' because ComplexTypes.xsd doesn't currently
#               support that enumeration>>
class CredentialLevelType
  include Enum

  CredentialLevelType.define :ALL_LEVEL_GRADES_EC_12, "All-Level (Grade Level EC-12)"
  CredentialLevelType.define :ALL_LEVEL_GRADES_PK_12, "All Level (Grade Level PK-12)"
  CredentialLevelType.define :EARLY_CHILDHOOD, "Early Childhood (PK-K)"
  CredentialLevelType.define :ELEMENTARY_GRADES_1_6, "Elementary (Grade Level 1-6)"
  CredentialLevelType.define :ELEMENTARY_GRADES_1_8, "Elementary (Grade Level 1-8)"
  CredentialLevelType.define :ELEMENTARY_GRADES_4_8, "Elementary (Grade Level 4-8)"
  CredentialLevelType.define :ELEMENTARY_GRADES_EC_4, "Elementary (Grade Level EC-4)"
  CredentialLevelType.define :ELEMENTARY_GRADES_EC_6, "Elementary (Grade Level EC-6)"
  CredentialLevelType.define :ELEMENTARY_GRADES_PK_5, "Elementary (Grade Level PK-5)"
  CredentialLevelType.define :ELEMENTARY_GRADES_PK_6, "Elementary (Grade Level PK-6)"
  CredentialLevelType.define :GRADE_LEVEL_N_A, "Grade Level NA"
  CredentialLevelType.define :JUNIOR_HIGH_GRADES_6_8, "Junior High (Grade Level 6-8)"
  CredentialLevelType.define :SECONDARY_GRADES_6_12, "Secondary (Grade Level 6-12)"
  CredentialLevelType.define :SECONDAY_GRADES_8_12, "Secondary (Grade Level 8-12)"
end
