class HomesController < ApplicationController
  # GET /homes
  # GET /homes.json
  def index
    @homes = Home.all
    
    @homes.each do |home|
      home.href.sub!("https://devapp1.slidev.org/api/rest", "http://#{request.host_with_port}")
      logger.info home
    end
    

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @homes }
    end
  end

end
