
class EulasController < ApplicationController
  # GET /eula 
  def show
    puts "session = #{session}"
    respond_to do |format|
      format.html 
    end
  end

  def create
    if Eula.accepted?(params)
      render :finish
    else 
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
