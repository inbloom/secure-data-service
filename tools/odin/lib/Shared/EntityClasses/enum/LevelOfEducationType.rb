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

# Enumerates the levels of educations for teachers. From Ed-Fi-Core.xsd:
# <xs:simpleType name="LevelOfEducationType">
#   <xs:annotation>
#     <xs:documentation>The enumeration of the different levels of education achievable.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="No Degree"/>
#     <xs:enumeration value="Bachelor's"/>
#     <xs:enumeration value="Master's"/>
#     <xs:enumeration value="Doctorate"/>
#   </xs:restriction>
# </xs:simpleType>
class LevelOfEducationType
  include Enum

  LevelOfEducationType.define :BACHELORS_DEGREE, "Bachelor's"
  LevelOfEducationType.define :DOCTORATE, "Doctorate"
  LevelOfEducationType.define :MASTERS_DEGREE, "Master's"
  LevelOfEducationType.define :NO_DEGREE, "No Degree"
end
