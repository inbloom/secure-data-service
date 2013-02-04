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

# Enumerates the types of participation a student can engage in (context: discipline incidents). From Ed-Fi-Core.xsd:
# <xs:simpleType name="StudentParticipationCodeType">
#   <xs:annotation>
#     <xs:documentation>The role or type of participation of a student in a discipline incident; for example: Victim, Perpetrator, Witness, Reporter</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Victim"/>
#     <xs:enumeration value="Perpetrator"/>
#     <xs:enumeration value="Witness"/>
#     <xs:enumeration value="Reporter"/>
#   </xs:restriction>
# </xs:simpleType>
class StudentParticipationCodeType
  include Enum

  StudentParticipationCodeType.define :PERPETRATOR, "Perpetrator"
  StudentParticipationCodeType.define :REPORTER, "Reporter"
  StudentParticipationCodeType.define :VICTIM, "Victim"
  StudentParticipationCodeType.define :WITNESS, "Witness"
end
