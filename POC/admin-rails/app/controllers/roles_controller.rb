class RolesController < ApplicationController
  # GET /roles
  # GET /roles.json
  def index
    @roles = Role.all
    @roles.each do |role|
      role.examples = nil
      role.individual = nil
      case role.name
      when /Aggregator/
        role.examples = "State Data Analyst, State DOE Representative"
        role.individual = nil
      when /Leader/
        role.examples = "School Principal, District Superintendent, State Superintendent" 
        role.individual = "student enrolled in my district(s) or school(s)"
      when /Educator/
        role.examples = "Teacher, Athletic Coach, Classroom Assistant"
        role.individual = "student enrolled in my sections"
      when /IT Administrator/
        role.examples = "SLC Operator, SEA IT Admin, LEA IT Admin"
        role.individual = "student enrolled in my district(s) or school(s)"
      end
      role.general = []
      role.restricted = []
      role.aggregate = nil
      role.rights.each do |right|
        puts "Right: #{right}"
        case right
        when /AGGREGATE_READ/
          role.aggregate = "yes"
        when /READ_GENERAL/
          role.general << "R"
        when /READ_RESTRICTED/
          role.restricted << "R"
        when /WRITE_GENERAL/
          role.general << "W"
        when /WRITE_RESTRICTED/
          role.restricted << "W"
        end    
      end
      role.general = role.general.join('/')
      role.restricted = role.restricted.join('/')
    end
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @roles }
    end
  end

  # GET /roles/1
  # GET /roles/1.json
  def show
    @role = Role.find(params[:id])
  
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @role }
    end
  end

  # # GET /roles/new
  # # GET /roles/new.json
  # def new
  #   @role = Role.new
  # 
  #   respond_to do |format|
  #     format.html # new.html.erb
  #     format.json { render json: @role }
  #   end
  # end

  # GET /roles/1/edit
  # def edit
  #   @role = Role.find(params[:id])
  # end

  # POST /roles
  # POST /roles.json
  # def create
  #   @role = Role.new(params[:role])
  # 
  #   respond_to do |format|
  #     if @role.save
  #       format.html { redirect_to @role, notice: 'Role was successfully created.' }
  #       format.json { render json: @role, status: :created, location: @role }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @role.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # PUT /roles/1
  # PUT /roles/1.json
  # def update
  #   @role = Role.find(params[:id])
  #   puts "Role found (Update): #{@role.attributes}"
  #   respond_to do |format|
  #     if @role.update_attributes(params[:role])
  #       format.html { redirect_to @role.id, notice: 'Role was successfully updated.' }
  #       format.json { head :ok }
  #     else
  #       format.html { render action: "edit" }
  #       format.json { render json: @role.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # DELETE /roles/1
  # DELETE /roles/1.json
  # def destroy
  #   @role = Role.find(params[:id])
  #   @role.destroy
  # 
  #   respond_to do |format|
  #     format.html { redirect_to roles_url }
  #     format.json { head :ok }
  #   end
  # end
end
