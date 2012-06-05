require 'approval'

class UserMailer < ActionMailer::Base
  default from: APP_CONFIG["email_sender_address"]

  EMAIL_SUBJECT_PROD = "Welcome to the SLC Developer Program"
  EMAIL_SUBJECT_SANDBOX = "Welcome to the SLC Developer Sandbox"

  def welcome_email(user)
    @firstName = user[:first]
    @landing_zone_link = "#{APP_CONFIG['email_replace_uri']}/landing_zone"
    @portal_link = APP_CONFIG["portal_url"]
    mail(:to => user[:emailAddress], :subject => (APP_CONFIG["is_sandbox"]?EMAIL_SUBJECT_SANDBOX : EMAIL_SUBJECT_PROD))
  end
end
