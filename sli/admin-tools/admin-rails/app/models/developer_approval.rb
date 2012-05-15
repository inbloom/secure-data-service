class DeveloperApproval < Ldap
  @@emailer=Emailer.new({:host=>EMAIL_HOST,:port=>EMAIL_PORT})
  @@j = ActiveSupport::JSON

  def initialize
    ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
  end

end
