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


class UserAccountValidationControllerTest < ActionController::TestCase

  # success message
  ACCOUNT_VERIFICATION_COMPLETE = {
      "status" => "Registration Complete!",
      "message" => "An administrator will email you when your account is ready."
  }

  test "should present success/failure screen" do
    UserAccountValidation.stubs(:validate_account).returns(ACCOUNT_VERIFICATION_COMPLETE)
    get :show ,:id => "123456"
    assert_template :show
  end
end
