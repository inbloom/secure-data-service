require 'test_helper'

class RoleTest < ActiveSupport::TestCase
  test "get roles" do
    returned_roles = Role.all
    assert_not_nil(returned_roles)
    assert_equal(returned_rols.size, @roles.size)
  end
end
