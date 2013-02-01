=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


class ApplicationMailer < ActionMailer::Base
  default from: "#{APP_CONFIG['email_sender_name']} <#{APP_CONFIG['email_sender_address']}>"

  WELCOME_EMAIL_SUBJECT_PROD = "Welcome to inBloom"
  WELCOME_EMAIL_SUBJECT_SANDBOX = "Welcome to the inBloom Developer Sandbox"
  VERIFY_EMAIL_SUBJECT_SANDBOX ="inBloom Developer Sandbox Account - Email Confirmation"
  VERIFY_EMAIL_SUBJECT_PROD = "inBloom Developer Account - Email Confirmation"
  PROVISION_EMAIL_SUBJECT_SANDBOX = "inBloom Sandbox Developer - Data Setup"
  PROVISION_EMAIL_SUBJECT_PROD = "inBloom Landing Zone Setup"
  PASSWORD_CHANGE_SUBJECT = "inBloom Notification - Password Changed"
  FORGOT_PASSWORD_SUBJECT = "inBloom Notification - Reset Password"
  SAMT_VERIFY_SUBJECT_SANDBOX = "inBloom Sandbox Account - Email Confirmation"
  SAMT_VERIFY_SUBJECT_PROD = "inBloom Administrator Account - Email Confirmation"
  SAMT_WELCOME_SANDBOX = "Welcome to the inBloom Developer Sandbox"
  SAMT_WELCOME_PROD = "inBloom - Administrator Account"

  def welcome_email(user)
    @firstName = user[:first]
    #@landing_zone_link = "#{APP_CONFIG['email_replace_uri']}/landing_zone"
    @portal_link = APP_CONFIG["portal_url"]
    @documentation_link = APP_CONFIG['app_dev_documentation_link']
    #@apps_link = "#{APP_CONFIG['email_replace_uri']}/apps"
    mail(:to => user[:emailAddress], :subject => (APP_CONFIG["is_sandbox"]?WELCOME_EMAIL_SUBJECT_SANDBOX : WELCOME_EMAIL_SUBJECT_PROD))
  end

  def notify_password_change(email_address, fullName)
    @fullName = fullName
    mail(:to => email_address, :subject => PASSWORD_CHANGE_SUBJECT )
  end

  def notify_reset_password(email, key)
    user = APP_LDAP_CLIENT.read_user(email)
    @fullName = user[:first] + " " + user[:last]
    Rails.logger.info("user status is: #{user[:status]}")
    if user[:status]=="submitted"
      @resetPasswordUrl=APP_CONFIG['email_replace_uri']+"/resetPassword/newAccount/"+key
    else
      @resetPasswordUrl = APP_CONFIG['email_replace_uri'] + "/resetPassword?key=" + key
    end
    mail(:to => user[:emailAddress], :subject => FORGOT_PASSWORD_SUBJECT )
  end

  def verify_email(email_address, firstName, userEmailValidationLink)
    @firstName = firstName
    @userEmailValidationLink=userEmailValidationLink
    @supportEmail=APP_CONFIG["support_email"]
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"]?VERIFY_EMAIL_SUBJECT_SANDBOX : VERIFY_EMAIL_SUBJECT_PROD))
  end

  def provision_email(email_address, firstName, serverName, edorgId)
    @firstName = firstName
    @serverName = serverName
    @edorgId = edorgId
    @sample_data_link = APP_CONFIG['sample_data_url']
    @redirect_email = APP_CONFIG['redirect_slc_url']
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"]?PROVISION_EMAIL_SUBJECT_SANDBOX : PROVISION_EMAIL_SUBJECT_PROD))
  end

  def auto_provision_email(email_address, firstName, login_user)
    @firstName = firstName
    @portal_link = APP_CONFIG["portal_url"]
    @login_user = login_user
    mail(:to => email_address,:subject => PROVISION_EMAIL_SUBJECT_SANDBOX)
  end

  def notify_operator(support_email, app, dev_name)
    @portal_link = "#{APP_CONFIG['portal_url']}/web/guest/admin"
    @dev_name = dev_name
    @app = app
    if !@app.nil? and support_email =~ /(\w|-)+@\w+\.\w+/
      mail(:to => support_email, :subject => 'inBloom - New Application Notification')
    end
  end

  def notify_operator_on_acct_creation(user_created)
    @user = user_created
    support_email = APP_CONFIG["support_email"]
    Rails.logger.debug {"Mailing to: #{support_email} on sandbox creation"}
    mail(:to => support_email, :subject => 'SLC Sandbox - New sandbox creation')
  end

  def notify_developer(app, first_name)
    Rails.logger.debug {"Mailing to: #{app.metaData.createdBy}"}
    @portal_link = "#{APP_CONFIG['portal_url']}/web/guest/admin"
    @firstName = first_name
    @app = app
    if !@app.nil? and @app.metaData.createdBy =~ /(\w|-)+@\w+\.\w+/
      mail(:to => app.metaData.createdBy, :subject => 'inBloom - Your Application Is Approved')
    end
  end

  def samt_verify_email(email_address, firstName, primary_role,reset_password_link)
    Rails.logger.info {"samt verification email is sent to: #{email_address}"}
    @firstName = firstName
    @reset_password_link = reset_password_link
    @primary_role = primary_role
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"]?SAMT_VERIFY_SUBJECT_SANDBOX : SAMT_VERIFY_SUBJECT_PROD))
  end

  def samt_welcome(email_address, firstName, groups)
    Rails.logger.debug("groups = #{groups}")
    @firstName = firstName
    @is_admin = !(["SLC Operator", "SEA Administrator", "LEA Administrator", "Sandbox SLC Operator", "Sandbox Administrator"] & groups).empty?
    @is_ingestion = groups.include?("ingestion_user")
    @is_app_dev = groups.include?("application_developer")
    @is_realm_admin = groups.include?("Realm Administrator")
    @is_slc_operator = groups.include?("SLC Operator")
    @is_sea_admin = groups.include?("SEA Administrator")
    @is_lea_admin = groups.include?("LEA Administrator")
    @portal_link = APP_CONFIG["portal_url"]
    @app_dev_documentation_link = APP_CONFIG['app_dev_documentation_link']
    @support_email = APP_CONFIG["support_email"]
    @admin_documentation_link = APP_CONFIG["admin_documentation_link"]
    if(@is_slc_operator)
      @account_type = "SLC Operator"
    elsif(@is_sea_admin)
      @account_type = "SEA Administrator"
    elsif(@is_lea_admin)
      @account_type = "LEA Administrator"
    elsif(@is_realm_admin)
      @account_type = "Realm Administrator"
    elsif(@is_ingestion)
      @account_type = "Ingestion User"
    end
    mail(:to => email_address, :subject => (APP_CONFIG["is_sandbox"] ? SAMT_WELCOME_SANDBOX : SAMT_WELCOME_PROD))
  end
end
