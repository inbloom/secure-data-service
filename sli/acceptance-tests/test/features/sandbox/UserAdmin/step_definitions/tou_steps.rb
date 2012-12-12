require 'ldapstorage'
require_relative '../../../admintools/step_definitions/reset_change_password.rb'

$user_email = "jraynor@#{Socket.gethostname}"
$ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                        PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                        PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])

Given /^I have an account of (.*) in (.*) status$/ do |role, tou_status|
  status = (tou_status == "TOU not set") ? "submitted" : "approved"
  $ldap.delete_user($user_email)
  $ldap.create_user({:first => 'Jim', :last => 'Raynor', :email => $user_email, :password => 'm@rs@r@',
                     :emailtoken => $user_email, :status => status, :homedir => '/dev/null',
                     :emailAddress => $user_email})
  $ldap.add_user_group($user_email, role)
end

When /^I access the password reset page$/ do
  step "I hit the Admin URL"
  step "I was redirected to the \"Simple\" IDP Login page"
  step "I click the \"forgotPassword\" link"
  step "I am redirected to the Forgot Password page"
  step "I see the input box to enter user id"
  step "I fill out the input field \"user_id\" as \"#{$user_email}\""
  step "I click on \"submit\""
  step "I am redirected to the Reset Password page"
  step "I check for message  \"Password reset instructions have been emailed to you. Please follow the instructions in the email.\""
  user = $ldap.read_user($user_email)
  resetKey = user[:resetKey].split("@")[0]
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/resetPassword/newAccount/" + resetKey)
  assertWithWait("Failed to navigate to the new account registration page")  {@driver.page_source.index("New Account Registration") != nil}

end

Then /^I am shown a checkbox with term\-of\-use$/ do
  assertWithWait("failed to find terms of use checkbox") {@driver.find_element(:id, "terms_and_conditions")}
end

Then /^I am not shown a checkbox with term\-of\-use$/ do
  assertWithWait("found terms of use checkbox") {@driver.page_source.index("terms_and_conditions") == nil}
end

Then /^I have to enter my password$/ do
  @explicitWait.until{@driver.find_element(:id, "submitForgotPasswordButton")}.click
  assertWithWait("No error for password not being set") {@driver.find_element(:id, "password_error_explanation")}
end

Then /^I have to check the terms\-of\-use to submit the form$/ do
  @explicitWait.until{@driver.find_element(:id, "submitForgotPasswordButton")}.click
  assertWithWait("No error for Terms of Use not checked") {@driver.find_element(:xpath, "//li[starts-with(text(), 'Terms and conditions')]")}
  @tou_required = true
end

When /^I submit the form$/ do
  @explicitWait.until{@driver.find_element(:id, "new_account_password_new_pass")}.send_keys("s@r@h")
  @explicitWait.until{@driver.find_element(:id, "new_account_password_confirmation")}.send_keys("s@r@h")
  if @tou_required
    @explicitWait.until{@driver.find_element(:id, "terms_and_conditions")}.click
  end
  @explicitWait.until{@driver.find_element(:id, "submitForgotPasswordButton")}.click
end

Then /^the password is saved$/ do
  assert $ldap.authenticate($user_email, "s@r@h")
end

Then /^the terms\-of\-use is saved$/ do
  user = $ldap.read_user($user_email)
  status = user[:status]
  assert"approved" == status
end

Then /^I do not have to check the terms\-of\-use to submit the form$/ do
  @tou_required = false
end

Then /^the terms\-of\-use is not saved$/ do
  #do nothing
end

at_exit do
  $ldap.delete_user($user_email)
end
