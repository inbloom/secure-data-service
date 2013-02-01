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

# Enumerates the types of grade entries. From Ed-Fi-Core.xsd:
# <xs:simpleType name="EntryType">
#   <xs:annotation>
#     <xs:documentation>The process by which a student enters a school during a given academic session.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Transfer from a public school in the same local education agency"/>
#     <xs:enumeration value="Transfer from a public school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transfer from a public school in a different state"/>
#     <xs:enumeration value="Transfer from a private, non-religiously-affiliated school in the same local education agency"/>
#     <xs:enumeration value="Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transfer from a private, non-religiously-affiliated school in a different state"/>
#     <xs:enumeration value="Transfer from a private, religiously-affiliated school in the same local education agency"/>
#     <xs:enumeration value="Transfer from a private, religiously-affiliated school in a different local education agency in the same state"/>
#     <xs:enumeration value="Transfer from a private, religiously-affiliated school in a different state"/>
#     <xs:enumeration value="Transfer from a school outside of the country"/>
#     <xs:enumeration value="Transfer from an institution"/>
#     <xs:enumeration value="Transfer from a charter school"/>
#     <xs:enumeration value="Transfer from home schooling"/>
#     <xs:enumeration value="Re-entry from the same school with no interruption of schooling"/>
#     <xs:enumeration value="Re-entry after a voluntary withdrawal"/>
#     <xs:enumeration value="Re-entry after an involuntary withdrawal"/>
#     <xs:enumeration value="Original entry into a United States school"/>
#     <xs:enumeration value="Original entry into a United States school from a foreign country with no interruption in schooling"/>
#     <xs:enumeration value="Original entry into a United States school from a foreign country with an interruption in schooling"/>
#     <xs:enumeration value="Next year school"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class GradeEntryType
  include Enum

  GradeEntryType.define :NEXT_YEAR_SCHOOL, "Next year school"
  GradeEntryType.define :ORIGINAL_ENTRY_INTO_US_SCHOOL, "Original entry into a United States school"
  GradeEntryType.define :ORIGINAL_ENTRY_INTO_US_SCHOOL_FROM_FOREIGN_COUNTRY_WITH_INTERRUPTION, "Original entry into a United States school from a foreign country with an interruption in schooling"
  GradeEntryType.define :ORIGINAL_ENTRY_INTO_US_SCHOOL_FROM_FOREIGN_COUNTRY_WITH_NO_INTERRUPTION, "Original entry into a United States school from a foreign country with no interruption in schooling"
  GradeEntryType.define :OTHER, "Other"
  GradeEntryType.define :RE_ENTRY_AFTER_INVOLUNTARY_WITHDRAWAL, "Re-entry after an involuntary withdrawal"
  GradeEntryType.define :RE_ENTRY_AFTER_VOLUNTARY_WITHDRAWAL, "Re-entry after a voluntary withdrawal"
  GradeEntryType.define :RE_ENTRY_FROM_SAME_SCHOOL_WITH_NO_INTERRUPTION, "Re-entry from the same school with no interruption of schooling"
  GradeEntryType.define :TRANSFER_FROM_CHARTER_SCHOOL, "Transfer from a charter school"
  GradeEntryType.define :TRANSFER_FROM_HOME_SCHOOL, "Transfer from home schooling"
  GradeEntryType.define :TRANSFER_FROM_INSTITUTION, "Transfer from an institution"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_NON_RELIGIOUS_SCHOOL_IN_DIFFERENT_LEA, "Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_NON_RELIGIOUS_SCHOOL_IN_DIFFERENT_STATE, "Transfer from a private, non-religiously-affiliated school in a different state"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_NON_RELIGIOUS_SCHOOL_IN_SAME_LEA, "Transfer from a private, non-religiously-affiliated school in the same local education agency"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_RELIGIOUS_SCHOOL_IN_DIFFERENT_LEA, "Transfer from a private, religiously-affiliated school in a different local education agency in the same state"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_RELIGIOUS_SCHOOL_IN_DIFFERENT_STATE, "Transfer from a private, religiously-affiliated school in a different state"
  GradeEntryType.define :TRANSFER_FROM_PRIVATE_RELIGIOUS_SCHOOL_IN_SAME_LEA, "Transfer from a private, religiously-affiliated school in the same local education agency"
  GradeEntryType.define :TRANSFER_FROM_PUBLIC_SCHOOL_IN_DIFFERENT_LEA, "Transfer from a public school in the same local education agency"
  GradeEntryType.define :TRANSFER_FROM_PUBLIC_SCHOOL_IN_DIFFERENT_STATE, "Transfer from a public school in a different state"
  GradeEntryType.define :TRANSFER_FROM_PUBLIC_SCHOOL_IN_SAME_LEA, "Transfer from a public school in the same local education agency"
  GradeEntryType.define :TRANSFER_FROM_SCHOOL_OUTSIDE_OF_COUNTRY, "Transfer from a school outside of the country"
end
