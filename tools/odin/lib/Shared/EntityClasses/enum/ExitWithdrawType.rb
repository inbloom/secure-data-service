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

# Enumerates the types of exit withdrawals. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ExitWithdrawType">
#   <xs:annotation>
#     <xs:documentation>The circumstances under which the student exited from membership in an educational institution.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Student is in a different public school in the same local education agency"/>
#     <xs:enumeration value="Transferred to a public school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transferred to a public school in a different state"/>
#     <xs:enumeration value="Transferred to a private, non-religiously-affiliated school in the same local education agency"/>
#     <xs:enumeration value="Transferred to a private, non-religiously-affiliated school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transferred to a private, non-religiously-affiliated school in a different state"/>
#     <xs:enumeration value="Transferred to a private, religiously-affiliated school in the same local education agency"/>
#     <xs:enumeration value="Transferred to a private, religiously-affiliated school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transferred to a private, religiously-affiliated school in a different state"/>
#     <xs:enumeration value="Transferred to a school outside of the country"/>
#     <xs:enumeration value="Transferred to an institution"/>
#     <xs:enumeration value="Transferred to home schooling"/>
#     <xs:enumeration value="Transferred to a charter school"/>
#     <xs:enumeration value="Graduated with regular, advanced, International Baccalaureate, or other type of diploma"/>
#     <xs:enumeration value="Completed school with other credentials"/>
#     <xs:enumeration value="Died or is permanently incapacitated"/>
#     <xs:enumeration value="Withdrawn due to illness"/>
#     <xs:enumeration value="Expelled or involuntarily withdrawn"/>
#     <xs:enumeration value="Reached maximum age for services"/>
#     <xs:enumeration value="Discontinued schooling"/>
#     <xs:enumeration value="Completed grade 12, but did not meet all graduation requirements"/>
#     <xs:enumeration value="Enrolled in a postsecondary early admission program, eligible to return"/>
#     <xs:enumeration value="Not enrolled, unknown status"/>
#     <xs:enumeration value="Student is in the same local education agency and receiving education services, but is not assigned to a particular school"/>
#     <xs:enumeration value="Enrolled in an adult education or training program"/>
#     <xs:enumeration value="Completed a state-recognized vocational education program"/>
#     <xs:enumeration value="Not enrolled, eligible to return"/>
#     <xs:enumeration value="Enrolled in a foreign exchange program, eligible to return"/>
#     <xs:enumeration value="Withdrawn from school, under the age for compulsory attendance; eligible to return"/>
#     <xs:enumeration value="Exited"/>
#     <xs:enumeration value="Student is in a charter school managed by the same local education agency"/>
#     <xs:enumeration value="Completed with a state-recognized equivalency certificate"/>
#     <xs:enumeration value="Removed by Child Protective Services"/>
#     <xs:enumeration value="Transferred to a private school in the state"/>
#     <xs:enumeration value="Graduated outside of state prior to enrollment"/>
#     <xs:enumeration value="Completed equivalency certificate outside of state"/>
#     <xs:enumeration value="Enrolled in University High School Diploma Program"/>
#     <xs:enumeration value="Court ordered to a GED program, has not earned a GED"/>
#     <xs:enumeration value="Incarcerated in a state jail or federal penitentiary as an adult"/>
#     <xs:enumeration value="Graduated from another state under Interstate Compact on Educational Opportunity for Military Children"/>
#     <xs:enumeration value="Dropout"/>
#     <xs:enumeration value="End of school year"/>
#     <xs:enumeration value="Invalid enrollment"/>
#     <xs:enumeration value="No show"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class ExitWithdrawType
  include Enum

  ExitWithdrawType.define :DISCONTINUED_SCHOOLING, "Discontinued schooling"
  ExitWithdrawType.define :END_OF_SCHOOL_YEAR, "End of school year"
  ExitWithdrawType.define :EXITED, "Exited"
  ExitWithdrawType.define :INVALID_ENROLLMENT, "Invalid enrollment"
  ExitWithdrawType.define :NO_SHOW, "No show"
  ExitWithdrawType.define :OTHER, "Other"
end
