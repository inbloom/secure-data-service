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

require_relative 'enum/AcademicSubjectType'
require_relative 'enum/GradeLevelType'

# creates learning standard
#
# <xs:complexType name="SLC-LearningStandard">
#   <xs:annotation>
#     <xs:documentation>This entity is a subelement of a learning objective consisting of a precise statement of the expectation of a student's proficiency.</xs:documentation>
#     <xs:appinfo>
#       <sli:recordType>learningStandard</sli:recordType>
#       <sli:naturalKeys>LearningStandardId.IdentificationCode</sli:naturalKeys>
#     </xs:appinfo>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="LearningStandardId" type="LearningStandardId"/>
#         <xs:element name="Description" type="Description"/>
#         <xs:element name="ContentStandard" type="ContentStandardType"/>
#         <xs:element name="GradeLevel" type="GradeLevelType"/>
#         <xs:element name="SubjectArea" type="AcademicSubjectType"/>
#         <xs:element name="CourseTitle" type="CourseTitle" minOccurs="0"/>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class LearningStandard < BaseEntity

  # required fields
  attr_accessor :learning_standard_id, :content_standard, :description

  # optional fields
  attr_accessor :course_title

  # additional fields needed
  attr_accessor :subject
  attr_accessor :grade

  def initialize(standard, subject, grade)
    @rand = Random.new((standard + AcademicSubjectType.to_string(subject) + GradeLevelType.to_string(grade)).size)
    @learning_standard_id  = standard
    @description = "This is a description for learning standard #{learning_standard_id}"
    @subject = subject
    @grade = grade
    @content_standard = "LEA Standard"

    optional { @course_title  = nil }
  end

  # maps to required field 'SubjectArea'
  def subject_area
    AcademicSubjectType.to_string(@subject)
  end

  # maps to required field 'GradeLevel'
  def grade_level
    GradeLevelType.to_string(@grade)
  end

  def self.build_learning_standards(count, subject, grade)
    learning_standard_ids(count, subject, grade).map{|id| LearningStandard.new(id, subject, grade) }
  end

  def self.learning_standard_ids(count, subject, grade)
    (1..count).collect{|x| "#{x}-#{AcademicSubjectType.index(subject)}-#{GradeLevelType.index(grade)}"}
  end

  # define equality between two entities by iterating over instance variables and comparing each field for equality
  def ==(entity)
    rval = true
    self.instance_variables.each { |variable| rval &= self.instance_variable_get(variable) == entity.instance_variable_get(variable) }
    rval
  end
end
