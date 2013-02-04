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

# Enumerates the credit earned method types. From Ed-Fi-Core.xsd:
# <xs:simpleType name="MethodCreditEarnedType">
#   <xs:annotation>
#     <xs:documentation>The method the credits were earned, for example:  Classroom, Examination, Transfer</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Adult education credit"/>
#     <xs:enumeration value="Classroom credit"/>
#     <xs:enumeration value="Converted occupational experience credit"/>
#     <xs:enumeration value="Correspondence credit"/>
#     <xs:enumeration value="Credit by examination"/>
#     <xs:enumeration value="Credit recovery"/>
#     <xs:enumeration value="Online credit"/>
#     <xs:enumeration value="Transfer credit "/>
#     <xs:enumeration value="Vocational credit"/>
#   </xs:restriction>
# </xs:simpleType>
class MethodCreditEarnedType
  include Enum

  MethodCreditEarnedType.define :ADULT_EDUCATION_CREDIT, "Adult education credit"
  MethodCreditEarnedType.define :CLASSROOM_CREDIT, "Classroom credit"
  MethodCreditEarnedType.define :CONVERTED_OCCUPATIONAL_EXPERIENCE_CREDIT, "Converted occupational experience credit"
  MethodCreditEarnedType.define :CORRESPONDENCE_CREDIT, "Correspondence credit"
  MethodCreditEarnedType.define :CREDIT_BY_EXAMINATION, "Credit by examination"
  MethodCreditEarnedType.define :CREDIT_RECOVERY, "Credit recovery"
  MethodCreditEarnedType.define :ONLINE_CREDIT, "Online credit"
  MethodCreditEarnedType.define :TRANSFER_CREDIT, "Transfer credit "
  MethodCreditEarnedType.define :VOCATIONAL_CREDIT, "Vocational credit"
end
