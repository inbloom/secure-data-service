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

# Enumerates the types of credentials. From Ed-Fi-Core.xsd:
# <xs:simpleType name="CredentialType">
#   <xs:annotation>
#     <xs:documentation>An indication of the category of credential an individual holds.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Certification"/>
#     <xs:enumeration value="Endorsement"/>
#     <xs:enumeration value="Licensure"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Registration"/>
#   </xs:restriction>
# </xs:simpleType>
class CredentialType
  include Enum

  CredentialType.define :CERTIFICATION, "Certification"
  CredentialType.define :ENDORSEMENT, "Endorsement"
  CredentialType.define :LICENSURE, "Licensure"
  CredentialType.define :OTHER, "Other"
  CredentialType.define :REGISTRATION, "Registration"
end
