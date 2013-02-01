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

# Enumerates the reasons for differences in length of discipline actions. From Ed-Fi-Core.xsd:
# <xs:simpleType name="DisciplineActionLengthDifferenceReasonType">
#   <xs:annotation>
#     <xs:documentation>Indicates the reason for the difference, if any, between the official and actual lengths of a student�s disciplinary assignment.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="No Difference"/>
#     <xs:enumeration value="Term Modified By District"/>
#     <xs:enumeration value="Term Modified By Court Order"/>
#     <xs:enumeration value="Term Modified By Mutual Agreement"/>
#     <xs:enumeration value="Student Completed Term Requirements Sooner Than Expected"/>
#     <xs:enumeration value="Student Incarcerated"/>
#     <xs:enumeration value="Term Decreased Due To Extenuating Health-Related Circumstances"/>
#     <xs:enumeration value="Student Withdrew From School"/>
#     <xs:enumeration value="School Year Ended"/>
#     <xs:enumeration value="Continuation Of Previous Year's Disciplinary Action Assignment"/>
#     <xs:enumeration value="Term Modified By Placement Program Due To Student Behavior While In The Placement"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class DisciplineActionLengthDifferenceReasonType
  include Enum

  DisciplineActionLengthDifferenceReasonType.define :CONTINUATION_OF_PREVIOUS_YEAR_DISCIPLINARY_ACTION, "Continuation Of Previous Year's Disciplinary Action Assignment"
  DisciplineActionLengthDifferenceReasonType.define :NO_DIFFERENCE, "No Difference"
  DisciplineActionLengthDifferenceReasonType.define :OTHER, "Other"
  DisciplineActionLengthDifferenceReasonType.define :SCHOOL_YEAR_ENDED, "School Year Ended"
  DisciplineActionLengthDifferenceReasonType.define :STUDENT_COMPLETED_TERM_REQUIREMENTS_SOONER_THAN_EXPECTED, "Student Completed Term Requirements Sooner Than Expected"
  DisciplineActionLengthDifferenceReasonType.define :STUDENT_INCARCERATED, "Student Incarcerated"
  DisciplineActionLengthDifferenceReasonType.define :STUDENT_WITHDREW_FROM_SCHOOL, "Student Withdrew From School"
  DisciplineActionLengthDifferenceReasonType.define :TERM_DECREASED_DUE_TO_EXTENUATING_HEALTH_CIRCUMSTANCES, "Term Decreased Due To Extenuating Health-Related Circumstances"
  DisciplineActionLengthDifferenceReasonType.define :TERM_MODIFIED_BY_COURT_ORDER, "Term Modified By Court Order"
  DisciplineActionLengthDifferenceReasonType.define :TERM_MODIFIED_BY_DISTRICT, "Term Modified By District"
  DisciplineActionLengthDifferenceReasonType.define :TERM_MODIFIED_BY_MUTUAL_AGREEMENT, "Term Modified By Mutual Agreement"
  DisciplineActionLengthDifferenceReasonType.define :TERM_MODIFIED_BY_PLACEMENT_PROGRAM_DUE_TO_STUDENT_BEHAVIOUR, "Term Modified By Placement Program Due To Student Behavior While In The Placement"
end
