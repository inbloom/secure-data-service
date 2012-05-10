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
      Eula.accept session[:guuid]
      render :finish
    else 
      Eula.reject session[:guuid]
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
