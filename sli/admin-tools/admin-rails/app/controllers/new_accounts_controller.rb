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

class NewAccountsController < ForgotPasswordsController

  skip_filter :handle_oauth
  before_filter :get_user
  before_filter :set_model
  before_filter :token_still_valid

  def index
    respond_to do |format|
      format.html { render action: 'new_account' }
    end

    # respond_to do |format|
    #   format.html # index.html.erb
    #   format.json { render json: @forgot_passwords }
    # end
  end

  def set_password
    @new_account_password.new_pass = params[:new_account_password][:new_pass]
    @new_account_password.confirmation = params[:new_account_password][:confirmation]
    @new_account_password.terms_and_conditions = params[:terms_and_conditions]
    respond_to do |format|
      # re-render the form if not valid otherwise redirect to the target page
      if @new_account_password.set_password {|emailAddress, fullName| ApplicationMailer.samt_welcome(@user[:emailAddress], @user[:first], APP_LDAP_CLIENT.get_user_groups(@user[:email])).deliver}
        @user[:status] = 'approved'
        APP_LDAP_CLIENT.update_status(@user)
        format.html { redirect_to "/forgotPassword/success", notice: 'Your password has been successfully modified.'}
        format.json { render :json => @forgot_password, status: :created, location: @forgot_password }
      else
        format.html { render action: 'new_account' }
      end
    end
  end

  def set_model
    puts @user
    token = params[:key]
    edorg = @user[:edorg]
    @new_account_password = NewAccountPassword.new
    @new_account_password.token = token
    @new_account_password.edorg = edorg
    @new_account_password.tou_required = APP_CONFIG['is_sandbox'] && @user[:status] == "submitted"
  end
end
