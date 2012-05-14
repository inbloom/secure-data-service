require 'test_helper'

class UserAccountRegistrationsControllerTest < ActionController::TestCase

  setup do
    #user_account_registrations=load_fixture("user_account_registrations")
    #@user_account_registration = load_fixture("user_account_registrations")[0]
    UserAccountRegistrationsHelper.expects(:register_user).with("km").returns({"email"=>"sweet@itworks.com"})
    puts "KM Result: " + ApprovalEngine.get_user_emailtoken("km").to_s
 
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

 end
