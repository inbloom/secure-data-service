class IdpController < ApplicationController
  
  def index
    @users = Teacher.all() + Staff.all()
    @roles = ['IT Administrator', 'Leader', 'Educator', 'Aggregator']
    @idp_name = 'Hard-coded Name'
  end

  def login
    @staffUniqueStateId = params[:selected_user]
    @roles = params[:selected_roles].join ','
    @tenant = "Not Implemented"
  end

  def logout
  end
end
