require 'test_helper'

class LandingZoneControllerTest < ActionController::TestCase
  # test "should get provision" do
  #   post :provision
  #   assert_response :redirect, @response.body
  # end

  test "should get index" do
    get :index, {}, { :roles => ["LEA Administrator"] }
    assert_response :success, @response.body
  end
  
  test "should fail due to lack of permissions" do
    get :index, {}, { :roles => ["Developer"] }
    assert_response 403
  end
end
