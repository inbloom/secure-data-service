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

# Enumerates the types of old ethnicity. From Ed-Fi-Core.xsd:
# <xs:simpleType name="OldEthnicityType">
#   <xs:annotation>
#     <xs:documentation>Previous definition of Ethnicity combining Hispanic/Latino and Race.  </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="American Indian Or Alaskan Native"/>
#     <xs:enumeration value="Asian Or Pacific Islander"/>
#     <xs:enumeration value="Black, Not Of Hispanic Origin"/>
#     <xs:enumeration value="Hispanic"/>
#     <xs:enumeration value="White, Not Of Hispanic Origin"/>
#   </xs:restriction>
# </xs:simpleType>
class OldEthnicityType
  include Enum

  OldEthnicityType.define :AMERICAN_INDIAN_OR_ALASKAN_NATIVE, "American Indian Or Alaskan Native"
  OldEthnicityType.define :ASIAN_OR_PACIFIC_ISLANDER, "Asian Or Pacific Islander"
  OldEthnicityType.define :BLACK_NOT_OF_HISPANIC_ORIGIN, "Black, Not Of Hispanic Origin"
  OldEthnicityType.define :HISPANIC, "Hispanic"
  OldEthnicityType.define :WHITE_NOT_OF_HISPANIC_ORIGIN, "White, Not Of Hispanic Origin"
end
