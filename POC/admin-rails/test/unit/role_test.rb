require 'test_helper'

class RoleTest < ActiveSupport::TestCase
  test "get roles" do
    returned_roles = Role.all
    assert_not_nil(returned_roles)
    puts "Returned #{returned_roles.size}/#{@role_fixtures.size}"
    assert_equal(returned_roles.size, @role_fixtures.size-1)
  end
  
  test "find admin" do
    returned_role = Role.find(0).attributes
    
    assert_not_nil(returned_role)
    assert_equal(returned_role, @role_fixtures["admin"])
  end
  
  test "find failures" do
    assert_raise(ActiveResource::ResourceNotFound) { Role.find(-123) }
  end
end
