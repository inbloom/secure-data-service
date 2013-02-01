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



class UserAccountValidationController < ApplicationController
  skip_before_filter :handle_oauth

  # GET /user_account_registrations/validate/1
  # GET /user_account_registrations/validate/1.json
  def show
    @validation_result = UserAccountValidation.validate_account params[:id]
    if @validation_result[:success] and APP_CONFIG["is_sandbox"]
        #email slc operator about newly created user
        begin 
          user = APP_LDAP_CLIENT.read_user_emailtoken(params[:id])
          ApplicationMailer.notify_operator_on_acct_creation(user).deliver unless user.nil?
        rescue Exception => e
          logger.error e.message
        end
    end
    render :show
  end # end def show
end
