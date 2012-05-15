class DeveloperApproval < Ldap
  IS_SANDBOX           = APP_CONFIG["is_sandbox"]
  EMAIL_HOST           = APP_CONFIG["email_host"]
  EMAIL_PORT           = APP_CONFIG["email_port"]
  
  
  @@emailer=Emailer.new({:host=>EMAIL_HOST,:port=>EMAIL_PORT})
  @@j = ActiveSupport::JSON

  def initialize
    ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
  end

end
