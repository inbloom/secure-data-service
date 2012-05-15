require 'set'
require 'digest'
require 'rest_client'

#
# Proxy to the ApprovalEngine.
#
module ApprovalEngineProxy

	## backend storage
	@@approvalUri  = nil
	@@is_sandbox   = false
    @@j = ActiveSupport::JSON


	# initialize the storage
	#
	# approvalUri -- fully qualified uri to the approval engine. For example,
	#   https://local.slidev.org:2001/developer_approval
	# is_sandbox -- true if this is a sandbox environment.
	#
	def ApprovalEngineProxy.init(approvalUri, is_sandbox)
		@@approvalUri = approvalUri
		@@is_sandbox = is_sandbox
	end

    #
    # Check if the provided user exists.
    #
    # Input: username name of the user
    # Return: HTTP Response with JSON 'status' (true/false) and 'validated' (true/false) in the body.
    #
    # Response body example:
    # { 'exists':true, 'validated':true }
    #
    def doesUserExist(username)
        return RestClient.get(@@approvalUri + "/does_user_exist/" + username, {:accept => :json})
    end

    #
    # Submit the user to the ApprovalEngine. If the user already exists,
    # this call is a no-op.
    #
    # Input:
	# user_info is a hash with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
	#
	# Return: HTTP Response with JSON 'status' JSON ('submitted' or 'existingUser')
	# and 'verificationToken':'string' in the body. This token must be passed
	# to the EULAStatus method to link the verification email to the user.
    #
	# Input Example:
	# user_info = {
	#     :first => 'John',
	#     :last => 'Doe',
	#     :email => 'jdoe@example.com',
	#     :password => 'secret',
	#     :vendor => 'Acme Inc.'
	# }
	#
	# Response body example:
	# { 'status':'submitted', 'verificationToken':'1234abcd' }
	#
    def submitUser(user_info)
        return RestClient.post(@@approvalUri + "/submit_user", @@j.encode(user_info), :content_type => :json, :accept => :json)
    end

    #
    # Update the user.
    #
    # Input:
	# user_info is a hash with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
	#
	# Return: HTTP Response with JSON 'status' JSON ('updated' or 'unknownUser')
	# and 'verificationToken':'string' in the body.  This token must be passed
	# to the EULAStatus method to link the verification email to the user.
    #
	# Input Example:
	# user_info = {
	#     :first => 'John',
	#     :last => 'Doe',
	#     :email => 'jdoe@example.com',
	#     :password => 'secret',
	#     :vendor => 'Acme Inc.'
	# }
	#
	# Response body example:
	# { 'status':'updated', 'verificationToken':'1234abcd' }
	#
    def updateUser(user_info)
        return RestClient.post(@@approvalUri + "/update_user", @@j.encode(user_info), :content_type => :json, :accept => :json)
    end

    #
    # Indicate the users response to the EULA.
    #
    # If the user accepts the EULA, this updates the user entry and sends a verification
    # email. Otherwise, the user is discarded.
    #
    # Input:
    #    eula_status is a hash with the following fields:
    #    - email: email address
    #    - verificationToken: token returned by submit_user or update_user
    #    - accepted: true/false
    #
    # Return: HTTP Response with no body.  If the verificationToken is not matched
    # to the user, returns a 403 error.
    #
    # Input Example:
    # eula_status = {
    #     :email => 'joe@example.com',
    #     :verificationToken => '1234abcd',
    #     :accepted => true
    # }
    #
    # POST /update_eula_status
    def EULAStatus(eula_status)
        return RestClient.post(@@approvalUri + "/update_eula_status", @@j.encode(eula_status), :content_type => :json, :accept => :json)
    end

    #
    # Verify the users email address by checking the token provided in their
    # verification email.
    #
    # Return HTTP Response with JSON 'status' ('unknownUser', 'previouslyVerified', 'success')
    # in the body.
    #
    # Response example:
    #    { 'status':'success' }
    #
    def verifyEmail(verifyToken)
        response = RestClient.get(@@approvalUri + "/verifyEmail/" + verifyToken)
    end
end
