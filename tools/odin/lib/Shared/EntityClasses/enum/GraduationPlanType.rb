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

# Enumerates the types of graduation plans. From Ed-Fi-Core.xsd:
# <xs:simpleType name="GraduationPlanType">
#   <xs:annotation>
#     <xs:documentation>The type of academic plan the student is following for graduation.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Career and Technical Education"/>
#     <xs:enumeration value="Distinguished"/>
#     <xs:enumeration value="Minimum"/>
#     <xs:enumeration value="Recommended"/>
#     <xs:enumeration value="Standard"/>
#   </xs:restriction>
# </xs:simpleType>
class GraduationPlanType
  include Enum

  GraduationPlanType.define :CAREER_AND_TECHNICAL_EDUCATION, "Career and Technical Education"
  GraduationPlanType.define :DISTINGUISHED, "Distinguished"
  GraduationPlanType.define :MINIMUM, "Minimum"
  GraduationPlanType.define :RECOMMENDED, "Recommended"
  GraduationPlanType.define :STANDARD, "Standard"
end
