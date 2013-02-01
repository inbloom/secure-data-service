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

# Enumerates the different types of programs. From Ed-Fi-Core.xsd:
# <xs:simpleType name="ProgramType">
#   <xs:annotation>
#     <xs:documentation>The formal name of the program of instruction, training, services or benefits available through federal, state, or local agencies.</xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Adult/Continuing Education"/>
#     <xs:enumeration value="Alternative Education"/>
#     <xs:enumeration value="Athletics"/>
#     <xs:enumeration value="Bilingual"/>
#     <xs:enumeration value="Bilingual Summer"/>
#     <xs:enumeration value="Career and Technical Education"/>
#     <xs:enumeration value="Cocurricular Programs"/>
#     <xs:enumeration value="College Preparatory"/>
#     <xs:enumeration value="Community Service Program"/>
#     <xs:enumeration value="Community/Junior College Education Program"/>
#     <xs:enumeration value="Compensatory Services for Disadvantaged Students"/>
#     <xs:enumeration value="Counseling Services"/>
#     <xs:enumeration value="English as a Second Language (ESL)"/>
#     <xs:enumeration value="Even Start"/>
#     <xs:enumeration value="Extended Day/Child Care Services"/>
#     <xs:enumeration value="Gifted and Talented"/>
#     <xs:enumeration value="Head Start"/>
#     <xs:enumeration value="Health Services Program"/>
#     <xs:enumeration value="High School Equivalency Program (HSEP)"/>
#     <xs:enumeration value="IDEA"/>
#     <xs:enumeration value="Immigrant Education"/>
#     <xs:enumeration value="Indian Education"/>
#     <xs:enumeration value="International Baccalaureate"/>
#     <xs:enumeration value="Library/Media Services Program"/>
#     <xs:enumeration value="Magnet/Special Program Emphasis"/>
#     <xs:enumeration value="Migrant Education"/>
#     <xs:enumeration value="Neglected and Delinquent Program"/>
#     <xs:enumeration value="Optional Flexible School Day Program (OFSDP)"/>
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Regular Education"/>
#     <xs:enumeration value="Remedial Education"/>
#     <xs:enumeration value="Section 504 Placement"/>
#     <xs:enumeration value="Service Learning"/>
#     <xs:enumeration value="Special Education"/>
#     <xs:enumeration value="Student Retention/Dropout Prevention"/>
#     <xs:enumeration value="Substance Abuse Education/Prevention"/>
#     <xs:enumeration value="Teacher Professional Development/Mentoring"/>
#     <xs:enumeration value="Technical Preparatory"/>
#     <xs:enumeration value="Title I Part A"/>
#     <xs:enumeration value="Vocational Education"/>
#   </xs:restriction>
# </xs:simpleType>
class ProgramType
  include Enum

  ProgramType.define :ADULT_CONTINUING_EDUCATION, "Adult/Continuing Education"
  ProgramType.define :ALTERNATIVE_EDUCATION, "Alternative Education"
  ProgramType.define :ATHLETICS, "Athletics"
  ProgramType.define :BILINGUAL, "Bilingual"
  ProgramType.define :BILINGUAL_SUMMER, "Bilingual Summer"
  ProgramType.define :CAREER_AND_TECHNICAL_EDUCATION, "Career and Technical Education"
  ProgramType.define :COCURRICULAR_PROGRAMS, "Cocurricular Programs"
  ProgramType.define :COLLEGE_PREPARATORY, "College Preparatory"
  ProgramType.define :COMMUNITY_JUNIOR_COLLEGE_EDUCATION_PROGRAM, "Community/Junior College Education Program"
  ProgramType.define :COMMUNITY_SERVICE_PROGRAM, "Community Service Program"
  ProgramType.define :COMPENSATORY_SERVICES_FOR_DISADVANTAGED_STUDENTS, "Compensatory Services for Disadvantaged Students"
  ProgramType.define :COUNSELING_SERVICES, "Counseling Services"
  ProgramType.define :ENGLISH_AS_A_SECOND_LANGUAGE, "English as a Second Language (ESL)"
  ProgramType.define :EVEN_START, "Even Start"
  ProgramType.define :EXTENDED_DAY_CHILD_CARE_SERVICES, "Extended Day/Child Care Services"
  ProgramType.define :GIFTED_AND_TALENTED, "Gifted and Talented"
  ProgramType.define :HEAD_START, "Head Start"
  ProgramType.define :HEALTH_SERVICES_PROGRAM, "Health Services Program"
  ProgramType.define :HIGH_SCHOOL_EQUIVALENCY_PROGRAM, "High School Equivalency Program (HSEP)"
  ProgramType.define :IDEA, "IDEA"
  ProgramType.define :IMMIGRANT_EDUCATION, "Immigrant Education"
  ProgramType.define :INDIAN_EDUCATION, "Indian Education"
  ProgramType.define :INTERNATIONAL_BACCALAUREATE, "International Baccalaureate"
  ProgramType.define :LIBRARY_MEDIA_SERVICES_PROGRAM, "Library/Media Services Program"
  ProgramType.define :MAGNET_SPECIAL_PROGRAM_EMPHASIS, "Magnet/Special Program Emphasis"
  ProgramType.define :MIGRANT_EDUCATION, "Migrant Education"
  ProgramType.define :NEGLECTED_AND_DELINQUENT_PROGRAM, "Neglected and Delinquent Program"
  ProgramType.define :OPTIONAL_FLEXIBLE_SCHOOL_DAY_PROGRAM, "Optional Flexible School Day Program (OFSDP)"
  ProgramType.define :OTHER, "Other"
  ProgramType.define :REGULAR_EDUCATION, "Regular Education"
  ProgramType.define :REMEDIAL_EDUCATION, "Remedial Education"
  ProgramType.define :SECTION_504_PLACEMENT, "Section 504 Placement"
  ProgramType.define :SERVICE_LEARNING, "Service Learning"
  ProgramType.define :SPECIAL_EDUCATION, "Special Education"
  ProgramType.define :STUDENT_RETENTION_DROPOUT_PREVENTION, "Student Retention/Dropout Prevention"
  ProgramType.define :SUBSTANCE_ABUSE_EDUCATION_PREVENTION, "Substance Abuse Education/Prevention"
  ProgramType.define :TEACHER_PROFESSIONAL_DEVELOPMENT_MENTORING, "Teacher Professional Development/Mentoring"
  ProgramType.define :TECHNICAL_PREPARATORY, "Technical Preparatory"
  ProgramType.define :TITLE_I_PART_A, "Title I Part A"
  ProgramType.define :VOCATIONAL_EDUCATION, "Vocational Education"
end
