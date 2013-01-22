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


class EulasController < ApplicationController
  skip_before_filter :handle_oauth
  # GET /eula 
  def show
    if !Session.valid?(session)
      not_found
    end

    respond_to do |format|
      format.html
    end
  end

  def create
    begin
      if Eula.accepted?(params)
        protocol=request.env['SERVER_PROTOCOL']
        protocol=protocol[0..-1+protocol.rindex('/')].downcase+"://"
        if (ApplicationHelper.send_user_verification_email(protocol+request.env['HTTP_HOST'], session[:guuid]))
          render :finish
        else
          render :account_error
        end
      else
        ApplicationHelper.remove_user_account session[:guuid]
        redirect_to APP_CONFIG['redirect_slc_url']
      end
    rescue ApplicationHelper::EmailException => e
      logger.error "Could not send email upon EULA acceptance for #{session[:guuid]}"
      logger.error e.message
      render :invalid_email
      begin
        APP_LDAP_CLIENT.delete_user(session[:guuid])
      rescue Exception => f
        logger.fatal "Could not delete user email account after failure: #{session[:guuid]}"
      end
    rescue Exception => e
      logger.error e.message
      logger.error e.backtrace.join("\n")
      render :noframe_500, :status => 500
    end

  end

  def not_found
    raise ActionController::RoutingError.new('Not Found')
  end
end
