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


require 'set'
require 'digest'
require 'ldapstorage'
require 'securerandom'

module ApprovalEngine

  # Custom Exception and error codes 
  class ApprovalException < StandardError 
    attr :error_code 
    def initialize(error_code, msg)
      super(msg)
      @error_code = error_code
    end 
  end 


  ERR_INVALID_TRANSITION = 1
  ERR_EMAIL_NOT_VERIFIED = 2
  ERR_UNKNOWN_TRANSITION = 3 
  ERR_UNKNOWN_USER = 4 

  # define the possible states of the finite state machine (FSM)
  STATE_SUBMITTED     = "submitted"
  STATE_EULA_ACCEPTED = "eula-accepted"
  STATE_PENDING       = "pending"
  STATE_REJECTED      = "rejected"
  STATE_APPROVED      = "approved"
  STATE_DISABLED      = "disabled"

  # define all possible actions of the state machine
  ACTION_ACCEPT_EULA  = "accept_eula"
  ACTION_VERIFY_EMAIL = "verify_email"
  ACTION_APPROVE      = "approve"
  ACTION_REJECT       = "reject"
  ACTION_DISABLE      = "disable"
  ACTION_ENABLE       = "enable"

  # define the FSM
  # for each state we define a set of transitions. Each transition has
  # a tuple associated with it:
  #  - target state : the next state
  #  - function     : a Proc that nees to be executed to get to the target state
  #
  FSM = {
      STATE_SUBMITTED     => { ACTION_ACCEPT_EULA => STATE_EULA_ACCEPTED},
      STATE_EULA_ACCEPTED => { ACTION_VERIFY_EMAIL => STATE_PENDING},
      STATE_PENDING       => { ACTION_APPROVE => STATE_APPROVED,
                               ACTION_REJECT => STATE_REJECTED  },
      STATE_REJECTED      => { ACTION_APPROVE => STATE_APPROVED },
      STATE_APPROVED      => { ACTION_DISABLE => STATE_DISABLED },
      STATE_DISABLED      => { ACTION_ENABLE  => STATE_APPROVED }
  }

  # Roles
  ROLE_APPLICATION_DEVELOPER = "application_developer"
  ROLE_INGESTION_USER        = "ingestion_user"
  ROLE_SANDBOX_ADMINISTRATOR = "Sandbox Administrator"

  # Roles to set in sandbox mode
  SANDBOX_ROLES = [
      ROLE_APPLICATION_DEVELOPER,
      ROLE_INGESTION_USER,
      ROLE_SANDBOX_ADMINISTRATOR
  ]

  # Roles to set in production mode
  PRODUCTION_ROLES = [
      ROLE_APPLICATION_DEVELOPER
  ]

  # all the user states that should be included in the user count
  COUNTABLE_STATES = Set.new [
                                 STATE_PENDING,
                                 STATE_APPROVED
                             ]

  ## backend storage
  @@storage                  = nil
  @@transition_action_config = nil
  @@is_sandbox               = false
  @@email_secret             = ""
  @@roles                    = []
  @@auto_approve             = nil

  # initialize the storage
  def ApprovalEngine.init(storage, transition_action_config, is_sandbox, auto_approve=nil)
    @@storage = storage
    @@transition_action_config = transition_action_config
    @@is_sandbox = is_sandbox
    @@email_secret = SecureRandom.hex(32).encode("UTF-8")
    @@roles = is_sandbox ? SANDBOX_ROLES : PRODUCTION_ROLES
    @@auto_approve = auto_approve
  end

  # Update the status of a user.
  #
  # Input parameter:
  #
  # email_address: Identifies a previosly added user that has verified their email address.
  # transition:    A transition identifier previously returned by the "get_users" method.
  #
  def ApprovalEngine.change_user_status(email_address, transition, email_verified=false)
    user = @@storage.read_user(email_address)
    status = user[:status]
    target = FSM[status]

    if (!target) || (!target.key?(transition))
      raise ApprovalException.new(ERR_INVALID_TRANSITION, "Current status '#{user[:status]}' does not allow transition '#{transition}'.")
    end

    # make sure that the email is verified if this is transitioning: eula_accepted -> pending
    if (status == STATE_EULA_ACCEPTED) && !email_verified
      raise ApprovalException.new(ERR_EMAIL_NOT_VERIFIED, "Email must be verified before being able to accept the Eula.")
    end

    # set the new user status
    user[:status] = target[transition]
    case [status, target[transition]]
      when [STATE_SUBMITTED, STATE_EULA_ACCEPTED]
        set_emailtoken(user)
        @@storage.update_status(user)
      when [STATE_EULA_ACCEPTED, STATE_PENDING]
        @@storage.update_status(user)
      when [STATE_PENDING, STATE_APPROVED]
        # update the status, set the roles and send an email
        @@storage.update_status(user)
        set_roles(user[:email])

        # if this is sandbox write the userid as the tennant
        set_sandbox_tenant(user)

      when [STATE_PENDING, STATE_REJECTED]
        # update the status and clear the roles
        @@storage.update_status(user)
        clear_roles(user[:email])
      when [STATE_REJECTED, STATE_APPROVED]
        # update the status and set the roles
        @@storage.update_status(user)
        set_roles(user[:email])

        # if this is sandbox write the userid as the tennant
        set_sandbox_tenant(user)
      when [STATE_APPROVED, STATE_DISABLED]
        @@storage.update_status(user)
        clear_roles(user[:email])
      when [STATE_DISABLED, STATE_APPROVED]
        @@storage.update_status(user)
        set_roles(user[:email])

        # if this is sandbox write the userid as the tennant
        set_sandbox_tenant(user)
      else
        raise ApprovalException.new(ERR_UNKNOWN_TRANSITION, "Unknown state transition #{status} => #{target[transition]}.")
    end

    @@transition_action_config.transition(user) if @@transition_action_config

    # if this is a sandbox and the new status is pending then move to status approved
    if ((@@auto_approve==nil && @@is_sandbox) || @@auto_approve) && (user[:status] == STATE_PENDING)
      change_user_status(email_address, ACTION_APPROVE)
    end
  end

  # Verify the email address against the backend.
  #
  # Input Parameter:
  #
  # email_hash : The email hash that was previously returned by the add_user
  # and included in a click through link that the user received in an email (as a query parameter).
  #
  def ApprovalEngine.verify_email(emailtoken)
    user = @@storage.read_user_emailtoken(emailtoken)
    raise ApprovalException.new(ERR_UNKNOWN_USER, "Could not find user for email id #{emailtoken}.") if !user

    # update to pending state
    change_user_status(user[:email], ACTION_VERIFY_EMAIL, true)
  end

  # Check whether a user with the given email address exists.
  # The email address serves as the unique userid.
  def ApprovalEngine.user_exists?(email_address)
    @@storage.user_exists?(email_address)
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
  #     :vendor => "Acme Inc.",
  #     :emailAddress => "jdoe@example.com"
  # }
  #     #
  def ApprovalEngine.add_disabled_user(user_info)
    new_user_info = user_info.clone
    new_user_info[:status]     = STATE_SUBMITTED
    @@storage.create_user(new_user_info)
    return new_user_info[:emailtoken]
  end

  # Returns a list of users and their states. If a target state is provided
  # all users in that state will be returned.
  #
  # Optional input Parameter:
  #
  # state : Target state. Only retrieve user records for this state.
  #
  # user_info = {
  #     :first => "John",
  #     :last => "Doe",
  #     :email => "jdoe@example.com",
  #     :password => "secret",
  #     :vendor => "Acme Inc.",
  #     :status => "pending",
  #     :transitions => ["approve", "reject"],
  # }    #
  def ApprovalEngine.get_users(status=nil)
    return @@storage.read_users(status).select {|u| !!u[:status] }.map do |user|
      user[:transitions] = FSM[user[:status]].keys
      user
    end
  end

  def ApprovalEngine.get_user_count
    users = get_users
    users = users.select { |u| COUNTABLE_STATES.include?(u[:status]) }
    return users.length
  end

  def ApprovalEngine.get_user_count_ignore_states
    return get_users.length
  end

  #return the count of application developers
  def ApprovalEngine.get_developer_count
    get_group_count(ROLE_APPLICATION_DEVELOPER)
  end

  #return the count of sandbox administrator
  def ApprovalEngine.get_sandbox_admin_count
    get_group_count(ROLE_SANDBOX_ADMINISTRATOR)
  end

  #return the count of ingestion user
  def ApprovalEngine.get_ingestion_user_count
    get_group_count(ROLE_INGESTION_USER)
  end



  # Returns an individual user via their email address or nil if the user does not exist.
  def ApprovalEngine.get_user(email_address)
    user = @@storage.read_user(email_address)
    if !!user[:status]
      user[:transitions] = FSM[user[:status]].keys
    end
    return user
  end

  # Returns an individual user via their email token or nil if the user does not exist.
  def ApprovalEngine.get_user_emailtoken(email_token)
    user = @@storage.read_user_emailtoken(email_token)
    if !!user[:status]
      user[:transitions] = FSM[user[:status]].keys
    end
    return user
  end

  # Update the user information that was submitted via the add_user method.
  #
  # Input parameter: A subset of the "user_info" submitted to the "add_user" method.
  #
  def ApprovalEngine.update_user_info(user_info)
    @@storage.update_user_info(user_info)
  end

  # Removes a user from the backend entirely.
  #
  # Input parameters:
  #
  # email_address: Previously added email_address identifying a user.
  def ApprovalEngine.remove_user(email_address)
    #user = @@storage.read_user(email_address)
    clear_roles(email_address)
    @@storage.delete_user(email_address)
  end

  def ApprovalEngine.get_roles(email_address)
    @@storage.get_user_groups(email_address)
  end

  #############################################################
  # Private methods
  #############################################################
  private

  def ApprovalEngine.get_group_count(groupid)
    count = 0
    group_found = @@storage.get_group(groupid)
    if group_found
      count = group_found['memberUid'].length
    end
    count
  end

  def ApprovalEngine.set_roles(email_address)
    @@roles.each do |role|
      @@storage.add_user_group(email_address, role)
    end
  end

  def ApprovalEngine.clear_roles(email_address)
    user_roles = @@storage.get_user_groups(email_address)
    user_roles.each do |role|
      @@storage.remove_user_group(email_address, role)
    end
  end

  def ApprovalEngine.set_emailtoken(user)
    user_info = {
        :email => user[:email],
        :emailtoken => Digest::MD5.hexdigest(@@email_secret + user[:email] + user[:first] + user[:last])
    }
    update_user_info(user_info)
  end

  def ApprovalEngine.set_sandbox_tenant(user)
    if @@is_sandbox
      new_user_info = {
          :email => user[:email],
          :tenant => user[:email]
      }
      @@storage.update_user_info(new_user_info)
    end
  end
end
