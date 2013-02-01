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

# Enumerates the types of performance bases. From Ed-Fi-Core.xsd:
# <xs:simpleType name="PerformanceBaseType">
#   <xs:annotation>
#     <xs:documentation>Defines standard  levels of competency or performance that can be used for dashboard visualizations: advanced, proficient, basic, below basic.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Advanced"/>
#     <xs:enumeration value="Proficient"/>
#     <xs:enumeration value="Basic"/>
#     <xs:enumeration value="Below Basic"/>
#     <xs:enumeration value="Well Below Basic"/>
#   </xs:restriction>
# </xs:simpleType>
class PerformanceBaseType
  include Enum

  PerformanceBaseType.define :ADVANCED, "Advanced"
  PerformanceBaseType.define :BASIC, "Basic"
  PerformanceBaseType.define :BELOW_BASIC, "Below Basic"
  PerformanceBaseType.define :PROFICIENT, "Proficient"
  PerformanceBaseType.define :WELL_BELOW_BASIC, "Well Below Basic"
end
