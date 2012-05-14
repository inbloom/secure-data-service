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
     if APP_CONFIG["is_sandbox"] == true
      @currEnvironment=true
     else
      @currEnvironment=false
     end
    ApprovalEngineProxy.init(APP_CONFIG['approval_uri'],@currEnvironment)
    if Eula.accepted?(params)
      ApprovalEngineProxy.EULAStatus(session[:email],true)
      render :finish
    else 
      ApprovalEngineProxy.EULAStatus(session[:email],false)
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
