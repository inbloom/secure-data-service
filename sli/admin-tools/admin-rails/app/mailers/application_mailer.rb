class ApplicationMailer < ActionMailer::Base
  default from: "#{APP_CONFIG['email_sender_name']} <#{APP_CONFIG['email_sender_address']}>"

  EMAIL_SUBJECT_PROD = "Welcome to the SLC Developer Program"
  EMAIL_SUBJECT_SANDBOX = "Welcome to the SLC Developer Sandbox"

  def welcome_email(user)
    @firstName = user[:first]
    @landing_zone_link = "#{APP_CONFIG['email_replace_uri']}/landing_zone"
    @portal_link = APP_CONFIG["portal_url"]
    @apps_link = "#{APP_CONFIG['email_replace_uri']}/apps"
    mail(:to => user[:emailAddress], :subject => (APP_CONFIG["is_sandbox"]?EMAIL_SUBJECT_SANDBOX : EMAIL_SUBJECT_PROD))
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
