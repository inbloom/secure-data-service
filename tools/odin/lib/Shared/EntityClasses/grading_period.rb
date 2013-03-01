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

require_relative '../date_interval'
require_relative 'baseEntity'
require_relative 'enum/GradingPeriodType'
require_relative 'enum/CalendarEventType'

# creates grading period
#
# from SLI-Ed-Fi-Core.xsd:
# <xs:complexType name="SLC-GradingPeriod">
#   <xs:annotation>
#     <xs:documentation>GradingPeriod record with key fields: GradingPeriod, EducationOrganizationReference (StateOrganizationId) and BeginDate. Added GradingPeriod, SchoolYear and EducationOrganizationReference. Changed type of CalendarDateReference to SLC reference type.</xs:documentation>
#     <xs:appinfo>
#       <sli:recordType>gradingPeriod</sli:recordType>
#     </xs:appinfo>
#   </xs:annotation>
#   <xs:complexContent>
#     <xs:extension base="ComplexObjectType">
#       <xs:sequence>
#         <xs:element name="GradingPeriod" type="GradingPeriodType"/>
#         <xs:element name="SchoolYear" type="SchoolYearType"/>
#         <xs:element name="EducationOrganizationReference" type="SLC-EducationalOrgReferenceType"/>
#         <xs:element name="BeginDate" type="xs:date"/>
#         <xs:element name="EndDate" type="xs:date"/>
#         <xs:element name="TotalInstructionalDays">
#           <xs:simpleType>
#             <xs:restriction base="xs:int">
#               <xs:minInclusive value="0"/>
#             </xs:restriction>
#           </xs:simpleType>
#         </xs:element>
#         <xs:element name="CalendarDateReference" type="SLC-CalendarDateReferenceType" maxOccurs="unbounded"/>
#       </xs:sequence>
#     </xs:extension>
#   </xs:complexContent>
# </xs:complexType>
class GradingPeriod < BaseEntity
  
  # required fields
  attr_accessor :school_year     # maps to 'SchoolYear'
  attr_accessor :ed_org_id       # maps to 'EducationOrganizationReference'
  attr_accessor :begin_date      # maps to 'BeginDate'
  attr_accessor :end_date        # maps to 'EndDate'
  attr_accessor :num_school_days # maps to 'TotalInstructionalDays'
  attr_accessor :calendar_dates  # maps to 'CalendarDateReference'

  def initialize(type, year, interval, ed_org_id, calendar_dates = [])
      @type            = type
      @school_year     = year.to_s + "-" + (year+1).to_s
      @ed_org_id       = ed_org_id
    @calendar_dates  = calendar_dates
    @begin_date      = interval.get_begin_date.to_s
    @end_date        = interval.get_end_date.to_s
    @num_school_days = interval.get_num_school_days
  end

  # maps to required field 'GradingPeriod'
  def type
      GradingPeriodType.to_string(@type)
  end
end
