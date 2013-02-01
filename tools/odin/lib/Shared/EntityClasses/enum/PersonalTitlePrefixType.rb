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

# Enumerates the types of personal title prefixes. From Ed-Fi-Core.xsd:
# <xs:simpleType name="PersonalTitlePrefixType">
#   <xs:annotation>
#     <xs:documentation>A prefix used to denote the title, degree, position or seniority of the person.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Colonel"/>
#     <xs:enumeration value="Dr"/>
#     <xs:enumeration value="Mr"/>
#     <xs:enumeration value="Mrs"/>
#     <xs:enumeration value="Ms"/>
#     <xs:enumeration value="Reverend"/>
#     <xs:enumeration value="Sr"/>
#     <xs:enumeration value="Sister"/>
#  </xs:restriction>
# </xs:simpleType>
class PersonalTitlePrefixType
  include Enum

  PersonalTitlePrefixType.define :COLONEL, "Colonel"
  PersonalTitlePrefixType.define :DR, "Dr"
  PersonalTitlePrefixType.define :MR, "Mr"
  PersonalTitlePrefixType.define :MRS, "Mrs"
  PersonalTitlePrefixType.define :MS, "Ms"
  PersonalTitlePrefixType.define :REVEREND, "Reverend"
  PersonalTitlePrefixType.define :SISTER, "Sister"
  PersonalTitlePrefixType.define :SR, "Sr"
end
