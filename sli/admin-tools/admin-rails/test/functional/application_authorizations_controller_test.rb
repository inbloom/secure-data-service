require 'test_helper'

class ApplicationAuthorizationsControllerTest < ActionController::TestCase
  setup do
    @AppAuths = ApplicationAuthorization.all
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:application_authorizations)
  end
  
  #test "should get new" do
  #  get :new
  #  assert_response :success
  #end
  #
  test "should create application_authorization" do
    post :create, application_authorization: @appauth_fixtures['new_district']  
    #assert_redirected_to application_authorization_path(assigns(:application_authorization))
    assert_redirected_to assigns(:application_authorization)
  end
  

  test "should show application_authorization" do
    get :show, id: "1"
    assert_response :success
  end
  #
  #test "should get edit" do
  #  get :edit, id: @application_authorization.to_param
  #  assert_response :success
  #end
  #
  test "should update application_authorization" do
    put :update, id: "1", application_authorization: @appauth_fixtures['district1']
    assert_redirected_to assigns(:application_authorization)
  end
  #
  #test "should destroy application_authorization" do
  #  assert_difference('ApplicationAuthorization.count', -1) do
  #    delete :destroy, id: @application_authorization.to_param
  #  end
  #
  #  assert_redirected_to application_authorizations_path
  #end
end
