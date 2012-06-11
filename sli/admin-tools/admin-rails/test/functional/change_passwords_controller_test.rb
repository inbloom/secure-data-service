require 'test_helper'
require "mocha"

class ChangePasswordsControllerTest < ActionController::TestCase
  setup do
    @change_password = ChangePassword.new(
        :old_pass => 'test1234',
        :new_pass => 'testabcd',
        :confirmation => 'testabcd'
    )
  end

  test "should get index" do
    get :index
    assert_response :success
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create change_password" do
    post :create, change_password: @change_password.attributes
    assert_response :success
  end


end
