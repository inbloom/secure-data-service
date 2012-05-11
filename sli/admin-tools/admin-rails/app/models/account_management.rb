require 'approval'



class AccountManagement < Ldap

  IS_SANDBOX=APP_CONFIG["is_sandbox"]
  EMAIL_HOST=APP_CONFIG["email_host"]
  EMAIL_PORT=APP_CONFIG["email_port"]
  REPLACER={"__URI__" => APP_CONFIG["email_replace_uri"]}
  @@emailer=Emailer.new({:host=>EMAIL_HOST,:port=>EMAIL_PORT, :replacer=>REPLACER})

  attr_accessor :name,:vendor,:lastUpdate,:status,:email,:transitions

  def self.get_users(status=nil)
    ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
    ApprovalEngine.get_users(status)
  end

  def self.change_user_status(email_address,transition)
    ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
    ApprovalEngine.change_user_status(email_address,transition)
  end

end