require 'approval'

class Ldap
  LDAP_HOST=APP_CONFIG["ldap_host"]
  LDAP_PORT=APP_CONFIG["ldap_port"]
  LDAP_BASE=APP_CONFIG["ldap_base"]
  LDAP_USER=APP_CONFIG["ldap_user"]
  LDAP_PASS=APP_CONFIG["ldap_pass"]
  IS_SANDBOX=APP_CONFIG["is_sandbox"]
  EMAIL_HOST=APP_CONFIG["email_host"]
  EMAIL_PORT=APP_CONFIG["email_port"]

  @@ldap=LDAPStorage.new(LDAP_HOST,LDAP_PORT,LDAP_BASE,LDAP_USER,LDAP_PASS)
end
