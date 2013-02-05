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


require 'approval'

module ApplicationHelper


  REST_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
  }

  API_BASE=APP_CONFIG["api_base"]+"/v1/userAccounts"
  UNKNOWN_EMAIL = {
      "email_address" => "UNKNOWN",
      "first_name" => "UNKNOWN",
      "last_name" => "UNKNOWN",
  }

  # marker exception indicating something went wrong when sending email
  class EmailException < Exception
  end

  # Looks up the provided GUID (record) through the API, removes (deletes) that record,
  # and removes the associated user from the LDAP.
  #
  # Input Prameters:
  #   - guid : ID of record containing email address to send to
  #
  # Returns:
  #     Nothing
  #
  def self.remove_user_account(email)
    ApprovalEngine.remove_user(email) unless email == nil
  end

  # Gets the email address for the supplied GUID and sends them a confirmation-request
  # email.
  #
  # Input Prameters:
  #   - guid : ID of record containing email address to send to
  #
  # Returns:
  #     Nothing
  #
  def self.send_user_verification_email(validate_base, email_address)
    ApprovalEngine.change_user_status(email_address, "accept_eula")
    user = ApprovalEngine.get_user(email_address)
    first_name = user[:first]
    email_token = user[:emailtoken]

    if (email_token.nil?)
      return false
    end

    userEmailValidationLink = "#{APP_CONFIG['email_replace_uri']}/user_account_validation/#{email_token}"
    begin
      ApplicationMailer.verify_email(email_address,first_name,userEmailValidationLink).deliver
    rescue Exception => e
      raise EmailException, e
    end
    true
  end

  # Checks if the user account exists.
  # Input Parameters:
  #   - email - user id (email)
  #
  # Returns : true or false
  def self.user_exists?(email)
    ApprovalEngine.user_exists?(email)
  end

  # Add all relevant information for a new user to the backend.
  #
  # Input Parameters:
  #
  # user_info is a hash with the following fields:
  #   - first : first name
  #   - last  : last name
  #   - email : email address (also serves as the userid)
  #   - password : password used to log in
  #   - vendor : optional vendor name
  #
  # Returns: A hash string that has to be used for email verification
  # by embedding a link into a confirmation email.
  #
  # Input Example:
  #
  # user_info = {
  #     :first => "John",
  #     :last => "Doe",
  #     :email => "jdoe@example.com",
  #     :password => "secret",
  #     :vendor => "Acme Inc."
  # }
  #
  def self.add_user(userAccountRegistration)
    new_user = {
        :first           => userAccountRegistration.firstName,
        :last            => userAccountRegistration.lastName,
        :email           => userAccountRegistration.email,
        :emailAddress    => userAccountRegistration.email,
        :password        => userAccountRegistration.password,
        :vendor          => userAccountRegistration.vendor,
        :status          => "submitted"
    }
    ApprovalEngine.add_disabled_user(new_user)
  end

  # Verify the email address against the backend.
  #
  # Input Parameter:
  #
  # email_hash : The email hash that was previously returned by the add_user
  # and included in a click through link that the user received in an email (as a query parameter).
  #
  def self.verify_email(emailtoken)
    # APP_LDAP_CLIENT.verify_email(emailtoken)
    ApprovalEngine.verify_email(emailtoken)
  end

  # Update the user information that was submitted via the add_user method.
  #
  # Input parameter: A subset of the "user_info" submitted to the "add_user" method.
  #
  def self.update_user_info(userAccountRegistration)
    new_user = {
        :first          => userAccountRegistration.firstName,
        :last           => userAccountRegistration.lastName,
        :email          => userAccountRegistration.email,
        :emailAddress   => userAccountRegistration.email,
        :password       => userAccountRegistration.password,
        :vendor         => userAccountRegistration.vendor,
        :status         => "submitted"
    }
    ApprovalEngine.update_user_info(new_user)
  end

  #get email token for a specific user
  def self.get_email_token(email_address)
    user_info= ApprovalEngine.get_user(email_address)
    if (user_info.nil?)
      return nil
    else
      return user_info[:emailtoken]
    end
  end

  def self.get_edorg_from_ldap(email_address)
    user_info = ApprovalEngine.get_user(email_address)
    if user_info.nil?
      return nil
    else
      return user_info[:edorg]
    end
  end

  # Returns an individual user via their email token or nil if the user does not exist.
  def self.get_user_with_emailtoken(email_token)
    return ApprovalEngine.get_user_emailtoken(email_token)
  end

  def required?(obj, attr)
    target = (obj.class == Class) ? obj : obj.class
    target.validators_on(attr).map(&:class).include?(
        ActiveModel::Validations::PresenceValidator)
  end

end
class ErbBinding < OpenStruct
  def get_binding
    return binding()
  end
end
