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

# Enumerates the types of teaching credentials. From Ed-Fi-Core.xsd:
# <xs:simpleType name="TeachingCredentialType">
#   <xs:annotation>
#     <xs:documentation>An indication of the category of a legal document giving authorization to perform teaching assignment services.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Emergency"/>
#     <xs:enumeration value="Emergency Certified"/>
#     <xs:enumeration value="Emergency Non-Certified"/>
#     <xs:enumeration value="Emergency Teaching"/>
#     <xs:enumeration value="Intern"/>
#     <xs:enumeration value="Master"/>
#     <xs:enumeration value="Nonrenewable"/>
#     <xs:enumeration value="One Year"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Paraprofessional"/>
#     <xs:enumeration value="Professional"/>
#     <xs:enumeration value="Probationary"/>
#     <xs:enumeration value="Provisional"/>
#     <xs:enumeration value="Regular"/>
#     <xs:enumeration value="Retired"/>
#     <xs:enumeration value="Specialist"/>
#     <xs:enumeration value="Substitute"/>
#     <xs:enumeration value="TeacherAssistant"/>
#     <xs:enumeration value="Temporary"/>
#     <xs:enumeration value="Special Assignment"/>
#     <xs:enumeration value="Standard"/>
#     <xs:enumeration value="Standard Professional"/>
#     <xs:enumeration value="Temporary Classroom"/>
#     <xs:enumeration value="Temporary Exemption"/>
#     <xs:enumeration value="Unknown"/>
#     <xs:enumeration value="Unknown Permit"/>
#     <xs:enumeration value="Vocational"/>
#     <xs:enumeration value="Standard Paraprofessional"/>
#     <xs:enumeration value="Probationary Extension"/>
#     <xs:enumeration value="Probationary Second Extension"/>
#     <xs:enumeration value="Visiting International Teacher"/>
#     <xs:enumeration value="District Local"/>
#   </xs:restriction>
# </xs:simpleType>
class TeachingCredentialType
  include Enum

  TeachingCredentialType.define :DISTRICT_LOCAL, "District Local"
  TeachingCredentialType.define :EMERGENCY, "Emergency"
  TeachingCredentialType.define :EMERGENCY_CERTIFIED, "Emergency Certified"
  TeachingCredentialType.define :EMERGENCY_NON_CERTIFIED, "Emergency Non-Certified"
  TeachingCredentialType.define :EMERGENCY_TEACHING, "Emergency Teaching"
  TeachingCredentialType.define :INTERN, "Intern"
  TeachingCredentialType.define :MASTER, "Master"
  TeachingCredentialType.define :NONRENEWABLE, "Nonrenewable"
  TeachingCredentialType.define :ONE_YEAR, "One Year"
  TeachingCredentialType.define :OTHER, "Other"
  TeachingCredentialType.define :PARAPROFESSIONAL, "Paraprofessional"
  TeachingCredentialType.define :PROBATIONARY, "Probationary"
  TeachingCredentialType.define :PROBATIONARY_EXTENSION, "Probationary Extension"
  TeachingCredentialType.define :PROBATIONARY_SECOND_EXTENSION, "Probationary Second Extension"
  TeachingCredentialType.define :PROFESSIONAL, "Professional"
  TeachingCredentialType.define :PROVISIONAL, "Provisional"
  TeachingCredentialType.define :REGULAR, "Regular"
  TeachingCredentialType.define :RETIRED, "Retired"
  TeachingCredentialType.define :SPECIALIST, "Specialist"
  TeachingCredentialType.define :SPECIAL_ASSIGNMENT, "Special Assignment"
  TeachingCredentialType.define :STANDARD, "Standard"
  TeachingCredentialType.define :STANDARD_PARAPROFESSIONAL, "Standard Paraprofessional"
  TeachingCredentialType.define :STANDARD_PROFESSIONAL, "Standard Professional"
  TeachingCredentialType.define :SUBSTITUTE, "Substitute"
  TeachingCredentialType.define :TEACHER_ASSISTANT, "TeacherAssistant"
  TeachingCredentialType.define :TEMPORARY, "Temporary"
  TeachingCredentialType.define :TEMPORARY_CLASSROOM, "Temporary Classroom"
  TeachingCredentialType.define :TEMPORARY_EXEMPTION, "Temporary Exemption"
  TeachingCredentialType.define :UNKNOWN, "Unknown"
  TeachingCredentialType.define :UNKNOWN_PERMIT, "Unknown Permit"
  TeachingCredentialType.define :VISITING_INTERNATIONAL_TEACHER, "Visiting International Teacher"
  TeachingCredentialType.define :VOCATIONAL, "Vocational"
end
