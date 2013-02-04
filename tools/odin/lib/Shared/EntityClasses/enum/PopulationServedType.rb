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

require_relative "Enum.rb"

# Enumerates the types of populations served. From Ed-Fi-Core.xsd:
# <xs:simpleType name="PopulationServedType">
#   <xs:annotation>
#     <xs:documentation>The type of students the course was designed for.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Regular Students"/>
#     <xs:enumeration value="Bilingual Students"/>
#     <xs:enumeration value="Compensatory/Remedial Education Students"/>
#     <xs:enumeration value="Gifted and Talented Students"/>
#     <xs:enumeration value="Career and Technical Education Students"/>
#     <xs:enumeration value="Special Education Students"/>
#     <xs:enumeration value="ESL Students"/>
#     <xs:enumeration value="Adult Basic Education Students"/>
#     <xs:enumeration value="Honors Students"/>
#     <xs:enumeration value="Migrant Students"/>
#   </xs:restriction>
# </xs:simpleType>
class PopulationServedType
  include Enum

  PopulationServedType.define :ADULT_BASIC_EDUCATION_STUDENTS, "Adult Basic Education Students"
  PopulationServedType.define :BILINGUAL_STUDENTS, "Bilingual Students"
  PopulationServedType.define :CAREER_AND_TECHNICAL_EDUCATION_STUDENTS, "Career and Technical Education Students"
  PopulationServedType.define :COMPENSATORY_REMEDIAL_EDUCATION_STUDENTS, "Compensatory/Remedial Education Students"
  PopulationServedType.define :ESL_STUDENTS, "ESL Students"
  PopulationServedType.define :GIFTED_AND_TALENTED_STUDENTS, "Gifted and Talented Students"
  PopulationServedType.define :HONORS_STUDENTS, "Honors Students"
  PopulationServedType.define :MIGRANT_STUDENTS, "Migrant Students"
  PopulationServedType.define :REGULAR_STUDENTS, "Regular Students"
  PopulationServedType.define :SPECIAL_EDUCATION_STUDENTS, "Special Education Students"
end
