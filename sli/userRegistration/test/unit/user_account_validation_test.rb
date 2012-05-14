require 'test_helper'
require 'proxy'

class UserAccountValidationTest < ActiveSupport::TestCase
  def setup
    
  end
    
  def test_validate_account_ok
    
    # mock external dependencies
    ApprovalEngineProxy.stubs(:verifyEmail).returns({"status"=>"success"})
    
    #test
    response=UserAccountValidation.validate_account "1234567890"
    assert_equal(response["status"],"Registration Complete!")
    assert_equal(response["message"],"An administrator will email you when your account is ready.")
    
  end
  
  def test_validate_account_duplicate
    
    # mock external dependencies
    ApprovalEngineProxy.stubs(:verifyEmail).returns({"status"=>"previouslyVerified"})
    
    #test
    response=UserAccountValidation.validate_account "1234567890"
    assert_equal(response["status"],"Account validation failed!")
    assert_equal(response["message"],"Account previously verified.")
    
  end
  
  def test_validate_account_invalid_verification_error1
    
    # mock external dependencies
     ApprovalEngineProxy.stubs(:verifyEmail).returns({"status"=>"unknownUser"})
    
    #test
    response=UserAccountValidation.validate_account "1234567890"
    assert_equal(response["status"],"Account validation failed!")
    assert_equal(response["message"],"Invalid account verification code.")
    
  end
  
end


