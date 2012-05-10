require 'approval'
module ApplicationHelper

  LDAP_HOST=APP_CONFIG["ldap_host"]
  LDAP_PORT=APP_CONFIG["ldap_port"]
  LDAP_BASE=APP_CONFIG["ldap_base"]
  LDAP_USER=APP_CONFIG["ldap_user"]
  LDAP_PASS=APP_CONFIG["ldap_pass"]
  IS_SANDBOX=APP_CONFIG["is_sandbox"]
  EMAIL_HOST=APP_CONFIG["email_host"]
  EMAIL_PORT=APP_CONFIG["email_port"]

  @@ldap=LDAPStorage.new(LDAP_HOST,LDAP_PORT,LDAP_BASE,LDAP_USER,LDAP_PASS)
  @@emailer=Emailer.new({:host=>EMAIL_HOST,:port=>EMAIL_PORT})

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
    # 	#
    def self.add_user(userAccountRegistration)
       ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
       new_user = {
       			:first      => userAccountRegistration.firstName,
       			:last       => userAccountRegistration.lastName,
       			:email      => userAccountRegistration.email,
       			:password   => userAccountRegistration.password,
       			:vendor     => userAccountRegistration.vendor,
       			:status     => "submitted"
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
        ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
        ApprovalEngine.verify_email(emailtoken)
    end

    # Update the user information that was submitted via the add_user method.
    #
    # Input parameter: A subset of the "user_info" submitted to the "add_user" method.
    #
    def self.update_user_info(userAccountRegistration)
       ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
       new_user = {
                :first      => userAccountRegistration.firstName,
                :last       => userAccountRegistration.lastName,
                :email      => userAccountRegistration.email,
                :password   => userAccountRegistration.password,
                :vendor     => userAccountRegistration.vendor,
                :status     => "submitted"
            }
       ApprovalEngine.update_user_info(new_user)
    end

    #get email token for a specific user
    def self.get_email_token(email_address)
        ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
        user_info= ApprovalEngine.get_user(email_address)
        return user_info[:emailtoken]
    end

    # Returns an individual user via their email token or nil if the user does not exist.
      def ApprovalEngine.get_user_with_emailtoken(email_token)
        ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
        return ApprovalEngine.get_user_emailtoken(email_token)
      end


    #remove user with address
    def self.remove_user(email_address)
        ApprovalEngine.init(@@ldap,@@emailer,IS_SANDBOX)
        ApprovalEngine.remove_user(email_address)
    end
end
