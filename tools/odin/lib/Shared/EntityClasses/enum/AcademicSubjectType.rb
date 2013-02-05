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

# Enumerates the academic subjects supported by ed-fi. From Ed-Fi-Core.xsd:
# <xs:simpleType name="AcademicSubjectType">
#   <xs:annotation>
#     <xs:documentation>The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.
#     </xs:documentation>
#   </xs:annotation>
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="English Language and Literature"/>
#     <xs:enumeration value="English"/>
#     <xs:enumeration value="ELA"/>
#     <xs:enumeration value="Reading"/>
#     <xs:enumeration value="Mathematics"/>
#     <xs:enumeration value="Life and Physical Sciences"/>
#     <xs:enumeration value="Social Sciences and History"/>
#     <xs:enumeration value="Social Studies"/>
#     <xs:enumeration value="Science"/>
#     <xs:enumeration value="Fine and Performing Arts"/>
#     <xs:enumeration value="Foreign Language and Literature"/>
#     <xs:enumeration value="Religious Education and Theology"/>
#     <xs:enumeration value="Physical, Health, and Safety Education"/>
#     <xs:enumeration value="Military Science"/>
#     <xs:enumeration value="Computer and Information Sciences"/>
#     <xs:enumeration value="Communication and Audio/Visual Technology"/>
#     <xs:enumeration value="Composite"/>
#     <xs:enumeration value="Critical Reading"/>
#     <xs:enumeration value="Business and Marketing"/>
#     <xs:enumeration value="Manufacturing"/>
#     <xs:enumeration value="Health Care Sciences"/>
#     <xs:enumeration value="Public, Protective, and Government Service"/>
#     <xs:enumeration value="Hospitality and Tourism"/>
#     <xs:enumeration value="Architecture and Construction"/>
#     <xs:enumeration value="Agriculture, Food, and Natural Resources"/>
#     <xs:enumeration value="Human Services"/>
#     <xs:enumeration value="Transportation, Distribution and Logistics"/>
#     <xs:enumeration value="Engineering and Technology"/>
#     <xs:enumeration value="Writing"/>
#     <xs:enumeration value="Miscellaneous"/>
#     <xs:enumeration value="Other"/>
#   </xs:restriction>
# </xs:simpleType>
class AcademicSubjectType
  include Enum

  AcademicSubjectType.define :AGRICULTURE_FOOD_AND_NATURAL_RESOURCES, "Agriculture, Food, and Natural Resources"
  AcademicSubjectType.define :ARCHITECTURE_AND_CONSTRUCTION, "Architecture and Construction"
  AcademicSubjectType.define :BUSINESS_AND_MARKETING, "Business and Marketing"
  AcademicSubjectType.define :COMMUNICATION_AND_AUDIO_VISUAL_TECHNOLOGY, "Communication and Audio/Visual Technology"
  AcademicSubjectType.define :COMPOSITE, "Composite"
  AcademicSubjectType.define :COMPUTER_AND_INFORMATION_SCIENCES, "Computer and Information Sciences"
  AcademicSubjectType.define :CRITICAL_READING, "Critical Reading"
  AcademicSubjectType.define :ELA, "ELA"
  AcademicSubjectType.define :ENGINEERING_AND_TECHNOLOGY, "Engineering and Technology"
  AcademicSubjectType.define :ENGLISH, "English"
  AcademicSubjectType.define :ENGLISH_LANGUAGE_AND_LITERATURE, "English Language and Literature"
  AcademicSubjectType.define :FINE_AND_PERFORMING_ARTS, "Fine and Performing Arts"
  AcademicSubjectType.define :FOREIGN_LANGUAGE_AND_LITERATURE, "Foreign Language and Literature"
  AcademicSubjectType.define :HEALTH_CARE_SCIENCES, "Health Care Sciences"
  AcademicSubjectType.define :HOSPITALITY_AND_TOURISM, "Hospitality and Tourism"
  AcademicSubjectType.define :HUMAN_SERVICES, "Human Services"
  AcademicSubjectType.define :LIFE_AND_PHYSICAL_SCIENCES, "Life and Physical Sciences"
  AcademicSubjectType.define :MANUFACTURING, "Manufacturing"
  AcademicSubjectType.define :MATHEMATICS, "Mathematics"
  AcademicSubjectType.define :MILITARY_SCIENCE, "Military Science"
  AcademicSubjectType.define :MISCELLANEOUS, "Miscellaneous"
  AcademicSubjectType.define :OTHER, "Other"
  AcademicSubjectType.define :PHYSICAL_HEALTH_AND_SAFETY_EDUCATION, "Physical, Health, and Safety Education"
  AcademicSubjectType.define :PUBLIC_PROTECTIVE_AND_GOVERNMENT_SERVICE, "Public, Protective, and Government Service"
  AcademicSubjectType.define :READING, "Reading"
  AcademicSubjectType.define :RELIGIOUS_EDUCATION_AND_THEOLOGY, "Religious Education and Theology"
  AcademicSubjectType.define :SCIENCE, "Science"
  AcademicSubjectType.define :SOCIAL_SCIENCES_AND_HISTORY, "Social Sciences and History"
  AcademicSubjectType.define :SOCIAL_STUDIES, "Social Studies"
  AcademicSubjectType.define :TRANSPORTATION_DISTRIBUTION_AND_LOGISTICS, "Transportation, Distribution and Logistics"
  AcademicSubjectType.define :WRITING, "Writing"

  # returns academic subjects commonly associated with elementary school courses
  def self.elementary
    subjects = []
    subjects << :MATHEMATICS << :READING << :SCIENCE << :SOCIAL_STUDIES << :WRITING << :ELA
    subjects
  end

  # returns academic subjects commonly associated with middle school courses
  def self.middle
    subjects = []
    subjects << elementary
    subjects << :AGRICULTURE_FOOD_AND_NATURAL_RESOURCES << :CRITICAL_READING << :ENGLISH << :LIFE_AND_PHYSICAL_SCIENCES
    subjects.flatten
  end

  # returns academic subjects commonly associated with high school courses
  def self.high
    subjects = []
    subjects << middle
    subjects << :BUSINESS_AND_MARKETING << :COMMUNICATION_AND_AUDIO_VISUAL_TECHNOLOGY << :COMPUTER_AND_INFORMATION_SCIENCES << :ENGINEERING_AND_TECHNOLOGY
    subjects << :ENGLISH_LANGUAGE_AND_LITERATURE << :FOREIGN_LANGUAGE_AND_LITERATURE << :HEALTH_CARE_SCIENCES << :HUMAN_SERVICES << :MISCELLANEOUS
    subjects << :PHYSICAL_HEALTH_AND_SAFETY_EDUCATION << :SOCIAL_SCIENCES_AND_HISTORY
    subjects.flatten 
  end

  def self.get_academic_subjects(grade)
    if GradeLevelType.is_elementary_school_grade grade
      return elementary
    elsif GradeLevelType.is_middle_school_grade grade
      return middle
    elsif GradeLevelType.is_high_school_grade grade
      return high
    end
  end
end
