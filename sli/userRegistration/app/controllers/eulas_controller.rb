class EulasController < ApplicationController
  before_filter :redirect, :only => [:new, :create]

  # GET /eulas
  # GET /eulas.json
  def index
    @eulas = Eula.new

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @eulas }
    end
  end

  # GET /eulas/1
  # GET /eulas/1.json
  def show
    @eula = Eula.new

          respond_to do |format|
            format.html # new.html.erb
            format.json { render json: @eula }
          end
  end

  # GET /eulas/new
  # GET /eulas/new.json
  def new
    @eula = Eula.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @eula }
    end
  end

  # POST /eulas
  # POST /eulas.json
  def create
    @eula = Eula.new(params[:eula])

    respond_to do |format|
      format.html { redirect_to @eula }
      format.json { render json: @eula }
    end
  end

private

  def redirect
    if params[:commit] == "Reject"
      redirect_to APP_CONFIG['redirect_slc_url']
    end
    if params[:commit] == "Accept"
      redirect_to "/finish"
    end
    if params[:commit] == "Ok"
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end

end
