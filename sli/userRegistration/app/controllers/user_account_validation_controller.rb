
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
  
  # error condition for attemptint to re-verify verified account
  UNEXPECTED_VERIFICATION_ERROR = {
    "status" => "Account validation failed!",
    "message" => "Unexpected verification error. Try again later."
  }
  
  REST_HEADER = {
    "Content-Type" => "application/json",
    "content-type" => "application/json",
    "accept" => "application/json"
  }
  
  # GET /user_account_registrations/validate/1
  # GET /user_account_registrations/validate/1.json
  def show
    
    url = "http://localhost:8080/api/rest/v1/userAccounts/" + params[:id]
    
    res = RestClient.get(url, REST_HEADER){|response, request, result| response }
    
    if (res.code == 200)
      parsedJsonHash = JSON.parse(res.body)
      if (parsedJsonHash["validated"] == "true")
        @validation_result = ACCOUNT_PREVIOUSLY_VERIFIED
      else
        parsedJsonHash["validated"] = "true"
        putRes = RestClient.put(url, parsedJsonHash.to_json, REST_HEADER){|response, request, result| response }
        if (putRes.code == 204)
          @validation_result = ACCOUNT_VERIFICATION_COMPLETE
        else
          @validation_result = UNEXPECTED_VERIFICATION_ERROR
        end
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
