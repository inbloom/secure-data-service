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
    @role_fixtures = load_fixture("roles")
    @realm_fixtures = load_fixture("realms")
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "/api/rest/admin/roles?sessionId=", {"Accept" => "application/json"}, [@role_fixtures["admin"], @role_fixtures["educator"]].to_json
      mock.get "/api/rest/admin/roles/0?sessionId=", {"Accept" => "application/json"}, @role_fixtures["admin"].to_json
      mock.get "/api/rest/admin/roles/1?sessionId=", {"Accept" => "application/json"}, @role_fixtures["educator"].to_json
      mock.get "/api/rest/admin/roles/-123?sessionId=", {"Accept" => "application/json"}, nil, 404
      #Realms
      mock.get "/api/rest/realm?sessionId=", {"Accept" => "application/json"}, [@realm_fixtures['one'], @realm_fixtures['two']].to_json
      mock.get "/api/rest/realm?realm.idp.id=http%3A%2F%2Fslidev.org&sessionId=", {"Accept" => "application/json"}, [@realm_fixtures['one']].to_json
      mock.get "/api/rest/realm?realm.idp.id=blah&sessionId=", {"Accept" => "application/json"}, [].to_json
      mock.get "/api/rest/realm/1?sessionId=", {"Accept" => "application/json"}, @realm_fixtures['one'].to_json
      mock.get "/api/rest/realm/5a4bfe96-1724-4565-9db1-35b3796e3ce2?sessionId=", {"Accept" => "application/json"}, nil, 404
      mock.put "/api/rest/realm/1?sessionId=", {"Content-Type"=>"application/json"}, {}
    end
  end

end
