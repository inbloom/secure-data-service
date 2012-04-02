class StaffController < ApplicationController
  # GET /staff
  # GET /staff.json
  def index
    @staffs = Staff.all

    printf("count: %d", @staffs.length)
    @staffs.each do |staff|
       puts(staff.metaData["idNamespace"])
    end

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @staffs }
    end
  end

  # GET /staffs/1
  # GET /staffs/1.json
  def show
    @staff = Staff.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @staff }
    end
  end

end
