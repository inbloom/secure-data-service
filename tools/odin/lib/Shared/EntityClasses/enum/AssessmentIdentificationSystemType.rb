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

# Enumerates the types of assessment identification systems. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AssessmentIdentificationSystemType">
#   <xs:annotation>
#     <xs:documentation>A coding scheme that is used for identification and record-keeping purposes by schools, social services, or other agencies to refer to an assessment.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="School"/>
#     <xs:enumeration value="District"/>
#     <xs:enumeration value="State"/>
#     <xs:enumeration value="Federal"/>
#     <xs:enumeration value="Other Federal"/>
#     <xs:enumeration value="Test Contractor"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentIdentificationSystemType
  include Enum

  AssessmentIdentificationSystemType.define :DISTRICT, "District"
  AssessmentIdentificationSystemType.define :FEDERAL, "Federal"
  AssessmentIdentificationSystemType.define :OTHER, "Other"
  AssessmentIdentificationSystemType.define :OTHER_FEDERAL, "Other Federal"
  AssessmentIdentificationSystemType.define :SCHOOL, "School"
  AssessmentIdentificationSystemType.define :STATE, "State"
  AssessmentIdentificationSystemType.define :TEST_CONTRACTOR, "Test Contractor"
end
