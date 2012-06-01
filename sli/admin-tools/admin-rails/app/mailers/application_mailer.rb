class ApplicationMailer < ActionMailer::Base
  default from: "from@example.com"
  
  def notify_operator(developer, app)
  end
  
  def notify_developer(developer, app)
  end
end
