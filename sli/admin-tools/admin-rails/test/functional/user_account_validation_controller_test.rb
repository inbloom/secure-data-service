require 'test_helper'
require 'mocha'

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