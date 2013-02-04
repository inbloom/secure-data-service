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

# Enumerates the types of assessment item results. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AssessmentItemResultType">
#   <xs:annotation>
#     <xs:documentation>The analyzed result of a student's response to an assessment item.. For example:
#     Correct
#     Incorrect
#     Met standard
#     ...
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Correct"/>
#     <xs:enumeration value="Incorrect"/>
#     <xs:enumeration value="Met standard"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentItemResultType
  include Enum

  AssessmentItemResultType.define :CORRECT, "Correct"
  AssessmentItemResultType.define :INCORRECT, "Incorrect"
  AssessmentItemResultType.define :MET_STANDARD, "Met standard"
end
