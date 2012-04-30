require 'set'

module ApprovalEngine
	# define the possible states of the finite state machine (FSM)
	SUBMITTED = 1
	PENDING   = 2
	REJECTED  = 3
	APPROVED  = 4
	DISABLED  = 5

	# gather all the valid states for easy lookup 
	VALID_STATES = Set.new [SUBMITTED, PENDING, REJECTED, APPROVED, DISABLED]

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
	# 
	#
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
	def get_users(state=nil)
	end 

	# Approve a given user. This moves the user into the APPROVED state if permitted by
	# the approval engine. 
	#
	# Input parameter:
	#
	# email_address: Identifies a previosly added user that has verified their email address. 
	# Thus is in state PENDING.
	# 
	def approve_user(email_address)
	end

	# Reject a user. This moves a user to the REJECTED state. 
	# Assumes that the user is in state PENDING. 
	#
	# Input parameter:
	# 
	# email_address: Previously verified email address. 	
	def reject_user(email_address)
	end

	# Disable a previously approved user. This moves a user to DISABLED state. 
	# Assumes that the user is in state PENDING. 
	#
	# Input parameter:
	# 
	# email_address: Previously verified email address. 	
	def disable_user(email_address)
	end

	# Enables a previously disabled user. This moves the user to the APPROVED state. 
	# Assumes that the user is in DISABLED state. 
	#
	# Input parameters:
	# 
	# email_address: Previously verified email address. 	
	def enable_user(email_address)
	end

	# Removes a user from the backend entirely.  
	#
	# Input parameters:
	# 
	# email_address: Previously added email_address identifying a user. 
	def remove_user(email_address)
	end
end
