=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

class ForgotPasswordsControllerTest < ActionController::TestCase
  setup do
    @forgot_password = ForgotPassword.new(
        :token => 's9a8qwiwdd9ww23e223e22e2e2wdqw==',
        :new_pass => 'testabcd',
        :confirmation => 'testabcd'
    )
  end

  test "should get index" do
    get :index
    assert_response :success
  end

  test "should create forgot_password" do
    APP_LDAP_CLIENT.stubs(:read_user_resetkey).returns({})
    post :create, forgot_password: { token: @forgot_password.token, new_pass: @forgot_password.new_pass, confirmation: @forgot_password.confirmation }
    assert_response :success
  end

end
