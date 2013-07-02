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

require_relative 'enum/GradeLevelType'

# creates student competency objetive
#
# <xs:complexType name="StudentCompetencyObjective">
#     <xs:annotation>
#       <xs:documentation>This entity holds additional competencies for student achievement that are not associated with specific learning objectives (e.g., paying attention in class).</xs:documentation>
#     </xs:annotation>
#     <xs:complexContent>
#       <xs:extension base="ComplexObjectType">
#         <xs:sequence>
#           <xs:element name="StudentCompetencyObjectiveId" type="IdentificationCode" minOccurs="0"/>
#           <xs:element name="Objective" type="Objective"/>
#           <xs:element name="Description" type="Description" minOccurs="0"/>
#           <xs:element name="ObjectiveGradeLevel" type="GradeLevelType"/>
#           <xs:element name="EducationOrganizationReference" type="EducationalOrgReferenceType"/>
#         </xs:sequence>
#       </xs:extension>
#     </xs:complexContent>
#   </xs:complexType>

class StudentCompetencyObjective < BaseEntity

  # required fields
  attr_accessor :sco_id
  attr_accessor :objective
  attr_accessor :objective_grade
  attr_accessor :edorg_id

  # optional fields
  attr_accessor :description

  def initialize(sco, objective, grade, edorg_id)
    @sco_id                = sco
    @objective             = objective
    @objective_grade       = GradeLevelType.to_string(grade)
    @edorg_id              = edorg_id

    #optional { @description  = "A descrpition of how awesome it is to achieve #{objective}" }
  end

  # define equality between two entities by iterating over instance variables and comparing each field for equality
  def ==(entity)
    rval = true
    self.instance_variables.each { |variable| rval &= self.instance_variable_get(variable) == entity.instance_variable_get(variable) }
    rval
  end
end
