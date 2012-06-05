require 'approval'

APP_CONFIG = YAML::load_file("#{Rails.root}/config/config.yml")[Rails.env]
APP_LDAP_CLIENT = LDAPStorage.new(APP_CONFIG["ldap_host"], APP_CONFIG["ldap_port"],
      APP_CONFIG["ldap_base"],APP_CONFIG["ldap_user"],APP_CONFIG["ldap_pass"])
APP_EMAILER = Emailer.new({
      :sender_name => APP_CONFIG["email_sender_name"],
      :sender_email_addr => APP_CONFIG["email_sender_address"],
      :host=>APP_CONFIG["email_host"],
      :port=>APP_CONFIG["email_port"],
      :replacer=>{"__URI__" => APP_CONFIG["email_replace_uri"],
      "__PORTAL__" => APP_CONFIG["portal_url"]}
    })

class MyTransitionActionConfig
  def transition(user)
    Rails.logger.debug("Inside transition!!!!\n\n\n")
    UserMailer.welcome_email(user).deliver
    if user[:status] == ApprovalEngine::STATE_APPROVED
    end

    #if user[:status] == STATE_APPROVED
    #  # TODO: Below should not be hardcoded and should be configurable by admin.
    #  # TODO: check that user[:emailAddress] is valid-ish email (regex?)
    #  email = {
    #      :email_addr => user[:emailAddress],
    #      :name       => "#{user[:first]} #{user[:last]}"
    #  }
    #  if APP_CONFIG["is_sandbox"]
    #    email[:subject] = "Welcome to the SLC Developer Sandbox"
    #    template=File.open("#{Rails.root}/public/welcome_email_sandbox_text.template") {|file| file.read}
    #  else
    #    template=File.read("#{Rails.root}/public/welcome_email_prod_text.template") {|file| file.read}
    #    email[:subject] = "Welcome to the SLC Developer Program"
    #  end
    #  email_content = ERB.new(template)
    #  template_data = {:firstName => user[:first],
    #                   :landingZoneLink => "__URI__/landing_zone",
    #                   :portalLink => "__PORTAL__" }
    #  email[:content] = email_content.result(ErbBinding.new(template_data).get_binding)
    #  APP_EMAILER.send_approval_email email
    #end
  end
end
ApprovalEngine.init(APP_LDAP_CLIENT, APP_EMAILER, MyTransitionActionConfig.new, APP_CONFIG["is_sandbox"])
