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

# Enumerates the types of course repeat codes. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CourseRepeatCodeType">
#   <xs:annotation>
#     <xs:documentation>Indicates that an academic course has been repeated by a student and how that repeat is to be computed in the student's academic grade average.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="RepeatCounted"/>
#     <xs:enumeration value="RepeatNotCounted"/>
#     <xs:enumeration value="ReplacementCounted"/>
#     <xs:enumeration value="ReplacedNotCounted"/>
#     <xs:enumeration value="RepeatOtherInstitution"/>
#     <xs:enumeration value="NotCountedOther"/>
#   </xs:restriction>
# </xs:simpleType>
class CourseRepeatCodeType
  include Enum

  CourseRepeatCodeType.define :NOT_COUNTED_OTHER, "NotCountedOther"
  CourseRepeatCodeType.define :REPEAT_COUNTED, "RepeatCounted"
  CourseRepeatCodeType.define :REPEAT_NOT_COUNTED, "RepeatNotCounted"
  CourseRepeatCodeType.define :REPEAT_OTHER_INSTITUTION, "RepeatOtherInstitution"
  CourseRepeatCodeType.define :REPLACED_NOT_COUNTED, "ReplacedNotCounted"
  CourseRepeatCodeType.define :REPLACEMENT_COUNTED, "ReplacementCounted"
end
