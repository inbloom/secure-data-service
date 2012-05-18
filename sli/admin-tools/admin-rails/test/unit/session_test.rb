require 'test_helper'

class EulaTest < ActiveSupport::TestCase

  test "session valid method" do
    fail_params = { :some_key => "some value" }
    success_params = { :guuid => "anything" }

    assert_equal false, Session.valid?(fail_params)
    assert_equal true, Session.valid?(success_params)
  end
end