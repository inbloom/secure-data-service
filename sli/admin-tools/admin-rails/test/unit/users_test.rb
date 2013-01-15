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