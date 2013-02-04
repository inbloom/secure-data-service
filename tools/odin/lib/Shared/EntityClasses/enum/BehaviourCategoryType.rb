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

# Enumerates the types of behaviour categories. From Ed-Fi-Core.xsd:
# <xs:simpleType name="BehaviorCategoryType">
#   <xs:annotation>
#     <xs:documentation>Enumeration items defining the categories of behavior coded for use in describing an incident.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="State Law Crime"/>
#     <xs:enumeration value="State Offense"/>
#     <xs:enumeration value="School Violation"/>
#     <xs:enumeration value="School Code of Conduct"/>
#   </xs:restriction>
# </xs:simpleType>
class BehaviourCategoryType
  include Enum

  BehaviourCategoryType.define :SCHOOL_CODE_OF_CONDUCT, "School Code of Conduct"
  BehaviourCategoryType.define :SCHOOL_VIOLATION, "School Violation"
  BehaviourCategoryType.define :STATE_LAW_CRIME, "State Law Crime"
  BehaviourCategoryType.define :STATE_OFFENSE, "State Offense"
end
