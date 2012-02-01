require 'test_helper'

class SessionsControllerTest < ActionController::TestCase
  test "destroy session" do
    get :destroy
    assert_response :success
  end

end
