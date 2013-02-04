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

# Enumerates the types of races currently in ed-fi. From Ed-Fi-Core.xsd:
# <xs:simpleType name="RaceItemType">
#   <xs:annotation>
#     <xs:documentation>The enumeration items defining the racial categories which most clearly reflects the individual's recognition of his or her community or with which the individual most identifies.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="American Indian - Alaskan Native"/>
#     <xs:enumeration value="Asian"/>
#     <xs:enumeration value="Black - African American"/>
#     <xs:enumeration value="Native Hawaiian - Pacific Islander"/>
#     <xs:enumeration value="White"/>
#   </xs:restriction>
# </xs:simpleType>
class RaceType
  include Enum

  RaceType.define :AMERICAN_INDIAN_OR_ALASKAN_NATIVE, "American Indian - Alaskan Native"
  RaceType.define :ASIAN, "Asian"
  RaceType.define :BLACK_OR_AFRICAN_AMERICAN, "Black - African American"
  RaceType.define :NATIVE_HAWAIIAN_OR_PACIFIC_ISLANDER, "Native Hawaiian - Pacific Islander"
  RaceType.define :WHITE, "White"
end
