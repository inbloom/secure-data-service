class EulasController < ApplicationController

  URL=APP_CONFIG['api_base']
  URL_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
      }
  INDEX=0

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
      render :finish
    else 
      gUID= session[:guuid]
      res = RestClient.get(URL+"/"+gUID, URL_HEADER){|response, request, result| response }
      if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            puts(jsonDocument)
            puts(jsonDocument["userName"])
            ApplicationHelper.remove_user(jsonDocument["userName"])
            res = RestClient.delete(URL+"/"+gUID, URL_HEADER){|response, request, result| response }
        end
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end
end
