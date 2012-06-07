require 'test_helper'

class ChangePasswordsControllerTest < ActionController::TestCase
  setup do
    @change_password = change_passwords(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:change_passwords)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create change_password" do
    assert_difference('ChangePassword.count') do
      post :create, change_password: @change_password.attributes
    end

    assert_redirected_to change_password_path(assigns(:change_password))
  end

  test "should show change_password" do
    get :show, id: @change_password.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @change_password.to_param
    assert_response :success
  end

  test "should update change_password" do
    put :update, id: @change_password.to_param, change_password: @change_password.attributes
    assert_redirected_to change_password_path(assigns(:change_password))
  end

  test "should destroy change_password" do
    assert_difference('ChangePassword.count', -1) do
      delete :destroy, id: @change_password.to_param
    end

    assert_redirected_to change_passwords_path
  end
end
