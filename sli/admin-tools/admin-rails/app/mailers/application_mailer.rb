class ApplicationMailer < ActionMailer::Base
  default from: "#{APP_CONFIG['email_sender_name']} <#{APP_CONFIG['email_sender_address']}>"

  WELCOME_EMAIL_SUBJECT_PROD = "Welcome to the SLC Developer Program"
  WELCOME_EMAIL_SUBJECT_SANDBOX = "Welcome to the SLC Developer Sandbox"
  VERIFY_EMAIL_SUBJECT_SANDBOX ="SLC Sandbox Developer Account - Email Confirmation"
  VERIFY_EMAIL_SUBJECT_PROD = "SLC Developer Account - Email Confirmation"
  PROVISION_EMAIL_SUBJECT_SANDBOX = "SLC Sandbox Developer - Data Setup"
  PROVISION_EMAIL_SUBJECT_PROD = "SLC Landing Zone Setup"

  def welcome_email(user)
    @firstName = user[:first]
    @landing_zone_link = "#{APP_CONFIG['email_replace_uri']}/landing_zone"
    @portal_link = APP_CONFIG["portal_url"]
    @apps_link = "#{APP_CONFIG['email_replace_uri']}/apps"
    mail(:to => user[:emailAddress], :subject => (APP_CONFIG["is_sandbox"]?WELCOME_EMAIL_SUBJECT_SANDBOX : WELCOME_EMAIL_SUBJECT_PROD))
  end
  
  def verify_email(email_address, firstName, userEmailValidationLink)
    @firstName = firstName
    @userEmailValidationLink=userEmailValidationLink
    @supportEmail=APP_CONFIG["support_email"]
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"]?VERIFY_EMAIL_SUBJECT_SANDBOX : VERIFY_EMAIL_SUBJECT_PROD))
  end
  
  def provision_email(email_address, firstName, serverName, edorgId)
    @firstName = firstName
    @serverName = serverName
    @edorgId = edorgId
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"]?PROVISION_EMAIL_SUBJECT_SANDBOX : PROVISION_EMAIL_SUBJECT_PROD))
  end

  def notify_operator(support_email, app)
    @app = app
    if !@app.nil? and support_email =~ /\w+@\w+\.\w+/
      mail(:to => support_email, :subject => "A new application has been registered")
    end
  end
  
  def notify_developer(app)
    logger.debug {"Mailing to: #{app.metaData.createdBy}"}
    @app = app
    if !@app.nil? and @app.metaData.createdBy =~ /\w+@\w+\.\w+/
      mail(:to => app.metaData.createdBy, :subject => "The status of #{app.name} has been updated.")
    end
  end
end
