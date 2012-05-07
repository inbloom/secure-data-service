require 'test_helper'

class LandingZoneControllerTest < ActionController::TestCase
  # test "should get provision" do
  #   post :provision
  #   assert_response :redirect, @response.body
  # end

  test "should get index" do
    get :index
    assert_response :success, @response.body
  end

end
