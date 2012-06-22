class TenantMetricsController < ApplicationController
  before_filter :check_roles

  # GET /tenant_metrics
  # GET /tenant_metrics.json
  def index
    @tenant_metrics = get_metrics(params)

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @tenant_metrics.metrics() }
    end
  end


  # GET /tenant_metrics/1
  # GET /tenant_metrics/1.json
  def show
    @tenant_metrics = get_metrics(params)

    respond_to do |format|
      format.html { render :template => "tenant_metrics/detail" }
      format.json { render json: @tenant_metrics.metrics() }
    end
  end


  def check_roles(all = false)

    # if we're in sandbox, developers have access to their tenant.  They do *not* have access to all tenants.
    if APP_CONFIG["is_sandbox"] && all == false
      unless is_operator? || is_developer?
        render_403
      end
      # in production, only the SLC Operator can view these metrics.
    else
      unless is_operator?
        render_403
      end
    end
  end

  def get_metrics(params)
    @tenant_metrics = TenantMetric.new()

    if params[:details]
      @tenant_metrics.isSummary = false
    end

    @tenant_metrics.calculate_metrics(params[:id])

    @tenant_metrics
  end

end
