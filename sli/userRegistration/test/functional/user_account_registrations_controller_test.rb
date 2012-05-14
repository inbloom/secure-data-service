require 'test_helper'

class UserAccountRegistrationsControllerTest < ActionController::TestCase
  setup do
    @user_account_registration = user_account_registrations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:user_account_registrations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create user_account_registration" do
    assert_difference('UserAccountRegistration.count') do
      post :create, user_account_registration: { email: @user_account_registration.email, firstName: @user_account_registration.firstName, lastName: @user_account_registration.lastName, password: @user_account_registration.password, vendor: @user_account_registration.vendor }
    end

    assert_redirected_to user_account_registration_path(assigns(:user_account_registration))
  end

  test "should show user_account_registration" do
    get :show, id: @user_account_registration
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @user_account_registration
    assert_response :success
  end

  test "should update user_account_registration" do
    put :update, id: @user_account_registration, user_account_registration: { email: @user_account_registration.email, firstName: @user_account_registration.firstName, lastName: @user_account_registration.lastName, password: @user_account_registration.password, vendor: @user_account_registration.vendor }
    assert_redirected_to user_account_registration_path(assigns(:user_account_registration))
  end

  test "should destroy user_account_registration" do
    assert_difference('UserAccountRegistration.count', -1) do
      delete :destroy, id: @user_account_registration
    end

    assert_redirected_to user_account_registrations_path
  end
end
