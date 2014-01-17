And /^an authorized app key has been created$/ do
  @oauthClientId = "fm67sH6vZZ"
  @oauthClientSecret = "sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4"
  @oauthRedirectURI = "http://local.slidev.org:8081/sample/callback"
end

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
  restHttpPatch("/v1/educationOrganizations/#{ed_org_id}", patch_ed_org.to_json, 'application/json', @sessionId)
end

Then /^I should receive a (\d+) error from the API$/ do |http_code|
  assert(@res.code == 403, "HTTP code is #{@res.code} - WRITE PUBLIC access should have been denied")
end

After('@DS917') do
  db = @conn[convertTenantIdToDbName("Midgar")]
  seoas = db.collection("staffEducationOrganizationAssociation")
  seoas.remove("_id" => @seoas_id)
end




