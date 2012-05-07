class EulasController < ApplicationController
  # GET /eulas
  # GET /eulas.json
  def index
    @eulas = Eula.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @eulas }
    end
  end

  # GET /eulas/1
  # GET /eulas/1.json
  def show
    @eula = Eula.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
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
      if @eula.save
        format.html { redirect_to @eula, notice: 'Eula was successfully created.' }
        format.json { render json: @eula, status: :created, location: @eula }
      else
        format.html { render action: "new" }
        format.json { render json: @eula.errors, status: :unprocessable_entity }
      end
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
end
