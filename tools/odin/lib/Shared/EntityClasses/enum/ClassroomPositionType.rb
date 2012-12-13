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

# Enumerates the types of classroom positions held by teachers. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ClassroomPositionType">
#   <xs:annotation>
#     <xs:documentation>The type of position the staff member holds in the specific class/section.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Teacher of Record"/>
#     <xs:enumeration value="Assistant Teacher"/>
#     <xs:enumeration value="Support Teacher"/>
#     <xs:enumeration value="Substitute Teacher"/>
#   </xs:restriction>
# </xs:simpleType>
class ClassroomPositionType
  include Enum

  ClassroomPositionType.define :ASSISTANT_TEACHER, "Assistant Teacher"
  ClassroomPositionType.define :SUBSTITUTE_TEACHER, "Substitute Teacher"
  ClassroomPositionType.define :SUPPORT_TEACHER, "Support Teacher"
  ClassroomPositionType.define :TEACHER_OF_RECORD, "Teacher of Record"

  # translates the specified Symbol into the String representation of a classroom position type
  # -> returns nil if the Symbol doesn't exist
  def self.to_string(key)
    const_get(key)
  end

  # translates the specified String representation of a classroom position type into a Symbol
  # -> returns nil if the String representation doesn't map to a Symbol
  def self.to_symbol(value)
    get_key(value)
  end
end
