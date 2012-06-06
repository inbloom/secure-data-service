require 'approval'

class UserMailer < ActionMailer::Base
  default from: APP_CONFIG["email_sender_address"]

EMAIL_SUBJECT_PROD = "SLC Developer Account - Email Confirmation"
EMAIL_SUBJECT_SANDBOX = "SLC Sandbox Developer Account - Email Confirmation"

  def welcome_email(user)
    @firstName = user[:first]
    @landing_zone_link = "__URI__/landing_zone"
    @portal_link = "__PORTAL__"
    Rails.logger.debug "User = #{user}\n\n\n"
    mail(:to => user[:emailAddress], :subject => (APP_CONFIG["is_sandbox"]?EMAIL_SUBJECT_SANDBOX : EMAIL_SUBJECT_PROD))
  end

  def self.get_email_token(email_address)
    user_info= ApprovalEngine.get_user(email_address)
    if (user_info.nil?)
      return nil
    else
      return user_info[:emailtoken]
    end
  end


  # Returns a map containing values for email_address, first_name, and last_name.
  #
  # Input Parameters:
  #   - guid : identifier of record containing an email address
  #
  # Returns : first, last, and user name on associated record
  #
  def get_email_info(guid)
    url = API_BASE + "/" + guid
    res = RestClient.get(url, REST_HEADER){|response, request, result| response }

    if (res.code==200)
      jsonDocument = JSON.parse(res.body)
      return {
          "email_address" => jsonDocument["userName"],
          "first_name" => jsonDocument["firstName"],
          "last_name" => jsonDocument["lastName"],
      }
    else
      return UNKNOWN_EMAIL
    end
  end
end
