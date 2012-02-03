class TeacherSchoolAssociationsController < ApplicationController
  # GET /teacher_school_associations
  # GET /teacher_school_associations.json
  def index
    @teacher_school_associations = TeacherSchoolAssociation.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @teacher_school_associations }
    end
  end

  # GET /teacher_school_associations/1
  # GET /teacher_school_associations/1.json
  def show
    @teacher_school_association = TeacherSchoolAssociation.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @teacher_school_association }
    end
  end

  # GET /teacher_school_associations/new
  # GET /teacher_school_associations/new.json
  def new
    @teacher_school_association = TeacherSchoolAssociation.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @teacher_school_association }
    end
  end

  # GET /teacher_school_associations/1/edit
  def edit
    @teacher_school_association = TeacherSchoolAssociation.find(params[:id])
  end

  # POST /teacher_school_associations
  # POST /teacher_school_associations.json
  def create
    @teacher_school_association = TeacherSchoolAssociation.new(params[:teacher_school_association])

    respond_to do |format|
      if @teacher_school_association.save
        format.html { redirect_to @teacher_school_association, notice: 'Teacher school association was successfully created.' }
        format.json { render json: @teacher_school_association, status: :created, location: @teacher_school_association }
      else
        format.html { render action: "new" }
        format.json { render json: @teacher_school_association.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /teacher_school_associations/1
  # PUT /teacher_school_associations/1.json
  def update
    @teacher_school_association = TeacherSchoolAssociation.find(params[:id])

    respond_to do |format|
      if @teacher_school_association.update_attributes(params[:teacher_school_association])
        format.html { redirect_to @teacher_school_association, notice: 'Teacher school association was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @teacher_school_association.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /teacher_school_associations/1
  # DELETE /teacher_school_associations/1.json
  def destroy
    @teacher_school_association = TeacherSchoolAssociation.find(params[:id])
    @teacher_school_association.destroy

    respond_to do |format|
      format.html { redirect_to teacher_school_associations_url }
      format.json { head :ok }
    end
  end
end
