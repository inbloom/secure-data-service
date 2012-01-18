ENV["RAILS_ENV"] = "test"
require File.expand_path('../../config/environment', __FILE__)
require 'rails/test_help'

class ActiveSupport::TestCase
  # Setup all fixtures in test/fixtures/*.(yml|csv) for all tests in alphabetical order.
  #
  # Note: You'll currently still have to declare fixtures explicitly in integration tests
  # -- they do not yet inherit this setting
  # fixtures :all

  # Add more helper methods to be used by all tests here...
  setup do
    path = File.join(Rails.root, "test", "fixtures", "roles.yml")
    return nil unless File.exists?(path)
    @role_fixtures = YAML::load(File.open path)
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "/api/rest/admin/roles?sessionId=", {"Accept" => "application/json"}, [@role_fixtures["admin"], @role_fixtures["educator"]].to_json
      mock.get "/api/rest/admin/roles/0?sessionId=", {"Accept" => "application/json"}, @role_fixtures["admin"].to_json
      mock.get "/api/rest/admin/roles/1?sessionId=", {"Accept" => "application/json"}, @role_fixtures["educator"].to_json
      mock.get "/api/rest/admin/roles/-123?sessionId=", {"Accept" => "application/json"}, nil, 404
      mock.put "/api/rest/admin/roles/?sessionId=", {"Content-Type" => "application/json"}, nil, 200
    end
  end

end
