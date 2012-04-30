require 'set'

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

	# Check whether a user with the given email address exists. 
	# The email address serves as the unique userid. 
	def user_exists?(email_address)
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
	def add_user(user_info)
	end

	# Verify the email address against the backend. 
	# 
	# Input Parameter:
	# 
	# email_hash : The email hash that was previously returned by the add_user 
	# and included in a click through link that the user received in an email (as a query parameter).
	#
	def verify_email(email_hash)
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
	def get_users(state=nil)
	end 

	# Update the status of a user. 
	#
	# Input parameter:
	#
	# email_address: Identifies a previosly added user that has verified their email address. 
	# transition:    A transition identifier previously returned by the "get_users" method. 
	# 
	def change_user_status(email_address, transition)
	end

	# Update the user information that was submitted via the add_user method. 
	#
	# Input parameter: A subset of the "user_info" submitted to the "add_user" method. 
	# 
	def update_user_info(user_info)
	end

	# Removes a user from the backend entirely.  
	#
	# Input parameters:
	# 
	# email_address: Previously added email_address identifying a user. 
	def remove_user(email_address)
	end
end
