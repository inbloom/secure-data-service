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

# Enumerates the types of school food services eligibility. From Ed-Fi-Core.xsd:
# <xs:simpleType name="SchoolFoodServicesEligibilityType">
#   <xs:annotation>
#     <xs:documentation>An indication of a student's level of eligibility for breakfast, lunch, snack, supper, and milk programs.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Free"/>
#     <xs:enumeration value="Full price"/>
#     <xs:enumeration value="Reduced price"/>
#     <xs:enumeration value="Unknown"/>
#   </xs:restriction>
# </xs:simpleType>
class SchoolFoodServicesEligibilityType
  include Enum

  SchoolFoodServicesEligibilityType.define :FREE, "Free"
  SchoolFoodServicesEligibilityType.define :FULL_PRICE, "Full price"
  SchoolFoodServicesEligibilityType.define :REDUCED_PRICE, "Reduced price"
  SchoolFoodServicesEligibilityType.define :UNKNOWN, "Unknown"
end
