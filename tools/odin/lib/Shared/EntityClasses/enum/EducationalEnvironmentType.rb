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

require_relative "Enum.rb"

# Enumerates the types of educational environments. From Ed-Fi-Core.xsd:
# <xs:simpleType name="EducationalEnvironmentType">
#   <xs:annotation>
#     <xs:documentation>The setting in which a child receives education and related services.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Classroom"/>
#     <xs:enumeration value="Homebound"/>
#     <xs:enumeration value="Hospital class"/>
#     <xs:enumeration value="In-school suspension"/>
#     <xs:enumeration value="Laboratory"/>
#     <xs:enumeration value="Mainstream (Special Education) "/>
#     <xs:enumeration value="Off-school center"/>
#     <xs:enumeration value="Pull-out class"/>
#     <xs:enumeration value="Resource room"/>
#     <xs:enumeration value="Self-contained (Special Education) "/>
#     <xs:enumeration value="Self-study"/>
#     <xs:enumeration value="Shop"/>
#   </xs:restriction>
# </xs:simpleType>
class EducationalEnvironmentType
  include Enum

  EducationalEnvironmentType.define :CLASSROOM, "Classroom"
  EducationalEnvironmentType.define :HOMEBOUND, "Homebound"
  EducationalEnvironmentType.define :HOSPITAL_CLASS, "Hospital class"
  EducationalEnvironmentType.define :IN_SCHOOL_SUSPENSION, "In-school suspension"
  EducationalEnvironmentType.define :LABORATORY, "Laboratory"
  EducationalEnvironmentType.define :MAINSTREAM_SPECIAL_EDUCATION, "Mainstream (Special Education) "
  EducationalEnvironmentType.define :OFF_SCHOOL_CENTER, "Off-school center"
  EducationalEnvironmentType.define :PULL_OUT_CLASS, "Pull-out class"
  EducationalEnvironmentType.define :RESOURCE_ROOM, "Resource room"
  EducationalEnvironmentType.define :SELF_CONTAINED_SPECIAL_EDUCATION, "Self-contained (Special Education) "
  EducationalEnvironmentType.define :SELF_STUDY, "Self-study"
  EducationalEnvironmentType.define :SHOP, "Shop"
end
