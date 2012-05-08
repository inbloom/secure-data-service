require 'test_helper'

class IdpControllerTest < ActionController::TestCase
  test "should get form" do
    get :index
    assert_response :success
  end

  test "should get login" do
    post :login
    assert_response :success
  end
  
  test "should do logout" do
    post :logout
    assert_response :success
  end

end
