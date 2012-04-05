require 'rubygems'
require 'uuidtools'

class TeacherController < ApplicationController
  # GET /teachers
  # GET /teachers.json
  def index
    @teachers = Teacher.all() + Staff.all()

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @teachers }
    end
  end

  # GET /teacher/1
  # GET /teacher/1.json
  def show
    tmp = BSON::Binary.new(UUIDTools::UUID.parse(params[:id]).raw(), 0x03)
    @teacher = Teacher.find(tmp)

    if (@teacher.nil?)
        @teacher = Staff.find(tmp)
    end

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @teacher }
    end
  end

  def loginAs(id)
      # get the application clientid, secret, and callback

      # generate a SAML assertion

      # redirect to the callback with the assertion
  end



end
