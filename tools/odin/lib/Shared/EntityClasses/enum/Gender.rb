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

# Enumerates the types of genders. From ed-fi-core.xsd:
# <xs:simpleType name="SexType">
#   <xs:annotation>
#     <xs:documentation>A person's gender.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Female"/>
#     <xs:enumeration value="Male"/>
#   </xs:restriction>
# </xs:simpleType>
class Gender
  include Enum

  Gender.define :FEMALE, "Female"
  Gender.define :MALE, "Male"
end
