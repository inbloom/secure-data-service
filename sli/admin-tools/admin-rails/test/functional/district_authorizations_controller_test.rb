require 'test_helper'

class DistrictAuthorizationsControllerTest < ActionController::TestCase
  setup do
    @district_authorization = district_authorizations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:district_authorizations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create district_authorization" do
    assert_difference('DistrictAuthorization.count') do
      post :create, district_authorization: @district_authorization.attributes
    end

    assert_redirected_to district_authorization_path(assigns(:district_authorization))
  end

  test "should show district_authorization" do
    get :show, id: @district_authorization.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @district_authorization.to_param
    assert_response :success
  end

  test "should update district_authorization" do
    put :update, id: @district_authorization.to_param, district_authorization: @district_authorization.attributes
    assert_redirected_to district_authorization_path(assigns(:district_authorization))
  end

  test "should destroy district_authorization" do
    assert_difference('DistrictAuthorization.count', -1) do
      delete :destroy, id: @district_authorization.to_param
    end

    assert_redirected_to district_authorizations_path
  end
end
