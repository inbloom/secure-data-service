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

require_relative "baseEntity.rb"

# creates grade book entry
#
# from SLI-Ed-Fi-Core.xsd:
# <xs:complexType name="SLC-GradebookEntry">
#   <xs:annotation>
#     <xs:documentation>GradebookEntry record with key fields: GradebookEntryType, DateAssigned, SectionReference (StateOrganizationId, UniqueSectionCode).  Changed types of LearningStandardReference, LearningObjectiveReference, SectionReference and GradingPeriodReference to SLC reference types.</xs:documentation>
#     <xs:appinfo>
#       <sli:recordType>gradebookEntry</sli:recordType>
#     </xs:appinfo>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="GradebookEntryType" type="GradebookEntryType"/>
#         <xs:element name="DateAssigned" type="xs:date"/>
#         <xs:element name="Description" type="Description" minOccurs="0"/>
#         <xs:element name="LearningStandardReference" type="SLC-LearningStandardReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#         <xs:element name="LearningObjectiveReference" type="SLC-LearningObjectiveReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#         <xs:element name="SectionReference" type="SLC-SectionReferenceType"/>
#         <xs:element name="GradingPeriodReference" type="SLC-GradingPeriodReferenceType" minOccurs="0"/>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class GradebookEntry < BaseEntity

  # required fields
  attr_accessor :type                # maps to 'GradebookEntryType'
  attr_accessor :date_assigned       # maps to 'DateAssigned'
  attr_accessor :section             # maps to 'SectionReference'
  
  # optional fields
  attr_accessor :description         # maps to 'Description'
  attr_accessor :learning_standards  # maps to 'LearningStandardReference'
  attr_accessor :learning_objectives # maps to 'LearningObjectiveReference'

  def initialize(type, date_assigned, section, description = nil, grading_period = nil, learning_objectives = [], learning_standards = [])
    @type                = type
    @date_assigned       = date_assigned
    @section             = section

    @description         = description
    @learning_standards  = learning_standards
    @learning_objectives = learning_objectives
    @grading_period      = grading_period
  end

  # maps to optional field 'GradingPeriodReference'
  def grading_period
    return nil if @grading_period.nil?

    type       = GradingPeriodType.to_string(@grading_period['type'])
    ed_org_id  = @grading_period['ed_org_id']
    begin_date = @grading_period['ed_org_id']['interval'].get_begin_date
    return {:type => type, :ed_org_id => ed_org_id, :begin_date => begin_date}
  end
end
