require 'simplecov'
require 'simplecov-rcov'
SimpleCov.formatter = SimpleCov::Formatter::RcovFormatter
SimpleCov.start 'rails'

ENV["RAILS_ENV"] = "test"
require File.expand_path('../../config/environment', __FILE__)
require 'rails/test_help'

class ActiveSupport::TestCase
  # Setup all fixtures in test/fixtures/*.(yml|csv) for all tests in alphabetical order.
  #
  # Note: You'll currently still have to declare fixtures explicitly in integration tests
  # -- they do not yet inherit this setting
  #fixtures :all

  # Add more helper methods to be used by all tests here...
  def load_fixture(name)
    path = File.join(Rails.root, "test", "fixtures", "#{name}.yml")
    puts("#{path}")
    return nil unless File.exists?(path)
    YAML::load(File.open path)
  end
  setup do
  	#@user_account_registration = load_fixture("user_account_registrations")
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
  	puts("#{@body}")
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