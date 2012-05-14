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
    # Return: true/false, and whether the user email is already validated
    #
    # Response example:
    # { exists:true/false, validated: true/false }
    #
    def doesUserExist(username)
        response = RestClient.get(@@approvalUri + "/doesUserExist/" + username)
        return response.body
    end

    #
    # Submit the user to the ApprovalEngine. If the user already exists,
    # this call is a no-op.
    #
	# user_info is a hash with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
    #
	# Input Example:
	# user_info = {
	#     'first' : 'John',
	#     'last' : 'Doe',
	#     'email' : 'jdoe@example.com',
	#     'password' : 'secret',
	#     'vendor' : 'Acme Inc.'
	# }
    def submitUser(user_info)
        response = RestClient.post(@@approvalUri + "/user", user_info)
    end

    #
    # Update the user.
    #
	# user_info is a hash with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
    #
	# Input Example:
	# user_info = {
	#     'first' : 'John',
	#     'last' : 'Doe',
	#     'email' : 'jdoe@example.com',
	#     'password' : 'secret',
	#     'vendor' : 'Acme Inc.'
	# }
    def updateUser(user_info)
        response = RestClient.put(@@approvalUri + "/user", user_info)
    end

    #
    # Indicate the users response to the EULA.
    #
    # If the user accepts the EULA, this returns the email verification token.
    # Otherwise, the user is discarded and the response token is nil.
    #
    # Response example:
    #    { 'token':'abcdefg' }
    #
    def EULAStatus(email, accepted)
        response = RestClient.post(@@approvalUri + "/UpdateEULAStatus", email, accepted)
    end

    #
    # Verify the users email address by checking the token provided in their
    # verification email.
    #
    # Return one of: 'unknownUser', 'previouslyVerified', 'success'
    #
    # Response example:
    #    { 'result':'success' }
    #
    def verifyEmail(verifyToken)
        response = RestClient.get(@@approvalUri + "/verifyEmail/" + verifyToken)
    end
end
