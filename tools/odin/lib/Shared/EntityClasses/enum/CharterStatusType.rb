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

# Enumerates the types of charter statuses. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CharterStatusType">
#   <xs:annotation>
#     <xs:documentation>The category of charter school. For example: School Charter, Open Enrollment Charter</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="School Charter"/>
#     <xs:enumeration value="College/University Charter"/>
#     <xs:enumeration value="Open Enrollment"/>
#     <xs:enumeration value="Not a Charter School"/>
#   </xs:restriction>
# </xs:simpleType>
class CharterStatusType
  include Enum

  CharterStatusType.define :SCHOOL_CHARTER, "School Charter"
  CharterStatusType.define :COLLEGE_OR_UNIVERSITY_CHARTER, "College/University Charter"
  CharterStatusType.define :OPEN_ENROLLMENT, "Open Enrollment"
  CharterStatusType.define :NOT_A_CHARTER_SCHOOL, "Not a Charter School"
end
