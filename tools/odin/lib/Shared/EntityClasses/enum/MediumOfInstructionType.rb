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

# Enumerates the medium of instruction types. From Ed-Fi-Core.xsd:
# <xs:simpleType name="MediumOfInstructionType">
#   <xs:annotation>
#     <xs:documentation>The media through which teachers provide instruction to students and students and teachers communicate about instructional matters.  </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Televised"/>
#     <xs:enumeration value="Telepresence/video conference"/>
#     <xs:enumeration value="Videotaped/prerecorded video"/>
#     <xs:enumeration value="Other technology-based instruction"/>
#     <xs:enumeration value="Technology-based instruction in classroom"/>
#     <xs:enumeration value="Correspondence instruction"/>
#     <xs:enumeration value="Face-to-face instruction"/>
#     <xs:enumeration value="Virtual/On-line Distance learning"/>
#     <xs:enumeration value="Center-based instruction"/>
#     <xs:enumeration value="Independent study"/>
#     <xs:enumeration value="Internship"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class MediumOfInstructionType
  include Enum

  MediumOfInstructionType.define :CENTER_BASED_INSTRUCTION, "Center-based instruction"
  MediumOfInstructionType.define :CORRESPONDENCE_INSTRUCTION, "Correspondence instruction"
  MediumOfInstructionType.define :FACE_TO_FACE_INSTRUCTION, "Face-to-face instruction"
  MediumOfInstructionType.define :INDEPENDENT_STUDY, "Independent study"
  MediumOfInstructionType.define :INTERNSHIP, "Internship"
  MediumOfInstructionType.define :OTHER, "Other"
  MediumOfInstructionType.define :OTHER_TECHNOLOGY_BASED_INSTRUCTION, "Other technology-based instruction"
  MediumOfInstructionType.define :TECHNOLOGY_BASED_INSTRUCTION_IN_CLASSROOM, "Technology-based instruction in classroom"
  MediumOfInstructionType.define :TELEPRESENCE_VIDEO_CONFERENCE, "Telepresence/video conference"
  MediumOfInstructionType.define :TELEVISED, "Televised"
  MediumOfInstructionType.define :VIDEOTAPED_PRERECORDED_VIDEO, "Videotaped/prerecorded video"
  MediumOfInstructionType.define :VIRTUAL_ON_LINE_DISTANCE_LEARNING, "Virtual/On-line Distance learning"
end
