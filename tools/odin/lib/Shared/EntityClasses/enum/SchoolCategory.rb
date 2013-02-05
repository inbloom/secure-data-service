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

# Enumeration for types of schools. From Ed-Fi-Core.xsd:
# <xs:simpleType name="SchoolCategoryItemType">
#   <xs:annotation>
#     <xs:documentation>The category of school. For example: High School, Middle School, Elementary School</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Elementary/Secondary School"/>
#     <xs:enumeration value="Elementary School"/>
#     <xs:enumeration value="High School"/>
#     <xs:enumeration value="Middle School"/>
#     <xs:enumeration value="Junior High School"/>
#     <xs:enumeration value="Elementary School"/>
#     <xs:enumeration value="SecondarySchool"/>
#     <xs:enumeration value="Ungraded"/>
#     <xs:enumeration value="Adult School"/>
#     <xs:enumeration value="Infant/toddler School"/>
#     <xs:enumeration value="Preschool/early childhood"/>
#     <xs:enumeration value="Primary School"/>
#     <xs:enumeration value="Intermediate School"/>
#   </xs:restriction>
# </xs:simpleType>
class SchoolCategory
  include Enum

  SchoolCategory.define :ADULT, "Adult School"
  SchoolCategory.define :ELEMENTARY, "Elementary School"
  SchoolCategory.define :ELEMENTARY_SECONDARY, "Elementary/Secondary School"
  SchoolCategory.define :HIGH, "High School"
  SchoolCategory.define :INFANT_TODDLER, "Infant/toddler School"
  SchoolCategory.define :INTERMEDIATE, "Intermediate School"
  SchoolCategory.define :JUNIOR_HIGH, "Junior High School"
  SchoolCategory.define :MIDDLE, "Middle School"
  SchoolCategory.define :PRESCHOOL_EARLY_CHILDHOOD, "Preschool/early childhood"
  SchoolCategory.define :PRIMARY, "Primary School"
  SchoolCategory.define :SECONDARY, "SecondarySchool"
  SchoolCategory.define :UNGRADED, "Ungraded"
end
