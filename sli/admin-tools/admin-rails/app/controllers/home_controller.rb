class HomeController < ApplicationController

  SANDBOX_ALLOWED_ROLES = ['Sandbox Administrator']
  PRODUCTION_ALLOWED_ROLES = ['SLC Operator', 'SEA Administrator', 'LEA Administrator']

  def index
    respond_to do |format|
      format.html # index.html.erb
    end
  end

  def has_users_access?
    allowed_roles = APP_CONFIG['is_sandbox'] ? SANDBOX_ALLOWED_ROLES : PRODUCTION_ALLOWED_ROLES
    overlap_roles = allowed_roles & session[:roles]
    !overlap_roles.empty?
  end

end
