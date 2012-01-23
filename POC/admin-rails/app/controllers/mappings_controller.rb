class MappingsController < ApplicationController
  # GET /mappings
  # GET /mappings.json
  def index
    @mappings = Mapping.all
    @realms = Realm.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: [@mappings, @realms] }
    end
  end

  # GET /mappings/1
  # GET /mappings/1.json
  def show
    @mapping = Mapping.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @mapping }
    end
  end

  # GET /mappings/new
  # GET /mappings/new.json
  def new
    @mapping = Mapping.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @mapping }
    end
  end

  # GET /mappings/1/edit
  def edit
    @mapping = Mapping.find(params[:id])
  end

  # POST /mappings
  # POST /mappings.json
  def create
    @mapping = Mapping.new(params[:mapping])

    respond_to do |format|
      if @mapping.save
        format.html { redirect_to @mapping, notice: 'Mapping was successfully created.' }
        format.json { render json: @mapping, status: :created, location: @mapping }
      else
        format.html { render action: "new" }
        format.json { render json: @mapping.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /mappings/1
  # PUT /mappings/1.json
  def update
    @mapping = Mapping.find(params[:id])

    respond_to do |format|
      if @mapping.update_attributes(params[:mapping])
        format.html { redirect_to @mapping, notice: 'Mapping was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @mapping.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /mappings/1
  # DELETE /mappings/1.json
  def destroy
    @mapping = Mapping.find(params[:id])
    @mapping.destroy

    respond_to do |format|
      format.html { redirect_to mappings_url }
      format.json { head :ok }
    end
  end
end
