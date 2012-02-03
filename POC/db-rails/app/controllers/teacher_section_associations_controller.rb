class TeacherSectionAssociationsController < ApplicationController
  # GET /teacher_section_associations
  # GET /teacher_section_associations.json
  def index
    @teacher_section_associations = TeacherSectionAssociation.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @teacher_section_associations }
    end
  end

  # GET /teacher_section_associations/1
  # GET /teacher_section_associations/1.json
  def show
    @teacher_section_association = TeacherSectionAssociation.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @teacher_section_association }
    end
  end

  # GET /teacher_section_associations/new
  # GET /teacher_section_associations/new.json
  def new
    @teacher_section_association = TeacherSectionAssociation.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @teacher_section_association }
    end
  end

  # GET /teacher_section_associations/1/edit
  def edit
    @teacher_section_association = TeacherSectionAssociation.find(params[:id])
  end

  # POST /teacher_section_associations
  # POST /teacher_section_associations.json
  def create
    @teacher_section_association = TeacherSectionAssociation.new(params[:teacher_section_association])

    respond_to do |format|
      if @teacher_section_association.save
        format.html { redirect_to @teacher_section_association, notice: 'Teacher section association was successfully created.' }
        format.json { render json: @teacher_section_association, status: :created, location: @teacher_section_association }
      else
        format.html { render action: "new" }
        format.json { render json: @teacher_section_association.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /teacher_section_associations/1
  # PUT /teacher_section_associations/1.json
  def update
    @teacher_section_association = TeacherSectionAssociation.find(params[:id])

    respond_to do |format|
      if @teacher_section_association.update_attributes(params[:teacher_section_association])
        format.html { redirect_to @teacher_section_association, notice: 'Teacher section association was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @teacher_section_association.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /teacher_section_associations/1
  # DELETE /teacher_section_associations/1.json
  def destroy
    @teacher_section_association = TeacherSectionAssociation.find(params[:id])
    @teacher_section_association.destroy

    respond_to do |format|
      format.html { redirect_to teacher_section_associations_url }
      format.json { head :ok }
    end
  end
end
