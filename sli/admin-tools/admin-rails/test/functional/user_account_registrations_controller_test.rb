=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


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

    UserAccountRegistration.stubs(:register).returns({"redirect"=>true,"error"=>""})
    ReCaptcha::AppHelper.stubs(:validate_recap).returns(true)

    post :create, user_account_registration: { email: @user_account_registration.email, firstName: @user_account_registration.firstName, lastName: @user_account_registration.lastName, password: @user_account_registration.password, password_confirmation: @user_account_registration.password_confirmation, vendor: @user_account_registration.vendor }
    assert_response :success
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
