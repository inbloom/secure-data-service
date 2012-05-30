require 'test_helper'
require "mocha"

class UserAccountRegistrationsControllerTest < ActionController::TestCase

  setup do
    @user_account_registration=UserAccountRegistration.new(
        :email=> 'validated@valid.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :password_confirmation => 'secret',
        :vendor => 'self'
    )
 
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create user_account_registration" do

    UserAccountRegistrationsHelper.stubs(:register_user).returns({"redirect"=>true,"error"=>""})

    post :create, user_account_registration: { email: @user_account_registration.email, firstName: @user_account_registration.firstName, lastName: @user_account_registration.lastName, password: @user_account_registration.password, vendor: @user_account_registration.vendor }
    assert_template :controller => "eula", :action => "show"
  end
  test "should validate user_account_registration" do

    post :create, user_account_registration: { email: "invalid.com", firstName: @user_account_registration.firstName, lastName: @user_account_registration.lastName, password: @user_account_registration.password, vendor: @user_account_registration.vendor }

    assert_template :new
  end
  test "should validate cancel registration" do
    post :create,:commit=>"Cancel"
    assert_redirected_to APP_CONFIG['redirect_slc_url']
  end

 end
