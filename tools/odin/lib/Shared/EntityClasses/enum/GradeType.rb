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

# Enumerates the grade types supported by ed-fi. From Ed-Fi-Core.xsd:
# <xs:simpleType name="GradeType">
#   <xs:annotation>
#     <xs:documentation>The type of grade in a report card or transcript (e.g., Final, Exam, Grading Period, ...)</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Conduct"/>
#     <xs:enumeration value="Exam"/>
#     <xs:enumeration value="Final"/>
#     <xs:enumeration value="Grading Period"/>
#     <xs:enumeration value="Mid-Term Grade"/>
#     <xs:enumeration value="Progress Report"/>
#     <xs:enumeration value="Semester"/>
#   </xs:restriction>
# </xs:simpleType>
class GradeType
  include Enum

  GradeType.define :CONDUCT, "Conduct"
  GradeType.define :EXAM, "Exam"
  GradeType.define :FINAL, "Final"
  GradeType.define :GRADING_PERIOD, "Grading Period"
  GradeType.define :MID_TERM_GRADE, "Mid-Term Grade"
  GradeType.define :PROGRESS_REPORT, "Progress Report"
  GradeType.define :SEMESTER, "Semester"
end
