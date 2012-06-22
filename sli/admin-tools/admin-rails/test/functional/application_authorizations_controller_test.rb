require 'test_helper'

class ApplicationAuthorizationsControllerTest < ActionController::TestCase
  setup do
    @AppAuths = ApplicationAuthorization.all
  end

  test "should get index" do
    session[:roles] = ["LEA Administrator"]
    get :index
    assert_response :success
    assert_not_nil assigns(:application_authorizations)
  end
  
  test "should create application_authorization" do
    session[:roles] = ["LEA Administrator"]
    post :create, application_authorization: @appauth_fixtures['new_district']  
    #assert_redirected_to application_authorization_path(assigns(:application_authorization))
    assert_redirected_to assigns(:application_authorization)
  end
  
  test "should fail if we are an operator" do
    session[:roles] = ["SLC Operator"]
    post :index
    assert(!session.has_key?("roles"), "Session should be reset after we are given a forbidden")
  end

  test "should update application_authorization" do
    session[:roles] = ["LEA Administrator"]
    put :update, id: "1", application_authorization: @appauth_fixtures['district1']
    assert_redirected_to application_authorizations_path
  end
end
