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

# Enumerates the types of assessment reporting methods. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AssessmentReportingMethodType">
#   <xs:annotation>
#     <xs:documentation>The method that the instructor of the class uses to report the performance and achievement of all students. It may be a qualitative method such as individualized teacher comments or a quantitative method such as a letter or a numerical grade. In some cases, more than one type of reporting method may be used.
#   </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Achievement/proficiency level"/>
#     <xs:enumeration value="ACT score"/>
#     <xs:enumeration value="Adaptive scale score"/>
#     <xs:enumeration value="Age score"/>
#     <xs:enumeration value="C-scaled scores"/>
#     <xs:enumeration value="College Board examination scores"/>
#     <xs:enumeration value="Composite Score"/>
#     <xs:enumeration value="Composite Rating"/>
#     <xs:enumeration value="Composition Score"/>
#     <xs:enumeration value="Grade equivalent or grade-level indicator"/>
#     <xs:enumeration value="Grade equivalent or grade-level indicator"/>
#     <xs:enumeration value="Graduation score"/>
#     <xs:enumeration value="Growth/value-added/indexing"/>
#     <xs:enumeration value="International Baccalaureate score"/>
#     <xs:enumeration value="Letter grade/mark"/>
#     <xs:enumeration value="Mastery level"/>
#     <xs:enumeration value="Normal curve equivalent"/>
#     <xs:enumeration value="Normalized standard score"/>
#     <xs:enumeration value="Number score"/>
#     <xs:enumeration value="Pass-fail"/>
#     <xs:enumeration value="Percentile"/>
#     <xs:enumeration value="Percentile rank"/>
#     <xs:enumeration value="Proficiency level"/>
#     <xs:enumeration value="Promotion score"/>
#     <xs:enumeration value="Ranking"/>
#     <xs:enumeration value="Ratio IQ's"/>
#     <xs:enumeration value="Raw score"/>
#     <xs:enumeration value="Scale score"/>
#     <xs:enumeration value="Standard age score"/>
#     <xs:enumeration value="Standard error measurement"/>
#     <xs:enumeration value="Stanine score"/>
#     <xs:enumeration value="Sten score"/>
#     <xs:enumeration value="Theta"/>
#     <xs:enumeration value="T-score"/>
#     <xs:enumeration value="Vertical score"/>
#     <xs:enumeration value="Workplace readiness score"/>
#     <xs:enumeration value="Z-score"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Not applicable"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentReportingMethodType
  include Enum

  AssessmentReportingMethodType.define :ACHIEVEMENT_PROFICIENCY_LEVEL, "Achievement/proficiency level"
  AssessmentReportingMethodType.define :ACT_SCORE, "ACT score"
  AssessmentReportingMethodType.define :ADAPTIVE_SCALE_SCORE, "Adaptive scale score"
  AssessmentReportingMethodType.define :AGE_SCORE, "Age score"
  AssessmentReportingMethodType.define :COLLEGE_BOARD_EXAMINATION_SCORES, "College Board examination scores"
  AssessmentReportingMethodType.define :COMPOSITE_RATING, "Composite Rating"
  AssessmentReportingMethodType.define :COMPOSITE_SCORE, "Composite Score"
  AssessmentReportingMethodType.define :COMPOSITION_SCORE, "Composition Score"
  AssessmentReportingMethodType.define :C_SCALED_SCORES, "C-scaled scores"
  AssessmentReportingMethodType.define :GRADE_EQUIVALENT_OR_GRADE_LEVEL_INDICATOR, "Grade equivalent or grade-level indicator"
  AssessmentReportingMethodType.define :GRADUATION_SCORE, "Graduation score"
  AssessmentReportingMethodType.define :GROWTH_VALUE_ADDED_INDEXING, "Growth/value-added/indexing"
  AssessmentReportingMethodType.define :INTERNATIONAL_BACCALAUREATE_SCORE, "International Baccalaureate score"
  AssessmentReportingMethodType.define :LETTER_GRADE_MARK, "Letter grade/mark"
  AssessmentReportingMethodType.define :MASTERY_LEVEL, "Mastery level"
  AssessmentReportingMethodType.define :NORMALIZED_STANDARD_SCORE, "Normalized standard score"
  AssessmentReportingMethodType.define :NORMAL_CURVE_EQUIVALENT, "Normal curve equivalent"
  AssessmentReportingMethodType.define :NUMBER_SCORE, "Number score"
  AssessmentReportingMethodType.define :OTHER, "Other"
  AssessmentReportingMethodType.define :PASS_FAIL, "Pass-fail"
  AssessmentReportingMethodType.define :PERCENTILE, "Percentile"
  AssessmentReportingMethodType.define :PERCENTILE_RANK, "Percentile rank"
  AssessmentReportingMethodType.define :PROFICIENCY_LEVEL, "Proficiency level"
  AssessmentReportingMethodType.define :PROMOTION_SCORE, "Promotion score"
  AssessmentReportingMethodType.define :RANKING, "Ranking"
  AssessmentReportingMethodType.define :RATIO_IQS, "Ratio IQ's"
  AssessmentReportingMethodType.define :RAW_SCORE, "Raw score"
  AssessmentReportingMethodType.define :SCALE_SCORE, "Scale score"
  AssessmentReportingMethodType.define :STANDARD_AGE_SCORE, "Standard age score"
  AssessmentReportingMethodType.define :STANDARD_ERROR_MEASUREMENT, "Standard error measurement"
  AssessmentReportingMethodType.define :STANINE_SCORE, "Stanine score"
  AssessmentReportingMethodType.define :STEN_SCORE, "Sten score"
  AssessmentReportingMethodType.define :THETA, "Theta"
  AssessmentReportingMethodType.define :T_SCORE, "T-score"
  AssessmentReportingMethodType.define :VERTICAL_SCORE, "Vertical score"
  AssessmentReportingMethodType.define :WORKPLACE_READINESS_SCORE, "Workplace readiness score"
  AssessmentReportingMethodType.define :Z_SCORE, "Z-score"
end
