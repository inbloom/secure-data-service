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

# Enumerates the types of assessment categories. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AssessmentCategoryType">
#   <xs:annotation>
#     <xs:documentation>The category of an assessment based on format and content. For example:
#     Achievement test
#     Advanced placement test
#     Alternate assessment/grade-level standards
#     Attitudinal test
#     Cognitive and perceptual skills test
#     ...
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Achievement test"/>
#     <xs:enumeration value="Advanced Placement"/>
#     <xs:enumeration value="International Baccalaureate"/>
#     <xs:enumeration value="Aptitude test"/>
#     <xs:enumeration value="Attitudinal test"/>
#     <xs:enumeration value="Benchmark test"/>
#     <xs:enumeration value="Class test"/>
#     <xs:enumeration value="class quiz"/>
#     <xs:enumeration value="College entrance exam"/>
#     <xs:enumeration value="Cognitive and perceptual skills test"/>
#     <xs:enumeration value="Developmental observation"/>
#     <xs:enumeration value="English proficiency screening test"/>
#     <xs:enumeration value="Foreign language proficiency test"/>
#     <xs:enumeration value="Interest inventory"/>
#     <xs:enumeration value="Manual dexterity test"/>
#     <xs:enumeration value="Mental ability (intelligence) test"/>
#     <xs:enumeration value="Performance assessment"/>
#     <xs:enumeration value="Personality test"/>
#     <xs:enumeration value="Portfolio assessment"/>
#     <xs:enumeration value="Psychological test"/>
#     <xs:enumeration value="Psychomotor test"/>
#     <xs:enumeration value="Reading readiness test"/>
#     <xs:enumeration value="State summative assessment 3-8 general"/>
#     <xs:enumeration value="State high school subject assessment"/>
#     <xs:enumeration value="State high school course assessment"/>
#     <xs:enumeration value="State alternative assessment/grade-level standards"/>
#     <xs:enumeration value="State alternative assessment/modified standards"/>
#     <xs:enumeration value="State alternate assessment/ELL"/>
#     <xs:enumeration value="State English proficiency test"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentCategoryType
  include Enum

  AssessmentCategoryType.define :ACHIEVEMENT_TEST, "Achievement test"
  AssessmentCategoryType.define :ADVANCED_PLACEMENT, "Advanced Placement"
  AssessmentCategoryType.define :APTITUDE_TEST, "Aptitude test"
  AssessmentCategoryType.define :ATTITUDINAL_TEST, "Attitudinal test"
  AssessmentCategoryType.define :BENCHMARK_TEST, "Benchmark test"
  AssessmentCategoryType.define :CLASS_QUIZ, "class quiz"
  AssessmentCategoryType.define :CLASS_TEST, "Class test"
  AssessmentCategoryType.define :COGNITIVE_AND_PERCEPTUAL_SKILLS_TEST, "Cognitive and perceptual skills test"
  AssessmentCategoryType.define :COLLEGE_ENTRANCE_EXAM, "College entrance exam"
  AssessmentCategoryType.define :DEVELOPMENTAL_OBSERVATION, "Developmental observation"
  AssessmentCategoryType.define :ENGLISH_PROFICIENCY_SCREENING_TEST, "English proficiency screening test"
  AssessmentCategoryType.define :FOREIGN_LANGUAGE_PROFICIENCY_TEST, "Foreign language proficiency test"
  AssessmentCategoryType.define :INTEREST_INVENTORY, "Interest inventory"
  AssessmentCategoryType.define :INTERNATIONAL_BACCALAUREATE, "International Baccalaureate"
  AssessmentCategoryType.define :MANUAL_DEXTERITY_TEST, "Manual dexterity test"
  AssessmentCategoryType.define :MENTAL_ABILITY_INTELLIGENCE_TEST, "Mental ability (intelligence) test"
  AssessmentCategoryType.define :OTHER, "Other"
  AssessmentCategoryType.define :PERFORMANCE_ASSESSMENT, "Performance assessment"
  AssessmentCategoryType.define :PERSONALITY_TEST, "Personality test"
  AssessmentCategoryType.define :PORTFOLIO_ASSESSMENT, "Portfolio assessment"
  AssessmentCategoryType.define :PSYCHOLOGICAL_TEST, "Psychological test"
  AssessmentCategoryType.define :PSYCHOMOTOR_TEST, "Psychomotor test"
  AssessmentCategoryType.define :READING_READINESS_TEST, "Reading readiness test"
  AssessmentCategoryType.define :STATE_ALTERNATE_ASSESSMENT_ELL, "State alternate assessment/ELL"
  AssessmentCategoryType.define :STATE_ALTERNATIVE_ASSESSMENT_GRADE_LEVEL_STANDARDS, "State alternative assessment/grade-level standards"
  AssessmentCategoryType.define :STATE_ALTERNATIVE_ASSESSMENT_MODIFIED_STANDARDS, "State alternative assessment/modified standards"
  AssessmentCategoryType.define :STATE_ENGLISH_PROFICIENCY_TEST, "State English proficiency test"
  AssessmentCategoryType.define :STATE_HIGH_SCHOOL_COURSE_ASSESSMENT, "State high school course assessment"
  AssessmentCategoryType.define :STATE_HIGH_SCHOOL_SUBJECT_ASSESSMENT, "State high school subject assessment"
  AssessmentCategoryType.define :STATE_SUMMATIVE_ASSESSMENT_3_8_GENERAL, "State summative assessment 3-8 general"
end
