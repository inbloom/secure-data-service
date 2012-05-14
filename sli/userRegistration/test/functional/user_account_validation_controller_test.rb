require 'test_helper'
require 'mocha'

class UserAccountValidationControllerTest < ActionController::TestCase
 
  test "should present success/failure screen" do
    get :show
    assert_response :success
  end

  test "should show eula" do
    assert_response :success
  end
end