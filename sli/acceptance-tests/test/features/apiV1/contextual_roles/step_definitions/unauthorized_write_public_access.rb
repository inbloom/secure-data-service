#And /^an authorized app key has been created$/ do
#  @oauthClientId = "fm67sH6vZZ"
#  @oauthClientSecret = "sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4"
#  @oauthRedirectURI = "http://local.slidev.org:8081/sample/callback"
#end

Given /^I associate "(.*?)" to edorg "(.*?)" with the role "(.*?)"$/ do |staff_id, ed_org_id, staff_classification|
  @tenant = "Midgar"
  @realm = "IL-Daybreak"
  @seoas_id = "b1c40ccc-b466-8f3b-b3c7-7e13c2bc4d5a-qwerty"

  body = { "beginDate" => "2001-01-01",
           "staffReference" => staff_id,
           "educationOrganizationReference" => ed_org_id,
           "staffClassification" => staff_classification }

  db = @conn[convertTenantIdToDbName(@tenant)]
  seoas = db.collection("staffEducationOrganizationAssociation")

  puts seoas.find(:_id => @seoas_id).first if $SLI_DEBUG

  seoas.insert("_id" => @seoas_id, "type" => "staffEducationOrganizationAssociation", "body" => body)
end

When /^I PATCH the edorg "(.*?)"$/ do |ed_org_id|
  patch_ed_org = { "nameOfInstitution" => "This Should Fail Junior High"}
  restHttpPatch("/v1/educationOrganizations/#{ed_org_id}", patch_ed_org.to_json, 'application/json')
end

When /^I PUT the edorg "(.*?)"$/ do |ed_org_id|
  edorg_map = edorg_reference
  restHttpPut("/v1/educationOrganizations/#{ed_org_id}", edorg_map.to_json, 'application/vnd.slc+json')
end

When /^I DELETE the edorg "(.*?)"$/ do |ed_org_id|
  restHttpDelete("/v1/educationOrganizations/#{ed_org_id}", 'application/json')
end

When /^I GET the edorg "(.*?)"$/ do |ed_org_id|
  restHttpGet("/v1/educationOrganizations/#{ed_org_id}", 'application/json')
end

Then /^I should receive a (\d+) Denied$/ do |http_code|
  @res.code.should == 403
end

Then /^I should receive a (\d+) OK$/ do |http_code|
  @res.code.should == 200
end

After('@DS917') do
  db = @conn[convertTenantIdToDbName("Midgar")]
  seoas = db.collection("staffEducationOrganizationAssociation")
  seoas.remove("_id" => @seoas_id)
end

def edorg_reference
  {
      'body' => {
      'schoolCategories'=> [
      'Junior High School'
  ],
      'accountabilityRatings'=> [

  ],
      'organizationCategories'=> [
      'School',
      'School',
      'School'
  ],
      'gradesOffered'=> [
      'Sixth grade',
      'Seventh grade',
      'Eighth grade'
  ],
      'address'=> [
      {
          'nameOfCounty'=> 'Wake',
      'streetNumberName'=> '111 Ave A',
      'postalCode'=> '11011',
      'stateAbbreviation'=> 'IL',
      'addressType'=> 'Physical',
      'city'=> 'Chicago'
  }
  ],
      'educationOrgIdentificationCode'=> [
      {
          'identificationSystem'=> 'School',
      'ID'=> 'East Daybreak Junior High'
  }
  ],
      'programReference'=> [

  ],
      'stateOrganizationId'=> 'East Daybreak Junior High',
      'parentEducationAgencyReference'=> [
      'bd086bae-ee82-4cf2-baf9-221a9407ea07'
  ],
      'telephone'=> [
      {
          'institutionTelephoneNumberType'=> 'Main',
      'telephoneNumber' => '(917)-555-0212'
  }
  ],
      'nameOfInstitution'=> 'East Daybreak Junior High'
  },
      'metaData'=> {
      'edOrgs'=> [
      'ec2e4218-6483-4e9c-8954-0aecccfd4731',
      'b1bd3db6-d020-4651-b1b8-a8dba688d9e1',
      'bd086bae-ee82-4cf2-baf9-221a9407ea07'
  ]
  },
      'type'=> 'educationOrganization'
  }
end