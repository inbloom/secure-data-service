
Given /^LDAP server has been setup and running$/ do
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
  @email_sender_name= "Administrator"
  @email_sender_address= "noreply@slidev.org"
  @email_conf = {
    :host =>  PropLoader.getProps['email_smtp_host'],
    :port => PropLoader.getProps['email_smtp_port'],
    :sender_name => @email_sender_name,
    :sender_email_addr => @email_sender_address
  }
end
