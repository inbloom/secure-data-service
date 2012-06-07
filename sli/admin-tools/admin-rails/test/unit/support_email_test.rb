require 'test_helper'

class SupportEmailTest < ActiveSupport::TestCase
  test "get support email" do
    email = SupportEmail.get("")
    assert_not_nil(email)
  end
end
