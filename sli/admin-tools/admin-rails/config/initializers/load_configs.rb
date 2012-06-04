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

ApprovalEngine.init(APP_LDAP_CLIENT, APP_EMAILER, APP_CONFIG["is_sandbox"])
