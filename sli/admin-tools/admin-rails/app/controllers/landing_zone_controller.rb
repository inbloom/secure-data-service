=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


class LandingZoneController < ApplicationController
  before_filter :check_roles
  rescue_from ProvisioningError, :with => :handle_error
  rescue_from KeyValidationError, :with => :handle_validation_error
  rescue_from ActiveResource::ResourceConflict, :with => :already_there
  rescue_from ActiveResource::BadRequest, :with => :handle_error

  def provision
    if (params[:cancel] == "Cancel")
      redirect_to "/"
      return
    end

    tenant = get_tenant
    if (tenant == nil)
      Rails.logger.warn("Tenant ID is nil for user #{uid()}")
      render_403
      return
    end

    if APP_CONFIG["is_sandbox"]
      ed_org_id = params[:ed_org]
      ed_org_id = params[:custom_ed_org] if ed_org_id == 'custom'
      sample_data_select = params[:sample_data_select]
      logger.info("received the sample data selection is: #{sample_data_select}")
      logger.info("received the edorg selection is: #{ed_org_id}")
    else
      ed_org_id = ApplicationHelper.get_edorg_from_ldap( uid() )
    end

    @public_key = params[:public_key]
    Rails.logger.debug("Public key: #{@public_key}")

    if (ed_org_id == nil || ed_org_id.gsub(/\s/, '').length == 0)
      redirect_to :action => 'index', :controller => 'landing_zone'
    else
      ed_org_id = ed_org_id.gsub(/^ed_org_/, '')

      if sample_data_select != nil && sample_data_select != ""
        @landingzone = LandingZone.provision ed_org_id, tenant, uid, sample_data_select, @public_key
        @landingzone[:preload] = sample_data_select
      else
        @landingzone = LandingZone.provision ed_org_id, tenant, uid, nil, @public_key
      end
    end
  end

  def index
    @edOrgs = LandingZone.possible_edorgs
    @sample_data =LandingZone.possible_sample_data
  end

  def handle_validation_error(exception)
    logger.error exception.message
    logger.error exception.backtrace.join("\n")

    @validation_errors = exception.to_s
    Rails.logger.warn("Caught KeyValidationError: #{@validation_errors}")
    render :action => 'index', :controller => 'landing_zone'
  end

  def handle_error(exception)
    logger.error exception.message
    logger.error exception.backtrace.join("\n")

    Rails.logger.error("Error occured provisioning landing zone for #{uid()}: #{exception}")
    render :status => 500, :text => "An error occurred when provisioning the landing zone"
  end

  def check_roles
    allowed_roles = ["Ingestion User"]
    overlapping_roles = allowed_roles & session[:roles]
    unless overlapping_roles.length > 0
      logger.warn "Rejecting user #{session[:full_name]} due to insufficient privileges: roles: #{session[:roles]}"
      render_403
    end
  end

  def uid
    check = Check.get ""
    return check["external_id"]
  end

  def already_there
    respond_to do |format|
      format.html { render :file => "#{Rails.root}/public/409.html", :status => :conflict }
      format.any  { head :conflict }
    end
  end
end
