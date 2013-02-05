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

# Enumerates the types of program sponsors. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ProgramSponsorType">
#   <xs:annotation>
#     <xs:documentation>Ultimate and intermediate providers of funds for a particular educational or service program or activity or for an individual's participation in the program or activity; e.g., Federal, State, ESC, District, School, Private Org, etc.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Federal"/>
#     <xs:enumeration value="State Education Agency"/>
#     <xs:enumeration value="Education Service Center"/>
#     <xs:enumeration value="Local Education Agency"/>
#     <xs:enumeration value="School"/>
#     <xs:enumeration value="Private Organization"/>
#   </xs:restriction>
# </xs:simpleType>
class ProgramSponsorType
  include Enum

  ProgramSponsorType.define :EDUCATION_SERVICE_CENTER, "Education Service Center"
  ProgramSponsorType.define :FEDERAL, "Federal"
  ProgramSponsorType.define :LOCAL_EDUCATION_AGENCY, "Local Education Agency"
  ProgramSponsorType.define :PRIVATE_ORGANIZATION, "Private Organization"
  ProgramSponsorType.define :SCHOOL, "School"
  ProgramSponsorType.define :STATE_EDUCATION_AGENCY, "State Education Agency"
end
