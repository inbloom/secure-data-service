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

require_relative "Enum.rb"

# Enumerates the types of grading periods available.
# From Ed-Fi-Core.xsd:
# <xs:simpleType name="GradingPeriodType">
#   <xs:annotation>
#     <xs:documentation>The name of the period for which grades are reported.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="First Six Weeks"/>
#     <xs:enumeration value="Second Six Weeks"/>
#     <xs:enumeration value="Third Six Weeks"/>
#     <xs:enumeration value="Fourth Six Weeks"/>
#     <xs:enumeration value="Fifth Six Weeks"/>
#     <xs:enumeration value="Sixth Six Weeks"/>
#     <xs:enumeration value="First Semester"/>
#     <xs:enumeration value="Second Semester"/>
#     <xs:enumeration value="First Summer Session"/>
#     <xs:enumeration value="Second Summer Session"/>
#     <xs:enumeration value="Third Summer Session"/>
#     <xs:enumeration value="Summer Semester"/>
#     <xs:enumeration value="First Nine Weeks"/>
#     <xs:enumeration value="Second Nine Weeks"/>
#     <xs:enumeration value="Third Nine Weeks"/>
#     <xs:enumeration value="Fourth Nine Weeks"/>
#     <xs:enumeration value="First Trimester"/>
#     <xs:enumeration value="Second Trimester"/>
#     <xs:enumeration value="Third Trimester"/>
#     <xs:enumeration value="End of Year"/>
#   </xs:restriction>
# </xs:simpleType>
class GradingPeriodType
  include Enum

  GradingPeriodType.define :END_OF_YEAR, "End of Year"
  GradingPeriodType.define :FIFTH_SIX_WEEKS, "Fifth Six Weeks"
  GradingPeriodType.define :FIRST_NINE_WEEKS, "First Nine Weeks"
  GradingPeriodType.define :FIRST_SEMESTER, "First Semester"
  GradingPeriodType.define :FIRST_SIX_WEEKS, "First Six Weeks"
  GradingPeriodType.define :FIRST_SUMMER_SESSION, "First Summer Session"
  GradingPeriodType.define :FIRST_TRIMESTER, "First Trimester"
  GradingPeriodType.define :FOURTH_NINE_WEEKS, "Fourth Nine Weeks"
  GradingPeriodType.define :FOURTH_SIX_WEEKS, "Fourth Six Weeks"
  GradingPeriodType.define :SECOND_NINE_WEEKS, "Second Nine Weeks"
  GradingPeriodType.define :SECOND_SEMESTER, "Second Semester"
  GradingPeriodType.define :SECOND_SIX_WEEKS, "Second Six Weeks"
  GradingPeriodType.define :SECOND_SUMMER_SESSION, "Second Summer Session"
  GradingPeriodType.define :SECOND_TRIMESTER, "Second Trimester"
  GradingPeriodType.define :SIXTH_SIX_WEEKS, "Sixth Six Weeks"
  GradingPeriodType.define :SUMMER_SEMESTER, "Summer Semester"
  GradingPeriodType.define :THIRD_NINE_WEEKS, "Third Nine Weeks"
  GradingPeriodType.define :THIRD_SIX_WEEKS, "Third Six Weeks"
  GradingPeriodType.define :THIRD_SUMMER_SESSION, "Third Summer Session"
  GradingPeriodType.define :THIRD_TRIMESTER, "Third Trimester"
end
