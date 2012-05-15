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
     
    @currEnvironment =(APP_CONFIG["is_sandbox"] == true)
     eulaStatus ={
      "email" => session[:email],
      "verificationToken" => session[:verificationToken],
      "accepted" => true,
      "validateBase" => request.env['HTTP_HOST']
     }
    ApprovalEngineProxy.init(APP_CONFIG['approval_uri'],@currEnvironment)
    if Eula.accepted?(params)
      ApprovalEngineProxy.EULAStatus(eulaStatus)
      render :finish
    else 
      eulaStatus["accepted"]=false
      ApprovalEngineProxy.EULAStatus(eulaStatus)
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
