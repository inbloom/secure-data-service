class LandingZoneController < ApplicationController
  before_filter :check_roles
  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ProvisioningError, :with => :handle_error
  rescue_from ActiveResource::ResourceConflict, :with => :already_there

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
        rval = { :exists => true, :validated: => false }
        usr = ApprovalEngine.get_user(params[:id])

        if usr==nil then
            rval[:exists] = false
        else
            if usr[:status] != ApprovalEngine.ACTION_VERIFY_EMAIL then
               rval[:validated] = true
            end
        end

        j = ActiveSupport::JSON
        respond_to do |format|
            format.any { :status => 200, :content_type: 'application/JSON', :body => j.encode(rval) }
        end
    end

    #
    # Submit the user to the ApprovalEngine.
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
    def submit_user do
        rval = { :status => 'submitted'. :verificationToken => nil }

        j = ActiveSupport::JSON
        user_info = j.decode(request.body.read)

        if ApprovalEngine.user_exists(user_info[:email]) then
            rval[:status] = 'existingUser'
        else
            rval[:verificationToken] = ApprovalEngine.add_disabled_user(user_info)
        end

        respond_to do |format|
            format.any { :status => 201, :content_type: 'application/JSON', :body => j.encode(rval) }
        end
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
    def update_user do
        rval = { :status => 'unknownUser'. :verificationToken => nil }

        j = ActiveSupport::JSON
        user_info = j.decode(request.body.read)

        if ApprovalEngine.user_exists(user_info[:email]) then
            rval[:status] = 'updated'
            rval[:verificationToken] = ApprovalEngine.update_user_info(user_info)
        end

        respond_to do |format|
            format.any { :status => 201, :content_type: 'application/JSON', :body => j.encode(rval) }
        end
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
    #     'email' : 'joe@example.com',
    #     'verificationToken' : '1234abcd',
    #     'accepted':true
    # }
    #
    # POST /update_eula_status
    def update_eula_status do
        email = params[:email]
        token = params[:verificationToken]
        accepted = params[:accept]
        success = false

        user = ApprovalEngine.get_user_emailtoken(token)

        if user == nil or user[:email] != email then
            success = false
        else
        end

        respond_to do |format|
            if success == false then
                format.any  { head :forbidden }
            else
                format.any { :status => 200 }
            end
        end

        if accepted == true then
        else
            if (guid.nil?)
                return
            end
            res = RestClient.get(API_BASE + "/" + guid, REST_HEADER){|response, request, result| response }
        if (res.code==200)
        jsonDocument = JSON.parse(res.body)
        remove_user(jsonDocument["userName"])
        res = RestClient.delete(API_BASE+"/"+guid, REST_HEADER){|response, request, result| response }
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
    post '/verifyEmail/:verifyToken' do
        ApprovalEngine.verify_email(params[:verifyToken])
    end

end
