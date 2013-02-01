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

# Enumerates the types of telephone numbers. From Ed-Fi-Core.xsd:
# <xs:simpleType name="TelephoneNumberType">
#   <xs:annotation>
#     <xs:documentation>The type of communication number listed for an individual.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Fax"/>
#     <xs:enumeration value="Emergency 1"/>
#     <xs:enumeration value="Emergency 2"/>
#     <xs:enumeration value="Home"/>
#     <xs:enumeration value="Mobile"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Unlisted"/>
#     <xs:enumeration value="Work"/>
#   </xs:restriction>
# </xs:simpleType>
class TelephoneNumberType
  include Enum

  TelephoneNumberType.define :EMERGENCY_1, "Emergency 1"
  TelephoneNumberType.define :EMERGENCY_2, "Emergency 2"
  TelephoneNumberType.define :FAX, "Fax"
  TelephoneNumberType.define :HOME, "Home"
  TelephoneNumberType.define :MOBILE, "Mobile"
  TelephoneNumberType.define :OTHER, "Other"
  TelephoneNumberType.define :UNLISTED, "Unlisted"
  TelephoneNumberType.define :WORK, "Work"
end
