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

require_relative "../data_utility.rb"
require_relative 'baseEntity'
require_relative 'enum/DisciplineActionLengthDifferenceReasonType'

# creates discipline action
#
# from SLI-Ed-Fi-Core.xsd:
#  <xs:complexType name="SLC-DisciplineAction">
#    <xs:annotation>
#      <xs:documentation>DisciplineAction record with key fields: DisciplineActionIdentifier, ResponsibilitySchoolReference (StateOrganizationId). Changed types of StudentReference, DisciplineIncidentReference, StaffReference, ResponsibilitySchoolReference and AssignmentSchoolReference to SLC reference types.</xs:documentation>
#      <xs:appinfo>
#        <sli:recordType>disciplineAction</sli:recordType>
#      </xs:appinfo>
#    </xs:annotation>
#    <xs:complexContent>
#      <xs:extension base="ComplexObjectType">
#        <xs:sequence>
#          <xs:element name="DisciplineActionIdentifier" type="DisciplineActionIdentifier"/>
#          <xs:element name="Disciplines" type="DisciplineDescriptorType" maxOccurs="unbounded"/>
#          <xs:element name="DisciplineDate" type="xs:date"/>
#          <xs:element name="DisciplineActionLength" type="xs:integer" minOccurs="0"/>
#          <xs:element name="ActualDisciplineActionLength" type="xs:integer" minOccurs="0"/>
#          <xs:element name="DisciplineActionLengthDifferenceReason" type="DisciplineActionLengthDifferenceReasonType" minOccurs="0"/>
#          <xs:element name="StudentReference" type="SLC-StudentReferenceType" maxOccurs="unbounded"/>
#          <xs:element name="DisciplineIncidentReference" type="SLC-DisciplineIncidentReferenceType" maxOccurs="unbounded"/>
#          <xs:element name="StaffReference" type="SLC-StaffReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="ResponsibilitySchoolReference" type="SLC-EducationalOrgReferenceType"/>
#          <xs:element name="AssignmentSchoolReference" type="SLC-EducationalOrgReferenceType" minOccurs="0"/>
#        </xs:sequence>
#      </xs:extension>
#    </xs:complexContent>
#  </xs:complexType>
class DisciplineAction < BaseEntity

  # required fields
  attr_accessor :action_id       # maps to 'DisciplineActionIdentifier'
  attr_accessor :disciplines     # maps to 'Disciplines'
  attr_accessor :date            # maps to 'DisciplineDate'
  attr_accessor :students        # maps to 'StudentReference'
  attr_accessor :incidents       # maps to 'DisciplineIncidentReference'
  attr_accessor :resp_ed_org_id  # maps to 'ResponsibilitySchoolReference'

  # optional fields
  attr_accessor :action_length   # maps to 'DisciplineActionLength'
  attr_accessor :actual_length   # maps to 'ActualDisciplineActionLength'
  attr_accessor :length_diff_rsn # maps to 'DisciplineActionLengthDifferenceReason'
  attr_accessor :staff_members   # maps to 'StaffReference'
  attr_accessor :asgn_ed_org_id  # maps to 'AssignmentSchoolReference'
  
  def initialize(student_id, ed_org_id, incident, staff_members = [])
    rand             = Random.new(incident.index)

    @action_id       = "#{incident.incident_identifier},#{student_id}"
    @disciplines     = ["DI#{incident.index}"]
    @date            = incident.date + 1
    @students        = [student_id]
    @incidents       = [incident]
    @resp_ed_org_id  = ed_org_id

    @action_length   = DataUtility.select_random_from_options(rand, (1..5).to_a)
    @actual_length   = DataUtility.select_random_from_options(rand, (0..@action_length).to_a)
    @length_diff_rsn = :NO_DIFFERENCE if @action_length - @actual_length == 0
    @length_diff_rsn = :OTHER         if @action_length - @actual_length != 0
    members          = []
    members          << incident.staff_id unless incident.staff_id.nil?
    members          << staff_members unless staff_members.nil? || staff_members.size == 0
    @staff_members   = members.flatten
    @asgn_ed_org_id  = ed_org_id
  end

  # convert DisciplineActionLengthDifferenceReason from symbol -> string representation
  def diff_reason
    DisciplineActionLengthDifferenceReasonType.to_string(@length_diff_rsn)
  end
end
