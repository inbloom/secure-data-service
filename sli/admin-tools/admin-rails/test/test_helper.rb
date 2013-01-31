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



require 'simplecov'
require 'simplecov-rcov'
SimpleCov.formatter = SimpleCov::Formatter::RcovFormatter
SimpleCov.start 'rails'

ENV["RAILS_ENV"] = "test"
require File.expand_path('../../config/environment', __FILE__)
require 'rails/test_help'
require 'active_resource/http_mock'

class ActiveSupport::TestCase
  # Setup all fixtures in test/fixtures/*.(yml|csv) for all tests in alphabetical order.
  #
  # Note: You'll currently still have to declare fixtures explicitly in integration tests
  # -- they do not yet inherit this setting
  # fixtures :all

  def load_fixture(name)
    path = File.join(Rails.root, "test", "fixtures", "#{name}.yml")
    return nil unless File.exists?(path)
    YAML::load(File.open path)
  end

  # Add more helper methods to be used by all tests here...
  setup do
    @realm_fixtures = load_fixture("realms")
    @app_fixtures = load_fixture("apps")
    @appauth_fixtures = load_fixture("application_authorizations")
    @ed_org_fixtures = load_fixture("education_organization")
    @account_managements_fixtures=load_fixture("account_managements")
    @admin_delegations_fixtures = load_fixture("admin_delegations")
    @custom_roles_fixtures = load_fixture("custom_role")
    @user_fixtures = load_fixture("users")

    ActiveResource::HttpMock.respond_to do |mock|
      #Realms
      mock.get "/api/rest/realm", {"Accept" => "application/json"}, [@realm_fixtures['one'], @realm_fixtures['two']].to_json
      mock.get "/api/rest/realm?realm.idp.id=http%3A%2F%2Fslidev.org", {"Accept" => "application/json"}, [@realm_fixtures['one']].to_json
      mock.get "/api/rest/realm?realm.idp.id=blah", {"Accept" => "application/json"}, [].to_json
      mock.get "/api/rest/realm/1", {"Accept" => "application/json"}, @realm_fixtures['one'].to_json
      mock.get "/api/rest/realm/5a4bfe96-1724-4565-9db1-35b3796e3ce2", {"Accept" => "application/json"}, nil, 404
      mock.put "/api/rest/realm/1", {"Content-Type"=>"application/json"}, {}

      #apps
      mock.get "/api/rest/apps", {"Accept" => "application/json"}, [@app_fixtures['admin'], @app_fixtures['waffles']].to_json
      mock.get "/api/rest/apps/1", {"Accept" => "application/json"}, @app_fixtures['admin'].to_json
      mock.post "/api/rest/apps", {"Content-Type" => "application/json"}, @app_fixtures['new'].to_json, 201
      mock.put "/api/rest/apps/1", {"Content-Type" => "application/json"}, @app_fixtures['admin'].merge(@app_fixtures['update']).to_json, 201

      #app auth
      mock.get "/api/rest/applicationAuthorization", {"Accept" => "application/json"}, [@appauth_fixtures['district1']].to_json
      #mock.get "/api/rest/applicationAuthorization", {"Accept" => "application/json"}, [].to_json
      mock.get "/api/rest/applicationAuthorization/1", {"Accept" => "application/json"}, @appauth_fixtures['district1'].to_json
      mock.post "/api/rest/applicationAuthorization", {"Content-Type" => "application/json"}, @app_fixtures['new_district'], 201
      mock.put "/api/rest/applicationAuthorization/1", {"Content-Type" => "application/json"}, @app_fixtures['district'], 201
      mock.get "/api/rest/system/session/check/", {"Accept" => "application/json"}, @appauth_fixtures['sessionCheck'].to_json

      #ed orgs
      mock.get "/api/rest/v1/educationOrganizations", {"Accept" => "application/json"}, [@ed_org_fixtures["state"], @ed_org_fixtures["local"]].to_json
      mock.get "/api/rest/v1/educationOrganizations?parentEducationAgencyReference=1", {"Accept" => "application/json"}, [@ed_org_fixtures["state"], @ed_org_fixtures["local"]].to_json
      mock.get "/api/rest/v1/educationOrganizations?organizationCategories=State+Education+Agency", {"Accept" => "application/json"}, [@ed_org_fixtures["state"]].to_json
      mock.get "/api/rest/v1/educationOrganizations?address.stateAbbreviation=NC&limit=0&organizationCategories=Local+Education+Agency", {"Accept" => "application/json"}, [@ed_org_fixtures["local"]].to_json
      mock.get "/api/rest/v1/educationOrganizations?address.stateAbbreviation=NC&limit=0&offset=1&organizationCategories=Local+Education+Agency", {"Accept" => "application/json"}, [].to_json
      mock.get "/api/rest/v1/educationOrganizations?parentEducationAgencyReference=2", {"Accept" => "application/json"}, [@ed_org_fixtures["state"], @ed_org_fixtures["local"]].to_json
      mock.get "/api/rest/v1/educationOrganizations/ID1", {"Accept" => "application/json"}, @ed_org_fixtures["local"].to_json
      mock.head "/api/rest/v1/educationOrganizations/ID1", {"Accept" => "application/json"}, @ed_org_fixtures["local"].to_json
      mock.get "/api/rest/v1/educationOrganizations/?stateOrganizationId=Waffles", {"Accept" => "application/json"}, @ed_org_fixtures["waffles"].to_json

      #admin delegations
      mock.get "/api/rest/adminDelegation", {"Accept" => "application/json"}, [@admin_delegations_fixtures["one"]].to_json
      mock.post "/api/rest/adminDelegation", {"Content-Type" => "application/json"}, @admin_delegations_fixtures["one"].to_json, 201

      #Support email
      mock.get "/api/rest/v1/system/support/email/", {"Accept" => "application/json"}, {"email" => "email@email.com"}.to_json

      #mock.get "/api/rest/educationOrganization-associations", {"Accept" => "application/json"}, [@ed_org_fixtures["assoc_one"]].to_json
      #ed org associations
      mock.get "/api/rest/"

      #change password
      mock.get "/api/rest/change_passwords", {"Accept"=>"application/json"}, [].to_json

      # users
      mock.get "/api/rest/users",{"Accept"=>"application/json"}, [@user_fixtures['user1']].to_json
      mock.delete "/api/rest/users/testuser@testwgen.net", {"Accept"=>"application/json"}, nil,200
      mock.post "/api/rest/users", {"Content-Type"=>"application/json"}, @user_fixtures['new_user'].to_json,201
      mock.put "/api/rest/users/testuser@testwgen.net", {"Content-Type"=>"application/json"}, @user_fixtures['update_user'].to_json,204

    end
  end

end
class MockResponse
  @responseCode
  @validation
  @body

  def initialize(newCode,newValidation=true,newBody="DEFAULT")
    @responseCode = newCode
    @validation = newValidation
    @body = newBody
  end

  def body
    if @body == "DEFAULT"
      return "[{\"validated\":#{@validation}, \"id\":\"1234567890\"}]"
    else
      return @body
    end
  end

  def code
    return @responseCode
  end
  def raw_headers
    return {
        "location"=>["http://host:8080/api/rest/v1/userAccounts/1234567890"],
        "content-type"=>["application/json"],
        "content-length"=>["0"],
        "server"=>["Jetty(6.1.10)"]}
  end
end

require 'mocha'
