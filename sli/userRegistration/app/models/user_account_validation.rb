
require 'rest-client'
require 'json'

class UserAccountValidation
    
  # success message
  ACCOUNT_VERIFICATION_COMPLETE = {
    "status" => "Registration Complete!",
    "message" => "An administrator will email you when your account is ready."
  }
  
  INVALID_VERIFICATION_CODE = {
    "status" => "Account validation failed!",
    "message" => "Invalid account verification code."
  }
  
  # error condition for attemptint to re-verify verified account
  ACCOUNT_PREVIOUSLY_VERIFIED = {
    "status" => "Account validation failed!",
    "message" => "Account previously verified."
  }
  
  # error condition for attemptint to re-verify verified account
  UNEXPECTED_VERIFICATION_ERROR = {
    "status" => "Account validation failed!",
    "message" => "Unexpected verification error. Try again later."
  }
  URL=APP_CONFIG['approval_uri']
    
  def self.validate_account(guid)
    emailToken=guid
    if APP_CONFIG["is_sandbox"] == true
      @currEnvironment=true
     else
      @currEnvironment=false
     end
    ApprovalEngineProxy.init(URL,@currEnvironment)
    status=ApprovalEngineProxy.verifyEmail(emailToken)
    if(status["status"] == "success")
      return ACCOUNT_VERIFICATION_COMPLETE
    elsif (status["status"] == "previouslyVerified")
      return ACCOUNT_PREVIOUSLY_VERIFIED
    else
      return INVALID_VERIFICATION_CODE
    end 
  end
end
