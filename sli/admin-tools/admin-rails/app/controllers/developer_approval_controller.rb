require 'approval'

class DeveloperApprovalController < ApplicationController

    API_BASE=APP_CONFIG["api_base"]

    INVALID_VERIFICATION_CODE = {
        "status" => 403,
        "message" => "Invalid account verification code."
    }

    REST_HEADER = {
        "Content-Type" => "application/json",
        "content_type" => "json",
        "accept" => "application/json"
    }

    EMAIL_SUBJECT = "SLC Account Verification Request"

    #
    # Check if the provided user exists.
    #
    # Input: username name of the user
    # Return: HTTP Response with JSON 'status' (true/false) and 'validated' (true/false) in the body.
    #
    # Response body example:
    # { 'exists':true, 'validated':true }
    #
    # GET /does_user_exist/1
    # GET /does_user_exist/1.json
    def does_user_exist
        rval = { :exists => true, :validated => false }
        user = ApprovalEngine.get_user(params[:id])

        if (user.nil?)
            rval[:exists] = false
        else
            if (user[:status] != ApprovalEngine.ACTION_VERIFY_EMAIL)
               rval[:validated] = true
            end
        end

        respond_to do |format|
            format.any({ :status => 200, :content_type => 'application/JSON', :body => @@j.encode(rval) })
        end
    end

    #
    # Submit the user to the ApprovalEngine.
    #
    # Input:
	# user_info is a JSON object with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
	#
	# Return: HTTP Response with JSON 'status' JSON ('submitted' or 'existingUser')
	# and 'verificationToken':'string' in the body. This token must be passed
	# to the update_eula_status to link the verification email to the user.
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
	# { 'status':'submitted', 'verificationToken':'1234abcd' }
	#
	# POST /submit_user
    def submit_user 
        rval = { :status => 'submitted', :verificationToken => nil }

        user_info = @@j.decode(request.body.read)

        if (APP_LDAP_CLIENT.user_exists?(user_info[:email]))
            rval[:status] = 'existingUser'
        else
            rval[:verificationToken] = ApprovalEngine.add_disabled_user(user_info)
        end

        respond_to do |format|
            format.any({ :status => 201, :content_type => 'application/JSON', :body => @@j.encode(rval) })
        end
    end

    #
    # Update the user.
    #
    # Input:
	# user_info is a JSON object with the following fields:
	#   - first : first name
	#   - last  : last name
	#   - email : email address (also serves as the userid)
	#   - password : password used to log in
	#   - vendor : optional vendor name
	#
	# Return: HTTP Response with JSON 'status' JSON ('updated' or 'unknownUser')
	# and 'verificationToken':'string' in the body.  This token must be passed
	# to the update_eula_status to link the verification email to the user.
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
	# { 'status':'updated', 'verificationToken':'1234abcd' }
	#
	# POST /update_user
    def update_user 
        rval = { :status => 'unknownUser', :verificationToken => nil }

        user_info = @@j.decode(request.body.read)
        if (ApprovalEngine.user_exists(user_info[:email]))
            rval[:status] = 'updated'
            rval[:verificationToken] = ApprovalEngine.update_user_info(user_info)
        end

        respond_to do |format|
            format.any({ :status => 201, :content_type => 'application/JSON', :body => @@j.encode(rval) })
        end
    end

    #
    # Indicate the users response to the EULA.
    #
    # If the user accepts the EULA, this updates the user entry and sends a verification
    # email. Otherwise, the user is discarded.
    #
    # Input:
    #    eula_status is a JSON object with the following fields:
    #    - email: email address
    #    - verificationToken: token returned by submit_user or update_user
    #    - accepted: true/false
    #    - validateBase: The base resource used in the email verification message.
    #        This needs to point to a user account registration app endpoint.
    #
    # Return: HTTP Response with no body.  If the verificationToken is not matched
    # to the user, returns a 403 error.
    #
    # Input Example:
    # eula_status = {
    #     'email' : 'joe@example.com',
    #     'verificationToken' : '1234abcd',
    #     'accepted':true,
    #     'validateBase':'http://dev.slc.org/developer_accounts'
    # }
    #
    # POST /update_eula_status
    def update_eula_status 

        eula_status = @@j.decode(request.body.read)

        email = eula_status[:email]
        email_token = eula_status[:verificationToken]
        validate_base = eula_status[:validateBase]

        success = false

        user = ApprovalEngine.get_user_emailtoken(email_token)

        if (user.nil? or user[:email] != email)
            success = false

        # if the user doesn't accept the EULA, remove their entry.
        elsif (!eula_status[:accepted])
            res = RestClient.get(API_BASE + "/" + guid, REST_HEADER){|response, request, result| response }
            if (res.code==200)
                jsonDocument = @@j.decode(res.body.read)
                if (jsonDocument[:userName])
                    ApprovalEngine.remove_user(jsonDocument[:userName])
                    res = RestClient.delete(API_BASE+"/"+guid, REST_HEADER){|response, request, result| response }
                end
            end

        else
            userEmailValidationLink = "http://#{validate_base}/user_account_validation/#{email_token}"

            email_message = "Your SLI account has been created pending email verification.\n" <<
                "\n\nPlease visit the following link to confirm your account:\n" <<
                "\n\n#{userEmailValidationLink}\n\n"

            if (email_token.nil?)
                email_message = "There was a problem creating your account. Please try again."
            end

            APP_EMAILER.send_approval_email(
                user[:email],
                user[:firstName],
                user[:lastName],
                EMAIL_SUBJECT,
                email_message)
        end

        respond_to do |format|
            if (!success)
                format.any({ :head => :forbidden}, INVALID_VERIFICATION_CODE )
            else
                format.any({ :status => 200 })
            end
        end
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
    # POST /verify_email/1
    # POST /verify_email/1.json
    def verify_email 
        token = params[:id]
        rval = { :status => 'success' }

   		user = ApprovalEngine.get_user_emailtoken(token)

   		if (user.nil?)
   		    rval[:status] = 'unknownUser'

   		elsif (user[:status] != ApprovalEngine.ACTION_VERIFY_EMAIL)
   		    rval[:status] = 'previouslyVerified'

   		else
            ApprovalEngine.verify_email(token)
   		end

        respond_to do |format|
            format.any({ :status => 200, :body => @@j.encode(rval) })
        end
    end
end
