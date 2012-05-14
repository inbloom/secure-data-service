require 'set'
require 'digest'
require 'rest_client'
require 'json'

#
# Proxy to the ApprovalEngine.
#
module ApprovalEngineProxy

	## backend storage
	@@approvalUri  = nil
	@@is_sandbox   = false

	# initialize the storage
	#
	# approvalUri -- fully qualified uri to the approval engine.
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
        response = RestClient.get(@@approvalUri + "/doesUserExist/" + username)
        return JSON.parse response.body.read
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
	# Return: HTTP Response with JSON 'status' JSON ('submitted' or 'existingUser') in the body.
    #
	# Input Example:
	# user_info = {
	#     'first' : 'John',
	#     'last' : 'Doe',
	#     'email' : 'jdoe@example.com',
	#     'password' : 'secret',
	#     'vendor' : 'Acme Inc.'
	# }
	#
	# Response body example:
	# { 'status':'submitted' }
	#
    def submitUser(user_info)
        response = RestClient.post(@@approvalUri + "/user", user_info)
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
    # Return: HTTP Response with JSON 'status' ('unknownUser', 'updated') in the body.
    #
	# Input Example:
	# user_info = {
	#     'first' : 'John',
	#     'last' : 'Doe',
	#     'email' : 'jdoe@example.com',
	#     'password' : 'secret',
	#     'vendor' : 'Acme Inc.'
	# }
	#
	# Response body example:
	# { 'status':'unknownUser' }
	#
    def updateUser(user_info)
        response = RestClient.put(@@approvalUri + "/user", user_info)
    end

    #
    # Indicate the users response to the EULA.
    #
    # If the user accepts the EULA, this returns the email verification token.
    # Otherwise, the user is discarded and the response token is nil.
    #
    # REturn HTTP Response with JSON 'token' (string) if the user accepts the EULA,
    # or 'token' (nil) if not.
    #
    # Response body example:
    #    { 'token':'abcdefg' }
    #
    def EULAStatus(email, accepted)
        response = RestClient.post(@@approvalUri + "/UpdateEULAStatus", email, accepted)
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
