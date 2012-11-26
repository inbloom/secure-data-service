require 'json'

class TenantMetric < SessionResource

  attr_accessor :host, :port, :dbName, :isSummary, :useJson, :metrics

  API_BASE=APP_CONFIG["api_base"]

  REST_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
  }


  def initialize(host = 'localhost', port = 27017, dbName = 'sli', summary=true, useJson=true)
    @host = host
    @port = port
    @dbName = dbName
    @isSummary = summary
    @useJson = useJson
  end

  def calculate_metrics(tenantId)
    header = REST_HEADER
    header['authorization'] = "bearer " + SessionResource.access_token

    resource = API_BASE + "/v1/tenant_metrics"

    if !tenantId.nil?
      resource = resource + "/" + tenantId
    end
    result  = RestClient.get(resource, header){|response, request, result| response }
    @metrics = JSON.parse(result)
  end

  def metrics()
    @metrics
  end

  def persisted?
    false
  end

  def get_binding
    binding
  end

end