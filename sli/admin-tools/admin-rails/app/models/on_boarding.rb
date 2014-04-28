class OnBoarding < SessionResource

  REST_HEADER = {
    'Content-Type' => 'application/json',
    'content_type' => 'json',
    'accept' => 'application/json'
  }

  self.collection_name = 'provision'
  self.timeout = 300

  schema do
    string  'tenantId'
    string  'stateOrganizationId'
  end

  def preload(tenantUuid,sample_data_select)
    header = REST_HEADER
    header['Authorization'] = "bearer #{SessionResource.access_token}"
    resource = "#{APP_CONFIG['api_base']}/tenants/#{tenantUuid}/preload"
    res  = RestClient.post(resource, sample_data_select,header) { |response, request, result| response }
  end

end
