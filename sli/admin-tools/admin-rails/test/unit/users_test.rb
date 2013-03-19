=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

class UsersTest < ActiveSupport::TestCase

  def setup
    @valid_user = User.new :email => "test@test.com", :fullName => 'Test User'
  end

  test "valid user" do
    assert_equal true, @valid_user.valid?
    @valid_user.email = ""
    assert_equal false, @valid_user.valid?
    @valid_user.fullName = ""
    @valid_user.email = "test@test.com"
    assert_equal false, @valid_user.valid?
  end

  test "tenant field" do
    @valid_user.tenant = "tenant"
    assert_equal true, @valid_user.valid?
    @valid_user.tenant = ""
    assert_equal true, @valid_user.valid?
    @valid_user.tenant = "tenant$%#!"
    assert_equal false, @valid_user.valid?
    @valid_user.tenant = "tenant.tenant"
    assert_equal true, @valid_user.valid?
    @valid_user.tenant = ".tenant"
    assert_equal false, @valid_user.valid?
    @valid_user.tenant = "@tenant"
    assert_equal false, @valid_user.valid?
    @valid_user.tenant = "ten@nt"
    assert_equal true, @valid_user.valid?
    @valid_user.tenant = "A" * 160
    assert_equal true, @valid_user.valid?
    @valid_user.tenant = "A" * 161
    assert_equal false, @valid_user.valid?
  end

  test "edorg field" do
    @valid_user.edorg = "edorg"
    assert_equal true, @valid_user.valid?
    @valid_user.edorg = ""
    assert_equal true, @valid_user.valid?
    @valid_user.edorg = "A" * 255
    assert_equal true, @valid_user.valid?
    @valid_user.edorg = "A" * 256
    assert_equal false, @valid_user.valid?
  end
end
