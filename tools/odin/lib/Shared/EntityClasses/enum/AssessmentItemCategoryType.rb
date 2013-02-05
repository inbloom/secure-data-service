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

# Enumerates the different types of assessment item categories. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ItemCategoryType">
#   <xs:annotation>
#     <xs:documentation>Category or type of the assessment item.  For example:
#     Multiple choice
#     Analytic
#     Prose
#     ....
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Analytic"/>
#     <xs:enumeration value="List Question"/>
#     <xs:enumeration value="Math Matrix"/>
#     <xs:enumeration value="Matching"/>
#     <xs:enumeration value="Multiple Choice"/>
#     <xs:enumeration value="Prose"/>
#     <xs:enumeration value="Rubric"/>
#     <xs:enumeration value="True-False"/>
#   </xs:restriction>
# </xs:simpleType>
class AssessmentItemCategoryType
  include Enum

  AssessmentItemCategoryType.define :ANALYTIC, "Analytic"
  AssessmentItemCategoryType.define :LIST_QUESTION, "List Question"
  AssessmentItemCategoryType.define :MATCHING, "Matching"
  AssessmentItemCategoryType.define :MATH_MATRIX, "Math Matrix"
  AssessmentItemCategoryType.define :MULTIPLE_CHOICE, "Multiple Choice"
  AssessmentItemCategoryType.define :PROSE, "Prose"
  AssessmentItemCategoryType.define :RUBRIC, "Rubric"
  AssessmentItemCategoryType.define :TRUE_FALSE, "True-False"
end
