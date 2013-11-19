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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'uri'
include REXML
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'



def findUserToken(developerLogin)
  @conn = Mongo::Connection.new(PropLoader.getProps['sli_db'], PropLoader.getProps['sli_db_port']) if !defined? @conn
  @db = @conn.db('sli')
  @coll = @db['userSession']
  session = @coll.find_one('body.principal.externalId'=>developerLogin)
  token = session['body']['appSession'][0]['token']
end

When(/^I login as developer "([^"]*)" and create application named "([^"]*)"$/) do |devName, appName|
  resourceBody = <<-jsonDelimiter
  {
    "isBulkExtract" : "false",
    "version" : "0.0",
    "image_url" : "http://placekitten.com/150/150",
    "administration_url" : "http://local.slidev.org:8081/sample/",
    "application_url" : "http://local.slidev.org:8081/sample",
    "redirect_uri" : "http://local.slidev.org:8081/sample/callback",
    "description" : "-----PLACEHOLDER-------",
    "name" : "-----PLACEHOLDER-------",
    "created_by" : "-----PLACEHOLDER-------",
    "is_admin" : false,
    "installed" : false,
    "behavior" : "Full Window App",
    "vendor" : "-----PLACEHOLDER-------"
  }
  jsonDelimiter
  resourceBody                    = JSON.parse(resourceBody)
  resourceBody["description"]     = appName
  resourceBody["name"]            = appName
  resourceBody["created_by"]      = "#{appName} creator"
  resourceBody["vendor"]          = "#{appName} vendor"

  devSessionId                    = findUserToken(devName)
  restHttpPost('/apps', resourceBody.to_json, 'application/vnd.slc+json', devSessionId)
  assert(@res.code == 201, "#{devName} could not create app1! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
  appId = @res.raw_headers['location'][0].split(/\//)[-1]
  location = "/apps/#{appId}"
  restHttpGet(location, 'application/json', devSessionId)
  assert(@res.code == 200, "#{devName} could not fetch newly created app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
  $createdEntities[appName]  = JSON.parse @res
  $createdEntityIds[appName] = appId
end

When(/^I login as slcoperator and approve application named "([^"]*)"$/) do |appName|
  resourceBody                           = $createdEntities[appName]
  resourceBody['registration']['status'] = 'APPROVED'
  sessionId                              = findUserToken('operator')
  restHttpPut("/apps/#{$createdEntityIds[appName]}", resourceBody.to_json, 'application/vnd.slc+json', sessionId)
  assert(@res.code == 204, "operator could not update app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
end

When(/^I login as developer "([^"]*)" and ([^ ]*) application named "([^"]*)" for "([^"]*)"$/) do |devName, enableDisable, appName, edOrg|

  enable =  case enableDisable
              when "enable"
                true
              when "disable"
                false
              else
                raise "Cannot '#{enableDisable}' application. Can only 'enable' or 'disable'."
            end

  edOrgId                  = $createdEntityIds[edOrg]
  appId                    = $createdEntityIds[appName]
  location                 = "/apps/#{appId}"
  devSessionId             = findUserToken(devName)
  restHttpGet(location, 'application/json', devSessionId)
  assert(@res.code == 200, "#{devName} could not fetch newly created app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
  app = JSON.parse @res
  app['authorized_ed_orgs'] = [] if !app['authorized_ed_orgs']

  if enable
      app['authorized_ed_orgs'] << edOrgId
  else
    app['authorized_ed_orgs'] = app['authorized_ed_orgs'].reject { |eo|
        eo == edOrgId
    }
  end

  restHttpPut("/apps/#{appId}", app.to_json, 'application/vnd.slc+json', devSessionId)
  assert(@res.code == 204, "#{devName} could not update app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
end

When(/^I ([^ ]*) a "([^"]*)" StaffEducationOrganizationAssociation  between "([^"]*)" and "([^"]*)"$/) do |addRemove, role, staff, edOrg|
    add = case addRemove
            when "add"
              true
            when "remove"
              false
            else
              raise "Cannot '#{addRemove}' StaffEducationOrganizationAssociation. Can only 'add' or 'remove'."
          end
  staffEducationOrganizationAssociation = <<-jsonDelimiter
  {
		"staffClassification" : "-----PLACEHOLDER-------",
		"educationOrganizationReference" : "-----PLACEHOLDER-------",
		"positionTitle" : "-----PLACEHOLDER-------",
		"staffReference" : "-----PLACEHOLDER-------",
		"endDate" : "2023-08-13",
		"beginDate" : "1967-08-13"
	}
  jsonDelimiter


  puts "SEO  Staff #{findStaffId(staff)}   EO #{$createdEntities[edOrg]['id']}"
  if add
    staffEducationOrganizationAssociation                                        = JSON.parse(staffEducationOrganizationAssociation)
    staffEducationOrganizationAssociation['staffReference']                      = findStaffId(staff)
    staffEducationOrganizationAssociation['educationOrganizationReference' ]     = $createdEntityIds[edOrg]
    staffEducationOrganizationAssociation['staffClassification' ]                = role
    staffEducationOrganizationAssociation['positionTitle' ]                      = role

    restHttpPost('/v1/staffEducationOrgAssignmentAssociations', staffEducationOrganizationAssociation.to_json, 'application/vnd.slc+json')
    assert(@res.code == 201, "Could not create StaffEducationOrganizationAssociation! HTTP CODE:#{@res.code}  HTTP BODY#{@res.body}")
    location = @res.raw_headers['location'][0]
    createdId = location.split(/\//)[-1]
    $createdEntityIds[role + staff + edOrg] = createdId
    $createdEntities[role + staff + edOrg]  = location
  else
    location = $createdEntities[role + staff + edOrg]
    createdId = location.to_s.split("/").last
    assert(createdId != nil, "Cannot find ID for StaffEducationOrganizationAssociation. Are you sure you created it in one of the steps? Have URLs for [#{$createdEntities.keys}]")
    puts "staffEducationOrgAssignmentAssociations ID: " + createdId
    restHttpDelete("/v1/staffEducationOrgAssignmentAssociations/#{createdId}", 'application/vnd.slc+json')
    assert(@res.code == 204, "Could not delete StaffEducationOrganizationAssociation! HTTP CODE:#{@res.code}  HTTP BODY#{@res.body}")
  end
end

When(/^I try to get app "([^"]*)" and get a response code "([^"]*)"$/) do |id, respCode|
  entityLink = $createdEntities[id]['links'].select{|link| link['rel'] == 'self'}[0]['href']
  restHttpGetAbs(entityLink, 'application/json')
  assert(@res.code.to_s == respCode, "Got [#{@res.code}] while fetching #{entityLink}. Expected [#{respCode}]")
end

When(/^I try to read the authorization for "([^"]*)" and get a response code of "([^"]*)"$/) do |appName, respCode|
  appId = $createdEntityIds[appName]
  authUrl = "/applicationAuthorization/#{appId}"
  restHttpGet("/applicationAuthorization/#{appId}", 'application/json')
  assert(@res.code.to_s == respCode, "Got [#{@res.code}] while fetching #{authUrl}. Expected [#{respCode}]")
end

When(/^I try to ([^ ]*) "([^"]*)" to access "([^"]*)" and get a response code of "([^"]*)"$/) do |authOrDeAuth, appName, edOrgName, respCode|

  auth = case authOrDeAuth
          when "authorize"
            true
          when "deAuthorize"
            false
          else
            raise "Cannot '#{authOrDeAuth}' StaffEducationOrganizationAssociation. Can only 'authorize' or 'deAuthorize'."
        end

  appId = $createdEntityIds[appName]
  edOrgId = $createdEntityIds[edOrgName]

  puts "Authorizing app [#{appName} #{appId}] to access [#{edOrgName} #{edOrgId}]"
  authRequest = {
      'authorized'    =>auth,
      'applicationId' =>appId,
      'edorgs'        =>[edOrgId]
  }
  authUrl = "/applicationAuthorization/#{appId}"
  restHttpPut(authUrl, authRequest.to_json, 'application/vnd.slc+json')
  assert(@res.code.to_s == respCode, "Got [#{@res.code}] while putting #{authUrl}. Expected [#{respCode}]")
end

When(/^I login as developer "([^"]*)" and ([^ ]*) application named "([^"]*)" for all edOrgs$/) do |devName, enableDisable, appName|
  enable =  case enableDisable
              when "enable"
                true
              when "disable"
                false
              else
                raise "Cannot '#{enableDisable}' application. Can only 'enable' or 'disable'."
            end

  appId                    = $createdEntityIds[appName]
  location                 = "/apps/#{appId}"
  devSessionId             = findUserToken(devName)
  restHttpGet(location, 'application/json', devSessionId)
  assert(@res.code == 200, "#{devName} could not fetch newly created app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
  app = JSON.parse @res

  if enable
    app['authorized_for_all_edorgs'] = true
  else
    app['authorized_for_all_edorgs'] = false
  end

  restHttpPut("/apps/#{appId}", app.to_json, 'application/vnd.slc+json', devSessionId)
  assert(@res.code == 204, "#{devName} could not update app! HTTP CODE:#{@res.code}  HTP BODY#{@res.body}")
end