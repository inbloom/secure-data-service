require 'approval'

class Ldap
  LDAP_HOST=APP_CONFIG["ldap_host"]
  LDAP_PORT=APP_CONFIG["ldap_port"]
  LDAP_BASE=APP_CONFIG["ldap_base"]
  LDAP_USER=APP_CONFIG["ldap_user"]
  LDAP_PASS=APP_CONFIG["ldap_pass"]

  @@ldap=LDAPStorage.new(LDAP_HOST,LDAP_PORT,LDAP_BASE,LDAP_USER,LDAP_PASS)

  IS_SANDBOX           = APP_CONFIG["is_sandbox"]
  EMAIL_SENDER_NAME    = APP_CONFIG["email_sender_name"]
  EMAIL_SENDER_ADDRESS = APP_CONFIG["email_sender_address"]
  EMAIL_HOST           = APP_CONFIG["email_host"]
  EMAIL_PORT           = APP_CONFIG["email_port"]
  REPLACER             = {"__URI__" => APP_CONFIG["email_replace_uri"]}
  @@emailer=Emailer.new({
    :sender_name => EMAIL_SENDER_NAME,
    :sender_email_addr => EMAIL_SENDER_ADDRESS,
    :host=>EMAIL_HOST,
    :port=>EMAIL_PORT,
    :replacer=>REPLACER
  })
end
