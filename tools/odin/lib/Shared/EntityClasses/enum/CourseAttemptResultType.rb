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

# Enumerates the results of course attempts (by students). From Ed-Fi-Core.xsd:
# <xs:simpleType name="CourseAttemptResultType">
#   <xs:annotation>
#     <xs:documentation>The result from the student's attempt to take the course, for example:
#     Pass
#     Fail
#     Incomplete
#     Withdrawn
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Pass"/>
#     <xs:enumeration value="Fail"/>
#     <xs:enumeration value="Incomplete"/>
#     <xs:enumeration value="Withdrawn"/>
#   </xs:restriction>
# </xs:simpleType>
class CourseAttemptResultType
  include Enum

  CourseAttemptResultType.define :FAIL, "Fail"
  CourseAttemptResultType.define :INCOMPLETE, "Incomplete"
  CourseAttemptResultType.define :PASS, "Pass"
  CourseAttemptResultType.define :WITHDRAWN, "Withdrawn"
end
