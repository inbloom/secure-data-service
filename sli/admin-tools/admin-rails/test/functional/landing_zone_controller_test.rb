require 'test_helper'

class LandingZoneControllerTest < ActionController::TestCase
  test "should get provision" do
    get :provision
    assert_response :success
  end

  test "should get index" do
    get :index
    assert_response :success
  end

end
