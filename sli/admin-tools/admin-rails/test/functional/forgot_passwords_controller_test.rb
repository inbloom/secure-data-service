require 'test_helper'

class ForgotPasswordsControllerTest < ActionController::TestCase
  setup do
    @forgot_password = forgot_passwords(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:forgot_passwords)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create forgot_password" do
    assert_difference('ForgotPassword.count') do
      post :create, forgot_password: @forgot_password.attributes
    end

    assert_redirected_to forgot_password_path(assigns(:forgot_password))
  end

  test "should show forgot_password" do
    get :show, id: @forgot_password.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @forgot_password.to_param
    assert_response :success
  end

  test "should update forgot_password" do
    put :update, id: @forgot_password.to_param, forgot_password: @forgot_password.attributes
    assert_redirected_to forgot_password_path(assigns(:forgot_password))
  end

  test "should destroy forgot_password" do
    assert_difference('ForgotPassword.count', -1) do
      delete :destroy, id: @forgot_password.to_param
    end

    assert_redirected_to forgot_passwords_path
  end
end
