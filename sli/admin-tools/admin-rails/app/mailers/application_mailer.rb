class ApplicationMailer < ActionMailer::Base
  default from: "noreply@slidev.org"
  
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
