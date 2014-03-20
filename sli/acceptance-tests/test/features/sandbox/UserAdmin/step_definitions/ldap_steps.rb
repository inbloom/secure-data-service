
Given /^LDAP server has been setup and running$/ do
  @ldap = ldap_storage
  @email_sender_name= "Administrator"
  @email_sender_address= "noreply@slidev.org"
  @email_conf = {
    :host =>  Property['email_smtp_host'],
    :port => Property['email_smtp_port'],
    :sender_name => @email_sender_name,
    :sender_email_addr => @email_sender_address
  }
end
