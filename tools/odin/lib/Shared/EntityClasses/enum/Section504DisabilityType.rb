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

# Enumerates the types of Section 504 disabilities currently supported by ed-fi. From Ed-Fi-Core.xsd:
# <xs:simpleType name="Section504DisabilityItemType">
#   <xs:annotation>
#     <xs:documentation>Enumeration items for Section 504 disabilities.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Attention Deficit Hyperactivity Disorder (ADHD)"/>
#     <xs:enumeration value="Medical Condition"/>
#     <xs:enumeration value="Motor Impairment"/>
#     <xs:enumeration value="Sensory Impairment"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class Section504DisabilityType
  include Enum

  Section504DisabilityType.define :ATTENTION_DEFICIT_HYPERACTIVITY_DISORDER, "Attention Deficit Hyperactivity Disorder (ADHD)"
  Section504DisabilityType.define :MEDICAL_CONDITION, "Medical Condition"
  Section504DisabilityType.define :MOTOR_IMPAIRMENT, "Motor Impairment"
  Section504DisabilityType.define :OTHER, "Other"
  Section504DisabilityType.define :SENSORY_IMPAIRMENT, "Sensory Impairment"
end
