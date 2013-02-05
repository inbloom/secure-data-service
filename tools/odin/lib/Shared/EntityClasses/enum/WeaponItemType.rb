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

# Enumerates the different types of weapons. From Ed-Fi-Core.xsd:
# <xs:simpleType name="WeaponItemType">
#   <xs:annotation>
#     <xs:documentation>The enumeration items for the types of weapon used during an incident. </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Firearm"/>
#     <xs:enumeration value="Illegal Knife"/>
#     <xs:enumeration value="Non-Illegal Knife"/>
#     <xs:enumeration value="Club"/>
#     <xs:enumeration value="Other Sharp Objects"/>
#     <xs:enumeration value="Other Object"/>
#     <xs:enumeration value="Substance Used as Weapon"/>
#     <xs:enumeration value="Knife"/>
#     <xs:enumeration value="Unknown"/>
#     <xs:enumeration value="None"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class WeaponItemType
  include Enum

  WeaponItemType.define :CLUB, "Club"
  WeaponItemType.define :FIREARM, "Firearm"
  WeaponItemType.define :ILLEGAL_KNIFE, "Illegal Knife"
  WeaponItemType.define :KNIFE, "Knife"
  WeaponItemType.define :NONE, "None"
  WeaponItemType.define :NON_ILLEGAL_KNIFE, "Non-Illegal Knife"
  WeaponItemType.define :OTHER, "Other"
  WeaponItemType.define :OTHER_OBJECT, "Other Object"
  WeaponItemType.define :OTHER_SHARP_OBJECTS, "Other Sharp Objects"
  WeaponItemType.define :SUBSTANCE_USED_AS_WEAPON, "Substance Used as Weapon"
  WeaponItemType.define :UNKNOWN, "Unknown"
end
