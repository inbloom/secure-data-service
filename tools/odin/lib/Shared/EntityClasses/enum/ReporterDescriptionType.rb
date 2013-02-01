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

# Enumerates the types of reporter descriptions for discipline incidents. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ReporterDescriptionType">
#   <xs:annotation>
#     <xs:documentation>Information on the type of individual who reported the incident. </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Parent"/>
#     <xs:enumeration value="Staff"/>
#     <xs:enumeration value="Student"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class ReporterDescriptionType
  include Enum

  ReporterDescriptionType.define :OTHER, "Other"
  ReporterDescriptionType.define :PARENT, "Parent"
  ReporterDescriptionType.define :STAFF, "Staff"
  ReporterDescriptionType.define :STUDENT, "Student"
end
