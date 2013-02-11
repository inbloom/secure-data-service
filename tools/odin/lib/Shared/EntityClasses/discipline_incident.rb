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

require_relative '../data_utility'
require_relative 'baseEntity'
require_relative 'enum/BehaviourCategoryType'
require_relative 'enum/ReporterDescriptionType'
require_relative 'enum/WeaponItemType'

# creates an discipline incident
#
# From SLI-Ed-Fi-Core.xsd:
#  <xs:complexType name="SLC-DisciplineIncident">
#    <xs:annotation>
#      <xs:documentation>DisciplineIncident record with key fields: SchoolReference (StateOrganizationId) and IncidentIdentifier . Changed types of SchoolReference and StaffReference to SLC reference types.</xs:documentation>
#      <xs:appinfo>
#        <sli:recordType>disciplineIncident</sli:recordType>
#      </xs:appinfo>
#    </xs:annotation>
#    <xs:complexContent>
#      <xs:extension base="ComplexObjectType">
#        <xs:sequence>
#          <xs:element name="IncidentIdentifier" type="IncidentIdentifier"/>
#          <xs:element name="IncidentDate" type="xs:date"/>
#          <xs:element name="IncidentTime" type="xs:time"/>
#          <xs:element name="IncidentLocation" type="IncidentLocationType"/>
#          <xs:element name="ReporterDescription" type="ReporterDescriptionType" minOccurs="0"/>
#          <xs:element name="ReporterName" type="ReporterName" minOccurs="0"/>
#          <xs:element name="Behaviors" type="BehaviorDescriptorType" maxOccurs="unbounded"/>
#          <xs:element name="SecondaryBehaviors" type="SecondaryBehavior" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="Weapons" type="WeaponsType" minOccurs="0"/>
#          <xs:element name="ReportedToLawEnforcement" type="xs:boolean" minOccurs="0"/>
#          <xs:element name="CaseNumber" type="CaseNumber" minOccurs="0"/>
#          <xs:element name="SchoolReference" type="SLC-EducationalOrgReferenceType"/>
#          <xs:element name="StaffReference" type="SLC-StaffReferenceType" minOccurs="0"/>
#        </xs:sequence>
#      </xs:extension>
#    </xs:complexContent>
#  </xs:complexType>
class DisciplineIncident < BaseEntity

  # required fields
  attr_accessor :incident_identifier   # maps to 'IncidentIdentifier'
  attr_accessor :date                  # maps to 'IncidentDate'
  attr_accessor :time                  # maps to 'IncidentTime'
  attr_accessor :location              # maps to 'IncidentLocation'
  attr_accessor :behaviors             # maps to 'Behaviors'
  attr_accessor :school_id             # maps to 'SchoolReference'

  # optional fields
  attr_accessor :reporter_desc         # maps to 'ReporterDescription'
  attr_accessor :reporter_name         # maps to 'ReporterName'
  attr_accessor :sec_behaviors         # maps to 'SecondaryBehaviors'
  attr_accessor :weapons               # maps to 'Weapons'
  attr_accessor :reported              # maps to 'ReportedToLawEnforcement'
  attr_accessor :case_number           # maps to 'CaseNumber'
  attr_accessor :staff_id              # maps to 'StaffReference'

  # additional fields (used for discipline incident lookups)
  attr_accessor :index

  def initialize(id, section_id, school, staff, interval, location, behaviors = nil)
    @rand      = Random.new(id + section_id * 10)
    eight_hours = 8 * 60 * 60
    
    @incident_identifier = DisciplineIncident.gen_id(id, section_id)
    @date                = interval.random_day(@rand)
    @time                = (@date.to_time + eight_hours + @rand.rand(eight_hours)).strftime("%H:%M:%S")
    @location            = location
    @behaviors           = behaviors || [DisciplineIncident.gen_behavior(id, section_id)]
    @school_id           = school

    reporters      = ReporterDescriptionType.all
    weapons        = WeaponItemType.all
    behavior_types = BehaviourCategoryType.all
    reporter       = DataUtility.select_random_from_options(@rand, reporters)
    weapon         = DataUtility.select_random_from_options(@rand, weapons)

    optional { @reporter_desc = reporter.value }
    optional { @reporter_name = "Reported " + @reporter_desc + " Name" }
    optional { 
      @sec_behaviors = [] 
      @behaviors.each do |behavior| 
        behavior_type  = DataUtility.select_random_from_options(@rand, behavior_types)
        @sec_behaviors << {:sec_behavior => "S" + behavior, :type => behavior_type.value}
      end
    }
    optional { @weapons    = [weapon.value] }
    optional { 
      @reported            = @rand.rand < 0.50
      @case_number         = "case-#{@incident_identifier}" if @reported == true
      @reported            = "true"  if @reported == true
      @reported            = "false" if @reported == false
    }
    optional { @staff_id            = staff }

    @index = DisciplineIncident.gen_index(id, section_id)
  end

  def self.gen_index(id, section_id)
    (id + section_id * 10) % @@scenario['BEHAVIORS'].count
  end

  def self.gen_behavior(id, section_id)
    "BE#{gen_index(id, section_id)}"
  end

  def self.gen_id(id, section_id)
    "#{section_id}##{id}"
  end
end
