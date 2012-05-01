require 'set'
require 'digest'
require 'ldapstorage'

#equire 'approval/storage'

module ApprovalEngine
	# define the possible states of the finite state machine (FSM)
	STATE_SUBMITTED = "submitted"
	STATE_PENDING   = "pending"
	STATE_REJECTED  = "rejected"
	STATE_APPROVED  = "approved"
	STATE_DISABLED  = "disabled"

	# define all possible actions of the state machine 
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
		STATE_SUBMITTED => {ACTION_VERIFY_EMAIL => STATE_PENDING},
		STATE_PENDING   => {ACTION_APPROVE => STATE_APPROVED, 
			                ACTION_REJECT => STATE_REJECTED },
		STATE_REJECTED  => {ACTION_APPROVE => STATE_APPROVED },
		STATE_APPROVED  => {ACTION_DISABLE => STATE_DISABLED }, 
		STATE_DISABLED  => {ACTION_ENABLE  => STATE_APPROVED }
	}

	## backend storage 
	@@storage = nil 

	# initialize the storage 
	def init_storage(storage)
		@@storage = storage
	end

	# Update the status of a user. 
	#
	# Input parameter:
	#
	# email_address: Identifies a previosly added user that has verified their email address. 
	# transition:    A transition identifier previously returned by the "get_users" method. 
	# 
	def ApprovalEngine.change_user_status(email_address, transition)
		user = @@storage.read_user(email_address)
		status = user[F_STATUS]
		target = FSM[status]

		# make sure this is not a transition to verify an eamil address. 
		raise "Cannot transition directly from state #{status}" if status==STATE_SUBMITTED

		if (!target) || (!target.key?(transition))
			raise "Current status '#{user[F_STATUS]}' does not allow transition '#{transition}'."
		end

		# set the new user status 
		user[status] = target[transition]
		case [status, target[transition]]
			when [STATE_PENDING, STATE_APPROVED]
				@@storage.enable_update_status(user)
			when [STATE_PENDING, STATE_REJECTED]
				@@storage.update_status(user)
			when [STATE_REJECTED, STATE_APPROVED]
				@@storage.enable_update_status(user)
			when [STATE_APPROVED, STATE_DISABLED]
				@@storage.disable_update_status(user)
			when [STATE_DISABLED, STATE_APPROVED]
				@@storage.enable_update_status(user)
			else
				raise "Unknow state transition #{status} => #{target[transition]}."
			end
	end


	# Check whether a user with the given email address exists. 
	# The email address serves as the unique userid. 
	def self.user_exists?(email_address)
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
	#     :vendor => "Acme Inc."
	# }
	# 	#
	def ApprovalEngine.add_disabled_user(user_info)
		new_user_info = user_info.clone 
		new_user_info[:emailtoken] = Digest::MD5.hexdigest(user_info[:email]+user_info[:first]+user_info[:last])
		new_user_info[:updated]    = 10
		new_user_info[:status]     = "pending"
		@@storage.create_user(new_user_info)
		return new_user_info[:emailtoken]
	end

	# Verify the email address against the backend. 
	# 
	# Input Parameter:
	# 
	# email_hash : The email hash that was previously returned by the add_user 
	# and included in a click through link that the user received in an email (as a query parameter).
	#
	def ApprovalEngine.verify_email(email_hash)
		user = @@storage.read_user_emailtoken(emailtoken)
		raise "Could not find user for email id #{email_hash}." if !user

		user[F_STATUS] = STATE_PENDING
		update_status(user)
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
	#     :updated => "datetime"
	# }	#
	def ApprovalEngine.get_users(status=nil)

	end 

	# Update the user information that was submitted via the add_user method. 
	#
	# Input parameter: A subset of the "user_info" submitted to the "add_user" method. 
	# 
	def ApprovalEngine.update_user_info(user_info)
	end

	# Removes a user from the backend entirely.  
	#
	# Input parameters:
	# 
	# email_address: Previously added email_address identifying a user. 
	def ApprovalEngine.remove_user(email_address)
	end
end
