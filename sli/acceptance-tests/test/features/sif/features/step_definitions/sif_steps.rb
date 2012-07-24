=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


require 'rubygems'
require 'mongo'
require 'pp'
require 'rest-client'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

SIF_DB_NAME = PropLoader.getProps['sif_database_name']
SIF_DB = PropLoader.getProps['sif_db']
SIF_ZIS_ADDRESS_TRIGGER = PropLoader.getProps['sif_zis_address_trigger']

############################################################
# STEPS: BEFORE
############################################################

Before do
  @conn = Mongo::Connection.new(SIF_DB)
  @mdb = @conn.db(SIF_DB_NAME)

  @postUri = SIF_ZIS_ADDRESS_TRIGGER
  @format = 'application/xml;charset=utf-8'

  # TODO change these to xml strings
  # @newEntities = {
    # 'SchoolInfo' => {
      # 'id' => 'S1',
      # 'name' => 'New School Name',
      # 'address' => 'New School Address'
    # },
    # 'LEAInfo' => {
      # 'id' => 'LEA1',
      # 'name' => 'New LEA Name'
    # },
    # 'SEAInfo' => {
      # 'id' => 'SEA1',
      # 'name' => 'New SEA Name'
    # }
  # }

  # @existingEntities = {
    # 'SchoolInfo' => {
      # 'id' => 'S1',
      # 'name' => 'Updated School Name',
      # 'address' => 'Updated School Address'
    # },
    # 'LEAInfo' => {
      # 'id' => 'LEA1',
      # 'name' => 'Updated LEA Name'
    # },
    # 'SEAInfo' => {
      # 'id' => 'SEA1',
      # 'name' => 'Updated SEA Name'
    # }
  # }

  @newEntities = {
    'SchoolInfo' => '
<?xml version="1.0"?>
<SIF_Message xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" Version="2.3">
  <SIF_Event>
    <SIF_Header>
      <SIF_MsgId>E89A2B2B571A402BAAE669568035987E</SIF_MsgId>
      <SIF_Timestamp>2012-07-24T13:48:13-04:00</SIF_Timestamp>
      <SIF_Security>
        <SIF_SecureChannel>
          <SIF_AuthenticationLevel>0</SIF_AuthenticationLevel>
          <SIF_EncryptionLevel>0</SIF_EncryptionLevel>
        </SIF_SecureChannel>
      </SIF_Security>
      <SIF_SourceId>test.publisher.agent</SIF_SourceId>
      <SIF_Contexts>
        <SIF_Context>SIF_Default</SIF_Context>
      </SIF_Contexts>
    </SIF_Header>
    <SIF_ObjectData>
      <SIF_EventObject ObjectName="SchoolInfo" Action="Add">
        <SchoolInfo RefId="D3E34B359D75101A8C3D00AA001A1652">
          <StateProvinceId>Daybreak West High</StateProvinceId>
          <NCESId>421575003045</NCESId>
          <SchoolName>Daybreak West High</SchoolName>
          <LEAInfoRefId>73648462888624AA5294BC6380173276</LEAInfoRefId>
          <SchoolType>2402</SchoolType>
          <SchoolFocusList>
            <SchoolFocus>Regular</SchoolFocus>
          </SchoolFocusList>
          <SchoolURL>http://IL-DAYBREAK.edu</SchoolURL>
          <AddressList>
            <Address Type="0123">
              <Street>
                <Line1>1 IBM Plaza</Line1>
                <Line2>Suite 2000</Line2>
                <Line3>Salt Lake City, IL 84102</Line3>
                <StreetNumber>1</StreetNumber>
                <StreetName>IBM way</StreetName>
                <StreetType>Plaza</StreetType>
                <ApartmentType>Suite</ApartmentType>
                <ApartmentNumber>2000</ApartmentNumber>
              </Street>
              <City>Salt Lake City</City>
              <StateProvince>IL</StateProvince>
              <Country>US</Country>
              <PostalCode>84102</PostalCode>
            </Address>
          </AddressList>
          <PhoneNumberList>
            <PhoneNumber Type="0096">
              <Number>(312) 555-1234</Number>
            </PhoneNumber>
            <PhoneNumber Type="2364">
              <Number>(312) 555-2364</Number>
            </PhoneNumber>
          </PhoneNumberList>
          <GradeLevels>
            <GradeLevel>
              <Code>09</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>10</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>11</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>12</Code>
            </GradeLevel>
          </GradeLevels>
          <OperationalStatus>0821</OperationalStatus>
        </SchoolInfo>
      </SIF_EventObject>
    </SIF_ObjectData>
  </SIF_Event>
</SIF_Message>
',
    'LEAInfo' => '
<?xml version="1.0"?>
<SIF_Message xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" Version="2.3">
  <SIF_Event>
    <SIF_Header>
      <SIF_MsgId>E89A2B2B571A402BAAE669568035987E</SIF_MsgId>
      <SIF_Timestamp>2012-07-24T13:48:13-04:00</SIF_Timestamp>
      <SIF_Security>
        <SIF_SecureChannel>
          <SIF_AuthenticationLevel>0</SIF_AuthenticationLevel>
          <SIF_EncryptionLevel>0</SIF_EncryptionLevel>
        </SIF_SecureChannel>
      </SIF_Security>
      <SIF_SourceId>test.publisher.agent</SIF_SourceId>
      <SIF_Contexts>
        <SIF_Context>SIF_Default</SIF_Context>
      </SIF_Contexts>
    </SIF_Header>
    <SIF_ObjectData>
      <SIF_EventObject ObjectName="LEAInfo" Action="Add">
        <LEAInfo RefId="73648462888624AA5294BC6380173276">
          <StateProvinceId>Daybreak School District 4530</StateProvinceId>
          <NCESId>4215750</NCESId>
          <LEAName>Daybreak School District 4530</LEAName>
          <LEAURL>http://IL-DAYBREAK.edu</LEAURL>
          <EducationAgencyType>
            <Code>1</Code>
          </EducationAgencyType>
          <PhoneNumberList>
            <PhoneNumber Type="0096">
              <Number>(312) 555-1234</Number>
            </PhoneNumber>
            <PhoneNumber Type="2364">
              <Number>(312) 555-2364</Number>
            </PhoneNumber>
          </PhoneNumberList>
          <AddressList>
            <Address Type="0123">
              <Street>
                <Line1>1 IBM Plaza</Line1>
                <Line2>Suite 2000</Line2>
                <Line3>Salt Lake City, IL 84102</Line3>
                <StreetNumber>1</StreetNumber>
                <StreetName>IBM way</StreetName>
                <StreetType>Plaza</StreetType>
                <ApartmentType>Suite</ApartmentType>
                <ApartmentNumber>2000</ApartmentNumber>
              </Street>
              <City>Salt Lake City</City>
              <StateProvince>IL</StateProvince>
              <Country>US</Country>
              <PostalCode>84102</PostalCode>
            </Address>
          </AddressList>
          <GradeLevels>
            <GradeLevel>
              <Code>09</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>10</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>11</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>12</Code>
            </GradeLevel>
          </GradeLevels>
          <OperationalStatus>0821</OperationalStatus>
        </LEAInfo>
      </SIF_EventObject>
    </SIF_ObjectData>
  </SIF_Event>
</SIF_Message>
'
  }

  @existingEntities = {
    'SchoolInfo' => '
<?xml version="1.0"?>
<SIF_Message xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" Version="2.3">
  <SIF_Event>
    <SIF_Header>
      <SIF_MsgId>E89A2B2B571A402BAAE669568035987E</SIF_MsgId>
      <SIF_Timestamp>2012-07-24T13:48:13-04:00</SIF_Timestamp>
      <SIF_Security>
        <SIF_SecureChannel>
          <SIF_AuthenticationLevel>0</SIF_AuthenticationLevel>
          <SIF_EncryptionLevel>0</SIF_EncryptionLevel>
        </SIF_SecureChannel>
      </SIF_Security>
      <SIF_SourceId>test.publisher.agent</SIF_SourceId>
      <SIF_Contexts>
        <SIF_Context>SIF_Default</SIF_Context>
      </SIF_Contexts>
    </SIF_Header>
    <SIF_ObjectData>
      <SIF_EventObject ObjectName="SchoolInfo" Action="Add">
        <SchoolInfo RefId="D3E34B359D75101A8C3D00AA001A1652">
          <StateProvinceId>Daybreak West High</StateProvinceId>
          <NCESId>421575003045</NCESId>
          <SchoolName>UPDATED Daybreak West High</SchoolName>
          <LEAInfoRefId>73648462888624AA5294BC6380173276</LEAInfoRefId>
          <SchoolType>2402</SchoolType>
          <SchoolFocusList>
            <SchoolFocus>Regular</SchoolFocus>
          </SchoolFocusList>
          <SchoolURL>http://IL-DAYBREAK.edu</SchoolURL>
          <AddressList>
            <Address Type="0123">
              <Street>
                <Line1>1 IBM Plaza</Line1>
                <Line2>Suite 2000</Line2>
                <Line3>Salt Lake City, IL 84102</Line3>
                <StreetNumber>1</StreetNumber>
                <StreetName>IBM way</StreetName>
                <StreetType>Plaza</StreetType>
                <ApartmentType>Suite</ApartmentType>
                <ApartmentNumber>2000</ApartmentNumber>
              </Street>
              <City>Salt Lake City</City>
              <StateProvince>IL</StateProvince>
              <Country>US</Country>
              <PostalCode>84102</PostalCode>
            </Address>
          </AddressList>
          <PhoneNumberList>
            <PhoneNumber Type="0096">
              <Number>(312) 555-1234</Number>
            </PhoneNumber>
            <PhoneNumber Type="2364">
              <Number>(312) 555-2364</Number>
            </PhoneNumber>
          </PhoneNumberList>
          <GradeLevels>
            <GradeLevel>
              <Code>09</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>10</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>11</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>12</Code>
            </GradeLevel>
          </GradeLevels>
          <OperationalStatus>0821</OperationalStatus>
        </SchoolInfo>
      </SIF_EventObject>
    </SIF_ObjectData>
  </SIF_Event>
</SIF_Message>
',
    'LEAInfo' => '
<?xml version="1.0"?>
<SIF_Message xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" Version="2.3">
  <SIF_Event>
    <SIF_Header>
      <SIF_MsgId>E89A2B2B571A402BAAE669568035987E</SIF_MsgId>
      <SIF_Timestamp>2012-07-24T13:48:13-04:00</SIF_Timestamp>
      <SIF_Security>
        <SIF_SecureChannel>
          <SIF_AuthenticationLevel>0</SIF_AuthenticationLevel>
          <SIF_EncryptionLevel>0</SIF_EncryptionLevel>
        </SIF_SecureChannel>
      </SIF_Security>
      <SIF_SourceId>test.publisher.agent</SIF_SourceId>
      <SIF_Contexts>
        <SIF_Context>SIF_Default</SIF_Context>
      </SIF_Contexts>
    </SIF_Header>
    <SIF_ObjectData>
      <SIF_EventObject ObjectName="LEAInfo" Action="Add">
        <LEAInfo RefId="73648462888624AA5294BC6380173276">
          <StateProvinceId>Daybreak School District 4530</StateProvinceId>
          <NCESId>4215750</NCESId>
          <LEAName>UPDATED Daybreak School District 4530</LEAName>
          <LEAURL>http://IL-DAYBREAK.edu</LEAURL>
          <EducationAgencyType>
            <Code>1</Code>
          </EducationAgencyType>
          <PhoneNumberList>
            <PhoneNumber Type="0096">
              <Number>(312) 555-1234</Number>
            </PhoneNumber>
            <PhoneNumber Type="2364">
              <Number>(312) 555-2364</Number>
            </PhoneNumber>
          </PhoneNumberList>
          <AddressList>
            <Address Type="0123">
              <Street>
                <Line1>1 IBM Plaza</Line1>
                <Line2>Suite 2000</Line2>
                <Line3>Salt Lake City, IL 84102</Line3>
                <StreetNumber>1</StreetNumber>
                <StreetName>IBM way</StreetName>
                <StreetType>Plaza</StreetType>
                <ApartmentType>Suite</ApartmentType>
                <ApartmentNumber>2000</ApartmentNumber>
              </Street>
              <City>Salt Lake City</City>
              <StateProvince>IL</StateProvince>
              <Country>US</Country>
              <PostalCode>84102</PostalCode>
            </Address>
          </AddressList>
          <GradeLevels>
            <GradeLevel>
              <Code>09</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>10</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>11</Code>
            </GradeLevel>
            <GradeLevel>
              <Code>12</Code>
            </GradeLevel>
          </GradeLevels>
          <OperationalStatus>0821</OperationalStatus>
        </LEAInfo>
      </SIF_EventObject>
    </SIF_ObjectData>
  </SIF_Event>
</SIF_Message>
'
  }

  # default
  @entities = @newEntities
end


############################################################
# STEPS: GIVEN
############################################################

Given /^this is a new entity$/ do
  @entities = @newEntities
end

Given /^this is an update to an existing entity$/ do
  @entities = @existingEntities
end

Given /^the following collections are clean in datastore:$/ do |table|
  # table is a Cucumber::Ast::Table
  pending # express the regexp above with the code you wish you had
end

############################################################
# STEPS: WHEN
############################################################


When /^I POST a\(n\) "(.*?)" SIF message$/ do |messageType|
  postMessage(@entities[messageType])
end

# TODO fill in the code here
def postMessage(message)
  puts "POSTing message: #{message}"

  headers = {:content_type => @format}
  @res = RestClient.post(@postUri, message, headers){|response, request, result| response }
  puts(@res.code,@res.body,@res.raw_headers)

  pp @res
end

############################################################
# STEPS: THEN
############################################################

############################################################
# STEPS: AFTER
############################################################

After do
  @conn.close if @conn != nil
end

############################################################
# END
############################################################
