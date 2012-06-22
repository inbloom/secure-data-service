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


require 'test_helper'

class RoleTest < ActiveSupport::TestCase
  test "get roles" do
    returned_roles = Role.all
    assert_not_nil(returned_roles)
    assert_equal(returned_roles.size, @role_fixtures.size)
  end
  
  test "find admin" do
    returned_role = Role.find(0).attributes
    assert_not_nil(returned_role)
    assert_equal(returned_role, @role_fixtures["admin"])
  end
  
  test "find failures" do
    assert_raise(ActiveResource::ResourceNotFound) { Role.find(-123) }
  end
  
  test "get static data" do
    static = Role.get_static_information
    assert_not_nil(static)
    assert_equal(static.size, 2)
  end
end
