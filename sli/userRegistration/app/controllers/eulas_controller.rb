class EulasController < ApplicationController
  before_filter :cancel_redirect, :only => [:new, :create]
  before_filter :ok, :only => [:new, :create]

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
    @user_account_registration = UserAccountRegistration.new(params[:id])

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


  # GET /eulas/1/edit
  def edit
    @eula = Eula.find(params[:id])
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

  # PUT /eulas/1
  # PUT /eulas/1.json
  def update
    @eula = Eula.find(params[:id])

    respond_to do |format|
      if @eula.update_attributes(params[:eula])
        format.html { redirect_to @eula, notice: 'Eula was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @eula.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /eulas/1
  # DELETE /eulas/1.json
  def destroy
    @eula = Eula.find(params[:id])
    @eula.destroy

    respond_to do |format|
      format.html { redirect_to eulas_url }
      format.json { head :no_content }
    end
  end

private

  def cancel_redirect
    if params[:commit] == "Reject"
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end

  def ok
    if params[:commit] == "Ok"
      redirect_to APP_CONFIG['redirect_slc_url']
    end
  end

end
