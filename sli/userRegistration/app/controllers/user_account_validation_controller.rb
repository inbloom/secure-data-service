
require 'rest-client'
require 'json'

class UserAccountValidationController < ApplicationController
  
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
  
  # GET /user_account_registrations/validate/1
  # GET /user_account_registrations/validate/1.json
  def show
    
    url = "http://localhost:8080/api/rest/v1/userAccounts/" + params[:id]
    urlHeader = {"accept" => "application/json"}
    res = RestClient.get(url, urlHeader){|response, request, result| response }
    
    if (res.code == 200)
      jsonDocument = JSON.parse(res.body)
      if (jsonDocument["validated"] == "true")
        @validation_result = ACCOUNT_PREVIOUSLY_VERIFIED
      else
        #
        @validation_result = ACCOUNT_VERIFICATION_COMPLETE
      end
    else # (res.code == 404)
      @validation_result = INVALID_VERIFICATION_CODE
    end 
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @validation_result }
    end
  end # end def show
end
