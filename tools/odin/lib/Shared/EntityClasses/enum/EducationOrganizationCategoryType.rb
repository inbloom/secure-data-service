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

# Enumerates the types of staff classifications. From Ed-Fi-Core.xsd:
# <xs:simpleType name="StaffClassificationType">
#   <xs:annotation>
#     <xs:documentation>The classification of the education agency within the geographic boundaries of a state according to the level of administrative and operational control granted by the state.
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="State Education Agency"/>
#     <xs:enumeration value="Education Service Center"/>
#     <xs:enumeration value="Local Education Agency"/>
#     <xs:enumeration value="School"/>
#   </xs:restriction>
# </xs:simpleType>
class EducationOrganizationCategoryType
  include Enum

  EducationOrganizationCategoryType.define :EDUCATION_SERVICE_CENTER, "Education Service Center"
  EducationOrganizationCategoryType.define :LOCAL_EDUCATION_AGENCY, "Local Education Agency"
  EducationOrganizationCategoryType.define :SCHOOL, "School"
  EducationOrganizationCategoryType.define :STATE_EDUCATION_AGENCY, "State Education Agency"

  # translates the specified Symbol into the String representation of the education organization category type
  def self.to_string(key)
    const_get(key)
  end
end
