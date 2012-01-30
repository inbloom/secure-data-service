class MappingsController < ApplicationController
  
  def get_mappings_from_realm(realm)
    roles = Role.all
    mapping = []
    roles.each do |role|
      map = {}
      map[:name] = role.name
      map[:mappings] = []
      if role.mappings.attributes[realm]
        map[:mappings] = role.mappings.attributes[realm]
      end
      mapping.push map
    end
    mapping
  end
  # GET /mappings
  # GET /mappings.json
  def index
    @mappings = Realm.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @mappings }
    end
  end

  # GET /mappings/1
  # GET /mappings/1.json
  def show
    @mapping = get_mappings_from_realm params[:id]
    @sli_roles = []
    @mapping.each do |mapping|
      @sli_roles.push mapping[:name]
    end
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @mapping }
    end
  end


  # GET /mappings/1/edit
  def edit
    @mapping = get_mappings_from_realm(params[:id])
    @sli_roles = []
    @mapping.each do |mapping|
      @sli_roles.push mapping[:name]
    end
  end
  
  # POST /mappings/1/add
  def add
    require 'net/http'
    url = URI.parse('https://testapi1.slidev.org/api/rest/pub/roles/mappings')
    req = Net::HTTP::Post.new("#{url.request_uri}?realmId=#{URI.escape(params[:id])}&sliRole=#{URI.escape(params[:sli_role])}&clientRole=#{URI.escape(params[:new_role])}")
    #req.set_form_data( {:sessionId => cookies['iPlanetDirectoryPro'], :realmId => params[:id], :sliRole => params[:sli_role], :clientRole => params[:new_role]})
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    resp = http.request(req)
    resp.value()
    respond_to do |format|
      format.json { render json: resp   }
    end
  end

  def remove 
    require 'net/http'
    url = URI.parse('https://testapi1.slidev.org/api/rest/pub/roles/mappings')
    req = Net::HTTP::Delete.new("#{url.request_uri}?realmId=#{URI.escape(params[:id])}&clientRole=#{URI.escape(params[:client_role])}")
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_NONE
    resp = http.request(req)
    resp.value()
    respond_to do |format|
      format.json { render json: resp   }
    end
  end

  # # POST /mappings
  # # POST /mappings.json
  # def create
  #   @mapping = Mapping.new(params[:mapping])
  # 
  #   respond_to do |format|
  #     if @mapping.save
  #       format.html { redirect_to @mapping, notice: 'Mapping was successfully created.' }
  #       format.json { render json: @mapping, status: :created, location: @mapping }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @mapping.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # PUT /mappings/1
  # PUT /mappings/1.json
  def update
    @realm_id = params[:id]
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
  # 
  # # DELETE /mappings/1
  # # DELETE /mappings/1.json
  def destroy
    @mapping = Mapping.find(params[:id])
    @mapping.destroy
  
    respond_to do |format|
      format.html { redirect_to mappings_url }
      format.json { head :ok }
    end
  end
end
