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
