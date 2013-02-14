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
require_relative '../date_utility'
require_relative 'baseEntity'
require_relative 'enum/CharterStatusType'
require_relative 'enum/OperationalStatusType'

# creates local education agency
#
# From SLI-Ed-Fi-Core.xsd:
#  <xs:complexType name="SLC-EducationOrganization" abstract="true">
#    <xs:annotation>
#      <xs:documentation>EducationOrganization record with key field: StateOrganizationId. Changed types of ProgramReference and EducationOrganizationPeerReference to SLC reference types.</xs:documentation>
#      <xs:appinfo>
#        <sli:recordType>educationOrganization</sli:recordType>
#      </xs:appinfo>
#    </xs:annotation>
#    <xs:complexContent>
#      <xs:extension base="ComplexObjectType">
#        <xs:sequence>
#          <xs:element name="StateOrganizationId" type="IdentificationCode"/>
#          <xs:element name="EducationOrgIdentificationCode" type="EducationOrgIdentificationCode" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="NameOfInstitution" type="NameOfInstitution"/>
#          <xs:element name="ShortNameOfInstitution" type="NameOfInstitution" minOccurs="0"/>
#          <xs:element name="OrganizationCategories" type="EducationOrganizationCategoriesType"/>
#          <xs:element name="Address" type="Address" maxOccurs="unbounded"/>
#          <xs:element name="Telephone" type="InstitutionTelephone" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="WebSite" type="WebSite" minOccurs="0"/>
#          <xs:element name="OperationalStatus" type="OperationalStatusType" minOccurs="0"/>
#          <xs:element name="AccountabilityRatings" type="AccountabilityRating" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="ProgramReference" type="SLC-ProgramReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#          <xs:element name="EducationOrganizationPeerReference" type="EducationalOrgReferenceType" minOccurs="0" maxOccurs="unbounded"/>
#        </xs:sequence>
#      </xs:extension>
#    </xs:complexContent>
#  </xs:complexType>
#
#  <xs:complexType name="SLC-LocalEducationAgency">
#    <xs:annotation>
#      <xs:documentation>LocalEducationAgency record with key field: StateOrganizationId. Changed type of LocalEducationAgencyReference, EducationServiceCenterReference and StateEducationAgencyReference to SLC reference types.</xs:documentation>
#      <xs:appinfo>
#        <sli:recordType>localEducationAgency</sli:recordType>
#      </xs:appinfo>
#    </xs:annotation>
#    <xs:complexContent>
#      <xs:extension base="SLC-EducationOrganization">
#        <xs:sequence>
#          <xs:element name="LEACategory" type="LEACategoryType"/>
#          <xs:element name="CharterStatus" type="CharterStatusType" minOccurs="0"/>
#          <xs:element name="LocalEducationAgencyReference" type="SLC-EducationalOrgReferenceType" minOccurs="0"/>
#          <xs:element name="EducationServiceCenterReference" type="SLC-EducationalOrgReferenceType" minOccurs="0"/>
#          <xs:element name="StateEducationAgencyReference" type="SLC-EducationalOrgReferenceType" minOccurs="0"/>
#        </xs:sequence>
#      </xs:extension>
#    </xs:complexContent>
#  </xs:complexType>
class LocalEducationAgency < BaseEntity

  # required fields
  attr_accessor :state_org_id   # maps to 'StateOrganizationId', 'NameOfInstitution'
  attr_accessor :address        # maps to 'Address'
  attr_accessor :programs       # maps to 'ProgramReference'
  
  # optional fields
  attr_accessor :ed_org_id_code # maps to 'EducationOrgIdentificationCode'
  attr_accessor :short_name     # maps to 'ShortNameOfInstitution'
  attr_accessor :telephone      # maps to 'Telephone'
  attr_accessor :website        # maps to 'WebSite'
  attr_accessor :accountability # maps to 'AccountabilityRatings'
  attr_accessor :ed_org_peers   # maps to 'EducationOrganizationPeerReference'
  attr_accessor :lea_parent_id  # maps to 'LocalEducationAgencyReference'
  attr_accessor :esc_parent_id  # maps to 'EducationServiceCenterReference'
  attr_accessor :sea_parent_id  # maps to 'StateEducationAgencyReference'

  attr_accessor :charter_status
  attr_accessor :op_status

  # fields not needed:
  # - OrganizationCategories (hard-coded in mustache template)
  # - LEACategory (hard-coded in mustache template)

  def initialize(id, sea_parent_id, programs = [], years = [])    
    @rand = Random.new(9876)
    @rand = Random.new(id) unless id.kind_of? String

    @state_org_id   = get_state_organization_id(id)
    @address        = get_address
    @programs       = programs
    @sea_parent_id  = sea_parent_id 

    # leave sea parent above get_accountability_ratings --> current rating organization
    optional { @ed_org_id_code = @state_org_id }
   # optional { @sea_parent_id  = sea_parent_id }
    optional { @short_name     = get_short_name }
    optional { @telephone      = get_telephone }
    optional { @website        = 'http://fake.local-education-agency.org.fake/fake' }
    optional { @op_status      = get_random_operational_status }
    optional { @accountability = get_accountability_ratings(years) unless years.nil? || years.empty? }
    optional { @ed_org_peers   = [] }
    optional { @charter_status = get_random_charter_status_type(@rand) }
    optional { @lea_parent_id  = nil }   # this will need to be added when odin can generate multiple sub-tiers within the LEA tier
    optional { @esc_parent_id  = nil }   # can we ingest education service centers? if so, do we have any security model for them?
  end

  # maps to optional field 'CharterStatus'
  def charter_status_type
    CharterStatusType.to_string(@charter_status)
  end

  # maps to optional field 'OperationalStatus'
  def op_status_type
    OperationalStatusType.to_string(@op_status)
  end

  # gets the state organization id of the local education agency
  def get_state_organization_id(id)
    return id if id.kind_of? String
    return DataUtility.get_local_education_agency_id(id)
  end

  # generates the address of the local education agency
  def get_address
    address = {}
    begin
      address[:line_one] = @rand.rand(1000).to_s + " " + DataUtility.select_random_from_options(@rand, BaseEntity.demographics['street'])
      address[:city] = BaseEntity.demographics['city']
      address[:state] = BaseEntity.demographics['state']
      address[:postal_code] = BaseEntity.demographics['postalCode']
    rescue NameError
      # occurs when @@d in BaseEntity hasn't been initialized (will happen during testing)
      return nil
    end
    address
  end

  # gets the short name of the local education agency
  def get_short_name
    @state_org_id.to_s + " LEA"
  end

  # generates the telephone number of the local education agency
  def get_telephone
    area_code        = @rand.rand(1000).to_s.rjust(3, '0')
    last_four_digits = @rand.rand(10000).to_s.rjust(4, '0')
    "(" + area_code + ") 555-" + last_four_digits
  end

  # generates the accountability ratings of the local education agency
  def get_accountability_ratings(years)
    ratings = []
    years.each do |year|
      title       = "#{year} rating: #{@state_org_id}"
      rating      = get_random_rating
      date        = DateUtility.random_date_from_years(@rand, year)
      school_year = "#{year}-#{year+1}"
      ratings << {:title => title, :rating => rating, :date => date, :year => school_year, :rating_org => @sea_parent_id, :program => "AEIS"}
    end
    ratings
  end

  # generates the operational status of the local education agency
  def get_random_operational_status
    statuses = [:ACTIVE, :ADDED, :CHANGED_AGENCY, :CONTINUING, :NEW, :REOPENED]
    DataUtility.select_random_from_options(@rand, statuses)
  end

  # generates an accountability rating, as per AEIS standard ratings
  def get_random_rating
    ratings = [
      ['Low Unsatisfactory', 2], 
      ['High Unsatisfactory', 3], 
      ['Low Satisfactory', 10], 
      ['Mid Satisfactory', 15], 
      ['High Satisfactory', 20], 
      ['Low Advanced', 25], 
      ['High Advanced', 25]
    ]
    wChooseUsingRand(@rand, ratings)
  end

  # generates the charter status of the local education agency
  def get_random_charter_status_type(random)
    DataUtility.select_random_from_options(random, [:OPEN_ENROLLMENT, :NOT_A_CHARTER_SCHOOL])
  end
end
