require 'test_helper'

class HomeTest < ActiveSupport::TestCase
  test "the index" do
    assert_not_nil(Home.all)
  end
end
