class EulasController < ApplicationController
  
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
    if Eula.accepted?(params)
      ApplicationHelper.send_user_verification_email(request.env['HTTP_HOST'], session[:guuid])
      render :finish
    else 
      ApplicationHelper.remove_user_account session[:guuid]
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
