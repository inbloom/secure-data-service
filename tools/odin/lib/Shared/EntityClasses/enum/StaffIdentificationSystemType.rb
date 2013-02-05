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

# Enumerates the types of staff identification systems. From Ed-Fi-Core.xsd:
# <xs:simpleType name="StaffIdentificationSystemType">
#   <xs:annotation>
#     <xs:documentation>A coding scheme that is used for identification and record-keeping purposes by schools, social services, or other agencies to refer to a staff member.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Drivers License"/>
#     <xs:enumeration value="Health Record"/>
#     <xs:enumeration value="Medicaid"/>
#     <xs:enumeration value="Professional Certificate"/>
#     <xs:enumeration value="School"/>
#     <xs:enumeration value="District"/>
#     <xs:enumeration value="State"/>
#     <xs:enumeration value="Federal"/>
#     <xs:enumeration value="Other Federal"/>
#     <xs:enumeration value="Selective Service"/>
#     <xs:enumeration value="SSN"/>
#     <xs:enumeration value="US Visa"/>
#     <xs:enumeration value="PIN"/>
#     <xs:enumeration value="Canadian SIN"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class StaffIdentificationSystemType
  include Enum

  StaffIdentificationSystemType.define :CANADIAN_SIN, "Canadian SIN"
  StaffIdentificationSystemType.define :DISTRICT, "District"
  StaffIdentificationSystemType.define :DRIVERS_LICENSE, "Drivers License"
  StaffIdentificationSystemType.define :FEDERAL, "Federal"
  StaffIdentificationSystemType.define :HEALTH_RECORD, "Health Record"
  StaffIdentificationSystemType.define :MEDICAID, "Medicaid"
  StaffIdentificationSystemType.define :OTHER, "Other"
  StaffIdentificationSystemType.define :OTHER_FEDERAL, "Other Federal"
  StaffIdentificationSystemType.define :PIN, "PIN"
  StaffIdentificationSystemType.define :PROFESSIONAL_CERTIFICATE, "Professional Certificate"
  StaffIdentificationSystemType.define :SCHOOL, "School"
  StaffIdentificationSystemType.define :SELECTIVE_SERVICE, "Selective Service"
  StaffIdentificationSystemType.define :SSN, "SSN"
  StaffIdentificationSystemType.define :STATE, "State"
  StaffIdentificationSystemType.define :US_VISA, "US Visa"
end
