require 'approval'



class AccountManagement < Ldap
  @@emailer=Emailer.new({:host=>EMAIL_HOST,:port=>EMAIL_PORT})

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