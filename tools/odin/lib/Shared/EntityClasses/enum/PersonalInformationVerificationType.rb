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

# Enumerates the mechanisms for verifying personal information. From Ed-Fi-Core.xsd:
# <xs:simpleType name="PersonalInformationVerificationType">
#   <xs:annotation>
#     <xs:documentation>The evidence presented to verify one's personal identity; for example: drivers license, passport, birth certificate, etc.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Baptismal or church certificate"/>
#     <xs:enumeration value="Birth certificate"/>
#     <xs:enumeration value="Drivers license"/>
#     <xs:enumeration value="Entry in family Bible"/>
#     <xs:enumeration value="Hospital certificate"/>
#     <xs:enumeration value="Immigration document/visa"/>
#     <xs:enumeration value="Life insurance policy"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Other non-official document"/>
#     <xs:enumeration value="Other official document"/>
#     <xs:enumeration value="Parents affidavit"/>
#     <xs:enumeration value="Passport"/>
#     <xs:enumeration value="Physicians certificate"/>
#     <xs:enumeration value="Previously verified school records"/>
#     <xs:enumeration value="State-issued ID"/>
#   </xs:restriction>
# </xs:simpleType>
class PersonalInformationVerificationType
  include Enum

  PersonalInformationVerificationType.define :BAPTISMAL_OR_CHURCH_CERTIFICATE, "Baptismal or church certificate"
  PersonalInformationVerificationType.define :BIRTH_CERTIFICATE, "Birth certificate"
  PersonalInformationVerificationType.define :DRIVERS_LICENSE, "Drivers license"
  PersonalInformationVerificationType.define :ENTRY_IN_FAMILY_BIBLE, "Entry in family Bible"
  PersonalInformationVerificationType.define :HOSPITAL_CERTIFICATE, "Hospital certificate"
  PersonalInformationVerificationType.define :IMMIGRATION_DOCUMENT_VISA, "Immigration document/visa"
  PersonalInformationVerificationType.define :LIFE_INSURANCE_POLICY, "Life insurance policy"
  PersonalInformationVerificationType.define :OTHER, "Other"
  PersonalInformationVerificationType.define :OTHER_NON_OFFICIAL_DOCUMENT, "Other non-official document"
  PersonalInformationVerificationType.define :OTHER_OFFICIAL_DOCUMENT, "Other official document"
  PersonalInformationVerificationType.define :PARENTS_AFFIDAVIT, "Parents affidavit"
  PersonalInformationVerificationType.define :PASSPORT, "Passport"
  PersonalInformationVerificationType.define :PHYSICIANS_CERTIFICATE, "Physicians certificate"
  PersonalInformationVerificationType.define :PREVIOUSLY_VERIFIED_SCHOOL_RECORDS, "Previously verified school records"
  PersonalInformationVerificationType.define :STATE_ISSUED_ID, "State-issued ID"
end
