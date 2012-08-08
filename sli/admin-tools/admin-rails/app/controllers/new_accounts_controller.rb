=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


require 'active_support/secure_random'
require 'digest'
require 'ldapstorage'
require 'time'
require 'date'

class NewAccountsController < ForgotPasswordsController
  
  skip_filter :handle_oauth
  before_filter :get_user, :only => [:new_account, :update]
  before_filter :token_still_valid, :only => [:new_account, :update]
  
  def index
    set_model(params)
    respond_to do |format|
      format.html { render action: 'new_account' }
    end 

    # respond_to do |format|
    #   format.html # index.html.erb
    #   format.json { render json: @forgot_passwords }
    # end
  end
  
  def set_password
    set_model(params)
    @new_account_password.new_pass = params[:new_account_password][:new_pass]
    @new_account_password.confirmation = params[:new_account_password][:confirmation]
    is_valid = @new_account_password.valid?

    respond_to do |format|
      # re-render the form if not valid otherwise redirect to the target page 
      if !is_valid 
        format.html { render action: 'new_account' }
      else 
        # TODO set the password and redirect to the rendered page 
      end 
    end 
  end

  def set_model(params)
    token = params[:key]
    # TODO get the username of the inviter and the edorg 
    inviter, edorg = "jdoe", "Fictitious School District"
    @new_account_password = NewAccountPassword.new
    @new_account_password.token = token
    @new_account_password.inviter = inviter
    @new_account_password.edorg = edorg
  end 
end
