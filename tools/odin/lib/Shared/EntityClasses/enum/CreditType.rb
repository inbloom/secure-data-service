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

# Enumerates the types of credits earned (context of academic record). From Ed-Fi-Core.xsd:
# <xs:simpleType name="CreditType">
#   <xs:annotation>
#     <xs:documentation>The type of credits or units of value awarded for the completion of a course.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Carnegie unit"/>
#     <xs:enumeration value="Semester hour credit"/>
#     <xs:enumeration value="Trimester hour credit"/>
#     <xs:enumeration value="Quarter hour credit"/>
#     <xs:enumeration value="Nine month year hour credit"/>
#     <xs:enumeration value="Twelve month year hour credit"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class CreditType
  include Enum

  CreditType.define :CARNEGIE_UNIT, "Carnegie unit"
  CreditType.define :NINE_MONTH_YEAR_HOUR_CREDIT, "Nine month year hour credit"
  CreditType.define :OTHER, "Other"
  CreditType.define :QUARTER_HOUR_CREDIT, "Quarter hour credit"
  CreditType.define :SEMESTER_HOUR_CREDIT, "Semester hour credit"
  CreditType.define :TRIMESTER_HOUR_CREDIT, "Trimester hour credit"
  CreditType.define :TWELVE_MONTH_YEAR_HOUR_CREDIT, "Twelve month year hour credit"
end
