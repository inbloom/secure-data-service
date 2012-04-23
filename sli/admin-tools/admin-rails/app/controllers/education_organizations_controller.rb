class EducationOrganizationsController < ApplicationController
  # GET /education_organizations
  # GET /education_organizations.json
  def index
    @education_organizations = EducationOrganization.all
    @education_organization_associations = EducationOrganizationAssociations.all

    @levels ={}
    @education_organizations.each do |edOrg|
      associations = @education_organization_associations.select{|assoc| assoc.educationOrganizationChildId == edOrg.id}
      if associations.size != 0
        @levels[edOrg.nameOfInstitution] = @education_organizations.select{|possibleChild|}
        #@education_organization_associations.select{|assoc| assoc.educationOrganizationChildId == }
      end
    end

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @education_organizations }
    end
  end

  # GET /education_organizations/1
  # GET /education_organizations/1.json
  def show
    @education_organization = EducationOrganization.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @education_organization }
    end
  end

  # GET /education_organizations/new
  # GET /education_organizations/new.json
  def new
    @education_organization = EducationOrganization.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @education_organization }
    end
  end

  # GET /education_organizations/1/edit
  def edit
    @education_organization = EducationOrganization.find(params[:id])
  end

  # POST /education_organizations
  # POST /education_organizations.json
  def create
    @education_organization = EducationOrganization.new(params[:education_organization])

    respond_to do |format|
      if @education_organization.save
        format.html { redirect_to @education_organization, notice: 'Education organization was successfully created.' }
        format.json { render json: @education_organization, status: :created, location: @education_organization }
      else
        format.html { render action: "new" }
        format.json { render json: @education_organization.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /education_organizations/1
  # PUT /education_organizations/1.json
  def update
    @education_organization = EducationOrganization.find(params[:id])

    respond_to do |format|
      if @education_organization.update_attributes(params[:education_organization])
        format.html { redirect_to @education_organization, notice: 'Education organization was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @education_organization.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /education_organizations/1
  # DELETE /education_organizations/1.json
  def destroy
    @education_organization = EducationOrganization.find(params[:id])
    @education_organization.destroy

    respond_to do |format|
      format.html { redirect_to education_organizations_url }
      format.json { head :ok }
    end
  end
end
