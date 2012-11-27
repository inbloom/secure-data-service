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

  # error condition for attempt to re-verify verified account
  ACCOUNT_PREVIOUSLY_VERIFIED = {
      "status" => "Account validation failed!",
      "message" => "This account was previously verified. For questions please contact #{APP_CONFIG['support_email']}"
  }

  # error condition for unhandled exceptions
  UNEXPECTED_VERIFICATION_ERROR = {
      "status" => "Account validation failed!",
      "message" => "Unexpected verification error. Try again later."
  }

  def self.validate_account(emailToken)
    begin
      ApplicationHelper.verify_email(emailToken)
      return ACCOUNT_VERIFICATION_COMPLETE
    rescue => e
      Rails.logger.error "VERIFICATION ERROR:   #{e}"
      # TODO: DE2202 - have approval engine raise exceptions or check approval state instead of matching msg strings
      if e.to_s =~ /^Current status '\w+' does not allow transition 'verify_email'\.$/
        return ACCOUNT_PREVIOUSLY_VERIFIED
      elsif e.to_s == "Could not find user for email id #{emailToken}."
        return INVALID_VERIFICATION_CODE
      else
        return UNEXPECTED_VERIFICATION_ERROR
      end
    end
  end
end
