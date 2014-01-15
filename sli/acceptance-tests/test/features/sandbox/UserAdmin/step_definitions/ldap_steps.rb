
Given /^LDAP server has been setup and running$/ do
  @ldap = LDAPStorage.new(Property['ldap_hostname'], Property['ldap_port'],
                          Property['ldap_base'], Property['ldap_admin_user'],
                          Property['ldap_admin_pass'], Property['ldap_use_ssl'])
  @email_sender_name= "Administrator"
  @email_sender_address= "noreply@slidev.org"
  @email_conf = {
    :host =>  Property['email_smtp_host'],
    :port => Property['email_smtp_port'],
    :sender_name => @email_sender_name,
    :sender_email_addr => @email_sender_address
  }
end
