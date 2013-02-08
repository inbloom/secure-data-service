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

require_relative 'baseEntity'
require_relative 'enum/GradeType'
require_relative 'enum/PerformanceBaseType'

# creates grade
#
# From SLI-Ed-Fi-Core.xsd:
#  <xs:complexType name="SLC-Grade">
#    <xs:annotation>
#      <xs:documentation>Grade record with key fields: StudentSectionAssociationReference (StudentUniqueStateId, StateOrganizationId, UniqueSectionCode, BeginDate) and GradingPeriodReference (StateOrganizationId, GradingPeriod, BeginDate). Changed types of StudentSectionAssociationReference and GradingPeriodReference to SLC reference types.</xs:documentation>
#      <xs:appinfo>
#        <sli:recordType>grade</sli:recordType>
#      </xs:appinfo>
#    </xs:annotation>
#    <xs:complexContent>
#      <xs:extension base="ComplexObjectType">
#        <xs:sequence>
#          <xs:element name="LetterGradeEarned" minOccurs="0">
#            <xs:simpleType>
#              <xs:restriction base="GradeEarned">
#                <xs:maxLength value="20"/>
#              </xs:restriction>
#            </xs:simpleType>
#          </xs:element>
#          <xs:element name="NumericGradeEarned" type="xs:integer" minOccurs="0"/>
#          <xs:element name="DiagnosticStatement" type="text" minOccurs="0"/>
#          <xs:element name="GradeType" type="GradeType"/>
#          <xs:element name="PerformanceBaseConversion" type="PerformanceBaseType" minOccurs="0"/>
#          <xs:element name="StudentSectionAssociationReference" type="SLC-StudentSectionAssociationReferenceType"/>
#          <xs:element name="GradingPeriodReference" type="SLC-GradingPeriodReferenceType" minOccurs="0"/>
#        </xs:sequence>
#      </xs:extension>
#    </xs:complexContent>
#  </xs:complexType>
class Grade < BaseEntity

  # required fields
  attr_accessor :student_section_association   # maps to 'StudentSectionAssociationReference'  
  
  # optional fields
  attr_accessor :letter_grade                  # maps to 'LetterGradeEarned'
  attr_accessor :number_grade                  # maps to 'NumericGradeEarned'
  attr_accessor :diagnostic                    # maps to 'DiagnosticStatement'      
  attr_accessor :grading_period                # maps to 'GradingPeriodReference'

  attr_accessor :performance

  def initialize(letter_grade, number_grade, type, student_section_association, grading_period = nil)
    @type                        = type
    @student_section_association = student_section_association
    @rand                        = Random.new((GradeType.to_string(type) + student_section_association['student'].to_s + student_section_association['ed_org_id'].to_s).size)
    
    # report card computations rely on this field being present
    # -> beyond that, it's ridiculous to me that BOTH letter and number grades are optional on a 'grade' type
    # -> so this optional field is not optional for odin
    @letter_grade = letter_grade

    optional { @number_grade = number_grade }
    optional { 
      @performance = get_performance_base
      @diagnostic  = "Student has #{performance_base} understanding of subject."
    }
    optional { @grading_period = grading_period }
  end

  # maps to required field 'GradeType'
  def type
    GradeType.to_string(@type)
  end

  # maps to required field 'PerformanceBaseConversion'
  def performance_base
    return PerformanceBaseType.to_string(@performance) if @performance
  end

  def get_performance_base
    return nil          if @letter_grade.nil?
    return :ADVANCED    if @letter_grade.include?("A")
    return :PROFICIENT  if @letter_grade.include?("B")
    return :BASIC       if @letter_grade.include?("C")
    return :BELOW_BASIC if @letter_grade.include?("D")
    return nil
  end
end
