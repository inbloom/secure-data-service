=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

# Enumerates the types of program assignments. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ProgramAssignmentType">
#   <xs:annotation>
#     <xs:documentation>The name of the education program for which a teacher is assigned to a school.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Regular Education"/>
#     <xs:enumeration value="Title I-Academic"/>
#     <xs:enumeration value="Title I-Non-Academic"/>
#     <xs:enumeration value="Special Education"/>
#     <xs:enumeration value="Bilingual/English as a Second Language"/>
#   </xs:restriction>
# </xs:simpleType>
class ProgramAssignmentType
  include Enum

  ProgramAssignmentType.define :BILINGUAL_ENGLISH_AS_A_SECOND_LANGUAGE, "Bilingual/English as a Second Language"
  ProgramAssignmentType.define :REGULAR_EDUCATION, "Regular Education"
  ProgramAssignmentType.define :SPECIAL_EDUCATION, "Special Education"
  ProgramAssignmentType.define :TITLE_I_ACADEMIC, "Title I-Academic"
  ProgramAssignmentType.define :TITLE_I_NON_ACADEMIC, "Title I-Non-Academic"

  # translates the specified Symbol into the String representation of the program assignment type
  # -> returns nil if the Symbol doesn't exist
  def self.to_string(key)
    const_get(key)
  end

  # translates the specified String representation of the program assignment type into a Symbol
  # -> returns nil if the String representation doesn't map to a Symbol
  def self.to_symbol(value)
    get_key(value)
  end
end
