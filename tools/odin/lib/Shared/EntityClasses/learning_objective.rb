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

# creates learning objective
#
# from SLI-Ed-Fi-Core.xsd:
# <xs:complexType name="SLC-LearningObjective">
#   <xs:annotation>
#     <xs:documentation>Learning objective record with key fields: Objective, AcademicSubject and ObjectiveGradeLevel. LocalChanged type of LearningStandardReference and LearningObjectiveReference to SLC reference types.</xs:documentation>
#     <xs:appinfo>
#       <sli:recordType>learningObjective</sli:recordType>
#     </xs:appinfo>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="LearningObjectiveId" type="LearningStandardId" minOccurs="0"/>
#         <xs:element name="Objective" type="Objective"/>
#         <xs:element name="Description" type="Description" minOccurs="0"/>
#         <xs:element name="AcademicSubject" type="AcademicSubjectType"/>
#         <xs:element name="ObjectiveGradeLevel" type="GradeLevelType"/>
#         <xs:element name="LearningStandardReference" type="SLC-LearningStandardReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#         <xs:element name="LearningObjectiveReference" type="SLC-LearningObjectiveReferenceType" minOccurs="0"/>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class LearningObjective < BaseEntity

  # required fields
  attr_accessor :objective            # maps to 'Objective'

  # optional fields
  attr_accessor :learning_standard_id # maps to 'LearningObjectiveId'
  attr_accessor :description          # maps to 'Description'
  attr_accessor :learning_standards   # maps to 'LearningStandardReference'
  attr_accessor :learning_objectives  # maps to 'LearningObjectiveReference'

  # additional fields needed
  attr_accessor :subject
  attr_accessor :grade

  def initialize(objective, subject, grade, learning_standards = [], learning_objectives = [])
    @objective             = objective
    @subject               = subject
    @grade                 = grade

    @description           = "#{objective} for grade: #{objective_grade_level} in subject: #{academic_subject}"
    #@learning_standard_id  = learning_standards.first['id'] unless learning_standards.empty?
    #@learning_standards    = learning_standards
    @learning_objectives   = learning_objectives
  end

  # maps to required field 'AcademicSubject'
  def academic_subject
    AcademicSubjectType.to_string(subject)
  end

  # maps to required field 'ObjectiveGradeLevel'
  def objective_grade_level
    GradeLevelType.to_string(grade)
  end

  def self.build_learning_objectives(count, academic_subject, objective_grade_level)
    (1..count).collect{|x| LearningObjective.new("Generic Learning Objective #{x}", academic_subject, objective_grade_level)}
  end

  # define equality between two entities by iterating over instance variables and comparing each field for equality
  def ==(entity)
    rval = true
    self.instance_variables.each { |variable| rval &= self.instance_variable_get(variable) == entity.instance_variable_get(variable) }
    rval
  end
end
