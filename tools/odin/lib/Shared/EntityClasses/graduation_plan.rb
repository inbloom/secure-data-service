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

# creates graduation plan
# 
# from SLI-Ed-Fi-Core.xsd:
# <xs:complexType name="SLC-GraduationPlan">
#   <xs:annotation>
#     <xs:documentation>GraduationPlan record with key fields: GraduationPlanType and EducationOrganizationReference (StateOrganizationId). Changed type of EducationOrganizationReference to SLC reference types.</xs:documentation>
#     <xs:appinfo>
#       <sli:recordType>graduationPlan</sli:recordType>
#     </xs:appinfo>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="GraduationPlanType">
#           <xs:simpleType>
#             <xs:restriction base="GraduationPlanType"/>
#           </xs:simpleType>
#         </xs:element>
#         <xs:element name="IndividualPlan" type="xs:boolean" minOccurs="0"/>
#         <xs:element name="TotalCreditsRequired" type="Credits"/>
#         <xs:element name="CreditsBySubject" type="CreditsBySubject" minOccurs="0" maxOccurs="unbounded"/>
#         <xs:element name="CreditsByCourse" type="CreditsByCourse" minOccurs="0" maxOccurs="unbounded"/>
#         <xs:element name="EducationOrganizationReference" type="SLC-EducationalOrgReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class GraduationPlan < BaseEntity

  # required fields
  attr_accessor :type           # maps to 'GraduationPlanType'
  attr_accessor :total_credits  # maps to 'TotalCreditsRequired'
  attr_accessor :ed_org_id      # maps to 'EducationOrganizationReference'

  # optional fields
  attr_accessor :individual     # maps to 'IndividualPlan'
  attr_accessor :subjects       # maps to 'CreditsBySubject'
  attr_accessor :courses        # maps to 'CreditsByCourse'
  
  def initialize(type, ed_org_id, credits_by_subject, credits_by_course = {})
    @type          = type
    @total_credits = credits_by_subject.values.inject(:+)
    @ed_org_id     = ed_org_id
    @rand          = Random.new((type + ed_org_id.to_s).size + @total_credits)

    optional { @individual = false }
    optional { @subjects   = credits_by_subject.map{ |subject, credits| {subject: subject, credits: credits} } }
    optional { @courses    = credits_by_subject.map{ |course, credits|  {course: {id: course, ed_org_id: ed_org_id}, credits: credits} } }
  end
end
