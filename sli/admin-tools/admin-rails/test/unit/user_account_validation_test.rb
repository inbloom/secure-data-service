require 'test_helper'

class UserAccountValidationTest < ActiveSupport::TestCase
  def setup
    
  end
    
  def test_validate_account_ok
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"sweet@itworks.com"})
    RestClient.stubs(:get).returns(MockResponse.new(200,false))
    RestClient.stubs(:put).returns(MockResponse.new(204))
    ApplicationHelper.stubs(:verify_email)
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
  
  def test_validate_account_duplicate
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"sweet@itworks.com"})
    RestClient.stubs(:get).returns(MockResponse.new(200,true))
    ApplicationHelper.stubs(:verify_email)
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
  
  def test_validate_account_unexpected_verification_error1
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"sweet@itworks.com"})
    RestClient.stubs(:get).returns(MockResponse.new(200,false))
    RestClient.stubs(:put).returns(MockResponse.new(500))
    ApplicationHelper.stubs(:verify_email)
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
  
  def test_validate_account_unexpected_verification_error2
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"sweet@itworks.com"})
    RestClient.stubs(:get).returns(MockResponse.new(500))
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
  
  def test_validate_account_invalid_verification_error1
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"sweet@itworks.com"})
    RestClient.stubs(:get).returns(MockResponse.new(404))
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
  
  def test_validate_account_invalid_verification_error2
    
    # mock external dependencies
    ApprovalEngine.stubs(:get_user_emailtoken).returns(nil)
    
    #test
    UserAccountValidation.validate_account "1234567890"
    
  end
end


